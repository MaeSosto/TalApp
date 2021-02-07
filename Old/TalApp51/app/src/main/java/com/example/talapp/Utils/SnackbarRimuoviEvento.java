package com.example.talapp.Utils;

import android.view.View;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.Map;

public class SnackbarRimuoviEvento implements View.OnClickListener {

    Map<String, Object> docRimosso;
    CollectionReference collectionReference;

    @Override
    public void onClick(View v) {
        collectionReference.add(docRimosso);
    }

    public void rimuovi(Map<String, Object> doc, CollectionReference collectionReference) {
        this.docRimosso = doc;
        this.collectionReference = collectionReference;
    }
}