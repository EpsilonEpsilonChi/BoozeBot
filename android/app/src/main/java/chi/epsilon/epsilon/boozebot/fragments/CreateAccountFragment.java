package chi.epsilon.epsilon.boozebot.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.activities.MainActivity;
import chi.epsilon.epsilon.boozebot.models.User;

public class CreateAccountFragment extends Fragment {
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private Firebase mFirebaseRef;

    private final static String TAG = CreateAccountFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_create_account, container, false);
        mFirebaseRef = new Firebase("https://boozebot.firebaseio.com/");

        Button createAcct = (Button) v.findViewById(R.id.btn_signup);
        createAcct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                mFirstNameView = (EditText) v.findViewById(R.id.input_first_name);
                mLastNameView = (EditText) v.findViewById(R.id.input_last_name);
                mEmailView = (EditText) v.findViewById(R.id.input_email);
                mPasswordView = (EditText) v.findViewById(R.id.input_password);

                attemptRegister();
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

    private void attemptRegister() {
        final String firstName = mFirstNameView.getText().toString();
        final String lastName = mLastNameView.getText().toString();
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        if (attemptSubmit(firstName, lastName, email, password)) {
            mFirebaseRef.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    User.UserBuilder builder = new User.UserBuilder();
                    User newUser = builder.firstName(firstName)
                            .lastName(lastName)
                            .email(email)
                            .build();

                    String uid = result.get("uid").toString();

                    // Add the user to db
                    mFirebaseRef.child("Users").child(uid).setValue(newUser);
                    Toast.makeText(getContext(), "Account created!", Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(getActivity(), MainActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    switch (firebaseError.getCode()) {
                        case FirebaseError.INVALID_EMAIL:
                        case FirebaseError.USER_DOES_NOT_EXIST:
                            mEmailView.setError(getString(R.string.error_invalid_email));
                            mEmailView.requestFocus();
                            break;
                        default:
                            Log.e(TAG, "Unrecognized auth error: " + firebaseError.toString());
                    }
                }
            });
        }
    }

    private boolean attemptSubmit(String firstName, String lastName, String email, String password) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);

        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(firstName)) {
            mPasswordView.setError(getString(R.string.no_first_name));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(lastName)) {
            mPasswordView.setError(getString(R.string.no_last_name));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.no_pw));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.no_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }
}
