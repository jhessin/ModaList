package com.grillbrickstudios.modalist.test;

import android.database.Cursor;

import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.ListItem;
import com.grillbrickstudios.modalist.model.structs.T;

import junit.framework.TestCase;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 * A test for all of the database functions.
 */
public class ListDatabaseTest extends TestCase {

	private ListDatabase db;
	private String _listName = "SomeList";
	private String _itemName = "SomeItem";
	private boolean _defaultChecked = false;
	private ListItem _sampleItem = new ListItem(_listName, _itemName, _defaultChecked);
	private ListItem[] _sampleItems = new ListItem[]{
			_sampleItem,
			new ListItem(_listName, _itemName + 1, !_defaultChecked),
			new ListItem(_listName + 1, _itemName + 2, _defaultChecked),
			new ListItem(_listName + 1, _itemName + 3, !_defaultChecked),
			new ListItem(_listName + 2, _itemName + 4, _defaultChecked),
			new ListItem(_listName + 2, _itemName + 5, !_defaultChecked),
	};

	public void setUp() throws Exception {
		super.setUp();
		db = ListDatabase.getInstance();
		db.open();
		db.drop();
	}

	public void tearDown() throws Exception {
		db.close();
	}

	public void testDrop() throws Exception {
		long i = db.size();
		assertEquals(0, i);
	}

	public void testInsert() throws Exception {
		long i = db.insertItem(_listName, _itemName, _defaultChecked);
		assertEquals(1, i);

		ListItem item = db.getItem(i);
		assertEquals(_defaultChecked, item.IsChecked);
		assertEquals(_listName, item.ListName);
		assertEquals(_itemName, item.ItemName);

		db.deleteAll();
		item = db.getItem(1);
		assertNull(item);

		item = _sampleItem;
		i = db.insertItem(item);
		assertEquals(2, i);

		item = db.getItem(i);
		assertEquals(_defaultChecked, item.IsChecked);
		assertEquals(_listName, item.ListName);
		assertEquals(_itemName, item.ItemName);
	}

	public void testUpdate() throws Exception {
		ListItem item = _sampleItem;
		long id = db.insertItem(item);

		ListItem newItem = new ListItem("newListName", "newItemName", !_defaultChecked);
		db.updateItem(id, newItem.ItemName);
		item = db.getItem(id);
		assertEquals(newItem.ItemName, item.ItemName);
		assertEquals(_listName, item.ListName);
		assertEquals(_defaultChecked, item.IsChecked);

		db.updateItem(id, newItem.IsChecked);
		item = db.getItem(id);
		assertEquals(newItem.ItemName, item.ItemName);
		assertEquals(newItem.IsChecked, item.IsChecked);
		assertEquals(_listName, item.ListName);
	}

	public void testDelete() throws Exception {
		long i = 1;
		for (ListItem item :
				_sampleItems) {
			long id = db.insertItem(item);
			assertEquals(i++, id);
		}

		assertTrue(db.delete(2));
		ListItem item = db.getItem(2);
		assertEquals(_sampleItems[2], item);
	}

	public void testQueryListItem() throws Exception {
		db.insertItem(_sampleItem);
		Cursor cursor = db.queryListItem(_sampleItem);
		assertTrue(cursor != null && cursor.getCount() == 1);
		cursor.moveToFirst();
		assertEquals(_listName, cursor.getString(cursor.getColumnIndex(T.C_LIST_NAME)));
		assertEquals(_itemName, cursor.getString(cursor.getColumnIndex(T.C_ITEM_NAME)));
		assertEquals(_defaultChecked, cursor.getLong(cursor.getColumnIndex(T.C_CHECKED)) != 0);
		cursor.close();
	}

	public void testItemExists() throws Exception {
		db.insertItem(_sampleItem);
		assertTrue(db.itemExists(_sampleItem));
	}

	public void testDeleteAll() throws Exception {
		db.insertItem(_sampleItems);
		assertEquals(_sampleItems.length, db.size());

		db.deleteAll();
		assertEquals(0, db.size());
	}

	public void testQueryMetaList() throws Exception {
		db.insertItem(_sampleItems);

		Cursor cursor = db.queryMetaList();
		assertTrue(cursor != null && cursor.getCount() == 3);
		cursor.moveToFirst();

		assertEquals(_sampleItems[0].ListName, cursor.getString(cursor.getColumnIndex(T
				.C_LIST_NAME)));
		cursor.moveToNext();
		assertEquals(_sampleItems[2].ListName, cursor.getString(cursor.getColumnIndex(T
				.C_LIST_NAME)));
		cursor.moveToNext();
		assertEquals(_sampleItems[4].ListName, cursor.getString(cursor.getColumnIndex(T
				.C_LIST_NAME)));
	}

	public void testQueryList() throws Exception {
		db.insertItem(_sampleItems);

		Cursor cursor = db.queryList(_sampleItems[0].ListName);
		assertTrue(cursor != null && cursor.getCount() == 2);
		cursor.moveToFirst();

		assertEquals(_sampleItems[0].ItemName, cursor.getString(cursor.getColumnIndex(T
				.C_ITEM_NAME)));
		cursor.moveToNext();
		assertEquals(_sampleItems[1].ItemName, cursor.getString(cursor.getColumnIndex(T
				.C_ITEM_NAME)));

		cursor = db.queryList(_sampleItems[2].ListName);
		assertTrue(cursor != null && cursor.getCount() == 2);
		cursor.moveToFirst();

		assertEquals(_sampleItems[2].ItemName, cursor.getString(cursor.getColumnIndex(T
				.C_ITEM_NAME)));
		cursor.moveToNext();
		assertEquals(_sampleItems[3].ItemName, cursor.getString(cursor.getColumnIndex(T
				.C_ITEM_NAME)));

		cursor = db.queryList(_sampleItems[4].ListName);
		assertTrue(cursor != null && cursor.getCount() == 2);
		cursor.moveToFirst();

		assertEquals(_sampleItems[4].ItemName, cursor.getString(cursor.getColumnIndex(T
				.C_ITEM_NAME)));
		cursor.moveToNext();
		assertEquals(_sampleItems[5].ItemName, cursor.getString(cursor.getColumnIndex(T
				.C_ITEM_NAME)));

		cursor.close();
	}

	public void testIsChecked() throws Exception {
		db.insertItem(_sampleItems);
		assertEquals(_sampleItems.length, db.size());
		for (int i = 0; i < _sampleItems.length; i++) {
			assertEquals(_sampleItems[i].IsChecked, db.isChecked(i+1));
		}
	}
}