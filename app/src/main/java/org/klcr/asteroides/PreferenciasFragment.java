package org.klcr.asteroides;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Kelvin on 25/10/2017.
 */

public class PreferenciasFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);

        ListPreference listPreference = (ListPreference) findPreference("almacenamientos");
        listPreference.setOnPreferenceChangeListener(this);

        ((PreferenciasActivity) getActivity()).setTipoAlmacenamiento(Integer.parseInt(listPreference.getValue()));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object o) {

        ((PreferenciasActivity) getActivity()).setTipoAlmacenamiento(Integer.parseInt(o.toString()));

        return true;
    }
}
