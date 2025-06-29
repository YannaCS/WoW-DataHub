package game.service;

import game.dal.*;
import game.model.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Centralized business rules validation service
 */
public class BusinessRulesService {
    
    /**
     * Custom exception for business rule violations
     */
    public static class BusinessRuleException extends Exception {
        public BusinessRuleException(String message) {
            super(message);
        }
        
        public BusinessRuleException(String message, Throwable cause) {
            super(message, cause);
        }
    }
    
    /**
     * Validates and equips a weapon, ensuring all business rules
     */
    public static void equipWeapon(Connection cxn, Characters character, Weapons weapon) 
            throws SQLException, BusinessRuleException {
        
        // 1. Check level requirement
        validateLevelRequirement(cxn, character, weapon);
        
        // 2. Check job compatibility
        validateJobCompatibility(cxn, character, weapon);
        
        // 3. Check if character owns this weapon in inventory
        validateWeaponOwnership(cxn, character, weapon);
        
        // 4. Update character's equipped weapon
        CharactersDao.updateWeaponWeared(cxn, character, weapon);
        
        System.out.println("✅ Successfully equipped " + weapon.getItemName() + " for " + character.getFirstName());
        System.out.println("🎮 " + character.getFirstName() + " is now playing as " + weapon.getWearableJob());
    }
    
    /**
     * Validates character owns the weapon in their inventory
     */
    public static void validateWeaponOwnership(Connection cxn, Characters character, Weapons weapon) 
            throws SQLException, BusinessRuleException {
        
        Inventory weaponInInventory = InventoryDao.getInventoryByCharactersAndInstance(cxn, character, weapon.getItemID());
        
        if (weaponInInventory == null) {
            throw new BusinessRuleException(
                String.format("Character %s does not own weapon %s in their inventory", 
                    character.getFirstName(), weapon.getItemName())
            );
        }
    }
    
    /**
     * Validates character meets level requirement for equipment
     */
    public static void validateLevelRequirement(Connection cxn, Characters character, Equipments equipment) 
            throws SQLException, BusinessRuleException {
        
        // Get character's highest job level
        List<CharacterUnlockedJob> jobs = CharacterUnlockedJobDao.getCharacterUnlockedJobByCharID(cxn, character.getCharID());
        
        int maxLevel = 1;
        for (CharacterUnlockedJob job : jobs) {
            if (job.getJobLevel() != null && job.getJobLevel() > maxLevel) {
                maxLevel = job.getJobLevel();
            }
        }
        
        if (maxLevel < equipment.getRequiredLevel()) {
            throw new BusinessRuleException(
                String.format("Character level %d is too low to equip %s (requires level %d)", 
                    maxLevel, equipment.getItemName(), equipment.getRequiredLevel())
            );
        }
    }
    
    /**
     * Validates job compatibility for weapons
     */
    public static void validateJobCompatibility(Connection cxn, Characters character, Weapons weapon) 
            throws SQLException, BusinessRuleException {
        
        // Check if character has the required job unlocked
        List<CharacterUnlockedJob> unlockedJobs = CharacterUnlockedJobDao.getCharacterUnlockedJobByCharID(cxn, character.getCharID());
        
        boolean hasRequiredJob = false;
        for (CharacterUnlockedJob job : unlockedJobs) {
            if (weapon.getWearableJob().equals(job.getJob())) {
                hasRequiredJob = true;
                break;
            }
        }
        
        if (!hasRequiredJob) {
            throw new BusinessRuleException(
                String.format("Character must have %s job unlocked to equip %s", 
                    weapon.getWearableJob(), weapon.getItemName())
            );
        }
    }
    
