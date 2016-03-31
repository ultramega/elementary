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
package com.ultramegatech.widget;

import java.util.Observable;

/**
 * Maintains the state of a View's zoom level and pan position.
 *
 * @author Steve Guidetti
 */
public class ZoomState extends Observable {
    /**
     * Zoom level, 1.0 being the level at which the content fits the View
     */
    private double mZoom;

    /**
     * Coordinates of the zoom window center relative to the content
     */
    private double mPanX;
    private double mPanY;

    /**
     * Get the pan value in the X dimension.
     *
     * @return The X position
     */
    public double getPanX() {
        return mPanX;
    }

    /**
     * Get the pan value in the Y dimension.
     *
     * @return The Y position
     */
    public double getPanY() {
        return mPanY;
    }

    /**
     * Get the zoom level.
     *
     * @return The zoom level
     */
    public double getZoom() {
        return mZoom;
    }

    /**
     * Calculate the zoom value in the X dimension.
     *
     * @param aspectQuotient Quotient of content and view aspect ratios
     * @return The X zoom value
     */
    public double getZoomX(double aspectQuotient) {
        return Math.min(mZoom, mZoom * aspectQuotient);
    }

    /**
     * Calculate the zoom value in the Y dimension.
     *
     * @param aspectQuotient Quotient of content and View aspect ratios
     * @return The Y zoom value
     */
    public double getZoomY(double aspectQuotient) {
        return Math.min(mZoom, mZoom / aspectQuotient);
    }

    /**
     * Set the pan value in the X dimension.
     *
     * @param panX The X position
     */
    public void setPanX(double panX) {
        if(panX != mPanX) {
            mPanX = panX;
            setChanged();
        }
    }

    /**
     * Set the pan value in the Y dimension.
     *
     * @param panY The Y position
     */
    public void setPanY(double panY) {
        if(panY != mPanY) {
            mPanY = panY;
            setChanged();
        }
    }

    /**
     * Set the zoom level.
     *
     * @param zoom The zoom level
     */
    public void setZoom(double zoom) {
        if(zoom != mZoom) {
            mZoom = zoom;
            setChanged();
        }
    }
}
