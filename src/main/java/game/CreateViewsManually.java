package game;

import java.sql.Connection;
import java.sql.SQLException;

import game.dal.ConnectionManager;

/**
 * Manual script to create views if they don't exist
 */
public class CreateViewsManually {
    
    public static void main(String[] args) {
        try {
            createViewsManually();
            System.out.println("✅ Views creation completed!");
        } catch (SQLException e) {
            System.err.println("❌ Views creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void createViewsManually() throws SQLException {
        try (Connection cxn = ConnectionManager.getConnection()) {
            Driver.createDatabaseViews(cxn);
        }
    }
}