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

/**
 * Stores data for a single block on a PeriodicTableView.
 *
 * @author Steve Guidetti
 */
public class PeriodicTableBlock {
    /**
     * Atomic number
     */
    public int number;

    /**
     * Element symbol
     */
    public String symbol;

    /**
     * Text to display below the symbol
     */
    public String subtext;

    /**
     * Element group, 0 for lanthinoids and actinoids
     */
    public int group;

    /**
     * Element period
     */
    public int period;

    /**
     * Category used for coloring
     */
    public Object category;

    /**
     * Grid position
     */
    public int row;
    public int col;

    /**
     * Block background color
     */
    public int color = 0xFFCCCCCC;
}
