package com.lhdz.adapter;

/**
 * 预约下单时服务列表的adapter 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lhdz.activity.R;
import com.lhdz.entity.ServiceListInfoEntity;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;

public class PopAppointOrderAdapter extends BaseAdapter{

	private Context mContext;
//	private List<String[]> mListData;
	private List<ServiceListInfoEntity> listInfoEntities;
	
	private ServiceListCallBackLister serviceListCallBackLister = null;
	
//	private Map<Integer, Integer> serviceNumMap;//所有服务的数量
	
	
	public PopAppointOrderAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.listInfoEntities = new ArrayList<ServiceListInfoEntity>();
//		this.mListData = new ArrayList<String[]>();
//		this.serviceNumMap = new HashMap<Integer, Integer>();
	}

	@Override
	public int getCount() {
		if (listInfoEntities!=null) {
			return listInfoEntities.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (listInfoEntities!=null) {
			return listInfoEntities.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
	
	
	public void setData(List<ServiceListInfoEntity> list) {
		this.listInfoEntities.clear();
		this.listInfoEntities.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void addData(List<ServiceListInfoEntity> list) {
		this.listInfoEntities.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void clear() {
		this.listInfoEntities.clear();
		notifyDataSetChanged();
	}
	
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final ServiceListInfoEntity infoEntities = (ServiceListInfoEntity) listInfoEntities.get(position);
		if(infoEntities == null){
			return null;
		}
		
		final ViewHolder viewHolder;
		if (convertView==null) {
			convertView=LayoutInflater.from(mContext).inflate(R.layout.item_service_list, null);
			viewHolder = new ViewHolder();
			
			viewHolder.service_list_name = (TextView) convertView.findViewById(R.id.service_list_name);//服务名称
			viewHolder.service_list_price = (TextView) convertView.findViewById(R.id.service_list_price);//单价
			viewHolder.service_list_unit =(TextView) convertView.findViewById(R.id.service_list_unit);//单价的单位
			viewHolder.service_list_minus = (TextView) convertView.findViewById(R.id.service_list_minus);//数量减
			viewHolder.service_list_num = (EditText) convertView.findViewById(R.id.service_list_num);//数量
			viewHolder.service_list_add = (TextView) convertView.findViewById(R.id.service_list_add);//数量加
			viewHolder.service_list_numunit = (TextView) convertView.findViewById(R.id.service_list_numunit);//数量的单位
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		
		viewHolder.service_list_name.setText(infoEntities.getServiceName());
		viewHolder.service_list_price.setText("¥"+infoEntities.getServicePrice());
		viewHolder.service_list_unit.setText("/"+infoEntities.getServiceUnit());
		viewHolder.service_list_num.setText(infoEntities.getServiceNum()+"");
		viewHolder.service_list_num.setSelection(viewHolder.service_list_num.getText().length());//设置输入光标在文字后面
		viewHolder.service_list_numunit.setText(infoEntities.getServiceUnit());
		
		viewHolder.service_list_minus.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(serviceListCallBackLister != null){
					serviceListCallBackLister.onClickMinus(position,infoEntities);
				}
			}
		});
		
		viewHolder.service_list_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(serviceListCallBackLister != null){
					serviceListCallBackLister.onClickAdd(position,infoEntities);
				}
			}
		});
		
		viewHolder.service_list_num.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
//				if(serviceListCallBackLister != null){
//					serviceListCallBackLister.onTextChanged(s, position, infoEntities);
//				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if(s.toString().length() > 1){
					String startStr = s.subSequence(0, 1).toString();
					if("0".equals(startStr)){
						s.delete(0, 1);
					}
				}
				
				if(serviceListCallBackLister != null){
					serviceListCallBackLister.afterTextChanged(s, position, infoEntities);
				}
				
			}
		});
		
		return convertView;
	}
	
	
//	public void putServiceNumMapByKey(int key,int value){
//		if(serviceNumMap != null){
//			serviceNumMap.put(key, value);
//		}
//	}
//	
//	
//	public int getServiceNumMapByKey(int key){
//		if(serviceNumMap != null){
//			if(serviceNumMap.get(key) == null){
//				return 0;
//			}else{
//				return serviceNumMap.get(key);
//			}
//		}
//		return 0;
//	}
	
	
//	public Map<Integer, Integer> getServiceMap(){
//		return serviceNumMap;
//	}
	
	
	
	public interface ServiceListCallBackLister{
		public void onClickMinus(int position, ServiceListInfoEntity entity);
		public void onClickAdd(int position, ServiceListInfoEntity entity);
		public void afterTextChanged(CharSequence s,int position, ServiceListInfoEntity entity);
	}
	
	
	public void setServiceListCallBackLister(ServiceListCallBackLister callBackLister){
		this.serviceListCallBackLister = callBackLister;
	}
	
	
	private class ViewHolder{
		TextView service_list_name = null;
		TextView service_list_price = null;
		TextView service_list_unit = null;
		TextView service_list_minus = null;
		EditText service_list_num = null;
		TextView service_list_add = null;
		TextView service_list_numunit = null;
	}

}
