package com.example.qr_code_hunter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

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

        //Toast.makeText(getContext(), hashString, Toast.LENGTH_LONG).show();

        // Access to the QrCodes collection to get necessary data
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
                // replace fragment with player's list of codes fragment screen
                getParentFragmentManager().popBackStack();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set focus on editText field, set editText field to editable, but not clickable?

                codeDesc.setFocusable(true);
                codeDesc.setFocusableInTouchMode(true);
                codeDesc.setClickable(true);

                codeDesc.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(codeDesc, InputMethodManager.SHOW_IMPLICIT);

                // Set cursor to the end of the sentence
                codeDesc.setSelection(codeDesc.getText().length());

                Toast.makeText(getActivity(), "Comment editing enabled", Toast.LENGTH_SHORT).show();

                // in case keyboard still does not show:
                //imm.showSoftInput(codeDesc, InputMethodManager.SHOW_FORCED);

            }
        });

        codeDesc.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    //Toast.makeText(getActivity(), "Comment editing disabled", Toast.LENGTH_SHORT).show();

                    codeDesc.setClickable(false);
                    codeDesc.setFocusable(false);
                    codeDesc.setFocusableInTouchMode(false);

                    String updateDoc = matchFound.getId();
                    DocumentReference toUpdate = db.collection("scannedBy").document(updateDoc);

                    String newComment = codeDesc.getText().toString();

                    toUpdate.update("Comment", newComment)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getContext(), "Comment updated successfully!", Toast.LENGTH_SHORT).show();
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







    }


}
