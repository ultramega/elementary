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

import java.util.Observable;

/**
 * Maintains the state of a view's zoom level and pan position.
 *
 * @author Steve Guidetti
 */
public class ZoomState extends Observable {
    /* Zoom level, 1.0 being the level at which the content fits the view */
    private double mZoom;

    /* Coordinates of the zoom window center relative to the content */
    private double mPanX;
    private double mPanY;

    /**
     * Get the pan value in the X dimension.
     *
     * @return
     */
    public double getPanX() {
        return mPanX;
    }

    /**
     * Get the pan value in the Y dimension.
     *
     * @return
     */
    public double getPanY() {
        return mPanY;
    }

    /**
     * Get the zoom level.
     *
     * @return
     */
    public double getZoom() {
        return mZoom;
    }

    /**
     * Calculate the zoom value in the X dimension.
     *
     * @param aspectQuotient Quotient of content and view aspect ratios
     * @return
     */
    public double getZoomX(double aspectQuotient) {
        return Math.min(mZoom, mZoom * aspectQuotient);
    }

    /**
     * Calculate the zoom value in the Y dimension.
     *
     * @param aspectQuotient Quotient of content and view aspect ratios
     * @return
     */
    public double getZoomY(double aspectQuotient) {
        return Math.min(mZoom, mZoom / aspectQuotient);
    }

    /**
     * Set the pan value in the X dimension.
     *
     * @param panX
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
     * @param panY
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
     * @param zoom
     */
    public void setZoom(double zoom) {
        if(zoom != mZoom) {
            mZoom = zoom;
            setChanged();
        }
    }
}
