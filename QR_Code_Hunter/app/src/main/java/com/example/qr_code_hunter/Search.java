package com.example.qr_code_hunter;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Search {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference player = db.collection("Players");

    public interface SearchPlayerCallback {                        //Used ChatGpt for callback interface
        void onSearchPlayerComplete(ArrayList<ArrayList<String>> usernames);
    }

    /**
     * Searches for the username the user inputs
     * @param string username the user inputs
     * @param callback stops the listener when task is complete
     */
    public void searchPlayer(String string, SearchPlayerCallback callback) {
        ArrayList<ArrayList<String>> usernames = new ArrayList<>();
        if(string.length() != 0) {
            Query query = player.whereGreaterThanOrEqualTo(FieldPath.documentId(), string)  //Used ChatGpt to get the query needed
                    .orderBy(FieldPath.documentId())
                    .limit(10);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        if (id.startsWith(string)) {
                            ArrayList<String> innerList = new ArrayList<String>();
                            innerList.add(id);
                            innerList.add(String.valueOf(document.getLong("score")));
                            usernames.add(innerList);
                        }
                    }
                } else {
                    Log.e("TAG", "Error getting documents: ", task.getException());
                }
                callback.onSearchPlayerComplete(usernames);
            });
        } else{
            callback.onSearchPlayerComplete(usernames);
        }
    }
}


