package org.klcr.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Kelvin on 25/10/2017.
 */

public class VistaJuego extends View implements SensorEventListener {
    // //// ASTEROIDES //////
    private List<Grafico> asteroides; // Lista con los Asteroides
    private int numAsteroides = 5; // Número inicial de asteroides
    private int numFragmentos = 3; // Fragmentos en que se divide

    // //// NAVE //////
    private Grafico nave; // Gráfico de la nave
    private int giroNave; // Incremento de dirección
    private double aceleracionNave; // aumento de velocidad
    private static final int MAX_VELOCIDAD_NAVE = 20;
    // Incremento estándar de giro y aceleración
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;

    // //// THREAD Y TIEMPO //////
// Thread encargado de procesar el juego
    private ThreadJuego thread = new ThreadJuego();
    // Cada cuanto queremos procesar cambios (ms)
    private static int PERIODO_PROCESO = 50;
    // Cuando se realizó el último proceso
    private long ultimoProceso = 0;

    // //// MISIL //////
    private Vector<Grafico> misiles;
    private static int PASO_VELOCIDAD_MISIL = 12;
    //private boolean misilActivo = false;
    //private int tiempoMisil;
    private Vector<Integer> tiempoMisiles;

    SensorManager mSensorManager;
    SharedPreferences pref;

    // //// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;
    Drawable drawableMisil;

    private int puntuacion = 0;
    private Activity padre;


    public void setPadre(Activity padre) {
        this.padre = padre;
    }

    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Drawable drawableNave, drawableAsteroide;
        pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (pref.getString("graficos", "1").equals("0")) {
            Path pathAsteroide = new Path();
            pathAsteroide.moveTo((float) 0.3, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.0);
            pathAsteroide.lineTo((float) 0.6, (float) 0.3);
            pathAsteroide.lineTo((float) 0.8, (float) 0.2);
            pathAsteroide.lineTo((float) 1.0, (float) 0.4);
            pathAsteroide.lineTo((float) 0.8, (float) 0.6);
            pathAsteroide.lineTo((float) 0.9, (float) 0.9);
            pathAsteroide.lineTo((float) 0.8, (float) 1.0);
            pathAsteroide.lineTo((float) 0.4, (float) 1.0);
            pathAsteroide.lineTo((float) 0.0, (float) 0.6);
            pathAsteroide.lineTo((float) 0.0, (float) 0.2);
            pathAsteroide.lineTo((float) 0.3, (float) 0.0);
            ShapeDrawable dAsteroide = new ShapeDrawable(
                    new PathShape(pathAsteroide, 1, 1));
            dAsteroide.getPaint().setColor(Color.WHITE);
            dAsteroide.getPaint().setStyle(Paint.Style.STROKE);
            dAsteroide.setIntrinsicWidth(50);
            dAsteroide.setIntrinsicHeight(50);
            drawableAsteroide = dAsteroide;
            setBackgroundColor(Color.BLACK);

            asteroides = new ArrayList<Grafico>();
            for (int i = 0; i < numAsteroides; i++) {
                Grafico asteroide = new Grafico(this, drawableAsteroide);
                asteroide.setIncY(Math.random() * 4 - 2);
                asteroide.setIncX(Math.random() * 4 - 2);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                asteroides.add(asteroide);
            }
        } else if (pref.getString("graficos", "1").equals("3")) {

            TypedArray images = getResources().obtainTypedArray(R.array.loading_images);
            asteroides = new ArrayList<Grafico>();
            for (int i = 0; i < numAsteroides; i++) {
                Random r = new Random();
                int choice = r.nextInt(3);
                drawableAsteroide = ContextCompat.getDrawable(getContext(), images.getResourceId(choice, R.drawable.ic_asteroid1));
                Grafico asteroide = new Grafico(this, drawableAsteroide);
                asteroide.setIncY(Math.random() * 4 - 2);
                asteroide.setIncX(Math.random() * 4 - 2);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                asteroides.add(asteroide);
            }

            images.recycle();
        } else {
            drawableAsteroide = ContextCompat.getDrawable(context, R.drawable.asteroide1);
            asteroides = new ArrayList<Grafico>();
            for (int i = 0; i < numAsteroides; i++) {
                Grafico asteroide = new Grafico(this, drawableAsteroide);
                asteroide.setIncY(Math.random() * 4 - 2);
                asteroide.setIncX(Math.random() * 4 - 2);
                asteroide.setAngulo((int) (Math.random() * 360));
                asteroide.setRotacion((int) (Math.random() * 8 - 4));
                asteroides.add(asteroide);
            }
        }


        if (pref.getString("graficos", "1").equals("0")) {
            Path path = new Path();
            path.moveTo(0f, 0f);
            path.lineTo(0f, 10f);
            path.moveTo(0f, 10f);
            path.lineTo(10f, 5f);
            path.moveTo(10f, 5f);
            path.lineTo(0f, 0f);
            path.close();

            ShapeDrawable dnave = new ShapeDrawable(new PathShape(path, 1, 1));
            dnave.getPaint().setColor(android.graphics.Color.RED);
            dnave.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
            dnave.getPaint().setStrokeWidth(1f);
            dnave.setIntrinsicWidth(10);
            dnave.setIntrinsicHeight(10);

            drawableNave = dnave;
        } else if (pref.getString("graficos", "1").equals("3")) {
            drawableNave = ContextCompat.getDrawable(context, R.drawable.ic_spaceship);
        } else {

            drawableNave = ContextCompat.getDrawable(context, R.drawable.nave);
        }
        nave = new Grafico(this, drawableNave);

