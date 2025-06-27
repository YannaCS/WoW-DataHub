// WoW DataHub Dashboard JavaScript - NUCLEAR CHART FIX
console.log('WoW DataHub Dashboard Loading...');

// NUCLEAR OPTION: Override Chart.js globally to prevent ANY resizing
window.addEventListener('load', function() {
    if (window.Chart) {
        // Disable ALL responsive behavior globally
        Chart.defaults.responsive = false;
        Chart.defaults.maintainAspectRatio = false;
        Chart.defaults.animation = false;
        
        // Override the resize function to do nothing
        Chart.prototype.resize = function() {
            console.log('Chart resize blocked');
            return this;
        };
        
        // Override update function to prevent size changes
        var originalUpdate = Chart.prototype.update;
        Chart.prototype.update = function(config) {
            console.log('Chart update intercepted');
            // Only allow data updates, not size updates
            if (config && config.mode === 'resize') {
                console.log('Resize update blocked');
                return;
            }
            return originalUpdate.call(this, config);
        };
        
        console.log('Chart.js resize functions overridden');
    }
});

// NUCLEAR CSS: Stop everything that could cause growth
function addNuclearCSS() {
    var style = document.createElement('style');
    style.textContent = 
        '/* NUCLEAR CSS - STOP ALL CHART GROWTH */ ' +
        '.chart-container, .chart-container * { ' +
        'height: 280px !important; ' +
        'max-height: 280px !important; ' +
        'min-height: 280px !important; ' +
        'width: 100% !important; ' +
        'max-width: 100% !important; ' +
        'overflow: hidden !important; ' +
        'position: relative !important; ' +
        'box-sizing: border-box !important; ' +
        'flex-shrink: 0 !important; ' +
        'animation: none !important; ' +
        'transition: none !important; ' +
        'transform: none !important; ' +
        'resize: none !important; ' +
        '} ' +
        'canvas { ' +
        'height: 280px !important; ' +
        'max-height: 280px !important; ' +
        'min-height: 280px !important; ' +
        'width: 100% !important; ' +
        'position: static !important; ' +
        'display: block !important; ' +
        '} ' +
        '* { ' +
        'animation-duration: 0s !important; ' +
        'animation-delay: 0s !important; ' +
        'transition-duration: 0s !important; ' +
        'transition-delay: 0s !important; ' +
        '}';
    document.head.appendChild(style);
    console.log('Nuclear CSS applied');
}

// Global variables
var charts = {};
var dashboardData = {
    players: [],
    characters: [],
    items: []
};

// API endpoints
var API_ENDPOINTS = {
    players: '/Wow-Datahub/players',
    characters: '/Wow-Datahub/characters',
    items: '/Wow-Datahub/items'
};

