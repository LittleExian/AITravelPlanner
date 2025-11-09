package com.aitravelplanner.model;

import lombok.Data;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "trips")
@EntityListeners(AuditingEntityListener.class)
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;
    private String name;
    private String destination;
    private Date startDate;
    private Date endDate;
    private Double budget;
    private Integer peopleCount;
    
    @ElementCollection
    @CollectionTable(name = "trip_preferences")
    private List<String> preferences = new ArrayList<>();
    
    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TripDay> days = new ArrayList<>();
    
    private String description;
    private TripStatus status = TripStatus.UPCOMING;
    @CreatedDate
    private Date createdAt;
    
    @LastModifiedDate
    private Date updatedAt;

    public enum TripStatus {
        UPCOMING, ONGOING, COMPLETED
    }

    public enum ActivityType {
        ATTRACTION, FOOD, ACCOMMODATION, TRANSPORTATION, SHOPPING
    }
}