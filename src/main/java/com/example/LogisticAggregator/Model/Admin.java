package com.example.LogisticAggregator.Model;

import com.example.LogisticAggregator.Enum.UserRole;
import com.example.LogisticAggregator.Service.AdminService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin implements AppUser{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_seq_gen")
    @SequenceGenerator(name = "admin_seq_gen", sequenceName = "admin_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String email;
    private String password;

    private String role = UserRole.USER.toString();
}
