package game.model.Analytics;

public class JobDistribution {
    private String jobName;
    private int characterCount;
    private double percentage;
    
    public JobDistribution(String jobName, int characterCount, double percentage) {
        this.jobName = jobName;
        this.characterCount = characterCount;
        this.percentage = percentage;
    }
    
    public String getJobName() { return jobName; }
    public int getCharacterCount() { return characterCount; }
    public double getPercentage() { return percentage; }
}