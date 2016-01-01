package chi.epsilon.epsilon.boozebot.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;

import chi.epsilon.epsilon.boozebot.fragments.MainLoginFragment;
import chi.epsilon.epsilon.boozebot.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
//        Firebase rootRef = new Firebase("https://boozebot.firebaseio.com/");
//        Firebase rootRef = new Firebase("https://kenzandroidtest.firebaseio.com/");
//        rootRef.child("users").push().setValue("Kenziscool");
//        rootRef.child("users").child("newuser").setValue("new one");


        setContentView(R.layout.activity_login);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = new MainLoginFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
