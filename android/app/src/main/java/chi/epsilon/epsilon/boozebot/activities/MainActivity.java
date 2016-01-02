package chi.epsilon.epsilon.boozebot.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.fragments.HomeFragment;
import chi.epsilon.epsilon.boozebot.fragments.MainLoginFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.home_fragment_container);

        if (fragment == null) {
            fragment = new HomeFragment();
            fm.beginTransaction()
                    .add(R.id.home_fragment_container, fragment)
                    .commit();
        }
    }
}
