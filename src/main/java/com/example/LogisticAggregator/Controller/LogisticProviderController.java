package com.example.LogisticAggregator.Controller;


import com.example.LogisticAggregator.Model.LogisticProvider;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import com.example.LogisticAggregator.Service.EmailSenderService;
import com.example.LogisticAggregator.Service.LogisticProviderService;
import com.example.LogisticAggregator.Service.ShipmentDetailsService;
import com.example.LogisticAggregator.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/logistic-provider")
@CrossOrigin(origins = "${frontend.url}")
public class LogisticProviderController {

    private final LogisticProviderService logisticProviderService;
    private final UserService userService;
    private final EmailSenderService emailSenderService;
    private final ShipmentDetailsService shipmentDetailsService;

    LogisticProviderController(LogisticProviderService logisticProviderService, ShipmentDetailsService shipmentDetailsService,UserService userService, EmailSenderService emailSenderService) {
        this.logisticProviderService = logisticProviderService;
        this.shipmentDetailsService = shipmentDetailsService;
        this.emailSenderService = emailSenderService;
        this.userService = userService;
    }

    @PostMapping
    public String createLogisticProvider(@RequestBody LogisticProvider logisticProvider) {
        return logisticProviderService.addLogisticProviderService(logisticProvider);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LogisticProvider logisticProvider) {
        return logisticProviderService.verify(logisticProvider);
    }

    @GetMapping("/orders")
    public List<ShipmentDetails> getOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        LogisticProvider logisticProvider = logisticProviderService.findByEmail(email);
        long id  = logisticProvider.getId();
        List<ShipmentDetails> shipmentDetails = logisticProviderService.getOrder(id);

        return shipmentDetails;
    }

    @PostMapping("/orders/{id}")
    public void atSenderDestination(@PathVariable long id) { // shipment id
        logisticProviderService.verifyUser(id);
    }

    @PostMapping("/otp/verify")
    public String verifyAtSenderSide(@RequestParam long shipmentId, @RequestParam String otp){
        boolean isVerified = logisticProviderService.verifyOtp(shipmentId, otp);

        if(isVerified) {
            ShipmentDetails shipmentDetails = shipmentDetailsService.findById(shipmentId);
            shipmentDetailsService.setInTransit(shipmentDetails);
            return "Status updated";
        }
        return String.valueOf(isVerified);
    }


    @PostMapping("/reachedDestination/{id}")
    public String delivered(@PathVariable long id) { //shipments details id
        logisticProviderService.verifyReceiver(id);

        return "Otp sent!";
    }

    @PostMapping("/otp/receiver/verify")
    public String verifyAtReceiversEnd(@RequestParam long shipmentId, @RequestParam String otp) {
        boolean isVerified = logisticProviderService.verifyOtp(shipmentId, otp);

        if(isVerified) {
            ShipmentDetails shipmentDetails = shipmentDetailsService.findById(shipmentId);
            shipmentDetailsService.setStatusDelivered(shipmentDetails);

            return "Status updated";
        }

        return String.valueOf(isVerified);
    }


    @GetMapping("/home")
    public String Home() {
        return "Welcome";
    }
}
