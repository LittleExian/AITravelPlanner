package com.aitravelplanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String tripId;
    
    @Column(nullable = false)
    private String userId;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetAllocation> allocations = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expense> expenses = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // JPA生命周期回调
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Entity
    @Table(name = "budget_allocations")
    public static class BudgetAllocation {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;
        
        @Enumerated(EnumType.STRING)
        private ExpenseCategory category;
        
        private Double amount;
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public ExpenseCategory getCategory() { return category; }
        public void setCategory(ExpenseCategory category) { this.category = category; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
    }

    @Entity
    @Table(name = "expenses")
    public static class Expense {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;
        
        private LocalDateTime date;
        
        @Enumerated(EnumType.STRING)
        private ExpenseCategory category;
        
        private Double amount;
        
        private String description;
        
        private LocalDateTime createdAt;
        
        // JPA生命周期回调
        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }
        
        // Getters and Setters
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public LocalDateTime getDate() { return date; }
        public void setDate(LocalDateTime date) { this.date = date; }
        public ExpenseCategory getCategory() { return category; }
        public void setCategory(ExpenseCategory category) { this.category = category; }
        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public enum ExpenseCategory {
        TRANSPORTATION, ACCOMMODATION, FOOD, ATTRACTION, SHOPPING, OTHER
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTripId() { return tripId; }
    public void setTripId(String tripId) { this.tripId = tripId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public List<BudgetAllocation> getAllocations() { return allocations; }
    public void setAllocations(List<BudgetAllocation> allocations) { this.allocations = allocations; }
    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}