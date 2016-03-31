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
 * Stores the quotient between the aspect ratios of the container and a View.
 *
 * @author Steve Guidetti
 */
class AspectQuotient extends Observable {
    /**
     * Current AspectQuotient
     */
    private double mAspectQuotient;

    /**
     * Get the AspectQuotient.
     *
     * @return The AspectQuotient
     */
    public double get() {
        return mAspectQuotient;
    }

    /**
     * Calculate aspect quotient based on supplied View and content dimensions.
     *
     * @param viewWidth     Width of container
     * @param viewHeight    Height of container
     * @param contentWidth  Width of content
     * @param contentHeight Height of content
     */
    public void updateAspectQuotient(double viewWidth, double viewHeight, double contentWidth,
                                     double contentHeight) {
        final double aspectQuotient = (contentWidth / contentHeight) / (viewWidth / viewHeight);
        if(aspectQuotient != mAspectQuotient) {
            mAspectQuotient = aspectQuotient;
            setChanged();
        }
    }
}
