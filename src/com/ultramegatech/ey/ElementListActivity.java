/*
 * The MIT License (MIT)
 * Copyright © 2015 Steve Guidetti
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
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
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.util.ActionBarWrapper;
import com.ultramegatech.widget.ElementListAdapter;
import com.ultramegatech.widget.ElementListAdapter.ElementHolder;
import java.util.ArrayList;

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
    private static final String KEY_FILTER = "key_filter";

    /* Fields to read from the database */
    private final String[] mListProjection = new String[]{
        Elements._ID,
        Elements.NUMBER,
        Elements.SYMBOL,
        Elements.CATEGORY
    };

    /* The list */
    private ListView mListView;

    /* List adapter */
    private ElementListAdapter mAdapter;

    /* Current value to filter results by */
    private String mFilter;

    /* Current value for sorting elements */
    private int mSort = ElementListAdapter.SORT_NUMBER;

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
                intent.putExtra(ElementDetailsActivity.EXTRA_ATOMIC_NUMBER, (int)id);
                startActivity(intent);
            }
        });

        loadPreferences();

        if(savedInstanceState != null) {
            mSort = savedInstanceState.getInt(KEY_SORT);
            mFilter = savedInstanceState.getString(KEY_FILTER);
        }

        setupFilter();
        setupSort();

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SORT, mSort);
        outState.putString(KEY_FILTER, mFilter);
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
            mListProjection[3] = Elements.BLOCK;
        } else {
            mListProjection[3] = Elements.CATEGORY;
        }
    }

    /**
     * Setup the listener for the filtering TextView.
     */
    private void setupFilter() {
        final EditText filterEditText = (EditText)findViewById(R.id.filter);
        filterEditText.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            public void afterTextChanged(Editable s) {
                mFilter = s.toString();
                mAdapter.getFilter().filter(mFilter);
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
     */
    public void setSort(int field) {
        mSort = field;
        mAdapter.setSort(mSort);
    }

    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        setProgressBarIndeterminateVisibility(true);
        return new CursorLoader(this, Elements.CONTENT_URI, mListProjection, null, null,
                Elements.NUMBER + " ASC");
    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
        setProgressBarIndeterminateVisibility(false);

        final ElementUtils utils = new ElementUtils(this);

        final ArrayList<ElementHolder> data = new ArrayList<ElementHolder>();
        while(d.moveToNext()) {
            final String number = d.getString(1);
            final String symbol = d.getString(2);
            final String name = getString(ElementUtils.getElementName(d.getInt(1)));
            final int color = utils.getElementColor(d.getString(3));

            data.add(new ElementHolder(number, symbol, name, color));
        }

        mAdapter = new ElementListAdapter(this, data, mFilter, mSort);
        mListView.setAdapter(mAdapter);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("elementColors")) {
            loadPreferences();
            getSupportLoaderManager().restartLoader(0, null, this).forceLoad();
        }
    }

    public static class SortDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Dialog d = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.titleSort)
                    .setItems(R.array.sortFieldNames, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            ((ElementListActivity)getActivity()).setSort(item);
                            dialog.dismiss();
                        }
                    })
                    .create();

            return d;
        }

    }
}
