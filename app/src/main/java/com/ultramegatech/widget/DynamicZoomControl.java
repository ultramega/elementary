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

import android.os.Handler;
import android.os.SystemClock;

import com.ultramegatech.util.GlideDynamics;

import java.util.Observable;
import java.util.Observer;

/**
 * Controls a ZoomState, enforcing limits and allowing flinging.
 *
 * @author Steve Guidetti
 */
class DynamicZoomControl implements Observer {
    /**
     * Zoom level limits
     */
    private static final int MIN_ZOOM = 1;
    private static final int MAX_ZOOM = 8;

    /**
     * Tolerances for determining if objects are at rest
     */
    private static final double REST_VELOCITY_TOLERANCE = 0.004;
    private static final double REST_POSITION_TOLERANCE = 0.01;

    /**
     * Target FPS for animations
     */
    private static final int FPS = 50;

    /**
     * The AspectQuotient of the object being controlled
     */
    private AspectQuotient mAspectQuotient;

    /**
     * ZoomState being controlled
     */
    private final ZoomState mState = new ZoomState();

    /**
     * Dynamic object for panning in the X dimension
     */
    private final GlideDynamics mPanDynamicsX = new GlideDynamics();

    /**
     * Dynamic object for panning in the Y dimension
     */
    private final GlideDynamics mPanDynamicsY = new GlideDynamics();

    /**
     * Pan limits in the X dimension
     */
    private double mPanMinX;
    private double mPanMaxX;

    /**
     * Pan limits in the Y dimension
     */
    private double mPanMinY;
    private double mPanMaxY;

    /**
     * Handler for posting the animations
     */
    private final Handler mHandler = new Handler();

    /**
     * Runnable to animate flings
     */
    private final Runnable mUpdateRunnable = new Runnable() {
        public void run() {
            final long startTime = SystemClock.uptimeMillis();
            mPanDynamicsX.update(startTime);
            mPanDynamicsY.update(startTime);
            final boolean isAtRest =
                    mPanDynamicsX.isAtRest(REST_VELOCITY_TOLERANCE, REST_POSITION_TOLERANCE)
                            && mPanDynamicsY.isAtRest(REST_VELOCITY_TOLERANCE,
                            REST_POSITION_TOLERANCE);
            mState.setPanX(mPanDynamicsX.getPosition());
            mState.setPanY(mPanDynamicsY.getPosition());

            if(!isAtRest) {
                final long stopTime = SystemClock.uptimeMillis();
                mHandler.postDelayed(mUpdateRunnable, 1000 / FPS - (stopTime - startTime));
            }

            mState.notifyObservers();
        }
    };

    public DynamicZoomControl() {
        mPanDynamicsX.setFriction(2.0);
        mPanDynamicsY.setFriction(2.0);
    }

    /**
     * Get the ZoomState.
     *
     * @return The ZoomState
     */
    public ZoomState getZoomState() {
        return mState;
    }

    /**
     * Set the AspectQuotient.
     *
     * @param aspectQuotient The AspectQuotient
     */
    public void setAspectQuotient(AspectQuotient aspectQuotient) {
        if(mAspectQuotient != null) {
            mAspectQuotient.deleteObserver(this);
        }

        mAspectQuotient = aspectQuotient;
        mAspectQuotient.addObserver(this);
    }

    /**
     * Zoom.
     *
     * @param f Zoom level
     * @param x X coordinate of focal point
     * @param y Y coordinate of focal point
     */
    public void zoom(double f, double x, double y) {
        final double aspectQuotient = mAspectQuotient.get();

        final double prevZoomX = mState.getZoomX(aspectQuotient);
        final double prevZoomY = mState.getZoomY(aspectQuotient);

        mState.setZoom(mState.getZoom() * f);
        limitZoom();

        final double newZoomX = mState.getZoomX(aspectQuotient);
        final double newZoomY = mState.getZoomY(aspectQuotient);

        mState.setPanX(mState.getPanX() + (x - 0.5) * (1f / prevZoomX - 1.0 / newZoomX));
        mState.setPanY(mState.getPanY() + (y - 0.5) * (1f / prevZoomY - 1.0 / newZoomY));

        updatePanLimits();

        mState.notifyObservers();
    }

    /**
     * Pan.
     *
     * @param dx Distance to pan in the X dimension
     * @param dy Distance to pan in the Y dimension
     */
    public void pan(double dx, double dy) {
        final double aspectQuotient = mAspectQuotient.get();

        final double newPanX = mState.getPanX() + dx / mState.getZoomX(aspectQuotient);
        final double newPanY = mState.getPanY() + dy / mState.getZoomY(aspectQuotient);

        mState.setPanX(newPanX);
        mState.setPanY(newPanY);
        limitPan();

        mState.notifyObservers();
    }

    /**
     * Ensure the zoom level is within limits.
     */
    private void limitZoom() {
        if(mState.getZoom() < MIN_ZOOM) {
            mState.setZoom(MIN_ZOOM);
        } else if(mState.getZoom() > MAX_ZOOM) {
            mState.setZoom(MAX_ZOOM);
        }
    }

    /**
     * Ensure the pan position is within the boundaries.
     */
    private void limitPan() {
        if(mState.getPanX() < mPanMinX) {
            mState.setPanX(mPanMinX);
        } else if(mState.getPanX() > mPanMaxX) {
            mState.setPanX(mPanMaxX);
        }
        if(mState.getPanY() < mPanMinY) {
            mState.setPanY(mPanMinY);
        } else if(mState.getPanY() > mPanMaxY) {
            mState.setPanY(mPanMaxY);
        }
    }

    /**
     * Start the animation for panning flings.
     *
     * @param vx Velocity in the X dimension
     * @param vy Velocity in the Y dimension
     */
    public void startFling(double vx, double vy) {
        final double aspectQuotient = mAspectQuotient.get();
        final long now = SystemClock.uptimeMillis();

        mPanDynamicsX.setState(mState.getPanX(), vx / mState.getZoomX(aspectQuotient), now);
        mPanDynamicsY.setState(mState.getPanY(), vy / mState.getZoomY(aspectQuotient), now);

        mPanDynamicsX.setMinPosition(mPanMinX);
        mPanDynamicsX.setMaxPosition(mPanMaxX);
        mPanDynamicsY.setMinPosition(mPanMinY);
        mPanDynamicsY.setMaxPosition(mPanMaxY);

        mHandler.post(mUpdateRunnable);
    }

    /**
     * Stop the fling animation.
     */
    public void stopFling() {
        mHandler.removeCallbacks(mUpdateRunnable);
    }

    /**
     * Get the max delta of pan from center position.
     *
     * @param zoom Zoom value
     * @return Max delta of pan
     */
    private double getMaxPanDelta(double zoom) {
        return Math.max(0f, 0.5f * ((zoom - 1) / zoom));
    }

    /**
     * Update limit values for panning.
     */
    private void updatePanLimits() {
        final double aspectQuotient = mAspectQuotient.get();

        final double zoomX = mState.getZoomX(aspectQuotient);
        final double zoomY = mState.getZoomY(aspectQuotient);

        mPanMinX = 0.5 - getMaxPanDelta(zoomX);
        mPanMaxX = 0.5 + getMaxPanDelta(zoomX);
        mPanMinY = 0.5 - getMaxPanDelta(zoomY);
        mPanMaxY = 0.5 + getMaxPanDelta(zoomY);
    }

    @Override
    public void update(Observable observable, Object data) {
        limitZoom();
        updatePanLimits();
        limitPan();
    }
}
