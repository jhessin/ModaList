package com.grillbrickstudios.modalist.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grillbrickstudios.modalist.App;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * All database functionality stems from here.
 */
public class ListDatabase {

	private static final String DATABASE_CREATE = String.format(
			"CREATE TABLE if not exists %s (" +
					"%s INTEGER PRIMARY KEY AUTOINCREMENT, " +
					"%s TEXT, " +
					"%s TEXT, " +
					"%s INTEGER)",
			Table.TBL_NAME, Table.C_ID, Table.C_LIST_NAME, Table.C_ITEM_NAME, Table.C_CHECKED
	);
	private static ListDatabase singleton;
	private final Context _context;
	/**
	 * Variables to create (<code>_dbHelper</code>) and hold (<code>_database</code>)
	 */
	private DatabaseHelper _dbHelper;
	private SQLiteDatabase _database;

	/**
	 * Constructor
	 *
	 * @param context save the context for creating the database.
	 */
	private ListDatabase(Context context) {
		_context = context;
	}

	public static ListDatabase getInstance() {
		if (singleton == null)
			singleton = new ListDatabase(App.getActivityContext());
		return singleton;
	}

	/**
	 * Opens the database for use.
	 *
	 * @return the opened database.
	 * @throws SQLException
	 */
	public ListDatabase open() throws SQLException {
		_dbHelper = new DatabaseHelper(_context);
		_database = _dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the openned database.
	 */
	public void close() {
		if (_dbHelper != null) {
			_dbHelper.close();
			_dbHelper = null;
		}
	}

	/**
	 * Inserts a new item with the provided parameters.
	 *
	 * @param listName The name of the list.
	 * @param itemName The item to add.
	 * @param checked  The checked status.
	 * @return The row id of the new row.
	 */
	public long insertItem(String listName, String itemName, boolean checked) {
		if (itemExists(listName, itemName)) return queryListItem(listName, itemName).getPosition();
		if (!isValidString(listName) || !isValidString(itemName)) {
			return -1;
		}

		ContentValues values = new ContentValues();
		values.put(Table.C_CHECKED, checked ? 1 : 0);
		values.put(Table.C_LIST_NAME, listName);
		values.put(Table.C_ITEM_NAME, itemName);

		return _database.insert(Table.TBL_NAME, null, values);
	}

	public boolean itemExists(String listName, String itemName) {
		Cursor cursor = queryListItem(listName, itemName);
		return cursor == null || cursor.getCount() == 0;
	}

	/**
	 * Updates an items name given its id.
	 *
	 * @param id       The id of the item to update.
	 * @param itemName The itemName to change it to.
	 * @return The number of rows effected.
	 */
	public long updateItem(long id, String itemName) {
		if (id < 0) return -1;
		if (!isValidString(itemName)) return 0;
		ContentValues values = new ContentValues();
		values.put(Table.C_ITEM_NAME, itemName);

		return _database.update(Table.TBL_NAME, values, String.format("%s = %d", Table.C_ID, id),
				null);
	}

	/**
	 * Updates an items name given its id.
	 *
	 * @param id      The id of the item to update.
	 * @param checked The updated checked status.
	 * @return The number of rows effected.
	 */
	public long updateItem(long id, boolean checked) {
		if (id < 0) return -1;
		ContentValues values = new ContentValues();
		values.put(Table.C_CHECKED, checked ? 1 : 0);

		return _database.update(Table.TBL_NAME, values, String.format("%s = %d", Table.C_ID, id),
				null);
	}

	/**
	 * Delete all data from the table.
	 *
	 * @return true - if data was deleted. false if nothing was deleted.
	 */
	public boolean deleteAll() {
		int doneDelete;
		doneDelete = _database.delete(Table.TBL_NAME, null, null);
		return doneDelete > 0;
	}

	/**
	 * Delete an item given its id.
	 *
	 * @param id The id of the item to delete.
	 * @return true if the item is deleted. false if the item is not found.
	 */
	public boolean delete(long id) {
		if (id < 0) return false;
		int itemsDeleted;
		itemsDeleted = _database.delete(Table.TBL_NAME, String.format("%s = %d", Table.C_ID, id),
				null);
		return itemsDeleted > 0;
	}

	private String wrapString(String listName) {
		String s = listName;
		if (!s.startsWith("'")) s = "'" + s;
		if (!s.endsWith("'")) s = s + "'";
		return s;
	}

	/**
	 * Query the database for the full list.
	 *
	 * @return A cursor to enumerate the results.
	 * @throws SQLException
	 */
	public Cursor queryMetaList() throws SQLException {
		return _database.query(Table.TBL_NAME, Table.S_META_LIST, null, null, Table.C_LIST_NAME, null,
				Table.C_ID);
	}

	/**
	 * Query the database for all items in a particular list.
	 *
	 * @param listName The list name to enumerate.
	 * @return A cursor to enumerate the results.
	 */
	public Cursor queryList(String listName) {
		if (isValidString(listName))
			return _database.query(Table.TBL_NAME, null, String.format("%s = %s AND ", Table
							.C_LIST_NAME, wrapString(listName)) + Table.W_CLAUSE, null, Table.C_LIST_NAME, null,
					Table
							.C_ID);
		else
			return queryMetaList();
	}

	public Cursor queryListItem(String listName, String itemName) {
		if (isValidString(listName) && isValidString(itemName))
			return _database.query(Table.TBL_NAME, null, String.format("%s = %s AND %s " +
					"= %s", Table.C_LIST_NAME, wrapString(listName), Table.C_ITEM_NAME, wrapString(itemName)), null, null, null, null);
		else
			return queryMetaList();
	}

	/**
	 * Query the database for a single item by its id.
	 *
	 * @param id the id of the item to find.
	 * @return A cursor to enumerate the results.
	 */
	public Cursor queryItem(long id) {
		if (id < 0) return null;
		return _database.query(Table.TBL_NAME, null, String.format("%s = %d", Table
				.C_ID, id), null, Table.C_LIST_NAME, null, Table.C_ID);
	}

	public boolean isChecked(long id) {
		if (id < 0) return false;
		Cursor cursor = _database.query(Table.TBL_NAME, null, String.format("%s = %d", Table
				.C_ID, id), null, Table.C_LIST_NAME, null, Table.C_ID);
		if (cursor == null) return false;
		cursor.moveToFirst();
		return cursor.getInt(cursor.getColumnIndex(Table.C_ID)) > 0;
	}

	/**
	 * A helper method to find if a string is valid.
	 *
	 * @param s The string to test.
	 * @return true if <code>s</code> is valid. false if it is not.
	 */
	public boolean isValidString(String s) {
		return s != null && s.trim().length() > 0;
	}

	/**
	 * Internal class to create the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String TAG = ".gbs.DatabaseHelper";

		/**
		 * Create a helper object to create, open, and/or manage a database.
		 * This method always returns very quickly.  The database is not actually
		 * created or opened until one of {@link #getWritableDatabase} or
		 * {@link #getReadableDatabase} is called.
		 *
		 * @param context to use to open or create the database
		 */
		public DatabaseHelper(Context context) {
			super(context, Table.DB_NAME, null, Table.DB_VERSION);
		}

		/**
		 * Called when the database is created for the first time. This is where the
		 * creation of tables and the initial population of the tables should happen.
		 *
		 * @param db The database.
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
		}

		/**
		 * Called when the database needs to be upgraded. The implementation
		 * should use this method to drop tables, add tables, or do anything else it
		 * needs to upgrade to the new schema version.
		 * <p/>
		 * <p>
		 * The SQLite ALTER TABLE documentation can be found
		 * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
		 * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
		 * you can use ALTER TABLE to rename the old table, then create the new table and then
		 * populate the new table with the contents of the old table.
		 * </p><p>
		 * This method executes within a transaction.  If an exception is thrown, all changes
		 * will automatically be rolled back.
		 * </p>
		 *
		 * @param db         The database.
		 * @param oldVersion The old database version.
		 * @param newVersion The new database version.
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, String.format("Uprading database from version %d to %d, which will destroy" +
					" all old data", oldVersion, newVersion));
			db.execSQL("DROP TABLE IF EXISTS " + Table.TBL_NAME);
			onCreate(db);
		}
	}
}
