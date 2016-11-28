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
package com.ultramegatech.ey.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;

import com.ultramegatech.ey.R;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;

import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Renders a color legend on a PeriodicTableView.
 *
 * @author Steve Guidetti
 */
class PeriodicTableLegend {
    /**
     * Map of key values to labels
     */
    @NonNull
    private final HashMap<String, String> mMap = new HashMap<>();

    /**
     * Paint used to draw backgrounds
     */
    @NonNull
    private final Paint mPaint = new Paint();

    /**
     * Paint used to draw text
     */
    @NonNull
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * Rectangle used to draw backgrounds
     */
    @NonNull
    private final Rect mRect = new Rect();

    /**
     * @param context The Context
     */
    PeriodicTableLegend(@NonNull Context context) {
        invalidate(context);
    }

    /**
     * Load the legend data from resources.
     *
     * @param context The Context
     */
    void invalidate(@NonNull Context context) {
        final Resources res = context.getResources();
        final String[] keys;
        final String[] nameValues;
        if(PreferenceUtils.COLOR_BLOCK.equals(PreferenceUtils.getPrefElementColors())) {
            keys = res.getStringArray(R.array.ptBlocks);
            nameValues = res.getStringArray(R.array.ptBlocks);
        } else {
            keys = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            nameValues = res.getStringArray(R.array.ptCategories);
        }

        mMap.clear();
        for(int i = 0; i < keys.length; i++) {
            mMap.put(keys[i], nameValues[i]);
        }
    }

    /**
     * Render the legend within the specified rectangle on the specified Canvas. The legend appears
     * as a grid of colored rectangles in 4 rows and a variable number of columns. Each rectangle
     * contains text declaring the value represented by the rectangle's color.
     *
     * @param canvas Canvas on which to draw
     * @param rect   Boundaries within which to draw
     */
    void drawLegend(@NonNull Canvas canvas, @NonNull Rect rect) {
        final int count = mMap.size();
        final int rows = 4;
        final int cols = (int)Math.ceil(count / (double)rows);
        final int boxHeight = (rect.bottom - rect.top) / rows;
        final int boxWidth = (rect.right - rect.left) / cols;

        mTextPaint.setTextSize(boxHeight / 2);

        int n = 0;
        for(Entry<String, String> entry : mMap.entrySet()) {
            mRect.top = rect.top + n % rows * boxHeight + 1;
            mRect.left = rect.left + n / rows * boxWidth + 1;
            mRect.bottom = mRect.top + boxHeight - 1;
            mRect.right = mRect.left + boxWidth - 1;

            mPaint.setColor(ElementUtils.getKeyColor(entry.getKey()));
            canvas.drawRect(mRect, mPaint);

            canvas.drawText(entry.getValue(), mRect.left + boxWidth / 20,
                    mRect.bottom - boxHeight / 2 + mTextPaint.getTextSize() / 2, mTextPaint);

            n++;
        }
    }
}
