package com.example.qr_code_hunter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference player = db.collection("Players");
    private final CollectionReference qrcode = db.collection("QrCodes");
    private final CollectionReference scanned = db.collection("scannedBy");

    private QrCode newCode;

    Button btn;
    DocumentReference justScan;

    ArrayList<DocumentReference> docRefs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Owner Elizabeth = new Owner("18927841233","elizabeth@ualberta.ca","Elizabeth", false, docRefs, 0, 0);

        try {
            newCode = new QrCode("Monkey");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        LatLng location = new LatLng(53.52346,-113.5279);
        newCode.setLocation(location);
        newCode.setPrivacy(false);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Elizabeth.checkQrCodeExist(newCode.getHashString(), new Owner.CheckExistCallback() {
                    @Override
                    public void onCheckExitedComplete(DocumentReference existQrRef) {
                        if (existQrRef != null) {
                            Elizabeth.checkDuplicateCodeScanned(existQrRef, new Owner.CheckDuplicateCallback() {
                                @Override
                                public void onCheckDuplicateComplete(Boolean duplicated) {
                                    if(!duplicated) {
                                        Toast.makeText(MainActivity.this, "Not Duplicated",Toast.LENGTH_SHORT).show();
                                        Elizabeth.addQRCode(newCode,"Context is Monkey","2A2A");
                                    } else {
                                        Toast.makeText(MainActivity.this, "Duplicated",Toast.LENGTH_SHORT).show();
                                    }  
                                }
                            });
                        } else {
                            Elizabeth.addQRCode(newCode,"Context is Monkey","2A2A");
                        }
                    }
                });
            }
        });
    }
}
