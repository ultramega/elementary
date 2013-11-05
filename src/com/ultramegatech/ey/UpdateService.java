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

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.HttpHelper;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This background service checks for and applies data updates.
 *
 * @author Steve Guidetti
 */
public class UpdateService extends IntentService {
    private static final String TAG = "UpdateService";

    /* SharedPreferences keys */
    private static final String KEY_VERSION = "version";
    private static final String KEY_LAST_CHECK = "last_update_check";
    
    /* Minimum number of ms between update checks */
    private static final long CHECK_INTERVAL = 24 * 60 * 60 * 1000;
    
    public UpdateService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(!HttpHelper.isConnected(this)) {
            return;
        }
        
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        final long lastCheck = preferences.getLong(KEY_LAST_CHECK, 0);
        final long now = System.currentTimeMillis();
        if(now - lastCheck < CHECK_INTERVAL) {
            return;
        }
        
        Log.v(TAG, "Checking for updates");
        
        final int version = preferences.getInt(KEY_VERSION, 0);
        final int newVersion = HttpHelper.getVersion();
        if(newVersion > version) {
            Log.v(TAG, "Downloading updates...");
            
            final ContentValues[] valuesArray = fetchElementData(newVersion);
            if(valuesArray == null) {
                return;
            }

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

            preferences.edit().putInt(KEY_VERSION, newVersion).commit();
            
            Log.v(TAG, "Update completed successfully");
        }
        
        preferences.edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).commit();
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
}
