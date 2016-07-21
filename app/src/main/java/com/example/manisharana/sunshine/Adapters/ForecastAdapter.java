package com.example.manisharana.sunshine.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.manisharana.sunshine.Fragments.ForecastFragment;
import com.example.manisharana.sunshine.R;
import com.example.manisharana.sunshine.Utility;

public class ForecastAdapter extends CursorAdapter {

    private static Context context;

    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;
    }

    public static String getHighLowFormat(double max, double min) {
        boolean isMetric = Utility.isMetric(context);

        return Utility.formatTemperature(max,isMetric) + "/" + Utility.formatTemperature(min,isMetric);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.text_view_forecast_list_view, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));
    }

    private String convertCursorRowToUXFormat(Cursor cursor) {

        String highAndLow = getHighLowFormat(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.getFormattedDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }
}
