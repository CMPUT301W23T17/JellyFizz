package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PlayerProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayerProfileFragment extends Fragment {
    TextView userName;
    TextView email;
    TextView mobile_number;
    TextView rank;
    TextView score;
    TextView numberCode;
    Switch privacySwitch;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PlayerProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Profie_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayerProfileFragment newInstance(String param1, String param2) {
        PlayerProfileFragment fragment = new PlayerProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        String ownerName = loginActivity.getOwnerName();
        userName = (TextView) getView().findViewById(R.id.user_name);
        userName.setText(ownerName);
        // Access to the player collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference OwnerRef = db.collection("Players").document(ownerName);
        // Display Email
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
        // Display Mobile Phone
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            String myAttribute = documentSnapshot.getString("phoneNumber");
                            // Do something with the value
                            mobile_number = (TextView) getView().findViewById(R.id.mobile_phone);
                            mobile_number.setText("Mobile Phone: "+ myAttribute);
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
    }
}