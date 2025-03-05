package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Enum.ShipmentEnum;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ShipmentDetailsRepository extends JpaRepository<ShipmentDetails, Long> {
    List<ShipmentDetails> findByLogisticProviderId(Long logisticProviderId);
    List<ShipmentDetails> findBySender_Id(Long userId);
    List<ShipmentDetails> findByLogisticProviderIdAndStatus(Long logisticProviderId, ShipmentEnum shipmentEnum);
}
