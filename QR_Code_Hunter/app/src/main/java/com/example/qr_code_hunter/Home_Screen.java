package com.example.qr_code_hunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;

import com.example.qr_code_hunter.databinding.ActivityMainBinding;


public class Home_Screen extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_main);

        binding.bottomNavigationView1.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_screen:
                    break;
                case R.id.map_screen:
                    replaceFragment(new Map_Screen());
                    break;
                case R.id.search_screen:
                    break;
                case R.id.player_profie_screen:
                    break;
                case R.id.ranking_user_screen:
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