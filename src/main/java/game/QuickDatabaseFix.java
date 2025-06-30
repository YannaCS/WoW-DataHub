package game;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Random;

import game.dal.ConnectionManager;

/**
 * Quick fix to add missing column and update data
 */
public class QuickDatabaseFix {
    
    public static void main(String[] args) {
        try {
            addMissingColumn();
            System.out.println("✅ Database fix completed!");
        } catch (SQLException e) {
            System.err.println("❌ Database fix failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void addMissingColumn() throws SQLException {
        try (Connection cxn = ConnectionManager.getConnection()) {
            
            // Check if column exists
            boolean columnExists = false;
            try {
                var rs = cxn.createStatement().executeQuery("SELECT lastActiveDateTime FROM Players LIMIT 1");
                columnExists = true;
                rs.close();
            } catch (SQLException e) {
                // Column doesn't exist
            }
            
            if (!columnExists) {
                System.out.println("🔄 Adding lastActiveDateTime column...");
                
                // Add the column
                cxn.createStatement().executeUpdate("""
                    ALTER TABLE Players 
                    ADD COLUMN lastActiveDateTime DATETIME DEFAULT CURRENT_TIMESTAMP;
                """);
                
                // Update existing players with random dates
                Random random = new Random();
                var selectStmt = cxn.createStatement();
                var rs = selectStmt.executeQuery("SELECT playerID FROM Players");
                
                var updateStmt = cxn.prepareStatement("""
                    UPDATE Players 
                    SET lastActiveDateTime = DATE_SUB(NOW(), INTERVAL ? DAY)
                    WHERE playerID = ?;
                """);
                
                int count = 0;
                while (rs.next()) {
                    int playerID = rs.getInt("playerID");
                    int daysAgo = random.nextInt(30);
                    
                    updateStmt.setInt(1, daysAgo);
                    updateStmt.setInt(2, playerID);
                    updateStmt.executeUpdate();
                    count++;
                }
                
                rs.close();
                selectStmt.close();
                updateStmt.close();
                
                System.out.println("✅ Updated " + count + " players with activity dates");
            } else {
                System.out.println("✅ Column already exists");
            }
        }
    }
}