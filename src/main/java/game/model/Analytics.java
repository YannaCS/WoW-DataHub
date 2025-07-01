package game.model;

import java.math.BigDecimal;

/**
 * Analytics model classes for dashboard views
 */
public class Analytics {
    
    public static class DailyActivePlayer {
        private String activityDate;
        private int activeCount;
        
        public DailyActivePlayer(String activityDate, int activeCount) {
            this.activityDate = activityDate;
            this.activeCount = activeCount;
        }
        
        public String getActivityDate() { return activityDate; }
        public void setActivityDate(String activityDate) { this.activityDate = activityDate; }
        public int getActiveCount() { return activeCount; }
        public void setActiveCount(int activeCount) { this.activeCount = activeCount; }
    }
    
    public static class JobDistribution {
        private String jobName;
        private int characterCount;
        private double percentage;
        
        public JobDistribution(String jobName, int characterCount, double percentage) {
            this.jobName = jobName;
            this.characterCount = characterCount;
            this.percentage = percentage;
        }
        
        public String getJobName() { return jobName; }
        public void setJobName(String jobName) { this.jobName = jobName; }
        public int getCharacterCount() { return characterCount; }
        public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
    
    public static class ClanDistribution {
        private String clanName;
        private String race;
        private int characterCount;
        private double percentage;
        
        public ClanDistribution(String clanName, String race, int characterCount, double percentage) {
            this.clanName = clanName;
            this.race = race;
            this.characterCount = characterCount;
            this.percentage = percentage;
        }
        
        public String getClanName() { return clanName; }
        public void setClanName(String clanName) { this.clanName = clanName; }
        public String getRace() { return race; }
        public void setRace(String race) { this.race = race; }
        public int getCharacterCount() { return characterCount; }
        public void setCharacterCount(int characterCount) { this.characterCount = characterCount; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }
    
    public static class OverallStats {
        private int totalPlayers;
        private int totalCharacters;
        private int totalWeapons;
        private int totalGears;
        private int totalConsumables;
        private int totalClans;
        
        public OverallStats(int totalPlayers, int totalCharacters, int totalWeapons, 
                           int totalGears, int totalConsumables, int totalClans) {
            this.totalPlayers = totalPlayers;
            this.totalCharacters = totalCharacters;
            this.totalWeapons = totalWeapons;
            this.totalGears = totalGears;
            this.totalConsumables = totalConsumables;
            this.totalClans = totalClans;
        }
        
        public int getTotalPlayers() { return totalPlayers; }
        public void setTotalPlayers(int totalPlayers) { this.totalPlayers = totalPlayers; }
        public int getTotalCharacters() { return totalCharacters; }
        public void setTotalCharacters(int totalCharacters) { this.totalCharacters = totalCharacters; }
        public int getTotalWeapons() { return totalWeapons; }
        public void setTotalWeapons(int totalWeapons) { this.totalWeapons = totalWeapons; }
        public int getTotalGears() { return totalGears; }
        public void setTotalGears(int totalGears) { this.totalGears = totalGears; }
        public int getTotalConsumables() { return totalConsumables; }
        public void setTotalConsumables(int totalConsumables) { this.totalConsumables = totalConsumables; }
        public int getTotalClans() { return totalClans; }
        public void setTotalClans(int totalClans) { this.totalClans = totalClans; }
    }
    
    public static class CurrencyStats {
        private String currencyName;
        private BigDecimal cap;
        private BigDecimal weeklyCap;
        private int playersWithCurrency;
        private BigDecimal avgAmount;
        private BigDecimal maxAmount;
        private BigDecimal totalInCirculation;
        
        public CurrencyStats(String currencyName, BigDecimal cap, BigDecimal weeklyCap,
                           int playersWithCurrency, BigDecimal avgAmount, 
                           BigDecimal maxAmount, BigDecimal totalInCirculation) {
            this.currencyName = currencyName;
            this.cap = cap;
            this.weeklyCap = weeklyCap;
            this.playersWithCurrency = playersWithCurrency;
            this.avgAmount = avgAmount;
            this.maxAmount = maxAmount;
            this.totalInCirculation = totalInCirculation;
        }
        
        public String getCurrencyName() { return currencyName; }
        public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }
        public BigDecimal getCap() { return cap; }
        public void setCap(BigDecimal cap) { this.cap = cap; }
        public BigDecimal getWeeklyCap() { return weeklyCap; }
        public void setWeeklyCap(BigDecimal weeklyCap) { this.weeklyCap = weeklyCap; }
        public int getPlayersWithCurrency() { return playersWithCurrency; }
        public void setPlayersWithCurrency(int playersWithCurrency) { this.playersWithCurrency = playersWithCurrency; }
        public BigDecimal getAvgAmount() { return avgAmount; }
        public void setAvgAmount(BigDecimal avgAmount) { this.avgAmount = avgAmount; }
        public BigDecimal getMaxAmount() { return maxAmount; }
        public void setMaxAmount(BigDecimal maxAmount) { this.maxAmount = maxAmount; }
        public BigDecimal getTotalInCirculation() { return totalInCirculation; }
        public void setTotalInCirculation(BigDecimal totalInCirculation) { this.totalInCirculation = totalInCirculation; }
    }
    
    public static class ItemTypeStats {
        private String itemType;
        private int count;
        
        public ItemTypeStats(String itemType, int count) {
            this.itemType = itemType;
            this.count = count;
        }
        
        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }
    
    public static class TopPlayer {
        private String characterName;
        private String playerName;
        private int maxLevel;
        private String currentJob;
        private String race;
        
        public TopPlayer(String characterName, String playerName, int maxLevel, 
                        String currentJob, String race) {
            this.characterName = characterName;
            this.playerName = playerName;
            this.maxLevel = maxLevel;
            this.currentJob = currentJob;
            this.race = race;
        }
        
        public String getCharacterName() { return characterName; }
        public void setCharacterName(String characterName) { this.characterName = characterName; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getMaxLevel() { return maxLevel; }
        public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }
        public String getCurrentJob() { return currentJob; }
        public void setCurrentJob(String currentJob) { this.currentJob = currentJob; }
        public String getRace() { return race; }
        public void setRace(String race) { this.race = race; }
    }
    
    public static class TopPlayerWealth {
        private String characterName;
        private BigDecimal totalWealth;
        private int currencyTypes;
        
        public TopPlayerWealth(String characterName, BigDecimal totalWealth, int currencyTypes) {
            this.characterName = characterName;
            this.totalWealth = totalWealth;
            this.currencyTypes = currencyTypes;
        }
        
        public String getCharacterName() { return characterName; }
        public void setCharacterName(String characterName) { this.characterName = characterName; }
        public BigDecimal getTotalWealth() { return totalWealth; }
        public void setTotalWealth(BigDecimal totalWealth) { this.totalWealth = totalWealth; }
        public int getCurrencyTypes() { return currencyTypes; }
        public void setCurrencyTypes(int currencyTypes) { this.currencyTypes = currencyTypes; }
    }
}