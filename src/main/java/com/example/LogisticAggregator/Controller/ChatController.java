package com.example.LogisticAggregator.Controller;

import com.example.LogisticAggregator.Model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/private-message")
    public void sendPrivateMessage(ChatMessage message, Principal principal) {
        System.out.println("Received: " + message.getContent() + " from " + message.getSenderId() + " to " + message.getReceiverId());
        System.out.println("Principal name: " + principal.getName());

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                message
        );
    }


}
