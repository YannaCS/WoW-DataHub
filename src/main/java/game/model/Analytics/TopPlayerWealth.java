package game.model.Analytics;

import java.math.BigDecimal;

public class TopPlayerWealth {
    private String characterName;
    private BigDecimal totalWealth;
    private int currencyTypes;
    
    public TopPlayerWealth(String characterName, BigDecimal totalWealth, int currencyTypes) {
        this.characterName = characterName;
        this.totalWealth = totalWealth;
        this.currencyTypes = currencyTypes;
    }
    
    public String getCharacterName() { return characterName; }
    public BigDecimal getTotalWealth() { return totalWealth; }
    public int getCurrencyTypes() { return currencyTypes; }
}