package game.dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import game.model.*;


public class ItemsDao {
	
	protected ItemsDao() { }

	public static int create(
		    Connection cxn,
		    String itemName, 
		    int level, 
		    int maxStackSize,
		    BigDecimal price
			) throws SQLException{
		
	    final String insertItems = """
	    	      INSERT INTO Items (itemName, level, maxStackSize, price) VALUES (?, ?, ?, ?);
	    		""";
	    try(PreparedStatement insertStmt = cxn.prepareStatement(insertItems, Statement.RETURN_GENERATED_KEYS)){
	    	
	        insertStmt.setString(1, itemName);
	        insertStmt.setInt(2, level);
	        insertStmt.setInt(3, maxStackSize);
	        insertStmt.setBigDecimal(4, price);

	        insertStmt.executeUpdate();
	        
	        try (ResultSet rs = insertStmt.getGeneratedKeys()) {
	            if (rs.next()) {
	              return rs.getInt(1);
	            } else {
	              throw new SQLException("Unable to retrieve auto-generated key");
	            }
	          }

	    }
		
	}
	
	
	public static String getNameByItemID(Connection cxn, int itemID) throws SQLException {
		String selectItem = """
				SELECT itemName
				FROM Items 
				WHERE itemID = ?;
				""";
		
		try (PreparedStatement selectStmt = cxn.prepareStatement(selectItem)) {
			selectStmt.setInt(1, itemID);
			
			try (ResultSet result = selectStmt.executeQuery()) {
				if (result.next()) {
					return result.getString("itemName");
				} else {
					return null;
				}
			}
		}
	}
	
	
	public static void delete(Connection cxn, Items item) throws SQLException{
	    String deleteItems = "DELETE FROM Items WHERE itemID = ?;";

	    try (PreparedStatement deleteStmt = cxn.prepareStatement(deleteItems)) {
	      deleteStmt.setInt(1, item.getItemID());
	      deleteStmt.executeUpdate();
	    }
		
	}
	
	/**
	 * Get item by ID with all basic information
	 */
	public static Items getItemByID(Connection cxn, int itemID) throws SQLException {
	    String selectItem = """
	            SELECT itemID, itemName, level, maxStackSize, price
	            FROM Items 
	            WHERE itemID = ?;
	            """;
	    
	    try (PreparedStatement selectStmt = cxn.prepareStatement(selectItem)) {
	        selectStmt.setInt(1, itemID);
	        
	        try (ResultSet result = selectStmt.executeQuery()) {
	            if (result.next()) {
	                return new Items(
	                        result.getInt("itemID"),
	                        result.getString("itemName"),
	                        result.getInt("level"),
	                        result.getInt("maxStackSize"),
	                        result.getBigDecimal("price"));
	            } else {
	                return null;
	            }
	        }
	    }
	}

	/**
	 * Get item type (Weapon, Gear, or Consumable)
	 */
	public static String getItemType(Connection cxn, int itemID) throws SQLException {
	    // Check if it's a weapon
	    String checkWeapon = "SELECT 1 FROM Weapons WHERE itemID = ?";
	    try (PreparedStatement ps = cxn.prepareStatement(checkWeapon)) {
	        ps.setInt(1, itemID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) return "Weapon";
	        }
	    }
	    
	    // Check if it's a gear
	    String checkGear = "SELECT 1 FROM Gears WHERE itemID = ?";
	    try (PreparedStatement ps = cxn.prepareStatement(checkGear)) {
	        ps.setInt(1, itemID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) return "Gear";
	        }
	    }
	    
	    // Check if it's a consumable
	    String checkConsumable = "SELECT 1 FROM Consumables WHERE itemID = ?";
	    try (PreparedStatement ps = cxn.prepareStatement(checkConsumable)) {
	        ps.setInt(1, itemID);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) return "Consumable";
	        }
	    }
	    
	    return "Unknown";
	}
	

}
