package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    public Admin findByEmail(String email);
}
