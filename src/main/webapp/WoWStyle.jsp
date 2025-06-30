<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- WoW Themed Styles -->
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
    
    /* Main Content Container */
    .main-container {
        background: var(--wow-card-bg);
        backdrop-filter: blur(10px);
        border: 2px solid var(--wow-border);
        border-radius: 15px;
        box-shadow: 0 15px 35px rgba(0, 0, 0, 0.3);
        margin: 20px;
        overflow: hidden;
        position: relative;
    }
    
    .main-container::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, var(--wow-gold) 0%, var(--wow-silver) 50%, var(--wow-gold) 100%);
    }
    
    .main-content {
        padding: 2rem;
    }
    
    /* Forms */
    .form-label {
        color: var(--wow-gold);
        font-weight: 600;
        margin-bottom: 0.5rem;
    }
    
    .form-control {
        background: rgba(44, 72, 117, 0.3);
        border: 1px solid var(--wow-border);
        border-radius: 8px;
        color: #ffffff;
        padding: 0.75rem;
    }
    
    .form-control:focus {
        background: rgba(44, 72, 117, 0.5);
        border-color: var(--wow-gold);
        box-shadow: 0 0 0 0.2rem rgba(244, 208, 63, 0.25);
        color: #ffffff;
    }
    
    .form-control::placeholder {
        color: var(--wow-silver);
        opacity: 0.7;
    }
    
    .form-select {
        background: rgba(44, 72, 117, 0.3);
        border: 1px solid var(--wow-border);
        border-radius: 8px;
        color: #ffffff;
        padding: 0.75rem;
    }
    
    .form-select:focus {
        background: rgba(44, 72, 117, 0.5);
        border-color: var(--wow-gold);
        box-shadow: 0 0 0 0.2rem rgba(244, 208, 63, 0.25);
        color: #ffffff;
    }
    
    /* Buttons */
    .btn-primary {
        background: linear-gradient(135deg, var(--wow-blue) 0%, var(--wow-dark-blue) 100%);
        border: 1px solid var(--wow-border);
        color: var(--wow-gold);
        font-weight: 600;
        padding: 0.75rem 1.5rem;
        border-radius: 8px;
        transition: all 0.3s ease;
    }
    
    .btn-primary:hover {
        background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%);
        color: var(--wow-dark-blue);
        border-color: var(--wow-gold);
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(244, 208, 63, 0.3);
    }
    
    .btn-outline-primary {
        background: transparent;
        border: 2px solid var(--wow-gold);
        color: var(--wow-gold);
        font-weight: 600;
        padding: 0.75rem 1.5rem;
        border-radius: 8px;
        transition: all 0.3s ease;
    }
    
    .btn-outline-primary:hover {
        background: var(--wow-gold);
        color: var(--wow-dark-blue);
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(244, 208, 63, 0.3);
    }
    
    /* Tables */
    .table {
        background: transparent;
        color: #ffffff;
    }
    
    .table th {
        background: rgba(244, 208, 63, 0.1);
        color: var(--wow-gold);
        border-color: var(--wow-border);
        font-weight: 600;
        font-family: 'Cinzel', serif;
    }
    
    .table td {
        border-color: var(--wow-border);
        color: #ffffff;
    }
    
    .table-hover tbody tr:hover {
        background: rgba(44, 72, 117, 0.3);
    }
    
    /* Cards */
    .card {
        background: var(--wow-card-bg);
        border: 1px solid var(--wow-border);
        border-radius: 12px;
        color: #ffffff;
    }
    
    .card-header {
        background: linear-gradient(135deg, var(--wow-gold) 0%, #e6b800 100%);
        color: var(--wow-dark-blue);
        border-bottom: 1px solid var(--wow-border);
        font-weight: 600;
        font-family: 'Cinzel', serif;
    }
    
    .card-body {
        padding: 1.5rem;
    }
    
    /* Alerts */
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
    
    /* Page Title */
    .page-title {
        color: var(--wow-gold);
        font-family: 'Cinzel', serif;
        font-weight: 700;
        font-size: 2rem;
        text-align: center;
        margin-bottom: 2rem;
        text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
    }
    
    .page-subtitle {
        color: var(--wow-silver);
        text-align: center;
        margin-bottom: 2rem;
        font-size: 1.1rem;
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
        
        .nav-header h1 {
            font-size: 2rem;
        }
        
        .main-content {
            padding: 1rem;
        }
    }
</style>

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
        <a href="${pageContext.request.contextPath}/home" class="nav-link-btn ${param.activePage == 'home' ? 'active' : ''}">
            <i class="bi bi-house-door"></i> Dashboard
        </a>
        <a href="${pageContext.request.contextPath}/etl" class="nav-link-btn ${param.activePage == 'etl' ? 'active' : ''}">
            <i class="bi bi-database-add"></i> ETL Management
        </a>
        <a href="${pageContext.request.contextPath}/findcharacter" class="nav-link-btn ${param.activePage == 'findcharacter' ? 'active' : ''}">
            <i class="bi bi-search"></i> Find Characters
        </a>
        <a href="${pageContext.request.contextPath}/weaponupdate" class="nav-link-btn ${param.activePage == 'weaponupdate' ? 'active' : ''}">
            <i class="bi bi-sword"></i> Weapon Update
        </a>
        <a href="${pageContext.request.contextPath}/characterdetailreport" class="nav-link-btn ${param.activePage == 'characterdetailreport' ? 'active' : ''}">
            <i class="bi bi-person-lines-fill"></i> Character Reports
        </a>
    </div>
</div>