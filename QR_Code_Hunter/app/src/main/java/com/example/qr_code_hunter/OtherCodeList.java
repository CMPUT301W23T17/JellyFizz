package com.example.qr_code_hunter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 * CHATGPT was referenced
 */
public class OtherCodeList extends Fragment {
    String username;
    public OtherCodeList(String username) {
        this.username = username;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other_code_list, container, false);
    }

    /**
     * This method retrieves a list of QR codes associated with the current user and sorts them based on their scores.
     * It then populates the UI with the sorted list of QR codes using a custom adapter. This method is executed on the main UI thread
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<deleteTag> currentSortedCodes = new ArrayList<>();

        displayCodes(username, new QrCodeList.sortedCodes() {
            @Override
            public void onSuccess(ArrayList<DocumentReference> sortedCodes) {

                View currentView = getView();
                //Update on main UI thread
                currentView.post(new Runnable() {
                    @Override
                    public void run() {
                        ListView qrCodeListView = currentView.findViewById(R.id.qr_code_lister);



                        currentSortedCodes.clear();
                        for (DocumentReference item: sortedCodes) {
                            deleteTag currentDelete = new deleteTag(item);
                            currentSortedCodes.add(currentDelete);
                        }

                        QrCodeAdapter codeAdapter = new QrCodeAdapter(getActivity(), 0, currentSortedCodes);
                        qrCodeListView.setAdapter(codeAdapter);
                    }
                });

            }
        });

        ImageView moreButton = view.findViewById(R.id.return_button);
        moreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout,new OtherPlayerFragment(username));
                fragmentTransaction.commit();
            }
        });

    }

    /**
     This method retrieves the QR codes associated with a given user and displays them in descending order
     of scores. The method uses a callback to communicate the sorted QR codes back to the calling method.
     @param username The username associated with the QR codes to be retrieved and sorted.
     @param callback A callback that communicates the sorted QR codes back to the calling method.
     */
    public void displayCodes(String username, QrCodeList.sortedCodes callback) {

        ArrayList<DocumentReference> returnedDocs = new ArrayList<DocumentReference>();

        ArrayList<DocumentReference> playerQrCodes = new ArrayList<DocumentReference>();
        CompletableFuture<ArrayList<DocumentReference>> qrCodesFuture = LoginActivity.getQrCodes(username);

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
        }).thenAccept(sortedQrCodes -> {
            callback.onSuccess(sortedQrCodes);
        });
    }


    /**
     This method retrieves the score associated with a given QR code and returns it as a CompletableFuture.
     @param docRef A DocumentReference object that represents the QR code whose score is to be retrieved.
     @return A CompletableFuture<Integer> object that eventually contains the score associated with the given QR code.
     */
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