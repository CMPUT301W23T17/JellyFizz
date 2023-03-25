package com.example.qr_code_hunter;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class CommentSection implements Parcelable {
    private String username;
    private String comment;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public CommentSection(){}

    public CommentSection(String username, String comment) {
        this.username = username;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(username);
        out.writeString(comment);
    }

    public static final Creator<CommentSection> CREATOR = new Creator<CommentSection>() {
        @Override
        public CommentSection createFromParcel(Parcel in) {
            return new CommentSection(in);
        }

        @Override
        public CommentSection[] newArray(int size) {
            return new CommentSection[size];
        }
    };

    private CommentSection(Parcel in) {
        username = in.readString();
        comment = in.readString();
    }

    public interface ArrangeCommentSectionCallback {
        void onArrangeCommentSectionComplete(ArrayList<CommentSection> comments, ArrayList<CommentSection> players);
    }

    /**
     * Get the comments of all players who scanned it and add to 2 lists: one for comments, other
     * for players' name
     * @param callback stops the listener when task is complete
     * @param qrRef document reference to qr code to be displayed
     */
    public void arrangeCommentSection(DocumentReference qrRef, ArrangeCommentSectionCallback callback) {
        ArrayList<CommentSection> commentList = new ArrayList<>();
        ArrayList<CommentSection> playerList = new ArrayList<>();
        db.collection("scannedBy")
                .whereEqualTo("qrCodeScanned",qrRef)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Document ref for player always in format Players/Username
                            String username = document.getDocumentReference("Player").getPath().substring(8);
                            String comment = document.getString("Comment");
                            // Add new player into player list
                            CommentSection playerObj = new CommentSection(username, null);
                            playerList.add(playerObj);
                            // Add comment object into comment list only if that player commented
                            if (!comment.isEmpty()) {
                                CommentSection commentObj = new CommentSection(username, comment);
                                commentList.add(commentObj);
                            }
                        }
                    } else {
                        Log.e("TAG", "Error getting documents ", task.getException());
                    }
                    callback.onArrangeCommentSectionComplete(commentList, playerList);
                });
    }
}
