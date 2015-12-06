package com.grillbrickstudios.modalist.view;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ListView;

import com.grillbrickstudios.modalist.App;
import com.grillbrickstudios.modalist.R;
import com.grillbrickstudios.modalist.controller.InputManager;

/**
 * The entry point of the program.
 * 1 - Sets up the main view
 * 2 - Attaches views to the ListViewManager
 * 3 - Attaches the InputManager to the views.
 */
public class MainActivity extends AppCompatActivity {

	// Create the input manager.
	InputManager _inManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// set the Activity context.
		App.setActivityContext(this);


		// set the ContentView.
		setContentView(R.layout.activity_main);

		// get the toolbar, set it as the action bar, and send it to the input manager.
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) actionBar.setDisplayShowTitleEnabled(false);
		// create the input manager.
		_inManager = new InputManager(toolbar,
				(FloatingActionButton) findViewById(R.id.fab_plus),
				(FloatingActionButton) findViewById(R.id.fab_back),
				(FloatingActionButton) findViewById(R.id.fab_delete),
				(ListView) findViewById(R.id.listView),
				(EditText) findViewById(R.id.edit_text));
	}

	@Override
	protected void onDestroy() {
		if (_inManager != null) _inManager.close();
		super.onDestroy();
	}
}
