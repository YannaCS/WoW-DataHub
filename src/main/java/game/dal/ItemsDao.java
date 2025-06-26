package game.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import game.model.Items;

public class ItemsDao {
    
    private ItemsDao() {}
    
    /**
     * Create a new item and return the generated itemID
     */
    public static int create(
            Connection cxn,
            String itemName,
            int level,
            int maxStackSize,
            double price
    ) throws SQLException {
        String insertItem = """
            INSERT INTO Items (itemName, level, maxStackSize, price)
            VALUES (?, ?, ?, ?);
        """;
        
        try (PreparedStatement insertStmt = cxn.prepareStatement(insertItem, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setString(1, itemName);
            insertStmt.setInt(2, level);
            insertStmt.setInt(3, maxStackSize);
            insertStmt.setDouble(4, price);
            insertStmt.executeUpdate();
            
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated itemID.");
                }
            }
        }
    }
    
    /**
     * Get item by itemID
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
                        result.getDouble("price")
                    );
                } else {
                    return null;
                }
            }
        }
    }
}