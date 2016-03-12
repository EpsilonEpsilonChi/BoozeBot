package chi.epsilon.epsilon.boozebot.activities;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

import chi.epsilon.epsilon.boozebot.R;
import chi.epsilon.epsilon.boozebot.fragments.RecipesFragment;
import chi.epsilon.epsilon.boozebot.fragments.UserFragment;

public class MainActivity extends AppCompatActivity {
    private Firebase mFirebaseRef;
    private List<Fragment> mFragments;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseRef = new Firebase("https://boozebot.firebaseio.com/");

        if (mFirebaseRef.getAuth() == null) {
            redirectToLogin();
        }
        mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null) {
                    redirectToLogin();
                }
            }
        });

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.martini_glass_icon));
//       tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.recipe_book_icon));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.person_icon));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = (ViewPager) findViewById(R.id.home_fragment_viewpager);
        mFragments = new ArrayList<>();
//        mFragments.add(new QueueFragment());
        mFragments.add(new RecipesFragment());
        mFragments.add(new UserFragment());

        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        });
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
