package chi.epsilon.epsilon.boozebot.activities;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.fragments.HomeFragment;
import chi.epsilon.epsilon.boozebot.fragments.MainLoginFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.home_fragment_container);

        if (fragment == null) {
            fragment = new HomeFragment();
            fm.beginTransaction()
                    .add(R.id.home_fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }
}