    /**
     * Validates currency transaction respects caps
     */
    public static void validateCurrencyTransaction(Connection cxn, Characters character, 
            Currencies currency, BigDecimal newAmount, BigDecimal weeklyAddition) 
            throws SQLException, BusinessRuleException {
        
        // Check total cap
        if (currency.getCap() != null && newAmount.compareTo(currency.getCap()) > 0) {
            throw new BusinessRuleException(
                String.format("Amount %s exceeds currency cap of %s for %s", 
                    newAmount, currency.getCap(), currency.getCurrencyName())
            );
        }
        
        // Check weekly cap
        if (currency.getWeeklyCap() != null && weeklyAddition != null) {
            CharacterWealth currentWealth = CharacterWealthDao.getCharacterWealthByCharacterAndCurrency(cxn, character, currency);
            BigDecimal currentWeekly = currentWealth != null ? 
                (currentWealth.getWeeklyAcquired() != null ? currentWealth.getWeeklyAcquired() : BigDecimal.ZERO) : 
                BigDecimal.ZERO;
            
            BigDecimal newWeeklyTotal = currentWeekly.add(weeklyAddition);
            
            if (newWeeklyTotal.compareTo(currency.getWeeklyCap()) > 0) {
                throw new BusinessRuleException(
                    String.format("Weekly acquisition %s would exceed weekly cap of %s for %s", 
                        newWeeklyTotal, currency.getWeeklyCap(), currency.getCurrencyName())
                );
            }
        }
    }
    
    /**
     * Ensures character always has at least one job unlocked
     */
    public static void validateJobUnlocks(Connection cxn, Characters character) 
            throws SQLException, BusinessRuleException {
        
        List<CharacterUnlockedJob> jobs = CharacterUnlockedJobDao.getCharacterUnlockedJobByCharID(cxn, character.getCharID());
        
        if (jobs.isEmpty()) {
            throw new BusinessRuleException("Character must have at least one job unlocked");
        }
    }
    
