package com.lhdz.adapter;

import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;
import com.lhdz.activity.SettingActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {
	private Context context;
	private List<Map<String, Object>> data;

	public SettingAdapter(SettingActivity settingActivity,
			List<Map<String, Object>> data) {
		this.context=settingActivity;
		this.data=data;
	}

	@Override
	public int getCount() {

		return data.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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


