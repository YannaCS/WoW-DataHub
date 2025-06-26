package game.model.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class RealmListResponse {
    @JsonProperty("realms")
    private List<RealmData> realms;
    
    @JsonProperty("_links")
    private Links links;
    
    // Default constructor
    public RealmListResponse() {}
    
    // Getters and setters
    public List<RealmData> getRealms() {
        return realms;
    }
    
    public void setRealms(List<RealmData> realms) {
        this.realms = realms;
    }
    
    public Links getLinks() {
        return links;
    }
    
    public void setLinks(Links links) {
        this.links = links;
    }
    
    @Override
    public String toString() {
        return String.format("RealmListResponse{realms=%d}", 
                           realms != null ? realms.size() : 0);
    }
    
    // Inner class for API links
    public static class Links {
        @JsonProperty("self")
        private LinkInfo self;
        
        public LinkInfo getSelf() { return self; }
        public void setSelf(LinkInfo self) { this.self = self; }
        
        public static class LinkInfo {
            @JsonProperty("href")
            private String href;
            
            public String getHref() { return href; }
            public void setHref(String href) { this.href = href; }
        }
    }
}