    /**
     * Validates inventory stack size limits
     */
    public static void validateInventoryAddition(Connection cxn, Items item, int quantity) 
            throws SQLException, BusinessRuleException {
        
        if (quantity > item.getMaxStackSize()) {
            throw new BusinessRuleException(
                String.format("Quantity %d exceeds max stack size of %d for %s", 
                    quantity, item.getMaxStackSize(), item.getItemName())
            );
        }
        
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity must be positive");
        }
    }
    
    /**
     * Validates character name uniqueness
     */
    public static void validateCharacterNameUniqueness(Connection cxn, String firstName, String lastName, Integer excludeCharID) 
            throws SQLException, BusinessRuleException {
        
        // Check if combination already exists (excluding current character if updating)
        var stmt = cxn.prepareStatement(
            "SELECT charID FROM Characters WHERE firstName = ? AND lastName = ?" + 
            (excludeCharID != null ? " AND charID != ?" : "")
        );
        
        stmt.setString(1, firstName);
        stmt.setString(2, lastName);
        if (excludeCharID != null) {
            stmt.setInt(3, excludeCharID);
        }
        
        var rs = stmt.executeQuery();
        if (rs.next()) {
            throw new BusinessRuleException(
                String.format("Character name '%s %s' already exists. Character names must be unique.", 
                    firstName, lastName)
            );
        }
        
        rs.close();
        stmt.close();
    }
    
    /**
     * Validates equipment slot compatibility
     */
    public static void validateEquipmentSlot(Connection cxn, Items item, String equipPosition) 
            throws SQLException, BusinessRuleException {
        
        // Main hand slot can only have weapons
        if ("MAIN_HAND".equals(equipPosition)) {
            Weapons weapon = WeaponsDao.getWeaponByItemID(cxn, item.getItemID());
            if (weapon == null) {
                throw new BusinessRuleException(
                    String.format("Item %s cannot be equipped in MAIN_HAND slot - only weapons allowed", 
                        item.getItemName())
                );
            }
        } else {
            // Non-main-hand slots cannot have weapons
            Weapons weapon = WeaponsDao.getWeaponByItemID(cxn, item.getItemID());
            if (weapon != null) {
                throw new BusinessRuleException(
                    String.format("Weapon %s can only be equipped in MAIN_HAND slot", 
                        weapon.getItemName())
                );
            }
        }
        
        // Validate slot name is valid
        String[] validSlots = {"MAIN_HAND", "HEAD", "BODY", "HANDS", "LEGS", "FEET", 
                              "EARRING", "NECKLACE", "WRIST", "RING"};
        boolean validSlot = false;
        for (String slot : validSlots) {
            if (slot.equals(equipPosition)) {
                validSlot = true;
                break;
            }
        }
        
        if (!validSlot) {
            throw new BusinessRuleException(
                String.format("Invalid equipment slot: %s", equipPosition)
            );
        }
    }
    
    /**
     * Creates a character with all business rule validations
     */
    public static Characters createCharacterWithValidation(Connection cxn, Players player, 
            String firstName, String lastName, Clans clan, Weapons weapon) 
            throws SQLException, BusinessRuleException {
        
        // 1. Validate name uniqueness
        validateCharacterNameUniqueness(cxn, firstName, lastName, null);
        
        // 2. Validate starting weapon job compatibility (implicit validation)
        // The weapon's job will become the character's starting job
        
        // 3. Create character
        Characters character = CharactersDao.create(cxn, player, firstName, lastName, clan, weapon);
        
        // 4. Ensure character has at least one job unlocked (the weapon's job)
        CharacterUnlockedJob startingJob = CharacterUnlockedJobDao.create(cxn, character, weapon.getWearableJob(), 1, 0);
        
        // 5. Add weapon to character's inventory (slot 1 for primary weapon)
        InventoryDao.create(cxn, character, 1, weapon, 1);
        
        System.out.println("✅ Created character " + firstName + " " + lastName + " with starting job " + weapon.getWearableJob());
        
        return character;
    }
    
    /**
     * Validates job level and XP consistency
     */
    public static void validateJobProgression(Integer jobLevel, Integer xp) throws BusinessRuleException {
        
        if (xp != null && jobLevel == null) {
            throw new BusinessRuleException("Cannot have XP without job level");
        }
        
        if (jobLevel != null) {
            if (jobLevel < 1 || jobLevel > 100) {
                throw new BusinessRuleException("Job level must be between 1 and 100");
            }
            
            if (xp != null) {
                // Basic XP validation - should be reasonable for the level
                int minXpForLevel = (jobLevel - 1) * 1000;
                int maxXpForLevel = jobLevel * 10000;
                
                if (xp < 0 || xp > maxXpForLevel) {
                    throw new BusinessRuleException(
                        String.format("XP value %d is unrealistic for job level %d", xp, jobLevel)
                    );
                }
            }
        }
    }
    
    /**
     * Validates gear can be equipped by character's unlocked jobs
     */
    public static void validateGearJobCompatibility(Connection cxn, Characters character, Gears gear) 
            throws SQLException, BusinessRuleException {
        
        // Get character's unlocked jobs
        List<CharacterUnlockedJob> unlockedJobs = CharacterUnlockedJobDao.getCharacterUnlockedJobByCharID(cxn, character.getCharID());
        
        // Get jobs that can use this gear
        // Note: This would require a query to JobsForGear table
        // For now, we'll assume gear can be used by any job (this could be enhanced)
        
        if (unlockedJobs.isEmpty()) {
            throw new BusinessRuleException("Character must have at least one job unlocked to equip gear");
        }
    }
    
    /**
     * Validates that all required business rules are met before allowing operations
     */
    public static void validateAllBusinessRules(Connection cxn, Characters character) 
            throws SQLException, BusinessRuleException {
        
        // 1. Character must have at least one job unlocked
        validateJobUnlocks(cxn, character);
        
        // 2. Character must have a weapon equipped
        if (character.getWeaponWeared() == null) {
            throw new BusinessRuleException("Character must have a weapon equipped in MAIN_HAND slot");
        }
        
        // 3. Character's equipped weapon must be compatible with an unlocked job
        validateJobCompatibility(cxn, character, character.getWeaponWeared());
    }
}