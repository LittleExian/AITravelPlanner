package com.aitravelplanner.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class BudgetAllocationRequest {
    @NotNull(message = "分配列表不能为空")
    @Valid
    private List<AllocationItem> allocations;

    public List<AllocationItem> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<AllocationItem> allocations) {
        this.allocations = allocations;
    }

    public static class AllocationItem {
        @NotNull(message = "分配名称不能为空")
        private String name;
        
        @NotNull(message = "分配金额不能为空")
        private Double amount;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}