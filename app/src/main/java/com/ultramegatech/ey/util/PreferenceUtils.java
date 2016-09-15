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

import android.content.SharedPreferences;

import com.ultramegatech.ey.provider.Elements;

/**
 * Helpers for the shared preferences of the application.
 *
 * @author Steve Guidetti
 */
public class PreferenceUtils {
    /**
     * The keys for the preference
     */
    public static final String KEY_DARK_THEME = "theme";
    public static final String KEY_TEMP_UNITS = "tempUnit";
    public static final String KEY_ELEMENT_COLORS = "elementColors";
    public static final String KEY_SUBTEXT_VALUE = "subtextValue";
    public static final String KEY_SHOW_CONTROLS = "showControls";

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
     * Get the value of the dark theme preference.
     *
     * @param prefs The SharedPreferences
     * @return Whether to use the dark theme
     */
    public static boolean getPrefDarkTheme(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_DARK_THEME, false);
    }

    /**
     * Get the value of the temperature unit preference.
     *
     * @param prefs The SharedPreferences
     * @return The unit to use for temperature values
     */
    public static String getPrefTempUnit(SharedPreferences prefs) {
        return prefs.getString(KEY_TEMP_UNITS, TEMP_K);
    }

    /**
     * Get the value of the element colors preference.
     *
     * @param prefs The SharedPreferences
     * @return The property to use for coloring elements
     */
    public static String getPrefElementColors(SharedPreferences prefs) {
        return prefs.getString(KEY_ELEMENT_COLORS, COLOR_CAT);
    }

    /**
     * Set value of the element colors preference.
     *
     * @param prefs The SharedPreferences
     * @param value The value
     */
    public static void setPrefElementColors(SharedPreferences prefs, String value) {
        prefs.edit().putString(KEY_ELEMENT_COLORS, value).apply();
    }

    /**
     * Get value of the block subtext value preference.
     *
     * @param prefs The SharedPreferences
     * @return The value of the block subtext value preference
     */
    public static String getPrefSubtextValue(SharedPreferences prefs) {
        return prefs.getString(KEY_SUBTEXT_VALUE, Elements.WEIGHT);
    }

    /**
     * Set value of the block subtext value preference.
     *
     * @param prefs The SharedPreferences
     * @param value The value
     */
    public static void setPrefSubtextValue(SharedPreferences prefs, String value) {
        prefs.edit().putString(KEY_SUBTEXT_VALUE, value).apply();
    }

    /**
     * Get the value of the show controls preference.
     *
     * @param prefs The SharedPreferences
     * @return The value of the show controls preference
     */
    public static boolean getPrefShowControls(SharedPreferences prefs) {
        return prefs.getBoolean(KEY_SHOW_CONTROLS, true);
    }
}
