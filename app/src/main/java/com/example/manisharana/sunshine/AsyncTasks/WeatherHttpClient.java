package com.example.manisharana.sunshine.AsyncTasks;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.manisharana.sunshine.Data.LocationEntry;
import com.example.manisharana.sunshine.Data.WeatherEntry;
import com.example.manisharana.sunshine.R;
import com.example.manisharana.sunshine.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherHttpClient extends AsyncTask<String, Void, Void>{

    private final Context context;

    public WeatherHttpClient(Context activity) {
        this.context = activity;
    }

    @Override
    protected Void doInBackground(String... values) {
        if (values == null) {
            return null;
        }

        Response response;
        URL url;
        String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        String appID = context.getString(R.string.MY_WEATHER_API_KEY);
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter("q", values[0]).appendQueryParameter("units", context.getString(R.string.metric)).appendQueryParameter("cnt", values[1]).appendQueryParameter("APPID", appID).build();
        try {
            url = new URL(builtUri.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            getWeatherForecastData(response.body().string(), values[0]);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void getWeatherForecastData(String jsonStr, String locationSetting) {
        GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("India/Kolkata"));


        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject cityObject = jsonObject.getJSONObject("city");
            String name = cityObject.getString("name");
            JSONObject coordObject = cityObject.getJSONObject("coord");
            double longitude = coordObject.getDouble("lon");
            double latitude = coordObject.getDouble("lat");

            long locationId = saveLocationEntry(locationSetting, name, latitude, longitude);

            JSONArray listArray = jsonObject.getJSONArray("list");
            Vector<ContentValues> cvVector = new Vector<>(listArray.length());

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject object = listArray.getJSONObject(i);
                JSONObject tempObject = object.getJSONObject("temp");
                double min = tempObject.getDouble("min");
                double max = tempObject.getDouble("max");

                double pressure = object.getDouble("pressure");
                double humidity = object.getDouble("humidity");
                double windSpeed = object.getDouble("speed");
                double windDeg = object.getDouble("deg");

                JSONArray weatherArray = object.getJSONArray("weather");
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String desc = weatherObject.getString("main");
                int weatherId = weatherObject.getInt("id");


                ContentValues contentValues = new ContentValues();
                contentValues.put(WeatherEntry.COLUMN_LOCATION_ID, locationId);
                contentValues.put(WeatherEntry.COLUMN_DATE, Utility.normalizeDate(gc));
                contentValues.put(WeatherEntry.COLUMN_DESC, desc);
                contentValues.put(WeatherEntry.COLUMN_MAX, max);
                contentValues.put(WeatherEntry.COLUMN_MIN, min);
                contentValues.put(WeatherEntry.COLUMN_HUMIDITY, humidity);
                contentValues.put(WeatherEntry.COLUMN_PRESSURE, pressure);
                contentValues.put(WeatherEntry.COLUMN_DEGREES, windDeg);
                contentValues.put(WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                contentValues.put(WeatherEntry.COLUMN_WEATHER_ID, weatherId);
                gc.add(Calendar.DATE, 1);
                cvVector.add(contentValues);
            }
            int inserted = 0;
            if (cvVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cvVector.size()];
                cvVector.toArray(cvArray);
                inserted = context.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, cvArray);
            }
            Log.d("WeatherHttpClient", "FetchWeatherTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public long saveLocationEntry(String locationSetting, String cityName, double latitude, double longitude) {
        long locationId;
        Cursor locationCursor = context.getContentResolver().query(LocationEntry.CONTENT_URI,
                new String[]{LocationEntry._ID}, LocationEntry.COLUMN_LOC_SETTING + " = ?", new String[]{locationSetting},
                null);

        if (locationCursor.moveToFirst()) {
            int locationIdIndex = locationCursor.getColumnIndex(LocationEntry._ID);
            locationId = locationCursor.getLong(locationIdIndex);
        } else {
            ContentValues locationValues = new ContentValues();

            locationValues.put(LocationEntry.COLUMN_CITY_NAME, cityName);
            locationValues.put(LocationEntry.COLUMN_LOC_SETTING, locationSetting);
            locationValues.put(LocationEntry.COLUMN_LATITUDE, latitude);
            locationValues.put(LocationEntry.COLUMN_LONGITUDE, longitude);

            Uri insertedUri = context.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);

            locationId = ContentUris.parseId(insertedUri);
        }

        locationCursor.close();
        return locationId;

    }


}
