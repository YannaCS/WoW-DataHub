package game.etl.dto;

import java.util.Map;

public class WoWItemData {
    private int itemId;
    private String name;
    private int itemLevel;
    private String itemClass;
    private String itemSubClass;
    private int requiredLevel;
    private Map<String, Integer> stats;
    private double estimatedValue;
    
    // Constructors
    public WoWItemData() {}
    
    public WoWItemData(int itemId, String name, int itemLevel, String itemClass) {
        this.itemId = itemId;
        this.name = name;
        this.itemLevel = itemLevel;
        this.itemClass = itemClass;
    }
    
    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getItemLevel() { return itemLevel; }
    public void setItemLevel(int itemLevel) { this.itemLevel = itemLevel; }
    
    public String getItemClass() { return itemClass; }
    public void setItemClass(String itemClass) { this.itemClass = itemClass; }
    
    public String getItemSubClass() { return itemSubClass; }
    public void setItemSubClass(String itemSubClass) { this.itemSubClass = itemSubClass; }
    
    public int getRequiredLevel() { return requiredLevel; }
    public void setRequiredLevel(int requiredLevel) { this.requiredLevel = requiredLevel; }
    
    public Map<String, Integer> getStats() { return stats; }
    public void setStats(Map<String, Integer> stats) { this.stats = stats; }
    
    public double getEstimatedValue() { return estimatedValue; }
    public void setEstimatedValue(double estimatedValue) { this.estimatedValue = estimatedValue; }
}