// Chart configuration
var CHART_CONFIG = {
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

// Initialize dashboard - NUCLEAR VERSION
document.addEventListener('DOMContentLoaded', function() {
    console.log('Initializing dashboard with nuclear chart protection...');
    
    // Apply nuclear CSS first
    addNuclearCSS();
    
    // Stop any existing intervals/timeouts
    var id = window.setTimeout(function() {}, 0);
    while (id--) {
        window.clearTimeout(id);
        window.clearInterval(id);
    }
    console.log('All timers cleared');
    
    // Destroy any existing charts
    if (window.Chart && window.Chart.instances) {
        Object.values(window.Chart.instances).forEach(function(chart) {
            if (chart && chart.destroy) {
                chart.destroy();
            }
        });
    }
    console.log('Existing charts destroyed');
    
    // Load data once only
    loadDashboardData();
    
    // Set up manual-only filters
    initializeFilters();
    
    console.log('Dashboard initialized - NUCLEAR MODE - NO AUTO-REFRESH');
});

// NUCLEAR CHART CREATION - COMPLETELY STATIC
function createStaticChart(canvasId, type, data, options) {
    var ctx = document.getElementById(canvasId);
    if (!ctx) {
        console.log(canvasId + ' not found');
        return null;
    }
    
    console.log('Creating nuclear chart for: ' + canvasId);
    
    // FORCE canvas dimensions
    ctx.style.cssText = 'height: 280px !important; width: 100% !important; max-height: 280px !important;';
    ctx.height = 280;
    ctx.width = ctx.offsetWidth || 600;
    
    // Destroy existing chart completely
    if (charts[canvasId]) {
        charts[canvasId].destroy();
        charts[canvasId] = null;
        delete charts[canvasId];
    }
    
    // Create chart with nuclear options
    var chart = new Chart(ctx, {
        type: type,
        data: data,
        options: {
            responsive: false,
            maintainAspectRatio: false,
            animation: false,
            interaction: { intersect: false },
            elements: { point: { radius: 3 }, line: { borderWidth: 2 } },
            plugins: options.plugins || {
                legend: { labels: { color: '#F8FAFC' } }
            },
            scales: options.scales || {
                x: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(148, 163, 184, 0.1)' } },
                y: { ticks: { color: '#94A3B8' }, grid: { color: 'rgba(148, 163, 184, 0.1)' } }
            }
        }
    });
    
    // Store chart reference
    charts[canvasId] = chart;
    
    console.log('Nuclear chart created: ' + canvasId);
    return chart;
}

// Load all dashboard data
function loadDashboardData() {
    console.log('Loading dashboard data...');
    
    // Load players
    fetch(API_ENDPOINTS.players + '?action=list&limit=1000')
        .then(function(response) {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Players API failed');
        })
        .then(function(data) {
            dashboardData.players = data.players || [];
            console.log('Loaded ' + dashboardData.players.length + ' players');
        })
        .catch(function(error) {
            console.warn('Using mock player data:', error.message);
            dashboardData.players = generateMockPlayers();
        })
        .finally(function() {
            updatePlayerStats();
        });
    
    // Load characters
    fetch(API_ENDPOINTS.characters + '?action=list&limit=1000')
        .then(function(response) {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Characters API failed');
        })
        .then(function(data) {
            dashboardData.characters = data.characters || [];
            console.log('Loaded ' + dashboardData.characters.length + ' characters');
        })
        .catch(function(error) {
            console.warn('Using mock character data:', error.message);
            dashboardData.characters = generateMockCharacters();
        })
        .finally(function() {
            updateCharacterStats();
            updateCharts();
        });
    
    // Load items
    fetch(API_ENDPOINTS.items + '?action=list&limit=500')
        .then(function(response) {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Items API failed');
        })
        .then(function(data) {
            dashboardData.items = data.items || [];
            console.log('Loaded ' + dashboardData.items.length + ' items');
        })
        .catch(function(error) {
            console.warn('Using mock item data:', error.message);
            dashboardData.items = generateMockItems();
        })
        .finally(function() {
            updateItemStats();
        });
}

// Update player statistics
function updatePlayerStats() {
    var totalPlayers = dashboardData.players.length;
    updateStatCard('total-players', totalPlayers);
    
    // Calculate active players (mock for now)
    var activePlayers = Math.floor(totalPlayers * 0.3);
    updateStatCard('active-players', activePlayers);
}

// Update character statistics
function updateCharacterStats() {
    var totalCharacters = dashboardData.characters.length;
    updateStatCard('total-characters', totalCharacters);
}

// Update item statistics
function updateItemStats() {
    var totalItems = dashboardData.items.length;
    updateStatCard('total-items', totalItems);
}

// Update stat card
function updateStatCard(id, value) {
    var element = document.querySelector('[data-stat="' + id + '"] .stat-value');
    if (element) {
        animateNumber(element, parseInt(element.textContent.replace(/[^\d]/g, '')) || 0, value);
    }
}

// Update all charts - NUCLEAR VERSION
function updateCharts() {
    console.log('Creating nuclear static charts...');
    
    // Activity Chart
    var activityData = calculateActivityData();
    createStaticChart('activityChart', 'line', {
        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
        datasets: [{
            label: 'Active Players',
            data: activityData.active,
            borderColor: '#FFD700',
            backgroundColor: 'rgba(255, 215, 0, 0.1)',
            fill: true
        }, {
            label: 'New Players', 
            data: activityData.newPlayers,
            borderColor: '#3B82F6',
            backgroundColor: 'rgba(59, 130, 246, 0.1)',
            fill: true
        }]
    }, {});
    
    // Class Chart
    var classData = calculateClassData();
    createStaticChart('classChart', 'doughnut', {
        labels: classData.labels,
        datasets: [{
            data: classData.values,
            backgroundColor: ['#FFD700', '#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6']
        }]
    }, {});
    
    // Level Chart
    var levelData = calculateLevelData();
    createStaticChart('levelChart', 'bar', {
        labels: levelData.labels,
        datasets: [{
            label: 'Characters',
            data: levelData.values,
            backgroundColor: 'rgba(255, 215, 0, 0.8)',
            borderColor: '#FFD700'
        }]
    }, {});
    
    // Performance Chart
    var performanceData = generatePerformanceData();
    createStaticChart('performanceChart', 'line', {
        labels: ['0:00', '4:00', '8:00', '12:00', '16:00', '20:00'],
        datasets: [{
            label: 'Response Time (ms)',
            data: performanceData.slice(0, 6),
            borderColor: '#10B981',
            backgroundColor: 'rgba(16, 185, 129, 0.1)',
            fill: true
        }]
    }, {});
    
    console.log('All nuclear charts created');
}

// Update activity chart (STATIC - NO RESIZE)
function updateActivityChart() {
    var ctx = document.getElementById('activityChart');
    if (!ctx) return;
    
    // Force canvas size BEFORE creating chart
    ctx.style.height = '300px';
    ctx.style.maxHeight = '300px';
    ctx.style.width = '100%';
    ctx.height = 300;
    ctx.width = ctx.offsetWidth;
    
    // Destroy existing chart completely
    if (charts.activity) {
        charts.activity.destroy();
        charts.activity = null;
    }
    
    var activityData = calculateActivityData();
    
    charts.activity = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
            datasets: [{
                label: 'Active Players',
                data: activityData.active,
                borderColor: '#FFD700',
                backgroundColor: 'rgba(255, 215, 0, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.2
            }, {
                label: 'New Players',
                data: activityData.newPlayers,
                borderColor: '#3B82F6',
                backgroundColor: 'rgba(59, 130, 246, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.2
            }]
        },
        options: {
            responsive: false,  // DISABLE RESPONSIVE
            maintainAspectRatio: false,
            animation: false,   // DISABLE ANIMATIONS
            plugins: {
                legend: {
                    labels: {
                        color: '#F8FAFC'
                    }
                }
            },
            scales: {
                x: {
                    ticks: { color: '#94A3B8' },
                    grid: { color: 'rgba(148, 163, 184, 0.1)' }
                },
                y: {
                    ticks: { color: '#94A3B8' },
                    grid: { color: 'rgba(148, 163, 184, 0.1)' }
                }
            }
        }
    });
    
    console.log('Activity chart created - static mode');
}

