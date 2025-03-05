package com.example.LogisticAggregator.Service;


import com.example.LogisticAggregator.DAO.HubRepository;
import com.example.LogisticAggregator.DAO.ShipmentDetailsRepository;
import com.example.LogisticAggregator.Enum.DeliveryPartnerEnum;
import com.example.LogisticAggregator.Enum.ShipmentEnum;
import com.example.LogisticAggregator.Model.Hub;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@Service
public class ShipmentDetailsService {

    private final ShipmentDetailsRepository shipmentDetailsRepository;
    private final HubRepository hubRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    ShipmentDetailsService(ShipmentDetailsRepository shipmentDetailsRepository, HubRepository hubRepository) {
        this.shipmentDetailsRepository = shipmentDetailsRepository;
        this.hubRepository = hubRepository;
    }

    public void setInTransitById(Long id) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Error"));
        setInTransit(shipmentDetails);
    }

    public void setInTransit(ShipmentDetails shipmentDetails) {
        shipmentDetails.setStatus(ShipmentEnum.IN_TRANSIT);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void sendProduct(ShipmentDetails shipmentDetails) {
        double[] orginLatLong = getLatLongFromAddress(shipmentDetails.getOrigin());
        double[] destinationLatLong = getLatLongFromAddress(shipmentDetails.getDestination());

        double lat1Rad = Math.toRadians(orginLatLong[0]);
        double long1Rad = Math.toRadians(orginLatLong[1]);
        double lat2Rad = Math.toRadians(destinationLatLong[0]);
        double long2Rad = Math.toRadians(destinationLatLong[1]);

        double deltaLat = lat2Rad - lat1Rad;
        double deltaLong = long2Rad - long1Rad;

        double a = Math.pow(Math.sin(deltaLat / 2), 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                Math.pow(Math.sin(deltaLong /2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distanceInKm = 6371.0  * c;
        int expectedDate = getExpectedDays(distanceInKm);
        System.out.println("Distance: "+distanceInKm);
        shipmentDetails.setExpectedDays(expectedDate);


        List<ShipmentDetails> s = shipmentDetailsRepository.findBySender_Id(shipmentDetails.getSender().getId());
        for(ShipmentDetails sd : s){
            if(!Objects.isNull(sd) && sd.getDestination().equals(shipmentDetails.getDestination()) && String.valueOf(sd.getStatus()).equals("PENDING")) {
                sd.setPackageName(sd.getPackageName() + " + " + shipmentDetails.getPackageName());
                sd.setPackageWeight(sd.getPackageWeight() + shipmentDetails.getPackageWeight());
                shipmentDetailsRepository.save(sd);
                System.out.println("Shipment details saved!");
                return;
            }
        }
        shipmentDetailsRepository.save(shipmentDetails);


        System.out.println("Shipment details saved!");
    }

    public int getExpectedDays(double km) {
        return (int) Math.ceil(km / 300);
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

    public List<ShipmentDetails> getShipmentDetailsByUserId(long userId) {
        return shipmentDetailsRepository.findBySender_Id(userId);
    }

    public ShipmentDetails findById(long shipmentId) {
        return shipmentDetailsRepository.findById(shipmentId).orElseThrow(()-> new RuntimeException("Invalid data"));
    }

    public void setStatusDelivered(ShipmentDetails shipmentDetails) {
        shipmentDetails.setStatus(ShipmentEnum.DELIVERED);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void setInOutForPick(Long id) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Exception!"));
        shipmentDetails.setStatus(ShipmentEnum.PICKUP);
        System.out.println("Delivery man is on the way! You can check real time location on the app");
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void setInDeliveredToHub(Long id) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Exception!!!"));
        shipmentDetails.setStatus(ShipmentEnum.DELIVERED_TO_HUB);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void receivedFromSender(ShipmentDetails shipmentDetails) {
        shipmentDetails.setStatus(ShipmentEnum.RECEIVED_FROM_SENDER);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void changeHub(Long id, Long hubId) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Error!!"));
        Hub hub = hubRepository.findById(hubId).orElseThrow(() -> new RuntimeException("Error!"));

        shipmentDetails.setHub(hub);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void setInDispatch(Long id) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Exception!"));
        shipmentDetails.setStatus(ShipmentEnum.DISPATCH);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public void setInDelivered(Long id) {
        ShipmentDetails shipmentDetails = shipmentDetailsRepository.findById(id).orElseThrow(() -> new RuntimeException("Error!"));
        shipmentDetails.setStatus(ShipmentEnum.DELIVERED);
        shipmentDetailsRepository.save(shipmentDetails);
    }

    public List<ShipmentDetails> findAll() {
        return shipmentDetailsRepository.findAll();
    }
}


