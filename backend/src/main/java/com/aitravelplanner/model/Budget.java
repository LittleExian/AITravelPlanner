package com.aitravelplanner.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Budget {
    private Double totalBudget;
    private Map<String, Double> allocations;  // 预算分配，如{"住宿": 1000, "餐饮": 500}
    private List<Expense> expenses;
    private Double spentAmount;
    private Double remainingAmount;
}