package com.example.LogisticAggregator.Controller;

import com.example.LogisticAggregator.DAO.HubManagerRepository;
import com.example.LogisticAggregator.DTO.HubManagerAssigner;
import com.example.LogisticAggregator.DTO.SendOneHubToAnother;
import com.example.LogisticAggregator.Model.HubManager;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import com.example.LogisticAggregator.Service.HubManagerService;
import com.example.LogisticAggregator.Service.HubService;
import com.example.LogisticAggregator.Service.ShipmentDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/hub-manager")
public class HubManagerController {

    private final HubManagerService hubManagerService;
    private final HubService hubService;
    private final ShipmentDetailsService shipmentDetailsService;

    HubManagerController(HubManagerService hubManagerService, HubService hubService, ShipmentDetailsService shipmentDetailsService) {
        this.hubManagerService = hubManagerService;
        this.shipmentDetailsService = shipmentDetailsService;
        this.hubService = hubService;
    }

    @PostMapping("/create-hub-manager")
    public HubManager createHubManager(@RequestBody HubManagerAssigner hubManagerAssigner) {
        return hubManagerService.assignManagerToHub(hubManagerAssigner.getHubId(), hubManagerAssigner.getHubManager());
    }

    @PostMapping("/{hubId}/shipment")
    public List<ShipmentDetails> getShipmentsById(@PathVariable Long hubId){
        Long id = hubManagerService.getHubIdById(hubId);
        return hubService.getShipmentsByHubId(id);
    }

    @PostMapping("/make-intransit")
    public ResponseEntity<String> sendOneHubToAnotherHub(@RequestBody SendOneHubToAnother sendOneHubToAnother) {
        List<Long> shipmentIds;

        if (sendOneHubToAnother.getIds() instanceof List<?>) {
            shipmentIds = ((List<?>) sendOneHubToAnother.getIds()).stream()
                    .map(id -> ((Number) id).longValue())  // Convert each element to Long
                    .collect(Collectors.toList());
        } else {
            return ResponseEntity.badRequest().body("Invalid data format for 'ids'");
        }

        System.out.println("HUB ID: "+ sendOneHubToAnother.getHubId());
        for (Long id : shipmentIds) {
            System.out.println("ID: " +id);

            shipmentDetailsService.setInTransitById(id);
            shipmentDetailsService.changeHub(id, sendOneHubToAnother.getHubId());
        }
        return ResponseEntity.ok("UPDATED");
    }

    @PostMapping("/intransit-to-delivered")
    public ResponseEntity<String> intransitToDelivered(@RequestBody List<Long> ids) {
        for(int i = 0; i < ids.size(); i++) {
            shipmentDetailsService.setInDeliveredToHub(ids.get(i));
        }

        return ResponseEntity.ok("UPDATED!");
    }

    @PostMapping("/dispatch")
    public ResponseEntity<String> dispatch(@RequestBody List<Long> ids) {
        for(int i = 0; i < ids.size(); i++) {
            shipmentDetailsService.setInDispatch(ids.get(i));
        }
        return ResponseEntity.ok("UPDATED!");
    }
}
