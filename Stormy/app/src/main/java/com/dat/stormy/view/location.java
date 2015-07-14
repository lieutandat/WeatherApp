package com.dat.stormy.view;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.dat.stormy.model.JsonHtmlService;
import com.dat.stormy.R;
import com.dat.stormy.model.Locate;
import com.dat.stormy.model.Locates;
import com.dat.stormy.model.SaveLocation;
import com.dat.stormy.model.WeatherForFiveDay;

import java.util.List;


public class location extends Activity {


    final String CONDITION_ADDRESS = "ADDRESS";
    final String CONDITION_FORECAST = "FORECAST";
    final String TAG = location.class.getSimpleName();

    private EditText mLocationEditText;
    private Button mButtonSearch;
    private ListView mListLocation;

    private JsonHtmlService mJsonHtmlService = new JsonHtmlService(location.this);
    private Locates mLocates;
    private int mSelectPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mLocationEditText = (EditText) findViewById(R.id.editTextLocation);
        mButtonSearch = (Button) findViewById(R.id.btnSearch);
        mListLocation = (ListView) findViewById(R.id.lstLocation);

        SaveLocation saveLocation = new SaveLocation(location.this);
        Locate locate = saveLocation.getLocation();
        if (locate != null) mLocationEditText.setText(locate.getAddress());
        else mLocationEditText.setText("");

        // ListView Item Click Listener
        mListLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                mSelectPosition = position;
                Locate locate = mLocates.getLocates().get(mSelectPosition);
                final String url = String.format(mJsonHtmlService.FORECAST, "lat=" + locate.getLatitude() + "&lon=" + locate.getLongtitude());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new LoadForecast().execute(url);
                    }
                });
                // ListView Clicked item value
                // String  itemValue    = (String) mListLocation.getItemAtPosition(position);
            }

        });

        mButtonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String namePlace = mLocationEditText.getText().toString();
                if (namePlace.equals(""))
                    Toast.makeText(location.this, "Please input name of place!", Toast.LENGTH_LONG).show();
                else {

                    final String key = String.format(mJsonHtmlService.GET_LONG_LAT, namePlace).replace(" ", "%20");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new LoadLocate().execute(key);
                        }
                    });
                }
            }
        });
    }

    //CalBack Get Json from web by API -> update View
    class LoadForecast extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return mJsonHtmlService.getXmlFromUrl(params[0]);
        }

        protected void onPostExecute(String s) {
            Log.d(TAG, "LoadWebBody : " + s);
            if (!s.equals("Wrong")) {
                try {
                    WeatherForFiveDay weatherForFiveDay = mJsonHtmlService.ExtractValueFromJsonFiveDay(s);
                    Log.d(TAG, "LoadWebBody : " + weatherForFiveDay.getFiveDayDataSets().size());
                    Intent intent = new Intent(location.this, MainActivity.class);
                    intent.putExtra("FORECAST", weatherForFiveDay);
                    intent.putExtra("LOCATION_INFO", mLocates.getLocates().get(mSelectPosition));
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class LoadLocate extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            return mJsonHtmlService.getXmlFromUrl(params[0]);
        }

        protected void onPostExecute(String s) {
            if (!s.equals("Wrong")) {
                try {
                    mLocates = mJsonHtmlService.extractLongLatFromAddress(s);
                    List<String> locateName = mLocates.getLocateName();
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(location.this,
                            android.R.layout.simple_list_item_1, android.R.id.text1, locateName);
                    mListLocation.setAdapter(adapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public void setupUI(View view) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(location.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
