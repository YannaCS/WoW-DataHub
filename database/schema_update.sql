-- Enhanced schema with ETL tracking and external data integration

-- Add ETL tracking tables
CREATE TABLE ETLMetadata (
    etlJobId VARCHAR(255) PRIMARY KEY,
    jobName VARCHAR(255) NOT NULL,
    lastRunTimestamp TIMESTAMP,
    sourceSystem VARCHAR(255),
    recordsProcessed INT,
    status ENUM('SUCCESS', 'FAILED', 'RUNNING'),
    errorMessage TEXT
);

-- Add external data staging tables
CREATE TABLE StagingRealms (
    realmId INT PRIMARY KEY,
    realmName VARCHAR(255),
    region VARCHAR(50),
    timezone VARCHAR(50),
    type VARCHAR(50),
    population VARCHAR(50),
    lastUpdated TIMESTAMP
);

-- Modify Characters table to include realm information
ALTER TABLE Characters ADD COLUMN realmId INT;
ALTER TABLE Characters ADD CONSTRAINT fk_Characters_realm 
    FOREIGN KEY (realmId) REFERENCES StagingRealms(realmId);

-- Add auction house data table
CREATE TABLE AuctionHouseData (
    auctionId BIGINT PRIMARY KEY,
    realmId INT,
    itemId INT,
    quantity INT,
    unitPrice DECIMAL(10,2),
    timeLeft VARCHAR(50),
    lastUpdated TIMESTAMP,
    FOREIGN KEY (realmId) REFERENCES StagingRealms(realmId),
    FOREIGN KEY (itemId) REFERENCES Items(itemID)
);