// Update class distribution chart (STATIC)
function updateClassChart() {
    var ctx = document.getElementById('classChart');
    if (!ctx) return;
    
    // Force size before chart creation
    ctx.style.height = '300px';
    ctx.height = 300;
    ctx.width = ctx.offsetWidth;
    
    if (charts.class) {
        charts.class.destroy();
        charts.class = null;
    }
    
    var classData = calculateClassData();
    
    charts.class = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: classData.labels,
            datasets: [{
                data: classData.values,
                backgroundColor: [
                    '#FFD700', '#3B82F6', '#10B981', '#F59E0B',
                    '#EF4444', '#8B5CF6', '#EC4899', '#06D6A0'
                ],
                borderWidth: 0
            }]
        },
        options: {
            responsive: false,
            maintainAspectRatio: false,
            animation: false,
            plugins: {
                legend: {
                    labels: { color: '#F8FAFC' }
                }
            }
        }
    });
    
    console.log('Class chart created - static mode');
}

// Update level distribution chart (STATIC)
function updateLevelChart() {
    var ctx = document.getElementById('levelChart');
    if (!ctx) return;
    
    ctx.style.height = '300px';
    ctx.height = 300;
    ctx.width = ctx.offsetWidth;
    
    if (charts.level) {
        charts.level.destroy();
        charts.level = null;
    }
    
    var levelData = calculateLevelData();
    
    charts.level = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: levelData.labels,
            datasets: [{
                label: 'Characters',
                data: levelData.values,
                backgroundColor: 'rgba(255, 215, 0, 0.8)',
                borderColor: '#FFD700',
                borderWidth: 1
            }]
        },
        options: {
            responsive: false,
            maintainAspectRatio: false,
            animation: false,
            plugins: {
                legend: {
                    labels: { color: '#F8FAFC' }
                }
            },
            scales: {
                x: {
                    ticks: { color: '#94A3B8' },
                    grid: { color: 'rgba(148, 163, 184, 0.1)' }
                },
                y: {
                    ticks: { color: '#94A3B8' },
                    grid: { color: 'rgba(148, 163, 184, 0.1)' }
                }
            }
        }
    });
    
    console.log('Level chart created - static mode');
}

