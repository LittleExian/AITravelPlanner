package com.aitravelplanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @ManyToOne
    @JoinColumn(name = "trip_day_id", nullable = false)
    private TripDay tripDay;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    private String time; // 使用简单字符串时间表示，与TripDay需求匹配
    private Trip.ActivityType type; // 使用Trip中的枚举类型
    private String address; // 地址信息
    private String ticket;
    private String budget;
    
    // 保留原有字段以兼容其他功能
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String location;
    private double latitude;
    private double longitude;
    private String category; // 'ATTRACTION', 'FOOD', 'TRANSPORT', 'ACCOMMODATION', 'OTHER'
    private String notes;
    private double estimatedCost;

    // 由于使用了lombok的@Data注解，以下getter和setter可能冗余，但保留以确保兼容性
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}