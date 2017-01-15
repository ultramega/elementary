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
package com.ultramegatech.ey.util;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Compatibility layer for the ActionBar.
 *
 * @author Steve Guidetti
 */
public class ActionBarCompat {
    /**
     * Enable or disable the display of the up arrow on the home button.
     *
     * @param activity        The Activity
     * @param displayHomeAsUp Whether to display the up arrow on the home button
     */
    @SuppressWarnings("SameParameterValue")
    public static void setDisplayHomeAsUpEnabled(@NonNull Activity activity,
                                                 boolean displayHomeAsUp) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            final ActionBar actionBar = activity.getActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(displayHomeAsUp);
            }
        }
    }
}
