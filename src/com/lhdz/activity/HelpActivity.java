package com.lhdz.activity;

import java.util.ArrayList;
import java.util.List;

import com.lhdz.adapter.HelpAdapter;
import com.lhdz.entity.UserItem;
import com.lhdz.wediget.WrapHeightListView;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


/**
 * 帮助反馈
 *
 */
public class HelpActivity extends BaseActivity implements OnClickListener{
	private TextView title;
	private List<UserItem> userItems;
	private WrapHeightListView lv_help;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		initViews();
		listenCenter();
		backArrow();
	}
	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("帮助反馈");
		userItems = new ArrayList<UserItem>();
		lv_help=(WrapHeightListView) findViewById(R.id.lv_help_items);
		setItem();
		HelpAdapter adapter=new HelpAdapter(this, userItems);
		lv_help.setAdapter(adapter);

	}
	
	
	/**
	 * 设置页面控件的数据
	 */
	private void setItem(){
		userItems.add(new UserItem(true, R.drawable.icon_28_help, "问题反馈", ""));
		userItems.add(new UserItem(true, R.drawable.icon_28_wallet, "帐户问题", "帮助指导密码，绑定手机等"));
		userItems.add(new UserItem(false, R.drawable.icon_28_order, "订单问题", "帮助指导下单，抢单，派单等"));
		userItems.add(new UserItem(false, R.drawable.icon_28_fukuan, "支付问题", "帮助指导支付，转账，提现等"));
		userItems.add(new UserItem(false, R.drawable.icon_28_gift, "活动积分", "活动规则，积分问题等"));
		
	}
	
	
	private void listenCenter() {
	}
	
	
	@Override
	public void onClick(View v) {
	}
	
	
	/**
	 * 返回按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					HelpActivity.this.finish();

					break;
				}
			}
		});
	}
	
	
	
}
