package game.etl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import game.dal.*;
import game.model.*;
import game.etl.dto.*;

public class WoWETLProcessor {
    
    private static final Logger logger = Logger.getLogger(WoWETLProcessor.class.getName());
    
    /**
     * Main ETL process - Extract, Transform, Load WoW data
     */
    public void processWoWData(List<WoWCharacterData> wowCharacters) throws SQLException {
        logger.info("Starting WoW ETL process for " + wowCharacters.size() + " characters");
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            // First, ensure required reference data exists
            setupReferenceData(cxn);
            
            // Process each character
            for (WoWCharacterData wowData : wowCharacters) {
                try {
                    loadCharacterData(cxn, wowData);
                    logger.info("Successfully processed character: " + wowData.getCharacterName());
                } catch (Exception e) {
                    logger.severe("Failed to process character " + wowData.getCharacterName() + ": " + e.getMessage());
                    // Continue with other characters
                }
            }
        }
        
        logger.info("WoW ETL process completed");
    }
    
    /**
     * Load individual character data into database
     */
    private void loadCharacterData(Connection cxn, WoWCharacterData wowData) throws SQLException {
        
        // 1. Load/Find Player (using battleTag as unique identifier)
        Players player = findOrCreatePlayer(cxn, wowData);
        
        // 2. Load/Find Clan (based on guild)
        Clans clan = findOrCreateClan(cxn, wowData);
        
        // 3. Create main hand weapon (required for character creation)
        Weapons mainWeapon = null;
        if (wowData.getEquipment() != null && wowData.getEquipment().getMainHand() != null) {
            mainWeapon = transformWeapon(cxn, wowData.getEquipment().getMainHand(), wowData.getCharacterClass());
        } else {
            // Create default weapon if none equipped
            mainWeapon = createDefaultWeapon(cxn, wowData.getCharacterClass());
        }
        
        // 4. Create Character
        Characters character = CharactersDao.create(cxn,
            player,
            wowData.getCharacterName(),
            "", // WoW doesn't have separate last names for characters
            clan,
            mainWeapon);
        
        // 5. Load Job Data (WoW class → Job)
        loadJobData(cxn, character, wowData);
        
        // 6. Load Currencies
        loadCurrencyData(cxn, character, wowData.getCurrencies());
        
        // 7. Load Equipment and Stats
        loadEquipmentAndStats(cxn, character, wowData);
    }
    
    private Players findOrCreatePlayer(Connection cxn, WoWCharacterData wowData) throws SQLException {
        // In a real implementation, you'd search by battleTag or email
        // For now, create a new player each time
        return PlayersDao.create(cxn,
            wowData.getFirstName() != null ? wowData.getFirstName() : "WoW",
            wowData.getLastName() != null ? wowData.getLastName() : "Player",
            wowData.getEmail() != null ? wowData.getEmail() : wowData.getBattleTag() + "@battle.net");
    }
    
    private Clans findOrCreateClan(Connection cxn, WoWCharacterData wowData) throws SQLException {
        String clanName = wowData.getGuildName() != null ? wowData.getGuildName() : "Unguilded";
        Clans.Races race = mapWoWRace(wowData.getRace());
        
        // Try to find existing clan, create if not found
        try {
            return ClansDao.getClanByClanName(cxn, clanName);
        } catch (Exception e) {
            // Clan doesn't exist, create it
            return ClansDao.create(cxn, clanName, race);
        }
    }
    
    private Weapons transformWeapon(Connection cxn, WoWItemData itemData, String characterClass) throws SQLException {
        String jobRestriction = mapWoWClassToJob(characterClass);
        int damage = calculateWeaponDamage(itemData);
        double price = itemData.getEstimatedValue() > 0 ? itemData.getEstimatedValue() : estimateItemValue(itemData);
        
        return WeaponsDao.create(cxn,
            itemData.getName(),
            itemData.getItemLevel(),
            1, // Max stack size for weapons is typically 1
            price,
            itemData.getRequiredLevel(),
            jobRestriction,
            damage);
    }
    
    private Weapons createDefaultWeapon(Connection cxn, String characterClass) throws SQLException {
        String jobName = mapWoWClassToJob(characterClass);
        String weaponName = "Basic " + jobName + " Weapon";
        
        return WeaponsDao.create(cxn,
            weaponName,
            1, // Level 1 weapon
            1, // Max stack size
            10.0, // Basic price
            1, // Required level
            jobName,
            5); // Basic damage
    }
    
    private void loadJobData(Connection cxn, Characters character, WoWCharacterData wowData) throws SQLException {
        String jobName = mapWoWClassToJob(wowData.getCharacterClass());
        int xp = calculateXPFromLevel(wowData.getLevel());
        
        CharacterUnlockedJobDao.create(cxn,
            character,
            jobName,
            wowData.getLevel(),
            xp);
    }
    
    private void loadCurrencyData(Connection cxn, Characters character, List<WoWCurrencyData> currencies) throws SQLException {
        if (currencies == null) return;
        
        for (WoWCurrencyData currencyData : currencies) {
            // Ensure currency exists in database
            Currencies currency = findOrCreateCurrency(cxn, currencyData);
            
            // Create character wealth record
            CharacterWealthDao.create(cxn,
                character,
                currency,
                currencyData.getAmount(),
                currencyData.getWeeklyEarned());
        }
    }
    
    private Currencies findOrCreateCurrency(Connection cxn, WoWCurrencyData currencyData) throws SQLException {
        try {
            return CurrenciesDao.getCurrenciesByName(cxn, currencyData.getCurrencyName());
        } catch (Exception e) {
            // Currency doesn't exist, create it
            return CurrenciesDao.create(cxn,
                currencyData.getCurrencyName(),
                currencyData.getCap(),
                currencyData.getWeeklyCap());
        }
    }
    
    private void loadEquipmentAndStats(Connection cxn, Characters character, WoWCharacterData wowData) throws SQLException {
        // Load character statistics
        if (wowData.getStats() != null) {
            for (Map.Entry<String, Integer> stat : wowData.getStats().entrySet()) {
                Statistics statistics = findOrCreateStatistic(cxn, stat.getKey());
                CharacterStatisticsDao.create(cxn, character, statistics, stat.getValue());
            }
        }
    }
    
    private Statistics findOrCreateStatistic(Connection cxn, String statName) throws SQLException {
        try {
            return StatisticsDao.getStatisticsByName(cxn, statName);
        } catch (Exception e) {
            return StatisticsDao.create(cxn, statName, "Imported from WoW");
        }
    }
    
    // Mapping methods
    private Clans.Races mapWoWRace(String wowRace) {
        if (wowRace == null) return Clans.Races.HUMAN;
        
        return switch(wowRace.toLowerCase()) {
            case "human" -> Clans.Races.HUMAN;
            case "orc" -> Clans.Races.ORC;
            case "dwarf" -> Clans.Races.DWARF;
            case "night elf", "blood elf", "void elf" -> Clans.Races.ELF;
            case "goblin" -> Clans.Races.GOBLIN;
            default -> Clans.Races.HUMAN;
        };
    }
    
    private String mapWoWClassToJob(String wowClass) {
        if (wowClass == null) return "Adventurer";
        
        return switch(wowClass.toLowerCase()) {
            case "warrior" -> "Warrior";
            case "paladin" -> "Paladin";
            case "death knight" -> "Death Knight";
            case "hunter" -> "Hunter";
            case "rogue" -> "Rogue";
            case "priest" -> "Priest";
            case "shaman" -> "Shaman";
            case "mage" -> "Mage";
            case "warlock" -> "Warlock";
            case "monk" -> "Monk";
            case "druid" -> "Druid";
            case "demon hunter" -> "Demon Hunter";
            case "evoker" -> "Evoker";
            default -> "Adventurer";
        };
    }
    
    private int calculateWeaponDamage(WoWItemData item) {
        // Simple damage calculation based on item level and stats
        int baseDamage = item.getItemLevel() / 2;
        
        if (item.getStats() != null) {
            // Add bonus damage from relevant stats
            Integer attackPower = item.getStats().get("Attack Power");
            if (attackPower != null) {
                baseDamage += attackPower / 10;
            }
        }
        
        return Math.max(baseDamage, 1);
    }
    
    private int calculateXPFromLevel(int level) {
        // Simple XP calculation - in reality this would be more complex
        return level * 1000;
    }
    
    private double estimateItemValue(WoWItemData item) {
        // Simple value estimation based on item level
        return item.getItemLevel() * 10.0;
    }
    
    private void setupReferenceData(Connection cxn) throws SQLException {
        // Create basic statistics that are commonly used
        String[] commonStats = {"Strength", "Agility", "Intelligence", "Stamina", "Spirit"};
        
        for (String statName : commonStats) {
            try {
                StatisticsDao.getStatisticsByName(cxn, statName);
            } catch (Exception e) {
                StatisticsDao.create(cxn, statName, "Core character statistic");
            }
        }
        
        // Create common currencies
        Map<String, float[]> currencies = new HashMap<>();
        currencies.put("Gold", new float[]{999999f, 0f}); // cap, weeklyCap
        currencies.put("Honor", new float[]{15000f, 0f});
        currencies.put("Justice Points", new float[]{4000f, 0f});
        currencies.put("Valor Points", new float[]{3000f, 1000f});
        
        for (Map.Entry<String, float[]> currency : currencies.entrySet()) {
            try {
                CurrenciesDao.getCurrenciesByName(cxn, currency.getKey());
            } catch (Exception e) {
                float[] caps = currency.getValue();
                CurrenciesDao.create(cxn, currency.getKey(), caps[0], caps[1]);
            }
        }
    }
}