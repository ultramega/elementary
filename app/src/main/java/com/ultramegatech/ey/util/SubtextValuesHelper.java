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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.ultramegatech.ey.R;

import java.util.Arrays;

/**
 * Helper for keeping the list of options for the block subtext value up to date.
 *
 * @author Steve Guidetti
 */
public class SubtextValuesHelper implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * The interface for listeners for changes to the value list.
     */
    public interface OnSubtextValuesChangedListener {
        /**
         * Called when the list of values is changed.
         *
         * @param helper The SubtextValuesHelper
         */
        void onSubtextValuesChanged(@NonNull SubtextValuesHelper helper);
    }

    /**
     * The list of options for the block subtext value
     */
    @NonNull
    private final String[] mList;

    /**
     * The listener for changes to the list of values
     */
    @Nullable
    private final OnSubtextValuesChangedListener mListener;

    /**
     * @param context  The Context
     * @param listener The listener for changes to the list of values
     */
    public SubtextValuesHelper(@NonNull Context context,
                               @Nullable OnSubtextValuesChangedListener listener) {
        mList = context.getResources().getStringArray(R.array.subtextValueNames);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateTempUnit(prefs);
        mListener = listener;
    }

    /**
     * Get the list of options for the block subtext value.
     *
     * @return The list of options for the block subtext value
     */
    @NonNull
    public String[] getList() {
        return Arrays.copyOf(mList, mList.length);
    }

    /**
     * Get a single item from the list of options for the block subtext value.
     *
     * @param index The index of the item
     * @return The item
     */
    @Nullable
    public String getItem(int index) {
        if(index >= 0 && index < mList.length) {
            return mList[index];
        }
        return null;
    }

    /**
     * Update the unit for temperatures.
     *
     * @param prefs The SharedPreferences
     */
    private void updateTempUnit(@NonNull SharedPreferences prefs) {
        final String unit;
        switch(PreferenceUtils.getPrefTempUnit(prefs)) {
            case PreferenceUtils.TEMP_C:
                unit = "℃";
                break;
            case PreferenceUtils.TEMP_F:
                unit = "℉";
                break;
            default:
                unit = "K";
        }
        mList[2] = mList[2].substring(0, mList[2].length() - 2) + unit + ")";
        mList[3] = mList[3].substring(0, mList[3].length() - 2) + unit + ")";
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceUtils.KEY_TEMP_UNITS.equals(key)) {
            updateTempUnit(sharedPreferences);
            if(mListener != null) {
                mListener.onSubtextValuesChanged(this);
            }
        }
    }
}
