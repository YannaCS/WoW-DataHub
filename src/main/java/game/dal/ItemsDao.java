package game.dal;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
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
	 * Get most popular items based on inventory count
	 * @param cxn Database connection
	 * @param limit Number of items to return
	 * @return List of most popular items
	 */
	public static List<Items> getMostPopularItems(Connection cxn, int limit) throws SQLException {
		List<Items> popularItems = new ArrayList<>();
		
		String popularQuery = """
			SELECT i.*, COUNT(inv.instance) as popularity
			FROM Items i
			JOIN Inventory inv ON i.itemID = inv.instance
			GROUP BY i.itemID
			ORDER BY popularity DESC
			LIMIT ?
			""";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(popularQuery)) {
			pstmt.setInt(1, limit);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					popularItems.add(new Items(
						rs.getInt("itemID"),
						rs.getString("itemName"),
						rs.getInt("level"),
						rs.getInt("maxStackSize"),
						rs.getBigDecimal("price")
					));
				}
			}
		}
		return popularItems;
	}

	/**
	 * Get average item price
	 * @param cxn Database connection
	 * @return Average price of all items
	 */
	public static BigDecimal getAverageItemPrice(Connection cxn) throws SQLException {
		String avgQuery = "SELECT AVG(price) as avgPrice FROM Items WHERE price IS NOT NULL";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(avgQuery);
			ResultSet rs = pstmt.executeQuery()) {
			
			if (rs.next()) {
				return rs.getBigDecimal("avgPrice");
			}
		}
		return BigDecimal.ZERO;
	}

	/**
	 * Get item statistics by type
	 * @param cxn Database connection
	 * @return Map of item types to statistics
	 */
	public static Map<String, Integer> getItemTypeDistribution(Connection cxn) throws SQLException {
		Map<String, Integer> distribution = new HashMap<>();
		
		String typeQuery = """
			SELECT 
				CASE 
					WHEN w.itemID IS NOT NULL THEN 'Weapon'
					WHEN g.itemID IS NOT NULL THEN 'Gear'
					WHEN c.itemID IS NOT NULL THEN 'Consumable'
					ELSE 'Other'
				END as itemType,
				COUNT(*) as count
			FROM Items i
			LEFT JOIN Weapons w ON i.itemID = w.itemID
			LEFT JOIN Gears g ON i.itemID = g.itemID
			LEFT JOIN Consumables c ON i.itemID = c.itemID
			GROUP BY itemType
			""";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(typeQuery);
			ResultSet rs = pstmt.executeQuery()) {
			
			while (rs.next()) {
				distribution.put(rs.getString("itemType"), rs.getInt("count"));
			}
		}
		return distribution;
	}

	/**
	 * Get items by price range
	 * @param cxn Database connection
	 * @param minPrice Minimum price
	 * @param maxPrice Maximum price
	 * @return List of items in price range
	 */
	public static List<Items> getItemsByPriceRange(Connection cxn, BigDecimal minPrice, BigDecimal maxPrice) throws SQLException {
		List<Items> items = new ArrayList<>();
		
		String priceRangeQuery = """
			SELECT * FROM Items 
			WHERE price BETWEEN ? AND ?
			ORDER BY price ASC
			""";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(priceRangeQuery)) {
			pstmt.setBigDecimal(1, minPrice);
			pstmt.setBigDecimal(2, maxPrice);
			
			try (ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
					items.add(new Items(
						rs.getInt("itemID"),
						rs.getString("itemName"),
						rs.getInt("level"),
						rs.getInt("maxStackSize"),
						rs.getBigDecimal("price")
					));
				}
			}
		}
		return items;
	}

	/**
	 * Get total item count
	 * @param cxn Database connection
	 * @return Total number of items
	 */
	public static int getTotalItemCount(Connection cxn) throws SQLException {
		String countQuery = "SELECT COUNT(*) as total FROM Items";
		
		try (PreparedStatement pstmt = cxn.prepareStatement(countQuery);
			ResultSet rs = pstmt.executeQuery()) {
			
			if (rs.next()) {
				return rs.getInt("total");
			}
		}
		return 0;
	}
}
