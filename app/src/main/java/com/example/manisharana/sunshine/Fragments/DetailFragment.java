package com.example.manisharana.sunshine.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manisharana.sunshine.R;

public class DetailFragment extends Fragment {
    public DetailFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.textview_weather_data);
        Intent intent = getActivity().getIntent();
        String forecastString = intent.getStringExtra("ForecastStr");
        textView.setText(forecastString);

        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
