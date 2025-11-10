package com.aitravelplanner.controller;

import com.aitravelplanner.dto.BudgetAllocationRequest;
import com.aitravelplanner.dto.ExpenseAddRequest;
import com.aitravelplanner.model.Budget;
import com.aitravelplanner.model.Expense;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.model.User;
import com.aitravelplanner.service.BudgetService;
import com.aitravelplanner.service.TripService;
import com.aitravelplanner.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private TripService tripService;

    @Autowired
    private UserService userService;

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<?> getTripBudget(@PathVariable String tripId) {
        try {
            Budget budget = budgetService.getTripBudget(tripId);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("获取预算失败: " + e.getMessage());
        }
    }

    @PostMapping("/trip/{tripId}/expenses")
    public ResponseEntity<?> addExpense(@PathVariable String tripId, @Valid @RequestBody ExpenseAddRequest request) {
        try {
            Expense expense = budgetService.addExpense(tripId, request);
            return ResponseEntity.ok(expense);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("添加费用失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/trip/{tripId}/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable String tripId, @PathVariable String expenseId) {
        try {
            budgetService.deleteExpense(tripId, expenseId);
            return ResponseEntity.ok("费用已删除");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除费用失败: " + e.getMessage());
        }
    }

    @PostMapping("/trip/{tripId}/allocations")
    public ResponseEntity<?> createBudgetAllocations(@PathVariable String tripId, @Valid @RequestBody BudgetAllocationRequest request) {
        try {
            Budget budget = budgetService.createBudgetAllocations(tripId, request);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("创建预算分配失败: " + e.getMessage());
        }
    }

    @PutMapping("/trip/{tripId}/allocations")
    public ResponseEntity<?> updateBudgetAllocations(@PathVariable String tripId, @RequestBody Map<String, Double> allocations) {
        try {
            Budget budget = budgetService.updateBudgetAllocations(tripId, allocations);
            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("更新预算分配失败: " + e.getMessage());
        }
    }

    // 辅助方法已移除，不再需要权限检查
}