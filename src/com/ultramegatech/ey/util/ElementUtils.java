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
    private final Context mContext;
        
    /* Map of values to colors */
    private HashMap<Object, Item> mLegendMap;
    
    /**
     * Constructor
     * 
     * @param context
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
     * @param context
     * @return 
     */
    public static HashMap<Object, Item> getLegendMap(Context context) {
        final HashMap<Object, Item> colorMap = new LinkedHashMap<Object, Item>();
        
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
            keys = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
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
}