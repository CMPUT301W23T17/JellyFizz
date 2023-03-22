package com.example.qr_code_hunter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Base64;

public class CodeDetailsFragment extends Fragment {
    String hashString; // what is provided into this fragment
    ArrayList<String> otherPlayers = new ArrayList<String>();

    TextView codeName;
    TextView codeVisual;
    TextView codeLocation;
    TextView codeScore;
    ImageView codeImage;
    EditText codeDesc;
    TextView codeOthers;
    TextView backButton;
    TextView editButton;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    QueryDocumentSnapshot matchFound;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CodeDetailsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CodeDetailsFragment newInstance(String param1, String param2) {
        CodeDetailsFragment fragment = new CodeDetailsFragment();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_code_details, container, false);

        codeName = view.findViewById(R.id.details_codename);
        codeVisual = view.findViewById(R.id.details_visual);
        codeLocation = view.findViewById(R.id.details_location);
        codeScore = view.findViewById(R.id.details_points);
        codeImage = view.findViewById(R.id.details_image);
        codeDesc = view.findViewById(R.id.details_comment);
        codeOthers = view.findViewById(R.id.details_others);

        backButton = view.findViewById(R.id.details_backBtn);
        editButton = view.findViewById(R.id.details_editCommentBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        hashString = bundle.getString("Hash");

        // Access to the QrCodes collection to get necessary data
        DocumentReference qrRef = db.collection("QrCodes").document(hashString);

        // Get code details
        qrRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            // Get necessary details
                            String name = documentSnapshot.getString("codeName");
                            int score = Math.toIntExact(documentSnapshot.getLong("Score"));
                            double lat = documentSnapshot.getLong("latitude");
                            double lng = documentSnapshot.getLong("longitude");

                            codeName.setText(name);

                            String locText = String.valueOf(lat) + ", " + String.valueOf(lng);
                            codeLocation.setText(locText);

                            String scoreText = String.valueOf(score) + " pts";
                            codeScore.setText(scoreText);
                        }
                    }
                });

        db.collection("scannedBy")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            otherPlayers.clear(); // avoid duplicates other players upon backstack pop
                            for(QueryDocumentSnapshot doc: task.getResult()) {
                                DocumentReference playerRef = doc.getDocumentReference("Player");
                                DocumentReference codeRef = doc.getDocumentReference("qrCodeScanned");

                                String username = playerRef.getId();
                                String codeString = codeRef.getId();

                                if(username.equals(loginActivity.getOwnerName()) && codeString.equals(hashString)) {
                                    codeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            matchFound = doc;
                                            String userComment = doc.getString("Comment");
                                            String encodedImageString = doc.getString("Photo");

                                            codeDesc.setText(userComment);

                                            // Decode the encoded string
                                            byte[] byteArray = Base64.getDecoder().decode(encodedImageString);
                                            //byte[] byteArray = android.util.Base64.decode(encodedImageString, android.util.Base64.DEFAULT);

                                            // Convert the array byte into bitmap
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

                                            codeImage.setImageBitmap(bitmap);

                                        }
                                    });
                                }
                                else if (!username.equals(loginActivity.getOwnerName()) && codeString.equals(hashString)) {
                                    // other players who have scanned this code
                                    otherPlayers.add(username);
                                }

                            }
                        }
                    }
                });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace fragment with player's list of codes fragment screen
                getParentFragmentManager().popBackStack();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set focus on editText field, set editText field to editable, but not clickable?
                codeDesc.setFocusable(true);
                codeDesc.setFocusableInTouchMode(true);
                codeDesc.setClickable(true);

                // so the comment would be updated to the database first
                editButton.setClickable(false);
                backButton.setClickable(false);

                codeDesc.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(codeDesc, InputMethodManager.SHOW_IMPLICIT);

                // Set cursor to the end of the sentence
                codeDesc.setSelection(codeDesc.getText().length());

                Toast.makeText(getActivity(), "Comment editing enabled", Toast.LENGTH_SHORT).show();

                codeDesc.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(s.length() == 150) {
                            Toast.makeText(getContext(), "Maximum character threshold exceeded!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
            }
        });

        codeDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    codeDesc.setClickable(false);
                    codeDesc.setFocusable(false);
                    codeDesc.setFocusableInTouchMode(false);

                    // re-enable the other buttons
                    editButton.setClickable(true);
                    backButton.setClickable(true);

                    String updateDoc = matchFound.getId();
                    DocumentReference toUpdate = db.collection("scannedBy").document(updateDoc);

                    String newComment = codeDesc.getText().toString();

                    toUpdate.update("Comment", newComment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Comment saved, click again to go back", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error updating comment!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        codeOthers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opens alert dialog
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                builderSingle.setTitle("Other hunters who have hunted this code");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice);
                for(String playerName: otherPlayers) {
                    arrayAdapter.add(playerName);
                }

                builderSingle.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedPlayer = arrayAdapter.getItem(which);
                        // go to the selectedPlayer profile
                        Fragment fragment = new OtherPlayerFragment(selectedPlayer);
                        FragmentManager fragmentManager = getParentFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.frame_layout, fragment);

                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }
                });
                builderSingle.show();
            }
        });

    }
}
