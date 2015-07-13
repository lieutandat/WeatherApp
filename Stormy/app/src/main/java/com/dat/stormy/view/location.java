package com.dat.stormy.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dat.stormy.model.GetXml;
import com.dat.stormy.R;
import com.dat.stormy.model.WeatherForFiveDay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class location extends Activity {


    final String CONDITION_ADDRESS = "ADDRESS";
    final String CONDITION_FORECAST = "FORECAST";
    final String TAG = location.class.getSimpleName();
    EditText mLocation;
    Button mButtonSearch;
    GetXml mGetXml = new GetXml(location.this);


    public final static String ADDRESS = "phonghoac";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocation = (EditText) findViewById(R.id.editTextLocation);
        mButtonSearch = (Button) findViewById(R.id.btnSearch);

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String namePlace = mLocation.getText().toString();
                if (namePlace.equals(""))
                    Toast.makeText(location.this, "Please input name of place!", Toast.LENGTH_LONG).show();
                else {

                    final String key = String.format(mGetXml.GET_LONG_LAT, namePlace).replace(" ", "%20");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new LoadWebBody().execute(CONDITION_ADDRESS,key);
                        }
                    });
                }
            }
        });
    }

    //Get the Forecast from API
    private void GetForeCast(double lat, double lng) { //
        final String forecastUrl = String.format(mGetXml.FORECAST, "lat=" + lat + "&lon=" + lng);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new LoadWebBody().execute(CONDITION_FORECAST, forecastUrl);
            }
        });
    }

    //CalBack Get Json from web by API -> update View
    class LoadWebBody extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = ChoiceCondition(params[0], params[1]);
            return mGetXml.getXmlFromUrl(url);
        }

        protected void onPostExecute(String s) {
            Log.d(TAG, "LoadWebBody : " + s);
            if (!s.equals("Wrong")) {
                try {
                    WeatherForFiveDay weatherForFiveDay = mGetXml.ExtractValueFromJsonFiveDay(s);
                    Log.d(TAG, "LoadWebBody : " + weatherForFiveDay.getFiveDayDataSets().size());
                    Intent intent = new Intent(location.this,MainActivity.class);
                    intent.putExtra("FORECAST",weatherForFiveDay);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String ChoiceCondition(String condition, String param) {
        if (condition.equals(CONDITION_FORECAST)) {
            Log.d(TAG, " LoadWebBody : do condition forecast : " + param);
            return param;
        } else if (condition.equals(CONDITION_ADDRESS)) {
            String json = mGetXml.getXmlFromUrl(param);
            Log.d(TAG, " LoadWebBody : Do ConditionAddress");
            Log.d(TAG, " LoadWebBody : json = " + json);
            String address = mGetXml.extractLongLatFromAddress(json);
            Log.d(TAG, " LoadWebBody : address = " + address);
            return String.format(mGetXml.FORECAST, address);
        }
        return " wrong ";
    }
}
