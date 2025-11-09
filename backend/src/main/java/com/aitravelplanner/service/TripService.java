package com.aitravelplanner.service;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;

import java.util.List;
import java.util.Optional;

public interface TripService {
    Trip createTrip(Trip trip);
    List<Trip> getUserTrips(String userId);
    List<Trip> getPublicTripsByUserId(String userId);
    Optional<Trip> getTripById(String tripId);
    Trip updateTrip(String tripId, Trip trip);
    void deleteTrip(String tripId);
    List<Trip> getPublicTrips();
    List<Trip> searchPublicTrips(String destination);
}