package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.DAO.DeliveryPartnerAssignedWorkRepository;
import com.example.LogisticAggregator.Enum.DeliveryPartnerEnum;
import com.example.LogisticAggregator.Model.DeliveryManAssignedWork;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeliveryPartnerAssignedWorkService {

    private final DeliveryPartnerAssignedWorkRepository assignedWorkRepository;


    @Autowired
    public DeliveryPartnerAssignedWorkService(DeliveryPartnerAssignedWorkRepository assignedWorkRepository) {
        this.assignedWorkRepository = assignedWorkRepository;
    }

    public String createWork(DeliveryManAssignedWork deliveryManAssignedWork) {
        deliveryManAssignedWork.setStatus(DeliveryPartnerEnum.PICKUP);
        assignedWorkRepository.save(deliveryManAssignedWork);
        return "SAVED SUCCESSFULLY!";
    }

    public String receivedFromSender(DeliveryManAssignedWork deliveryManAssignedWork) {
        deliveryManAssignedWork.setStatus(DeliveryPartnerEnum.RECEIVED_FROM_SENDER);

        assignedWorkRepository.save(deliveryManAssignedWork);
        return "RECEIVED FROM SENDER";
    }

    public String reachedHub(DeliveryManAssignedWork deliveryManAssignedWork) {
        deliveryManAssignedWork.setStatus(DeliveryPartnerEnum.DELIVERED_TO_HUB);
        assignedWorkRepository.save(deliveryManAssignedWork);
        return "DELIVERED TO HUB";
    }

    public String dispatch(DeliveryManAssignedWork deliveryManAssignedWork) {
        deliveryManAssignedWork.setStatus(DeliveryPartnerEnum.DISPATCH);
        assignedWorkRepository.save(deliveryManAssignedWork);
        return "DISPATCHED";
    }

    public String deliverd(DeliveryManAssignedWork deliveryManAssignedWork) {
        deliveryManAssignedWork.setStatus(DeliveryPartnerEnum.DELIVERED);
        assignedWorkRepository.save(deliveryManAssignedWork);
        return "DELIVERD";
    }


    @Transactional
    public DeliveryManAssignedWork findByShipmentId(long shipmentId) {
        System.out.println(shipmentId);
        System.out.println(assignedWorkRepository.findByShipmentDetails_Id(shipmentId));
        return assignedWorkRepository.findByShipmentDetails_Id(shipmentId).orElseThrow(() -> new RuntimeException("Error!"));
    }
}
