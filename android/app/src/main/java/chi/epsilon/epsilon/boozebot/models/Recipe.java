package chi.epsilon.epsilon.boozebot.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Recipe implements Serializable {
    private String name;
    private List<Ingredient> mIngredientList;

    public String getName() {
        return name;
    }

    public List<Ingredient> getIngredientList() {
        return mIngredientList;
    }

    public void addIngredient(Ingredient ingredient) {
        mIngredientList.add(ingredient);
    }

    public Recipe() {
        mIngredientList = new ArrayList<>();
    }

    public Recipe(String name) {
        mIngredientList = new ArrayList<>();
        this.name = name;
    }
}
