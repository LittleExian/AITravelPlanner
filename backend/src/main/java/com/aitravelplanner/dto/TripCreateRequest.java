package com.aitravelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TripCreateRequest {
    @NotBlank(message = "用户ID不能为空")
    private String userId;
    
    @NotBlank(message = "行程标题不能为空")
    private String title;

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "开始日期不能为空")
    private Date startDate;

    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    private String description;
    private List<String> tags;
    private String coverImage;

    private Double budgetAmount; // 预算金额
    private Integer peopleCount; // 同行人数
    private List<String> travelPreferences; // 旅行偏好
    private String specialNeeds; // 特殊需求，用于AI生成行程
}