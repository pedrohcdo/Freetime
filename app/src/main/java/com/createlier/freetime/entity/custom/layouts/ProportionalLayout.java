package com.createlier.freetime.entity.custom.layouts;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.createlier.freetime.R;
import com.createlier.freetime.exceptions.ProportionalLayoutException;

/**
 * Created by Pedro on 15/03/2016.
 */
public class ProportionalLayout extends ViewGroup {

    // Private Variables
    private int[] mBasedScreen = new int[2];
    private int[] mEditModeScreen = new int[2];
    private int[] mContentSize = new int[2];
    private float[] mDisplayFactor = new float[2];


    private UnifyChildsListener mUnifyChildsListener;

    /**
     * Constructor
     *
     * @param context
     */
    public ProportionalLayout(Context context) {
        this(context, null);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     */
    public ProportionalLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public ProportionalLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Constructor
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public void init(Context context, AttributeSet attrs, int defStyleAttr) {
    	
        // Get Styled Attributes
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ProportionalLayout, defStyleAttr, 0);
        // Get Based Screen Width and Height values
        int basedScreenWidth = typedArray.getInt(R.styleable.ProportionalLayout_based_screen_width, -1);
        int basedScreenHeight = typedArray.getInt(R.styleable.ProportionalLayout_based_screen_height, -1);
        if(basedScreenWidth <= 0 || basedScreenHeight <= 0)
            throw new ProportionalLayoutException("It is necessary to inform on what screen size the layout was based, use the attributes \"basedScreenWidth\" and \"basedScreenHeight\", and values must be greater than 0.");
        int contentWidth = typedArray.getInt(R.styleable.ProportionalLayout_content_width, 0);
        int contentHeight = typedArray.getInt(R.styleable.ProportionalLayout_content_height, 0);
        if(contentWidth < 0 || contentHeight < 0)
            throw new ProportionalLayoutException("The content has to have a size larger than or equal to 0.");
        int editModeWidth = typedArray.getInt(R.styleable.ProportionalLayout_edit_mode_width, 0);
        int editModeHeight = typedArray.getInt(R.styleable.ProportionalLayout_edit_mode_height, 0);
        if(editModeWidth < 0 || editModeHeight < 0)
            throw new ProportionalLayoutException("The edit mode screen size has to have a size larger than or equal to 0.");
        typedArray.recycle();

        //
        mEditModeScreen[0] = editModeWidth;
        mEditModeScreen[1] = editModeHeight;

        // Set
        setBasedScreen(basedScreenWidth, basedScreenHeight);
        setContentSize(contentWidth, contentHeight);

        Log.d("LogTest", "Factor: " + mDisplayFactor[0] + ", " + mDisplayFactor[1]);
        Log.d("LogTest", "Content: " + mContentSize[0] + ", " + mContentSize[1]);
    }

