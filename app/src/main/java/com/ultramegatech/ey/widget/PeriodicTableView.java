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
package com.ultramegatech.ey.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.OverScroller;

import com.ultramegatech.ey.R;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Zoomable, color coded View of the Periodic Table of the Elements. Renders a list of
 * PeriodicTableBlock objects in the standard Periodic Table layout. Also implements a custom
 * OnItemClickListener that passes the selected PeriodicTableBlock object.
 *
 * @author Steve Guidetti
 */
public class PeriodicTableView extends View implements Observer {
    /**
     * The amount to zoom in on double taps
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
     * Callback interface for click listeners.
     */
    public interface OnItemClickListener {
        /**
         * Called when a block is clicked.
         *
         * @param item The selected block
         */
        void onItemClick(PeriodicTableBlock item);
    }

    /**
     * The list of blocks to render
     */
    private List<PeriodicTableBlock> mPeriodicTableBlocks;

    /**
     * Callback for item clicks
     */
    private OnItemClickListener mItemClickListener;

    /**
     * Color legend
     */
    private final PeriodicTableLegend mLegend = new PeriodicTableLegend();

    /**
     * Title string
     */
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
    private final Paint mBgPaint = new Paint();

    /**
     * Paint for block backgrounds
     */
    private final Paint mBlockPaint = new Paint();

    /**
     * Paint for row and column headers
     */
    private Paint mHeaderPaint;

    /**
     * Paint for the table title
     */
    private Paint mTitlePaint;

    /**
     * Paint for symbols
     */
    private Paint mSymbolPaint;

    /**
     * Paint for atomic numbers
     */
    private Paint mNumberPaint;

    /**
     * Paint for the text below the symbol
     */
    private Paint mSmallTextPaint;

    /**
     * Paint for the selection indicator
     */
    private Paint mSelectedPaint;

    /**
     * Rectangle for many purposes
     */
    private final Rect mRect = new Rect();

    /**
     * The currently selected block
     */
    private PeriodicTableBlock mBlockSelected;

    /**
     * The area for drawing the content
     */
    private Rect mContentRect = new Rect();

    /**
     * The initial area for relative scale operations
     */
    private Rect mScaleRect = new Rect();

    /**
     * The focal point of the current scale operation
     */
    private PointF mScaleFocalPoint = new PointF();

    /**
     * Touch gesture detectors
     */
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;

    /**
     * Handler for animating programmatic scaling
     */
    private Zoomer mZoomer;

    /**
     * The current zoom level
     */
    private float mCurrentZoom = 1f;

    /**
     * Handler for programmatic scrolling and flings
     */
    private OverScroller mScroller;

    /**
     * Edge effects to provide visual indicators that an edge has been reached
     */
    private EdgeEffectCompat mEdgeEffectTop;
    private EdgeEffectCompat mEdgeEffectBottom;
    private EdgeEffectCompat mEdgeEffectLeft;
    private EdgeEffectCompat mEdgeEffectRight;

    /**
     * The active status of the edge effects
     */
    private boolean mEdgeEffectTopActive;
    private boolean mEdgeEffectBottomActive;
    private boolean mEdgeEffectLeftActive;
    private boolean mEdgeEffectRightActive;

    public PeriodicTableView(Context context) {
        this(context, null, 0);
    }

