package com.example.LogisticAggregator.Model;

import com.example.LogisticAggregator.Enum.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "logistic_providers")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class LogisticProvider implements AppUser{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "logistic_provider_sequence")
    @SequenceGenerator(name = "logistic_provider_sequence", sequenceName = "logistic_id_seq", allocationSize = 1)
    private Long id;

    private String email;
    private String password;
    private String companyName;
    private String phoneNumber;
    private String address;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "logisticProvider", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryPartners> deliveryPartners = new ArrayList<>();

    private boolean isApproved = false;
    private String role = UserRole.LOGISTIC_PROVIDER.toString();

}
