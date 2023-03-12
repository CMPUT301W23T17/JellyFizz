package com.example.qr_code_hunter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link loginPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class loginPageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public loginPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment loginPage.
     */
    // TODO: Rename and change types and number of parameters
    public static loginPageFragment newInstance(String param1, String param2) {
        loginPageFragment fragment = new loginPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference playersRef = db.collection("Players");

        EditText userNameViewer = view.findViewById(R.id.editTextUsername);

        //verify UserName in realtime as user is typing, use a handler to minimize pressure on database
        //if pressure still to much, can make it not in realtime
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Thread currentThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EditText userNameView = view.findViewById(R.id.editTextUsername);
                        String username = userNameView.getText().toString().trim().replace("/","");
                        TextView userNameError = view.findViewById(R.id.userNameTaken);

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


        Button register = view.findViewById(R.id.registerButton);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //verify Email adress
                EditText emailEditText = view.findViewById(R.id.editTextEmail);
                TextView emailError = view.findViewById(R.id.emailError);
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
                EditText phoneNumbertext = view.findViewById(R.id.editTextPhone);
                TextView phoneError = view.findViewById(R.id.phoneError);
                String phoneNumber = phoneNumbertext.getText().toString().trim();

                if (phoneNumber.length() < 10) {
                    phoneError.setVisibility(View.VISIBLE);
                    phoneNumbertext.requestFocus();
                    return;
                } else {
                    phoneError.setVisibility(View.INVISIBLE);
                }

                //verify UserName error button is invisible
                EditText userNameView = view.findViewById(R.id.editTextUsername);
                String username = userNameView.getText().toString().trim();
                TextView userNameError = view.findViewById(R.id.userNameTaken);

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

                //now add user to database
                Map<String, Object> currentPlayer = new HashMap<>();

                currentPlayer.put("email", email);
                currentPlayer.put("hideInfo", false);
                currentPlayer.put("phoneNumber", phoneNumber);
                currentPlayer.put("rank", 0);
                currentPlayer.put("score", 0);

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
    private void registerKey(String username) {
        String accountCreatedKey = getString(R.string.accountCreated);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(accountCreatedKey, username);
        editor.apply();
    }

    private void goToHomepage() {
        Fragment homePage = new HomepageFragment();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,homePage);
        fragmentTransaction.commit();
    }
}