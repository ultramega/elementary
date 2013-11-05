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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.util.ActionBarWrapper;

/**
 * This activity displays a list of all the elements sorted by atomic number. Clicking on an item
 * will launch an ElementDetailsActivity for the selected element. The list can be filtered by name
 * or symbol.
 * 
 * @author Steve Guidetti
 */
public class ElementListActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor>, OnSharedPreferenceChangeListener {
    /* Keys for saving instance state */
    private static final String KEY_SORT = "key_sort";
    
    /* Fields to read from the database */
    private final String[] mListProjection = new String[] {
        Elements._ID,
        Elements.NUMBER,
        Elements.SYMBOL,
        Elements.NAME,
        Elements.CATEGORY
    };
    
    /* Mapping of fields to views */
    private final String[] mListFields = new String[] {
        Elements.NUMBER,
        Elements.SYMBOL,
        Elements.NAME,
        Elements.CATEGORY
    };
    private final int[] mListViews = new int[] {
        R.id.number,
        R.id.symbol,
        R.id.name,
        R.id.block
    };
    
    /* Sort directions */
    private static String SORT_ASC = "ASC";
    private static String SORT_DESC = "DESC";
    
    /* The list */
    private ListView mListView;
    
    /* List adapter */
    private SimpleCursorAdapter mAdapter;
    
    /* Current value to filter results by */
    private String mFilter;
    
    /* Current value for sorting elements */
    private String mSort = Elements.NUMBER + " " + SORT_ASC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        ActionBarWrapper.getInstance(this).setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.element_list);
        mListView = (ListView)findViewById(android.R.id.list);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Intent intent =
                        new Intent(getApplicationContext(), ElementDetailsActivity.class);
                intent.putExtra(ElementDetailsActivity.EXTRA_ELEMENT_ID, id);
                startActivity(intent);
            }
        });
        
        loadPreferences();
        
        if(savedInstanceState != null) {
            mSort = savedInstanceState.getString(KEY_SORT);
        }
        
        setupAdapter();
        setupFilter();
        setupSort();
        
        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_SORT, mSort);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.element_list, menu);
        inflater.inflate(R.menu.common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
            case R.id.menu_table:
                finish();
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
            mListProjection[4] = Elements.BLOCK;
            mListFields[3] = Elements.BLOCK;
        } else {
            mListProjection[4] = Elements.CATEGORY;
            mListFields[3] = Elements.CATEGORY;
        }
    }
    
    /**
     * Create and configure the list adapter.
     */
    private void setupAdapter() {
        mAdapter = new SimpleCursorAdapter(this, R.layout.element_list_item,
                null, mListFields, mListViews, 0);
        
        final ElementUtils elementUtils = new ElementUtils(this);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int i) {
                if(view instanceof RelativeLayout) {
                    final int background = elementUtils.getElementColor(cursor.getString(i));
                    view.setBackgroundColor(background);
                    return true;
                }
                
                return false;
            }
        });
        
        mListView.setAdapter(mAdapter);
    }
    
    /**
     * Setup the listener for the filtering TextView.
     */
    private void setupFilter() {
        final EditText filterEditText = (EditText)findViewById(R.id.filter);
        filterEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            public void afterTextChanged(Editable s) {
                mFilter = s.toString();
                restartLoader();
            }
        });
    }
    
    /**
     * Setup the listener for the sort button.
     */
    private void setupSort() {
        final Button sortButton = (Button)findViewById(R.id.sort);
        sortButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                openSortDialog();
            }
        });
    }
    
    /**
     * Display the sorting dialog.
     */
    private void openSortDialog() {
        final DialogFragment fragment = new SortDialog();
        fragment.show(getSupportFragmentManager(), null);
    }
    
    /**
     * Set the sorting parameters.
     * 
     * @param field Field to sort by
     * @param direction Direction to sort, ASC or DESC
     */
    public void setSort(String field, String direction) {
        final String oldSort = mSort;
        mSort = field + " " + direction;
        if(!oldSort.equals(mSort)) {
            restartLoader();
        }
    }
    
    /**
     * Restart the Cursor Loader.
     */
    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        setProgressBarIndeterminateVisibility(true);
        
        final Uri uri;
        if(TextUtils.isEmpty(mFilter)) {
            uri = Elements.CONTENT_URI;
        } else {
            uri = Uri.withAppendedPath(Elements.CONTENT_URI_FILTER, mFilter);
        }
        
        return new CursorLoader(this, uri, mListProjection, null, null, mSort);
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
        setProgressBarIndeterminateVisibility(false);
        
        mAdapter.swapCursor(d);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("elementColors")) {
            getSupportLoaderManager().destroyLoader(0);
            loadPreferences();
            setupAdapter();
            restartLoader();
        }
    }
    
    public static class SortDialog extends DialogFragment {
        // these correspond to R.array.sortFieldNames
        private final static String[] FIELDS = new String[] {
            Elements.NUMBER,
            Elements.NAME
        };
        
        public static final String EXTRA_FIELD = "extra_field";
        
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog d = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.titleSort)
                    .setItems(R.array.sortFieldNames, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            ((ElementListActivity)getActivity()).setSort(FIELDS[item], SORT_ASC);
                            dialog.dismiss();
                        }
                    })
                    .create();
            
            return d;
        }
        
    }
}