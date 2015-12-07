package com.grillbrickstudios.modalist.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.model.ListDatabase;
import com.grillbrickstudios.modalist.model.structs.ListItem;
import com.grillbrickstudios.modalist.model.structs.T;

public class DetailActivity extends AppCompatActivity {

	long _id;
	private String _action;
	private EditText _editText;
	private ListDatabase _db;
	private String _listName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		App.setActivityContext(this);

		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;

		_db = ListDatabase.getInstance();
		_db.open();

		Intent intent = getIntent();
		_action = intent.getAction();
		_editText = (EditText) findViewById(R.id.editText);
		assert _editText != null;
		ListItem item;

		_id = intent.getLongExtra(T.C_ID, -1);
		item = _db.getItem(_id);

		switch (_action) {
			case Intent.ACTION_INSERT:
				_listName = intent.getStringExtra(T.C_LIST_NAME);
				findViewById(R.id.btn_edit).setVisibility(View.INVISIBLE);
				findViewById(R.id.btn_delete).setVisibility(View.INVISIBLE);
				break;
			case T.C_LIST_NAME:
				assert item != null;
				_editText.setText(item.ListName);
				actionBar.setTitle(item.ListName);
				break;
			case T.C_ITEM_NAME:
				assert item != null;
				_editText.setText(item.ItemName);
				actionBar.setTitle(item.ItemName);
				findViewById(R.id.btn_edit).setVisibility(View.INVISIBLE);
				break;
		}
	}

	public void saveItem(View view) {
		// Called by the save button.
		updateItem();
		finish();
	}

	public String updateItem() {
		String newName = _editText.getText().toString();

		switch (_action) {
			case Intent.ACTION_INSERT:
				_db.insertItem(_listName, newName, false);
				break;
			case T.C_LIST_NAME:
				_db.updateList(_id, newName);
				break;
			case T.C_ITEM_NAME:
				_db.updateItem(_id, newName);
				break;
		}
		return newName;
	}

	public void editItem(View view) {
		Intent intent = new Intent(this, ListActivity.class);
		intent.setAction(Intent.ACTION_EDIT);
		intent.putExtra(T.C_LIST_NAME, updateItem());
		startActivity(intent);
	}

	public void deleteItem(View view) {
		switch (_action) {
			case T.C_LIST_NAME:
				_db.deleteList(_id);
				break;
			case T.C_ITEM_NAME:
				_db.delete(_id);
				break;
		}
		finish();
	}
}
