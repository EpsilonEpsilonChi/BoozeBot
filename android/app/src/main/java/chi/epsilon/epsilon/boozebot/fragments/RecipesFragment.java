package chi.epsilon.epsilon.boozebot.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import chi.epsilon.epsilon.boozebot.models.Task;

public class RecipesFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new RecipeAdapter(new ArrayList<Recipe>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recipes, container, false);
        Log.d("RecipeFragment.java", "onCreate");

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recipe_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(mAdapter);

        updateUI();

        return v;
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
                mAdapter = new RecipeAdapter(recipes);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mAdapter = new RecipeAdapter(recipes);
        mRecyclerView.setAdapter(mAdapter);
    }

    private class RecipeHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView mRecipeImage;
        public Recipe mRecipe;

        public RecipeHolder(View itemView) {
           super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.recipe_title);
            mRecipeImage = (ImageView) itemView.findViewById(R.id.recipe_image);
            mRecipeImage.setImageDrawable(getResources().getDrawable(R.drawable.martini_glass_icon));
        }

        public void bindRecipe(Recipe recipe) {
            mRecipe = recipe;
            mTextView.setText(recipe.getName());
        }
    }

    private class RecipeAdapter extends RecyclerView.Adapter<RecipeHolder> {
        private List<Recipe> mRecipeList;

        public RecipeAdapter(List<Recipe> recipes) {
            mRecipeList = recipes;
        }

        @Override
        public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recipe_grid_item, parent, false);
            return new RecipeHolder(view);
        }

        @Override
        public void onBindViewHolder(RecipeHolder holder, int position) {
            Recipe recipe = mRecipeList.get(position);
            holder.bindRecipe(recipe);
        }

        @Override
        public int getItemCount() {
            return mRecipeList.size();
        }
    }
}
