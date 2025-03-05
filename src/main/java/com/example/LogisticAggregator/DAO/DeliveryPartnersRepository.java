package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Model.DeliveryPartners;
import com.example.LogisticAggregator.Model.LogisticProvider;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryPartnersRepository extends JpaRepository<DeliveryPartners, Long> {
    public DeliveryPartners findByEmail(String email);
}
