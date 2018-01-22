package org.klcr.asteroides;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on 6/12/2017.
 */

public class AlmacenPuntuacionesGSon implements AlmacenPuntuaciones {

    private static String FICHERO = Environment.getExternalStorageDirectory() + "/puntuaciones.json";

    private Gson gson = new Gson();
    private Type type = new TypeToken<Clase>() {
    }.getType();

    Context context;

    public AlmacenPuntuacionesGSon(Context context) {
        this.context = context;
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerString();
        Log.d("GSON GUARDAR", "" + string);
        Clase objeto;
        if (string == null || string.isEmpty()) {
            objeto = new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }
        objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = gson.toJson(objeto, type);

        Log.d("GSON LAST", "" + string);
        guardarString(string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerString();
        Log.d("GSON LEER", "" + string);
        Clase objeto;
        if (string == null || string.isEmpty()) {
            objeto = new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }

        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }

        return salida;
    }

    public class Clase {
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;
    }

    private String leerString() {

        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED) &&
                !stadoSD.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, "No puedo leer en la memoria externa",
                    Toast.LENGTH_LONG).show();
            return null;
        }
        try {
            File file = new File(FICHERO);
            if (!file.exists())
                file.createNewFile();
            FileInputStream f = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(f));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            f.close();
            return sb.toString();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return null;
    }

    private void guardarString(String string) {

        String stadoSD = Environment.getExternalStorageState();
        if (!stadoSD.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "No puedo escribir en la memoria externa",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            File file = new File(FICHERO);
            Writer output = new BufferedWriter(new FileWriter(file));
            output.write(string);
            output.close();
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
