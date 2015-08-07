package com.demo.appsenatidemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.demo.appsenatidemo.components.CalendarComponent;
import com.demo.appsenatidemo.R;
import com.demo.appsenatidemo.entities.Visit;
import com.demo.appsenatidemo.utils.GlobalVariables;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button btnShowCalendar;
    final int DATE_SELECTED=100;
    //git@github.com:oaemdl1/AppSenatiPEA.git
//http://androidshenanigans.blogspot.com/2015/03/material-design-template.html
    //mapas: http://glennsayers.com/android-mapping/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Menú");

        btnShowCalendar=(Button) findViewById(R.id.button);

        /*btnShowCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(MainActivity.this, CalendarComponent.class);
                Bundle bundle = new Bundle();
                bundle.putInt("MODE", CalendarComponent.SELECTION_MODE_SINGLE);
                bundle.putLong("DATE1", -1);
                intent.putExtras(bundle);

                startActivityForResult(intent, DATE_SELECTED);
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivityForResult(intent, DATE_SELECTED);
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

    public void openCalendar(View view) {
        ReadWeatherJSONFeedTask objReadWeatherJSONFeedTask = new ReadWeatherJSONFeedTask();
        objReadWeatherJSONFeedTask.execute("https://dam-atlas-luisreque.c9.io/visitas/get_by_supervisor?pIntIdSupervisor=5");

        boolean bolGetResponse = false;
        String strResultWS="";
        while(!bolGetResponse){
            strResultWS = objReadWeatherJSONFeedTask.getResultWS();
            if(strResultWS!="-999"){
                bolGetResponse = true;
                break;
            }
        }

        if(bolGetResponse){
            try {
                JSONArray objJSONArray = new JSONArray(strResultWS);

                String var = "";
                var = "asd";
                List<Visit> lstVisits = new ArrayList<Visit>();
                Calendar fechaActual = Calendar.getInstance();

                List<Date> lstFechasVisita= new ArrayList<Date>();
                for (int i = 0; i < objJSONArray.length(); ++i) {
                    JSONObject objJSONVisit = objJSONArray.getJSONObject(i);
					/*Visit objVisit = new Visit();
					objVisit.setMotive(objJSONVisit.getString("motive"));
					lstVisits.add(objVisit);*/


                    Date datFecVisit=new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    datFecVisit = sdf.parse(objJSONVisit.getString("visit_date"));

/*
					DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
					Date datFecVisit = (Date)formatter.parse(objJSONVisit.getString("visit_date"));
*/
                    //calendar.getFechasVisita()

                    if(datFecVisit.after(fechaActual.getTime())){
                        lstFechasVisita.add(datFecVisit);
                    }
                    //map.put(KEY_DURATION, objJSONVisit.getString("visit_date"));

                }
                GlobalVariables.lstFechasVisita = lstFechasVisita;
                //calendar.setLstFechasVisita(lstFechasVisita);
                //calendar.highlightDates(lstFechasVisita);
            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }
        }

        Intent intent = new Intent(this, CalendarComponent.class);
        Bundle bundle = new Bundle();
        bundle.putInt("MODE", CalendarComponent.SELECTION_MODE_SINGLE);
        bundle.putLong("DATE1", -1);
        intent.putExtras(bundle);

        startActivityForResult(intent, DATE_SELECTED);
        /*
        Intent intent = new Intent(this, CalendarComponent.class);
        startActivity(intent);*/
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

        ProgressDialog dialog;
        String strResultWS="-999";
        @Override
        protected String doInBackground(String... urls) {
            strResultWS= getVisitsJSON(urls[0]);
            return strResultWS;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Cargando...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
            dialog.hide();
        }

        public String getResultWS(){
            return this.strResultWS;
        }
    }
}
