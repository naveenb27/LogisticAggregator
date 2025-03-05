package com.example.LogisticAggregator.DTO;

import com.example.LogisticAggregator.Model.ShipmentDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendingShipment {
    private ShipmentDetails shipmentDetails;
    private double distance;
}
