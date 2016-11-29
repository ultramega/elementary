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

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ultramegatech.ey.provider.Element;
import com.ultramegatech.ey.provider.Elements;
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

        PreferenceManager.getDefaultSharedPreferences(getContext())
                .registerOnSharedPreferenceChangeListener(this);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateViews();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
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
            getActivity().setTitle(getString(R.string.titleElementDetails, name));
        }

        mTxtHeader.setText(name);

        mTxtElementSymbol.setText(mElement.symbol);
        mTxtElementNumber.setText(String.valueOf(mElement.number));
        getWeight(mTxtElementWeight);
        populateBlockElectrons();
        setBlockBackground();

        mTxtNumber.setText(String.valueOf(mElement.number));
        mTxtSymbol.setText(mElement.symbol);
        mTxtSymbol.setContentDescription(mElement.symbol.toUpperCase());
        mTxtName.setText(name);
        getWeight(mTxtWeight);
        getElectronConfiguration(mTxtConfiguration);
        mTxtElectrons.setText(getElectrons());
        mTxtCategory.setText(getCategory());
        getGPB(mTxtGPB);
        mTxtDensity.setText(getDensity());
        mTxtMelt.setText(getTemperature(mElement.melt));
        mTxtBoil.setText(getTemperature(mElement.boil));
        mTxtHeat.setText(getHeat());
        mTxtNegativity.setText(getNegativity());
        mTxtAbundance.setText(getAbundance());
    }

    /**
     * Get the category name.
     *
     * @return The category name
     */
    @NonNull
    private CharSequence getCategory() {
        final CharSequence[] cats = getResources().getTextArray(R.array.ptCategories);
        return cats[mElement.category];
    }

    /**
     * Set the block color based on element properties.
     */
    private void setBlockBackground() {
        final int background = ElementUtils.getElementColor(mElement);
        mElementBlock.setBackgroundColor(background);
    }

    /**
     * Fill the column of electrons in the block.
     */
    private void populateBlockElectrons() {
        mTxtElementElectrons.setText(TextUtils.join("\n", mElement.electrons));
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
     * Get the atomic weight. For unstable elements, the value of the most stable isotope is
     * returned surrounded by brackets.
     *
     * @param textView The TextView to display the weight
     */
    private void getWeight(@NonNull TextView textView) {
        if(mElement.unstable) {
            textView.setText(String.format(Locale.getDefault(), "[%.0f]", mElement.weight));
            textView.setContentDescription(String.valueOf((int)mElement.weight));
        } else {
            textView.setText(DECIMAL_FORMAT.format(mElement.weight));
        }
    }

    /**
     * Get the electron configuration.
     *
     * @param textView The TextView to display the electron configuration
     */
    @SuppressWarnings("deprecation")
    private void getElectronConfiguration(@NonNull TextView textView) {
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
            textView.setText(Html.fromHtml(builder.toString().trim(), 0));
            textView.setContentDescription(Html.fromHtml(descBuilder.toString(), 0));
        } else {
            textView.setText(Html.fromHtml(builder.toString().trim()));
            textView.setContentDescription(Html.fromHtml(descBuilder.toString()));
        }
    }

    /**
     * Get the electrons per shell.
     *
     * @return List of electrons separated by commas
     */
    @Nullable
    private String getElectrons() {
        return TextUtils.join(", ", mElement.electrons);
    }

    /**
     * Get the group, period, and block.
     *
     * @param textView The TextView to display the group, period, and block
     */
    private void getGPB(@NonNull TextView textView) {
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

        textView.setText(builder.toString());
        textView.setContentDescription(descBuilder.toString());
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
     * Launch YouTube video Intent.
     */
    private void showVideo() {
        if(mElement == null) {
            return;
        }
        final Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://dev.ultramegasoft.com/el/video?num=" + mElement.number));
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
