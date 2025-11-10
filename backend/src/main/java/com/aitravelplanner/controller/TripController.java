package com.aitravelplanner.controller;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.model.User;
import com.aitravelplanner.service.TripService;
import com.aitravelplanner.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);
    
    @Autowired
    private TripService tripService;

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Trip> createTrip(@Valid @RequestBody TripCreateRequest request) {
        try {
            // 创建Trip对象并从请求中获取userId
            Trip trip = new Trip();
            trip.setUserId(request.getUserId());
            // 从request复制其他属性
            trip.setTitle(request.getTitle());
            trip.setDestination(request.getDestination());
            trip.setStartDate(request.getStartDate());
            trip.setEndDate(request.getEndDate());
            trip.setDescription(request.getDescription());
            trip.setTags(request.getTags());
            trip.setCoverImage(request.getCoverImage());
            // 设置新增字段
            trip.setBudgetAmount(request.getBudgetAmount());
            trip.setPeopleCount(request.getPeopleCount());
            trip.setTravelPreferences(request.getTravelPreferences());
            
            Trip createdTrip = tripService.createTrip(trip);
            logger.info("用户ID {} 成功创建行程: {}", request.getUserId(), createdTrip.getId());
            return ResponseEntity.ok(createdTrip);
        } catch (Exception e) {
            logger.error("创建行程失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getUserTrips(@PathVariable String userId) {
        try {
            // 直接根据用户ID获取行程，不需要认证和权限检查
            logger.info("获取用户 {} 的行程请求", userId);
            
            List<Trip> trips = tripService.getUserTrips(userId);
            logger.info("成功获取到用户 {} 的 {} 个行程", userId, trips.size());
            return ResponseEntity.ok(trips);
        } catch (Exception e) {
            logger.error("获取行程失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTripById(@PathVariable String tripId) {
        try {
            logger.info("获取行程 {} 的请求", tripId);
            
            Trip trip = tripService.getTripById(tripId)
                    .orElseThrow(() -> new RuntimeException("行程不存在"));
            
            logger.info("成功获取行程 {}", tripId);
            return ResponseEntity.ok(trip);
        } catch (Exception e) {
            logger.error("获取行程失败: {}", e.getMessage());
            if (e.getMessage().contains("行程不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("行程不存在");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("获取行程失败");
        }
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTrip(@PathVariable String tripId, @RequestBody Trip trip) {
        try {
            logger.info("更新行程 {} 的请求", tripId);
            
            Trip existingTrip = tripService.getTripById(tripId)
                    .orElseThrow(() -> new RuntimeException("行程不存在"));

            Trip updatedTrip = tripService.updateTrip(tripId, trip);
            logger.info("行程 {} 更新成功", tripId);
            return ResponseEntity.ok(updatedTrip);
        } catch (Exception e) {
            logger.error("更新行程失败: {}", e.getMessage());
            if (e.getMessage().contains("行程不存在")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("行程不存在");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新行程失败");
        }
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable String tripId) {
        try {
            logger.info("尝试删除行程 {}", tripId);
            
            Trip trip = tripService.getTripById(tripId)
                    .orElseThrow(() -> new RuntimeException("行程不存在"));
            
            tripService.deleteTrip(tripId);
            logger.info("行程 {} 删除成功", tripId);
            return ResponseEntity.ok("行程已删除");
        } catch (Exception e) {
            logger.error("删除行程失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("删除行程失败");
        }
    }

    @GetMapping("/public/all")
    public ResponseEntity<?> getPublicTrips() {
        List<Trip> trips = tripService.getPublicTrips();
        return ResponseEntity.ok(trips);
    }

    @GetMapping("/public/search")
    public ResponseEntity<?> searchPublicTrips(@RequestParam String destination) {
        List<Trip> trips = tripService.searchPublicTrips(destination);
        return ResponseEntity.ok(trips);
    }
}