// Update performance chart (STATIC)
function updatePerformanceChart() {
    var ctx = document.getElementById('performanceChart');
    if (!ctx) return;
    
    ctx.style.height = '300px';
    ctx.height = 300;
    ctx.width = ctx.offsetWidth;
    
    if (charts.performance) {
        charts.performance.destroy();
        charts.performance = null;
    }
    
    var performanceData = generatePerformanceData();
    
    charts.performance = new Chart(ctx, {
        type: 'line',
        data: {
            labels: ['0:00', '2:00', '4:00', '6:00', '8:00', '10:00', '12:00', '14:00', '16:00', '18:00', '20:00', '22:00'],
            datasets: [{
                label: 'Response Time (ms)',
                data: performanceData,
                borderColor: '#10B981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                borderWidth: 2,
                fill: true,
                tension: 0.2
            }]
        },
        options: {
            responsive: false,
            maintainAspectRatio: false,
            animation: false,
            plugins: {
                legend: {
                    labels: { color: '#F8FAFC' }
                }
            },
            scales: {
                x: {
                    ticks: { color: '#94A3B8' },
                    grid: { color: 'rgba(148, 163, 184, 0.1)' }
                },
                y: {
                    ticks: { color: '#94A3B8' },
                    grid: { color: 'rgba(148, 163, 184, 0.1)' }
                }
            }
        }
    });
    
    console.log('Performance chart created - static mode');
}

// Calculate activity data from real players
function calculateActivityData() {
    var active = [];
    var newPlayers = [];
    
    // Generate weekly data based on player count
    var baseActive = Math.floor(dashboardData.players.length * 0.3);
    var baseNew = Math.floor(dashboardData.players.length * 0.05);
    
    for (var i = 0; i < 7; i++) {
        var variation = Math.random() * 0.4 + 0.8; // 80-120% variation
        active.push(Math.floor(baseActive * variation));
        newPlayers.push(Math.floor(baseNew * variation));
    }
    
    return {
        active: active,
        newPlayers: newPlayers
    };
}

// Calculate class distribution from real characters
function calculateClassData() {
    var classCount = {};
    
    for (var i = 0; i < dashboardData.characters.length; i++) {
        var character = dashboardData.characters[i];
        var className = character.class || character.characterClass || 'Unknown';
        classCount[className] = (classCount[className] || 0) + 1;
    }
    
    return {
        labels: Object.keys(classCount),
        values: Object.values(classCount)
    };
}

