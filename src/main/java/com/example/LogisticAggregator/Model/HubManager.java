package com.example.LogisticAggregator.Model;


import com.example.LogisticAggregator.Enum.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class HubManager implements AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hub_manager_sequence")
    @SequenceGenerator(name = "hub_manager_sequence", sequenceName = "hub_manager_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    @ManyToOne
    @JoinColumn(name = "hub_id", nullable = false)
    private Hub hub;

    private String role = UserRole.HUB_MANAGER.toString();
}
