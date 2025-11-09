package com.aitravelplanner.repository;

import com.aitravelplanner.model.TravelPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TravelRepository extends JpaRepository<TravelPlan, String> {
    List<TravelPlan> findByUserId(String userId);
    List<TravelPlan> findByDestinationContaining(String keyword);
    List<TravelPlan> findByUserIdAndStatus(String userId, String status);
}