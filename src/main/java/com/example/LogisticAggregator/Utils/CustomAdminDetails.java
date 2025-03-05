package com.example.LogisticAggregator.Utils;

import com.example.LogisticAggregator.Model.Admin;
import com.example.LogisticAggregator.Model.AppUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CustomAdminDetails implements UserDetails {

    private final AppUser user;
    public CustomAdminDetails(AppUser user) {
        System.out.println(user);
        System.out.println("Creating CustomAdminDetails for: " + user.getEmail());
        System.out.println("User role: " + user.getRole());
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("Role: "+user.getRole());
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    public Long Id() {
        return user.getId();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
