package com.lhdz.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderInfo_Pro;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class StarCompanaydAdapter extends BaseAdapter {
	Context context;
	List<Map<String, String>> starCompanyDataList;
	List<Map<String, String>> appHomeDataList = null;//首页快捷下单数据
	ImageLoader iLoader=ImageLoader.getInstance();
//	String netUrl="http://imxiexie.com/Public/images/logo.png";
	String netUrl="http://img3.imgtn.bdimg.com/it/u=3292087472,1976560985&fm=21&gp=0.jpg";
	

	public StarCompanaydAdapter(Context context,List<Map<String, String>> appHomeDataList) {
		super();
		this.context = context;
		this.appHomeDataList = appHomeDataList;
		this.starCompanyDataList = new ArrayList<Map<String,String>>();
	}
	
	
	@Override
	public int getCount() {
		if (starCompanyDataList!=null) {
			return starCompanyDataList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (starCompanyDataList!=null) {
			return starCompanyDataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public void setData(List<Map<String, String>> list) {
		this.starCompanyDataList.clear();
		this.starCompanyDataList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void addData(List<Map<String, String>> list) {
		this.starCompanyDataList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void clear() {
		this.starCompanyDataList.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Map<String, String> starCompany = starCompanyDataList.get(position);
		if(starCompany == null){
			return null;
		}
		
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_starcomponay, null);
			holder = new ViewHolder();
			holder.vhImage = (ImageView) convertView.findViewById(R.id.iv_);
			holder.vhName = (TextView) convertView
					.findViewById(R.id.tv_comname);
			holder.vhServer = (TextView) convertView
					.findViewById(R.id.tv_server);
			holder.star=(RatingBar) convertView.findViewById(R.id.statrbar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		List<Map<String, String>> serviceTypeList = UniversalUtils.getServiceType(appHomeDataList, starCompany.get("szServiceInfo"));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < serviceTypeList.size(); i++) {
			builder.append(serviceTypeList.get(i).get("code")+" ");
		}
		
		
		holder.vhName.setText(starCompany.get("szName").toString());
		iLoader.displayImage(Define.URL_COMPANY_IMAGE + starCompany.get("szCompanyUrl"), holder.vhImage);
//		iLoader.displayImage(netUrl, holder.vhImage);
		holder.vhServer.setText(builder.toString());
		holder.star.setRating(UniversalUtils.processRatingLevel(starCompany.get("iStarLevel")));
		return convertView;
	}

}
