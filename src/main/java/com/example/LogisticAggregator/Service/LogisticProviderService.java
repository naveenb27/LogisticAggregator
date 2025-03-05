package com.example.LogisticAggregator.Service;


import com.example.LogisticAggregator.DAO.LogisticProviderRepository;
import com.example.LogisticAggregator.DAO.ShipmentDetailsRepository;
import com.example.LogisticAggregator.DTO.PendingShipment;
import com.example.LogisticAggregator.Enum.ShipmentEnum;
import com.example.LogisticAggregator.Model.LogisticProvider;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import com.example.LogisticAggregator.Model.User;
import com.example.LogisticAggregator.Utils.OtpDetails;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class LogisticProviderService implements AppUserService{

    private final LogisticProviderRepository logisticProviderRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ShipmentDetailsRepository shipmentDetailsRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailSenderService emailSenderService;
    private final UserService userService;
    private static final SecureRandom random = new SecureRandom();

    private final Map<Long, OtpDetails> otpStore = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate = new RestTemplate();


    LogisticProviderService(LogisticProviderRepository logisticProviderRepository, EmailSenderService emailSenderService, UserService userService, ShipmentDetailsRepository shipmentDetailsRepository, BCryptPasswordEncoder bCryptPasswordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.logisticProviderRepository = logisticProviderRepository;
        this.userService = userService;
        this.emailSenderService = emailSenderService;
        this.jwtService = jwtService;
        this.shipmentDetailsRepository = shipmentDetailsRepository;
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String addLogisticProviderService(LogisticProvider logisticProvider) {
        logisticProvider.setPassword(bCryptPasswordEncoder.encode(logisticProvider.getPassword()));
        logisticProviderRepository.save(logisticProvider);
        return "Your application is submitted!!! waiting for approval";
    }

    public List<LogisticProvider> getLogisticProviders() {
        return logisticProviderRepository.findAll();
    }

    public ResponseEntity<String> verify(LogisticProvider logisticProvider) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(logisticProvider.getEmail(), logisticProvider.getPassword())
        );

        if (authenticate.isAuthenticated()) {
            Long id = findByEmail(logisticProvider.getEmail()).getId();
            String jwtToken = jwtService.generateToken(logisticProvider.getEmail(), id,"LOGISTIC_PROVIDER");
            ResponseCookie cookie = ResponseCookie.from("authtoken", jwtToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body("{\"message\": \"Login successful\", \"token\": \"" + jwtToken + "\"}");

        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"error\": \"Invalid credentials\"}");
    }

        public LogisticProvider findById(long id) {
        return logisticProviderRepository.findById(id);
    }

    public LogisticProvider findByEmail(String email) {
        return logisticProviderRepository.findByEmail(email);
    }

    public void update(LogisticProvider logisticProvider) {
        logisticProviderRepository.save(logisticProvider);
        System.out.println("Updated!");
    }

    public List<ShipmentDetails> getOrder(long id) {
        return shipmentDetailsRepository.findByLogisticProviderId(id);
    }

    public void verifyUser(long id) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid user"));

        String email = shipmentDetails.getSender().getEmail();
        String generateOTP = generateOTP();
        emailSenderService.sendMain(email, "OTP to verify user", generateOTP);

        otpStore.put(id, new OtpDetails(generateOTP, System.currentTimeMillis() + ( 10* 60 * 1000)));
    }

    public void verifyReceiver(long id) { //shipment id
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Invalid user"));

        String email = shipmentDetails.getReceiverEmail();
        String generateOTP = generateOTP();

        emailSenderService.sendMain(email, "OTP!", generateOTP);
        otpStore.put(id, new OtpDetails(generateOTP, System.currentTimeMillis() + (10 * 60 * 1000)));
    }

    public String generateOTP() {
        String digits = "0123456789";
        StringBuilder opt = new StringBuilder();

        for(int i = 0; i < 6; i++) {
            opt.append(digits.charAt(random.nextInt(digits.length())));
        }

        return opt.toString();
    }

    public boolean verifyOtp(long id, String enteredOtp) {
        OtpDetails otpDetails = otpStore.get(id);

        System.out.println(otpStore.get(id));
        System.out.println(id);

        if(otpDetails == null) {
            return false;
        }

        if(System.currentTimeMillis() > otpDetails.getExpiryTime()) {
            otpStore.remove(id);
            return false;
        }

        boolean isValid = enteredOtp.equals(otpDetails.getOtp());

        if(isValid) {
            otpStore.remove(id);
        }

        return isValid;
    }

    public List<PendingShipment> getPendingOrder(Long id, ShipmentEnum status, double lon, double lat) {
        List<ShipmentDetails> shipmentDetailsList = shipmentDetailsRepository.findByLogisticProviderIdAndStatus(id, status);

        List<PendingShipment> withinRange = new ArrayList<>();

        double lat1Rad = Math.toRadians(lat);
        double lon1Rad = Math.toRadians(lon);

        for (ShipmentDetails shipment : shipmentDetailsList) {
            double[] originLatLong = getLatLongFromAddress(shipment.getOrigin());

            double lat2Rad = Math.toRadians(originLatLong[0]);
            double lon2Rad = Math.toRadians(originLatLong[1]);

            double deltaLat = lat2Rad - lat1Rad;
            double deltaLon = lon2Rad - lon1Rad;

            double a = Math.pow(Math.sin(deltaLat / 2), 2)
                    + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.pow(Math.sin(deltaLon / 2), 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distanceInKm = 6371.0 * c;

            System.out.printf("Distance: %.2f km | Origin: %.6f, %.6f | Current: %.6f, %.6f%n",
                    distanceInKm, originLatLong[0], originLatLong[1], lat, lon
            );

            if (distanceInKm < 200) {
                withinRange.add(new PendingShipment(shipment, distanceInKm));
            }
        }

        return withinRange;
    }

    public double[] getLatLongFromAddress(String address) {
        String pincode = address.substring(address.length() - 7);
        System.out.println("PIN CODE: " + pincode);

        String url = "https://nominatim.openstreetmap.org/search?q={" + pincode + "}&format=json&limit=1";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("User-Agent", "LogisticAggregator");

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);

        ResponseEntity<List> respone = restTemplate.exchange(url, HttpMethod.GET, entity, List.class, pincode);

        if (respone.getStatusCode().is2xxSuccessful() && respone.getBody() != null && !respone.getBody().isEmpty()) {
            Map<String, Object> location = (Map<String, Object>) respone.getBody().get(0);

            double lat = Double.parseDouble((String) location.get("lat"));
            double lon = Double.parseDouble((String) location.get("lon"));

            return new double[]{lat, lon};
        }

        return new double[]{0, 0};
    }
}
