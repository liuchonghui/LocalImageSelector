package tool.imagedownloader.android;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Change appearance from list-like to grid-like but still use ListView.
 * 
 * @author liu_chonghui
 * 
 */
public class ConvertListToGridAdapter extends BaseAdapter implements
		ListAdapter, OnItemClickListener {

	public static enum COLUMN {
		SIZE_2, SIZE_3,
	}

	private final DataSetObserver dataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			updateSessionCache();
		}

		@Override
		public void onInvalidated() {
			super.onInvalidated();
			updateSessionCache();
		};
	};

	protected Context context;

	protected Context getContext() {
		return this.context;
	}

	protected ListAdapter linkedAdapter;
	protected COLUMN column;
	protected int columnNum;

	protected Map<Integer, List<Integer>> linkedPositions;
	protected Map<Integer, List<Object>> linkedObjects;

	private OnItemClickListener linkedListener;

	@SuppressLint("UseSparseArrays")
	public ConvertListToGridAdapter(final Context context,
									final COLUMN columnSize, final ListAdapter linkedAdapter) {
		this.linkedAdapter = linkedAdapter;
		this.context = context;
		this.column = columnSize;
		if (null == this.column) {
			throw new IllegalArgumentException();
		} else {
			this.columnNum = this.column.ordinal() + 2;
		}
		linkedPositions = new HashMap<Integer, List<Integer>>();
		linkedObjects = new HashMap<Integer, List<Object>>();
		linkedAdapter.registerDataSetObserver(dataSetObserver);
		updateSessionCache();
	}

	protected synchronized void updateSessionCache() {
		int count = 0;
		int factor = 0;
		int currentPosition = 0;
		linkedPositions.clear();
		linkedObjects.clear();
		count = linkedAdapter.getCount();
		count -= count % columnNum;
		for (int i = 0; i < count; i++) {
			List<Integer> positionArray = linkedPositions.get(currentPosition);
			if (positionArray == null) {
				positionArray = new ArrayList<Integer>();
			}
			positionArray.add(i);
			linkedPositions.put(currentPosition, positionArray);

			List<Object> objectArray = linkedObjects.get(currentPosition);
			if (objectArray == null) {
				objectArray = new ArrayList<Object>();
			}
			objectArray.add(linkedAdapter.getItem(i));
			linkedObjects.put(currentPosition, objectArray);

			if (columnNum == ++factor) {
				factor = 0;
				currentPosition++;
			}
		}
		currentPosition = 0;
	}

	@Override
	public synchronized int getCount() {
		return linkedPositions.keySet().size();
	}

	@Override
	public synchronized Object getItem(final int position) {
		return linkedObjects.get(position);
	}

	public long getItemId(final int position) {
		return linkedObjects.get(position).hashCode();
	}

	@Override
	public int getItemViewType(final int position) {
		return linkedAdapter.getItemViewType(position);
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView,
			final ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();

			convertView = new RelativeLayout(getContext());
			convertView.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			List<View> views = new ArrayList<View>();
			for (int i = 0; i < columnNum; i++) {
				views.add(linkedAdapter.getView(0, null, null));
			}
			for (int i = 0; i < columnNum; i++) {
				((RelativeLayout) convertView).addView(views.get(i));
			}
			holder.left = getLeft(this.column, views);
			holder.middle = getMiddle(this.column, views);
			holder.right = getRight(this.column, views);

			// convertView = (RelativeLayout) LayoutInflater.from(getContext())
			// .inflate(R.layout.relative_item, null);
			// if (COLUMN.SIZE_2 == this.column) {
			// holder.left = linkedAdapter.getView(0, null, null);
			// RelativeLayout.LayoutParams leftParam = new
			// RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// leftParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// leftParam.setMargins(leftMargin, 0, 0, 0);
			// ((RelativeLayout) convertView).addView(holder.left, 0,
			// leftParam);
			//
			// holder.right = linkedAdapter.getView(0, null, null);
			// RelativeLayout.LayoutParams rightParam = new
			// RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// rightParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// rightParam.setMargins(0, 0, rightMargin, 0);
			// ((RelativeLayout) convertView).addView(holder.right, 1,
			// rightParam);
			// } else if (COLUMN.SIZE_3 == this.column) {
			// holder.left = linkedAdapter.getView(0, null, null);
			// RelativeLayout.LayoutParams leftParam = new
			// RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// leftParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			// leftParam.setMargins(leftMargin, 0, 0, 0);
			// ((RelativeLayout) convertView).addView(holder.left, 0,
			// leftParam);
			//
			// holder.middle = linkedAdapter.getView(0, null, null);
			// RelativeLayout.LayoutParams midParam = new
			// RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// midParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
			// midParam.setMargins(0, 0, 0, 0);
			// ((RelativeLayout) convertView).addView(holder.middle, 1,
			// midParam);
			//
			// holder.right = linkedAdapter.getView(0, null, null);
			// RelativeLayout.LayoutParams rightParam = new
			// RelativeLayout.LayoutParams(
			// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			// rightParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			// rightParam.setMargins(0, 0, rightMargin, 0);
			// ((RelativeLayout) convertView).addView(holder.right, 2,
			// rightParam);
			// }

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		adjustConvertView(position, convertView, holder, getItem(position),
				this.column);

		return convertView;
	}

	protected void adjustConvertView(int position, View convertView,
			ViewHolder holder, Object object, COLUMN column) {
		List<Integer> positions = linkedPositions.get(position);
		if (COLUMN.SIZE_2 == column) {
			linkedAdapter.getView(positions.get(0), holder.left, null);
			linkedAdapter.getView(positions.get(1), holder.right, null);
		} else if (COLUMN.SIZE_3 == column) {
			linkedAdapter.getView(positions.get(0), holder.left, null);
			linkedAdapter.getView(positions.get(1), holder.middle, null);
			linkedAdapter.getView(positions.get(2), holder.right, null);
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public int getViewTypeCount() {
		return linkedAdapter.getViewTypeCount();
	}

	@Override
	public boolean hasStableIds() {
		return linkedAdapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return linkedAdapter.isEmpty();
	}

	@Override
	public void registerDataSetObserver(final DataSetObserver observer) {
		try {
			linkedAdapter.unregisterDataSetObserver(dataSetObserver);
		} catch (Exception e) {
		}
		try {
			linkedAdapter.registerDataSetObserver(observer);
		} catch (Exception e) {
		}
		try {
			linkedAdapter.registerDataSetObserver(dataSetObserver);
		} catch (Exception e) {
		}
	}

	@Override
	public void unregisterDataSetObserver(final DataSetObserver observer) {
		try {
			linkedAdapter.unregisterDataSetObserver(observer);
		} catch (Exception e) {
		}
	}

	@Override
	public boolean areAllItemsEnabled() {
		return linkedAdapter.areAllItemsEnabled();
	}

	@Override
	public boolean isEnabled(final int position) {
		return linkedAdapter.isEnabled(position);
	}

	protected void sectionClicked(final String section) {
		// do nothing
	}

	@Override
	public void onItemClick(final AdapterView<?> parent, final View view,
			final int position, final long id) {
		linkedListener.onItemClick(parent, view, position, id);
	}

	public void setOnItemClickListener(final OnItemClickListener linkedListener) {
		this.linkedListener = linkedListener;
	}

	protected class ViewHolder {
		View left;
		View middle;
		View right;
	}

	protected View getLeft(COLUMN column, List<View> views) {
		View left = views.get(0);
		((RelativeLayout.LayoutParams) left.getLayoutParams())
				.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		((RelativeLayout.LayoutParams) left.getLayoutParams()).setMargins(
				leftMargin, 0, 0, 0);
		return left;
	}

	protected View getMiddle(COLUMN column, List<View> views) {
		if (COLUMN.SIZE_2 == column) {
			return null;
		}
		View middle = views.get(1);
		((RelativeLayout.LayoutParams) middle.getLayoutParams())
				.addRule(RelativeLayout.CENTER_HORIZONTAL);
		((RelativeLayout.LayoutParams) middle.getLayoutParams()).setMargins(0,
				0, 0, 0);
		return middle;
	}

	protected View getRight(COLUMN column, List<View> views) {
		View right = null;
		if (COLUMN.SIZE_2 == column) {
			right = views.get(1);
		} else if (COLUMN.SIZE_3 == column) {
			right = views.get(2);
		} else {
			throw new IllegalArgumentException();
		}
		((RelativeLayout.LayoutParams) right.getLayoutParams())
				.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		((RelativeLayout.LayoutParams) right.getLayoutParams()).setMargins(0,
				0, rightMargin, 0);
		return right;
	}

	int leftMargin = 0;
	int rightMargin = 0;

	public void setLeftMargin(int pixValue) {
		leftMargin = pixValue;
	}

	public void setRightMargin(int pixValue) {
		rightMargin = pixValue;
	}
}