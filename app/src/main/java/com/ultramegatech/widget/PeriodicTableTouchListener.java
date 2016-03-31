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

import android.content.Context;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

/**
 * Custom OnTouchListener for PeriodicTableView. Drag or fling to pan, long press to zoom, and tap
 * to click on a block.
 *
 * @author Steve Guidetti
 */
public class PeriodicTableTouchListener implements View.OnTouchListener {
    /**
     * Listener modes. Before the View is touched the listener is in the UNDEFINED mode. Once touch
     * starts it can enter either one of the other two modes: If the user scrolls over the View the
     * listener will enter PAN mode, if the user lets their finger rest and makes a long press the
     * listener will enter ZOOM mode.
     */
    public enum Mode {
        UNDEFINED, PAN, ZOOM
    }

    /**
     * Time of tactile feedback vibration when entering zoom mode
     */
    private static final long VIBRATE_TIME = 50;

    /**
     * DynamicZoomControl to manipulate
     */
    private DynamicZoomControl mZoomControl;

    /**
     * Current listener mode
     */
    private Mode mMode = Mode.UNDEFINED;

    /**
     * Coordinates of previous touch event
     */
    private double mX;
    private double mY;

    /**
     * Coordinates of latest down event
     */
    private double mDownX;
    private double mDownY;

    /**
     * Velocity tracker for touch events
     */
    private VelocityTracker mVelocityTracker;

    /**
     * Distance a touch can travel to be considered dragging
     */
    private final int mScaledTouchSlop;

    /**
     * Duration of a long press
     */
    private final int mLongPressTimeout;

    /**
     * Vibrator for tactile feedback
     */
    private final Vibrator mVibrator;

    /**
     * Maximum velocity of a fling
     */
    private final int mScaledMaximumFlingVelocity;

    /**
     * Runnable to enter zoom mode
     */
    private final Runnable mLongPressRunnable = new Runnable() {
        public void run() {
            mMode = Mode.ZOOM;
            mVibrator.vibrate(VIBRATE_TIME);
        }
    };

    /**
     * @param context The Context
     */
    public PeriodicTableTouchListener(Context context) {
        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScaledMaximumFlingVelocity =
                ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    /**
     * Set the DynamicZoomControl.
     *
     * @param control The DynamicZoomControl
     */
    public void setZoomControl(DynamicZoomControl control) {
        mZoomControl = control;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final PeriodicTableView periodicTableView = (PeriodicTableView)v;

        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        if(mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch(action) {
            case MotionEvent.ACTION_DOWN:
                periodicTableView.onDown(x, y);
                mZoomControl.stopFling();
                v.postDelayed(mLongPressRunnable, mLongPressTimeout);
                mDownX = x;
                mDownY = y;
                mX = x;
                mY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                final double dx = (x - mX) / v.getWidth();
                final double dy = (y - mY) / v.getHeight();

                if(mMode == Mode.ZOOM) {
                    periodicTableView.clearSelection();
                    mZoomControl.zoom(Math.pow(20, -dy),
                            mDownX / v.getWidth(), mDownY / v.getHeight());
                } else if(mMode == Mode.PAN) {
                    periodicTableView.clearSelection();
                    mZoomControl.pan(-dx, -dy);
                } else {
                    final double scrollX = mDownX - x;
                    final double scrollY = mDownY - y;

                    final double dist = Math.sqrt(scrollX * scrollX + scrollY * scrollY);

                    if(dist >= mScaledTouchSlop) {
                        v.removeCallbacks(mLongPressRunnable);
                        mMode = Mode.PAN;
                    }
                }

                mX = x;
                mY = y;
                break;
            case MotionEvent.ACTION_UP:
                if(mMode == Mode.PAN) {
                    periodicTableView.clearSelection();
                    mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                    mZoomControl.startFling(-mVelocityTracker.getXVelocity() / v.getWidth(),
                            -mVelocityTracker.getYVelocity() / v.getHeight());
                } else {
                    mZoomControl.startFling(0, 0);
                }
                periodicTableView.onClick();
            default:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                v.removeCallbacks(mLongPressRunnable);
                mMode = Mode.UNDEFINED;
                break;
        }

        return true;
    }
}
