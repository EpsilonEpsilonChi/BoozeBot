package chi.epsilon.epsilon.boozebot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.models.Ingredient;
import chi.epsilon.epsilon.boozebot.models.Recipe;
import chi.epsilon.epsilon.boozebot.util.HttpClient;

public class ConfirmDrinkFragment extends DialogFragment {
    private RecyclerView mIngredientRecyclerView;
    private IngredientAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        final Recipe recipe = (Recipe) getArguments().get("recipe");

        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_recipe_dialog, null);
        mIngredientRecyclerView = (RecyclerView) v.findViewById(R.id.ingredients_recycler);
        mIngredientRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new IngredientAdapter(recipe.getIngredientList());
        mIngredientRecyclerView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String username = (String)getArguments().get("user");
        final String drinkname = (String)getArguments().get("drink");

        return new AlertDialog.Builder(getActivity())
                .setTitle(String.format("Order a %s for %s?", drinkname, username))
                .setPositiveButton("Fuck Yes.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RequestQueue queue = HttpClient.getInstance(getActivity().getApplicationContext())
                                .getRequestQueue();

                        String url = "https://boozebot-boozebotapi.rhcloud.com/queue_drink";
                        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response:", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("Error:", error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("user", username);
                                params.put("drink", drinkname);
                                return params;
                            }
                        };
                        queue.add(request);
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Naw", null)
                .setView(R.layout.fragment_recipe_dialog)
                .create();
    }

    private void updateUI() {

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
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new IngredientHolder(view);
        }

        @Override
        public void onBindViewHolder(IngredientHolder holder, int position) {
            Ingredient ingredient = mIngredients.get(position);
            holder.bindIngredient(ingredient);
        }

        @Override
        public int getItemCount() {
            return mIngredients.size();
        }
    }
}
