package com.example.qr_code_hunter;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.qr_code_hunter.databinding.ActivityMainBinding;


public class main_activity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new Home_screen());
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.home_screen:
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
                    replaceFragment(new Ranking_screen());
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.map_screen);
        assert fragment != null;
        fragment.onActivityResult(requestCode, resultCode, data);
    }

}