<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>WoW Data Hub - ETL Process</title>
<style>
    body {
        font-family: 'Segoe UI', 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol', Arial, sans-serif;
        max-width: 1200px;
        margin: 0 auto;
        padding: 20px;
        background-color: #f5f5f5;
    }
    
    .container {
        background-color: white;
        padding: 30px;
        border-radius: 10px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    
    h1 {
        color: #2c3e50;
        text-align: center;
        margin-bottom: 30px;
        border-bottom: 3px solid #3498db;
        padding-bottom: 10px;
    }
    
    .message {
        padding: 15px;
        margin: 20px 0;
        border-radius: 5px;
        font-weight: bold;
    }
    
    .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
    .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    .warning { background-color: #fff3cd; color: #856404; border: 1px solid #ffeaa7; }
    .info { background-color: #d1ecf1; color: #0c5460; border: 1px solid #bee5eb; }
    
    .etl-section {
        margin: 30px 0;
        padding: 20px;
        border: 2px solid #3498db;
        border-radius: 8px;
        background-color: #f8f9fa;
    }
    
    .etl-button {
        background-color: #3498db;
        color: white;
        padding: 15px 30px;
        border: none;
        border-radius: 5px;
        font-size: 16px;
        font-weight: bold;
        cursor: pointer;
        transition: background-color 0.3s;
        display: block;
        margin: 20px auto;
    }
    
    .etl-button:hover {
        background-color: #2980b9;
    }
    
    .etl-button:disabled {
        background-color: #95a5a6;
        cursor: not-allowed;
    }
    
    .records-table {
        width: 100%;
        border-collapse: collapse;
        margin-top: 20px;
    }
    
    .records-table th, .records-table td {
        border: 1px solid #ddd;
        padding: 12px;
        text-align: left;
    }
    
    .records-table th {
        background-color: #3498db;
        color: white;
        font-weight: bold;
    }
    
    .records-table tr:nth-child(even) {
        background-color: #f2f2f2;
    }
    
    .records-table tr:hover {
        background-color: #e8f4f8;
    }
    
    .navigation {
        text-align: center;
        margin-top: 30px;
        padding-top: 20px;
        border-top: 2px solid #ecf0f1;
    }
    
    .nav-link {
        display: inline-block;
        margin: 0 10px;
        padding: 10px 20px;
        background-color: #34495e;
        color: white;
        text-decoration: none;
        border-radius: 5px;
        transition: background-color 0.3s;
    }
    
    .nav-link:hover {
        background-color: #2c3e50;
    }
    
    .description {
        background-color: #ecf0f1;
        padding: 15px;
        border-radius: 5px;
        margin-bottom: 20px;
        font-style: italic;
    }
</style>
</head>
<body>
    <div class="container">
        <h1>&#127918; WoW Data Hub - ETL Process</h1>
        
        <div class="description">
            <p><strong>Extract, Transform, Load (ETL) Process:</strong> This process will add 100 new records to each table in the database using real World of Warcraft API data where possible. Existing data will be preserved.</p>
        </div>
        
        <!-- Display Messages -->
        <c:if test="${messages.success != null}">
            <div class="message success">
                &#9989; ${messages.success}
            </div>
        </c:if>
        
        <c:if test="${messages.error != null}">
            <div class="message error">
                &#10060; ${messages.error}
            </div>
        </c:if>
        
        <c:if test="${messages.warning != null}">
            <div class="message warning">
                &#9888; ${messages.warning}
            </div>
        </c:if>
        
        <c:if test="${messages.info != null}">
            <div class="message info">
                &#8505; ${messages.info}
            </div>
        </c:if>
        
        <!-- ETL Control Section -->
        <div class="etl-section">
            <h2>&#128640; Run ETL Process</h2>
            <p>Click the button below to start the ETL process. This will:</p>
            <ul>
                <li>Connect to the World of Warcraft API (if credentials are configured)</li>
                <li>Extract real game data including races, realms, items, and more</li>
                <li>Add 100 new records to each table in the database</li>
                <li>Preserve all existing data</li>
            </ul>
            
            <form method="post">
                <input type="hidden" name="action" value="runETL">
                <button type="submit" class="etl-button" 
                        ${messages.info != null ? 'disabled' : ''}>
                    ${messages.info != null ? 'ETL Running...' : 'Start ETL Process'}
                </button>
            </form>
            
            <p><em>Note: The ETL process may take several minutes to complete. Please be patient and do not refresh the page.</em></p>
        </div>
        
        <!-- Current Database Status -->
        <div class="etl-section">
            <h2>&#128202; Current Database Status</h2>
            <p>Current number of records in each table:</p>
            
            <c:if test="${recordCounts != null}">
                <table class="records-table">
                    <thead>
                        <tr>
                            <th>Table Name</th>
                            <th>Record Count</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="entry" items="${recordCounts}">
                            <tr>
                                <td><strong>${entry.key}</strong></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${entry.value == -1}">
                                            <span style="color: red;">Error</span>
                                        </c:when>
                                        <c:otherwise>
                                            ${entry.value}
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${entry.value == -1}">
                                            <span style="color: red;">&#10060; Error</span>
                                        </c:when>
                                        <c:when test="${entry.value == 0}">
                                            <span style="color: orange;">&#9888; Empty</span>
                                        </c:when>
                                        <c:when test="${entry.value > 0 && entry.value < 50}">
                                            <span style="color: blue;">&#128202; Low Data</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="color: green;">&#9989; Good</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:if>
            
            <c:if test="${recordCounts == null}">
                <p style="color: red;">&#10060; Unable to retrieve database statistics.</p>
            </c:if>
        </div>
        
        <!-- Navigation -->
        <div class="navigation">
            <h3>Navigate to Other Features</h3>
            <a href="findcharacter" class="nav-link">&#128269; Find Characters</a>
            <a href="weaponupdate" class="nav-link">&#9876; Update Weapons</a>
            <a href="characterdetailreport" class="nav-link">&#128203; Character Details</a>
        </div>
    </div>
    
    <script>
        // Auto-refresh the page every 30 seconds if ETL is running
        <c:if test="${messages.info != null}">
            setTimeout(function() {
                window.location.reload();
            }, 30000);
        </c:if>
    </script>
</body>
</html>