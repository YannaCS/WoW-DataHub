package game.etl;

import game.config.WoWApiConfig;
import game.dal.*;
import game.model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * ETL for default/static game data that should be loaded once during database initialization
 * This includes: Clans, Statistics, Currencies, Weapons, Gears, Consumables
 */
public class DefaultDataETL {
    
    private final WoWApiClient apiClient = new WoWApiClient();
    private final Random random = new Random();
    
    // Known working item IDs for weapons (from WoW database)
    private final int[] KNOWN_WEAPON_IDS = {
        19019, 40395, 50735, 50070, 186404, 187854, 188267, 189859,
        6948, 25, 2092, 117, 159, 4540, 17223, 12784, 13262, 22691,
        30311, 32837, 32838, 128289, 128306, 128403, 161356, 159122,
        171415, 178298, 191236, 194308
    };
    
    // Storage for created default objects
    private List<Clans> createdClans = new ArrayList<>();
    private List<Statistics> createdStatistics = new ArrayList<>();
    private List<Currencies> createdCurrencies = new ArrayList<>();
    private List<Weapons> createdWeapons = new ArrayList<>();
    private List<Gears> createdGears = new ArrayList<>();
    private List<Consumables> createdConsumables = new ArrayList<>();
    
    /**
     * Run default data ETL - loads all static/unchanging game data
     */
    public void runDefaultDataETL(Connection cxn) throws SQLException {
        System.out.println("🚀 Starting Default Data ETL (Static Game Data)...");
        
        try {
            // 1. Create predefined clans (race-clan relationships)
            createPredefinedClans(cxn);
            
            // 2. Create comprehensive WoW statistics
            createComprehensiveStatistics(cxn);
            
            // 3. Create real WoW currencies
            createRealWoWCurrencies(cxn);
            
            // 4. Create weapons (try real API, fallback to generated)
            createWeaponsData(cxn);
            
            // 5. Create gear items
            createGearsData(cxn);
            
            // 6. Create consumables
            createConsumablesData(cxn);
            
            // 7. Create equipment and consumable bonuses
            createItemBonuses(cxn);
            
            System.out.println("🎉 Default Data ETL COMPLETED!");
            printDefaultDataSummary();
            
        } catch (Exception e) {
            System.err.println("❌ Default Data ETL failed: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Default Data ETL failed", e);
        }
    }
    
