package com.example.LogisticAggregator.Controller;


import com.example.LogisticAggregator.Service.ShipmentDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "${frontend.url}")
public class ShipmentDetailsController {

    private final ShipmentDetailsService shipmentDetailsService;

    ShipmentDetailsController(ShipmentDetailsService shipmentDetailsService) {
        this.shipmentDetailsService = shipmentDetailsService;
    }
}
