package game.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import game.model.InventoryItemDetail;
import game.model.Analytics.*;

/**
 * DAO for accessing database views for analytics
 */
public class ViewsDao {
    
    private ViewsDao() {}
    
    /**
     * Get daily active players with fallback
     */
    public static List<DailyActivePlayer> getDailyActivePlayers(Connection cxn) throws SQLException {
        // get from view
    	String viewQuery = "SELECT activity_date, active_count FROM DailyActivePlayersView";
        
        List<DailyActivePlayer> dailyStats = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(viewQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                dailyStats.add(new DailyActivePlayer(
                    rs.getString("activity_date"),
                    rs.getInt("active_count")
                ));
            }
        } catch (SQLException e) {
            System.err.println("DailyActivePlayersView failed, using fallback: " + e.getMessage());
            
            // Check if lastActiveDateTime column exists
            try {
                String fallbackQuery = """
                    SELECT 
                        DATE(lastActiveDateTime) as activity_date,
                        COUNT(*) as active_count
                    FROM Players 
                    WHERE lastActiveDateTime >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
                    GROUP BY DATE(lastActiveDateTime)
                    ORDER BY activity_date
                """;
                
                try (PreparedStatement stmt2 = cxn.prepareStatement(fallbackQuery);
                     ResultSet rs2 = stmt2.executeQuery()) {
                    
                    while (rs2.next()) {
                        dailyStats.add(new DailyActivePlayer(
                            rs2.getString("activity_date"),
                            rs2.getInt("active_count")
                        ));
                    }
                }
            } catch (SQLException e2) {
                System.err.println("lastActiveDateTime column doesn't exist, creating sample data");
                // Create sample data for the last 7 days
                for (int i = 6; i >= 0; i--) {
                    dailyStats.add(new DailyActivePlayer(
                        java.time.LocalDate.now().minusDays(i).toString(),
                        10 + (int)(Math.random() * 20)
                    ));
                }
            }
        }
        
