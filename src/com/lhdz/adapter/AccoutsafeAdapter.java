package com.lhdz.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhdz.activity.HelpWebActivity;
import com.lhdz.activity.LoginActivity;
import com.lhdz.activity.ModifyPwdActivity;
import com.lhdz.activity.R;
import com.lhdz.entity.UserItem;
import com.lhdz.publicMsg.MyApplication;

public class AccoutsafeAdapter extends BaseAdapter {
	private Context context;
	List<UserItem> item;

	public AccoutsafeAdapter(Context context, List<UserItem> item) {
		this.context = context;
		this.item = item;
	}

	@Override
	public int getCount() {
		return item.size();
	}

	@Override
	public UserItem getItem(int position) {
		return item.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		UserItem items = getItem(position);
		
		ViewHolder vholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.adapter_accout_safe, null);
			vholder = new ViewHolder();
			vholder.ivImg = (ImageView) convertView.findViewById(R.id.accoutsafe_iv_icon);
			vholder.tvItem = (TextView) convertView.findViewById(R.id.accoutsafe_tv_item);
			vholder.tvInfo = (TextView) convertView.findViewById(R.id.accoutsafe_tv_info);
			vholder.ll_divider=(LinearLayout) convertView.findViewById(R.id.accountsafe_ll_divider);
			vholder.accountsafe_rl_all = (RelativeLayout) convertView.findViewById(R.id.accountsafe_rl_all);
			convertView.setTag(vholder);

		} else {
			vholder = (ViewHolder) convertView.getTag();
		}
		
		vholder.ivImg.setImageResource(items.getLeftImg());
		vholder.tvItem.setText(items.getSubhead());
		vholder.tvInfo.setText(items.getSubItem());
		vholder.ll_divider.setVisibility(items.isShowTopDivider()?View.VISIBLE:View.GONE);
		
		vholder.accountsafe_rl_all.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				switch (position) {
				case 0:
					if(MyApplication.loginState){
						intent.setClass(context, ModifyPwdActivity.class);
						context.startActivity(intent);
					}else{
						intent.setClass(context, LoginActivity.class);
						context.startActivity(intent);
					}
					break;

				default:
					break;
				}

			}
		});
		
		return convertView;
	}

	
	private class ViewHolder {
		private ImageView ivImg;
		private TextView tvItem;
		private TextView tvInfo;
		private LinearLayout ll_divider;
		private RelativeLayout accountsafe_rl_all;
	}

}
