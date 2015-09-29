package com.saiswathipujari.rentals;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by saiswathipujari on 9/11/15.
 */
public class MyCustomSuggestionProvider extends ContentProvider{

    private static final String LOG_TAG = "ExampleApp";
    JSONObject object;
    String query;
    public static final String AUTHORITY = "com.saiswathipujari.rentals.MyCustomSuggestionProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/search");

    // UriMatcher constant for search suggestions
    private static final int SEARCH_SUGGEST = 1;

    private static final UriMatcher uriMatcher;

    private static final String[] SEARCH_SUGGEST_COLUMNS = {
            BaseColumns._ID,
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_TEXT_2,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
    };

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
    }
    @Override
    public boolean onCreate() {
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Log.d(LOG_TAG, "query = " + uri);

        // Use the UriMatcher to see what kind of query we have
        switch (uriMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                Log.d(LOG_TAG, "Search suggestions requested.");
                query = selectionArgs[0];
                if (query == null || query.length() == 0) {
                    return null;
                }
                try {
                    query = query.replace(",", "%2C").replace(" ","+");
                    return getSuggestions(query);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);

        }
    }

    private Cursor getSuggestions(String query) throws JSONException {

        new RetrieveInfoTask().execute();
//
        MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS, 1);

        JSONArray sugg = object.getJSONArray("suggestions");

        for (int i = 0; i < sugg.length(); i++) {
            JSONObject explrObject = sugg.getJSONObject(i);
            cursor.addRow(new String[]{
                    "1", explrObject.getString("value"), null, "content_id"
            });
        }

        return cursor;
    }

    //Background process to get results from api
    class RetrieveInfoTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... urls) {

            try {

                URL url = new URL("http://api.zoopla.co.uk/api/v1/geo_autocomplete.js?search_type=listings&search_term="+query+"&api_key=3fz5vsxvqd5a7ccfsj4cmwae");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }


        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);

            // do something with the feed

            try {
                object = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                return SearchManager.SUGGEST_MIME_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }

    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        throw new UnsupportedOperationException();
    }
}
