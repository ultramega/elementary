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

package com.ultramegatech.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;

/**
 * Renders a color legend on a PeriodicTableView. Also used to set the color value of
 * PeriodicTableBlock objects based on the legend.
 * 
 * @author Steve Guidetti
 */
public class PeriodicTableLegend extends Observable {
    /* Map of values to colors */
    private HashMap<Object, Integer> mColorMap;
    
    /* Paint used to draw backgrounds */
    private final Paint mPaint = new Paint();
    
    /* Paint used to draw text */
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    
    /* Rectangle used to draw backgrounds */
    private final Rect mRect = new Rect();
    
    /**
     * Set the color map.
     * 
     * @param hashMap Map of values to color hex values
     */
    public void setColorMap(HashMap<Object, Integer> hashMap) {
        mColorMap = hashMap;
        notifyObservers();
    }
    
    /**
     * Get the color map.
     * 
     * @return Map of values to color hex values
     */
    public HashMap<Object, Integer> getColorMap() {
        return mColorMap;
    }
    
    /**
     * Color a list of blocks.
     * 
     * @param blocks
     */
    public void colorBlocks(List<PeriodicTableBlock> blocks) {
        if(mColorMap == null) {
            return;
        }
        
        for(PeriodicTableBlock block : blocks) {
            colorBlock(block);
        }
    }
    
    /**
     * Set the color value of a block.
     * 
     * @param block 
     */
    public void colorBlock(PeriodicTableBlock block) {
        if(mColorMap == null) {
            return;
        }
        
        final Integer color = mColorMap.get(block.category);
        if(color != null) {
            block.color = color;
        }
    }
    
    /**
     * Render the legend within the specified rectangle on the specified canvas. The legend appears
     * as a grid of colored rectangles in 4 rows and a variable number of columns. Each rectangle
     * contains text declaring the value represented by the rectangle's color.
     * 
     * @param canvas Canvas on which to draw
     * @param rect Boundaries within which to draw
     */
    public void drawLegend(Canvas canvas, Rect rect) {
        if(mColorMap == null) {
            return;
        }
        final int count = mColorMap.size();
        final int rows = 4;
        final int cols = (int)Math.ceil(count / (double)rows);
        final int boxHeight = (rect.bottom - rect.top) / rows;
        final int boxWidth = (rect.right - rect.left) / cols;
        
        mTextPaint.setTextSize(boxHeight / 2);
        
        int n = 0;
        for(Entry<Object, Integer> entry : mColorMap.entrySet()) {
            mRect.top = rect.top + n % rows * boxHeight + 1;
            mRect.left = rect.left + n / rows * boxWidth + 1;
            mRect.bottom = mRect.top + boxHeight - 1;
            mRect.right = mRect.left + boxWidth - 1;
            
            mPaint.setColor(entry.getValue());
            canvas.drawRect(mRect, mPaint);
            
            canvas.drawText(entry.getKey().toString(), mRect.left + boxWidth / 20,
                    mRect.bottom - boxHeight / 2 + mTextPaint.getTextSize() / 2, mTextPaint);
            
            n++;
        }
    }
}