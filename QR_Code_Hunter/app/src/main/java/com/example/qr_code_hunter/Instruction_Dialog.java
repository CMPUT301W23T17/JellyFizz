package com.example.qr_code_hunter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

// How to create Instruction Dialog in Fragment
// Link: https://chat.openai.com/
public class Instruction_Dialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog (Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate the layout for the dialog
        View view = inflater.inflate(R.layout.instruction_dialog, null);
        builder.setView(view);
        return builder.create();
    }
}