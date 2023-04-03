package com.example.qr_code_hunter;

import static org.junit.Assert.assertTrue;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(AndroidJUnit4.class)
public class insertQrCodes {

    private Solo solo;
    private Solo solo2;
    static FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    public ActivityTestRule<MainActivity> mainActivityRule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    //Enter 3 hashCodes to test here:
    static String hashString3 = "4411111111111111111111111111111111111111111111111111111111111111";
    static String hashString2 = "3311111111111111111111111111111111111111111111111111111111111111";
    static String hashString1 = "2211111111111111111111111111111111111111111111111111111111111111";
    static String hashString4 = "5511111111111111111111111111111111111111111111111111111111111111";

    static String hashString5 = "6611111111111111111111111111111111111111111111111111111111111111";
    static String hashString6 = "7711111111111111111111111111111111111111111111111111111111111111";
    static String hashString7 = "8811111111111111111111111111111111111111111111111111111111111111";
    static String hashString8 = "9911111111111111111111111111111111111111111111111111111111111111";

    static String[] testHashStrings = {hashString3, hashString2, hashString1, hashString4, hashString5, hashString6, hashString7, hashString8};

    /**
     * Runs before all tests and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo2 = new Solo(InstrumentationRegistry.getInstrumentation(), mainActivityRule.getActivity());

        for (int i = 0; i < testHashStrings.length; i++) {

            //Futures
            CompletableFuture complete1 = new CompletableFuture<>();
            CompletableFuture complete2 = new CompletableFuture<>();

            QrCode fillerCode = new QrCode();

            // Mock relation in QrCodes collection
            Map<String, Object> mockQr = new HashMap<>();
            mockQr.put("binaryString", "hashString" + i);
            mockQr.put("codeName", "hashString" + i);
            mockQr.put("Score", fillerCode.setScore(testHashStrings[i]));

            // Mock relation in scannedBy collection
            Map<String, Object> mockRelation = new HashMap<>();
            DocumentReference qrRef = db.collection("QrCodes")
                    .document(testHashStrings[i]);
            DocumentReference playerRef = db.collection("Players").document("MastrMatt");
            mockRelation.put("Player", playerRef);
            mockRelation.put("qrCodeScanned", qrRef);

            // Add to database
            db.collection("QrCodes")
                    .document(testHashStrings[i])
                    .set(mockQr).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            complete1.complete(null);
                        }
                    });

            db.collection("scannedBy")
                    .document()
                    .set(mockRelation).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            complete2.complete(null);
                        }
                    });

            complete2.join();
            complete1.join();
        }

    }

    @Test
    public void insert() {

    }
}
