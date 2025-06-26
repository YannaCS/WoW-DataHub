package game.etl;

import java.util.*;

import game.etl.dto.*;

/**
 * Simulates WoW API data for testing the ETL process
 * In a real implementation, this would be replaced by actual WoW API calls
 */
public class WoWDataSimulator {
    
    private static final String[] WOW_CLASSES = {
        "Warrior", "Paladin", "Death Knight", "Hunter", "Rogue", 
        "Priest", "Shaman", "Mage", "Warlock", "Monk", "Druid", "Demon Hunter"
    };
    
    private static final String[] WOW_RACES = {
        "Human", "Orc", "Dwarf", "Night Elf", "Blood Elf", "Goblin"
    };
    
    private static final String[] GUILD_NAMES = {
        "Knights of Azeroth", "Horde Legends", "Alliance Elite", "Stormwind Guards",
        "Orgrimmar Warriors", "Ironforge Militia", "Shadow Council", "Light's Hope"
    };
    
    private static final String[] WEAPON_NAMES = {
        "Shadowmourne", "Ashbringer", "Frostmourne", "Doomhammer", "Atiesh",
        "Sulfuras", "Thunderfury", "Warglaives of Azzinoth", "Gorehowl"
    };
    
    private static final String[] ARMOR_NAMES = {
        "Crown of Destruction", "Chestplate of Might", "Legplates of the Apocalypse",
        "Shoulders of the Champion", "Gauntlets of Power"
    };
    
    private Random random = new Random();
    
    /**
     * Generate sample WoW character data for testing
     */
    public List<WoWCharacterData> generateSampleCharacters(int count) {
        List<WoWCharacterData> characters = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            characters.add(generateCharacter(i + 1));
        }
        
