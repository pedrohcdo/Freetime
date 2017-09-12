package com.createlier.freetime.entity.custom.layouts;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.createlier.freetime.exceptions.SplashLogoLayoutException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Action Bar Layout
 * 
 * @author Pedro Henrique
 *
 */
public class SplashLogoLayout extends ViewGroup {

	/**
	 * Logo
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final public static class Logo {
		
		// Final Variables
		final private int mLogo;
		private float mScale;
		private Bitmap mBitmap;
		
		/**
		 * Constructor
		 * 
		 * @param logo
		 * @param scale
		 */
		public Logo(final int logo, final float scale) {
			mLogo = logo;
			mScale = scale;
		}
	}
	
	/**
	 * Consume Listener
	 * 
	 * @author Pedro Henrique
	 *
	 */
	public interface SplashConsumeListener {
		
		/**
		 * On Consumed List
		 */
		public void onConsumedList();
	}
	
	/**
	 * Animation Process
	 * 
	 * @author Pedro Henrique
	 *
	 */
	final private class ConsumeProcess implements Runnable {

		/**
		 * Run
		 */
		@Override
		public void run() {
			animationProcess();
		}
	}
	
	
	// Final Private Static Variables
	final private static int SPACE = 30; /** DP */ 
	final private static int BORDER = 10; /** DP */
	
	// Final Private Variables
	final private ConsumeProcess mConsumeProcess = new ConsumeProcess();
	
	// Private Variables
	private List<Logo> mLogoList;
	private SplashConsumeListener mListener;
	private List<Logo> mConsumedLogosList;
	private boolean mConsumeReleased = false;
	private float mConsumedHeight = 0;
	private int mConsumePhase = -1;
	private Handler mHandler;
	private int mMaxLogosPerPage = -1;
	
	/**
	 * Constructor
	 * @param context
	 */
	public SplashLogoLayout(Context context) {
		this(context, null, 0);
	}
	
	/**
	 * Constructor
	 * @param context
	 */
	public SplashLogoLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Constructor
	 * @param context
	 */
	@SuppressLint("NewApi")
	public SplashLogoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	/**
	 * Max Logo Per Page
	 * @param max Max Logos per Page (Set -1 to undetermined)
	 */
	final public void setMaxLogosPerPage(final int max) {
		if(max <= 0 && max != -1)
			throw new SplashLogoLayoutException("Use a value for maximum amount of logos per page of -1 or greater than 0.");
		mMaxLogosPerPage = max;
	}
	
	/**
	 * Consume Image Views List
	 * @param list
	 */
	final public void requestConsumeLogosList(final Logo[] list, final SplashConsumeListener listener) {
		if(mHandler != null)
			mHandler.removeCallbacksAndMessages(null);
		else
			mHandler = new Handler();
		mLogoList = new ArrayList<Logo>();
		mConsumedLogosList = new ArrayList<Logo>();
		for(final Logo i : list)
			mLogoList.add(new Logo(i.mLogo, i.mScale));
		mListener = listener;
		mConsumeReleased = true;
		if(isLayoutRequested())
			requestLayout();
	}
	
	/**
	 * On Layout
	 */
	@SuppressLint("DrawAllocation")
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
        final float space = SPACE * density;
 
        
		// If released to consume
		if(mConsumeReleased) {
			consumeLogoList();
			
			mHandler.postDelayed(mConsumeProcess, 125 * getChildCount() + 3000);
			mConsumeReleased = false;
		}
		
        
        // Final Layout
        int start = (int) ((height - mConsumedHeight) / 2);
        for(int i=0; i<mConsumedLogosList.size(); i++) {
        	final Logo logo = mConsumedLogosList.get(i);
        	final View view = getChildAt(i);
        	// Measure and Layout
        	final int leftLogo = (int)((width - logo.mBitmap.getWidth()) / 2);
        	view.measure(MeasureSpec.makeMeasureSpec(logo.mBitmap.getWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(logo.mBitmap.getHeight(), MeasureSpec.EXACTLY));
        	view.layout(leftLogo, start, leftLogo + logo.mBitmap.getWidth(), start + logo.mBitmap.getHeight());
        	// Move
        	start += logo.mBitmap.getHeight() + space;
        }
        
	}
	
	/**
	 * Animation Process
	 */
	final private void animationProcess() {
		if(mConsumePhase == 0) {
			if(mLogoList.size() <= 0) {
				mListener.onConsumedList();
			} else {
				// Start remove childs
				startChildsExchange();
				//
				mConsumePhase++;
				mHandler.postDelayed(mConsumeProcess, 600);
			}
		} else if(mConsumePhase == 1) {
			// Start next Phase
			mConsumeReleased = true;
			requestLayout();
			mConsumePhase++;
		}
	}
	
	/**
	 * Consume Logo List
	 */
	final private void consumeLogoList() {
		//
        final int left = this.getPaddingLeft();
        final int top = this.getPaddingTop();
        final int right = this.getMeasuredWidth() - this.getPaddingRight();
        final int bottom = this.getMeasuredHeight() - this.getPaddingBottom();
        
        // Available content
        final int width = right - left;
        final int height = bottom - top;
        final float density = getResources().getDisplayMetrics().density;
        final float space = SPACE * density;
        final float border = BORDER * density;
        final float freeWidth = width - border * 2;
        final float freeHeight = height - border * 2;
        
		// Clear childs
		removeAllViews();
		invalidate();
		
		// Reset Global Scale
		mConsumedHeight = 0;
		mConsumedLogosList.clear();
		mConsumePhase = 0;
		
		//
		float consumedHeight = 0;
        boolean first = true;
        
        
        final float[] globalScaleQueue = new float[mLogoList.size()]; 
        final Options[] options = new Options[mLogoList.size()];
        int index = 0;
        float globalScale = 1;
        
        // List Global Scales and Decodes
        Iterator<Logo> itr = mLogoList.iterator();
        while(itr.hasNext()) {
        	final Logo logo = itr.next();
        	// Decode Output
        	options[index] = new Options();
        	options[index].inJustDecodeBounds = true;
        	BitmapFactory.decodeResource(getResources(), logo.mLogo, options[index]);
        	// Update Global Scale
        	float bitmapWidth = options[index].outWidth * logo.mScale;
        	if(bitmapWidth > freeWidth) {
        		final float scale = bitmapWidth / freeWidth;
        		if(scale > globalScale)
        			globalScale = scale;
        	}
        	globalScaleQueue[index] = globalScale;
        	//
        	index++;
        }
        
        // Get Best Global Scale
        float bestGlobalScale = 1;
        for(int i=0; i<mLogoList.size(); i++) {
        	if(mMaxLogosPerPage != -1 && i >= mMaxLogosPerPage)
        		break;
        	consumedHeight = measureConsumedHeight(i, globalScaleQueue, options, mLogoList);
        	if(consumedHeight <= freeHeight)
        		bestGlobalScale = globalScaleQueue[i];
        }
        
        // Consume Logo List
        consumedHeight = 0;
        itr = mLogoList.iterator();
        index = 0;
        
        while(itr.hasNext()) {
        	final Logo logo = itr.next();
        	//
        	if(mMaxLogosPerPage != -1 && index >= mMaxLogosPerPage)
        		break;
        	
        	// Decode and Fit Dimensions
        	float bitmapWidth = (options[index].outWidth * logo.mScale) / bestGlobalScale;
        	float bitmapHeight = (options[index].outHeight * logo.mScale) / bestGlobalScale;
        	
        	// First
        	if(first) {
        		// Decode Bitmap
            	final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), logo.mLogo);
            	final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int)bitmapWidth, (int)bitmapHeight, false);
            	if(!bitmap.isRecycled() && !bitmap.equals(scaledBitmap))
            		bitmap.recycle();
            	
            	// Add new Child
            	addNewChild(scaledBitmap);
            	
            	// Add consumed Logo
        		consumedHeight += bitmapHeight;
        		final Logo nLogo = new Logo(logo.mLogo, logo.mScale);
        		nLogo.mBitmap = scaledBitmap;
        		mConsumedLogosList.add(nLogo);
        		itr.remove();
        		first = false;
        	// Others
        	} else {
        		final float newConsumedHeight = consumedHeight + bitmapHeight + space;
        		if(newConsumedHeight <= freeHeight) {
	        		// Decode Bitmap
	            	final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), logo.mLogo);
	            	final Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int)bitmapWidth, (int)bitmapHeight, false);
	            	if(!bitmap.isRecycled() && !bitmap.equals(scaledBitmap))
	            		bitmap.recycle();
	            	
	            	// Add new Child
	            	addNewChild(scaledBitmap);
	            	
	            	// Add consumed Logo
        			consumedHeight = newConsumedHeight;
        			final Logo nLogo = new Logo(logo.mLogo, logo.mScale);
	        		nLogo.mBitmap = scaledBitmap;
	        		mConsumedLogosList.add(nLogo);
        			itr.remove();
        		} else {
        			break;
        		}
        	}
        	
        	//
        	index++;
        }
        
        //
        startChildsEntrace(height); 
        
        //
        mConsumedHeight = consumedHeight;
	}
	
	/**
	 * Is Correct Scale
	 * @return True if correct scale
	 */
	final private float measureConsumedHeight(final int point, final float[] scaleQueue, final Options[] options, final List<Logo> logos) {
		final float density = getResources().getDisplayMetrics().density;
		final float space = SPACE * density;
        final float globalScale = scaleQueue[point];
        float consumedHeight = 0;
        // Interact
        for(int i=0; i<=point; i++) {
        	final Logo logo = logos.get(i);
        	// Decode and Fit Dimensions
        	float bitmapHeight = (options[i].outHeight * logo.mScale) / globalScale;
        	// First
        	if(i == 0)
        		consumedHeight += bitmapHeight;
        	else
        		consumedHeight += bitmapHeight + space;
        }
		//
		return consumedHeight;
	}
	
	boolean mPaused = false;
	
	/**
	 * On Pause
	 */
	final public void onPause() {
		mPaused = true;
		mHandler.removeCallbacksAndMessages(null);
	}
	
	/**
	 * On Resume
	 */
	final public void onResume() {
		if(mPaused) {
			mHandler.postDelayed(mConsumeProcess, 600);
			mPaused = false;
		}
	}
	
	/**
	 * Add New Child
	 */
	final private void addNewChild(final Bitmap logo) {
		// Create Image View Child
		final ImageView imageView = new ImageView(getContext());
    	imageView.setImageBitmap(logo);
    	// Add child
    	addView(imageView);
	}
	
	/**
	 * Start Childs Entrace
	 */
	final private void startChildsEntrace(final int animationSpace) {
	   	for(int i=0; i<getChildCount(); i++) {
	   		final int j = i;
	   		// Start Swing Animation
			final AlphaAnimation alphaAnimation = new AlphaAnimation(0, 0);
	    	alphaAnimation.setDuration(125 * j + 300);
	    	alphaAnimation.setFillAfter(true);
	    	alphaAnimation.setFillEnabled(true);
	    	alphaAnimation.setAnimationListener(new AnimationListener() {
				
	    		/** Unused */
				public void onAnimationStart(Animation animation) {}
				public void onAnimationRepeat(Animation animation) {}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, animationSpace, 0);
			    	translateAnimation.setDuration(250 - j * 35);
			    	// Start Translate Animation
			    	getChildAt(j).startAnimation(translateAnimation);
				}
			});
	    	// Start Alpha Animation
	    	getChildAt(j).startAnimation(alphaAnimation);
	    	
	   	}
	}
	
	/**
	 * Start Remove Childs
	 */
	final private void startChildsExchange() {
		for(int i=0; i<getChildCount(); i++) {
			final View view = getChildAt(i);
			AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
	    	alphaAnimation.setDuration(500);
	    	alphaAnimation.setFillAfter(true);
	    	view.startAnimation(alphaAnimation);
		}
	}
}
