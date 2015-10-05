package com.ultramegatech.ey;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.widget.ElementListAdapter;

import java.util.ArrayList;

/**
 * This Fragment displays a sortable filterable list of all the elements. Clicking on an item
 * will launch an ElementDetailsFragment for the selected element.
 *
 * @author Steve Guidetti
 */
public class ElementListFragment extends ListFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * Keys for saving instance state
     */
    private static final String KEY_SORT = "key_sort";
    private static final String KEY_SORT_REVERSE = "key_sort_reverse";
    private static final String KEY_FILTER = "key_filter";
    private static final String KEY_ACTIVATED_ITEM = "key_activated_item";

    /**
     * Fields to read from the database
     */
    private final String[] mListProjection = new String[] {
            Elements._ID,
            Elements.NUMBER,
            Elements.SYMBOL,
            Elements.CATEGORY
    };

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
        loadPreferences();

        if(savedInstanceState != null) {
            mSort = savedInstanceState.getInt(KEY_SORT, mSort);
            mSortReverse = savedInstanceState.getBoolean(KEY_SORT_REVERSE, mSortReverse);
            mFilter = savedInstanceState.getString(KEY_FILTER);
            mActivatedItem = savedInstanceState.getLong(KEY_ACTIVATED_ITEM, mActivatedItem);
        }

        setListShown(false);
        mAdapter = new ElementListAdapter(getContext());
        mAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setActivatedPosition(mAdapter.getItemPosition(mActivatedItem));
            }
        });
        setListAdapter(mAdapter);

        setupFilter();
        setupSort();

        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        mActivatedItem = id;
        ((ElementListActivity)getActivity()).onItemSelected((int)id);
    }

    /**
     * Load relevant preferences.
     */
    private void loadPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        final String colorKey = PreferenceUtils.getPrefElementColors(getContext(), prefs);
        if(PreferenceUtils.COLOR_BLOCK.equals(colorKey)) {
            mListProjection[3] = Elements.BLOCK;
        } else {
            mListProjection[3] = Elements.CATEGORY;
        }
    }

    /**
     * Setup the listener for the filtering TextView.
     */
    private void setupFilter() {
        final EditText filterEditText = (EditText)getActivity().findViewById(R.id.filter);
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
        final Button sortButton = (Button)getActivity().findViewById(R.id.sort);
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
        fragment.setTargetFragment(this, 0);
        fragment.show(getFragmentManager(), null);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(), Elements.CONTENT_URI, mListProjection, null, null,
                Elements.NUMBER + " ASC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
        final ElementUtils utils = new ElementUtils(getContext());

        final ArrayList<ElementListAdapter.ElementHolder> data = new ArrayList<>();
        while(d.moveToNext()) {
            final String number = d.getString(1);
            final String symbol = d.getString(2);
            final String name = getString(ElementUtils.getElementName(d.getInt(1)));
            final int color = utils.getElementColor(d.getString(3));

            data.add(new ElementListAdapter.ElementHolder(number, symbol, name, color));
        }

        mAdapter.setItems(data);
        mAdapter.getFilter().filter(mFilter);
        mAdapter.setSort(mSort, mSortReverse);

        setListShown(true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PreferenceUtils.getKeyElementColors(getContext()))) {
            loadPreferences();
            getLoaderManager().restartLoader(0, null, this).forceLoad();
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
                            ((ElementListFragment)getTargetFragment()).setSort(item);
                            dialog.dismiss();
                        }
                    })
                    .create();
        }

    }
}
