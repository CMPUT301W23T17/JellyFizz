package com.example.qr_code_hunter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

// CHATGPT was referenced
public class LoginActivity extends AppCompatActivity {

    // Owner of the account(username of player on current device)
    private static String owner;
    static Owner currentOwnerObject;
    // Database
    static FirebaseFirestore db = FirebaseFirestore.getInstance();
    static CollectionReference playersRef = db.collection("Players");

    public static String getOwnerName() {
        return owner;
    }
    public static void setOwnerName(String username) {
        owner = username;
    }

    /**
     * Retrieves all the QR code references for a specific player from the database and returns them
     * as an ArrayList. This method queries the "Players" collection to find all documents that reference
     * the player's document and retrieves the QR code references from those documents.
     *
     * @param currentPlayer The ID of the player for whom to retrieve the QR code references.
     * @return An ArrayList containing the DocumentReference objects for all the QR codes scanned by the player.
     */
    public static CompletableFuture<ArrayList<DocumentReference>> getQrCodes(String currentPlayer) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference scannedBy = db.collection("scannedBy");

        // Get owner of account reference
        DocumentReference playerReference = db.collection("Players").document(currentPlayer);

        CompletableFuture<ArrayList<DocumentReference>> returnCode = new CompletableFuture<>();

        ArrayList<DocumentReference> qrCodeRefs = new ArrayList<>();


        // Query the scannedBy collection to get all documents that reference the player's document
        Query query = scannedBy.whereEqualTo("Player", playerReference);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (querySnapshot.isEmpty()) {
                    Log.d("GetQrCodes", "No documents found for player: " + playerReference);
                    return;
                }

                // Iterate over the query results and add the QR code references to the ArrayList
                for (QueryDocumentSnapshot document : querySnapshot) {
                    DocumentReference docRef = document.getReference();

                    qrCodeRefs.add(((DocumentReference)document.get("qrCodeScanned")));
                }

