package com.example.qr_code_hunter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class qrCodeAdapter extends ArrayAdapter<DocumentReference> {
    private Context currentContext;
    private ArrayList<DocumentReference> mQRCodeItemList;



    /**
     Constructs a new qrCodeAdapter with the given context, resource ID, and list of QR code items. These qrCodeItems are represented as
     Document References

     @param context The current context.

     @param resource The resource ID for a layout file containing a TextView to use when instantiating views.

     @param qrCodeItemList The list of QR code items to display.
     */
    public qrCodeAdapter(Context context, int resource, ArrayList<DocumentReference> qrCodeItemList) {
        super(context, resource, qrCodeItemList);

        currentContext = context;
        mQRCodeItemList = new ArrayList<>(qrCodeItemList);

    }


    /**
     Returns the number of items in the list.
     @return The number of items in the list.
     */
    @Override
    public int getCount() {
        return mQRCodeItemList.size();
    }

    /**
     * This method is responsible for setting up each individual qrCodeItem. It fetches their binaryString from the databse.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        //Creating new view to handle asynchronocity, refractor to recycler view if laggy or have time later
        LayoutInflater inflater = (LayoutInflater) currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.item_qrcodelist, parent, false);


        //Fetch hashString
        String hashString = mQRCodeItemList.get(position).getId();


        //Fetch next item in list and it's score
        DocumentReference nextCode;
        int nextScore;
        if (position < getCount() - 1) {
            QrCode nextCodeFiller = new QrCode();

            String nextHashString = mQRCodeItemList.get(position + 1).getId();
            nextScore = nextCodeFiller.setScore(nextHashString);
        } else {
            nextCode = null;

            //If no next score, Set the score to 0
            nextScore = 0;
        }


        //Fetch binaryString
        CompletableFuture<String> binaryStringFuture = fetchBinaryString(mQRCodeItemList.get(position));

        //Create filler QrCode
        QrCode currentQrCode = new QrCode();

        //Filler view to update view later
        final View viewPost = view;

        // Declare a variable to store the current position
        final int currentPosition = position;

        //Wait for binary string to be done
        binaryStringFuture.thenAccept(producedString -> {
            // Check if the current position is still the same
                //Run on UI Thread for updates
                viewPost.post(new Runnable() {
                    @Override
                    public void run() {
                        //Objects of the current qrCode item in the list
                        TextView qrCodeVisualRepTextView = (TextView) viewPost.findViewById(R.id.qr_code_visualRep);
                        TextView codeNameTextView = (TextView) viewPost.findViewById(R.id.code_name_text_view);
                        TextView pointsTextView = (TextView) viewPost.findViewById(R.id.points_text_view);
                        TextView highestLowestCodeTextView = (TextView) viewPost.findViewById(R.id.highest_lowest_code);
                        CheckBox checkBox = (CheckBox) viewPost.findViewById(R.id.qrCodeCheckbox);

                        qrCodeVisualRepTextView.setText(currentQrCode.getVisualRep(producedString));
                        codeNameTextView.setText(currentQrCode.setName(producedString));
                        Log.d("Hash", hashString);
                        pointsTextView.setText(String.valueOf(currentQrCode.setScore(hashString)) + " pts");

                        if (position == 0) {
                            highestLowestCodeTextView.setVisibility(View.VISIBLE);
                            highestLowestCodeTextView.setText("Highest Score Code!");
                        } else if (position == mQRCodeItemList.size() - 1) {
                            highestLowestCodeTextView.setVisibility(View.VISIBLE);
                            highestLowestCodeTextView.setText("Lowest Score Code!");
                        } else {
                            highestLowestCodeTextView.setVisibility(View.INVISIBLE);
                        }

                        //Create tag for Code and set the tag
                        qrCodeTag currentTag = new qrCodeTag(hashString, currentQrCode.setScore(hashString), nextScore);
                        viewPost.setTag(currentTag);
                        Log.d("Codes", " " + hashString);
                    }
                });
        });

        return view;
    }


    /**
     This method fetches a binary string from the Firestore database corresponding to a given document reference.

     @param currentDocument the document reference for which the binary string is to be fetched.

     @return a CompletableFuture that will complete with the binary string from the database when it is successfully fetched.
     */
    public CompletableFuture<String> fetchBinaryString(DocumentReference currentDocument) {

        CompletableFuture<String> binaryStringFuture = new CompletableFuture<String>();

        currentDocument.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            binaryStringFuture.complete(documentSnapshot.getString("binaryString"));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Inside qrCode Adapter", "Could not fetch binary string");
                    }
                });

        return binaryStringFuture;
    }

    /**
     This method updates the data in the adapter with a new ArrayList of DocumentReferences. Each DocumentReferennce represents a QrCode item
     @param newData the new ArrayList of DocumentReferences to set as the data in the adapter.
     */
    public void setData(ArrayList<DocumentReference> newData) {
        mQRCodeItemList = newData;
    }

}
