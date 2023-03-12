package com.example.qr_code_hunter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewCodeActivity2 extends AppCompatActivity {
    ImageView picture;
    EditText descBox;
    TextView charCount;
    CheckBox saveGeo;
    CheckBox recordCode;
    Button saveBtn;
    QrCode newCode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_code_2);
        picture = findViewById(R.id.picture_details);
        descBox = findViewById(R.id.comment_box);
        charCount = findViewById(R.id.char_count_label);
        saveGeo = findViewById(R.id.save_geolocation_button);
        recordCode = findViewById(R.id.record_code_button);
        saveBtn = findViewById(R.id.save_button);
        newCode = getIntent().getParcelableExtra("New QrCode");

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent openCam = new Intent("android.media.action.IMAGE_CAPTURE");
                Intent openCam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhoto.launch(openCam);
            }
        });

        descBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

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
                if(saveGeo.isChecked()) {
                    // newCode.setLocation(); -- save the location based on user's current location
                    // is not geolocation, must implement a method setGeolocation()?
                }
                if(recordCode.isChecked()) {
                    newCode.setPrivacy(false); // actual code is saved
                } else {
                    newCode.setPrivacy(true);
                }

                Intent intent = new Intent(NewCodeActivity2.this, MainActivity.class);
                intent.putExtra("Save code", newCode);
                startActivity(intent);
            }
        });
    }

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
                }
            }
    });
}