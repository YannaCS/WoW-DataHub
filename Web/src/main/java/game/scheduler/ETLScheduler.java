package game.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import game.etl.ETLProcessor;

public class ETLScheduler {
    private final ScheduledExecutorService scheduler;
    private final ETLProcessor etlProcessor;
    
    public ETLScheduler(String clientId, String clientSecret) {
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.etlProcessor = new ETLProcessor(clientId, clientSecret);
    }
    
    public void start() {
        // Schedule realm data sync every hour
        scheduler.scheduleAtFixedRate(
            () -> etlProcessor.processRealmData(),
            0, 1, TimeUnit.HOURS
        );
    }
}