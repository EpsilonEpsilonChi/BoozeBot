package chi.epsilon.epsilon.boozebot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.models.User;

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
                final String firstName = ((EditText)v.findViewById(R.id.input_first_name)).getText().toString();
                final String lastName = ((EditText)v.findViewById(R.id.input_last_name)).getText().toString();
                final String un = ((EditText)v.findViewById(R.id.input_username)).getText().toString();
                final String email = ((EditText)v.findViewById(R.id.input_email)).getText().toString();
                final String pw = ((EditText)v.findViewById(R.id.input_password)).getText().toString();

                if (firstName.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_first_name, Toast.LENGTH_SHORT).show();
                } else if (lastName.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_last_name, Toast.LENGTH_SHORT).show();
                } else if (un.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_username, Toast.LENGTH_SHORT).show();
                } else if (email.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_email, Toast.LENGTH_SHORT).show();
                } else if (pw.isEmpty()) {
                    Toast.makeText(getContext(), R.string.no_pw, Toast.LENGTH_SHORT).show();
                } else {
                    // TODO(mshelley) check that username is unique; if not, show an error & do not create the account.

                    // If all fields are filled in:
                    rootRef.createUser(email, pw, new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> result) {
                            User.UserBuilder builder = new User.UserBuilder();
                            User newUser = builder.firstName(firstName).lastName(lastName).username(un).email(email).build();

                            // Add the user to db
                            rootRef.child("Users").child(un).setValue(newUser);
                            Toast.makeText(getContext(), "Account created!", Toast.LENGTH_SHORT).show();

                            // TODO(mshelley) Go to the main activity
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            // TODO(mshelley) Add error handling -- display toasts for duplicate email, etc.
                            // there was an error
                            Log.d("LOOK", firebaseError.toString());
                            Toast.makeText(getContext(), "Firebase fucked up!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        TextView loginLink = (TextView) v.findViewById(R.id.link_login);
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Goto main login fragment

                MainLoginFragment loginFragment = new MainLoginFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, loginFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }
}
