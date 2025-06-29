package game.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Creates SQL triggers to enforce critical business rules
 */
public class BusinessRuleTriggers {
    
    /**
     * Creates all business rule triggers
     */
    public static void createAllTriggers(Connection cxn) throws SQLException {
        System.out.println("📋 Creating business rule triggers...");
        
        // 1. Currency cap enforcement triggers
        createCurrencyCapTriggers(cxn);
        
        // 2. Inventory stack size triggers
        createInventoryStackTriggers(cxn);
        
        // 3. Equipment slot validation triggers
        createEquipmentSlotTriggers(cxn);
        
        // 4. Character business rule triggers
        createCharacterBusinessRuleTriggers(cxn);
        
        // 5. Job unlocking validation triggers
        createJobValidationTriggers(cxn);
        
        System.out.println("✅ All business rule triggers created successfully!");
    }
    
    /**
     * Creates triggers to enforce currency caps
     */
    private static void createCurrencyCapTriggers(Connection cxn) throws SQLException {
        
        // Trigger for INSERT on CharacterWealth
        String insertTrigger = """
            CREATE TRIGGER enforce_currency_caps_insert
            BEFORE INSERT ON CharacterWealth
            FOR EACH ROW
            BEGIN
                DECLARE currency_cap DECIMAL(10,2);
                DECLARE weekly_cap DECIMAL(10,2);
                DECLARE error_msg VARCHAR(255);
                
                -- Get currency limits
                SELECT cap, weeklyCap INTO currency_cap, weekly_cap
                FROM Currencies 
                WHERE currencyName = NEW.currencyName;
                
                -- Check total amount cap
                IF currency_cap IS NOT NULL AND NEW.amount > currency_cap THEN
                    SET error_msg = CONCAT('Amount ', NEW.amount, ' exceeds currency cap of ', currency_cap, ' for ', NEW.currencyName);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                -- Check weekly acquisition cap
                IF weekly_cap IS NOT NULL AND NEW.weeklyAcquired IS NOT NULL AND NEW.weeklyAcquired > weekly_cap THEN
                    SET error_msg = CONCAT('Weekly acquired ', NEW.weeklyAcquired, ' exceeds cap of ', weekly_cap, ' for ', NEW.currencyName);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        // Trigger for UPDATE on CharacterWealth
        String updateTrigger = """
            CREATE TRIGGER enforce_currency_caps_update
            BEFORE UPDATE ON CharacterWealth
            FOR EACH ROW
            BEGIN
                DECLARE currency_cap DECIMAL(10,2);
                DECLARE weekly_cap DECIMAL(10,2);
                DECLARE error_msg VARCHAR(255);
                
                SELECT cap, weeklyCap INTO currency_cap, weekly_cap
                FROM Currencies 
                WHERE currencyName = NEW.currencyName;
                
                IF currency_cap IS NOT NULL AND NEW.amount > currency_cap THEN
                    SET error_msg = CONCAT('Amount ', NEW.amount, ' exceeds currency cap of ', currency_cap, ' for ', NEW.currencyName);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                IF weekly_cap IS NOT NULL AND NEW.weeklyAcquired IS NOT NULL AND NEW.weeklyAcquired > weekly_cap THEN
                    SET error_msg = CONCAT('Weekly acquired ', NEW.weeklyAcquired, ' exceeds cap of ', weekly_cap, ' for ', NEW.currencyName);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        try {
            cxn.createStatement().executeUpdate(insertTrigger);
            cxn.createStatement().executeUpdate(updateTrigger);
            System.out.println("✅ Currency cap triggers created");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
            System.out.println("⚠️ Currency cap triggers already exist");
        }
    }
    
    /**
     * Creates triggers to enforce inventory stack size limits
     */
    private static void createInventoryStackTriggers(Connection cxn) throws SQLException {
        
        String insertTrigger = """
            CREATE TRIGGER enforce_stack_size_insert
            BEFORE INSERT ON Inventory
            FOR EACH ROW
            BEGIN
                DECLARE max_stack_size INT;
                DECLARE item_name VARCHAR(255);
                DECLARE error_msg VARCHAR(255);
                
                -- Get max stack size for the item
                SELECT maxStackSize, itemName INTO max_stack_size, item_name
                FROM Items 
                WHERE itemID = NEW.instance;
                
                -- Check if quantity exceeds max stack size
                IF NEW.quantity > max_stack_size THEN
                    SET error_msg = CONCAT('Quantity ', NEW.quantity, ' exceeds max stack size of ', max_stack_size, ' for ', item_name);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        String updateTrigger = """
            CREATE TRIGGER enforce_stack_size_update
            BEFORE UPDATE ON Inventory
            FOR EACH ROW
            BEGIN
                DECLARE max_stack_size INT;
                DECLARE item_name VARCHAR(255);
                DECLARE error_msg VARCHAR(255);
                
                SELECT maxStackSize, itemName INTO max_stack_size, item_name
                FROM Items 
                WHERE itemID = NEW.instance;
                
                IF NEW.quantity > max_stack_size THEN
                    SET error_msg = CONCAT('Quantity ', NEW.quantity, ' exceeds max stack size of ', max_stack_size, ' for ', item_name);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        try {
            cxn.createStatement().executeUpdate(insertTrigger);
            cxn.createStatement().executeUpdate(updateTrigger);
            System.out.println("✅ Inventory stack size triggers created");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
            System.out.println("⚠️ Inventory stack size triggers already exist");
        }
    }
    
