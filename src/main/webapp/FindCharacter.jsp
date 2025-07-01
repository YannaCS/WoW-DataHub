<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>WoW DataHub - Find Characters</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600;700&family=Roboto:wght@300;400;500;700&display=swap');
        
        :root {
            --wow-gold: #f4d03f;
            --wow-blue: #1e3c72;
            --wow-dark-blue: #0f1d36;
            --wow-silver: #c0c0c0;
            --wow-bg: #0a0e1a;
            --wow-card-bg: rgba(20, 30, 48, 0.95);
            --wow-border: #2c4875;
        }
        
        body {
            background: linear-gradient(135deg, var(--wow-bg) 0%, var(--wow-dark-blue) 50%, var(--wow-blue) 100%);
            background-attachment: fixed;
            min-height: 100vh;
            font-family: 'Roboto', sans-serif;
            color: #ffffff;
        }
        
        .navbar {
            background: var(--wow-card-bg) !important;
            border-bottom: 2px solid var(--wow-border);
            backdrop-filter: blur(10px);
        }
        
        .navbar-brand {
            font-family: 'Cinzel', serif;
            color: var(--wow-gold) !important;
            font-weight: 700;
        }
        
        .search-container {
            background: var(--wow-card-bg);
            border: 2px solid var(--wow-border);
            border-radius: 15px;
            padding: 2rem;
            margin: 2rem auto;
            max-width: 800px;
            backdrop-filter: blur(10px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
        }
        
        .form-label {
            color: var(--wow-gold);
            font-weight: 600;
            margin-bottom: 0.5rem;
        }
        
        .form-control, .form-select {
            background: rgba(30, 60, 114, 0.3);
            border: 1px solid var(--wow-border);
            color: #ffffff;
            padding: 0.75rem;
        }
        
        .form-control:focus, .form-select:focus {
            background: rgba(30, 60, 114, 0.5);
            border-color: var(--wow-gold);
            color: #ffffff;
            box-shadow: 0 0 0 0.2rem rgba(244, 208, 63, 0.25);
        }
        
        .form-control::placeholder {
            color: var(--wow-silver);
        }
        
        .btn-search {
            background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%);
            color: var(--wow-dark-blue);
            border: none;
            padding: 0.75rem 2rem;
            font-weight: 600;
            border-radius: 8px;
            transition: all 0.3s ease;
        }
        
        .btn-search:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(244, 208, 63, 0.3);
        }
        
        .btn-show-all {
            background: linear-gradient(135deg, var(--wow-blue) 0%, var(--wow-dark-blue) 100%);
            color: var(--wow-gold);
            border: 1px solid var(--wow-border);
            padding: 0.75rem 2rem;
            font-weight: 600;
            border-radius: 8px;
            transition: all 0.3s ease;
        }
        
        .btn-show-all:hover {
            background: linear-gradient(135deg, #2e5090 0%, #1a2f5a 100%);
            border-color: var(--wow-gold);
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(30, 60, 114, 0.3);
        }
        
        .results-container {
            background: var(--wow-card-bg);
            border: 2px solid var(--wow-border);
            border-radius: 15px;
            padding: 2rem;
            margin: 2rem auto;
            backdrop-filter: blur(10px);
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
        }
        
        .results-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1.5rem;
            padding-bottom: 1rem;
            border-bottom: 2px solid var(--wow-border);
        }
        
        .results-title {
            font-family: 'Cinzel', serif;
            color: var(--wow-gold);
            font-size: 1.5rem;
            margin: 0;
        }
        
        .table {
            color: #ffffff;
        }
        
        .table thead th {
            background: rgba(30, 60, 114, 0.5);
            color: var(--wow-gold);
            border-color: var(--wow-border);
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 1px;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .table tbody tr {
            transition: all 0.3s ease;
        }
        
        .table tbody tr:hover {
            background: rgba(44, 72, 117, 0.3);
            transform: translateX(5px);
        }
        
        .table tbody td {
            border-color: rgba(244, 208, 63, 0.1);
            vertical-align: middle;
        }
        
        .sort-link {
            color: var(--wow-gold);
            text-decoration: none;
            cursor: pointer;
            transition: color 0.3s ease;
        }
        
        .sort-link:hover {
            color: #ffffff;
        }
        
        .sort-icon {
            font-size: 0.8rem;
            margin-left: 0.25rem;
        }
        
        .btn-action {
            background: linear-gradient(135deg, var(--wow-blue) 0%, var(--wow-dark-blue) 100%);
            color: var(--wow-gold);
            border: 1px solid var(--wow-border);
            padding: 0.5rem 1rem;
            font-size: 0.875rem;
            border-radius: 6px;
            transition: all 0.3s ease;
        }
        
        .btn-action:hover {
            background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%);
            color: var(--wow-dark-blue);
            border-color: var(--wow-gold);
            transform: translateY(-2px);
        }
        
        .empty-state {
            text-align: center;
            padding: 3rem;
            color: var(--wow-silver);
        }
        
        .empty-state i {
            font-size: 4rem;
            color: var(--wow-gold);
            margin-bottom: 1rem;
        }
        
        .alert {
            border-radius: 10px;
            border: 1px solid var(--wow-border);
            background: var(--wow-card-bg);
            color: #ffffff;
        }
        
        .alert-info {
            border-color: var(--wow-gold);
            background: rgba(244, 208, 63, 0.1);
        }
    </style>
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark">
        <div class="container">
            <a class="navbar-brand" href="home">
                <i class="bi bi-shield-shaded"></i> WoW DataHub
            </a>
            <div class="navbar-nav ms-auto">
                <a class="nav-link" href="home">Dashboard</a>
                <a class="nav-link active" href="findcharacter">Find Characters</a>
                <a class="nav-link" href="etl">ETL Management</a>
            </div>
        </div>
    </nav>

    <div class="container">
        <!-- Search Form -->
        <div class="search-container">
            <h2 class="text-center mb-4" style="font-family: 'Cinzel', serif; color: var(--wow-gold);">
                <i class="bi bi-search"></i> Find Characters by Player
            </h2>
            
            <form action="findcharacter" method="post">
                <div class="row g-3">
                    <div class="col-md-12">
                        <label for="playerlastname" class="form-label">Player's Last Name</label>
                        <input type="text" 
                               class="form-control" 
                               id="playerlastname" 
                               name="playerlastname" 
                               placeholder="Enter player's last name (partial match supported)"
                               value="${param.playerlastname}">
                        <p class="small" style="color: #B8C5D6;">Search supports partial matches (e.g., "Storm" will find "Stormwind")</p>
                    </div>
                    
                    <div class="col-md-6">
                        <label for="sortBy" class="form-label">Sort By</label>
                        <select class="form-select" id="sortBy" name="sortBy">
                            <option value="playerLastName" ${param.sortBy == 'playerLastName' || empty param.sortBy ? 'selected' : ''}>Player Last Name</option>
                            <option value="playerFirstName" ${param.sortBy == 'playerFirstName' ? 'selected' : ''}>Player First Name</option>
                            <option value="characterLastName" ${param.sortBy == 'characterLastName' ? 'selected' : ''}>Character Last Name</option>
                            <option value="characterFirstName" ${param.sortBy == 'characterFirstName' ? 'selected' : ''}>Character First Name</option>
                            <option value="clan" ${param.sortBy == 'clan' ? 'selected' : ''}>Clan</option>
                            <option value="job" ${param.sortBy == 'job' ? 'selected' : ''}>Job</option>
                        </select>
                    </div>
                    
                    <div class="col-md-6">
                        <label for="sortOrder" class="form-label">Sort Order</label>
                        <select class="form-select" id="sortOrder" name="sortOrder">
                            <option value="ascending" ${param.sortOrder == 'ascending' || empty param.sortOrder ? 'selected' : ''}>Ascending</option>
                            <option value="descending" ${param.sortOrder == 'descending' ? 'selected' : ''}>Descending</option>
                        </select>
                    </div>
                    
                    <div class="col-12 text-center mt-4">
                        <button type="submit" class="btn btn-search">
                            <i class="bi bi-search"></i> Search Characters
                        </button>
                        <a href="findcharacter" class="btn btn-show-all ms-2">
                            <i class="bi bi-list-ul"></i> Show All Characters (only for test)
                        </a>
                    </div>
                </div>
            </form>
        </div>

        <!-- Messages -->
        <c:if test="${not empty messages}">
            <div class="mt-3">
                <c:forEach items="${messages}" var="message">
                    <div class="alert alert-info alert-dismissible fade show">
                        <i class="bi bi-info-circle"></i> ${message.value}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <!-- Results Table -->
        <c:if test="${not empty characters}">
            <div class="results-container">
                <div class="results-header">
                    <h3 class="results-title">
                        <i class="bi bi-person-lines-fill"></i> Characters Found: ${characters.size()}
                    </h3>
                </div>
                
                <div class="table-responsive">
                    <table class="table table-dark table-hover">
                        <thead>
                            <tr>
                                <th>Character Name</th>
                                <th>Player Name</th>
                                <th>Clan</th>
                                <th>Race</th>
                                <th>Current Job</th>
                                <th>Weapon</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${characters}" var="character">
                                <tr>
                                    <td>
                                        <strong>${character.firstName} ${character.lastName}</strong>
                                        <br>
                                        <p class="small" style="color: #B8C5D6;">ID: ${character.charID}</p>>
                                    </td>
                                    <td>
                                        ${character.players.firstName} ${character.players.lastName}
                                        <br>
                                        <small class="text-muted">${character.players.emailAddress}</small>
                                    </td>
                                    <td>${character.clan.clanName}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${character.clan.race == 'HUMAN'}">
                                                <span style="color: #FFD700;">Human</span>
                                            </c:when>
                                            <c:when test="${character.clan.race == 'ELF'}">
                                                <span style="color: #87CEEB;">Elf</span>
                                            </c:when>
                                            <c:when test="${character.clan.race == 'DWARF'}">
                                                <span style="color: #CD853F;">Dwarf</span>
                                            </c:when>
                                            <c:when test="${character.clan.race == 'ORC'}">
                                                <span style="color: #DC143C;">Orc</span>
                                            </c:when>
                                            <c:when test="${character.clan.race == 'GOBLIN'}">
                                                <span style="color: #32CD32;">Goblin</span>
                                            </c:when>
                                            <c:otherwise>${character.clan.race}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${character.weaponWeared.wearableJob}</td>
                                    <td>
                                        ${character.weaponWeared.itemName}
                                        <br>
                                        <p class="small" style="color: #B8C5D6;">Damage: ${character.weaponWeared.damage}</p>
                                    </td>
                                    <td>
                                        <a href="characterdetailreport?charid=${character.charID}" class="btn btn-action btn-sm">
                                            <i class="bi bi-eye"></i> View Details
                                        </a>
                                        <a href="weaponupdate?charid=${character.charID}" class="btn btn-action btn-sm mt-1">
                                            <i class="bi bi-sword"></i> Change Weapon
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:if>
        
        <!-- Empty State -->
        <c:if test="${empty characters}">
            <div class="results-container">
                <div class="empty-state">
                    <i class="bi bi-person-x"></i>
                    <h4>No Characters Found</h4>
                    <p>Try searching with a different player last name or show all characters.</p>
                </div>
            </div>
        </c:if>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>