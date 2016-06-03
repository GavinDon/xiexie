package com.lhdz.adapter;

import java.util.ArrayList;

import com.lhdz.activity.ComplaintsActivity;
import com.lhdz.activity.IncludeBabyActivity;
import com.lhdz.activity.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class StarbabyAdapter extends BaseAdapter {
	Context context;
	ArrayList<String> mList;
	
	public StarbabyAdapter(Context context, ArrayList<String> mList) {
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
					R.layout.adapter_starbaby, null);
		}
		TextView tvName=(TextView) convertView.findViewById(R.id.tv_babyname);
		tvName.setText(mList.get(position));
		//对投票按钮进行跳转
		TextView complaints=(TextView) convertView.findViewById(R.id.tv_vote);
				complaints.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent=new Intent(context,IncludeBabyActivity.class);
						context.startActivity(intent);
					}
				});
		return convertView;
	}
}
