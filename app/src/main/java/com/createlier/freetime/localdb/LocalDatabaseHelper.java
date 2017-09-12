package com.createlier.freetime.localdb;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.createlier.freetime.R;

/**
 * Database Helper
 * 
 * @author Pedro Henrique
 *
 */
final public class LocalDatabaseHelper extends SQLiteOpenHelper {

	// Final Private Variables
	final private Context mContext;
	final private LocalDatabaseListener mListener;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public LocalDatabaseHelper(final Context context, final String name, final int version, final LocalDatabaseListener listener) {
		super(context, name, null, version);
		mContext = context;
		mListener = listener;
	}

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public LocalDatabaseHelper(final Context context) {
		super(context, context.getString(R.string.localdb_name), null,
				context.getResources().getInteger(R.integer.localdb_version));
		mContext = context;
		mListener = null;
	}
	
	/**
	 * On Create Database
	 * 
	 * Create table structure. The structure must be stored in database.xml
	 * file.
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		if(mListener != null)
			mListener.onCreate(mContext, database);
	}

	/**
	 * On Update Database
	 * 
	 * Upgrade table structure. The structure must be stored in database.xml
	 * file.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		final String sql = mContext.getString(R.string.localdb_upgrade).trim();
		if (sql.length() > 0)
			db.execSQL(sql);
		if (mContext.getResources().getBoolean(R.bool.localdb_reset_on_upgrade))
			onCreate(db);
		if(mListener != null)
			mListener.onUpgrade(mContext, db, oldVersion, newVersion);
	}
}
