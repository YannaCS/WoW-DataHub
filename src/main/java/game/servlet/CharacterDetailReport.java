package game.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import game.dal.*;
import game.model.*;

@WebServlet("/characterdetailreport")
public class CharacterDetailReport extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Map for storing messages
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        // Retrieve and validate charID parameter
        String charIDStr = req.getParameter("charid");
        if (charIDStr == null || charIDStr.trim().isEmpty()) {
            messages.put("success", "Please provide a valid character ID.");
        } else {
            try {
                int charID = Integer.parseInt(charIDStr);
                
                try (Connection connection = ConnectionManager.getConnection()) {
                    // Get character details
                    Characters character = CharactersDao.getCharacterByCharID(connection, charID);
                    if (character == null) {
                        messages.put("success", "No character found with ID " + charID);
                    } else {
                        messages.put("success", "Displaying details for character ID " + charID);
                        req.setAttribute("character", character);
                        
                        // Get character's inventory
                        List<Inventory> inventory = InventoryDao.getInventoryOnlyByCharacters(connection, character);
                        req.setAttribute("inventory", inventory);
                        
                        // Get character's equipped items
                        List<EquippedItems> equippedItems = EquippedItemsDao.getEquippedItemsOnlyByCharacters(connection, character);
                        req.setAttribute("equippedItems", equippedItems);
                        
                        // Get character's wealth
                        List<CharacterWealth> wealth = CharacterWealthDao.getCharacterWealthByCharacter(connection, character);
                        req.setAttribute("wealth", wealth);
                        
                        // Get character's unlocked jobs
                        List<CharacterUnlockedJob> unlockedJobs = CharacterUnlockedJobDao.getCharacterUnlockedJobByCharID(connection, charID);
                        req.setAttribute("unlockedJobs", unlockedJobs);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
            } catch (NumberFormatException e) {
                messages.put("success", "Invalid character ID format.");
            }
        }
        
        req.getRequestDispatcher("/CharacterDetailReport.jsp").forward(req, resp);
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}