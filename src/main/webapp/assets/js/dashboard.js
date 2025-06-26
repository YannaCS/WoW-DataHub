// WoW DataHub Dashboard JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Initialize dashboard
    initializeCharts();
    initializeFilters();
    initializeRealTimeUpdates();
    
    // Add smooth scrolling for navigation
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth'
                });
            }
        });
    });
});

// Chart Configuration
const chartConfig = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
        legend: {
            labels: {
                color: '#F8FAFC',
                font: {
                    family: 'Segoe UI',
                    size: 12
                }
            }
        }
    },
    scales: {
        x: {
            ticks: {
                color: '#94A3B8'
            },
            grid: {
                color: 'rgba(148, 163, 184, 0.1)'
            }
        },
        y: {
            ticks: {
                color: '#94A3B8'
            },
            grid: {
                color: 'rgba(148, 163, 184, 0.1)'
            }
        }
    }
};

// Generate sample performance data
function generatePerformanceData() {
    const baseLatency = 25;
    const data = [];
    for (let i = 0; i < 24; i++) {
        const variation = Math.sin(i * Math.PI / 12) * 15 + Math.random() * 10;
        data.push(Math.max(5, baseLatency + variation));
    }
    return data;
}

// Utility: Debounce function
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Easing function
function easeOutQuart(t) {
    return 1 - (--t) * t * t * t;
}

// Animate number changes
function animateNumber(element, from, to) {
    const duration = 1000;
    const startTime = performance.now();
    
    function animate(currentTime) {
        const elapsed = currentTime - startTime;
        const progress = Math.min(elapsed / duration, 1);
        
        const currentValue = Math.round(from + (to - from) * easeOutQuart(progress));
        element.textContent = currentValue.toLocaleString();
        
        if (progress < 1) {
            requestAnimationFrame(animate);
        }
    }
    
    requestAnimationFrame(animate);
}

// Collect current filter values
function collectFilters() {
    const filters = {};
    const filterSelects = document.querySelectorAll('.filter-select');
    const filterInputs = document.querySelectorAll('.filter-input');
    
    filterSelects.forEach(select => {
        if (select.value && select.value !== 'All') {
            filters[select.name || select.id] = select.value;
        }
    });
    
    filterInputs.forEach(input => {
        if (input.value.trim()) {
            filters[input.name || input.id] = input.value.trim();
        }
    });
    
    return filters;
}

// Show loading state
function showLoadingState() {
    const loadingElements = document.querySelectorAll('.stat-value, .chart-container');
    loadingElements.forEach(el => {
        el.style.opacity = '0.6';
        el.style.pointerEvents = 'none';
    });
}

// Hide loading state
function hideLoadingState() {
    const loadingElements = document.querySelectorAll('.stat-value, .chart-container');
    loadingElements.forEach(el => {
        el.style.opacity = '1';
        el.style.pointerEvents = 'auto';
    });
}

// Update stat cards
function updateStatCards(filters) {
    const statCards = document.querySelectorAll('.stat-card');
    
    statCards.forEach(card => {
        const value = card.querySelector('.stat-value');
        const change = card.querySelector('.stat-change');
        
        if (value) {
            // Simulate data update
            const currentValue = parseInt(value.textContent.replace(/[^\d]/g, '')) || 0;
            const variation = Math.random() * 0.2 - 0.1; // ±10% variation
            const newValue = Math.round(currentValue * (1 + variation));
            
            animateNumber(value, currentValue, newValue);
        }
        
        if (change) {
            // Update change indicator
            const changePercent = (Math.random() * 20 - 10).toFixed(1);
            const isPositive = changePercent > 0;
            
            change.className = `stat-change ${isPositive ? 'stat-positive' : 'stat-negative'}`;
            change.innerHTML = `<i class="fas fa-arrow-${isPositive ? 'up' : 'down'}"></i> ${Math.abs(changePercent)}%`;
        }
    });
}

// Update charts with new data
function updateCharts(filters) {
    // This would typically make API calls to get filtered data
    console.log('Updating charts with filters:', filters);
}

// Update leaderboard
function updateLeaderboard(filters) {
    const leaderboard = document.querySelector('.leaderboard');
    if (!leaderboard) return;
    
    // Simulate leaderboard update
    const entries = leaderboard.querySelectorAll('.player-entry');
    entries.forEach(entry => {
        const level = entry.querySelector('.player-level');
        if (level) {
            const currentLevel = parseInt(level.textContent.replace(/\D/g, '')) || 1;
            const newLevel = Math.max(1, currentLevel + Math.floor(Math.random() * 3 - 1));
            level.textContent = `Lv. ${newLevel}`;
        }
    });
}

// Update dashboard data based on filters
function updateDashboardData(filters) {
    // Update stat cards
    updateStatCards(filters);
    
    // Update charts
    updateCharts(filters);
    
    // Update leaderboard
    updateLeaderboard(filters);
}

// Apply filters to dashboard - FIXED: Now properly defined
function applyFilters(filters) {
    console.log('Applying filters:', filters);
    
    // Show loading state
    showLoadingState();
    
    // Simulate API call
    setTimeout(() => {
        updateDashboardData(filters);
        hideLoadingState();
    }, 1000);
}

// Global function for onclick handlers
window.applyFilters = function() {
    const filters = collectFilters();
    applyFilters(filters);
};

