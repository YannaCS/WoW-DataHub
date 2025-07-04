package game.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import game.model.*;


public class InventoryDao {
	
	private InventoryDao() { }

	public static Inventory create( Connection cxn,
	           Characters character,
	           int slotID,
	           Items items,
	           int quantity
	         ) throws SQLException{
		
        String insertInventorySQL = """
                INSERT INTO Inventory (charID, slotID, instance, quantity) 
                VALUES (?, ?, ?, ?);
            """;

            try (PreparedStatement ps = cxn.prepareStatement(insertInventorySQL)) {
                ps.setInt(1, character.getCharID()); 
                ps.setInt(2, slotID);
                ps.setInt(3, items.getItemID());
                ps.setInt(4, quantity);
                ps.executeUpdate();
            }

            return new Inventory(character.getCharID(), slotID, items.getItemID(), quantity);
	}
	
	public static Inventory getInventoryByCharactersAndSlot( Connection cxn,
			Characters character,
	        int slotID
	         ) throws SQLException{
		
     String query = """
             SELECT charID, slotID, instance, quantity
             FROM Inventory
             WHERE charID = ? AND slotID = ?;
         """;

	    try (PreparedStatement selectStmt = cxn.prepareStatement(query)) {
	    	
	      selectStmt.setInt(1, character.getCharID()); 
	      selectStmt.setInt(2, slotID);  

	      try (ResultSet results = selectStmt.executeQuery()) {
	        if (results.next()) {
	          return new Inventory(
	            results.getInt("charID"),
	            results.getInt("slotID"),
	            results.getInt("instance"),
	            results.getInt("quantity")

	          );
	        } else {
	          return null;
	        }
	      }
	    }
	}
	
	public static Inventory getInventoryByCharactersAndInstance( Connection cxn,
			Characters character,
	        int instance
	         ) throws SQLException{
		
     String query = """
             SELECT charID, slotID, instance, quantity
             FROM Inventory
             WHERE charID = ? AND instance = ?;
         """;

	    try (PreparedStatement selectStmt = cxn.prepareStatement(query)) {
	    	
	      selectStmt.setInt(1, character.getCharID()); 
	      selectStmt.setInt(2, instance);  

	      try (ResultSet results = selectStmt.executeQuery()) {
	        if (results.next()) {
	          return new Inventory(
	            results.getInt("charID"),
	            results.getInt("slotID"),
	            results.getInt("instance"),
	            results.getInt("quantity")

	          );
	        } else {
	          return null;
	        }
	      }
	    }
	}
	
	
	public static List<Inventory> getInventoryOnlyByCharacters( Connection cxn,
			Characters character
	         ) throws SQLException{
		
	     String query = """
	             SELECT charID, slotID, instance, quantity
	             FROM Inventory
	             WHERE charID = ?;
	         """;

	        List<Inventory> inventorys = new ArrayList<>();

	        try (PreparedStatement ps = cxn.prepareStatement(query)) {
	            ps.setInt(1, character.getCharID()); 

	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                	Inventory inventory = new Inventory(
	                        rs.getInt("charID"),
	                        rs.getInt("slotID"),
	                        rs.getInt("instance"),
	                        rs.getInt("quantity")
	                    );
	                	inventorys.add(inventory);
	                }
	    	        return inventorys; // 如果没有找到数据，返回空列表（而不是 null）
	            }
	        }
	    }
	
	public static Inventory updateInventoryQuantity(Connection cxn, Inventory inventory, int newQuantity) throws SQLException {

		String updateInventorySQL = """
				    UPDATE Inventory
				    SET quantity = ?
				    WHERE charID = ? AND slotID = ?;
				""";

		try (PreparedStatement ps = cxn.prepareStatement(updateInventorySQL)) {
			ps.setInt(1, newQuantity);
			ps.setInt(2, inventory.getCharID());
			ps.setInt(3, inventory.getSlotID());
		    ps.executeUpdate();
		    inventory.setQuantity(newQuantity);
		    
		    return inventory;
		}
	}
	
	
	public static void delete(Connection cxn, Inventory inventory) throws SQLException{
	    String deletePerson = "DELETE FROM Inventory WHERE  charID = ? AND slotID = ?;";

	    try (PreparedStatement deleteStmt = cxn.prepareStatement(deletePerson)) {
	      deleteStmt.setInt(1, inventory.getCharID());
	      deleteStmt.setInt(2, inventory.getSlotID());
	      deleteStmt.executeUpdate();
	    }
		
	}
	
	public static List<InventoryItemDetail> getInventoryWithItemDetails(Connection cxn, Characters character) throws SQLException {
	    String query = """
	            SELECT inv.slotID, inv.instance as itemID, inv.quantity,
	                   i.itemName, i.level,
	                   CASE 
	                       WHEN w.itemID IS NOT NULL THEN 'Weapon'
	                       WHEN g.itemID IS NOT NULL THEN 'Gear' 
	                       WHEN c.itemID IS NOT NULL THEN 'Consumable'
	                       ELSE 'Unknown'
	                   END as itemType
	            FROM Inventory inv
	            JOIN Items i ON inv.instance = i.itemID
	            LEFT JOIN Weapons w ON i.itemID = w.itemID
	            LEFT JOIN Gears g ON i.itemID = g.itemID  
	            LEFT JOIN Consumables c ON i.itemID = c.itemID
	            WHERE inv.charID = ?
	            ORDER BY inv.slotID;
	            """;

	    List<InventoryItemDetail> inventoryDetails = new ArrayList<>();

	    try (PreparedStatement ps = cxn.prepareStatement(query)) {
	        ps.setInt(1, character.getCharID());

	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                InventoryItemDetail detail = new InventoryItemDetail(
	                    rs.getInt("slotID"),
	                    rs.getInt("itemID"),
	                    rs.getString("itemName"),
	                    rs.getString("itemType"),
	                    rs.getInt("quantity"),
	                    rs.getInt("level")
	                );
	                inventoryDetails.add(detail);
	            }
	        }
	    }
	    return inventoryDetails;
	}
	

}
