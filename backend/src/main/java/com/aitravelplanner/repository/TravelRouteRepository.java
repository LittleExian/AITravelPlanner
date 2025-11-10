package com.aitravelplanner.repository;

import com.aitravelplanner.model.TravelRoute;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelRouteRepository extends MongoRepository<TravelRoute, String> {
    List<TravelRoute> findByTripId(String tripId);
    List<TravelRoute> findByTripIdOrderByDayNumberAsc(String tripId);
    void deleteByTripId(String tripId);
}