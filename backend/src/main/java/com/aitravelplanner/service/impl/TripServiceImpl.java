package com.aitravelplanner.service.impl;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.repository.TripRepository;
import com.aitravelplanner.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TripServiceImpl implements TripService {

    @Autowired
    private TripRepository tripRepository;

    @Override
    public Trip createTrip(String userId, TripCreateRequest request) {
        Trip trip = new Trip();
        trip.setUserId(userId);
        trip.setName(request.getName());
        trip.setDestination(request.getDestination());
        trip.setStartDate(request.getStartDate());
        trip.setEndDate(request.getEndDate());
        trip.setBudget(request.getBudget());
        trip.setPeopleCount(request.getPeopleCount());
        trip.setPreferences(request.getPreferences());
        trip.setDescription(request.getDescription());
        
        // 设置行程状态
        Date now = new Date();
        if (now.after(request.getEndDate())) {
            trip.setStatus(Trip.TripStatus.COMPLETED);
        } else if (now.after(request.getStartDate())) {
            trip.setStatus(Trip.TripStatus.ONGOING);
        } else {
            trip.setStatus(Trip.TripStatus.UPCOMING);
        }
        
        return tripRepository.save(trip);
    }

    @Override
    public List<Trip> getUserTrips(String userId) {
        return tripRepository.findByUserId(userId);
    }

    @Override
    public Trip getTripById(String tripId) {
        return tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("行程不存在"));
    }

    @Override
    public Trip updateTrip(String userId, String tripId, Trip trip) {
        // 验证行程是否属于当前用户
        Trip existingTrip = getTripById(tripId);
        if (!existingTrip.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此行程");
        }
        
        // 更新行程信息
        existingTrip.setName(trip.getName());
        existingTrip.setDestination(trip.getDestination());
        existingTrip.setStartDate(trip.getStartDate());
        existingTrip.setEndDate(trip.getEndDate());
        existingTrip.setBudget(trip.getBudget());
        existingTrip.setPeopleCount(trip.getPeopleCount());
        existingTrip.setPreferences(trip.getPreferences());
        existingTrip.setDescription(trip.getDescription());
        existingTrip.setDays(trip.getDays());
        existingTrip.setUpdatedAt(new Date());
        
        return tripRepository.save(existingTrip);
    }

    @Override
    public void deleteTrip(String userId, String tripId) {
        // 验证行程是否属于当前用户
        Trip trip = getTripById(tripId);
        if (!trip.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此行程");
        }
        
        tripRepository.deleteByUserIdAndId(userId, tripId);
    }
}