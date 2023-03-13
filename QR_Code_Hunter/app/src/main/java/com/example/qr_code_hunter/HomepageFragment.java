package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {
    ImageButton instruction_button;
    AlertDialog.Builder builder;
    View scanButton;
    TextView welcomeOwner;
    TextView rank;
    TextView score;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home_screen.
     */
    // TODO: Rename and change types and number of parameters
    public static HomepageFragment newInstance(String param1, String param2) {
        HomepageFragment fragment = new HomepageFragment();
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
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        instruction_button = (ImageButton) getView().findViewById(R.id.ask_button);
        builder = new AlertDialog.Builder(getActivity());

        String ownerName = loginActivity.getOwner();
        welcomeOwner = (TextView) getView().findViewById(R.id.welcome_user);
        welcomeOwner.setText("WELCOME "+ ownerName);

        instruction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        scanButton = getView().findViewById(R.id.scan_button);
        scanButton.setOnClickListener(v -> {
            scanCode();
        });

        // Access to the player collection
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference OwnerRef = db.collection("Players").document(ownerName);
        // Display Rank
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("rank"));
                            // Do something with the value
                            rank = (TextView) getView().findViewById(R.id.my_ranking_number);
                            rank.setText("Your Rank: #"+myAttribute);
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
        // Display Score
        OwnerRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Get the value of the specific attribute
                            Integer myAttribute = Math.toIntExact(documentSnapshot.getLong("score"));
                            // Do something with the value
                            score = (TextView) getView().findViewById(R.id.score_display);
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
    }

    private void openDialog() {
        Instruction_Dialog instruction_dialog = new Instruction_Dialog();
        instruction_dialog.show(getParentFragmentManager(),"dede");
    }
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            String inputString = result.getContents();
            Intent intent = new Intent(getActivity(), NewCodeActivity.class);
            intent.putExtra("scanned string", inputString);
            startActivity(intent);
        }
    });
}