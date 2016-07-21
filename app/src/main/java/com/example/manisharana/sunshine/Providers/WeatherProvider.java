package com.example.manisharana.sunshine.Providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.manisharana.sunshine.Data.LocationEntry;
import com.example.manisharana.sunshine.Data.WeatherDbHelper;
import com.example.manisharana.sunshine.Data.WeatherEntry;
import com.example.manisharana.sunshine.Utility;

public class WeatherProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
    static final int LOCATION = 400;

    private static SQLiteQueryBuilder sQueryBuilder;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        String tableQuery =
                WeatherEntry.TABLE_NAME + " INNER JOIN "
                        + LocationEntry.TABLE_NAME + " ON "
                        + WeatherEntry.TABLE_NAME + "."
                        + WeatherEntry.COLUMN_LOCATION_ID + " = "
                        + LocationEntry.TABLE_NAME + "."
                        + LocationEntry._ID;
        sQueryBuilder.setTables(tableQuery);
    }

    private static String selectLocationSetting =
            LocationEntry.TABLE_NAME + "." +
                    LocationEntry.COLUMN_LOC_SETTING + " = ? ";

    private static String selectLocationWithStartingDate =
            LocationEntry.TABLE_NAME + "."
                    + LocationEntry.COLUMN_LOC_SETTING + " = ? AND "
                    + WeatherEntry.TABLE_NAME + "."
                    + WeatherEntry.COLUMN_DATE + " >= ? ";

    private static String selectLocationWithSpecificDate =
            LocationEntry.TABLE_NAME + "."
                    + LocationEntry.COLUMN_LOC_SETTING + " = ? AND "
                    + WeatherEntry.TABLE_NAME + "."
                    + WeatherEntry.COLUMN_DATE + " = ? ";
    private WeatherDbHelper weatherDbHelper;


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherEntry.CONTENT_AUTHORITY;

        matcher.addURI(authority, WeatherEntry.WEATHER_PATH, WEATHER);
        matcher.addURI(authority, WeatherEntry.WEATHER_PATH + "/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, WeatherEntry.WEATHER_PATH + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        matcher.addURI(authority, LocationEntry.LOCATION_PATH, LOCATION);

        return matcher;
    }

    private Cursor getWeatherByLocationWithStartingDate(Uri uri, String[] columns, String sortOrder) {
        String location = WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherEntry.getStartingDateFromUri(uri);
        String[] selectArgs;
        String selection;

        if (date == 0) {
            selection = selectLocationSetting;
            selectArgs = new String[]{location};
        } else {
            selection = selectLocationWithStartingDate;
            selectArgs = new String[]{location, Long.toString(date)};
        }

        return sQueryBuilder.query(weatherDbHelper.getReadableDatabase(), columns, selection, selectArgs, null, null, sortOrder);
    }

    private Cursor getWeatherByLocationAndDate(Uri uri, String[] columns, String sortOrder) {
        String location = WeatherEntry.getLocationSettingFromUri(uri);
        long date = WeatherEntry.getDateFromUri(uri);
        String selectArgs[] = new String[]{location, Long.toString(date)};

        return sQueryBuilder.query(weatherDbHelper.getReadableDatabase(), columns, selectLocationWithSpecificDate, selectArgs, null, null, sortOrder);
    }

    @Override
    public boolean onCreate() {
        weatherDbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] columns, String selection, String[] selectArgs, String sortOrder) {
        Cursor retCursor;
        SQLiteDatabase readableDatabase = weatherDbHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)) {
            case WEATHER:
                retCursor = readableDatabase.query(WeatherEntry.TABLE_NAME, columns, selection, selectArgs, null, null, sortOrder);
                break;
            case LOCATION:
                retCursor = readableDatabase.query(LocationEntry.TABLE_NAME, columns, selection, selectArgs, null, null, sortOrder);
                break;
            case WEATHER_WITH_LOCATION:
                retCursor = getWeatherByLocationWithStartingDate(uri, columns, sortOrder);
                break;
            case WEATHER_WITH_LOCATION_AND_DATE:
                retCursor = getWeatherByLocationAndDate(uri, columns, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case WEATHER:
                return WeatherEntry.CONTENT_LIST_TYPE;
            case LOCATION:
                return LocationEntry.CONTENT_LIST_TYPE;
            case WEATHER_WITH_LOCATION:
                return WeatherEntry.CONTENT_LIST_TYPE;
            case WEATHER_WITH_LOCATION_AND_DATE:
                return WeatherEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (uriMatcher.match(uri)) {
            case WEATHER:
                normalizeDate(contentValues);
                long _id = db.insert(WeatherEntry.TABLE_NAME, null, contentValues);
                if (_id > 0)
                    returnUri = WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            case LOCATION:
                long loc_id = db.insert(LocationEntry.TABLE_NAME, null, contentValues);
                if (loc_id > 0)
                    returnUri = LocationEntry.buildLocationUri(loc_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case WEATHER_WITH_LOCATION:
                returnUri = null;
                break;
            case WEATHER_WITH_LOCATION_AND_DATE:
                returnUri = null;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] selectArgs) {
        final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        if (null == where) where = "1";
        int count;
        switch (uriMatcher.match(uri)) {
            case WEATHER:
                count = db.delete(WeatherEntry.TABLE_NAME, where, selectArgs);
                break;

            case LOCATION:
                count = db.delete(LocationEntry.TABLE_NAME, where, selectArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (count != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        int rowsUpdated;

        switch (uriMatcher.match(uri)) {
            case WEATHER:
                normalizeDate(values);
                rowsUpdated = db.update(WeatherEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case LOCATION:
                rowsUpdated = db.update(LocationEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private void normalizeDate(ContentValues values) {
        if (values.containsKey(WeatherEntry.COLUMN_DATE)) {
            long dateValue = values.getAsLong(WeatherEntry.COLUMN_DATE);
            values.put(WeatherEntry.COLUMN_DATE, Utility.normalizeDate(dateValue));
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = weatherDbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);

        switch (match) {
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
