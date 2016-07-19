package com.example.manisharana.sunshine.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.manisharana.sunshine.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeeklyForecastFragment extends Fragment {
    private String[] forecastArray;
    private List<String> forecastFakeData;
    private ArrayAdapter<String> weatherDataAdapter;

    public WeeklyForecastFragment() {
        forecastArray= new String[]{"Today-Sunny-88/64","Tomorrow-Foggy-88/64","Wednesday-Rainyy-88/64","Thursday-Sunny-88/64","Friday-Sunny-88/64","Saturday-Sunny-88/64"};
        forecastFakeData = new ArrayList<>();
        forecastFakeData.addAll(Arrays.asList(forecastArray));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        weatherDataAdapter = new ArrayAdapter<String>(getActivity(), R.layout.text_view_forecast_list_view, R.id.list_item_forecast_textview, forecastFakeData);
        View rootView = inflater.inflate(R.layout.fragment_forecast,container,false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(weatherDataAdapter);
        return rootView;
    }
}
