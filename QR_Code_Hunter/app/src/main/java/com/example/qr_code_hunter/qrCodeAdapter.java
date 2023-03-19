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

    public qrCodeAdapter(Context context, int resource, ArrayList<DocumentReference> qrCodeItemList) {
        super(context, resource, qrCodeItemList);

        currentContext = context;
        mQRCodeItemList = new ArrayList<>(qrCodeItemList);

    }

    @Override
    public int getCount() {
        return mQRCodeItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_qrcodelist, parent, false);
        }

        //Fetch hashString
        String hashString = mQRCodeItemList.get(position).getId();


        //Fetch next item in list and it's score
        DocumentReference nextCode;
        int nextScore;
        if (position < getCount() - 1) {
            QrCode nextCodeFiller = new QrCode();

            String nextHashString = mQRCodeItemList.get(position+1).getId();
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

        //Wait for binary string to be done
        binaryStringFuture.thenAccept(producedString -> {
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
                    pointsTextView.setText(String.valueOf(currentQrCode.setScore(hashString))+ " pts");


                    //Create tag for Code and set the tag
                    qrCodeTag currentTag = new qrCodeTag(hashString, currentQrCode.setScore(hashString), nextScore);
                    viewPost.setTag(currentTag);

                   viewPost.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {

                           CheckBox currentCheckBox = (CheckBox) viewPost.findViewById(R.id.qrCodeCheckbox);
                           if(currentCheckBox.getVisibility() == View.INVISIBLE) return;

                           currentCheckBox.toggle();
                       }
                   });
                }
            });

        }
        );

        return view;
    }

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

        return  binaryStringFuture;
    }

    public void setData(ArrayList<DocumentReference> newData) {
        mQRCodeItemList = newData;
    }

}
