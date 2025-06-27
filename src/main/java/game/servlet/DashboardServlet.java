package game.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.math.BigDecimal;
import com.fasterxml.jackson.databind.ObjectMapper;

import game.dal.*;
import game.model.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            // Fetch dashboard statistics
            int totalCharacters = CharactersDao.getTotalCharacterCount(cxn);
            List<Characters> topPlayers = CharactersDao.getTopPlayersByLevel(cxn, 10);
            Map<String, Integer> classDistribution = CharactersDao.getClassDistribution(cxn);
            List<Items> popularItems = ItemsDao.getMostPopularItems(cxn, 10);
            BigDecimal averageItemPrice = ItemsDao.getAverageItemPrice(cxn);
            
            // Calculate additional metrics
            double averageLevel = calculateAverageLevel(cxn);
            double characterGrowth = CharactersDao.getCharacterGrowthRate(cxn, 30);
            int totalGuilds = calculateTotalGuilds(cxn);
            BigDecimal averageWealth = calculateAverageWealth(cxn);
            
            // Convert data to JSON for JavaScript
            String classDistributionJson = objectMapper.writeValueAsString(classDistribution);
            String topPlayersJson = createTopPlayersJson(topPlayers);
            String realmDataJson = "{}"; // Placeholder for realm data
            
            // Set attributes for JSP
            request.setAttribute("totalCharacters", totalCharacters);
            request.setAttribute("topPlayers", topPlayers);
            request.setAttribute("classDistribution", classDistribution);
            request.setAttribute("popularItems", popularItems);
            request.setAttribute("averageLevel", averageLevel);
            request.setAttribute("characterGrowth", String.format("%.1f", characterGrowth));
            request.setAttribute("totalGuilds", totalGuilds);
            request.setAttribute("averageWealth", averageWealth);
            request.setAttribute("lastUpdated", new Date());
            request.setAttribute("totalRealms", 1); // Placeholder
            
            // JSON data for JavaScript
            request.setAttribute("classDistributionJson", classDistributionJson);
            request.setAttribute("topPlayersJson", topPlayersJson);
            request.setAttribute("realmDataJson", realmDataJson);
            
            // Forward to JSP
            request.getRequestDispatcher("index.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database connection error: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Unable to load dashboard data");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "An unexpected error occurred");
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle AJAX requests for filtered data
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            switch (action) {
                case "filter":
                    handleFilterRequest(request, response, cxn);
                    break;
                case "refresh":
                    handleRefreshRequest(request, response, cxn);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                "Database error: " + e.getMessage());
        }
    }
    
    private void handleFilterRequest(HttpServletRequest request, HttpServletResponse response, 
                                   Connection cxn) throws IOException, SQLException {
        // Get filter parameters
        String realmFilter = request.getParameter("realm");
        String classFilter = request.getParameter("class");
        String levelRange = request.getParameter("levelRange");
        
        // Apply filters and get updated data
        List<Characters> filteredPlayers = getFilteredPlayers(cxn, realmFilter, classFilter, levelRange);
        Map<String, Integer> filteredClassDistribution = getFilteredClassDistribution(cxn, realmFilter, levelRange);
        
        // Create response JSON
        DashboardData dashboardData = new DashboardData();
        dashboardData.setTopPlayers(filteredPlayers);
        dashboardData.setClassDistribution(filteredClassDistribution);
        dashboardData.setTotalCharacters(filteredPlayers.size());
        
        response.getWriter().write(objectMapper.writeValueAsString(dashboardData));
    }
    
    private void handleRefreshRequest(HttpServletRequest request, HttpServletResponse response, 
                                    Connection cxn) throws IOException, SQLException {
        // Get fresh data
        int totalCharacters = CharactersDao.getTotalCharacterCount(cxn);
        Map<String, Integer> classDistribution = CharactersDao.getClassDistribution(cxn);
        
        DashboardData dashboardData = new DashboardData();
        dashboardData.setTotalCharacters(totalCharacters);
        dashboardData.setClassDistribution(classDistribution);
        
        response.getWriter().write(objectMapper.writeValueAsString(dashboardData));
    }
    
    private List<Characters> getFilteredPlayers(Connection cxn, String realmFilter, 
                                              String classFilter, String levelRange) throws SQLException {
        // For now, return top players (implement filtering logic as needed)
        return CharactersDao.getTopPlayersByLevel(cxn, 10);
    }
    
    private Map<String, Integer> getFilteredClassDistribution(Connection cxn, String realmFilter, 
                                                            String levelRange) throws SQLException {
        // For now, return regular distribution (implement filtering logic as needed)
        return CharactersDao.getClassDistribution(cxn);
    }
    
    private double calculateAverageLevel(Connection cxn) throws SQLException {
        // Calculate average level across all characters
        // This would require adding a method to CharactersDao
        return 47.3; // Placeholder
    }
    
    private int calculateTotalGuilds(Connection cxn) throws SQLException {
        // Count unique clans (treating them as guilds)
        return 12; // Placeholder - you'd need to implement this
    }
    
    private BigDecimal calculateAverageWealth(Connection cxn) throws SQLException {
        // Calculate average wealth across all characters
        // This would require querying CharacterWealth table
        return new BigDecimal("1250.50"); // Placeholder
    }
    
    private String createTopPlayersJson(List<Characters> topPlayers) throws IOException {
        // Create a simplified JSON structure for JavaScript consumption
        return objectMapper.writeValueAsString(topPlayers.stream()
            .map(character -> {
                TopPlayerData data = new TopPlayerData();
                data.setName(character.getFirstName() + " " + character.getLastName());
                data.setLevel(character.getWeaponWeared().getLevel());
                data.setCharacterClass(character.getWeaponWeared().getWearableJob());
                data.setClan(character.getClan().getClanName());
                return data;
            })
            .toArray());
    }
    
    // Inner classes for JSON serialization
    public static class DashboardData {
        private List<Characters> topPlayers;
        private Map<String, Integer> classDistribution;
        private int totalCharacters;
        
        // Getters and setters
        public List<Characters> getTopPlayers() { return topPlayers; }
        public void setTopPlayers(List<Characters> topPlayers) { this.topPlayers = topPlayers; }
        
        public Map<String, Integer> getClassDistribution() { return classDistribution; }
        public void setClassDistribution(Map<String, Integer> classDistribution) { 
            this.classDistribution = classDistribution; 
        }
        
        public int getTotalCharacters() { return totalCharacters; }
        public void setTotalCharacters(int totalCharacters) { this.totalCharacters = totalCharacters; }
    }
    
    public static class TopPlayerData {
        private String name;
        private int level;
        private String characterClass;
        private String clan;
        
        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
        
        public String getCharacterClass() { return characterClass; }
        public void setCharacterClass(String characterClass) { this.characterClass = characterClass; }
        
        public String getClan() { return clan; }
        public void setClan(String clan) { this.clan = clan; }
    }
}