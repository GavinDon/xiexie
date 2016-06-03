package com.lhdz.adapter;

import java.util.List;

import com.lhdz.activity.HelpWebActivity;
import com.lhdz.activity.R;
import com.lhdz.entity.UserItem;
import com.lhdz.util.Define;
import com.lhdz.util.UniversalUtils;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class HelpAdapter extends BaseAdapter {

	private Context context;
	private List<UserItem> datas;

	public HelpAdapter(Context context, List<UserItem> datas) {
		this.context = context;
		this.datas = datas;
	}

	@Override
	public int getCount() {
		return datas.size();
	}

	@Override
	public UserItem getItem(int position) {
		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.item_user, null);
			holder.v_divider = convertView.findViewById(R.id.v_divider);
			holder.ll_content = convertView.findViewById(R.id.ll_content);
			holder.iv_left = (ImageView) convertView.findViewById(R.id.iv_left);
			holder.tv_subHead = (TextView) convertView.findViewById(R.id.subhead);
			holder.tv_subItem = (TextView) convertView.findViewById(R.id.subitem);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// set data
		UserItem item = getItem(position);
		holder.iv_left.setImageResource(item.getLeftImg());
		holder.tv_subHead.setText(item.getSubhead());
		if(UniversalUtils.isStringEmpty(item.getSubItem())){
			holder.tv_subItem.setVisibility(View.GONE);
		}
		holder.tv_subItem.setText(item.getSubItem());

		holder.v_divider.setVisibility(item.isShowTopDivider() ? 
				View.VISIBLE : View.GONE);
		
		holder.ll_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(context, HelpWebActivity.class);
				switch (position) {
				case 0:
					intent.putExtra("title", "问题反馈");
					intent.putExtra("url", Define.URL_QUESTION_WAY);
					context.startActivity(intent);
					break;
				case 1:
					intent.putExtra("title", "账户问题");
					intent.putExtra("url", Define.URL_QUESTION_ACCOUNT);
					context.startActivity(intent);
					break;
				case 2:
					intent.putExtra("title", "订单问题");
					intent.putExtra("url", Define.URL_QUESTION_ORDER);
					context.startActivity(intent);
					break;
				case 3:
					intent.putExtra("title", "支付问题");
					intent.putExtra("url", Define.URL_QUESTION_PAY);
					context.startActivity(intent);
					break;
				case 4:
					intent.putExtra("title", "活动积分");
					intent.putExtra("url", Define.URL_QUESTION_POINT);
					context.startActivity(intent);
					break;

				default:
					break;
				}
				
				
			}
		});
		
		return convertView;
	}

	public static class ViewHolder{
		public View v_divider;
		public View ll_content;
		public ImageView iv_left;
		public TextView tv_subHead;
		public TextView tv_subItem;
	}


}

