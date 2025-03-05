package com.example.LogisticAggregator.Model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Hub {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hub_sequence")
    @SequenceGenerator(name = "hub_sequence", sequenceName = "hub_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String location;
    private String managerName;
    private String contactNumber;

    @OneToMany(mappedBy = "hub", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ShipmentDetails> shipmentDetailsList;

}