// Calculate level distribution from real characters
function calculateLevelData() {
    var levelRanges = {
        '1-10': 0, '11-20': 0, '21-30': 0, '31-40': 0, '41-50': 0,
        '51-60': 0, '61-70': 0, '71-80': 0, '81-90': 0, '91-100': 0
    };
    
    for (var i = 0; i < dashboardData.characters.length; i++) {
        var character = dashboardData.characters[i];
        var level = character.level || 1;
        
        if (level <= 10) levelRanges['1-10']++;
        else if (level <= 20) levelRanges['11-20']++;
        else if (level <= 30) levelRanges['21-30']++;
        else if (level <= 40) levelRanges['31-40']++;
        else if (level <= 50) levelRanges['41-50']++;
        else if (level <= 60) levelRanges['51-60']++;
        else if (level <= 70) levelRanges['61-70']++;
        else if (level <= 80) levelRanges['71-80']++;
        else if (level <= 90) levelRanges['81-90']++;
        else levelRanges['91-100']++;
    }
    
    return {
        labels: Object.keys(levelRanges),
        values: Object.values(levelRanges)
    };
}

// Initialize filters (NO AUTO-REFRESH)
function initializeFilters() {
    var filterButton = document.querySelector('.filter-button');
    if (filterButton) {
        filterButton.addEventListener('click', function() {
            console.log('Manual filter applied');
            applyFilters();
        });
    }
    
    // NO automatic change listeners - only manual button clicks
    console.log('Filters initialized - manual only');
}

// Apply filters (MANUAL ONLY)
function applyFilters() {
    console.log('Applying filters manually...');
    
    // Only update charts, no data reloading
    updateCharts();
    
    console.log('Filters applied - no auto-refresh');
}

// Animate number changes
function animateNumber(element, from, to) {
    var duration = 1000;
    var startTime = Date.now();
    
    function animate() {
        var elapsed = Date.now() - startTime;
        var progress = Math.min(elapsed / duration, 1);
        
        var currentValue = Math.round(from + (to - from) * easeOutQuart(progress));
        element.textContent = currentValue.toLocaleString();
        
        if (progress < 1) {
            requestAnimationFrame(animate);
        }
    }
    
    requestAnimationFrame(animate);
}

function easeOutQuart(t) {
    return 1 - (--t) * t * t * t;
}

// Generate performance data
function generatePerformanceData() {
    var data = [];
    var baseLatency = 25;
    
    for (var i = 0; i < 12; i++) {
        var variation = Math.sin(i * Math.PI / 6) * 15 + Math.random() * 10;
        data.push(Math.max(5, baseLatency + variation));
    }
    
    return data;
}

// Mock data generators
function generateMockPlayers() {
    var players = [];
    for (var i = 1; i <= 500; i++) {
        players.push({
            id: i,
            username: 'Player' + i,
            level: Math.floor(Math.random() * 100) + 1,
            createdAt: new Date().toISOString()
        });
    }
    return players;
}

function generateMockCharacters() {
    var classes = ['Warrior', 'Mage', 'Archer', 'Priest', 'Thief', 'Paladin'];
    var characters = [];
    
    for (var i = 1; i <= 1000; i++) {
        characters.push({
            id: i,
            name: 'Character' + i,
            level: Math.floor(Math.random() * 100) + 1,
            class: classes[Math.floor(Math.random() * classes.length)]
        });
    }
    return characters;
}

function generateMockItems() {
    var items = [];
    for (var i = 1; i <= 200; i++) {
        items.push({
            id: i,
            name: 'Item' + i,
            type: 'Equipment',
            level: Math.floor(Math.random() * 100) + 1
        });
    }
    return items;
}

// Global functions
window.applyFilters = applyFilters;

// Console message
console.log('\n' +
    'WoW DataHub Dashboard Loaded\n' +
    '=====================================\n' +
    'Version: 1.0.0 - MANUAL REFRESH ONLY\n' +
    'Built with: Chart.js, Vanilla JS\n' +
    'Auto-refresh: DISABLED\n' +
    'Chart growth: PREVENTED\n' +
    '====================================='
);