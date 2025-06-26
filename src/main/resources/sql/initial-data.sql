-- WoW DataHub Initial Reference Data
-- This file contains essential reference data required for the system to function

USE CS5200Project;

-- Insert Available Jobs (WoW Classes)
INSERT INTO AvailableJobs (jobName, description) VALUES
('Warrior', 'Melee DPS and tank class with high survivability'),
('Paladin', 'Hybrid class capable of tanking, healing, and DPS'),
('Death Knight', 'Melee DPS and tank class with dark magic abilities'),
('Hunter', 'Ranged DPS class with pet companions'),
('Rogue', 'Melee DPS class with stealth and high burst damage'),
('Priest', 'Primary healing class with shadow DPS capabilities'),
('Shaman', 'Hybrid class with elemental magic, healing, and melee abilities'),
('Mage', 'Ranged DPS class specializing in arcane, fire, and frost magic'),
('Warlock', 'Ranged DPS class with demonic magic and DoT spells'),
('Monk', 'Hybrid class with martial arts, tanking, and healing'),
('Druid', 'Ultimate hybrid class with shapeshifting abilities'),
('Demon Hunter', 'Melee DPS and tank class with demonic abilities'),
('Evoker', 'Ranged DPS and healing class with dragon abilities'),
('Adventurer', 'Default class for new or classless characters');

-- Insert Core Statistics
INSERT INTO Statistics (statsName, description) VALUES
('Strength', 'Increases melee attack power and damage for strength-based classes'),
('Agility', 'Increases ranged attack power, critical strike, and dodge for agility-based classes'),
('Intelligence', 'Increases spell power and mana pool for caster classes'),
('Stamina', 'Increases health points for all classes'),
('Spirit', 'Increases mana and health regeneration rates'),
('Armor', 'Reduces physical damage taken from attacks'),
('Attack Power', 'Directly increases melee and ranged damage output'),
('Spell Power', 'Directly increases magical damage and healing output'),
('Critical Strike', 'Increases chance for attacks and spells to deal critical damage'),
('Haste', 'Increases attack speed and casting speed'),
('Mastery', 'Class-specific stat that enhances unique abilities'),
('Versatility', 'Increases damage done and reduces damage taken');

-- Insert Game Currencies
INSERT INTO Currencies (currencyName, cap, weeklyCap) VALUES
('Gold', 999999.00, 0.00),
('Honor Points', 15000.00, 0.00),
('Justice Points', 4000.00, 0.00),
('Valor Points', 3000.00, 1000.00),
('Conquest Points', 2200.00, 1800.00),
('Artifact Power', 999999999.00, 0.00),
('Anima', 200000.00, 10000.00),
('Soul Ash', 99999.00, 0.00),
('Soul Cinders', 99999.00, 0.00),
('Cosmic Flux', 99999.00, 0.00);

-- Insert Sample Clans/Guilds
INSERT INTO Clans (clanName, race) VALUES
('Knights of Azeroth', 'human'),
('Ironforge Militia', 'dwarf'),
('Silvermoon Elite', 'elf'),
('Orgrimmar Horde', 'orc'),
('Goblin Trade Empire', 'goblin'),
('Stormwind Guard', 'human'),
('Darnassus Sentinels', 'elf'),
('Thunder Bluff Tribe', 'orc'),
('Gnomeregan Exiles', 'dwarf'),
('Darkspear Trolls', 'orc'),
('Unguilded', 'human');

-- Note: Item data, player data, and character data will be populated via ETL process
-- This file only contains the essential reference data needed for foreign key constraints