package game.model.Analytics;

public class ClanDistribution {
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
    public String getRace() { return race; }
    public int getCharacterCount() { return characterCount; }
    public double getPercentage() { return percentage; }
}