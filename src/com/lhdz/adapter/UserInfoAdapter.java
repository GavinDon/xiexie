package com.lhdz.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;
import com.lhdz.activity.UserInfoActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UserInfoAdapter extends BaseAdapter {
	Context context;
	List<Map<String, Object>> data =null;

	public UserInfoAdapter(UserInfoActivity userInfoActivity) {
		this.context = userInfoActivity;
		data = new ArrayList<Map<String, Object>>();
	}

	public void setData(List<Map<String, Object>> list) {
		if (data != null) {
			this.data.clear();
			this.data.addAll(list);
			notifyDataSetChanged();
		}
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
		UserinfoHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_userinfo, null);
			holder = new UserinfoHolder();
			holder.vhTag = (TextView) convertView
					.findViewById(R.id.tv_useraccout);
			holder.vhContent = (TextView) convertView
					.findViewById(R.id.userinfo_adapter_tv);
			holder.userinfo_arrow = (ImageView) convertView.findViewById(R.id.userinfo_arrow);
			convertView.setTag(holder);
		} else {
			holder = (UserinfoHolder) convertView.getTag();
		}
		
		holder.vhTag.setText(data.get(position).get("item").toString().trim());
		holder.vhContent.setText(data.get(position).get("info").toString().trim());
		
		if(position == 0){
			holder.userinfo_arrow.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	public static final class UserinfoHolder {
		public TextView vhTag, vhContent;
		public ImageView userinfo_arrow;
	}

}
