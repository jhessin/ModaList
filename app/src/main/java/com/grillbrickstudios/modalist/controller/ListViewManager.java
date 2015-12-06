package com.grillbrickstudios.modalist.controller;

import android.database.SQLException;
import android.support.v7.widget.RecyclerView;

import com.grillbrickstudios.modalist.controller.adapters.CheckCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.CreateCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.EditCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.MetaCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.WrappedAdapter;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.ListItem;
import com.grillbrickstudios.modalist.model.structs.T;
import com.grillbrickstudios.modalist.view.custom.ModeSpinner;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * Controls the display of the data to the screen. As well as user input.
 */
public class ListViewManager {
	private RecyclerView _view;
	private ListDatabase _db;
	private WrappedAdapter _adapter;
	private String _selectedList;

	public ListViewManager() {
		_db = ListDatabase.getInstance();
		_db.open();
		_adapter = new WrappedAdapter(new MetaCursorAdapter());
	}

	private void enterCreateMode() {
		if (isListSelected()) {
			_adapter = new WrappedAdapter(new CreateCursorAdapter(_selectedList));
			_view.setAdapter(_adapter);
		}
	}

	private void enterEditMode() {
		if (isListSelected()) {
			_adapter = new WrappedAdapter(new EditCursorAdapter(_selectedList));
			_view.setAdapter(_adapter);
		}
	}

	private void enterCheckMode() {
		if (isListSelected()) {
			_adapter = new WrappedAdapter(new CheckCursorAdapter(_selectedList));
			_view.setAdapter(_adapter);
		}
	}

	private void enterMetaListMode() {
		_adapter = new WrappedAdapter(new MetaCursorAdapter());
		_view.setAdapter(_adapter);
	}

	public void setRecyclerView(RecyclerView listView) {
		_view = listView;
		enterMetaListMode();
	}

	/**
	 * Notifies the attached observers that the underlying data has been changed
	 * and any View reflecting the data set should refresh itself.
	 */
	public void update() {
		updateMode();
		_adapter.notifyDataSetChanged();
	}

	/**
	 * Closes the openned database.
	 */
	public void close() {
		if (_db != null) _db.close();
	}

	/**
	 * Inserts a new list with an empty item.
	 *
	 * @param listName The name of the list to add.
	 * @return True if the list is successfully created, or if it already exists.
	 */
	public boolean newList(String listName) {
		return _db.insertItem(listName, T.EMPTY_ITEM, false) >= 0;
	}


	/**
	 * Inserts a new item into the selected list.
	 *
	 * @param itemName The name of the item to add.
	 * @param checked  If the item is checked.
	 * @return True if the item is successfully inserted.
	 * @throws AssertionError if there is no list selected.
	 */
	public boolean insertItem(String itemName, boolean checked) throws AssertionError {
		assert _selectedList != null;
		return insertItem(new ListItem(_selectedList, itemName, checked));
	}

	/**
	 * Inserts a new item.
	 *
	 * @param item the item to insert.
	 * @return True if the item is successfully inserted.
	 */
	public boolean insertItem(ListItem item) {
		return _db.insertItem(item) >= 0;
	}

	/**
	 * Updates an items name given its id.
	 *
	 * @param id       The id of the item to update.
	 * @param itemName The itemName to change it to.
	 * @return True if the object is successfully updated.
	 */
	public boolean updateItem(long id, String itemName) {
		return _db.updateItem(id, itemName) == 1;
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

	public boolean selectList(String listName) {
		if (_selectedList.equals(listName)) return false;

		if (!_db.isValidString(listName)) return false;
		try {
			_db.queryList(listName);
		} catch (SQLException e) {
			newList(listName);
			_db.queryList(listName);
		}
		_selectedList = listName;
		update();
		return true;
	}

	private void updateMode() {
		if (!isListSelected()) {
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

	public boolean isListSelected() {
		return _selectedList != null;
	}

	public void selectItem(long id) {
		// TODO: Start item edit activity here.
	}

	public ListItem getItem(long id) {
		return _db.getItem(id);
	}
}
