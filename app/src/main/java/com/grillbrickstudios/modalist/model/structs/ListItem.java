package com.grillbrickstudios.modalist.model.structs;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 * A wrapper struct for list items.
 */
public final class ListItem {
	public final String ListName;
	public final String ItemName;
	public final boolean IsChecked;

	public ListItem(String listName, String itemName, boolean isChecked) {
		ListName = listName;
		ItemName = itemName;
		IsChecked = isChecked;
	}
}
