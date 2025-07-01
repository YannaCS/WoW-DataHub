<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ETL Management - WoW DataHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <!-- Include WoW Navigation -->
    <jsp:include page="WoWStyle.jsp">
        <jsp:param name="activePage" value="etl" />
    </jsp:include>

    <div class="main-container">
        <div class="main-content">
            <h1 class="page-title">
                <i class="bi bi-database-add"></i> ETL Management
            </h1>
            <p class="page-subtitle">
                Extract, Transform, and Load dynamic game data
            </p>

            <!-- Messages -->
            <c:if test="${not empty messages}">
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
            </c:if>

            <div class="row">
                <!-- ETL Controls -->
                <div class="col-lg-6">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-play-circle"></i> Dynamic ETL Process</h5>
                        </div>
                        <div class="card-body">
                            <p class="mb-4">
                                Run the Dynamic ETL process to add new players, characters, and game data to the database.
                                This process will create realistic game content using the WoW API when available.
                            </p>
                            
                            <form method="post" action="etl">
                                <input type="hidden" name="action" value="runETL">
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary btn-lg">
                                        <i class="bi bi-database-add"></i> Run Dynamic ETL
                                    </button>
                                </div>
                            </form>
                            
                            <div class="mt-4">
                                <h6 class="text-warning">
                                    <i class="bi bi-info-circle"></i> ETL Process Information
                                </h6>
                                <p class="small" style="color: #B8C5D6;">
                                    <li>Adds 50 new players from real WoW realm data</li>
                                    <li>Creates 100+ new characters with business rule validation</li>
                                    <li>Populates character relationships (jobs, wealth, inventory)</li>
                                    <li>Uses authentic WoW data when API credentials are available</li>
                                    <li>Process typically takes 2-3 minutes to complete</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Current Database Status -->
                <div class="col-lg-6">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-database"></i> Current Database Status</h5>
                        </div>
                        <div class="card-body">
                            <c:if test="${not empty recordCounts}">
                                <div class="row">
                                    <c:forEach items="${recordCounts}" var="count">
                                        <div class="col-md-6 mb-3">
                                            <div class="d-flex justify-content-between align-items-center p-2" 
                                                 style="background: rgba(44, 72, 117, 0.3); border-radius: 8px; border: 1px solid var(--wow-border);">
                                                <span class="text-gold">
                                                    <c:choose>
                                                        <c:when test="${count.key == 'Players'}"><i class="bi bi-people"></i></c:when>
                                                        <c:when test="${count.key == 'Characters'}"><i class="bi bi-person-badge"></i></c:when>
                                                        <c:when test="${count.key == 'Weapons'}"><i class="bi bi-sword"></i></c:when>
                                                        <c:when test="${count.key == 'Gears'}"><i class="bi bi-shield"></i></c:when>
                                                        <c:when test="${count.key == 'Consumables'}"><i class="bi bi-cup"></i></c:when>
                                                        <c:when test="${count.key == 'Clans'}"><i class="bi bi-flag"></i></c:when>
                                                        <c:when test="${count.key == 'Currencies'}"><i class="bi bi-coin"></i></c:when>
                                                        <c:when test="${count.key == 'Statistics'}"><i class="bi bi-graph-up"></i></c:when>
                                                        <c:when test="${count.key == 'Inventory'}"><i class="bi bi-box"></i></c:when>
                                                        <c:when test="${count.key == 'EquippedItems'}"><i class="bi bi-gem"></i></c:when>
                                                        <c:when test="${count.key == 'CharacterWealth'}"><i class="bi bi-bank"></i></c:when>
                                                        <c:otherwise><i class="bi bi-table"></i></c:otherwise>
                                                    </c:choose>
                                                    ${count.key}
                                                </span>
                                                <span class="badge" style="background: var(--wow-gold); color: var(--wow-dark-blue); font-weight: 600;">
                                                    <c:choose>
                                                        <c:when test="${count.value == -1}">ERROR</c:when>
                                                        <c:otherwise>${count.value}</c:otherwise>
                                                    </c:choose>
                                                </span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:if>
                            
                            <div class="mt-4">
                                <h6 class="text-info">
                                    <i class="bi bi-lightbulb"></i> ETL Best Practices
                                </h6>
                                <p class="small" style="color: #B8C5D6;">
                                    <li>Run ETL during low-traffic periods</li>
                                    <li>Monitor database size and performance</li>
                                    <li>Backup database before major ETL operations</li>
                                    <li>Check logs for any business rule violations</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- ETL History and Logs -->
            <div class="row mt-4">
                <div class="col-12">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-clock-history"></i> ETL Process Overview</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-4">
                                    <h6 class="text-warning">Default Data ETL</h6>
                                    <p class="small" style="color: #B8C5D6;">
                                        Loads static game data including races, clans, statistics, currencies, 
                                        weapons, gear, and consumables. This runs once during database initialization.
                                    </p>
                                    <div class="badge" style="background: #00ff88; color: var(--wow-dark-blue);">
                                        <i class="bi bi-check-circle"></i> Completed
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <h6 class="text-warning">Dynamic Data ETL</h6>
                                    <p class="small" style="color: #B8C5D6;">
                                        Adds players, characters, and their relationships. Can be run multiple times 
                                        to add more data to the system.
                                    </p>
                                    <div class="badge" style="background: var(--wow-gold); color: var(--wow-dark-blue);">
                                        <i class="bi bi-play-circle"></i> On Demand
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <h6 class="text-warning">Business Rules</h6>
                                    <p class="small" style="color: #B8C5D6;">
                                        All data insertion follows WoW business rules including currency caps, 
                                        job requirements, and equipment validation.
                                    </p>
                                    <div class="badge" style="background: var(--wow-blue); color: var(--wow-gold);">
                                        <i class="bi bi-shield-check"></i> Active
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="text-center mt-4">
                <a href="home" class="btn btn-outline-primary me-3">
                    <i class="bi bi-house-door"></i> Back to Dashboard
                </a>
                <a href="findcharacter" class="btn btn-outline-primary">
                    <i class="bi bi-search"></i> Find Characters
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>