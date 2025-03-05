package com.example.LogisticAggregator.Controller;

import com.example.LogisticAggregator.DTO.PendingShipment;
import com.example.LogisticAggregator.Enum.ShipmentEnum;
import com.example.LogisticAggregator.Model.*;
import com.example.LogisticAggregator.Service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery")
public class DeliveryPartnersController {

    private final LogisticProviderService logisticProviderService;
    private final ShipmentDetailsService shipmentDetailsService;
    private final DeliveryPatnersService deliveryPatnersService;
    private final DeliveryPartnerAssignedWorkService deliveryPartnerAssignedWorkService;
    private final HubService hubService;

    DeliveryPartnersController(LogisticProviderService logisticProviderService, DeliveryPatnersService deliveryPatnersService, ShipmentDetailsService shipmentDetailsService, DeliveryPartnerAssignedWorkService deliveryPartnerAssignedWorkService, HubService hubService) {
        this.logisticProviderService = logisticProviderService;
        this.hubService = hubService;
        this.deliveryPartnerAssignedWorkService = deliveryPartnerAssignedWorkService;
        this.deliveryPatnersService = deliveryPatnersService;
        this.shipmentDetailsService = shipmentDetailsService;
    }

    @PostMapping("/signup")
    public void save(@RequestBody DeliveryPartners deliveryPartners) {
        deliveryPatnersService.createDeliveryPatner(deliveryPartners);
    }

    @PostMapping("/orders/{id}")
    public void atSenderDestination(@PathVariable long id) { // shipment id
        logisticProviderService.verifyUser(id);
    }

    @PostMapping("/otp/verify")
    public String verifyAtSenderSide(@RequestParam long shipmentId, @RequestParam String otp){
        System.out.println("Shipment ID" + " Otp");
        boolean isVerified = logisticProviderService.verifyOtp(shipmentId, otp);

        if(isVerified) {
            ShipmentDetails shipmentDetails = shipmentDetailsService.findById(shipmentId);
            shipmentDetailsService.receivedFromSender(shipmentDetails);
            DeliveryManAssignedWork assignedWorkService = deliveryPartnerAssignedWorkService.findByShipmentId(shipmentId);
            deliveryPartnerAssignedWorkService.receivedFromSender(assignedWorkService);
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


    @PostMapping("/login")
    public void verify(@RequestBody DeliveryPartners deliveryPartners) {
        deliveryPatnersService.verify(deliveryPartners);
    }

    @PostMapping("/pendings")
    public List<PendingShipment> showPendingOrder(@RequestBody PendingOrderRequest pendingOrderRequest) {
        List<PendingShipment> shipmentDetailsList = logisticProviderService.getPendingOrder(pendingOrderRequest.getId(), ShipmentEnum.PENDING, pendingOrderRequest.getLon(), pendingOrderRequest.getLat());
        return shipmentDetailsList;
    }

    @PostMapping("/pick-order")
    public void pickOrder(@RequestBody DeliveryManAssignedWork assignedWork){ // shipmentId
        ShipmentDetails shipmentDetails = assignedWork.getShipmentDetails();
        System.out.println(shipmentDetails);
        shipmentDetailsService.setInOutForPick(assignedWork.getShipmentDetails().getId());
        deliveryPartnerAssignedWorkService.createWork(assignedWork);
    }

    @GetMapping("/orders")
    public List<ShipmentDetails> getOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        DeliveryPartners deliveryPartners = deliveryPatnersService.findByEmail(email);
        System.out.println(deliveryPartners);
        LogisticProvider id  = deliveryPartners.getLogisticProvider();
        List<ShipmentDetails> shipmentDetails = logisticProviderService.getOrder(id.getId());

        return shipmentDetails;
    }

    @PostMapping("/reached-hub/{id}")
    public String reachedHub(@PathVariable Long id) {
        DeliveryManAssignedWork assignedWorkService = deliveryPartnerAssignedWorkService.findByShipmentId(id);
        deliveryPartnerAssignedWorkService.reachedHub(assignedWorkService);
        shipmentDetailsService.setInDeliveredToHub(assignedWorkService.getShipmentDetails().getId());
        hubService.assignShipmentToHub(assignedWorkService.getDeliveryPartners().getId() ,id);

        return "UPDATED";
    }

    @PostMapping("/{id}/dispatched-orders")
    public ResponseEntity<List<ShipmentDetails>> dispatchedOrders(@PathVariable Long id) {
        DeliveryPartners deliveryPartners = deliveryPatnersService.findById(id);
        Hub hub = deliveryPartners.getHub();
        System.out.println(hub.getId());
        List<ShipmentDetails> shipmentDetails = hubService.getShipmentsByHubId(hub.getId());
        System.out.println(shipmentDetails);

        return ResponseEntity.ok(shipmentDetails);
    }

    @PostMapping("/delivered/{id}")
    public ResponseEntity<String> delivered(@PathVariable Long id) {
        DeliveryManAssignedWork deliveryManAssignedWork = deliveryPartnerAssignedWorkService.findByShipmentId(id);
        deliveryPartnerAssignedWorkService.deliverd(deliveryManAssignedWork);
        shipmentDetailsService.setInDelivered(deliveryManAssignedWork.getShipmentDetails().getId());

        return ResponseEntity.ok("DELIVERED!");
    }
}
