<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Character Detail Report - WoW DataHub</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="assets/css/dashboard.css" rel="stylesheet">
    <style>
        .character-detail-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }
        
        .character-header {
            background: var(--card-bg);
            border-radius: 16px;
            padding: 2rem;
            margin-bottom: 2rem;
            border: 1px solid var(--border-color);
        }
        
        .character-info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .info-section {
            background: var(--card-bg);
            border-radius: 16px;
            padding: 1.5rem;
            border: 1px solid var(--border-color);
        }
        
        .section-title {
            font-size: 1.2rem;
            color: var(--primary-gold);
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .info-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.5rem 0;
            border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        }
        
        .info-item:last-child {
            border-bottom: none;
        }
        
        .info-label {
            color: var(--text-muted);
            font-weight: 500;
        }
        
        .info-value {
            color: var(--text-light);
            font-weight: bold;
        }
        
        .back-button {
            background: linear-gradient(45deg, var(--primary-gold), #FFA500);
            color: var(--dark-bg);
            border: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            font-weight: bold;
            text-decoration: none;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            transition: transform 0.3s ease;
        }
        
        .back-button:hover {
            transform: translateY(-2px);
            color: var(--dark-bg);
            text-decoration: none;
        }
        
        .weapon-display {
            background: linear-gradient(135deg, rgba(255, 215, 0, 0.1), rgba(255, 165, 0, 0.1));
            border: 2px solid var(--primary-gold);
            border-radius: 12px;
            padding: 1rem;
            margin-top: 1rem;
        }
        
        .weapon-name {
            color: var(--primary-gold);
            font-size: 1.1rem;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <header class="header">
        <nav class="nav-container">
            <div class="logo">
                <i class="fas fa-dragon"></i> WoW DataHub
            </div>
            <div class="nav-links">
                <a href="dashboard" class="nav-link">
                    <i class="fas fa-chart-line"></i> Dashboard
                </a>
                <a href="players" class="nav-link">
                    <i class="fas fa-users"></i> Players
                </a>
                <a href="characters" class="nav-link active">
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

    <main class="character-detail-container">
        <div class="character-header">
            <a href="characters" class="back-button">
                <i class="fas fa-arrow-left"></i> Back to Characters
            </a>
            
            <h1 style="margin: 1rem 0; color: var(--primary-gold);">
                <i class="fas fa-user-shield"></i> Character Detail Report
            </h1>
            
            <c:if test="${not empty character}">
                <h2 style="color: var(--text-light); margin: 0.5rem 0;">
                    ${character.firstName} ${character.lastName}
                </h2>
                <p style="color: var(--text-muted);">
                    Player: ${character.players.firstName} ${character.players.lastName}
                </p>
            </c:if>
            
            <c:if test="${empty character}">
                <div style="text-align: center; padding: 2rem;">
                    <i class="fas fa-exclamation-triangle" style="font-size: 3rem; color: var(--warning-orange); margin-bottom: 1rem;"></i>
                    <h2 style="color: var(--text-light);">Character Not Found</h2>
                    <p style="color: var(--text-muted);">The requested character could not be found in the database.</p>
                </div>
            </c:if>
        </div>

        <c:if test="${not empty character}">
            <div class="character-info-grid">
                <!-- Basic Character Information -->
                <div class="info-section">
                    <h3 class="section-title">
                        <i class="fas fa-id-card"></i> Basic Information
                    </h3>
                    <div class="info-item">
                        <span class="info-label">Character ID:</span>
                        <span class="info-value">${character.charID}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Character Name:</span>
                        <span class="info-value">${character.firstName} ${character.lastName}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Player:</span>
                        <span class="info-value">${character.players.firstName} ${character.players.lastName}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Player Email:</span>
                        <span class="info-value">${character.players.emailAddress}</span>
                    </div>
                </div>

                <!-- Clan Information -->
                <div class="info-section">
                    <h3 class="section-title">
                        <i class="fas fa-shield-alt"></i> Clan Information
                    </h3>
                    <div class="info-item">
                        <span class="info-label">Clan Name:</span>
                        <span class="info-value">${character.clan.clanName}</span>
                    </div>
                    <div class="info-item">
                        <span class="info-label">Race:</span>
                        <span class="info-value">${character.clan.race}</span>
                    </div>
                </div>

                <!-- Weapon Information -->
                <div class="info-section">
                    <h3 class="section-title">
                        <i class="fas fa-sword"></i> Equipped Weapon
                    </h3>
                    <div class="weapon-display">
                        <div class="weapon-name">${character.weaponWeared.itemName}</div>
                        <div class="info-item">
                            <span class="info-label">Weapon ID:</span>
                            <span class="info-value">${character.weaponWeared.itemID}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Level:</span>
                            <span class="info-value">${character.weaponWeared.level}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Damage:</span>
                            <span class="info-value">${character.weaponWeared.damage}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Class:</span>
                            <span class="info-value">${character.weaponWeared.wearableJob}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Required Level:</span>
                            <span class="info-value">${character.weaponWeared.requiredLevel}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Price:</span>
                            <span class="info-value">
                                <fmt:formatNumber value="${character.weaponWeared.price}" type="currency"/>
                            </span>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Character Statistics -->
            <c:if test="${not empty characterStats}">
                <div class="info-section">
                    <h3 class="section-title">
                        <i class="fas fa-chart-bar"></i> Character Statistics
                    </h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                        <c:forEach var="stat" items="${characterStats}">
                            <div class="info-item">
                                <span class="info-label">${stat.statistics.statsName}:</span>
                                <span class="info-value">${stat.value}</span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!-- Character Wealth -->
            <c:if test="${not empty characterWealth}">
                <div class="info-section">
                    <h3 class="section-title">
                        <i class="fas fa-coins"></i> Character Wealth
                    </h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 1rem;">
                        <c:forEach var="wealth" items="${characterWealth}">
                            <div class="info-item">
                                <span class="info-label">${wealth.currency.currencyName}:</span>
                                <span class="info-value">
                                    <fmt:formatNumber value="${wealth.amount}" type="number"/>
                                </span>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <!-- Character Jobs -->
            <c:if test="${not empty characterJobs}">
                <div class="info-section">
                    <h3 class="section-title">
                        <i class="fas fa-briefcase"></i> Unlocked Jobs
                    </h3>
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1rem;">
                        <c:forEach var="job" items="${characterJobs}">
                            <div style="background: rgba(255, 215, 0, 0.1); border-radius: 8px; padding: 1rem;">
                                <div class="info-item">
                                    <span class="info-label">Job:</span>
                                    <span class="info-value">${job.job}</span>
                                </div>
                                <c:if test="${not empty job.jobLevel}">
                                    <div class="info-item">
                                        <span class="info-label">Level:</span>
                                        <span class="info-value">${job.jobLevel}</span>
                                    </div>
                                </c:if>
                                <c:if test="${not empty job.xP}">
                                    <div class="info-item">
                                        <span class="info-label">XP:</span>
                                        <span class="info-value">
                                            <fmt:formatNumber value="${job.xP}" type="number"/>
                                        </span>
                                    </div>
                                </c:if>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </c:if>
        </c:if>
    </main>

    <footer class="footer-section">
        <p>WoW DataHub Management System | Character Detail Report</p>
        <p style="margin-top: 10px; font-size: 0.9em;">
            Last updated: <fmt:formatDate value="${lastUpdated}" pattern="MMM dd, yyyy HH:mm"/>
        </p>
    </footer>
</body>
</html>