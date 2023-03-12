package com.example.qr_code_hunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
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

        list = search.searchPlayer("Kyle");


//        String[] cities = {
//                "Edmonton", "Vancouver", "Moscow",
//                "Sydney", "Berlin", "Vienna",
//                "Tokyo", "Beijing", "Osaka", "New Delhi"
//        };

//        ArrayList<String> list = new ArrayList<>();
//
//        list.addAll(Arrays.asList(cities));


        listView = findViewById(R.id.search_list);
        adapter = new ArrayAdapter<>(this, R.layout.searching_context, list);
        listView.setAdapter(adapter);


    }
}