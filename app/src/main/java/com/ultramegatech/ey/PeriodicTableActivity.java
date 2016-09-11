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
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.ZoomControls;

import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.ey.util.UnitUtils;
import com.ultramegatech.ey.widget.BlockSubtextValueListAdapter;
import com.ultramegatech.ey.widget.PeriodicTableBlock;
import com.ultramegatech.ey.widget.PeriodicTableView;

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

    /**
     * The zoom controls
     */
    private ZoomControls mZoomControls;

    /**
     * The key to the value to display as the block subtext
     */
    private String mSubtextValueKey;

    /**
     * The Spinner to choose how to color the blocks
     */
    private Spinner mSpinnerBlockColors;

    /**
     * The SharedPreferences for the Activity
     */
    private SharedPreferences mPreferences;

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
        mPeriodicTableView.setPeriodicTableListener(new PeriodicTableView.PeriodicTableListener() {
            @Override
            public void onItemClick(PeriodicTableBlock item) {
                ElementDetailsFragment.showDialog(getSupportFragmentManager(), item.number);
            }

            @Override
            public void onZoomEnd(PeriodicTableView periodicTableView) {
                mZoomControls.setIsZoomInEnabled(periodicTableView.canZoomIn());
                mZoomControls.setIsZoomOutEnabled(periodicTableView.canZoomOut());
            }
        });

        mZoomControls = (ZoomControls)findViewById(R.id.zoom);
        mZoomControls.setIsZoomOutEnabled(false);
        mZoomControls.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPeriodicTableView.zoomIn();
                mZoomControls.setIsZoomOutEnabled(true);
            }
        });
        mZoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPeriodicTableView.zoomOut();
                mZoomControls.setIsZoomInEnabled(true);
            }
        });

        setupSubtextValueSpinner();
        setupBlockColorSpinner();

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    /**
     * Set up the Spinner for choosing the value to display as the subtext of each block.
     */
    private void setupSubtextValueSpinner() {
        final Spinner spinner = (Spinner)findViewById(R.id.subtextValue);
        final BlockSubtextValueListAdapter adapter = new BlockSubtextValueListAdapter(this);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getItemIndex(PreferenceUtils
                .getPrefSubtextValue(mPreferences)));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferenceUtils.setPrefSubtextValue(mPreferences, adapter.getItem(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * Set up the Spinner for choosing how to color the blocks.
     */
    private void setupBlockColorSpinner() {
        mSpinnerBlockColors = (Spinner)findViewById(R.id.blockColors);
        final String pref = PreferenceUtils.getPrefElementColors(mPreferences);
        mSpinnerBlockColors.setSelection(pref.equals(PreferenceUtils.COLOR_CAT) ? 0 : 1);
        mSpinnerBlockColors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                PreferenceUtils.setPrefElementColors(mPreferences,
                        i == 0 ? PreferenceUtils.COLOR_CAT : PreferenceUtils.COLOR_BLOCK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
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
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mPreferences.registerOnSharedPreferenceChangeListener(this);

        final boolean darkTheme = PreferenceUtils.getPrefDarkTheme(mPreferences);
        setTheme(darkTheme ? R.style.DarkTheme_TableView : R.style.LightTheme_TableView);

        final String colorKey = PreferenceUtils.getPrefElementColors(mPreferences);
        if(PreferenceUtils.COLOR_BLOCK.equals(colorKey)) {
            mProjection[5] = Elements.BLOCK;
        } else {
            mProjection[5] = Elements.CATEGORY;
        }

        mSubtextValueKey = PreferenceUtils.getPrefSubtextValue(mPreferences);
        mProjection[2] = mSubtextValueKey;
    }

    /**
     * Get the block subtext for the current record of the provided Cursor.
     *
     * @param cursor The Cursor
     * @param df     The DecimalFormat to use for decimal values
     * @return The subtext for the current record
     */
    private String getSubtext(Cursor cursor, DecimalFormat df) {
        if(Elements.WEIGHT.equals(mSubtextValueKey)) {
            if(cursor.getInt(cursor.getColumnIndex(Elements.UNSTABLE)) == 1) {
                return "[" + cursor.getInt(2) + "]";
            } else {
                return df.format(cursor.getDouble(2));
            }
        }
        if(Elements.MELT.equals(mSubtextValueKey) || Elements.BOIL.equals(mSubtextValueKey)) {
            final double value;
            switch(PreferenceUtils.getPrefTempUnit(mPreferences)) {
                case PreferenceUtils.TEMP_C:
                    value = UnitUtils.KtoC(cursor.getDouble(2));
                    break;
                case PreferenceUtils.TEMP_F:
                    value = UnitUtils.KtoF(cursor.getDouble(2));
                    break;
                default:
                    value = cursor.getDouble(2);
            }
            return df.format(value);
        }
        if(Elements.ABUNDANCE.equals(mSubtextValueKey)) {
            if(!cursor.isNull(2)) {
                final double value = cursor.getDouble(2);
                if(value < 0.001) {
                    return "<0.001";
                }
                return df.format(value);
            }
        }
        final String value = cursor.getString(2);
        return value == null ? "?" : value;
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
            block.subtext = getSubtext(d, df);
            block.group = d.getInt(3);
            block.period = d.getInt(4);
            block.category = d.getString(5);

            periodicTableBlocks.add(block);
        }

        mPeriodicTableView.setBlocks(periodicTableBlocks);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceUtils.KEY_ELEMENT_COLORS.equals(key)) {
            final String colorKey = PreferenceUtils.getPrefElementColors(mPreferences);
            if(PreferenceUtils.COLOR_BLOCK.equals(colorKey)) {
                mProjection[5] = Elements.BLOCK;
                mSpinnerBlockColors.setSelection(1);
            } else {
                mProjection[5] = Elements.CATEGORY;
                mSpinnerBlockColors.setSelection(0);
            }
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        } else if(PreferenceUtils.KEY_SUBTEXT_VALUE.equals(key)) {
            mSubtextValueKey = PreferenceUtils.getPrefSubtextValue(sharedPreferences);
            mProjection[2] = mSubtextValueKey;
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }
    }
}
