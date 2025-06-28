<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WoW Data Hub - Character Details</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #1a1a2e; color: #eee; }
        .container { max-width: 1000px; margin: 0 auto; background: #16213e; padding: 30px; border-radius: 10px; }
        h1 { color: #f39c12; text-align: center; }
        .form-group { margin: 20px 0; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"] { width: 300px; padding: 10px; border: 1px solid #555; background: #2c3e50; color: #fff; border-radius: 5px; }
        button { background: #e74c3c; color: white; padding: 12px 30px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }
        button:hover { background: #c0392b; }
        .character-info { background: #0f3460; padding: 20px; margin: 20px 0; border-radius: 5px; border-left: 4px solid #f39c12; }
        .section { margin: 30px 0; }
        .item-list { background: #2c3e50; padding: 15px; border-radius: 5px; margin: 10px 0; }
        .message { padding: 10px; margin: 10px 0; background: #27ae60; border-radius: 5px; }
        .back-link { color: #3498db; text-decoration: none; }
        .back-link:hover { color: #2980b9; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🏰 WoW Data Hub - Character Details</h1>
        
        <form action="characterdetailreport" method="get">
            <div class="form-group">
                <label for="charid">Character ID:</label>
                <input type="text" id="charid" name="charid" 
                       value="<c:out value='${param.charid}'/>" 
                       placeholder="Enter character ID">
                <button type="submit">📋 Get Character Details</button>
            </div>
        </form>
        
        <c:if test="${messages.success != null}">
            <div class="message">
                <strong><c:out value="${messages.success}"/></strong>
            </div>
        </c:if>
        
        <c:if test="${character != null}">
            <div class="character-info">
                <h2>⚔️ <c:out value="${character.firstName}"/> <c:out value="${character.lastName}"/></h2>
                <p><strong>Character ID:</strong> ${character.charID}</p>
                <p><strong>Player:</strong> <c:out value="${character.players.firstName}"/> <c:out value="${character.players.lastName}"/></p>
                <p><strong>Clan:</strong> <c:out value="${character.clan.clanName}"/> (<c:out value="${character.clan.race}"/>)</p>
                <p><strong>Current Weapon:</strong> <c:out value="${character.weaponWeared.itemName}"/> (Damage: ${character.weaponWeared.damage})</p>
            </div>
            
            <div class="section">
                <h3>🎒 Inventory</h3>
                <c:choose>
                    <c:when test="${inventory != null && not empty inventory}">
                        <c:forEach items="${inventory}" var="item">
                            <div class="item-list">
                                <strong>Slot ${item.slotID}:</strong> Item ID ${item.instance} (Quantity: ${item.quantity})
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>No items in inventory.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="section">
                <h3>🛡️ Equipped Items</h3>
                <c:choose>
                    <c:when test="${equippedItems != null && not empty equippedItems}">
                        <c:forEach items="${equippedItems}" var="equipped">
                            <div class="item-list">
                                <strong><c:out value="${equipped.equipPosition}"/>:</strong> Item ID ${equipped.itemID}
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>No equipped items.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="section">
                <h3>💰 Wealth</h3>
                <c:choose>
                    <c:when test="${wealth != null && not empty wealth}">
                        <c:forEach items="${wealth}" var="currency">
                            <div class="item-list">
                                <strong><c:out value="${currency.currency.currencyName}"/>:</strong> ${currency.amount}
                                <c:if test="${currency.weeklyAcquired != null}">
                                    (Weekly: ${currency.weeklyAcquired})
                                </c:if>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>No currency information available.</p>
                    </c:otherwise>
                </c:choose>
            </div>
            
            <div class="section">
                <h3>🎯 Unlocked Jobs</h3>
                <c:choose>
                    <c:when test="${unlockedJobs != null && not empty unlockedJobs}">
                        <c:forEach items="${unlockedJobs}" var="job">
                            <div class="item-list">
                                <strong><c:out value="${job.job}"/>:</strong> 
                                <c:choose>
                                    <c:when test="${job.jobLevel != null}">
                                        Level ${job.jobLevel} (XP: ${job.xP})
                                    </c:when>
                                    <c:otherwise>
                                        Unlocked (No level data)
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p>No unlocked jobs.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
        
        <div style="margin-top: 30px; text-align: center;">
            <a href="findcharacter" class="back-link">🔍 Find Characters</a> |
            <a href="weaponupdate?charid=${character.charID}" class="back-link">⚔️ Update Weapon</a>
        </div>
    </div>
</body>
</html>