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
public class AdapterShotcut1Button extends BaseAdapter {
	private List<Map<String, String>> shotCut1Data;
	private Context context;
	ImageView shotButton;
	TextView shotText;

	public AdapterShotcut1Button(Context context,
			List<Map<String, String>> shotCut1Data) {
		this.shotCut1Data = shotCut1Data;
		this.context = context;
	}
	
	
	public void setData(List<Map<String, String>> shotCut1Data){
		this.shotCut1Data = shotCut1Data;
		notifyDataSetChanged();
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
	
		vh.holder_shotText.setText(shotCut1Data.get(position).get("SonName")
				.toString());
		int sonId = Integer.parseInt(shotCut1Data.get(position).get("Sonid"));
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
		return shotCut1Data.size();
	}

	static class ViewHolder {
		public ImageView holder_shotButton;
		public TextView holder_shotText;
	}
}
