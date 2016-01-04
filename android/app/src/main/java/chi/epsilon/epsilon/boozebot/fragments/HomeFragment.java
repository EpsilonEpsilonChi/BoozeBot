package chi.epsilon.epsilon.boozebot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import chi.epsilon.epsilon.boozebot.R;

public class HomeFragment extends Fragment {
    private RecyclerView mQueueRecyclerView;
    private DrinkAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_home, container, false);

        mQueueRecyclerView = (RecyclerView) v.findViewById(R.id.queue_recycler);
        mQueueRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    private void updateUI() {
        // Get list of drinks in Queue
        List<String> drinks = new ArrayList<String>();
        drinks.add("Test Drink1");
        drinks.add("Test Drink2");
        drinks.add("Test Drink3");

        mAdapter = new DrinkAdapter(drinks);
        mQueueRecyclerView.setAdapter(mAdapter);
    }

    private class DrinkHolder extends RecyclerView.ViewHolder {
        public TextView mTitleTextView;

        public DrinkHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView)itemView;
        }
    }

    private class DrinkAdapter extends RecyclerView.Adapter<DrinkHolder> {
        private List<String> mDrinks;

        public DrinkAdapter(List<String> drinks) {
            mDrinks = drinks;
        }

        @Override
        public DrinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new DrinkHolder(view);
        }

        @Override
        public void onBindViewHolder(DrinkHolder holder, int position) {
            String string = mDrinks.get(position);
            holder.mTitleTextView.setText(string);
            holder.mTitleTextView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        @Override
        public int getItemCount() {
            Log.d("LOOK", String.valueOf(mDrinks.size()));
            return mDrinks.size();
        }
    }
}
