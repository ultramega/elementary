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

package com.ultramegatech.ey.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;
import com.ultramegatech.ey.R;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of SQLiteOpenHelper to manage the backing database.
 * 
 * @author Steve Guidetti
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseOpenHelper";
    
    /* Schema version */
    public static final int VERSION = 1;
    
    /* Data version */
    public static final int DATA_VERSION = 9;
    
    /* Database file name */
    private static final String DB_NAME = "elements.db";
    
    /* Schema for 'elements' table */
    private static final String SCHEMA_ELEMENTS = "CREATE TABLE "
            + Elements.TABLE_NAME + " ("
            + Elements._ID + " INTEGER PRIMARY KEY, "
            + Elements.NUMBER + " INTEGER, "
            + Elements.SYMBOL + " TEXT COLLATE NOCASE, "
            + Elements.NAME + " TEXT COLLATE NOCASE, "
            + Elements.GROUP + " INTEGER, "
            + Elements.PERIOD + " INTEGER, "
            + Elements.BLOCK + " TEXT COLLATE NOCASE, "
            + Elements.WEIGHT + " REAL, "
            + Elements.DENSITY + " REAL, "
            + Elements.MELT + " REAL, "
            + Elements.BOIL + " REAL, "
            + Elements.HEAT + " REAL, "
            + Elements.NEGATIVITY + " REAL, "
            + Elements.ABUNDANCE + " REAL, "
            + Elements.CATEGORY + " TEXT COLLATE NOCASE, "
            + Elements.CONFIGURATION + " TEXT, "
            + Elements.ELECTRONS + " TEXT, "
            + Elements.UNSTABLE + " INTEGER, "
            + Elements.VIDEO + " TEXT, "
            + Elements.WIKIPEDIA + " TEXT"
            + ");";
    
    private final Context mContext;

    public DatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SCHEMA_ELEMENTS);
        db.execSQL(getDataSQL());
        PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                .putInt("version", DATA_VERSION)
                .commit();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + Elements.TABLE_NAME + ";");
        onCreate(db);
    }
    
    private String getDataSQL() {
        final InputStream is = mContext.getResources().openRawResource(R.raw.elements);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            byte buf[] = new byte[1024];
            int len;
            while((len = is.read(buf)) != -1) {
                os.write(buf, 0, len);
            }
            is.close();
            os.close();
        } catch(IOException e) {
            Log.e(TAG, "Error reading raw resource!", e);
            return null;
        }
        return os.toString();
    }
}