package org.klcr.asteroides;

import java.util.List;
import java.util.Vector;

/**
 * Created by Kelvin on 25/10/2017.
 */

public interface AlmacenPuntuaciones {

    public void guardarPuntuacion(int puntos, String nombre, long fecha);

    public List<String> listaPuntuaciones(int cantidad);
}
