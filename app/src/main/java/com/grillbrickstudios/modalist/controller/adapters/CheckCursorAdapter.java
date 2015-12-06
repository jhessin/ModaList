package com.grillbrickstudios.modalist.controller.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.T;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * A simple cursor adapter that also supports the use of a checkbox.
 */
public class CheckCursorAdapter extends SimpleCursorAdapter {

	final int _layout = R.layout.listview_check;

	/**
	 * Standard constructor.
	 *
	 * @param listname
	 */
	public CheckCursorAdapter(String listname) {
		super(App.getActivityContext(), R.layout.listview_check, ListDatabase.getInstance()
						.queryList(listname),
				new String[]{
						T.C_CHECKED,
						T.C_ITEM_NAME
				}, new int[]{
						R.id.box,
						R.id.itemText
				}, 0);
	}

	/**
	 * Inflates view(s) from the specified XML file.
	 *
	 * @param context
	 * @param cursor
	 * @param parent
	 * @see CursorAdapter#newView(Context,
	 * Cursor, ViewGroup)
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		Cursor c = getCursor();

		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(_layout, parent, false);

		TextView text = (TextView) v.findViewById(R.id.itemText);
		CheckBox box = (CheckBox) v.findViewById(R.id.box);

		if (text != null) {
			text.setText(c.getString(c.getColumnIndex(T.C_ITEM_NAME)));
		}

		if (box != null) {
			final ListDatabase db = ListDatabase.getInstance();
			final int id = c.getInt(c.getColumnIndex(T.C_ID));
			box.setChecked(c.getInt(c.getColumnIndex(T.C_CHECKED)) != 0);
			box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					db.updateItem(id, isChecked);
				}
			});
		}
		return v;
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
	 * @param c
	 * @throws IllegalStateException if binding cannot occur
	 * @see CursorAdapter#bindView(View,
	 * Context, Cursor)
	 * @see #getViewBinder()
	 * @see #setViewBinder(ViewBinder)
	 * @see #setViewImage(ImageView, String)
	 * @see #setViewText(TextView, String)
	 */
	@Override
	public void bindView(View view, Context context, Cursor c) {
		TextView text = (TextView) view.findViewById(R.id.itemText);
		CheckBox box = (CheckBox) view.findViewById(R.id.box);

		if (text != null) {
			text.setText(c.getString(c.getColumnIndex(T.C_ITEM_NAME)));
		}
		if (box != null) {
			final ListDatabase db = ListDatabase.getInstance();
			final int id = c.getInt(c.getColumnIndex(T.C_ID));
			box.setChecked(c.getInt(c.getColumnIndex(T.C_CHECKED)) != 0);
			box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					db.updateItem(id, isChecked);
				}
			});
		}
	}
}
