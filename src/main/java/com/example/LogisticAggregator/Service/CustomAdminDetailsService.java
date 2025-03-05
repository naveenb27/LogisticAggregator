package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.DAO.*;
import com.example.LogisticAggregator.Model.*;
import com.example.LogisticAggregator.Utils.CustomAdminDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CustomAdminDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final LogisticProviderRepository logisticProviderRepository;
    private final UserRepository userRepository;
    private final HubManagerRepository hubManagerRepository;
    private final DeliveryPartnersRepository deliveryPartnersRepository;

    public CustomAdminDetailsService(AdminRepository adminRepository,
                                     HubManagerRepository hubManagerRepository,
                                     DeliveryPartnersRepository deliveryPartnersRepository,
                                     LogisticProviderRepository logisticProviderRepository,
                                     UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.hubManagerRepository = hubManagerRepository;
        this.logisticProviderRepository = logisticProviderRepository;
        this.userRepository = userRepository;
        this.deliveryPartnersRepository = deliveryPartnersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email);
        if (!Objects.isNull(admin)) {
            return new CustomAdminDetails(admin);
        }

        LogisticProvider logisticProvider = logisticProviderRepository.findByEmail(email);
        if (!Objects.isNull(logisticProvider)) {
            return new CustomAdminDetails(logisticProvider);
        }

        DeliveryPartners deliveryPartner = deliveryPartnersRepository.findByEmail(email);
        if (!Objects.isNull(deliveryPartner)) {
            return new CustomAdminDetails(deliveryPartner);
        }

        User user = userRepository.findByEmail(email);
        System.out.println("In custom admin service: "+user);
        if (!Objects.isNull(user)) {
            System.out.println("Entered");
            System.out.println(new CustomAdminDetails(user));
            return new CustomAdminDetails(user);
        }

        HubManager hubManager = hubManagerRepository.findByEmail(email);
        if(!Objects.isNull(hubManager)) {
            return new CustomAdminDetails(hubManager);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
