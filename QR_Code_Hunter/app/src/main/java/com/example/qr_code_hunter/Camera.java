package com.example.qr_code_hunter;

/**
 *The Camera class allows users to take pictures using their device's camera.
 *The class handles the permissions required for camera usage and saving pictures.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Camera {
    private static final String TAG = "Camera";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSIONS = 2;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private final Activity activity;
    private Uri photoUri;
    private OnPictureTakenListener onPictureTakenListener;

/**
 * The interface OnPictureTakenListener is used to notify clients when a picture has been taken.
 */
    public interface OnPictureTakenListener {
        void onPictureTaken(Uri photoUri);
    }

/**
 * Constructor for the Camera class.
 * @param activity the activity from which the Camera object is created.
 */
    public Camera(Activity activity) {
        this.activity = activity;
    }

/**
 * Method to take a picture using the device's camera.
 */
    public void takePicture() {
        if (allPermissionsGranted()) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (Exception ex) {
                    Log.e(TAG, "Error occurred while creating the File", ex);
                }
                if (photoFile != null) {
                    photoUri = Uri.fromFile(photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } else {
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_PERMISSIONS);
        }
    }

/**
 * Method to set the listener for the picture taken event.
 * @param onPictureTakenListener the listener to be set.
 */
    public void setOnPictureTakenListener(OnPictureTakenListener onPictureTakenListener) {
        this.onPictureTakenListener = onPictureTakenListener;
    }
/**
 * Method to handle the permission request result.
 * @param requestCode the request code associated with the permission request.
 * @param permissions the requested permissions.
 * @param grantResults the grant results for the requested permissions.
 */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (allPermissionsGranted()) {
                takePicture();
            } else {
                Log.e(TAG, "Permissions not granted: " + REQUIRED_PERMISSIONS.toString());
            }
        }
    }

/**
 * Method to handle the picture taken event.
 * @param requestCode the request code associated with the picture taken event.
 * @param resultCode the result code associated with the picture taken event.
 * @param data the data associated with the picture taken event.
 */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (photoUri != null) {
                if (onPictureTakenListener != null) {
                    onPictureTakenListener.onPictureTaken(photoUri);
                }
            }
        } else {
            Log.e(TAG, "Error occurred while taking photo");
        }
    }

/**
 *
 *Checks whether all required permissions have been granted by the user.
 *@return true if all required permissions have been granted, false otherwise
 */
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    
/**
 *Creates a new image file with a unique name and stores it in the external storage directory
 *for pictures.
 *@return the newly created image file
 *@throws Exception if an error occurs while creating the file
 */
    private File createImageFile() throws Exception {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
