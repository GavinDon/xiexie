package com.lhdz.adapter;
/**
 * 资讯适配器
 * @author 王哲
 * @date 2015-8-26
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	Context context;
	ArrayList<String> mList;
	
	public NewsAdapter(Context context, ArrayList<String> mList) {
		super();
		this.context = context;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		if (mList!=null) {
			return mList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mList!=null) {
			return mList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_news, null);
		}
		TextView tvName=(TextView) convertView.findViewById(R.id.tv_title_news);
		tvName.setText(mList.get(position));
		return convertView;
	}
}
