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
import android.widget.ImageView;
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
    private Firebase mFirebaseRef;
    private TaskAdapter mTaskAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTaskAdapter = new TaskAdapter(new ArrayList<Task>());
        mFirebaseRef = new Firebase("https://boozebot.firebaseio.com/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_queued_drinks, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.queued_drink_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mTaskAdapter);

        updateUI();
        return v;
    }

    private void updateUI() {
        final List<Task> tasks = new ArrayList<>();
        String username = mFirebaseRef.getAuth().getUid();

        mFirebaseRef.child("Users").child(username).child("pendingTransactions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks.clear();
                for (DataSnapshot taskSnapshot : dataSnapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    tasks.add(task);
                }

                // This is jank as fuck.
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tasks.size() * 70, getResources().getDisplayMetrics());
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
        public TextView mRecipeName;
        public TextView mEta;
        public ImageView mRecipeImage;

        public TaskHolder(View itemView) {
            super(itemView);
            mRecipeName = (TextView) itemView.findViewById(R.id.recipe_title);
            mEta = (TextView) itemView.findViewById(R.id.eta);
            mRecipeImage = (ImageView) itemView.findViewById(R.id.recipe_image);
            mRecipeImage.setImageDrawable(getResources().getDrawable(R.drawable.martini_glass_icon));
        }

        public void bindTask(Task task) {
            mRecipeName.setText(task.getRecipeUsed());
            mEta.setText("ETA");
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
            View view = layoutInflater.inflate(R.layout.queued_drink_item, parent, false);
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
