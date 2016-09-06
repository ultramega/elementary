package com.ultramegatech.ey.widget;

import android.annotation.TargetApi;
import android.os.Build;
import android.widget.OverScroller;

/**
 * Compatibility layer for OverScroller.
 *
 * @author Steve Guidetti
 */
public class OverScrollerCompat {
    /**
     * Get the current velocity of the OverScroller.
     *
     * @param overScroller The OverScroller
     * @return The current velocity
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static float getCurrVelocity(OverScroller overScroller) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return overScroller.getCurrVelocity();
        } else {
            return 0;
        }
    }
}
