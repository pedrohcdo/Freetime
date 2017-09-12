package com.createlier.freetime.entity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.createlier.freetime.exceptions.SectorsManagerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Entity Manager
 * 
 * @author Pedro Henrique
 *
 */
final public class SectorsManager {

	// Final Private Variables
	final private FragmentManager mFragmentManager;
	final private List<Class<? extends Sector>> mSectors = new ArrayList<Class<? extends Sector>>();
	final private int mContainer;
	
	// Private Variables
	private int mSectorId = -1;
	private Sector mCurrentSector;
	
	/**
	 * Constructor
	 */
	public SectorsManager(final FragmentManager fragmentManager, final int container) {
		mFragmentManager = fragmentManager;
		mContainer = container;
		mCurrentSector = null;
	}
	
	/**
	 * Add Sector
	 */
	public void mapSectors(final Class<? extends Sector>[] sectors) {
		mSectors.addAll(Arrays.asList(sectors));
		setSector(0, null);
	}
	
	/**
	 * Map Sector
	 * @param index
	 * @param sector
	 */
	public void remapSector(final int index, final Class<? extends Sector> sector) {
		if(index >= mSectors.size())
			throw new SectorsManagerException("This sector can not be mapped because there is no sector in the given position.");
		mSectors.set(index, sector);
		// Force setSector() if need
		if(index == mSectorId) {
			mSectorId = -1;
			setSector(index, null);
		}
	}
	
	/**
	 * Set Sector
	 * @param sector
	 */
	public void setSector(final int sector, final Bundle arguments) {
		// If same sector
		if(sector == mSectorId)
			return;
		mSectorId = sector;
		try {
			mCurrentSector = mSectors.get(sector).newInstance();
		} catch (final Exception e) {
			throw new SectorsManagerException("The Navigation Sector constructor can not have arguments.");
		}
		if(arguments != null)
			mCurrentSector.setArguments(arguments);
		// Replace fragment sector
		mFragmentManager.beginTransaction()
		.replace(mContainer, mCurrentSector)
		.commitAllowingStateLoss();
	}

	/**
	 * Set Sector
	 * @param sector
	 */
	public void setSector(final int sector) {
		setSector(sector, null);
	}
	
	/**
	 * On Back Pressed
	 * 
	 * @return
	 */
	public boolean onBackPressed() {
		if(mCurrentSector != null)
			return mCurrentSector.onBackPressed();
		return false;
	}
	
	/**
	 * Get Current Sector Index
	 * @return
	 */
	public int getCurrentSectorIndex() {
		return mSectorId;
	}
	
	/**
	 * Get Current Sector
	 * 
	 * @return
	 */
	public Sector getCurrentSector() {
		return mCurrentSector;
	}
}