// Initialize Charts
function initializeCharts() {
    // Player Activity Chart
    const activityCtx = document.getElementById('activityChart');
    if (activityCtx) {
        new Chart(activityCtx, {
            type: 'line',
            data: {
                labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
                datasets: [{
                    label: 'Active Players',
                    data: [12000, 19000, 15000, 25000, 22000, 30000, 28000],
                    borderColor: '#FFD700',
                    backgroundColor: 'rgba(255, 215, 0, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }, {
                    label: 'New Registrations',
                    data: [800, 1200, 900, 1600, 1400, 2000, 1800],
                    borderColor: '#3B82F6',
                    backgroundColor: 'rgba(59, 130, 246, 0.1)',
                    borderWidth: 3,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: chartConfig
        });
    }

    // Class Distribution Chart
    const classCtx = document.getElementById('classChart');
    if (classCtx) {
        new Chart(classCtx, {
            type: 'doughnut',
            data: {
                labels: ['Warrior', 'Mage', 'Archer', 'Priest', 'Thief', 'Paladin'],
                datasets: [{
                    data: [25, 20, 18, 15, 12, 10],
                    backgroundColor: [
                        '#FFD700',
                        '#3B82F6', 
                        '#10B981',
                        '#F59E0B',
                        '#EF4444',
                        '#8B5CF6'
                    ],
                    borderWidth: 0
                }]
            },
            options: chartConfig
        });
    }

    // Level Distribution Chart
    const levelCtx = document.getElementById('levelChart');
    if (levelCtx) {
        new Chart(levelCtx, {
            type: 'bar',
            data: {
                labels: ['1-10', '11-20', '21-30', '31-40', '41-50', '51-60', '61-70', '71-80', '81-90', '91-100'],
                datasets: [{
                    label: 'Characters',
                    data: [1200, 2800, 3500, 4200, 3800, 3200, 2500, 1800, 1200, 500],
                    backgroundColor: 'rgba(255, 215, 0, 0.8)',
                    borderColor: '#FFD700',
                    borderWidth: 1
                }]
            },
            options: chartConfig
        });
    }

    // Server Performance Chart
    const performanceCtx = document.getElementById('performanceChart');
    if (performanceCtx) {
        new Chart(performanceCtx, {
            type: 'line',
            data: {
                labels: Array.from({length: 24}, (_, i) => `${i}:00`),
                datasets: [{
                    label: 'Response Time (ms)',
                    data: generatePerformanceData(),
                    borderColor: '#10B981',
                    backgroundColor: 'rgba(16, 185, 129, 0.1)',
                    borderWidth: 2,
                    fill: true,
                    tension: 0.4
                }]
            },
            options: chartConfig
        });
    }
} // <- FIXED: Added missing closing brace here

// Initialize Filters
function initializeFilters() {
    const filterButton = document.querySelector('.filter-button');
    const filterSelects = document.querySelectorAll('.filter-select');
    const filterInputs = document.querySelectorAll('.filter-input');

    if (filterButton) {
        filterButton.addEventListener('click', function() {
            const filters = collectFilters();
            applyFilters(filters);
        });
    }

    // Add change listeners for real-time filtering
    filterSelects.forEach(select => {
        select.addEventListener('change', debounce(function() {
            const filters = collectFilters();
            applyFilters(filters);
        }, 300));
    });
}

// Create connection status indicator
function createConnectionStatusIndicator() {
    const indicator = document.createElement('div');
    indicator.className = 'connection-status';
    indicator.innerHTML = '<i class="fas fa-circle"></i> Live';
    indicator.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: rgba(16, 185, 129, 0.9);
        color: white;
        padding: 8px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: bold;
        z-index: 1001;
        display: flex;
        align-items: center;
        gap: 5px;
    `;
    
    document.body.appendChild(indicator);
    
    // Animate the indicator
    setInterval(() => {
        indicator.style.opacity = '0.5';
        setTimeout(() => {
            indicator.style.opacity = '1';
        }, 500);
    }, 2000);
}

// Initialize real-time updates
function initializeRealTimeUpdates() {
    // Update every 30 seconds
    setInterval(function() {
        const filters = collectFilters();
        updateDashboardData(filters);
    }, 30000);
    
    // Add connection status indicator
    createConnectionStatusIndicator();
}

// Export functions for global access
window.WoWDashboard = {
    updateDashboardData,
    applyFilters,
    collectFilters,
    animateNumber
};

// Add keyboard shortcuts
document.addEventListener('keydown', function(e) {
    // Ctrl/Cmd + R to refresh dashboard
    if ((e.ctrlKey || e.metaKey) && e.key === 'r') {
        e.preventDefault();
        location.reload();
    }
    
    // Ctrl/Cmd + F to focus on search
    if ((e.ctrlKey || e.metaKey) && e.key === 'f') {
        const searchInput = document.querySelector('.filter-input');
        if (searchInput) {
            e.preventDefault();
            searchInput.focus();
        }
    }
});

// Add smooth hover effects
document.addEventListener('mouseover', function(e) {
    if (e.target.classList.contains('stat-card')) {
        e.target.style.transform = 'translateY(-8px) scale(1.02)';
    }
});

document.addEventListener('mouseout', function(e) {
    if (e.target.classList.contains('stat-card')) {
        e.target.style.transform = 'translateY(0) scale(1)';
    }
});

// Console welcome message
console.log('\n' +
    'WoW DataHub Dashboard Loaded\n' +
    '=====================================\n' +
    'Version: 1.0.0\n' +
    'Built with: Chart.js, Vanilla JS\n' +
    'Real-time updates: Enabled\n' +
    '====================================='
);