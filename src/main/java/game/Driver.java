package game;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import game.dal.*;
import game.etl.*;
import game.etl.dto.*;

public class Driver {
    
    private static final Logger logger = Logger.getLogger(Driver.class.getName());
    
    public static void main(String[] args) {
        try {
            logger.info("Starting WoW DataHub ETL Process");
            
            // 1. Reset and create database schema
            resetSchema();
            
            // 2. Run ETL process with WoW data
            runWoWETL();
            
            // 3. Display loaded data summary
            displayDataSummary();
            
            logger.info("ETL Process completed successfully");
            
        } catch (SQLException e) {
            logger.severe("SQL Exception occurred: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            logger.severe("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
    }
    
    /**
     * Run the ETL process to load WoW data
     */
    public static void runWoWETL() throws SQLException {
        logger.info("Starting WoW ETL process...");
        
        // Initialize ETL components
        WoWDataSimulator dataSimulator = new WoWDataSimulator();
        WoWETLProcessor etlProcessor = new WoWETLProcessor();
        
        // Extract: Get WoW character data (simulated)
        List<WoWCharacterData> wowCharacters = dataSimulator.fetchFromWoWAPI();
        logger.info("Extracted " + wowCharacters.size() + " characters from WoW API");
        
        // Transform & Load: Process the data into our database
        etlProcessor.processWoWData(wowCharacters);
        
        logger.info("WoW ETL process completed");
    }
    
    /**
     * Display summary of loaded data
     */
    public static void displayDataSummary() throws SQLException {
        try (Connection cxn = ConnectionManager.getConnection()) {
            logger.info("=== WoW DATAHUB SUMMARY ===");
            
            // Count records in each table
            var stmt = cxn.createStatement();
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM Players")) {
                rs.next();
                logger.info("Players: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM Characters")) {
                rs.next();
                logger.info("Characters: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM Clans")) {
                rs.next();
                logger.info("Clans: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM Weapons")) {
                rs.next();
                logger.info("Weapons: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM Currencies")) {
                rs.next();
                logger.info("Currencies: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM Statistics")) {
                rs.next();
                logger.info("Statistics: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM CharacterWealth")) {
                rs.next();
                logger.info("Character Wealth Records: " + rs.getInt(1));
            }
            
            try (var rs = stmt.executeQuery("SELECT COUNT(*) FROM CharacterUnlockedJob")) {
                rs.next();
                logger.info("Character Jobs: " + rs.getInt(1));
            }
            
            logger.info("============================");
        }
    }
    
    /**
     * Reset database schema (removes all existing data)
     */
    public static void resetSchema() throws SQLException {
        logger.info("Resetting database schema...");
        
        try (Connection cxn = ConnectionManager.getSchemalessConnection()) {
            cxn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS CS5200Project;");
            cxn.createStatement().executeUpdate("CREATE SCHEMA CS5200Project;");
            logger.info("Schema reset completed");
        }
        
        createTables();
    }
    
    /**
     * Create all database tables with proper relationships
     */
    private static void createTables() throws SQLException {
        try (Connection cxn = ConnectionManager.getConnection()) {
            logger.info("Creating database tables...");
            
            // 1. Create Items table (base for all items)
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Items (
                    itemID INTEGER NOT NULL AUTO_INCREMENT,
                    itemName VARCHAR(255) NOT NULL,
                    level INTEGER NOT NULL,
                    maxStackSize INTEGER NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    CONSTRAINT pk_items PRIMARY KEY (itemID)
                );
            """);
            
            // 2. Create AvailableJobs table (for job validation)
            cxn.createStatement().executeUpdate("""
                CREATE TABLE AvailableJobs (
                    jobName VARCHAR(255) NOT NULL,
                    description TEXT,
                    CONSTRAINT pk_jobs PRIMARY KEY (jobName)
                );
            """);
            
            // Insert default WoW jobs
            var jobStmt = cxn.prepareStatement("INSERT INTO AvailableJobs (jobName, description) VALUES (?, ?)");
            String[] jobs = {"Warrior", "Paladin", "Death Knight", "Hunter", "Rogue", "Priest", 
                           "Shaman", "Mage", "Warlock", "Monk", "Druid", "Demon Hunter", "Evoker", "Adventurer"};
            
            for (String job : jobs) {
                jobStmt.setString(1, job);
                jobStmt.setString(2, "WoW Class: " + job);
                jobStmt.executeUpdate();
            }
            
            // 3. Create Statistics table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Statistics (
                    statsName VARCHAR(255) NOT NULL,
                    description TEXT,
                    CONSTRAINT pk_statistics PRIMARY KEY (statsName)
                );
            """);
            
            // 4. Create Currencies table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Currencies (
                    currencyName VARCHAR(255) NOT NULL,
                    cap FLOAT NOT NULL,
                    weeklyCap FLOAT NOT NULL,
                    CONSTRAINT pk_currencies PRIMARY KEY (currencyName)
                );
            """);
            
            // 5. Create Players table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Players (
                    playerID INTEGER NOT NULL AUTO_INCREMENT,
                    firstName VARCHAR(255) NOT NULL,
                    lastName VARCHAR(255) NOT NULL,
                    emailAddress VARCHAR(255) NOT NULL UNIQUE,
                    CONSTRAINT pk_players PRIMARY KEY (playerID)
                );
            """);
            
