package com.example.LogisticAggregator.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String senderId;
    private String receiverId;
    private String content;
    private String timestamp;
}
