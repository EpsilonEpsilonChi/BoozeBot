package chi.epsilon.epsilon.boozebot.activities;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.fragments.HomeFragment;
import chi.epsilon.epsilon.boozebot.fragments.UserFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<Fragment> mFragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        //FragmentManager fm = getSupportFragmentManager();
        //Fragment fragment = fm.findFragmentById(R.id.home_fragment_container);

        //if (fragment == null) {
        //    fragment = new HomeFragment();
        //    fm.beginTransaction()
        //            .add(R.id.home_fragment_container, fragment)
        //            .commit();
        //}

        mViewPager = (ViewPager) findViewById(R.id.home_fragment_viewpager);
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new UserFragment());

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

}
