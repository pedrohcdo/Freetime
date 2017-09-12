package com.createlier.freetime.localdb.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.createlier.freetime.R;
import com.createlier.freetime.exceptions.WebRequestDaoException;
import com.createlier.freetime.localdb.Dao;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.LocalDatabaseController;
import com.createlier.freetime.localdb.objects.WebRequestDBO;
import com.createlier.freetime.utils.GeneralUtils;
import com.createlier.freetime.webservices.RequestConfig;
import com.createlier.freetime.webservices.RequestParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * Location DAO
 * 
 * @author Pedro Henrique
 *
 */
final public class WebRequestDao implements Dao<WebRequestDBO> {

	// Final Private Variables
	final private LocalDatabaseController mLocalDatabaseController;
	
	/**
	 * Constructor
	 *
	 */
	public WebRequestDao(final LocalDatabase localDatabase) {
		mLocalDatabaseController = localDatabase.getLocalDatabaseController();
	}

	/**
	 * Get Table Name
	 * 
	 * @return
	 */
	private String getTableName() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_name);
	}
	
	/**
	 * Get Latitude Column Name
	 * 
	 * @return
	 */
	private String getPKColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_pk);
	}

	/**
	 * Get Type Column Name
	 * 
	 * @return
	 */
	private String getTypeColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_key_a);
	}

	/**
	 * Get Url Column Name
	 * 
	 * @return
	 */
	private String getUrlColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_key_b);
	}
	
	/**
	 * Get Parameters Column Name
	 * 
	 * @return
	 */
	private String getParamsColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_key_c);
	}
	
	/**
	 * Get Connection Timeout Column Name
	 * 
	 * @return
	 */
	private String getConTOColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_key_d);
	}
	
	/**
	 * Get Read Timeout Column Name
	 * 
	 * @return
	 */
	private String getReadTOColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequest_key_e);
	}
	
	/**
	 * List All Items
	 */
	@Override
	public List<WebRequestDBO> listAll() {
		//
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		//
		final List<WebRequestDBO> all = new ArrayList<WebRequestDBO>();
		final Cursor cursor = mLocalDatabaseController.queryAll(getTableName());
		final int pkIndex = cursor.getColumnIndex(getPKColumn());
		final int typeIndex = cursor.getColumnIndex(getTypeColumn());
		final int urlIndex = cursor.getColumnIndex(getUrlColumn());
		final int paramsIndex = cursor.getColumnIndex(getParamsColumn());
		final int conTOIndex = cursor.getColumnIndex(getConTOColumn());
		final int readTOIndex = cursor.getColumnIndex(getReadTOColumn());
		if(cursor.moveToFirst()) {
			do {
				// Make Request Config
				final RequestConfig requestConfig = new RequestConfig();
				requestConfig.connectionTimeout = cursor.getInt(conTOIndex);
				requestConfig.readTimeout = cursor.getInt(readTOIndex);
				// Make Request Parameters
				final RequestParameters requestParameters = RequestParameters.make(cursor.getString(paramsIndex));
				// Make WebRequestDBO
				final WebRequestDBO webRequestDBO = new WebRequestDBO(cursor.getInt(pkIndex));
				webRequestDBO.setType(cursor.getInt(typeIndex));
				webRequestDBO.setUrl(cursor.getString(urlIndex));
				webRequestDBO.setConfig(requestConfig);
				webRequestDBO.setParameters(requestParameters);
				//
				all.add(webRequestDBO);
			} while(cursor.moveToNext());
		}
		cursor.close();
		return all;
	}

	/**
	 * Get Item
	 */
	@Override
	public WebRequestDBO get(int roll) {
		//
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		
		return null;
	}
	
	/**
	 * Get Last
	 *
	 * @return
	 */
	public WebRequestDBO getLast() {
		//
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		
		//
		WebRequestDBO last = null;
		final Cursor cursor = mLocalDatabaseController.queryLast(getTableName(), getPKColumn());
		final int pkIndex = cursor.getColumnIndex(getPKColumn());
		final int typeIndex = cursor.getColumnIndex(getTypeColumn());
		final int urlIndex = cursor.getColumnIndex(getUrlColumn());
		final int paramsIndex = cursor.getColumnIndex(getParamsColumn());
		final int conTOIndex = cursor.getColumnIndex(getConTOColumn());
		final int readTOIndex = cursor.getColumnIndex(getReadTOColumn());
		if(cursor.moveToFirst()) {
			do {
				// Make Request Config
				final RequestConfig requestConfig = new RequestConfig();
				requestConfig.connectionTimeout = cursor.getInt(conTOIndex);
				requestConfig.readTimeout = cursor.getInt(readTOIndex);
				// Make Request Parameters
				final RequestParameters requestParameters = RequestParameters.make(cursor.getString(paramsIndex));
				// Make WebRequestDBO
				final WebRequestDBO webRequestDBO = new WebRequestDBO(cursor.getInt(pkIndex));
				webRequestDBO.setType(cursor.getInt(typeIndex));
				webRequestDBO.setUrl(cursor.getString(urlIndex));
				webRequestDBO.setConfig(requestConfig);
				webRequestDBO.setParameters(requestParameters);
				//
				last = webRequestDBO;
			} while(cursor.moveToNext());
		}
		cursor.close();
		
		// TODO Auto-generated method stub
		return last;
	}

	/**
	 * Insert Item
	 */
	@Override
	public long insert(WebRequestDBO item) {
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		//
		final String params = item.getParameters().mold();
		if(params == null)
			throw new WebRequestDaoException("Wrong params.");
		ContentValues contentValues = new ContentValues();
		contentValues.put(getTypeColumn(), item.getType());
		contentValues.put(getUrlColumn(), item.getUrl());
		contentValues.put(getParamsColumn(), params);
		contentValues.put(getConTOColumn(), item.getConfig().connectionTimeout);
		contentValues.put(getReadTOColumn(), item.getConfig().readTimeout);
		return mLocalDatabaseController.insert(getTableName(), contentValues);
	}

	/**
	 * Delete Item
	 */
	@Override
	public void delete(WebRequestDBO item) {
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		mLocalDatabaseController.delete(getTableName(), getPKColumn() + "=" + item.getId()); 
	}

	@Override
	public void update(WebRequestDBO item) {
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAll() {
		GeneralUtils.SyncThreadPass.assertCurrentThreadPass(WebRequestDao.class);
		mLocalDatabaseController.clearTable(getTableName());
	}
}
