package com.createlier.freetime.localdb.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.createlier.freetime.R;
import com.createlier.freetime.localdb.Dao;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.LocalDatabaseController;
import com.createlier.freetime.localdb.objects.LocationDBO;

import java.util.ArrayList;
import java.util.List;

/**
 * Location DAO
 * 
 * @author Pedro Henrique
 *
 */
final public class LocationDao implements Dao<LocationDBO> {

	// Final Private Variables
	final private LocalDatabaseController mLocalDatabaseController;
	
	/**
	 * Constructor
	 */
	public LocationDao(final LocalDatabase localDatabase) {
		mLocalDatabaseController = localDatabase.getLocalDatabaseController();
	}

	/**
	 * Get Table Name
	 * 
	 * @return
	 */
	private String getTableName() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_locations_name);
	}
	
	/**
	 * Get Latitude Column Name
	 * 
	 * @return
	 */
	private String getPKColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_locations_pk);
	}

	/**
	 * Get Latitude Column Name
	 * 
	 * @return
	 */
	private String getLatitudeColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_locations_key_a);
	}

	/**
	 * Get Latitude Column Name
	 * 
	 * @return
	 */
	private String getLongitudeColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_locations_key_b);
	}
	
	/**
	 * Get Date Column Name
	 * 
	 * @return
	 */
	private String getDateColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_locations_key_c);
	}
	
	/**
	 * List All Items
	 */
	@Override
	public List<LocationDBO> listAll() {
		final List<LocationDBO> all = new ArrayList<LocationDBO>();
		final Cursor cursor = mLocalDatabaseController.queryAll(getTableName());
		final int pkIndex = cursor.getColumnIndex(getPKColumn());
		final int latitudeIndex = cursor.getColumnIndex(getLatitudeColumn());
		final int longitudeIndex = cursor.getColumnIndex(getLongitudeColumn());
		final int dateIndex = cursor.getColumnIndex(getDateColumn());
		if(cursor.moveToFirst()) {
			do {
				all.add(new LocationDBO(cursor.getInt(pkIndex), cursor.getDouble(latitudeIndex), cursor.getDouble(longitudeIndex), cursor.getLong(dateIndex)));
			} while(cursor.moveToNext());
		}
		cursor.close();
		return all;
	}

	/**
	 * Get Item
	 */
	@Override
	public LocationDBO get(int roll) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Insert Item
	 */
	@Override
	public long insert(LocationDBO item) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(getLatitudeColumn(), item.getLatitude());
		contentValues.put(getLongitudeColumn(), item.getLongitude());
		contentValues.put(getDateColumn(), item.getDate());
		return mLocalDatabaseController.insert(getTableName(), contentValues);
	}

	/**
	 * Delete Item
	 */
	@Override
	public void delete(LocationDBO item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(LocationDBO item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAll() {
		mLocalDatabaseController.clearTable(getTableName());
	}
}
