package com.createlier.freetime;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;

import com.createlier.freetime.entity.custom.layouts.SplashLogoLayout;


/**
 * Main Activity
 * 
 * @author Pedro Henrique
 *
 */
public class SplashLogoActivity extends AppCompatActivity implements SplashLogoLayout.SplashConsumeListener {
	
	/**
	 * Splash Logo Listener
	 * 
	 * @author user
	 *
	 */
	public interface SplashLogoListener {
		
		/**
		 * End Logos
		 */
		public void onEndLogos();
	}
	
	// Private Variables
	private SplashLogoLayout mLayout;
	private Class<? extends Activity> mActivityClass;
	private SplashLogoListener mSplashLogoListener;
	
	/**
	 * On Create
	 */
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		setContentView(R.layout.splash_activity);
	}
	
	/**
	 * Create View
	 */
	@SuppressLint("NewApi")
	public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
	    if(Build.VERSION.SDK_INT >= 11)
	      return super.onCreateView(parent, name, context, attrs);
	    return null;
	}
	
	/**
	 * Set Listener
	 * 
	 * @param listener
	 */
	public void setListener(final SplashLogoListener listener) {
		mSplashLogoListener = listener;
	}
	
	/**
	 * On Create
	 */
	public void splashLogos(final SplashLogoLayout.Logo[] logos, final Class<? extends Activity> endSplash) {
		mLayout = (SplashLogoLayout) findViewById(R.id.splash_root_layout);
		mLayout.setMaxLogosPerPage(4);
		mLayout.requestConsumeLogosList(logos, this);
		mActivityClass = endSplash;
	}

	/**
	 * Splash Consumed List
	 */
	@Override
	public void onConsumedList() {
		if(mSplashLogoListener != null)
			mSplashLogoListener.onEndLogos();
		if(mActivityClass != null) {
			startActivity(new Intent(this, mActivityClass));
			finish();
			overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
		}
	}
	
	/**
	 * On Pause
	 */
	@Override
	protected void onPause() {
		mLayout.onPause();
		super.onPause();
	}
	
	/**
	 * On Resume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mLayout.onResume();
	}
}