    /**
     * Create predefined clans with proper race-clan relationships
     */
    private void createPredefinedClans(Connection cxn) throws SQLException {
        System.out.println("🏰 Creating predefined clans with race relationships...");
        
        // Define proper race-clan relationships according to data model
        String[][] raceClanMappings = {
            // Human clans
            {"Stormwind Alliance", "HUMAN"},
            {"Kul Tiran Fleet", "HUMAN"},
            {"Gilnean Pack", "HUMAN"},
            {"Midlanders", "HUMAN"},
            {"Highlanders", "HUMAN"},
            
            // Elf clans
            {"Darnassus Sentinels", "ELF"},
            {"Void Elves", "ELF"},
            {"Blood Elves", "ELF"},
            {"Duskwight", "ELF"},
            {"Wildwood", "ELF"},
            
            // Dwarf clans
            {"Ironforge Dwarves", "DWARF"},
            {"Wildhammer Clan", "DWARF"},
            {"Dark Iron Dwarves", "DWARF"},
            
            // Orc clans
            {"Orgrimmar Horde", "ORC"},
            {"Mag'har Orcs", "ORC"},
            {"Undercity Forsaken", "ORC"},
            
            // Goblin clans
            {"Bilgewater Cartel", "GOBLIN"},
            {"Darkspear Trolls", "GOBLIN"},
            {"Zandalari Empire", "GOBLIN"}
        };
        
        for (String[] mapping : raceClanMappings) {
            try {
                Clans.Races race = Clans.Races.valueOf(mapping[1]);
                Clans clan = ClansDao.create(cxn, mapping[0], race);
                createdClans.add(clan);
                System.out.println("✅ Created clan: " + mapping[0] + " (" + mapping[1] + ")");
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Created " + createdClans.size() + " predefined clans");
    }
    
    /**
     * Create comprehensive WoW statistics
     */
    private void createComprehensiveStatistics(Connection cxn) throws SQLException {
        System.out.println("📊 Creating comprehensive WoW statistics...");
        
        String[][] wowStats = {
            {"Strength", "Increases melee damage and carry capacity"},
            {"Agility", "Increases ranged damage, dodge, and armor"},
            {"Intellect", "Increases mana pool and spell power"},
            {"Stamina", "Increases health points"},
            {"Spirit", "Increases mana and health regeneration"},
            {"Critical Strike", "Increases chance for critical hits"},
            {"Haste", "Increases attack and casting speed"},
            {"Mastery", "Enhances class-specific abilities"},
            {"Versatility", "Increases damage and healing, reduces damage taken"},
            {"Multistrike", "Grants chance for additional strikes"},
            {"Armor", "Reduces physical damage taken"},
            {"Dodge", "Chance to completely avoid attacks"},
            {"Parry", "Chance to deflect melee attacks"},
            {"Block", "Reduces damage from blocked attacks"},
            {"Hit Rating", "Increases chance to hit targets"},
            {"Expertise", "Reduces target's dodge and parry chance"},
            {"Spell Power", "Increases magic damage and healing"},
            {"Attack Power", "Increases melee and ranged damage"},
            {"Spell Penetration", "Reduces target's magic resistance"},
            {"Spell Hit", "Increases chance for spells to hit"}
        };
        
        for (String[] stat : wowStats) {
            try {
                Statistics newStat = StatisticsDao.create(cxn, stat[0], stat[1]);
                createdStatistics.add(newStat);
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Created " + createdStatistics.size() + " statistics");
    }
    
    /**
     * Create real WoW currencies
     */
    private void createRealWoWCurrencies(Connection cxn) throws SQLException {
        System.out.println("💰 Creating real WoW currencies...");
        
        // Real WoW currencies with accurate caps
        String[][] realCurrencies = {
            {"Gold", null, null},
            {"Honor", "15000", "1800"},
            {"Conquest", "2400", "550"},
            {"Valor Points", "2000", "750"},
            {"Justice Points", "4000", null},
            {"Anima", "200000", null},
            {"Soul Ash", "5100", "1140"},
            {"Soul Cinders", "1650", "300"},
            {"Cosmic Flux", "2000", null},
            {"Timewarped Badge", "5000", null},
            {"Champion's Seal", "999", "15"},
            {"Venture Coin", "18000", null},
            {"Stygia", "40000", null},
            {"Catalogued Research", "6000", "500"},
            {"Grateful Offering", "999", null},
            {"Dragon Isles Supplies", "2000", "300"},
            {"Residual Memories", "2500", "500"},
            {"Elemental Overflow", "3000", "2000"},
            {"Primal Focus", "1000", "150"},
            {"Storm Sigil", "2000", "900"}
        };
        
        for (String[] currency : realCurrencies) {
            try {
                BigDecimal cap = currency[1] != null ? new BigDecimal(currency[1]) : null;
                BigDecimal weeklyCap = currency[2] != null ? new BigDecimal(currency[2]) : null;
                
                Currencies newCurrency = CurrenciesDao.create(cxn, currency[0], cap, weeklyCap);
                createdCurrencies.add(newCurrency);
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Created " + realCurrencies.length + " real WoW currencies");
    }
    
    /**
     * Create weapons (try API first, then generated)
     */
    private void createWeaponsData(Connection cxn) throws SQLException {
        System.out.println("⚔️  Creating weapons data...");
        
        int realWeaponsCreated = 0;
        
        // Try to get real weapons from API if credentials are set
        if (!WoWApiConfig.CLIENT_ID.equals("PUT_YOUR_CLIENT_ID_HERE")) {
            try {
                apiClient.authenticate();
                realWeaponsCreated = createRealWeapons(cxn);
            } catch (Exception e) {
                System.err.println("⚠️ Failed to get real weapons from API: " + e.getMessage());
            }
        }
        
        // Create generated weapons to fill the gap
        int generatedWeaponsNeeded = Math.max(0, 100 - realWeaponsCreated);
        createGeneratedWeapons(cxn, generatedWeaponsNeeded);
        
        System.out.println("🎯 Created " + createdWeapons.size() + " total weapons (" + 
                          realWeaponsCreated + " real, " + (createdWeapons.size() - realWeaponsCreated) + " generated)");
    }
    
    /**
     * Create real weapons from API
     */
    private int createRealWeapons(Connection cxn) throws SQLException {
        int successCount = 0;
        
        for (int itemId : KNOWN_WEAPON_IDS) {
            if (successCount >= 50) break;
            
            try {
                String itemJson = apiClient.testWorkingEndpoint("/data/wow/item/" + itemId, "static-us");
                WoWApiClient.ItemInfo item = apiClient.parseItem(itemJson);
                
                if (item.name != null && !item.name.isEmpty()) {
                    String job = determineJobFromItemName(item.name);
                    int damage = calculateDamageFromLevel(item.level);
                    BigDecimal price = BigDecimal.valueOf(Math.max(item.sellPrice / 100.0, 50.0));
                    
                    Weapons weapon = WeaponsDao.create(cxn, item.name, item.level, 1, 
                        price, Math.max(item.level - 5, 1), job, damage);
                    createdWeapons.add(weapon);
                    successCount++;
                }
                
                Thread.sleep(150); // Rate limiting
                
            } catch (Exception e) {
                // Continue with next item
            }
        }
        
        return successCount;
    }
    
    /**
     * Create generated weapons
     */
    private void createGeneratedWeapons(Connection cxn, int count) throws SQLException {
        String[] weaponTypes = {"Sword", "Axe", "Mace", "Dagger", "Staff", "Bow", "Gun", "Crossbow", 
                              "Polearm", "Fist Weapon", "Thrown", "Wand"};
        String[] weaponPrefixes = {"Ancient", "Blessed", "Cursed", "Divine", "Enchanted", "Fel", 
                                 "Glorious", "Heroic", "Legendary", "Mythic", "Sacred", "Wicked"};
        String[] jobs = {"Warrior", "Paladin", "Hunter", "Rogue", "Priest", "Death Knight", 
                       "Shaman", "Mage", "Warlock", "Monk", "Druid", "Demon Hunter"};
        String[] weaponSuffixes = {"Power", "Fury", "Vengeance", "Glory", "Honor", "Might", "Wrath", "Justice"};
        
        for (int i = 0; i < count; i++) {
            try {
                String prefix = weaponPrefixes[random.nextInt(weaponPrefixes.length)];
                String type = weaponTypes[random.nextInt(weaponTypes.length)];
                String suffix = weaponSuffixes[random.nextInt(weaponSuffixes.length)];
                String weaponName = prefix + " " + type + " of " + suffix;
                
                int level = 1 + random.nextInt(120);
                String job = jobs[random.nextInt(jobs.length)];
                int damage = level * 2 + random.nextInt(100);
                BigDecimal price = new BigDecimal(level * 1000 + random.nextInt(50000));
                
                Weapons weapon = WeaponsDao.create(cxn, weaponName, level, 1, price, level, job, damage);
                createdWeapons.add(weapon);
                
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
    }
    
    /**
     * Create gear items
     */
    private void createGearsData(Connection cxn) throws SQLException {
        System.out.println("🛡️ Creating gear items...");
        
        String[] gearTypes = {"Helm", "Shoulders", "Chest", "Bracers", "Gloves", "Belt", "Legs", "Boots", 
                             "Cloak", "Ring", "Necklace", "Trinket", "Shield", "Off-hand"};
        String[] gearPrefixes = {"Heroic", "Mythic", "Elite", "Champion", "Legendary", "Epic", "Rare", 
                               "Superior", "Masterwork", "Enchanted", "Blessed", "Cursed"};
        String[] gearSets = {"Judgment", "Nemesis", "Prophecy", "Lawbringer", "Cenarion", "Earthfury", 
                           "Giantstalker", "Might", "Transcendence", "Bloodfang", "Netherwind", "Stormrage"};
        
        int targetCount = 150;
        int count = 0;
        
        while (count < targetCount) {
            String setName = gearSets[random.nextInt(gearSets.length)];
            String prefix = gearPrefixes[random.nextInt(gearPrefixes.length)];
            String type = gearTypes[random.nextInt(gearTypes.length)];
            String gearName = prefix + " " + setName + " " + type;
            
            int level = 1 + random.nextInt(120);
            BigDecimal price = new BigDecimal(level * 500 + random.nextInt(25000));
            
            try {
                Gears newGear = GearsDao.create(cxn, gearName, level, 1, price, level);
                createdGears.add(newGear);
                count++;
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Created " + count + " gear pieces");
    }
    
    /**
     * Create consumables
     */
    private void createConsumablesData(Connection cxn) throws SQLException {
        System.out.println("🧪 Creating consumables...");
        
        String[] consumableTypes = {"Flask", "Elixir", "Potion", "Food", "Scroll", "Bandage", "Oil", "Stone"};
        String[] effects = {"Strength", "Agility", "Intellect", "Stamina", "Health", "Mana", "Speed", "Armor"};
        String[] qualities = {"Lesser", "Greater", "Superior", "Major", "Grand", "Ultimate", "Perfect", "Flawless"};
        
        int targetCount = 100;
        int count = 0;
        
        while (count < targetCount) {
            String quality = qualities[random.nextInt(qualities.length)];
            String type = consumableTypes[random.nextInt(consumableTypes.length)];
            String effect = effects[random.nextInt(effects.length)];
            String consumableName = quality + " " + type + " of " + effect;
            
            int level = 1;
            BigDecimal price = new BigDecimal(10 + random.nextInt(200));
            String description = "Provides " + effect + " enhancement for a limited time. " +
                               "Created through alchemy and cooking professions.";
            
            try {
                Consumables newConsumable = ConsumablesDao.create(cxn, consumableName, level, 20, price, description);
                createdConsumables.add(newConsumable);
                count++;
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Created " + count + " consumables");
    }
    
    /**
     * Create item bonuses for equipment and consumables
     */
    private void createItemBonuses(Connection cxn) throws SQLException {
        System.out.println("⚡ Creating item bonuses...");
        
        int equipmentBonuses = createEquipmentBonuses(cxn, 200);
        int consumableBonuses = createConsumableBonuses(cxn, 150);
        int jobsForGear = createJobsForGear(cxn, 100);
        
        System.out.println("🎯 Created " + equipmentBonuses + " equipment bonuses, " + 
                          consumableBonuses + " consumable bonuses, " + jobsForGear + " jobs for gear");
    }
    
    // Helper methods
    private int createEquipmentBonuses(Connection cxn, int targetCount) throws SQLException {
        int count = 0;
        while (count < targetCount && (!createdWeapons.isEmpty() || !createdGears.isEmpty()) && !createdStatistics.isEmpty()) {
            try {
                boolean useWeapon = random.nextBoolean() && !createdWeapons.isEmpty();
                Equipments equipment = null;
                
                if (useWeapon) {
                    Weapons weapon = createdWeapons.get(random.nextInt(createdWeapons.size()));
                    equipment = EquipmentsDao.getEquipmentByItemID(cxn, weapon.getItemID());
                } else if (!createdGears.isEmpty()) {
                    Gears gear = createdGears.get(random.nextInt(createdGears.size()));
                    equipment = EquipmentsDao.getEquipmentByItemID(cxn, gear.getItemID());
                }
                
                if (equipment != null) {
                    Statistics statistic = createdStatistics.get(random.nextInt(createdStatistics.size()));
                    int value = 5 + random.nextInt(50);
                    
                    EquipmentBonuseDao.create(cxn, equipment, statistic, value);
                    count++;
                }
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        return count;
    }
    
    private int createConsumableBonuses(Connection cxn, int targetCount) throws SQLException {
        int count = 0;
        while (count < targetCount && !createdConsumables.isEmpty() && !createdStatistics.isEmpty()) {
            try {
                Consumables consumable = createdConsumables.get(random.nextInt(createdConsumables.size()));
                Statistics statistic = createdStatistics.get(random.nextInt(createdStatistics.size()));
                float bonusPercent = 5.0f + (random.nextFloat() * 25.0f);
                int valueCap = 10 + random.nextInt(40);
                
                ConsumableItemBonuseDao.create(cxn, consumable, statistic, bonusPercent, valueCap);
                count++;
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        return count;
    }
    
    private int createJobsForGear(Connection cxn, int targetCount) throws SQLException {
        String[] jobs = {"Warrior", "Paladin", "Hunter", "Rogue", "Priest", "Death Knight", 
                        "Shaman", "Mage", "Warlock", "Monk", "Druid", "Demon Hunter"};
        
        int count = 0;
        while (count < targetCount && !createdGears.isEmpty()) {
            try {
                Gears gear = createdGears.get(random.nextInt(createdGears.size()));
                String job = jobs[random.nextInt(jobs.length)];
                
                JobsForGearDao.create(cxn, gear, job);
                count++;
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        return count;
    }
    
    // Helper methods
    private int calculateDamageFromLevel(int level) {
        return Math.max(level * 2 + random.nextInt(level + 10), 15);
    }
    
    private String determineJobFromItemName(String itemName) {
        String name = itemName.toLowerCase();
        if (name.contains("staff") || name.contains("wand") || name.contains("tome")) return "Mage";
        if (name.contains("bow") || name.contains("gun") || name.contains("crossbow")) return "Hunter";
        if (name.contains("dagger") || name.contains("blade")) return "Rogue";
        if (name.contains("hammer") || name.contains("mace")) return "Paladin";
        if (name.contains("sword") || name.contains("axe")) return "Warrior";
        return "Warrior"; // Default
    }
    
    private void printDefaultDataSummary() {
        System.out.println("\n📋 DEFAULT DATA ETL SUMMARY:");
        System.out.println("========================");
        System.out.println("🏰 Clans: " + createdClans.size());
        System.out.println("📊 Statistics: " + createdStatistics.size());
        System.out.println("💰 Currencies: " + createdCurrencies.size());
        System.out.println("⚔️ Weapons: " + createdWeapons.size());
        System.out.println("🛡️ Gears: " + createdGears.size());
        System.out.println("🧪 Consumables: " + createdConsumables.size());
        System.out.println("========================");
        System.out.println("🎮 Default WoW Data loaded successfully!");
    }
    
    // Getters for accessing created data
    public List<Clans> getCreatedClans() { return createdClans; }
    public List<Statistics> getCreatedStatistics() { return createdStatistics; }
    public List<Currencies> getCreatedCurrencies() { return createdCurrencies; }
    public List<Weapons> getCreatedWeapons() { return createdWeapons; }
    public List<Gears> getCreatedGears() { return createdGears; }
    public List<Consumables> getCreatedConsumables() { return createdConsumables; }
}