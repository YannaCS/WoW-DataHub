package game.model.Analytics;

import java.math.BigDecimal;

public class CurrencyStats {
    private String currencyName;
    private BigDecimal cap;
    private BigDecimal weeklyCap;
    private int playersWithCurrency;
    private BigDecimal avgAmount;
    private BigDecimal maxAmount;
    private BigDecimal totalInCirculation;
    
    public CurrencyStats(String currencyName, BigDecimal cap, BigDecimal weeklyCap,
                        int playersWithCurrency, BigDecimal avgAmount, BigDecimal maxAmount,
                        BigDecimal totalInCirculation) {
        this.currencyName = currencyName;
        this.cap = cap;
        this.weeklyCap = weeklyCap;
        this.playersWithCurrency = playersWithCurrency;
        this.avgAmount = avgAmount;
        this.maxAmount = maxAmount;
        this.totalInCirculation = totalInCirculation;
    }
    
    public String getCurrencyName() { return currencyName; }
    public BigDecimal getCap() { return cap; }
    public BigDecimal getWeeklyCap() { return weeklyCap; }
    public int getPlayersWithCurrency() { return playersWithCurrency; }
    public BigDecimal getAvgAmount() { return avgAmount; }
    public BigDecimal getMaxAmount() { return maxAmount; }
    public BigDecimal getTotalInCirculation() { return totalInCirculation; }
}