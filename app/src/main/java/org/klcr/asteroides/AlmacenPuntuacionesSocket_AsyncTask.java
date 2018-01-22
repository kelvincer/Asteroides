package org.klcr.asteroides;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kelvin on 11/12/2017.
 */

public class AlmacenPuntuacionesSocket_AsyncTask implements AlmacenPuntuaciones {

    private Context contexto;

    public AlmacenPuntuacionesSocket_AsyncTask(Context contexto) {
        this.contexto = contexto;
    }

    public List<String> listaPuntuaciones(int cantidad) {
        try {
            AlmacenPuntuacionesSocket_AsyncTask.TareaLista tarea = new AlmacenPuntuacionesSocket_AsyncTask.TareaLista();
            tarea.execute(cantidad);
            return tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(contexto, "Tiempo excedido al conectar",
                    Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(contexto, "Error al conectar con servidor",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(contexto, "Error con tarea asíncrona",
                    Toast.LENGTH_LONG).show();
        }
        return new ArrayList<String>();
    }

    private class TareaLista extends AsyncTask<Integer, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Integer... cantidad) {

            List<String> result = new ArrayList<String>();
            try {
                Socket sk = new Socket("158.42.146.127", 1234);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(sk.getInputStream()));
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(sk.getOutputStream()), true);
                salida.println("PUNTUACIONES");
                int n = 0;
                String respuesta;
                do {
                    respuesta = entrada.readLine();
                    if (respuesta != null) {
                        result.add(respuesta);
                        n++;
                    }
                } while (n < cantidad[0] && respuesta != null);
                sk.close();
            } catch (Exception e) {
                Log.e("Asteroides", e.toString(), e);
            }
            return result;
        }
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        try {
            AlmacenPuntuacionesSocket_AsyncTask.TareaGuardar tarea = new AlmacenPuntuacionesSocket_AsyncTask.TareaGuardar();
            tarea.execute(String.valueOf(puntos), nombre,
                    String.valueOf(fecha));
            tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(contexto, "Tiempo excedido al conectar",
                    Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(contexto, "Error al conectar con servidor",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(contexto, "Error con tarea asíncrona", Toast.LENGTH_LONG).show();
        }
    }

    private class TareaGuardar extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {

            try {
                Socket sk = new Socket("158.42.146.127", 1234);
                //Socket sk = new Socket("158.42.146.127", 7);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(sk.getInputStream()));
                PrintWriter salida = new PrintWriter(
                        new OutputStreamWriter(sk.getOutputStream()), true);
                salida.println(param[0] + " " + param[1]);
                String respuesta = entrada.readLine();
                if (!respuesta.equals("OK")) {
                    Log.e("Asteroides", "Error: respuesta de servidor incorrecta");
                }
                sk.close();
            } catch (Exception e) {
                Log.e("Asteroides", e.toString(), e);
            }

            return null;
        }
    }
}
