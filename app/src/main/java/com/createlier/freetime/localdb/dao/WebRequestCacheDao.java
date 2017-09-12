package com.createlier.freetime.localdb.dao;

import android.content.ContentValues;
import android.database.Cursor;

import com.createlier.freetime.R;
import com.createlier.freetime.localdb.Dao;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.LocalDatabaseController;
import com.createlier.freetime.localdb.objects.WebRequestCacheDBO;

import java.util.ArrayList;
import java.util.List;

/**
 * Location DAO
 * 
 * @author Pedro Henrique
 *
 */
final public class WebRequestCacheDao implements Dao<WebRequestCacheDBO> {

	// Final Private Variables
	final private LocalDatabaseController mLocalDatabaseController;

	/**
	 * Constructor
	 *
	 */
	public WebRequestCacheDao(final LocalDatabase localDatabase) {
		mLocalDatabaseController = localDatabase.getLocalDatabaseController();
	}

	/**
	 * Get Table Name
	 * 
	 * @return
	 */
	private String getTableName() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_name);
	}
	
	/**
	 * Get Latitude Column Name
	 * 
	 * @return
	 */
	private String getPKColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_pk);
	}

	/**
	 * Get Type Column Name
	 * 
	 * @return
	 */
	private String getTypeColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_key_a);
	}

	/**
	 * Get Url Column Name
	 * 
	 * @return
	 */
	private String getUrlColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_key_b);
	}
	
	/**
	 * Get Parameters Column Name
	 * 
	 * @return
	 */
	private String getParamsColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_key_c);
	}
	
	/**
	 * Get Connection Timeout Column Name
	 * 
	 * @return
	 */
	private String getResultCodeColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_key_d);
	}
	
	/**
	 * Get Read Timeout Column Name
	 * 
	 * @return
	 */
	private String getResultBodyColumn() {
		return mLocalDatabaseController.getContext().getString(R.string.localdb_webrequestcache_key_e);
	}
	
	/**
	 * List All Items
	 */
	@Override
	public List<WebRequestCacheDBO> listAll() {
		//
		final List<WebRequestCacheDBO> all = new ArrayList<WebRequestCacheDBO>();
		final Cursor cursor = mLocalDatabaseController.queryAll(getTableName());
		final int pkIndex = cursor.getColumnIndex(getPKColumn());
		final int typeIndex = cursor.getColumnIndex(getTypeColumn());
		final int urlIndex = cursor.getColumnIndex(getUrlColumn());
		final int paramsIndex = cursor.getColumnIndex(getParamsColumn());
		final int resultCodeIndex = cursor.getColumnIndex(getResultCodeColumn());
		final int resultBodyIndex = cursor.getColumnIndex(getResultBodyColumn());
		if(cursor.moveToFirst()) {
			do {
				// Make WebRequestCacheDBO
				final WebRequestCacheDBO webRequestDBO = new WebRequestCacheDBO(cursor.getInt(pkIndex));
				webRequestDBO.setType(cursor.getInt(typeIndex));
				webRequestDBO.setUrl(cursor.getString(urlIndex));
				webRequestDBO.setParameters(cursor.getString(paramsIndex));
				webRequestDBO.setResultCode(cursor.getInt(resultCodeIndex));
				webRequestDBO.setResultBody(cursor.getString(resultBodyIndex));
				//
				all.add(webRequestDBO);
			} while(cursor.moveToNext());
		}
		cursor.close();
		return all;
	}

	/**
	 * List All Items of
	 */
	public List<WebRequestCacheDBO> listAll(final String url, final int requestType) {
		//
		final List<WebRequestCacheDBO> all = new ArrayList<>();
		final Cursor cursor = mLocalDatabaseController.rawQuery("SELECT * FROM " + getTableName() + " WHERE " + getUrlColumn() + " = ? AND " + getTypeColumn() + " = ?", new String[] {url.trim(), requestType + ""});
		final int pkIndex = cursor.getColumnIndex(getPKColumn());
		final int typeIndex = cursor.getColumnIndex(getTypeColumn());
		final int urlIndex = cursor.getColumnIndex(getUrlColumn());
		final int paramsIndex = cursor.getColumnIndex(getParamsColumn());
		final int resultCodeIndex = cursor.getColumnIndex(getResultCodeColumn());
		final int resultBodyIndex = cursor.getColumnIndex(getResultBodyColumn());
		if(cursor.moveToFirst()) {
			do {
				// Make WebRequestCacheDBO
				final WebRequestCacheDBO webRequestDBO = new WebRequestCacheDBO(cursor.getInt(pkIndex));
				webRequestDBO.setType(cursor.getInt(typeIndex));
				webRequestDBO.setUrl(cursor.getString(urlIndex));
				webRequestDBO.setParameters(cursor.getString(paramsIndex));
				webRequestDBO.setResultCode(cursor.getInt(resultCodeIndex));
				webRequestDBO.setResultBody(cursor.getString(resultBodyIndex));
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
	public WebRequestCacheDBO get(int roll) {
		return null;
	}
	
	/**
	 * Get Last
	 *
	 * @return
	 */
	public WebRequestCacheDBO getLast() {
		//
		WebRequestCacheDBO last = null;
		final Cursor cursor = mLocalDatabaseController.queryLast(getTableName(), getPKColumn());
		final int pkIndex = cursor.getColumnIndex(getPKColumn());
		final int typeIndex = cursor.getColumnIndex(getTypeColumn());
		final int urlIndex = cursor.getColumnIndex(getUrlColumn());
		final int paramsIndex = cursor.getColumnIndex(getParamsColumn());
		final int resultCodeIndex = cursor.getColumnIndex(getResultCodeColumn());
		final int resultBodyIndex = cursor.getColumnIndex(getResultBodyColumn());
		if(cursor.moveToFirst()) {
			do {
				// Make WebRequestCacheDBO
				final WebRequestCacheDBO webRequestDBO = new WebRequestCacheDBO(cursor.getInt(pkIndex));
				webRequestDBO.setType(cursor.getInt(typeIndex));
				webRequestDBO.setUrl(cursor.getString(urlIndex));
				webRequestDBO.setParameters(cursor.getString(paramsIndex));
				webRequestDBO.setResultCode(cursor.getInt(resultCodeIndex));
				webRequestDBO.setResultBody(cursor.getString(resultBodyIndex));
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
	public long insert(WebRequestCacheDBO item) {
		//
		ContentValues contentValues = new ContentValues();
		contentValues.put(getTypeColumn(), item.getType());
		contentValues.put(getUrlColumn(), item.getUrl());
		contentValues.put(getParamsColumn(), item.getParameters());
		contentValues.put(getResultCodeColumn(), item.getResultCode());
		contentValues.put(getResultBodyColumn(), item.getResultBody());
		return mLocalDatabaseController.insert(getTableName(), contentValues);
	}

	/**
	 * Delete Item
	 */
	@Override
	public void delete(WebRequestCacheDBO item) {
		mLocalDatabaseController.delete(getTableName(), getPKColumn() + "=" + item.getId()); 
	}

	/**
	 * Delete Type
	 */
	public void deleteType(WebRequestCacheDBO item) {
		mLocalDatabaseController.delete(getTableName(), getUrlColumn() + "= '" + item.getUrl() + "' AND " + getTypeColumn() + " = " + item.getType());
	}

	@Override
	public void update(WebRequestCacheDBO item) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearAll() {;
		mLocalDatabaseController.clearTable(getTableName());
	}
}
