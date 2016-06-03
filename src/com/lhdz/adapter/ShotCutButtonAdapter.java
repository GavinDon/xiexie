package com.lhdz.adapter;

import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 首页快捷下单按扭适配器
 * 
 * @author ln
 * 
 */
public class ShotCutButtonAdapter extends BaseAdapter {
	private List<Map<String, String>> shotCut2Data;
	private Context context;
	ImageView shotButton;
	TextView shotText;

	public ShotCutButtonAdapter(Context context,
			List<Map<String, String>> shotCut2Data) {
		this.shotCut2Data = shotCut2Data;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_shotcut_button, null);

			vh.holder_shotButton = (ImageView) convertView
					.findViewById(R.id.iv_shotcut);
			vh.holder_shotText = (TextView) convertView
					.findViewById(R.id.tv_shotcut);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}
	
		vh.holder_shotText.setText(shotCut2Data.get(position).get("SonName")
				.toString());
		int sonId = Integer.parseInt(shotCut2Data.get(position).get("Sonid"));
		vh.holder_shotButton.getDrawable().setLevel(sonId);
	
		
		
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return shotCut2Data.size();
	}
	
	
	public void setData(List<Map<String, String>> shotCut2Data){
		this.shotCut2Data = shotCut2Data;
		notifyDataSetChanged();
	}
	

	static class ViewHolder {
		public ImageView holder_shotButton;
		public TextView holder_shotText;
	}
}
