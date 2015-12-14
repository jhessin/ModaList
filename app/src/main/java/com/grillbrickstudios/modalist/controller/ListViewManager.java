package com.grillbrickstudios.modalist.controller;

import android.database.Cursor;
import android.database.SQLException;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.grillbrickstudios.modalist.controller.adapters.CheckCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.CreateCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.EditCursorAdapter;
import com.grillbrickstudios.modalist.controller.adapters.MetaCursorAdapter;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.ListItem;
import com.grillbrickstudios.modalist.model.structs.T;
import com.grillbrickstudios.modalist.view.custom.ModeSpinner;

import java.io.Closeable;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * Controls the display of the data to the screen. As well as user input.
 */
public class ListViewManager {
	private ListView _listView;
	private ListDatabase _db;
	private SimpleCursorAdapter _adapter;
	private String _selectedList;

	public ListViewManager() {
		_db = ListDatabase.getInstance();
		_db.open();
		_adapter = new MetaCursorAdapter();

		ModeSpinner.addListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				update();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				update();
			}
		});
	}

	/**
	 * Invoked when the garbage collector has detected that this instance is no longer reachable.
	 * The default implementation does nothing, but this method can be overridden to free resources.
	 * <p/>
	 * <p>Note that objects that override {@code finalize} are significantly more expensive than
	 * objects that don't. Finalizers may be run a long time after the object is no longer
	 * reachable, depending on memory pressure, so it's a bad idea to rely on them for cleanup.
	 * Note also that finalizers are run on a single VM-wide finalizer thread,
	 * so doing blocking work in a finalizer is a bad idea. A finalizer is usually only necessary
	 * for a class that has a native peer and needs to call a native method to destroy that peer.
	 * Even then, it's better to provide an explicit {@code close} method (and implement
	 * {@link Closeable}), and insist that callers manually dispose of instances. This
	 * works well for something like files, but less well for something like a {@code BigInteger}
	 * where typical calling code would have to deal with lots of temporaries. Unfortunately,
	 * code that creates lots of temporaries is the worst kind of code from the point of view of
	 * the single finalizer thread.
	 * <p/>
	 * <p>If you <i>must</i> use finalizers, consider at least providing your own
	 * {@link ReferenceQueue} and having your own thread process that queue.
	 * <p/>
	 * <p>Unlike constructors, finalizers are not automatically chained. You are responsible for
	 * calling {@code super.finalize()} yourself.
	 * <p/>
	 * <p>Uncaught exceptions thrown by finalizers are ignored and do not terminate the finalizer
	 * thread.
	 * <p/>
	 * See <i>Effective Java</i> Item 7, "Avoid finalizers" for more.
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		_db.close();
	}

	private void enterCreateMode() {
		if (isListSelected()) {
			_adapter = new CreateCursorAdapter(_selectedList);
			_listView.setAdapter(_adapter);
		}
	}

	private void enterEditMode() {
		if (isListSelected()) {
			_adapter = new EditCursorAdapter(_selectedList);
			_listView.setAdapter(_adapter);
		}
	}

	private void enterCheckMode() {
		if (isListSelected()) {
			_adapter = new CheckCursorAdapter(_selectedList);
			_listView.setAdapter(_adapter);
		}
	}

	private void enterMetaListMode() {
		_adapter = new MetaCursorAdapter();
		_listView.setAdapter(_adapter);
	}

	public void setListView(ListView listView) {
		_listView = listView;
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
		if (_selectedList != null && _selectedList.equals(listName)) return false;

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

	public ListItem getItem(long id) {
		return _db.getItem(id);
	}

	public String newList() {
		String listName = "New List";
		int i = 0;
		listName = listName + i;
		while (_db.itemExists(listName, T.EMPTY_ITEM)) {
			listName = listName.replace(
					Character.forDigit(i, Character.MAX_RADIX),
					Character.forDigit(++i, Character.MAX_RADIX));
		}

		return listName;
	}

	public void deleteChecked() {
		if (_adapter == null) return;

		ArrayList<Long> ids = new ArrayList<>();

		Cursor c = _adapter.getCursor();
		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			if (c.getInt(c.getColumnIndex(T.C_CHECKED)) != 0)
				ids.add((long) c.getInt(c.getColumnIndex(T.C_ID)));
		}

		_db.delete(ids);

	}
}
