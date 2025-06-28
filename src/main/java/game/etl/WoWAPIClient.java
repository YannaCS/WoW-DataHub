package game.etl;

import game.config.WoWApiConfig;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * WoW API Client following official Blizzard documentation
 */
public class WoWApiClient {
    
    private final HttpClient httpClient;
    private String accessToken;
    private long tokenExpiresAt;
    
    public WoWApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(30000))
                .build();
    }
    
    /**
     * Authenticate using OAuth client credentials flow (from official docs)
     */
    public void authenticate() throws IOException, InterruptedException {
        String credentials = WoWApiConfig.CLIENT_ID + ":" + WoWApiConfig.CLIENT_SECRET;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        
        String requestBody = "grant_type=client_credentials";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://oauth.battle.net/token"))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            String responseBody = response.body();
            this.accessToken = extractJsonValue(responseBody, "access_token");
            String expiresInStr = extractJsonValue(responseBody, "expires_in");
            if (this.accessToken != null && expiresInStr != null) {
                int expiresIn = Integer.parseInt(expiresInStr);
                this.tokenExpiresAt = System.currentTimeMillis() + (expiresIn * 1000L);
                System.out.println("✅ Successfully authenticated with Battle.net API");
            } else {
                throw new IOException("Failed to parse authentication response");
            }
        } else {
            throw new IOException("Failed to authenticate: " + response.statusCode() + " - " + response.body());
        }
    }
    
    /**
     * Test the token endpoint (from official docs)
     */
    public String testTokenEndpoint() throws IOException, InterruptedException {
        if (!isTokenValid()) {
            authenticate();
        }
        
        String url = "https://us.api.blizzard.com/data/wow/token/?namespace=dynamic-us";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .timeout(Duration.ofMillis(30000))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("Token API request failed with status " + response.statusCode() + ": " + response.body());
        }
    }
    
    /**
     * Test working endpoints from the documentation
     */
    public String testWorkingEndpoint(String endpoint, String namespace) throws IOException, InterruptedException {
        if (!isTokenValid()) {
            authenticate();
        }
        
        String url = "https://us.api.blizzard.com" + endpoint + "?namespace=" + namespace + "&locale=en_US";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .timeout(Duration.ofMillis(30000))
                .GET()
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200) {
            return response.body();
        } else {
            throw new IOException("API request failed with status " + response.statusCode() + ": " + response.body());
        }
    }
    
    /**
     * Check if current token is still valid
     */
    private boolean isTokenValid() {
        return accessToken != null && System.currentTimeMillis() < tokenExpiresAt - 60000;
    }
    
    /**
     * Extract a JSON value using regex
     */
    public String extractJsonValue(String json, String key) {
        // Try string values first
        Pattern stringPattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = stringPattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        // Try numeric values
        Pattern numericPattern = Pattern.compile("\"" + Pattern.quote(key) + "\"\\s*:\\s*([0-9]+)");
        matcher = numericPattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        
        return null;
    }
    
    /**
     * Extract array items from JSON
     */
    public List<String> extractJsonArray(String json, String arrayKey) {
        List<String> items = new ArrayList<>();
        
        try {
            // Find the array start
            String searchPattern = "\"" + Pattern.quote(arrayKey) + "\"\\s*:\\s*\\[";
            Pattern arrayStartPattern = Pattern.compile(searchPattern);
            Matcher startMatcher = arrayStartPattern.matcher(json);
            
            if (startMatcher.find()) {
                int arrayStart = startMatcher.end();
                
                // Find the matching closing bracket
                int bracketCount = 1;
                int pos = arrayStart;
                int arrayEnd = -1;
                
                while (pos < json.length() && bracketCount > 0) {
                    char c = json.charAt(pos);
                    if (c == '[') bracketCount++;
                    else if (c == ']') bracketCount--;
                    
                    if (bracketCount == 0) {
                        arrayEnd = pos;
                        break;
                    }
                    pos++;
                }
                
                if (arrayEnd > arrayStart) {
                    String arrayContent = json.substring(arrayStart, arrayEnd);
                    
                    // Parse objects within the array
                    int objectStart = -1;
                    int braceCount = 0;
                    
                    for (int i = 0; i < arrayContent.length(); i++) {
                        char c = arrayContent.charAt(i);
                        
                        if (c == '{') {
                            if (braceCount == 0) {
                                objectStart = i;
                            }
                            braceCount++;
                        } else if (c == '}') {
                            braceCount--;
                            if (braceCount == 0 && objectStart >= 0) {
                                String object = arrayContent.substring(objectStart, i + 1);
                                items.add(object);
                                objectStart = -1;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON array: " + e.getMessage());
        }
        
        return items;
    }
    
    // Simple data classes
    public static class RaceInfo {
        public final String name;
        public final String id;
        
        public RaceInfo(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
    
    public static class ItemInfo {
        public final String name;
        public final int level;
        public final long sellPrice;
        public final String itemClass;
        public final int damage;
        public final String id;
        
        public ItemInfo(String name, int level, long sellPrice, String itemClass, int damage, String id) {
            this.name = name;
            this.level = level;
            this.sellPrice = sellPrice;
            this.itemClass = itemClass;
            this.damage = damage;
            this.id = id;
        }
    }
    
    /**
     * Parse item info from JSON response
     */
    public ItemInfo parseItem(String itemJson) {
        String name = extractJsonValue(itemJson, "name");
        String levelStr = extractJsonValue(itemJson, "level");
        String sellPriceStr = extractJsonValue(itemJson, "sell_price");
        String idStr = extractJsonValue(itemJson, "id");
        
        int level = levelStr != null ? Integer.parseInt(levelStr) : 1;
        long sellPrice = sellPriceStr != null ? Long.parseLong(sellPriceStr) : 10000;
        int damage = 50; // default
        
        // Try to extract item class
        String itemClass = "Miscellaneous";
        if (itemJson.contains("\"item_class\"")) {
            String itemClassSection = itemJson.substring(itemJson.indexOf("\"item_class\""));
            String itemClassName = extractJsonValue(itemClassSection, "name");
            if (itemClassName != null) {
                itemClass = itemClassName;
            }
        }
        
        return new ItemInfo(name, level, sellPrice, itemClass, damage, idStr);
    }
    
    /**
     * Parse races from JSON response
     */
    public List<RaceInfo> parseRaces(String racesJson) {
        List<RaceInfo> races = new ArrayList<>();
        List<String> raceObjects = extractJsonArray(racesJson, "playable_races");
        
        for (String raceObject : raceObjects) {
            String name = extractJsonValue(raceObject, "name");
            String id = extractJsonValue(raceObject, "id");
            if (name != null) {
                races.add(new RaceInfo(name, id));
            }
        }
        
        return races;
    }
}