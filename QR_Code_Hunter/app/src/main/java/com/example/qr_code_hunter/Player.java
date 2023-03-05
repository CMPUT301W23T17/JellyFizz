package com.example.qr_code_hunter;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class Player {
    private String username = "BIGJOHN";
    FirebaseFirestore db;
    final CollectionReference cr1 = db.collection(username);

    /**
     * Adds qrcode to players database collection
     * @param qr_code takes in the qrcode user wants to add
     */
    public void addQrCode(QR_Code qr_code){
        String testinput = "BFG5DGW54";
        db = FirebaseFirestore.getInstance();
        final String qrId = qr_code.shaGeneratorHexadecimal(testinput);

        HashMap<String, String> data = new HashMap<>();

        if (qrId.length() > 0) {
            data.put("QrCode Id Name", qrId);
        }

        cr1.document(testinput)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) { Log.d("Working", "Data added successfully");
                    }})
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) { Log.d("Working", "Data not added!" + e.toString());
                    }});

    }

    /**
     * Deletes Qrcode from players collection database
     * @param qr_code the qrcode that user wants to delete
     */
    public void deleteQrCode(QR_Code qr_code){
        String testinput = "BFG5DGW54";
        String data_to_delete = qr_code.shaGeneratorHexadecimal(testinput);
        db = FirebaseFirestore.getInstance();
        cr1.document(testinput).delete();
    }


}
