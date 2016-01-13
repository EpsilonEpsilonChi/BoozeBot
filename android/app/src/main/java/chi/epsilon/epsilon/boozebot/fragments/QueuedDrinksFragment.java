package chi.epsilon.epsilon.boozebot.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import chi.epsilon.epsilon.boozebot.BoozeBotApp;
import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.models.Task;

public class QueuedDrinksFragment extends Fragment {
    private TaskAdapter mTaskAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskAdapter = new TaskAdapter(new ArrayList<Task>());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_queued_drinks, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.queued_drink_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mTaskAdapter);

        updateUI();

        Log.d("QueuedDrinkFrag", "Trying to inflate queued drink fragment!");
        return v;
    }

    private void updateUI() {
        final List<Task> tasks = new ArrayList<>();
        final Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");
        String username = ((BoozeBotApp) getActivity().getApplication()).getCurrentUser();

        rootRef.child("Users").child(username).child("pendingTransactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    tasks.add(task);
                }

                // This is jank as fuck.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tasks.size() * 50, getResources().getDisplayMetrics());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
                mRecyclerView.setLayoutParams(lp);
                mTaskAdapter = new TaskAdapter(tasks);
                mRecyclerView.setAdapter(mTaskAdapter);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        mTaskAdapter = new TaskAdapter(tasks);
        mRecyclerView.setAdapter(mTaskAdapter);
    }

    private class TaskHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public TaskHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        public void bindTask(Task task) {
            mTextView.setText(task.getRecipeUsed());
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {
        private List<Task> mTasks;

        public TaskAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            return new TaskHolder(view);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bindTask(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }
    }

}
