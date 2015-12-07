package com.grillbrickstudios.modalist.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.controller.ListViewManager;
import com.grillbrickstudios.modalist.model.structs.T;
import com.grillbrickstudios.modalist.view.custom.ModeSpinner;

public class ListActivity extends AppCompatActivity {

	private ListViewManager _manager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		// Set Global Activity context.
		App.setActivityContext(this);

		// create a list view manager.
		_manager = new ListViewManager();

		// attach the list view to the list view manager.
		ListView listView = (ListView) findViewById(R.id.listView);
		assert listView != null;
		_manager.setListView(listView);

		// Enable back navigation.
		ActionBar actionBar = getSupportActionBar();
		assert actionBar != null;
		actionBar.setDisplayHomeAsUpEnabled(true);

		// get the selected list
		Intent intent = getIntent();
		switch (intent.getAction()) {
			case Intent.ACTION_INSERT:
				if (intent.hasExtra(T.C_LIST_NAME)) {
					String listName = intent.getStringExtra(T.C_LIST_NAME);
					_manager.selectList(listName);
					actionBar.setTitle(listName);
				}
				break;
		}

	}

	/**
	 * Prepare the Screen's standard options menu to be displayed.  This is
	 * called right before the menu is shown, every time it is shown.  You can
	 * use this method to efficiently enable/disable items or otherwise
	 * dynamically modify the contents.
	 * <p/>
	 * <p>The default implementation updates the system menu items based on the
	 * activity's state.  Deriving classes should always call through to the
	 * base class implementation.
	 *
	 * @param menu The options menu as last shown or first initialized by
	 *             onCreateOptionsMenu().
	 * @return You must return true for the menu to be displayed;
	 * if you return false it will not be shown.
	 * @see #onCreateOptionsMenu
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem spinner = menu.findItem(R.id.mode_spinner);
		spinner.setActionView(new ModeSpinner(this));

		return true;
	}

	/**
	 * Initialize the contents of the Activity's standard options menu.  You
	 * should place your menu items in to <var>menu</var>.
	 * <p/>
	 * <p>This is only called once, the first time the options menu is
	 * displayed.  To update the menu every time it is displayed, see
	 * {@link #onPrepareOptionsMenu}.
	 * <p/>
	 * <p>The default implementation populates the menu with standard system
	 * menu items.  These are placed in the {@link Menu#CATEGORY_SYSTEM} group so that
	 * they will be correctly ordered with application-defined menu items.
	 * Deriving classes should always call through to the base implementation.
	 * <p/>
	 * <p>You can safely hold on to <var>menu</var> (and any items created
	 * from it), making modifications to it as desired, until the next
	 * time onCreateOptionsMenu() is called.
	 * <p/>
	 * <p>When you add items to the menu, you can implement the Activity's
	 * {@link #onOptionsItemSelected} method to handle them there.
	 *
	 * @param menu The options menu in which you place your items.
	 * @return You must return true for the menu to be displayed;
	 * if you return false it will not be shown.
	 * @see #onPrepareOptionsMenu
	 * @see #onOptionsItemSelected
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);

		return true;
	}

	public void addListItem(View view) {
		// TODO: add a list item here.
	}
}
