package com.demo.appsenatidemo.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.appsenatidemo.LazyAdapter;
import com.demo.appsenatidemo.LoginActivity;
import com.demo.appsenatidemo.VisitDetailActivity;
import com.demo.appsenatidemo.components.Flog;
//import com.nmviajes.app.R;
import com.demo.appsenatidemo.R;
import com.demo.appsenatidemo.entities.Visit;
import com.demo.appsenatidemo.utils.DateUtil;
import com.demo.appsenatidemo.utils.GlobalVariables;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

import org.apache.http.client.HttpClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CalendarComponent extends Activity {

	public static final int SELECTION_MODE_SINGLE = 1;
	public static final int SELECTION_MODE_RANGE = 2;

	final int DATE_SELECTED=100;

	private CalendarPickerView calendar;
	//private TextView tvSinVisitas;
	private List<Date> selectedDates;
	private int currentMode = 1;
	// private String userDate1;
	private Date userFormatDate1;
	
	private Date userFormatLastDate1;


	// XML node keys
	public static final String KEY_SONG = "song"; // parent node
	public static final String KEY_ID = "id";
	public static final String KEY_TITLE = "title";
	public static final String KEY_ARTIST = "artist";
	public static final String KEY_DURATION = "duration";
	public static final String KEY_THUMB_URL = "thumb_url";

	ListView list;
	LazyAdapter adapter;
	ArrayList<HashMap<String, String>> lstVisitasViewFinal;

	// private String userDate2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calendar);

		setTitle("Visitas agendadas");

		validateExtra();

		selectedDates = null;
		final Calendar nextYear = Calendar.getInstance();
		nextYear.add(Calendar.YEAR, 1);

		final Calendar lastYear = Calendar.getInstance();
		lastYear.add(Calendar.YEAR, 0);
		lastYear.add(Calendar.DAY_OF_MONTH, 0);
		

		Log.i("limites2", lastYear.get(Calendar.DAY_OF_MONTH)+"");
		Log.i("limites3", nextYear.get(Calendar.DAY_OF_MONTH)+"");

		if(userFormatLastDate1!=null){
			Log.i("limite x", userFormatLastDate1+"");
			lastYear.setTime(userFormatLastDate1);
			lastYear.add(Calendar.DAY_OF_MONTH, 1);
		}
		// final Date aux = DateUtils.getDateToStringFormat(userDate1);
		calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
		calendar.lstFechasVisita = GlobalVariables.lstFechasVisita;
		//tvSinVisitas = (TextView) findViewById(R.id.tvSinVisitas);

		//Se obtiene todas las visitas del supervisor para mostrarlas en el calendario


		if (userFormatDate1 != null) {
			calendar.init(lastYear.getTime(), nextYear.getTime())
					.inMode((currentMode == SELECTION_MODE_SINGLE) ? (SelectionMode.SINGLE)
							: (SelectionMode.RANGE)).withSelectedDate(userFormatDate1);
			// .withSelectedDate(new Date());
		} else {
			calendar.init(lastYear.getTime(), nextYear.getTime()).inMode(
					(currentMode == SELECTION_MODE_SINGLE) ? (SelectionMode.SINGLE)
							: (SelectionMode.RANGE));
		}

		calendar.setOnDateSelectedListener(new OnDateSelectedListener() {

			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub
				// FLog.v("onDateUnselected");

			}

			@Override
			public void onDateSelected(Date date) {
				// TODO Auto-generated method stub
				selectedDates = calendar.getSelectedDates();

				Date datFechaTmp = new Date();

				for(int intX=0; intX<=selectedDates.size()-1;intX++){
					datFechaTmp=selectedDates.get(intX);
				}
				//String strFecha1 = DateUtil.formatDateString(strFecha);
				//String strFecha  = datFechaTmp.getYear()+"-"+datFechaTmp.getMonth()+"-"+datFechaTmp.getDate();
				String strFecha  = DateUtil.formatDateYY_MM_DD(datFechaTmp);

				ReadWeatherJSONFeedTask objReadWeatherJSONFeedTask = new ReadWeatherJSONFeedTask();
				objReadWeatherJSONFeedTask.execute("https://dam-atlas-luisreque.c9.io/visitas/get_by_supervisor_date?pIntIdSupervisor="+ GlobalVariables.objAppUser.getIdSupervisor()+"&pStrFecha="+strFecha);

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
					//String strResult = new ReadWeatherJSONFeedTask().execute("https://dam-atlas-luisreque.c9.io/visitas/get_by_supervisor?pIntIdSupervisor=5");
					//https://dam-atlas-luisreque.c9.io/visitas/get_by_supervisor?pIntIdSupervisor=5
					//String strResultado = readJSONVisits("https://dam-atlas-luisreque.c9.io/visitas/get_by_supervisor?pIntIdSupervisor=5");

					ArrayList<HashMap<String, String>> lstVisitasView = new ArrayList<HashMap<String, String>>();
					//http://stackoverflow.com/questions/28302812/parse-json-to-string-android-studio

					try {
						//JSONObject jsonObject = new JSONObject(strResultado);
						//JSONArray postalCodesItems = new JSONArray(jsonObject.getString("postalCodes"));
						JSONArray objJSONArray = new JSONArray(strResultWS);

						List<Visit> lstVisits = new ArrayList<Visit>();

						if(objJSONArray.length() == 0){
							//tvSinVisitas.setVisibility(View.VISIBLE);
						}
						else{
							//tvSinVisitas.setVisibility(View.INVISIBLE);

							for (int i = 0; i < objJSONArray.length(); ++i) {
								JSONObject objJSONVisit = objJSONArray.getJSONObject(i);

								HashMap<String, String> map = new HashMap<String, String>();
								// adding each child node to HashMap key =&gt; value
								//{"id":null,"visit_date":"2015-09-09T10:00:00.000Z","id_visit":2,"company_name":"CER CONTRATISTAS GENERALES S.A.C.","id_company":8,"student_name":"Giovanna","student_last_name":"Diaz"}

								String strCompanyName = objJSONVisit.getString("company_name");

								if(strCompanyName.length()>20){
									strCompanyName = strCompanyName.substring(0,20)+"...";
								}

								map.put(KEY_ID, objJSONVisit.getString("id_visit"));
								map.put(KEY_TITLE,strCompanyName);
								map.put(KEY_ARTIST, objJSONVisit.getString("student_name")+" "+objJSONVisit.getString("student_last_name"));

								Date datTmpIniVisita=new Date();
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
								datTmpIniVisita = sdf.parse(objJSONVisit.getString("visit_date"));
								Date datTmpFinVisita=new Date();
								long minutesIni=datTmpIniVisita.getTime();
								datTmpFinVisita = new Date(minutesIni + (objJSONVisit.getInt("estimated_time")*60000 ));

								map.put(KEY_DURATION, "De "+DateUtil.formatHora(datTmpIniVisita)+" a "+DateUtil.formatHora(datTmpFinVisita));
								map.put(KEY_THUMB_URL, objJSONVisit.getString("url"));

								// adding HashList to ArrayList
								lstVisitasView.add(map);
							}
							lstVisitasViewFinal=lstVisitasView;
						}

					} catch (Exception e) {
						Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
					}

					list=(ListView)findViewById(R.id.list);

					adapter=new LazyAdapter(CalendarComponent.this, lstVisitasView);
					list.setAdapter(adapter);

					// Click event for single list row
					list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
												int position, long id) {

							HashMap<String, String> objVisit = (HashMap<String, String>) lstVisitasViewFinal.get(position);
							String strIdVisit = objVisit.get(KEY_ID);

							Intent newActivity = new Intent(CalendarComponent.this, VisitDetailActivity.class);
							newActivity.putExtra("VISIT_ID",strIdVisit);
							startActivity(newActivity);

							/*AlertDialog alertDialog = new AlertDialog.Builder(CalendarComponent.this).create();
							alertDialog.setTitle("Reset...");
							alertDialog.setMessage("position:" + position + "/ id:" + id);
							alertDialog.show();*/

							/*for(int i=0; i<((ViewGroup)view).getChildCount(); ++i) {
								View nextChild = ((ViewGroup)view).getChildAt(i);
							}*/
							//LazyAdapter objLazyAdapter = (LazyAdapter) parent.getAdapter();

							//ListView lstTmpView = (ListView) view;

							//ArrayList<HashMap<String, String>> item = (ArrayList<HashMap<String, String>>)parent.getItemAtPosition(position);
							//String id = item.get(0);
							//String str = "";
							//str="asd";

							//parent.getAdapter().getItem(position)
						}
					});
				}
			}
		});
