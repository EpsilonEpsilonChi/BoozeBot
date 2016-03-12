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

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.activities.MainActivity;

public class MainLoginFragment extends Fragment {
    private Button mLoginButton;
    private Button  mCreateButton;
    private TextView mEmailView;
    private TextView mPasswordView;
    private  Firebase mFirebaseRef;

    private static String TAG = MainLoginFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseRef = new Firebase("https://boozebot.firebaseio.com/");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main_login, container, false);

        mEmailView = (EditText) v.findViewById(R.id.username);
        mPasswordView = (EditText) v.findViewById(R.id.password);

        mLoginButton = (Button) v.findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {

                attemptLogin();
            }
        });

        mCreateButton = (Button) v.findViewById(R.id.create_button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show create account fragment
                CreateAccountFragment createAccount = new CreateAccountFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, createAccount)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return v;
    }

    private void attemptLogin() {
        final String email = mEmailView.getText().toString();
        final String password =  mPasswordView.getText().toString();


        if (attemptSubmit(email, password)) {
            mFirebaseRef.authWithPassword(email, password, new AuthResultHandler());
        }
    }

    private boolean attemptSubmit(String email, String password) {

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);

        mEmailView.setError(null);
        mPasswordView.setError(null);

        boolean cancel = false;
        View focusView = null;

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

    private void startMainActivity() {
        Intent i = new Intent(getActivity(), MainActivity.class);
        startActivity(i);
        getActivity().finish();

    }

    private class AuthResultHandler implements Firebase.AuthResultHandler {

        @Override
        public void onAuthenticated(AuthData authData) {
            startMainActivity();
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            switch (firebaseError.getCode()) {
                case FirebaseError.INVALID_EMAIL:
                case FirebaseError.USER_DOES_NOT_EXIST:
                    mEmailView.setError(getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                    break;
                case FirebaseError.INVALID_PASSWORD:
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    break;
                default:
                    Log.e(TAG, "Unrecognized auth error: " + firebaseError.toString());
            }
        }
    }
}
