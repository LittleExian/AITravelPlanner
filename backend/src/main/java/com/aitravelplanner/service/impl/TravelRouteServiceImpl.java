package com.aitravelplanner.service.impl;

import com.aitravelplanner.model.TravelRoute;
import com.aitravelplanner.repository.TravelRouteRepository;
import com.aitravelplanner.service.TravelRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TravelRouteServiceImpl implements TravelRouteService {

    @Autowired
    private TravelRouteRepository routeRepository;

    @Override
    public TravelRoute createRoute(TravelRoute route) {
        Date now = new Date();
        route.setCreatedAt(now);
        route.setUpdatedAt(now);
        return routeRepository.save(route);
    }

    @Override
    public List<TravelRoute> getRoutesByTripId(String tripId) {
        return routeRepository.findByTripIdOrderByDayNumberAsc(tripId);
    }

    @Override
    public Optional<TravelRoute> getRouteById(String routeId) {
        return routeRepository.findById(routeId);
    }

    @Override
    public TravelRoute updateRoute(String routeId, TravelRoute route) {
        TravelRoute existingRoute = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("旅行路线不存在"));
        
        existingRoute.setDayNumber(route.getDayNumber());
        existingRoute.setTransportation(route.getTransportation());
        existingRoute.setAttractions(route.getAttractions());
        existingRoute.setRestaurants(route.getRestaurants());
        existingRoute.setDescription(route.getDescription());
        existingRoute.setEstimatedCost(route.getEstimatedCost());
        existingRoute.setUpdatedAt(new Date());
        
        return routeRepository.save(existingRoute);
    }

    @Override
    public void deleteRoute(String routeId) {
        routeRepository.deleteById(routeId);
    }

    @Override
    public void deleteRoutesByTripId(String tripId) {
        routeRepository.deleteByTripId(tripId);
    }
}