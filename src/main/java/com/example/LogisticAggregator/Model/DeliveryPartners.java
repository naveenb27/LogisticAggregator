package com.example.LogisticAggregator.Model;

import com.example.LogisticAggregator.Enum.UserRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "delivery_partners")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DeliveryPartners implements AppUser{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delivery_partner_sequence")
    @SequenceGenerator(name = "delivery_partner_sequence", sequenceName = "delivery_partner_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "logistic_provider_id", nullable = false)
    private LogisticProvider logisticProvider;

    private String vehicleDetails;
    private String role = UserRole.DELIVERY_MAN.toString();
    private String address;

    @ManyToOne
    @JoinColumn(name = "hub_id")
    private Hub hub;
}
