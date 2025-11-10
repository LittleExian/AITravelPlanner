package com.aitravelplanner.controller;

import com.aitravelplanner.dto.TripCreateRequest;
import com.aitravelplanner.model.Budget;
import com.aitravelplanner.model.Expense;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.model.TravelRoute;
import com.aitravelplanner.model.User;
import com.aitravelplanner.service.TripService;
import com.aitravelplanner.service.TravelRouteService;
import com.aitravelplanner.service.UserService;
import com.aitravelplanner.service.BudgetService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private static final Logger logger = LoggerFactory.getLogger(TripController.class);
    
    @Autowired
    private TripService tripService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private TravelRouteService travelRouteService;
    
    @Autowired
    private BudgetService budgetService;
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
    
    // AI生成行程端点
    @PostMapping("/ai-generate")
    public ResponseEntity<Trip> aiGenerateTrip(@Valid @RequestBody TripCreateRequest request) {
        try {
            logger.info("收到AI生成行程请求，用户ID: {}, 目的地: {}", request.getUserId(), request.getDestination());
            
            // 1. 创建基本行程信息
            Trip trip = new Trip();
            trip.setUserId(request.getUserId());
            trip.setTitle(request.getTitle());
            trip.setDestination(request.getDestination());
            trip.setStartDate(request.getStartDate());
            trip.setEndDate(request.getEndDate());
            trip.setDescription(request.getDescription() != null ? request.getDescription() : "AI生成的行程计划");
            trip.setBudgetAmount(request.getBudgetAmount());
            trip.setPeopleCount(request.getPeopleCount());
            trip.setTravelPreferences(request.getTravelPreferences());
            
            // 保存基本行程信息
            Trip createdTrip = tripService.createTrip(trip);
            logger.info("基本行程创建成功，行程ID: {}", createdTrip.getId());
            
            // 2. 调用大模型API生成行程详细信息（这里使用模拟数据，实际项目中需要集成真实的大模型API）
            List<TravelRoute> generatedRoutes = generateRoutesWithAI(createdTrip.getId(), request);
            
            // 3. 保存生成的路线信息
            for (TravelRoute route : generatedRoutes) {
                route.setTripId(createdTrip.getId());
                travelRouteService.createRoute(route);
            }
            logger.info("成功保存 {} 条旅行路线", generatedRoutes.size());
            
            // 4. 使用预算分配API接口更新预算分配
            Map<String, Double> allocations = generateBudgetAllocations(request.getBudgetAmount());
            
            // 调用预算分配API（通过service层实现，实际会调用/api/budgets/trip/{tripId}/allocations接口）
            // AI更新budget时只需要分配allocation
            budgetService.updateBudgetAllocations(createdTrip.getId(), allocations);
            logger.info("成功更新行程预算分配信息");
            
            // 返回完整的行程信息
            createdTrip = tripService.getTripById(createdTrip.getId()).orElseThrow();
            logger.info("AI行程生成完成，返回行程信息");
            return ResponseEntity.ok(createdTrip);
        } catch (Exception e) {
            logger.error("AI生成行程失败: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 模拟AI生成旅行路线（实际项目中应替换为大模型API调用）
    private List<TravelRoute> generateRoutesWithAI(String tripId, TripCreateRequest request) {
        List<TravelRoute> routes = new ArrayList<>();
        
        // 计算行程天数
        long diffInMillies = request.getEndDate().getTime() - request.getStartDate().getTime();
        int days = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;
        
        // 生成每天的路线
        Date currentDate = new Date(request.getStartDate().getTime());
        for (int i = 1; i <= Math.min(days, 7); i++) { // 最多生成7天
            TravelRoute route = new TravelRoute();
            route.setTripId(tripId);
            route.setDayNumber(i);
            route.setCreatedAt(new Date());
            route.setUpdatedAt(new Date());
            
            // 根据目的地和旅行偏好生成路线内容
            String destination = request.getDestination();
            List<String> preferences = request.getTravelPreferences();
            
            // 模拟生成交通信息
            route.setTransportation("公共交通/步行");
            
            // 模拟生成景点信息
            List<String> attractions = new ArrayList<>();
            if (preferences != null && preferences.contains("文化")) {
                attractions.add(destination + "博物馆");
                attractions.add(destination + "历史街区");
            } else if (preferences != null && preferences.contains("自然")) {
                attractions.add(destination + "国家公园");
                attractions.add(destination + "海滩/湖泊");
            } else if (preferences != null && preferences.contains("美食")) {
                attractions.add(destination + "美食街");
                attractions.add(destination + "当地市场");
            } else {
                attractions.add(destination + "主要景点" + i);
                attractions.add(destination + "热门观光地" + i);
            }
            route.setAttractions(attractions);
            
            // 模拟生成景点经纬度信息
            List<Map<String, Object>> attractionDes = new ArrayList<>();
            for (int j = 0; j < attractions.size(); j++) {
                Map<String, Object> des = new HashMap<>();
                des.put("name", attractions.get(j));
                // 生成随机经纬度（模拟数据）
                // 这里假设目的地是一个主要城市，我们生成该城市附近的随机坐标
                double baseLat = 30.0 + (Math.random() - 0.5) * 2; // 基础纬度，可根据不同目的地调整
                double baseLng = 120.0 + (Math.random() - 0.5) * 2; // 基础经度，可根据不同目的地调整
                des.put("latitude", Math.round(baseLat * 1000000) / 1000000.0); // 保留6位小数
                des.put("longitude", Math.round(baseLng * 1000000) / 1000000.0); // 保留6位小数
                des.put("address", destination + "市某区某路" + j + "号");
                attractionDes.add(des);
            }
            route.setAttractionDes(attractionDes);
            
            // 模拟生成餐厅信息
            List<String> restaurants = new ArrayList<>();
            restaurants.add("当地特色餐厅" + i);
            restaurants.add("人气餐厅" + i);
            route.setRestaurants(restaurants);
            
            // 模拟生成住宿信息
            List<String> accommodations = new ArrayList<>();
            if (preferences != null && preferences.contains("豪华")) {
                accommodations.add(destination + "五星级酒店" + i);
            } else if (preferences != null && preferences.contains("经济型")) {
                accommodations.add(destination + "经济型酒店" + i);
            } else {
                accommodations.add(destination + "商务酒店" + i);
            }
            route.setAccommodations(accommodations);
            
            // 模拟生成描述
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            route.setDescription("第" + i + "天行程安排（" + sdf.format(currentDate) + "）: 探索" + destination + "的主要景点和美食。");
            
            // 模拟生成预计费用
            route.setEstimatedCost(request.getBudgetAmount() != null ? request.getBudgetAmount() / days : 500.0);
            
            routes.add(route);
            
            // 增加一天
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            currentDate = calendar.getTime();
        }
        
        return routes;
    }
    
    // 模拟AI生成预算分配信息（实际项目中应替换为大模型API调用）
    private Map<String, Double> generateBudgetAllocations(Double totalBudget) {
        Map<String, Double> allocations = new HashMap<>();
        double total = totalBudget != null ? totalBudget : 5000.0;
        
        // 根据API文档要求的字段名生成分配信息，将Math.round结果转换为Double类型
        allocations.put("住宿", (double)Math.round(total * 0.3 * 100) / 100);
        allocations.put("餐饮", (double)Math.round(total * 0.2 * 100) / 100);
        allocations.put("交通", (double)Math.round(total * 0.15 * 100) / 100);
        allocations.put("门票", (double)Math.round(total * 0.2 * 100) / 100); // 使用API文档中的字段名
        allocations.put("购物", (double)Math.round(total * 0.1 * 100) / 100);
        allocations.put("其他", (double)Math.round(total * 0.05 * 100) / 100);
        
        return allocations;
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