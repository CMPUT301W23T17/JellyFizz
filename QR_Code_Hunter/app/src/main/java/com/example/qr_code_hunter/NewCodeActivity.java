package com.example.qr_code_hunter;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

/**
 * This is the first page of the new code details (non-editable fields)
 */
public class NewCodeActivity extends AppCompatActivity {
    private QrCode newCode;
    private List<Address> myAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_code);
        TextView codeName = findViewById(R.id.code_name);
        TextView visualRep = findViewById(R.id.visual_rep);
        TextView codeLoc = findViewById(R.id.code_location);
        TextView codePts = findViewById(R.id.points_label);
        ImageButton nextPageBtn = findViewById(R.id.next_button);

        String scannedString = getIntent().getExtras().getString("scanned string");
        Location curLoc = getIntent().getParcelableExtra("current location");

        // Getting LatLng from Location
        // URL          : https://www.youtube.com/watch?v=ByjxO66Y_1I
        // Author       : Knowledge Extension
        // Date         : April 23, 2020
        // Timestamp    : 31:30 - 33:58
        Geocoder geocoder = new Geocoder(NewCodeActivity.this, Locale.getDefault());
        LatLng latLng = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
        try {
            myAddress = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = myAddress.get(0).getAddressLine(0);
        codeLoc.setText(address);

        try {
            newCode = new QrCode(scannedString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        codeName.setText(newCode.getName());
        visualRep.setText(newCode.getVisualRep(newCode.getBinaryString()));

        String scoreLabel = "You earned " + String.valueOf(newCode.getScore()) + " points!";
        codePts.setText(scoreLabel);

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewCodeActivity.this, NewCodeActivity2.class);
                intent.putExtra("New QrCode", newCode);
                intent.putExtra("Coordinates", latLng);
                startActivity(intent);
            }
        });
    }
}