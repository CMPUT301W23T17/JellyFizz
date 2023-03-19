package com.example.qr_code_hunter;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Rank {
    public String username;
    public int position, score;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference player = db.collection("Players");

    public Rank(){}

    public Rank(String username, int score, int ranking) {
        this.username = username;
        this.position = ranking;
        this.score = score;
    }

    public interface ArrangeRankCallback {
        void onArrangeRankComplete(ArrayList<Rank> ranking);
    }

    /**
     * Get the ranking of all players, add to list in descending order
     * @param callback stops the listener when task is complete
     */
    public void arrangeRank(ArrangeRankCallback callback) {
        ArrayList<Rank> rankArrs = new ArrayList<>();
        player.orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getId();
                            int userRank = document.getLong("rank").intValue();
                            int score = document.getLong("score").intValue();
                            Rank playerR = new Rank(username,score,userRank);
                            rankArrs.add(playerR);
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                    callback.onArrangeRankComplete(rankArrs);
                });
    }
}
