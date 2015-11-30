package com.grillbrickstudios.modalist.controller.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.Table;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * A simple cursor adapter that also supports the use of a checkbox.
 */
public class CheckCursorAdapter extends SimpleCursorAdapter {

	/**
	 * Standard constructor.
	 *
	 * @param context The context where the ListView associated with this
	 *                SimpleListItemFactory is running
	 * @param layout  resource identifier of a layout file that defines the views
	 *                for this list item. The layout file should include at least
	 *                those named views defined in "to"
	 * @param c       The database cursor.  Can be null if the cursor is not available yet.
	 * @param from    A list of column names representing the data to bind to the UI.  Can be null
	 *                if the cursor is not available yet.
	 * @param to      The views that should display column in the "from" parameter.
	 *                These should all be TextViews. The first N views in this list
	 *                are given the values of the first N columns in the from
	 *                parameter.  Can be null if the cursor is not available yet.
	 * @param flags   Flags used to determine the behavior of the adapter,
	 *                as per {@link CursorAdapter#CursorAdapter(Context, Cursor, int)}.
	 */
	public CheckCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
		super(context, layout, c, from, to, flags);
	}

	/**
	 * Binds all of the field names passed into the "to" parameter of the
	 * constructor with their corresponding cursor columns as specified in the
	 * "from" parameter.
	 * <p/>
	 * Binding occurs in two phases. First, if a
	 * {@link ViewBinder} is available,
	 * {@link ViewBinder#setViewValue(View, Cursor, int)}
	 * is invoked. If the returned value is true, binding has occured. If the
	 * returned value is false and the view to bind is a TextView,
	 * {@link #setViewText(TextView, String)} is invoked. If the returned value is
	 * false and the view to bind is an ImageView,
	 * {@link #setViewImage(ImageView, String)} is invoked. If no appropriate
	 * binding can be found, an {@link IllegalStateException} is thrown.
	 *
	 * @param view
	 * @param context
	 * @param cursor
	 * @throws IllegalStateException if binding cannot occur
	 * @see CursorAdapter#bindView(View,
	 * Context, Cursor)
	 * @see #getViewBinder()
	 * @see #setViewBinder(ViewBinder)
	 * @see #setViewImage(ImageView, String)
	 * @see #setViewText(TextView, String)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (view == view.findViewById(R.id.box)) {
			final ListDatabase db = ListDatabase.getInstance();
			final int id = cursor.getInt(cursor.getColumnIndex(Table.C_ID));
			CheckBox box = (CheckBox) view;
			//noinspection ConstantConditions
			if (box == null) return;
			box.setChecked(db.isChecked(id));
			box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					db.updateItem(id, isChecked);
				}
			});
		} else
			super.bindView(view, context, cursor);
	}
}
