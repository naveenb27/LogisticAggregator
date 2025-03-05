package com.example.LogisticAggregator.DTO;

import com.example.LogisticAggregator.Enum.ShipmentEnum;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDetailsDTO {
    private Long id;
    private String origin;
    private String destination;
    private int expectedDays;
    private String packageName;
    private Double packageWeight;
    private String receiverPhoneNumber;
    private String receiverEmail;
    private ShipmentEnum status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LogisticProviderDTO logisticProvider;
    private SenderDTO sender;

    public static ShipmentDetailsDTO fromEntity(ShipmentDetails shipment) {
        ShipmentDetailsDTO dto = new ShipmentDetailsDTO();
        dto.setId(shipment.getId());
        dto.setOrigin(shipment.getOrigin());
        dto.setDestination(shipment.getDestination());
        dto.setExpectedDays(shipment.getExpectedDays());
        dto.setPackageName(shipment.getPackageName());
        dto.setPackageWeight(shipment.getPackageWeight());
        dto.setReceiverPhoneNumber(shipment.getReceiverPhoneNumber());
        dto.setReceiverEmail(shipment.getReceiverEmail());
        dto.setStatus(shipment.getStatus());
        dto.setCreatedAt(shipment.getCreatedAt());
        dto.setUpdatedAt(shipment.getUpdatedAt());
        dto.setLogisticProvider(shipment.getLogisticProviderDTO());
        dto.setSender(shipment.getSenderDTO());

        return dto;
    }
}
