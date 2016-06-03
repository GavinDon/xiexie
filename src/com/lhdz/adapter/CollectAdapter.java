package com.lhdz.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.R;
import com.lhdz.entity.SortModel;
import com.lhdz.util.Define;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class CollectAdapter extends BaseAdapter{
	Context context;
	List<Map<String, String>> collectCompanyDataList;
	ImageLoader loader = ImageLoader.getInstance();

	public CollectAdapter(Context context) {
		this.context = context;
		this.collectCompanyDataList = new ArrayList<Map<String,String>>();
	}

	@Override
	public int getCount() {
		if (collectCompanyDataList!=null) {
			return collectCompanyDataList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (collectCompanyDataList!=null) {
			return collectCompanyDataList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public void setData(List<Map<String, String>> list) {
		this.collectCompanyDataList.clear();
		this.collectCompanyDataList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void addData(List<Map<String, String>> list) {
		this.collectCompanyDataList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void clear() {
		this.collectCompanyDataList.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Map<String, String> collectCompany = collectCompanyDataList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_collect, null);
			holder = new ViewHolder();
			holder.joinVhName = (TextView) convertView.findViewById(R.id.tv_comname);
			holder.joinVhImage = (ImageView) convertView.findViewById(R.id.iv_collectadapter);
			holder.star = (RatingBar) convertView.findViewById(R.id.collect_rating);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.joinVhName.setText(collectCompany.get("szName"));
		holder.star.setRating(UniversalUtils.processRatingLevel(collectCompany.get("iStarLevel")));
		loader.displayImage(Define.URL_COMPANY_IMAGE + collectCompany.get("szCompanyUrl"), holder.joinVhImage);
//		holder.joinVhImage.setImageResource(Integer.parseInt(collectCompany.get("icons")));
		return convertView;
	}
}
