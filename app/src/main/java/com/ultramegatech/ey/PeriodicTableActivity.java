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
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.widget.PeriodicTableBlock;
import com.ultramegatech.widget.PeriodicTableView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * This Activity displays the PeriodicTableView. Clicking on an element block will launch an
 * ElementDetailsActivity for the selected element.
 *
 * @author Steve Guidetti
 */
public class PeriodicTableActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor>, OnSharedPreferenceChangeListener {
    /**
     * Delay in milliseconds before entering or re-entering immersive full screen mode
     */
    private static final long IMMERSIVE_MODE_DELAY = 3000;

    /**
     * Handler for posting delayed callbacks
     */
    private Handler mHandler;

    /**
     * Callback to enter immersive full screen mode
     */
    private Runnable mImmersiveModeCallback;

    /**
     * Fields to read from the database
     */
    private final String[] mProjection = new String[] {
            Elements.NUMBER,
            Elements.SYMBOL,
            Elements.WEIGHT,
            Elements.GROUP,
            Elements.PERIOD,
            Elements.CATEGORY,
            Elements.UNSTABLE
    };

    /**
     * The main View
     */
    private PeriodicTableView mPeriodicTableView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        loadPreferences();

        super.onCreate(savedInstanceState);
        setupImmersiveMode();
        setContentView(R.layout.activity_periodic_table);

        mPeriodicTableView = (PeriodicTableView)findViewById(R.id.ptview);
        mPeriodicTableView.setOnItemClickListener(new PeriodicTableView.OnItemClickListener() {
            public void onItemClick(PeriodicTableBlock item) {
                ElementDetailsFragment.showDialog(getSupportFragmentManager(), item.number);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mHandler.removeCallbacks(mImmersiveModeCallback);
            mHandler.postDelayed(mImmersiveModeCallback, IMMERSIVE_MODE_DELAY);
        }
    }

    /**
     * Set up immersive full screen mode for supported devices.
     */
    private void setupImmersiveMode() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mHandler = new Handler();
            mImmersiveModeCallback = new Runnable() {
                @Override
                public void run() {
                    hideSystemUi();
                }
            };

            getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                            | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
            );
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(
                    new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            mHandler.removeCallbacks(mImmersiveModeCallback);
                            if((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                                mHandler.postDelayed(mImmersiveModeCallback, IMMERSIVE_MODE_DELAY);
                            }
                        }
                    });
        }
    }

    /**
     * Enable immersive full screen mode.
     */
    private void hideSystemUi() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE
            );

            findViewById(R.id.touchFrame).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.periodic_table, menu);
        inflater.inflate(R.menu.common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case R.id.menu_list:
                startActivity(new Intent(this, ElementListActivity.class));
                break;
            default:
                CommonMenuHandler.handleSelect(this, id);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Load relevant preferences.
     */
    private void loadPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

        final boolean darkTheme = PreferenceUtils.getPrefDarkTheme(this, prefs);
        setTheme(darkTheme ? R.style.DarkTheme_TableView : R.style.LightTheme_TableView);

        final String colorKey = PreferenceUtils.getPrefElementColors(this, prefs);
        if(PreferenceUtils.COLOR_BLOCK.equals(colorKey)) {
            mProjection[5] = Elements.BLOCK;
        } else {
            mProjection[5] = Elements.CATEGORY;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Elements.CONTENT_URI, mProjection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
        mPeriodicTableView.getLegend().setMap(ElementUtils.getLegendMap(this));

        final ArrayList<PeriodicTableBlock> periodicTableBlocks = new ArrayList<>();
        PeriodicTableBlock block;

        final DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);

        while(d.moveToNext()) {
            block = new PeriodicTableBlock();
            block.number = d.getInt(0);
            block.symbol = d.getString(1);
            block.group = d.getInt(3);
            block.period = d.getInt(4);
            block.category = d.getString(5);

            if(d.getInt(6) == 1) {
                block.subtext = "[" + d.getInt(2) + "]";
            } else {
                block.subtext = df.format(d.getDouble(2));
            }

            periodicTableBlocks.add(block);
        }

        mPeriodicTableView.setBlocks(periodicTableBlocks);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PreferenceUtils.getKeyElementColors(this))) {
            loadPreferences();
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }
    }
}
