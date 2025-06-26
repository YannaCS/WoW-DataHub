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
    
    // Getters and setters...
}