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
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Helpers for the shared preferences of the application.
 *
 * @author Steve Guidetti
 */
@SuppressWarnings("unused,WeakerAccess")
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
     * Subtext value values
     */
    public static final String SUBTEXT_WEIGHT = "w";
    public static final String SUBTEXT_DENSITY = "dens";
    public static final String SUBTEXT_MELT = "melt";
    public static final String SUBTEXT_BOIL = "boil";
    public static final String SUBTEXT_HEAT = "heat";
    public static final String SUBTEXT_NEGATIVITY = "neg";
    public static final String SUBTEXT_ABUNDANCE = "ab";

    private static boolean sPrefDarkTheme;
    private static String sPrefTempUnit;
    private static String sPrefElementColors;
    private static String sPrefSubtextValue;
    private static boolean sPrefShowControls;

    private static SharedPreferences sPreferences;

    private static final SharedPreferences.OnSharedPreferenceChangeListener LISTENER =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                      String key) {
                    update(sharedPreferences, key);
                }
            };

    public static void setup(@NonNull Context context) {
        sPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sPreferences.registerOnSharedPreferenceChangeListener(LISTENER);

        sPrefDarkTheme = sPreferences.getBoolean(KEY_DARK_THEME, false);
        sPrefTempUnit = sPreferences.getString(KEY_TEMP_UNITS, TEMP_K);
        sPrefElementColors = sPreferences.getString(KEY_ELEMENT_COLORS, COLOR_CAT);
        sPrefSubtextValue = sPreferences.getString(KEY_SUBTEXT_VALUE, SUBTEXT_WEIGHT);
        sPrefShowControls = sPreferences.getBoolean(KEY_SHOW_CONTROLS, true);
    }

    private static void update(@NonNull SharedPreferences prefs, @NonNull String key) {
        switch(key) {
            case KEY_DARK_THEME:
                sPrefDarkTheme = prefs.getBoolean(key, false);
                break;
            case KEY_TEMP_UNITS:
                sPrefTempUnit = prefs.getString(KEY_TEMP_UNITS, TEMP_K);
                break;
            case KEY_ELEMENT_COLORS:
                sPrefElementColors = prefs.getString(KEY_ELEMENT_COLORS, COLOR_CAT);
                break;
            case KEY_SUBTEXT_VALUE:
                sPrefSubtextValue = prefs.getString(KEY_SUBTEXT_VALUE, SUBTEXT_WEIGHT);
                break;
            case KEY_SHOW_CONTROLS:
                sPrefShowControls = prefs.getBoolean(KEY_SHOW_CONTROLS, true);
                break;
        }
    }

    /**
     * Get the value of the dark theme preference.
     *
     * @return Whether to use the dark theme
     */
    public static boolean getPrefDarkTheme() {
        return sPrefDarkTheme;
    }

    /**
     * Get the value of the temperature unit preference.
     *
     * @return The unit to use for temperature values
     */
    @NonNull
    public static String getPrefTempUnit() {
        return sPrefTempUnit;
    }

    /**
     * Get the value of the element colors preference.
     *
     * @return The property to use for coloring elements
     */
    @NonNull
    public static String getPrefElementColors() {
        return sPrefElementColors;
    }

    /**
     * Set value of the element colors preference.
     *
     * @param value The value
     */
    public static void setPrefElementColors(@NonNull String value) {
        sPrefElementColors = value;
        sPreferences.edit().putString(KEY_ELEMENT_COLORS, value).apply();
    }

    /**
     * Get value of the block subtext value preference.
     *
     * @return The value of the block subtext value preference
     */
    @NonNull
    public static String getPrefSubtextValue() {
        return sPrefSubtextValue;
    }

    /**
     * Set value of the block subtext value preference.
     *
     * @param value The value
     */
    public static void setPrefSubtextValue(@NonNull String value) {
        sPrefSubtextValue = value;
        sPreferences.edit().putString(KEY_SUBTEXT_VALUE, value).apply();
    }

    /**
     * Get the value of the show controls preference.
     *
     * @return The value of the show controls preference
     */
    public static boolean getPrefShowControls() {
        return sPrefShowControls;
    }
}
