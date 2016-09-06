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
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

/**
 * Handles interpolating zoom operations from 1x to arbitrary zoom levels.
 *
 * @author Steve Guidetti
 */
public class Zoomer {
    /**
     * The interpolator to use
     */
    private Interpolator mInterpolator;

    /**
     * The animation duration in milliseconds
     */
    private int mAnimationDuration;

    /**
     * Whether the current operation has finished
     */
    private boolean mZoomInProgress;

    /**
     * The current zoom level
     */
    private float mCurrentZoom = 1f;

    /**
     * The start time of the current operation
     */
    private long mStartTime;

    /**
     * The target zoom level
     */
    private float mTargetZoom;

    /**
     * @param context The Context
     */
    public Zoomer(Context context) {
        mInterpolator = new DecelerateInterpolator();
        mAnimationDuration = context.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
    }

    /**
     * Stop the current operation
     */
    public void forceFinished() {
        mZoomInProgress = false;
    }

    /**
     * Start a zoom operation.
     *
     * @param targetZoom The target zoom level
     */
    public void startZoom(float targetZoom) {
        mStartTime = SystemClock.elapsedRealtime();
        mTargetZoom = targetZoom;

        mZoomInProgress = true;
        mCurrentZoom = 1f;
    }

    /**
     * Compute the current zoom level.
     *
     * @return Whether a zoom operation is in progress
     */
    public boolean computeZoom() {
        if(!mZoomInProgress) {
            return false;
        }

        final long timeElapsed = SystemClock.elapsedRealtime() - mStartTime;
        if(timeElapsed >= mAnimationDuration) {
            mZoomInProgress = false;
            mCurrentZoom = mTargetZoom;
            return true;
        }

        final float progress = timeElapsed * 1f / mAnimationDuration;
        mCurrentZoom = mTargetZoom * mInterpolator.getInterpolation(progress);
        return true;
    }

    /**
     * Get the current zoom level.
     *
     * @return The current zoom level
     */
    public float getCurrZoom() {
        return mCurrentZoom;
    }
}
