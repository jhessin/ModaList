package com.grillbrickstudios.modalist.controller;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.controller.adapters.CheckCursorAdapter;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.T;
import com.grillbrickstudios.modalist.view.custom.ModeSpinner;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * Controls the display of the data to the screen. As well as user input.
 */
public class ListViewManager {
	private ListView _listView;
	private ListDatabase _db;
	private SimpleCursorAdapter _createAdapter,
			_editAdapter,
			_checkAdapter,
			_metaAdapter;

	private String _selectedList;
	private long _selectedItem;

	public ListViewManager() {
		Context _context = App.getActivityContext();
		_db = ListDatabase.getInstance();

		_db.open();

		// The Columns to bind to the metalist view.
		String[] from = new String[]{
				T.C_LIST_NAME
		};

		// The views to bind.
		int[] to = new int[]{
				R.id.itemText
		};

		_metaAdapter = new SimpleCursorAdapter(_context, R.layout.listview_meta, _db
				.queryMetaList(), from, to, 0);

		// The columns to bind to the item list view.
		from = new String[]{
				T.C_ITEM_NAME
		};

		_createAdapter = new SimpleCursorAdapter(_context, R.layout.listview_create, null, from,
				to, 0);
		_editAdapter = new SimpleCursorAdapter(_context, R.layout.listview_edit, null, from, to, 0);

		// The columns to bind to the checklist view.
		from = new String[]{
				T.C_CHECKED,
				T.C_ITEM_NAME
		};

		// The views to bind to the checklist view.
		to = new int[]{
				R.id.box,
				R.id.itemText
		};

		_checkAdapter = new CheckCursorAdapter(_context, R.layout.listview_check, null, from, to,
				0);
	}

	public void enterCreateMode() {
		_createAdapter.changeCursor(_db.queryList(_selectedList));
		_listView.setAdapter(_createAdapter);
	}

	public void enterEditMode() {
		_editAdapter.changeCursor(_db.queryList(_selectedList));
		_listView.setAdapter(_editAdapter);
	}

	public void enterCheckMode() {
		_checkAdapter.changeCursor(_db.queryList(_selectedList));
		_listView.setAdapter(_checkAdapter);
	}

	public void enterMetaListMode() {
		_metaAdapter.changeCursor(_db.queryMetaList());
		_listView.setAdapter(_metaAdapter);
	}

	public void setListView(ListView listView) {
		_listView = listView;
		enterMetaListMode();
	}

	/**
	 * Notifies the attached observers that the underlying data has been changed
	 * and any View reflecting the data set should refresh itself.
	 */
	public void notifyDataSetChanged() {
		updateMode();
		_createAdapter.notifyDataSetChanged();
		_editAdapter.notifyDataSetChanged();
		_checkAdapter.notifyDataSetChanged();
		_metaAdapter.notifyDataSetChanged();
	}

	/**
	 * Closes the openned database.
	 */
	public void close() {
		_db.close();
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
		return _db.insertItem(listName, itemName, checked);
	}

	public long insertItem(String itemName, boolean checked) {
		return insertItem(_selectedList, itemName, checked);
	}

	/**
	 * Updates an items name given its id.
	 *
	 * @param id       The id of the item to update.
	 * @param itemName The itemName to change it to.
	 * @return The number of rows effected.
	 */
	public long updateItem(long id, String itemName) {
		return _db.updateItem(id, itemName);
	}

	/**
	 * Updates an items name given its id.
	 *
	 * @param id      The id of the item to update.
	 * @param checked The updated checked status.
	 * @return The number of rows effected.
	 */
	public long updateItem(int id, boolean checked) {
		return _db.updateItem(id, checked);
	}

	/**
	 * Delete all data from the table.
	 *
	 * @return true - if data was deleted. false if nothing was deleted.
	 */
	public boolean deleteAll() {
		return _db.deleteAll();
	}

	/**
	 * Delete an item given its id.
	 *
	 * @param id The id of the item to delete.
	 * @return true if the item is deleted. false if the item is not found.
	 */
	public boolean delete(long id) {
		return _db.delete(id);
	}

	public boolean isChecked(int id) {
		return _db.isChecked(id);
	}

	public boolean selectList(String listName) {
		String old = _selectedList;
		if (listName == null) {
			_selectedList = null;
			if (old != null) updateMode();
			return false;
		}
		if (!_db.isValidString(listName)) return false;
		try {
			_db.queryList(listName);
		} catch (SQLException e) {
			insertItem(listName, "...", false);
		}
		_selectedList = listName;
		if (old == null) updateMode();
		notifyDataSetChanged();
		return true;
	}

	private void updateMode() {
		if (!listSelected()) {
			enterMetaListMode();
			return;
		}

		switch (ModeSpinner.getMode()) {
			case ModeSpinner.CREATE:
				enterCreateMode();
				break;
			case ModeSpinner.EDIT:
				enterEditMode();
				break;
			case ModeSpinner.CHECK:
				enterCheckMode();
				break;
		}
	}

	public boolean listSelected() {
		return _selectedList != null;
	}

	public String getItemText(long item) {
		if (item < 0) return null;
		Cursor cursor = _db.queryItem(item);
		if (cursor == null) return null;
		return cursor.getString(cursor.getColumnIndex(T.C_ITEM_NAME));
	}

	public boolean selectItem(long itemID) {
		long old = _selectedItem;
		if (itemID < 0) {
			deselectItem();
			return false;
		}
		Cursor cursor = _db.queryItem(itemID);
		if (cursor != null && cursor.getCount() > 0) {
			_selectedItem = itemID;
			return true;
		}
		return false;
	}

	public void deselectItem() {
		_selectedItem = -1;
	}

	public boolean itemSelected() {
		return _selectedItem >= 0;
	}

	public long updateSelectedItem(String entry) {
		return updateItem(_selectedItem, entry);
	}

	public String getSelectedItemText() {
		return getItemText(_selectedItem);
	}

	public boolean deleteSelected() {
		if (!itemSelected()) return false;

		if (delete(_selectedItem)) {
			deselectItem();
			return true;
		} else return false;
	}
}
