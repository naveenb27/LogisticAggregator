package com.example.LogisticAggregator.Controller;


import com.example.LogisticAggregator.DTO.LogisticProviderDTO;
import com.example.LogisticAggregator.DTO.ShipmentDetailsDTO;
import com.example.LogisticAggregator.Model.LogisticProvider;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import com.example.LogisticAggregator.Model.User;
import com.example.LogisticAggregator.Service.LogisticProviderService;
import com.example.LogisticAggregator.Service.ShipmentDetailsService;
import com.example.LogisticAggregator.Service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final LogisticProviderService logisticProviderService;
    private final ShipmentDetailsService shipmentDetailsService;

    UserController(UserService userService, ShipmentDetailsService shipmentDetailsService, LogisticProviderService logisticProviderService) {
        this.userService = userService;
        this.logisticProviderService = logisticProviderService;
        this.shipmentDetailsService = shipmentDetailsService;
    }

    @PostMapping
    public String createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        System.out.println(user);
         return userService.verify(user);
    }

    @GetMapping("/logistic-providers")
    public List<LogisticProvider> getLogisticProvider() {
        return logisticProviderService.getLogisticProviders();
    }

    @GetMapping
    public String welcome() {
        return "Welcome!";
    }

    @PostMapping("/send-product")
    public ResponseEntity<String> sendProduct(@RequestBody ShipmentDetails shipmentDetails) {
        System.out.println(shipmentDetails);
        shipmentDetailsService.sendProduct(shipmentDetails);

        return ResponseEntity.ok("Shipment Added successfully");
    }

    @GetMapping("/track-my-orders")
    public ResponseEntity<List<ShipmentDetailsDTO>> trackMyOrder(@CookieValue("authtoken") String token) {
        List<ShipmentDetails> shipmentDetailsList = userService.trackMyOrder(token);


        List<ShipmentDetailsDTO> shipmentDTOs = shipmentDetailsList.stream()
                .map(ShipmentDetailsDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(shipmentDTOs);
    }
}
