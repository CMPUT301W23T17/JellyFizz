package com.example.qr_code_hunter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

public class NewCodeActivity extends AppCompatActivity {
    TextView codeName;
    TextView visualRep;
    TextView codeLoc;
    TextView codePts;
    TextView otherPlayers;
    Button nextPageBtn;
    QrCode newCode;
    String scannedString; // assume this is the scanned string returned from scanner

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_code);
        codeName = findViewById(R.id.code_name);
        visualRep = findViewById(R.id.visual_rep);
        codeLoc = findViewById(R.id.code_location);
        codePts = findViewById(R.id.points_label);
        otherPlayers = findViewById(R.id.others_scan_list);
        nextPageBtn = findViewById(R.id.next_button);

        scannedString = "b1ab25c55913c95cc6913f1dbce9bef185ebf00a64553a8ef"; // example
        try {
            newCode = new QrCode(scannedString);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        codeName.setText(newCode.getName());
        visualRep.setText(newCode.getVisualRep());

        String scoreLabel = "You earned " + String.valueOf(newCode.getScore()) + " points!";
        codePts.setText(scoreLabel);


        if (newCode.getPlayerList().size() > 2) {
            String othersLabel = "+" + String.valueOf(newCode.getPlayerList().size() - 1) + " others have scanned this code";
            otherPlayers.setText(othersLabel);
            otherPlayers.setVisibility(View.VISIBLE);

        } else if (newCode.getPlayerList().size() == 2){ // you and someone else
            String othersLabel = "+1 other have scanned this code";
            otherPlayers.setText(othersLabel);
            otherPlayers.setVisibility(View.VISIBLE);
        }
        else { // you are the only one
            otherPlayers.setVisibility(View.INVISIBLE);
        }

        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewCodeActivity.this, NewCodeActivity2.class);
                intent.putExtra("New QrCode", newCode);
                startActivity(intent);
            }
        });
    }
}
