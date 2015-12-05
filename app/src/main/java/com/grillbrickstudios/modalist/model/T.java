package com.grillbrickstudios.modalist.model;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * A list of variables used to create the database.
 */
public final class T {
	public static final int DB_VERSION = 1;
	public static final String DB_NAME = "ListDb.db";
	public static final String TBL_NAME = "ListTable";
	public static final String C_ID = "_id";
	public static final String C_LIST_NAME = "List";
	public static final String C_ITEM_NAME = "Item";
	public static final String C_CHECKED = "Checked";
	public static final String EMPTY_ITEM = "'...'";

	public static final String[] S_META_LIST = new String[]{String.format("MIN(%s)", C_ID), C_ID,
			C_LIST_NAME};
	public static final String W_CLAUSE = C_ITEM_NAME + " <> " + EMPTY_ITEM;
}
