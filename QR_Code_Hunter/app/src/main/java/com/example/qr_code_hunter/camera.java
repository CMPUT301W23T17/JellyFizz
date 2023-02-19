package com.example.qr_code_hunter;

public class Camera {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 201;
    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private Activity mActivity;
    private CameraView mCameraView;
    private boolean mHasCameraPermission;
    private boolean mHasWriteExternalStoragePermission;

    public Camera(Activity activity, CameraView cameraView) {
        mActivity = activity;
        mCameraView = cameraView;
    }

    public void checkPermissions() {
        mHasCameraPermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        mHasWriteExternalStoragePermission = ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (!mHasCameraPermission || !mHasWriteExternalStoragePermission) {
            ActivityCompat.requestPermissions(mActivity, PERMISSIONS, REQUEST_CAMERA_PERMISSION);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mHasCameraPermission = true;
                }
                if (grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mHasWriteExternalStoragePermission = true;
                }
                if (mHasCameraPermission && mHasWriteExternalStoragePermission) {
                    startCamera();
                } else {
                    Toast.makeText(mActivity, "Camera and storage permission required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void startCamera() {
        mCameraView.setLifecycleOwner(mActivity);
        mCameraView.setCaptureMode(CameraView.CaptureMode.IMAGE);
        mCameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(@NonNull PictureResult result) {
                super.onPictureTaken(result);
                savePicture(result);
            }
        });
    }

    public void takePicture() {
        if (mHasCameraPermission && mHasWriteExternalStoragePermission) {
            mCameraView.takePicture();
        } else {
            Toast.makeText(mActivity, "Camera and storage permission required", Toast.LENGTH_SHORT).show();
        }
    }

    private void savePicture(PictureResult result) {
        if (!mHasWriteExternalStoragePermission) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        } else {
            File file = new File(mActivity.getExternalFilesDir(null), "picture.jpg");
            result.toFile(file, ignored -> {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                mediaScanIntent.setData(Uri.fromFile(file));
                mActivity.sendBroadcast(mediaScanIntent);
                Toast.makeText(mActivity, "Picture saved to " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            });
        }
    }
}
