package com.example.qr_code_hunter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
 */

//CHATGPT was referenced
public class QrCodeList extends Fragment {
    public Owner currentOwner;
    public boolean goToGarbage = true;

    public QrCodeList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_code_list, container, false);

    }


    /**
     * Overrides the {@link androidx.fragment.app.Fragment#onViewCreated(View, Bundle)} method to set up the UI elements and
     * functionalities of the QR code list screen. This includes setting up the adapter for the ListView, adding item click listeners,
     * setting up the garbage can button and delete button functionality, and handling the return button.
     *
     * @param view               The View object associated with this fragment.
     * @param savedInstanceState A Bundle object containing the saved state of the fragment.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<deleteTag> currentSortedCodes = new ArrayList<>();

        displayCodes(LoginActivity.getOwnerName(), new sortedCodes() {
            @Override
            public void onSuccess(ArrayList<DocumentReference> sortedCodes) {
                View currentView = getView();
                //Update on main UI thread
                currentView.post(new Runnable() {
                    @Override
                    public void run() {
                        View currentView = getView();
                        ListView qrCodeListView = currentView.findViewById(R.id.qr_code_lister);


                        currentSortedCodes.clear();
                        for (DocumentReference item : sortedCodes) {
                            deleteTag currentDelete = new deleteTag(item);
                            currentSortedCodes.add(currentDelete);
                        }

                        QrCodeAdapter codeAdapter = new QrCodeAdapter(getActivity(), 0, currentSortedCodes);
                        qrCodeListView.setAdapter(codeAdapter);

                        qrCodeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                if (!goToGarbage) {
                                    QrCodeTag currentTag = (QrCodeTag) view.getTag();

                                    for(int j = 0; j < currentSortedCodes.size(); j++) {
                                        if (currentSortedCodes.get(j).getHashString().getId() == currentTag.hashString) {
                                            currentSortedCodes.get(j).setChecked(!currentSortedCodes.get(j).isChecked());
                                        }
                                    }

                                    CheckBox currentCheckBox = view.findViewById(R.id.qrCodeCheckbox);
                                    currentCheckBox.setVisibility(View.VISIBLE);
                                    currentCheckBox.toggle();
                                } else {
                                    Log.d("Item Click", "Item is being clicked");
                                    Fragment fragment = new CodeDetailsFragment();
                                    FragmentManager fragmentManager = getParentFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_layout, fragment);

                                    DocumentReference selected = ((deleteTag) parent.getItemAtPosition(position)).getHashString();
                                    String selectedHash = selected.getId();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("Hash", selectedHash);
                                    fragment.setArguments(bundle);


                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                            }
                        });
                    }
                });
            }
        });

        // Set garbage can listener
        ImageView garbageButton = view.findViewById(R.id.garbage_can_icon);
        garbageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView qrCodeDisplay = getView().findViewById(R.id.qr_code_lister);

                if (goToGarbage) {
                    // Change garbage can icon to red
                    garbageButton.setImageResource(R.drawable.ic_delete_red);

                    //set deleteButton to be visible
                    Button deleteButton = getView().findViewById(R.id.delete_qrcode_list);
                    deleteButton.setVisibility(View.VISIBLE);
                    goToGarbage = false;
                } else {
                    for (int j = 0; j < currentSortedCodes.size(); j++) {
                        currentSortedCodes.get(j).setChecked(false);
                    }

                    //Update the adapter that the boxes are not checked
                    QrCodeAdapter adapter1 = (QrCodeAdapter) qrCodeDisplay.getAdapter();
                    adapter1.notifyDataSetChanged();


                    //set garbage can to be black again
                    ImageView garbageButton = getView().findViewById(R.id.garbage_can_icon);
                    garbageButton.setImageResource(R.drawable.ic_delete);


                    //set deleteButton to be invisible
                    Button deleteButton = getView().findViewById(R.id.delete_qrcode_list);
                    deleteButton.setVisibility(View.GONE);
                    goToGarbage = true;
                }
            }

        });


        //set garbage can listener
        ImageView undoGarbageButton = view.findViewById(R.id.return_button);
        undoGarbageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new PlayerProfileFragment());
                fragmentTransaction.commit();
            }
        });


        //set deleteButton
        Button deleteButton = view.findViewById(R.id.delete_qrcode_list);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView qrCodeDisplay = getView().findViewById(R.id.qr_code_lister);

                if (qrCodeDisplay.getCount() < 1) return;

                QrCodeAdapter adapter1 = (QrCodeAdapter) qrCodeDisplay.getAdapter();
                adapter1.setData(currentSortedCodes);

                List<Integer> itemsToRemove = new ArrayList<>();

                for (int j = 0; j < currentSortedCodes.size(); j++) {
                    if (currentSortedCodes.get(j).isChecked()) {
                        // Add the index to the list of items to remove
                        itemsToRemove.add(j);

                        //uncheck the item
                        currentSortedCodes.get(j).setChecked(false);
                    }
                }


                // Remove the items from the list in reverse order
                Collections.reverse(itemsToRemove);
                ArrayList<QrCodeTag> tags = new ArrayList<>();

                int staticSize = currentSortedCodes.size();

                for (int i : itemsToRemove) {

                    // Fetch next item in list and its score
                    DocumentReference nextCode;
                    int nextScore;
                    QrCode nextCodeFiller = new QrCode();


                    if (i < staticSize - 1) {

                        String nextHashString = currentSortedCodes.get(i+ 1).getHashString().getId();
                        nextScore = nextCodeFiller.setScore(nextHashString);
                    } else {
                        nextCode = null;

                        // If no next score, Set the score to 0
                        nextScore = 0;
                    }

                    QrCodeTag currentTag = new QrCodeTag(currentSortedCodes.get(i).getHashString().getId(), nextCodeFiller.setScore(currentSortedCodes.get(i).getHashString().getId()), nextScore);
                    tags.add(currentTag);
                }

                for (int i: itemsToRemove) {
                    //Remove from storage of tags within the adapter
                    currentSortedCodes.remove(i);
                }

                LoginActivity.createOwnerObject(LoginActivity.getOwnerName(), new LoginActivity.getAllInfo() {
                    @Override
                    public void onGetInfo(Owner owner) {
                        currentOwner = owner;
                        currentOwner.deleteQRCode(tags);
                    }
                });


                //Uncheck all boxes
                for (int j = 0; j < currentSortedCodes.size(); j++) {
                    currentSortedCodes.get(j).setChecked(false);
                }

                //set garbagecan to be black again
                ImageView garbageButton = getView().findViewById(R.id.garbage_can_icon);
                garbageButton.setImageResource(R.drawable.ic_delete);

                //set deleteButton to be invisible
                Button deleteButton = getView().findViewById(R.id.delete_qrcode_list);
                deleteButton.setVisibility(View.GONE);
                goToGarbage = true;

                //update adapter
                adapter1.notifyDataSetChanged();
            }
        });

    }

    /**
     * Interface that defines a method to be called when a list of DocumentReferences is successfully sorted.
     */
    public interface sortedCodes {
        void onSuccess(ArrayList<DocumentReference> sortedCodes);
    }


    /**
     * This method displays the sorted QR codes for a given username by sorting them based on their scores and then relying on a callback
     *
     * @param username the username for which the QR codes are to be displayed
     * @param callback the sortedCodes object to be called upon successful sorting of the ArrayList of DocumentReferences
     */
    public void displayCodes(String username, sortedCodes callback) {

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
     * This method gets the score of a given DocumentReference object.
     *
     * @param docRef the DocumentReference object for which the score is to be obtained
     * @return a CompletableFuture<Integer> object which will eventually be completed with the score of the given DocumentReference
     */
    public static CompletableFuture<Integer> getScoreCode(DocumentReference docRef) {

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