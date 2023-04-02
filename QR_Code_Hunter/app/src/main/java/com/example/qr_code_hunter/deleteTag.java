package com.example.qr_code_hunter;

import com.google.firebase.firestore.DocumentReference;

/**
 * This class is a wrapper class for the hashString of a qrCode. It marks which documents need to be deleted
 */
public class deleteTag {
    private DocumentReference hashString;

    private boolean isChecked;

    public DocumentReference getHashString() {
        return hashString;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    deleteTag(DocumentReference qrCodeDocumentReference) {
        hashString = qrCodeDocumentReference;
        this.isChecked = false;
    }
}
