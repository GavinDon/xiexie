package com.lhdz.adapter;

import java.util.HashMap;
import java.util.List;

import com.lhdz.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class OutoftimeAdapter extends BaseAdapter {
	Context context;
	List<HashMap<String, Object>> listImageItem;

	public OutoftimeAdapter(FragmentActivity activity,
			List<HashMap<String, Object>> listImageItem) {
		this.context = activity;
		this.listImageItem = listImageItem;
	}

	@Override
	public int getCount() {
		return listImageItem.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		int color[] = new int[] { R.color.color1, R.color.color2,
				R.color.color3, R.color.color4 };
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_outoftime, null);
			holder = new ViewHolder();
			holder.holderImage = (ImageView) convertView
					.findViewById(R.id.out_iv);
			holder.holderTitle = (TextView) convertView
					.findViewById(R.id.out_title);
			holder.holderItem = (TextView) convertView
					.findViewById(R.id.out_item);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.holderImage.setImageResource((Integer) listImageItem.get(
				position).get("ItemImage"));
		holder.holderTitle.setText(listImageItem.get(position).get("title")
				.toString());
		// holder.holderTitle.setTextColor(R.color.black);
		holder.holderTitle.setTextColor(context.getResources().getColor(
				color[position]));

		holder.holderItem.setText(listImageItem.get(position).get("item")
				.toString());
		return convertView;
	}

	public static final class ViewHolder {
		TextView holderTitle, holderItem;
		ImageView holderImage;

	}

}
