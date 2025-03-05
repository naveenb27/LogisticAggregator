package com.example.LogisticAggregator.Utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OtpDetails {
    private String otp;
    private long expiryTime;
}
