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

package com.ultramegatech.util;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;

/**
 * Compatibility wrapper for accessing the Action Bar.
 * 
 * @author Steve Guidetti
 */
public abstract class ActionBarWrapper {
    /**
     * Get a compatible implementation of ActionBarWrapper.
     * 
     * @param activity
     * @return 
     */
    public static ActionBarWrapper getInstance(Activity activity) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new Honeycomb(activity);
        } else {
            return new PreHoneycomb();
        }
    }
    
    /**
     * Cause the options menu to be re-created.
     */
    public abstract void invalidateOptionsMenu();
    
    /**
     * Enable or disable the display of the up arrow on the home button.
     * 
     * @param arg 
     */
    public abstract void setDisplayHomeAsUpEnabled(boolean arg);
    
    private static class PreHoneycomb extends ActionBarWrapper {
        public PreHoneycomb() { }

        @Override
        public void invalidateOptionsMenu() { }

        @Override
        public void setDisplayHomeAsUpEnabled(boolean arg) { }
    }
    
    @TargetApi(11)
    private static class Honeycomb extends ActionBarWrapper {
        private final Activity mActivity;

        public Honeycomb(Activity activity) {
            mActivity = activity;
        }

        @Override
        public void invalidateOptionsMenu() {
            mActivity.invalidateOptionsMenu();
        }

        @Override
        public void setDisplayHomeAsUpEnabled(boolean arg) {
            final ActionBar actionBar = mActivity.getActionBar();
            if(actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(arg);
            }
        }
    }
}