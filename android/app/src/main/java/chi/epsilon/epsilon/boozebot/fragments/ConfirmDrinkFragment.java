package chi.epsilon.epsilon.boozebot.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import chi.epsilon.epsilon.boozebot.models.Recipe;
import chi.epsilon.epsilon.boozebot.util.HttpClient;

public class ConfirmDrinkFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String username = (String)getArguments().get("user");
        final String drinkname = (String)getArguments().get("drink");
        final Recipe recipe = (Recipe) getArguments().get("recipe");

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
                .setView(new RecipeDialogFragment(getContext(), recipe))
                .create();
    }
}
