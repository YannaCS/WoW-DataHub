package game.model.Analytics;

public class DailyActivePlayer {
    private String date;
    private int activeUsers;
    
    public DailyActivePlayer(String date, int activeUsers) {
        this.date = date;
        this.activeUsers = activeUsers;
    }
    
    public String getDate() { return date; }
    public int getActiveUsers() { return activeUsers; }
}