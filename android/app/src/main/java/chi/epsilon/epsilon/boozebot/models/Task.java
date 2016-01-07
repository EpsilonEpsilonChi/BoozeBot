package chi.epsilon.epsilon.boozebot.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {
    private String recipeUsed;
    private String timestamp;
    private Double totalCost;
    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getRecipeUsed() {
        return recipeUsed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public void setRecipeUsed(String recipeUsed) {
        this.recipeUsed = recipeUsed;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }

    public Task() {}
}
