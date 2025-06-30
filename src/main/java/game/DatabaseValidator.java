package game;

import java.sql.Connection;
import java.sql.SQLException;

import game.dal.ConnectionManager;

public class DatabaseValidator {
    
    public static void main(String[] args) {
        try {
            validateDatabase();
        } catch (SQLException e) {
            System.err.println("❌ Validation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public static void validateDatabase() throws SQLException {
        try (Connection cxn = ConnectionManager.getConnection()) {
            
            System.out.println("🔍 Validating database schema and data...");
            
            // Check if lastActiveDateTime column exists
            boolean hasColumn = false;
            try {
                var rs = cxn.createStatement().executeQuery("SELECT lastActiveDateTime FROM Players LIMIT 1");
                hasColumn = true;
                rs.close();
                System.out.println("✅ lastActiveDateTime column exists");
            } catch (SQLException e) {
                System.out.println("❌ lastActiveDateTime column missing");
            }
            
            // Check table counts
            checkTableCount(cxn, "Players");
            checkTableCount(cxn, "Characters");
            checkTableCount(cxn, "Weapons");
            checkTableCount(cxn, "Gears");
            checkTableCount(cxn, "Consumables");
            checkTableCount(cxn, "Clans");
            checkTableCount(cxn, "Currencies");
            
            // Check views
            checkView(cxn, "OverallStatsView");
            checkView(cxn, "JobDistributionView");
            checkView(cxn, "ClanDistributionView");
            checkView(cxn, "DailyActivePlayersView");
            
            System.out.println("🎯 Database validation completed");
        }
    }
    
    private static void checkTableCount(Connection cxn, String tableName) throws SQLException {
        var stmt = cxn.createStatement();
        var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
        if (rs.next()) {
            int count = rs.getInt(1);
            System.out.println("📊 " + tableName + ": " + count + " records");
        }
        rs.close();
        stmt.close();
    }
    
    private static void checkView(Connection cxn, String viewName) {
        try {
            var stmt = cxn.createStatement();
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + viewName);
            if (rs.next()) {
                System.out.println("✅ View " + viewName + " exists and accessible");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("❌ View " + viewName + " not accessible: " + e.getMessage());
        }
    }
}