/*
		((Button) (findViewById(R.id.btnAccept))).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				accept();
			}
		});*/
/*
		((Button) (findViewById(R.id.btnCancel))).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				cancel();
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
							Intent intent = new Intent(CalendarComponent.this, LoginActivity.class);
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

	private void validateExtra() {
		// TODO Auto-generated method stub
		if (getIntent() != null) {
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				currentMode = bundle.getInt("MODE", SELECTION_MODE_SINGLE);
				long aux = bundle.getLong("DATE1", -1);
				long auxlast1 = bundle.getLong("DATELAST1", -1);

				userFormatDate1 = null;
				userFormatLastDate1 = null;
				
				if (aux > 0) {
					userFormatDate1 = new Date(aux);
					// userFormatDate1.setTime(aux);
				}
				if (auxlast1 > 0) {
					userFormatLastDate1 = new Date(auxlast1);
					// userFormatDate1.setTime(aux);
				}
				Flog.v("CalendarComponent ", "aux ", aux, " userFormatDate1 ", userFormatDate1);
				// userDate1 = bundle.getString("DATE1");
				// userDate2 = bundle.getString("DATE2");

				// FLog.v("CalendarComponent currentMode ",
				// " userDate1 ",userDate1," userDate2 ",userDate2);
				// if(userDate1!=null){FLog.v("userDate1 ",userDate1);};
			}
		}
	}

	protected void cancel() {
		// TODO Auto-generated method stub
		canceled();
	}

	protected void accept() {
		// TODO Auto-generated method stub
		/*switch (currentMode) {
		case SELECTION_MODE_SINGLE:
			if (selectedDates != null && selectedDates.size() > 0) {
				resultSingle(selectedDates.get(0));
			} else {
				Toast.makeText(this, R.string.calendario_error_simple, Toast.LENGTH_SHORT).show();
			}
			break;
		case SELECTION_MODE_RANGE:
			if (selectedDates != null && selectedDates.size() >= 2) {
				resultRange(selectedDates.get(0), selectedDates.get(selectedDates.size() - 1));
			} else {
				Toast.makeText(this, R.string.calendario_error_rango, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}*/

	}

	public void resultSingle(Date date1) {
		Intent returnIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("DATE1", date1.toString());

		returnIntent.putExtras(bundle);

		setResult(RESULT_OK, returnIntent);

		finish();
	}

	public void resultRange(Date date1, Date date2) {
		Intent returnIntent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("DATE1", date1.toString());
		bundle.putString("DATE2", date2.toString());
		// FLog.v("result 1 ",date1.toString());
		// FLog.v("result 2 ",date2.toString());

		returnIntent.putExtras(bundle);

		setResult(RESULT_OK, returnIntent);

		finish();
	}

	public void canceled() {
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent);
		finish();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// super.onBackPressed();
		// startActivity(new Intent(this, FlightsActivity.class));
		// finish();
		canceled();
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

		protected void onPreExecute() {
			dialog = new ProgressDialog(CalendarComponent.this);
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
