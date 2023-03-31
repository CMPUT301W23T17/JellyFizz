package com.example.qr_code_hunter;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomepageFragment extends Fragment {
    protected AlertDialog.Builder builder;
    private FusedLocationProviderClient client;
    private Location currentLocation;
    private TextView rank;
    private TextView score;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Initialize view
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        // Assign variable
        View scanButton = view.findViewById(R.id.scan_button);
        // Initialize location client
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Getting current user location
                // URL          : https://www.youtube.com/watch?v=VdCQoJtNXAg
                // Author       : Android Coding
                // Date         : November 1, 2020
                // Timestamp    : 5:42 - 14:03
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // When permission is granted, call method
                    setCurrentLocation();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }
            }

        });
        // Return view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setTag("HomepageFragment");
        ImageButton instruction_button = (ImageButton) getView().findViewById(R.id.ask_button);
        builder = new AlertDialog.Builder(getActivity());

        // Display user name
        String ownerName = LoginActivity.getOwnerName();
        TextView welcomeOwner = (TextView) getView().findViewById(R.id.welcome_user);
        welcomeOwner.setText("HELLO, "+ ownerName+ " !");

        instruction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
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

    /**
     * This opens the instruction dialog
     */
    private void openDialog() {
        InstructionDialog instruction_dialog = new InstructionDialog();
        instruction_dialog.show(getParentFragmentManager(),"dede");
    }

    /**
     * This gets the user current location when they scan a QrCode
     */
    @SuppressLint("MissingPermission")
    private void setCurrentLocation() {
        // Initialize location manager
        LocationManager locationManager = (LocationManager) getActivity()
                .getSystemService(Context.LOCATION_SERVICE);
        // Check if location services is turned on
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            // When location service is enabled, get last location
            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    // Initialize location
                    Location location = task.getResult();
                    // Check condition
                    if (location != null) {
                        // When location result is not null
                        currentLocation = location;
                        scanCode();
                    }
                }
            });
        } else {
            // When location service is not enabled, open location setting
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    /**
     * This opens the QR Scanner screen using a built-in library
     */
    private void scanCode() {
        // Scanner Implementation
        // URL      : https://www.youtube.com/watch?v=jtT60yFPelI
        // Author   : Cambo Tutorial
        // Date     : March 18, 2022
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * This scans a QrCode object and sends data to an activity
     */
    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            String inputString = result.getContents();
            Intent intent = new Intent(getActivity(), NewCodeActivity.class);
            intent.putExtra("scanned string", inputString);
            intent.putExtra("current location", currentLocation);
            startActivity(intent);
        }
    });
}