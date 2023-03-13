package com.example.qr_code_hunter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class GoogleMapsActivity extends FragmentActivity implements OnMapReadyCallback {
    FrameLayout map;
    GoogleMap gMap;
    double longitude, latitude;
    Marker marker;
    GroundOverlay overlay;
    private Bitmap bitmap;
    private static final int REQUEST_CODE = 101;
    private static final int REQUEST_CHECK_SETTING = 1001;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 110;
    private LocationRequest locationRequest;
    private static String TAG = "Info";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
        map = findViewById(R.id.map);
        // Create radial gradient circle
        getCircleRadiant();
        // Get to current location on google map when open
        getLocation();

        // Initiate the SDK
        String apiKey = getString(R.string.api_key);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(),apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Set location bias for result to Edmonton only
        LatLngBounds edmontonBounds = new LatLngBounds(
                new LatLng(53.3343, -113.5812), // Southwest bound of Edmonton
                new LatLng(53.6758, -113.2693)  // Northeast bound of Edmonton
        );

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(edmontonBounds));

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

                // Focus google map on the chosen search location
                LatLng latLng = place.getLatLng();
                if (marker != null){
                    marker.remove();
                }
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(place.getName());
//                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                gMap.animateCamera(cameraUpdate);
                marker = gMap.addMarker(markerOptions);

                //Show radius of nearby QR codes
                float radius = 500; // Replace with the radius of the circle in meters
                overlay.setPosition(place.getLatLng());
                overlay.setDimensions(radius * 2, radius * 2);
            }


            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    // https://developer.android.com/reference/android/graphics/RadialGradient
    // Android Developer Community
    // Use to get a pain of a radial gradiant
    private void getCircleRadiant() {
        int radius = 500;
        // Set color and position
        int color1 = 0xDDFF6B6B;
        int color2 = 0xAAFFA8A8;
        int color3 = 0x99FFE3E3;
        // Define the gradient stops as percentages of the radius
        float[] gradientStops = { 0.2f, 0.5f, 0.9f };
        // Define the colors for each gradient stop
        int[] gradientColors = { color1, color2, color3 };
        // Create a RadialGradient object
        RadialGradient gradient = new RadialGradient(500, 500, radius, gradientColors, gradientStops, Shader.TileMode.CLAMP);
        // Create a Paint object with the RadialGradient
        Paint paint = new Paint();
        paint.setShader(gradient);
        // Create a Bitmap object and a Canvas object
        bitmap = Bitmap.createBitmap((int) (2 * radius), (int) (2 * radius), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // Draw a circle with the Paint object and the Canvas object
        canvas.drawCircle(radius, radius, radius, paint);
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
        marker = googleMap.addMarker(markerOptions);
        try {
            // Convert the Bitmap object to a BitmapDescriptor object
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

            // Add a GroundOverlay to the map with the BitmapDescriptor object
            GroundOverlayOptions options = new GroundOverlayOptions()
                    .image(descriptor)
                    .position(latLng, 2 * 500)
                    .transparency(0.3f)
                    .clickable(false);
            overlay = gMap.addGroundOverlay(options);
        } catch (Exception e){
            e.printStackTrace();}
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
        // https://developers.google.com/maps/documentation/places/android-sdk/autocomplete-->
        // Author: Android Developer Community-->
        // Use CardView to create border for search fragment
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
                Toast.makeText(GoogleMapsActivity.this, "No location found",Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }
}