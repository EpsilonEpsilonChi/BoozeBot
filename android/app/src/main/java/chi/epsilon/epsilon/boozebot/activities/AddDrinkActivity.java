package chi.epsilon.epsilon.boozebot.activities;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chi.epsilon.epsilon.boozebot.BoozeBotApp;
import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.fragments.ConfirmDrinkFragment;
import chi.epsilon.epsilon.boozebot.models.Ingredient;
import chi.epsilon.epsilon.boozebot.models.Recipe;

public class AddDrinkActivity extends AppCompatActivity {
    private RecyclerView mDrinkRecyclerView;
    private DrinkRecipeAdapter mAdapter;
    private Button mAddDrinkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink);

        mDrinkRecyclerView = (RecyclerView) findViewById(R.id.drink_recipe_recycler);
        mDrinkRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    private void updateUI() {
        final List<Recipe> recipes = new ArrayList<>();
        final Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");
        rootRef.child("Recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                recipes.clear();
                for (DataSnapshot recipeSnap : dataSnapshot.getChildren()) {
                    Recipe recipe = new Recipe(recipeSnap.getKey());
                    for (DataSnapshot ingredientSnap : recipeSnap.getChildren()) {
                        Ingredient ingredient = ingredientSnap.getValue(Ingredient.class);
                        recipe.addIngredient(ingredient);
                    }
                    recipes.add(recipe);
                }
                mAdapter = new DrinkRecipeAdapter(recipes);
                mDrinkRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
        mAdapter = new DrinkRecipeAdapter(recipes);
        mDrinkRecyclerView.setAdapter(mAdapter);
    }

    private class DrinkRecipeHolder extends RecyclerView.ViewHolder {
        //public TextView mRecipe;
        public Button mRecipe;

        public DrinkRecipeHolder(View itemView) {
            super(itemView);

            mRecipe = (Button) itemView.findViewById(R.id.recipe_button);
        }

        public void bindDrink(Recipe drink) {
            mRecipe.setText(drink.getName());
        }
    }

    private class DrinkRecipeAdapter extends RecyclerView.Adapter<DrinkRecipeHolder> {
        private List<Recipe> mRecipes;
        private Map<String, Recipe> mNameToRecipe;

        public DrinkRecipeAdapter(List<Recipe> drinkRecipes) {
            mRecipes = drinkRecipes;
            mNameToRecipe = new HashMap<>();
            for (Recipe recipe : mRecipes) {
                mNameToRecipe.put(recipe.getName(), recipe);
            }
        }

        @Override
        public DrinkRecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(R.layout.select_recipe_item, parent, false);

            Button recipeButton = (Button)view.findViewById(R.id.recipe_button);
            recipeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    String recipe = tv.getText().toString();
                    Log.d("AddDrinkActivity.java", recipe);

                    FragmentManager manager = getFragmentManager();
                    ConfirmDrinkFragment dialog = new ConfirmDrinkFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("drink", recipe);
                    args.putSerializable("user", ((BoozeBotApp) getApplication()).getCurrentUser());
                    args.putSerializable("recipe", mNameToRecipe.get(recipe));

                    dialog.setArguments(args);
                    dialog.show(manager, "confused");


                }
            });
            return new DrinkRecipeHolder(view);
        }

        @Override
        public void onBindViewHolder(DrinkRecipeHolder holder, int position) {
            Recipe recipe = mRecipes.get(position);
            holder.bindDrink(recipe);
        }

        @Override
        public int getItemCount() {
            return mRecipes.size();
        }
    }
}
