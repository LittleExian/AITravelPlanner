package com.aitravelplanner.controller;

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
        // 检查访问权限
        if (!hasTripAccess(tripId)) {
            return ResponseEntity.status(403).body("无权限访问此行程的预算");
        }

        Budget budget = budgetService.getTripBudget(tripId);
        return ResponseEntity.ok(budget);
    }

    @PostMapping("/trip/{tripId}/expenses")
    public ResponseEntity<?> addExpense(@PathVariable String tripId, @Valid @RequestBody ExpenseAddRequest request) {
        // 检查访问权限
        if (!hasTripAccess(tripId)) {
            return ResponseEntity.status(403).body("无权限为此行程添加费用");
        }

        Expense expense = budgetService.addExpense(tripId, request);
        return ResponseEntity.ok(expense);
    }

    @DeleteMapping("/trip/{tripId}/expenses/{expenseId}")
    public ResponseEntity<?> deleteExpense(@PathVariable String tripId, @PathVariable String expenseId) {
        // 检查访问权限
        if (!hasTripAccess(tripId)) {
            return ResponseEntity.status(403).body("无权限删除此行程的费用");
        }

        budgetService.deleteExpense(tripId, expenseId);
        return ResponseEntity.ok("费用已删除");
    }

    @PutMapping("/trip/{tripId}/allocations")
    public ResponseEntity<?> updateBudgetAllocations(@PathVariable String tripId, @RequestBody Map<String, Double> allocations) {
        // 检查访问权限
        if (!hasTripAccess(tripId)) {
            return ResponseEntity.status(403).body("无权限更新此行程的预算分配");
        }

        Budget budget = budgetService.updateBudgetAllocations(tripId, allocations);
        return ResponseEntity.ok(budget);
    }

    // 辅助方法：检查用户是否有权限访问行程
    private boolean hasTripAccess(String tripId) {
        User currentUser = userService.getCurrentUser();
        Trip trip = tripService.getTripById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));
        
        // 行程公开或属于当前用户
        return trip.isPublic() || trip.getUserId().equals(currentUser.getId());
    }
}