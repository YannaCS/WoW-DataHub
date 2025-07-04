package game;

import java.sql.Connection;
import java.sql.SQLException;

import game.dal.ConnectionManager;
import game.etl.DefaultDataETL;

public class Driver {
	
	public static void main(String[] args) {
		try {
			resetSchema();
			
			// Run Default Data ETL to load static game data
			System.out.println("🎯 Loading default game data...");
			DefaultDataETL defaultETL = new DefaultDataETL();
			try (Connection cxn = ConnectionManager.getConnection()) {
				defaultETL.runDefaultDataETL(cxn);
			}
			
			System.out.println("🎉 Default WoW Data ETL completed successfully!");
			System.out.println("💡 Use the web interface to run Dynamic ETL for players and characters.");
			
		} catch (SQLException e) {
			System.out.print("SQL Exception: ");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	// schema with constraints and triggers
	// trigger defined in /src/main/java/game/sql/BusinessRuleTriggers.java
	public static void resetSchema() throws SQLException {
	    try (Connection cxn = ConnectionManager.getSchemalessConnection()) {
	        cxn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS WoWDataHub;");
	        cxn.createStatement().executeUpdate("CREATE SCHEMA WoWDataHub;");
	    }
	    
	    try (Connection cxn = ConnectionManager.getConnection()) {
	        // 1. Create Players table
	    	cxn.createStatement().executeUpdate("""
	    		    CREATE TABLE `Players` (
	    		        `playerID` INTEGER AUTO_INCREMENT,
	    		        `firstName` VARCHAR(255) NOT NULL,
	    		        `lastName` VARCHAR(255) NOT NULL,
	    		        `emailAddress` VARCHAR(255) NOT NULL,
	    		        `lastActiveDateTime` DATETIME DEFAULT CURRENT_TIMESTAMP,
	    		        CONSTRAINT pk_players PRIMARY KEY (playerID),
	    		        CONSTRAINT uk_players_email UNIQUE (emailAddress)
	    		    );
	    		""");
	        
	        // 2. Create Clans table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Clans` (
	                `clanName` VARCHAR(255),
	                `race` enum('dwarf', 'elf', 'goblin', 'human', 'orc') NOT NULL,
	                CONSTRAINT pk_clans PRIMARY KEY (clanName)
	            );
	        """);
	        
	        // 3. Create Items table with constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Items` (
	                `itemID` INTEGER AUTO_INCREMENT,
	                `itemName` VARCHAR(255) NOT NULL,
	                `level` INTEGER NOT NULL,
	                `maxStackSize` INTEGER NOT NULL,
	                `price` DECIMAL(10,2),
	                CONSTRAINT pk_item PRIMARY KEY (`itemID`),
	                CONSTRAINT chk_positive_level CHECK (level > 0),
	                CONSTRAINT chk_positive_stack_size CHECK (maxStackSize > 0),
	                CONSTRAINT chk_non_negative_price CHECK (price IS NULL OR price >= 0)
	            );
	        """);
	        
	        // 4. Create Equipments table with constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Equipments` (
	                `itemID` INTEGER NOT NULL,
	                `requiredLevel` INTEGER NOT NULL,
	                CONSTRAINT pk_equipment PRIMARY KEY (`itemID`),
	                CONSTRAINT fk_equipment_itemID FOREIGN KEY (`itemID`)
	                    REFERENCES Items(`itemID`)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT chk_positive_required_level CHECK (requiredLevel > 0)
	            );
	        """);
	        
	        // 5. Create Gears table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Gears` (
	                `itemID` INTEGER,
	                CONSTRAINT pk_gear PRIMARY KEY (`itemID`),
	                CONSTRAINT fk_gear_itemID FOREIGN KEY (`itemID`)
	                    REFERENCES Equipments(`itemID`)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE
	            );
	        """);
	        
