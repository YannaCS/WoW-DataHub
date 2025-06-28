package game;

import java.sql.Connection;
import java.sql.SQLException;

import game.dal.ConnectionManager;
import game.etl.WoWDataETL;

public class Driver {
	
	public static void main(String[] args) {
		try {
			resetSchema();
			
			// Run simple ETL process to load WoW-themed data (no Jackson required)
			WoWDataETL etl = new WoWDataETL();
			etl.runETL();
			
			System.out.println("WoW Data ETL completed successfully!");
			
		} catch (SQLException e) {
			System.out.print("SQL Exception: ");
			System.out.println(e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	public static void resetSchema() throws SQLException {
		try (Connection cxn = ConnectionManager.getSchemalessConnection()) {
			cxn.createStatement().executeUpdate("DROP SCHEMA IF EXISTS WoWDataHub;");
			cxn.createStatement().executeUpdate("CREATE SCHEMA WoWDataHub;");
		}
		
		try (Connection cxn = ConnectionManager.getConnection()) {
			// Create tables in order
			cxn.createStatement().executeUpdate("""
				CREATE TABLE `Players` (
				    `playerID` INTEGER AUTO_INCREMENT,
				    `firstName` VARCHAR(255) NOT NULL,
				    `lastName` VARCHAR(255) NOT NULL,
				    `emailAddress` VARCHAR(255) NOT NULL,
				    CONSTRAINT pk_players PRIMARY KEY (playerID)
				);
						""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `Clans` (
					    `clanName` VARCHAR(255),
					    `race` enum('dwarf', 'elf', 'goblin', 'human', 'orc') NOT NULL,
					    CONSTRAINT pk_clans PRIMARY KEY (clanName)
					);
							""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `Items` (
					    `itemID` INTEGER AUTO_INCREMENT,
					    `itemName` VARCHAR(255) NOT NULL,
					    `level` INTEGER NOT NULL,
					    `maxStackSize` INTEGER NOT NULL,
					    `price` DECIMAL(10,2),
					    CONSTRAINT pk_item PRIMARY KEY (`itemID`)
					);
							""");
			
			cxn.createStatement().executeUpdate("""
				CREATE TABLE `Equipments` (
				`itemID` INTEGER NOT NULL,
				`requiredLevel` INTEGER NOT NULL,
				CONSTRAINT pk_equipment PRIMARY KEY (`itemID`),
				CONSTRAINT fk_equipment_itemID FOREIGN KEY (`itemID`)
					REFERENCES Items(`itemID`)
					ON UPDATE CASCADE
					ON DELETE CASCADE);
					""");
			
			cxn.createStatement().executeUpdate("""
				CREATE TABLE `Gears` (
				`itemID` INTEGER,
				CONSTRAINT pk_gear PRIMARY KEY (`itemID`),
				CONSTRAINT fk_gear_itemID FOREIGN KEY (`itemID`)
					REFERENCES Equipments(`itemID`)
					ON UPDATE CASCADE
					ON DELETE CASCADE);
						""");
			
			cxn.createStatement().executeUpdate("""
				CREATE TABLE `Weapons` (
					`itemID` INTEGER,
					`wearableJob` VARCHAR(255) NOT NULL,
					`damage` INTEGER NOT NULL,
					CONSTRAINT pk_weapon PRIMARY KEY (`itemID`),
					CONSTRAINT fk_weapon_itemID FOREIGN KEY (`itemID`)
						REFERENCES Equipments(`itemID`)
						ON UPDATE CASCADE
						ON DELETE CASCADE
				);
						""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `Characters` (
					    `charID` INTEGER AUTO_INCREMENT,
					    `playerID` INTEGER NOT NULL,
					    `firstName` VARCHAR(255) NOT NULL,
					    `lastName` VARCHAR(255) NOT NULL,
					    `clan` VARCHAR(255) NOT NULL,
					    `weaponWeared` INTEGER NOT NULL,
					    CONSTRAINT pk_characters PRIMARY KEY (charID),
					    constraint fk_Char_playerID FOREIGN KEY (playerID)
							references  Players(playerID)
					        on update cascade
					        on delete cascade,
						constraint fk_Char_clan FOREIGN KEY (clan)
							references  Clans(clanName)
					        on update cascade
					        on delete cascade,
						constraint fk_Char_weapon FOREIGN KEY (weaponWeared)
							references  Weapons(itemID)
					        on update cascade
					        on delete restrict
					);
							""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `Statistics` (
					    `statsName` VARCHAR(255),
					    `description` TEXT NOT NULL,
					    CONSTRAINT pk_statistics PRIMARY KEY (statsNAme)
					);
							""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `Currencies` (
					    `currencyName` VARCHAR(255),
					    `cap` DECIMAL(10,2),
					    `weeklyCap` DECIMAL(10,2),
					    CONSTRAINT pk_Currencies PRIMARY KEY (`currencyName`)
					);
							""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `CharacterStatistics` (
					    `charID` INTEGER,
					    `statistics` VARCHAR(255),
					    `value` INTEGER NOT NULL,
					    constraint pk_CharStat_charID_stat PRIMARY KEY (`charID`, `statistics`),
					    constraint fk_CharStat_charID FOREIGN KEY (charID)
							references  Characters(charID)
					        on update cascade
					        on delete cascade,
						constraint fk_CharStat_stat FOREIGN KEY (statistics)
							references Statistics(statsName)
					        on update cascade
					        on delete cascade
					);
							""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `EquipmentBonuse` (
						`equipmentID` INTEGER NOT NULL,
						`statistics` VARCHAR(255) NOT NULL,
						`value` INTEGER NOT NULL,
						constraint pk_EquipmentBonuse PRIMARY KEY (`equipmentID`, `statistics`),
						constraint fk_EqBonuse_eqID FOREIGN KEY (equipmentID)
							references  Equipments(itemID)
						    on update cascade
						    on delete cascade,
						constraint fk_EqBonuse_stat FOREIGN KEY (statistics)
							references  Statistics(statsName)
						    on update cascade
						    on delete restrict
					);
								""");
			
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
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE `ConsumableItemBonuse` (
					    `itemID` INTEGER NOT NULL,
					    `statistics` VARCHAR(255) NOT NULL,
					    `bonusePercent` FLOAT NOT NULL,
					    `valueCap` INTEGER NOT NULL,
					    constraint pk_ConsBonuse PRIMARY KEY (`itemID`, `statistics`),
					    constraint fk_ConsBonuse_itemID FOREIGN KEY (itemID)
							references  Consumables(itemID)
					        on update cascade
					        on delete cascade,
						constraint fk_ConsBonuse_stat FOREIGN KEY (statistics)
							references  Statistics(statsName)
					        on update cascade
					        on delete restrict
					);
								""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE JobsForGear (
						gear INTEGER,
					    jobName VARCHAR(255),
					    CONSTRAINT pk_JobsForGear PRIMARY KEY (gear, jobName),
					    CONSTRAINT fk_JobsForGear_gear FOREIGN KEY (gear) 
							REFERENCES Gears (itemID)
					        ON UPDATE CASCADE 
					        ON DELETE CASCADE
					);
								""");
			
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
					        ON DELETE CASCADE
					);
								""");
			
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
					      ON DELETE CASCADE
					);
								""");
			
			cxn.createStatement().executeUpdate("""
					CREATE TABLE Inventory (
					    charID INT ,  
					    slotID INT ,  
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
					        ON DELETE RESTRICT
					);
								""");
			
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
					        ON DELETE RESTRICT
					);
								""");
		}
		
		System.out.println("Database schema 'WoWDataHub' created successfully!");
	}
}