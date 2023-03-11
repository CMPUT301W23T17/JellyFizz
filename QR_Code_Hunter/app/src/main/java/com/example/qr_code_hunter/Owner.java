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

    /**
     * This function add new QrCode
     * @param code
     *       object of QrCode class, store newly created code info
     * @param comment
     *       comment string of that player for new qrcode
     * @param image
     *       image go along with the qrcode just scanned
     */
    public void addQRCode(QrCode code, String comment, String image) {
        DocumentReference qrRef = checkQrCodeExist(code.getHashString());
        if (qrRef == null) {
            // assign document reference to newly create QrCode in case it isn't in the database
            qrRef = createNewCode(code, comment, image);
        }
        addRelationship(qrRef);
        updateSumScore(code);
        // Update user rank
        // Update player rank
    }


    /**
     * Check to see if qrcode already exists in collection
     * @param hashString
     *      SHA-256 string of the newly scanned qr code
     * @return
     *      document reference to scanned code in database if possible, null otherwise
     */
    public DocumentReference checkQrCodeExist(String hashString) {
        DocumentReference newCode = null;
        Task<DocumentSnapshot> documentSnapshotTask = qrcode.document(hashString).get();
        if (documentSnapshotTask.isSuccessful()) {
            DocumentSnapshot documentSnapshot = documentSnapshotTask.getResult();
            if (documentSnapshot.exists()) {
                newCode = qrcode.document(hashString);
            }
        }
        return newCode;
    }


    /**
     * This create add new QrCode into QrCodes collection on Firebase
     * @param qrCode
     *       object of QrCode class, store newly created code info
     * @param comment
     *       comment string of that player for new qrcode
     * @param image
     *       image go along with the qrcode just scanned
     * @return
     *      Returns document reference to new code added in QrCode collection
     */
    public DocumentReference createNewCode(QrCode qrCode, String comment, String image) {
        // Hashing geolocation for new qrcode
        double latitude = qrCode.getLocation().latitude;
        double longitude = qrCode.getLocation().longitude;
        String geohash = GeoFireUtils.getGeoHashForLocation(new GeoLocation(latitude,longitude));
        // Input into database
        Map<String, Object> data = new HashMap<>();
        data.put("geohash",geohash);
        data.put("latitude",latitude);
        data.put("longitude",longitude);
        data.put("score",qrCode.getScore());
        data.put("Privacy",qrCode.getPrivacy());
        // Create new document whose ID is the hash string
        DocumentReference newRef = qrcode.document(qrCode.getHashString());
        newRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Data added successfully");
                        addSubCollection(newRef, comment, image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Working", "Data not added" + e.toString());
                    }
                });
        return newRef;
    }

    /**
     * This add comment and photo into QrCode document, stored as subcollection
     * @param newRef
     *       document reference of new code
     * @param comment
     *       comment string of that player for new qrcode
     * @param image
     *       image go along with the qrcode just scanned
     */
    public void addSubCollection(DocumentReference newRef, String comment, String image) {
        // Create a subcollection called "CommentAndPhoto" and add a new document with "username"
        CollectionReference subRef = newRef.collection("CommentAndPhoto");
        Map<String, Object> subData = new HashMap<>();
        subData.put("comment", comment);
        subData.put("image", image);
        subRef.document(this.getUsername()).set(subData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Working", "Data not added" + e.toString());
                    }
                });
    }


    /**
     * This add new document (represents relationship) into scannedBy collection
     * @param
     *      qrRef document reference to newly scanned QrCode
     */
    public void addRelationship(DocumentReference qrRef) {
        Map<String, Object> data = new HashMap<>();
        data.put("Player",ownerRef);
        data.put("qrCodeScanned",qrRef);
        scanned
                .document()
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Working", "Data not added" + e.toString());
                    }
                });
    }


    /**
     * This update total score of the player after adding given qrCode
     * @param
     *      qrCode belongs to QrCode class, it is the newly scanned code
     */
    public void updateSumScore(QrCode qrCode) {
        int newScore = this.getTotalScore() + qrCode.getScore();
        Map<String, Object> data = new HashMap<>();
        data.put("score",newScore);
        ownerRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Score updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Working", "Score not updated" + e.toString());
                    }
                });
    }


    /**
     * This returns duplication check of newly scanned QrCode
     * @param qrRef
     *      document reference of code just scanned
     * @return
     *      Returns true if duplicated, false otherwise
     */
    public Boolean checkDuplicateCodeScanned(DocumentReference qrRef) {
        Boolean duplicated = false;
        // Get query of players scan newly scanned code
        Query query = scanned.whereEqualTo("Player",ownerRef)
                        .whereEqualTo("qrCodeScanned",qrRef).limit(1);
        // Check if query is empty
        Task<QuerySnapshot> querySnapshotTask = query.get();
        if (querySnapshotTask.isSuccessful()) {
            QuerySnapshot querySnapshot = querySnapshotTask.getResult();
            if (!querySnapshot.isEmpty()) {
                duplicated = true;
            }
        }
        return duplicated;
    }

    public void deleteQRCode(QrCode code) {
    // We need to access the QrCode list to see how it works first, cause it is real time update
    }

    /**
     * This set privacy for owner's info (email and phone number) on their user profile
     * @param visibility
     *      true indicates shows info, false will hide info
     */
    public void setPrivacy(Boolean visibility) {
        this.profileInfo.privacy = visibility;
    }
}
