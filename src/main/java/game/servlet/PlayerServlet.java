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

import game.dal.ConnectionManager;
import game.dal.PlayersDao;
import game.model.Players;

@WebServlet("/players")
public class PlayerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            // Get players by first name (example)
            String firstName = request.getParameter("firstName");
            List<Players> players;
            
            if (firstName != null && !firstName.trim().isEmpty()) {
                players = PlayersDao.getPlayersFromFirstName(cxn, firstName);
            } else {
                // For demo purposes, get players named "Ethan"
                players = PlayersDao.getPlayersFromFirstName(cxn, "Ethan");
            }
            
            request.setAttribute("players", players);
            request.getRequestDispatcher("/WEB-INF/views/players.jsp").forward(request, response);
        
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                              "Database error: " + e.getMessage());
        }
    }
}