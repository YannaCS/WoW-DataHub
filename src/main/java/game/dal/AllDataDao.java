package game.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import game.model.*;

/**
 * Helper DAO class for getting all existing data from the database
 * Used by Dynamic ETL to access default/static data
 */
public class AllDataDao {
    
    private AllDataDao() {}
    
    /**
     * Get all statistics from the database
     */
    public static List<Statistics> getAllStatistics(Connection cxn) throws SQLException {
        String query = """
                SELECT statsName, description
                FROM Statistics
                ORDER BY statsName;
                """;
        
        List<Statistics> statistics = new ArrayList<>();
        
        try (PreparedStatement pstmt = cxn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Statistics stat = new Statistics(
                        rs.getString("statsName"),
                        rs.getString("description")
                    );
                    statistics.add(stat);
                }
            }
        }
        return statistics;
    }
    
    /**
     * Get all currencies from the database
     */
    public static List<Currencies> getAllCurrencies(Connection cxn) throws SQLException {
        String query = """
                SELECT currencyName, cap, weeklyCap
                FROM Currencies
                ORDER BY currencyName;
                """;
        
        List<Currencies> currencies = new ArrayList<>();
        
        try (PreparedStatement pstmt = cxn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Currencies currency = new Currencies(
                        rs.getString("currencyName"),
                        rs.getBigDecimal("cap"),
                        rs.getBigDecimal("weeklyCap")
                    );
                    currencies.add(currency);
                }
            }
        }
        return currencies;
    }
    
    /**
     * Get all gears from the database
     */
    public static List<Gears> getAllGears(Connection cxn) throws SQLException {
        String query = """
                SELECT G.itemID, I.itemName, I.level, I.maxStackSize, I.price, E.requiredLevel
                FROM Gears G
                JOIN Equipments E ON G.itemID = E.itemID
                JOIN Items I ON E.itemID = I.itemID
                ORDER BY I.itemName;
                """;
        
        List<Gears> gears = new ArrayList<>();
        
        try (PreparedStatement pstmt = cxn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Gears gear = new Gears(
                        rs.getInt("itemID"),
                        rs.getString("itemName"),
                        rs.getInt("level"),
                        rs.getInt("maxStackSize"),
                        rs.getBigDecimal("price"),
                        rs.getInt("requiredLevel")
                    );
                    gears.add(gear);
                }
            }
        }
        return gears;
    }
    
    /**
     * Get all consumables from the database
     */
    public static List<Consumables> getAllConsumables(Connection cxn) throws SQLException {
        String query = """
                SELECT C.itemID, I.itemName, I.level, I.maxStackSize, I.price, C.description
                FROM Consumables C
                JOIN Items I ON C.itemID = I.itemID
                ORDER BY I.itemName;
                """;
        
        List<Consumables> consumables = new ArrayList<>();
        
        try (PreparedStatement pstmt = cxn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Consumables consumable = new Consumables(
                        rs.getInt("itemID"),
                        rs.getString("itemName"),
                        rs.getInt("level"),
                        rs.getInt("maxStackSize"),
                        rs.getBigDecimal("price"),
                        rs.getString("description")
                    );
                    consumables.add(consumable);
                }
            }
        }
        return consumables;
    }
    
    /**
     * Get count of records in a table
     */
    public static int getTableCount(Connection cxn, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM " + tableName;
        
        try (PreparedStatement pstmt = cxn.prepareStatement(query)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        }
        return 0;
    }
    
    /**
     * Check if default data exists in the database
     */
    public static boolean hasDefaultData(Connection cxn) throws SQLException {
        int clansCount = getTableCount(cxn, "Clans");
        int weaponsCount = getTableCount(cxn, "Weapons");
        int statisticsCount = getTableCount(cxn, "Statistics");
        int currenciesCount = getTableCount(cxn, "Currencies");
        
        return clansCount > 0 && weaponsCount > 0 && statisticsCount > 0 && currenciesCount > 0;
    }
}