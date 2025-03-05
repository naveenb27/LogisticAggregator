package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.DAO.DeliveryPartnersRepository;
import com.example.LogisticAggregator.DAO.HubRepository;
import com.example.LogisticAggregator.DAO.ShipmentDetailsRepository;
import com.example.LogisticAggregator.Model.DeliveryPartners;
import com.example.LogisticAggregator.Model.Hub;
import com.example.LogisticAggregator.Model.ShipmentDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HubService {

    private final HubRepository hubRepository;
    private final DeliveryPartnersRepository deliveryPartnersRepository;
    private final ShipmentDetailsRepository shipmentDetailsRepository;

    HubService(HubRepository hubRepository, DeliveryPartnersRepository deliveryPartnersRepository, ShipmentDetailsRepository shipmentDetailsRepository) {
        this.hubRepository = hubRepository;
        this.shipmentDetailsRepository = shipmentDetailsRepository;
        this.deliveryPartnersRepository = deliveryPartnersRepository;
    }

    public String createHub(Hub hub) {
        hubRepository.save(hub);

        return "CREATED HUB";
    }

    public List<Hub> getAllHub() {
        return hubRepository.findAll();
    }

    public Optional<Hub> getHubById(Long id) {
        return hubRepository.findById(id);
    }

    public ShipmentDetails assignShipmentToHub(Long id, Long ShipmentId){
        DeliveryPartners deliveryPartners = deliveryPartnersRepository.findById(id).orElseThrow(() -> new RuntimeException("Error !!!"));
        Hub hub = deliveryPartners.getHub();

        ShipmentDetails shipmentDetails = shipmentDetailsRepository.getById(ShipmentId);
        shipmentDetails.setHub(hub);
        shipmentDetailsRepository.save(shipmentDetails);

        return shipmentDetails;
    }

    public List<ShipmentDetails> getShipmentsByHubId(Long hubId) {
        Hub hub = hubRepository.findById(hubId).orElse(null);
        return (hub != null) ? hub.getShipmentDetailsList() : null;
    }
}
