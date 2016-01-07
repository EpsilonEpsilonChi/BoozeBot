package chi.epsilon.epsilon.boozebot.models;

public class Ingredient {
    private String type;
    private String amount;

    public String getType() {
        return type;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Ingredient() {}
}
