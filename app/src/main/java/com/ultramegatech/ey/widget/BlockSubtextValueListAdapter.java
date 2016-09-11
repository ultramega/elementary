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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ultramegatech.ey.R;
import com.ultramegatech.ey.util.PreferenceUtils;

/**
 * Custom Adapter for the block subtext Spinner.
 *
 * @author Steve Guidetti
 */
public class BlockSubtextValueListAdapter extends BaseAdapter
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * The Context
     */
    private final Context mContext;

    /**
     * The keys of the items
     */
    private final String[] mKeys;

    /**
     * The names of the items
     */
    private final String[] mNames;

    /**
     * @param context The Context
     */
    public BlockSubtextValueListAdapter(Context context) {
        mContext = context;
        mKeys = context.getResources().getStringArray(R.array.subtextValues);
        mNames = context.getResources().getStringArray(R.array.subtextValueNames);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.registerOnSharedPreferenceChangeListener(this);

        updateTempUnit(prefs);
    }

    /**
     * Update the unit for temperatures.
     *
     * @param prefs The SharedPreferences
     */
    private void updateTempUnit(SharedPreferences prefs) {
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
        mNames[2] = mNames[2].substring(0, mNames[2].length() - 2) + unit + ")";
        mNames[3] = mNames[3].substring(0, mNames[3].length() - 2) + unit + ")";
    }

    @Override
    public int getCount() {
        return mKeys.length;
    }

    @Override
    public String getItem(int i) {
        return mKeys[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * Get the index of an item based on its key.
     *
     * @param key The key
     * @return The item index
     */
    public int getItemIndex(String key) {
        for(int i = 0; i < mKeys.length; i++) {
            if(mKeys[i].equals(key)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent,
                android.R.layout.simple_spinner_dropdown_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createView(position, convertView, parent, android.R.layout.simple_spinner_item);
    }

    /**
     * Create the view for an item.
     *
     * @param position    The item index
     * @param convertView The previous View to recycle
     * @param parent      The parent ViewGroup
     * @param layout      The layout ID to inflate
     * @return The populated View
     */
    private View createView(int position, View convertView, ViewGroup parent, int layout) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(layout, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.text = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        }
        ((ViewHolder)convertView.getTag()).text.setText(mNames[position]);
        return convertView;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceUtils.KEY_TEMP_UNITS.equals(key)) {
            updateTempUnit(sharedPreferences);
            notifyDataSetChanged();
        }
    }

    /**
     * Caches references to Views within a layout.
     */
    private static class ViewHolder {
        /**
         * The primary text area
         */
        public TextView text;
    }
}
