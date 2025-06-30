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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

import game.dal.ConnectionManager;
import game.etl.WoWDataETL;

@WebServlet("/etl")
public class ETLController extends HttpServlet {
    
    // Track if ETL is currently running to prevent multiple simultaneous runs
    private static final AtomicBoolean isETLRunning = new AtomicBoolean(false);
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        // Check if ETL is currently running
        if (isETLRunning.get()) {
            messages.put("info", "Dynamic ETL process is currently running. Please wait...");
        } else {
            messages.put("success", "Ready to run Dynamic ETL process to add more players and characters to the database.");
        }
        
        // Get current record counts for display
        try (Connection connection = ConnectionManager.getConnection()) {
            Map<String, Integer> recordCounts = getCurrentRecordCounts(connection);
            req.setAttribute("recordCounts", recordCounts);
        } catch (SQLException e) {
            messages.put("error", "Failed to get current record counts: " + e.getMessage());
        }
        
        req.getRequestDispatcher("/ETL.jsp").forward(req, resp);
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        String action = req.getParameter("action");
        
        if ("runETL".equals(action)) {
            // Check if ETL is already running
            if (isETLRunning.compareAndSet(false, true)) {
                try {
                    // Run Dynamic ETL process asynchronously to avoid timeout
                    CompletableFuture.runAsync(() -> {
                        try {
                            WoWDataETL etl = new WoWDataETL();
                            etl.runETL();
                            System.out.println("Dynamic ETL process completed successfully from web interface");
                        } catch (Exception e) {
                            System.err.println("Dynamic ETL process failed: " + e.getMessage());
                            e.printStackTrace();
                        } finally {
                            isETLRunning.set(false);
                        }
                    });
                    
                    messages.put("success", "Dynamic ETL process started successfully! New players and characters are being added to the database. This may take a few minutes...");
                    
                } catch (Exception e) {
                    isETLRunning.set(false);
                    messages.put("error", "Failed to start Dynamic ETL process: " + e.getMessage());
                }
            } else {
                messages.put("warning", "Dynamic ETL process is already running. Please wait for it to complete.");
            }
        }
        
        // Get updated record counts
        try (Connection connection = ConnectionManager.getConnection()) {
            Map<String, Integer> recordCounts = getCurrentRecordCounts(connection);
            req.setAttribute("recordCounts", recordCounts);
        } catch (SQLException e) {
            messages.put("error", "Failed to get current record counts: " + e.getMessage());
        }
        
        req.getRequestDispatcher("/ETL.jsp").forward(req, resp);
    }
    
    /**
     * Get current record counts for all major tables
     */
    private Map<String, Integer> getCurrentRecordCounts(Connection connection) {
        Map<String, Integer> counts = new HashMap<>();
        String[] tables = {"Players", "Clans", "Characters", "Weapons", "Gears", 
                          "Consumables", "Statistics", "Currencies", "Inventory", 
                          "EquippedItems", "CharacterWealth"};
        
        for (String table : tables) {
            try {
                var stmt = connection.createStatement();
                var rs = stmt.executeQuery("SELECT COUNT(*) FROM " + table);
                if (rs.next()) {
                    counts.put(table, rs.getInt(1));
                }
                rs.close();
                stmt.close();
            } catch (SQLException e) {
                counts.put(table, -1); // Indicate error
            }
        }
        
        return counts;
    }
}