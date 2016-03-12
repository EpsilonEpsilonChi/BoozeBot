package chi.epsilon.epsilon.boozebot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
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
import chi.epsilon.epsilon.boozebot.models.Ingredient;
import chi.epsilon.epsilon.boozebot.models.Recipe;
import chi.epsilon.epsilon.boozebot.models.Task;

public class RecipesFragment extends Fragment {
    private Firebase mFirebaseRef;
    private RecyclerView mRecyclerView;
    private RecipeAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new RecipeAdapter(new ArrayList<Recipe>());
        mFirebaseRef = new Firebase("https://boozebot.firebaseio.com/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recipes, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.recipe_recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecyclerView.setAdapter(mAdapter);

        updateUI();

        return v;
    }

    private void updateUI() {
        final List<Recipe> recipes = new ArrayList<>();
        final List<Task> tasks = new ArrayList<>();

        // This feels weird -- we make a request here to check if there are transactions,
        // and then another one inside the fragment to actually get the transactions.
        mFirebaseRef.child("Users").child(mFirebaseRef.getAuth().getUid()).
                child("pendingTransactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks.clear();
                // If there are queued drinks
                if (dataSnapshot.hasChildren()) {
                    QueuedDrinksFragment queuedDrinksFragment = new QueuedDrinksFragment();
                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.add(R.id.queue_fragment_container, queuedDrinksFragment)
                            .commit();
                } else {
                    Fragment fragment = getChildFragmentManager().findFragmentById(R.id.queue_fragment_container);
                    if (fragment != null) {
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.hide(fragment).commit();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        mFirebaseRef.child("Recipes").addValueEventListener(new ValueEventListener() {
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
            public void onCancelled(FirebaseError firebaseError) {}
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
        private Map<String, Recipe> mNameToRecipe;

        public RecipeAdapter(List<Recipe> recipes) {
            mRecipeList = recipes;
            mNameToRecipe = new HashMap<>();
            for (Recipe recipe : mRecipeList) {
                mNameToRecipe.put(recipe.getName(), recipe);
            }
        }

        @Override
        public RecipeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.recipe_grid_item, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView textView = (TextView) v.findViewById(R.id.recipe_title);
                    String recipe = textView.getText().toString();
                    Log.d("RecipesFragment", "Ordering a " + recipe);

                    FragmentManager manager = getFragmentManager();
                    ConfirmDrinkFragment dialog = new ConfirmDrinkFragment();
                    Bundle args = new Bundle();
                    args.putSerializable("drink", recipe);
                    args.putSerializable("user", mFirebaseRef.getAuth().getUid());
                    args.putSerializable("recipe", mNameToRecipe.get(recipe));

                    dialog.setArguments(args);
                    dialog.show(manager, "confused");
                }
            });
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
