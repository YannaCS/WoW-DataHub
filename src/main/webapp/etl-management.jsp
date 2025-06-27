<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>ETL Management - WoW Data Hub</title>
    <link rel="stylesheet" href="assets/css/dashboard.css">
    <style>
        .etl-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .etl-section {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            margin-bottom: 20px;
            padding: 20px;
        }
        
        .etl-section h3 {
            margin-top: 0;
            color: #495057;
        }
        
        .btn {
            display: inline-block;
            padding: 8px 16px;
            margin: 5px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .btn:hover {
            background-color: #0056b3;
        }
        
        .btn-danger {
            background-color: #dc3545;
        }
        
        .btn-danger:hover {
            background-color: #c82333;
        }
        
        .job-status {
            margin-top: 20px;
            padding: 15px;
            border-radius: 4px;
        }
        
        .status-running {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
        }
        
        .status-completed {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
        }
        
        .status-failed {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
        }
        
        .jobs-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        
        .jobs-table th, .jobs-table td {
            padding: 10px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        
        .jobs-table th {
            background-color: #e9ecef;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="etl-container">
        <h1>ETL Management Dashboard</h1>
        <p>Manage World of Warcraft data synchronization jobs</p>
        
        <!-- Start New Jobs Section -->
        <div class="etl-section">
            <h3>Start New ETL Jobs</h3>
            <p>Launch data synchronization processes to update your local database with the latest WoW API data.</p>
            
            <button class="btn" onclick="window.startRealmSync()">Start Realm Sync</button>
            <button class="btn" onclick="window.startCharacterSync()">Start Character Sync</button>
        </div>
        
        <!-- Job Status Section -->
        <div class="etl-section">
            <h3>Job Status</h3>
            <div id="job-status">
                <p>No active jobs. Click "Refresh Jobs" to check for running or completed jobs.</p>
            </div>
            <button class="btn" onclick="window.refreshJobs()">Refresh Jobs</button>
        </div>
        
        <!-- Running Jobs Section -->
        <div class="etl-section">
            <h3>Running Jobs</h3>
            <div id="running-jobs">
                <p>No running jobs.</p>
            </div>
        </div>
        
        <!-- Completed Jobs Section -->
        <div class="etl-section">
            <h3>Recent Completed Jobs</h3>
            <div id="completed-jobs">
                <p>No completed jobs found.</p>
            </div>
        </div>
    </div>

    <script>
        // Make ALL functions globally accessible
        window.currentJobs = {};
        
        window.refreshJobs = function() {
            console.log('🔧 DEBUG: refreshJobs() called');
            console.log('🔧 DEBUG: About to fetch etl?action=jobs');
            
            fetch('etl?action=jobs')
            .then(response => {
                console.log('🔧 DEBUG: Jobs response received, status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('🔧 DEBUG: Jobs data received:', data);
                console.log('🔧 DEBUG: Running jobs:', data.runningJobs);
                console.log('🔧 DEBUG: Completed jobs:', data.completedJobs);
                
                window.updateRunningJobs(data.runningJobs || []);
                window.updateCompletedJobs(data.completedJobs || []);
                
                console.log('🔧 DEBUG: UI updated successfully');
            })
            .catch(error => {
                console.error('🔧 DEBUG: Error fetching jobs:', error);
                document.getElementById('running-jobs').innerHTML = '<p style="color: red;">Error loading jobs: ' + error.message + '</p>';
            });
        };
        
        window.startRealmSync = function() {
            console.log('🔧 DEBUG: startRealmSync() called');
            console.log('Starting realm sync...');
            
            fetch('etl', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=start-realm-sync'
            })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Response data:', data);
                if (data.jobId) {
                    alert('Realm sync job started: ' + data.jobId);
                    window.currentJobs[data.jobId] = 'RUNNING';
                    window.refreshJobs();
                } else {
                    alert('Failed to start realm sync job: ' + JSON.stringify(data));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error starting realm sync job: ' + error.message);
            });
        };
        
        window.startCharacterSync = function() {
            console.log('🔧 DEBUG: startCharacterSync() called');
            console.log('Starting character sync...');            
            
            fetch('etl', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=start-character-sync'
            })
            .then(response => {
                console.log('Response status:', response.status);
                if (!response.ok) {
                    throw new Error('HTTP error! status: ' + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log('Response data:', data);
                if (data.jobId) {
                    alert('Character sync job started: ' + data.jobId);
                    window.currentJobs[data.jobId] = 'RUNNING';
                    window.refreshJobs();
                } else {
                    alert('Failed to start character sync job: ' + JSON.stringify(data));
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error starting character sync job: ' + error.message);
            });
        };
        
        window.stopJob = function(jobId) {
            console.log('🔧 DEBUG: stopJob() called for:', jobId);
            
            fetch('etl', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'action=stop-job&jobId=' + encodeURIComponent(jobId)
            })
            .then(response => response.json())
            .then(data => {
                alert('Job stopped: ' + jobId);
                window.refreshJobs();
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error stopping job');
            });
        };
        
        window.updateRunningJobs = function(runningJobs) {
            console.log('🔧 DEBUG: updateRunningJobs called with:', runningJobs);
            const container = document.getElementById('running-jobs');
            
            if (runningJobs.length === 0) {
                console.log('🔧 DEBUG: No running jobs to display');
                container.innerHTML = '<p>No running jobs.</p>';
                return;
            }
            
            console.log('🔧 DEBUG: Building HTML for', runningJobs.length, 'running jobs');
            
            let html = '<table class="jobs-table">';
            html += '<tr><th>Job ID</th><th>Type</th><th>Status</th><th>Actions</th></tr>';
            
            runningJobs.forEach(job => {
                console.log('🔧 DEBUG: Processing running job:', job);
                
                // Extract job type from job ID if jobType is null
                let jobType = job.jobType || 'Unknown';
                if (!job.jobType && job.jobId) {
                    if (job.jobId.includes('REALM_SYNC')) {
                        jobType = 'Realm Sync';
                    } else if (job.jobId.includes('CHARACTER_SYNC')) {
                        jobType = 'Character Sync';
                    }
                }
                
                html += '<tr>';
                html += '<td>' + (job.jobId || 'No ID') + '</td>';
                html += '<td>' + jobType + '</td>';
                html += '<td><span class="status-running">RUNNING</span></td>';
                html += '<td><button class="btn btn-danger" onclick="window.stopJob(\'' + job.jobId + '\')">Stop</button></td>';
                html += '</tr>';
            });
            
            html += '</table>';
            container.innerHTML = html;
            console.log('🔧 DEBUG: Running jobs HTML updated');
        };
        
        window.updateCompletedJobs = function(completedJobs) {
            console.log('🔧 DEBUG: updateCompletedJobs called with:', completedJobs);
            const container = document.getElementById('completed-jobs');
            
            if (completedJobs.length === 0) {
                console.log('🔧 DEBUG: No completed jobs to display');
                container.innerHTML = '<p>No completed jobs found.</p>';
                return;
            }
            
            console.log('🔧 DEBUG: Building HTML for', completedJobs.length, 'completed jobs');
            
            let html = '<table class="jobs-table">';
            html += '<tr><th>Job ID</th><th>Type</th><th>Status</th><th>Records</th><th>Duration</th></tr>';
            
            completedJobs.forEach(job => {
                console.log('🔧 DEBUG: Processing completed job:', job);
                
                // Extract job type from job ID if jobType is null
                let jobType = job.jobType || 'Unknown';
                if (!job.jobType && job.jobId) {
                    if (job.jobId.includes('REALM_SYNC')) {
                        jobType = 'Realm Sync';
                    } else if (job.jobId.includes('CHARACTER_SYNC')) {
                        jobType = 'Character Sync';
                    }
                }
                
                const statusClass = job.status === 'COMPLETED' ? 'status-completed' : 'status-failed';
                const duration = job.durationMillis ? (job.durationMillis / 1000).toFixed(2) + 's' : 'N/A';
                
                html += '<tr>';
                html += '<td>' + (job.jobId || 'No ID') + '</td>';
                html += '<td>' + jobType + '</td>';
                html += '<td><span class="' + statusClass + '">' + job.status + '</span></td>';
                html += '<td>' + (job.recordsProcessed || 0) + '</td>';
                html += '<td>' + duration + '</td>';
                html += '</tr>';
            });
            
            html += '</table>';
            container.innerHTML = html;
            console.log('🔧 DEBUG: Completed jobs HTML updated');
        };
        
        // Auto-refresh every 5 seconds when there are running jobs
        setInterval(() => {
            const runningJobsContainer = document.getElementById('running-jobs');
            if (runningJobsContainer.innerHTML.includes('RUNNING')) {
                refreshJobs();
            }
        }, 5000);
        
        // Load jobs on page load and set up event listeners
        window.onload = function() {
            console.log('🔧 DEBUG: Page loaded, setting up event listeners');
            
            // Set up refresh button click handler
            const refreshBtn = document.getElementById('refresh-jobs-btn');
            if (refreshBtn) {
                refreshBtn.addEventListener('click', function() {
                    console.log('🔧 DEBUG: Refresh button clicked!');
                    refreshJobs();
                });
                console.log('🔧 DEBUG: Refresh button event listener added');
            } else {
                console.error('🔧 DEBUG: Refresh button not found!');
            }
            
            // Set up start realm sync button
            const realmSyncBtn = document.getElementById('start-realm-sync-btn');
            if (realmSyncBtn) {
                realmSyncBtn.addEventListener('click', function() {
                    console.log('🔧 DEBUG: Start Realm Sync button clicked!');
                    startRealmSync();
                });
                console.log('🔧 DEBUG: Realm sync button event listener added');
            }
            
            // Set up start character sync button
            const charSyncBtn = document.getElementById('start-character-sync-btn');
            if (charSyncBtn) {
                charSyncBtn.addEventListener('click', function() {
                    console.log('🔧 DEBUG: Start Character Sync button clicked!');
                    startCharacterSync();
                });
                console.log('🔧 DEBUG: Character sync button event listener added');
            }
            
            // Load initial jobs
            refreshJobs();
        };
    </script>
</body>
</html>