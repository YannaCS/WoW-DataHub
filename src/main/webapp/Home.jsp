<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WoW DataHub - Management Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <style>
        @import url('https://fonts.googleapis.com/css2?family=Cinzel:wght@400;600;700&family=Roboto:wght@300;400;500;700&display=swap');
        
        :root {
            --wow-gold: #f4d03f;
            --wow-blue: #1e3c72;
            --wow-dark-blue: #0f1d36;
            --wow-silver: #c0c0c0;
            --wow-bronze: #cd7f32;
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
            position: relative;
            overflow-x: hidden;
        }
        
        body::before {
            content: '';
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: 
                radial-gradient(circle at 20% 80%, rgba(244, 208, 63, 0.1) 0%, transparent 50%),
                radial-gradient(circle at 80% 20%, rgba(30, 60, 114, 0.1) 0%, transparent 50%),
                url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="star" patternUnits="userSpaceOnUse" width="20" height="20"><polygon fill="rgba(244,208,63,0.05)" points="10,1 4,19 19,8 1,8 16,19"/></pattern></defs><rect width="100" height="100" fill="url(%23star)"/></svg>');
            z-index: -1;
            pointer-events: none;
        }
        
        /* Navigation Card */
        .nav-card {
            background: linear-gradient(135deg, var(--wow-card-bg) 0%, rgba(44, 72, 117, 0.9) 100%);
            border: 2px solid var(--wow-border);
            border-radius: 15px;
            backdrop-filter: blur(10px);
            box-shadow: 
                0 8px 32px rgba(0, 0, 0, 0.3),
                inset 0 1px 0 rgba(255, 255, 255, 0.1);
            margin: 20px;
            padding: 0;
            overflow: hidden;
            position: relative;
        }
        
        .nav-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 3px;
            background: linear-gradient(90deg, var(--wow-gold) 0%, var(--wow-silver) 50%, var(--wow-gold) 100%);
        }
        
        .nav-header {
            background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%);
            color: var(--wow-dark-blue);
            padding: 1rem 2rem;
            text-align: center;
            font-family: 'Cinzel', serif;
            position: relative;
        }
        
        .nav-header h1 {
            font-size: 2.5rem;
            font-weight: 700;
            margin: 0;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
            letter-spacing: 2px;
        }
        
        .nav-header .subtitle {
            font-size: 1rem;
            opacity: 0.8;
            margin-top: 0.5rem;
            font-family: 'Roboto', sans-serif;
            font-weight: 500;
        }
        
        .nav-links {
            padding: 1.5rem 2rem;
            display: flex;
            flex-wrap: wrap;
            gap: 1rem;
            justify-content: center;
        }
        
        .nav-link-btn {
            background: linear-gradient(135deg, var(--wow-blue) 0%, var(--wow-dark-blue) 100%);
            color: var(--wow-gold);
            text-decoration: none;
            padding: 0.75rem 1.5rem;
            border-radius: 8px;
            border: 1px solid var(--wow-border);
            font-weight: 600;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
            font-size: 0.95rem;
        }
        
        .nav-link-btn::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(244, 208, 63, 0.2), transparent);
            transition: left 0.5s;
        }
        
        .nav-link-btn:hover {
            color: #ffffff;
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(244, 208, 63, 0.3);
            border-color: var(--wow-gold);
        }
        
        .nav-link-btn:hover::before {
            left: 100%;
        }
        
        .nav-link-btn.active {
            background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%);
            color: var(--wow-dark-blue);
            border-color: var(--wow-gold);
        }
        
        /* Dashboard Container */
        .dashboard-container {
            background: var(--wow-card-bg);
            backdrop-filter: blur(10px);
            border: 2px solid var(--wow-border);
            border-radius: 15px;
            box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
            margin: 20px;
            overflow: hidden;
            position: relative;
        }
        
        .dashboard-container::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 3px;
            background: linear-gradient(90deg, var(--wow-gold) 0%, var(--wow-silver) 50%, var(--wow-gold) 100%);
        }
        
        /* Stats Grid */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
            padding: 1.5rem;
        }
        
        .stat-card {
            background: linear-gradient(135deg, var(--wow-blue) 0%, var(--wow-dark-blue) 100%);
            color: var(--wow-gold);
            padding: 1.0rem;
            border-radius: 12px;
            border: 1px solid var(--wow-border);
            text-align: center;
            transition: all 0.3s ease;
            position: relative;
            overflow: hidden;
        }
        
        .stat-card::before {
            content: '';
            position: absolute;
            top: -50%;
            left: -50%;
            width: 200%;
            height: 200%;
            background: linear-gradient(45deg, transparent, rgba(244, 208, 63, 0.1), transparent);
            transform: rotate(45deg);
            transition: transform 0.6s;
        }
        
        .stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(244, 208, 63, 0.3);
            border-color: var(--wow-gold);
        }
        
        .stat-card:hover::before {
            transform: rotate(45deg) translate(100%, 100%);
        }
        
        .stat-card .icon {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: var(--wow-gold);
        }
        
        .stat-card .number {
            font-size: 2rem;
            font-weight: 700;
            margin-bottom: 0.5rem;
            color: #ffffff;
        }
        
        .stat-card .label {
            font-size: 0.9rem;
            opacity: 0.9;
            text-transform: uppercase;
            letter-spacing: 1px;
            color: var(--wow-silver);
        }
        
        /* Chart Section */
        .chart-section {
            padding: 2rem;
        }
        
        .chart-container {
            background: var(--wow-card-bg);
            border: 1px solid var(--wow-border);
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }
        
        .chart-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: var(--wow-gold);
            margin-bottom: 1rem;
            text-align: center;
            font-family: 'Cinzel', serif;
        }
        
        /* Leaderboard */
        .leaderboard-card {
            background: var(--wow-card-bg);
            border: 1px solid var(--wow-border);
            border-radius: 12px;
            padding: 1.5rem;
            margin-bottom: 2rem;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        }
        
        .leaderboard-title {
            font-size: 1.25rem;
            font-weight: 600;
            color: var(--wow-gold);
            margin-bottom: 1rem;
            text-align: center;
            border-bottom: 2px solid var(--wow-border);
            padding-bottom: 0.5rem;
            font-family: 'Cinzel', serif;
        }
        
        .leaderboard-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.75rem;
            margin-bottom: 0.5rem;
            background: rgba(44, 72, 117, 0.3);
            border: 1px solid rgba(244, 208, 63, 0.2);
            border-radius: 8px;
            transition: all 0.3s ease;
        }
        
        .leaderboard-item:hover {
            background: rgba(44, 72, 117, 0.5);
            border-color: var(--wow-gold);
            transform: translateX(5px);
        }
        
        .leaderboard-rank {
            font-weight: 700;
            color: var(--wow-gold);
            min-width: 30px;
        }
        
        .leaderboard-name {
            flex-grow: 1;
            margin-left: 1rem;
            font-weight: 500;
            color: #ffffff;
        }
        
        .leaderboard-value {
            font-weight: 600;
            color: var(--wow-silver);
        }
        
        /* Currency Items */
        .currency-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0.75rem;
            background: rgba(44, 72, 117, 0.3);
            border: 1px solid rgba(244, 208, 63, 0.2);
            border-radius: 8px;
            margin-bottom: 0.5rem;
            transition: all 0.3s ease;
        }
        
        .currency-item:hover {
            background: rgba(44, 72, 117, 0.5);
            border-color: var(--wow-gold);
        }
        
        .currency-name {
            font-weight: 600;
            color: var(--wow-gold);
        }
        
        .currency-stats {
            text-align: right;
            font-size: 0.9rem;
            color: var(--wow-silver);
        }
        
        /* Online Indicator */
        .online-indicator {
            display: inline-block;
            width: 12px;
            height: 12px;
            background: #00ff88;
            border-radius: 50%;
            margin-right: 0.5rem;
            animation: pulse 2s infinite;
        }
        
        @keyframes pulse {
            0% { opacity: 1; box-shadow: 0 0 5px #00ff88; }
            50% { opacity: 0.5; box-shadow: 0 0 15px #00ff88; }
            100% { opacity: 1; box-shadow: 0 0 5px #00ff88; }
        }
        
        /* Alert Styling */
        .alert {
            border-radius: 10px;
            border: 1px solid var(--wow-border);
            background: var(--wow-card-bg);
            color: #ffffff;
        }
        
        .alert-success {
            border-color: #00ff88;
            background: rgba(0, 255, 136, 0.1);
        }
        
        .alert-danger {
            border-color: #ff4757;
            background: rgba(255, 71, 87, 0.1);
        }
        
        .alert-warning {
            border-color: var(--wow-gold);
            background: rgba(244, 208, 63, 0.1);
        }
        
        /* Responsive Design */
        @media (max-width: 768px) {
            .nav-links {
                flex-direction: column;
                align-items: center;
            }
            
            .nav-link-btn {
                width: 200px;
                text-align: center;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
                padding: 1rem;
            }
            
            .nav-header h1 {
                font-size: 2rem;
            }
        }
    </style>
</head>
<body>
    <!-- Navigation Card -->
    <div class="nav-card">
        <div class="nav-header">
            <h1><i class="bi bi-shield-shaded"></i> WoW DataHub</h1>
            <p class="subtitle">
                <span class="online-indicator"></span>
                Management Dashboard & Analytics Platform
            </p>
        </div>
        <div class="nav-links">
            <a href="home" class="nav-link-btn active">
                <i class="bi bi-house-door"></i> Dashboard
            </a>
            <a href="etl" class="nav-link-btn">
                <i class="bi bi-database-add"></i> ETL Management
            </a>
            <a href="findcharacter" class="nav-link-btn">
                <i class="bi bi-search"></i> Find Characters
            </a>
            <a href="weaponupdate" class="nav-link-btn">
                <i class="bi bi-sword"></i> Weapon Update
            </a>
            <a href="characterdetailreport" class="nav-link-btn">
                <i class="bi bi-person-lines-fill"></i> Character Reports
            </a>
        </div>
    </div>

    <div class="dashboard-container">
    <!-- Debug section - remove after testing
	<div style="background: #333; color: #fff; padding: 10px; margin: 10px; display: none;" id="debugInfo">
	    <h4>Debug Info:</h4>
	    <p>Daily Active Users count: ${fn:length(dailyActiveUsers)}</p>
	    <p>Job Distribution count: ${fn:length(jobDistribution)}</p>
	    <p>Clan Distribution count: ${fn:length(clanDistribution)}</p>
	    <p>Currency Stats count: ${fn:length(currencyStats)}</p>
	    <p>Item Stats count: ${fn:length(itemStats)}</p>
	</div> -->
        <!-- Messages -->
        <c:if test="${not empty messages}">
            <div class="px-4 pt-3">
                <c:forEach items="${messages}" var="message">
                    <div class="alert alert-${message.key == 'error' ? 'danger' : (message.key == 'warning' ? 'warning' : 'success')} alert-dismissible fade show">
                        <strong>
                            <c:choose>
                                <c:when test="${message.key == 'error'}"><i class="bi bi-exclamation-triangle"></i> Error!</c:when>
                                <c:when test="${message.key == 'warning'}"><i class="bi bi-exclamation-circle"></i> Warning!</c:when>
                                <c:otherwise><i class="bi bi-check-circle"></i> Success!</c:otherwise>
                            </c:choose>
                        </strong>
                        ${message.value}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:forEach>
            </div>
        </c:if>

        <!-- Overall Statistics -->
        <div class="stats-grid">
            <div class="stat-card">
                <div class="icon"><i class="bi bi-people"></i></div>
                <div class="number">${overallStats.totalPlayers}</div>
                <div class="label">Total Players</div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="bi bi-person-badge"></i></div>
                <div class="number">${overallStats.totalCharacters}</div>
                <div class="label">Characters</div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="bi bi-hammer"></i></div>
                <div class="number">${overallStats.totalWeapons}</div>
                <div class="label">Weapons</div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="bi bi-shield"></i></div>
                <div class="number">${overallStats.totalGears}</div>
                <div class="label">Gear Items</div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="bi bi-cup"></i></div>
                <div class="number">${overallStats.totalConsumables}</div>
                <div class="label">Consumables</div>
            </div>
            <div class="stat-card">
                <div class="icon"><i class="bi bi-flag"></i></div>
                <div class="number">${overallStats.totalClans}</div>
                <div class="label">Clans</div>
            </div>
        </div>

        <!-- Charts and Analytics Section -->
		<div class="chart-section">
		    <div class="row">
		        <!-- Left Column - Charts -->
		        <div class="col-lg-8">
		            <!-- Daily Active Players Chart -->
		            <div class="chart-container" style="margin-bottom: 30px;">
		                <h3 class="chart-title"><i class="bi bi-graph-up"></i> Daily Active Players (Last 30 Days)</h3>
		                <canvas id="dailyActiveChart" width="400" height="200"></canvas>
		            </div>
		            
		            <div class="row">
		                <!-- Job Distribution -->
		                <div class="col-md-6">
		                    <div class="chart-container">
		                        <h3 class="chart-title"><i class="bi bi-pie-chart"></i> Job Distribution</h3>
		                        <canvas id="jobDistributionChart" width="200" height="200"></canvas>
		                    </div>
		                </div>
		                
		                <!-- Item Type Distribution (moved here) -->
		                <div class="col-md-6">
		                    <div class="chart-container">
		                        <h3 class="chart-title"><i class="bi bi-box"></i> Item Type Distribution</h3>
		                        <canvas id="itemTypeChart" width="100" height="100"></canvas>
		                    </div>
		                </div>
		            </div>
		            
		            <!-- Clan and Race Distribution (moved here) -->
					<div class="chart-container" style="margin-top: 30px;">
					    <h3 class="chart-title"><i class="bi bi-people-fill"></i> Clan and Race Distribution</h3>
					    <div style="width: 600px; height: 600px; margin: 0 auto;">
					        <canvas id="clanRaceChart"></canvas>
					    </div>
					</div>
		        </div>
		
		        <!-- Right Column - Leaderboards (unchanged) -->
		        <div class="col-lg-4">
		            <!-- Top Players by Level -->
		            <div class="leaderboard-card" style="margin-bottom: 20px;">
		                <h3 class="leaderboard-title"><i class="bi bi-trophy"></i> Top Players by Level</h3>
		                <div style="max-height: 525px; overflow-y: auto;">
		                    <c:forEach items="${topPlayersByLevel}" var="player" varStatus="status">
		                        <div class="leaderboard-item">
		                            <span class="leaderboard-rank">#${status.index + 1}</span>
		                            <div class="leaderboard-name">
		                                <strong>${player.characterName}</strong><br>
		                                <small class="text-muted">${player.currentJob} • ${player.race}</small>
		                            </div>
		                            <span class="leaderboard-value">Lv.${player.maxLevel}</span>
		                        </div>
		                    </c:forEach>
		                </div>
		            </div>
		
		            <!-- Wealthiest Players -->
		            <div class="leaderboard-card" style="margin-bottom: 20px;">
		                <h3 class="leaderboard-title"><i class="bi bi-gem"></i> Wealthiest Players</h3>
		                <div style="max-height: 525px; overflow-y: auto;">
		                    <c:forEach items="${topPlayersByWealth}" var="player" varStatus="status">
		                        <div class="leaderboard-item">
		                            <span class="leaderboard-rank">#${status.index + 1}</span>
		                            <div class="leaderboard-name">
		                                <strong>${player.characterName}</strong><br>
		                                <small class="text-muted">${player.currencyTypes} currencies</small>
		                            </div>
		                            <span class="leaderboard-value">
		                                <fmt:formatNumber value="${player.totalWealth}" type="number" groupingUsed="true"/>
		                            </span>
		                        </div>
		                    </c:forEach>
		                </div>
		            </div>
		
		            <!-- Currency Statistics -->
		            <div class="leaderboard-card">
		                <h3 class="leaderboard-title"><i class="bi bi-coin"></i> Currency Statistics</h3>
		                <div style="max-height: 525px; overflow-y: auto;">
		                    <c:forEach items="${currencyStats}" var="currency">
		                        <div class="currency-item">
		                            <div class="currency-name">${currency.currencyName}</div>
		                            <div class="currency-stats">
		                                <div><strong><fmt:formatNumber value="${currency.totalInCirculation}" type="number" groupingUsed="true"/></strong> Total</div>
		                                <div class="text-muted">${currency.playersWithCurrency} players</div>
		                            </div>
		                        </div>
		                    </c:forEach>
		                </div>
		            </div>
		        </div>
		    </div>
		</div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
	    // Add debugging
	    console.log("=== CHART DATA DEBUG ===");
	    
	    // Helper function to check if data exists
	    function hasData(dataArray) {
	        return dataArray && dataArray.length > 0;
	    }
	    
	 	// Helper function to adjust color brightness
	    function adjustColor(color, amount) {
	        const num = parseInt(color.replace('#', ''), 16);
	        const r = Math.min(255, Math.max(0, (num >> 16) + amount));
	        const g = Math.min(255, Math.max(0, ((num >> 8) & 0x00FF) + amount));
	        const b = Math.min(255, Math.max(0, (num & 0x0000FF) + amount));
	        return '#' + ((r << 16) | (g << 8) | b).toString(16).padStart(6, '0');
	    }
	
	    // Daily Active Users Chart
	    const dailyActiveCtx = document.getElementById('dailyActiveChart').getContext('2d');
	    const dailyLabels = [
	        <c:forEach items="${dailyActiveUsers}" var="day" varStatus="status">
	            '${fn:escapeXml(day.activityDate)}'<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    const dailyData = [
	        <c:forEach items="${dailyActiveUsers}" var="day" varStatus="status">
	            ${day.activeCount}<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    
	    console.log("Daily Active - Labels:", dailyLabels);
	    console.log("Daily Active - Data:", dailyData);
	    console.log("Daily Active - Labels length:", dailyLabels.length);
	
	    if (dailyLabels.length === 0) {
	        console.log("No daily active data - showing empty state");
	        // Show empty state for daily active chart
	        dailyActiveCtx.font = '16px Arial';
	        dailyActiveCtx.fillStyle = '#6c757d';
	        dailyActiveCtx.textAlign = 'center';
	        dailyActiveCtx.fillText('No activity data available', dailyActiveCtx.canvas.width/2, dailyActiveCtx.canvas.height/2);
	        dailyActiveCtx.fillText('Run ETL to populate data', dailyActiveCtx.canvas.width/2, dailyActiveCtx.canvas.height/2 + 25);
	    } else {
	        console.log("Creating daily active chart with data");
	        const dailyActiveChart = new Chart(dailyActiveCtx, {
	            type: 'line',
	            data: {
	                labels: dailyLabels,
	                datasets: [{
	                    label: 'Active Players',
	                    data: dailyData,
	                    borderColor: '#f4d03f',
	                    backgroundColor: 'rgba(244, 208, 63, 0.2)',
	                    borderWidth: 3,
	                    fill: true,
	                    tension: 0.4
	                }]
	            },
	            options: {
	                responsive: true,
	                plugins: {
	                    legend: {
	                        display: false
	                    }
	                },
	                scales: {
	                    y: {
	                        beginAtZero: true,
	                        grid: {
	                            color: 'rgba(244, 208, 63, 0.1)'
	                        },
	                        ticks: {
	                            color: '#c0c0c0'
	                        }
	                    },
	                    x: {
	                        grid: {
	                            display: false
	                        },
	                        ticks: {
	                            color: '#c0c0c0'
	                        }
	                    }
	                }
	            }
	        });
	    }
	
	    // Job Distribution Chart
	    const jobCtx = document.getElementById('jobDistributionChart').getContext('2d');
	    const jobLabels = [
	        <c:forEach items="${jobDistribution}" var="job" varStatus="status">
	            '${fn:escapeXml(job.jobName)}'<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    const jobData = [
	        <c:forEach items="${jobDistribution}" var="job" varStatus="status">
	            ${job.characterCount}<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    
	    console.log("Job Distribution - Labels:", jobLabels);
	    console.log("Job Distribution - Data:", jobData);
	    console.log("Job Distribution - Labels length:", jobLabels.length);
	
	    if (jobLabels.length === 0) {
	        console.log("No job data - showing empty state");
	        jobCtx.font = '16px Arial';
	        jobCtx.fillStyle = '#6c757d';
	        jobCtx.textAlign = 'center';
	        jobCtx.fillText('No job data available', jobCtx.canvas.width/2, jobCtx.canvas.height/2);
	        jobCtx.fillText('Run ETL to populate data', jobCtx.canvas.width/2, jobCtx.canvas.height/2 + 25);
	    } else {
	        console.log("Creating job distribution chart with data");
	        const jobChart = new Chart(jobCtx, {
	            type: 'pie',
	            data: {
	                labels: jobLabels,
	                datasets: [{
	                    data: jobData,
	                    backgroundColor: [
	                        '#f4d03f', '#1e3c72', '#c0c0c0', '#cd7f32', '#9966FF', 
	                        '#FF9F40', '#FF6384', '#4BC0C0', '#36A2EB', '#FFCE56',
	                        '#2c4875', '#0f1d36'
	                    ],
	                    borderWidth: 2,
	                    borderColor: '#0a0e1a'
	                }]
	            },
	            options: {
	                responsive: true,
	                plugins: {
	                    legend: {
	                        position: 'bottom',
	                        labels: {
	                            padding: 20,
	                            usePointStyle: true,
	                            color: '#c0c0c0'
	                        }
	                    }
	                }
	            }
	        });
	    }
	
	 	
	
	    // Item Type Chart
	    const itemCtx = document.getElementById('itemTypeChart').getContext('2d');
	    const itemLabels = [
	        <c:forEach items="${itemStats}" var="item" varStatus="status">
	            '${fn:escapeXml(item.itemType)}'<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    const itemData = [
	        <c:forEach items="${itemStats}" var="item" varStatus="status">
	            ${item.count}<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    
	    console.log("Item Type - Labels:", itemLabels);
	    console.log("Item Type - Data:", itemData);
	
	    if (itemLabels.length === 0) {
	        itemCtx.font = '16px Arial';
	        itemCtx.fillStyle = '#6c757d';
	        itemCtx.textAlign = 'center';
	        itemCtx.fillText('No item data available', itemCtx.canvas.width/2, itemCtx.canvas.height/2);
	        itemCtx.fillText('Run ETL to populate data', itemCtx.canvas.width/2, itemCtx.canvas.height/2 + 25);
	    } else {
	        const itemChart = new Chart(itemCtx, {
	            type: 'pie',
	            data: {
	                labels: itemLabels,
	                datasets: [{
	                    data: itemData,
	                    backgroundColor: ['#f4d03f', '#1e3c72', '#c0c0c0'],
	                    borderWidth: 3,
	                    borderColor: '#0a0e1a'
	                }]
	            },
	            options: {
	                responsive: true,
	                plugins: {
	                    legend: {
	                        position: 'bottom',
	                        labels: {
	                            padding: 15,
	                            usePointStyle: true,
	                            color: '#c0c0c0'
	                        }
	                    }
	                }
	            }
	        });
	    }
	    
	 	// Clan and Race Distribution Chart
	    const clanRaceCtx = document.getElementById('clanRaceChart').getContext('2d');

	    // Prepare data for the chart
	    const clanLabels = [
	        <c:forEach items="${clanDistribution}" var="clan" varStatus="status">
	            '${fn:escapeXml(clan.clanName)}'<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    const clanData = [
	        <c:forEach items="${clanDistribution}" var="clan" varStatus="status">
	            ${clan.characterCount}<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];
	    const raceForEachClan = [
	        <c:forEach items="${clanDistribution}" var="clan" varStatus="status">
	            '${fn:escapeXml(clan.race)}'<c:if test="${!status.last}">,</c:if>
	        </c:forEach>
	    ];

	    // Define color schemes for each race
	    const raceColorSchemes = {
	        'human': ['#FFD700', '#FFED4E', '#F4A460', '#FFA500', '#FFB347'],      // Gold/Yellow tones
	        'elf': ['#4169E1', '#6495ED', '#87CEEB', '#4682B4', '#5F9EA0'],       // Blue tones
	        'dwarf': ['#CD853F', '#DEB887', '#D2691E', '#BC8F8F', '#F4A460'],     // Brown/Bronze tones
	        'orc': ['#DC143C', '#FF6347', '#CD5C5C', '#F08080', '#E9967A'],       // Red tones
	        'goblin': ['#32CD32', '#90EE90', '#3CB371', '#2E8B57', '#228B22']     // Green tones
	    };

	    // Organize clans by race
	    const raceOrder = ['human', 'elf', 'dwarf', 'orc', 'goblin'];
	    const clansByRace = {};
	    raceOrder.forEach(race => {
	        clansByRace[race] = [];
	    });

	    // Group clans by race
	    clanLabels.forEach((clan, index) => {
	        const race = raceForEachClan[index].toLowerCase();
	        clansByRace[race].push({
	            name: clan,
	            count: clanData[index],
	            originalIndex: index
	        });
	    });

	    // Reorganize data to group clans by race
	    let sortedClanLabels = [];
	    let sortedClanData = [];
	    let clanColors = [];
	    let clanRaceLabels = [];

	    raceOrder.forEach(race => {
	        const clansForRace = clansByRace[race];
	        const colorScheme = raceColorSchemes[race] || raceColorSchemes['human'];
	        
	        // Sort clans within each race by count (optional)
	        clansForRace.sort((a, b) => b.count - a.count);
	        
	        // Add clans for this race
	        clansForRace.forEach((clan, index) => {
	            sortedClanLabels.push(clan.name);
	            sortedClanData.push(clan.count);
	            clanRaceLabels.push(race);
	            // Use different shades for each clan within the race
	            clanColors.push(colorScheme[index % colorScheme.length]);
	        });
	    });

	    console.log("Sorted Clan Labels:", sortedClanLabels);
	    console.log("Sorted Clan Data:", sortedClanData);

	    if (sortedClanLabels.length === 0) {
	        clanRaceCtx.font = '16px Arial';
	        clanRaceCtx.fillStyle = '#6c757d';
	        clanRaceCtx.textAlign = 'center';
	        clanRaceCtx.fillText('No clan data available', clanRaceCtx.canvas.width/2, clanRaceCtx.canvas.height/2);
	        clanRaceCtx.fillText('Run ETL to populate data', clanRaceCtx.canvas.width/2, clanRaceCtx.canvas.height/2 + 25);
	    } else {
	        const clanRaceChart = new Chart(clanRaceCtx, {
	            type: 'doughnut',
	            data: {
	                labels: sortedClanLabels,
	                datasets: [{
	                    data: sortedClanData,
	                    backgroundColor: clanColors,
	                    borderWidth: 2,
	                    borderColor: '#0a0e1a'
	                }]
	            },
	            options: {
	                responsive: true,
	                maintainAspectRatio: true,
	                plugins: {
	                    legend: {
	                        display: true,
	                        position: 'right',
	                        labels: {
	                            padding: 10,
	                            usePointStyle: true,
	                            color: '#c0c0c0',
	                            font: {
	                                size: 12
	                            },
	                            generateLabels: function(chart) {
	                                let labels = [];
	                                let currentRace = '';
	                                
	                                sortedClanLabels.forEach((clan, index) => {
	                                    const race = clanRaceLabels[index];
	                                    
	                                    // Add race header when it changes
	                                    if (race !== currentRace) {
	                                        currentRace = race;
	                                        labels.push({
	                                            text: race.charAt(0).toUpperCase() + race.slice(1),
	                                            fillStyle: 'transparent',
	                                            strokeStyle: 'transparent',
	                                            lineWidth: 0,
	                                            fontStyle: 'bold',
	                                            fontColor: '#f4d03f'
	                                        });
	                                    }
	                                    
	                                    // Add clan entry
	                                    labels.push({
	                                        text: '  • ' + clan + ' (' + sortedClanData[index] + ')',
	                                        fillStyle: clanColors[index],
	                                        strokeStyle: '#0a0e1a',
	                                        lineWidth: 2,
	                                        hidden: false,
	                                        index: index
	                                    });
	                                });
	                                
	                                return labels;
	                            }
	                        }
	                    },
	                    tooltip: {
	                        callbacks: {
	                            label: function(context) {
	                                const index = context.dataIndex;
	                                const clan = sortedClanLabels[index];
	                                const race = clanRaceLabels[index];
	                                const value = context.parsed;
	                                const total = sortedClanData.reduce((a, b) => a + b, 0);
	                                const percentage = ((value / total) * 100).toFixed(1);
	                                
	                                return [
	                                    clan + ' (' + race.charAt(0).toUpperCase() + race.slice(1) + ')',
	                                    'Characters: ' + value,
	                                    'Percentage: ' + percentage + '%'
	                                ];
	                            }
	                        }
	                    }
	                },
	                cutout: '30%',
	                rotation: -90,
	                circumference: 360
	            }
	        });
	    }
	</script>
</body>
</html>