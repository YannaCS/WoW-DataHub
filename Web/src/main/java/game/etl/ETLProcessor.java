package game.etl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import game.dal.ConnectionManager;
import game.model.external.RealmData;
import game.model.external.RealmListResponse;

public class ETLProcessor {
    private static final Logger LOGGER = Logger.getLogger(ETLProcessor.class.getName());
    
    private final WoWAPIClient apiClient;
    private final String jobId;
    
    public ETLProcessor(String clientId, String clientSecret) {
        this.apiClient = new WoWAPIClient(clientId, clientSecret);
        this.jobId = "ETL_" + System.currentTimeMillis();
    }
    
    /**
     * Process realm data from WoW API and store in database
     */
    public ETLResult processRealmData() {
        LOGGER.info("Starting realm data processing job: " + jobId);
        
        ETLResult result = new ETLResult();
        result.setJobId(jobId);
        result.setStartTime(LocalDateTime.now());
        result.setJobType("REALM_SYNC");
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            // Test API connection first
            if (!apiClient.testConnection()) {
                result.setStatus("FAILED");
                result.setErrorMessage("Failed to connect to WoW API");
                result.setEndTime(LocalDateTime.now());
                return result;
            }
            
            // Start ETL job tracking
            startETLJob(cxn, result);
            
            // Fetch realm data from API
            LOGGER.info("Fetching realm data from WoW API...");
            RealmListResponse realmsResponse = apiClient.getRealms();
            List<RealmData> realms = realmsResponse.getRealms();
            
            if (realms == null || realms.isEmpty()) {
                result.setStatus("WARNING");
                result.setErrorMessage("No realm data received from API");
                result.setRecordsProcessed(0);
            } else {
                // Process each realm
                int processed = 0;
                for (RealmData realm : realms) {
                    try {
                        processRealm(cxn, realm);
                        processed++;
                        
                        // Log progress every 10 realms
                        if (processed % 10 == 0) {
                            LOGGER.info(String.format("Processed %d/%d realms", processed, realms.size()));
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to process realm: " + realm.getName(), e);
                    }
                }
                
                result.setRecordsProcessed(processed);
                result.setStatus("SUCCESS");
                LOGGER.info(String.format("Successfully processed %d realms", processed));
            }
            
            // Complete ETL job tracking
            result.setEndTime(LocalDateTime.now());
            completeETLJob(cxn, result);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "ETL job failed: " + jobId, e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setEndTime(LocalDateTime.now());
            
            // Try to update job status in database
            try (Connection cxn = ConnectionManager.getConnection()) {
                completeETLJob(cxn, result);
            } catch (SQLException dbEx) {
                LOGGER.log(Level.SEVERE, "Failed to update ETL job status", dbEx);
            }
        }
        
        return result;
    }
    
    /**
     * Process sample character data (simulation)
     */
    public ETLResult processCharacterData() {
        LOGGER.info("Starting character data processing job: " + jobId);
        
        ETLResult result = new ETLResult();
        result.setJobId(jobId + "_CHAR");
        result.setStartTime(LocalDateTime.now());
        result.setJobType("CHARACTER_SYNC");
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            startETLJob(cxn, result);
            
            // Simulate character data processing
            // In a real implementation, this would fetch character data from WoW API
            Thread.sleep(2000); // Simulate processing time
            
            int simulatedCharacters = (int) (Math.random() * 50) + 10; // 10-60 characters
            result.setRecordsProcessed(simulatedCharacters);
            result.setStatus("SUCCESS");
            
            LOGGER.info(String.format("Simulated processing of %d characters", simulatedCharacters));
            
            result.setEndTime(LocalDateTime.now());
            completeETLJob(cxn, result);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Character ETL job failed: " + result.getJobId(), e);
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setEndTime(LocalDateTime.now());
        }
        
        return result;
    }
    
    /**
     * Process a single realm
     */
    private void processRealm(Connection cxn, RealmData realm) throws SQLException {
        // In a real implementation, you would:
        // 1. Check if realm exists in StagingRealms table
        // 2. Insert or update realm data
        // 3. Update any related character data
        
        LOGGER.fine("Processing realm: " + realm.getName() + " (ID: " + realm.getId() + ")");
        
        // For now, just log the realm data
        // You would implement StagingRealmsDao.upsertRealm(cxn, realm) here
    }
    
    /**
     * Start ETL job tracking
     */
    private void startETLJob(Connection cxn, ETLResult result) throws SQLException {
        // In a real implementation, you would insert into ETLMetadata table
        LOGGER.info("Starting ETL job tracking for: " + result.getJobId());
    }
    
    /**
     * Complete ETL job tracking
     */
    private void completeETLJob(Connection cxn, ETLResult result) throws SQLException {
        // In a real implementation, you would update ETLMetadata table
        LOGGER.info(String.format("Completed ETL job %s with status: %s, records: %d", 
                                result.getJobId(), result.getStatus(), result.getRecordsProcessed()));
    }
    
    /**
     * Get ETL job status
     */
    public ETLResult getJobStatus(String jobId) {
        // In a real implementation, you would query ETLMetadata table
        ETLResult result = new ETLResult();
        result.setJobId(jobId);
        result.setStatus("RUNNING");
        return result;
    }
    
    /**
     * ETL Result class to track job execution
     */
    public static class ETLResult {
        private String jobId;
        private String jobType;
        private LocalDateTime startTime;
        private LocalDateTime endTime;
        private String status;
        private int recordsProcessed;
        private String errorMessage;
        
        // Getters and setters
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        
        public String getJobType() { return jobType; }
        public void setJobType(String jobType) { this.jobType = jobType; }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getRecordsProcessed() { return recordsProcessed; }
        public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        
        public long getDurationMillis() {
            if (startTime != null && endTime != null) {
                return java.time.Duration.between(startTime, endTime).toMillis();
            }
            return 0;
        }
        
        @Override
        public String toString() {
            return String.format("ETLResult{jobId='%s', status='%s', records=%d, duration=%dms}", 
                               jobId, status, recordsProcessed, getDurationMillis());
        }
    }
}