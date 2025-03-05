package com.example.LogisticAggregator.Model;

import lombok.Data;

@Data
public class PendingOrderRequest {
    private Long id;
    private String status;

    private double lat;
    private double lon;
}