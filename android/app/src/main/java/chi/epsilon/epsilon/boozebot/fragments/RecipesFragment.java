package chi.epsilon.epsilon.boozebot.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import chi.epsilon.epsilon.boozebot.R;

public class RecipesFragment extends Fragment {
    private FloatingActionButton mFloatingActionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_recipes, container, false);
        Log.d("RecipeFragment.java", "onCreate");

        FloatingActionButton floatingActionButton = (FloatingActionButton) v.findViewById(R.id.newRecipeButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddDrinkFragment addDrinkFragment = new AddDrinkFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment_container, addDrinkFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        return v;
    }
}
