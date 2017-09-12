package com.createlier.freetime.localdb;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Pedro Henrique
 *
 */
final public class LocalDatabaseController {

	// Final Private Variables
	final private Context mContext;
	final private LocalDatabase mLocalDatabase;
	
	// Shared Lock
	final private static Object mSharedILock = new Object();
	
	/**
	 * Constructor
	 * 
	 * @param localDatabase
	 */
	protected LocalDatabaseController(final Context context, final LocalDatabase localDatabase) {
		mContext = context;
		mLocalDatabase = localDatabase;
	}
	
	/**
	 * Get Contex
	 * @return
	 */
	public Context getContext() {
		return mContext;
	}
	
	/**
	 * Insert Dao
	 *
	 */
	public long insert(final String tableName, final ContentValues values) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			return database.insert(tableName, null, values);
		}
	}

	/**
	 * Update Table
	 * 
	 * @param tableName
	 * @param values
	 * @param whereClause
	 */
	public void update(final String tableName, final ContentValues values, final String whereClause) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			database.update(tableName, values, whereClause, null);
		}
	}

	/**
	 * Update Table
	 *
	 * @param tableName
	 * @param values
	 * @param whereClause
	 */
	public long insertOrUpdate(final String tableName, final ContentValues values, final String whereClause, final String[] whereArgs, final String pkColumnName) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			Cursor cursor = database.rawQuery("select * from " + tableName + " where " + whereClause, whereArgs);
			if(cursor.moveToFirst()) {
				long id = cursor.getLong(cursor.getColumnIndex(pkColumnName));
				cursor.close();
				return database.update(tableName, values, whereClause, whereArgs);
			} else {
				cursor.close();
				return insert(tableName, values);
			}
		}
	}
	
	/**
	 * Update Table
	 * 
	 * @param tableName
	 * @param values
	 * @param whereClause
	 * @param whereArgs
	 */
	public void update(final String tableName, final ContentValues values, final String whereClause, final String[] whereArgs) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			database.update(tableName, values, whereClause, whereArgs);
		}
	}
	
	/**
	 * Delete all rows of table
	 * @param tableName
	 */
	public void clearTable(final String tableName) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			database.delete(tableName, null, null);
		}
	}
	
	/**
	 * Query Dao
	 * 
	 * @return
	 */
	public Cursor queryAll(final String tableName) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			return database.rawQuery("select * from " + tableName, null);
		}
	}

	/**
	 * Query Dao
	 *
	 * @return
	 */
	public Cursor rawQuery(final String query) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			return database.rawQuery(query, null);
		}
	}

	/**
	 * Query Dao
	 *
	 * @return
	 */
	public Cursor rawQuery(final String query, final String[] selectionArgs) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			return database.rawQuery(query, selectionArgs);
		}
	}

	/**
	 * Query Last
	 * 
	 * @param tableName
	 */
	public Cursor queryLast(final String tableName, final String column) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
			return database.rawQuery("select * from " + tableName + " order by " + column + " desc limit 1", null);
		}
	}
	
	/**
	 * Delete 
	 * 
	 * @param tableName
	 * @param clause
	 * @return
	 */
	public boolean delete(final String tableName, final String clause) {
		synchronized(mSharedILock) {
			final SQLiteDatabase database = mLocalDatabase.getDatabase();
	    	return database.delete(tableName, clause, null) > 0;
		}
	}
}
