package com.aitravelplanner.service;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;

import java.util.List;

public interface TripService {

    Trip createTrip(String userId, TripCreateRequest request);
    List<Trip> getUserTrips(String userId);
    Trip getTripById(String tripId);
    Trip updateTrip(String userId, String tripId, Trip trip);
    void deleteTrip(String userId, String tripId);
}