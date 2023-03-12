package com.example.qr_code_hunter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Instruction_Dialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog (Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate the layout for the dialog
        View view = inflater.inflate(R.layout.instruction_dialog, null);

//        // Customize the dialog UI
//        TextView titleTextView = view.findViewById(R.id.title);
//        titleTextView.setText("Enter your name");

        // Add the view to the dialog builder
        builder.setView(view);

//        // Add action buttons
//        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User clicked OK button
//                    }
//                })
//                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    }
//                });

//        // Set the dialog title and message
//        builder.setTitle("My Dialog")
//                .setMessage("This is a custom dialog fragment");
//
//        // Add a positive button
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // Do something
//            }
//        });
//
//        // Add a negative button
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int id) {
//                // Do something
//            }
//        });
        return builder.create();
    }
}