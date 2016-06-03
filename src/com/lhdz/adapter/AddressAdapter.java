package com.lhdz.adapter;
/**
 * 地址适配器
 * @author 王哲
 * @date 2015-8-26
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhdz.activity.R;

public class AddressAdapter extends BaseAdapter{

	private Context mContext;
	private List<Map<String, String>> mListData;
	
	private AddrCallBackLister addrCallBackLister = null;
	
	public AddressAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.mListData = new ArrayList<Map<String, String>>();
	}

	@Override
	public int getCount() {
		if (mListData!=null) {
			return mListData.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mListData!=null) {
			return mListData.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}
	
	
	public void setData(List<Map<String, String>> list) {
		this.mListData.clear();
		this.mListData.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void addData(List<Map<String, String>> list) {
		this.mListData.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void clear() {
		this.mListData.clear();
		notifyDataSetChanged();
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Map<String, String> serviceAdd = mListData.get(position);
		if(serviceAdd == null){
			return null;
		}
		
		final ViewHolder viewHolder;
		if (convertView==null) {
			convertView=LayoutInflater.from(mContext).inflate(R.layout.adapter_address, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_uname = (TextView) convertView.findViewById(R.id.tv_uname);
			viewHolder.tv_uphone = (TextView) convertView.findViewById(R.id.tv_uphone);
			viewHolder.tv_uaddress = (TextView) convertView.findViewById(R.id.tv_uaddress);
			viewHolder.lin_address_chose = (LinearLayout) convertView.findViewById(R.id.lin_address_chose);
			viewHolder.iv_address_chose = (ImageView) convertView.findViewById(R.id.iv_address_chose);
			viewHolder.tv_address_chose = (TextView) convertView.findViewById(R.id.tv_address_chose);
			
			viewHolder.lin_edit = (LinearLayout) convertView.findViewById(R.id.lin_edit);//编辑按钮的LinearLayout
			viewHolder.lin_delete = (LinearLayout) convertView.findViewById(R.id.lin_delete);//删除按钮的LinearLayout
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv_uname.setText(serviceAdd.get("objName"));
		viewHolder.tv_uphone.setText(serviceAdd.get("objTel"));
		viewHolder.tv_uaddress.setText(serviceAdd.get("longAddr"));
		
		
		if(Integer.parseInt(serviceAdd.get("selecState")) == 0){
			viewHolder.iv_address_chose.setBackgroundResource(R.drawable.select_gray);
			viewHolder.tv_address_chose.setText("设为默认");
		}
		if (Integer.parseInt(serviceAdd.get("selecState")) == 1) {
			viewHolder.iv_address_chose.setBackgroundResource(R.drawable.select_green);
			viewHolder.tv_address_chose.setText("默认地址");
		}
		
		//为设置默认按钮添加回调点击事件
		viewHolder.lin_address_chose.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addrCallBackLister.onClickDefault(serviceAdd);
			}
		});
		
		
		//为编辑按钮添加回调点击事件
		viewHolder.lin_edit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addrCallBackLister.onClickEdit(serviceAdd);
			}
		});
		
		
		//为删除按钮添加回调点击事件
		viewHolder.lin_delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addrCallBackLister.onClickDelete(serviceAdd);
			}
		});
		
		return convertView;
	}
	
	
	
	public interface AddrCallBackLister{
		public void onClickDefault(Map<String, String> serviceAdd);
		public void onClickEdit(Map<String, String> serviceAdd);
		public void onClickDelete(Map<String, String> serviceAdd);
		
	}
	
	
	
	public void setAddrCallBackLister(AddrCallBackLister addrCallBackLister){
		this.addrCallBackLister = addrCallBackLister;
	}
	
	
	
	private class ViewHolder{
		TextView tv_uname = null;
		TextView tv_uphone = null;
		TextView tv_uaddress = null;
		LinearLayout lin_address_chose = null;
		ImageView iv_address_chose = null;
		TextView tv_address_chose = null;
		LinearLayout lin_edit = null;
		LinearLayout lin_delete = null;
	}

}
