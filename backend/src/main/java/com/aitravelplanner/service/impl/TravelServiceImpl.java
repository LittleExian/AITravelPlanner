package com.aitravelplanner.service.impl;

import com.aitravelplanner.exception.ResourceNotFoundException;
import com.aitravelplanner.model.TravelPlan;
import com.aitravelplanner.repository.TravelRepository;
import com.aitravelplanner.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TravelServiceImpl implements TravelService {

    @Autowired
    private TravelRepository travelRepository;

    @Override
    public TravelPlan createTravelPlan(TravelPlan travelPlan) {
        travelPlan.setId(UUID.randomUUID().toString());
        return travelRepository.save(travelPlan);
    }

    @Override
    public TravelPlan getTravelPlanById(String planId) {
        return travelRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + planId));
    }

    @Override
    public List<TravelPlan> getTravelPlansByUserId(String userId) {
        return travelRepository.findByUserId(userId);
    }

    @Override
    public TravelPlan updateTravelPlan(String planId, TravelPlan travelPlan) {
        TravelPlan existingPlan = travelRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + planId));

        existingPlan.setTitle(travelPlan.getTitle());
        existingPlan.setDescription(travelPlan.getDescription());
        existingPlan.setDestination(travelPlan.getDestination());
        existingPlan.setStartDate(travelPlan.getStartDate());
        existingPlan.setEndDate(travelPlan.getEndDate());
        existingPlan.setStatus(travelPlan.getStatus());
        existingPlan.setTags(travelPlan.getTags());
        existingPlan.setDayItineraries(travelPlan.getDayItineraries());

        return travelRepository.save(existingPlan);
    }

    @Override
    public void deleteTravelPlan(String planId) {
        TravelPlan travelPlan = travelRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + planId));
        travelRepository.delete(travelPlan);
    }

    @Override
    public List<TravelPlan> searchTravelPlans(String keyword) {
        return travelRepository.findByDestinationContaining(keyword);
    }

    @Override
    public TravelPlan updateTravelPlanStatus(String planId, String status) {
        TravelPlan travelPlan = travelRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Travel plan not found with id: " + planId));
        
        // 验证状态值是否有效
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        
        travelPlan.setStatus(status);
        return travelRepository.save(travelPlan);
    }

    private boolean isValidStatus(String status) {
        return "DRAFT".equals(status) || "ACTIVE".equals(status) || 
               "COMPLETED".equals(status) || "CANCELLED".equals(status);
    }
}