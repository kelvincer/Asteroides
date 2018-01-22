package org.klcr.asteroides;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Kelvin on 25/10/2017.
 */

public class Juego extends Activity {

    private VistaJuego vistaJuego;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);

        vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
        vistaJuego.setPadre(this);
    }

    @Override
    protected void onPause() {
        vistaJuego.getThread().pausar();
        vistaJuego.desactivarSensores();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();
        vistaJuego.activarSensores();
    }

    @Override
    protected void onDestroy() {
        vistaJuego.getThread().detener();
        super.onDestroy();
    }
}
