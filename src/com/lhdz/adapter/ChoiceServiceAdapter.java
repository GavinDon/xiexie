package com.lhdz.adapter;


/**
 * 选择服务适配器
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhdz.activity.R;
import com.lhdz.activity.RaceDetailActivity;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ChoiceServiceAdapter extends BaseAdapter{
	Context mContext;
	private List<HsRaceCompanyInfo_Pro> raceCompanyList = null;
	
	private int uOrderID;//订单号
	private String szOrderValue;//订单号
	private String szOrderStateName;//订单状态
	ImageLoader imageLoader = ImageLoader.getInstance();
	
	private ChoiceCompanyCallBack callBack = null;
	
	

	public ChoiceServiceAdapter(Context mContext, int uOrderID, String szOrderValue,String szOrderStateName) {
		super();
		this.mContext = mContext;
		this.uOrderID = uOrderID;
		this.szOrderValue = szOrderValue;
		this.szOrderStateName = szOrderStateName;
		
		this.raceCompanyList = new ArrayList<HsRaceCompanyInfo_Pro>();
	}

	@Override
	public int getCount() {
		if (raceCompanyList!=null) {
			return raceCompanyList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (raceCompanyList!=null) {
			return raceCompanyList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	
	public void setData(List<HsRaceCompanyInfo_Pro> list) {
		this.raceCompanyList.clear();
		this.raceCompanyList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void addData(List<HsRaceCompanyInfo_Pro> list) {
		this.raceCompanyList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void clear() {
		this.raceCompanyList.clear();
		notifyDataSetChanged();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final HsRaceCompanyInfo_Pro raceCompanyInfo = raceCompanyList.get(position);
		
		final ViewHolder viewHolder;
		if (convertView==null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_choice_service, null);
			viewHolder = new ViewHolder();
			viewHolder.companyPic = (ImageView) convertView.findViewById(R.id.iv_);
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			viewHolder.race_rating = (RatingBar) convertView.findViewById(R.id.race_rating);
			viewHolder.race_price = (TextView) convertView.findViewById(R.id.race_price);
			viewHolder.tv_appointorder = (TextView) convertView.findViewById(R.id.tv_appointorder);
			viewHolder.rl_choice_item = (RelativeLayout) convertView.findViewById(R.id.rl_choice_item);
			
			viewHolder.rb_AuthFlag = (RadioButton) convertView.findViewById(R.id.rb_AuthFlag);//身份认证
			viewHolder.rb_Filing = (RadioButton) convertView.findViewById(R.id.rb_Filing);//证件备案
			viewHolder.rb_OffLine = (RadioButton) convertView.findViewById(R.id.rb_OffLine);//线下验证
			viewHolder.rb_Nominate = (RadioButton) convertView.findViewById(R.id.rb_Nominate);//官方推荐
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.tv_appointorder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//预约下单按钮监听--选择该公司
				callBack.choiceCompany(raceCompanyInfo);
			}
		});
		
		
		viewHolder.rl_choice_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//进入抢单详情页面
				Intent intent = new Intent(mContext, RaceDetailActivity.class);
				intent.putExtra("raceCompanyInfo", (Serializable)raceCompanyInfo);//抢单公司数据
				intent.putExtra("uOrderID", uOrderID);//订单id
				intent.putExtra("szOrderValue", szOrderValue);//订单号
				intent.putExtra("isSelectCompany", false);//用于标识是否已经选定服务公司，从此item进入抢单详情时，未选定公司
				mContext.startActivity(intent);
			}
		});
		
		
		viewHolder.tv_name.setText(raceCompanyInfo.getSzName());//公司名称
		viewHolder.race_rating.setRating(UniversalUtils.processRatingLevel(raceCompanyInfo.getIStarLevel()));//星级
		viewHolder.race_price.setText(UniversalUtils.getString2Float(raceCompanyInfo.getSzPayMent())+" 元");//抢单公司要求的预付款金额
		imageLoader.displayImage(Define.URL_COMPANY_IMAGE + raceCompanyInfo.getSzCompanyUrl(), viewHolder.companyPic);
		
		viewHolder.rb_AuthFlag.getBackground().setLevel(raceCompanyInfo.getIAuthFlag());
		viewHolder.rb_Filing.getBackground().setLevel(raceCompanyInfo.getIFiling());
		viewHolder.rb_OffLine.getBackground().setLevel(raceCompanyInfo.getIOffLine());
		viewHolder.rb_Nominate.getBackground().setLevel(raceCompanyInfo.getINominate());
		
		return convertView;
	}
	
	
	public interface ChoiceCompanyCallBack{
		public void choiceCompany(HsRaceCompanyInfo_Pro raceCompanyInfo);
	}
	
	
	public void setChoiceCompanyListener(ChoiceCompanyCallBack callBack){
		this.callBack = callBack;
	}
	
	
	
	private class ViewHolder{
		ImageView companyPic = null;
		TextView tv_name = null;
		RatingBar race_rating = null;
		TextView race_price = null;
		RelativeLayout rl_choice_item = null;
		TextView tv_appointorder = null;
		RadioButton rb_AuthFlag;
		RadioButton rb_Filing;
		RadioButton rb_OffLine;
		RadioButton rb_Nominate;
	}

}
