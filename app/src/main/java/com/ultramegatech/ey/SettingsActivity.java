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
package com.ultramegatech.ey;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.MenuItem;

import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.ey.util.SubtextValuesHelper;

/**
 * Simple implementation of PreferenceActivity for setting general application settings.
 *
 * @author Steve Guidetti
 */
public class SettingsActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        SubtextValuesHelper.OnSubtextValuesChangedListener {
    /**
     * The Preference for setting the block subtext value
     */
    private ListPreference mSubtextValuePreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        final boolean darkTheme = PreferenceUtils.getPrefDarkTheme();
        setTheme(darkTheme ? R.style.DarkTheme_Preferences : R.style.LightTheme_Preferences);

        super.onCreate(savedInstanceState);

        final ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        addPreferencesFromResource(R.xml.preferences);

        mSubtextValuePreference = (ListPreference)findPreference(PreferenceUtils.KEY_SUBTEXT_VALUE);
        final SubtextValuesHelper subtextValuesHelper = new SubtextValuesHelper(this, this);
        mSubtextValuePreference.setEntries(subtextValuesHelper.getList());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceUtils.KEY_DARK_THEME.equals(key)) {
            final Intent intent = new Intent(this, SettingsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            Runtime.getRuntime().exit(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSubtextValuesChanged(@NonNull SubtextValuesHelper helper) {
        mSubtextValuePreference.setEntries(helper.getList());
    }
}
