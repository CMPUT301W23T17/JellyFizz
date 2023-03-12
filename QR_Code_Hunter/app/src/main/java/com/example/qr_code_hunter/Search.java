package com.example.qr_code_hunter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Search extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference player = db.collection("Players");
    CollectionReference codes = db.collection("QrCodes");

    private ListView listView;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searching);

    }



    /**
     * This method searches for the players with the username the user searches for
     * @param string this is the username the user searched
     */
    public void searchPlayer(String string){
        Query query = player.whereGreaterThanOrEqualTo(FieldPath.documentId(), string)
                .orderBy(FieldPath.documentId())
                .limit(10);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> usernames = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String id = document.getId() ;
                    if (id.startsWith(string)) {
                        usernames.add(id + ": " + document.getLong("score") + "pts");
                    }
                }

                listView = findViewById(R.id.search_list);
//                adapter = new ArrayAdapter<>(this, R.layout.searching_context, usernames);
//                listView.setAdapter(adapter);

            } else {
                // Handle errors here.
                Log.e("TAG", "Error getting documents: ", task.getException());
            }
        });
    }



}


