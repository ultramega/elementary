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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.EdgeEffect;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.widget.EdgeEffectCompat;
import androidx.customview.widget.ExploreByTouchHelper;

import com.ultramegatech.ey.R;
import com.ultramegatech.ey.provider.Element;
import com.ultramegatech.ey.util.ElementUtils;
import com.ultramegatech.ey.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Zoomable, color coded View of the Periodic Table of the Elements. Renders a list of
 * PeriodicTableBlock objects in the standard Periodic Table layout. Also implements a custom
 * PeriodicTableListener that passes the selected PeriodicTableBlock object.
 *
 * @author Steve Guidetti
 */
@SuppressWarnings("unused")
public class PeriodicTableView extends View {
    /**
     * The amount to zoom in or out for programmatic zooms
     */
    private static final float ZOOM_STEP = 0.5f;

    /**
     * The maximum zoom level
     */
    private static final float MAX_ZOOM = 8f;

    /**
     * Color value for the selected block indicator
     */
    private static final int COLOR_SELECTED = 0x9900d4ff;

    /**
     * Color value for the text within the blocks
     */
    private static final int COLOR_BLOCK_FOREGROUND = 0xff000000;

    /**
     * Default color values
     */
    private static final int COLOR_DEFAULT_FOREGROUND = 0xff000000;
    private static final int COLOR_DEFAULT_BACKGROUND = 0xffffffff;

    /**
     * Callback interface for events.
     */
    public interface PeriodicTableListener {
        /**
         * Called when a block is clicked.
         *
         * @param item The selected block
         */
        void onItemClick(@NonNull PeriodicTableBlock item);

        /**
         * Called when a zoom operation has completed.
         *
         * @param periodicTableView The PeriodicTableView
         */
        void onZoomEnd(@NonNull PeriodicTableView periodicTableView);
    }

    /**
     * The list of blocks to render
     */
    @NonNull
    private final List<PeriodicTableBlock> mPeriodicTableBlocks = new ArrayList<>();

    /**
     * Callback for item clicks
     */
    @Nullable
    private PeriodicTableListener mPeriodicTableListener;

    /**
     * Color legend
     */
    @NonNull
    private final PeriodicTableLegend mLegend;

    /**
     * Title string
     */
    @NonNull
    private CharSequence mTitle;

    /**
     * The current block size
     */
    private int mBlockSize;

    /**
     * Amount of space around the table
     */
    private int mPadding;

    /**
     * Number of rows and columns in the table
     */
    private int mNumRows;
    private int mNumCols;

    /**
     * Paint for the table background
     */
    @NonNull
    private final Paint mBgPaint = new Paint();

    /**
     * Paint for block backgrounds
     */
    @NonNull
    private final Paint mBlockPaint = new Paint();

    /**
     * Paint for row and column headers
     */
    @NonNull
    private final Paint mHeaderPaint;

    /**
     * Paint for the table title
     */
    @NonNull
    private final Paint mTitlePaint;

    /**
     * Paint for symbols
     */
    @NonNull
    private final Paint mSymbolPaint;

    /**
     * Paint for atomic numbers
     */
    @NonNull
    private final Paint mNumberPaint;

    /**
     * Paint for the text below the symbol
     */
    @NonNull
    private final Paint mSmallTextPaint;

    /**
     * Paint for the selection indicator
     */
    @NonNull
    private final Paint mSelectedPaint;

    /**
     * Rectangle for many purposes
     */
    @NonNull
    private final Rect mRect = new Rect();

    /**
     * The currently selected block
     */
    @Nullable
    private PeriodicTableBlock mBlockSelected;

    /**
     * The area for drawing the content
     */
    @NonNull
    private final Rect mContentRect = new Rect();

    /**
     * The offset of the content within the content area
     */
    @NonNull
    private final Point mContentOffset = new Point();

    /**
     * The initial area for relative scale operations
     */
    @NonNull
    private final Rect mScaleRect = new Rect();

