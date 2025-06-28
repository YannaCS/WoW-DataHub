package game.config;

/**
 * Configuration class for World of Warcraft API credentials and endpoints
 */
public class WoWApiConfig {
    
    // Replace these with your actual Battle.net API credentials
    public static final String CLIENT_ID = "7b510b4bb81e44ae8fbd9294a0a6e7cf";
    public static final String CLIENT_SECRET = "iJYELZwo4kyxQkV6Bwe4LwCaqb87te3M";
    
    // OAuth2 endpoints
    public static final String OAUTH_TOKEN_URL = "https://oauth.battle.net/token";
    
    // WoW API base URLs
    public static final String US_API_BASE = "https://us.api.blizzard.com";
    public static final String EU_API_BASE = "https://eu.api.blizzard.com";
    public static final String DEFAULT_REGION = "us";
    public static final String DEFAULT_NAMESPACE = "dynamic-us";
    public static final String DEFAULT_LOCALE = "en_US";
    
    // API endpoints - updated for correct paths
    public static final String REALMS_ENDPOINT = "/data/wow/realm/index";
    public static final String CHARACTERS_ENDPOINT = "/profile/wow/character";
    public static final String GUILDS_ENDPOINT = "/data/wow/guild";
    public static final String ITEMS_ENDPOINT = "/data/wow/item";
    public static final String RACES_ENDPOINT = "/data/wow/playable-race/index";
    public static final String CLASSES_ENDPOINT = "/data/wow/playable-class/index";
    
    // Rate limiting
    public static final int MAX_REQUESTS_PER_SECOND = 100;
    public static final int MAX_REQUESTS_PER_HOUR = 36000;
    
    // ETL Configuration
    public static final int MAX_RECORDS_PER_FETCH = 100;
    public static final int CONNECTION_TIMEOUT = 30000; // 30 seconds
    public static final int READ_TIMEOUT = 60000; // 60 seconds
    
    private WoWApiConfig() {
        // Private constructor to prevent instantiation
    }
}