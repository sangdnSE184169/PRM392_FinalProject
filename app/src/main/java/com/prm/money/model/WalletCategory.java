package com.prm.money.model;

public class WalletCategory {
    private int id;
    private String name;
    private String description;
    private String color;
    private String icon;
    private boolean isDelete;

    public WalletCategory(int id, String name, String description, String color, String icon, boolean isDelete) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.isDelete = isDelete;
    }

    // Getter và Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public boolean isDelete() { return isDelete; }
    public void setDelete(boolean delete) { isDelete = delete; }
}