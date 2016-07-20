package com.example.manisharana.sunshine.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class WeatherEntry implements BaseColumns {
    public static final String TABLE_NAME = "weather";
    public static final String COLUMN_DESC = "description";
    public static final String COLUMN_MAX = "max_temp";
    public static final String COLUMN_MIN = "min_temp";
    public static final String COLUMN_HUMIDITY = "humidity";
    public static final String COLUMN_PRESSURE = "pressure";
    public static final String COLUMN_DEGREES = "degrees";
    public static final String COLUMN_WIND_SPEED = "wind_speed";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_LOCATION_ID = "location_id";
    public static final String COLUMN_WEATHER_ID = "weather_id";

    public static final String CONTENT_AUTHORITY = "com.example.manisharana.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);
    public static final String WEATHER_PATH = "weather";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(WEATHER_PATH).build();
    public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ CONTENT_AUTHORITY+"/"+WEATHER_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+ CONTENT_AUTHORITY+"/"+WEATHER_PATH;


    public static String getCreateWeatherEntry() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_DESC + " TEXT NOT NULL, "
                + COLUMN_DATE + " INTEGER NOT NULL, "
                + COLUMN_MAX + " REAL NOT NULL, "
                + COLUMN_MIN + " REAL NOT NULL, "
                + COLUMN_HUMIDITY + " REAL NOT NULL, "
                + COLUMN_PRESSURE + " REAL NOT NULL, "
                + COLUMN_DEGREES + " REAL NOT NULL, "
                + COLUMN_WIND_SPEED + " REAL NOT NULL, "
                + COLUMN_WEATHER_ID + " INTEGER NOT NULL, "
                + COLUMN_LOCATION_ID + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + COLUMN_LOCATION_ID + ") REFERENCES "
                + LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), "
                + " UNIQUE (" + COLUMN_DATE + ", "
                + WeatherEntry.COLUMN_LOCATION_ID + ") ON CONFLICT REPLACE);";
    }

    public static String getDeleteEntry() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static String getLocationSettingFromUri(Uri uri) {
        return uri.getPathSegments().get(1);
    }

    public static long getDateFromUri(Uri uri) {
        return Long.parseLong(uri.getPathSegments().get(2));
    }

    public static long getStartingDateFromUri(Uri uri) {
        String dateString = uri.getQueryParameter(COLUMN_DATE);
        if (null != dateString && dateString.length() > 0)
            return Long.parseLong(dateString);
        else
            return 0;
    }

    public static Uri buildWeatherUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI,id);
    }

    public static Uri buildWeatherLocationUri(String locationSetting){
        return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
    }

    public static Uri buildWeatherLocationWithStartingDate(String locationSetting,long startDate){
        return CONTENT_URI.buildUpon().appendPath(locationSetting).appendQueryParameter(COLUMN_DATE,Long.toString(startDate)).build();
    }

    public static Uri buildWeatherLocationWithDate(String locationSetting,long date){
        return  CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(Long.toString(date)).build();
    }

    public static byte[] normalizeDate(long dateValue) {
        return new byte[0];
    }
}


