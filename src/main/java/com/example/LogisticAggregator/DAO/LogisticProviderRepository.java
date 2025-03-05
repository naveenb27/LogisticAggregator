package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Model.LogisticProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogisticProviderRepository extends JpaRepository<LogisticProvider, Long> {
    public LogisticProvider findByEmail(String email);
    public LogisticProvider findById(long id);
}
