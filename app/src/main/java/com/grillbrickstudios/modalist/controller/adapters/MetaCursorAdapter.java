package com.grillbrickstudios.modalist.controller.adapters;

import android.widget.SimpleCursorAdapter;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.T;

/**
 * Created by jhess on 12/5/2015 for ModaList.
 * A simple cursor adapter for use when displaying the MetaList.
 */
public class MetaCursorAdapter extends SimpleCursorAdapter {
	/**
	 * Standard constructor.
	 *
	 */
	public MetaCursorAdapter() {
		super(App.getActivityContext(), R.layout.lv_meta, ListDatabase.getInstance().queryMetaList(),
				new String[]{
						T.C_LIST_NAME
				}, new int[]{
						R.id.itemText
				}, 0);
	}
}
