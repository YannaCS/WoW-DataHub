package game.model.Analytics;

public class TopPlayer {
    private String characterName;
    private String playerName;
    private int maxLevel;
    private String currentJob;
    private String race;
    
    public TopPlayer(String characterName, String playerName, int maxLevel, String currentJob, String race) {
        this.characterName = characterName;
        this.playerName = playerName;
        this.maxLevel = maxLevel;
        this.currentJob = currentJob;
        this.race = race;
    }
    
    public String getCharacterName() { return characterName; }
    public String getPlayerName() { return playerName; }
    public int getMaxLevel() { return maxLevel; }
    public String getCurrentJob() { return currentJob; }
    public String getRace() { return race; }
}