package com.grillbrickstudios.modalist.view.custom;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.grillbrickstudios.modalist.R;

import java.util.ArrayList;

/**
 * Created by jhess on 11/29/2015 for ModaList.
 * A custom spinner to handle modes throughout the app.
 */
public class ModeSpinner extends AppCompatSpinner implements AdapterView.OnItemSelectedListener {
	public static final short CREATE = 0;
	public static final short EDIT = 1;
	public static final short CHECK = 2;
	private static int _currentMode;
	private static ArrayList<OnItemSelectedListener> _listenerList;

	/**
	 * Construct a new spinner with the given context's theme.
	 *
	 * @param context The Context the view is running in, through which it can
	 *                access the current theme, resources, etc.
	 */
	public ModeSpinner(Context context) {
		super(context);
		init(context);
	}

	/**
	 * Construct a new spinner with the given context's theme and the supplied attribute set.
	 *
	 * @param context The Context the view is running in, through which it can
	 *                access the current theme, resources, etc.
	 * @param attrs   The attributes of the XML tag that is inflating the view.
	 */
	public ModeSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Construct a new spinner with the given context's theme, the supplied attribute set,
	 * and default style attribute.
	 *
	 * @param context      The Context the view is running in, through which it can
	 *                     access the current theme, resources, etc.
	 * @param attrs        The attributes of the XML tag that is inflating the view.
	 * @param defStyleAttr An attribute in the current theme that contains a
	 *                     reference to a style resource that supplies default values for
	 */
	public ModeSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);

	}

	public static int getMode() {
		return _currentMode;
	}

	public static void addListener(OnItemSelectedListener l) {
		_listenerList.add(l);
	}

	private void init(Context context) {
		SpinnerAdapter adapter = ArrayAdapter.createFromResource(context, R.array.modes, android
				.R.layout.simple_spinner_item);
		setAdapter(adapter);
		setOnItemSelectedListener(this);
		_listenerList = new ArrayList<>();
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
		String result = parent.getItemAtPosition(position).toString();


		if (result.equals(parent.getContext().getString(R.string.mode_create))) {
			Toast.makeText(view.getContext(), "Create mode", Toast.LENGTH_SHORT).show();
			_currentMode = CREATE;

		} else if (result.equals(parent.getContext().getString(R.string.mode_edit))) {
			Toast.makeText(view.getContext(), "Edit mode", Toast.LENGTH_SHORT).show();
			_currentMode = EDIT;

		} else if (result.equals(parent.getContext().getString(R.string.mode_check))) {
			Toast.makeText(view.getContext(), "Check mode", Toast.LENGTH_SHORT).show();
			_currentMode = CHECK;
		}
		for (OnItemSelectedListener listener :
				_listenerList) {
			listener.onItemSelected(parent, view, position, id);
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
	}
}
