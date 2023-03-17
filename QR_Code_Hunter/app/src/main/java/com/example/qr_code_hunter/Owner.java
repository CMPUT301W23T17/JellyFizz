package com.example.qr_code_hunter;

import android.os.Parcel;
import android.os.Parcelable;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Owner extends Player implements Parcelable {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference player = db.collection("Players");
    private final CollectionReference qrcode = db.collection("QrCodes");
    private final CollectionReference scanned = db.collection("scannedBy");

    private DocumentReference ownerRef;
    Boolean codeDuplicated = false;
    DocumentReference existedQrRef;
    DocumentReference highestQrCode;

    public Owner(){}

    public Owner(String phone, String email, String username, Boolean privacy,
                 ArrayList<DocumentReference> codeScanned, int score, int rank, int totalCodeScanned) {
        super(phone, email, username, privacy, codeScanned, score, rank, totalCodeScanned);
        this.ownerRef = this.player.document(username);
    }

    protected Owner(Parcel in) {
        byte tmpCodeDuplicated = in.readByte();
        codeDuplicated = tmpCodeDuplicated == 0 ? null : tmpCodeDuplicated == 1;
    }

    public static final Creator<Owner> CREATOR = new Creator<Owner>() {
        @Override
        public Owner createFromParcel(Parcel in) {
            return new Owner(in);
        }

        @Override
        public Owner[] newArray(int size) {
            return new Owner[size];
        }
    };

    /**
     * This function add new QrCode
     *
     * @param code    object of QrCode class, store newly created code info
     * @param comment comment string of that player for new qrcode
     * @param image   image go along with the qrcode just scanned
     * @param privacy  true indicates private code, false means public
     */
    public void addQRCode(QrCode code, Boolean privacy, String comment, String image) {
        final DocumentReference[] qrRef = new DocumentReference[1];
        checkQrCodeExist(code.getHashString(), new CheckExistCallback() {
            @Override
            public void onCheckExitedComplete(DocumentReference qrRef) {
                // Do something with the documentReference object here
                    if (qrRef == null) {
                        // assign document reference to newly create QrCode in case it isn't in the database
                        qrRef = createNewCode(code, privacy, comment, image);
                    }
                    addRelationship(qrRef, comment, image);
                    updateSumScore(code);
                    updateRank();
                    updateHighestCode(code);
                    }
        });
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeByte((byte) (codeDuplicated == null ? 0 : codeDuplicated ? 1 : 2));
    }

    public interface CheckExistCallback {
        void onCheckExitedComplete(DocumentReference existedQrRef);
    }

    /**
     * Check to see if qrcode already exists in collection
     *
     * @param hashString SHA-256 string of the newly scanned qr code
     * @param callback
     *      interface deal with asynchronous problem when check code existence
     */
    public void checkQrCodeExist(String hashString, CheckExistCallback callback) {
        DocumentReference docRef = qrcode.document(hashString);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().exists()) {
                    existedQrRef = qrcode.document(hashString);
                    Log.d("Working", "Document exists!");
                }
            } else {
                Log.e("Working", "Failed with: ", task.getException());
            }
            callback.onCheckExitedComplete(existedQrRef);
        });
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
    public DocumentReference createNewCode(QrCode qrCode, Boolean privacy, String comment, String image) {
        // Hashing geolocation for new qrcode
        double latitude = qrCode.getGeolocation().latitude;
        double longitude = qrCode.getGeolocation().longitude;
        // Input into database
        Map<String, Object> data = new HashMap<>();
        data.put("latitude",latitude);
        data.put("longitude",longitude);
        data.put("Score",qrCode.getScore());
        data.put("Privacy",privacy);
        data.put("codeName",qrCode.getName());
        data.put("binaryString",qrCode.getBinaryString());
        // Create new document whose ID is the hash string
        DocumentReference newRef = qrcode.document(qrCode.getHashString());
        newRef.set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Working", "Data not added" + e.toString());
                    }
                });
        return newRef;
    }

    /**
     * This add new document (represents relationship) into scannedBy collection
     * @param qrRef
     *       document reference to newly scanned QrCode
     * @param comment
     *       comment string of that player for new qrcode
     * @param image
     *       image go along with the qrcode just scanned
     */
    public void addRelationship(DocumentReference qrRef, String comment, String image) {
        Map<String, Object> data = new HashMap<>();
        data.put("Player", ownerRef);
        data.put("qrCodeScanned", qrRef);
        data.put("Comment", comment);
        data.put("Photo", image);
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
                        Log.e("Working", "Data not added" + e.toString());
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
        int newTotalCodeScanned = this.getTotalCodeScanned() + 1;
        Map<String, Object> data = new HashMap<>();
        data.put("score",newScore);
        data.put("totalCodeScanned",newTotalCodeScanned);
        ownerRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Score & CodeNum updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Working", "Score & CodeNum not updated" + e.toString());
                    }
                });
    }

    public interface CheckDuplicateCallback {
        void onCheckDuplicateComplete(Boolean duplicated);
    }

    /**
     * This returns duplication check of newly scanned QrCode
     * @param qrRef
     *      document reference of code just scanned
     * @param callback
     *      interface deal with asynchronous problem when check duplicated
     */
    public void checkDuplicateCodeScanned(DocumentReference qrRef, CheckDuplicateCallback callback) {
        // Get query of players scan newly scanned code
        Query query = scanned.whereEqualTo("Player",ownerRef)
                .whereEqualTo("qrCodeScanned",qrRef)
                .limit(1) ;
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    codeDuplicated = true;
                    Log.d("Working", "Document exists!");
                }
            } else {
                Log.e("Working", "Failed with: ", task.getException());
            }
            callback.onCheckDuplicateComplete(codeDuplicated);
        });
    }


    public void deleteQRCode(QrCode code) {
        // We need to access the QrCode list to see how it works first, cause it is real time update
    }




    /**
     * This set privacy for owner's info (email and phone number) on their user profile
     * @param visibility
     *      true indicates shows info, false will hide info
     */
