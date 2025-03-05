package com.example.LogisticAggregator.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationUpdate {
    private String driverId;
    private double latitude;
    private double longitude;
    private String shipmentId;
}
