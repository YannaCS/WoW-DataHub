package game.etl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * ETL Processor for World of Warcraft API data synchronization
 * Handles OAuth authentication and data fetching with configurable limits
 */
public class ETLProcessor {
    
    private final String clientId;
    private final String clientSecret;
    private final int maxRealmRecords;
    private final int maxCharacterRecords;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    // WoW API endpoints
    private static final String TOKEN_URL = "https://oauth.battle.net/token";
    private static final String API_BASE_URL = "https://us.api.blizzard.com";
    private static final String REALM_LIST_URL = "/data/wow/realm/index";
    private static final String CHARACTER_SEARCH_URL = "/profile/wow/search/character";
    
    private String accessToken;
    private long tokenExpiryTime;
    
    /**
     * Constructor with record limits
     */
    public ETLProcessor(String clientId, String clientSecret, int maxRealmRecords, int maxCharacterRecords) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.maxRealmRecords = maxRealmRecords;
        this.maxCharacterRecords = maxCharacterRecords;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Constructor with default 50-record limit
     */
    public ETLProcessor(String clientId, String clientSecret) {
        this(clientId, clientSecret, 50, 50);
    }
    
    /**
     * Process realm data from WoW API
     */
    public ETLResult processRealmData() {
        return processRealmData(null);
    }
    
    /**
     * Process realm data from WoW API with job ID
     */
    public ETLResult processRealmData(String jobId) {
        ETLResult result = new ETLResult();
        result.setJobId(jobId);
        result.setJobType("REALM_SYNC");
        result.setStartTime(System.currentTimeMillis());
        
        try {
            System.out.println("Starting realm sync process...");
            
            // Get access token
            if (!ensureValidToken()) {
                result.setStatus("FAILED");
                result.setErrorMessage("Failed to obtain access token");
                return result;
            }
            
            // Fetch realm data
            List<RealmInfo> realms = fetchRealmsFromAPI();
            System.out.println("Fetched " + realms.size() + " realms from API");
            
            // Limit to maxRealmRecords
            List<RealmInfo> limitedRealms = realms.stream()
                    .limit(maxRealmRecords)
                    .toList();
            
            System.out.println("Processing " + limitedRealms.size() + " realms (limited to " + maxRealmRecords + ")");
            
            // Process each realm (simulate database save)
            int processedCount = 0;
            for (RealmInfo realm : limitedRealms) {
                // Simulate processing time
                simulateProcessingDelay();
                
                // Here you would save to database
                // saveRealmToDatabase(realm);
                System.out.println("Processed realm: " + realm.getName() + " (" + realm.getSlug() + ")");
                
                processedCount++;
            }
            
            result.setRecordsProcessed(processedCount);
            result.setStatus("COMPLETED");
            result.setEndTime(System.currentTimeMillis());
            
            System.out.println("Realm sync completed. Processed " + processedCount + " records.");
            
        } catch (Exception e) {
            System.err.println("Error in realm sync: " + e.getMessage());
            e.printStackTrace();
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setEndTime(System.currentTimeMillis());
        }
        
        return result;
    }
    
    /**
     * Process character data from WoW API
     */
    public ETLResult processCharacterData() {
        return processCharacterData(null);
    }
    
    /**
     * Process character data from WoW API with job ID
     */
    public ETLResult processCharacterData(String jobId) {
        ETLResult result = new ETLResult();
        result.setJobId(jobId);
        result.setJobType("CHARACTER_SYNC");
        result.setStartTime(System.currentTimeMillis());
        
        try {
            System.out.println("Starting character sync process...");
            
            // Get access token
            if (!ensureValidToken()) {
                result.setStatus("FAILED");
                result.setErrorMessage("Failed to obtain access token");
                return result;
            }
            
            // Fetch character data
            List<CharacterInfo> characters = fetchCharactersFromAPI();
            System.out.println("Fetched " + characters.size() + " characters from API");
            
            // Limit to maxCharacterRecords
            List<CharacterInfo> limitedCharacters = characters.stream()
                    .limit(maxCharacterRecords)
                    .toList();
            
            System.out.println("Processing " + limitedCharacters.size() + " characters (limited to " + maxCharacterRecords + ")");
            
            // Process each character (simulate database save)
            int processedCount = 0;
            for (CharacterInfo character : limitedCharacters) {
                // Simulate processing time
                simulateProcessingDelay();
                
                // Here you would save to database
                // saveCharacterToDatabase(character);
                System.out.println("Processed character: " + character.getName() + " (" + character.getRealm() + ")");
                
                processedCount++;
            }
            
            result.setRecordsProcessed(processedCount);
            result.setStatus("COMPLETED");
            result.setEndTime(System.currentTimeMillis());
            
            System.out.println("Character sync completed. Processed " + processedCount + " records.");
            
        } catch (Exception e) {
            System.err.println("Error in character sync: " + e.getMessage());
            e.printStackTrace();
            result.setStatus("FAILED");
            result.setErrorMessage(e.getMessage());
            result.setEndTime(System.currentTimeMillis());
        }
        
        return result;
    }
    
