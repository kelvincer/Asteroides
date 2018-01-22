package org.klcr.asteroides;

import android.content.Context;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by Kelvin on 7/12/2017.
 */

public class AlmacenPuntuacionesFicheroExtApl implements AlmacenPuntuaciones {

    private static String FICHERO = "puntuaciones.txt";
    private Context context;

    public AlmacenPuntuacionesFicheroExtApl(Context context) {
        this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {

        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "No puedo escribir en la memoria externa",
                    Toast.LENGTH_LONG).show();
            return;
        }

        File root = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (!root.exists()) {
            root.mkdir();
        }

        File ruta = new File(root, FICHERO);
        Log.d("RUTA", ruta.toString());

        try {
            FileOutputStream f = new FileOutputStream(ruta, true);
            String texto = puntos + " " + nombre + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    public Vector<String> listaPuntuaciones(int cantidad) {

        Vector<String> result = new Vector<>();

        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED) &&
                !stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, "No puedo leer en la memoria externa",
                    Toast.LENGTH_LONG).show();
            return result;
        }

        File root = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File ruta = new File(root, FICHERO);

        Log.d("RUTA", ruta.toString());

        if (!ruta.exists()) {
            Toast.makeText(context, "No existe el archivo", Toast.LENGTH_SHORT).show();
            return result;
        }

        try {
            FileInputStream f = new FileInputStream(ruta);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(f));
            int n = 0;
            String linea;
            do {
                linea = entrada.readLine();
                if (linea != null) {
                    result.add(linea);
                    n++;
                }
            } while (n < cantidad && linea != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }
}
