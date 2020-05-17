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

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.ey.util.SubtextValuesHelper;

/**
 * Simple implementation of PreferenceActivity for setting general application settings.
 *
 * @author Steve Guidetti
 */
public class SettingsFragment extends PreferenceFragmentCompat
        implements SubtextValuesHelper.OnSubtextValuesChangedListener {
    /**
     * The Preference for setting the block subtext value
     */
    private ListPreference mSubtextValuePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        mSubtextValuePreference = findPreference(PreferenceUtils.KEY_SUBTEXT_VALUE);
        final SubtextValuesHelper subtextValuesHelper = new SubtextValuesHelper(getContext(), this);
        mSubtextValuePreference.setEntries(subtextValuesHelper.getList());
    }

    @Override
    public void onSubtextValuesChanged(@NonNull SubtextValuesHelper helper) {
        mSubtextValuePreference.setEntries(helper.getList());
    }
}
