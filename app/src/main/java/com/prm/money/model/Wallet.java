package com.prm.money.model;

public class Wallet {
    private int id;
    private String name;
    private double balance;
    private String description;
    private Integer categoryId; // FK -> WalletCategory (nullable)
    private String icon;
    private boolean isDelete;

    public Wallet() {
        // Constructor rỗng
    }

    public Wallet(int id, String name, double balance, String description, Integer categoryId, String icon, boolean isDelete) {
        this.id = id;
        this.name = name;
        this.balance = balance;
        this.description = description;
        this.categoryId = categoryId;
        this.icon = icon;
        this.isDelete = isDelete;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public boolean isDelete() { return isDelete; }
    public void setDelete(boolean delete) { isDelete = delete; }

}