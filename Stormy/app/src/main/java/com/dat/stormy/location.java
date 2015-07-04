package com.dat.stormy;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class location extends Activity {
    final String TAG = location.class.getSimpleName();
    EditText mLocation;
    Button mButtonSearch;
    GetXml mGetXml = new GetXml();
    final String GET_LONG_LAT = "http://maps.googleapis.com/maps/api/geocode/json?address=%1$s&sensor=true";
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

                    final String key = String.format(GET_LONG_LAT, namePlace).replace(" ", "%20");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new LoadWebBody().execute(key);
                        }
                    });
                }
            }
        });
    }

    private String GetLongLatFromAddress(String json) {
        String outPut = "";
        try {
            JSONObject body = new JSONObject(json);
            JSONArray result = body.getJSONArray("results");
            JSONObject temp = result.getJSONObject(0);
            JSONObject geo = temp.getJSONObject("geometry");
            JSONObject location = geo.getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            outPut = "lat=" + lat + "&lon=" + lng;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, " GetLongLatFromAddress : " + outPut);
        return outPut;
    }

    class LoadWebBody extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            String url = mGetXml.getXmlFromUrl(params[0]);
            return url;
        }

        protected void onPostExecute(String s) {
            String address = GetLongLatFromAddress(s);
            Intent intent = new Intent(location.this, MainActivity.class);
            intent.putExtra(ADDRESS, address);
            startActivity(intent);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }


}
