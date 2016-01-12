package chi.epsilon.epsilon.boozebot.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chi.epsilon.epsilon.boozebot.BoozeBotApp;
import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.models.Ingredient;
import chi.epsilon.epsilon.boozebot.models.Recipe;
import chi.epsilon.epsilon.boozebot.util.HttpClient;

public class RecipeDialogFragment extends LinearLayout {
    private List<Ingredient> mIngredientList;

    public RecipeDialogFragment(final Context context, final Recipe recipe) {
        super(context);
        mIngredientList = recipe.getIngredientList();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_recipe_dialog, null);

        TextView recipeTitle = (TextView) v.findViewById(R.id.recipe_title);
        recipeTitle.setText(recipe.getName());

        // Set temp image
        ImageView recipeImage = (ImageView) v.findViewById(R.id.recipe_image);
        recipeImage.setImageDrawable(getResources().getDrawable(R.drawable.martini_glass_icon));

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.ingredients_recycler);
        recyclerView.setAdapter(new IngredientAdapter(mIngredientList));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        addView(v);
    }

    private class IngredientHolder extends RecyclerView.ViewHolder {
        TextView mIngredientView;
        Ingredient mIngredient;

        public IngredientHolder(View itemView) {
            super(itemView);
            mIngredientView = (TextView) itemView;
        }

        public void bindIngredient(Ingredient ingredient) {
            mIngredient = ingredient;
            mIngredientView.setText(ingredient.getType());
            mIngredientView.setTextColor(getResources().getColor(R.color.colorAccent));
            Log.d("ConfirmDrink", "Binding ingredient!" + ingredient.getType());
        }
    }

    private class IngredientAdapter extends RecyclerView.Adapter<IngredientHolder> {
        private List<Ingredient> mIngredients;

        public IngredientAdapter(List<Ingredient> ingredients) {
            mIngredients = ingredients;
        }

        @Override
        public IngredientHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new IngredientHolder(view);
        }

        @Override
        public void onBindViewHolder(IngredientHolder holder, int position) {
            Ingredient ingredient = mIngredients.get(position);
            Log.d("ConfirmFrag", "Binding an ingredient");
            holder.bindIngredient(ingredient);
        }

        @Override
        public int getItemCount() {
            return mIngredients.size();
        }
    }

}
