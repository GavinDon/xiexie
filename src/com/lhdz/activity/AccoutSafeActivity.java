package com.lhdz.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lhdz.adapter.AccoutsafeAdapter;
import com.lhdz.entity.UserItem;

/**
 * 账号安全页面
 * @author wangf
 *
 */
public class AccoutSafeActivity extends BaseActivity implements OnClickListener{
	
	public static AccoutSafeActivity instanceAccoutSafeActivity = null;
	
	private TextView title;
	private ListView mListView;
	private List<UserItem> itemData = new ArrayList<UserItem>();;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_accout_safe);
		
		instanceAccoutSafeActivity = this;
		
		backArrow();
		initView();
		setItemData();
		
		mListView.setAdapter(new AccoutsafeAdapter(this, itemData));
		
	}

	
	/**
	 * 初始化页面view
	 */
	private void initView() {
		title = (TextView)findViewById(R.id.tv_title);
		title.setText("帐户安全");
		mListView = (ListView)findViewById(R.id.lv_accout_safe);
	}


	@Override
	public void onClick(View v) {

	}


	/**
	 * 为view设置数据
	 */
	private void setItemData() {
		itemData.add(new UserItem(false, R.drawable.icon_28_key, "修改密码", ""));
//		itemData.add(new UserItem(true, R.drawable.icon_28_account, "帐号","530893850"));
//		itemData.add(new UserItem(true, R.drawable.icon_28_iphone, "手机", "13720417643"));
//		itemData.add(new UserItem(false, R.drawable.icon_28_email, "邮箱", "fengyun@163.com"));
//		itemData.add(new UserItem(false, R.drawable.icon_28_lock, "帐号保护", ""));
//		itemData.add(new UserItem(false, R.drawable.icon_28_saft02, "安全中心", ""));
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
					AccoutSafeActivity.this.finish();

					break;
				}
			}
		});
	}

}