	        // 6. Create Weapons table with constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Weapons` (
	                `itemID` INTEGER,
	                `wearableJob` VARCHAR(255) NOT NULL,
	                `damage` INTEGER NOT NULL,
	                CONSTRAINT pk_weapon PRIMARY KEY (`itemID`),
	                CONSTRAINT fk_weapon_itemID FOREIGN KEY (`itemID`)
	                    REFERENCES Equipments(`itemID`)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT chk_positive_damage CHECK (damage > 0),
	                CONSTRAINT chk_valid_job CHECK (wearableJob IN 
	                    ('Warrior', 'Paladin', 'Hunter', 'Rogue', 'Priest', 'Death Knight', 
	                     'Shaman', 'Mage', 'Warlock', 'Monk', 'Druid', 'Demon Hunter'))
	            );
	        """);
	        
	        // 7. Create Characters table with business rule constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Characters` (
	                `charID` INTEGER AUTO_INCREMENT,
	                `playerID` INTEGER NOT NULL,
	                `firstName` VARCHAR(255) NOT NULL,
	                `lastName` VARCHAR(255) NOT NULL,
	                `clan` VARCHAR(255) NOT NULL,
	                `weaponWeared` INTEGER NOT NULL,
	                CONSTRAINT pk_characters PRIMARY KEY (charID),
	                CONSTRAINT uk_character_name UNIQUE (firstName, lastName),
	                CONSTRAINT fk_Char_playerID FOREIGN KEY (playerID)
	                    REFERENCES Players(playerID)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_Char_clan FOREIGN KEY (clan)
	                    REFERENCES Clans(clanName)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_Char_weapon FOREIGN KEY (weaponWeared)
	                    REFERENCES Weapons(itemID)
	                    ON UPDATE CASCADE
	                    ON DELETE RESTRICT
	            );
	        """);
	        
	        // 8. Create Statistics table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Statistics` (
	                `statsName` VARCHAR(255),
	                `description` TEXT NOT NULL,
	                CONSTRAINT pk_statistics PRIMARY KEY (statsName)
	            );
	        """);
	        
	        // 9. Create Currencies table with business constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Currencies` (
	                `currencyName` VARCHAR(255),
	                `cap` DECIMAL(10,2),
	                `weeklyCap` DECIMAL(10,2),
	                CONSTRAINT pk_Currencies PRIMARY KEY (`currencyName`),
	                CONSTRAINT chk_positive_cap CHECK (cap IS NULL OR cap > 0),
	                CONSTRAINT chk_positive_weekly_cap CHECK (weeklyCap IS NULL OR weeklyCap > 0),
	                CONSTRAINT chk_weekly_cap_reasonable CHECK (weeklyCap IS NULL OR cap IS NULL OR weeklyCap <= cap)
	            );
	        """);
	        
	        // 10. Create CharacterStatistics table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `CharacterStatistics` (
	                `charID` INTEGER,
	                `statistics` VARCHAR(255),
	                `value` INTEGER NOT NULL,
	                CONSTRAINT pk_CharStat_charID_stat PRIMARY KEY (`charID`, `statistics`),
	                CONSTRAINT fk_CharStat_charID FOREIGN KEY (charID)
	                    REFERENCES Characters(charID)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_CharStat_stat FOREIGN KEY (statistics)
	                    REFERENCES Statistics(statsName)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT chk_non_negative_stat_value CHECK (value >= 0)
	            );
	        """);
	        
	        // 11. Create EquipmentBonuse table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `EquipmentBonuse` (
	                `equipmentID` INTEGER NOT NULL,
	                `statistics` VARCHAR(255) NOT NULL,
	                `value` INTEGER NOT NULL,
	                CONSTRAINT pk_EquipmentBonuse PRIMARY KEY (`equipmentID`, `statistics`),
	                CONSTRAINT fk_EqBonuse_eqID FOREIGN KEY (equipmentID)
	                    REFERENCES Equipments(itemID)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_EqBonuse_stat FOREIGN KEY (statistics)
	                    REFERENCES Statistics(statsName)
	                    ON UPDATE CASCADE
	                    ON DELETE RESTRICT
	            );
	        """);
	        
	        // 12. Create Consumables table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `Consumables` (
	                `itemID` INTEGER NOT NULL,
	                `description` VARCHAR(255) NOT NULL,
	                CONSTRAINT pk_consumable PRIMARY KEY (`itemID`),
	                CONSTRAINT fk_consumable_itemID FOREIGN KEY (`itemID`)
	                    REFERENCES Items(`itemID`)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE
	            );
	        """);
	        
	        // 13. Create ConsumableItemBonuse table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `ConsumableItemBonuse` (
	                `itemID` INTEGER NOT NULL,
	                `statistics` VARCHAR(255) NOT NULL,
	                `bonusePercent` FLOAT NOT NULL,
	                `valueCap` INTEGER NOT NULL,
	                CONSTRAINT pk_ConsBonuse PRIMARY KEY (`itemID`, `statistics`),
	                CONSTRAINT fk_ConsBonuse_itemID FOREIGN KEY (itemID)
	                    REFERENCES Consumables(itemID)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_ConsBonuse_stat FOREIGN KEY (statistics)
	                    REFERENCES Statistics(statsName)
	                    ON UPDATE CASCADE
	                    ON DELETE RESTRICT,
	                CONSTRAINT chk_positive_bonus_percent CHECK (bonusePercent > 0),
	                CONSTRAINT chk_positive_value_cap CHECK (valueCap > 0)
	            );
	        """);
	        
	        // 14. Create JobsForGear table
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE JobsForGear (
	                gear INTEGER,
	                jobName VARCHAR(255),
	                CONSTRAINT pk_JobsForGear PRIMARY KEY (gear, jobName),
	                CONSTRAINT fk_JobsForGear_gear FOREIGN KEY (gear) 
	                    REFERENCES Gears (itemID)
	                    ON UPDATE CASCADE 
	                    ON DELETE CASCADE,
	                CONSTRAINT chk_valid_gear_job CHECK (jobName IN 
	                    ('Warrior', 'Paladin', 'Hunter', 'Rogue', 'Priest', 'Death Knight', 
	                     'Shaman', 'Mage', 'Warlock', 'Monk', 'Druid', 'Demon Hunter'))
	            );
	        """);
	        
	        // 15. Create CharacterUnlockedJob table with business constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE CharacterUnlockedJob (
	                charID INTEGER,
	                jobName VARCHAR(255),
	                jobLevel INTEGER,
	                XP INTEGER,
	                CONSTRAINT pk_CharacterUnlockedJob PRIMARY KEY (charID, jobName),
	                CONSTRAINT fk_CharacterUnlockedJob_charID FOREIGN KEY (charID)
	                    REFERENCES Characters (charID)
	                    ON UPDATE CASCADE 
	                    ON DELETE CASCADE,
	                CONSTRAINT chk_job_level CHECK (jobLevel IS NULL OR (jobLevel >= 1 AND jobLevel <= 100)),
	                CONSTRAINT chk_positive_xp CHECK (XP IS NULL OR XP >= 0),
	                CONSTRAINT chk_xp_requires_level CHECK (XP IS NULL OR jobLevel IS NOT NULL),
	                CONSTRAINT chk_valid_unlocked_job CHECK (jobName IN 
	                    ('Warrior', 'Paladin', 'Hunter', 'Rogue', 'Priest', 'Death Knight', 
	                     'Shaman', 'Mage', 'Warlock', 'Monk', 'Druid', 'Demon Hunter'))
	            );
	        """);
	        
	        // 16. Create CharacterWealth table with currency constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE `CharacterWealth` (
	                `charID` INTEGER,
	                `currencyName` VARCHAR(255),
	                `amount` DECIMAL(10,2) NOT NULL,
	                `weeklyAcquired` DECIMAL(10,2),
	                CONSTRAINT pk_characterWealth PRIMARY KEY (`charID`, `currencyName`),
	                CONSTRAINT fk_characterWealth_currency FOREIGN KEY (`currencyName`)
	                    REFERENCES `Currencies`(`currencyName`) 
	                    ON UPDATE CASCADE
	                    ON DELETE RESTRICT,
	                CONSTRAINT fk_characterWealth_charID FOREIGN KEY (`charID`) 
	                    REFERENCES `Characters` (`charID`)
	                    ON UPDATE CASCADE
	                    ON DELETE CASCADE,
	                CONSTRAINT chk_positive_amount CHECK (amount >= 0),
	                CONSTRAINT chk_positive_weekly CHECK (weeklyAcquired IS NULL OR weeklyAcquired >= 0)
	            );
	        """);
	        
	        // 17. Create Inventory table with quantity constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE Inventory (
	                charID INT,  
	                slotID INT,  
	                `instance` INT NOT NULL,  
	                quantity INT NOT NULL,  
	                CONSTRAINT pk_Inventory PRIMARY KEY (charID, slotID),
	                CONSTRAINT fk_Inventory_Characters FOREIGN KEY (charID)
	                    REFERENCES Characters(charID) 
	                    ON UPDATE CASCADE 
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_Inventory_Items FOREIGN KEY (instance)
	                    REFERENCES Items(itemID) 
	                    ON UPDATE CASCADE 
	                    ON DELETE RESTRICT,
	                CONSTRAINT chk_positive_quantity CHECK (quantity > 0),
	                CONSTRAINT chk_positive_slot CHECK (slotID > 0)
	            );
	        """);
	        
	        // 18. Create EquippedItems table with slot constraints
	        cxn.createStatement().executeUpdate("""
	            CREATE TABLE EquippedItems (
	                charID INT,  
	                equipPosition VARCHAR(255),
	                itemID INT NOT NULL,  
	                CONSTRAINT pk_EquippedItems PRIMARY KEY (charID, equipPosition),
	                CONSTRAINT fk_EquippedItems_Characters FOREIGN KEY (charID)
	                    REFERENCES Characters(charID) 
	                    ON UPDATE CASCADE 
	                    ON DELETE CASCADE,
	                CONSTRAINT fk_EquippedItems_Items FOREIGN KEY (itemID)
	                    REFERENCES Gears(itemID) 
	                    ON UPDATE CASCADE 
	                    ON DELETE RESTRICT,
	                CONSTRAINT chk_valid_equip_position CHECK (equipPosition IN 
	                    ('MAIN_HAND', 'HEAD', 'BODY', 'HANDS', 'LEGS', 'FEET', 
	                     'EARRING', 'NECKLACE', 'WRIST', 'RING'))
	            );
	        """);
	    }
	    
	    // Create business rule triggers and views
	    try (Connection cxn = ConnectionManager.getConnection()) {
	        game.sql.BusinessRuleTriggers.createAllTriggers(cxn);
	        createDatabaseViews(cxn);
	    }
	    
	    
	    System.out.println("Database schema 'WoWDataHub' created successfully with business rule constraints!");
	}
	public static void createDatabaseViews(Connection cxn) throws SQLException {
	    System.out.println("📋 Creating database views for analytics...");
	    
	    // Drop existing views if they exist (case-insensitive cleanup)
	    String[] viewNames = {
	        "DailyActivePlayersView", "JobDistributionView", "ClanDistributionView",
	        "CurrencyStatsView", "ItemTypeDistributionView", "TopPlayersByLevelView", 
	        "TopPlayersByWealthView", "OverallStatsView", "CharacterInventoryDetailsView"
	    };
	    
	    for (String viewName : viewNames) {
	        try {
	            cxn.createStatement().executeUpdate("DROP VIEW IF EXISTS " + viewName);
	        } catch (SQLException e) {
	            // Ignore if view doesn't exist
	        }
	    }
	    
	    try {
	        // 1. Overall Statistics View (Create this first as it's needed immediately)
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW OverallStatsView AS
	            SELECT 
	                (SELECT COUNT(*) FROM Players) as total_players,
	                (SELECT COUNT(*) FROM Characters) as total_characters,
	                (SELECT COUNT(*) FROM Weapons) as total_weapons,
	                (SELECT COUNT(*) FROM Gears) as total_gears,
	                (SELECT COUNT(*) FROM Consumables) as total_consumables,
	                (SELECT COUNT(*) FROM Clans) as total_clans;
	        """);
	        System.out.println("✅ Created OverallStatsView");
	        
	        // 2. Daily Active Players View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW DailyActivePlayersView AS
	            SELECT 
	                DATE(lastActiveDateTime) as activity_date,
	                COUNT(*) as active_count
	            FROM Players 
	            WHERE lastActiveDateTime >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
	            GROUP BY DATE(lastActiveDateTime)
	            ORDER BY activity_date;
	        """);
	        System.out.println("✅ Created DailyActivePlayersView");
	        
	        // 3. Job Distribution View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW JobDistributionView AS
	            SELECT 
	                w.wearableJob as job_name,
	                COUNT(*) as character_count,
	                ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM Characters), 2) as percentage
	            FROM Characters c
	            JOIN Weapons w ON c.weaponWeared = w.itemID
	            GROUP BY w.wearableJob
	            ORDER BY character_count DESC;
	        """);
	        System.out.println("✅ Created JobDistributionView");
	        
	        // 4. Clan Distribution View  
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW ClanDistributionView AS
	            SELECT 
	                cl.clanName as clan_name,
	                cl.race,
	                COUNT(c.charID) as character_count,
	                ROUND(COUNT(c.charID) * 100.0 / (SELECT COUNT(*) FROM Characters), 2) as percentage
	            FROM Clans cl
	            LEFT JOIN Characters c ON cl.clanName = c.clan
	            GROUP BY cl.clanName, cl.race
	            HAVING character_count > 0
	            ORDER BY character_count DESC;
	        """);
	        System.out.println("✅ Created ClanDistributionView");
	        
	        // 5. Currency Statistics View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW CurrencyStatsView AS
	            SELECT 
	                curr.currencyName as currency_name,
	                curr.cap,
	                curr.weeklyCap as weekly_cap,
	                COUNT(cw.charID) as players_with_currency,
	                COALESCE(AVG(cw.amount), 0) as avg_amount,
	                COALESCE(MAX(cw.amount), 0) as max_amount,
	                COALESCE(SUM(cw.amount), 0) as total_in_circulation
	            FROM Currencies curr
	            LEFT JOIN CharacterWealth cw ON curr.currencyName = cw.currencyName
	            GROUP BY curr.currencyName, curr.cap, curr.weeklyCap
	            ORDER BY total_in_circulation DESC;
	        """);
	        System.out.println("✅ Created CurrencyStatsView");
	        
	        // 6. Item Type Distribution View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW ItemTypeDistributionView AS
	            SELECT 'Weapons' as item_type, COUNT(*) as count FROM Weapons
	            UNION ALL
	            SELECT 'Gears' as item_type, COUNT(*) as count FROM Gears  
	            UNION ALL
	            SELECT 'Consumables' as item_type, COUNT(*) as count FROM Consumables;
	        """);
	        System.out.println("✅ Created ItemTypeDistributionView");
	        
	        // 7. Top Players by Level View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW TopPlayersByLevelView AS
	            SELECT 
	                CONCAT(c.firstName, ' ', c.lastName) as character_name,
	                CONCAT(p.firstName, ' ', p.lastName) as player_name,
	                MAX(COALESCE(cuj.jobLevel, 1)) as max_level,
	                w.wearableJob as current_job,
	                cl.race
	            FROM Characters c
	            JOIN Players p ON c.playerID = p.playerID
	            JOIN Clans cl ON c.clan = cl.clanName
	            JOIN Weapons w ON c.weaponWeared = w.itemID
	            LEFT JOIN CharacterUnlockedJob cuj ON c.charID = cuj.charID
	            GROUP BY c.charID, c.firstName, c.lastName, p.firstName, p.lastName, w.wearableJob, cl.race
	            ORDER BY max_level DESC, c.firstName
	            LIMIT 10;
	        """);
	        System.out.println("✅ Created TopPlayersByLevelView");
	        
	        // 8. Top Players by Wealth View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW TopPlayersByWealthView AS
	            SELECT 
	                CONCAT(c.firstName, ' ', c.lastName) as character_name,
	                SUM(cw.amount) as total_wealth,
	                COUNT(DISTINCT cw.currencyName) as currency_types
	            FROM Characters c
	            LEFT JOIN CharacterWealth cw ON c.charID = cw.charID
	            GROUP BY c.charID, c.firstName, c.lastName
	            HAVING total_wealth IS NOT NULL
	            ORDER BY total_wealth DESC
	            LIMIT 10;
	        """);
	        System.out.println("✅ Created TopPlayersByWealthView");
	        
	        // 9. Character Inventory Details View
	        cxn.createStatement().executeUpdate("""
	            CREATE VIEW CharacterInventoryDetailsView AS
	            SELECT 
	                inv.charID,
	                inv.slotID,
	                inv.instance as itemID,
	                inv.quantity,
	                i.itemName,
	                i.level,
	                CASE 
	                    WHEN w.itemID IS NOT NULL THEN 'Weapon'
	                    WHEN g.itemID IS NOT NULL THEN 'Gear' 
	                    WHEN co.itemID IS NOT NULL THEN 'Consumable'
	                    ELSE 'Unknown'
	                END as item_type
	            FROM Inventory inv
	            JOIN Items i ON inv.instance = i.itemID
	            LEFT JOIN Weapons w ON i.itemID = w.itemID
	            LEFT JOIN Gears g ON i.itemID = g.itemID  
	            LEFT JOIN Consumables co ON i.itemID = co.itemID;
	        """);
	        System.out.println("✅ Created CharacterInventoryDetailsView");
	        
	    } catch (SQLException e) {
	        System.err.println("❌ Error creating views: " + e.getMessage());
	        e.printStackTrace();
	        throw e;
	    }
	    
	    System.out.println("✅ All database views created successfully!");
	}
}