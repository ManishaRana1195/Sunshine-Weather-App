package com.example.manisharana.sunshine.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.manisharana.sunshine.ForecastDetailActivity;
import com.example.manisharana.sunshine.R;
import com.example.manisharana.sunshine.SettingsActivity;
import com.example.manisharana.sunshine.SettingsFragment;
import com.example.manisharana.sunshine.WeatherHttpClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeeklyForecastFragment extends Fragment {
    private String[] forecastArray;
    private List<String> forecastFakeData;
    private ArrayAdapter<String> weatherDataAdapter;

    public WeeklyForecastFragment() {
        forecastArray= new String[]{"Today-Sunny-88/64","Tomorrow-Foggy-88/64","Wednesday-Rainy-88/64","Thursday-Sunny-88/64","Friday-Sunny-88/64","Saturday-Sunny-88/64"};
        forecastFakeData = new ArrayList<>();
        forecastFakeData.addAll(Arrays.asList(forecastArray));
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main_menu,menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        weatherDataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.text_view_forecast_list_view, R.id.list_item_forecast_textview, forecastFakeData);
        View rootView = inflater.inflate(R.layout.fragment_forecast,container,false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(weatherDataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String forecastStr = (String)adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(), ForecastDetailActivity.class);
                intent.putExtra("ForecastStr",forecastStr);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_refresh:
                fetchWeatherTask();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchWeatherTask() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCity = sharedPreferences.getString(getString(R.string.preference_default_city), getString(R.string.Bangalore));
        String defUnit = sharedPreferences.getString(getString(R.string.preference_default_unit), getString(R.string.metric));
        String defDays = sharedPreferences.getString(getString(R.string.preference_default_days), getString(R.string.week));


        new WeatherHttpClient(getActivity(),weatherDataAdapter).execute(defCity,defUnit,defDays);
    }
}
