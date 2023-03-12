package com.example.qr_code_hunter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

public class Instruction_Dialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog (Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());












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
