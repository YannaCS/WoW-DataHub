<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WoW Data Hub - Find Characters</title>
    <style>
        body { 
            font-family: Arial, sans-serif; 
            margin: 40px; 
            background: #1a1a2e; 
            color: #eee; 
        }
        .container { 
            max-width: 800px; 
            margin: 0 auto; 
            background: #16213e; 
            padding: 30px; 
            border-radius: 10px; 
        }
        h1 { 
            color: #f39c12; 
            text-align: center; 
        }
        .form-group { 
            margin: 20px 0; 
        }
        label { 
            display: block; 
            margin-bottom: 5px; 
            font-weight: bold; 
        }
        input[type="text"] { 
            width: 100%; 
            padding: 10px; 
            border: 1px solid #555; 
            background: #2c3e50; 
            color: #fff; 
            border-radius: 5px; 
        }
        button { 
            background: #e74c3c; 
            color: white; 
            padding: 12px 30px; 
            border: none; 
            border-radius: 5px; 
            cursor: pointer; 
            font-size: 16px; 
        }
        button:hover { 
            background: #c0392b; 
        }
        .results { 
            margin-top: 30px; 
        }
        .player-card { 
            background: #0f3460; 
            padding: 15px; 
            margin: 10px 0; 
            border-radius: 5px; 
            border-left: 4px solid #f39c12; 
        }
        .message { 
            padding: 10px; 
            margin: 10px 0; 
            background: #27ae60; 
            border-radius: 5px; 
        }
        .character-link { 
            color: #3498db; 
            text-decoration: none; 
        }
        .character-link:hover { 
            color: #2980b9; 
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>🏰 WoW Data Hub - Find Characters</h1>
        
        <form action="findcharacter" method="post">
            <div class="form-group">
                <label for="firstname">Player First Name:</label>
                <input type="text" id="firstname" name="firstname" 
                       value="<c:out value='${param.firstname}'/>" 
                       placeholder="Enter first name to search for players">
            </div>
            <button type="submit">🔍 Search Players</button>
        </form>
        
        <div class="results">
            <c:if test="${messages.success != null}">
                <div class="message">
                    <strong><c:out value="${messages.success}"/></strong>
                </div>
            </c:if>
            
            <c:if test="${players != null}">
                <h3>Players Found:</h3>
                <c:forEach items="${players}" var="player">
                    <div class="player-card">
                        <h4>👤 <c:out value="${player.firstName}"/> <c:out value="${player.lastName}"/></h4>
                        <p><strong>Player ID:</strong> ${player.playerID}</p>
                        <p><strong>Email:</strong> <c:out value="${player.emailAddress}"/></p>
                        <p>
                            <a href="characterdetailreport?charid=${player.playerID}" class="character-link">
                                📋 View Character Details
                            </a>
                        </p>
                    </div>
                </c:forEach>
            </c:if>
        </div>
        
        <div style="margin-top: 30px; text-align: center;">
            <a href="characterdetailreport" class="character-link">📊 Character Detail Report</a> |
            <a href="weaponupdate" class="character-link">⚔️ Update Weapon</a>
        </div>
    </div>
</body>
</html>