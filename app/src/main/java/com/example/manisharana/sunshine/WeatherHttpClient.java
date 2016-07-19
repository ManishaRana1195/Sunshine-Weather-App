package com.example.manisharana.sunshine;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherHttpClient extends AsyncTask<String,Void,String> {

    private final ArrayAdapter<String> arrayAdapter;
    private final Context context;

    public WeatherHttpClient(Context activity, ArrayAdapter<String> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
        this.context = activity;
    }

    @Override
    protected String doInBackground(String... strings) {
        Response response;
        URL url;
        String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
        String location = "Bangalore";
        String unit = "metric";
        String days = "7";
        String appID = context.getString(R.string.MY_WEATHER_API_KEY);
        Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon().appendQueryParameter("q", location).appendQueryParameter("units", unit).appendQueryParameter("cnt", days).appendQueryParameter("APPID", appID).build();
        try {
            url = new URL(builtUri.toString());
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            response = client.newCall(request).execute();
            return response != null ? response.body().string() : null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        List<String> weatherForecastData = getWeatherForecastData(s);
        arrayAdapter.clear();
        arrayAdapter.addAll(weatherForecastData);
    }

    public List<String> getWeatherForecastData(String jsonStr){
        List<String> result = new LinkedList<String>();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject cityObject = jsonObject.getJSONObject("city");
            JSONObject coordObject = cityObject.getJSONObject("coord");
            double longitude = coordObject.getDouble("lon");
            double latitude = coordObject.getDouble("lat");

            JSONArray listArray = jsonObject.getJSONArray("list");
            for(int i=0;i<listArray.length();i++){
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

                result.add(getDay(i)+" - "+desc+" - "+getHighLowFormat(max,min));

            }

            } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getDay(int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR,num);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        return sdf.format(calendar.getTime());
    }

    private String getHighLowFormat(double max, double min) {
        System.out.println("------------------------------"+max+"  "+min);
        return Math.round(max)+"/"+Math.round(min);
    }




}
