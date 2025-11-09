package com.aitravelplanner.controller;

import com.aitravelplanner.model.TravelPlan;
import com.aitravelplanner.service.TravelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-plans")
public class TravelController {

    @Autowired
    private TravelService travelService;

    @PostMapping
    public ResponseEntity<TravelPlan> createTravelPlan(@RequestBody TravelPlan travelPlan) {
        TravelPlan createdPlan = travelService.createTravelPlan(travelPlan);
        return ResponseEntity.ok(createdPlan);
    }

    @GetMapping("/{planId}")
    public ResponseEntity<TravelPlan> getTravelPlan(@PathVariable String planId) {
        TravelPlan travelPlan = travelService.getTravelPlanById(planId);
        return ResponseEntity.ok(travelPlan);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TravelPlan>> getTravelPlansByUserId(@PathVariable String userId) {
        List<TravelPlan> travelPlans = travelService.getTravelPlansByUserId(userId);
        return ResponseEntity.ok(travelPlans);
    }

    @PutMapping("/{planId}")
    public ResponseEntity<TravelPlan> updateTravelPlan(@PathVariable String planId, @RequestBody TravelPlan travelPlan) {
        TravelPlan updatedPlan = travelService.updateTravelPlan(planId, travelPlan);
        return ResponseEntity.ok(updatedPlan);
    }

    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> deleteTravelPlan(@PathVariable String planId) {
        travelService.deleteTravelPlan(planId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TravelPlan>> searchTravelPlans(@RequestParam String keyword) {
        List<TravelPlan> travelPlans = travelService.searchTravelPlans(keyword);
        return ResponseEntity.ok(travelPlans);
    }

    @PutMapping("/{planId}/status")
    public ResponseEntity<TravelPlan> updateTravelPlanStatus(@PathVariable String planId, @RequestParam String status) {
        TravelPlan updatedPlan = travelService.updateTravelPlanStatus(planId, status);
        return ResponseEntity.ok(updatedPlan);
    }
}