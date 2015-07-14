package com.dat.stormy.view;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;

import com.dat.stormy.R;
import com.dat.stormy.model.FiveDayDataSet;
import com.dat.stormy.model.JsonHtmlService;
import com.dat.stormy.model.Locate;
import com.dat.stormy.model.SaveLocation;
import com.dat.stormy.model.WeatherForFiveDay;
import com.dat.stormy.view.MainActivity;


/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    private JsonHtmlService mJsonHtmlService;
    private SaveLocation mSaveLocation;
    private WeatherForFiveDay mWeatherForFiveDay;

    private Context mContext;
    private AppWidgetManager mAppWidgetManager;
    private int mAppWidgetId;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d("TestWidget", "Run onUpdate");
        final int N = appWidgetIds.length;

        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            mJsonHtmlService = new JsonHtmlService(context);
            mSaveLocation = new SaveLocation(context);
            mContext = context;
            mAppWidgetManager = appWidgetManager;
            mAppWidgetId = appWidgetId;

            String forecastUrl = "";
            SaveLocation saveLocation = new SaveLocation(context);
            Locate locate = saveLocation.getLocation();
            if (locate != null) {
                forecastUrl = String.format(mJsonHtmlService.FORECAST, "lat=" + locate.getLatitude() + "&lon=" + locate.getLongtitude());
            } else {
                double latitude;
                double longtitude;
                Location currentLocation = mJsonHtmlService.getCurrentGPS();
                if (currentLocation == null) {
                    latitude = 10.7568441;
                    longtitude = 106.6491496;
                } else {
                    latitude = currentLocation.getLatitude();
                    longtitude = currentLocation.getLongitude();
                }
                forecastUrl = String.format(mJsonHtmlService.FORECAST, "lat=" + latitude + "&lon=" + longtitude);
            }

            new LoadWebBody().execute(forecastUrl);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        Log.d("TestWidget", "Run onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        Log.d("TestWidget", "Run onDisabled");
    }

    void updateAppWidget() {

        Log.d("TestWidget", "Run updateAppWidget");

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.weather_widget);

        Intent intent = new Intent(mContext, MainActivity.class);
        ClassLoader loader = MainActivity.class.getClassLoader();

        intent.setExtrasClassLoader(loader);
        intent.putExtra("FORECAST", mWeatherForFiveDay);

        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent);

        FiveDayDataSet fiveDayDataSet = mWeatherForFiveDay.getFiveDayDataSets().get(0);
        Log.d("TestWidget ", fiveDayDataSet.getTime().toGMTString());
        views.setImageViewResource(R.id.WidgetWeatherIcon, fiveDayDataSet.getWeatherIconId());
        views.setTextViewText(R.id.WidgetLocate, mWeatherForFiveDay.getCountry() + "\\" + mWeatherForFiveDay.getLocation());
        views.setTextViewText(R.id.WidgetTemperature, fiveDayDataSet.getTemperature() + "");
        views.setTextViewText(R.id.WidgetTemperatureMetric, "C");
        views.setTextViewText(R.id.WidgetHumidity, "Humidity: " + (int) fiveDayDataSet.getHumidity() + "%");
        views.setTextViewText(R.id.WidgetRain, "Rain: " + fiveDayDataSet.getRain() + "mm");
        views.setTextViewText(R.id.WidgetWeatherDescription, fiveDayDataSet.getWeatherDescription());

        // Instruct the widget manager to update the widget
        mAppWidgetManager.updateAppWidget(mAppWidgetId, views);
    }

    class LoadWebBody extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Log.d("TestWidget: ", params[0]);
            return mJsonHtmlService.getXmlFromUrl(params[0]);
        }

        protected void onPostExecute(String s) {
            Log.d("TestWidget", s);
            if (!s.equals("Wrong")) {
                try {
                    mWeatherForFiveDay = mJsonHtmlService.ExtractValueFromJsonFiveDay(s);
                    updateAppWidget();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else updateAppWidget();
        }
    }
}


