package com.aitravelplanner.service.impl;

import com.aitravelplanner.dto.BudgetAllocationRequest;
import com.aitravelplanner.dto.ExpenseAddRequest;
import com.aitravelplanner.model.Budget;
import com.aitravelplanner.model.Expense;
import com.aitravelplanner.model.Trip;
import com.aitravelplanner.repository.TripRepository;
import com.aitravelplanner.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private TripRepository tripRepository;

    @Override
    public Budget getTripBudget(String tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        // 如果预算为null，初始化一个新的预算
        if (trip.getBudget() == null) {
            Budget budget = new Budget();
            budget.setTotalBudget(0.0);
            budget.setAllocations(Map.of());
            budget.setExpenses(new ArrayList<>());
            budget.setSpentAmount(0.0);
            budget.setRemainingAmount(0.0);
            trip.setBudget(budget);
            tripRepository.save(trip);
        }

        return trip.getBudget();
    }

    @Override
    public Expense addExpense(String tripId, ExpenseAddRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        // 初始化预算（如果需要）
        if (trip.getBudget() == null) {
            trip.setBudget(new Budget());
            trip.getBudget().setExpenses(new ArrayList<>());
        }

        // 创建新费用
        Expense expense = new Expense();
        expense.setId(UUID.randomUUID().toString());
        expense.setName(request.getName());
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDate(request.getDate());
        expense.setDescription(request.getDescription());
        expense.setReceiptImage(request.getReceiptImage());

        // 添加到费用列表
        List<Expense> expenses = trip.getBudget().getExpenses();
        if (expenses == null) {
            expenses = new ArrayList<>();
        }
        expenses.add(expense);
        trip.getBudget().setExpenses(expenses);

        // 更新预算总额
        updateBudgetTotals(tripId);

        tripRepository.save(trip);
        return expense;
    }

    @Override
    public void deleteExpense(String tripId, String expenseId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (trip.getBudget() != null && trip.getBudget().getExpenses() != null) {
            List<Expense> expenses = trip.getBudget().getExpenses();
            boolean removed = expenses.removeIf(expense -> expense.getId().equals(expenseId));
            
            if (removed) {
                updateBudgetTotals(tripId);
                tripRepository.save(trip);
            } else {
                throw new RuntimeException("费用不存在");
            }
        }
    }

    @Override
    public Budget createBudgetAllocations(String tripId, BudgetAllocationRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (trip.getBudget() == null) {
            trip.setBudget(new Budget());
        }

        // 转换AllocationItem列表为Map<String, Double>进行存储
        Map<String, Double> allocationMap = new HashMap<>();
        if (request.getAllocations() != null) {
            for (BudgetAllocationRequest.AllocationItem item : request.getAllocations()) {
                allocationMap.put(item.getName(), item.getAmount());
            }
        }
        
        // 设置预算分配
        trip.getBudget().setAllocations(allocationMap);
        
        // 设置总预算：通过allocation中的amount累加计算得出
        Double totalBudget = allocationMap.values().stream().mapToDouble(Double::doubleValue).sum();
        trip.getBudget().setTotalBudget(totalBudget);
        
        // 初始化其他预算信息
        if (trip.getBudget().getExpenses() == null) {
            trip.getBudget().setExpenses(new ArrayList<>());
        }
        
        // 更新已花费金额
        updateBudgetTotals(tripId);
        
        // 更新剩余金额
        updateRemainingAmount(trip);

        tripRepository.save(trip);
        return trip.getBudget();
    }

    @Override
    public Budget updateBudgetAllocations(String tripId, Map<String, Double> allocations) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (trip.getBudget() == null) {
            trip.setBudget(new Budget());
        }

        // 覆盖现有分配
        trip.getBudget().setAllocations(new HashMap<>(allocations));
        
        // 设置总预算：通过allocation中的amount累加计算得出
        Double totalBudget = allocations.values().stream().mapToDouble(Double::doubleValue).sum();
        trip.getBudget().setTotalBudget(totalBudget);
        
        // 更新已花费金额
        updateBudgetTotals(tripId);
        
        // 更新剩余金额
        updateRemainingAmount(trip);

        tripRepository.save(trip);
        return trip.getBudget();
    }

    @Override
    public void updateBudgetTotals(String tripId) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new RuntimeException("行程不存在"));

        if (trip.getBudget() != null && trip.getBudget().getExpenses() != null) {
            // 计算已花费金额
            Double spentAmount = trip.getBudget().getExpenses().stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();
            trip.getBudget().setSpentAmount(spentAmount);
            
            // 直接更新剩余金额（remainingAmount = totalBudget - spentAmount）
            Double totalBudget = trip.getBudget().getTotalBudget() != null ? trip.getBudget().getTotalBudget() : 0.0;
            trip.getBudget().setRemainingAmount(totalBudget - spentAmount);
            
            // 保存更新后的trip对象到数据库
            tripRepository.save(trip);
        }
    }

    private void updateRemainingAmount(Trip trip) {
        Double totalBudget = trip.getBudget().getTotalBudget() != null ? trip.getBudget().getTotalBudget() : 0.0;
        Double spentAmount = trip.getBudget().getSpentAmount() != null ? trip.getBudget().getSpentAmount() : 0.0;
        trip.getBudget().setRemainingAmount(totalBudget - spentAmount);
    }
}