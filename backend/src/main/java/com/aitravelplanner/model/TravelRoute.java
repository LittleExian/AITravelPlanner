package com.aitravelplanner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "travel_routes")
public class TravelRoute {
    @Id
    private String id;
    private String tripId; // 关联到Trip表的外键
    private Integer dayNumber; // 第几天
    private String transportation; // 交通信息
    private List<String> attractions; // 景点信息
    private List<Map<String, Object>> attractionDes; // 景点经纬度信息
    private List<String> restaurants; // 餐厅信息
    private List<String> accommodations; // 住宿信息
    private String description; // 简介
    private Double estimatedCost; // 预计经费
    private Date createdAt;
    private Date updatedAt;
}