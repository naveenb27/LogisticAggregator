package com.example.LogisticAggregator.Model;

import com.example.LogisticAggregator.Enum.DeliveryPartnerEnum;
import com.example.LogisticAggregator.Enum.ShipmentEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "delivery_man_assigned_work")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryManAssignedWork {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_man_assign")
    @SequenceGenerator(name = "delivery_man_assign", sequenceName = "delivery_man_assign_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_partner_id", nullable = false)
    private DeliveryPartners deliveryPartners;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private ShipmentDetails shipmentDetails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryPartnerEnum status;
}
