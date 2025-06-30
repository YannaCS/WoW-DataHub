<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Weapon Update - WoW DataHub</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body>
    <!-- Include WoW Navigation -->
    <jsp:include page="WoWStyle.jsp">
        <jsp:param name="activePage" value="weaponupdate" />
    </jsp:include>

    <div class="main-container">
        <div class="main-content">
            <h1 class="page-title">
                <i class="bi bi-sword"></i> Weapon & Equipment Management
            </h1>
            <p class="page-subtitle">
                Manage character weapons and equipment loadouts
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
                                <form method="get" action="weaponupdate">
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
                                            Enter the ID of the character whose weapons you want to manage.
                                        </div>
                                    </div>
                                    <div class="d-grid">
                                        <button type="submit" class="btn btn-primary">
                                            <i class="bi bi-search"></i> Load Character
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

            <!-- Character Information & Weapon Management -->
            <c:if test="${not empty character}">
                <div class="row">
                    <!-- Character Information -->
                    <div class="col-lg-5">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="bi bi-person-badge"></i> Character Information
                                </h5>
                            </div>
                            <div class="card-body">
                                <div class="row">
                                    <div class="col-sm-6">
                                        <div class="mb-3">
                                            <label class="form-label">Character Name</label>
                                            <div style="background: rgba(244, 208, 63, 0.1); padding: 0.75rem; border-radius: 8px; border: 1px solid var(--wow-gold);">
                                                <strong style="color: var(--wow-gold); font-size: 1.1rem;">
                                                    ${character.firstName} ${character.lastName}
                                                </strong>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="mb-3">
                                            <label class="form-label">Character ID</label>
                                            <div style="background: rgba(30, 60, 114, 0.3); padding: 0.75rem; border-radius: 8px; border: 1px solid var(--wow-border);">
                                                <span class="badge" style="background: var(--wow-blue); color: var(--wow-gold); font-size: 0.9rem;">
                                                    ID: ${character.charID}
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="row">
                                    <div class="col-sm-6">
                                        <div class="mb-3">
                                            <label class="form-label">Clan</label>
                                            <div style="background: rgba(44, 72, 117, 0.3); padding: 0.75rem; border-radius: 8px; border: 1px solid var(--wow-border);">
                                                <i class="bi bi-flag"></i> ${character.clan.clanName}
                                                <br><small class="text-muted">${character.clan.race}</small>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-6">
                                        <div class="mb-3">
                                            <label class="form-label">Player</label>
                                            <div style="background: rgba(44, 72, 117, 0.3); padding: 0.75rem; border-radius: 8px; border: 1px solid var(--wow-border);">
                                                <i class="bi bi-person"></i> ${character.players.firstName} ${character.players.lastName}
                                                <br><small class="text-muted">${character.players.emailAddress}</small>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <!-- Current Weapon -->
                                <div class="mb-3">
                                    <label class="form-label">Currently Equipped Weapon</label>
                                    <div style="background: linear-gradient(135deg, rgba(244, 208, 63, 0.1), rgba(30, 60, 114, 0.1)); padding: 1rem; border-radius: 8px; border: 2px solid var(--wow-gold);">
                                        <div class="d-flex align-items-center">
                                            <i class="bi bi-sword" style="font-size: 1.5rem; color: var(--wow-gold); margin-right: 1rem;"></i>
                                            <div>
                                                <strong style="color: var(--wow-gold); font-size: 1.1rem;">
                                                    ${character.weaponWeared.itemName}
                                                </strong>
                                                <br>
                                                <small class="text-muted">
                                                    <i class="bi bi-shield-shaded"></i> ${character.weaponWeared.wearableJob} • 
                                                    <i class="bi bi-lightning"></i> ${character.weaponWeared.damage} DMG • 
                                                    <i class="bi bi-bar-chart"></i> Level ${character.weaponWeared.level}
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Weapon Selection -->
                    <div class="col-lg-7">
                        <div class="card">
                            <div class="card-header">
                                <h5 class="mb-0">
                                    <i class="bi bi-collection"></i> Available Weapons
                                    <c:if test="${not empty weapons}">
                                        <span class="badge" style="background: var(--wow-gold); color: var(--wow-dark-blue);">
                                            ${weapons.size()} weapons
                                        </span>
                                    </c:if>
                                </h5>
                            </div>
                            <div class="card-body">
                                <c:if test="${not empty weapons}">
                                    <form method="post" action="weaponupdate">
                                        <input type="hidden" name="charid" value="${character.charID}">
                                        
                                        <div class="mb-3">
                                            <label for="weaponid" class="form-label">
                                                <i class="bi bi-list-ul"></i> Select New Weapon
                                            </label>
                                            <select class="form-select" id="weaponid" name="weaponid" required>
                                                <option value="">Choose a weapon...</option>
                                                <c:forEach items="${weapons}" var="weapon">
                                                    <option value="${weapon.itemID}" 
                                                            ${weapon.itemID == character.weaponWeared.itemID ? 'selected' : ''}>
                                                        ${weapon.itemName} (${weapon.wearableJob} • ${weapon.damage} DMG • Lv.${weapon.level})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        
                                        <div class="d-grid">
                                            <button type="submit" class="btn btn-primary">
                                                <i class="bi bi-arrow-repeat"></i> Equip Selected Weapon
                                            </button>
                                        </div>
                                    </form>
                                </c:if>
                                
                                <c:if test="${empty weapons}">
                                    <div class="text-center" style="padding: 2rem;">
                                        <i class="bi bi-exclamation-triangle" style="font-size: 2rem; color: var(--wow-silver); opacity: 0.5;"></i>
                                        <h6 class="mt-3" style="color: var(--wow-silver);">No Weapons Available</h6>
                                        <p class="text-muted">
                                            This character doesn't have any weapons in their inventory.
                                            <br>Run the ETL process to add more weapons to the database.
                                        </p>
                                        <a href="etl" class="btn btn-outline-primary">
                                            <i class="bi bi-database-add"></i> Run ETL Process
                                        </a>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                        <!-- Weapon Details -->
                        <c:if test="${not empty weapons}">
                            <div class="card mt-3">
                                <div class="card-header">
                                    <h6 class="mb-0"><i class="bi bi-info-circle"></i> Weapon Information</h6>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <h6 class="text-warning">Equipment Rules</h6>
                                            <ul class="small text-muted">
                                                <li>Character must have the weapon's required job unlocked</li>
                                                <li>Character level must meet weapon requirements</li>
                                                <li>Weapon must be in character's inventory</li>
                                                <li>Main hand slot must always have a weapon equipped</li>
                                            </ul>
                                        </div>
                                        <div class="col-md-6">
                                            <h6 class="text-warning">Job Switching</h6>
                                            <ul class="small text-muted">
                                                <li>Equipped weapon determines current job</li>
                                                <li>Each weapon is tied to a specific job class</li>
                                                <li>Job abilities and stats change with weapon</li>
                                                <li>Character keeps progress in all unlocked jobs</li>
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Character Action Buttons -->
                <div class="text-center mt-4">
                    <a href="weaponupdate" class="btn btn-outline-primary me-3">
                        <i class="bi bi-arrow-left"></i> Select Different Character
                    </a>
                    <a href="characterdetailreport?charid=${character.charID}" class="btn btn-outline-primary me-3">
                        <i class="bi bi-eye"></i> View Full Character Details
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
        
        // Add weapon preview functionality
        const weaponSelect = document.getElementById('weaponid');
        if (weaponSelect) {
            weaponSelect.addEventListener('change', function() {
                const selectedOption = this.options[this.selectedIndex];
                if (selectedOption.value) {
                    // Could add weapon preview functionality here
                    console.log('Selected weapon:', selectedOption.text);
                }
            });
        }
        
        // Form submission loading state
        document.querySelectorAll('form').forEach(form => {
            form.addEventListener('submit', function() {
                const submitBtn = this.querySelector('button[type="submit"]');
                if (submitBtn) {
                    const originalText = submitBtn.innerHTML;
                    submitBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Processing...';
                    submitBtn.disabled = true;
                    
                    // Re-enable after 3 seconds in case of errors
                    setTimeout(() => {
                        submitBtn.innerHTML = originalText;
                        submitBtn.disabled = false;
                    }, 3000);
                }
            });
        });
    </script>
</body>
</html>