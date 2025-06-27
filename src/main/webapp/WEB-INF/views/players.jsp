<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="game.model.Players" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Players - WoW DataHub</title>
    <link href="../css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>Players Management</h1>
        
        <div class="table-responsive">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th>Player ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Email</th>
                    </tr>
                </thead>
                <tbody>
                    <% 
                    @SuppressWarnings("unchecked")
                    List<Players> players = (List<Players>) request.getAttribute("players");
                    if (players != null && !players.isEmpty()) {
                        for (Players player : players) {
                    %>
                    <tr>
                        <td><%= player.getPlayerID() %></td>
                        <td><%= player.getFirstName() %></td>
                        <td><%= player.getLastName() %></td>
                        <td><%= player.getEmailAddress() %></td>
                    </tr>
                    <% 
                        }
                    } else {
                    %>
                    <tr>
                        <td colspan="4" class="text-center">No players found</td>
                    </tr>
                    <% } %>
                </tbody>
            </table>
        </div>
        
        <a href="../" class="btn btn-secondary">← Back to Home</a>
    </div>
</body>
</html>