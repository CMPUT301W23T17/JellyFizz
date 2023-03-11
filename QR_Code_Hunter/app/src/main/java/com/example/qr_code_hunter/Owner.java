package com.example.qr_code_hunter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Owner extends Player{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference player = db.collection("Players");
    private final CollectionReference qrcode = db.collection("QrCodes");
    private final CollectionReference scanned = db.collection("scannedBy");

    private DocumentReference ownerRef;

    public Owner(String phone, String email, String username, Boolean privacy, ArrayList<DocumentReference> codeScanned, int score, int rank) {
        super(phone, email, username, privacy, codeScanned, score, rank);
        this.ownerRef = this.player.document(username);
    }

    public void addQRCode(QRCode code, String comment, String image) {
        DocumentReference qrRef = checkQrCodeExist(code.getHashString());
        if (qrRef == null) {
            qrRef = createNewCode(code, comment, image);
            addRelationship(qrRef);
            updateSumScore(code);
            // Update user rank
            // Update player rank
        } else {
            // Check if player have scanned

            if (checkDuplicateCodeScanned(qrRef)) {
                // prevent from adding new code
                // Prompt scanned again
            } else {
                // Player have not scan, but code already exists
                addRelationship(qrRef);
                updateSumScore(code);
                // Update user rank
                // Update player rank
            }
        }
    }



    

    public void deleteQRCode(QRCode code) {

    }

    public void setPrivacy(Boolean visibility) {
        this.profileInfo.privacy = visibility;
    }
}
