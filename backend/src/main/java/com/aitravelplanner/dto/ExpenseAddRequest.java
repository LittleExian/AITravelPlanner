package com.aitravelplanner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
public class ExpenseAddRequest {
    @NotBlank(message = "费用名称不能为空")
    private String name;

    @NotNull(message = "费用金额不能为空")
    @Positive(message = "费用金额必须大于0")
    private Double amount;

    @NotBlank(message = "费用类别不能为空")
    private String category;

    @NotNull(message = "费用日期不能为空")
    private Date date;

    private String description;
    private String receiptImage;
}