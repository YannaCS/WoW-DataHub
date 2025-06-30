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
 * ETL for dynamic player and character data that can be run multiple times
 * This includes: Players, Characters, CharacterWealth, CharacterStatistics, 
 * CharacterUnlockedJobs, Inventory, EquippedItems
 */
public class WoWDataETL {
    
    private final WoWApiClient apiClient = new WoWApiClient();
    private int recordsProcessed = 0;
    private final Random random = new Random();
    
    // Storage for created dynamic objects
    private List<Players> createdPlayers = new ArrayList<>();
    private List<Characters> createdCharacters = new ArrayList<>();
    
    public WoWDataETL() {
    }
    
    /**
     * Run dynamic ETL for players and characters
     */
    public void runETL() {
        try {
            System.out.println("🚀 Starting Dynamic WoW Data ETL (Players & Characters)...");
            
            // Check credentials for API usage
            boolean useApi = !WoWApiConfig.CLIENT_ID.equals("PUT_YOUR_CLIENT_ID_HERE");
            
            if (useApi) {
                System.out.println("🔐 Authenticating with Battle.net API...");
                apiClient.authenticate();
            }
            
            try (Connection cxn = ConnectionManager.getConnection()) {
                
                // Check if default data exists
                if (!AllDataDao.hasDefaultData(cxn)) {
                    System.err.println("❌ No default data found! Please run DefaultDataETL first.");
                    return;
                }
                
                // Get existing default data from database
                List<Clans> existingClans = ClansDao.getAllClans(cxn);
                List<Weapons> existingWeapons = WeaponsDao.getAllWeapons(cxn);
                List<Statistics> existingStatistics = AllDataDao.getAllStatistics(cxn);
                List<Currencies> existingCurrencies = AllDataDao.getAllCurrencies(cxn);
                List<Gears> existingGears = AllDataDao.getAllGears(cxn);
                List<Consumables> existingConsumables = AllDataDao.getAllConsumables(cxn);
                
                System.out.println("📊 Found existing data: " + existingClans.size() + " clans, " + 
                                 existingWeapons.size() + " weapons, " + existingStatistics.size() + " statistics, " +
                                 existingCurrencies.size() + " currencies, " + existingGears.size() + " gears, " +
                                 existingConsumables.size() + " consumables");
                
                // 1. Add players (from API realms or generated)
                if (useApi) {
                    fetchRealRealmsAndAddPlayers(cxn);
                } else {
                    addSamplePlayers(cxn);
                }
                
                // 2. Add characters with business rule compliance
                addCharactersWithValidation(cxn, existingClans, existingWeapons);
                
                // 3. Add character relationships and data
                addCharacterRelationships(cxn, existingStatistics, existingCurrencies, 
                                           existingWeapons, existingGears, existingConsumables);
                
            }
            
            System.out.println("🎉 Dynamic ETL COMPLETED!");
            System.out.println("📊 Total records processed: " + recordsProcessed);
            printDynamicSummary();
            
        } catch (Exception e) {
            System.err.println("❌ Dynamic ETL failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Fetch real realms and add players
     */
    private void fetchRealRealmsAndAddPlayers(Connection cxn) throws Exception {
        System.out.println("👥 Adding players from real WoW realms...");
        
        // Get real realms
        String realmsJson = apiClient.testWorkingEndpoint("/data/wow/realm/index", "dynamic-us");
        List<String> realmObjects = apiClient.extractJsonArray(realmsJson, "realms");
        
        String[] firstNames = {"Arthas", "Jaina", "Thrall", "Sylvanas", "Anduin", "Varian",
                              "Tyrande", "Malfurion", "Illidan", "Uther", "Cairne", "Vol'jin",
                              "Aelynn", "Baine", "Calia", "Darius", "Elaria", "Falstad"};
        String[] lastNames = {"Stormwind", "Ironforge", "Darnassus", "Orgrimmar", "Thunderbluff", 
                             "Undercity", "Silvermoon", "Shattrath", "Dalaran", "Boralus"};
        
        int count = 0;
        int targetCount = 50;
        Random random = new Random();
        
        for (String realmObject : realmObjects) {
            if (count >= targetCount) break;
            
            String realmName = apiClient.extractJsonValue(realmObject, "name");
            String realmSlug = apiClient.extractJsonValue(realmObject, "slug");
            
            if (realmName != null) {
                String firstName = firstNames[random.nextInt(firstNames.length)];
                String lastName = lastNames[random.nextInt(lastNames.length)];
                String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "." + 
                              System.currentTimeMillis() + "@" + 
                              (realmSlug != null ? realmSlug : realmName.toLowerCase().replaceAll("\\s+", "")) + ".realm";
                
                try {
                    Players newPlayer = PlayersDao.create(cxn, firstName, lastName, email);
                    
                    // Set random activity date
                    int daysAgo = random.nextInt(30);
                    try {
                        var updateStmt = cxn.prepareStatement(
                            "UPDATE Players SET lastActiveDateTime = DATE_SUB(NOW(), INTERVAL ? DAY) WHERE playerID = ?"
                        );
                        updateStmt.setInt(1, daysAgo);
                        updateStmt.setInt(2, newPlayer.getPlayerID());
                        updateStmt.executeUpdate();
                        updateStmt.close();
                    } catch (SQLException e) {
                        // Column might not exist, ignore
                    }
                    
                    createdPlayers.add(newPlayer);
                    recordsProcessed++;
                    count++;
                    
                    if (count % 10 == 0) {
                        System.out.println("✅ Added " + count + " players so far...");
                    }
                } catch (SQLException e) {
                    if (!e.getMessage().contains("Duplicate entry")) {
                        throw e;
                    }
                }
            }
            
            Thread.sleep(50);
        }
        
        System.out.println("🎯 Added " + count + " players from real realms");
    }
    
    /**
     * Add sample players (fallback when no API)
     */
    private void addSamplePlayers(Connection cxn) throws SQLException {
        System.out.println("👥 Adding sample players...");
        
        String[] firstNames = {"Arthas", "Jaina", "Thrall", "Sylvanas", "Anduin", "Varian",
                              "Tyrande", "Malfurion", "Illidan", "Uther", "Cairne", "Vol'jin"};
        String[] lastNames = {"Stormwind", "Ironforge", "Darnassus", "Orgrimmar", "Thunderbluff", 
                             "Undercity", "Silvermoon", "Shattrath", "Dalaran", "Boralus"};
        
        int targetCount = 50;
        int count = 0;
        Random random = new Random();
        
        while (count < targetCount) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "." + 
                          System.currentTimeMillis() + random.nextInt(1000) + "@sample.realm";
            
            try {
                // Use the create method which should now include lastActiveDateTime
                Players newPlayer = PlayersDao.create(cxn, firstName, lastName, email);
                
                // Update with a random recent activity date
                int daysAgo = random.nextInt(30);
                try {
                    var updateStmt = cxn.prepareStatement(
                        "UPDATE Players SET lastActiveDateTime = DATE_SUB(NOW(), INTERVAL ? DAY) WHERE playerID = ?"
                    );
                    updateStmt.setInt(1, daysAgo);
                    updateStmt.setInt(2, newPlayer.getPlayerID());
                    updateStmt.executeUpdate();
                    updateStmt.close();
                } catch (SQLException e) {
                    // Column might not exist, ignore
                }
                
                createdPlayers.add(newPlayer);
                recordsProcessed++;
                count++;
            } catch (SQLException e) {
                if (!e.getMessage().contains("Duplicate entry")) {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Added " + count + " sample players");
    }
    
    /**
     * Add characters with business rule validation
     */
    private void addCharactersWithValidation(Connection cxn, List<Clans> clans, List<Weapons> weapons) throws SQLException {
        System.out.println("🦸 Adding characters with business rule validation...");
        
        String[] characterFirstNames = {"Aelindra", "Brenon", "Celaena", "Draven", "Elara", "Fenris", 
                                      "Gwendolyn", "Haldor", "Iona", "Jaxon", "Kira", "Lyanna"};
        String[] characterLastNames = {"Lightbringer", "Shadowbane", "Stormcaller", "Ironhart", "Goldleaf", 
                                     "Dragonslayer", "Moonwhisper", "Flamestrike", "Frostborn", "Earthshaker"};
        
        int targetCount = Math.min(100, createdPlayers.size() * 2); // Up to 2 characters per player
        int count = 0;
        int attempts = 0;
        int maxAttempts = targetCount * 3;
        
        while (count < targetCount && attempts < maxAttempts) {
            attempts++;
            
            Players player = createdPlayers.get(random.nextInt(createdPlayers.size()));
            Clans clan = clans.get(random.nextInt(clans.size()));
            Weapons weapon = weapons.get(random.nextInt(weapons.size()));
            
            String firstName = characterFirstNames[random.nextInt(characterFirstNames.length)];
            String lastName = characterLastNames[random.nextInt(characterLastNames.length)];
            
            // Add timestamp to ensure uniqueness
            if (attempts > targetCount) {
                lastName = lastName + System.currentTimeMillis() % 1000;
            }
            
            try {
                // Use business rules service to create character
                Characters newCharacter = game.service.BusinessRulesService.createCharacterWithValidation(
                    cxn, player, firstName, lastName, clan, weapon);
                
                createdCharacters.add(newCharacter);
                recordsProcessed++;
                count++;
                
                if (count % 10 == 0) {
                    System.out.println("✅ Added " + count + " characters so far...");
                }
                
            } catch (game.service.BusinessRulesService.BusinessRuleException e) {
                // Continue with next attempt
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    continue;
                } else {
                    throw e;
                }
            }
        }
        
        System.out.println("🎯 Added " + count + " characters with business rule compliance");
    }
    
    /**
     * Add comprehensive relationships between characters and game data
     */
    private void addCharacterRelationships(Connection cxn, List<Statistics> statistics, 
                                            List<Currencies> currencies, List<Weapons> weapons,
                                            List<Gears> gears, List<Consumables> consumables) throws SQLException {
        System.out.println("🔗 Adding character relationships...");
        
        // 1. Character statistics
        int charStats = addCharacterStatistics(cxn, statistics, 300);
        
        // 2. Character unlocked jobs (additional jobs beyond starting)
        int charJobs = addCharacterUnlockedJobs(cxn, 150);
        
        // 3. Character wealth (with currency cap validation)
        int charWealth = addCharacterWealth(cxn, currencies, 200);
        
        // 4. Inventory entries
        int inventory = addInventoryEntries(cxn, weapons, gears, consumables, 400);
        
        // 5. Equipped items
        int equipped = addEquippedItems(cxn, gears, 100);
        
        System.out.println("✅ Added relationships: " + charStats + " stats, " + charJobs + " jobs, " + 
                          charWealth + " wealth, " + inventory + " inventory, " + equipped + " equipped");
    }
    
    /**
     * Add character statistics
     */
    private int addCharacterStatistics(Connection cxn, List<Statistics> statistics, int targetCount) throws SQLException {
        if (createdCharacters.isEmpty() || statistics.isEmpty()) return 0;
        
        int count = 0;
        while (count < targetCount) {
            Characters character = createdCharacters.get(random.nextInt(createdCharacters.size()));
            Statistics statistic = statistics.get(random.nextInt(statistics.size()));
            int value = 10 + random.nextInt(90);
            
            try {
                CharacterStatisticsDao.create(cxn, character, statistic, value);
                recordsProcessed++;
                count++;
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    continue;
                } else {
                    throw e;
                }
            }
        }
        return count;
    }
    
    /**
     * Add character unlocked jobs
     */
    private int addCharacterUnlockedJobs(Connection cxn, int targetCount) throws SQLException {
        if (createdCharacters.isEmpty()) return 0;
        
        String[] jobs = {"Warrior", "Paladin", "Hunter", "Rogue", "Priest", "Death Knight", 
                        "Shaman", "Mage", "Warlock", "Monk", "Druid", "Demon Hunter"};
        
        int count = 0;
        while (count < targetCount) {
            Characters character = createdCharacters.get(random.nextInt(createdCharacters.size()));
            String job = jobs[random.nextInt(jobs.length)];
            
            Integer jobLevel = random.nextBoolean() ? 1 + random.nextInt(100) : null;
            Integer xp = null;
            
            if (jobLevel != null) {
                int minXp = (jobLevel - 1) * 1000;
                int maxXp = jobLevel * 1000 + random.nextInt(5000);
                xp = minXp + random.nextInt(maxXp - minXp + 1);
            }
            
            try {
                game.service.BusinessRulesService.validateJobProgression(jobLevel, xp);
                CharacterUnlockedJobDao.create(cxn, character, job, jobLevel, xp);
                recordsProcessed++;
                count++;
            } catch (game.service.BusinessRulesService.BusinessRuleException e) {
                // Skip this entry and continue
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    continue;
                } else {
                    throw e;
                }
            }
        }
        return count;
    }
    
    /**
     * Add character wealth with currency cap validation
     */
    private int addCharacterWealth(Connection cxn, List<Currencies> currencies, int targetCount) throws SQLException {
        if (createdCharacters.isEmpty() || currencies.isEmpty()) return 0;
        
        int count = 0;
        while (count < targetCount) {
            Characters character = createdCharacters.get(random.nextInt(createdCharacters.size()));
            Currencies currency = currencies.get(random.nextInt(currencies.size()));
            
            // Generate amounts that respect currency caps
            BigDecimal amount;
            BigDecimal weeklyAcquired = null;
            
            if (currency.getCap() != null) {
                // Generate amount within cap (10% to 90% of cap)
                double capValue = currency.getCap().doubleValue();
                double minAmount = Math.max(capValue * 0.1, 100);
                double maxAmount = capValue * 0.9;
                amount = new BigDecimal(minAmount + random.nextDouble() * (maxAmount - minAmount));
                amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
            } else {
                // No cap, use reasonable amounts
                amount = new BigDecimal(random.nextInt(50000) + 1000);
            }
            
            if (currency.getWeeklyCap() != null && random.nextBoolean()) {
                // Generate weekly acquired within weekly cap (10% to 80% of weekly cap)
                double weeklyCap = currency.getWeeklyCap().doubleValue();
                double minWeekly = Math.max(weeklyCap * 0.1, 10);
                double maxWeekly = weeklyCap * 0.8;
                weeklyAcquired = new BigDecimal(minWeekly + random.nextDouble() * (maxWeekly - minWeekly));
                weeklyAcquired = weeklyAcquired.setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            
            try {
                // Validate before creating
                game.service.BusinessRulesService.validateCurrencyTransaction(cxn, character, currency, amount, weeklyAcquired);
                
                CharacterWealthDao.create(cxn, character, currency, amount, weeklyAcquired);
                recordsProcessed++;
                count++;
            } catch (game.service.BusinessRulesService.BusinessRuleException e) {
                // Skip this entry and continue - validation failed
                System.out.println("⚠️ Skipped currency " + currency.getCurrencyName() + " for character " + character.getFirstName() + ": " + e.getMessage());
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    continue;
                } else if (e.getMessage().contains("exceeds currency cap") || e.getMessage().contains("exceeds cap")) {
                    // Skip entries that violate currency caps
                    System.out.println("⚠️ Skipped currency entry due to cap violation: " + e.getMessage());
                    continue;
                } else {
                    throw e;
                }
            }
        }
        return count;
    }
    
    /**
     * Add inventory entries
     */
    private int addInventoryEntries(Connection cxn, List<Weapons> weapons, List<Gears> gears, 
                                      List<Consumables> consumables, int targetCount) throws SQLException {
        if (createdCharacters.isEmpty()) return 0;
        
        int count = 0;
        
        // First, ensure every character has additional weapons in their inventory
        for (Characters character : createdCharacters) {
            if (count >= targetCount) break;
            
            // Add 1-2 additional weapons to each character's inventory
            int weaponsToAdd = 1 + random.nextInt(2);
            
            for (int i = 0; i < weaponsToAdd && count < targetCount; i++) {
                if (!weapons.isEmpty()) {
                    Weapons weapon = weapons.get(random.nextInt(weapons.size()));
                    int slotID = 10 + i; // Start from slot 10 to avoid conflict with equipped weapon
                    int quantity = 1;
                    
                    try {
                        InventoryDao.create(cxn, character, slotID, weapon, quantity);
                        recordsProcessed++;
                        count++;
                    } catch (SQLException e) {
                        if (e.getMessage().contains("Duplicate entry")) {
                            continue;
                        } else {
                            throw e;
                        }
                    }
                }
            }
        }
        
        // Fill remaining slots with other items
        while (count < targetCount) {
            if (createdCharacters.isEmpty()) break;
            
            Characters character = createdCharacters.get(random.nextInt(createdCharacters.size()));
            int slotID = 20 + random.nextInt(50); // Slots 20-70 for other items
            
            // Choose random item from weapons, gears, or consumables
            Items item = null;
            int itemType = random.nextInt(3);
            if (itemType == 0 && !weapons.isEmpty()) {
                item = weapons.get(random.nextInt(weapons.size()));
            } else if (itemType == 1 && !gears.isEmpty()) {
                item = gears.get(random.nextInt(gears.size()));
            } else if (!consumables.isEmpty()) {
                item = consumables.get(random.nextInt(consumables.size()));
            }
            
            if (item != null) {
                int quantity = item instanceof Consumables ? 1 + random.nextInt(20) : 1;
                
                try {
                    InventoryDao.create(cxn, character, slotID, item, quantity);
                    recordsProcessed++;
                    count++;
                } catch (SQLException e) {
                    if (e.getMessage().contains("Duplicate entry")) {
                        continue;
                    } else {
                        throw e;
                    }
                }
            }
        }
        
        return count;
    }
    
    /**
     * Add equipped items
     */
    private int addEquippedItems(Connection cxn, List<Gears> gears, int targetCount) throws SQLException {
        if (createdCharacters.isEmpty() || gears.isEmpty()) return 0;
        
        String[] equipPositions = {"HEAD", "BODY", "HANDS", "LEGS", "FEET", 
                                 "WRIST", "RING", "NECKLACE", "EARRING"}; // Exclude MAIN_HAND (handled by weapon)
        
        int count = 0;
        while (count < targetCount) {
            Characters character = createdCharacters.get(random.nextInt(createdCharacters.size()));
            String equipPosition = equipPositions[random.nextInt(equipPositions.length)];
            Gears gear = gears.get(random.nextInt(gears.size()));
            
            try {
                EquippedItemsDao.create(cxn, character, equipPosition, gear);
                recordsProcessed++;
                count++;
            } catch (SQLException e) {
                if (e.getMessage().contains("Duplicate entry")) {
                    continue;
                } else {
                    throw e;
                }
            }
        }
        return count;
    }
    
    // Helper methods to get existing data - now use the proper DAOs
    private List<Clans> getAllClans(Connection cxn) throws SQLException {
        return ClansDao.getAllClans(cxn);
    }
    
    private List<Weapons> getAllWeapons(Connection cxn) throws SQLException {
        return WeaponsDao.getAllWeapons(cxn);
    }
    
    private List<Statistics> getAllStatistics(Connection cxn) throws SQLException {
        return AllDataDao.getAllStatistics(cxn);
    }
    
    private List<Currencies> getAllCurrencies(Connection cxn) throws SQLException {
        return AllDataDao.getAllCurrencies(cxn);
    }
    
    private List<Gears> getAllGears(Connection cxn) throws SQLException {
        return AllDataDao.getAllGears(cxn);
    }
    
    private List<Consumables> getAllConsumables(Connection cxn) throws SQLException {
        return AllDataDao.getAllConsumables(cxn);
    }
    
    private void printDynamicSummary() {
        System.out.println("\n📋 DYNAMIC ETL SUMMARY:");
        System.out.println("========================");
        System.out.println("👥 Players: " + createdPlayers.size());
        System.out.println("🦸 Characters: " + createdCharacters.size());
        System.out.println("📈 Total Records: " + recordsProcessed);
        System.out.println("========================");
        System.out.println("🎮 Dynamic WoW Data added successfully!");
    }
}