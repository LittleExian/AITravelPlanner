package com.aitravelplanner.service.impl;

import com.aitravelplanner.exception.ResourceNotFoundException;
import com.aitravelplanner.model.Budget;
import com.aitravelplanner.repository.BudgetRepository;
import com.aitravelplanner.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    @Override
    public Budget getBudgetByTravelPlanId(String travelPlanId) {
        return budgetRepository.findByTripId(travelPlanId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found for travel plan: " + travelPlanId));
    }

    @Override
    public Budget createBudget(Budget budget) {
        budget.setId(UUID.randomUUID().toString());
        return budgetRepository.save(budget);
    }

    @Override
    public Budget updateBudget(String budgetId, Budget budget) {
        Budget existingBudget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        
        existingBudget.setAllocations(budget.getAllocations());
        
        return budgetRepository.save(existingBudget);
    }

    @Override
    public Budget getBudgetByTripId(String tripId) {
        return budgetRepository.findByTripId(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found for trip: " + tripId));
    }
    
    @Override
    public Budget addExpense(String tripId, Budget.Expense expense) {
        Budget budget = getBudgetByTripId(tripId);
        
        expense.setId(UUID.randomUUID().toString());
        expense.setDate(LocalDateTime.now());
        budget.getExpenses().add(expense);
        
        return budgetRepository.save(budget);
    }
    
    @Override
    public Budget deleteExpense(String tripId, String expenseId) {
        Budget budget = getBudgetByTripId(tripId);
        
        boolean removed = budget.getExpenses().removeIf(expense -> expense.getId().equals(expenseId));
        if (!removed) {
            throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
        }
        
        return budgetRepository.save(budget);
    }
    
    @Override
    public Budget updateBudgetAllocations(String tripId, List<Budget.BudgetAllocation> allocations) {
        Budget budget = getBudgetByTripId(tripId);
        
        budget.getAllocations().clear();
        for (Budget.BudgetAllocation allocation : allocations) {
            allocation.setId(UUID.randomUUID().toString());
            budget.getAllocations().add(allocation);
        }
        
        return budgetRepository.save(budget);
    }

    @Override
    public Budget removeExpense(String budgetId, String expenseId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        
        boolean removed = budget.getExpenses().removeIf(expense -> expense.getId().equals(expenseId));
        if (!removed) {
            throw new ResourceNotFoundException("Expense not found with id: " + expenseId);
        }
        
        return budgetRepository.save(budget);
    }

    @Override
    public List<Budget> getBudgetsByUserId(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    @Override
    public void deleteBudget(String budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        
        budgetRepository.delete(budget);
    }

    @Override
    public double calculateTotalExpenses(String budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        
        return budget.getExpenses().stream()
                .mapToDouble(Budget.Expense::getAmount)
                .sum();
    }

    @Override
    public double calculateRemainingBudget(String budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found with id: " + budgetId));
        
        double totalAllocation = budget.getAllocations().stream()
                .mapToDouble(Budget.BudgetAllocation::getAmount)
                .sum();
        
        double totalExpenses = calculateTotalExpenses(budgetId);
        
        return totalAllocation - totalExpenses;
    }
}