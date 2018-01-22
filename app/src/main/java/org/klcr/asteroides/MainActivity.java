package org.klcr.asteroides;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button bAcercaDe;
    public static AlmacenPuntuaciones almacen;
    MediaPlayer mp;
    static final int ACTIV_JUEGO = 0;
    static final int REQUEST_CODE_PREFERENCIAS = 1;
    public static RequestQueue colaPeticiones;
    public static ImageLoader lectorImagenes;
    private int SOLICITUD_PERMISO = 100;
    private List<String> listaPermisos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bAcercaDe = (Button) findViewById(R.id.button03);
        bAcercaDe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                lanzarAcercaDe(null);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.giro_con_zoom);
                bAcercaDe.startAnimation(animation);
            }
        });


        mp = MediaPlayer.create(this, R.raw.audio);
        mp.start();

        almacen = new AlmacenPuntuacionesArray();

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                permitNetwork().build());

        colaPeticiones = Volley.newRequestQueue(this);
        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPermissions()) {
                solicitarPermiso(SOLICITUD_PERMISO);
            }
        }
    }

    public boolean checkPermissions() {

        listaPermisos.clear();

        int accesFineLocationPermiso = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (accesFineLocationPermiso != PackageManager.PERMISSION_GRANTED) {
            listaPermisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        return listaPermisos.isEmpty();
    }

    public void solicitarPermiso(int requestCode) {

        ActivityCompat.requestPermissions(this, listaPermisos.toArray((new String[listaPermisos.size()])), requestCode);
    }

    public void lanzarAcercaDe(View view) {
        Intent i = new Intent(this, AcercaDeActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menú ya está visible */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.acercaDe) {
            lanzarAcercaDe(null);
            return true;
        }

        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        if (mp.isPlaying()) {
            mp.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mp != null && !mp.isPlaying())
            mp.start();
    }

    public void lanzarPreferencias(View view) {

        Intent i = new Intent(this, PreferenciasActivity.class);
        startActivityForResult(i, REQUEST_CODE_PREFERENCIAS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITUD_PERMISO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso otorgado", Toast.LENGTH_SHORT).show();
                ;
            } else {
                Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void mostrarPreferencias(View view) {

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String s = "Música: " + pref.getBoolean("musica", true)
                + ", Gráficos: " + pref.getString("graficos", "?")
                + ", Número de fragmentos: " + pref.getString("fragmentos", "?")
                + ", Activar multijugador: " + pref.getBoolean("multijugador", true)
                + ", Máximo de jugadores: " + pref.getString("jugadores", "?")
                + ", Conexión: " + pref.getString("conexion", "?");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    public void lanzarPuntuaciones(View view) {
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mp != null) {
            int pos = mp.getCurrentPosition();
            outState.putInt("posicion", pos);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null && mp != null) {
            int pos = savedInstanceState.getInt("posicion");
            mp.seekTo(pos);
        }
    }

    public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        startActivityForResult(i, ACTIV_JUEGO);
    }

    public void lanzarDivisas(View view) {
        startActivity(new Intent(this, DivisasActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIV_JUEGO && resultCode == RESULT_OK && data != null) {
            int puntuacion = data.getExtras().getInt("puntuacion");
            String nombre = "Yo";
// Mejor leer nombre desde un AlertDialog.Builder o preferencias
            almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
            lanzarPuntuaciones(null);
        } else if (requestCode == REQUEST_CODE_PREFERENCIAS && resultCode == RESULT_OK && data != null) {

            int tipoAlmacenamiento = data.getExtras().getInt("tipoAlmacenamiento");
            Log.d("MainActivity", "" + tipoAlmacenamiento);

            switch (tipoAlmacenamiento) {

                case 0:
                    almacen = new AlmacenPuntuacionesArray();
                    break;
                case 1:
                    almacen = new AlmacenPuntuacionesPreferencias(this);
                    break;
                case 2:
                    almacen = new AlmacenPuntuacionesFicheroInterno(this);
                    break;
                case 3:
                    almacen = new AlmacenPuntuacionesFicheroExterno(this);
                    break;
                case 4:
                    almacen = new AlmacenPuntuacionesXML_SAX(this);
                    break;
                case 5:
                    almacen = new AlmacenPuntuacionesGSon(this);
                    break;
                case 6:
                    almacen = new AlmacenPuntuacionesJSon(this);
                    break;
                case 7:
                    almacen = new AlmacenPuntuacionesSQLite(this);
                    break;
                case 8:
                    almacen = new AlmacenPuntuacionesFicheroExtApl(this);
                    break;
                case 9:
                    almacen = new AlmacenPuntuacionesRecursoRaw(this);
                    break;
                case 10:
                    almacen = new AlmacenPuntuacionesRecursoAssets(this);
                    break;
                case 11:
                    almacen = new AlmacenPuntuacionesSQLiteRel(this);
                    break;
                case 12:
                    almacen = new AlmacenPuntuacionesSocket();
                    break;
                case 13:
                    almacen = new AlmacenPuntuacionesSW_PHP();
                    break;
                case 14:
                    almacen = new AlmacenPuntuacionesSW_PHP_AsyncTask(this);
                    break;
                case 15:
                    almacen = new AlmacenPuntuacionesSocket_AsyncTask(this);
                    break;
                case 16:
                    almacen = new AlmacenPuntuacionesProvider(this);
                    break;
                case 17:
                    almacen = new AlmacenPuntuacionesXML_DOM(this);
                    break;
                case 18:
                    almacen = new AlmacenPuntuacionesHostingSW_PHP();
                    break;
            }
        }
    }
}
