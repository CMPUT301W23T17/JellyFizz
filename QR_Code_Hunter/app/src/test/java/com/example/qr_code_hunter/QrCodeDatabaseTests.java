package com.example.qr_code_hunter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.CompletableFuture;

import org.junit.Test;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class QrCodeDatabaseTests {
    @Test
    public void testGetScoreCode() throws Exception {
        // mock the document reference
        DocumentReference docRef = mock(DocumentReference.class);

        // mock the document snapshot
        DocumentSnapshot documentSnapshot = mock(DocumentSnapshot.class);
        when(documentSnapshot.exists()).thenReturn(true);
        when(documentSnapshot.getLong("Score")).thenReturn(10L);

        // mock the task that is returned by docRef.get()
        Task<DocumentSnapshot> task = mock(Task.class);
        when(task.addOnSuccessListener(any())).thenReturn(task);
        when(task.addOnFailureListener(any())).thenReturn(task);
        when(task.getResult()).thenReturn(documentSnapshot);

        // mock the firebase exception that can be thrown by docRef.get()
        FirebaseFirestoreException exception = mock(FirebaseFirestoreException.class);
        when(exception.getMessage()).thenReturn("Test exception");

        // mock the task that is returned by docRef.get() when it fails
        Task<DocumentSnapshot> taskWithError = mock(Task.class);
        when(taskWithError.addOnSuccessListener(any())).thenReturn(taskWithError);
        when(taskWithError.addOnFailureListener(any())).thenReturn(taskWithError);
        when(taskWithError.getException()).thenReturn(exception);

        // mock the behavior of docRef.get()
        when(docRef.get()).thenReturn(task);
        when(docRef.get().addOnSuccessListener(any())).thenReturn(task);
        when(docRef.get().addOnFailureListener(any())).thenReturn(taskWithError);

        // call the method and test the result
        CompletableFuture<Integer> future = qrCodeList.getScoreCode(docRef);
        assertTrue(future.isDone());
        assertEquals(10, (int) future.get());
    }
}
