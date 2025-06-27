package game.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import game.Driver;

@WebServlet("/database/init")
public class DatabaseInitServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<html><head><title>Database Initialization</title></head><body>");
        out.println("<h1>Database Initialization</h1>");
        
        try {
            // Run your existing Driver logic
            Driver.resetSchema();
            Driver.insertRecords();
            
            out.println("<div style='color: green;'>");
            out.println("<h2>✅ Success!</h2>");
            out.println("<p>Database has been successfully initialized with test data.</p>");
            out.println("</div>");
            
        } catch (SQLException e) {
            out.println("<div style='color: red;'>");
            out.println("<h2>❌ Error!</h2>");
            out.println("<p>Failed to initialize database: " + e.getMessage() + "</p>");
            out.println("</div>");
            e.printStackTrace();
        }
        
        out.println("<a href='../'>← Back to Home</a>");
        out.println("</body></html>");
    }
}