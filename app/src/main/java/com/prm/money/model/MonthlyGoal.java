package com.prm.money.model;

public class MonthlyGoal {
    private int id;
    private int month;
    private int year;
    private double goalAmount;
    private int categoryId;
    private Integer walletId; // FK -> Wallet (nullable)
    private String note;
    private String createdAt;
    private String updatedAt;
    private boolean isDelete;

    public MonthlyGoal(int id, int month, int year, double goalAmount, int categoryId, Integer walletId, String note, String createdAt, String updatedAt, boolean isDelete) {
        this.id = id;
        this.month = month;
        this.year = year;
        this.goalAmount = goalAmount;
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.note = note;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDelete = isDelete;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getGoalAmount() { return goalAmount; }
    public void setGoalAmount(double goalAmount) { this.goalAmount = goalAmount; }
    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public Integer getWalletId() { return walletId; }
    public void setWalletId(Integer walletId) { this.walletId = walletId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    public boolean isDelete() { return isDelete; }
    public void setDelete(boolean delete) { isDelete = delete; }
}