    /**
     * The focal point of the current scale operation
     */
    @NonNull
    private final PointF mScaleFocalPoint = new PointF();

    /**
     * Touch gesture detectors
     */
    @NonNull
    private final ScaleGestureDetector mScaleGestureDetector;
    @NonNull
    private final GestureDetector mGestureDetector;

    /**
     * Handler for animating programmatic scaling
     */
    @NonNull
    private final Zoomer mZoomer;

    /**
     * The current zoom level
     */
    private float mCurrentZoom = 1f;

    /**
     * Handler for programmatic scrolling and flings
     */
    @NonNull
    private final Scroller mScroller;

    /**
     * Edge effects to provide visual indicators that an edge has been reached
     */
    @NonNull
    private final EdgeEffect mEdgeEffectTop;
    @NonNull
    private final EdgeEffect mEdgeEffectBottom;
    @NonNull
    private final EdgeEffect mEdgeEffectLeft;
    @NonNull
    private final EdgeEffect mEdgeEffectRight;

    /**
     * The accessibility delegate for this View
     */
    @Nullable
    private AccessibilityDelegate mAccessibilityDelegate;

    public PeriodicTableView(Context context) {
        this(context, null, 0);
    }

    public PeriodicTableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeriodicTableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mSelectedPaint = new Paint();
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        mSelectedPaint.setStrokeJoin(Paint.Join.ROUND);
        mSelectedPaint.setColor(COLOR_SELECTED);

        mNumberPaint = new Paint();
        mNumberPaint.setAntiAlias(true);
        mNumberPaint.setColor(COLOR_BLOCK_FOREGROUND);

        mSymbolPaint = new Paint(mNumberPaint);
        mSymbolPaint.setTextAlign(Paint.Align.CENTER);

        mTitlePaint = new Paint(mSymbolPaint);
        mHeaderPaint = new Paint(mSymbolPaint);
        mSmallTextPaint = new Paint(mSymbolPaint);

