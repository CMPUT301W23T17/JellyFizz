package com.example.qr_code_hunter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This is the second page of the new code details (editable fields)
 */
public class NewCodeActivity2 extends AppCompatActivity {
    ImageView picture;
    EditText descBox;
    TextView charCount;
    CheckBox saveGeo;
    CheckBox recordCode;
    Button saveBtn;
    QrCode newCode;
    String encodedImage;
    CompletableFuture<Owner> currentOwnerDone = null;
    Owner currentOwner;

    DocumentReference justScan;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_code_2);
        picture = findViewById(R.id.picture_details);
        descBox = findViewById(R.id.comment_box);
        charCount = findViewById(R.id.char_count_label);
        saveGeo = findViewById(R.id.save_geolocation_button);
        recordCode = findViewById(R.id.record_code_button);
        saveBtn = findViewById(R.id.save_button);
        newCode = getIntent().getParcelableExtra("New QrCode");
        //currentOwner = getIntent().getParcelableExtra("Current Owner");

        // Set the owner object, still need to discuss what is happening with this list of qrcodes
        currentOwnerDone = loginActivity.getOwnerFuture(loginActivity.getOwnerName());

        try {
            currentOwner = currentOwnerDone.get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.launch(openCam);
            }
        });

        descBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * This increments the character count everytime the user types
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String countLabel = String.valueOf(s.length()) + "/150";
                charCount.setText(countLabel);

                if(s.length() == 150) {
                    charCount.setTextColor(Color.RED);
                } else {
                    charCount.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveGeo.isChecked()) {
                    newCode.setLocation(getIntent().getParcelableExtra("Coordinates"));
                }
                if(recordCode.isChecked()) {
                    newCode.setPrivacy(false); // actual code is saved
                } else {
                    newCode.setPrivacy(true);
                }

                currentOwner.checkQrCodeExist(newCode.getHashString(), new Owner.CheckExistCallback() {
                    @Override
                    public void onCheckExitedComplete(DocumentReference existQrRef) {
                        if (existQrRef != null) {
                            currentOwner.checkDuplicateCodeScanned(existQrRef, new Owner.CheckDuplicateCallback() {
                                @Override
                                public void onCheckDuplicateComplete(Boolean duplicated) {
                                    if(!duplicated) {
                                        Toast.makeText(NewCodeActivity2.this, "Saving to database...",Toast.LENGTH_SHORT).show();
                                        if((int) charCount.getText().toString().charAt(0) > 0) {
                                            currentOwner.addQRCode(newCode, descBox.getText().toString(), encodedImage);
                                        } else {
                                            currentOwner.addQRCode(newCode, null, encodedImage);
                                        }

                                    } else {
                                        Toast.makeText(NewCodeActivity2.this, "You've scanned this code before!",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            //currentOwner.addQRCode(newCode, descBox.getText().toString(),encodedImage);
                            if((int) charCount.getText().toString().charAt(0) > 0) {
                                currentOwner.addQRCode(newCode, descBox.getText().toString(),encodedImage);
                            } else {
                                currentOwner.addQRCode(newCode, null, encodedImage);
                            }
                        }
                        Intent intent = new Intent(NewCodeActivity2.this, MainActivity.class);
                        (new Handler()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(intent);
                            }
                        }, 2000);
                    }
                });

            }
        });
    }

    /**
     * This opens the camera app and set the taken photograph to an ImageView
     * and encode image to string to be sent to another activity
     */
    ActivityResultLauncher<Intent> takePhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        Bitmap finalPhoto = (Bitmap) bundle.get("data");
                        picture.setImageBitmap(finalPhoto);

                        // Encode image to string
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        finalPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        encodedImage = Base64.getEncoder().encodeToString(byteArray);
                    }
                }
            });


    /**
     * Retrieves all the QR code references for a specific player from the database and returns them
     * as an ArrayList. This method queries the "Players" collection to find all documents that reference
     * the player's document and retrieves the QR code references from those documents.
     *
     * @param currentPlayer The ID of the player for whom to retrieve the QR code references.
     * @return An ArrayList containing the DocumentReference objects for all the QR codes scanned by the player.
     */
    private static CompletableFuture<ArrayList<DocumentReference>> getQR_Codes(String currentPlayer) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scannedBy = db.collection("scannedBy");

        // Get owner of account reference
        DocumentReference playerReference = db.collection("Players").document(currentPlayer);

        CompletableFuture<ArrayList<DocumentReference>> returnCode = new CompletableFuture<>();

        ArrayList<DocumentReference> qrCodeRefs = new ArrayList<>();


        // Query the scannedBy collection to get all documents that reference the player's document
        Query query = scannedBy.whereEqualTo("Player", playerReference);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (querySnapshot.isEmpty()) {
                    Log.d("GetTest", "No documents found for player: " + playerReference);
                    return;
                }

                // Iterate over the query results and add the QR code references to the ArrayList
                for (QueryDocumentSnapshot document : querySnapshot) {
                    DocumentReference docRef = document.getReference();
                    qrCodeRefs.add(docRef);
                }

                returnCode.complete(qrCodeRefs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Display error
                Log.d("Database error", "Error getting all qrcodes for a player", e);
            }
        });

        return returnCode;
    }


    /**
     * Retrieves the owner details for the given input owner from the Firestore database
     * and returns a CompletableFuture that completes with the Owner object.
     *
     * @param inputOwner The owner ID for which to fetch the details.
     * @return A CompletableFuture that completes with the Owner object once the data is fetched from the database.
     * @throws NullPointerException if inputOwner is null.
     */
    private static CompletableFuture<Owner> getOwnerFuture(String inputOwner) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference playersRef = db.collection("Players");

        CompletableFuture<Owner> retOwner = new CompletableFuture<>();


        playersRef.document(inputOwner).get()
                .addOnSuccessListener(document -> {
                    Log.d("Future", "ran");


                    if (document.exists()) {
                        String email = document.getString("email");
                        boolean hideInfo = document.getBoolean("hideInfo");
                        String phoneNumber = document.getString("phoneNumber");
                        int rank = document.getLong("rank").intValue();
                        int score = document.getLong("score").intValue();
                        int totalCodeScanned = document.getLong("totalCodeScanned").intValue();

                        CompletableFuture<ArrayList<DocumentReference>> qrCodeFuture = getQR_Codes(inputOwner);
                        qrCodeFuture.thenAccept(qrCodeRefs -> {
                            // Create the Owner object and set its properties
                            Owner filler = new Owner(phoneNumber, email, inputOwner,
                                    false, qrCodeRefs, score, rank, totalCodeScanned);
                            retOwner.complete(filler);
                        });

                    } else {
                        Log.d("Database Program Logic Error", "This player does not exist in database");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Database error", "Could not fetch data from database", e);
                });

        Log.d("Future", "ran");
        return retOwner;
    }
}