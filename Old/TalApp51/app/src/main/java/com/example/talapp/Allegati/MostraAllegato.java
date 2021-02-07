package com.example.talapp.Allegati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.talapp.Esami.ModificaEsamiFragment;
import com.example.talapp.Impostazioni.ImpostazioniActivity;
import com.example.talapp.R;
import com.example.talapp.Utils.SnackbarRimuoviAllegato;
import com.example.talapp.Utils.SnackbarRimuoviEvento;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.example.talapp.Notification.ForegroundService.esamiRef;
import static com.example.talapp.Utils.Util.KEY_ALLEGATI;
import static com.example.talapp.Utils.Util.navController;

public class MostraAllegato extends AppCompatActivity {
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostra_allegato);

        Bundle bundle = getIntent().getExtras();

        uri = Uri.parse(getIntent().getStringExtra("uri"));

        ImageView imageView = findViewById(R.id.imageViewAllegato);
        //imageView.setImageURI(myUri);
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

            Log.d("ELIMINO ALLEGATO", "CLICK");

            StorageReference mStorageRef= FirebaseStorage.getInstance().getReference("Allegati esami");
            StorageReference fileReference = mStorageRef.child(uri.toString());
           // String idEsame = fileReference.getParent().getName();
            fileReference.delete();
            Log.d("ELIMINO ALLEGATO", ModificaEsamiFragment.getIdEsame());
            Log.d("ELIMINO ALLEGATO", String.valueOf(ModificaEsamiFragment.getListAllegati()));
            Log.d("ELIMINO ALLEGATO", fileReference.toString());
            //Log.d("ELIMINO ALLEGATO", idEsame);

            //esamiRef.document(ModificaEsamiFragment.getIdEsame()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            //    @Override
            //    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
            //        List<String> list = (List<String>) task.getResult().getData().get(KEY_ALLEGATI);
            //        list.remove(uri);
            //        esamiRef.document(idEsame).update(KEY_ALLEGATI, list);
            //    }
            //});

            //SnackbarRimuoviAllegato SU = new SnackbarRimuoviAllegato();
            //SU.rimuovi(uri);
            //Snackbar snackbar = Snackbar.make(getCurrentFocus(), "Immagine rimossa", BaseTransientBottomBar.LENGTH_LONG);
            //snackbar.setAction("Cancella operazione", SU);
            //snackbar.show();
            ////Navigation.findNavController(root).popBackStack();
            //finish();
        }
        return super.onOptionsItemSelected(item);
    }
}