package com.example.talapp.Esami;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.talapp.Allegati.ImageAdapter;
import com.example.talapp.R;
import com.example.talapp.Utils.SnackbarUndo;
import com.example.talapp.Utils.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.example.talapp.Esami.EsamiFragment.setTipoEsami;
import static com.example.talapp.HomeActivity.actionBar;
import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.ESAMI_LABORATORIO;
import static com.example.talapp.Utils.Util.KEY_ALLEGATI;
import static com.example.talapp.Utils.Util.KEY_DATA;
import static com.example.talapp.Utils.Util.KEY_ESITO;
import static com.example.talapp.Utils.Util.KEY_NOME;
import static com.example.talapp.Utils.Util.KEY_NOTE;


public class ModificaEsamiFragment extends Fragment {

    private static Map<String, Object> esame_old;
    private String id;
    private static final int ID_IMAGE = 1;
    private Uri mImageUri;
    private List<Uri> mUriList;
    private StorageReference mStorageRef;
    private ProgressBar progressBarAllegato;
    private RecyclerView recyclerViewUploads;
    private ImageAdapter imageAdapter;
    private View root;
    private Button buttonUploadAllegato;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Indipendence)));
        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
               esame_old = null;
               setTipoEsami(ESAMI_LABORATORIO);
               remove();
            }
        };

        mStorageRef = FirebaseStorage.getInstance().getReference("Allegati esami");
        mUriList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_modifica_esami, container, false);
        this.root = root;
        id = getArguments().getString("ID");
        progressBarAllegato = root.findViewById(R.id.progressBarAllegato);

        //Prendo le informazioni dell'esame
        esamiRef.document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            esame_old = task.getResult().getData();

                            TextView TXVdata = root.findViewById(R.id.TXVDataEsame);
                            TXVdata.setText(esame_old.get(KEY_NOME)+" del " + Util.TimestampToStringData((Timestamp) esame_old.get(KEY_DATA)) + " ore " +  Util.TimestampToOrario((Timestamp) esame_old.get(KEY_DATA)));

                            EditText ETEsito = root.findViewById(R.id.editTextEsito);
                            if (esame_old.containsKey(KEY_ESITO)) {
                                ETEsito.setText(String.valueOf(esame_old.get(KEY_ESITO)));
                            }

                            EditText ETNote = root.findViewById(R.id.ETNoteModEsame);
                            if (esame_old.containsKey(KEY_NOTE)) {
                                ETNote.setText((CharSequence) esame_old.get(KEY_NOTE));
                            }
                        }
                    }
                });

        recyclerViewUploads = root.findViewById(R.id.recyclerViewUploads);
        imageAdapter = new ImageAdapter(getContext());
        recyclerViewUploads.setAdapter(imageAdapter);
        recyclerViewUploads.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageAdapter.setImage(mUriList);

        buttonUploadAllegato = root.findViewById(R.id.buttonUploadAllegato);
        buttonUploadAllegato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(0);
            }
        });


        //Bottone aggiungi allegato
        Button buttonAggiungiAllegato = root.findViewById(R.id.buttonAggiungiAllegato);
        buttonAggiungiAllegato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUriList.size() <= 3) {
                    //Apre il file chooser e scelgo l'immagine
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, ID_IMAGE);
                }
                else Toast.makeText(getContext(), "Puoi aggiungere al massimo 3 file", Toast.LENGTH_SHORT).show();
            }
        });



        //Bottone modifica esame
        root.findViewById(R.id.buttonModificaTerapia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ETEsito = root.findViewById(R.id.editTextEsito);
                if (!ETEsito.getText().toString().isEmpty()) {
                    esame_old.put(KEY_ESITO, Double.parseDouble(ETEsito.getText().toString()));
                } else {
                    esame_old.put(KEY_ESITO, FieldValue.delete());
                }

                EditText ETNote = root.findViewById(R.id.ETNoteModEsame);
                if (!ETNote.getText().toString().isEmpty()) {
                    esame_old.put(KEY_NOTE, ETNote.getText().toString());
                } else {
                    esame_old.put(KEY_NOTE, FieldValue.delete());
                }

                aggiornaEsame();
                //uploadFile(0);
            }
        });

        //Bottone elimina esame
        root.findViewById(R.id.buttonEliminaTerapia).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                esamiRef.document(id).delete();
                Navigation.findNavController(root).popBackStack();
                SnackbarUndo SU = new SnackbarUndo();
                SU.rimuovi(esame_old, esamiRef);
                Snackbar snackbar = Snackbar.make(root, "Esame rimosso", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.setAction("Cancella operazione", SU);
                snackbar.show();
                Navigation.findNavController(root).popBackStack();
            }
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ID_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            mImageUri = data.getData();
            mUriList.add(mImageUri);
            imageAdapter.setImage(mUriList);
            buttonUploadAllegato.setVisibility(View.VISIBLE);
            recyclerViewUploads.setVisibility(View.VISIBLE);
            //uploadFile();
            //progressBarAllegato.setVisibility(View.INVISIBLE);
        }
    }

    //Prende l'estensione del file
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    //Carica il file
    private void uploadFile(int tot) {
        if(mUriList.size() > 0) {
            String idAllegato = String.valueOf(System.currentTimeMillis());
            StorageReference fileReference = mStorageRef.child(id + "/" +idAllegato+"."+ getFileExtension(mUriList.get(tot)));

            fileReference.putFile(mUriList.get(tot)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBarAllegato.setProgress(0);
                        }
                    }, 5000);

                    if(tot+1 < mUriList.size()){
                        uploadFile(tot+1);
                    }
                    else {
                        mUriList = new ArrayList<>();
                        progressBarAllegato.setVisibility(View.GONE);
                        buttonUploadAllegato.setVisibility(View.GONE);
                        recyclerViewUploads.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Allegato caricato", Toast.LENGTH_SHORT).show();
                        //aggiornaEsame();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Errore di caricamento", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBarAllegato.setVisibility(View.VISIBLE);
                    double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBarAllegato.setProgress((int) progress);
                }
            });
        }
        else {
            //aggiornaEsame();
        }

    }

    private void aggiornaEsame(){
        esamiRef.document(id).update(esame_old);
        Toast.makeText(getContext(), "Esame aggiornata", Toast.LENGTH_SHORT).show();
        esame_old = null;
        Navigation.findNavController(root).popBackStack();
    }

}