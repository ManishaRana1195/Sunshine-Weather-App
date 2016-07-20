package com.example.manisharana.sunshine.AsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.manisharana.sunshine.Data.LocationEntry;
import com.example.manisharana.sunshine.Data.WeatherEntry;
import com.example.manisharana.sunshine.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherHttpClient extends AsyncTask<String, Void, List<String>> {

    private final ArrayAdapter<String> arrayAdapter;
    private final Context context;

    public WeatherHttpClient(Context activity, ArrayAdapter<String> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
        this.context = activity;
    }

    @Override
    protected List<String> doInBackground(String... values) {
        if (values == null) {
            return null;
        }

        Response response;
        URL url;
        String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        String appID = context.getString(R.string.MY_WEATHER_API_KEY);
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter("q", values[0]).appendQueryParameter("units", values[1]).appendQueryParameter("cnt", values[2]).appendQueryParameter("APPID", appID).build();
        try {
            url = new URL(builtUri.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            return response != null ? getWeatherForecastData(response.body().string(), values[0]) : null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<String> s) {
        super.onPostExecute(s);
        arrayAdapter.clear();
        arrayAdapter.addAll(s);
    }

    public List<String> getWeatherForecastData(String jsonStr, String locationSetting) {
        List<String> result = new LinkedList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject cityObject = jsonObject.getJSONObject("city");
            String name = cityObject.getString("name");
            JSONObject coordObject = cityObject.getJSONObject("coord");
            double longitude = coordObject.getDouble("lon");
            double latitude = coordObject.getDouble("lat");

            long locationId = LocationEntry.saveLocationEntry(locationSetting, name, latitude, longitude);

            JSONArray listArray = jsonObject.getJSONArray("list");
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

                Vector<ContentValues> cvVector = new Vector<>(10);

                ContentValues contentValues = new ContentValues();
                contentValues.put(WeatherEntry.COLUMN_LOCATION_ID,locationId);
                contentValues.put(WeatherEntry.COLUMN_DATE, getDay(i));
                contentValues.put(WeatherEntry.COLUMN_DESC,desc);
                contentValues.put(WeatherEntry.COLUMN_MAX,max);
                contentValues.put(WeatherEntry.COLUMN_MIN,min);
                contentValues.put(WeatherEntry.COLUMN_HUMIDITY,humidity);
                contentValues.put(WeatherEntry.COLUMN_PRESSURE,pressure);
                contentValues.put(WeatherEntry.COLUMN_DEGREES,windDeg);
                contentValues.put(WeatherEntry.COLUMN_WIND_SPEED,windSpeed);
                contentValues.put(WeatherEntry.COLUMN_WEATHER_ID,weatherId);

                cvVector.add(contentValues);
                Log.d("WeatherHttpClient", "FetchWeatherTask Complete. " + cvVector.size() + " Inserted");

    //            String[] resultStrs = convertContentValuesToUXFormat(cvVector);

                result.add(getDay(i) + " - " + desc + " - " + getHighLowFormat(max, min));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getDay(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, num);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        return sdf.format(calendar.getTime());
    }

    private String getHighLowFormat(double max, double min) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String unitPref = defaultSharedPreferences.getString(context.getString(R.string.preference_default_unit), context.getString(R.string.metric));

        if (unitPref.equals(context.getString(R.string.imperial))) {
            max = (max * 1.8) + 32;
            min = (min * 1.8) + 32;
        }

        return Math.round(max) + "/" + Math.round(min);
    }

    String[] convertContentValuesToUXFormat(Vector<ContentValues> cvv) {
        String[] resultStrs = new String[cvv.size()];
        for (int i = 0; i < cvv.size(); i++) {
            ContentValues weatherValues = cvv.elementAt(i);
            String highAndLow = getHighLowFormat(
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MAX),
                    weatherValues.getAsDouble(WeatherEntry.COLUMN_MIN));
            resultStrs[i] = getDay(i) +
                    " - " + weatherValues.getAsString(WeatherEntry.COLUMN_DESC) +
                    " - " + highAndLow;
        }
        return resultStrs;
    }


}
