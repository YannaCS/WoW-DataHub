-- WoW DataHub Database Schema
-- Complete database structure for MMORPG management system

-- Drop existing schema if exists
DROP SCHEMA IF EXISTS CS5200Project;
CREATE SCHEMA CS5200Project;
USE CS5200Project;

-- 1. Items Table (Base table for all items)
CREATE TABLE Items (
    itemID INTEGER NOT NULL AUTO_INCREMENT,
    itemName VARCHAR(255) NOT NULL,
    level INTEGER NOT NULL,
    maxStackSize INTEGER NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    CONSTRAINT pk_items PRIMARY KEY (itemID)
);

-- 2. Available Jobs Table (Reference table for job validation)
CREATE TABLE AvailableJobs (
    jobName VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT pk_jobs PRIMARY KEY (jobName)
);

-- 3. Statistics Table (Reference table for character stats)
CREATE TABLE Statistics (
    statsName VARCHAR(255) NOT NULL,
    description TEXT,
    CONSTRAINT pk_statistics PRIMARY KEY (statsName)
);

-- 4. Currencies Table (Game currencies and their caps)
CREATE TABLE Currencies (
    currencyName VARCHAR(255) NOT NULL,
    cap FLOAT NOT NULL,
    weeklyCap FLOAT NOT NULL,
    CONSTRAINT pk_currencies PRIMARY KEY (currencyName)
);

-- 5. Players Table (Real players/users)
CREATE TABLE Players (
    playerID INTEGER NOT NULL AUTO_INCREMENT,
    firstName VARCHAR(255) NOT NULL,
    lastName VARCHAR(255) NOT NULL,
    emailAddress VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT pk_players PRIMARY KEY (playerID)
);

-- 6. Clans Table (Guilds/Organizations)
CREATE TABLE Clans (
    clanName VARCHAR(255) NOT NULL,
    race ENUM('dwarf', 'elf', 'goblin', 'human', 'orc') NOT NULL,
    CONSTRAINT pk_clans PRIMARY KEY (clanName)
);

-- 7. Equipments Table (Equipment items - extends Items)
CREATE TABLE Equipments (
    itemID INTEGER NOT NULL,
    requiredLevel INTEGER NOT NULL,
    CONSTRAINT pk_equipment PRIMARY KEY (itemID),
    CONSTRAINT fk_equipment_itemID FOREIGN KEY (itemID)
        REFERENCES Items(itemID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- 8. Gears Table (Armor and accessories - extends Equipments)
CREATE TABLE Gears (
    itemID INTEGER,
    CONSTRAINT pk_gear PRIMARY KEY (itemID),
    CONSTRAINT fk_gear_itemID FOREIGN KEY (itemID)
        REFERENCES Equipments(itemID)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- 9. Weapons Table (Weapons - extends Equipments)
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

-- 10. Characters Table (Game characters)
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

-- 11. Character Wealth Table (Character currency holdings)
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

-- 12. Character Unlocked Jobs Table (Character job progression)
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

-- 13. Character Statistics Table (Character stat values)
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

-- 14. Equipment Bonuses Table (Equipment stat bonuses)
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

-- 15. Jobs For Gear Table (Job restrictions for gear)
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

-- Create indexes for better performance
CREATE INDEX idx_players_email ON Players(emailAddress);
CREATE INDEX idx_characters_player ON Characters(playerID);
CREATE INDEX idx_characters_clan ON Characters(clan);
CREATE INDEX idx_character_wealth_char ON CharacterWealth(charID);
CREATE INDEX idx_character_jobs_char ON CharacterUnlockedJob(charID);
CREATE INDEX idx_character_stats_char ON CharacterStatistics(charID);
CREATE INDEX idx_equipment_bonus_equip ON EquipmentBonuse(equipmentID);
CREATE INDEX idx_items_name ON Items(itemName);
CREATE INDEX idx_weapons_job ON Weapons(wearableJob);