//    public void setPrivacy(Boolean visibility) {
//        this.profileInfo.privacy = visibility;
//    }


    /**
     * This method updates the ranks of the players in the database based on their score
     */
    public void updateRank() {
        player.orderBy("score", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int rank = 1;
                int nextScore = 0;
                for(QueryDocumentSnapshot document: queryDocumentSnapshots){
                    int score = document.getLong("score").intValue();
                    document.getReference().update("rank", rank);

                    QueryDocumentSnapshot nextDocument = (QueryDocumentSnapshot) queryDocumentSnapshots.getDocuments().get(rank);
                    nextScore = nextDocument.getLong("score").intValue();
                    if (nextScore == 0) {
                        rank = 0;
                    } else if (nextScore < score && nextDocument != null){
                        rank++;
                    }
                }
            }
        });
    }
    /**
     * This get current highest score of player and compare to new code's score
     * @param newCode
     *      QrCode object that just scanned
     */
    public void updateHighestCode(QrCode newCode) {
        findHighestCode(new FindHighestCallback() {
            @Override
            public void onFindHighestComplete(int highestCode) {
                if (highestCode < newCode.getScore()) {
                    updateNewHighestCodePoint(newCode.getScore());
                }
            }
        });
    }


    public interface FindHighestCallback {
        void onFindHighestComplete(int highestCode);
    }

    /**
     * This get the highest score code of current player (owner)
     * @param
     *      callback start until get data finishes
     */
    public void findHighestCode(FindHighestCallback callback) {
        ownerRef.get().addOnCompleteListener(task -> {
            int highestCode = 0;
            if (task.isSuccessful()) {
                DocumentSnapshot result = task.getResult();
                highestCode = result.getLong("highestCode").intValue();
                Log.d("Working", "Code highest exists!");
            } else {
                Log.e("Working", "Failed with: ", task.getException());
            }
            callback.onFindHighestComplete(highestCode);
        });
    }

    /**
     * Update new highest score code for current player
     * @param
     *      newHighestScore new highest score to be update in database
     */
    public void updateNewHighestCodePoint (int newHighestScore) {
        Map<String, Object> data = new HashMap<>();
        data.put("highestCode",newHighestScore);
        ownerRef.update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Working", "Highest code updated successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Working", "Highest code not updated" + e.toString());
                    }
                });
    }

}
