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
package com.ultramegatech.ey.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.ultramegatech.ey.R;

/**
 * Helpers for the shared preferences of the application.
 *
 * @author Steve Guidetti
 */
public class PreferenceUtils {
    /**
     * Temperature unit preference values
     */
    public static final String TEMP_K = "K";
    public static final String TEMP_C = "C";
    public static final String TEMP_F = "F";

    /**
     * Element color preference values
     */
    public static final String COLOR_CAT = "category";
    public static final String COLOR_BLOCK = "block";

    /**
     * Cached preference key names
     */
    private static String sPrefDarkTheme;
    private static String sPrefTempUnit;
    private static String sPrefElementColors;

    /**
     * Get the dark theme preference.
     *
     * @param context The Context
     * @param prefs   The SharedPreferences
     * @return Whether to use the dark theme
     */
    public static boolean getPrefDarkTheme(Context context, SharedPreferences prefs) {
        return prefs.getBoolean(getKeyDarkTheme(context), false);
    }

    /**
     * Get the key for the dark theme preference.
     *
     * @param context The Context
     * @return The key for the dark theme preference
     */
    public static String getKeyDarkTheme(Context context) {
        if(sPrefDarkTheme == null) {
            sPrefDarkTheme = context.getString(R.string.prefKeyDarkTheme);
        }
        return sPrefDarkTheme;
    }

    /**
     * Get the temperature unit preference.
     *
     * @param context The Context
     * @param prefs   The SharedPreferences
     * @return The unit to use for temperature values
     */
    public static String getPrefTempUnit(Context context, SharedPreferences prefs) {
        return prefs.getString(getKeyTempUnit(context), TEMP_K);
    }

    /**
     * Get the key for the temperature unit preference.
     *
     * @param context The Context
     * @return The key for the temperature unit preference
     */
    public static String getKeyTempUnit(Context context) {
        if(sPrefTempUnit == null) {
            sPrefTempUnit = context.getString(R.string.prefKeyTemp);
        }
        return sPrefTempUnit;
    }

    /**
     * Get the element colors preference.
     *
     * @param context The Context
     * @param prefs   The SharedPreferences
     * @return The property to use for coloring elements
     */
    public static String getPrefElementColors(Context context, SharedPreferences prefs) {
        return prefs.getString(getKeyElementColors(context), COLOR_CAT);
    }

    /**
     * Get the key for the element colors preference.
     *
     * @param context The Context
     * @return The key for the element colors preference
     */
    public static String getKeyElementColors(Context context) {
        if(sPrefElementColors == null) {
            sPrefElementColors = context.getString(R.string.prefKeyColors);
        }
        return sPrefElementColors;
    }
}
