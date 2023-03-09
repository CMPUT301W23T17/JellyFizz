package com.example.qr_code_hunter;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qr_code_hunter.databinding.ActivityMainBinding;


public class main_activity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Home_screen());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_screen:
                    getSupportActionBar().setTitle(R.string.app_name);
                    getSupportActionBar().hide();
                    replaceFragment(new Home_screen());
                    break;
                case R.id.map_screen:
                    replaceFragment(new Map_screen());
                    break;
                case R.id.search_screen:
                    replaceFragment(new Search_screen());
                    break;
                case R.id.player_profie_screen:
                    replaceFragment(new Profie_screen());
                    break;
                case R.id.ranking_user_screen:
                    getSupportActionBar().show();
                    getSupportActionBar().setTitle("Leaderboard");
                    replaceFragment(new Ranking_screen());
                    break;
            }
            return true;
        });

    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}