    public PeriodicTableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PeriodicTableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setupPaints();

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PeriodicTableView,
                defStyle, 0);

        mTitle = a.getText(R.styleable.PeriodicTableView_title);
        setFgColor(a.getColor(R.styleable.PeriodicTableView_fgColor, COLOR_DEFAULT_FOREGROUND));
        setBgColor(a.getColor(R.styleable.PeriodicTableView_bgColor, COLOR_DEFAULT_BACKGROUND));

        a.recycle();

        mLegend.addObserver(this);

        mScaleGestureDetector = new ScaleGestureDetector(context, getOnScaleGestureListener());
        mGestureDetector = new GestureDetector(context, getOnGestureListener());

        mZoomer = new Zoomer(context);
        mScroller = new OverScroller(context);

        mEdgeEffectLeft = new EdgeEffectCompat(context);
        mEdgeEffectTop = new EdgeEffectCompat(context);
        mEdgeEffectRight = new EdgeEffectCompat(context);
        mEdgeEffectBottom = new EdgeEffectCompat(context);
    }

    /**
     * Initialize and configure all Paint objects.
     */
    private void setupPaints() {
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
    }

    /**
     * Create the listener for the ScaleGestureDetector.
     *
     * @return The OnScaleGestureListener
     */
    private ScaleGestureDetector.OnScaleGestureListener getOnScaleGestureListener() {
        return new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            /**
             * The initial span of the scale gesture
             */
            private float mStartSpan;

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                clearSelection();
                mStartSpan = detector.getCurrentSpan();

                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                mScaleFocalPoint.set(detector.getFocusX(), detector.getFocusY());
                setZoom(mCurrentZoom + mCurrentZoom
                        * (detector.getCurrentSpan() - detector.getPreviousSpan()) / mStartSpan);

                return true;
            }
        };
    }

    /**
     * Create the listener for the GestureDetector.
     *
     * @return The OnGestureListener
     */
    private GestureDetector.OnGestureListener getOnGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                clearEdgeEffects();
                mScaleRect.set(mContentRect);
                mScroller.forceFinished(true);
                ViewCompat.postInvalidateOnAnimation(PeriodicTableView.this);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
                mBlockSelected = null;
                for(PeriodicTableBlock block : mPeriodicTableBlocks) {
                    findBlockPosition(block);
                    if(mRect.contains((int)e.getX(), (int)e.getY())) {
                        mBlockSelected = block;
                        ViewCompat.postInvalidateOnAnimation(PeriodicTableView.this);
                        break;
                    }
                }
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if(mItemClickListener != null && mBlockSelected != null) {
                    mItemClickListener.onItemClick(mBlockSelected);
                }
                clearSelection();
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                clearSelection();
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                mZoomer.forceFinished();
                mScaleFocalPoint.set(e.getX(), e.getY());
                mZoomer.startZoom(mCurrentZoom, mCurrentZoom * ZOOM_STEP);
                ViewCompat.postInvalidateOnAnimation(PeriodicTableView.this);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
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
                        mEdgeEffectTop.onPull(-distanceY / getHeight(), e2.getX() / getWidth());
                        mEdgeEffectTopActive = true;
                    } else if(distanceY > 0 && mContentRect.bottom == getHeight()) {
                        mEdgeEffectBottom.onPull(-distanceY / getHeight(),
                                1f - (e2.getX() / getWidth()));
                        mEdgeEffectBottomActive = true;
                    }
                }

                if(mContentRect.width() > getWidth()) {
                    if(distanceX < 0 && mContentRect.left == 0) {
                        mEdgeEffectLeft.onPull(-distanceX / getWidth(),
                                1f - (e2.getY() / getHeight()));
                        mEdgeEffectLeftActive = true;
                    } else if(distanceX > 0 && mContentRect.right == getWidth()) {
                        mEdgeEffectRight.onPull(-distanceX / getWidth(), e2.getY() / getHeight());
                        mEdgeEffectRightActive = true;
                    }
                }

                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                clearSelection();
                clearEdgeEffects();
                mScroller.forceFinished(true);
                mScroller.fling(
                        mContentRect.left, mContentRect.top,
                        (int)-velocityX, (int)-velocityY,
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
    public void setBlocks(List<PeriodicTableBlock> blocks) {
        if(blocks.isEmpty()) {
            return;
        }

        int numRows = 0;
        int numCols = 0;

        for(PeriodicTableBlock block : blocks) {
            if(block.period > numRows) {
                numRows = block.period;
            }
            if(block.group > numCols) {
                numCols = block.group;
            }
            if(block.group == 0) {
                if(block.period == 6) {
                    block.row = 8;
                    block.col = block.number - 54;
                } else if(block.period == 7) {
                    block.row = 9;
                    block.col = block.number - 86;
                }
            } else {
                block.row = block.period;
                block.col = block.group;
            }

            mLegend.colorBlock(block);
        }
        numRows += 2;

        mNumRows = numRows;
        mNumCols = numCols;
        mPeriodicTableBlocks = blocks;

        measureCanvas();
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Get the color legend.
     *
     * @return The PeriodicTableLegend
     */
    public PeriodicTableLegend getLegend() {
        return mLegend;
    }

    /**
     * Set the item click listener.
     *
     * @param listener The listener
     */
    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    /**
     * Get the item click listener.
     *
     * @return The listener
     */
    public OnItemClickListener getOnItemClickListener() {
        return mItemClickListener;
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
    public void setTitle(CharSequence title) {
        mTitle = title;
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * Get the title.
     *
     * @return The title
     */
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
     * Determine if a block is within the visible region. This is used to avoid useless drawing
     * operations.
     *
     * @param rect The block boundaries
     * @return True if the block is visible
     */
    private boolean isBlockVisible(Rect rect) {
        return rect.intersects(0, 0, getWidth(), getHeight());
    }

    /**
     * Calculate the position of the specified block and store it in the shared rectangle.
     *
     * @param block The block
     */
    private void findBlockPosition(PeriodicTableBlock block) {
        mRect.right = (block.col * mBlockSize + mContentRect.left + mPadding) - 1;
        mRect.bottom = (block.row * mBlockSize + mContentRect.top + mPadding) - 1;
        mRect.left = mRect.right - mBlockSize + 1;
        mRect.top = mRect.bottom - mBlockSize + 1;

        final int number = block.number;
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
    private void writeHeaders(Canvas canvas) {
        mHeaderPaint.setTextSize(mBlockSize / 4);

        for(int i = 1; i <= mNumCols; i++) {
            canvas.drawText(String.valueOf(i), mBlockSize * i + mContentRect.left,
                    mPadding / 2 + mContentRect.top, mHeaderPaint);
        }
        for(int i = 1; i <= mNumRows - 2; i++) {
            canvas.drawText(String.valueOf(i), mPadding / 2 + mContentRect.left,
                    mBlockSize * i + mContentRect.top, mHeaderPaint);
        }

        canvas.drawText("57-71", mBlockSize * 3 + mContentRect.left,
                mBlockSize * 6 + mContentRect.top + mHeaderPaint.getTextSize() / 2, mHeaderPaint);

        canvas.drawText("89-103", mBlockSize * 3 + mContentRect.left,
                mBlockSize * 7 + mContentRect.top + mHeaderPaint.getTextSize() / 2, mHeaderPaint);
    }

    /**
     * Draw the title on the supplied Canvas.
     *
     * @param canvas The Canvas
     */
    private void writeTitle(Canvas canvas) {
        if(mTitle != null) {
            canvas.drawText(mTitle, 0, mTitle.length(),
                    mBlockSize * mNumCols / 2 + mContentRect.left,
                    mBlockSize + mContentRect.top, mTitlePaint);
        }
    }

    /**
     * Draw the edge effects to the supplied Canvas.
     *
     * @param canvas The Canvas
     */
    private void drawEdgeEffects(Canvas canvas) {
        boolean invalidate = false;

        if(!mEdgeEffectTop.isFinished()) {
            mEdgeEffectTop.draw(canvas);
            invalidate = true;
        }
        if(!mEdgeEffectBottom.isFinished()) {
            canvas.save();
            canvas.rotate(180, getWidth() / 2, getHeight() / 2);
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
        mEdgeEffectTopActive = mEdgeEffectBottomActive = mEdgeEffectLeftActive =
                mEdgeEffectRightActive = false;
        mEdgeEffectTop.onRelease();
        mEdgeEffectBottom.onRelease();
        mEdgeEffectLeft.onRelease();
        mEdgeEffectRight.onRelease();
    }

    /**
     * Ensure that the content area fills the viewport.
     */
    private void fillViewport() {
        if(mContentRect.width() < getWidth()) {
            mContentRect.left = 0;
            mContentRect.right = getWidth();
        } else if(mContentRect.left > 0) {
            mContentRect.right -= mContentRect.left;
            mContentRect.left = 0;
        } else if(mContentRect.right < getWidth()) {
            mContentRect.left += getWidth() - mContentRect.right;
            mContentRect.right = getWidth();
        }
        if(mContentRect.height() < getHeight()) {
            mContentRect.top = 0;
            mContentRect.bottom = getHeight();
        } else if(mContentRect.top > 0) {
            mContentRect.bottom -= mContentRect.top;
            mContentRect.top = 0;
        } else if(mContentRect.bottom < getHeight()) {
            mContentRect.top += getHeight() - mContentRect.bottom;
            mContentRect.bottom = getHeight();
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

        mTitlePaint.setTextSize(mBlockSize / 2);
        mSymbolPaint.setTextSize(mBlockSize / 2);
        mNumberPaint.setTextSize(mBlockSize / 4);
        mSmallTextPaint.setTextSize(mBlockSize / 5);
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
            final float focalX = mScaleFocalPoint.x / getWidth();
            final float focalY = mScaleFocalPoint.y / getHeight();
            mContentRect.set(
                    mScaleRect.left - (int)(deltaWidth * focalX),
                    mScaleRect.top - (int)(deltaHeight * focalY),
                    mScaleRect.right + (int)(deltaWidth * (1 - focalX)),
                    mScaleRect.bottom + (int)(deltaHeight * (1 - focalY))
            );

            fillViewport();
            measureCanvas();
            ViewCompat.postInvalidateOnAnimation(this);
            mCurrentZoom = zoomLevel;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = mScaleGestureDetector.onTouchEvent(event);
        ret = mGestureDetector.onTouchEvent(event) || ret;
        return ret || super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mContentRect.isEmpty()) {
            mContentRect.set(0, 0, w, h);
        } else {
            fillViewport();
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
            final int vx = mScroller.getCurrX();
            final int vy = mScroller.getCurrY();
            mContentRect.offsetTo(vx, vy);

            if(mContentRect.height() > getHeight()) {
                if(mContentRect.top >= 0 && mEdgeEffectTop.isFinished() && !mEdgeEffectTopActive) {
                    mEdgeEffectTop.onAbsorb((int)OverScrollerCompat.getCurrVelocity(mScroller));
                    mEdgeEffectTopActive = true;
                } else if(mContentRect.bottom <= getHeight() && mEdgeEffectBottom.isFinished()
                        && !mEdgeEffectBottomActive) {
                    mEdgeEffectBottom.onAbsorb((int)OverScrollerCompat.getCurrVelocity(mScroller));
                    mEdgeEffectBottomActive = true;
                }
            }

            if(mContentRect.width() > getWidth()) {
                if(mContentRect.left >= 0 && mEdgeEffectLeft.isFinished()
                        && !mEdgeEffectLeftActive) {
                    mEdgeEffectLeft.onAbsorb((int)OverScrollerCompat.getCurrVelocity(mScroller));
                    mEdgeEffectLeftActive = true;
                } else if(mContentRect.right <= getWidth() && mEdgeEffectRight.isFinished()
                        && !mEdgeEffectRightActive) {
                    mEdgeEffectRight.onAbsorb((int)OverScrollerCompat.getCurrVelocity(mScroller));
                    mEdgeEffectRightActive = true;
                }
            }

            ViewCompat.postInvalidateOnAnimation(this);
        }

        if(mZoomer.computeZoom()) {
            setZoom(mZoomer.getCurrZoom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0, 0, getRight(), getBottom(), mBgPaint);
        if(mPeriodicTableBlocks != null) {
            mRect.top = (int)(mBlockSize * 1.3) + mContentRect.top;
            mRect.left = mBlockSize * 4 + mContentRect.left;
            mRect.bottom = mRect.top + mBlockSize * 2;
            mRect.right = mRect.left + mBlockSize * 8;
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

                canvas.drawText(block.symbol, mRect.left + mBlockSize / 2,
                        mRect.bottom - (int)(mBlockSize / 2.8), mSymbolPaint);

                canvas.drawText(String.valueOf(block.number), mRect.left + mBlockSize / 20,
                        mRect.top + mNumberPaint.getTextSize(), mNumberPaint);

                canvas.drawText(block.subtext, mRect.left + mBlockSize / 2,
                        mRect.bottom - mBlockSize / 20, mSmallTextPaint);
            }

            if(mBlockSelected != null) {
                mSelectedPaint.setStrokeWidth(mBlockSize / 10);
                findBlockPosition(mBlockSelected);
                canvas.drawRect(mRect, mSelectedPaint);
            }
        }

        drawEdgeEffects(canvas);
    }

    @Override
    public void update(Observable observable, Object data) {
        if(observable instanceof PeriodicTableLegend) {
            mLegend.colorBlocks(mPeriodicTableBlocks);
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }
}
