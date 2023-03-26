package com.example.qr_code_hunter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import org.checkerframework.checker.units.qual.C;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Base64;

public class CodeDetailsFragment extends Fragment {
    String hashString; // what is provided into this fragment
    String ownerName;
    CommentSection commentSection = new CommentSection();
    ArrayList<CommentSection> commentList = new ArrayList<>();
    ArrayList<CommentSection> playerList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView codeName;
    TextView codeVisual;
    TextView codeLocation;
    TextView codeScore;
    ImageView codeImage;
    TextView backButton;
    Button showComment;

    public CodeDetailsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        backButton = view.findViewById(R.id.details_backBtn);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();
        assert bundle != null;
        hashString = bundle.getString("Hash");
        ownerName = loginActivity.getOwnerName();

        // Access to the QrCodes collection to get necessary data
        DocumentReference qrRef = db.collection("QrCodes").document(hashString);

        // Get code details
        qrRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            // Get necessary details
                            String scoreText = documentSnapshot.getLong("Score").intValue()+" pts";
                            String locText = "Geolocation not saved";
                            if(documentSnapshot.contains("latitude")) {
                                double lat = documentSnapshot.getLong("latitude");
                                double lng = documentSnapshot.getLong("longitude");
                                locText = "(" + lat + ", " + lng + ")";
                            }
                            codeLocation.setText(locText);
                            codeName.setText(documentSnapshot.getString("codeName"));
                            codeScore.setText(scoreText);
                        }
                    }
                });

        // Display image if available
        db.collection("scannedBy")
                .whereEqualTo("qrCodeScanned",qrRef)
                .whereEqualTo("Player",db.document("Players/"+ownerName))
                .limit(1)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                        if(document.contains("Photo")) {
                            // Decode the encoded string
                            byte[] byteArray = Base64.getDecoder().decode(document.getString("Photo"));
                            //byte[] byteArray = android.util.Base64.decode(encodedImageString, android.util.Base64.DEFAULT);
                            // Convert the array byte into bitmap
                            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            codeImage.setImageBitmap(bitmap);
                        } else {
                            codeImage.setImageResource(R.drawable.no_image);
                        }
                    } else {
                        Log.e("TAG", "Error getting image", task.getException());
                    }
                });


        // Get list of players scanned this code and list of their comments if available
        commentSection.arrangeCommentSection(qrRef, (comments, players) -> {
            commentList.addAll(comments);
            playerList.addAll(players);
        });

        // Show bottom dialog of comments
        showComment = view.findViewById(R.id.comment_dialog_btn);
        showComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDialogFragment commentDialog = CommentDialogFragment.newInstance(commentList, playerList);
                commentDialog.show(getChildFragmentManager(), commentDialog.getTag());
            }
        });

        // Option to come back to previous fragment (user profile)
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace fragment with player's list of codes fragment screen
                getParentFragmentManager().popBackStack();
            }
        });
    }
}
