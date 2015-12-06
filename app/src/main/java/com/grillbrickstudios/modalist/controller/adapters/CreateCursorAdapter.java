package com.grillbrickstudios.modalist.controller.adapters;

import android.widget.SimpleCursorAdapter;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.T;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 */
public class CreateCursorAdapter extends SimpleCursorAdapter {
	/**
	 * Standard constructor.
	 *
	 * @param listname
	 */
	public CreateCursorAdapter(String listname) {
		super(App.getActivityContext(), R.layout.listview_create, ListDatabase.getInstance()
						.queryList(listname),
				new String[]{
						T.C_ITEM_NAME
				}, new int[]{
						R.id.itemText
				}, 0);
	}
}
