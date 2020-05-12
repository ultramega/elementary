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
package com.ultramegatech.ey.widget;

import android.content.Context;
import android.os.SystemClock;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;

/**
 * Handles interpolating between arbitrary zoom levels.
 *
 * @author Steve Guidetti
 */
class Zoomer {
    /**
     * The interpolator to use
     */
    @NonNull
    private final Interpolator mInterpolator;

    /**
     * The animation duration in milliseconds
     */
    private final int mAnimationDuration;

    /**
     * Whether the current operation has finished
     */
    private boolean mZoomInProgress;

    /**
     * The start time of the current operation
     */
    private long mStartTime;

    /**
     * The starting zoom level
     */
    private float mStartZoom;

    /**
     * The target zoom level
     */
    private float mTargetZoom;

    /**
     * The current zoom level
     */
    private float mCurrentZoom;

    /**
     * @param context The Context
     */
    Zoomer(@NonNull Context context) {
        mInterpolator = new DecelerateInterpolator();
        mAnimationDuration = context.getResources()
                .getInteger(android.R.integer.config_shortAnimTime);
    }

    /**
     * Start a zoom operation.
     *
     * @param currentZoom The current zoom level
     * @param targetZoom  The target zoom level
     */
    void startZoom(float currentZoom, float targetZoom) {
        mStartTime = SystemClock.elapsedRealtime();
        mStartZoom = mCurrentZoom = currentZoom;
        mTargetZoom = targetZoom;

        mZoomInProgress = true;
    }

    /**
     * Stop the current operation
     */
    void forceFinished() {
        mZoomInProgress = false;
    }

    /**
     * Check whether a zoom operation has finished.
     *
     * @return Whether the zoom operation has finished
     */
    boolean isFinished() {
        return !mZoomInProgress;
    }

    /**
     * Compute the current zoom level.
     *
     * @return Whether a zoom operation is in progress
     */
    boolean computeZoom() {
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
        mCurrentZoom =
                mStartZoom + (mTargetZoom - mStartZoom) * mInterpolator.getInterpolation(progress);
        return true;
    }

    /**
     * Get the current zoom level.
     *
     * @return The current zoom level
     */
    float getCurrZoom() {
        return mCurrentZoom;
    }
}
