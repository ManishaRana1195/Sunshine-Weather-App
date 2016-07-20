package com.example.manisharana.sunshine.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class LocationEntry implements BaseColumns {
    public static final String TABLE_NAME = "location";
    public static final String COLUMN_LOC_SETTING = "loc_setting";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_CITY_NAME = "city_name";

    public static final String LOCATION_PATH = "location";
    public static final String CONTENT_AUTHORITY = "com.example.manisharana.sunshine";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+ CONTENT_AUTHORITY);

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(LOCATION_PATH).build();
    public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE+"/"+ CONTENT_AUTHORITY+"/"+LOCATION_PATH;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE+"/"+ CONTENT_AUTHORITY+"/"+LOCATION_PATH;


    public static String getCreateLocationEntry() {
        return "CREATE TABLE " + TABLE_NAME + " ( "
                + _ID + " INTEGER PRIMARY KEY"
                + COLUMN_LOC_SETTING + " TEXT UNIQUE NOT NULL, "
                + COLUMN_CITY_NAME + " TEXT NOT NULL, "
                + COLUMN_LATITUDE + " REAL NOT NULL, "
                + COLUMN_LONGITUDE + " REAL NOT NULL );";
    }

    public static Uri buildLocationUri(long id){
        return ContentUris.withAppendedId(CONTENT_URI,id);
    }

    public static String getDeleteEntry() {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static long saveLocationEntry(String locationSetting, String cityName, double latitude, double longitude){
        return -1;
    }
}
