package com.createlier.freetime.entity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.createlier.freetime.FreetimeActivity;
import com.createlier.freetime.services.shared.SharedServicesManager;
import com.createlier.freetime.tools.ToolsManager;

/**
 * Navigation Sector
 * 
 * @author Pedro Henrique
 *
 */
abstract public class Sector extends Fragment {
	
	// Final Private Variables
	final private int mLayout;
	
	// Private Variables
	private View mRootView;
	private SharedServicesManager mSharedServiceManager;

	/**
	 * Constructor
	 */
	public Sector(final int layout) {
		mLayout = layout;
	}
	
	/**
	 * On Create View
	 */
	@Override
	final public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(mLayout, container, false);
		mSharedServiceManager = getMainActivity().getSharedServicesManager();
		onCreatingView();
		return mRootView;
	}
	
	/**
	 * On Created View
	 */
	public abstract void onCreatingView();
	
	/**
	 * Get Activity
	 * @return
	 */
	final private FreetimeActivity getMainActivity() {
		return (FreetimeActivity)getActivity();
	}
	
	/**
	 * Get Shared Services Manager
	 * 
	 * @return
	 */
	final public SharedServicesManager getSharedServicesManager() {
		return mSharedServiceManager;
	}
	
	/**
	 * On Save Instance State
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {}
	
	/**
	 * Get Tools Manager
	 * 
	 * @return
	 */
	final public ToolsManager getToolsManager() {
		return getMainActivity().getToolsManager();
	}
	
	/**
	 * On Back Pressed
	 * @return
	 */
	public boolean onBackPressed() {
		return false;
	}
	
	/**
	 * Find View by Id
	 * @param id
	 * @return
	 */
	public View findViewById(final int id) {
		return mRootView.findViewById(id);
	}
}