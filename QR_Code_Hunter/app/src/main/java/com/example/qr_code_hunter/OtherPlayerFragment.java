package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class OtherPlayerFragment extends Fragment{
    private TextView email;
    private TextView mobileNumber;
    private TextView rank;
    private TextView score;
    private TextView codeScanned;
    private String username;

    public OtherPlayerFragment(String username){
        this.username = username;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.other_player_profile, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle saveInstanceState){
        super.onViewCreated(view, saveInstanceState);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference otherPlayer = db.collection("Players").document(username);

        TextView userNameText = getView().findViewById(R.id.user_name);
        userNameText.setText(username);

        otherPlayer.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Boolean hideInfo = documentSnapshot.getBoolean("hideInfo");
                    email = (TextView) getView().findViewById(R.id.email);
                    mobileNumber = (TextView) getView().findViewById(R.id.mobile_phone);
                    ImageView imageView = getView().findViewById(R.id.rectangle_);
                    if (hideInfo == true) {
                        imageView.setVisibility(View.GONE);
                        if(Objects.equals(username, LoginActivity.getOwnerName())) {
                            email.setText("Your profile is set to private. \nYou can change your privacy from your Profile screen.");
                        } else {
                            email.setText("                     This profile is private.");
                        }
                        mobileNumber.setVisibility((View.GONE));

                    } else {
                        String myAttribute = documentSnapshot.getString("email");
                        email.setText("Email: "+myAttribute);

                        String myAttribute2 = documentSnapshot.getString("phoneNumber");
                        mobileNumber.setText("Mobile Phone: "+ myAttribute2);
                    }

                    Integer myAttribute3 = Math.toIntExact(documentSnapshot.getLong("score"));
                    score = (TextView) getView().findViewById(R.id.number_points);
                    score.setText(myAttribute3.toString());

                    Integer myAttribute4 = Math.toIntExact(documentSnapshot.getLong("rank"));
                    rank = (TextView) getView().findViewById(R.id.number_rank);
                    rank.setText(myAttribute4.toString());

                    Integer myAttribute5 = Math.toIntExact(documentSnapshot.getLong("totalCodeScanned"));
                    codeScanned = (TextView) getView().findViewById(R.id.number_code);
                    codeScanned.setText(myAttribute5.toString());
                } else {
                    Log.d(TAG, "No such document!");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@androidx.annotation.NonNull Exception e) {
                Log.e(TAG, "Error reading document", e);
            }
        });

        ImageView imageView = getView().findViewById(R.id.imageView7);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        // Set listener for more button
        TextView moreButton = view.findViewById(R.id.otherMoreButton);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new OtherCodeList(username));
                fragmentTransaction.commit();
            }
        });

        // Get Codes
        CompletableFuture<ArrayList<DocumentReference>> currentCodes = LoginActivity.getQR_Codes(username);

        currentCodes.thenAccept(qrCodes -> {
            TextView firstCodeView = getView().findViewById(R.id.otherFirstQrCodeImage);
            TextView secondCodeView = getView().findViewById(R.id.otherSecondQrCodeImage);


            if (qrCodes.size() > 0) {
                DocumentReference firstScanned = qrCodes.get(0);

                // Assuming qrCodeScanned is a DocumentReference field in a Firestore document
                firstScanned.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            String binaryString = (String) documentSnapshot.get("binaryString");
                            QrCode filler = new QrCode();

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

                            // Do something with the ID
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
