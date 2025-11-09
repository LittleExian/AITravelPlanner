package com.aitravelplanner.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class TripCreateRequest {

    @NotBlank(message = "行程名称不能为空")
    private String name;

    @NotBlank(message = "目的地不能为空")
    private String destination;

    @NotNull(message = "开始日期不能为空")
    private Date startDate;

    @NotNull(message = "结束日期不能为空")
    private Date endDate;

    @NotNull(message = "预算不能为空")
    private Double budget;

    @NotNull(message = "人数不能为空")
    private Integer peopleCount;

    private List<String> preferences;
    private String description;
}