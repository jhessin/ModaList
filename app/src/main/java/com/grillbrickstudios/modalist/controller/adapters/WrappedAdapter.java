package com.grillbrickstudios.modalist.controller.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.grillbrickstudios.modalist.App;

/**
 * Created by jhess on 12/6/2015 for ModaList.
 * A Cursor adapter wrapped to look like a RecyclerView.Adapter
 */
public class WrappedAdapter extends RecyclerView.Adapter<WrappedAdapter.ViewHolder> {

	SimpleCursorAdapter _cursorAdapter;

	public WrappedAdapter(SimpleCursorAdapter adapter) {
		assert adapter != null;
		_cursorAdapter = adapter;
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = _cursorAdapter.newView(App.getActivityContext(), _cursorAdapter.getCursor(), parent);
		return new ViewHolder(v);
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		_cursorAdapter.getCursor().moveToPosition(position);
		_cursorAdapter.bindView(holder.itemView, App.getActivityContext(), _cursorAdapter.getCursor());
	}

	/**
	 * Returns the total number of items in the data set hold by the adapter.
	 *
	 * @return The total number of items in this adapter.
	 */
	@Override
	public int getItemCount() {
		return _cursorAdapter.getCount();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View itemView) {
			super(itemView);
		}
	}
}
