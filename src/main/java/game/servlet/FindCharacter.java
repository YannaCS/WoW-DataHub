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

import game.dal.*;
import game.model.*;

@WebServlet("/findcharacter")
public class FindCharacter extends HttpServlet {
    
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Map for storing messages
        Map<String, String> messages = new HashMap<>();
        req.setAttribute("messages", messages);
        
        // Retrieve and validate name parameter
        String firstName = req.getParameter("firstname");
        if (firstName == null || firstName.trim().isEmpty()) {
            messages.put("success", "Please enter a first name.");
        } else {
            // Retrieve Characters, and store as a message
            try (Connection connection = ConnectionManager.getConnection()) {
                List<Players> players = PlayersDao.getPlayersFromFirstName(connection, firstName);
                if (players.isEmpty()) {
                    messages.put("success", "No players found with first name " + firstName);
                } else {
                    messages.put("success", "Displaying results for " + firstName);
                    // Save the previous search term, so it can be displayed in the form
                    req.setAttribute("previousFirstName", firstName);
                }
                req.setAttribute("players", players);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new IOException(e);
            }
        }
        
        req.getRequestDispatcher("/FindCharacter.jsp").forward(req, resp);
    }
    
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        doGet(req, resp);
    }
}