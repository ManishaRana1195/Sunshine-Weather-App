package com.example.manisharana.sunshine.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class WeatherDbHelper extends SQLiteOpenHelper {
    public static int DB_VERSION = 1;
    public static final String DB_NAME = "forecast.db";

    public WeatherDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        List<String> queries = new LinkedList<>();
        queries.add(LocationEntry.getCreateLocationEntry());
        queries.add(WeatherEntry.getCreateWeatherEntry());
        for(String query: queries){
            sqLiteDatabase.execSQL(query);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        List<String> queries = new LinkedList<>();
        queries.add(LocationEntry.getDeleteEntry());
        queries.add(WeatherEntry.getDeleteEntry());

        for(String query: queries){
            sqLiteDatabase.execSQL(query);
        }
        onCreate(sqLiteDatabase);
    }
}
