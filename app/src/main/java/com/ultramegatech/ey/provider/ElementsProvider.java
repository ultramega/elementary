/*
 * The MIT License (MIT)
 * Copyright © 2016 Steve Guidetti
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

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * ContentProvider for accessing the chemical element database.
 *
 * @author Steve Guidetti
 */
public class ElementsProvider extends ContentProvider {
    /**
     * Provider authority
     */
    public static final String AUTHORITY = "com.ultramegatech.ey.provider";

    /**
     * Uri matcher IDs
     */
    private static final int ELEMENTS = 1;
    private static final int ELEMENTS_NUMBER = 2;
    private static final int ELEMENTS_SYMBOL = 3;

    /**
     * Uri matcher
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH) {
        {
            addURI(AUTHORITY, "elements", ELEMENTS);
            addURI(AUTHORITY, "elements/#", ELEMENTS_NUMBER);
            addURI(AUTHORITY, "elements/*", ELEMENTS_SYMBOL);
        }
    };

    /**
     * The SQLiteOpenHelper for accessing the database
     */
    private DatabaseOpenHelper mDatabaseOpenHelper;

    @Override
    public boolean onCreate() {
        mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(Elements.TABLE_NAME);
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
                break;
            case ELEMENTS_NUMBER:
                qb.appendWhere(Elements.NUMBER + " = " + uri.getLastPathSegment());
                break;
            case ELEMENTS_SYMBOL:
                qb.appendWhere(Elements.SYMBOL + " = ");
                qb.appendWhereEscapeString(uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri.toString());
        }

        final SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        final Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        //noinspection ConstantConditions
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
                return Elements.DATA_TYPE;
            case ELEMENTS_NUMBER:
            case ELEMENTS_SYMBOL:
                return Elements.DATA_TYPE_ITEM;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new SQLException("ElementsProvider is read-only!");
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new SQLException("ElementsProvider is read-only!");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new SQLException("ElementsProvider is read-only!");
    }
}
