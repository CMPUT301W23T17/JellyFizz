package com.example.qr_code_hunter;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Rank {
    public String username;
    public int rankingTotalScore, totalScore, rankingCode, highestCode;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference player = db.collection("Players");

    public Rank(){
    }

    public Rank(FirebaseFirestore fireStore) {
        db = fireStore;
        player = db.collection("Players");
    }

    public Rank(String username, int score, int rankingTotal, int rankingCode, int highestCode) {
        this.username = username;
        this.rankingTotalScore = rankingTotal;
        this.totalScore = score;
        this.rankingCode = rankingCode;
        this.highestCode = highestCode;
    }

    public interface ArrangeRankCallback {
        void onArrangeRankComplete(ArrayList<Rank> ranking);
    }

    /**
     * Get the ranking of all players, add to list in descending order
     * @param callback stops the listener when task is complete
     */
    public void arrangeRankTotal(ArrangeRankCallback callback) {
        ArrayList<Rank> rankArrs = new ArrayList<>();
        player.orderBy("score", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getId();
                            int userRank = document.getLong("rank").intValue();
                            int score = document.getLong("score").intValue();
                            Rank playerR = new Rank(username,score,userRank, 0,0);
                            rankArrs.add(playerR);
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                    callback.onArrangeRankComplete(rankArrs);
                });
    }
    /**
     * Get the ranking of all players, add to list in descending order
     * @param callback stops the listener when task is complete
     */
    public void arrangeRankCode(ArrangeRankCallback callback) {
        ArrayList<Rank> rankArrs = new ArrayList<>();
        player.orderBy("highestCode", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int index = 1;
                        int rankingCode = 1;
                        int lastCode = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String username = document.getId();
                            int userHighestCode = document.getLong("highestCode").intValue();
                            if (userHighestCode == lastCode) {
                                Rank playerR = new Rank(username, 0, 0, rankingCode, userHighestCode);
                                rankArrs.add(playerR);
                            }
                            else {
                                Rank playerR = new Rank(username, 0, 0, index, userHighestCode);
                                rankingCode = index;
                                rankArrs.add(playerR);
                            }
                            lastCode = userHighestCode;
                            index = index +1 ;
                        }
                    } else {
                        Log.e("TAG", "Error getting documents: ", task.getException());
                    }
                    callback.onArrangeRankComplete(rankArrs);
                });
    }
}
