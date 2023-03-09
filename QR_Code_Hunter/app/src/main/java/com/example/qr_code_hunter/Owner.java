package com.example.qr_code_hunter;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Owner extends Player{

    private FirebaseFirestore db;
    private CollectionReference player;

    public Owner(String phone, String email, String username, Boolean privacy, ArrayList<String> codeScanned, int score, int rank) {
        super(phone, email, username, privacy, codeScanned, score, rank);
    }

    public void addQRCode(QRCode code) {

    }

    public void deleteQRCode(QRCode code) {

    }

    public void updateSumScore() {
        // this method might not be needed
    }

    public void setPrivacy(Boolean visibility) {
        this.profileInfo.privacy = visibility;
    }
}
