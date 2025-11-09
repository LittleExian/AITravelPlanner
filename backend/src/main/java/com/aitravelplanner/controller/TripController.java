package com.aitravelplanner.controller;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.model.User;
import com.aitravelplanner.service.TripService;
import com.aitravelplanner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    @Autowired
    private TripService tripService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<?> createTrip(@Valid @RequestBody TripCreateRequest request) {
        User currentUser = userService.getCurrentUser();
        Trip trip = tripService.createTrip(currentUser.getId(), request);
        return ResponseEntity.ok(trip);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserTrips(@PathVariable String userId) {
        // 检查是否是当前用户或有权限访问
        User currentUser = userService.getCurrentUser();
        if (!currentUser.getId().equals(userId)) {
            return ResponseEntity.status(403).body("无权限访问其他用户的行程");
        }

        List<Trip> trips = tripService.getUserTrips(userId);
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTripById(@PathVariable String tripId) {
        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        // 检查访问权限：行程公开或属于当前用户
        User currentUser = userService.getCurrentUser();
        if (!trip.isPublic() && !trip.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限访问此行程");
        }

        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTrip(@PathVariable String tripId, @RequestBody Trip trip) {
        // 检查权限：只能更新自己的行程
        User currentUser = userService.getCurrentUser();
        Trip existingTrip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (!existingTrip.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限更新此行程");
        }

        Trip updatedTrip = tripService.updateTrip(tripId, trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable String tripId) {
        // 检查权限：只能删除自己的行程
        User currentUser = userService.getCurrentUser();
        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (!trip.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限删除此行程");
        }

        tripService.deleteTrip(tripId);
        return ResponseEntity.ok("行程已删除");
    }

    @GetMapping("/public/all")
    public ResponseEntity<?> getPublicTrips() {
        List<Trip> trips = tripService.getPublicTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/public/search")
    public ResponseEntity<?> searchPublicTrips(@RequestParam String destination) {
        List<Trip> trips = tripService.searchPublicTripsByDestination(destination);
        return ResponseEntity.ok(trips);
    }
}