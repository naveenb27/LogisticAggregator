package com.example.LogisticAggregator.Controller;

import com.example.LogisticAggregator.DTO.LocationUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LocationController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/location/update")
    public void sendLocationUpdate(LocationUpdate locationUpdate) {

        System.out.println(locationUpdate);
        messagingTemplate.convertAndSend("/topic/location/" + locationUpdate.getShipmentId(), locationUpdate);
    }
}
