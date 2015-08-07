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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.widget.PeriodicTableBlock;
import com.ultramegatech.widget.PeriodicTableView;
import java.util.ArrayList;

/**
 * This activity displays the periodic table view. Clicking on an element block will launch an
 * ElementDetailsActivity for the selected element.
 * 
 * @author Steve Guidetti
 */
public class PeriodicTableActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor>, OnSharedPreferenceChangeListener {
    /* Fields to read from the database */
    private final String[] mProjection = new String[] {
        Elements.NUMBER,
        Elements.SYMBOL,
        Elements.WEIGHT,
        Elements.GROUP,
        Elements.PERIOD,
        Elements.CATEGORY,
        Elements.UNSTABLE
    };
    
    /* The main view */
    private PeriodicTableView mPeriodicTableView;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.periodic_table);
        
        mPeriodicTableView = (PeriodicTableView)findViewById(R.id.ptview);
        
        mPeriodicTableView.setOnItemClickListener(new PeriodicTableView.OnItemClickListener() {
            public void onItemClick(PeriodicTableBlock item) {
                final Intent intent =
                        new Intent(getApplicationContext(), ElementDetailsActivity.class);
                intent.putExtra(ElementDetailsActivity.EXTRA_ATOMIC_NUMBER, item.number);
                startActivity(intent);
            }
        });
        
        loadPreferences();
        
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
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
     * Load relevant shared preferences.
     */
    private void loadPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        
        final String colorKey = prefs.getString("elementColors", "category");
        if(colorKey.equals("block")) {
            mProjection[5] = Elements.BLOCK;
        } else {
            mProjection[5] = Elements.CATEGORY;
        }
    }
    
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this, Elements.CONTENT_URI, mProjection, null, null, null);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
        mPeriodicTableView.getLegend().setMap(ElementUtils.getLegendMap(this));
        
        final ArrayList<PeriodicTableBlock> periodicTableBlocks =
                new ArrayList<PeriodicTableBlock>();
        PeriodicTableBlock block;

        while(d.moveToNext()) {
            block = new PeriodicTableBlock();
            block.number = d.getInt(0);
            block.symbol = d.getString(1);
            block.subtext = d.getString(2);
            block.group = d.getInt(3);
            block.period = d.getInt(4);
            block.category = d.getString(5);
            
            if(d.getInt(6) == 1) {
                block.subtext = "[" + Integer.parseInt(block.subtext) + "]";
            }

            periodicTableBlocks.add(block);
        }
        
        mPeriodicTableView.setBlocks(periodicTableBlocks);
    }

    public void onLoaderReset(Loader<Cursor> loader) { }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("elementColors")) {
            loadPreferences();
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }
    }
}