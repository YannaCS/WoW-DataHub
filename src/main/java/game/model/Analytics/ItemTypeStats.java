package game.model.Analytics;

public class ItemTypeStats {
    private String itemType;
    private int count;
    
    public ItemTypeStats(String itemType, int count) {
        this.itemType = itemType;
        this.count = count;
    }
    
    public String getItemType() { return itemType; }
    public int getCount() { return count; }
}