        return characters;
    }
    
    private WoWCharacterData generateCharacter(int index) {
        WoWCharacterData character = new WoWCharacterData();
        
        // Basic character info
        character.setBattleTag("Player" + index + "#" + (1000 + random.nextInt(9000)));
        character.setCharacterName(generateCharacterName());
        character.setRealm("Stormrage");
        character.setCharacterClass(WOW_CLASSES[random.nextInt(WOW_CLASSES.length)]);
        character.setRace(WOW_RACES[random.nextInt(WOW_RACES.length)]);
        character.setLevel(random.nextInt(70) + 10); // Level 10-80
        character.setGuildName(GUILD_NAMES[random.nextInt(GUILD_NAMES.length)]);
        
        // Player info
        character.setFirstName("Player");
        character.setLastName("" + index);
        character.setEmail("player" + index + "@example.com");
        
        // Equipment
        character.setEquipment(generateEquipment(character.getLevel()));
        
        // Currencies
        character.setCurrencies(generateCurrencies());
        
        // Stats
        character.setStats(generateStats(character.getCharacterClass(), character.getLevel()));
        
        return character;
    }
    
    private String generateCharacterName() {
        String[] prefixes = {"Thral", "Jain", "Uthe", "Arthas", "Varian", "Sylvan", "Kael", "Illid"};
        String[] suffixes = {"los", "na", "r", "as", "wrynn", "as", "thas", "an"};
        
        return prefixes[random.nextInt(prefixes.length)] + 
               suffixes[random.nextInt(suffixes.length)];
    }
    
    private WoWEquipmentData generateEquipment(int characterLevel) {
        WoWEquipmentData equipment = new WoWEquipmentData();
        List<WoWItemData> allItems = new ArrayList<>();
        
        // Main hand weapon
        WoWItemData mainHand = generateWeapon(characterLevel, "Two-Handed Sword");
        equipment.setMainHand(mainHand);
        allItems.add(mainHand);
        
        // Armor pieces
        WoWItemData head = generateArmor(characterLevel, "Head", "Helm");
        WoWItemData chest = generateArmor(characterLevel, "Chest", "Chestplate");
        WoWItemData legs = generateArmor(characterLevel, "Legs", "Legplates");
        
        equipment.setHead(head);
        equipment.setChest(chest);
        equipment.setLegs(legs);
        
        allItems.add(head);
        allItems.add(chest);
        allItems.add(legs);
        
        equipment.setAllItems(allItems);
        return equipment;
    }
    
    private WoWItemData generateWeapon(int characterLevel, String weaponType) {
        WoWItemData weapon = new WoWItemData();
        
        weapon.setItemId(10000 + random.nextInt(90000));
        weapon.setName(WEAPON_NAMES[random.nextInt(WEAPON_NAMES.length)]);
        weapon.setItemLevel(Math.max(characterLevel + random.nextInt(20) - 10, 1));
        weapon.setItemClass("Weapon");
        weapon.setItemSubClass(weaponType);
        weapon.setRequiredLevel(Math.max(characterLevel - 5, 1));
        weapon.setEstimatedValue(weapon.getItemLevel() * 15.0 + random.nextDouble() * 100);
        
        // Weapon stats
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Attack Power", weapon.getItemLevel() * 2 + random.nextInt(50));
        stats.put("Stamina", weapon.getItemLevel() + random.nextInt(20));
        weapon.setStats(stats);
        
        return weapon;
    }
    
    private WoWItemData generateArmor(int characterLevel, String slot, String type) {
        WoWItemData armor = new WoWItemData();
        
        armor.setItemId(20000 + random.nextInt(80000));
        armor.setName(ARMOR_NAMES[random.nextInt(ARMOR_NAMES.length)] + " of " + slot);
        armor.setItemLevel(Math.max(characterLevel + random.nextInt(15) - 5, 1));
        armor.setItemClass("Armor");
        armor.setItemSubClass(type);
        armor.setRequiredLevel(Math.max(characterLevel - 3, 1));
        armor.setEstimatedValue(armor.getItemLevel() * 8.0 + random.nextDouble() * 50);
        
        // Armor stats
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Armor", armor.getItemLevel() * 10 + random.nextInt(100));
        stats.put("Stamina", armor.getItemLevel() / 2 + random.nextInt(15));
        
        // Add random primary stat
        String[] primaryStats = {"Strength", "Agility", "Intelligence"};
        stats.put(primaryStats[random.nextInt(primaryStats.length)], 
                  armor.getItemLevel() / 3 + random.nextInt(10));
        
        armor.setStats(stats);
        
        return armor;
    }
    
    private List<WoWCurrencyData> generateCurrencies() {
        List<WoWCurrencyData> currencies = new ArrayList<>();
        
        // Gold
        currencies.add(new WoWCurrencyData("Gold", 
            random.nextFloat() * 10000, // amount
            random.nextFloat() * 500,   // weekly earned
            999999f,                    // cap
            0f));                       // weekly cap
        
        // Honor
        currencies.add(new WoWCurrencyData("Honor", 
            random.nextFloat() * 8000,
            random.nextFloat() * 2000,
            15000f,
            0f));
        
        // Justice Points
        currencies.add(new WoWCurrencyData("Justice Points", 
            random.nextFloat() * 3000,
            random.nextFloat() * 400,
            4000f,
            0f));
        
        // Valor Points
        currencies.add(new WoWCurrencyData("Valor Points", 
            random.nextFloat() * 2000,
            random.nextFloat() * 600,
            3000f,
            1000f));
        
        return currencies;
    }
    
    private Map<String, Integer> generateStats(String characterClass, int level) {
        Map<String, Integer> stats = new HashMap<>();
        
        // Base stats that scale with level
        int baseStatValue = level * 2 + 10;
        
        stats.put("Stamina", baseStatValue + random.nextInt(50));
        
        // Primary stat based on class
        String primaryStat = getPrimaryStat(characterClass);
        stats.put(primaryStat, baseStatValue + 20 + random.nextInt(30));
        
        // Secondary stats
        stats.put("Spirit", baseStatValue / 2 + random.nextInt(20));
        
        // Combat stats
        stats.put("Attack Power", level * 5 + random.nextInt(100));
        stats.put("Spell Power", level * 4 + random.nextInt(80));
        
        return stats;
    }
    
    private String getPrimaryStat(String characterClass) {
        return switch(characterClass.toLowerCase()) {
            case "warrior", "paladin", "death knight" -> "Strength";
            case "hunter", "rogue", "monk", "demon hunter" -> "Agility";
            case "priest", "shaman", "mage", "warlock", "druid" -> "Intelligence";
            default -> "Strength";
        };
    }
    
    /**
     * Simulate fetching data from WoW API
     */
    public List<WoWCharacterData> fetchFromWoWAPI() {
        // In a real implementation, this would make HTTP calls to Battle.net API
        // For now, return simulated data
        System.out.println("Simulating WoW API call...");
        return generateSampleCharacters(5);
    }
}