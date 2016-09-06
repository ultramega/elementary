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
