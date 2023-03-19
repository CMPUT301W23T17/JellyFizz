package com.example.qr_code_hunter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link qrCodeList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class qrCodeList extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public qrCodeList() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment qrCodeList.
     */
    // TODO: Rename and change types and number of parameters
    public static qrCodeList newInstance(String param1, String param2) {
        qrCodeList fragment = new qrCodeList();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        // pass the hashString
        // make the username acceptance general

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
        return inflater.inflate(R.layout.fragment_qr_code_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        displayCodes("QuinNguyen", view);

    }

    public void displayCodes(String username, View view) {

        ArrayList<DocumentReference> returnedDocs = new ArrayList<DocumentReference>();

        ArrayList<DocumentReference> playerQrCodes = new ArrayList<DocumentReference>();
        CompletableFuture<ArrayList<DocumentReference>> qrCodesFuture = loginActivity.getQR_Codes(username);

        qrCodesFuture.thenCompose(qrCodesDocRef -> {
            // Create a list of CompletableFuture<Integer> objects that will eventually be completed with the scores of the QR codes
            List<CompletableFuture<Integer>> scoreFutures = new ArrayList<>();
            for (DocumentReference qrCode : qrCodesDocRef) {

                scoreFutures.add(getScoreCode(qrCode));
                playerQrCodes.add(qrCode);
            }

            // Combine all CompletableFuture<Integer> objects into a single CompletableFuture<List<Integer>> object
            CompletableFuture<ArrayList<Integer>> scoresFuture = CompletableFuture.allOf(scoreFutures.toArray(new CompletableFuture[0]))
                    .thenApply(v -> {
                        ArrayList<Integer> scores = new ArrayList<>();
                        for (CompletableFuture<Integer> scoreFuture : scoreFutures) {
                            scores.add(scoreFuture.join());
                        }
                        return scores;
                    });

            // Sort the playerQrCodes list based on the scores
            return scoresFuture.thenApply(scores -> {
                ArrayList<DocumentReference> sortedQrCodes = new ArrayList<>(playerQrCodes);
                Collections.sort(sortedQrCodes, (doc1, doc2) -> scores.get(playerQrCodes.indexOf(doc2)) - scores.get(playerQrCodes.indexOf(doc1)));
                return sortedQrCodes;
            });
        }).thenAcceptAsync(sortedQrCodes -> {

            for (DocumentReference d: sortedQrCodes) {
                Log.d("Passing", " " + d.getPath());
            }

                ListView qrCodeList = getView().findViewById(R.id.qr_code_list);

                qrCodeAdapter adapter = new qrCodeAdapter(getActivity(), 0, sortedQrCodes);
                qrCodeList.setAdapter(adapter);
        });

    }

    public CompletableFuture<Integer> getScoreCode(DocumentReference docRef) {

        CompletableFuture<Integer> currentScoreFuture = new CompletableFuture<Integer>();

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    int score = documentSnapshot.getLong("Score").intValue();

//                    Complete future
                    currentScoreFuture.complete(score);
                } else {
                    Log.d("getScoreCode", "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("getScoreCode", "Error getting document: " + e.getMessage());
            }
        });

        return currentScoreFuture;
    }

}