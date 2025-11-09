package com.aitravelplanner.service;

import com.aitravelplanner.model.Budget;
import java.util.List;

public interface BudgetService {

    Budget getBudgetByTravelPlanId(String travelPlanId);
    Budget getBudgetByTripId(String tripId);
    Budget createBudget(Budget budget);
    Budget updateBudget(String budgetId, Budget budget);
    Budget addExpense(String tripId, Budget.Expense expense);
    Budget removeExpense(String budgetId, String expenseId);
    Budget deleteExpense(String tripId, String expenseId);
    Budget updateBudgetAllocations(String tripId, List<Budget.BudgetAllocation> allocations);
    List<Budget> getBudgetsByUserId(String userId);
    void deleteBudget(String budgetId);
    double calculateTotalExpenses(String budgetId);
    double calculateRemainingBudget(String budgetId);
}