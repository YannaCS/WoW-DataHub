<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Find Characters - WoW DataHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <!-- Include WoW Navigation -->
    <jsp:include page="WoWStyle.jsp">
        <jsp:param name="activePage" value="findcharacter" />
    </jsp:include>

    <div class="main-container">
        <div class="main-content">
            <h1 class="page-title">
                <i class="bi bi-search"></i> Find Characters
            </h1>
            <p class="page-subtitle">
                Search for players and their characters in the realm
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

            <!-- Search Form -->
            <div class="row justify-content-center">
                <div class="col-lg-6">
                    <div class="card">
                        <div class="card-header">
                            <h5 class="mb-0"><i class="bi bi-person-search"></i> Player Search</h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="findcharacter">
                                <div class="mb-3">
                                    <label for="firstname" class="form-label">
                                        <i class="bi bi-person"></i> Player First Name
                                    </label>
                                    <input type="text" 
                                           class="form-control" 
                                           id="firstname" 
                                           name="firstname" 
                                           value="${previousFirstName}" 
                                           placeholder="Enter player's first name..."
                                           required>
                                </div>
                                <div class="d-grid">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="bi bi-search"></i> Search Players
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Search Results -->
            <c:if test="${not empty players}">
                <div class="row mt-4">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="bi bi-people"></i> Search Results 
                                    <span class="badge" style="background: var(--wow-gold); color: var(--wow-dark-blue);">
                                        ${players.size()} found
                                    </span>
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="table-responsive">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th><i class="bi bi-hash"></i> Player ID</th>
                                                <th><i class="bi bi-person"></i> First Name</th>
                                                <th><i class="bi bi-person-badge"></i> Last Name</th>
                                                <th><i class="bi bi-envelope"></i> Email</th>
                                                <th><i class="bi bi-gear"></i> Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${players}" var="player">
                                                <tr>
                                                    <td>
                                                        <span class="badge" style="background: var(--wow-blue); color: var(--wow-gold);">
                                                            ${player.playerID}
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <strong style="color: var(--wow-gold);">${player.firstName}</strong>
                                                    </td>
                                                    <td>${player.lastName}</td>
                                                    <td>
                                                        <small class="text-muted">
                                                            <i class="bi bi-envelope-at"></i> ${player.emailAddress}
                                                        </small>
                                                    </td>
                                                    <td>
                                                        <div class="btn-group" role="group">
                                                            <a href="characterdetailreport?charid=${player.playerID}" 
                                                               class="btn btn-outline-primary btn-sm"
                                                               title="View Characters">
                                                                <i class="bi bi-eye"></i> View
                                                            </a>
                                                            <a href="weaponupdate?playerid=${player.playerID}" 
                                                               class="btn btn-outline-primary btn-sm"
                                                               title="Manage Equipment">
                                                                <i class="bi bi-sword"></i> Equipment
                                                            </a>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Player Statistics (if results found) -->
            <c:if test="${not empty players && players.size() > 0}">
                <div class="row mt-4">
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h6 class="mb-0"><i class="bi bi-graph-up"></i> Search Statistics</h6>
                            </div>
                            <div class="card-body">
                                <div class="row text-center">
                                    <div class="col-6">
                                        <div style="background: rgba(244, 208, 63, 0.1); padding: 1rem; border-radius: 8px; border: 1px solid var(--wow-gold);">
                                            <div style="font-size: 1.5rem; font-weight: 700; color: var(--wow-gold);">
                                                ${players.size()}
                                            </div>
                                            <small class="text-muted">Players Found</small>
                                        </div>
                                    </div>
                                    <div class="col-6">
                                        <div style="background: rgba(30, 60, 114, 0.1); padding: 1rem; border-radius: 8px; border: 1px solid var(--wow-blue);">
                                            <div style="font-size: 1.5rem; font-weight: 700; color: var(--wow-silver);">
                                                <c:set var="totalChars" value="0" />
                                                <c:forEach items="${players}" var="player">
                                                    <c:set var="totalChars" value="${totalChars + 1}" />
                                                </c:forEach>
                                                Est. ${totalChars * 2}
                                            </div>
                                            <small class="text-muted">Est. Characters</small>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div class="card">
                            <div class="card-header">
                                <h6 class="mb-0"><i class="bi bi-info-circle"></i> Search Tips</h6>
                            </div>
                            <div class="card-body">
                                <ul class="small text-muted mb-0">
                                    <li>Search is case-sensitive and looks for exact matches</li>
                                    <li>Use the View button to see detailed character information</li>
                                    <li>Equipment button allows weapon and gear management</li>
                                    <li>Players may have multiple characters in different clans</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Empty State -->
            <c:if test="${empty players && not empty previousFirstName}">
                <div class="row mt-4 justify-content-center">
                    <div class="col-lg-6">
                        <div class="text-center" style="padding: 3rem; background: rgba(44, 72, 117, 0.2); border-radius: 15px; border: 2px dashed var(--wow-border);">
                            <i class="bi bi-search" style="font-size: 3rem; color: var(--wow-silver); opacity: 0.5;"></i>
                            <h4 class="mt-3" style="color: var(--wow-silver);">No Players Found</h4>
                            <p class="text-muted">
                                No players found with the first name "<strong>${previousFirstName}</strong>".
                                <br>Try a different name or run the ETL process to add more data.
                            </p>
                            <div class="mt-3">
                                <a href="etl" class="btn btn-primary me-2">
                                    <i class="bi bi-database-add"></i> Run ETL
                                </a>
                                <button type="button" class="btn btn-outline-primary" onclick="document.getElementById('firstname').focus();">
                                    <i class="bi bi-arrow-repeat"></i> Try Again
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Quick Actions -->
            <div class="text-center mt-4">
                <a href="home" class="btn btn-outline-primary me-3">
                    <i class="bi bi-house-door"></i> Back to Dashboard
                </a>
                <a href="etl" class="btn btn-outline-primary">
                    <i class="bi bi-database-add"></i> ETL Management
                </a>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Auto-focus on search input when page loads
        document.addEventListener('DOMContentLoaded', function() {
            const searchInput = document.getElementById('firstname');
            if (searchInput && !searchInput.value) {
                searchInput.focus();
            }
        });
        
        // Add loading state to search button
        document.querySelector('form').addEventListener('submit', function() {
            const submitBtn = this.querySelector('button[type="submit"]');
            submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Searching...';
            submitBtn.disabled = true;
        });
    </script>
</body>
</html>