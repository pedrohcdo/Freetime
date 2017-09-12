package com.createlier.freetime.entity.custom.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.createlier.freetime.R;

/**
 * Created by Pedro on 05/07/2016.
 */
public class MaskView extends View {

    // Private Variables
    private int mDelegateTouchTo = -1;
    private View mDelegateToView = null;

    /**
     * Constructor
     * @param context
     */
    public MaskView(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor
     * @param context
     */
    public MaskView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context, attrs, 0);
    }

    /**
     * Constructor
     * @param context
     */
    @SuppressLint("NewApi")
    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    /**
     * Initialize
     *
     * @param attrs
     */
    public void init(final Context context, final AttributeSet attrs, int defStyleAttr) {
        // Get Styled Attributes
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MaskView, defStyleAttr, 0);
        int delegateTouchTo = typedArray.getResourceId(R.styleable.MaskView_delegate_touch_to, -1);
        typedArray.recycle();
        // Set Delegate touch to
        setDelegateTouchTo(delegateTouchTo);
        //
        setOnClickListener(new OnClickListener() {

            /**
             * On Clicked
             *
             * @param v
             */
            @Override
            public void onClick(View v) {
                if(mDelegateToView != null) {
                    mDelegateToView.performClick();
                }
            }
        });
    }

    /**
     * Set Delegate Touch To
     * @param to -1 to unset
     */
    public void setDelegateTouchTo(int to) {
        if(to < -1)
            to = -1;
        mDelegateTouchTo = to;
        updateSets();
    }

    /**
     * Get Delegate Touch To
     * @return
     */
    public int getDelegateTouchTo() {
        return mDelegateTouchTo;
    }

    /**
     * On Attached To Window
     */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateSets();
    }

    /**
     * Update Sets
     */
    private void updateSets() {
        mDelegateToView = null;
        if(mDelegateTouchTo == -1) return;
        final ViewParent parent = getParent();
        if(null != parent && parent instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) parent;
            mDelegateToView = group.findViewById(mDelegateTouchTo);
        }
    }

    /**
     * Remove References
     */
    @Override
    protected void onDetachedFromWindow() {
        mDelegateToView = null;
        super.onDetachedFromWindow();
    }
}