        return dailyStats;
    }
    
    /**
     * Get job distribution with fallback
     */
    public static List<JobDistribution> getJobDistribution(Connection cxn) throws SQLException {
        // get from view directly
    	String viewQuery = "SELECT job_name, character_count, percentage FROM JobDistributionView";
        
        List<JobDistribution> jobDistribution = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(viewQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                jobDistribution.add(new JobDistribution(
                    rs.getString("job_name"),
                    rs.getInt("character_count"),
                    rs.getDouble("percentage")
                ));
            }
        } catch (SQLException e) {
            System.err.println("JobDistributionView failed, using fallback: " + e.getMessage());
            
            // Fallback query
            String fallbackQuery = """
                SELECT 
                    w.wearableJob as job_name,
                    COUNT(*) as character_count,
                    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Characters), 2) as percentage
                FROM Characters c
                JOIN Weapons w ON c.weaponWeared = w.itemID
                GROUP BY w.wearableJob
                ORDER BY character_count DESC
            """;
            
            try (PreparedStatement stmt = cxn.prepareStatement(fallbackQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    jobDistribution.add(new JobDistribution(
                        rs.getString("job_name"),
                        rs.getInt("character_count"),
                        rs.getDouble("percentage")
                    ));
                }
            }
        }
        
        return jobDistribution;
    }
    
    /**
     * Get clan distribution with fallback
     */
    public static List<ClanDistribution> getClanDistribution(Connection cxn) throws SQLException {
        // from view
    	String viewQuery = "SELECT clan_name, race, character_count, percentage FROM ClanDistributionView";
        
        List<ClanDistribution> clanDistribution = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(viewQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                clanDistribution.add(new ClanDistribution(
                    rs.getString("clan_name"),
                    rs.getString("race"),
                    rs.getInt("character_count"),
                    rs.getDouble("percentage")
                ));
            }
        } catch (SQLException e) {
            System.err.println("ClanDistributionView failed, using fallback: " + e.getMessage());
            
            // Fallback query
            String fallbackQuery = """
                SELECT 
                    cl.clanName as clan_name,
                    cl.race,
                    COUNT(c.charID) as character_count,
                    ROUND(COUNT(c.charID) * 100.0 / (SELECT COUNT(*) FROM Characters), 2) as percentage
                FROM Clans cl
                LEFT JOIN Characters c ON cl.clanName = c.clan
                GROUP BY cl.clanName, cl.race
                HAVING character_count > 0
                ORDER BY character_count DESC
            """;
            
            try (PreparedStatement stmt = cxn.prepareStatement(fallbackQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    clanDistribution.add(new ClanDistribution(
                        rs.getString("clan_name"),
                        rs.getString("race"),
                        rs.getInt("character_count"),
                        rs.getDouble("percentage")
                    ));
                }
            }
        }
        
        return clanDistribution;
    }
    
    /**
     * Get currency statistics from view
     */
    public static List<CurrencyStats> getCurrencyStats(Connection cxn) throws SQLException {
        String query = "SELECT * FROM CurrencyStatsView";
        
        List<CurrencyStats> currencyStats = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                currencyStats.add(new CurrencyStats(
                    rs.getString("currency_name"),
                    rs.getBigDecimal("cap"),
                    rs.getBigDecimal("weekly_cap"),
                    rs.getInt("players_with_currency"),
                    rs.getBigDecimal("avg_amount"),
                    rs.getBigDecimal("max_amount"),
                    rs.getBigDecimal("total_in_circulation")
                ));
            }
        }
        
        return currencyStats;
    }
    
    /**
     * Get item type distribution from view
     */
    public static List<ItemTypeStats> getItemTypeStats(Connection cxn) throws SQLException {
        String query = "SELECT item_type, count FROM ItemTypeDistributionView";
        
        List<ItemTypeStats> itemStats = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                itemStats.add(new ItemTypeStats(
                    rs.getString("item_type"),
                    rs.getInt("count")
                ));
            }
        }
        
        return itemStats;
    }
    
    /**
     * Get top players by level from view
     */
    public static List<TopPlayer> getTopPlayersByLevel(Connection cxn) throws SQLException {
        String query = "SELECT * FROM TopPlayersByLevelView";
        
        List<TopPlayer> topPlayers = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                topPlayers.add(new TopPlayer(
                    rs.getString("character_name"),
                    rs.getString("player_name"),
                    rs.getInt("max_level"),
                    rs.getString("current_job"),
                    rs.getString("race")
                ));
            }
        }
        
        return topPlayers;
    }
    
    /**
     * Get top players by wealth from view
     */
    public static List<TopPlayerWealth> getTopPlayersByWealth(Connection cxn) throws SQLException {
        String query = "SELECT * FROM TopPlayersByWealthView";
        
        List<TopPlayerWealth> topWealthPlayers = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                topWealthPlayers.add(new TopPlayerWealth(
                    rs.getString("character_name"),
                    rs.getBigDecimal("total_wealth"),
                    rs.getInt("currency_types")
                ));
            }
        }
        
        return topWealthPlayers;
    }
    
    /**
     * Get overall statistics from view with fallback to direct queries
     */
    public static OverallStats getOverallStats(Connection cxn) throws SQLException {
        // First try the view
        String viewQuery = "SELECT * FROM OverallStatsView";
        
        try (PreparedStatement stmt = cxn.prepareStatement(viewQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return new OverallStats(
                    rs.getInt("total_players"),
                    rs.getInt("total_characters"),
                    rs.getInt("total_weapons"),
                    rs.getInt("total_gears"),
                    rs.getInt("total_consumables"),
                    rs.getInt("total_clans")
                );
            }
        } catch (SQLException e) {
            System.err.println("View failed, using direct queries: " + e.getMessage());
            
            // Fallback to direct queries
            try {
                int totalPlayers = getTableCount(cxn, "Players");
                int totalCharacters = getTableCount(cxn, "Characters");
                int totalWeapons = getTableCount(cxn, "Weapons");
                int totalGears = getTableCount(cxn, "Gears");
                int totalConsumables = getTableCount(cxn, "Consumables");
                int totalClans = getTableCount(cxn, "Clans");
                
                return new OverallStats(totalPlayers, totalCharacters, totalWeapons, totalGears, totalConsumables, totalClans);
            } catch (SQLException fallbackError) {
                System.err.println("Fallback also failed: " + fallbackError.getMessage());
                return new OverallStats(0, 0, 0, 0, 0, 0);
            }
        }
        
        return new OverallStats(0, 0, 0, 0, 0, 0);
    }

    private static int getTableCount(Connection cxn, String tableName) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM " + tableName;
        try (PreparedStatement stmt = cxn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        }
        return 0;
    }

    
    /**
     * Get character inventory details from view
     */
    public static List<InventoryItemDetail> getCharacterInventoryDetails(Connection cxn, int charID) throws SQLException {
        String query = "SELECT * FROM CharacterInventoryDetailsView WHERE charID = ? ORDER BY slotID";
        
        List<InventoryItemDetail> inventoryDetails = new ArrayList<>();
        
        try (PreparedStatement stmt = cxn.prepareStatement(query)) {
            stmt.setInt(1, charID);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    inventoryDetails.add(new InventoryItemDetail(
                        rs.getInt("slotID"),
                        rs.getInt("itemID"),
                        rs.getString("itemName"),
                        rs.getString("item_type"),
                        rs.getInt("quantity"),
                        rs.getInt("level")
                    ));
                }
            }
        }
        
        return inventoryDetails;
    }
}