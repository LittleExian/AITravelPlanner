package com.aitravelplanner.model;

import lombok.Data;

import java.util.Date;

@Data
public class Expense {
    private String id;
    private String name;
    private Double amount;
    private String category;
    private Date date;
    private String description;
    private String receiptImage;
}