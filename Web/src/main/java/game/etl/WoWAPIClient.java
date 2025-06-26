package game.etl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import com.fasterxml.jackson.databind.ObjectMapper;
import game.model.external.*;

public class WoWAPIClient {
    private static final String BASE_URL = "https://us.api.blizzard.com";
    private final String clientId;
    private final String clientSecret;
    private String accessToken;
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
    
    public RealmListResponse getRealms() throws IOException, InterruptedException {
        authenticateIfNeeded();
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/data/wow/realm/index"))
            .header("Authorization", "Bearer " + accessToken)
            .header("Battlenet-Namespace", "dynamic-us")
            .build();
            
        HttpResponse<String> response = httpClient.send(request, 
            HttpResponse.BodyHandlers.ofString());
            
        return objectMapper.readValue(response.body(), RealmListResponse.class);
    }
    
    private void authenticateIfNeeded() throws IOException, InterruptedException {
        // OAuth2 implementation for Blizzard API
        // Implementation details for token refresh...
    }
}