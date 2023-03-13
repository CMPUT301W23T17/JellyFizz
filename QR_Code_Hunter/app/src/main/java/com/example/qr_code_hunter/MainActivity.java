package com.example.qr_code_hunter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Arrays;


import com.example.qr_code_hunter.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    //ActivyMainBinding is an android library that allows a way to access the views in the activity_main.xml (navigation bar is stored there)
    ActivityMainBinding binding;
    
    Search search;
    ListView listView;
    ArrayAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        //Check if user has created an account before on this specific device, 1) if yes go to homepage 2) if no go to loginPage
        String accountCreatedKey = getString(R.string.accountCreated);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean accountCreated = prefs.contains(accountCreatedKey);

        if (accountCreated) {
            //set the owner
            loginActivity.setOwner( prefs.getString(accountCreatedKey, ""));
            replaceFragment(new HomepageFragment());
        } else {
            Intent intent = new Intent(this, loginActivity.class);
            startActivity(intent);
        }

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
        
        search = new Search();
        ArrayList<String> list = new ArrayList<>();

        View searchbutton = findViewById(R.id.search_bar);

        searchbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText text = findViewById(R.id.enter_username);
                search.searchPlayer(text.getText().toString(), new Search.SearchPlayerCallback() {
                    @Override
                    public void onSearchPlayerComplete(ArrayList<String> usernames) {
                        if(list.isEmpty()){
                            list.addAll(usernames);
                        }else{
                            list.clear();
                            list.addAll(usernames);
                        }
                        listView = findViewById(R.id.search_list);
                        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.searching_context, list);
                        listView.setAdapter(adapter);
                        System.out.println(list);
                    }
                });

            }
        });
    }

    private void replaceFragment(Fragment fragment ){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}