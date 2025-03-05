package com.example.LogisticAggregator.Service;

import com.example.LogisticAggregator.Model.AppUser;

public interface AppUserService {

    public AppUser findByEmail(String email);
}
