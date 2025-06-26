<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WoW DataHub - Analytics Dashboard</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="assets/css/dashboard.css" rel="stylesheet">
</head>
<body>
    <header class="header">
        <nav class="nav-container">
            <div class="logo">
                <i class="fas fa-dragon"></i> WoW DataHub
            </div>
            <div class="nav-links">
                <a href="#" class="nav-link active">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
                <a href="players.jsp" class="nav-link">
                    <i class="fas fa-users"></i> Players
                </a>
                <a href="guilds.jsp" class="nav-link">
                    <i class="fas fa-shield-alt"></i> Guilds
                </a>
                <a href="items.jsp" class="nav-link">
                    <i class="fas fa-sword"></i> Items
                </a>
                <a href="realms.jsp" class="nav-link">
                    <i class="fas fa-globe"></i> Realms
                </a>
            </div>
        </nav>
    </header>

    <main class="main-container">
        <section class="hero-section">
            <h1 class="hero-title">World of Warcraft Data Analytics</h1>
            <p class="hero-subtitle">Real-time insights from Azeroth's greatest adventures</p>
        </section>

        <!-- Stats cards with real data -->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-users"></i></div>
                <div class="stat-title">Active Characters</div>
                <div class="stat-value">${totalCharacters}</div>
                <span class="stat-change stat-positive">
                    <i class="fas fa-arrow-up"></i> +${characterGrowth}%
                </span>
            </div>
            <!-- More stat cards... -->
        </section>

        <!-- Interactive charts -->
        <section class="dashboard-grid">
            <div class="chart-container">
                <h3 class="chart-title">
                    <i class="fas fa-chart-area"></i> Player Activity Trends
                </h3>
                <canvas id="activityChart"></canvas>
            </div>
            
            <div class="leaderboard">
                <h3 class="leaderboard-title">
                    <i class="fas fa-trophy"></i> Top Players by Level
                </h3>
                <c:forEach var="player" items="${topPlayers}" varStatus="status">
                    <div class="player-entry">
                        <span class="player-rank">#${status.index + 1}</span>
                        <span class="player-name">${player.firstName} ${player.lastName}</span>
                        <span class="player-level">Level ${player.level}</span>
                        <span class="player-class">${player.weaponWeared.wearableJob}</span>
                    </div>
                </c:forEach>
            </div>
        </section>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="assets/js/dashboard.js"></script>
</body>
</html>