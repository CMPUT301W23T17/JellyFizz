package com.example.qr_code_hunter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class loginActivity extends AppCompatActivity {

    //Owner of the account(username of player on current device)
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

    public interface getAllInfo {
        void onGetInfo(Owner owner);
    }

    /**
     * This read the data of current player from database and assign it to a Owner object
     * @param inputOwner The username of current player
     * @param callback  when to stop function when fetching data completes
     */
    public static void setCurrentOwnerObject(String inputOwner, getAllInfo callback) {
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

                    //Discus about this list of QR codes, not sure if necessary, passing an empty list for now
                    currentOwnerObject = new Owner(phoneNumber, email, inputOwner,
                            false, new ArrayList<>(), score, rank, totalCodeScanned);
                } else {
                    Log.d("Database Program Logic Error", "This player does not exist in database");
                }
            } else {
                Log.d("Database error", "Could not fetch data from database");
            }
            callback.onGetInfo(currentOwnerObject);
        });
    }


    /**
     This class handles the user login and registration process.
     It includes methods for verifying user input, checking if a user already exists in the database,
     and creating a new user in the database. It also saves the user's information locally using SharedPreferences.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Check if user has created an account before on this specific device, 1) if yes go to homepage 2) if no go to loginPage
        String accountCreatedKey = getString(R.string.accountCreated);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean accountCreated = prefs.contains(accountCreatedKey);

        if (accountCreated) {
            //set the owner
            owner = prefs.getString(accountCreatedKey, "");
            goToHomepage();
        }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference playersRef = db.collection("Players");

            EditText userNameViewer = findViewById(R.id.editTextUsername);

            //verify UserName in realtime as user is typing, use a handler to minimize pressure on database
            //if pressure still to much, can make it not in realtime
            Handler handler = new Handler(Looper.getMainLooper());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Thread currentThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EditText userNameView = findViewById(R.id.editTextUsername);
                            String username = userNameView.getText().toString().trim();
                            TextView userNameError = findViewById(R.id.userNameTaken);

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
                    } catch (InterruptedException e) {
                        Log.d("Thread Error", "Could not execute thread");
                    }

                    // Free the thread
                    currentThread.interrupt();
                }
            };

            userNameViewer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    handler.removeCallbacksAndMessages(null);
                    handler.postDelayed(runnable, 500);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    //nothing
                }
            });


            Button register = findViewById(R.id.registerButton);
            register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //verify Email adress
                    EditText emailEditText = findViewById(R.id.editTextEmail);
                    TextView emailError = findViewById(R.id.emailError);
                    String email = emailEditText.getText().toString().trim();

                    if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        // Email entered was invalid
                        emailError.setVisibility(View.VISIBLE);
                        emailEditText.requestFocus();
                        return;
                    } else {
                        emailError.setVisibility(View.INVISIBLE);
                    }

                    //verify Phone Number
                    EditText phoneNumbertext = findViewById(R.id.editTextPhone);
                    TextView phoneError = findViewById(R.id.phoneError);
                    String phoneNumber = phoneNumbertext.getText().toString().trim();

                    if (phoneNumber.length() < 10) {
                        phoneError.setVisibility(View.VISIBLE);
                        phoneNumbertext.requestFocus();
                        return;
                    } else {
                        phoneError.setVisibility(View.INVISIBLE);
                    }

                    //verify UserName error button is invisible
                    EditText userNameView = findViewById(R.id.editTextUsername);
                    String username = userNameView.getText().toString().trim();
                    TextView userNameError = findViewById(R.id.userNameTaken);

                    if (username.length() < 1) {
                        userNameError.setVisibility(View.VISIBLE);
                        userNameView.requestFocus();
                        return;
                    }

                    // Make sure string only contains letters and numbers
                    if (!username.matches("^[a-zA-Z0-9]*$")) {
                        userNameError.setText("username can only contain letters or numbers");
                        Log.d("String Checking", username);
                        userNameError.setVisibility(View.VISIBLE);
                        userNameView.requestFocus();
                        return;
                    }

                    if (userNameError.getVisibility() != View.INVISIBLE) {
                        // the TextView is currently visible
                        userNameView.requestFocus();
                        return;
                    }

                    //all checks passed, register key to shared Permissions to allow not to register next time
                    registerKey(username);

                    //  set up Owner object for new player
                    currentOwnerObject = new Owner(phoneNumber, email, username,
                            false, new ArrayList<DocumentReference>(), 0,0, 0);

                    //now add user to database
                    Map<String, Object> currentPlayer = new HashMap<>();

                    currentPlayer.put("email", email);
                    currentPlayer.put("hideInfo", false);
                    currentPlayer.put("phoneNumber", phoneNumber);
                    currentPlayer.put("rank", 0);
                    currentPlayer.put("score", 0);
                    currentPlayer.put("totalCodeScanned",0);

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
            });

    }

    /**
     * This function is used to return owner object.
     * @return return Owner object.
     */
    public Owner getOwnerNew() {return currentOwnerObject;}

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

        // Start the MainActivity.
        startActivity(intent);
    }
}
