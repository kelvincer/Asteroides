package org.klcr.asteroides;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

/**
 * Created by Kelvin on 6/12/2017.
 */

public class AlmacenPuntuacionesRecursoAssets implements AlmacenPuntuaciones {

    private Context context;

    public AlmacenPuntuacionesRecursoAssets(Context context) {
        this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
    }

    public Vector<String> listaPuntuaciones(int cantidad) {
        Vector<String> result = new Vector<>();
        try {
            InputStream f = context.getAssets().open("carpeta/puntuaciones.txt");
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
