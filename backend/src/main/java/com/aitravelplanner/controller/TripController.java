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
import com.aitravelplanner.service.AliBaiLianService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
    
    @Autowired
    private AliBaiLianService aliBaiLianService;
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
    public ResponseEntity<String> aiGenerateTrip(@Valid @RequestBody TripCreateRequest request) {
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
            String tripId = createdTrip.getId(); // ✅ 一定要从数据库返回对象中取ID
            logger.info("基本行程创建成功，行程ID: {}", tripId);
            
            // 2. 调用大模型API生成行程详细信息（这里使用模拟数据，实际项目中需要集成真实的大模型API）
            List<TravelRoute> aiRoutes = generateRoutesWithAI(tripId, request);
    
            ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

            // 打印原始结果
            logger.info("=== AI生成行程路线 ===\n{}", mapper.writeValueAsString(aiRoutes));

            // 3. 保存生成的路线信息
            logger.info("使用行程ID: {} 保存路线信息", tripId);
            for (TravelRoute route : aiRoutes) {
                route.setTripId(tripId);
                travelRouteService.createRoute(route);
            }
            logger.info("成功保存 {} 条旅行路线", aiRoutes.size());
            
            // 4. 使用预算分配API接口更新预算分配
            Map<String, Double> allocations = generateBudgetAllocations(request.getBudgetAmount());
            
            // 调用预算分配API，使用相同的tripId变量
            budgetService.updateBudgetAllocations(tripId, allocations);
            logger.info("使用行程ID: {} 成功更新行程预算分配信息", tripId);
            
            logger.info("使用行程ID: {} 完成AI行程生成，返回行程ID", tripId);
            return ResponseEntity.ok(tripId);
        } catch (Exception e) {
            logger.error("AI生成行程失败: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    // 使用阿里百炼大模型生成旅行路线
    private List<TravelRoute> generateRoutesWithAI(String tripId, TripCreateRequest request) {
        List<TravelRoute> routes = new ArrayList<>();
        
        try {
            // 计算行程天数
            long diffInMillies = request.getEndDate().getTime() - request.getStartDate().getTime();
            int days = (int) (diffInMillies / (1000 * 60 * 60 * 24)) + 1;
            
            // 构建提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("请为我规划一段在")
                  .append(request.getDestination())
                  .append("的")
                  .append(days)
                  .append("天旅行行程。");
            
            if (request.getTravelPreferences() != null && !request.getTravelPreferences().isEmpty()) {
                prompt.append(" 旅行偏好包括: ");
                for (int i = 0; i < request.getTravelPreferences().size(); i++) {
                    prompt.append(request.getTravelPreferences().get(i));
                    if (i < request.getTravelPreferences().size() - 1) {
                        prompt.append("、");
                    }
                }
            }
            
            if (request.getSpecialNeeds() != null && !request.getSpecialNeeds().isEmpty()) {
                prompt.append(" 特殊需求: ").append(request.getSpecialNeeds());
            }
            
            prompt.append("\n\n请按以下JSON格式返回行程规划，确保返回内容是有效的JSON格式:")
                  .append("\n{\n  \"routes\": [\n    {\n      \"day\": 1,\n      \"description\": \"第1天行程描述\",\n      \"transportation\": \"交通方式\",\n      \"attractions\": [\"景点1\", \"景点2\"],\n      \"attractionDetails\": [\n        {\"name\": \"景点1\", \"latitude\": 30.123456, \"longitude\": 120.123456, \"address\": \"地址1\"},\n        {\"name\": \"景点2\", \"latitude\": 30.234567, \"longitude\": 120.234567, \"address\": \"地址2\"}\n      ],\n      \"restaurants\": [\"餐厅1\", \"餐厅2\"],\n      \"accommodations\": [\"酒店1\"],\n      \"estimatedCost\": 500\n    }\n  ]\n}")
                  .append("\n\n请确保返回的是纯JSON格式，不要包含其他文字说明。");
            
            // 调用阿里百炼API
            String generatedContent = aliBaiLianService.generateContent(prompt.toString());
            logger.info("阿里百炼API返回内容: {}", generatedContent);
            
            // 解析JSON响应
            JSONObject jsonResponse = JSON.parseObject(generatedContent);
            List<JSONObject> aiRoutes = jsonResponse.getJSONArray("routes").toJavaList(JSONObject.class);
            
            Date currentDate = new Date(request.getStartDate().getTime());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            
            // 转换为TravelRoute对象
            for (JSONObject aiRoute : aiRoutes) {
                TravelRoute route = new TravelRoute();
                route.setTripId(tripId);
                route.setDayNumber(aiRoute.getInteger("day"));
                route.setCreatedAt(new Date());
                route.setUpdatedAt(new Date());
                
                // 设置基本信息
                route.setDescription(aiRoute.getString("description"));
                route.setTransportation(aiRoute.getString("transportation"));
                
                // 设置景点信息
                List<String> attractions = aiRoute.getJSONArray("attractions").toJavaList(String.class);
                route.setAttractions(attractions);
                
                // 设置景点详情
                List<Map<String, Object>> attractionDes = new ArrayList<>();
                if (aiRoute.containsKey("attractionDetails")) {
                    List<JSONObject> details = aiRoute.getJSONArray("attractionDetails").toJavaList(JSONObject.class);
                    for (JSONObject detail : details) {
                        Map<String, Object> des = new HashMap<>();
                        des.put("name", detail.getString("name"));
                        des.put("latitude", detail.getDouble("latitude"));
                        des.put("longitude", detail.getDouble("longitude"));
                        des.put("address", detail.getString("address"));
                        attractionDes.add(des);
                    }
                } else {
                    // 如果没有经纬度信息，生成模拟数据
                    for (int j = 0; j < attractions.size(); j++) {
                        Map<String, Object> des = new HashMap<>();
                        des.put("name", attractions.get(j));
                        // 生成随机经纬度（作为备选）
                        double baseLat = 30.0 + (Math.random() - 0.5) * 2;
                        double baseLng = 120.0 + (Math.random() - 0.5) * 2;
                        des.put("latitude", Math.round(baseLat * 1000000) / 1000000.0);
                        des.put("longitude", Math.round(baseLng * 1000000) / 1000000.0);
                        des.put("address", request.getDestination() + "市某区某路" + j + "号");
                        attractionDes.add(des);
                    }
                }
                route.setAttractionDes(attractionDes);
                
                // 设置餐厅信息
                List<String> restaurants = aiRoute.getJSONArray("restaurants").toJavaList(String.class);
                route.setRestaurants(restaurants);
                
                // 设置住宿信息
                List<String> accommodations = aiRoute.getJSONArray("accommodations").toJavaList(String.class);
                route.setAccommodations(accommodations);
                
                // 设置预计费用
                route.setEstimatedCost(aiRoute.getDouble("estimatedCost"));
                
                routes.add(route);
                
                // 增加一天
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                currentDate = calendar.getTime();
            }
            
        } catch (Exception e) {
            logger.error("调用阿里百炼API生成路线失败: {}", e.getMessage());
            // 出错时回退到模拟数据生成
            logger.info("回退到模拟数据生成路线");
            routes = generateMockRoutes(tripId, request);
        }
        
        return routes;
    }
    
    // 备用的模拟数据生成方法
    private List<TravelRoute> generateMockRoutes(String tripId, TripCreateRequest request) {
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
                double baseLat = 30.0 + (Math.random() - 0.5) * 2;
                double baseLng = 120.0 + (Math.random() - 0.5) * 2;
                des.put("latitude", Math.round(baseLat * 1000000) / 1000000.0);
                des.put("longitude", Math.round(baseLng * 1000000) / 1000000.0);
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
    
    // 使用阿里百炼大模型生成预算分配信息
    private Map<String, Double> generateBudgetAllocations(Double totalBudget) {
        try {
            double total = totalBudget != null ? totalBudget : 5000.0;
            
            // 构建提示词
            StringBuilder prompt = new StringBuilder();
            prompt.append("请为一场总预算为")
                  .append(total)
                  .append("元的旅行生成预算分配方案。")
                  .append("\n\n请按以下JSON格式返回预算分配，确保返回内容是有效的JSON格式:")
                  .append("\n{")
                  .append("\n  \"住宿\": 1500,")
                  .append("\n  \"餐饮\": 1000,")
                  .append("\n  \"交通\": 750,")
                  .append("\n  \"门票\": 1000,")
                  .append("\n  \"购物\": 500,")
                  .append("\n  \"其他\": 250")
                  .append("\n}")
                  .append("\n\n请确保所有项的总和接近总预算，且返回的是纯JSON格式，不要包含其他文字说明。");
            
            // 调用阿里百炼API
            String generatedContent = aliBaiLianService.generateContent(prompt.toString());
            logger.info("阿里百炼API预算分配返回内容: {}", generatedContent);
            
            // 解析JSON响应
            JSONObject jsonResponse = JSON.parseObject(generatedContent);
            Map<String, Double> allocations = new HashMap<>();
            
            // 提取预算分配信息
            allocations.put("住宿", jsonResponse.getDoubleValue("住宿"));
            allocations.put("餐饮", jsonResponse.getDoubleValue("餐饮"));
            allocations.put("交通", jsonResponse.getDoubleValue("交通"));
            allocations.put("门票", jsonResponse.getDoubleValue("门票"));
            allocations.put("购物", jsonResponse.getDoubleValue("购物"));
            allocations.put("其他", jsonResponse.getDoubleValue("其他"));
            
            return allocations;
        } catch (Exception e) {
            logger.error("调用阿里百炼API生成预算分配失败: {}", e.getMessage());
            // 出错时回退到模拟数据生成
            logger.info("回退到模拟预算分配");
            return generateMockBudgetAllocations(totalBudget);
        }
    }
    
    // 备用的模拟预算分配方法
    private Map<String, Double> generateMockBudgetAllocations(Double totalBudget) {
        Map<String, Double> allocations = new HashMap<>();
        double total = totalBudget != null ? totalBudget : 5000.0;
        
        // 根据API文档要求的字段名生成分配信息，将Math.round结果转换为Double类型
        allocations.put("住宿", (double)Math.round(total * 0.3 * 100) / 100);
        allocations.put("餐饮", (double)Math.round(total * 0.2 * 100) / 100);
        allocations.put("交通", (double)Math.round(total * 0.15 * 100) / 100);
        allocations.put("门票", (double)Math.round(total * 0.2 * 100) / 100);
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