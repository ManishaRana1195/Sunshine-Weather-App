package com.example.manisharana.sunshine.Fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.manisharana.sunshine.Activities.ForecastDetailActivity;
import com.example.manisharana.sunshine.Adapters.ForecastAdapter;
import com.example.manisharana.sunshine.Data.LocationEntry;
import com.example.manisharana.sunshine.Data.WeatherEntry;
import com.example.manisharana.sunshine.R;
import com.example.manisharana.sunshine.Activities.SettingsActivity;
import com.example.manisharana.sunshine.AsyncTasks.WeatherHttpClient;
import com.example.manisharana.sunshine.Utility;

public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private ForecastAdapter weatherDataAdapter;
    private static final int FORECAST_LOADER = 0;
    private static final String[] FORECAST_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_DESC,
            WeatherEntry.COLUMN_MAX,
            WeatherEntry.COLUMN_MIN,
            LocationEntry.COLUMN_LOC_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID,
            LocationEntry.COLUMN_LATITUDE,
            LocationEntry.COLUMN_LONGITUDE
    };
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_CONDITION_ID = 6;
    public static final int COL_COORD_LAT = 7;
    public static final int COL_COORD_LONG = 8;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_main_menu, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        weatherDataAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(weatherDataAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Uri uri = WeatherEntry.buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE));
                    Intent intent = new Intent(getActivity(), ForecastDetailActivity.class);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String defCity = Utility.getPreferredLocation(getActivity());
        switch (item.getItemId()) {
            case R.id.action_refresh:
                fetchWeatherTask();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_view:
                Uri locationUri = Uri.parse("geo:0,0?" + defCity).buildUpon().appendQueryParameter("q", defCity).build();
                Intent viewIntent = new Intent(Intent.ACTION_VIEW, locationUri);
                if (viewIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(viewIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchWeatherTask() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String defCity = sharedPreferences.getString(getString(R.string.preference_default_city), getString(R.string.Bangalore));
        String defDays = sharedPreferences.getString(getString(R.string.preference_default_days), getString(R.string.week));


        new WeatherHttpClient(getActivity()).execute(defCity, defDays);
    }

    public void onLocationChanged() {

        fetchWeatherTask();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locSetting = Utility.getPreferredLocation(getActivity());
        String sortOrder = WeatherEntry.COLUMN_DATE + " ASC";
        Uri uri = WeatherEntry.buildWeatherLocationWithStartingDate(locSetting, System.currentTimeMillis());
        return new CursorLoader(getActivity(), uri, FORECAST_COLUMNS, null, null, sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        weatherDataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        weatherDataAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);

    }
}
