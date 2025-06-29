package game.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import game.model.Characters;
import game.model.EquippedItems;
import game.model.Gears;
import game.model.Items;
import game.service.BusinessRulesService;


public class EquippedItemsDao {
	
	private EquippedItemsDao() { }
	

	public static EquippedItems create( Connection cxn,
	           Characters character,
	           String equipmentSlot,
	           Gears gears
	           ) throws SQLException {
		
		try {
			// Validate equipment slot compatibility
			BusinessRulesService.validateEquipmentSlot(cxn, gears, equipmentSlot);
		} catch (BusinessRulesService.BusinessRuleException e) {
			throw new SQLException("Equipment validation failed: " + e.getMessage());
		}
		
        String insertEquippedItemsSQL = """
                INSERT INTO EquippedItems (charID, equipPosition, itemID) 
                VALUES (?, ?, ?);
            """;

            try (PreparedStatement ps = cxn.prepareStatement(insertEquippedItemsSQL)) {
                ps.setInt(1, character.getCharID()); 
                ps.setString(2, equipmentSlot);
                ps.setInt(3, gears.getItemID());
                ps.executeUpdate();
            }

            return new EquippedItems(character.getCharID(), equipmentSlot, gears.getItemID());
	}
	
	
	
	public static EquippedItems getEquippedItemsByCharIDAndSlot( Connection cxn,
			int charID,
			String equipmentSlot
	         ) throws SQLException{
		
       String query = """
             SELECT charID, equipPosition, itemID
             FROM EquippedItems
             WHERE charID = ? AND equipPosition = ?;
         """;

	    try (PreparedStatement selectStmt = cxn.prepareStatement(query)) {
	    	
	      selectStmt.setInt(1, charID); 
	      selectStmt.setString(2, equipmentSlot);  

	      try (ResultSet results = selectStmt.executeQuery()) {
	        if (results.next()) {
	          return new EquippedItems(
	            results.getInt("charID"),
	            results.getString("equipPosition"),
	            results.getInt("itemID")

	          );
	        } else {
	          return null;
	        }
	      }
	    }
	}
	
	
	public static List<EquippedItems> getEquippedItemsOnlyByCharacters( Connection cxn,
			Characters character
	         ) throws SQLException{
		
	     String query = """
	             SELECT charID, equipPosition, itemID
	             FROM EquippedItems
	             WHERE charID = ?;
	         """;

	        List<EquippedItems> equippedItems = new ArrayList<>();

	        try (PreparedStatement ps = cxn.prepareStatement(query)) {
	            ps.setInt(1, character.getCharID()); 

	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                	EquippedItems equippedItem = new EquippedItems(
	                        rs.getInt("charID"),
	        	            rs.getString("equipPosition"),
	                        rs.getInt("itemID")
	                        );
	                	equippedItems.add(equippedItem);
	                }
	    	        return equippedItems;
	            }
	        }
	    }
	
	public static EquippedItems updateEquippedItems(Connection cxn, EquippedItems equippedItems, Gears newGear) throws SQLException {
		
		try {
			// Validate equipment slot compatibility for the new gear
			BusinessRulesService.validateEquipmentSlot(cxn, newGear, equippedItems.getEquipPosition());
		} catch (BusinessRulesService.BusinessRuleException e) {
			throw new SQLException("Equipment validation failed: " + e.getMessage());
		}

		String updateEquippedItemsSQL = """
				    UPDATE EquippedItems
				    SET itemID = ?
				    WHERE charID = ? AND equipPosition = ?;
				""";

		try (PreparedStatement ps = cxn.prepareStatement(updateEquippedItemsSQL)) {
			ps.setInt(1, newGear.getItemID());
			ps.setInt(2, equippedItems.getCharID());
			ps.setString(3, equippedItems.getEquipPosition());
		    ps.executeUpdate();
		    equippedItems.setItemID(newGear.getItemID());
		    
		    return equippedItems;
		}
	}
	
	/**
	 * Create equipped item with validation for any item type (including weapons in MAIN_HAND)
	 */
	public static EquippedItems createWithItemValidation(Connection cxn,
			Characters character,
			String equipmentSlot,
			Items item) throws SQLException {
		
		try {
			// Validate equipment slot compatibility
			BusinessRulesService.validateEquipmentSlot(cxn, item, equipmentSlot);
		} catch (BusinessRulesService.BusinessRuleException e) {
			throw new SQLException("Equipment validation failed: " + e.getMessage());
		}
		
		String insertEquippedItemsSQL = """
		        INSERT INTO EquippedItems (charID, equipPosition, itemID) 
		        VALUES (?, ?, ?);
		    """;

		    try (PreparedStatement ps = cxn.prepareStatement(insertEquippedItemsSQL)) {
		        ps.setInt(1, character.getCharID()); 
		        ps.setString(2, equipmentSlot);
		        ps.setInt(3, item.getItemID());
		        ps.executeUpdate();
		    }

		    return new EquippedItems(character.getCharID(), equipmentSlot, item.getItemID());
	}
	
	/**
	 * Get equipped item by character and equipment slot with full item details
	 */
	public static EquippedItems getEquippedItemWithDetailsById(Connection cxn,
			int charID,
			String equipmentSlot) throws SQLException {
		
		String query = """
		    SELECT ei.charID, ei.equipPosition, ei.itemID, i.itemName, i.level, i.maxStackSize, i.price
		    FROM EquippedItems ei
		    JOIN Items i ON ei.itemID = i.itemID
		    WHERE ei.charID = ? AND ei.equipPosition = ?;
		""";

		try (PreparedStatement selectStmt = cxn.prepareStatement(query)) {
			selectStmt.setInt(1, charID); 
			selectStmt.setString(2, equipmentSlot);  

			try (ResultSet results = selectStmt.executeQuery()) {
				if (results.next()) {
					return new EquippedItems(
						results.getInt("charID"),
						results.getString("equipPosition"),
						results.getInt("itemID")
					);
				} else {
					return null;
				}
			}
		}
	}
	
	/**
	 * Check if character has any item equipped in a specific slot
	 */
	public static boolean hasItemEquippedInSlot(Connection cxn, Characters character, String equipmentSlot) throws SQLException {
		String query = """
		    SELECT COUNT(*) as count
		    FROM EquippedItems
		    WHERE charID = ? AND equipPosition = ?;
		""";

		try (PreparedStatement ps = cxn.prepareStatement(query)) {
			ps.setInt(1, character.getCharID());
			ps.setString(2, equipmentSlot);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("count") > 0;
				}
				return false;
			}
		}
	}
	
	/**
	 * Remove equipped item from specific slot
	 */
	public static void unequipItem(Connection cxn, Characters character, String equipmentSlot) throws SQLException {
		// Validate that MAIN_HAND cannot be unequipped (character must always have a weapon)
		if ("MAIN_HAND".equals(equipmentSlot)) {
			throw new SQLException("Cannot unequip MAIN_HAND slot - character must always have a weapon equipped");
		}
		
		String deleteSQL = "DELETE FROM EquippedItems WHERE charID = ? AND equipPosition = ?;";

		try (PreparedStatement deleteStmt = cxn.prepareStatement(deleteSQL)) {
			deleteStmt.setInt(1, character.getCharID());
			deleteStmt.setString(2, equipmentSlot);
			deleteStmt.executeUpdate();
		}
	}
	
	/**
	 * Get count of equipped items for a character
	 */
	public static int getEquippedItemCount(Connection cxn, Characters character) throws SQLException {
		String query = """
		    SELECT COUNT(*) as count
		    FROM EquippedItems
		    WHERE charID = ?;
		""";

		try (PreparedStatement ps = cxn.prepareStatement(query)) {
			ps.setInt(1, character.getCharID());

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("count");
				}
				return 0;
			}
		}
	}
	
	
	public static void delete(Connection cxn, EquippedItems equippedItems) throws SQLException{
		// Validate that MAIN_HAND cannot be deleted (character must always have a weapon)
		if ("MAIN_HAND".equals(equippedItems.getEquipPosition())) {
			throw new SQLException("Cannot delete MAIN_HAND slot - character must always have a weapon equipped");
		}
		
	    String deleteEquippedItem = "DELETE FROM EquippedItems WHERE charID = ? AND equipPosition = ?;";

	    try (PreparedStatement deleteStmt = cxn.prepareStatement(deleteEquippedItem)) {
	      deleteStmt.setInt(1, equippedItems.getCharID());
	      deleteStmt.setString(2, equippedItems.getEquipPosition());
	      deleteStmt.executeUpdate();
	    }
	}
	

}