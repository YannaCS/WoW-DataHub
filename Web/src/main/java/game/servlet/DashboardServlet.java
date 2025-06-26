package game.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import game.dal.*;
import game.model.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            // Fetch dashboard data
            List<Characters> topPlayers = CharactersDao.getTopPlayersByLevel(cxn, 10);
            int totalCharacters = CharactersDao.getTotalCharacterCount(cxn);
            
            // Set attributes for JSP
            request.setAttribute("topPlayers", topPlayers);
            request.setAttribute("totalCharacters", totalCharacters);
            
            request.getRequestDispatcher("index.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}