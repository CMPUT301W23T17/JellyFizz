package com.example.qr_code_hunter;

import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import io.grpc.Context;

public class Search {
    FirebaseFirestore db;
    CollectionReference player = db.collection("Players");
    CollectionReference codes = db.collection("QrCodes");

    private ListView listView;
    private ArrayAdapter adapter;

    public void searchPlayer(String string){
        Query query = player.whereGreaterThanOrEqualTo(FieldPath.documentId(), string)
                .orderBy(FieldPath.documentId())
                .limit(10);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<String> usernames = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String documentId = document.getId();
                    if (documentId.startsWith(string)) {
                        usernames.add(documentId);
                    }
                }

                listView = listView.findViewById(R.id.search_list);
                adapter = new ArrayAdapter<>(this, R.layout.searching_context, usernames);
                listView.setAdapter(adapter);



            } else {
                // Handle errors here.
                Log.e("TAG", "Error getting documents: ", task.getException());
            }
        });


    }



}


