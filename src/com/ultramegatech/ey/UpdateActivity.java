/*
 * The MIT License (MIT)
 * Copyright © 2012 Steve Guidetti
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the “Software”), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.ultramegatech.ey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ultramegatech.ey.provider.DatabaseOpenHelper;
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.HttpHelper;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This activity serves as the entry point of the application and checks for data updates, applying
 * them as necessary.
 * 
 * @author Steve Guidetti
 */
public class UpdateActivity extends Activity {
    private static final String TAG = "UpdateActivity";
    
    /* SharedPreferences keys */
    private static final String KEY_LAST_CHECK = "last_check";
    private static final String KEY_VERSION = "version";
    private static final String KEY_DB_VERSION = "db_version";
    
    /* Minimum number of ms between update checks */
    private static final long CHECK_INTERVAL = 24 * 60 * 60 * 1000;
    
    /* True if the database has not been populated */
    private boolean mFirstRun;
    
    /* Progress indicator */
    private ProgressBar mProgressBar;
    
    /* Progress message display */
    private TextView mProgressTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.launcher);
        mProgressBar = (ProgressBar)findViewById(R.id.progress);
        mProgressTextView = (TextView)findViewById(R.id.progressText);
        
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        
        final long lastCheck = preferences.getLong(KEY_LAST_CHECK, 0);
        final long now = System.currentTimeMillis();
        
        final int dbVersion = preferences.getInt(KEY_DB_VERSION, 0);
        mFirstRun = dbVersion != DatabaseOpenHelper.VERSION;
        
        if((mFirstRun || now - lastCheck > CHECK_INTERVAL) && HttpHelper.isConnected(this)) {
            new UpdateTask().execute();
        } else if(mFirstRun) {
            showConnectionError();
        } else {
            launchMainApp();
        }
    }
    
    /**
     * Launch the main activity and finish this one.
     */
    private void launchMainApp() {
        final Intent intent = new Intent(this, PeriodicTableActivity.class);
        startActivity(intent);
        finish();
    }
    
    /**
     * Display a dialog indicating a connection error.
     */
    private void showConnectionError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.titleError)
                .setMessage(R.string.errorNoConnection)
                .setPositiveButton(R.string.buttonRetry, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton(R.string.buttonClose, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * Display a dialog indicating an installation error.
     */
    private void showInstallationError() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.titleError)
                .setMessage(R.string.errorInstallation)
                .setPositiveButton(R.string.buttonRetry, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        new UpdateTask().execute();
                    }
                })
                .setNegativeButton(R.string.buttonClose, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }
    
    /**
     * Download and parse the JSON data from the remote server.
     * 
     * @param version The version number of the data to request
     * @return Array of ContentValues objects containing element data
     */
    private static ContentValues[] fetchElementData(int version) {
        try {
            final JSONArray elementsArray = new JSONArray(HttpHelper.getElementData(version));
            final int len = elementsArray.length();

            final ContentValues[] valuesArray = new ContentValues[len];
            for(int i = 0; i < len; i++) {
                valuesArray[i] = parseJsonObject(elementsArray.getJSONObject(i));
            }
            
            return valuesArray;
        } catch(JSONException e) {
            Log.d(TAG, "Error parsing JSON");
        }
        
        return null;
    }
    
    /**
     * Convert a JSONObject to a ContentValues object.
     * 
     * @param object
     * @return ContentValues containing data from the JSONObject
     * @throws JSONException 
     */
    private static ContentValues parseJsonObject(JSONObject object) throws JSONException {
        final ContentValues values = new ContentValues();
        
        final Iterator keys = object.keys();
        while(keys.hasNext()) {
            addValue(values, object, keys.next().toString());
        }
        
        return values;
    }
    
    /**
     * Add a value from a JSONObject to a ContentValues object in the appropriate data type.
     * 
     * @param to
     * @param from
     * @param key The key of the entry to process
     * @throws JSONException 
     */
    private static void addValue(ContentValues to, JSONObject from, String key)
            throws JSONException {
        switch(Elements.getColumnType(key)) {
            case INTEGER:
            case BOOLEAN:
                to.put(key, from.getInt(key));
                break;
            case REAL:
                to.put(key, from.getDouble(key));
                break;
            case TEXT:
                to.put(key, from.getString(key));
                break;
        }
    }
    
    /**
     * AsyncTask for downloading and parsing updates in the background.
     */
    private class UpdateTask extends AsyncTask<Void, Integer, Boolean> {
        private static final int PROGRESS_GET_VERSION = 1;
        private static final int PROGRESS_FETCH_DATA = 2;
        private static final int PROGRESS_POPULATE_DATABASE = 3;

        @Override
        protected void onPreExecute() {
            mProgressBar.setProgress(0);
            mProgressTextView.setText(null);
        }
        
        @Override
        protected Boolean doInBackground(Void... arg0) {
            final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
            
            publishProgress(PROGRESS_GET_VERSION);
            
            final int version = preferences.getInt(KEY_VERSION, 0);
            final int newVersion = HttpHelper.getVersion();
            
            if(mFirstRun || newVersion > version) {
                publishProgress(PROGRESS_FETCH_DATA);
                
                final ContentValues[] valuesArray = fetchElementData(newVersion);
                if(valuesArray == null) {
                    return false;
                }
                
                publishProgress(PROGRESS_POPULATE_DATABASE);

                final ContentResolver cr = getContentResolver();
                Uri uri;
                int changed;
                for(int i = 0; i < valuesArray.length; i++) {
                    uri = ContentUris.withAppendedId(Elements.CONTENT_URI_NUMBER,
                            valuesArray[i].getAsLong(Elements.NUMBER));
                    changed = cr.update(uri, valuesArray[i], null, null);

                    if(changed == 0) {
                        cr.insert(Elements.CONTENT_URI, valuesArray[i]);
                    }
                }
                
                preferences.edit()
                        .putInt(KEY_VERSION, newVersion)
                        .putLong(KEY_LAST_CHECK, System.currentTimeMillis())
                        .putInt(KEY_DB_VERSION, DatabaseOpenHelper.VERSION)
                        .commit();
                
                return true;
            }
            
            if(!mFirstRun) {
                preferences.edit()
                        .putLong(KEY_LAST_CHECK, System.currentTimeMillis())
                        .commit();
            }
            
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            switch(values[0]) {
                case PROGRESS_GET_VERSION:
                    mProgressBar.setProgress(25);
                    mProgressTextView.setText(R.string.updateGetVersion);
                    break;
                case PROGRESS_FETCH_DATA:
                    mProgressBar.setProgress(50);
                    mProgressTextView.setText(R.string.updateFetchData);
                    break;
                case PROGRESS_POPULATE_DATABASE:
                    mProgressBar.setProgress(75);
                    mProgressTextView.setText(R.string.updatePopulateDatabase);
                    break;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mProgressBar.setProgress(100);
            mProgressTextView.setText(R.string.updateFinished);
            if(mFirstRun && !result) {
                showInstallationError();
            } else {
                launchMainApp();
            }
        }
    }
}