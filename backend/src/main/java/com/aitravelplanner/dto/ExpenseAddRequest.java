package com.aitravelplanner.dto;

import com.aitravelplanner.model.Budget;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

@Data
public class ExpenseAddRequest {

    @NotNull(message = "日期不能为空")
    private Date date;

    @NotNull(message = "类别不能为空")
    private Budget.ExpenseCategory category;

    @NotNull(message = "金额不能为空")
    private Double amount;

    private String description;
}