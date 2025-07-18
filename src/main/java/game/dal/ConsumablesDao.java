package game.dal;

import game.model.*;

import java.math.BigDecimal;
import java.sql.*;

public class ConsumablesDao {

	/**
	 * create rows in both Items and Consumables tables
	 */
	public static Consumables create(
			Connection cxn,
			String itemName,
			int level,
			int maxStackSize,
			BigDecimal price,
			String description
	) throws SQLException {
		int itemID = ItemsDao.create(cxn, itemName, level, maxStackSize, price);
		String insertConsumables = "INSERT INTO Consumables (itemID, description) VALUES (?, ?);";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(insertConsumables)) {
			pstmt.setInt(1, itemID);
			pstmt.setString(2, description);
			
			pstmt.executeUpdate();
			
			return new Consumables(itemID, itemName, level, maxStackSize, price, description);
		} catch (SQLException e) {
		    if (e.getMessage().contains("Duplicate entry")) {
		        // Silently ignore duplicates for ETL process
		        return null; // or return existing record if you have a get method
		    } else {
		        throw e;
		    }
		}
	}
	
	
	/**
	 * retrieves a single record based on pk(itemID)
	 * return an Consumables object or null if not found
	 */
	public static Consumables getConsumableByItemID(Connection cxn, int itemID) throws SQLException {
	    String query_Consumable = """
	            SELECT I.itemID,
	                   I.itemName,
	                   I.level,
	                   I.maxStackSize,
	                   I.price,
	                   C.description
	            FROM Consumables C
	            JOIN Items I ON C.itemID = I.itemID
	            WHERE C.itemID = ? ;
	            """;
	    try (PreparedStatement pstmt = cxn.prepareStatement(query_Consumable)) {
	        pstmt.setInt(1, itemID);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return new Consumables(
	                        rs.getInt("itemID"),
	                        rs.getString("itemName"),
	                        rs.getInt("level"),
	                        rs.getInt("maxStackSize"),
	                        rs.getBigDecimal("price"),
	                        rs.getString("description")
	                        );
	            } else {
	                return null;
	            }
	        }
	    }
	}

	/**
	 * update an existing Consumables record (description) in the database
	 * returns a Consumables object
	 */
	public static Consumables updateConsumablesDescription(
			Connection cxn,
			Consumables old,
			String newDescription
	) throws SQLException {
		String query_updateConsumables = """
				UPDATE Consumables
				SET description = ?
				WHERE itemID = ?;
				""";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(query_updateConsumables)) {
			pstmt.setString(1, newDescription);
			pstmt.setInt(2, old.getItemID());
			
			pstmt.executeUpdate();
			
			return new Consumables(
					old.getItemID(),
					old.getItemName(),
					old.getLevel(),
					old.getMaxStackSize(),
					old.getPrice(),
					newDescription
					);
		}
	}

}
