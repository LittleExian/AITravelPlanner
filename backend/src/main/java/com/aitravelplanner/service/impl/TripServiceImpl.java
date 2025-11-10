package com.aitravelplanner.service.impl;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.repository.TripRepository;
import com.aitravelplanner.service.TripService;
import com.aitravelplanner.service.TravelRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;
    
    @Autowired
    private TravelRouteService travelRouteService;

    @Override
    public Trip createTrip(Trip trip) {
        // 确保设置创建和更新时间
        if (trip.getCreatedAt() == null) {
            trip.setCreatedAt(new Date());
        }
        trip.setUpdatedAt(new Date());
        return tripRepository.save(trip);
    }
    
    @Override
    public List<Trip> getPublicTripsByUserId(String userId) {
        // 移除公开行程限制，返回用户所有行程
        return tripRepository.findByUserId(userId);
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
        // 更新新增字段
        existingTrip.setBudgetAmount(trip.getBudgetAmount());
        existingTrip.setPeopleCount(trip.getPeopleCount());
        existingTrip.setTravelPreferences(trip.getTravelPreferences());
        existingTrip.setUpdatedAt(new Date());

        return tripRepository.save(existingTrip);
    }

    @Override
    public void deleteTrip(String tripId) {
        // 先删除相关的旅行路线
        travelRouteService.deleteRoutesByTripId(tripId);
        // 再删除行程
        tripRepository.deleteById(tripId);
    }

    @Override
    public List<Trip> getPublicTrips() {
        // 返回所有行程（不再区分公开/私有）
        return tripRepository.findAll();
    }

    @Override
    public List<Trip> searchPublicTrips(String destination) {
        // 搜索所有行程中的目的地，不再区分公开/私有
        return tripRepository.findByDestinationContaining(destination);
    }
}