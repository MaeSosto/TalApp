package com.example.talapp.Allegati;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.talapp.Esami.ModificaEsamiFragment;
import com.example.talapp.R;
import com.example.talapp.Utils.SnackbarRimuoviAllegato;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.KEY_ALLEGATI;
import static com.example.talapp.Utils.Util.Utente;
import static com.example.talapp.Utils.Util.navController;

public class MostraAllegato extends AppCompatActivity {
    private Uri uri;
    private String idFile;
    private int filePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_allegato);
        uri = Uri.parse(getIntent().getStringExtra("uri"));
        filePos = getIntent().getIntExtra("pos", 0);
        ImageView imageView = findViewById(R.id.imageViewAllegato);
        Picasso.get().load(uri).into(imageView);
    }

    @Override
    public boolean onSupportNavigateUp() {
        navController.navigateUp();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image, menu);
        return true;
    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
        //Log.i("MENU", "CLICK");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.eliminaAllegato) {

            //Log.d("ELIMINO ALLEGATO", "CLICK");

            StorageReference mStorageRef= FirebaseStorage.getInstance().getReference(Utente);
            StorageReference fileReference = mStorageRef.child(ModificaEsamiFragment.getListAllegati().get(filePos));
           // String idEsame = fileReference.getParent().getName();
            fileReference.delete().addOnCompleteListener(task -> {
                //Log.d("ELIMINO ALLEGATO", ModificaEsamiFragment.getIdEsame());
                //Log.d("ELIMINO ALLEGATO", String.valueOf(ModificaEsamiFragment.getListAllegati()));
                //Log.d("ELIMINO ALLEGATO", fileReference.toString());
                //Log.d("ELIMINO ALLEGATO", idEsame);

                esamiRef.document(ModificaEsamiFragment.getIdEsame()).get().addOnCompleteListener(task1 -> {
                    Log.d("LISTA ALLEGATI", String.valueOf(ModificaEsamiFragment.getListAllegati()));
                    List<String> list = ModificaEsamiFragment.getListAllegati();
                    list.remove(filePos);
                    ModificaEsamiFragment.setListAllegati(list);
                    Log.d("LISTA ALLEGATI", String.valueOf(ModificaEsamiFragment.getListAllegati()));
                    esamiRef.document(ModificaEsamiFragment.getIdEsame()).update(KEY_ALLEGATI, list).addOnCompleteListener(task11 -> {
                        SnackbarRimuoviAllegato SU = new SnackbarRimuoviAllegato();
                        SU.rimuovi(uri);
                        //Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Immagine rimossa", BaseTransientBottomBar.LENGTH_LONG);
                        //snackbar.setAction("Cancella operazione", SU);
                        //snackbar.show();
                        //Navigation.findNavController(root).popBackStack();
                        finish();
                    });
                });
            });
        }
        return super.onOptionsItemSelected(item);
    }
}