                returnCode.complete(qrCodeRefs);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Display error
                Log.d("Database error", "Error getting all qrcodes for a player", e);
            }
        });

        return returnCode;
    }


    public interface getAllInfo {
        void onGetInfo(Owner owner);
    }

    /**
     * This read the data of current player from database and assign it to a Owner object
     * @param inputOwner The username of current player
     * @param callback  when to stop function when fetching data completes
     */
    public static void createOwnerObject(String inputOwner, getAllInfo callback) {
        playersRef.document(inputOwner).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String email = document.getString("email");
                    boolean hideInfo = document.getBoolean("hideInfo");
                    String phoneNumber = document.getString("phoneNumber");
                    int rank = document.getLong("rank").intValue();
                    int score = document.getLong("score").intValue();
                    int totalCodeScanned = document.getLong("totalCodeScanned").intValue();
                    int highestCode = document.getLong("highestCode").intValue();

                        currentOwnerObject = new Owner(phoneNumber, email, inputOwner,
                                false, new ArrayList<>(), score, rank, totalCodeScanned, highestCode);

                        Log.d("Callback", "being called");
                        callback.onGetInfo(currentOwnerObject);
                } else {
                    Log.d("Database Program Logic Error", "This player does not exist in database");
                }
            } else {
                Log.d("Database error", "Could not fetch data from database");
            }

        });
    }


    /**
     This class handles the user login and registration process.
     It includes methods for verifying user input, registering the user key, registering the user
     and creating a new user in the database. It also saves the user's information locally using SharedPreferences.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Futures, CompletableFuture, Handler, Executionservice(isShudown(), isTerminated()) are methods for handling asynchronocity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Check if user has created an account before on this specific device, 1) if yes go to homepage 2) if no go to loginPage
        String accountCreatedKey = getString(R.string.accountCreated);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean accountCreated = prefs.contains(accountCreatedKey);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference playersRef = db.collection("Players");
        Boolean noActiveTask = true;

        EditText userNameViewer = findViewById(R.id.editTextUsername);

        //verify UserName in realtime as user is typing, use a handler to minimize pressure on database
        //if pressure still to much, can make it not in realtime
        Handler handler = new Handler(Looper.getMainLooper());


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                boolean characterVerification = verifyUsername();
                if (!characterVerification) return;

                Thread currentThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText userNameView = findViewById(R.id.editTextUsername);
                        String username = userNameView.getText().toString().trim();
                        TextView userNameError = findViewById(R.id.userNameTaken);

                        //this empty message is used as a key to handle asynchornosity
                        handler.sendEmptyMessage(12);


                        if (!username.isEmpty()) {
                            playersRef.whereEqualTo(FieldPath.documentId(), username)
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (!Thread.currentThread().isInterrupted()) {
                                            if (task.isSuccessful()) {
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if (!querySnapshot.isEmpty()) {
                                                    // A player with the same username already exists
                                                    userNameError.setText("This username has been taken, please choose another");
                                                    userNameError.setVisibility(View.VISIBLE);
                                                    userNameView.requestFocus();
                                                } else {
                                                    // The username is available
                                                    userNameError.setVisibility(View.INVISIBLE);
                                                }
                                            } else {
                                                Log.d("Database Error", "Could not fetch data from the database");
                                            }
                                        }
                                    });
                        }
                    }
                });

                currentThread.start();

                // Wait for the thread to finish executing
                try {
                    currentThread.join();
                    handler.removeMessages(12);
                } catch (InterruptedException e) {
                    Log.d("Thread Error", "Could not execute thread");
                }

                // Free the thread
                currentThread.interrupt();
            }
        };


        //Watch as user types text
        userNameViewer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(runnable, 500);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Do nothing
            }
        });


        Button register = findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                register.setEnabled(false);
                // Get email address
                EditText emailEditText = findViewById(R.id.editTextEmail);
                String email = emailEditText.getText().toString().trim();


                // Get Phone Number
                EditText phoneNumbertext = findViewById(R.id.editTextPhone);
                String phoneNumber = phoneNumbertext.getText().toString().trim();

                // Get UserName
                EditText userNameView = findViewById(R.id.editTextUsername);
                String username = userNameView.getText().toString().trim();


                // Stall program to ensure database queries have been completed
                while (handler.hasMessages(12)) {

                }

                // If checks failed return
                if (!verifyInput()) {
                    register.setEnabled(true);
                    return;
                };

                // All checks passed, register key to shared Permissions to allow not to register next time
                registerKey(username);

                // Register user and go to homepage
                registerUser(username, email, phoneNumber, playersRef);
            }
        });

    }

    private boolean verifyUsername() {
        //verify UserName error button is invisible
        EditText userNameView = findViewById(R.id.editTextUsername);
        String username = userNameView.getText().toString().trim();
        TextView userNameError = findViewById(R.id.userNameTaken);


        if (username.length() < 1 || username.length() > 13) {
            userNameError.setText(getString(R.string.userNameLength));
            userNameError.setVisibility(View.VISIBLE);
            userNameView.requestFocus();
            return false;
        }


        // Make sure string only contains letters and numbers
        if (!username.matches("^[a-zA-Z0-9]*$")) {
            userNameError.setText(R.string.userNameInvalidCharacters);
            Log.d("String Checking", username);
            userNameError.setVisibility(View.VISIBLE);
            userNameView.requestFocus();
            return false;
        }

      return true;
    }

    /**
     This method is used to verify user input when registering for an account.
     It verifies that the email entered is a valid email address and that the phone number entered
     is at least 10 digits long. It also checks that a username has been entered and that it only contains
     letters or numbers. Finally, it returns true if all checks pass.
     @return true if all checks pass, false otherwise.
     */
    private boolean verifyInput() {
        // Verify email address
        EditText emailEditText = findViewById(R.id.editTextEmail);
        TextView emailError = findViewById(R.id.emailError);
        String email = emailEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            // Email entered was invalid
            emailError.setText(getString(R.string.invalidEmail));
            emailError.setVisibility(View.VISIBLE);
            emailEditText.requestFocus();
            return false;
        } else {
            emailError.setVisibility(View.INVISIBLE);
        }

        // Verify phone number
        EditText phoneNumberText = findViewById(R.id.editTextPhone);
        TextView phoneError = findViewById(R.id.phoneError);
        String phoneNumber = phoneNumberText.getText().toString().trim();

        if (phoneNumber.length() < 10) {
            phoneError.setVisibility(View.VISIBLE);
            phoneNumberText.requestFocus();
            return false;
        } else {
            phoneError.setVisibility(View.INVISIBLE);
        }

        //Username verification
        EditText userNameView = findViewById(R.id.editTextUsername);
        String username = userNameView.getText().toString().trim();
        TextView userNameError = findViewById(R.id.userNameTaken);

        boolean userName = verifyUsername();
        if (!userName) return false;

        if (userNameError.getVisibility() != View.INVISIBLE) {
            // The TextView is currently visible
            userNameView.requestFocus();
            return false;
        }

        // Passed all checks
        return true;
    }

    /**
     Registers a new user in the Firebase Firestore database with the given username, email, phone number, and CollectionReference.

     @param username the username of the new user
     @param email the email of the new user
     @param phoneNumber the phone number of the new user
     @param playersRef the CollectionReference object for the "Players" collection in Firestore database
     */
    private void registerUser(String username, String email, String phoneNumber, CollectionReference playersRef) {
        //now add user to database
        Map<String, Object> currentPlayer = new HashMap<>();

        currentPlayer.put("email", email);
        currentPlayer.put("hideInfo", false);
        currentPlayer.put("phoneNumber", phoneNumber);
        currentPlayer.put("rank", 0);
        currentPlayer.put("score", 0);
        currentPlayer.put("totalCodeScanned",0);
        currentPlayer.put("highestCode", 0);

        playersRef.document(username).set(currentPlayer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Player added successfully, time to go to homepage
                        goToHomepage();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Database Error", "Could not add a player to the database");
                    }
                });
    }


    /**
     * Register a username with the accountCreatedKey in SharedPreferences.
     * @param username The username to register.
     */
    private void registerKey(String username) {
        // Get the key to use for storing the account creation timestamp in SharedPreferences.
        String accountCreatedKey = getString(R.string.accountCreated);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(accountCreatedKey, username);

        // Save the changes to SharedPreferences.
        editor.apply();
    }


    /**
     * Navigate to the homepage activity.
     */
    private void goToHomepage() {
        // Create an Intent to start the MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
