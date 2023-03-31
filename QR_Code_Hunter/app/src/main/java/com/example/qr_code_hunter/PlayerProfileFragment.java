package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PlayerProfileFragment extends Fragment {
    private TextView email;
    private TextView mobileNumber;
    private TextView rank;
    private TextView score;
    private TextView numberCode;
    private Switch privacySwitch;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_player_profile, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get the owner name
        String ownerName = LoginActivity.getOwnerName();
        TextView userName = (TextView) getView().findViewById(R.id.user_name);
        userName.setText(ownerName);
        // Access to the player collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference OwnerRef = db.collection("Players").document(ownerName);


        //Set listener for more button
        TextView moreButton = view.findViewById(R.id.more_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new QrCodeList());
                fragmentTransaction.commit();
            }
        });

        // Display Email by accessing to firebase
        // Link: https://chat.openai.com/
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            String myAttribute = documentSnapshot.getString("email");
                            // Do something with the value
                            email = (TextView) getView().findViewById(R.id.email);
                            email.setText("Email: "+myAttribute);
                            Log.d(TAG, "Value of myAttribute: " + myAttribute);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error reading document", e);
                    }
                });
        // Display Mobile Phone by accessing to firebase
        // Link: https://chat.openai.com/
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            String myAttribute = documentSnapshot.getString("phoneNumber");
                            // Do something with the value
                            mobileNumber = (TextView) getView().findViewById(R.id.mobile_phone);
                            mobileNumber.setText("Mobile Phone: "+ myAttribute);
                            Log.d(TAG, "Value of myAttribute: " + myAttribute);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error reading document", e);
                    }
                });
        // Display score by accessing to firebase
        // Link: https://chat.openai.com/
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("score"));
                            // Do something with the value
                            score = (TextView) getView().findViewById(R.id.number_points);
                            score.setText(myAttribute.toString());

                            Log.d(TAG, "Value of myAttribute: " + myAttribute);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error reading document", e);
                    }
                });
        // Display Rank by accessing to firebase
        // Link: https://chat.openai.com/
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("rank"));
                            // Do something with the value
                            rank = (TextView) getView().findViewById(R.id.number_rank);
                            rank.setText(myAttribute.toString());
                            Log.d(TAG, "Value of myAttribute: " + myAttribute);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error reading document", e);
                    }
                });
        // Display total score by accessing to firebase
        // Link: https://chat.openai.com/
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("totalCodeScanned"));
                            // Do something with the value
                            numberCode = (TextView) getView().findViewById(R.id.number_code);
                            numberCode.setText(myAttribute.toString());
                            Log.d(TAG, "Value of myAttribute: " + myAttribute);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error reading document", e);
                    }
                });
        // Update privacy profile information of owner in database
        // Link: https://chat.openai.com/
        privacySwitch = getView().findViewById(R.id.switch_privacy);
        privacySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Handle the switch state change
                boolean isOn = isChecked;
                // Do something with the boolean value, such as updating a database or UI element
                OwnerRef.update("hideInfo", isOn);
            }
        });
        // Display Privacy by accessing to firebase
        // Link: https://chat.openai.com/
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            Boolean myAttribute = documentSnapshot.getBoolean("hideInfo");
                            // Do something with the value
                            privacySwitch.setChecked(myAttribute);
                        } else {
                            Log.d(TAG, "No such document!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error reading document", e);
                    }
                });

        //Get Codes
        CompletableFuture<ArrayList<DocumentReference>> currentCodes = LoginActivity.getQrCodes(LoginActivity.getOwnerName());

        currentCodes.thenAccept(qrCodes -> {
            TextView firstCodeView = getView().findViewById(R.id.firstQrCodeImage);
            TextView secondCodeView = getView().findViewById(R.id.secondQrCodeImage);

            QrCode filler = new QrCode();


            if (qrCodes.size() > 0) {
                DocumentReference firstScanned = qrCodes.get(0);

                // assuming qrCodeScanned is a DocumentReference field in a Firestore document
                firstScanned.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            String binaryString = (String) documentSnapshot.get("binaryString");
                            QrCode filler = new QrCode();

                            // do something with the ID
                            QrCodeTag firstTag = new QrCodeTag(documentSnapshot.getId(), 0, 0);

                            firstCodeView.setTag(firstTag);
                            firstCodeView.setText(filler.getVisualRep(binaryString));
                        }
                    }
                });

            }

            if (qrCodes.size() > 1) {
                DocumentReference secondScanned = qrCodes.get(1);
                secondScanned.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String binaryString = (String) documentSnapshot.get("binaryString");
                            QrCode filler = new QrCode();

                            QrCodeTag secondTag = new QrCodeTag(documentSnapshot.getId(), 0, 0);

                            secondCodeView.setTag(secondTag);
                            secondCodeView.setText(filler.getVisualRep(binaryString));
                        }
                    }
                });
            }

        });
    }
}