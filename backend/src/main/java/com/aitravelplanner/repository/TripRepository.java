package com.aitravelplanner.repository;

import com.aitravelplanner.model.Trip;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends MongoRepository<Trip, String> {
    List<Trip> findByUserId(String userId);
    List<Trip> findByUserIdAndIsPublicTrue(String userId);
    List<Trip> findByIsPublicTrue();
    List<Trip> findByDestinationContainingAndIsPublicTrue(String destination);
}