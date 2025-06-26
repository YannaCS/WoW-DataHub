package game.etl.dto;

import java.util.List;

public class WoWEquipmentData {
    private WoWItemData mainHand;
    private WoWItemData offHand;
    private WoWItemData head;
    private WoWItemData chest;
    private WoWItemData legs;
    private List<WoWItemData> allItems;
    
    // Constructors
    public WoWEquipmentData() {}
    
    // Getters and Setters
    public WoWItemData getMainHand() { return mainHand; }
    public void setMainHand(WoWItemData mainHand) { this.mainHand = mainHand; }
    
    public WoWItemData getOffHand() { return offHand; }
    public void setOffHand(WoWItemData offHand) { this.offHand = offHand; }
    
    public WoWItemData getHead() { return head; }
    public void setHead(WoWItemData head) { this.head = head; }
    
    public WoWItemData getChest() { return chest; }
    public void setChest(WoWItemData chest) { this.chest = chest; }
    
    public WoWItemData getLegs() { return legs; }
    public void setLegs(WoWItemData legs) { this.legs = legs; }
    
    public List<WoWItemData> getAllItems() { return allItems; }
    public void setAllItems(List<WoWItemData> allItems) { this.allItems = allItems; }
}