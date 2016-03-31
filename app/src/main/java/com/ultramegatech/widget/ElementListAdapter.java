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
package com.ultramegatech.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ultramegatech.ey.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This custom ListAdapter is for displaying a list of elements.
 *
 * @author Steve Guidetti
 */
public class ElementListAdapter extends BaseAdapter implements ListAdapter, Filterable {
    /**
     * Sorting options
     */
    public static final int SORT_NUMBER = 0;
    public static final int SORT_NAME = 1;

    /**
     * The Context
     */
    private final Context mContext;

    /**
     * The filter for this ListAdapter
     */
    private final Filter mFilter;

    /**
     * The original data set
     */
    private final ArrayList<ElementHolder> mListItems = new ArrayList<>();

    /**
     * The filtered and sorted data set
     */
    private final ArrayList<ElementHolder> mFiltered = new ArrayList<>();

    /**
     * The current field used for sorting
     */
    private int mSort = SORT_NUMBER;

    /**
     * The current sorting direction
     */
    private boolean mSortReverse = false;

    /**
     * @param context The Context
     */
    public ElementListAdapter(Context context) {
        mContext = context;

        mFilter = new Filter() {
            @Override
            protected Filter.FilterResults performFiltering(CharSequence cs) {
                filterList(cs);
                sortList(mSort, mSortReverse);
                return null;
            }

            @Override
            protected void publishResults(CharSequence cs, Filter.FilterResults fr) {
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int getCount() {
        return mFiltered.size();
    }

    @Override
    public ElementHolder getItem(int i) {
        return mFiltered.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mFiltered.get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup vg) {
        if(view == null) {
            view = View.inflate(mContext, R.layout.element_list_item, null);
        }

        final ElementHolder holder = getItem(i);

        ((TextView)view.findViewById(R.id.number)).setText(holder.number);
        ((TextView)view.findViewById(R.id.symbol)).setText(holder.symbol);
        ((TextView)view.findViewById(R.id.name)).setText(holder.name);
        view.findViewById(R.id.block).setBackgroundColor(holder.color);

        return view;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * Get the position of the element with the specified ID.
     *
     * @param id The ID of the element
     * @return The position of the element in the list
     */
    public int getItemPosition(long id) {
        for(int i = 0; i < mFiltered.size(); i++) {
            if(getItem(i).id == id) {
                return i;
            }
        }
        return ListView.INVALID_POSITION;
    }

    /**
     * Set the list of elements
     *
     * @param listItems The list of elements
     */
    public void setItems(ArrayList<ElementHolder> listItems) {
        mListItems.clear();
        mFiltered.clear();

        if(listItems != null) {
            mListItems.addAll(listItems);
            mFiltered.addAll(listItems);
        }

        notifyDataSetChanged();
    }

    /**
     * Set the field used to sort elements.
     *
     * @param sortBy  One of the SORT_ constants
     * @param reverse Whether to sort items in reverse order
     */
    public void setSort(int sortBy, boolean reverse) {
        sortList(sortBy, reverse);
        notifyDataSetChanged();
    }

    /**
     * Filter the original data set.
     *
     * @param filter Text used to filter the elements
     */
    private void filterList(CharSequence filter) {
        mFiltered.clear();

        if(TextUtils.isEmpty(filter)) {
            mFiltered.addAll(mListItems);
            return;
        }

        for(ElementHolder element : mListItems) {
            if(element.symbol.toLowerCase().startsWith(filter.toString().toLowerCase())
                    || element.name.toLowerCase().startsWith(filter.toString().toLowerCase())) {
                mFiltered.add(element);
            }
        }
    }

    /**
     * Sort the filtered list.
     *
     * @param sortBy  One of the SORT_ constants
     * @param reverse Whether to sort items in reverse order
     */
    private void sortList(int sortBy, boolean reverse) {
        mSort = sortBy;
        mSortReverse = reverse;

        Collections.sort(mFiltered, new ElementComparator(sortBy));
        if(reverse) {
            Collections.reverse(mFiltered);
        }
    }

    /**
     * Class to hold data for a single element.
     */
    public static class ElementHolder {
        /**
         * The database ID
         */
        public final long id;

        /**
         * The atomic number
         */
        public final String number;

        /**
         * The element symbol
         */
        public final String symbol;

        /**
         * The element name
         */
        public final String name;

        /**
         * The block color
         */
        public final int color;

        /**
         * @param number The atomic number
         * @param symbol The element symbol
         * @param name   The element name
         * @param color  The block color
         */
        public ElementHolder(String number, String symbol, String name, int color) {
            this.id = Long.valueOf(number);
            this.number = number;
            this.symbol = symbol;
            this.name = name;
            this.color = color;
        }

    }

    /**
     * Comparator used for sorting elements.
     */
    private static class ElementComparator implements Comparator<ElementHolder> {
        /**
         * The field to sort by
         */
        private final int mSortField;

        /**
         * @param sortField One of the SORT_ constants
         */
        public ElementComparator(int sortField) {
            mSortField = sortField;
        }

        @Override
        public int compare(ElementHolder l, ElementHolder r) {
            if(mSortField == SORT_NUMBER) {
                return Integer.valueOf(l.number) - Integer.valueOf(r.number);
            }

            return l.name.compareTo(r.name);
        }

    }
}
