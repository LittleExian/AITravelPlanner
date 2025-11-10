package com.aitravelplanner.controller;

import com.aitravelplanner.model.TravelRoute;
import com.aitravelplanner.service.TravelRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/routes")
public class TravelRouteController {

    @Autowired
    private TravelRouteService routeService;

    // 创建旅行路线
    @PostMapping
    public ResponseEntity<?> createRoute(@RequestBody TravelRoute route) {
        try {
            TravelRoute createdRoute = routeService.createRoute(route);
            return ResponseEntity.ok(createdRoute);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("创建旅行路线失败: " + e.getMessage());
        }
    }

    // 获取行程的所有路线
    @GetMapping("/trip/{tripId}")
    public ResponseEntity<?> getRoutesByTripId(@PathVariable String tripId) {
        try {
            List<TravelRoute> routes = routeService.getRoutesByTripId(tripId);
            return ResponseEntity.ok(routes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取旅行路线失败: " + e.getMessage());
        }
    }

    // 获取单个路线详情
    @GetMapping("/{routeId}")
    public ResponseEntity<?> getRouteById(@PathVariable String routeId) {
        try {
            Optional<TravelRoute> routeOptional = routeService.getRouteById(routeId);
            if (routeOptional.isPresent()) {
                return ResponseEntity.ok(routeOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("旅行路线不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取旅行路线失败: " + e.getMessage());
        }
    }

    // 更新旅行路线
    @PutMapping("/{routeId}")
    public ResponseEntity<?> updateRoute(@PathVariable String routeId, @RequestBody TravelRoute route) {
        try {
            TravelRoute updatedRoute = routeService.updateRoute(routeId, route);
            return ResponseEntity.ok(updatedRoute);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("更新旅行路线失败: " + e.getMessage());
        }
    }

    // 删除旅行路线
    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable String routeId) {
        try {
            routeService.deleteRoute(routeId);
            return ResponseEntity.ok("旅行路线已删除");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("删除旅行路线失败: " + e.getMessage());
        }
    }
}