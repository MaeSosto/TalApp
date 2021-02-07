package com.example.talapp.Utils;

import android.net.Uri;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.KEY_ALLEGATI;

public class SnackbarRimuoviAllegato implements View.OnClickListener {

    Uri uri;

    @Override
    public void onClick(View v) {
        //aggiungo allo storage
        StorageReference mStorageRef= FirebaseStorage.getInstance().getReference("Allegati esami");
        StorageReference fileReference = mStorageRef.child(uri.toString());
        fileReference.putFile(uri);

        String id = fileReference.getParent().getName();

        //Aggiungo al db
        esamiRef.document(id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                List<String> list = (List<String>) task.getResult().getData().get(KEY_ALLEGATI);
                list.add(uri.toString());

                esamiRef.document(id).update(KEY_ALLEGATI, list);
            }
        });
    }

    public void rimuovi(Uri uri) {
        this.uri = uri;
    }
}