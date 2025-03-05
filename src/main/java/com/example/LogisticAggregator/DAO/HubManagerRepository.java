package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Model.DeliveryPartners;
import com.example.LogisticAggregator.Model.HubManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HubManagerRepository extends JpaRepository<HubManager, Long> {
    public HubManager findByEmail(String email);

    HubManager findByHub_Id(Long hubId);
}
