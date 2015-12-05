package com.grillbrickstudios.modalist.test;

import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.ListItem;

import junit.framework.TestCase;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 * A test for all of the database functions.
 */
public class ListDatabaseTest extends TestCase {

	ListDatabase db;

	public void setUp() throws Exception {
		super.setUp();
		db = ListDatabase.getInstance();
		db.open();
		db.deleteAll();
	}

	public void tearDown() throws Exception {
		db.close();
	}

	public void testInsertItem() throws Exception {
		final String LISTNAME = "SomeList";
		final String ITEMNAME = "SomeItem";
		final boolean CHECKED = false;
		long i = db.insertItem(LISTNAME, ITEMNAME, CHECKED);

		ListItem item = db.getItem(i);
		assertEquals(item.IsChecked, CHECKED);
		assertEquals(item.ListName, LISTNAME);
		assertEquals(item.ItemName, ITEMNAME);
		db.deleteAll();
	}

	public void testItemExists() throws Exception {

	}

	public void testUpdateItem() throws Exception {

	}

	public void testUpdateItem1() throws Exception {

	}

	public void testDeleteAll() throws Exception {

	}

	public void testDelete() throws Exception {

	}

	public void testQueryMetaList() throws Exception {

	}

	public void testQueryList() throws Exception {

	}

	public void testQueryListItem() throws Exception {

	}

	public void testQueryItem() throws Exception {

	}

	public void testIsChecked() throws Exception {

	}

	public void testIsValidString() throws Exception {

	}
}