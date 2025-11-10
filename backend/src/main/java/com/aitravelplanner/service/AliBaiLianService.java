package com.aitravelplanner.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AliBaiLianService {
    private static final Logger logger = LoggerFactory.getLogger(AliBaiLianService.class);
    
    // 阿里百炼API地址
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    
    // API Key (实际应用中应从配置文件或环境变量读取)
    private final String apiKey = "sk-d0d96d7a0bfa496291897fa48bb5807d";
    
    private final RestTemplate restTemplate;
    
    public AliBaiLianService() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * 调用阿里百炼大模型生成内容
     * @param prompt 提示词
     * @return 生成的内容
     */
    public String generateContent(String prompt) {
        try {
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen-plus"); // 使用通义千问模型
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("max_tokens", 2048);
            parameters.put("temperature", 0.7);
            parameters.put("top_p", 0.9);
            
            requestBody.put("parameters", parameters);
            
            Map<String, String> input = new HashMap<>();
            input.put("prompt", prompt);
            requestBody.put("input", input);
            
            // 发送请求
            HttpEntity<String> requestEntity = new HttpEntity<>(
                JSON.toJSONString(requestBody), 
                headers
            );
            
            logger.info("发送请求到阿里百炼API，提示词长度: {}", prompt.length());
            
            ResponseEntity<String> response = restTemplate.exchange(
                API_URL, 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );
            
            // 解析响应
            JSONObject responseJson = JSON.parseObject(response.getBody());
            if (responseJson.containsKey("output") && responseJson.getJSONObject("output").containsKey("text")) {
                String generatedText = responseJson.getJSONObject("output").getString("text");
                logger.info("成功获取阿里百炼API响应，内容长度: {}", generatedText.length());
                return generatedText;
            } else {
                logger.error("阿里百炼API返回格式异常: {}", response.getBody());
                throw new RuntimeException("AI生成失败: 返回格式异常");
            }
        } catch (Exception e) {
            logger.error("调用阿里百炼API失败: {}", e.getMessage());
            throw new RuntimeException("AI生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成旅行路线提示词
     */
    public String generateRoutePrompt(String destination, int days, List<String> preferences, String specialNeeds) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请为我规划一段在")
              .append(destination)
              .append("的")
              .append(days)
              .append("天旅行行程。");
        
        if (preferences != null && !preferences.isEmpty()) {
            prompt.append(" 旅行偏好包括: ");
            for (int i = 0; i < preferences.size(); i++) {
                prompt.append(preferences.get(i));
                if (i < preferences.size() - 1) {
                    prompt.append("、");
                }
            }
        }
        
        if (specialNeeds != null && !specialNeeds.isEmpty()) {
            prompt.append(" 特殊需求: ").append(specialNeeds);
        }
        
        prompt.append("\n\n请按以下JSON格式返回行程规划，确保返回内容是有效的JSON格式:");
        prompt.append("\n{\n  \"routes\": [\n    {\n      \"day\": 1,\n      \"description\": \"第1天行程描述\",\n      \"transportation\": \"交通方式\",\n      \"attractions\": [\"景点1\", \"景点2\"],\n      \"attractionDetails\": [\n        {\"name\": \"景点1\", \"latitude\": 30.123456, \"longitude\": 120.123456, \"address\": \"地址1\"},\n        {\"name\": \"景点2\", \"latitude\": 30.234567, \"longitude\": 120.234567, \"address\": \"地址2\"}\n      ],\n      \"restaurants\": [\"餐厅1\", \"餐厅2\"],\n      \"accommodations\": [\"酒店1\"],\n      \"estimatedCost\": 500\n    }\n  ]\n}");
        
        return prompt.toString();
    }
}