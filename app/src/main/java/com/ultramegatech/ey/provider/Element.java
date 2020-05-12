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
package com.ultramegatech.ey.provider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Container for a single element.
 *
 * @author Steve Guidetti
 */
public class Element {
    /**
     * The element number
     */
    public final int number;

    /**
     * The element symbol
     */
    @NonNull
    public final String symbol;

    /**
     * The group
     */
    public final int group;

    /**
     * The period
     */
    public final int period;

    /**
     * The block
     */
    public final char block;

    /**
     * The atomic weight
     */
    public final double weight;

    /**
     * The density in g/cm³
     */
    @Nullable
    public final Double density;

    /**
     * The melting point in K
     */
    @Nullable
    public final Double melt;

    /**
     * The boiling point in K
     */
    @Nullable
    public final Double boil;

    /**
     * The specific heat in J/g·K
     */
    @Nullable
    public final Double heat;

    /**
     * The electronegativity on the Pauling scale
     */
    @Nullable
    public final Double negativity;

    /**
     * The abundance in mg/kg
     */
    @Nullable
    public final Double abundance;

    /**
     * The category
     */
    public final int category;

    /**
     * The electron configuration
     */
    @NonNull
    public final Configuration configuration;

    /**
     * The number of electrons per shell
     */
    @NonNull
    public final Integer[] electrons;

    /**
     * Whether the element is unstable
     */
    public final boolean unstable;

    /**
     * @param number        The element number
     * @param symbol        The element symbol
     * @param group         The group
     * @param period        The period
     * @param block         The block
     * @param weight        The atomic weight
     * @param density       The density in g/cm³
     * @param melt          The melting point in K
     * @param boil          The boiling point in K
     * @param heat          The specific heat in J/g·K
     * @param negativity    The electronegativity on the Pauling scale
     * @param abundance     The abundance in mg/kg
     * @param category      The category
     * @param configuration The electron configuration
     * @param electrons     The number of electrons per shell
     * @param unstable      Whether the element is unstable
     */
    Element(int number, @NonNull String symbol, int group, int period, char block, double weight,
            @Nullable Double density, @Nullable Double melt, @Nullable Double boil,
            @Nullable Double heat, @Nullable Double negativity, @Nullable Double abundance,
            int category, @NonNull Configuration configuration, @NonNull Integer[] electrons,
            boolean unstable) {
        this.number = number;
        this.symbol = symbol;
        this.group = group;
        this.period = period;
        this.block = block;
        this.weight = weight;
        this.density = density;
        this.melt = melt;
        this.boil = boil;
        this.heat = heat;
        this.negativity = negativity;
        this.abundance = abundance;
        this.category = category;
        this.configuration = configuration;
        this.electrons = electrons;
        this.unstable = unstable;
    }

    /**
     * Represents the electron configuration of an element.
     */
    public static class Configuration {
        /**
         * The symbol of the base element that this configuration is built upon, if any
         */
        @Nullable
        public final String baseElement;

        /**
         * The list of orbitals
         */
        @NonNull
        public final Orbital[] orbitals;

        /**
         * @param baseElement The symbol of the base element that this configuration is built upon,
         *                    if any
         * @param orbitals    The list of orbitals
         */
        Configuration(@Nullable String baseElement, @NonNull Orbital[] orbitals) {
            this.baseElement = baseElement;
            this.orbitals = orbitals;
        }
    }

    /**
     * Represents an orbital in an electron configuration.
     */
    public static class Orbital {
        /**
         * The shell number
         */
        public final int shell;

        /**
         * The orbital type
         */
        public final char orbital;

        /**
         * The number of electrons in this orbital
         */
        public final int electrons;

        /**
         * @param shell     The shell number
         * @param orbital   The orbital type
         * @param electrons The number of electrons in this orbital
         */
        Orbital(int shell, char orbital, int electrons) {
            this.shell = shell;
            this.orbital = orbital;
            this.electrons = electrons;
        }
    }
}
