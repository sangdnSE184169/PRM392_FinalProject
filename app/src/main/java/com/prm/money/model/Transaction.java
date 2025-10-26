package com.prm.money.model;

public class Transaction {
    private int id;
    private double amount;
    private String date;
    private String note;
    private int walletId;
    private int categoryId;
    private String type; // "income" hoặc "expense"
    private String activity; // Ví dụ: "Ăn uống", "Đi lại", ...
    private Integer monthlyGoalId; // FK -> MonthlyGoal (nullable)
    private String createdAt;
    private String updatedAt;
    private boolean isDelete;

    public Transaction(int id, double amount, String date, String note, int walletId, int categoryId, String type, String activity, Integer monthlyGoalId, String createdAt, String updatedAt, boolean isDelete) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.walletId = walletId;
        this.categoryId = categoryId;
        this.type = type;
        this.activity = activity;
        this.monthlyGoalId = monthlyGoalId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDelete = isDelete;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
    public Integer getMonthlyGoalId() { return monthlyGoalId; }
    public void setMonthlyGoalId(Integer monthlyGoalId) { this.monthlyGoalId = monthlyGoalId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public boolean isDelete() { return isDelete; }
    public void setDelete(boolean delete) { isDelete = delete; }
}
