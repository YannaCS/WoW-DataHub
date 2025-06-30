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
            
            List<DailyActivePlayer> dailyActivePlayers = ViewsDao.getDailyActivePlayers(connection);
            req.setAttribute("dailyActiveUsers", dailyActivePlayers);
            
            List<TopPlayer> topPlayersByLevel = ViewsDao.getTopPlayersByLevel(connection);
            req.setAttribute("topPlayersByLevel", topPlayersByLevel);
            
            List<TopPlayerWealth> topPlayersByWealth = ViewsDao.getTopPlayersByWealth(connection);
            req.setAttribute("topPlayersByWealth", topPlayersByWealth);
            
            List<JobDistribution> jobDistribution = ViewsDao.getJobDistribution(connection);
            req.setAttribute("jobDistribution", jobDistribution);
            
            List<ClanDistribution> clanDistribution = ViewsDao.getClanDistribution(connection);
            req.setAttribute("clanDistribution", clanDistribution);
            
            List<CurrencyStats> currencyStats = ViewsDao.getCurrencyStats(connection);
            req.setAttribute("currencyStats", currencyStats);
            
            List<ItemTypeStats> itemStats = ViewsDao.getItemTypeStats(connection);
            req.setAttribute("itemStats", itemStats);
            
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