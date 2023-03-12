package com.example.qr_code_hunter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qr_code_hunter.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomepageFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_screen:
                    replaceFragment(new HomepageFragment());
                    break;
                case R.id.map_screen:
                    replaceFragment(new MapFragment());
                    break;
                case R.id.search_screen:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.player_profile_screen:
                    replaceFragment(new PlayerProfileFragment());
                    break;
                case R.id.ranking_screen:
                    replaceFragment(new RankingFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment ){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}