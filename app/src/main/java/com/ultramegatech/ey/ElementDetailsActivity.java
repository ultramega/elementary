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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.Spanned;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.util.CommonMenuHandler;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.util.ActionBarWrapper;
import com.ultramegatech.util.UnitUtils;

import java.text.DecimalFormat;

/**
 * This Activity displays details about a single chemical element. It can be launched by an Intent
 * with an extra identifying the element by database ID or atomic number.
 *
 * @author Steve Guidetti
 */
public class ElementDetailsActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor>, OnSharedPreferenceChangeListener {
    /**
     * Intent extras
     */
    public static final String EXTRA_ATOMIC_NUMBER = "atomic_number";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        ActionBarWrapper.getInstance(this).setDisplayHomeAsUpEnabled(true);

        mStringUnknown = getString(R.string.unknown);
        mDecimalFormat = new DecimalFormat();
        mDecimalFormat.setMaximumFractionDigits(8);

        setContentView(R.layout.element_details);
        findViews();

        loadPreferences();

        getSupportLoaderManager().initLoader(0, null, this).forceLoad();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
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

        final String tempUnit = prefs.getString("tempUnit", "K");
        if("K".equals(tempUnit)) {
            mTemperatureUnits = Units.KELVIN;
        } else if("C".equals(tempUnit)) {
            mTemperatureUnits = Units.CELSIUS;
        } else {
            mTemperatureUnits = Units.FAHRENHEIT;
        }

        final String colorKey = prefs.getString("elementColors", "category");
        if(colorKey.equals("block")) {
            mColorKey = Elements.BLOCK;
        } else {
            mColorKey = Elements.CATEGORY;
        }
    }

    /**
     * Load references to all Views.
     */
    private void findViews() {
        mTxtHeader = (TextView)findViewById(R.id.header);

        mElementBlock = (RelativeLayout)findViewById(R.id.elementBlock);
        mTxtElementSymbol = (TextView)findViewById(R.id.elementSymbol);
        mTxtElementNumber = (TextView)findViewById(R.id.elementNumber);
        mTxtElementWeight = (TextView)findViewById(R.id.elementWeight);
        mTxtElementElectrons = (TextView)findViewById(R.id.elementElectrons);

        mTxtNumber = (TextView)findViewById(R.id.number);
        mTxtSymbol = (TextView)findViewById(R.id.symbol);
        mTxtName = (TextView)findViewById(R.id.name);
        mTxtWeight = (TextView)findViewById(R.id.weight);
        mTxtConfiguration = (TextView)findViewById(R.id.config);
        mTxtElectrons = (TextView)findViewById(R.id.electrons);
        mTxtCategory = (TextView)findViewById(R.id.category);
        mTxtGPB = (TextView)findViewById(R.id.gpb);
        mTxtDensity = (TextView)findViewById(R.id.density);
        mTxtMelt = (TextView)findViewById(R.id.melt);
        mTxtBoil = (TextView)findViewById(R.id.boil);
        mTxtHeat = (TextView)findViewById(R.id.heat);
        mTxtNegativity = (TextView)findViewById(R.id.negativity);
        mTxtAbundance = (TextView)findViewById(R.id.abundance);

        findViewById(R.id.videoButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showVideo();
            }
        });

        findViewById(R.id.wikiButton).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                showWikipedia();
            }
        });
    }

    /**
     * Fill Views with data loaded from the database.
     */
    private void populateViews() {
        final String name =
                getString(ElementUtils.getElementName(mData.getAsInteger(Elements.NUMBER)));

        setTitle(getString(R.string.titleElementDetails, name));

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
        final int background = new ElementUtils(this).getElementColor(key);
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
                    return String.format("%.2f °C", UnitUtils.KtoC(kelvin));
                case FAHRENHEIT:
                    return String.format("%.2f °F", UnitUtils.KtoF(kelvin));
                default:
                    return String.format("%.2f K", kelvin);
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
                return String.format("[%.0f]", value);
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
            return Html.fromHtml(value);
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
        final String videoId = mData.getAsString(Elements.VIDEO);
        if(videoId == null) {
            return;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://m.youtube.com/watch?v=" + videoId));
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
     * Get the content Uri based on an Intent extra.
     *
     * @return The content Uri
     */
    private Uri getUri() {
        final long id = getIntent().getIntExtra(EXTRA_ATOMIC_NUMBER, 0);
        return ContentUris.withAppendedId(Elements.CONTENT_URI_NUMBER, id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        setProgressBarIndeterminateVisibility(true);

        return new CursorLoader(this, getUri(), null, null, null, null);
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

            setProgressBarIndeterminateVisibility(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals("tempUnit")) {
            loadPreferences();
            mTxtMelt.setText(getTemperature(Elements.MELT));
            mTxtBoil.setText(getTemperature(Elements.BOIL));
        } else if(key.equals("elementColors")) {
            loadPreferences();
            setBlockBackground();
        }
    }
}
