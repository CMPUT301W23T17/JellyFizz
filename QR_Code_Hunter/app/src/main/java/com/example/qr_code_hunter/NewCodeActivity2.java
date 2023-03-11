package com.example.qr_code_hunter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewCodeActivity2 extends AppCompatActivity {
    ImageView picture;
    EditText descBox;
    TextView charCount;
    CheckBox saveLoc;
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
        saveLoc = findViewById(R.id.save_geolocation_button);
        recordCode = findViewById(R.id.record_code_button);
        saveBtn = findViewById(R.id.save_button);
        newCode = getIntent().getParcelableExtra("New QrCode");

        picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open camera fragment/activity
            }
        });

        descBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String desc = "\n" + s;
                descBox.setText(desc);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String desc = "\n" + s;
                descBox.setText(desc);
                String countLabel = String.valueOf(count) + "/150";
                charCount.setText(countLabel);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String desc = "\n" + s;
                descBox.setText(desc);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveLoc.isChecked()) {
                    // newCode.setLocation(); -- save the location based on user's current location?
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
}
