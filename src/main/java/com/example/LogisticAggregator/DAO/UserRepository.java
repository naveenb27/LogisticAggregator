package com.example.LogisticAggregator.DAO;

import com.example.LogisticAggregator.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    public User findByEmail(String email);
    public Optional<User> findById(Long id);
}
