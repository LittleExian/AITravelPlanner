package com.aitravelplanner.service;

import com.aitravelplanner.model.TravelRoute;

import java.util.List;
import java.util.Optional;

public interface TravelRouteService {
    TravelRoute createRoute(TravelRoute route);
    List<TravelRoute> getRoutesByTripId(String tripId);
    Optional<TravelRoute> getRouteById(String routeId);
    TravelRoute updateRoute(String routeId, TravelRoute route);
    void deleteRoute(String routeId);
    void deleteRoutesByTripId(String tripId);
}