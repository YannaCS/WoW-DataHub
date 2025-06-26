package game.etl.dto;

public class WoWCurrencyData {
    private String currencyName;
    private float amount;
    private float weeklyEarned;
    private float cap;
    private float weeklyCap;
    
    // Constructors
    public WoWCurrencyData() {}
    
    public WoWCurrencyData(String currencyName, float amount, float weeklyEarned, float cap, float weeklyCap) {
        this.currencyName = currencyName;
        this.amount = amount;
        this.weeklyEarned = weeklyEarned;
        this.cap = cap;
        this.weeklyCap = weeklyCap;
    }
    
    // Getters and Setters
    public String getCurrencyName() { return currencyName; }
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }
    
    public float getAmount() { return amount; }
    public void setAmount(float amount) { this.amount = amount; }
    
    public float getWeeklyEarned() { return weeklyEarned; }
    public void setWeeklyEarned(float weeklyEarned) { this.weeklyEarned = weeklyEarned; }
    
    public float getCap() { return cap; }
    public void setCap(float cap) { this.cap = cap; }
    
    public float getWeeklyCap() { return weeklyCap; }
    public void setWeeklyCap(float weeklyCap) { this.weeklyCap = weeklyCap; }
}