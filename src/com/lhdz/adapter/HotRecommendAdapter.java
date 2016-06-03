package com.lhdz.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;
import com.lhdz.util.Define;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class HotRecommendAdapter extends BaseAdapter {
	Context context;
	List<Map<String, String>> starCompanyDataList;
	List<Map<String, String>> appHomeDataList = null;//首页快捷下单数据
	ImageLoader loader = ImageLoader.getInstance();

	public HotRecommendAdapter(FragmentActivity activity,List<Map<String, String>> appHomeDataList) {
		this.context = activity;
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
		
		ViewHolder holder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_starcomponay, null);
			holder=new ViewHolder();
			holder.image=(ImageView) convertView.findViewById(R.id.iv_);
			holder.state=(ImageView) convertView.findViewById(R.id.iv_state);
			holder.star= (RatingBar) convertView.findViewById(R.id.statrbar);
			holder.server=(TextView) convertView.findViewById(R.id.tv_server);
			holder.companyName=(TextView) convertView.findViewById(R.id.tv_comname);
//			holder.statement=(TextView) convertView.findViewById(R.id.tv_statement);
//			holder.price=(TextView) convertView.findViewById(R.id.homepager3_price);
//			holder.oldPrice=(TextView) convertView.findViewById(R.id.homepager3_oldprice);
//			holder.haveSale=(TextView) convertView.findViewById(R.id.homepager3_havesale);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		List<Map<String, String>> serviceTypeList = UniversalUtils.getServiceType(appHomeDataList, starCompany.get("szServiceInfo"));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < serviceTypeList.size(); i++) {
			builder.append(serviceTypeList.get(i).get("code")+" ");
		}
		
//		holder.image.setImageResource((Integer) data.get(position).get("ItemImage"));
		holder.companyName.setText(starCompany.get("szName").toString());
		holder.server.setText(builder.toString());
		holder.star.setRating(UniversalUtils.processRatingLevel(starCompany.get("iStarLevel")));
		loader.displayImage(Define.URL_COMPANY_IMAGE + starCompany.get("szCompanyUrl"), holder.image);
		
//		holder.price.setText(data.get(position).get("price").toString());
//		holder.oldPrice.setText(data.get(position).get("oldprice").toString());
//		holder.haveSale.setText(data.get(position).get("havesale").toString());
//		holder.oldPrice.getPaint().setFlags(0x10); //中间横线
		return convertView;
	}
	private static final class ViewHolder{
		public TextView statement,companyName,price,oldPrice,haveSale,server;
		public ImageView image,state;
		public RatingBar star;
		
		
	}

}