    /**
     * Creates triggers to validate equipment slot rules
     */
    private static void createEquipmentSlotTriggers(Connection cxn) throws SQLException {
        
        String equipmentTrigger = """
            CREATE TRIGGER validate_equipment_slots
            BEFORE INSERT ON EquippedItems
            FOR EACH ROW
            BEGIN
                DECLARE item_name VARCHAR(255);
                DECLARE error_msg VARCHAR(255);
                
                -- Get item name for error messages
                SELECT itemName INTO item_name
                FROM Items 
                WHERE itemID = NEW.itemID;
                
                -- Validate that main hand slot cannot have gear items (only weapons)
                IF NEW.equipPosition = 'MAIN_HAND' THEN
                    -- Check if the item is actually a weapon
                    IF NOT EXISTS (SELECT 1 FROM Weapons WHERE itemID = NEW.itemID) THEN
                        SET error_msg = CONCAT('Item ', item_name, ' cannot be equipped in MAIN_HAND slot - only weapons allowed');
                        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                    END IF;
                END IF;
                
                -- Validate that non-main-hand slots cannot have weapons
                IF NEW.equipPosition != 'MAIN_HAND' THEN
                    IF EXISTS (SELECT 1 FROM Weapons WHERE itemID = NEW.itemID) THEN
                        SET error_msg = CONCAT('Weapon ', item_name, ' can only be equipped in MAIN_HAND slot');
                        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                    END IF;
                END IF;
            END
        """;
        
        try {
            cxn.createStatement().executeUpdate(equipmentTrigger);
            System.out.println("✅ Equipment slot triggers created");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
            System.out.println("⚠️ Equipment slot triggers already exist");
        }
    }
    
    /**
     * Creates triggers for character business rules
     */
    private static void createCharacterBusinessRuleTriggers(Connection cxn) throws SQLException {
        
        // Trigger to ensure character always has a weapon equipped
        String weaponRequiredTrigger = """
            CREATE TRIGGER enforce_weapon_required
            BEFORE UPDATE ON Characters
            FOR EACH ROW
            BEGIN
                DECLARE error_msg VARCHAR(255);
                
                -- Check if trying to set weaponWeared to NULL
                IF NEW.weaponWeared IS NULL THEN
                    SET error_msg = 'Character must always have a weapon equipped in MAIN_HAND slot';
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                -- Check if the weapon exists
                IF NOT EXISTS (SELECT 1 FROM Weapons WHERE itemID = NEW.weaponWeared) THEN
                    SET error_msg = CONCAT('Weapon with ID ', NEW.weaponWeared, ' does not exist');
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        try {
            cxn.createStatement().executeUpdate(weaponRequiredTrigger);
            System.out.println("✅ Character business rule triggers created");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
            System.out.println("⚠️ Character business rule triggers already exist");
        }
    }
    
    /**
     * Creates triggers for job validation
     */
    private static void createJobValidationTriggers(Connection cxn) throws SQLException {
        
        // Trigger to validate job level and XP consistency
        String jobProgressionTrigger = """
            CREATE TRIGGER validate_job_progression
            BEFORE INSERT ON CharacterUnlockedJob
            FOR EACH ROW
            BEGIN
                DECLARE error_msg VARCHAR(255);
                
                -- Check XP without job level
                IF NEW.XP IS NOT NULL AND NEW.jobLevel IS NULL THEN
                    SET error_msg = 'Cannot have XP without job level';
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                -- Check job level range
                IF NEW.jobLevel IS NOT NULL AND (NEW.jobLevel < 1 OR NEW.jobLevel > 100) THEN
                    SET error_msg = CONCAT('Job level must be between 1 and 100, got: ', NEW.jobLevel);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                -- Check XP is non-negative
                IF NEW.XP IS NOT NULL AND NEW.XP < 0 THEN
                    SET error_msg = CONCAT('XP cannot be negative, got: ', NEW.XP);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        String jobProgressionUpdateTrigger = """
            CREATE TRIGGER validate_job_progression_update
            BEFORE UPDATE ON CharacterUnlockedJob
            FOR EACH ROW
            BEGIN
                DECLARE error_msg VARCHAR(255);
                
                -- Check XP without job level
                IF NEW.XP IS NOT NULL AND NEW.jobLevel IS NULL THEN
                    SET error_msg = 'Cannot have XP without job level';
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                -- Check job level range
                IF NEW.jobLevel IS NOT NULL AND (NEW.jobLevel < 1 OR NEW.jobLevel > 100) THEN
                    SET error_msg = CONCAT('Job level must be between 1 and 100, got: ', NEW.jobLevel);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
                
                -- Check XP is non-negative
                IF NEW.XP IS NOT NULL AND NEW.XP < 0 THEN
                    SET error_msg = CONCAT('XP cannot be negative, got: ', NEW.XP);
                    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = error_msg;
                END IF;
            END
        """;
        
        try {
            cxn.createStatement().executeUpdate(jobProgressionTrigger);
            cxn.createStatement().executeUpdate(jobProgressionUpdateTrigger);
            System.out.println("✅ Job validation triggers created");
        } catch (SQLException e) {
            if (!e.getMessage().contains("already exists")) {
                throw e;
            }
            System.out.println("⚠️ Job validation triggers already exist");
        }
    }
    /**
     * Drops all business rule triggers (for testing/reset)
     */
    public static void dropAllTriggers(Connection cxn) throws SQLException {
        String[] triggerNames = {
            "enforce_currency_caps_insert",
            "enforce_currency_caps_update", 
            "enforce_stack_size_insert",
            "enforce_stack_size_update",
            "validate_equipment_slots",
            "enforce_weapon_required",
            "validate_job_progression",
            "validate_job_progression_update"
        };
        
        for (String triggerName : triggerNames) {
            try {
                cxn.createStatement().executeUpdate("DROP TRIGGER IF EXISTS " + triggerName);
            } catch (SQLException e) {
                // Ignore if trigger doesn't exist
            }
        }
        
        System.out.println("🗑️ All business rule triggers dropped");
    }
}