package com.lhdz.adapter;

import java.util.ArrayList;

import com.lhdz.activity.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class JoinAdapter extends BaseAdapter {
	Context context;
	ArrayList<Integer> mList;
	
	public JoinAdapter(Context context, ArrayList<Integer> mList) {
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
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_join, null);
		}
		ImageView ivBaby=(ImageView) convertView.findViewById(R.id.iv_baby_img);
		ivBaby.setImageResource(mList.get(position));
		
		return convertView;
	}
}
