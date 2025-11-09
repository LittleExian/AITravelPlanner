package com.aitravelplanner.repository;

import com.aitravelplanner.model.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, String> {

    Optional<Budget> findByTripId(String tripId);
    List<Budget> findByUserId(String userId);
    void deleteByTripId(String tripId);
}