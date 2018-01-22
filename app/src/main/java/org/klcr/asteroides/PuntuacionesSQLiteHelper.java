package org.klcr.asteroides;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kelvin on 11/12/2017.
 */

public class PuntuacionesSQLiteHelper extends SQLiteOpenHelper {

    public PuntuacionesSQLiteHelper(Context context) {
        super(context, "puntuaciones", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE puntuaciones ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "puntos INTEGER, "
                + "nombre TEXT, "
                + "fecha BIGINT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
// En caso de una nueva versión habría que actualizar las tablas
    }
}