        mNumberPaint.setSubpixelText(true);
        mSmallTextPaint.setSubpixelText(true);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PeriodicTableView,
                defStyle, 0);

        mTitle = a.getText(R.styleable.PeriodicTableView_title);
        setFgColor(a.getColor(R.styleable.PeriodicTableView_fgColor, COLOR_DEFAULT_FOREGROUND));
        setBgColor(a.getColor(R.styleable.PeriodicTableView_bgColor, COLOR_DEFAULT_BACKGROUND));

        a.recycle();

        mLegend = new PeriodicTableLegend(context);

        mScaleGestureDetector = new ScaleGestureDetector(context, getOnScaleGestureListener());
        mGestureDetector = new GestureDetector(context, getOnGestureListener());

        mZoomer = new Zoomer(context);
        mScroller = new Scroller(context);

        mEdgeEffectLeft = new EdgeEffect(context);
        mEdgeEffectTop = new EdgeEffect(context);
        mEdgeEffectRight = new EdgeEffect(context);
        mEdgeEffectBottom = new EdgeEffect(context);

        mAccessibilityDelegate = new AccessibilityDelegate(this);
        ViewCompat.setAccessibilityDelegate(this, mAccessibilityDelegate);
    }

    /**
     * Create the listener for the ScaleGestureDetector.
     *
     * @return The OnScaleGestureListener
     */
    @NonNull
    private ScaleGestureDetector.OnScaleGestureListener getOnScaleGestureListener() {
        return new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            /**
             * The initial span of the scale gesture
             */
            private float mStartSpan;

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                clearSelection();
                mScaleRect.set(mContentRect);
                mStartSpan = detector.getCurrentSpan();

                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFocalPoint.set(detector.getFocusX() / getWidth(),
                        detector.getFocusY() / getHeight());
                setZoom(mCurrentZoom + mCurrentZoom
                        * (detector.getCurrentSpan() - detector.getPreviousSpan()) / mStartSpan);

                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                if(mPeriodicTableListener != null) {
                    mPeriodicTableListener.onZoomEnd(PeriodicTableView.this);
                }
            }
        };
    }

    /**
     * Create the listener for the GestureDetector.
     *
     * @return The OnGestureListener
     */
    @NonNull
    private GestureDetector.OnGestureListener getOnGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                clearEdgeEffects();
                mScroller.forceFinished(true);

                mBlockSelected = null;
                for(PeriodicTableBlock block : mPeriodicTableBlocks) {
                    findBlockPosition(block);
                    if(mRect.contains((int)e.getX(), (int)e.getY())) {
                        mBlockSelected = block;
                        break;
                    }
                }

                ViewCompat.postInvalidateOnAnimation(PeriodicTableView.this);
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if(mPeriodicTableListener != null && mBlockSelected != null) {
                    mPeriodicTableListener.onItemClick(mBlockSelected);
                }
                clearSelection();
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                                    float distanceY) {
                clearSelection();
                int offsetX = (int)-distanceX;
                int offsetY = (int)-distanceY;
                if(offsetX > 0) {
                    offsetX = Math.min(offsetX, -mContentRect.left);
                } else if(offsetX < 0) {
                    offsetX = Math.max(offsetX, -Math.max(0, mContentRect.right - getWidth()));
                }
                if(offsetY > 0) {
                    offsetY = Math.min(offsetY, -mContentRect.top);
                } else if(offsetY < 0) {
                    offsetY = Math.max(offsetY, -Math.max(0, mContentRect.bottom - getHeight()));
                }
                mContentRect.offset(offsetX, offsetY);

                if(mContentRect.height() > getHeight()) {
                    if(distanceY < 0 && mContentRect.top == 0) {
                        EdgeEffectCompat.onPull(mEdgeEffectTop, -distanceY / getHeight(),
                                e2.getX() / getWidth());
                    } else if(distanceY > 0 && mContentRect.bottom == getHeight()) {
                        EdgeEffectCompat.onPull(mEdgeEffectBottom, -distanceY / getHeight(),
                                1f - (e2.getX() / getWidth()));
                    }
                }

                if(mContentRect.width() > getWidth()) {
                    if(distanceX < 0 && mContentRect.left == 0) {
                        EdgeEffectCompat.onPull(mEdgeEffectLeft, -distanceX / getWidth(),
                                1f - (e2.getY() / getHeight()));
                    } else if(distanceX > 0 && mContentRect.right == getWidth()) {
                        EdgeEffectCompat.onPull(mEdgeEffectRight, -distanceX / getWidth(),
                                e2.getY() / getHeight());
                    }
                }

                if(mAccessibilityDelegate != null) {
                    mAccessibilityDelegate.invalidateRoot();
                }

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                clearSelection();
                clearEdgeEffects();
                mScroller.forceFinished(true);
                mScroller.fling(
                        mContentRect.left, mContentRect.top,
                        (int)velocityX, (int)velocityY,
                        mContentRect.left - (mContentRect.right - getWidth()), 0,
                        mContentRect.top - (mContentRect.bottom - getHeight()), 0
                );
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                clearSelection();
            }
        };
    }

    /**
     * Set the foreground color. This is the color of all text outside of the blocks and legend.
     *
     * @param color The color value
     */
    public void setFgColor(int color) {
        mTitlePaint.setColor(color);
        mHeaderPaint.setColor(color);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Get the current foreground color. This is the color of all text outside of the blocks and
     * legend.
     *
     * @return The color value
     */
    public int getFgColor() {
        return mTitlePaint.getColor();
    }

    /**
     * Set the background color.
     *
     * @param color The color value
     */
    public void setBgColor(int color) {
        mBgPaint.setColor(color);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Get the current background color.
     *
     * @return The color value
     */
    public int getBgColor() {
        return mBgPaint.getColor();
    }

    /**
     * Set the list of blocks to be rendered. This method also determines the row and column of
     * each block and sets the colors using the legend.
     *
     * @param blocks The list of blocks
     */
    public void setBlocks(@NonNull List<PeriodicTableBlock> blocks) {
        mPeriodicTableBlocks.clear();
        mPeriodicTableBlocks.addAll(blocks);

        int numRows = 0;
        int numCols = 0;

        for(PeriodicTableBlock block : mPeriodicTableBlocks) {
            if(block.element.period > numRows) {
                numRows = block.element.period;
            }
            if(block.element.group > numCols) {
                numCols = block.element.group;
            }
            if(block.element.group == 0) {
                if(block.element.period == 6) {
                    block.row = 8;
                    block.col = block.element.number - 54;
                } else if(block.element.period == 7) {
                    block.row = 9;
                    block.col = block.element.number - 86;
                }
            } else {
                block.row = block.element.period;
                block.col = block.element.group;
            }

            block.color = ElementUtils.getElementColor(block.element);
        }
        numRows += 2;

        mNumRows = numRows;
        mNumCols = numCols;

        measureCanvas();
        if(mAccessibilityDelegate != null) {
            mAccessibilityDelegate.loadLabels();
            mAccessibilityDelegate.invalidateRoot();
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    public void invalidateLegend() {
        mLegend.invalidate(getContext());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Set the PeriodicTableListener.
     *
     * @param listener The PeriodicTableListener
     */
    public void setPeriodicTableListener(@Nullable PeriodicTableListener listener) {
        mPeriodicTableListener = listener;
    }

    /**
     * Get the PeriodicTableListener.
     *
     * @return The PeriodicTableListener
     */
    @Nullable
    public PeriodicTableListener getPeriodicTableListener() {
        return mPeriodicTableListener;
    }

    /**
     * Set the title from a string resource.
     *
     * @param resId Resource ID
     */
    public void setTitle(int resId) {
        setTitle(getResources().getText(resId));
    }

    /**
     * Set the title.
     *
     * @param title The title
     */
    public void setTitle(@NonNull CharSequence title) {
        mTitle = title;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Get the title.
     *
     * @return The title
     */
    @NonNull
    public CharSequence getTitle() {
        return mTitle;
    }

    /**
     * Clear the selected block.
     */
    public void clearSelection() {
        mBlockSelected = null;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Check whether the table can be zoomed in.
     *
     * @return Whether the table can be zoomed in
     */
    public boolean canZoomIn() {
        return mCurrentZoom < MAX_ZOOM;
    }

    /**
     * Check whether the table can be zoomed out.
     *
     * @return Whether the table can be zoomed out
     */
    public boolean canZoomOut() {
        return mCurrentZoom > 1f;
    }

    /**
     * Zoom to a specified zoom level.
     *
     * @param zoomLevel The target zoom level
     */
    public void zoomTo(float zoomLevel) {
        mZoomer.forceFinished();
        mScaleRect.set(mContentRect);
        mScaleFocalPoint.set(0.5f, 0.5f);
        mZoomer.startZoom(mCurrentZoom, zoomLevel);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Zoom in one step.
     */
    public void zoomIn() {
        zoomTo(mCurrentZoom + mCurrentZoom * ZOOM_STEP);
    }

    /**
     * Zoom out one step.
     */
    public void zoomOut() {
        zoomTo(mCurrentZoom - mCurrentZoom * ZOOM_STEP);
    }

    /**
     * Determine if a block is within the visible region. This is used to avoid useless drawing
     * operations.
     *
     * @param rect The block boundaries
     * @return True if the block is visible
     */
    private boolean isBlockVisible(@NonNull Rect rect) {
        return rect.intersects(0, 0, getWidth(), getHeight());
    }

    /**
     * Calculate the position of the specified block and store it in the shared rectangle.
     *
     * @param block The block
     */
    private void findBlockPosition(@NonNull PeriodicTableBlock block) {
        mRect.right =
                (block.col * mBlockSize + mContentRect.left + mContentOffset.x + mPadding) - 1;
        mRect.bottom =
                (block.row * mBlockSize + mContentRect.top + mContentOffset.y + mPadding) - 1;
        mRect.left = mRect.right - mBlockSize + 1;
        mRect.top = mRect.bottom - mBlockSize + 1;

        final int number = block.element.number;
        if((number > 56 && number < 72) || (number > 88 && number < 104)) {
            mRect.top += mPadding / 2;
            mRect.bottom += mPadding / 2;
        }
    }

    /**
     * Draw the headers and placeholders on the supplied Canvas.
     *
     * @param canvas The Canvas
     */
    private void writeHeaders(@NonNull Canvas canvas) {
        mHeaderPaint.setTextSize(mBlockSize / 4f);

        for(int i = 1; i <= mNumCols; i++) {
            canvas.drawText(String.valueOf(i),
                    mBlockSize * i + mContentRect.left + mContentOffset.x,
                    mPadding / 2f + mContentRect.top + mContentOffset.y,
                    mHeaderPaint);
        }
        for(int i = 1; i <= mNumRows - 2; i++) {
            canvas.drawText(String.valueOf(i),
                    mPadding / 2f + mContentRect.left + mContentOffset.x,
                    mBlockSize * i + mContentRect.top + mContentOffset.y,
                    mHeaderPaint);
        }

        canvas.drawText("57-71",
                mBlockSize * 3 + mContentRect.left + mContentOffset.x,
                mBlockSize * 6 + mContentRect.top + mContentOffset.y + mHeaderPaint.getTextSize()
                        / 2,
                mHeaderPaint);

        canvas.drawText("89-103",
                mBlockSize * 3 + mContentRect.left + mContentOffset.x,
                mBlockSize * 7 + mContentRect.top + mContentOffset.y + mHeaderPaint.getTextSize()
                        / 2,
                mHeaderPaint);
    }

    /**
     * Draw the title on the supplied Canvas.
     *
     * @param canvas The Canvas
     */
    private void writeTitle(@NonNull Canvas canvas) {
        canvas.drawText(mTitle, 0, mTitle.length(),
                mBlockSize * mNumCols / 2f + mContentRect.left + mContentOffset.x,
                mBlockSize + mContentRect.top + mContentOffset.y,
                mTitlePaint);
    }

    /**
     * Draw the edge effects to the supplied Canvas.
     *
     * @param canvas The Canvas
     */
    private void drawEdgeEffects(@NonNull Canvas canvas) {
        boolean invalidate = false;

        if(!mEdgeEffectTop.isFinished()) {
            mEdgeEffectTop.draw(canvas);
            invalidate = true;
        }
        if(!mEdgeEffectBottom.isFinished()) {
            canvas.save();
            canvas.rotate(180, getWidth() / 2f, getHeight() / 2f);
            mEdgeEffectBottom.draw(canvas);
            canvas.restore();
            invalidate = true;
        }
        if(!mEdgeEffectLeft.isFinished()) {
            canvas.save();
            canvas.translate(0, getHeight());
            canvas.rotate(-90);
            mEdgeEffectLeft.draw(canvas);
            canvas.restore();
            invalidate = true;
        }
        if(!mEdgeEffectRight.isFinished()) {
            canvas.save();
            canvas.translate(getWidth(), 0);
            canvas.rotate(90, 0, 0);
            mEdgeEffectRight.draw(canvas);
            canvas.restore();
            invalidate = true;
        }

        if(invalidate) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * Deactivate and release the edge effects.
     */
    private void clearEdgeEffects() {
        mEdgeEffectTop.onRelease();
        mEdgeEffectBottom.onRelease();
        mEdgeEffectLeft.onRelease();
        mEdgeEffectRight.onRelease();
    }

    /**
     * Ensure that the content area fills the viewport.
     */
    private void fillViewport() {
        if(mContentRect.left > 0) {
            mContentRect.right -= mContentRect.left;
            mContentRect.left = 0;
        } else if(mContentRect.right < getWidth()) {
            mContentRect.left += getWidth() - mContentRect.right;
            mContentRect.right = getWidth();
        }
        if(mContentRect.top > 0) {
            mContentRect.bottom -= mContentRect.top;
            mContentRect.top = 0;
        } else if(mContentRect.bottom < getHeight()) {
            mContentRect.top += getHeight() - mContentRect.bottom;
            mContentRect.bottom = getHeight();
        }
    }

    /**
     * Trim the content area to the specified size.
     *
     * @param width  The actual width of the content
     * @param height The actual height of the content
     */
    private void trimCanvas(int width, int height) {
        if(mContentRect.width() > width || mContentRect.height() > height) {
            final int deltaWidth = Math.max(0,
                    Math.min(mContentRect.width() - getWidth(), mContentRect.width() - width));
            final int deltaHeight = Math.max(0,
                    Math.min(mContentRect.height() - getHeight(), mContentRect.height() - height));
            final float focusX = (getWidth() / 2f - mContentRect.width()) / mContentRect.width();
            final float focusY = (getHeight() / 2f - mContentRect.top) / mContentRect.height();
            mContentRect.top += deltaHeight * focusY;
            mContentRect.bottom -= deltaHeight * (1f - focusY);
            mContentRect.left += deltaWidth * focusX;
            mContentRect.right -= deltaWidth * (1f - focusX);
        }
    }

    /**
     * Measure the content area and determine the block size, padding, and text size.
     */
    private void measureCanvas() {
        final int blockWidth = (int)(mContentRect.width() / (mNumCols + 0.5));
        final int blockHeight = mContentRect.height() / (mNumRows + 1);
        mBlockSize = Math.min(blockWidth, blockHeight);
        mPadding = mBlockSize / 2;

        final int realWidth = mBlockSize * mNumCols + mBlockSize;
        final int realHeight = mBlockSize * mNumRows + mBlockSize;
        trimCanvas(realWidth, realHeight);
        mContentOffset.set(Math.max(0, (mContentRect.width() - realWidth) / 2),
                Math.max(0, (mContentRect.height() - realHeight) / 2));
        fillViewport();

        mTitlePaint.setTextSize(mBlockSize / 2f);
        mSymbolPaint.setTextSize(mBlockSize / 2f);
        mNumberPaint.setTextSize(mBlockSize / 4f);
        mSmallTextPaint.setTextSize(mBlockSize / 5f);
    }

    /**
     * Set the current zoom level.
     *
     * @param zoomLevel The target zoom level
     */
    private void setZoom(float zoomLevel) {
        zoomLevel = Math.max(1f, Math.min(MAX_ZOOM, zoomLevel));
        if(zoomLevel != mCurrentZoom) {
            final int deltaWidth = (int)(zoomLevel * getWidth()) - mScaleRect.width();
            final int deltaHeight = (int)(zoomLevel * getHeight()) - mScaleRect.height();
            final float focusX =
                    (mScaleFocalPoint.x * getWidth() - mScaleRect.left) / mScaleRect.width();
            final float focusY =
                    (mScaleFocalPoint.y * getHeight() - mScaleRect.top) / mScaleRect.height();
            mContentRect.set(
                    mScaleRect.left - (int)(deltaWidth * focusX),
                    mScaleRect.top - (int)(deltaHeight * focusY),
                    mScaleRect.right + (int)(deltaWidth * (1f - focusX)),
                    mScaleRect.bottom + (int)(deltaHeight * (1f - focusY))
            );

            mCurrentZoom = zoomLevel;
            measureCanvas();
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = mScaleGestureDetector.onTouchEvent(event);
        ret = mGestureDetector.onTouchEvent(event) || ret;
        return ret || super.onTouchEvent(event);
    }

    @Override
    protected boolean dispatchHoverEvent(MotionEvent event) {
        return (mAccessibilityDelegate != null && mAccessibilityDelegate.dispatchHoverEvent(event))
                || super.dispatchHoverEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return (mAccessibilityDelegate != null && mAccessibilityDelegate.dispatchKeyEvent(event))
                || super.dispatchKeyEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if(mAccessibilityDelegate != null) {
            mAccessibilityDelegate.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mContentRect.width() < w) {
            mContentRect.left = 0;
            mContentRect.right = w;
        }
        if(mContentRect.height() < h) {
            mContentRect.top = 0;
            mContentRect.bottom = h;
        }

        mEdgeEffectTop.setSize(w, h);
        mEdgeEffectBottom.setSize(w, h);
        mEdgeEffectLeft.setSize(h, w);
        mEdgeEffectRight.setSize(h, w);

        measureCanvas();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()) {
            mContentRect.offsetTo(mScroller.getCurrX(), mScroller.getCurrY());
            ViewCompat.postInvalidateOnAnimation(this);
        }

        if(mZoomer.computeZoom()) {
            setZoom(mZoomer.getCurrZoom());
            if(mPeriodicTableListener != null && mZoomer.isFinished()) {
                mPeriodicTableListener.onZoomEnd(this);
            }
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawRect(0, 0, getRight(), getBottom(), mBgPaint);
        mRect.top = (int)(mBlockSize * 1.3) + mContentRect.top + mContentOffset.y;
        mRect.left = mBlockSize * 3 + mContentRect.left + mContentOffset.x;
        mRect.bottom = mRect.top + mBlockSize * 2;
        mRect.right = mRect.left + mBlockSize * 9;
        mLegend.drawLegend(canvas, mRect);

        writeHeaders(canvas);
        writeTitle(canvas);

        for(PeriodicTableBlock block : mPeriodicTableBlocks) {
            findBlockPosition(block);

            if(!isBlockVisible(mRect)) {
                continue;
            }

            mBlockPaint.setColor(block.color);

            canvas.drawRect(mRect, mBlockPaint);

            canvas.drawText(block.element.symbol, mRect.left + mBlockSize / 2f,
                    mRect.bottom - (int)(mBlockSize / 2.8), mSymbolPaint);

            canvas.drawText(String.valueOf(block.element.number), mRect.left + mBlockSize / 20f,
                    mRect.top + mNumberPaint.getTextSize(), mNumberPaint);

            canvas.drawText(block.subtext, mRect.left + mBlockSize / 2f,
                    mRect.bottom - mBlockSize / 20f, mSmallTextPaint);
        }

        if(mBlockSelected != null) {
            mSelectedPaint.setStrokeWidth(mBlockSize / 10f);
            findBlockPosition(mBlockSelected);
            canvas.drawRect(mRect, mSelectedPaint);
        }

        drawEdgeEffects(canvas);
    }

    /**
     * The ExploreByTouchHelper implementation to provide accessibility.
     */
    private class AccessibilityDelegate extends ExploreByTouchHelper {
        /**
         * The description string for unknown values
         */
        @NonNull
        private final String mUnknownString;

        /**
         * The label for subtext descriptions
         */
        private String mSubtextLabel;

        /**
         * The label for category descriptions
         */
        private String mCatLabel;

        /**
         * The list of category names
         */
        private String[] mCatNames;

        AccessibilityDelegate(View host) {
            super(host);
            mUnknownString = getResources().getString(R.string.unknown);
            loadLabels();
        }

        /**
         * Load the labels used for descriptions.
         */
        void loadLabels() {
            final Resources res = getResources();
            switch(PreferenceUtils.getPrefSubtextValue()) {
                case PreferenceUtils.SUBTEXT_DENSITY:
                    mSubtextLabel = res.getString(R.string.labelDensity);
                    break;
                case PreferenceUtils.SUBTEXT_MELT:
                    mSubtextLabel = res.getString(R.string.labelMelt);
                    break;
                case PreferenceUtils.SUBTEXT_BOIL:
                    mSubtextLabel = res.getString(R.string.labelBoil);
                    break;
                case PreferenceUtils.SUBTEXT_HEAT:
                    mSubtextLabel = res.getString(R.string.labelHeat);
                    break;
                case PreferenceUtils.SUBTEXT_NEGATIVITY:
                    mSubtextLabel = res.getString(R.string.labelNegativity);
                    break;
                case PreferenceUtils.SUBTEXT_ABUNDANCE:
                    mSubtextLabel = res.getString(R.string.labelAbundance);
                    break;
                default:
                    mSubtextLabel = res.getString(R.string.labelWeight);
            }
            if(PreferenceUtils.COLOR_BLOCK.equals(PreferenceUtils.getPrefElementColors())) {
                mCatLabel = res.getStringArray(R.array.elementColorNames)[1];
            } else {
                if(mCatNames == null) {
                    mCatNames = res.getStringArray(R.array.ptCategories);
                }
                mCatLabel = res.getStringArray(R.array.elementColorNames)[0];
            }
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            for(PeriodicTableBlock block : mPeriodicTableBlocks) {
                findBlockPosition(block);
                if(mRect.contains((int)x, (int)y)) {
                    return block.element.number - 1;
                }
            }
            return INVALID_ID;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            for(PeriodicTableBlock block : mPeriodicTableBlocks) {
                virtualViewIds.add(block.element.number - 1);
            }
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId,
                                                    @NonNull AccessibilityNodeInfoCompat node) {
            final PeriodicTableBlock block = mPeriodicTableBlocks.get(virtualViewId);
            findBlockPosition(block);
            node.setBoundsInParent(new Rect(mRect));
            node.setText(getDescription(block));
            node.setClickable(true);
        }

        /**
         * Get the description for a block.
         *
         * @param block The PeriodicTableBlock
         * @return The description string
         */
        private String getDescription(@NonNull PeriodicTableBlock block) {
            final Element element = block.element;
            final Resources res = getResources();
            final String symbol = element.symbol.toUpperCase();
            final String name = res.getString(ElementUtils.getElementName(element.number));

            final String subtext;
            switch(PreferenceUtils.getPrefSubtextValue()) {
                case PreferenceUtils.SUBTEXT_WEIGHT:
                    subtext = element.unstable ? String.valueOf((int)element.weight)
                            : block.subtext;
                    break;
                case PreferenceUtils.SUBTEXT_DENSITY:
                    subtext = element.density == null ? mUnknownString : block.subtext;
                    break;
                case PreferenceUtils.SUBTEXT_MELT:
                    subtext = element.melt == null ? mUnknownString : block.subtext;
                    break;
                case PreferenceUtils.SUBTEXT_BOIL:
                    subtext = element.boil == null ? mUnknownString : block.subtext;
                    break;
                case PreferenceUtils.SUBTEXT_HEAT:
                    subtext = element.heat == null ? mUnknownString : block.subtext;
                    break;
                case PreferenceUtils.SUBTEXT_NEGATIVITY:
                    subtext = element.negativity == null ? mUnknownString : block.subtext;
                    break;
                case PreferenceUtils.SUBTEXT_ABUNDANCE:
                    subtext = element.abundance == null ? mUnknownString : block.subtext;
                    break;
                default:
                    subtext = mUnknownString;
            }
            final String cat;
            if(PreferenceUtils.COLOR_BLOCK.equals(PreferenceUtils.getPrefElementColors())) {
                cat = String.valueOf(element.block);
            } else {
                cat = mCatNames[element.category];
            }

            return res.getString(R.string.descTableBlock, element.number, symbol, name,
                    mSubtextLabel, subtext, mCatLabel, cat);
        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action,
                                                        Bundle arguments) {
            if (action == AccessibilityNodeInfoCompat.ACTION_CLICK) {
                if (mPeriodicTableListener != null) {
                    mPeriodicTableListener.onItemClick(mPeriodicTableBlocks.get(virtualViewId));
                }

                return true;
            }

            return false;
        }
    }
}
