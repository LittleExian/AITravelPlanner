package com.aitravelplanner.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "trips")
public class Trip {
    @Id
    private String id;
    private String userId;
    private String title;
    private String destination;
    private Date startDate;
    private Date endDate;
    private String description;
    private List<String> tags;
    private String coverImage;
    private Date createdAt;
    private Date updatedAt;

    private Budget budget;
    private Double budgetAmount; // 预算金额
    private Integer peopleCount; // 同行人数
    private List<String> travelPreferences; // 旅行偏好
}