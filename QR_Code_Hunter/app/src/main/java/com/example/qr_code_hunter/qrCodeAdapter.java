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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

        Log.d("Getting into listview", "aasdasd");

        View view = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) currentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_qrcodelist, parent, false);
        }

        //Fetch hashString
        String hashString = mQRCodeItemList.get(position).getId();

        //Fetch binaryString
        CompletableFuture<String> binaryStringFuture = fetchBinaryString(mQRCodeItemList.get(position));

        //Create filler QrCode
        QrCode currentQrCode = new QrCode();

        //Objects of the current qrCode item in the list
        TextView qrCodeVisualRepTextView = (TextView) view.findViewById(R.id.qr_code_visualRep);
        TextView codeNameTextView = (TextView) view.findViewById(R.id.code_name_text_view);
        TextView pointsTextView = (TextView) view.findViewById(R.id.points_text_view);
        TextView highestLowestCodeTextView = (TextView) view.findViewById(R.id.highest_lowest_code);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);


        //Wait for binary string to be done
        binaryStringFuture.thenAccept(producedString -> {
            qrCodeVisualRepTextView.setText(currentQrCode.getVisualRep(producedString));
            codeNameTextView.setText(currentQrCode.setName(producedString));
            pointsTextView.setText(currentQrCode.setScore(hashString));
        }
        );



        qrCodeVisualRepTextView.setText("asdasdasdasd");
        codeNameTextView.setText(currentQrCode.setName("asdasasd"));
        pointsTextView.setText(currentQrCode.setScore("asdasdasd"));

        return view;
    }

    public CompletableFuture<String> fetchBinaryString(DocumentReference currentDocument) {

        CompletableFuture<String> binaryStringFuture = new CompletableFuture<String>();

        currentDocument.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {

                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.d("BinaryString", "Binary String is");

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
}
