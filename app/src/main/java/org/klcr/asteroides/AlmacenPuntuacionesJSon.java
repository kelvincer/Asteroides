package org.klcr.asteroides;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kelvin on 7/12/2017.
 */

public class AlmacenPuntuacionesJSon implements AlmacenPuntuaciones {

    private static String FICHERO = Environment.getExternalStorageDirectory() + "/puntuacionesJson.json";

    Context context;

    public AlmacenPuntuacionesJSon(Context context) {
        this.context = context;
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerString();
        List<Puntuacion> puntuaciones = leerJSon(string);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = guardarJSon(puntuaciones);
        guardarString(string);
        Log.d("JSON", string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerString();
        List<Puntuacion> puntuaciones = leerJSon(string);
        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
        }
        return salida;
    }

    private String guardarJSon(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    private List<Puntuacion> leerJSon(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();

        if (string == null || string.isEmpty())
            return puntuaciones;

        try {
            JSONArray json_array = new JSONArray(string);
            for (int i = 0; i < json_array.length(); i++) {
                JSONObject objeto = json_array.getJSONObject(i);
                puntuaciones.add(new Puntuacion(objeto.getInt("puntos"),
                        objeto.getString("nombre"), objeto.getLong("fecha")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuaciones;
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