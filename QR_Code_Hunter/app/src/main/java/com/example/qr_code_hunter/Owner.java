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
    private String image;
    private QrCode newCode;
    private QrCode newCode2;

    Button btn;
    DocumentReference justScan;

    ArrayList<DocumentReference> docRefs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Owner Quin = new Owner("QuinNguyen","elizabeth@ualberta.ca","KyleQuach", false, docRefs, 0, 0,0);
        Owner Elizabeth = new Owner("Elizabeth","elizabeth@ualberta.ca","Elizabeth", false, docRefs, 30, 1,1);

        try {
            newCode = new QrCode("Surprise");
            newCode2 = new QrCode("Craziness");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        LatLng location = new LatLng(53.542061,-113.489435);
        LatLng location2 = new LatLng(53.542482, -113.491763);

        newCode.setLocation(location);
        newCode2.setLocation(location2);

        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Quin.checkQrCodeExist(newCode2.getHashString(), new Owner.CheckExistCallback() {
                    @Override
                    public void onCheckExitedComplete(DocumentReference existQrRef) {
                        if (existQrRef != null) {
                            Toast.makeText(MainActivity.this, "existed",Toast.LENGTH_SHORT).show();
                            Quin.checkDuplicateCodeScanned(existQrRef, new Owner.CheckDuplicateCallback() {
                                @Override
                                public void onCheckDuplicateComplete(Boolean duplicated) {
                                    if(!duplicated) {
                                        Toast.makeText(MainActivity.this, "Not Duplicated",Toast.LENGTH_SHORT).show();
                                        Quin.addQRCode(newCode2, false,"Kyle add","hihi");
                                    } else {
                                        Toast.makeText(MainActivity.this, "Duplicated",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(MainActivity.this, "Not existed",Toast.LENGTH_SHORT).show();
                            Quin.addQRCode(newCode2, false,"Kyle add","hihi");
                        }
                    }
                });
            }
        });
    }
}
