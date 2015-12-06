package com.grillbrickstudios.modalist.controller;

import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.structs.T;
import com.grillbrickstudios.modalist.view.custom.ModeSpinner;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * Manages all user input.
 */
public class InputManager implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
	private static final String TAG = ".gbs.InputManager";
	private FloatingActionButton _fabPlus;

	private ListViewManager _lvManager;
	private ModeSpinner _modeSpinner;
	private EditText _textEntry;
	private TextView _titleBar;
	private FloatingActionButton _fabDelete;
	private FloatingActionButton _fabBack;

	public InputManager(Toolbar toolbar, FloatingActionButton fabPlus, FloatingActionButton
			fabBack, FloatingActionButton fabDelete, ListView listView, EditText textEntry) {
		_lvManager = new ListViewManager();

		if (toolbar == null) {
			Log.d(TAG, "setToolbar: Error getting the toolbar.");
			return;
		}

		_modeSpinner = (ModeSpinner) toolbar.findViewById(R.id.mode_spinner);
		_titleBar = (TextView) toolbar.findViewById(R.id.toolbar_title);
		_titleBar.setText(R.string.string_all_lists);
		_modeSpinner.addListener(this);

		_fabDelete = fabDelete;
		_fabDelete.hide();
		_fabDelete.setOnClickListener(this);

		_fabBack = fabBack;
		_fabBack.hide();
		_fabBack.setOnClickListener(this);

		_fabPlus = fabPlus;
		_fabPlus.setOnClickListener(this);

		listView.setOnItemClickListener(this);
		_lvManager.setListView(listView);

		_textEntry = textEntry;
	}

	/**
	 * Called when a view has been clicked.
	 *
	 * @param v The view that was clicked.
	 */
	@Override
	public void onClick(View v) {
		String entry = _textEntry.getText().toString();
		if (v.equals(_fabPlus)) {
			// TODO: FabPlus code here.
		} else if (v.equals(_fabBack)) {
			// TODO: FabBack code here.
		} else if (v.equals(_fabDelete)) {
			// TODO: FabDelete code here.
		}
		_lvManager.update();
	}

	/**
	 * Callback method to be invoked when an item in this AdapterView has
	 * been clicked.
	 * <p/>
	 * Implementers can call getItemAtPosition(position) if they need
	 * to access the data associated with the selected item.
	 *
	 * @param parent   The AdapterView where the click happened.
	 * @param view     The view within the AdapterView that was clicked (this
	 *                 will be a view provided by the adapter)
	 * @param position The position of the view in the adapter.
	 * @param id       The row id of the item that was clicked.
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// view == _listView.item
		Cursor cursor = ((CursorAdapter) parent.getAdapter()).getCursor();
		if (cursor == null || cursor.getCount() <= 0) return;
		cursor.moveToPosition(position);
		if (_lvManager.isListSelected()) {
			_lvManager.selectItem(id);
		} else {
			String listName = cursor.getString(cursor.getColumnIndex(T.C_LIST_NAME));
			_lvManager.selectList(listName);
			_titleBar.setText(listName);
			_fabBack.show();
		}
		_lvManager.update();
	}


	/**
	 * <p>Callback method to be invoked when an item in this view has been
	 * selected. This callback is invoked only when the newly selected
	 * position is different from the previously selected position or if
	 * there was no selected item.</p>
	 * <p/>
	 * Impelmenters can call getItemAtPosition(position) if they need to access the
	 * data associated with the selected item.
	 *
	 * @param parent   The AdapterView where the selection happened
	 * @param view     The view within the AdapterView that was clicked
	 * @param position The position of the view in the adapter
	 * @param id       The row id of the item that is selected
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		if (view == _modeSpinner) {
			_lvManager.update();
		}
	}

	/**
	 * Callback method to be invoked when the selection disappears from this
	 * view. The selection can disappear for instance when touch is activated
	 * or when the adapter becomes empty.
	 *
	 * @param parent The AdapterView that now contains no selected item.
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		_lvManager.update();
	}

	/**
	 * Closes the openned database.
	 */
	public void close() {
		if (_lvManager != null) _lvManager.close();
	}

}
