package com.lhdz.adapter;

import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSeeCmpServiceInfo_Pro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 预约下单的弹出 
 */
public class AdapterShotcutAppoint extends BaseAdapter {
//	private List<Map<String, String>> shotCut1Data;
	private List<HsUserSeeCmpServiceInfo_Pro> serviceInfoList;
	private Context context;
	ImageView shotButton;
	TextView shotText;

	public AdapterShotcutAppoint(Context context,
			List<HsUserSeeCmpServiceInfo_Pro> serviceInfoList) {
		this.serviceInfoList = serviceInfoList;
		this.context = context;
	}
	
	
	public void setData(List<HsUserSeeCmpServiceInfo_Pro> serviceInfoList){
		this.serviceInfoList = serviceInfoList;
		notifyDataSetChanged();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HsUserSeeCmpServiceInfo_Pro cmpServiceInfo_Pro = serviceInfoList.get(position);
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
	
		vh.holder_shotText.setText(cmpServiceInfo_Pro.getSzServiceName());
		int sonId = cmpServiceInfo_Pro.getIServiceID();
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
		return serviceInfoList.size();
	}

//	 class ViewHolder {
//		public ImageView holder_shotButton;
//		public TextView holder_shotText;
//	}
}