        if (pref.getString("graficos", "1").equals("0")) {
            ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
            dMisil.getPaint().setColor(Color.WHITE);
            dMisil.getPaint().setStyle(Paint.Style.STROKE);
            dMisil.setIntrinsicWidth(15);
            dMisil.setIntrinsicHeight(3);
            drawableMisil = dMisil;
        } else {
            drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
        }


        mSensorManager = (SensorManager)
                context.getSystemService(Context.SENSOR_SERVICE);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);

        misiles = new Vector<>();
        tiempoMisiles = new Vector<>();
    }

    @Override
    protected void onSizeChanged(int ancho, int alto,
                                 int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);

        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);

// Una vez que conocemos nuestro ancho y alto.
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }

        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }

        nave.dibujaGrafico(canvas);

        for (int i = 0; i < misiles.size(); i++) {
            misiles.get(i).dibujaGrafico(canvas);
        }
    }

    protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return; // Salir si el período de proceso no se ha cumplido.
        }
// Para una ejecución en tiempo real calculamos el factor de movimiento
        double factorMov = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora; // Para la próxima vez
// Actualizamos velocidad y dirección de la nave a partir de
// giroNave y aceleracionNave (según la entrada del jugador)
        nave.setAngulo((int) (nave.getAngulo() + giroNave * factorMov));
        double nIncX = nave.getIncX() + aceleracionNave *
                Math.cos(Math.toRadians(nave.getAngulo())) * factorMov;
        double nIncY = nave.getIncY() + aceleracionNave *
                Math.sin(Math.toRadians(nave.getAngulo())) * factorMov;
// Actualizamos si el módulo de la velocidad no excede el máximo
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIDAD_NAVE) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(factorMov); // Actualizamos posición
        for (Grafico asteroide : asteroides) {
            asteroide.incrementaPos(factorMov);
        }

        for (int i = 0; i < misiles.size(); i++) {
            misiles.get(i).incrementaPos(factorMov);
            tiempoMisiles.set(i, (int) (tiempoMisiles.get(i) - factorMov));
            if (tiempoMisiles.get(i) < 0) {
                //misilActivo = false;
                misiles.remove(i);
                tiempoMisiles.remove(i);
            } else {
                for (int j = 0; j < asteroides.size(); j++) {
                    if (misiles.get(i).verificaColision(asteroides.get(j))) {
                        destruyeAsteroide(j);
                        misiles.remove(i);
                        tiempoMisiles.remove(i);
                        break;
                    }
                }
            }
        }

        for (Grafico asteroide : asteroides) {
            if (asteroide.verificaColision(nave)) {
                salir();
            }
        }
    }

    private boolean hayValorInicial = false;
    private float valorInicial;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float valor = event.values[1];
        if (!hayValorInicial) {
            valorInicial = valor;
            hayValorInicial = true;
        }
        giroNave = (int) (valor - valorInicial) / 3;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    class ThreadJuego extends Thread {
        private boolean pausa, corriendo;

        public synchronized void pausar() {
            pausa = true;
        }

        public synchronized void reanudar() {
            pausa = false;
            notify();
        }

        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }

        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
// Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                break;
            default:
// Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
// Suponemos que vamos a procesar la pulsación
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
// Si estamos aquí, no hay pulsación que nos interese
                procesada = false;
                break;
        }
        return procesada;
    }

    private float mX = 0, mY = 0;
    private boolean disparo = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) {
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx < 6 && dy > 6) {
                    aceleracionNave = aceleracionNave
                            + Math.abs(Math.round((mY - y) / 25));

                    disparo = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo) {
                    activaMisil();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    private void destruyeAsteroide(int i) {

        synchronized (asteroides) {
            asteroides.remove(i);
        }
        this.postInvalidate();
        soundPool.play(idExplosion, 1, 1, 0, 0, 1);

        puntuacion += 1000;

        if (asteroides.isEmpty()) {
            salir();
        }
    }

    private void activaMisil() {
        Grafico misil = new Grafico(this, drawableMisil);
        misil.setCenX(nave.getCenX());
        misil.setCenY(nave.getCenY());
        misil.setAngulo(nave.getAngulo());
        misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) *
                PASO_VELOCIDAD_MISIL);
        misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) *
                PASO_VELOCIDAD_MISIL);
        int tiempoMisil = (int) Math.min(this.getWidth() / Math.abs(misil.
                getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;

        tiempoMisiles.add(tiempoMisil);
        misiles.add(misil);

        soundPool.play(idDisparo, 1, 1, 1, 0, 1);
    }

    public ThreadJuego getThread() {
        return thread;
    }

    public void activarSensores() {

        if (pref.getBoolean("sensor", true)) {

            List<Sensor> listSensors = mSensorManager.getSensorList(
                    Sensor.TYPE_ORIENTATION);
            if (!listSensors.isEmpty()) {
                Sensor orientationSensor = listSensors.get(0);
                mSensorManager.registerListener(this, orientationSensor,
                        SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void desactivarSensores() {

        if (pref.getBoolean("sensor", true)) {
            mSensorManager.unregisterListener(this);
        }
    }

    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }
}
