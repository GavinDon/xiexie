package com.lhdz.adapter;

import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MineAdapter extends BaseAdapter {
	List<Map<String, Object>> data=null;
	Context context;

	public MineAdapter(FragmentActivity activity, List<Map<String, Object>> data) {
		this.data = data;
		this.context = activity;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_mine, null);
			holder = new ViewHolder();
			holder.mineVhIcon = (ImageView) convertView
					.findViewById(R.id.minelv_iv);
			holder.mineVhItem = (TextView) convertView
					.findViewById(R.id.minelv_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		 holder.mineVhIcon.setImageResource((Integer) data.get(position).get(
		 "icons"));
		holder.mineVhItem.setText(data.get(position).get("item").toString());
		return convertView;
	}

}
