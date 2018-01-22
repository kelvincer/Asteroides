package org.klcr.asteroides;

import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DivisasActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DivisasActivity.class.getSimpleName();
    private static final String YOUR_APP_ID = "0867f8dc7b5245ada4a956b33832963c";
    Map<String, String> monedasMap = new LinkedHashMap<>();
    List<String> listMonedas = new ArrayList<>();
    Spinner entradaSpinner;
    Spinner salidaSpinner;
    EditText cantidadEtx;
    TextView txvResultado;
    Button btnConvertir;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divisas);

        setTitle(getString(R.string.convertidor));
        entradaSpinner = (Spinner) findViewById(R.id.entrada_spinner);
        salidaSpinner = (Spinner) findViewById(R.id.salida_spinner);
        cantidadEtx = (EditText) findViewById(R.id.etx_cantidad);
        txvResultado = (TextView) findViewById(R.id.txv_resultado);
        btnConvertir = (Button) findViewById(R.id.btn_convertir);
        btnConvertir.setOnClickListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ViewCompat.setTranslationZ(progressBar, 20);

        new GetMonedas().execute();
    }

    private void configurarSpinners() {

        for (Map.Entry<String, String> entry : monedasMap.entrySet()) {
            listMonedas.add(entry.getKey() + ": " + entry.getValue());
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMonedas);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salidaSpinner.setAdapter(spinnerArrayAdapter);
        entradaSpinner.setAdapter(spinnerArrayAdapter);
    }

    public String getElementByIndex(int index) {
        return (String) monedasMap.keySet().toArray()[index];
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_convertir) {
            getConversion();
        }
    }

    private void getConversion() {

        if (cantidadEtx.getText().toString().isEmpty()) {
            Toast.makeText(this, "Ingrese una cantidad", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        new Exchange().execute();
    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {


        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();

        return new JSONObject(jsonString);
    }

    private class GetMonedas extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            JSONObject jsonObject = null;
            try {
                jsonObject = getJSONObjectFromURL("https://openexchangerates.org/api/currencies.json");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            parse(jsonObject);
            configurarSpinners();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private class Exchange extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Void... voids) {
            String url = "https://openexchangerates.org/api/latest.json?app_id=" + YOUR_APP_ID;

            JSONObject jsonObject = null;
            try {
                jsonObject = getJSONObjectFromURL(url);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            String de = getElementByIndex(entradaSpinner.getSelectedItemPosition());
            String a = getElementByIndex(salidaSpinner.getSelectedItemPosition());

            try {
                JSONObject rateJsonObject = jsonObject.getJSONObject("rates");

                double entrada = rateJsonObject.getDouble(de);
                double salida = rateJsonObject.getDouble(a);
                double resultado = Double.parseDouble(cantidadEtx.getText().toString()) * salida / entrada;
                txvResultado.setText(String.format("%.3f", resultado));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void parse(JSONObject jsonObject) {

        try {
            String s = jsonObject.getString("AED");
            monedasMap.put("AED", s);
            s = jsonObject.getString("AFN");
            monedasMap.put("AFN", s);
            s = jsonObject.getString("ALL");
            monedasMap.put("ALL", s);
            s = jsonObject.getString("AMD");
            monedasMap.put("AMD", s);
            s = jsonObject.getString("ANG");
            monedasMap.put("ANG", s);
            s = jsonObject.getString("AOA");
            monedasMap.put("AOA", s);
            s = jsonObject.getString("ARS");
            monedasMap.put("ARS", s);
            s = jsonObject.getString("AUD");
            monedasMap.put("AUD", s);
            s = jsonObject.getString("AWG");
            monedasMap.put("AWG", s);
            s = jsonObject.getString("AZN");
            monedasMap.put("AZN", s);
            s = jsonObject.getString("BAM");
            monedasMap.put("BAM", s);
            s = jsonObject.getString("BBD");
            monedasMap.put("BBD", s);
            s = jsonObject.getString("BDT");
            monedasMap.put("BDT", s);
            s = jsonObject.getString("BGN");
            monedasMap.put("BGN", s);
            s = jsonObject.getString("BHD");
            monedasMap.put("BHD", s);
            s = jsonObject.getString("BIF");
            monedasMap.put("BIF", s);
            s = jsonObject.getString("BMD");
            monedasMap.put("BMD", s);
            s = jsonObject.getString("BND");
            monedasMap.put("BND", s);
            s = jsonObject.getString("BOB");
            monedasMap.put("BOB", s);
            s = jsonObject.getString("BRL");
            monedasMap.put("BRL", s);
            s = jsonObject.getString("BSD");
            monedasMap.put("BSD", s);
            s = jsonObject.getString("BTC");
            monedasMap.put("BTC", s);
            s = jsonObject.getString("BTN");
            monedasMap.put("BTN", s);
            s = jsonObject.getString("BWP");
            monedasMap.put("BWP", s);
            s = jsonObject.getString("BYN");
            monedasMap.put("BYN", s);
            s = jsonObject.getString("BZD");
            monedasMap.put("BZD", s);
            s = jsonObject.getString("CAD");
            monedasMap.put("CAD", s);
            s = jsonObject.getString("CDF");
            monedasMap.put("CDF", s);
            s = jsonObject.getString("CHF");
            monedasMap.put("CHF", s);
            s = jsonObject.getString("CLF");
            monedasMap.put("CLF", s);
            s = jsonObject.getString("CLP");
            monedasMap.put("CLP", s);
            s = jsonObject.getString("CNH");
            monedasMap.put("CNH", s);
            s = jsonObject.getString("CNY");
            monedasMap.put("CNY", s);
            s = jsonObject.getString("COP");
            monedasMap.put("COP", s);
            s = jsonObject.getString("CRC");
            monedasMap.put("CRC", s);
            s = jsonObject.getString("CUC");
            monedasMap.put("CUC", s);
            s = jsonObject.getString("CUP");
            monedasMap.put("CUP", s);
            s = jsonObject.getString("CVE");
            monedasMap.put("CVE", s);
            s = jsonObject.getString("CZK");
            monedasMap.put("CZK", s);
            s = jsonObject.getString("DJF");
            monedasMap.put("DJF", s);
            s = jsonObject.getString("DKK");
            monedasMap.put("DKK", s);
            s = jsonObject.getString("DOP");
            monedasMap.put("DOP", s);
            s = jsonObject.getString("DZD");
            monedasMap.put("DZD", s);
            s = jsonObject.getString("EGP");
            monedasMap.put("EGP", s);
            s = jsonObject.getString("ERN");
            monedasMap.put("ERN", s);
            s = jsonObject.getString("ETB");
            monedasMap.put("ETB", s);
            s = jsonObject.getString("EUR");
            monedasMap.put("EUR", s);
            s = jsonObject.getString("FJD");
            monedasMap.put("FJD", s);
            s = jsonObject.getString("FKP");
            monedasMap.put("FKP", s);
            s = jsonObject.getString("GBP");
            monedasMap.put("GBP", s);
            s = jsonObject.getString("GEL");
            monedasMap.put("GEL", s);
            s = jsonObject.getString("GGP");
            monedasMap.put("GGP", s);
            s = jsonObject.getString("GHS");
            monedasMap.put("GHS", s);
            s = jsonObject.getString("GIP");
            monedasMap.put("GIP", s);
            s = jsonObject.getString("GMD");
            monedasMap.put("GMD", s);
            s = jsonObject.getString("GNF");
            monedasMap.put("GNF", s);
            s = jsonObject.getString("GTQ");
            monedasMap.put("GTQ", s);
            s = jsonObject.getString("GYD");
            monedasMap.put("GYD", s);
            s = jsonObject.getString("HKD");
            monedasMap.put("HKD", s);
            s = jsonObject.getString("HNL");
            monedasMap.put("HNL", s);
            s = jsonObject.getString("HRK");
            monedasMap.put("HRK", s);
            s = jsonObject.getString("HTG");
            monedasMap.put("HTG", s);
            s = jsonObject.getString("HUF");
            monedasMap.put("HUF", s);
            s = jsonObject.getString("IDR");
            monedasMap.put("IDR", s);
            s = jsonObject.getString("ILS");
            monedasMap.put("ILS", s);
            s = jsonObject.getString("IMP");
            monedasMap.put("IMP", s);
            s = jsonObject.getString("INR");
            monedasMap.put("INR", s);
            s = jsonObject.getString("IQD");
            monedasMap.put("IQD", s);
            s = jsonObject.getString("IRR");
            monedasMap.put("IRR", s);
            s = jsonObject.getString("ISK");
            monedasMap.put("ISK", s);
            s = jsonObject.getString("JEP");
            monedasMap.put("JEP", s);
            s = jsonObject.getString("JMD");
            monedasMap.put("JMD", s);
            s = jsonObject.getString("JOD");
            monedasMap.put("JOD", s);
            s = jsonObject.getString("JPY");
            monedasMap.put("JPY", s);
            s = jsonObject.getString("KES");
            monedasMap.put("KES", s);
            s = jsonObject.getString("KGS");
            monedasMap.put("KGS", s);
            s = jsonObject.getString("KHR");
            monedasMap.put("KHR", s);
            s = jsonObject.getString("KMF");
            monedasMap.put("KMF", s);
            s = jsonObject.getString("KPW");
            monedasMap.put("KPW", s);
            s = jsonObject.getString("KRW");
            monedasMap.put("KRW", s);
            s = jsonObject.getString("KWD");
            monedasMap.put("KWD", s);
            s = jsonObject.getString("KYD");
            monedasMap.put("KYD", s);
            s = jsonObject.getString("KZT");
            monedasMap.put("KZT", s);
            s = jsonObject.getString("LAK");
            monedasMap.put("LAK", s);
            s = jsonObject.getString("LBP");
            monedasMap.put("LBP", s);
            s = jsonObject.getString("LKR");
            monedasMap.put("LKR", s);
            s = jsonObject.getString("LRD");
            monedasMap.put("LRD", s);
            s = jsonObject.getString("LSL");
            monedasMap.put("LSL", s);
            s = jsonObject.getString("LYD");
            monedasMap.put("LYD", s);
            s = jsonObject.getString("MAD");
            monedasMap.put("MAD", s);
            s = jsonObject.getString("MDL");
            monedasMap.put("MDL", s);
            s = jsonObject.getString("MGA");
            monedasMap.put("MGA", s);
            s = jsonObject.getString("MKD");
            monedasMap.put("MKD", s);
            s = jsonObject.getString("MMK");
            monedasMap.put("MMK", s);
            s = jsonObject.getString("MNT");
            monedasMap.put("MNT", s);
            s = jsonObject.getString("MOP");
            monedasMap.put("MOP", s);
            s = jsonObject.getString("MRO");
            monedasMap.put("MRO", s);
            s = jsonObject.getString("MUR");
            monedasMap.put("MUR", s);
            s = jsonObject.getString("MVR");
            monedasMap.put("MVR", s);
            s = jsonObject.getString("MWK");
            monedasMap.put("MWK", s);
            s = jsonObject.getString("MXN");
            monedasMap.put("MXN", s);
            s = jsonObject.getString("MYR");
            monedasMap.put("MYR", s);
            s = jsonObject.getString("MZN");
            monedasMap.put("MZN", s);
            s = jsonObject.getString("NAD");
            monedasMap.put("NAD", s);
            s = jsonObject.getString("NGN");
            monedasMap.put("NGN", s);
            s = jsonObject.getString("NIO");
            monedasMap.put("NIO", s);
            s = jsonObject.getString("NOK");
            monedasMap.put("NOK", s);
            s = jsonObject.getString("NPR");
            monedasMap.put("NPR", s);
            s = jsonObject.getString("NZD");
            monedasMap.put("NZD", s);
            s = jsonObject.getString("OMR");
            monedasMap.put("OMR", s);
            s = jsonObject.getString("PAB");
            monedasMap.put("PAB", s);
            s = jsonObject.getString("PEN");
            monedasMap.put("PEN", s);
            s = jsonObject.getString("PGK");
            monedasMap.put("PGK", s);
            s = jsonObject.getString("PHP");
            monedasMap.put("PHP", s);
            s = jsonObject.getString("PKR");
            monedasMap.put("PKR", s);
            s = jsonObject.getString("PLN");
            monedasMap.put("PLN", s);
            s = jsonObject.getString("PYG");
            monedasMap.put("PYG", s);
            s = jsonObject.getString("QAR");
            monedasMap.put("QAR", s);
            s = jsonObject.getString("RON");
            monedasMap.put("RON", s);
            s = jsonObject.getString("RSD");
            monedasMap.put("RSD", s);
            s = jsonObject.getString("RUB");
            monedasMap.put("RUB", s);
            s = jsonObject.getString("RWF");
            monedasMap.put("RWF", s);
            s = jsonObject.getString("SAR");
            monedasMap.put("SAR", s);
            s = jsonObject.getString("SBD");
            monedasMap.put("SBD", s);
            s = jsonObject.getString("SCR");
            monedasMap.put("SCR", s);
            s = jsonObject.getString("SDG");
            monedasMap.put("SDG", s);
            s = jsonObject.getString("SEK");
            monedasMap.put("SEK", s);
            s = jsonObject.getString("SGD");
            monedasMap.put("SGD", s);
            s = jsonObject.getString("SHP");
            monedasMap.put("SHP", s);
            s = jsonObject.getString("SEK");
            monedasMap.put("SEK", s);
            s = jsonObject.getString("SLL");
            monedasMap.put("SLL", s);
            s = jsonObject.getString("SOS");
            monedasMap.put("SOS", s);
            s = jsonObject.getString("SRD");
            monedasMap.put("SRD", s);
            s = jsonObject.getString("SSP");
            monedasMap.put("SSP", s);
            s = jsonObject.getString("STD");
            monedasMap.put("STD", s);
            s = jsonObject.getString("SVC");
            monedasMap.put("SVC", s);
            s = jsonObject.getString("SYP");
            monedasMap.put("SYP", s);
            s = jsonObject.getString("SZL");
            monedasMap.put("SZL", s);
            s = jsonObject.getString("THB");
            monedasMap.put("THB", s);
            s = jsonObject.getString("TJS");
            monedasMap.put("TJS", s);
            s = jsonObject.getString("TMT");
            monedasMap.put("TMT", s);
            s = jsonObject.getString("TND");
            monedasMap.put("TND", s);
            s = jsonObject.getString("TOP");
            monedasMap.put("TOP", s);
            s = jsonObject.getString("TRY");
            monedasMap.put("TRY", s);
            s = jsonObject.getString("TTD");
            monedasMap.put("TTD", s);
            s = jsonObject.getString("TWD");
            monedasMap.put("TWD", s);
            s = jsonObject.getString("TZS");
            monedasMap.put("TZS", s);
            s = jsonObject.getString("UAH");
            monedasMap.put("UAH", s);
            s = jsonObject.getString("UGX");
            monedasMap.put("UGX", s);
            s = jsonObject.getString("USD");
            monedasMap.put("USD", s);
            s = jsonObject.getString("UYU");
            monedasMap.put("UYU", s);
            s = jsonObject.getString("UZS");
            monedasMap.put("UZS", s);
            s = jsonObject.getString("VEF");
            monedasMap.put("VEF", s);
            s = jsonObject.getString("VND");
            monedasMap.put("VND", s);
            s = jsonObject.getString("VUV");
            monedasMap.put("VUV", s);
            s = jsonObject.getString("WST");
            monedasMap.put("WST", s);
            s = jsonObject.getString("XAF");
            monedasMap.put("XAF", s);
            s = jsonObject.getString("XAG");
            monedasMap.put("XAG", s);
            s = jsonObject.getString("XAU");
            monedasMap.put("XAU", s);
            s = jsonObject.getString("XCD");
            monedasMap.put("XCD", s);
            s = jsonObject.getString("XDR");
            monedasMap.put("XDR", s);
            s = jsonObject.getString("XOF");
            monedasMap.put("XOF", s);
            s = jsonObject.getString("XPD");
            monedasMap.put("XPD", s);
            s = jsonObject.getString("XPF");
            monedasMap.put("XPF", s);
            s = jsonObject.getString("XPT");
            monedasMap.put("XPT", s);
            s = jsonObject.getString("YER");
            monedasMap.put("YER", s);
            s = jsonObject.getString("ZAR");
            monedasMap.put("ZAR", s);
            s = jsonObject.getString("ZMW");
            monedasMap.put("ZMW", s);
            s = jsonObject.getString("ZWL");
            monedasMap.put("ZWL", s);


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
