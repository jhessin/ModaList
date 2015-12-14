package com.grillbrickstudios.modalist.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.model.structs.ListItem;
import com.grillbrickstudios.modalist.model.structs.T;

import java.util.ArrayList;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * All database functionality stems from here.
 */
public class ListDatabase {


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
		if (_database != null) _database.close();
		if (_dbHelper != null) _dbHelper.close();

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
		return insertItem(new ListItem(listName, itemName, checked));
	}

	/**
	 * Inserts a new item.
	 *
	 * @param item A wrapper object that holds the data to add.
	 * @return The row id of the new row.
	 */
	public long insertItem(ListItem item) {
		if (item == null) return -1;
		if (!isValidItem(item)) return -1;
		if (itemExists(item)) return queryListItem(item).getPosition();

		return _database.insert(T.TBL_NAME, null, itemToValues(item));
	}

	public ContentValues itemToValues(ListItem item) {
		ContentValues values = new ContentValues();
		values.put(T.C_LIST_NAME, item.ListName);
		values.put(T.C_ITEM_NAME, item.ItemName);
		values.put(T.C_CHECKED, item.IsChecked ? 1 : 0);
		return values;
	}

	public Cursor queryListItem(ListItem item) {
		return queryListItem(item.ListName, item.ItemName);
	}

	public boolean itemExists(String listName, String itemName) {
		Cursor cursor = queryListItem(listName, itemName);
		return cursor != null && cursor.getCount() != 0;
	}

	public boolean itemExists(ListItem item) {
		return itemExists(item.ListName, item.ItemName);
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
		values.put(T.C_ITEM_NAME, itemName);

		return _database.update(T.TBL_NAME, values, String.format("%s = %d", T.C_ID, id),
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
		values.put(T.C_CHECKED, checked ? 1 : 0);

		return _database.update(T.TBL_NAME, values, String.format("%s = %d", T.C_ID, id),
				null);
	}

	/**
	 * Delete all data from the table.
	 *
	 * @return true - if data was deleted. false if nothing was deleted.
	 */
	public boolean deleteAll() {
		int doneDelete;
		doneDelete = _database.delete(T.TBL_NAME, null, null);
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
		itemsDeleted = _database.delete(T.TBL_NAME, String.format("%s = %d", T.C_ID, id),
				null);

		_database.execSQL(String.format(
				"UPDATE %s " +
						"SET %s = %s - 1 " +
						"WHERE %s > %d",
				T.TBL_NAME,
				T.C_ID, T.C_ID,
				T.C_ID, id));

		return itemsDeleted > 0;
	}

	public boolean delete(ArrayList<Long> ids) {
		int totalDeleted = 0;
		for (Long id :
				ids) {
			totalDeleted += _database.delete(T.TBL_NAME, String.format("%s = %d", T.C_ID, id),
					null);
		}

		return totalDeleted > 0;
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
		return _database.query(T.TBL_NAME, T.S_META_LIST, null, null, T.C_LIST_NAME, null,
				T.C_ID);
	}

	/**
	 * Query the database for all items in a particular list.
	 *
	 * @param listName The list name to enumerate.
	 * @return A cursor to enumerate the results.
	 */
	public Cursor queryList(String listName) {
		if (isValidString(listName))
			return _database.query(T.TBL_NAME, null, String.format("%s = %s AND ", T
							.C_LIST_NAME, wrapString(listName)) + T.HIDE_EMPTY, null, null, null,
					T
							.C_ID);
		else
			return queryMetaList();
	}

	public Cursor queryListItem(String listName, String itemName) {
		if (isValidString(listName) && isValidString(itemName))
			return _database.query(T.TBL_NAME, null, String.format("%s = %s AND %s " +
					"= %s", T.C_LIST_NAME, wrapString(listName), T.C_ITEM_NAME, wrapString(itemName)), null, null, null, null);
		else
			return queryMetaList();
	}

	private Cursor queryListItem(long id) {
		if (id < 0) return null;
		return _database.query(T.TBL_NAME, null, String.format("%s = %d", T.C_ID, id), null,
				null, null, null);
	}

	public boolean isChecked(long id) throws IllegalArgumentException {
		ListItem item = getItem(id);
		assert item != null;
		return item.IsChecked;
	}

	public ListItem getItem(long id) {
		if (id < 0) return null;
		Cursor cursor = queryListItem(id);
		if (cursor == null || cursor.getCount() == 0) return null;
		cursor.moveToFirst();
		String listName = cursor.getString(cursor.getColumnIndex(T.C_LIST_NAME));
		String itemName = cursor.getString(cursor.getColumnIndex(T.C_ITEM_NAME));
		Boolean isChecked = cursor.getInt(cursor.getColumnIndex(T.C_CHECKED)) != 0;
		cursor.close();
		return new ListItem(listName, itemName, isChecked);
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

	public boolean isValidItem(ListItem item) {
		return isValidString(item.ListName) && isValidString(item.ItemName);
	}

	public void drop() {
		_dbHelper.onUpgrade(_database, 0, 1);
	}

	public long size() {
		return DatabaseUtils.queryNumEntries(_database, T.TBL_NAME);
	}

	public int insertItem(ListItem[] items) {
		if (items.length < 1) return 0;
		int count = 0;
		for (ListItem item :
				items) {
			long id = insertItem(item);
			if (id >= 0)
				count++;
		}
		return count;
	}

	public int updateList(long id, String newName) {
		if (id < 0) return -1;
		if (!isValidString(newName)) return 0;

		String oldName = getItem(id).ListName;
		ContentValues values = new ContentValues();
		values.put(T.C_LIST_NAME, newName);

		return _database.update(T.TBL_NAME, (values), String.format("%s = %s", T.C_LIST_NAME,
				wrapString(
						(oldName))), null);
	}

	public void deleteList(long id) {
		if (id < 0) return;
		String listName = getItem(id).ListName;

		_database.delete(T.TBL_NAME, String.format("%s = %s", T.C_LIST_NAME, wrapString(listName)
		), null);
	}

	/**
	 * Internal class to create the database file.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		private static final String TAG = ".gbs.DatabaseHelper";
		private static final String DATABASE_CREATE = String.format(
				"CREATE TABLE if not exists %s (" +
						"%s INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT, " +
						"%s TEXT, " +
						"%s TEXT, " +
						"%s INTEGER)",
				T.TBL_NAME, T.C_ID, T.C_LIST_NAME, T.C_ITEM_NAME, T.C_CHECKED
		);

		/**
		 * Create a helper object to create, open, and/or manage a database.
		 * This method always returns very quickly.  The database is not actually
		 * created or opened until one of {@link #getWritableDatabase} or
		 * {@link #getReadableDatabase} is called.
		 *
		 * @param context to use to open or create the database
		 */
		public DatabaseHelper(Context context) {
			super(context, T.DB_NAME, null, T.DB_VERSION);
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
			db.execSQL("DROP TABLE IF EXISTS " + T.TBL_NAME);
			onCreate(db);
		}
	}
}
