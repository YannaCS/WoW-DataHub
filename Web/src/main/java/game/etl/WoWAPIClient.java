package game.etl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import game.model.external.RealmListResponse;

public class WoWAPIClient {
    private static final String BASE_URL = "https://us.api.blizzard.com";
    private static final String AUTH_URL = "https://oauth.battle.net/token";
    
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
    private long tokenExpiryTime;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public WoWAPIClient(String clientId, String clientSecret) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Get list of realms from WoW API
     */
    public RealmListResponse getRealms() throws IOException, InterruptedException {
        authenticateIfNeeded();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/data/wow/realm/index"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Battlenet-Namespace", "dynamic-us")
            .GET()
            .build();
            
        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());
            
        if (response.statusCode() != 200) {
            throw new IOException("API request failed with status: " + response.statusCode() + 
                                " Body: " + response.body());
        }
        
        return objectMapper.readValue(response.body(), RealmListResponse.class);
    }
    
    /**
     * Get character profile data (example method)
     */
    public JsonNode getCharacterProfile(String realmSlug, String characterName) 
            throws IOException, InterruptedException {
        authenticateIfNeeded();
        
        String url = String.format("%s/profile/wow/character/%s/%s", 
                                 BASE_URL, realmSlug, characterName.toLowerCase());
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + accessToken)
            .header("Battlenet-Namespace", "profile-us")
            .GET()
            .build();
            
        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());
            
        if (response.statusCode() != 200) {
            throw new IOException("Character profile request failed with status: " + 
                                response.statusCode());
        }
        
        return objectMapper.readTree(response.body());
    }
    
    /**
     * Get auction house data for a connected realm
     */
    public JsonNode getAuctionHouseData(int connectedRealmId) 
            throws IOException, InterruptedException {
        authenticateIfNeeded();
        
        String url = String.format("%s/data/wow/connected-realm/%d/auctions", 
                                 BASE_URL, connectedRealmId);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + accessToken)
            .header("Battlenet-Namespace", "dynamic-us")
            .GET()
            .build();
            
        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());
            
        if (response.statusCode() != 200) {
            throw new IOException("Auction house request failed with status: " + 
                                response.statusCode());
        }
        
        return objectMapper.readTree(response.body());
    }
    
    /**
     * Authenticate with Battle.net OAuth if token is expired or missing
     */
    private void authenticateIfNeeded() throws IOException, InterruptedException {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            authenticate();
        }
    }
    
    /**
     * Perform OAuth2 client credentials authentication
     */
    private void authenticate() throws IOException, InterruptedException {
        String credentials = Base64.getEncoder()
            .encodeToString((clientId + ":" + clientSecret).getBytes());
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(AUTH_URL))
            .header("Authorization", "Basic " + credentials)
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
            .build();
            
        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());
            
        if (response.statusCode() != 200) {
            throw new IOException("Authentication failed with status: " + response.statusCode() + 
                                " Body: " + response.body());
        }
        
        JsonNode authResponse = objectMapper.readTree(response.body());
        this.accessToken = authResponse.get("access_token").asText();
        
        // Token expires in seconds, convert to milliseconds and add current time
        long expiresIn = authResponse.get("expires_in").asLong() * 1000;
        this.tokenExpiryTime = System.currentTimeMillis() + expiresIn - 60000; // 1 minute buffer
        
        System.out.println("Successfully authenticated with Battle.net API");
    }
    
    /**
     * Test connection to the API
     */
    public boolean testConnection() {
        try {
            authenticate();
            return true;
        } catch (Exception e) {
            System.err.println("Failed to connect to WoW API: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current access token (for debugging)
     */
    public String getCurrentToken() {
        return accessToken;
    }
    
    /**
     * Check if token is valid
     */
    public boolean isTokenValid() {
        return accessToken != null && System.currentTimeMillis() < tokenExpiryTime;
    }
}