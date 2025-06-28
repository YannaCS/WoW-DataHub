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

import game.dal.*;
import game.model.*;

@WebServlet("/weaponupdate")
public class WeaponUpdate extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Map for storing messages
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        // Retrieve and validate character ID parameter
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
                        req.setAttribute("character", character);
                        
                        // Get character's weapons from inventory
                        java.util.List<Weapons> weapons = WeaponsDao.getWeaponsByCharacter(connection, charID);
                        req.setAttribute("weapons", weapons);
                        
                        messages.put("success", "Select a weapon to equip for " + character.getFirstName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
            } catch (NumberFormatException e) {
                messages.put("success", "Invalid character ID format.");
            }
        }
        
        req.getRequestDispatcher("/WeaponUpdate.jsp").forward(req, resp);
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Map for storing messages
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        // Retrieve and validate form parameters
        String charIDStr = req.getParameter("charid");
        String weaponIDStr = req.getParameter("weaponid");
        
        if (charIDStr == null || charIDStr.trim().isEmpty() ||
            weaponIDStr == null || weaponIDStr.trim().isEmpty()) {
            messages.put("success", "Please provide valid character and weapon IDs.");
        } else {
            try {
                int charID = Integer.parseInt(charIDStr);
                int weaponID = Integer.parseInt(weaponIDStr);
                
                try (Connection connection = ConnectionManager.getConnection()) {
                    // Get character and weapon
                    Characters character = CharactersDao.getCharacterByCharID(connection, charID);
                    Weapons weapon = WeaponsDao.getWeaponByItemID(connection, weaponID);
                    
                    if (character == null) {
                        messages.put("success", "No character found with ID " + charID);
                    } else if (weapon == null) {
                        messages.put("success", "No weapon found with ID " + weaponID);
                    } else {
                        // Update character's weapon
                        CharactersDao.updateWeaponWeared(connection, character, weapon);
                        
                        // Get updated character for display
                        character = CharactersDao.getCharacterByCharID(connection, charID);
                        req.setAttribute("character", character);
                        
                        messages.put("success", "Successfully updated " + character.getFirstName() + 
                                   "'s weapon to " + weapon.getItemName());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    throw new IOException(e);
                }
            } catch (NumberFormatException e) {
                messages.put("success", "Invalid ID format.");
            }
        }
        
        req.getRequestDispatcher("/WeaponUpdate.jsp").forward(req, resp);
    }
}