    /**
     * Get Status Bar Height
     * @return
     */
    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        return 0;
    }

    /**
     * Has Permanent Keys
     * @return
     */
    final private boolean hasPermanentKeys() {
        int height=0;
        int realHeight=0;
        WindowManager w = ((Activity)getContext()).getWindowManager();
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        height = metrics.heightPixels;
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
            try {
                realHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception ignored) {
            }
        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT >= 17)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                realHeight = realSize.y;
            } catch (Exception ignored) {
            }
        if(height == realHeight){
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get Navigation Bar Height
     * @return
     */
    final private int getNavigationBarHeight() {
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (!hasPermanentKeys() && resourceId > 0)
            return getResources().getDimensionPixelSize(resourceId);
        return 0;
    }

    /**
     * Resolve Display Factor
     */
    final private void resolveDisplayFactor() {
        //
        if(isInEditMode()) {
            // Set final Screen Size
            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mDisplayFactor[0] = (mEditModeScreen[1] * 1.0f) / mBasedScreen[0];
                mDisplayFactor[1] = (mEditModeScreen[0] * 1.0f) / mBasedScreen[1];
            } else {
                mDisplayFactor[0] = (mEditModeScreen[0] * 1.0f) / mBasedScreen[0];
                mDisplayFactor[1] = (mEditModeScreen[1] * 1.0f) / mBasedScreen[1];
            }
        } else {
            //
            int displayWidth = 0;
            int displayHeight = 0;
            //
            DisplayMetrics metrics = new DisplayMetrics();
            Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
            display.getMetrics(metrics);
            // Default size
            displayWidth = metrics.widthPixels;
            displayHeight = metrics.heightPixels;
            //
            if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
                try {
                    displayWidth = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                    displayHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
                } catch (Exception ignored) {
                }
            } else if (Build.VERSION.SDK_INT >= 17) {
                try {
                    Point size = new Point();
                    Display.class.getMethod("getRealSize", Point.class).invoke(display, size);
                    displayWidth = size.x;
                    displayHeight = size.y;
                } catch (Exception ignored) {
                }
            }

            displayHeight -= (getNavigationBarHeight() + getStatusBarHeight());

            mDisplayFactor[0] = (displayWidth * 1.0f) / mBasedScreen[0];
            mDisplayFactor[1] = (displayHeight * 1.0f) / mBasedScreen[1];
        }
    }

    /**
     * Set Based Screen Size
     *
     * @param basedWidth
     * @param basedHeight
     */
    final public void setBasedScreen(final int basedWidth, final int basedHeight) {
        if(basedWidth <= 0 || basedHeight <= 0)
            throw new ProportionalLayoutException("The values can not be less than or equal to 0.");
        mBasedScreen[0] = basedWidth;
        mBasedScreen[1] = basedHeight;
        resolveDisplayFactor();
        requestLayout();
    }

    /**
     * Set Content Size
     *
     * @param contentWidth
     * @param contentHeight
     */
    final public void setContentSize(final int contentWidth, final int contentHeight) {
        if(contentWidth < 0 || contentHeight < 0)
            throw new ProportionalLayoutException("The values can not be less than 0.");
        mContentSize[0] = contentWidth;
        mContentSize[1] = contentHeight;
        resolveDisplayFactor();
        requestLayout();
    }

    /**
     * Set Unify Childs Listener
     * @param unifyChildsListener
     */
    final public void setUnifyChildsListener(final UnifyChildsListener unifyChildsListener) {
        mUnifyChildsListener = unifyChildsListener;
        requestLayout();
    }

    /**
     * Measure
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final float newWidth = mContentSize[0] * mDisplayFactor[0];
        final float newHeight = mContentSize[1] * mDisplayFactor[1];
        for(int index=0; index<getChildCount(); index++) {
            final View child = getChildAt(index);
            final LayoutParams params = (LayoutParams) child.getLayoutParams();
            float childW = 0;
            float childH = 0;
            if(params.sizeMode == 0) {
                childW = params.sizeX * mDisplayFactor[0];
                childH = params.sizeY * mDisplayFactor[1];
            } else if(params.sizeMode == 1) {
                childW = params.sizeX * mDisplayFactor[0];
                childH = params.sizeY * mDisplayFactor[0];
            } else if(params.sizeMode == 2) {
                childW = params.sizeX * mDisplayFactor[1];
                childH = params.sizeY * mDisplayFactor[1];
            } else if(params.sizeMode == 3) {
                if(mDisplayFactor[0] > mDisplayFactor[1]) {
                    childW = params.sizeX * mDisplayFactor[1];
                    childH = params.sizeY * mDisplayFactor[1];
                } else {
                    childW = params.sizeX * mDisplayFactor[0];
                    childH = params.sizeY * mDisplayFactor[0];
                }
            } else if(params.sizeMode == 4) {
                if(mDisplayFactor[0] < mDisplayFactor[1]) {
                    childW = params.sizeX * mDisplayFactor[1];
                    childH = params.sizeY * mDisplayFactor[1];
                } else {
                    childW = params.sizeX * mDisplayFactor[0];
                    childH = params.sizeY * mDisplayFactor[0];
                }
            }
            //
            final int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(childW), MeasureSpec.EXACTLY);
            final int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(Math.round(childH), MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if(mUnifyChildsListener != null)
                mUnifyChildsListener.onUnify(child, mDisplayFactor);
        }
        Log.d("LogTest", "Width: " + newWidth + ", Height: " + newHeight);

        setMeasuredDimension(Math.round(newWidth), Math.round(newHeight));
    }

    /**
     * Generate Layout Params
     *
     * @param attrs
     * @return
     */
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /**
     * Return true if same Layout Params
     * @param p
     * @return
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Generate Layout Params
     * @param p
     * @return
     */
    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    /**
     * Returns a set of layout parameters with a width of
     * {@link ViewGroup.LayoutParams#WRAP_CONTENT},
     * a height of {@link ViewGroup.LayoutParams#WRAP_CONTENT}
     * and with the coordinates (0, 0).
     */
    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0, 0, 0, 0, 0);
    }

    /**
     * On Layout
     *
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for(int index =0; index<getChildCount(); index++) {
            final View child = getChildAt(index);
            final LayoutParams params = (LayoutParams) child.getLayoutParams();


            float childW = 0;
            float childH = 0;
            float childX = 0;
            float childY = 0;

            if(params.sizeMode == 0) {
                childX = params.coordinateX * mDisplayFactor[0];
                childY = params.coordinateY * mDisplayFactor[1];
                childW = params.sizeX * mDisplayFactor[0];
                childH = params.sizeY * mDisplayFactor[1];
            } else if(params.sizeMode == 1) {
                float realY = params.sizeY * mDisplayFactor[1];
                float aspectedY = params.sizeY * mDisplayFactor[0];

                childX = params.coordinateX * mDisplayFactor[0];
                childY = params.coordinateY * mDisplayFactor[1] + (realY - aspectedY) / 2;
                childW = params.sizeX * mDisplayFactor[0];
                childH = params.sizeY * mDisplayFactor[0];
            } else if(params.sizeMode == 2) {
                float realX = params.sizeX * mDisplayFactor[0];
                float aspectedX = params.sizeX * mDisplayFactor[1];

                childX = params.coordinateX * mDisplayFactor[0] + (realX - aspectedX) / 2;
                childY = params.coordinateY * mDisplayFactor[1];

                childW = params.sizeX * mDisplayFactor[1];
                childH = params.sizeY * mDisplayFactor[1];
            } else if(params.sizeMode == 3) {
                if(mDisplayFactor[0] > mDisplayFactor[1]) {
                    float realX = params.sizeX * mDisplayFactor[0];
                    float aspectedX = params.sizeX * mDisplayFactor[1];

                    childX = params.coordinateX * mDisplayFactor[0] + (realX - aspectedX) / 2;
                    childY = params.coordinateY * mDisplayFactor[1];
                    childW = params.sizeX * mDisplayFactor[1];
                    childH = params.sizeY * mDisplayFactor[1];
                } else {
                    float realY = params.sizeY * mDisplayFactor[1];
                    float aspectedY = params.sizeY * mDisplayFactor[0];

                    childX = params.coordinateX * mDisplayFactor[0];
                    childY = params.coordinateY * mDisplayFactor[1] + (realY - aspectedY) / 2;
                    childW = params.sizeX * mDisplayFactor[0];
                    childH = params.sizeY * mDisplayFactor[0];
                }

            } else if(params.sizeMode == 4) {
                if(mDisplayFactor[0] < mDisplayFactor[1]) {
                    float realX = params.sizeX * mDisplayFactor[0];
                    float aspectedX = params.sizeX * mDisplayFactor[1];

                    childX = params.coordinateX * mDisplayFactor[0] + (realX - aspectedX) / 2;
                    childY = params.coordinateY * mDisplayFactor[1];
                    childW = params.sizeX * mDisplayFactor[1];
                    childH = params.sizeY * mDisplayFactor[1];
                } else {
                    float realY = params.sizeY * mDisplayFactor[1];
                    float aspectedY = params.sizeY * mDisplayFactor[0];

                    childX = params.coordinateX * mDisplayFactor[0];
                    childY = params.coordinateY * mDisplayFactor[1] + (realY - aspectedY) / 2;
                    childW = params.sizeX * mDisplayFactor[0];
                    childH = params.sizeY * mDisplayFactor[0];
                }


            }
            child.layout(Math.round(childX), Math.round(childY), Math.round(childX + childW), Math.round(childY + childH));
        }
    }

    /**
     * Proportional Layout Params
     */
    class LayoutParams extends ViewGroup.LayoutParams {

        // Public Variables
        public float coordinateX;
        public float coordinateY;
        public float sizeX;
        public float sizeY;
        public int sizeMode;

        /**
         * Constructor
         *
         * @param width
         * @param height
         * @param cx
         * @param cy
         * @param sx
         * @param sy
         */
        public LayoutParams(int width, int height, int cx, int cy, int sx, int sy, int sm) {
            super(width, height);
            coordinateX = cx;
            coordinateY = cy;
            sizeX = sx;
            sizeY = sy;
            sizeMode = sm;
        }

        /**
         * Creates a new set of layout parameters. The values are extracted from
         * the supplied attributes set and context. The XML attributes mapped
         * to this set of layout parameters are:
         *
         * <ul>
         *   <li>coordinate_x: the X location of the child
         *   <li>coordinate_y: the Y location of the child
         *   <li>size_x: the width of the child
         *   <li>size_y: the height of the child
         *   <li>All the XML attributes from
         *   {@link ViewGroup.LayoutParams}</li>
         * </ul>
         *
         * @param c the application environment
         * @param attrs the set of attributes fom which to extract the layout
         *              parameters values
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ProportionalLayout_Layout);
            coordinateX = a.getFloat(R.styleable.ProportionalLayout_Layout_coordinate_x, 0);
            coordinateY = a.getFloat(R.styleable.ProportionalLayout_Layout_coordinate_y, 0);
            sizeX = a.getFloat(R.styleable.ProportionalLayout_Layout_size_x, 0);
            sizeY = a.getFloat(R.styleable.ProportionalLayout_Layout_size_y, 0);
            sizeMode = a.getInt(R.styleable.ProportionalLayout_Layout_size_mode, 0);
            a.recycle();
        }

        /**
         * {@inheritDoc}
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    /**
     * Unify Childs Listener
     */
    public interface UnifyChildsListener {

        /**
         * Unify Childs Listener
         * @param child
         * @param factor
         */
        public void onUnify(final View child, final float[] factor);
    }
}
