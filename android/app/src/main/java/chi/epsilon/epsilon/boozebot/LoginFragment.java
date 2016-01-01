package chi.epsilon.epsilon.boozebot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.Map;

public class LoginFragment extends Fragment {
    private Button mLoginButton;
    private Button  mCreateButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main_login, container, false);
        final Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");


        mLoginButton = (Button) v.findViewById(R.id.login_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                String un = ((EditText)v.findViewById(R.id.username)).getText().toString();
                String pw =  ((EditText)v.findViewById(R.id.password)).getText().toString();

                // Attempt to authenticate the user
                rootRef.authWithPassword(un, pw, new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        Toast.makeText(getContext(), "Successful Login!", Toast.LENGTH_SHORT).show();
                        // If successful, start main activity
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        // Else, show error toast
                        Toast.makeText(getContext(), "You fucked up!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        mCreateButton = (Button) v.findViewById(R.id.create_button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // show create account fragment

                rootRef.createUser("test@blah.com", "pw", new Firebase.ValueResultHandler<Map<String, Object>>() {
                    @Override
                    public void onSuccess(Map<String, Object> result) {
                        System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    }

                    @Override
                    public void onError(FirebaseError firebaseError) {
                        // there was an error
                    }
                });
            }
        });

        return v;
    }

}
