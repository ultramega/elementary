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
package com.ultramegatech.ey.widget;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ultramegatech.ey.R;
import com.ultramegatech.ey.provider.Element;
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.ElementUtils;

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
    @SuppressWarnings("unused")
    public static final int SORT_NAME = 1;

    /**
     * The Context
     */
    @NonNull
    private final Context mContext;

    /**
     * The filter for this ListAdapter
     */
    @NonNull
    private final Filter mFilter;

    /**
     * The original data set
     */
    @NonNull
    private final ElementHolder[] mListItems;

    /**
     * The filtered and sorted data set
     */
    @NonNull
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
    public ElementListAdapter(@NonNull Context context) {
        mContext = context;

        final Element[] elements = Elements.getElements();
        mListItems = new ElementHolder[elements.length];
        Element element;
        for(int i = 0; i < elements.length; i++) {
            element = elements[i];
            mListItems[i] = new ElementHolder(context, element);
        }

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
    public void notifyDataSetInvalidated() {
        super.notifyDataSetInvalidated();
        for(ElementHolder holder : mListItems) {
            holder.color = ElementUtils.getElementColor(holder.element);
        }
    }

    @Override
    public int getCount() {
        return mFiltered.size();
    }

    @Override
    public ElementHolder getItem(int position) {
        return mFiltered.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mFiltered.get(position).element.number;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.element_list_item, parent, false);
            final ViewHolder holder = new ViewHolder();
            holder.block = convertView.findViewById(R.id.block);
            holder.number = convertView.findViewById(R.id.number);
            holder.symbol = convertView.findViewById(R.id.symbol);
            holder.name = convertView.findViewById(R.id.name);
            convertView.setTag(holder);
        }

        final ElementHolder item = getItem(position);
        final ViewHolder holder = (ViewHolder)convertView.getTag();

        holder.number.setText(String.valueOf(item.element.number));
        holder.symbol.setText(item.element.symbol);
        holder.symbol.setContentDescription(item.element.symbol.toUpperCase());
        holder.name.setText(item.name);
        holder.block.setBackgroundColor(item.color);

        return convertView;
    }

    @NonNull
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
            if(getItem(i).element.number == id) {
                return i;
            }
        }
        return ListView.INVALID_POSITION;
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
    private void filterList(@Nullable CharSequence filter) {
        mFiltered.clear();

        if(TextUtils.isEmpty(filter)) {
            Collections.addAll(mFiltered, mListItems);
            return;
        }

        for(ElementHolder element : mListItems) {
            if(element.element.symbol.toLowerCase().startsWith(filter.toString().toLowerCase())
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
    private static class ElementHolder {
        /**
         * The Element
         */
        @NonNull
        final Element element;

        /**
         * The element name
         */
        @NonNull
        final String name;

        /**
         * The block color
         */
        int color;

        /**
         * @param context The Context
         * @param element The Element
         */
        ElementHolder(@NonNull Context context, @NonNull Element element) {
            this.element = element;
            this.name = context.getString(ElementUtils.getElementName(element.number));
            this.color = ElementUtils.getElementColor(element);
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
        ElementComparator(int sortField) {
            mSortField = sortField;
        }

        @Override
        public int compare(ElementHolder l, ElementHolder r) {
            if(mSortField == SORT_NUMBER) {
                return l.element.number - r.element.number;
            }

            return l.name.compareTo(r.name);
        }
    }

    /**
     * Caches references to Views within a layout.
     */
    private static class ViewHolder {
        /**
         * The element block container
         */
        View block;

        /**
         * The element number
         */
        TextView number;

        /**
         * The element symbol
         */
        TextView symbol;

        /**
         * The element name
         */
        TextView name;
    }
}
