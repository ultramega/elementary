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
import android.content.res.Resources;
import com.ultramegatech.ey.R;
import java.util.HashMap;

/**
 * Utility class for common methods relating to chemical elements.
 * 
 * @author Steve Guidetti
 */
public class ElementUtils {
    private Context mContext;
    
    /* Map of values to colors */
    private HashMap<String, Integer> mColorMap;
    
    /**
     * Constructor
     * 
     * @param context Context
     */
    public ElementUtils(Context context) {
        mContext = context;
    }
    
    /**
     * Get the element color based on the category name.
     * 
     * @param context
     * @param key The category name
     * @return Color hex value
     */
    public int getElementColor(String key) {
        if(mColorMap == null) {
            mColorMap = new HashMap<String, Integer>();
            
            final Resources res = mContext.getResources();
            final String[] colorKeys = res.getStringArray(R.array.ptFamilies);
            final int[] colorValues = res.getIntArray(R.array.ptColors);
            
            if(colorKeys != null && colorValues != null && colorValues.length >= colorKeys.length) {
                for(int i = 0; i < colorKeys.length; i++) {
                    mColorMap.put(colorKeys[i], colorValues[i]);
                }
            }
        }
        
        return mColorMap.get(key);
    }
}