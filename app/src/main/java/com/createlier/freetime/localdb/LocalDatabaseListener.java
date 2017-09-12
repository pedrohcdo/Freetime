package com.createlier.freetime.localdb;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database Helper
 * 
 * @author Pedro Henrique
 *
 */
public interface LocalDatabaseListener {
	
	/**
	 * On Create Database
	 * 
	 * Create table structure
	 */
	public void onCreate(final Context context, final SQLiteDatabase database);

	/**
	 * On Update Database
	 * 
	 * Upgrade table structure
	 */
	public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion, final int newVersion);
}
