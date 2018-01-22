package org.klcr.asteroides;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Kelvin on 25/10/2017.
 */

public class PreferenciasActivity extends AppCompatActivity {

    private int tipoAlmacenamiento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PreferenciasFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("tipoAlmacenamiento", tipoAlmacenamiento);
        setResult(Activity.RESULT_OK, returnIntent);
        super.onBackPressed();
    }

    public void setTipoAlmacenamiento(int tipo) {

        tipoAlmacenamiento = tipo;
        Log.d("Alamacenamiento", "" + tipoAlmacenamiento);
    }
}
