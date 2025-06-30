package game.model;

public class InventoryItemDetail {
    private int slotID;
    private int itemID;
    private String itemName;
    private String itemType;
    private int quantity;
    private int level;
    
    public InventoryItemDetail(int slotID, int itemID, String itemName, String itemType, int quantity, int level) {
        this.slotID = slotID;
        this.itemID = itemID;
        this.itemName = itemName;
        this.itemType = itemType;
        this.quantity = quantity;
        this.level = level;
    }
    
    // Getters
    public int getSlotID() { return slotID; }
    public int getItemID() { return itemID; }
    public String getItemName() { return itemName; }
    public String getItemType() { return itemType; }
    public int getQuantity() { return quantity; }
    public int getLevel() { return level; }
    
    // Setters
    public void setSlotID(int slotID) { this.slotID = slotID; }
    public void setItemID(int itemID) { this.itemID = itemID; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLevel(int level) { this.level = level; }
}