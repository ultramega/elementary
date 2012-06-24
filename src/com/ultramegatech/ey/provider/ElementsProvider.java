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

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * ContentProvider for accessing the chemical element database.
 * 
 * @author Steve Guidetti
 */
public class ElementsProvider extends ContentProvider {
    /* Provider authority */
    public static final String AUTHORITY = "com.ultramegatech.ey.provider";
    
    /* Uri matcher ids */
    private static final int ELEMENTS = 1;
    private static final int ELEMENTS_ID = 2;
    private static final int ELEMENTS_NUMBER = 3;
    private static final int ELEMENTS_SYMBOL = 4;
    private static final int ELEMENTS_FILTER = 5;
    
    /* Uri matcher */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "elements", ELEMENTS);
        sUriMatcher.addURI(AUTHORITY, "elements/#", ELEMENTS_ID);
        sUriMatcher.addURI(AUTHORITY, "elements/n/#", ELEMENTS_NUMBER);
        sUriMatcher.addURI(AUTHORITY, "elements/s/*", ELEMENTS_SYMBOL);
        sUriMatcher.addURI(AUTHORITY, "elements/filter/*", ELEMENTS_FILTER);
    }
    
    /* SQLiteOpenHelper */
    private DatabaseOpenHelper mDatabaseOpenHelper;
    
    @Override
    public boolean onCreate() {
        mDatabaseOpenHelper = new DatabaseOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        final SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
                qb.setTables(Elements.TABLE_NAME);
                break;
            case ELEMENTS_ID:
                qb.setTables(Elements.TABLE_NAME);
                qb.appendWhere(Elements._ID + " = " + uri.getLastPathSegment());
                break;
            case ELEMENTS_NUMBER:
                qb.setTables(Elements.TABLE_NAME);
                qb.appendWhere(Elements.NUMBER + " = " + uri.getLastPathSegment());
                break;
            case ELEMENTS_SYMBOL:
                qb.setTables(Elements.TABLE_NAME);
                qb.appendWhere(Elements.SYMBOL + " = ");
                qb.appendWhereEscapeString(uri.getLastPathSegment());
                break;
            case ELEMENTS_FILTER:
                qb.setTables(Elements.TABLE_NAME);
                qb.appendWhere(Elements.NAME + " LIKE ");
                qb.appendWhereEscapeString(uri.getLastPathSegment() + "%");
                qb.appendWhere(" OR " + Elements.SYMBOL + " LIKE ");
                qb.appendWhereEscapeString(uri.getLastPathSegment() + "%");
                break;
            default:
                throw new IllegalArgumentException("Invalid URI: " + uri.toString());
        }
        
        final SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        final Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null,
                sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
            case ELEMENTS_FILTER:
                return Elements.DATA_TYPE;
            case ELEMENTS_ID:
            case ELEMENTS_NUMBER:
            case ELEMENTS_SYMBOL:
                return Elements.DATA_TYPE_ITEM;
            default:
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table;
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
                table = Elements.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Invalid URI");
        }
        
        final SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        final long rowId = db.insert(table, null, values);
        if(rowId > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, rowId);
        }
        
        throw new SQLException("Failed to insert row into " + uri.toString());
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table;
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
                table = Elements.TABLE_NAME;
                break;
            case ELEMENTS_ID:
                table = Elements.TABLE_NAME;
                final String id = uri.getLastPathSegment();
                if(selection == null) {
                    selection = Elements._ID + " = " + id;
                } else {
                    selection += " AND " + Elements._ID + " = " + id;
                }
                break;
            case ELEMENTS_NUMBER:
                table = Elements.TABLE_NAME;
                final String number = uri.getLastPathSegment();
                if(selection == null) {
                    selection = Elements.NUMBER + " = " + number;
                } else {
                    selection += " AND " + Elements.NUMBER + " = " + number;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI");
        }
        
        final SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        final int rowsAffected = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table;
        switch(sUriMatcher.match(uri)) {
            case ELEMENTS:
                table = Elements.TABLE_NAME;
                break;
            case ELEMENTS_ID:
                table = Elements.TABLE_NAME;
                final String id = uri.getLastPathSegment();
                if(selection == null) {
                    selection = Elements._ID + " = " + id;
                } else {
                    selection += " AND " + Elements._ID + " = " + id;
                }
                break;
            case ELEMENTS_NUMBER:
                table = Elements.TABLE_NAME;
                final String number = uri.getLastPathSegment();
                if(selection == null) {
                    selection = Elements.NUMBER + " = " + number;
                } else {
                    selection += " AND " + Elements.NUMBER + " = " + number;
                }
                break;
            default:
                throw new IllegalArgumentException("Invalid URI");
        }
        
        final SQLiteDatabase db = mDatabaseOpenHelper.getWritableDatabase();
        final int rowsAffected = db.update(table, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }
}