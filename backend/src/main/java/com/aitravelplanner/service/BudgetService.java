package com.aitravelplanner.service;

import com.aitravelplanner.dto.ExpenseAddRequest;
import com.aitravelplanner.model.Budget;
import com.aitravelplanner.model.Expense;

import java.util.Map;

public interface BudgetService {
    Budget getTripBudget(String tripId);
    Expense addExpense(String tripId, ExpenseAddRequest request);
    void deleteExpense(String tripId, String expenseId);
    Budget updateBudgetAllocations(String tripId, Map<String, Double> allocations);
    void updateBudgetTotals(String tripId);
}