package com.aitravelplanner.service.impl;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.repository.TripRepository;
import com.aitravelplanner.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Override
    public Trip createTrip(String userId, TripCreateRequest request) {
        Trip trip = new Trip();
        trip.setUserId(userId);
        trip.setTitle(request.getTitle());
        trip.setDestination(request.getDestination());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setDescription(request.getDescription());
        trip.setTags(request.getTags());
        trip.setCoverImage(request.getCoverImage());
        trip.setPublic(request.isPublic());
        trip.setCreatedAt(new Date());
        trip.setUpdatedAt(new Date());

        return tripRepository.save(trip);
    }

    @Override
    public List<Trip> getUserTrips(String userId) {
        return tripRepository.findByUserId(userId);
    }

    @Override
    public Optional<Trip> getTripById(String tripId) {
        return tripRepository.findById(tripId);
    }

    @Override
    public Trip updateTrip(String tripId, Trip trip) {
        Trip existingTrip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        // 更新行程信息
        existingTrip.setTitle(trip.getTitle());
        existingTrip.setDestination(trip.getDestination());
        existingTrip.setStartDate(trip.getStartDate());
        existingTrip.setEndDate(trip.getEndDate());
        existingTrip.setDescription(trip.getDescription());
        existingTrip.setTags(trip.getTags());
        existingTrip.setCoverImage(trip.getCoverImage());
        existingTrip.setPublic(trip.isPublic());
        existingTrip.setUpdatedAt(new Date());

        return tripRepository.save(existingTrip);
    }

    @Override
    public void deleteTrip(String tripId) {
        tripRepository.deleteById(tripId);
    }

    @Override
    public List<Trip> getPublicTrips() {
        return tripRepository.findByIsPublicTrue();
    }

    @Override
    public List<Trip> searchPublicTripsByDestination(String destination) {
        return tripRepository.findByDestinationContainingAndIsPublicTrue(destination);
    }
}