package chi.epsilon.epsilon.boozebot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import chi.epsilon.epsilon.boozebot.R;

public class CreateAccountFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_create_account, container, false);
        final Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");

        Button createAcct = (Button) v.findViewById(R.id.btn_signup);
        createAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                final String name = ((EditText)v.findViewById(R.id.input_name)).getText().toString();
                final String un = ((EditText)v.findViewById(R.id.input_username)).getText().toString();
                final String email = ((EditText)v.findViewById(R.id.input_email)).getText().toString();
                final String pw = ((EditText)v.findViewById(R.id.input_password)).getText().toString();

                if (name.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_name, Toast.LENGTH_SHORT).show();
                } else if (un.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_username, Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_email, Toast.LENGTH_SHORT).show();
                } else if (pw.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_pw, Toast.LENGTH_SHORT).show();
                } else {
                    // If all fields are filled in:
                    rootRef.createUser(un, pw, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            // Add the user to fb
                            rootRef.child("Users").child(un).setValue("");
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // there was an error
                            Toast.makeText(getContext(), "Firebase fucked up!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        return v;
    }
}