    /**
     * Ensure we have a valid access token
     */
    private boolean ensureValidToken() throws IOException, InterruptedException {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            return refreshAccessToken();
        }
        return true;
    }
    
    /**
     * Get OAuth access token from Battle.net
     */
    private boolean refreshAccessToken() throws IOException, InterruptedException {
        System.out.println("Refreshing access token...");
        
        String credentials = java.util.Base64.getEncoder()
                .encodeToString((clientId + ":" + clientSecret).getBytes());
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TOKEN_URL))
                .header("Authorization", "Basic " + credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            JsonNode tokenResponse = objectMapper.readTree(response.body());
            accessToken = tokenResponse.get("access_token").asText();
            int expiresIn = tokenResponse.get("expires_in").asInt();
            tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L) - 60000; // 1 minute buffer
            
            System.out.println("Access token refreshed successfully");
            return true;
        } else {
            System.err.println("Failed to get access token. Status: " + response.statusCode());
            System.err.println("Response: " + response.body());
            return false;
        }
    }
    
    /**
     * Fetch realms from WoW API
     */
    private List<RealmInfo> fetchRealmsFromAPI() throws IOException, InterruptedException {
        System.out.println("Fetching realms from WoW API...");
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_BASE_URL + REALM_LIST_URL + "?namespace=dynamic-us&locale=en_US"))
                .header("Authorization", "Bearer " + accessToken)
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch realms. Status: " + response.statusCode() + ", Body: " + response.body());
        }
        
        List<RealmInfo> realms = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode realmsNode = rootNode.get("realms");
        
        if (realmsNode != null && realmsNode.isArray()) {
            for (JsonNode realmNode : realmsNode) {
                RealmInfo realm = new RealmInfo();
                realm.setId(realmNode.get("id").asLong());
                realm.setName(realmNode.get("name").asText());
                realm.setSlug(realmNode.get("slug").asText());
                
                // Optional fields
                if (realmNode.has("timezone")) {
                    realm.setTimezone(realmNode.get("timezone").asText());
                }
                if (realmNode.has("type")) {
                    realm.setType(realmNode.get("type").get("name").asText());
                }
                
                realms.add(realm);
            }
        }
        
        return realms;
    }
    
    /**
     * Fetch characters from WoW API (simplified search)
     */
    private List<CharacterInfo> fetchCharactersFromAPI() throws IOException, InterruptedException {
        System.out.println("Fetching characters from WoW API...");
        
        // Note: This is a simplified character search. In reality, you might want to
        // search by specific criteria or fetch characters from specific guilds/realms
        
        List<CharacterInfo> characters = new ArrayList<>();
        
        // For demonstration, we'll create some sample character data
        // In a real implementation, you would make API calls to search for characters
        // or fetch specific character profiles
        
        // Sample character search (you would replace this with actual API calls)
        String[] sampleNames = {"Thrall", "Jaina", "Arthas", "Sylvanas", "Varian", "Garrosh", "Tyrande", "Malfurion"};
        String[] sampleRealms = {"Stormrage", "Tichondrius", "Area-52", "Illidan", "Mal'Ganis"};
        String[] sampleClasses = {"Shaman", "Mage", "Paladin", "Hunter", "Warrior", "Priest", "Druid"};
        
        for (int i = 0; i < Math.min(100, maxCharacterRecords * 2); i++) {
            CharacterInfo character = new CharacterInfo();
            character.setId((long) (i + 1));
            character.setName(sampleNames[i % sampleNames.length] + (i + 1));
            character.setRealm(sampleRealms[i % sampleRealms.length]);
            character.setCharacterClass(sampleClasses[i % sampleClasses.length]);
            character.setLevel(ThreadLocalRandom.current().nextInt(1, 81));
            
            characters.add(character);
        }
        
        return characters;
    }
    
    /**
     * Simulate processing delay to make the ETL job more realistic
     */
    private void simulateProcessingDelay() {
        try {
            // Random delay between 100-500ms per record
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * ETL Result class
     */
    public static class ETLResult {
        private String jobId;
        private String jobType;
        private String status;
        private int recordsProcessed;
        private long startTime;
        private long endTime;
        private String errorMessage;
        
        // Getters and setters
        public String getJobId() { return jobId; }
        public void setJobId(String jobId) { this.jobId = jobId; }
        
        public String getJobType() { return jobType; }
        public void setJobType(String jobType) { this.jobType = jobType; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public int getRecordsProcessed() { return recordsProcessed; }
        public void setRecordsProcessed(int recordsProcessed) { this.recordsProcessed = recordsProcessed; }
        
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        
        public long getDurationMillis() { 
            return endTime > 0 ? endTime - startTime : 0; 
        }
        
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }
    
    /**
     * Realm information class
     */
    public static class RealmInfo {
        private Long id;
        private String name;
        private String slug;
        private String timezone;
        private String type;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getSlug() { return slug; }
        public void setSlug(String slug) { this.slug = slug; }
        
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
    
    /**
     * Character information class
     */
    public static class CharacterInfo {
        private Long id;
        private String name;
        private String realm;
        private String characterClass;
        private int level;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getRealm() { return realm; }
        public void setRealm(String realm) { this.realm = realm; }
        
        public String getCharacterClass() { return characterClass; }
        public void setCharacterClass(String characterClass) { this.characterClass = characterClass; }
        
        public int getLevel() { return level; }
        public void setLevel(int level) { this.level = level; }
    }
}