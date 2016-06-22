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
package com.ultramegatech.ey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.util.ActionBarWrapper;

/**
 * This Activity holds the ElementDetailsFragment. It can be launched by an Intent with an extra
 * identifying the element by atomic number.
 *
 * @author Steve Guidetti
 */
public class ElementDetailsActivity extends FragmentActivity {
    /**
     * The tag to identify the Activity
     */
    private static final String TAG = "ElementDetailsActivity";

    /**
     * Intent extras
     */
    public static final String EXTRA_ATOMIC_NUMBER = "atomic_number";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean darkTheme = PreferenceUtils.getPrefDarkTheme(this, prefs);
        setTheme(darkTheme ? R.style.DarkTheme : R.style.LightTheme);

        super.onCreate(savedInstanceState);

        ActionBarWrapper.getInstance(this).setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null) {
            final Intent intent = getIntent();
            Fragment fragment = null;
            if(intent.hasExtra(EXTRA_ATOMIC_NUMBER)) {
                fragment = ElementDetailsFragment
                        .getInstance(intent.getIntExtra(EXTRA_ATOMIC_NUMBER, 0));
            } else if(getIntent().getData() != null) {
                final Uri uri = getIntent().getData();
                if(uri.getHost().equals("element")) {
                    final String path = uri.getPathSegments().get(0);
                    if(TextUtils.isDigitsOnly(path)) {
                        try {
                            fragment = ElementDetailsFragment
                                    .getInstance(Integer.parseInt(uri.getPathSegments().get(0)));
                        } catch(NumberFormatException e) {
                            Log.w(TAG, "Invalid atomic number");
                        }
                    } else {
                        fragment = ElementDetailsFragment.getInstance(path);
                    }
                }
            }
            if(fragment != null) {
                getSupportFragmentManager().beginTransaction().add(android.R.id.content, fragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                break;
            default:
                CommonMenuHandler.handleSelect(this, id);
        }
        return super.onOptionsItemSelected(item);
    }
}
