package com.example.qr_code_hunter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Player{
    protected ArrayList<DocumentReference> myQrCodes = new ArrayList<>();
    protected String uniqueUsername;
    protected PlayerProfile profileInfo;
    protected int totalCodeScanned;
    protected int totalScore;
    protected int rank;

    public Player(String phone, String email, String username, Boolean privacy, ArrayList<DocumentReference> codeScanned, int score, int rank) {
        this.profileInfo = new PlayerProfile(phone, email, privacy);
        this.uniqueUsername = username;
        this.myQrCodes = codeScanned; // Document references of player's QR codes in db
        this.totalScore = score;
        this.rank = rank;
    }

    public String lowestScoreQrCode() {
        final int[] lowest = {-1};
        final String[] lowestQr = {null};
        for (DocumentReference hashString: myQrCodes) {
            hashString.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int qrScore = documentSnapshot.getLong("Score").intValue();
                    if (lowest[0] < 0) {
                        lowest[0] = qrScore;
                        lowestQr[0] = hashString.toString();
                    }
                    else if (lowest[0] > 0 && qrScore < lowest[0]) {
                        lowest[0] = qrScore;
                        lowestQr[0] = hashString.toString();
                    }
                }
            });
        }
        return lowestQr[0];
    }

    public String highestScoreQrCode() {
        final int[] highest = {-1};
        final String[] highestQr = {null};
        for (DocumentReference hashString: myQrCodes) {
            hashString.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int qrScore = documentSnapshot.getLong("Score").intValue();
                    if (highest[0] < 0) {
                        highest[0] = qrScore;
                        highestQr[0] = hashString.toString();
                    }
                    else if (highest[0] > 0 && qrScore > highest[0]) {
                        highest[0] = qrScore;
                        highestQr[0] = hashString.toString();
                    }
                }
            });
        }
        return highestQr[0];
    }

    public void calculateScore() {
        for (DocumentReference hashString: myQrCodes) {
            hashString.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int qrScore = documentSnapshot.getLong("Score").intValue();
                    totalScore += qrScore;
                }
            });
        }
    }

    public int getTotalCodeScanned() {
        return totalCodeScanned;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getRank() {
        return rank;
    }

    public String getUsername() {
        return uniqueUsername;
    }

    public PlayerProfile getProfileInfo() {
        return profileInfo;
    }
}
