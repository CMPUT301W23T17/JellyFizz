package com.example.qr_code_hunter;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class Player {
    protected ArrayList<DocumentReference> myQrCodes = new ArrayList<>();
    protected String uniqueUsername;
    protected PlayerProfile profileInfo;
    protected int totalCodeScanned;
    protected int totalScore;
    protected int rank;


    /*
     * `maxScore` is initialized to the lowest possible integer value, and `maxScoreQrCode` is initialized to null.
     * `minScore` is initialized to the highest possible integer value, and `minScoreQrCode` is initialized to null.
     */
    protected int maxScore = Integer.MIN_VALUE;
    protected DocumentReference maxScoreQrCode = null;
    protected int minScore = Integer.MAX_VALUE;
    protected DocumentReference minScoreQrCode = null;


    //Have to discuss how the arraylist of qrcodes in the app will be handled. I was thinking it will be initialized once and then
    // add qrCode and delete Code will update it

    public Player(String phone, String email, String username, Boolean privacy, ArrayList<DocumentReference> codeScanned, int score, int rank) {
        this.profileInfo = new PlayerProfile(phone, email, privacy);
        this.uniqueUsername = username;
        this.myQrCodes = codeScanned; // Document references of player's QR codes in db
        this.totalScore = score;
        this.rank = rank;
    }

    /**
     * Calculates the minimum and maximum score achieved by the player across all their scanned QR codes,
     * <p>
     * as well as the total score. This method retrieves all the QR codes that have been scanned by the player
     * <p>
     * and iterates over them only once to minimize the number of database calls. The results are stored in
     * <p>
     * instance variables.
     */
    public void setMinMaxandTotalScore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get all the QR code references for the current player
        ArrayList<DocumentReference> qrCodeRefs = myQrCodes;

        // Set the total score to 0 initially
        maxScore = Integer.MIN_VALUE;
        minScore = Integer.MAX_VALUE;
        this.totalScore = 0;

        // Iterate over each QR code and update the min, max, and total scores
        for (DocumentReference qrCodeRef : qrCodeRefs) {
            qrCodeRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        int score = document.getLong("score").intValue();
                        // Update the total score
                        totalScore += score;
                        // Check if this score is greater than the current max score
                        if (score > maxScore) {
                            maxScore = score;
                            maxScoreQrCode = qrCodeRef;
                        }
                        // Check if this score is less than the current min score
                        if (score < minScore) {
                            minScore = score;
                            minScoreQrCode = qrCodeRef;
                        }
                    }
                } else {
                // Handle the error
                    Log.d("Database Error", "Error getting qrCode document", task.getException());
                }
            });
        }
    }


    /**
     * This finds the QrCode with the lowest score
     *
     * @return Returns the hash string of the QrCode with the lowest score
     */

    public String lowestScoreQrCode() {
        final int[] lowest = {-1};
        final String[] lowestQr = {null};

        for (DocumentReference hashString : myQrCodes) {
            hashString.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int qrScore = documentSnapshot.getLong("Score").intValue();
                    if (lowest[0] < 0) {
                        lowest[0] = qrScore;
                        lowestQr[0] = hashString.toString();
                    } else if (lowest[0] > 0 && qrScore < lowest[0]) {
                        lowest[0] = qrScore;
                        lowestQr[0] = hashString.toString();
                    }
                }
            });
        }
        return lowestQr[0];
    }

    /**
     * This finds the QrCode with the highest score
     *
     * @return Returns the hash string of the QrCode with the highest score
     */
    public String highestScoreQrCode() {
        final int[] highest = {-1};
        final String[] highestQr = {null};
        for (DocumentReference hashString : myQrCodes) {
            hashString.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int qrScore = documentSnapshot.getLong("Score").intValue();
                    if (highest[0] < 0) {
                        highest[0] = qrScore;
                        highestQr[0] = hashString.toString();
                    } else if (highest[0] > 0 && qrScore > highest[0]) {
                        highest[0] = qrScore;
                        highestQr[0] = hashString.toString();
                    }
                }
            });
        }
        return highestQr[0];
    }

    /**
     * This sums up the scores of all QrCodes the player has scanned
     *
     * @return Returns the total score of the QrCodes scanned
     */
    public void calculateScore() {
        for (DocumentReference hashString : myQrCodes) {
            hashString.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    int qrScore = documentSnapshot.getLong("Score").intValue();
                    totalScore += qrScore;
                }
            });
        }
    }

    /**
     * This returns the total amount of QrCode scanned by the player
     *
     * @return Returns an integer
     */
    public int getTotalCodeScanned() {
        return myQrCodes.size();
    }

    /**
     * This returns the total score of the player
     *
     * @return Returns an integer
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * This returns the rank of the player compared to other players based on the highest scoring QrCode
     *
     * @return Returns an integer
     */
    public int getRank() {
        return rank;
    }

    /**
     * This returns the username of the player
     *
     * @return Returns a string
     */
    public String getUsername() {
        return uniqueUsername;
    }

    /**
     * This returns the player profile of the player
     *
     * @return Returns a PlayerProfile type
     */
    public PlayerProfile getProfileInfo() {
        return profileInfo;
    }
}
