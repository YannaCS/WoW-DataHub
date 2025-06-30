package game.model.Analytics;

public class OverallStats {
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
    
    // Getters
    public int getTotalPlayers() { return totalPlayers; }
    public int getTotalCharacters() { return totalCharacters; }
    public int getTotalWeapons() { return totalWeapons; }
    public int getTotalGears() { return totalGears; }
    public int getTotalConsumables() { return totalConsumables; }
    public int getTotalClans() { return totalClans; }
}