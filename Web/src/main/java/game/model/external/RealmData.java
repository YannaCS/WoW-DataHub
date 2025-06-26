package game.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RealmData {
    @JsonProperty("id")
    private int id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("timezone")
    private String timezone;
    
    @JsonProperty("type")
    private TypeInfo type;
    
    @JsonProperty("region")
    private RegionInfo region;
    
    @JsonProperty("connected_realm")
    private ConnectedRealmInfo connectedRealm;
    
    // Default constructor
    public RealmData() {}
    
    // Constructor
    public RealmData(int id, String name, String category, String timezone) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.timezone = timezone;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    
    public TypeInfo getType() {
        return type;
    }
    
    public void setType(TypeInfo type) {
        this.type = type;
    }
    
    public RegionInfo getRegion() {
        return region;
    }
    
    public void setRegion(RegionInfo region) {
        this.region = region;
    }
    
    public ConnectedRealmInfo getConnectedRealm() {
        return connectedRealm;
    }
    
    public void setConnectedRealm(ConnectedRealmInfo connectedRealm) {
        this.connectedRealm = connectedRealm;
    }
    
    @Override
    public String toString() {
        return String.format("RealmData{id=%d, name='%s', category='%s', timezone='%s'}", 
                           id, name, category, timezone);
    }
    
    // Inner classes for nested JSON objects
    public static class TypeInfo {
        @JsonProperty("type")
        private String type;
        
        @JsonProperty("name")
        private String name;
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    public static class RegionInfo {
        @JsonProperty("id")
        private int id;
        
        @JsonProperty("name")
        private String name;
        
        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }
    
    public static class ConnectedRealmInfo {
        @JsonProperty("href")
        private String href;
        
        // Getters and setters
        public String getHref() { return href; }
        public void setHref(String href) { this.href = href; }
    }
}