            // 6. Create Clans table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Clans (
                    clanName VARCHAR(255) NOT NULL,
                    race ENUM('dwarf', 'elf', 'goblin', 'human', 'orc') NOT NULL,
                    CONSTRAINT pk_clans PRIMARY KEY (clanName)
                );
            """);
            
            // 7. Create Equipments table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Equipments (
                    itemID INTEGER NOT NULL,
                    requiredLevel INTEGER NOT NULL,
                    CONSTRAINT pk_equipment PRIMARY KEY (itemID),
                    CONSTRAINT fk_equipment_itemID FOREIGN KEY (itemID)
                        REFERENCES Items(itemID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                );
            """);
            
            // 8. Create Gears table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Gears (
                    itemID INTEGER,
                    CONSTRAINT pk_gear PRIMARY KEY (itemID),
                    CONSTRAINT fk_gear_itemID FOREIGN KEY (itemID)
                        REFERENCES Equipments(itemID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE
                );
            """);
            
            // 9. Create Weapons table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Weapons (
                    itemID INTEGER,
                    wearableJob VARCHAR(255) NOT NULL,
                    damage INTEGER NOT NULL,
                    CONSTRAINT pk_weapon PRIMARY KEY (itemID),
                    CONSTRAINT fk_weapon_itemID FOREIGN KEY (itemID)
                        REFERENCES Equipments(itemID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_weapon_job FOREIGN KEY (wearableJob)
                        REFERENCES AvailableJobs(jobName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            // 10. Create Characters table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE Characters (
                    charID INTEGER NOT NULL AUTO_INCREMENT,
                    playerID INTEGER NOT NULL,
                    firstName VARCHAR(255) NOT NULL,
                    lastName VARCHAR(255),
                    clan VARCHAR(255) NOT NULL,
                    weaponWeared INTEGER NOT NULL,
                    CONSTRAINT pk_characters PRIMARY KEY (charID),
                    CONSTRAINT fk_characters_player FOREIGN KEY (playerID)
                        REFERENCES Players(playerID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_characters_clan FOREIGN KEY (clan)
                        REFERENCES Clans(clanName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT,
                    CONSTRAINT fk_characters_weapon FOREIGN KEY (weaponWeared)
                        REFERENCES Weapons(itemID)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            // 11. Create CharacterWealth table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE CharacterWealth (
                    charID INTEGER NOT NULL,
                    currencyName VARCHAR(255) NOT NULL,
                    amount FLOAT NOT NULL,
                    weeklyAcquired FLOAT NOT NULL,
                    CONSTRAINT pk_wealth PRIMARY KEY (charID, currencyName),
                    CONSTRAINT fk_wealth_character FOREIGN KEY (charID)
                        REFERENCES Characters(charID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_wealth_currency FOREIGN KEY (currencyName)
                        REFERENCES Currencies(currencyName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            // 12. Create CharacterUnlockedJob table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE CharacterUnlockedJob (
                    charID INTEGER NOT NULL,
                    jobName VARCHAR(255) NOT NULL,
                    jobLevel INTEGER NOT NULL,
                    XP INTEGER NOT NULL,
                    CONSTRAINT pk_char_job PRIMARY KEY (charID, jobName),
                    CONSTRAINT fk_char_job_character FOREIGN KEY (charID)
                        REFERENCES Characters(charID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_char_job_job FOREIGN KEY (jobName)
                        REFERENCES AvailableJobs(jobName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            // 13. Create CharacterStatistics table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE CharacterStatistics (
                    charID INTEGER NOT NULL,
                    statistics VARCHAR(255) NOT NULL,
                    value INTEGER NOT NULL,
                    CONSTRAINT pk_char_stats PRIMARY KEY (charID, statistics),
                    CONSTRAINT fk_char_stats_character FOREIGN KEY (charID)
                        REFERENCES Characters(charID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_char_stats_stat FOREIGN KEY (statistics)
                        REFERENCES Statistics(statsName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            // 14. Create EquipmentBonuse table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE EquipmentBonuse (
                    equipmentID INTEGER NOT NULL,
                    statistics VARCHAR(255) NOT NULL,
                    value INTEGER NOT NULL,
                    CONSTRAINT pk_equipment_bonus PRIMARY KEY (equipmentID, statistics),
                    CONSTRAINT fk_eq_bonus_equipment FOREIGN KEY (equipmentID)
                        REFERENCES Equipments(itemID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_eq_bonus_stat FOREIGN KEY (statistics)
                        REFERENCES Statistics(statsName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            // 15. Create JobsForGear table
            cxn.createStatement().executeUpdate("""
                CREATE TABLE JobsForGear (
                    gear INTEGER NOT NULL,
                    jobName VARCHAR(255) NOT NULL,
                    CONSTRAINT pk_jobs_gear PRIMARY KEY (gear, jobName),
                    CONSTRAINT fk_jobs_gear_gear FOREIGN KEY (gear)
                        REFERENCES Gears(itemID)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
                    CONSTRAINT fk_jobs_gear_job FOREIGN KEY (jobName)
                        REFERENCES AvailableJobs(jobName)
                        ON UPDATE CASCADE
                        ON DELETE RESTRICT
                );
            """);
            
            logger.info("All " + 15 + " tables created successfully with proper relationships");
        }
    }
}