package com.aitravelplanner.controller;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @PostMapping
    public ResponseEntity<Trip> createTrip(@RequestBody TripCreateRequest request, @RequestParam String userId) {
        Trip trip = tripService.createTrip(userId, request);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getUserTrips(@PathVariable String userId) {
        List<Trip> trips = tripService.getUserTrips(userId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<Trip> getTrip(@PathVariable String tripId) {
        Trip trip = tripService.getTripById(tripId);
        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<Trip> updateTrip(@PathVariable String tripId, @RequestBody Trip trip, @RequestParam String userId) {
        Trip updatedTrip = tripService.updateTrip(userId, tripId, trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<Void> deleteTrip(@PathVariable String tripId, @RequestParam String userId) {
        tripService.deleteTrip(userId, tripId);
        return ResponseEntity.noContent().build();
    }
}