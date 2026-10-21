package com.example.smartpantrymanager;

/**
 * Model class representing a single pantry item.
 * One Ingredient object = one row in the pantry_item table.
 */
public class Ingredient {
    private int id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // stored as text, e.g. "2026-11-30"; may be empty

    public Ingredient(int id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    // Constructor for new items that don't have a database id yet
    public Ingredient(String name, double quantity, String unit, String expiryDate) {
        this(0, name, quantity, unit, expiryDate);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public String getExpiryDate() { return expiryDate; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setQuantity(double quantity) { this.quantity = quantity; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
}