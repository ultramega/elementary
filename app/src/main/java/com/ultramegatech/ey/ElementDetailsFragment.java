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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceManager;

import com.ultramegatech.ey.provider.Element;
import com.ultramegatech.ey.provider.Elements;
import com.ultramegatech.ey.provider.Isotope;
import com.ultramegatech.ey.provider.Isotopes;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;
import com.ultramegatech.ey.util.UnitUtils;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * This Fragment displays details about a single chemical element. It can be embedded or shown as a
 * dialog.
 *
 * @author Steve Guidetti
 */
@SuppressWarnings("WeakerAccess")
public class ElementDetailsFragment extends DialogFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
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
     * The table for common isotopes.
     */
    private TableLayout mIsoTable;

    /**
     * Value to return for unknown values
     */
    private String mStringUnknown;

    /**
     * Format for decimal values
     */
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();

    static {
        DECIMAL_FORMAT.setMaximumFractionDigits(8);
    }

    /**
     * The Element being displayed
     */
    private Element mElement;

    /**
     * Create a new instance of this Fragment.
     *
     * @param atomicNumber The atomic number of the element to display
     * @return An instance of this Fragment
     */
    @NonNull
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
    @NonNull
    public static DialogFragment getInstance(@NonNull String atomicSymbol) {
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
    public static void showDialog(@NonNull FragmentManager fm, int atomicNumber) {
        getInstance(atomicNumber).show(fm, TAG);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);

        final Context context = getContext();
        if (context != null) {
            PreferenceManager.getDefaultSharedPreferences(context)
                    .registerOnSharedPreferenceChangeListener(this);
        }

        final Bundle args = getArguments();
        if(args != null) {
            if(args.containsKey(ARG_ATOMIC_NUMBER)) {
                mElement = Elements.getElement(args.getInt(ARG_ATOMIC_NUMBER));
            } else if(args.containsKey(ARG_ATOMIC_SYMBOL)) {
                mElement = Elements.getElement(args.getString(ARG_ATOMIC_SYMBOL));
            }
        }

        mStringUnknown = getString(R.string.unknown);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_element_details, container, false);

        mTxtHeader = root.findViewById(R.id.header);

        mElementBlock = root.findViewById(R.id.elementBlock);
        mTxtElementSymbol = root.findViewById(R.id.elementSymbol);
        mTxtElementNumber = root.findViewById(R.id.elementNumber);
        mTxtElementWeight = root.findViewById(R.id.elementWeight);
        mTxtElementElectrons = root.findViewById(R.id.elementElectrons);

        mTxtNumber = root.findViewById(R.id.number);
        mTxtSymbol = root.findViewById(R.id.symbol);
        mTxtName = root.findViewById(R.id.name);
        mTxtWeight = root.findViewById(R.id.weight);
        mTxtConfiguration = root.findViewById(R.id.config);
        mTxtElectrons = root.findViewById(R.id.electrons);
        mTxtCategory = root.findViewById(R.id.category);
        mTxtGPB = root.findViewById(R.id.gpb);
        mTxtDensity = root.findViewById(R.id.density);
        mTxtMelt = root.findViewById(R.id.melt);
        mTxtBoil = root.findViewById(R.id.boil);
        mTxtHeat = root.findViewById(R.id.heat);
        mTxtNegativity = root.findViewById(R.id.negativity);
        mTxtAbundance = root.findViewById(R.id.abundance);

        mIsoTable = root.findViewById(R.id.isoTable);

        root.findViewById(R.id.videoButton).setOnClickListener(v -> showVideo());

        root.findViewById(R.id.wikiButton).setOnClickListener(v -> showWikipedia());

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final Context context = getContext();
        if (context != null) {
            final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            prefs.unregisterOnSharedPreferenceChangeListener(this);
        }
    }

    /**
     * Fill Views with element data.
     */
    private void populateViews() {
        if(mElement == null) {
            return;
        }
        final String name = getString(ElementUtils.getElementName(mElement.number));

        if(!getShowsDialog()) {
            final Activity activity = getActivity();
            if(activity != null) {
                activity.setTitle(getString(R.string.titleElementDetails, name));
            }
        }

        mTxtHeader.setText(name);
        mTxtName.setText(name);

        setBlockBackground();

        getNumber();
        getSymbol();
        getWeight();
        getCategory();
        getGPB();
        getElectronConfiguration();
        getElectrons();
        mTxtDensity.setText(getDensity());
        mTxtMelt.setText(getTemperature(mElement.melt));
        mTxtBoil.setText(getTemperature(mElement.boil));
        mTxtHeat.setText(getHeat());
        mTxtNegativity.setText(getNegativity());
        mTxtAbundance.setText(getAbundance());

        populateIsotopes();
    }

    /**
     * Set the block color based on element properties.
     */
    private void setBlockBackground() {
        final int background = ElementUtils.getElementColor(mElement);
        mElementBlock.setBackgroundColor(background);
    }

    /**
     * Get the atomic number.
     */
    private void getNumber() {
        mTxtNumber.setText(String.valueOf(mElement.number));
        mTxtElementNumber.setText(String.valueOf(mElement.number));
    }

    /**
     * Get the element symbol.
     */
    private void getSymbol() {
        mTxtSymbol.setText(mElement.symbol);
        mTxtSymbol.setContentDescription(mElement.symbol.toUpperCase());
        mTxtElementSymbol.setText(mElement.symbol);
    }

    /**
     * Get the atomic weight. For unstable elements, the value of the most stable isotope is
     * returned surrounded by brackets.
     */
    private void getWeight() {
        if(mElement.unstable) {
            mTxtWeight.setText(String.format(Locale.getDefault(), "[%.0f]", mElement.weight));
            mTxtWeight.setContentDescription(String.valueOf((int)mElement.weight));
        } else {
            mTxtWeight.setText(DECIMAL_FORMAT.format(mElement.weight));
        }
        mTxtElementWeight.setText(mTxtWeight.getText());
    }

    /**
     * Get the category name.
     */
    private void getCategory() {
        final CharSequence[] cats = getResources().getTextArray(R.array.ptCategories);
        mTxtCategory.setText(cats[mElement.category]);
    }

    /**
     * Get the group, period, and block.
     */
    private void getGPB() {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder descBuilder = new StringBuilder();

        if(mElement.group == 0) {
            builder.append("∅, ");
        } else {
            builder.append(mElement.group).append(", ");
            descBuilder.append(getString(R.string.descGroup)).append(' ').append(mElement.group)
                    .append(", ");
        }

        builder.append(mElement.period).append(", ");
        descBuilder.append(getString(R.string.descPeriod)).append(' ').append(mElement.period)
                .append(", ");

        builder.append(mElement.block);
        descBuilder.append(getString(R.string.descBlock)).append(' ')
                .append(String.valueOf(mElement.block).toUpperCase());

        mTxtGPB.setText(builder.toString());
        mTxtGPB.setContentDescription(descBuilder.toString());
    }

    /**
     * Get the electron configuration.
     */
    private void getElectronConfiguration() {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder descBuilder = new StringBuilder();

        if(mElement.configuration.baseElement != null) {
            builder.append('[').append(mElement.configuration.baseElement).append("] ");
            final Element baseElement = Elements.getElement(mElement.configuration.baseElement);
            if(baseElement != null) {
                descBuilder.append(getString(ElementUtils.getElementName(baseElement.number)));
                descBuilder.append(", ");
            }
        }

        for(Element.Orbital orbital : mElement.configuration.orbitals) {
            builder.append(orbital.shell).append(orbital.orbital);
            builder.append("<sup><small>").append(orbital.electrons).append("</small></sup> ");
            descBuilder.append(orbital.shell).append(' ');
            descBuilder.append(String.valueOf(orbital.orbital).toUpperCase()).append(' ');
            descBuilder.append(orbital.electrons).append(", ");
        }

        descBuilder.delete(descBuilder.length() - 2, descBuilder.length() - 1);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mTxtConfiguration.setText(Html.fromHtml(builder.toString().trim(), 0));
            mTxtConfiguration.setContentDescription(Html.fromHtml(descBuilder.toString(), 0));
        } else {
            mTxtConfiguration.setText(Html.fromHtml(builder.toString().trim()));
            mTxtConfiguration.setContentDescription(Html.fromHtml(descBuilder.toString()));
        }
    }

    /**
     * Get the electrons per shell.
     */
    private void getElectrons() {
        mTxtElectrons.setText(TextUtils.join(", ", mElement.electrons));
        mTxtElementElectrons.setText(TextUtils.join("\n", mElement.electrons));
    }

    /**
     * Get the density value with unit.
     *
     * @return The density
     */
    @NonNull
    private String getDensity() {
        if(mElement.density != null) {
            return DECIMAL_FORMAT.format(mElement.density) + " g/cm³";
        }
        return mStringUnknown;
    }

    /**
     * Get a value as a temperature string.
     *
     * @param kelvin The temperature in Kelvin
     * @return The converted temperature string
     */
    @NonNull
    private String getTemperature(@Nullable Double kelvin) {
        if(kelvin != null) {
            switch(PreferenceUtils.getPrefTempUnit()) {
                case PreferenceUtils.TEMP_C:
                    return String.format(Locale.getDefault(), "%.2f ℃", UnitUtils.KtoC(kelvin));
                case PreferenceUtils.TEMP_F:
                    return String.format(Locale.getDefault(), "%.2f ℉", UnitUtils.KtoF(kelvin));
                default:
                    return String.format(Locale.getDefault(), "%.2f K", kelvin);
            }
        }

        return mStringUnknown;
    }

    /**
     * Get the heat value with unit.
     *
     * @return The heat
     */
    @NonNull
    private String getHeat() {
        if(mElement.heat != null) {
            return DECIMAL_FORMAT.format(mElement.heat) + " J/g·K";
        }
        return mStringUnknown;
    }

    /**
     * Get the electronegativity value with unit.
     *
     * @return The electronegativity
     */
    @NonNull
    private String getNegativity() {
        if(mElement.negativity != null) {
            return DECIMAL_FORMAT.format(mElement.negativity);
        }
        return mStringUnknown;
    }

    /**
     * Get the abundance value with unit.
     *
     * @return The abundance
     */
    @NonNull
    private String getAbundance() {
        if(mElement.abundance != null) {
            if(mElement.abundance < 0.001) {
                return "<0.001 mg/kg";
            }
            return DECIMAL_FORMAT.format(mElement.abundance) + " mg/kg";
        }
        return mStringUnknown;
    }

    /**
     * Populate the table of common isotopes.
     */
    private void populateIsotopes() {
        final Isotope[] isotopes = Isotopes.getIsotopes(mElement.number);
        if(isotopes != null) {
            final LayoutInflater inflater = getLayoutInflater();
            for(Isotope isotope : isotopes) {
                final TableRow tableRow =
                        (TableRow)inflater.inflate(R.layout.isotope_table_row, mIsoTable, false);

                final TextView symbolText = tableRow.findViewById(R.id.isoSymbol);
                symbolText.setText(isotope.getSymbol());

                final TextView massText = tableRow.findViewById(R.id.isoMass);
                massText.setText(DECIMAL_FORMAT.format(isotope.mass));

                if (isotope.ic != null) {
                    final TextView icText = tableRow.findViewById(R.id.isoComp);
                    icText.setText(DECIMAL_FORMAT.format(isotope.ic));
                }

                mIsoTable.addView(tableRow);
            }
        }
    }

    /**
     * Launch YouTube video Intent.
     */
    private void showVideo() {
        if(mElement == null) {
            return;
        }

        final int resId = ElementUtils.getElementVideo(mElement.number);
        if(resId == 0) {
            return;
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.youtube.com/watch?v=" + getString(resId)));
        startActivity(intent);
    }

    /**
     * Launch view Intent for the Wikipedia page.
     */
    private void showWikipedia() {
        if(mElement == null) {
            return;
        }
        final int pageId = ElementUtils.getElementWiki(mElement.number);
        final Uri uri = Uri.parse(String.format("https://%s.m.wikipedia.org/wiki/%s",
                getString(R.string.wikiLang), getString(pageId)));
        final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(PreferenceUtils.KEY_TEMP_UNITS.equals(key)) {
            if(mElement != null) {
                mTxtMelt.setText(getTemperature(mElement.melt));
                mTxtBoil.setText(getTemperature(mElement.boil));
            }
        } else if(PreferenceUtils.KEY_ELEMENT_COLORS.equals(key)) {
            setBlockBackground();
        }
    }
}
