package com.aitravelplanner.controller;

import com.aitravelplanner.dto.ExpenseAddRequest;
import com.aitravelplanner.model.Budget;
import com.aitravelplanner.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<Budget> getBudgetByTripId(@PathVariable String tripId) {
        Budget budget = budgetService.getBudgetByTripId(tripId);
        return ResponseEntity.ok(budget);
    }

    @PostMapping("/trip/{tripId}/expenses")
    public ResponseEntity<Budget> addExpense(@PathVariable String tripId, @RequestBody Budget.Expense request) {
        Budget updatedBudget = budgetService.addExpense(tripId, request);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/trip/{tripId}/expenses/{expenseId}")
    public ResponseEntity<Budget> deleteExpense(@PathVariable String tripId, @PathVariable String expenseId) {
        Budget updatedBudget = budgetService.deleteExpense(tripId, expenseId);
        return ResponseEntity.ok(updatedBudget);
    }

    @PutMapping("/trip/{tripId}/allocations")
    public ResponseEntity<Budget> updateBudgetAllocations(@PathVariable String tripId, @RequestBody List<Budget.BudgetAllocation> allocations) {
        Budget updatedBudget = budgetService.updateBudgetAllocations(tripId, allocations);
        return ResponseEntity.ok(updatedBudget);
    }
}