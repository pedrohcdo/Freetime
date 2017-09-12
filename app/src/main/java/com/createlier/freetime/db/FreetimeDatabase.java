package com.createlier.freetime.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.createlier.freetime.R;
import com.createlier.freetime.localdb.LocalDatabase;
import com.createlier.freetime.localdb.LocalDatabaseListener;

/**
 * Database Struct
 * @author user
 *
 */
final public class FreetimeDatabase implements LocalDatabaseListener {

	//
	static LocalDatabase sSingletonDatabase;

	/**
	 * Setup Database
	 *
	 * @param context
     */
	public static void setupDatabase(final Context context) {
		sSingletonDatabase = newInstance(context);
	}

	/**
	 * Get Singleton
	 * @return
     */
	public static LocalDatabase singleton(){
		return sSingletonDatabase;
	}

	/**
	 * On Create Database
	 * 
	 * Create table structure. The structure must be stored in database.xml
	 * file.
	 */
	@Override
	public void onCreate(final Context context, final SQLiteDatabase database) {
		final String sql = context.getString(R.string.localdb_structure).trim();
		if (sql.length() > 0) {
			final String[] statements = sql.split(";");
			for(final String statement : statements) {
				final String statementFormated = statement.trim().replaceAll("\n", "").replaceAll("\t", "").replaceAll("\r", "");
				database.execSQL(statementFormated);
			}
		}
	}

	/**
	 * On Update Database
	 * 
	 * Upgrade table structure. The structure must be stored in database.xml
	 * file.
	 */
	@Override
	public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion, final int newVersion) {
	}
	
	/**
	 * Get New Instance
	 * @param context
	 * @return
	 */
	static public LocalDatabase newInstance(final Context context) {
		return new LocalDatabase(context, context.getString(R.string.localdb_name), context.getResources().getInteger(R.integer.localdb_version), new FreetimeDatabase());
	}
}
