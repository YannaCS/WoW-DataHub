-- Update schema name and add ETL tables
USE WoWDataHub;

-- Add ETL tracking tables
CREATE TABLE IF NOT EXISTS ETLMetadata (
    etlJobId VARCHAR(255) PRIMARY KEY,
    jobName VARCHAR(255) NOT NULL,
    lastRunTimestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    sourceSystem VARCHAR(255),
    recordsProcessed INT DEFAULT 0,
    status ENUM('SUCCESS', 'FAILED', 'RUNNING') DEFAULT 'RUNNING',
    errorMessage TEXT
);

-- Add external data staging tables
CREATE TABLE IF NOT EXISTS StagingRealms (
    realmId INT PRIMARY KEY,
    realmName VARCHAR(255),
    region VARCHAR(50),
    timezone VARCHAR(50),
    type VARCHAR(50),
    population VARCHAR(50),
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Add realm information to Characters (optional)
ALTER TABLE Characters ADD COLUMN IF NOT EXISTS realmId INT;

-- Add auction house data table
CREATE TABLE IF NOT EXISTS AuctionHouseData (
    auctionId BIGINT PRIMARY KEY,
    realmId INT,
    itemId INT,
    quantity INT,
    unitPrice DECIMAL(10,2),
    timeLeft VARCHAR(50),
    lastUpdated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (realmId) REFERENCES StagingRealms(realmId) ON DELETE CASCADE,
    FOREIGN KEY (itemId) REFERENCES Items(itemID) ON DELETE CASCADE
);

-- Insert sample realm data
INSERT IGNORE INTO StagingRealms (realmId, realmName, region, timezone, type, population) VALUES
(1, 'Stormrage', 'US', 'America/New_York', 'PvE', 'High'),
(2, 'Tichondrius', 'US', 'America/Los_Angeles', 'PvP', 'High'),
(3, 'Illidan', 'US', 'America/Chicago', 'PvP', 'Full'),
(4, 'Mal\'Ganis', 'US', 'America/Chicago', 'PvP', 'High'),
(5, 'Area-52', 'US', 'America/New_York', 'PvE', 'Full');