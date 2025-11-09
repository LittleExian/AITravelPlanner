package com.aitravelplanner.service;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    Trip createTrip(String userId, TripCreateRequest request);
    List<Trip> getUserTrips(String userId);
    Optional<Trip> getTripById(String tripId);
    Trip updateTrip(String tripId, Trip trip);
    void deleteTrip(String tripId);
    List<Trip> getPublicTrips();
    List<Trip> searchPublicTripsByDestination(String destination);
}