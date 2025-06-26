package game.etl.dto;

import java.util.List;
import java.util.Map;

public class WoWCharacterData {
    private String battleTag;
    private String characterName;
    private String realm;
    private String characterClass;
    private String race;
    private int level;
    private String guildName;
    private WoWEquipmentData equipment;
    private List<WoWCurrencyData> currencies;
    private Map<String, Integer> stats;
    private String firstName;
    private String lastName;
    private String email;
    
    // Constructors
    public WoWCharacterData() {}
    
    public WoWCharacterData(String battleTag, String characterName, String realm, 
                           String characterClass, String race, int level, String guildName) {
        this.battleTag = battleTag;
        this.characterName = characterName;
        this.realm = realm;
        this.characterClass = characterClass;
        this.race = race;
        this.level = level;
        this.guildName = guildName;
    }
    
    // Getters and Setters
    public String getBattleTag() { return battleTag; }
    public void setBattleTag(String battleTag) { this.battleTag = battleTag; }
    
    public String getCharacterName() { return characterName; }
    public void setCharacterName(String characterName) { this.characterName = characterName; }
    
    public String getRealm() { return realm; }
    public void setRealm(String realm) { this.realm = realm; }
    
    public String getCharacterClass() { return characterClass; }
    public void setCharacterClass(String characterClass) { this.characterClass = characterClass; }
    
    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public String getGuildName() { return guildName; }
    public void setGuildName(String guildName) { this.guildName = guildName; }
    
    public WoWEquipmentData getEquipment() { return equipment; }
    public void setEquipment(WoWEquipmentData equipment) { this.equipment = equipment; }
    
    public List<WoWCurrencyData> getCurrencies() { return currencies; }
    public void setCurrencies(List<WoWCurrencyData> currencies) { this.currencies = currencies; }
    
    public Map<String, Integer> getStats() { return stats; }
    public void setStats(Map<String, Integer> stats) { this.stats = stats; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}