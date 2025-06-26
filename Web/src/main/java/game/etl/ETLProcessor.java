package game.etl;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import game.dal.ConnectionManager;
import game.dal.ETLMetadataDao;
import game.model.external.*;

public class ETLProcessor {
    private final WoWAPIClient apiClient;
    
    public ETLProcessor(String clientId, String clientSecret) {
        this.apiClient = new WoWAPIClient(clientId, clientSecret);
    }
    
    public void processRealmData() {
        String jobId = "REALM_SYNC_" + System.currentTimeMillis();
        
        try (Connection cxn = ConnectionManager.getConnection()) {
            ETLMetadataDao.startJob(cxn, jobId, "Realm Data Sync", "WoW API");
            
            RealmListResponse realms = apiClient.getRealms();
            int processed = 0;
            
            for (RealmData realm : realms.getRealms()) {
                // Transform and load realm data
                StagingRealmsDao.upsertRealm(cxn, realm);
                processed++;
            }
            
            ETLMetadataDao.completeJob(cxn, jobId, processed, "SUCCESS", null);
            
        } catch (Exception e) {
            try (Connection cxn = ConnectionManager.getConnection()) {
                ETLMetadataDao.completeJob(cxn, jobId, 0, "FAILED", e.getMessage());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}