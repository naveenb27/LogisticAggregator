package com.example.LogisticAggregator.Model;

import com.example.LogisticAggregator.DTO.LogisticProviderDTO;
import com.example.LogisticAggregator.DTO.SenderDTO;
import com.example.LogisticAggregator.Enum.ShipmentEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "shipment_details")
public class ShipmentDetails {
    @Id
    @SequenceGenerator(name = "shipment_details_seq", sequenceName = "shipment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shipment_details_seq")
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logistic_provider_id")
    @ToString.Exclude
    private LogisticProvider logisticProvider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;

    private String origin;
    private String destination;
    private int expectedDays;
    private String packageName;
    private Double packageWeight;
    private String receiverPhoneNumber;
    private String receiverEmail;

    @Enumerated(EnumType.STRING)
    private ShipmentEnum status = ShipmentEnum.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hub_id")
    @JsonIgnore // Prevents recursion issue
    private Hub hub;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public LogisticProviderDTO getLogisticProviderDTO() {
        if (this.logisticProvider == null) {
            return null;
        }

        return new LogisticProviderDTO(
                logisticProvider.getId(),
                logisticProvider.getEmail(),
                logisticProvider.getCompanyName(),
                logisticProvider.getPhoneNumber()
        );
    }


    public SenderDTO getSenderDTO() {
        if(this.sender == null) {
            return null;
        }

        SenderDTO senderDTO = new SenderDTO();
        senderDTO.setId(this.sender.getId());
        senderDTO.setEmail(this.sender.getEmail());
        senderDTO.setAddress(this.sender.getAddress());
        senderDTO.setPhoneNumber(this.sender.getPhoneNumber());

        return senderDTO;
    }
}
