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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ListItem item = (ListItem) o;

		return IsChecked == item.IsChecked && ListName.equals(item.ListName) && ItemName.equals(item.ItemName);

	}

	@Override
	public int hashCode() {
		int result = ListName.hashCode();
		result = 31 * result + ItemName.hashCode();
		result = 31 * result + (IsChecked ? 1 : 0);
		return result;
	}
}
