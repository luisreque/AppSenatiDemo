package com.demo.appsenatidemo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.net.http.*;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.demo.appsenatidemo.entities.AppUser;
import com.demo.appsenatidemo.entities.Visit;
import com.demo.appsenatidemo.utils.DateUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.demo.appsenatidemo.utils.GlobalVariables;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class VisitDetailActivity extends AppCompatActivity {

    private TextView tvCompany;
    private TextView tvStudent;
    private TextView tvCompanyAddress;
    private TextView tvHorario;
    private TextView tvEstado;
    private Button btnCambiarEstado;
    //private WebView mWebView;
    int intIdVisita=0;
    GoogleMap googleMap;
    //http://hmkcode.com/run-google-map-v2-on-android-emulator/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_detail);

        btnCambiarEstado = (Button) findViewById(R.id.btnCambiarEstado);
        setTitle("Detalle de visita");

        Bundle bundle = getIntent().getExtras();
        String strIdVisita = bundle.getString("VISIT_ID");
        intIdVisita = Integer.parseInt(strIdVisita);

        createMapView();

        if(strIdVisita!=null){
            ReadWeatherJSONFeedTask objReadWeatherJSONFeedTask = new ReadWeatherJSONFeedTask();
            objReadWeatherJSONFeedTask.execute("https://dam-atlas-luisreque.c9.io/visitas/get_by_id?pIntIdVisit="+strIdVisita);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_visit_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            //Yes button clicked
                            GlobalVariables.objAppUser = null;
                            Intent intent = new Intent(VisitDetailActivity.this, LoginActivity.class);
                            startActivityForResult(intent, 100);
                            dialog.dismiss();
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            dialog.dismiss();
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("¿Está seguro que desea cerrar sesión?").setPositiveButton("Sí", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void iniciarEvaluacion(View view) {


        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
/*
                        ProgressDialog objProgressDialog = new ProgressDialog(VisitDetailActivity.this);
                        objProgressDialog.setMessage("Cargando...");
                        objProgressDialog.setCancelable(false);
                        objProgressDialog.show();
*/
                        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                                Request.Method.GET, "http://dam-atlas-luisreque.c9.io/visitas/cambio_estado?id="+intIdVisita+"&estado=2", null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // the response is already constructed as a JSONObject!
                                try {

                                    int intIdEstado = response.getInt("visit_status_id");

                                    if(intIdEstado==1){
                                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(VisitDetailActivity.this);
                                        dlgAlert.setMessage("Ocurrió un error al cambiar el estado");
                                        dlgAlert.setTitle("App Senati");
                                        dlgAlert.setPositiveButton("OK", null);
                                        dlgAlert.setCancelable(true);
                                        dlgAlert.create().show();
                                    }
                                    else{
                                        tvEstado.setText("Inicia evaluación");
                                        btnCambiarEstado.setEnabled(false);
                                        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(VisitDetailActivity.this);
                                        dlgAlert.setMessage("Se grabó satisfactoriamente");
                                        dlgAlert.setTitle("App Senati");
                                        dlgAlert.setPositiveButton("OK", null);
                                        dlgAlert.setCancelable(true);
                                        dlgAlert.create().show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }
                        );
                        //objProgressDialog.hide();
                        Volley.newRequestQueue(VisitDetailActivity.this).add(jsonRequest);

                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Está seguro que desea iniciar la evaluación?").setPositiveButton("Sí", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public String getVisitsJSON(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
                reader.close();
            } else {
            }
        } catch (Exception e) {
            String strError = e.toString();
            strError=strError;
        }

        String strResultado = stringBuilder.toString();

        return strResultado;
    }


    private class ReadWeatherJSONFeedTask extends AsyncTask<String, Void, String> {

        String strResultWS="-999";
        ProgressDialog dialog;

        @Override
        protected String doInBackground(String... urls) {
            strResultWS= getVisitsJSON(urls[0]);
            return strResultWS;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(VisitDetailActivity.this);
            dialog.setMessage("Cargando...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            if(strResultWS!="" && strResultWS!="-999"){
                try{
                    JSONArray objJSONArray = new JSONArray(strResultWS);

                    List<Visit> lstVisits = new ArrayList<Visit>();

                    if(objJSONArray.length() == 0){
                        //tvSinVisitas.setVisibility(View.VISIBLE);
                    }
                    else{
                        //tvSinVisitas.setVisibility(View.INVISIBLE);
                        JSONObject objJSONVisit = objJSONArray.getJSONObject(0);

                        tvCompany = (TextView) findViewById(R.id.tvCompany);
                        tvStudent = (TextView) findViewById(R.id.tvStudent);
                        tvCompanyAddress = (TextView) findViewById(R.id.tvCompanyAddress);
                        tvHorario = (TextView) findViewById(R.id.tvHorario);
                        tvEstado = (TextView) findViewById(R.id.tvEstado);

                        tvCompany.setText(objJSONVisit.getString("company_name"));
                        tvStudent.setText(objJSONVisit.getString("student_name")+" "+objJSONVisit.getString("student_last_name"));
                        tvCompanyAddress.setText(objJSONVisit.getString("address"));

                        if(objJSONVisit.getInt("id_estado")==1){
                            tvEstado.setText("Pendiente");
                            btnCambiarEstado.setEnabled(true);
                        }
                        else if (objJSONVisit.getInt("id_estado")==2){
                            tvEstado.setText("Inicia evaluación");
                            btnCambiarEstado.setEnabled(false);
                        }

                        Date datTmpIniVisita=new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        datTmpIniVisita = sdf.parse(objJSONVisit.getString("visit_date"));
                        Date datTmpFinVisita=new Date();
                        long minutesIni=datTmpIniVisita.getTime();
                        datTmpFinVisita = new Date(minutesIni + (objJSONVisit.getInt("estimated_time")*60000 ));

                        tvHorario.setText("De "+DateUtil.formatHora(datTmpIniVisita)+" a "+DateUtil.formatHora(datTmpFinVisita));

                        googleMap.setMyLocationEnabled(true);
                        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        Criteria criteria = new Criteria();
                        String provider = locationManager.getBestProvider(criteria, true);
                        Location myLocation = locationManager.getLastKnownLocation(provider);

                        setLocation(objJSONVisit.getDouble("latitude"), objJSONVisit.getDouble("longitude"), objJSONVisit.getString("company_name"));
                        if(myLocation!=null){
                            addMarker(myLocation.getLatitude(), myLocation.getLongitude(),"Estoy aqui");
                        }
                        //setLocation(0,0);

                        //mWebView = (WebView) findViewById(R.id.wbMap);
                        //mWebView.loadUrl("https://www.google.com/maps?z=10&t=m&q=loc:" + objJSONVisit.getString("latitude") + "," + objJSONVisit.getString("longitude"));
                        /*
                        mWebView.setWebViewClient(new WebViewClient(){
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                view.loadUrl(url);
                                return true;
                            }
                            @Override
                            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                                handler.proceed();
                            }
                        });*/
                    }
                    //Do anything with response..
                }
                catch(Exception ex){
                    Log.d("Error VisitDetail: ", ex.toString());
                }
            }
            dialog.hide();
            super.onPostExecute(result);
        }

        public String getResultWS(){
            return this.strResultWS;
        }
    }

    private void createMapView(){
        /**
         * Catch the null pointer exception that
         * may be thrown when initialising the map
         */
        try {
            if(null == googleMap){
                googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                        R.id.mapView)).getMap();

                /**
                 * If the map is still null after attempted initialisation,
                 * show an error to the user
                 */
                if(null == googleMap) {
                    /*Toast.makeText(getApplicationContext(),
                            "Error creating map",Toast.LENGTH_SHORT).show();*/
                }
            }
        } catch (NullPointerException exception){
            Log.e("mapApp", exception.toString());
        }
    }

    private void setLocation(double pDblLatitude, double pDblLongitude, String pStrNomEmpresa){
        /** Make sure that the map has been initialised **/
        if(null != googleMap){
            LatLng myCoordinates = new LatLng(pDblLatitude, pDblLongitude);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.addMarker(new MarkerOptions()
                            .position(myCoordinates)
                            .title(pStrNomEmpresa)
                            .draggable(true)
            );
            CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myCoordinates, 17);
            googleMap.animateCamera(yourLocation);

        }
    }

    private void addMarker(double pDblLatitude, double pDblLongitude, String pStrMarker){
        if(null != googleMap){
            LatLng myCoordinates = new LatLng(pDblLatitude, pDblLongitude);
            googleMap.addMarker(new MarkerOptions()
                    .position(myCoordinates)
                    .title(pStrMarker)
                    .draggable(true)
            );
        }
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);

        return Radius * c;
    }
}
