package chi.epsilon.epsilon.boozebot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.activities.AddDrinkActivity;
import chi.epsilon.epsilon.boozebot.activities.MainActivity;
import chi.epsilon.epsilon.boozebot.models.Task;

public class QueueFragment extends Fragment {
    private RecyclerView mQueueRecyclerView;
    private DrinkAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("QueueFragment.java", "trying to show queue view!");
        final View v = inflater.inflate(R.layout.fragment_queue, container, false);

        mQueueRecyclerView = (RecyclerView) v.findViewById(R.id.queue_recycler);
        mQueueRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQueueRecyclerView.setAdapter(mAdapter);

        mFloatingActionButton = (FloatingActionButton) v.findViewById(R.id.newDrinkButton);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AddDrinkActivity.class);
                startActivity(i);
            }
        });

        updateUI();

        return v;
    }

    private void updateUI() {
        final List<Task> drinks = new ArrayList<>();
        // Get list of drinks in Queue
        final Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");
        rootRef.child("drinkQueue").child("tasks").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                drinks.clear();
                Log.d("LOOK", dataSnapshot.getValue().toString());
                for (DataSnapshot taskSnap : dataSnapshot.getChildren()) {
                    Task task = taskSnap.getValue(Task.class);
                    drinks.add(task);
                }
                mAdapter = new DrinkAdapter(drinks);
                mQueueRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        mAdapter = new DrinkAdapter(drinks);
        mQueueRecyclerView.setAdapter(mAdapter);
    }

    private class DrinkHolder extends RecyclerView.ViewHolder {
        public TextView mRecipeNameTextView;
        public TextView mTimestampTextView;
        private Task mTask;

        public DrinkHolder(View itemView) {
            super(itemView);

            mRecipeNameTextView = (TextView) itemView.findViewById(R.id.drinkName);
            mTimestampTextView = (TextView) itemView.findViewById(R.id.timestamp);
        }

        public void bindDrink(Task task) {
            mTask = task;
            mRecipeNameTextView.setText(mTask.getRecipeUsed());
            mTimestampTextView.setText(mTask.getTimestamp());
        }
    }

    private class DrinkAdapter extends RecyclerView.Adapter<DrinkHolder> {
        private List<Task> mDrinks;

        public DrinkAdapter(List<Task> drinks) {
            mDrinks = drinks;
        }

        @Override
        public DrinkHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.drink_queue_item, parent, false);
            return new DrinkHolder(view);
        }

        @Override
        public void onBindViewHolder(DrinkHolder holder, int position) {
            Task task = mDrinks.get(position);
            holder.bindDrink(task);
        }

        @Override
        public int getItemCount() {
            return mDrinks.size();
        }
    }
}
