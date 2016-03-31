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
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.ultramegatech.ey.R;
import com.ultramegatech.widget.PeriodicTableLegend.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Utility class for common methods relating to chemical elements.
 *
 * @author Steve Guidetti
 */
public class ElementUtils {
    /**
     * List of string resources for each element name
     */
    private static final int[] ELEMENT_NAMES = {
            R.string.el001, R.string.el002, R.string.el003, R.string.el004, R.string.el005,
            R.string.el006, R.string.el007, R.string.el008, R.string.el009, R.string.el010,
            R.string.el011, R.string.el012, R.string.el013, R.string.el014, R.string.el015,
            R.string.el016, R.string.el017, R.string.el018, R.string.el019, R.string.el020,
            R.string.el021, R.string.el022, R.string.el023, R.string.el024, R.string.el025,
            R.string.el026, R.string.el027, R.string.el028, R.string.el029, R.string.el030,
            R.string.el031, R.string.el032, R.string.el033, R.string.el034, R.string.el035,
            R.string.el036, R.string.el037, R.string.el038, R.string.el039, R.string.el040,
            R.string.el041, R.string.el042, R.string.el043, R.string.el044, R.string.el045,
            R.string.el046, R.string.el047, R.string.el048, R.string.el049, R.string.el050,
            R.string.el051, R.string.el052, R.string.el053, R.string.el054, R.string.el055,
            R.string.el056, R.string.el057, R.string.el058, R.string.el059, R.string.el060,
            R.string.el061, R.string.el062, R.string.el063, R.string.el064, R.string.el065,
            R.string.el066, R.string.el067, R.string.el068, R.string.el069, R.string.el070,
            R.string.el071, R.string.el072, R.string.el073, R.string.el074, R.string.el075,
            R.string.el076, R.string.el077, R.string.el078, R.string.el079, R.string.el080,
            R.string.el081, R.string.el082, R.string.el083, R.string.el084, R.string.el085,
            R.string.el086, R.string.el087, R.string.el088, R.string.el089, R.string.el090,
            R.string.el091, R.string.el092, R.string.el093, R.string.el094, R.string.el095,
            R.string.el096, R.string.el097, R.string.el098, R.string.el099, R.string.el100,
            R.string.el101, R.string.el102, R.string.el103, R.string.el104, R.string.el105,
            R.string.el106, R.string.el107, R.string.el108, R.string.el109, R.string.el110,
            R.string.el111, R.string.el112, R.string.el113, R.string.el114, R.string.el115,
            R.string.el116, R.string.el117, R.string.el118
    };

    /**
     * List of string resources for each element's Wikipedia page
     */
    private static final int[] ELEMENT_WIKI = {
            R.string.wiki001, R.string.wiki002, R.string.wiki003, R.string.wiki004, R.string.wiki005,
            R.string.wiki006, R.string.wiki007, R.string.wiki008, R.string.wiki009, R.string.wiki010,
            R.string.wiki011, R.string.wiki012, R.string.wiki013, R.string.wiki014, R.string.wiki015,
            R.string.wiki016, R.string.wiki017, R.string.wiki018, R.string.wiki019, R.string.wiki020,
            R.string.wiki021, R.string.wiki022, R.string.wiki023, R.string.wiki024, R.string.wiki025,
            R.string.wiki026, R.string.wiki027, R.string.wiki028, R.string.wiki029, R.string.wiki030,
            R.string.wiki031, R.string.wiki032, R.string.wiki033, R.string.wiki034, R.string.wiki035,
            R.string.wiki036, R.string.wiki037, R.string.wiki038, R.string.wiki039, R.string.wiki040,
            R.string.wiki041, R.string.wiki042, R.string.wiki043, R.string.wiki044, R.string.wiki045,
            R.string.wiki046, R.string.wiki047, R.string.wiki048, R.string.wiki049, R.string.wiki050,
            R.string.wiki051, R.string.wiki052, R.string.wiki053, R.string.wiki054, R.string.wiki055,
            R.string.wiki056, R.string.wiki057, R.string.wiki058, R.string.wiki059, R.string.wiki060,
            R.string.wiki061, R.string.wiki062, R.string.wiki063, R.string.wiki064, R.string.wiki065,
            R.string.wiki066, R.string.wiki067, R.string.wiki068, R.string.wiki069, R.string.wiki070,
            R.string.wiki071, R.string.wiki072, R.string.wiki073, R.string.wiki074, R.string.wiki075,
            R.string.wiki076, R.string.wiki077, R.string.wiki078, R.string.wiki079, R.string.wiki080,
            R.string.wiki081, R.string.wiki082, R.string.wiki083, R.string.wiki084, R.string.wiki085,
            R.string.wiki086, R.string.wiki087, R.string.wiki088, R.string.wiki089, R.string.wiki090,
            R.string.wiki091, R.string.wiki092, R.string.wiki093, R.string.wiki094, R.string.wiki095,
            R.string.wiki096, R.string.wiki097, R.string.wiki098, R.string.wiki099, R.string.wiki100,
            R.string.wiki101, R.string.wiki102, R.string.wiki103, R.string.wiki104, R.string.wiki105,
            R.string.wiki106, R.string.wiki107, R.string.wiki108, R.string.wiki109, R.string.wiki110,
            R.string.wiki111, R.string.wiki112, R.string.wiki113, R.string.wiki114, R.string.wiki115,
            R.string.wiki116, R.string.wiki117, R.string.wiki118
    };

    /**
     * The Context
     */
    private final Context mContext;

    /**
     * Map of values to colors
     */
    private HashMap<Object, Item> mLegendMap;

    /**
     * @param context The Context
     */
    public ElementUtils(Context context) {
        mContext = context;
    }

    /**
     * Get the element color based on the category name.
     *
     * @param key The category name
     * @return Color hex value
     */
    public int getElementColor(String key) {
        if(mLegendMap == null) {
            mLegendMap = getLegendMap(mContext);
        }

        return mLegendMap.get(key).color;
    }

    /**
     * Load the element legend map from array resources.
     *
     * @param context The Context
     * @return Map of legend items
     */
    public static HashMap<Object, Item> getLegendMap(Context context) {
        final HashMap<Object, Item> colorMap = new LinkedHashMap<>();

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final String colorKey = prefs.getString("elementColors", "category");

        final Resources res = context.getResources();
        final Object[] keys;
        final int[] colorValues;
        final String[] nameValues;
        if(colorKey.equals("block")) {
            keys = res.getStringArray(R.array.ptBlocks);
            colorValues = res.getIntArray(R.array.ptBlockColors);
            nameValues = res.getStringArray(R.array.ptBlocks);
        } else {
            keys = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            colorValues = res.getIntArray(R.array.ptCategoryColors);
            nameValues = res.getStringArray(R.array.ptCategories);
        }

        if(keys != null && colorValues != null && nameValues != null) {
            for(int i = 0; i < keys.length; i++) {
                colorMap.put(keys[i], new Item(colorValues[i], nameValues[i]));
            }
        }

        return colorMap;
    }

    /**
     * Get the name of an element.
     *
     * @param number The atomic number
     * @return The string resource ID for the element name
     */
    public static int getElementName(int number) {
        if(number > ELEMENT_NAMES.length) {
            return R.string.unknown;
        }

        return ELEMENT_NAMES[number - 1];
    }

    /**
     * Get the wiki link of an element.
     *
     * @param number The atomic number
     * @return The string resource ID for the wiki link
     */
    public static int getElementWiki(int number) {
        if(number > ELEMENT_WIKI.length) {
            return getElementName(number);
        }

        return ELEMENT_WIKI[number - 1];
    }
}
