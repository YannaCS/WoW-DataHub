<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
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
                <a href="dashboard" class="nav-link active">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
                <a href="players" class="nav-link">
                    <i class="fas fa-users"></i> Players
                </a>
                <a href="characters" class="nav-link">
                    <i class="fas fa-user-shield"></i> Characters
                </a>
                <a href="items" class="nav-link">
                    <i class="fas fa-sword"></i> Items
                </a>
                <a href="etl" class="nav-link">
                    <i class="fas fa-sync-alt"></i> ETL
                </a>
            </div>
        </nav>
    </header>

    <main class="main-container">
        <section class="hero-section">
            <h1 class="hero-title">World of Warcraft Data Analytics</h1>
            <p class="hero-subtitle">Real-time insights from Azeroth's greatest adventures</p>
        </section>

        <!-- Filter Bar -->
        <section class="filter-bar">
            <div class="filter-row">
                <div class="filter-group">
                    <label class="filter-label">Realm</label>
                    <select class="filter-select" id="realmFilter">
                        <option value="">All Realms</option>
                        <c:forEach var="realm" items="${realms}">
                            <option value="${realm.realmId}">${realm.realmName}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filter-group">
                    <label class="filter-label">Time Period</label>
                    <select class="filter-select" id="timeFilter">
                        <option value="7">Last 7 Days</option>
                        <option value="30">Last 30 Days</option>
                        <option value="90">Last 3 Months</option>
                        <option value="365">Last Year</option>
                    </select>
                </div>
                <div class="filter-group">
                    <label class="filter-label">Character Class</label>
                    <select class="filter-select" id="classFilter">
                        <option value="">All Classes</option>
                        <c:forEach var="entry" items="${classDistribution}">
                            <option value="${entry.key}">${entry.key}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="filter-group">
                    <label class="filter-label">Level Range</label>
                    <input type="text" class="filter-input" id="levelRange" placeholder="e.g., 1-100">
                </div>
                <button class="filter-button" onclick="applyFilters()">
                    <i class="fas fa-filter"></i> Apply Filters
                </button>
            </div>
        </section>

        <!-- Stats Cards -->
        <section class="stats-grid">
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-users"></i></div>
                <div class="stat-title">Total Characters</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${totalCharacters}" type="number"/>
                </div>
                <span class="stat-change stat-positive">
                    <i class="fas fa-arrow-up"></i> +${characterGrowth}%
                </span>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-level-up-alt"></i></div>
                <div class="stat-title">Average Level</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${averageLevel}" maxFractionDigits="1"/>
                </div>
                <span class="stat-change stat-positive">
                    <i class="fas fa-arrow-up"></i> +2.3%
                </span>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-shield-alt"></i></div>
                <div class="stat-title">Active Guilds</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${totalGuilds}" type="number"/>
                </div>
                <span class="stat-change stat-neutral">
                    <i class="fas fa-minus"></i> 0.0%
                </span>
            </div>
            
            <div class="stat-card">
                <div class="stat-icon"><i class="fas fa-coins"></i></div>
                <div class="stat-title">Average Wealth</div>
                <div class="stat-value">
                    <fmt:formatNumber value="${averageWealth}" type="currency"/>
                </div>
                <span class="stat-change stat-positive">
                    <i class="fas fa-arrow-up"></i> +8.7%
                </span>
            </div>
        </section>

        <!-- Dashboard Grid -->
        <section class="dashboard-grid">
            <!-- Activity Chart -->
            <div class="chart-container">
                <h3 class="chart-title">
                    <i class="fas fa-chart-area"></i> Player Activity Trends
                </h3>
                <canvas id="activityChart" height="300"></canvas>
            </div>
            
            <!-- Top Players Leaderboard -->
            <div class="leaderboard">
                <h3 class="leaderboard-title">
                    <i class="fas fa-trophy"></i> Top Players by Level
                </h3>
                <c:forEach var="character" items="${topPlayers}" varStatus="status">
                    <div class="player-entry">
                        <span class="player-rank">#${status.index + 1}</span>
                        <div class="player-info">
                            <span class="player-name">${character.firstName} ${character.lastName}</span>
                            <small class="player-realm">${character.players.firstName} ${character.players.lastName}</small>
                        </div>
                        <span class="player-level">Lv. ${character.weaponWeared.level}</span>
                        <span class="player-class">${character.weaponWeared.wearableJob}</span>
                    </div>
                </c:forEach>
            </div>
        </section>

        <!-- Second Row Charts -->
        <section class="dashboard-grid">
            <!-- Class Distribution -->
            <div class="chart-container">
                <h3 class="chart-title">
                    <i class="fas fa-chart-pie"></i> Class Distribution
                </h3>
                <canvas id="classChart" height="300"></canvas>
            </div>
            
            <!-- Level Distribution -->
            <div class="chart-container">
                <h3 class="chart-title">
                    <i class="fas fa-chart-bar"></i> Level Distribution
                </h3>
                <canvas id="levelChart" height="300"></canvas>
            </div>
        </section>

        <!-- Popular Items Section -->
        <section class="dashboard-grid">
            <div class="chart-container">
                <h3 class="chart-title">
                    <i class="fas fa-star"></i> Most Popular Items
                </h3>
                <div class="items-list">
                    <c:forEach var="item" items="${popularItems}" varStatus="status">
                        <div class="item-entry">
                            <span class="item-rank">#${status.index + 1}</span>
                            <span class="item-name">${item.itemName}</span>
                            <span class="item-level">Lv. ${item.level}</span>
                            <span class="item-price">
                                <fmt:formatNumber value="${item.price}" type="currency"/>
                            </span>
                        </div>
                    </c:forEach>
                </div>
            </div>
            
            <!-- Recent Activity -->
            <div class="chart-container">
                <h3 class="chart-title">
                    <i class="fas fa-clock"></i> Recent Activity
                </h3>
                <div class="activity-feed">
                    <div class="activity-item">
                        <i class="fas fa-user-plus activity-icon"></i>
                        <span>New character created: <strong>Shadowbane</strong></span>
                        <small>2 minutes ago</small>
                    </div>
                    <div class="activity-item">
                        <i class="fas fa-level-up-alt activity-icon"></i>
                        <span><strong>Lightbringer</strong> reached level 85</span>
                        <small>5 minutes ago</small>
                    </div>
                    <div class="activity-item">
                        <i class="fas fa-sword activity-icon"></i>
                        <span>New item acquired: <strong>Blade of Destiny</strong></span>
                        <small>12 minutes ago</small>
                    </div>
                    <div class="activity-item">
                        <i class="fas fa-shield-alt activity-icon"></i>
                        <span>Guild <strong>DragonSlayers</strong> formed</span>
                        <small>1 hour ago</small>
                    </div>
                </div>
            </div>
        </section>

        <footer class="footer-section">
            <p>WoW DataHub Management System | Built with Java, JSP, MySQL | Real-time Analytics</p>
            <p style="margin-top: 10px; font-size: 0.9em;">
                Connected to <strong>${totalRealms}</strong> realms • 
                Tracking <strong><fmt:formatNumber value="${totalCharacters}"/></strong> characters •
                Last updated: <fmt:formatDate value="${lastUpdated}" pattern="MMM dd, yyyy HH:mm"/>
            </p>
        </footer>
    </main>

    <!-- Hidden data for JavaScript -->
    <script type="text/javascript">
        // Pass server data to JavaScript
        window.dashboardData = {
            classDistribution: ${classDistributionJson},
            topPlayers: ${topPlayersJson},
            realmData: ${realmDataJson}
        };
    </script>
    
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="assets/js/dashboard.js"></script>
</body>
</html>