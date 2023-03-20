package com.example.qr_code_hunter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * This is the second page of the new code details (editable fields)
 */
public class NewCodeActivity2 extends AppCompatActivity {
    ImageView picture;
    EditText descBox;
    TextView charCount;
    CheckBox saveGeo;
    Button saveBtn;
    QrCode newCode;
    String encodedImage;
    Owner currentOwner;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {





        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_code_2);
        picture = findViewById(R.id.picture_details);
        descBox = findViewById(R.id.comment_box);
        charCount = findViewById(R.id.char_count_label);
        saveGeo = findViewById(R.id.save_geolocation_button);
        saveBtn = findViewById(R.id.save_button);
        saveBtn.setEnabled(false);
        newCode = getIntent().getParcelableExtra("New QrCode");


        //set the owner object, still need to discuss what is happening with this list of qrcodes
        loginActivity.setCurrentOwnerObject(loginActivity.getOwnerName(), new loginActivity.getAllInfo() {
            @Override
            public void onGetInfo(Owner owner) {
                currentOwner = owner;
                saveBtn.setEnabled(true);
            }
        });


        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.launch(openCam);
            }
        });

        descBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            /**
             * This increments the character count everytime the user types
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String countLabel = String.valueOf(s.length()) + "/150";
                charCount.setText(countLabel);

                if(s.length() == 150) {
                    charCount.setTextColor(Color.RED);
                } else {
                    charCount.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtn.setEnabled(false);

                if(saveGeo.isChecked()) {
                    newCode.setLocation(getIntent().getParcelableExtra("Coordinates"));
                }

                currentOwner.checkQrCodeExist(newCode.getHashString(), new Owner.CheckExistCallback() {
                    @Override
                    public void onCheckExitedComplete(DocumentReference existQrRef) {
                        if (existQrRef != null) {
                            currentOwner.checkDuplicateCodeScanned(existQrRef, new Owner.CheckDuplicateCallback() {
                                @Override
                                public void onCheckDuplicateComplete(Boolean duplicated) {
                                    if(!duplicated) {
                                        Toast.makeText(NewCodeActivity2.this, "Add new code successfully",Toast.LENGTH_SHORT).show();
                                        currentOwner.addQRCode(newCode, descBox.getText().toString(),encodedImage);
                                    } else {
                                        Toast.makeText(NewCodeActivity2.this, "You've scanned this code before!",Toast.LENGTH_LONG).show();
                                    }

                                    saveBtn.setEnabled(true);
                                    Intent intent = new Intent(NewCodeActivity2.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Toast.makeText(NewCodeActivity2.this, "Add new code successfully",Toast.LENGTH_SHORT).show();
                            currentOwner.addQRCode(newCode, descBox.getText().toString(),encodedImage);
                            saveBtn.setEnabled(true);
                            Intent intent = new Intent(NewCodeActivity2.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }


    /**
     * This opens the camera app and set the taken photograph to an ImageView
     * and encode image to string to be sent to another activity
     */
    ActivityResultLauncher<Intent> takePhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        Bitmap finalPhoto = (Bitmap) bundle.get("data");
                        picture.setImageBitmap(finalPhoto);

                        // Encode image to string
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        finalPhoto.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        encodedImage = Base64.getEncoder().encodeToString(byteArray);
                    }
                }
            });


}