package chi.epsilon.epsilon.boozebot.fragments;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.models.Ingredient;
import chi.epsilon.epsilon.boozebot.models.Recipe;

public class RecipeDialogFragment extends LinearLayout {
    private List<Ingredient> mIngredientList;

    public RecipeDialogFragment(Context context, Recipe recipe) {
        super(context);
        mIngredientList = recipe.getIngredientList();

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.fragment_recipe_dialog, null);
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
            mIngredientView.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
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
