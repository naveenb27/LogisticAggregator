package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Model.DeliveryManAssignedWork;
import com.example.LogisticAggregator.Model.DeliveryPartners;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryPartnerAssignedWorkRepository extends JpaRepository<DeliveryManAssignedWork, Long> {
    Optional<DeliveryManAssignedWork> findByShipmentDetails_Id(long shipmentId);
}
