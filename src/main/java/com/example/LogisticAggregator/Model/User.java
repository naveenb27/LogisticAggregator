package com.example.LogisticAggregator.Model;


import com.example.LogisticAggregator.Enum.UserRole;
import com.example.LogisticAggregator.Service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "\"user\"")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements AppUser{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_gen")
    @SequenceGenerator(name = "user_seq_gen", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String address;

    private String phoneNumber;

    private String email;

    private String password;

    private String role = "USER";
}
