<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Character Details - WoW DataHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <!-- Include WoW Navigation -->
    <jsp:include page="WoWStyle.jsp">
        <jsp:param name="activePage" value="characterdetailreport" />
    </jsp:include>

    <div class="main-container">
        <div class="main-content">
            <h1 class="page-title">
                <i class="bi bi-person-lines-fill"></i> Character Detail Report
            </h1>
            <p class="page-subtitle">
                Complete character profile and statistics
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

            <!-- Character Selection Form -->
            <c:if test="${empty character}">
                <div class="row justify-content-center">
                    <div class="col-lg-6">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0"><i class="bi bi-person-gear"></i> Select Character</h5>
                            </div>
                            <div class="card-body">
                                <form method="get" action="characterdetailreport">
                                    <div class="mb-3">
                                        <label for="charid" class="form-label">
                                            <i class="bi bi-hash"></i> Character ID
                                        </label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="charid" 
                                               name="charid" 
                                               placeholder="Enter character ID..."
                                               min="1"
                                               required>
                                        <div class="form-text">
                                            Enter the ID of the character whose details you want to view.
                                        </div>
                                    </div>
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-search"></i> Load Character Details
                                        </button>
                                    </div>
                                </form>
                                
                                <div class="mt-4 text-center">
                                    <small class="text-muted">
                                        <i class="bi bi-lightbulb"></i> Tip: Use the 
                                        <a href="findcharacter" class="text-decoration-none" style="color: var(--wow-gold);">Find Characters</a> 
                                        page to discover character IDs.
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Character Details -->
            <c:if test="${not empty character}">
                <!-- Character Header -->
                <div class="row">
                    <div class="col-12">
                        <div class="card mb-4">
                            <div class="card-header" style="background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%); color: var(--wow-dark-blue);">
                                <div class="row align-items-center">
                                    <div class="col">
                                        <h3 class="mb-0">
                                            <i class="bi bi-person-badge"></i> ${character.firstName} ${character.lastName}
                                        </h3>
                                        <small>Character ID: ${character.charID}</small>
                                    </div>
                                    <div class="col-auto">
                                        <span class="badge" style="background: var(--wow-dark-blue); color: var(--wow-gold); font-size: 1rem; padding: 0.5rem 1rem;">
                                            <i class="bi bi-shield-shaded"></i> ${character.weaponWeared.wearableJob}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h6 class="text-warning"><i class="bi bi-person"></i> Player Information</h6>
                                        <p class="mb-1"><strong>Player:</strong> ${character.players.firstName} ${character.players.lastName}</p>
                                        <p class="mb-3"><strong>Email:</strong> ${character.players.emailAddress}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <h6 class="text-warning"><i class="bi bi-flag"></i> Clan Information</h6>
                                        <p class="mb-1"><strong>Clan:</strong> ${character.clan.clanName}</p>
                                        <p class="mb-3"><strong>Race:</strong> ${character.clan.race}</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <!-- Unlocked Jobs -->
                    <div class="col-lg-6">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5 class="mb-0"><i class="bi bi-briefcase"></i> Unlocked Jobs</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty unlockedJobs}">
                                    <div class="table-responsive">
                                        <table class="table table-sm">
                                            <thead>
                                                <tr>
                                                    <th>Job</th>
                                                    <th>Level</th>
                                                    <th>Experience</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <c:forEach items="${unlockedJobs}" var="job">
                                                    <tr class="${job.job == character.weaponWeared.wearableJob ? 'table-warning' : ''}">
                                                        <td>
                                                            <strong style="color: ${job.job == character.weaponWeared.wearableJob ? 'var(--wow-dark-blue)' : 'var(--wow-gold)'};">
                                                                ${job.job}
                                                                <c:if test="${job.job == character.weaponWeared.wearableJob}">
                                                                    <i class="bi bi-star-fill ms-1"></i>
                                                                </c:if>
                                                            </strong>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${job.jobLevel != null}">
                                                                    <span class="badge" style="background: var(--wow-blue); color: var(--wow-gold);">
                                                                        Lv.${job.jobLevel}
                                                                    </span>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">Unlocked</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${job.xP != null}">
                                                                    <fmt:formatNumber value="${job.xP}" type="number" groupingUsed="true"/> XP
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-muted">0 XP</span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                    <small class="text-muted">
                                        <i class="bi bi-star-fill text-warning"></i> Currently active job
                                    </small>
                                </c:if>
                                <c:if test="${empty unlockedJobs}">
                                    <p class="text-muted text-center">No jobs unlocked yet.</p>
                                </c:if>
                            </div>
                        </div>
                    </div>

                    <!-- Character Wealth -->
                    <div class="col-lg-6">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5 class="mb-0"><i class="bi bi-coin"></i> Character Wealth</h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty wealth}">
                                    <div style="max-height: 300px; overflow-y: auto;">
                                        <c:forEach items="${wealth}" var="currency">
                                            <div class="d-flex justify-content-between align-items-center mb-2 p-2" 
                                                 style="background: rgba(44, 72, 117, 0.3); border-radius: 8px; border: 1px solid var(--wow-border);">
                                                <div>
                                                    <strong style="color: var(--wow-gold);">${currency.currency.currencyName}</strong>
                                                    <c:if test="${currency.weeklyAcquired != null}">
                                                        <br><small class="text-muted">Weekly: <fmt:formatNumber value="${currency.weeklyAcquired}" type="number" groupingUsed="true"/></small>
                                                    </c:if>
                                                </div>
                                                <div class="text-end">
                                                    <strong style="color: var(--wow-silver);">
                                                        <fmt:formatNumber value="${currency.amount}" type="number" groupingUsed="true"/>
                                                    </strong>
                                                    <c:if test="${currency.currency.cap != null}">
                                                        <br><small class="text-muted">Cap: <fmt:formatNumber value="${currency.currency.cap}" type="number" groupingUsed="true"/></small>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:if>
                                <c:if test="${empty wealth}">
                                    <p class="text-muted text-center">No currency records found.</p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <!-- Inventory -->
				<c:if test="${not empty inventoryDetails}">
				    <div style="max-height: 400px; overflow-y: auto;">
				        <div class="table-responsive">
				            <table class="table table-sm">
				                <thead>
				                    <tr>
				                        <th>Slot</th>
				                        <th>Item Name</th>
				                        <th>Type</th>
				                        <th>Level</th>
				                        <th>Qty</th>
				                    </tr>
				                </thead>
				                <tbody>
				                    <c:forEach items="${inventoryDetails}" var="item">
				                        <tr>
				                            <td>
				                                <span class="badge" style="background: var(--wow-blue); color: var(--wow-gold);">
				                                    ${item.slotID}
				                                </span>
				                            </td>
				                            <td>
				                                <strong style="color: var(--wow-gold);">${item.itemName}</strong>
				                            </td>
				                            <td>
				                                <span class="badge" style="background: ${item.itemType == 'Weapon' ? 'var(--wow-gold)' : item.itemType == 'Gear' ? 'var(--wow-silver)' : 'var(--wow-bronze)'}; color: var(--wow-dark-blue); font-size: 0.8rem;">
				                                    ${item.itemType}
				                                </span>
				                            </td>
				                            <td>
				                                <span class="text-muted">Lv.${item.level}</span>
				                            </td>
				                            <td>
				                                <span style="color: var(--wow-silver);">${item.quantity}</span>
				                            </td>
				                        </tr>
				                    </c:forEach>
				                </tbody>
				            </table>
				        </div>
				    </div>
				</c:if>
				<c:if test="${empty inventoryDetails}">
				    <p class="text-muted text-center">Inventory is empty.</p>
				</c:if>

                    <!-- Equipped Items -->
                    <div class="col-lg-6">
                        <div class="card mb-4">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="bi bi-gem"></i> Equipped Items
                                    <c:if test="${not empty equippedItems}">
                                        <span class="badge" style="background: var(--wow-gold); color: var(--wow-dark-blue);">
                                            ${equippedItems.size()} equipped
                                        </span>
                                    </c:if>
                                </h5>
                            </div>
                            <div class="card-body">
                                <!-- Current Weapon (Always Present) -->
                                <div class="mb-3 p-3" style="background: linear-gradient(135deg, rgba(244, 208, 63, 0.1), rgba(30, 60, 114, 0.1)); border-radius: 8px; border: 2px solid var(--wow-gold);">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <strong style="color: var(--wow-gold);">MAIN_HAND</strong>
                                            <br><small class="text-muted">Current Weapon</small>
                                        </div>
                                        <div class="text-end">
                                            <strong>${character.weaponWeared.itemName}</strong>
                                            <br><small class="text-muted">${character.weaponWeared.damage} DMG • Lv.${character.weaponWeared.level}</small>
                                        </div>
                                    </div>
                                </div>

                                <!-- Other Equipped Items -->
                                <c:if test="${not empty equippedItems}">
                                    <c:forEach items="${equippedItems}" var="equipped">
                                        <div class="d-flex justify-content-between align-items-center mb-2 p-2" 
                                             style="background: rgba(44, 72, 117, 0.3); border-radius: 8px; border: 1px solid var(--wow-border);">
                                            <div>
                                                <strong style="color: var(--wow-gold);">${equipped.equipPosition}</strong>
                                            </div>
                                            <div class="text-end">
                                                <span style="color: var(--wow-silver);">Item ID: ${equipped.itemID}</span>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </c:if>
                                
                                <c:if test="${empty equippedItems}">
                                    <p class="text-muted text-center">Only weapon is equipped.</p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Action Buttons -->
                <div class="text-center mt-4">
                    <a href="characterdetailreport" class="btn btn-outline-primary me-3">
                        <i class="bi bi-arrow-left"></i> Select Different Character
                    </a>
                    <a href="weaponupdate?charid=${character.charID}" class="btn btn-outline-primary me-3">
                        <i class="bi bi-sword"></i> Manage Weapons
                    </a>
                    <a href="home" class="btn btn-outline-primary">
                        <i class="bi bi-house-door"></i> Back to Dashboard
                    </a>
                </div>
            </c:if>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Auto-focus on character ID input when page loads
        document.addEventListener('DOMContentLoaded', function() {
            const charidInput = document.getElementById('charid');
            if (charidInput) {
                charidInput.focus();
            }
        });
        
        // Form submission loading state
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', function() {
                const submitBtn = this.querySelector('button[type="submit"]');
                if (submitBtn) {
                    const originalText = submitBtn.innerHTML;
                    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Loading...';
                    submitBtn.disabled = true;
                }
            });
        });
    </script>
</body>
</html>