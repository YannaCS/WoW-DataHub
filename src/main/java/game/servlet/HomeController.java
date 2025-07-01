package game.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.dal.ConnectionManager;
import game.dal.ViewsDao;
import game.model.Analytics.*;

@WebServlet("/home")
public class HomeController extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        try (Connection connection = ConnectionManager.getConnection()) {
            
            // Get all data from views
            OverallStats overallStats = ViewsDao.getOverallStats(connection);
            req.setAttribute("overallStats", overallStats);
            System.out.println("DEBUG: Overall stats loaded - Players: " + overallStats.getTotalPlayers());
            
            List<DailyActivePlayer> dailyActivePlayers = ViewsDao.getDailyActivePlayers(connection);
            req.setAttribute("dailyActiveUsers", dailyActivePlayers);
            System.out.println("DEBUG: Daily active players count: " + dailyActivePlayers.size());
            for (DailyActivePlayer dap : dailyActivePlayers) {
                System.out.println("  - Date: " + dap.getActivityDate() + ", Count: " + dap.getActiveCount());
            }
            
            List<TopPlayer> topPlayersByLevel = ViewsDao.getTopPlayersByLevel(connection);
            req.setAttribute("topPlayersByLevel", topPlayersByLevel);
            System.out.println("DEBUG: Top players by level count: " + topPlayersByLevel.size());
            
            List<TopPlayerWealth> topPlayersByWealth = ViewsDao.getTopPlayersByWealth(connection);
            req.setAttribute("topPlayersByWealth", topPlayersByWealth);
            System.out.println("DEBUG: Top players by wealth count: " + topPlayersByWealth.size());
            
            List<JobDistribution> jobDistribution = ViewsDao.getJobDistribution(connection);
            req.setAttribute("jobDistribution", jobDistribution);
            System.out.println("DEBUG: Job distribution count: " + jobDistribution.size());
            for (JobDistribution job : jobDistribution) {
                System.out.println("  - Job: " + job.getJobName() + ", Count: " + job.getCharacterCount());
            }
            
            List<ClanDistribution> clanDistribution = ViewsDao.getClanDistribution(connection);
            req.setAttribute("clanDistribution", clanDistribution);
            System.out.println("DEBUG: Clan distribution count: " + clanDistribution.size());
            
            List<CurrencyStats> currencyStats = ViewsDao.getCurrencyStats(connection);
            req.setAttribute("currencyStats", currencyStats);
            System.out.println("DEBUG: Currency stats count: " + currencyStats.size());
            
            List<ItemTypeStats> itemStats = ViewsDao.getItemTypeStats(connection);
            req.setAttribute("itemStats", itemStats);
            System.out.println("DEBUG: Item stats count: " + itemStats.size());
            
            messages.put("success", "Dashboard data loaded successfully from database views");
            
        } catch (SQLException e) {
            e.printStackTrace();
            messages.put("error", "Failed to load dashboard data: " + e.getMessage());
        }
        
        req.getRequestDispatcher("/Home.jsp").forward(req, resp);
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}