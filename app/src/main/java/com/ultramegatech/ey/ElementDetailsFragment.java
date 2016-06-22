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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.util.UnitUtils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * This Fragment displays details about a single chemical element. It can be embedded or shown as a
 * dialog.
 *
 * @author Steve Guidetti
 */
public class ElementDetailsFragment extends DialogFragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     * The tag to identify the Fragment
     */
    private static final String TAG = "ElementDetailsFragment";

    /**
     * Fragment arguments
     */
    private static final String ARG_ATOMIC_NUMBER = "atomic_number";
    private static final String ARG_ATOMIC_SYMBOL = "atomic_symbol";

    /**
     * Units of measurement
     */
    private enum Units {
        KELVIN, CELSIUS, FAHRENHEIT
    }

    /**
     * Values from the database row
     */
    private final ContentValues mData = new ContentValues();

    /**
     * Units to use for temperature values
     */
    private Units mTemperatureUnits;

    /**
     * Field used for coloring the element block
     */
    private String mColorKey;

    /**
     * Header text
     */
    private TextView mTxtHeader;

    /**
     * Element block Views
     */
    private RelativeLayout mElementBlock;
    private TextView mTxtElementSymbol;
    private TextView mTxtElementNumber;
    private TextView mTxtElementWeight;
    private TextView mTxtElementElectrons;

    /**
     * Table Views
     */
    private TextView mTxtNumber;
    private TextView mTxtSymbol;
    private TextView mTxtName;
    private TextView mTxtWeight;
    private TextView mTxtConfiguration;
    private TextView mTxtElectrons;
    private TextView mTxtCategory;
    private TextView mTxtGPB;
    private TextView mTxtDensity;
    private TextView mTxtMelt;
    private TextView mTxtBoil;
    private TextView mTxtHeat;
    private TextView mTxtNegativity;
    private TextView mTxtAbundance;

    /**
     * Value to return for unknown values
     */
    private String mStringUnknown;

    /**
     * Format for decimal values
     */
    private DecimalFormat mDecimalFormat;

    /**
     * Create a new instance of this Fragment.
     *
     * @param atomicNumber The atomic number of the element to display
     * @return An instance of this Fragment
     */
    public static DialogFragment getInstance(int atomicNumber) {
        final DialogFragment fragment = new ElementDetailsFragment();

        final Bundle args = new Bundle();
        args.putInt(ARG_ATOMIC_NUMBER, atomicNumber);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Create a new instance of this Fragment.
     *
     * @param atomicSymbol The atomic symbol of the element to display
     * @return An instance of this Fragment
     */
    public static DialogFragment getInstance(String atomicSymbol) {
        final DialogFragment fragment = new ElementDetailsFragment();

        final Bundle args = new Bundle();
        args.putString(ARG_ATOMIC_SYMBOL, atomicSymbol);
        fragment.setArguments(args);

        return fragment;
    }

    /**
     * Show this Fragment as a dialog.
     *
     * @param fm           The FragmentManager to use
     * @param atomicNumber The atomic number of the element to display
     */
    public static void showDialog(FragmentManager fm, int atomicNumber) {
        getInstance(atomicNumber).show(fm, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadPreferences();

        mStringUnknown = getString(R.string.unknown);
        mDecimalFormat = new DecimalFormat();
        mDecimalFormat.setMaximumFractionDigits(8);

        getLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_element_details, container, false);

        mTxtHeader = (TextView)root.findViewById(R.id.header);

        mElementBlock = (RelativeLayout)root.findViewById(R.id.elementBlock);
        mTxtElementSymbol = (TextView)root.findViewById(R.id.elementSymbol);
        mTxtElementNumber = (TextView)root.findViewById(R.id.elementNumber);
        mTxtElementWeight = (TextView)root.findViewById(R.id.elementWeight);
        mTxtElementElectrons = (TextView)root.findViewById(R.id.elementElectrons);

        mTxtNumber = (TextView)root.findViewById(R.id.number);
        mTxtSymbol = (TextView)root.findViewById(R.id.symbol);
        mTxtName = (TextView)root.findViewById(R.id.name);
        mTxtWeight = (TextView)root.findViewById(R.id.weight);
        mTxtConfiguration = (TextView)root.findViewById(R.id.config);
        mTxtElectrons = (TextView)root.findViewById(R.id.electrons);
        mTxtCategory = (TextView)root.findViewById(R.id.category);
        mTxtGPB = (TextView)root.findViewById(R.id.gpb);
        mTxtDensity = (TextView)root.findViewById(R.id.density);
        mTxtMelt = (TextView)root.findViewById(R.id.melt);
        mTxtBoil = (TextView)root.findViewById(R.id.boil);
        mTxtHeat = (TextView)root.findViewById(R.id.heat);
        mTxtNegativity = (TextView)root.findViewById(R.id.negativity);
        mTxtAbundance = (TextView)root.findViewById(R.id.abundance);

        root.findViewById(R.id.videoButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showVideo();
            }
        });

        root.findViewById(R.id.wikiButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showWikipedia();
            }
        });

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Load relevant preferences.
     */
    private void loadPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        final String tempUnit = PreferenceUtils.getPrefTempUnit(getContext(), prefs);
        if(PreferenceUtils.TEMP_K.equals(tempUnit)) {
            mTemperatureUnits = Units.KELVIN;
        } else if(PreferenceUtils.TEMP_C.equals(tempUnit)) {
            mTemperatureUnits = Units.CELSIUS;
        } else {
            mTemperatureUnits = Units.FAHRENHEIT;
        }

        final String colorKey = PreferenceUtils.getPrefElementColors(getContext(), prefs);
        if(PreferenceUtils.COLOR_BLOCK.equals(colorKey)) {
            mColorKey = Elements.BLOCK;
        } else {
            mColorKey = Elements.CATEGORY;
        }
    }

    /**
     * Fill Views with data loaded from the database.
     */
    private void populateViews() {
        final String name =
                getString(ElementUtils.getElementName(mData.getAsInteger(Elements.NUMBER)));

        if(!getShowsDialog()) {
            getActivity().setTitle(getString(R.string.titleElementDetails, name));
        }

        mTxtHeader.setText(name);

        mTxtElementSymbol.setText(mData.getAsString(Elements.SYMBOL));
        mTxtElementNumber.setText(mData.getAsString(Elements.NUMBER));
        mTxtElementWeight.setText(getWeight());
        populateBlockElectrons();
        setBlockBackground();

        mTxtNumber.setText(mData.getAsString(Elements.NUMBER));
        mTxtSymbol.setText(mData.getAsString(Elements.SYMBOL));
        mTxtName.setText(name);
        mTxtWeight.setText(getWeight());
        mTxtConfiguration.setText(getElectronConfiguration());
        mTxtElectrons.setText(getElectrons());
        mTxtCategory.setText(getCategory());
        mTxtGPB.setText(getGPB());
        mTxtDensity.setText(getDensity());
        mTxtMelt.setText(getTemperature(Elements.MELT));
        mTxtBoil.setText(getTemperature(Elements.BOIL));
        mTxtHeat.setText(getHeat());
        mTxtNegativity.setText(getNegativity());
        mTxtAbundance.setText(getAbundance());
    }

    /**
     * Get the category name.
     *
     * @return The category name
     */
    private CharSequence getCategory() {
        final CharSequence[] cats = getResources().getTextArray(R.array.ptCategories);
        return cats[mData.getAsInteger(Elements.CATEGORY)];
    }

    /**
     * Set the block color based on element properties.
     */
    private void setBlockBackground() {
        final String key = mData.getAsString(mColorKey);
        if(key == null) {
            return;
        }
        final int background = new ElementUtils(getContext()).getElementColor(key);
        mElementBlock.setBackgroundColor(background);
    }

    /**
     * Fill the column of electrons in the block.
     */
    private void populateBlockElectrons() {
        final String electrons = mData.getAsString(Elements.ELECTRONS);
        if(electrons != null) {
            mTxtElementElectrons.setText(electrons.replace(',', '\n'));
        }
    }

    /**
     * Get a value as a temperature string.
     *
     * @param key The value to read
     * @return The converted temperature string
     */
    private String getTemperature(String key) {
        final Double kelvin = mData.getAsDouble(key);
        if(kelvin != null) {
            switch(mTemperatureUnits) {
                case CELSIUS:
                    return String.format(Locale.getDefault(), "%.2f °C", UnitUtils.KtoC(kelvin));
                case FAHRENHEIT:
                    return String.format(Locale.getDefault(), "%.2f °F", UnitUtils.KtoF(kelvin));
                default:
                    return String.format(Locale.getDefault(), "%.2f K", kelvin);
            }
        }

        return mStringUnknown;
    }

    /**
     * Get the atomic weight. For unstable elements, the value of the most stable isotope is
     * returned surrounded by brackets.
     *
     * @return The atomic weight
     */
    private String getWeight() {
        final Double value = mData.getAsDouble(Elements.WEIGHT);
        if(value != null) {
            final Boolean unstable = mData.getAsBoolean(Elements.UNSTABLE);
            if(unstable != null && unstable) {
                return String.format(Locale.getDefault(), "[%.0f]", value);
            }
            return mDecimalFormat.format(value);
        }
        return null;
    }

    /**
     * Get the electron configuration.
     *
     * @return The formatted electron configuration
     */
    private Spanned getElectronConfiguration() {
        String value = mData.getAsString(Elements.CONFIGURATION);
        if(value != null) {
            value = value.replaceAll("([spdf])([0-9]+)", "$1<sup><small>$2</small></sup>");
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return Html.fromHtml(value, 0);
            } else {
                //noinspection deprecation
                return Html.fromHtml(value);
            }
        }
        return null;
    }

    /**
     * Get the electrons per shell.
     *
     * @return List of electrons separated by commas
     */
    private String getElectrons() {
        final String value = mData.getAsString(Elements.ELECTRONS);
        if(value != null) {
            return value.replace(",", ", ");
        }
        return null;
    }

    /**
     * Get the group, period, and block.
     *
     * @return Group, period, block
     */
    private String getGPB() {
        String group = mData.getAsString(Elements.GROUP);
        String period = mData.getAsString(Elements.PERIOD);
        String block = mData.getAsString(Elements.BLOCK);
        if(group == null || group.equals("0")) {
            group = "n/a";
        }
        return group + ", " + period + ", " + block;
    }

    /**
     * Get the density value with unit.
     *
     * @return The density
     */
    private String getDensity() {
        final Double value = mData.getAsDouble(Elements.DENSITY);
        if(value != null) {
            return mDecimalFormat.format(value) + " g/cm³";
        }
        return mStringUnknown;
    }

    /**
     * Get the heat value with unit.
     *
     * @return The heat
     */
    private String getHeat() {
        final Double value = mData.getAsDouble(Elements.HEAT);
        if(value != null) {
            return mDecimalFormat.format(value) + " J/g·K";
        }
        return mStringUnknown;
    }

    /**
     * Get the electronegativity value with unit.
     *
     * @return The electronegativity
     */
    private String getNegativity() {
        final Double value = mData.getAsDouble(Elements.NEGATIVITY);
        if(value != null) {
            return mDecimalFormat.format(value) + " V";
        }
        return mStringUnknown;
    }

    /**
     * Get the abundance value with unit.
     *
     * @return The abundance
     */
    private String getAbundance() {
        final Double value = mData.getAsDouble(Elements.ABUNDANCE);
        if(value != null) {
            if(value < 0.001) {
                return "<0.001 mg/kg";
            }
            return mDecimalFormat.format(value) + " mg/kg";
        }
        return mStringUnknown;
    }

    /**
     * Launch YouTube video Intent.
     */
    private void showVideo() {
        final Integer num = mData.getAsInteger(Elements.NUMBER);
        if(num == null) {
            return;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://dev.ultramegasoft.com/el/video?num=" + num));
        startActivity(intent);
    }

    /**
     * Launch view Intent for the Wikipedia page.
     */
    private void showWikipedia() {
        final int pageId = ElementUtils.getElementWiki(mData.getAsInteger(Elements.NUMBER));
        final Uri uri = Uri.parse(String.format("https://%s.m.wikipedia.org/wiki/%s",
                getString(R.string.wikiLang), getString(pageId)));
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    /**
     * Get the content Uri based on the Fragment argument.
     *
     * @return The content Uri
     */
    private Uri getUri() {
        final Bundle args = getArguments();
        if(args != null) {
            if(args.containsKey(ARG_ATOMIC_NUMBER)) {
                return ContentUris.withAppendedId(Elements.CONTENT_URI_ITEM,
                        args.getInt(ARG_ATOMIC_NUMBER));
            } else if(args.containsKey(ARG_ATOMIC_SYMBOL)) {
                return Uri.withAppendedPath(Elements.CONTENT_URI_ITEM,
                        args.getString(ARG_ATOMIC_SYMBOL));
            }
        }
        return ContentUris.withAppendedId(Elements.CONTENT_URI_ITEM, 0);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getContext(), getUri(), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor d) {
        if(d.moveToFirst()) {
            final String[] columns = d.getColumnNames();
            for(int i = 0; i < columns.length; i++) {
                if(d.isNull(i)) {
                    continue;
                }

                switch(Elements.getColumnType(columns[i])) {
                    case INTEGER:
                        mData.put(columns[i], d.getInt(i));
                        break;
                    case REAL:
                        mData.put(columns[i], d.getDouble(i));
                        break;
                    case BOOLEAN:
                        mData.put(columns[i], d.getInt(i) == 1);
                        break;
                    case TEXT:
                        mData.put(columns[i], d.getString(i));
                        break;
                }
            }

            populateViews();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(PreferenceUtils.getKeyTempUnit(getContext()))) {
            loadPreferences();
            mTxtMelt.setText(getTemperature(Elements.MELT));
            mTxtBoil.setText(getTemperature(Elements.BOIL));
        } else if(key.equals(PreferenceUtils.getKeyElementColors(getContext()))) {
            loadPreferences();
            setBlockBackground();
        }
    }
}
