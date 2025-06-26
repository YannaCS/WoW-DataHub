package game.model;

import java.util.Objects;

public class Items {
    private int itemID;
    private String itemName;
    private int level;
    private int maxStackSize;
    private double price;
    
    public Items(int itemID, String itemName, int level, int maxStackSize, double price) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.level = level;
        this.maxStackSize = maxStackSize;
        this.price = price;
    }
    
    // Getters
    public int getItemID() {
        return itemID;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public int getLevel() {
        return level;
    }
    
    public int getMaxStackSize() {
        return maxStackSize;
    }
    
    public double getPrice() {
        return price;
    }
    
    // Setters
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public void setMaxStackSize(int maxStackSize) {
        this.maxStackSize = maxStackSize;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Items items = (Items) o;
        return itemID == items.itemID &&
               level == items.level &&
               maxStackSize == items.maxStackSize &&
               Double.compare(items.price, price) == 0 &&
               Objects.equals(itemName, items.itemName);
    }
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("hashing not supported");
    }
    
    @Override
    public String toString() {
        return String.format("Items(%s)", fieldsToString());
    }
    
    protected String fieldsToString() {
        return String.format(
            "%d, %s, %d, %d, %.2f",
            itemID,
            itemName,
            level,
            maxStackSize,
            price
        );
    }
}