package com.example.qr_code_hunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    Search search;
    ListView listView;
    ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searching);

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
}