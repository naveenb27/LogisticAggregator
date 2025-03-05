package com.example.LogisticAggregator.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogisticProviderDTO {
    private Long id;
    private String email;
    private String companyName;
    private String phoneNumber;
}
