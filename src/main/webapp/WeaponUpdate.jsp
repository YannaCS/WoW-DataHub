<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>WoW Data Hub - Update Weapon</title>
<style>
    body {
        font-family: 'Segoe UI', 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', Arial, sans-serif;
        max-width: 1200px;
        margin: 0 auto;
        padding: 20px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        color: white;
    }
    
    .container {
        background-color: rgba(255, 255, 255, 0.95);
        color: #2c3e50;
        padding: 40px;
        border-radius: 15px;
        box-shadow: 0 10px 30px rgba(0,0,0,0.3);
        backdrop-filter: blur(10px);
    }
    
    .header {
        text-align: center;
        margin-bottom: 40px;
    }
    
    .header h1 {
        color: #2c3e50;
        font-size: 2.5em;
        margin-bottom: 10px;
        text-shadow: 2px 2px 4px rgba(0,0,0,0.1);
    }
    
    .header .subtitle {
        color: #7f8c8d;
        font-size: 1.2em;
        font-style: italic;
    }
    
    .form-section {
        background-color: #f8f9fa;
        padding: 30px;
        border-radius: 10px;
        margin: 30px 0;
        border-left: 5px solid #3498db;
    }
    
    .form-group {
        margin-bottom: 25px;
    }
    
    .form-group label {
        display: block;
        margin-bottom: 8px;
        font-weight: bold;
        color: #2c3e50;
        font-size: 1.1em;
    }
    
    .form-group input, .form-group select {
        width: 100%;
        padding: 15px;
        border: 2px solid #bdc3c7;
        border-radius: 8px;
        font-size: 16px;
        transition: all 0.3s ease;
        background-color: white;
    }
    
    .form-group input:focus, .form-group select:focus {
        outline: none;
        border-color: #3498db;
        box-shadow: 0 0 10px rgba(52, 152, 219, 0.3);
        transform: translateY(-2px);
    }
    
    .btn {
        background: linear-gradient(45deg, #3498db, #2980b9);
        color: white;
        padding: 15px 30px;
        border: none;
        border-radius: 8px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        transition: all 0.3s ease;
        text-transform: uppercase;
        letter-spacing: 1px;
        box-shadow: 0 4px 15px rgba(52, 152, 219, 0.4);
    }
    
    .btn:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(52, 152, 219, 0.6);
        background: linear-gradient(45deg, #2980b9, #3498db);
    }
    
    .btn:active {
        transform: translateY(-1px);
    }
    
    .btn-secondary {
        background: linear-gradient(45deg, #95a5a6, #7f8c8d);
        box-shadow: 0 4px 15px rgba(149, 165, 166, 0.4);
    }
    
    .btn-secondary:hover {
        background: linear-gradient(45deg, #7f8c8d, #95a5a6);
        box-shadow: 0 6px 20px rgba(149, 165, 166, 0.6);
    }
    
    .message {
        padding: 20px;
        margin: 25px 0;
        border-radius: 10px;
        font-weight: bold;
        border-left: 5px solid;
        backdrop-filter: blur(5px);
    }
    
    .message.success {
        background: linear-gradient(45deg, rgba(46, 204, 113, 0.1), rgba(39, 174, 96, 0.1));
        color: #27ae60;
        border-color: #27ae60;
    }
    
    .message.error {
        background: linear-gradient(45deg, rgba(231, 76, 60, 0.1), rgba(192, 57, 43, 0.1));
        color: #e74c3c;
        border-color: #e74c3c;
    }
    
    .message.warning {
        background: linear-gradient(45deg, rgba(241, 196, 15, 0.1), rgba(230, 126, 34, 0.1));
        color: #f39c12;
        border-color: #f39c12;
    }
    
    .character-info {
        background: linear-gradient(45deg, #667eea, #764ba2);
        color: white;
        padding: 25px;
        border-radius: 10px;
        margin: 25px 0;
        box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
    }
    
    .character-info h3 {
        margin-top: 0;
        font-size: 1.5em;
        text-shadow: 1px 1px 3px rgba(0,0,0,0.3);
    }
    
    .character-info .detail {
        margin: 10px 0;
        font-size: 1.1em;
    }
    
    .weapon-list {
        background-color: #ecf0f1;
        padding: 20px;
        border-radius: 10px;
        margin: 20px 0;
    }
    
    .weapon-item {
        background-color: white;
        padding: 15px;
        margin: 10px 0;
        border-radius: 8px;
        border-left: 4px solid #e74c3c;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        transition: all 0.3s ease;
    }
    
    .weapon-item:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    }
    
    .weapon-item .weapon-name {
        font-weight: bold;
        color: #2c3e50;
        font-size: 1.1em;
    }
    
    .weapon-item .weapon-stats {
        color: #7f8c8d;
        margin-top: 5px;
    }
    
    .navigation {
        text-align: center;
        margin-top: 40px;
        padding-top: 30px;
        border-top: 2px solid #ecf0f1;
    }
    
    .nav-link {
        display: inline-block;
        margin: 10px 15px;
        padding: 12px 25px;
        background: linear-gradient(45deg, #34495e, #2c3e50);
        color: white;
        text-decoration: none;
        border-radius: 8px;
        transition: all 0.3s ease;
        font-weight: bold;
        box-shadow: 0 4px 15px rgba(52, 73, 94, 0.4);
    }
    
    .nav-link:hover {
        transform: translateY(-3px);
        box-shadow: 0 6px 20px rgba(52, 73, 94, 0.6);
        background: linear-gradient(45deg, #2c3e50, #34495e);
    }
    
    .empty-state {
        text-align: center;
        padding: 40px;
        color: #7f8c8d;
        font-style: italic;
    }
    
    .empty-state .icon {
        font-size: 3em;
        margin-bottom: 20px;
        opacity: 0.5;
    }
    
    @keyframes slideIn {
        from {
            opacity: 0;
            transform: translateY(30px);
        }
        to {
            opacity: 1;
            transform: translateY(0);
        }
    }
    
    .container {
        animation: slideIn 0.6s ease-out;
    }
    
    .responsive-table {
        overflow-x: auto;
        margin: 20px 0;
    }
    
    @media (max-width: 768px) {
        body {
            padding: 10px;
        }
        
        .container {
            padding: 20px;
        }
        
        .header h1 {
            font-size: 2em;
        }
        
        .nav-link {
            display: block;
            margin: 10px 0;
        }
    }
</style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>&#9876;&#65039; Update Character Weapon</h1>
            <div class="subtitle">Equip your character with powerful weapons</div>
        </div>
        
        <!-- Display Messages -->
        <c:if test="${messages.success != null}">
            <div class="message success">
                &#9989; ${messages.success}
            </div>
        </c:if>
        
        <c:if test="${messages.error != null}">
            <div class="message error">
                &#10060; ${messages.error}
            </div>
        </c:if>
        
        <c:if test="${messages.warning != null}">
            <div class="message warning">
                &#9888; ${messages.warning}
            </div>
        </c:if>
        
        <!-- Character Selection Form -->
        <c:if test="${character == null}">
            <div class="form-section">
                <h2>&#128100; Select Character</h2>
                <p>Enter a character ID to view and update their equipped weapon.</p>
                
                <form method="get">
                    <div class="form-group">
                        <label for="charid">Character ID:</label>
                        <input type="number" 
                               id="charid" 
                               name="charid" 
                               placeholder="Enter character ID (e.g., 1, 2, 3...)" 
                               min="1" 
                               required>
                    </div>
                    <button type="submit" class="btn">
                        &#128269; Find Character
                    </button>
                </form>
            </div>
        </c:if>
        
        <!-- Character Information and Weapon Update -->
        <c:if test="${character != null}">
            <div class="character-info">
                <h3>&#127881; Character Information</h3>
                <div class="detail"><strong>Character ID:</strong> ${character.charID}</div>
                <div class="detail"><strong>Name:</strong> ${character.firstName} ${character.lastName}</div>
                <div class="detail"><strong>Player:</strong> ${character.players.firstName} ${character.players.lastName}</div>
                <div class="detail"><strong>Clan:</strong> ${character.clan.clanName} (${character.clan.race})</div>
                <div class="detail"><strong>Current Weapon:</strong> 
                    <c:choose>
                        <c:when test="${character.weaponWeared != null}">
                            ${character.weaponWeared.itemName} (Damage: ${character.weaponWeared.damage})
                        </c:when>
                        <c:otherwise>
                            <span style="color: #f39c12;">No weapon equipped</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Weapon Selection Form -->
            <div class="form-section">
                <h2>&#9876; Select New Weapon</h2>
                
                <c:choose>
                    <c:when test="${weapons != null && !empty weapons}">
                        <p>Choose a weapon from ${character.firstName}'s inventory:</p>
                        
                        <form method="post">
                            <input type="hidden" name="charid" value="${character.charID}">
                            
                            <div class="form-group">
                                <label for="weaponid">Available Weapons:</label>
                                <select id="weaponid" name="weaponid" required>
                                    <option value="">-- Select a weapon --</option>
                                    <c:forEach var="weapon" items="${weapons}">
                                        <option value="${weapon.itemID}">
                                            ${weapon.itemName} - Level ${weapon.level} - Damage: ${weapon.damage} - ${weapon.wearableJob}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                            
                            <button type="submit" class="btn">
                                &#9876; Equip Weapon
                            </button>
                            
                            <a href="weaponupdate" class="btn btn-secondary" style="margin-left: 15px;">
                                &#8592; Select Different Character
                            </a>
                        </form>
                        
                        <!-- Weapon Details -->
                        <div class="weapon-list">
                            <h3>&#128202; Weapon Details</h3>
                            <c:forEach var="weapon" items="${weapons}">
                                <div class="weapon-item">
                                    <div class="weapon-name">${weapon.itemName}</div>
                                    <div class="weapon-stats">
                                        Level: ${weapon.level} | 
                                        Damage: ${weapon.damage} | 
                                        Class: ${weapon.wearableJob} | 
                                        Required Level: ${weapon.requiredLevel} |
                                        Price: $${weapon.price}
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="icon">&#128542;</div>
                            <h3>No Weapons Available</h3>
                            <p>This character doesn't have any weapons in their inventory.</p>
                            <p>Try running the ETL process to add more items to the database.</p>
                            
                            <a href="etl" class="btn" style="margin-top: 20px;">
                                &#128640; Run ETL Process
                            </a>
                        </div>
                        
                        <a href="weaponupdate" class="btn btn-secondary">
                            &#8592; Select Different Character
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
        
        <!-- Navigation -->
        <div class="navigation">
            <h3>Navigate to Other Features</h3>
            <a href="findcharacter" class="nav-link">&#128269; Find Characters</a>
            <a href="characterdetailreport" class="nav-link">&#128203; Character Details</a>
            <a href="etl" class="nav-link">&#128640; Run ETL Process</a>
        </div>
    </div>
</body>
</html>