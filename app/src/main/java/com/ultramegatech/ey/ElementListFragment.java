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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;
import androidx.preference.PreferenceManager;

import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.ey.widget.ElementListAdapter;

/**
 * This Fragment displays a sortable filterable list of all the elements. Clicking on an item
 * will launch an ElementDetailsFragment for the selected element.
 *
 * @author Steve Guidetti
 */
public class ElementListFragment extends ListFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Keys for saving instance state
     */
    private static final String KEY_SORT = "key_sort";
    private static final String KEY_SORT_REVERSE = "key_sort_reverse";
    private static final String KEY_FILTER = "key_filter";
    private static final String KEY_ACTIVATED_ITEM = "key_activated_item";

    /**
     * The Adapter backing the list
     */
    private ElementListAdapter mAdapter;

    /**
     * Current value to filter results by
     */
    private String mFilter;

    /**
     * Current value for sorting elements
     */
    private int mSort = ElementListAdapter.SORT_NUMBER;

    /**
     * Current sorting direction
     */
    private boolean mSortReverse = false;

    /**
     * The current activated item if in two-pane mode
     */
    private long mActivatedItem = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Context context = getContext();
        if(context == null) {
            return;
        }

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        if(savedInstanceState != null) {
            mSort = savedInstanceState.getInt(KEY_SORT, mSort);
            mSortReverse = savedInstanceState.getBoolean(KEY_SORT_REVERSE, mSortReverse);
            mFilter = savedInstanceState.getString(KEY_FILTER);
            mActivatedItem = savedInstanceState.getLong(KEY_ACTIVATED_ITEM, mActivatedItem);
        }

        mAdapter = new ElementListAdapter(context);
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setActivatedPosition(mAdapter.getItemPosition(mActivatedItem));
            }
        });
        mAdapter.getFilter().filter(mFilter);
        mAdapter.setSort(mSort, mSortReverse);
        setListAdapter(mAdapter);

        setupFilter();
        setupSort();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SORT, mSort);
        outState.putBoolean(KEY_SORT_REVERSE, mSortReverse);
        outState.putString(KEY_FILTER, mFilter);
        outState.putLong(KEY_ACTIVATED_ITEM, mActivatedItem);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mActivatedItem = id;

        final ElementListActivity activity = (ElementListActivity)getActivity();
        if(activity != null) {
            activity.onItemSelected((int)id);
        }
    }

    /**
     * Setup the listener for the filtering TextView.
     */
    private void setupFilter() {
        final Activity activity = getActivity();
        if(activity == null) {
            return;
        }

        final EditText filterEditText = activity.findViewById(R.id.filter);
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
        final Activity activity = getActivity();
        if(activity == null) {
            return;
        }

        final Button sortButton = activity.findViewById(R.id.sort);
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
        if (!isAdded()) {
            return;
        }

        final FragmentManager fm = getParentFragmentManager();
        final DialogFragment fragment = new SortDialog();
        fragment.setTargetFragment(this, 0);
        fragment.show(fm, null);
    }

    /**
     * Set the sorting parameters.
     *
     * @param field Field to sort by
     */
    private void setSort(int field) {
        mSortReverse = field == mSort && !mSortReverse;
        mSort = field;
        mAdapter.setSort(mSort, mSortReverse);
    }

    /**
     * Turn on activate-on-click mode. When this mode is on, list items will be given the activated
     * state when touched.
     */
    @SuppressWarnings("SameParameterValue")
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    /**
     * Set the selected list item.
     *
     * @param position The index of the item to activate
     */
    private void setActivatedPosition(int position) {
        if(position != ListView.INVALID_POSITION) {
            getListView().setItemChecked(position, true);
        } else {
            getListView().setItemChecked(getListView().getCheckedItemPosition(), false);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceUtils.KEY_ELEMENT_COLORS.equals(key)) {
            mAdapter.notifyDataSetInvalidated();
        }
    }

    /**
     * Dialog for setting the sort parameter for the list.
     */
    public static class SortDialog extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.titleSort)
                    .setItems(R.array.sortFieldNames, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            final ElementListFragment fragment =
                                    (ElementListFragment)getTargetFragment();
                            if(fragment != null) {
                                fragment.setSort(item);
                            }

                            dialog.dismiss();
                        }
                    })
                    .create();
        }

    }
}
