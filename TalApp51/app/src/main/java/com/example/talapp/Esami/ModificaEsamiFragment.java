package com.example.talapp.Esami;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.example.talapp.Utils.SnackbarRimuoviEvento;
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
import static com.example.talapp.Allegati.ImageAdapter.DOWNLOAD;
import static com.example.talapp.Allegati.ImageAdapter.UPLOAD;
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
    private static String id;
    private static final int ID_IMAGE = 1;
    private static List<String> uriList_esame_old;
    private List<Uri> mUriListUpload;
    private RecyclerView recyclerViewUploads;
    private ImageAdapter imageAdapterUpload;
    private List<Uri> mUriListDownload;
    private RecyclerView recyclerViewDownload;
    private ImageAdapter imageAdapterDownload;
    private StorageReference mStorageRef;
    private ProgressBar progressBarAllegato;
    private Button buttonUploadAllegato;
    private View root;


    public static String getIdEsame() {
        return id;
    }

    public static List<String> getListAllegati(){
        return uriList_esame_old;
    }

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
        mUriListUpload = new ArrayList<>();
        mUriListDownload = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_modifica_esami, container, false);
        this.root = root;
        id = getArguments().getString("ID");
        progressBarAllegato = root.findViewById(R.id.progressBarAllegato);

        //Recyclerview dei vecchi allegati
        recyclerViewDownload = root.findViewById(R.id.recyclerViewAllegati);
        imageAdapterDownload = new ImageAdapter(getContext(), DOWNLOAD);
        recyclerViewDownload.setAdapter(imageAdapterDownload);
        recyclerViewDownload.setLayoutManager(new GridLayoutManager(getContext(), 1));
        imageAdapterDownload.setImage(mUriListDownload);


        //Prendo le informazioni dell'esame
        esamiRef.document(id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            esame_old = task.getResult().getData();

                            //Prendo i vecchi esami
                            if(esame_old.containsKey(KEY_ALLEGATI)) {
                                Log.d("PRENDO LE INFO", "CONTIENE LA CHIAVE");
                                uriList_esame_old = (List<String>) esame_old.get(KEY_ALLEGATI);
                                DownloadUri(0);
                            }
                            else {
                                Log.d("PRENDO LE INFO", "NON CONTIENE LA CHIAVE");
                                uriList_esame_old = new ArrayList<>();
                                esame_old.put(KEY_ALLEGATI, uriList_esame_old);
                                esamiRef.document(id).update(esame_old);
                                Log.d("PRENDO LE INFO", "CHIAVE AGGIUNTA");
                            }

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

        //RecyclerView degli upload
        recyclerViewUploads = root.findViewById(R.id.recyclerViewUploads);
        imageAdapterUpload = new ImageAdapter(getContext(), UPLOAD);
        recyclerViewUploads.setAdapter(imageAdapterUpload);
        recyclerViewUploads.setLayoutManager(new GridLayoutManager(getContext(), 3));
        imageAdapterUpload.setImage(mUriListUpload);

        buttonUploadAllegato = root.findViewById(R.id.buttonUploadAllegato);
        buttonUploadAllegato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile(0);
            }
        });


        //Bottone aggiungi allegato
        Button buttonAggiungiAllegato = root.findViewById(R.id.buttonAggiungiAllegato);
        //Controllo se sono connessa a internet
        ConnectivityManager cm =
                (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected) buttonAggiungiAllegato.setVisibility(View.VISIBLE);
        else buttonAggiungiAllegato.setVisibility(View.GONE);

        buttonAggiungiAllegato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUriListUpload.size() <= 3) {
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
                SnackbarRimuoviEvento SU = new SnackbarRimuoviEvento();
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
            mUriListUpload.add(data.getData());
            Log.d("UPLOAD ARRAY", String.valueOf(mUriListUpload));
            imageAdapterUpload.setImage(mUriListUpload);
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
        if(mUriListUpload.size() > 0) {
            String idAllegato = String.valueOf(System.currentTimeMillis());
            StorageReference fileReference = mStorageRef.child(id + "/" +idAllegato+"."+ getFileExtension(mUriListUpload.get(tot)));

            fileReference.putFile(mUriListUpload.get(tot)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBarAllegato.setProgress(0);
                        }
                    }, 5000);

                    uriList_esame_old.add(id + "/" +idAllegato+"."+ getFileExtension(mUriListUpload.get(tot)));

                    if(tot+1 < mUriListUpload.size()){
                        uploadFile(tot+1);
                    }
                    else {
                        //uriList_esame_old.addAll(mUriListUpload);
                        //Log.d("UPLOAD", String.valueOf(uriList_esame_old));
                        esamiRef.document(id).update(KEY_ALLEGATI, uriList_esame_old);
                        mUriListUpload = new ArrayList<>();
                        progressBarAllegato.setVisibility(View.GONE);
                        buttonUploadAllegato.setVisibility(View.GONE);
                        recyclerViewUploads.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Allegato caricato", Toast.LENGTH_SHORT).show();
                        DownloadUri(0);
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
    }

    private void aggiornaEsame(){
        esamiRef.document(id).update(esame_old);
        Toast.makeText(getContext(), "Esame aggiornata", Toast.LENGTH_SHORT).show();
        esame_old = null;
        Navigation.findNavController(root).popBackStack();
    }

    private void DownloadUri(int tot){
        if(uriList_esame_old.size() > 0) {

            StorageReference storageReference = mStorageRef.child(uriList_esame_old.get(tot));
            Log.d("STORAGE REF", storageReference.toString());
            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {

                    mUriListDownload.add(uri);

                    if(tot+1 < uriList_esame_old.size()){
                        DownloadUri(tot+1);
                    }
                    else {
                        Log.d("DOWNLOAD URI", String.valueOf(mUriListDownload));
                        Log.d("DOWNLOAD URI", String.valueOf(mUriListDownload));
                        imageAdapterUpload.setImage(mUriListDownload);
                        recyclerViewDownload.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
        else{
            recyclerViewDownload.setVisibility(View.GONE);
        }
    }
}