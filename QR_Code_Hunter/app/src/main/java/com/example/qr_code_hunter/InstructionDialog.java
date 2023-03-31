package com.example.qr_code_hunter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatDialogFragment;

public class InstructionDialog extends AppCompatDialogFragment {
    public Dialog onCreateDialog (Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate the layout for the dialog
        View view = inflater.inflate(R.layout.instruction_dialog, null);

        // Add the view to the dialog builder
        builder.setView(view);

        return builder.create();
    }
}