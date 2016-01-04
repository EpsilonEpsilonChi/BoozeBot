package chi.epsilon.epsilon.boozebot.activities;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.List;

import chi.epsilon.epsilon.boozebot.R;
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

        mAddDrinkButton = (Button) findViewById(R.id.select_button);
        mAddDrinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateUI();
    }

    private void updateUI() {
        final List<Recipe> recipes = new ArrayList<>();
        final List<String> testList = new ArrayList<String>();
        final Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");
        rootRef.child("Recipes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                testList.add("Recipe 1");
                testList.add("Recipe 2");
                testList.add("Recipe 3");
                testList.add("Recipe 4");

                recipes.clear();
                for (DataSnapshot recipeSnap : dataSnapshot.getChildren()) {
                    Recipe recipe = new Recipe(recipeSnap.getKey());
                    for (DataSnapshot ingredientSnap : recipeSnap.getChildren()) {
                        Ingredient ingredient = ingredientSnap.getValue(Ingredient.class);
                        recipe.addIngredient(ingredient);
                    }
                    recipes.add(recipe);
                }
                mAdapter = new DrinkRecipeAdapter(testList);
                mDrinkRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });
        mAdapter = new DrinkRecipeAdapter(testList);
        mDrinkRecyclerView.setAdapter(mAdapter);
    }

    private class DrinkRecipeHolder extends RecyclerView.ViewHolder {
        public TextView mRecipeImage;

        public DrinkRecipeHolder(View itemView) {
            super(itemView);

            mRecipeImage = (TextView)itemView;
        }
    }

    private class DrinkRecipeAdapter extends RecyclerView.Adapter<DrinkRecipeHolder> {
        private List<String> mDrinkImages;

        public DrinkRecipeAdapter(List<String> drinkRecipes) {
            mDrinkImages = drinkRecipes;
        }

        @Override
        public DrinkRecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new DrinkRecipeHolder(view);
        }

        @Override
        public void onBindViewHolder(DrinkRecipeHolder holder, int position) {
            String string = mDrinkImages.get(position);
            holder.mRecipeImage.setText(string);
            holder.mRecipeImage.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        @Override
        public int getItemCount() {
            return mDrinkImages.size();
        }
    }
}
