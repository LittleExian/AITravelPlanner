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
            User currentUser = userService.getCurrentUser();
            // 创建Trip对象并设置当前用户为行程所有者
            Trip trip = new Trip();
            trip.setUserId(currentUser.getId());
            // 从request复制其他属性
            trip.setTitle(request.getTitle());
            trip.setDestination(request.getDestination());
            trip.setStartDate(request.getStartDate());
            trip.setEndDate(request.getEndDate());
            trip.setDescription(request.getDescription());
            trip.setTags(request.getTags());
            trip.setCoverImage(request.getCoverImage());
            trip.setPublic(request.isPublic());
            
            Trip createdTrip = tripService.createTrip(trip);
            logger.info("用户 {} 成功创建行程: {}", currentUser.getEmail(), createdTrip.getId());
            return ResponseEntity.ok(createdTrip);
        } catch (Exception e) {
            logger.error("创建行程失败: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Trip>> getUserTrips(@PathVariable String userId) {
        try {
            // 获取当前认证用户
            User currentUser = userService.getCurrentUser();
            
            // 日志记录当前用户和请求的用户ID
            logger.info("获取行程请求 - 当前用户: {}, 请求用户ID: {}", 
                        currentUser.getEmail(), userId);
            
            // 权限检查：用户只能查看自己的行程
            if (currentUser.getId().equals(userId)) {
                List<Trip> trips = tripService.getUserTrips(userId);
                logger.info("成功获取到 {} 个行程", trips.size());
                return ResponseEntity.ok(trips);
            } else {
                logger.warn("权限拒绝 - 用户 {} 尝试访问用户 {} 的行程", 
                           currentUser.getId(), userId);
                // 对于非自己的行程，只返回公开行程
                List<Trip> publicTrips = tripService.getPublicTripsByUserId(userId);
                return ResponseEntity.ok(publicTrips);
            }
        } catch (Exception e) {
            logger.error("获取行程失败: {}", e.getMessage());
            // 添加更详细的错误日志
            if (e instanceof SecurityException || e.getMessage().contains("未认证")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<?> getTripById(@PathVariable String tripId) {
        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        // 检查访问权限：行程公开或属于当前用户
        User currentUser = userService.getCurrentUser();
        if (!trip.isPublic() && !trip.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限访问此行程");
        }

        return ResponseEntity.ok(trip);
    }

    @PutMapping("/{tripId}")
    public ResponseEntity<?> updateTrip(@PathVariable String tripId, @RequestBody Trip trip) {
        // 检查权限：只能更新自己的行程
        User currentUser = userService.getCurrentUser();
        Trip existingTrip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (!existingTrip.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限更新此行程");
        }

        Trip updatedTrip = tripService.updateTrip(tripId, trip);
        return ResponseEntity.ok(updatedTrip);
    }

    @DeleteMapping("/{tripId}")
    public ResponseEntity<?> deleteTrip(@PathVariable String tripId) {
        // 检查权限：只能删除自己的行程
        User currentUser = userService.getCurrentUser();
        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (!trip.getUserId().equals(currentUser.getId())) {
            return ResponseEntity.status(403).body("无权限删除此行程");
        }

        tripService.deleteTrip(tripId);
        return ResponseEntity.ok("行程已删除");
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