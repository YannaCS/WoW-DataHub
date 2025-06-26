-- WoW DataHub Test Data
-- Sample data for development and testing purposes
-- This file is separate from production data and can be used for development testing

USE CS5200Project;

-- Sample Items (Base items before they become equipment)
INSERT INTO Items (itemName, level, maxStackSize, price) VALUES
('Thunderfury, Blessed Blade of the Windseeker', 60, 1, 50000.00),
('Atiesh, Greatstaff of the Guardian', 60, 1, 75000.00),
('Ashbringer', 60, 1, 100000.00),
('Warglaives of Azzinoth', 70, 1, 80000.00),
('Shadowmourne', 80, 1, 120000.00),
('Helm of Domination', 50, 1, 25000.00),
('Chestplate of the Apocalypse', 55, 1, 30000.00),
('Legplates of Eternal Glory', 52, 1, 28000.00),
('Basic Sword', 1, 1, 10.00),
('Novice Staff', 1, 1, 15.00);

-- Sample Equipment entries (linking items to equipment properties)
INSERT INTO Equipments (itemID, requiredLevel) VALUES
(1, 60), (2, 60), (3, 60), (4, 70), (5, 80),
(6, 50), (7, 55), (8, 52), (9, 1), (10, 1);

-- Sample Weapons (legendary weapons from WoW)
INSERT INTO Weapons (itemID, wearableJob, damage) VALUES
(1, 'Warrior', 150),      -- Thunderfury
(2, 'Mage', 200),         -- Atiesh
(3, 'Paladin', 180),      -- Ashbringer
(4, 'Demon Hunter', 220), -- Warglaives
(5, 'Death Knight', 250), -- Shadowmourne
(9, 'Adventurer', 10),    -- Basic Sword
(10, 'Adventurer', 15);   -- Novice Staff

-- Sample Gears (armor pieces)
INSERT INTO Gears (itemID) VALUES
(6), (7), (8); -- Helm, Chestplate, Legplates

-- Sample Players (test players)
INSERT INTO Players (firstName, lastName, emailAddress) VALUES
('John', 'Doe', 'john.doe@example.com'),
('Jane', 'Smith', 'jane.smith@example.com'),
('Bob', 'Johnson', 'bob.johnson@example.com'),
('Alice', 'Williams', 'alice.williams@example.com'),
('Charlie', 'Brown', 'charlie.brown@example.com');

-- Sample Characters (test characters)
INSERT INTO Characters (playerID, firstName, lastName, clan, weaponWeared) VALUES
(1, 'Arthas', 'Menethil', 'Knights of Azeroth', 3),  -- Paladin with Ashbringer
(2, 'Jaina', 'Proudmoore', 'Stormwind Guard', 2),    -- Mage with Atiesh
(3, 'Thrall', 'Doomhammer', 'Orgrimmar Horde', 1),   -- Warrior with Thunderfury
(4, 'Illidan', 'Stormrage', 'Darkspear Trolls', 4),  -- Demon Hunter with Warglaives
(5, 'Varian', 'Wrynn', 'Stormwind Guard', 9);        -- Warrior with Basic Sword

-- Sample Character Jobs (character class progression)
INSERT INTO CharacterUnlockedJob (charID, jobName, jobLevel, XP) VALUES
(1, 'Paladin', 85, 85000),
(1, 'Warrior', 25, 25000),
(2, 'Mage', 80, 80000),
(3, 'Warrior', 90, 90000),
(3, 'Shaman', 70, 70000),
(4, 'Demon Hunter', 85, 85000),
(5, 'Warrior', 60, 60000);

-- Sample Character Statistics (character stats)
INSERT INTO CharacterStatistics (charID, statistics, value) VALUES
-- Arthas (Paladin)
(1, 'Strength', 450), (1, 'Stamina', 380), (1, 'Spirit', 200),
-- Jaina (Mage)
(2, 'Intelligence', 500), (2, 'Stamina', 300), (2, 'Spell Power', 800),
-- Thrall (Warrior)
(3, 'Strength', 480), (3, 'Stamina', 420), (3, 'Attack Power', 1200),
-- Illidan (Demon Hunter)
(4, 'Agility', 470), (4, 'Stamina', 350), (4, 'Attack Power', 1100),
-- Varian (Warrior)
(5, 'Strength', 280), (5, 'Stamina', 250), (5, 'Attack Power', 600);

-- Sample Character Wealth (currency holdings)
INSERT INTO CharacterWealth (charID, currencyName, amount, weeklyAcquired) VALUES
(1, 'Gold', 15000.50, 500.25),
(1, 'Honor Points', 8500.00, 1200.00),
(2, 'Gold', 22000.75, 800.50),
(2, 'Justice Points', 3200.00, 400.00),
(3, 'Gold', 18500.00, 600.00),
(3, 'Valor Points', 2800.00, 900.00),
(4, 'Gold', 12500.25, 400.75),
(4, 'Conquest Points', 1800.00, 300.00),
(5, 'Gold', 5500.00, 200.00);

-- Sample Equipment Bonuses (item stat bonuses)
INSERT INTO EquipmentBonuse (equipmentID, statistics, value) VALUES
-- Thunderfury bonuses
(1, 'Strength', 25), (1, 'Stamina', 20), (1, 'Attack Power', 50),
-- Atiesh bonuses
(2, 'Intelligence', 35), (2, 'Spell Power', 100), (2, 'Stamina', 15),
-- Ashbringer bonuses
(3, 'Strength', 30), (3, 'Stamina', 25), (3, 'Attack Power', 60),
-- Warglaives bonuses
(4, 'Agility', 40), (4, 'Attack Power', 80), (4, 'Critical Strike', 25),
-- Shadowmourne bonuses
(5, 'Strength', 45), (5, 'Stamina', 35), (5, 'Attack Power', 120),
-- Helm bonuses
(6, 'Stamina', 15), (6, 'Armor', 200),
-- Chestplate bonuses
(7, 'Stamina', 25), (7, 'Armor', 350), (7, 'Strength', 15),
-- Legplates bonuses
(8, 'Stamina', 20), (8, 'Armor', 280), (8, 'Agility', 10);

-- Sample Jobs for Gear (class restrictions for armor)
INSERT INTO JobsForGear (gear, jobName) VALUES
-- Helm can be worn by multiple classes
(6, 'Warrior'), (6, 'Paladin'), (6, 'Death Knight'),
-- Chestplate restrictions
(7, 'Warrior'), (7, 'Paladin'), (7, 'Death Knight'),
-- Legplates restrictions
(8, 'Warrior'), (8, 'Paladin'), (8, 'Death Knight'), (8, 'Hunter');

-- Note: This test data represents iconic WoW items and characters
-- It can be used for development testing without affecting production data