package com.example.qr_code_hunter;

import android.Manifest;
import android.content.Context;
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

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;
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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    protected FrameLayout map;
    private GoogleMap gMap;
    private double longitude, latitude;
    private Marker marker;
    private GroundOverlay overlay;
    private Bitmap bitmap;
    private LocationRequest locationRequest;
    private static final String TAG = "Info";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference qrCodes = db.collection("QrCodes");
//    Map<LatLng, MarkerTag> nearbyCodes = new HashMap<LatLng, MarkerTag>();
    ArrayList<Marker> foundMarkers = new ArrayList<>();
    AutocompleteSupportFragment autocompleteFragment;
    private boolean hasLocationPermission = false;

    // https://stackoverflow.com/questions/61455381/how-to-replace-startactivityforresult-with-activity-result-apis
    // Author: https://stackoverflow.com/users/9255057/hardik-hirpara
    // Create an ActivityResultLauncher to request location permission
    ActivityResultLauncher<String[]> requestLocationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> result) {
                    if (result.containsKey(Manifest.permission.ACCESS_FINE_LOCATION)
                            && result.get(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Permission granted, call getLocation()
                        hasLocationPermission = true;
                        getLocation();
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Request location permission if needed
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_google_maps, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        map = getView().findViewById(R.id.map);
        // Create radial gradient circle
        getCircleRadiant();
        // Get to current location on google map when open
        getLocation();

        // https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
        // Author: Google Developer team
        // Use to initialize place autocomplete in google maps
        // Initiate the SDK
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getActivity().getApplicationContext(),apiKey);
        }

        autocompleteFragment = (AutocompleteSupportFragment)getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Set location bias for result to Edmonton
        LatLngBounds edmontonBounds = new LatLngBounds(
                new LatLng(53.3343, -113.5812), // Southwest bound of Edmonton
                new LatLng(53.6758, -113.2693)  // Northeast bound of Edmonton
        );
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(edmontonBounds));

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        findLocationByAutoComplete();
    }

    /**
     * This function is used to get location from search bar and display markers on map
     */
    public void findLocationByAutoComplete() {
        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                // Focus google map on the chosen search location
                LatLng latLng = place.getLatLng();
                assert latLng != null;

                if (marker != null){marker.remove();}

                findNearbyQrCodes(latLng.latitude, latLng.longitude, new FindNearbyCodeCallback() {
                    @Override
                    public void FindNearbyCodeComplete(Map<LatLng, MarkerTag> nearbyCodes) {
                        // Remove markers from last search if applicable
                        for (Marker point: foundMarkers) {
                            point.remove();
                        }
                        // Draw nearby codes
                        nearbyCodes.forEach((key, value) -> {
                            MarkerOptions markerOptions = new MarkerOptions().position(key);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            Marker newMarker = gMap.addMarker(markerOptions);
                            assert newMarker != null;
                            newMarker.setTag(value);
                            foundMarkers.add(newMarker);
                        });
                        // Draw current location
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(place.getName());
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                        gMap.animateCamera(cameraUpdate);
                        marker = gMap.addMarker(markerOptions);
                        //Show radius of nearby QR codes
                        float radius = 500; // Replace with the radius of the circle in meters
                        overlay.setPosition(place.getLatLng());
                        overlay.setDimensions(radius * 2, radius * 2);
                    }
                });
            }
            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    /**
     * This function is to create a radial gradient with radius of 500m (in ratio of maps)
     */
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

    /**
     * This function is used to get current location from phone's GPS
     */
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
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestLocationPermissionLauncher.launch(new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
            } else {
                if (isGPSEnabled()) {
                    // https://www.youtube.com/watch?v=mbQd6frpC3g
                    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
                    // Get current location
                    LocationServices.getFusedLocationProviderClient(getActivity())
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(getActivity())
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int index  = locationResult.getLocations().size() -1;
                                        hasLocationPermission = true;
                                        latitude = locationResult.getLocations().get(index).getLatitude();
                                        longitude = locationResult.getLocations().get(index).getLongitude();
                                        Toast.makeText(getActivity().getApplicationContext(), latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                                        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                                        assert supportMapFragment != null;
                                        supportMapFragment.getMapAsync(MapFragment.this);
                                    }
                                }
                            }, Looper.getMainLooper());
                } else {
                    turnOnGPS();
                }
            }
        }
    }

    /**
     * This function is used to turn on GPS
     */
    // https://www.youtube.com/watch?v=E0JiNsxD6L8
    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
    // Turn on GPS on your device
    private void turnOnGPS() {
        // https://developers.google.com/android/reference/com/google/android/gms/location/LocationRequest
        // https://www.youtube.com/watch?v=E0JiNsxD6L8
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getContext().getApplicationContext())
                .checkLocationSettings(builder.build());


        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(getActivity(),"GPS is ON",Toast.LENGTH_SHORT).show();
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(getActivity(), 2);
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

    /**
     * This function is used to check whether GPS is on
     */
    // https://www.youtube.com/watch?v=E0JiNsxD6L8
    // Author: Technical Coding - https://www.youtube.com/@TechnicalCoding
    // Check to see if GPS is enabled for the device
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    // https://www.youtube.com/watch?v=JzxjNNCYt_o&list=PLQ_Ai1O7sMV3eyA6q0spONZ2VgEj7FcF3&index=1
    // Author: Android Knowledge - https://www.youtube.com/@android_knowledge
    // This is used to mark your current location on map, and move map view to your current location
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (hasLocationPermission) {
            this.gMap = googleMap;
            LatLng latLng = new LatLng(latitude, longitude);
            findNearbyQrCodes(latitude, longitude, new FindNearbyCodeCallback() {
                @Override
                public void FindNearbyCodeComplete(Map<LatLng, MarkerTag> nearbyCodes) {
                    // Draw nearby codes
                    nearbyCodes.forEach((key, value) -> {
                        MarkerOptions markerOptions = new MarkerOptions().position(key);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        Marker newMarker = gMap.addMarker(markerOptions);
                        assert newMarker != null;
                        newMarker.setTag(value);
                        foundMarkers.add(newMarker);
                    });
                    // Draw current location
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(@NonNull Marker marker1) {
                            if (marker1 != marker) {
                                solveOnClick(marker1);
                            }
                            return true;
                        }
                    });
                }
            });
        }
    }

    public interface FindNearbyCodeCallback {
        void FindNearbyCodeComplete(Map<LatLng, MarkerTag> nearbyCodes);
    }

    /**
     * This function is used to find all nearby QrCode within 500m of the current location
     * @param latitude store latitude of current location
     * @param longitude store longitude of current location
     * @param callback called until finish fetching data
     */
    public void findNearbyQrCodes(double latitude, double longitude, FindNearbyCodeCallback callback) {
        final GeoLocation center = new GeoLocation(latitude, longitude);
        final double radiusInM = 500;
        Map<LatLng, MarkerTag> nearbyCodes = new HashMap<LatLng, MarkerTag>();
        qrCodes.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Only show codes whose location is recorded
                            if (document.getDouble("latitude") != null && document.getDouble("longitude") != null) {
                                double lat = document.getDouble("latitude");
                                double lng = document.getDouble("longitude");
                                GeoLocation docLocation = new GeoLocation(lat, lng);
                                // https://firebase.google.com/docs/firestore/solutions/geoqueries#query_geohashes
                                // Author: Firebase.google.com
                                // I use the GeoFireUtils.getDistanceBetween(a,b) to find distance between two location on map
                                double distanceInM = GeoFireUtils.getDistanceBetween(docLocation, center);
                                // Only accept codes that is 500m within the current location
                                if (distanceInM <= radiusInM) {
                                    LatLng validMarker = new LatLng(lat, lng);
                                    // Round to 3 decimal points
                                    lat = Math.round(lat * 1000.0) / 1000.0;
                                    lng = Math.round(lng * 1000.0) / 1000.0;
                                    // Create new markerTag
                                    MarkerTag tag;
                                    if (nearbyCodes.containsKey(validMarker)) {
                                        // if there already other QrCodes at that location
                                        tag = nearbyCodes.get(validMarker);
                                    } else {
                                        tag = new MarkerTag();
                                    }
                                    // Create new NearbyCode object and add it to the MarkerTag
                                    NearbyCode code = new NearbyCode(document.getString("codeName"), (int) distanceInM + "m away",
                                            document.getLong("Score").intValue() + "pts", "(" + lat + ", " + lng + ")");
                                    assert tag != null;
                                    tag.addTag(code);
                                    // Add to hash map
                                    nearbyCodes.put(validMarker, tag);
                                }
                            }
                        }
                        callback.FindNearbyCodeComplete(nearbyCodes);
                    }
                });
    }

    /**
     * This function is used to display codes information on click
     * @param marker marker clicked on map
     */
    // This function is used to show bottom dialog when user click markers
    public void solveOnClick(Marker marker) {
        MarkerTag newTag = new MarkerTag();
        newTag = (MarkerTag) marker.getTag();
        // Create a new instance of the BottomSheetDialog
        if (newTag != null) {
            BottomSheetDialog bottomSheetDialog = BottomSheetDialog.newInstance(newTag.getNearbyCodes());
            bottomSheetDialog.show(getChildFragmentManager(), bottomSheetDialog.getTag());
        }
    }
}
