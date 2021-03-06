package com.grillbrickstudios.modalist.controller.adapters;

import android.widget.SimpleCursorAdapter;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.T;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 * A simple cursor adapter for use when the list is in Edit mode.
 */
public class EditCursorAdapter extends SimpleCursorAdapter {
	/**
	 * Standard constructor.
	 *
	 * @param listName The name of the list to preload the cursor with.
	 */
	public EditCursorAdapter(String listName) {
		super(App.getActivityContext(), R.layout.lv_edit, ListDatabase.getInstance()
						.queryList(listName),
				new String[]{
						T.C_ITEM_NAME
				}, new int[]{
						R.id.itemText
				}, 0);
	}
}
