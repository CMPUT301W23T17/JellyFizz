package com.example.qr_code_hunter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    FrameLayout map;
    GoogleMap gMap;
    double longitude, latitude;
    Marker marker;
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CHECK_SETTING = 1001;
    private LocationRequest locationRequest;
    //SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        map = findViewById(R.id.map);
        getLocation();
    }
    private void getLocation() {
        // https://developers.google.com/android/reference/com/google/android/gms/location/LocationResult
        // Author: Google developer community
        // To get the newest updated location every 5s
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMinUpdateIntervalMillis(2000)
                    .setIntervalMillis(5000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build();
            // https://www.youtube.com/watch?v=JzxjNNCYt_o&list=PLQ_Ai1O7sMV3eyA6q0spONZ2VgEj7FcF3&index=1
            // Author: Android Knowledge - https://www.youtube.com/@android_knowledge
            // Check to see if user allow to access location and nearby location info
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GoogleMapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                return;
            } else {
                if (isGPSEnabled()) {
                    // https://www.youtube.com/watch?v=mbQd6frpC3g
                    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
                    // Get current location
                    LocationServices.getFusedLocationProviderClient(GoogleMapsActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(GoogleMapsActivity.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index  = locationResult.getLocations().size() -1;
                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();
                                        Toast.makeText(getApplicationContext(), latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                                        assert supportMapFragment != null;
                                        supportMapFragment.getMapAsync(GoogleMapsActivity.this);
                                    }
                                }
                            }, Looper.getMainLooper());
                } else {
                    turnOnGPS();
                }
            }
        }
    }

    // https://www.youtube.com/watch?v=E0JiNsxD6L8
    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
    // Turn on GPS on your device
    private void turnOnGPS() {
        // https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
        // https://www.youtube.com/watch?v=E0JiNsxD6L8
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(GoogleMapsActivity.this,"GPS is ON",Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(GoogleMapsActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {

                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    // https://www.youtube.com/watch?v=E0JiNsxD6L8
    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
    // Check to see if GPS is enabled for the device
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    // https://www.youtube.com/watch?v=JzxjNNCYt_o&list=PLQ_Ai1O7sMV3eyA6q0spONZ2VgEj7FcF3&index=1
    // Author: Android Knowledge - https://www.youtube.com/@android_knowledge
    // This is used to mark your current location on map, and move map view to your current location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng latLng = new LatLng(latitude,longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("My Current Location");
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        googleMap.addMarker(markerOptions);
    }

    // https://www.youtube.com/watch?v=JzxjNNCYt_o&list=PLQ_Ai1O7sMV3eyA6q0spONZ2VgEj7FcF3&index=1
    // Author: Android Knowledge - https://www.youtube.com/@android_knowledge
    // This is used to get current location when permission to get location granted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }

    // https://www.youtube.com/watch?v=E0JiNsxD6L8
    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
    // This is used to notice on status of gps
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTING) {
            switch(resultCode) {
                case Activity.RESULT_OK:
                    Toast.makeText(GoogleMapsActivity.this,"GPS is turned on",Toast.LENGTH_SHORT).show();
                    break;

                case Activity.RESULT_CANCELED:
                    Toast.makeText(GoogleMapsActivity.this,"GPS is required to be turned on",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}