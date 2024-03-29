package com.example.qr_code_hunter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;

import com.example.qr_code_hunter.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private boolean isFragmentTransactionInProgress = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ActivityMainBinding is an android library that allows a way to access the views
        // in the activity_main.xml (navigation bar is stored there)
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check if user has created an account before on this specific device,
        // 1) if yes: go to homepage, 2) if no: go to loginPage
        String accountCreatedKey = getString(R.string.accountCreated);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean accountCreated = prefs.contains(accountCreatedKey);

        if (accountCreated) {
            // Set the ownerName to userName
            LoginActivity.setOwnerName(prefs.getString(accountCreatedKey, ""));
            replaceFragment(new HomepageFragment());

        } else {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_screen:
                    if(!(getVisibleFragment() instanceof HomepageFragment)){
                        replaceFragment(new HomepageFragment());}
                    break;

                case R.id.map_screen:
                    if(!(getVisibleFragment() instanceof MapFragment)){
                        replaceFragment(new MapFragment());}
                    break;

                case R.id.search_screen:
                    if(!(getVisibleFragment() instanceof SearchFragment)){
                        replaceFragment(new SearchFragment());}
                    break;

                case R.id.player_profile_screen:
                    if(!(getVisibleFragment() instanceof PlayerProfileFragment)){
                        replaceFragment(new PlayerProfileFragment());}
                    break;

                case R.id.ranking_screen:
                    if(!(getVisibleFragment() instanceof RankingFragment)){
                        replaceFragment(new RankingFragment());}
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment ){
        if (!isFragmentTransactionInProgress) {
            isFragmentTransactionInProgress = true;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commitAllowingStateLoss();
            getSupportFragmentManager().executePendingTransactions();
            isFragmentTransactionInProgress = false;
        }
    }

    private Fragment getVisibleFragment() {
        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();

        for (Fragment fragment: fragments) {
            if (fragment != null && fragment.isVisible()) return fragment;
        }
        return null;
    }

}
