package com.aitravelplanner.service;

import com.aitravelplanner.model.TravelPlan;
import java.util.List;

public interface TravelService {

    TravelPlan createTravelPlan(TravelPlan travelPlan);
    TravelPlan getTravelPlanById(String planId);
    List<TravelPlan> getTravelPlansByUserId(String userId);
    TravelPlan updateTravelPlan(String planId, TravelPlan travelPlan);
    void deleteTravelPlan(String planId);
    List<TravelPlan> searchTravelPlans(String keyword);
    TravelPlan updateTravelPlanStatus(String planId, String status);
}