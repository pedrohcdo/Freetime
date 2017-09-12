package com.createlier.freetime.entity.custom.layouts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Action Bar Layout
 * 
 * @author Pedro Henrique
 *
 */
public class ActionBarLayout extends LinearLayout {
	
	// Private Variables
	private int mCursorPosition = 0;
	private float mCursorPositionTransformation = 0;
	
	private Paint mCursorPaint;
	
	/**
	 * Constructor
	 * @param context
	 */
	public ActionBarLayout(Context context) {
		this(context, null, 0);
		init();
	}
	
	/**
	 * Constructor
	 * @param context
	 */
	public ActionBarLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init();
	}

	/**
	 * Constructor
	 * @param context
	 */
	@SuppressLint("NewApi")
	public ActionBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}
	
	/**
	 * Initialize
	 */
	final private void init() {
		mCursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCursorPaint.setColor(0x33333333);
	}
	
	/**
	 * Set Cursor
	 * @param index
	 */
	final public void setCursor(final int index) {
		mCursorPosition = index;
		postInvalidate();
	}
	
	/**
	 * On Draw
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int cursorPosition = getTransformedCursorPosition(mCursorPosition, mCursorPositionTransformation);
        final Point cursorSize = getTransformedCursorSize(mCursorPosition, mCursorPositionTransformation);
        canvas.drawRect(cursorPosition, 0, cursorPosition + cursorSize.x,  cursorSize.y, mCursorPaint);
        updateCursor();
	}
	
	/**
	 * Update Cursor
	 */
	final private void updateCursor() {
		boolean needDraw = false;
		if(mCursorPositionTransformation > mCursorPosition) {
			final float scale = Math.max(1, mCursorPositionTransformation - mCursorPosition);
			mCursorPositionTransformation = Math.max(mCursorPositionTransformation - 0.2f * scale, mCursorPosition);
			needDraw = true;
		} else if(mCursorPositionTransformation < mCursorPosition) {
			final float scale = Math.max(1, mCursorPosition - mCursorPositionTransformation);
			mCursorPositionTransformation = Math.min(mCursorPositionTransformation + 0.2f * scale, mCursorPosition);
			needDraw = true;
		}
		if(needDraw)
			postInvalidate();
	}
	
	/**
	 * Get Transformed Cursor Position
	 * @return
	 */
	final private int getTransformedCursorPosition(final int index, final float indexTranformation) {
		if(Math.abs(index - indexTranformation) <= 0.0)
			return getCursorPosition(index);
		int a;
		int b;
		float aPart = 0;
		float bPart = 0;
		if(indexTranformation > index) {
			final int toPosition = (int) Math.floor(indexTranformation);
			final int fromPosition = (int) Math.ceil(indexTranformation);
			a = getCursorPosition(toPosition);
			b = getCursorPosition(fromPosition);
			aPart = fromPosition - indexTranformation;
			bPart = indexTranformation - toPosition;
		} else {
			final int toPosition = (int) Math.ceil(indexTranformation);
			final int fromPosition = (int) Math.floor(indexTranformation);
			a = getCursorPosition(toPosition);
			b = getCursorPosition(fromPosition);
			aPart = indexTranformation - fromPosition;
			bPart = toPosition - indexTranformation;
		}
		final int newPosition = (int)(a * aPart + b * bPart);
		return newPosition;
	}
	
	/**
	 * Get Transformed Cursor Size
	 * @return
	 */
	final private Point getTransformedCursorSize(final int index, final float indexTranformation) {
		if(Math.abs(index - indexTranformation) <= 0.0)
			return getCursorSize(index);
		Point a;
		Point b;
		float aPart = 0;
		float bPart = 0;
		if(indexTranformation > index) {
			final int toPosition = (int) Math.floor(indexTranformation);
			final int fromPosition = (int) Math.ceil(indexTranformation);
			a = getCursorSize(toPosition);
			b = getCursorSize(fromPosition);
			aPart = fromPosition - indexTranformation;
			bPart = indexTranformation - toPosition;
		} else {
			final int toPosition = (int) Math.ceil(indexTranformation);
			final int fromPosition = (int) Math.floor(indexTranformation);
			a = getCursorSize(toPosition);
			b = getCursorSize(fromPosition);
			aPart = indexTranformation - fromPosition;
			bPart = toPosition - indexTranformation;
		}
		final int newWidth = (int)(a.x * aPart + b.x * bPart);
		final int newHeight = a.y;
		return new Point(newWidth, newHeight);
	}
	
	/**
	 * Get Cursor Width
	 * @return
	 */
	final private Point getCursorSize(final int index) {
		final int left = this.getPaddingLeft();
        final int right = this.getMeasuredWidth() - this.getPaddingRight();
        final int width = right - left;
        final float density = getResources().getDisplayMetrics().density;
        final int iconSpace = (int)(10 * density);
        final int iconSubSpace = (width - iconSpace * 7) / 6;
        int cursorWidth;
        if(index == 0 || index == getChildCount() - 1)
        	cursorWidth = iconSubSpace + (int)(iconSpace * 1.5f);
        else 
        	cursorWidth = iconSubSpace + iconSpace;
        return new Point(cursorWidth, (int)(iconSubSpace + iconSpace * 2.0f));
	}
	
	/**
	 * Get Cursor Position
	 * @return
	 */
	final private int getCursorPosition(final int index) {
		final int left = this.getPaddingLeft();
        final int right = this.getMeasuredWidth() - this.getPaddingRight();
        final int width = right - left;
        final float density = getResources().getDisplayMetrics().density;
        final int iconSpace = (int)(10 * density);
        final int iconSubSpace = (width - iconSpace * 7) / 6;
        final int middleSize = iconSubSpace + iconSpace;
        final int borderSize = iconSubSpace + (int)(iconSpace * 1.5f);
        if(index == 0)
        	return 0;
        else if(index == getChildCount() - 1)
        	return right - borderSize;
        else 
        	return borderSize + middleSize * (index - 1);
	}

	/**
	 * On Measure
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int left = this.getPaddingLeft();
        final int right = this.getMeasuredWidth() - this.getPaddingRight();
        // Available content
        final int width = right - left;
        final float density = getResources().getDisplayMetrics().density;
        final int icon_space = (int)(10 * density);
        final int icon_size = (width - icon_space * 7) / 6;
        final int newHeight = icon_size + icon_space * 2;
        // Child Measure Spec
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY));
	}
	
	/**
	 * On Layout
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int left = this.getPaddingLeft();
        final int top = this.getPaddingTop();
        final int right = this.getMeasuredWidth() - this.getPaddingRight();
        final int bottom = this.getMeasuredHeight() - this.getPaddingBottom();
        // Available content
        final int width = right - left;
        final int height = bottom - top;
        final float density = getResources().getDisplayMetrics().density;
        final int icon_space = (int)(10 * density);
        final int icon_size = (width - icon_space * 7) / 6;
       	final int center_vertical = (height - icon_size) / 2;
        // Child Measure Spec
        final int widthSpec = MeasureSpec.makeMeasureSpec(icon_size, MeasureSpec.EXACTLY);
        final int heightSpec = MeasureSpec.makeMeasureSpec(icon_size, MeasureSpec.EXACTLY);
        //
        int currentX = left + icon_space;
        int currentY = top + center_vertical;
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            // Measure and Layout Childs
            child.measure(widthSpec, heightSpec);
            child.layout(currentX, currentY, currentX + icon_size, currentY + icon_size);
            currentX += icon_size + icon_space;
        }
	}

}
