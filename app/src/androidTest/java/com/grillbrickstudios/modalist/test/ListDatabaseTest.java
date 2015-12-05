package com.grillbrickstudios.modalist.test;

import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.ListItem;

import junit.framework.TestCase;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 * A test for all of the database functions.
 */
public class ListDatabaseTest extends TestCase {

	ListDatabase db;
	private String LISTNAME;
	private String ITEMNAME;
	private boolean CHECKED;

	public void setUp() throws Exception {
		super.setUp();
		db = ListDatabase.getInstance();
		db.open();
		db.drop();
		LISTNAME = "SomeList";
		ITEMNAME = "SomeItem";
		CHECKED = false;
	}

	public void tearDown() throws Exception {
		db.close();
	}

	public void testDrop() throws Exception {
		db.drop();
		long i = db.size();
		assertEquals(0, i);
	}

	public void testInsert() throws Exception {
		db.drop();
		long i = db.insertItem(LISTNAME, ITEMNAME, CHECKED);
		assertEquals(1, i);

		ListItem item = db.getItem(i);
		assertEquals(CHECKED, item.IsChecked);
		assertEquals(LISTNAME, item.ListName);
		assertEquals(ITEMNAME, item.ItemName);

		db.deleteAll();
		item = db.getItem(0);
		assertNull(item);

		item = new ListItem(LISTNAME, ITEMNAME, CHECKED);
		i = db.insertItem(item);
		assertEquals(2, i);

		item = db.getItem(i);
		assertEquals(CHECKED, item.IsChecked);
		assertEquals(LISTNAME, item.ListName);
		assertEquals(ITEMNAME, item.ItemName);
	}

	public void testUpdate() throws Exception {
		db.drop();
		ListItem item = new ListItem(LISTNAME, ITEMNAME, CHECKED);
		long id = db.insertItem(item);

		ListItem newItem = new ListItem("newListName", "newItemName", !CHECKED);
		db.updateItem(id, newItem.ItemName);
		item = db.getItem(id);
		assertEquals(newItem.ItemName, item.ItemName);
		assertEquals(LISTNAME, item.ListName);
		assertEquals(CHECKED, item.IsChecked);

		db.updateItem(id, newItem.IsChecked);
		item = db.getItem(id);
		assertEquals(newItem.ItemName, item.ItemName);
		assertEquals(newItem.IsChecked, item.IsChecked);
		assertEquals(LISTNAME, item.ListName);
	}
}