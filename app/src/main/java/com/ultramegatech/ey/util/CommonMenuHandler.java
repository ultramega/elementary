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
package com.ultramegatech.ey.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.ultramegatech.ey.AboutFragment;
import com.ultramegatech.ey.R;
import com.ultramegatech.ey.SettingsActivity;

/**
 * Handles menu items shared by all Activities.
 *
 * @author Steve Guidetti
 */
public class CommonMenuHandler {
    /**
     * Select an action based on a menu selection.
     *
     * @param activity The calling Activity
     * @param id       Menu item ID
     * @return Whether the menu selection was handled
     */
    public static boolean handleSelect(@NonNull FragmentActivity activity, int id) {
        switch(id) {
            case R.id.menu_settings:
                activity.startActivity(new Intent(activity, SettingsActivity.class));
                return true;
            case R.id.menu_about:
                AboutFragment.showDialog(activity.getSupportFragmentManager());
                return true;
        }
        return false;
    }
}
