package com.lhdz.activity;

/**
 * 更多分类界面
 * @author 王哲
 * @date 2015-9-5
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lhdz.adapter.MoreAdapter;
import com.lhdz.dao.CoreDbHelper;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.LineGridView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MoreActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {

	private TextView title;
	private LinearLayout ll_more_1, ll_more_2, ll_more_3, ll_more_4, ll_more_5,
			ll_more_6, ll_more_7, ll_more_8, ll_more_9;
	private LineGridView gv1, gv2, gv3, gv4, gv5, gv6, gv7, gv8, gv9;
	private MoreAdapter adapter1, adapter2, adapter3, adapter4, adapter5,
			adapter6, adapter7, adapter8, adapter9;
	Intent intent;
	private List<Map<String, String>> dataList1, dataList2, dataList3,// 存放查询出parentId对应中的所有值
			dataList4, dataList5, dataList6, dataList7, dataList8, dataList9;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more1);
		// 数据初始化
		initData();
		initViews();
		backArrow();
	}

	/**
	 * 初始化页面数据
	 */
	private void initData() {
		String sql1 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "10");
		CoreDbHelper core1 = new CoreDbHelper(this);
		dataList1 = core1.queryCoredata(sql1);

		String sql2 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "20");
		CoreDbHelper core2 = new CoreDbHelper(this);
		dataList2 = core2.queryCoredata(sql2);

		String sql3 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "30");
		CoreDbHelper core3 = new CoreDbHelper(this);
		dataList3 = core3.queryCoredata(sql3);

		String sql4 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "40");
		CoreDbHelper core4 = new CoreDbHelper(this);
		dataList4 = core4.queryCoredata(sql4);

		String sql5 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "50");
		CoreDbHelper core5 = new CoreDbHelper(this);
		dataList5 = core5.queryCoredata(sql5);

		String sql6 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "60");
		CoreDbHelper core6 = new CoreDbHelper(this);
		dataList6 = core6.queryCoredata(sql6);

		String sql7 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "70");
		CoreDbHelper core7 = new CoreDbHelper(this);
		dataList7 = core7.queryCoredata(sql7);

		String sql8 = DbOprationBuilder.queryBuilderby("*", "coreTable",
				"Parentid", "80");
		CoreDbHelper core8 = new CoreDbHelper(this);
		dataList8 = core8.queryCoredata(sql8);
		
		String sql9 = DbOprationBuilder.queryBuilderby("*", "coreTable",  
				"Parentid", "90");
		CoreDbHelper core9 = new CoreDbHelper(this);
		dataList9 = core9.queryCoredata(sql9);

	}

	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("全部分类");
		
		ll_more_1 = (LinearLayout) findViewById(R.id.ll_more_1);
		ll_more_2 = (LinearLayout) findViewById(R.id.ll_more_2);
		ll_more_3 = (LinearLayout) findViewById(R.id.ll_more_3);
		ll_more_4 = (LinearLayout) findViewById(R.id.ll_more_4);
		ll_more_5 = (LinearLayout) findViewById(R.id.ll_more_5);
		ll_more_6 = (LinearLayout) findViewById(R.id.ll_more_6);
		ll_more_7 = (LinearLayout) findViewById(R.id.ll_more_7);
		ll_more_8 = (LinearLayout) findViewById(R.id.ll_more_8);
		ll_more_9 = (LinearLayout) findViewById(R.id.ll_more_9);
		
		ll_more_1.setVisibility(View.GONE);
		ll_more_2.setVisibility(View.GONE);
		ll_more_3.setVisibility(View.GONE);
		ll_more_4.setVisibility(View.GONE);
		ll_more_5.setVisibility(View.GONE);
		ll_more_6.setVisibility(View.GONE);
		ll_more_7.setVisibility(View.GONE);
		ll_more_8.setVisibility(View.GONE);
		ll_more_9.setVisibility(View.GONE);
		
		
		gv1 = (LineGridView) findViewById(R.id.gv1);
		gv2 = (LineGridView) findViewById(R.id.gv2);
		gv3 = (LineGridView) findViewById(R.id.gv3);
		gv4 = (LineGridView) findViewById(R.id.gv4);
		gv5 = (LineGridView) findViewById(R.id.gv5);
		gv6 = (LineGridView) findViewById(R.id.gv6);
		gv7 = (LineGridView) findViewById(R.id.gv7);
		gv8 = (LineGridView) findViewById(R.id.gv8);
		gv9 = (LineGridView) findViewById(R.id.gv9);
		
		gv1.setVisibility(View.GONE);
		gv2.setVisibility(View.GONE);
		gv3.setVisibility(View.GONE);
		gv4.setVisibility(View.GONE);
		gv5.setVisibility(View.GONE);
		gv6.setVisibility(View.GONE);
		gv7.setVisibility(View.GONE);
		gv8.setVisibility(View.GONE);
		gv9.setVisibility(View.GONE);

		setViewData();
		
	}
	
	

	private void setViewData() {
		if(!UniversalUtils.isStringEmpty(dataList1)){
			ll_more_1.setVisibility(View.VISIBLE);
			gv1.setVisibility(View.VISIBLE);
			
			adapter1 = new MoreAdapter(this, dataList1);
			gv1.setAdapter(adapter1);
			gv1.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList2)){
			ll_more_2.setVisibility(View.VISIBLE);
			gv2.setVisibility(View.VISIBLE);
			
			adapter2 = new MoreAdapter(this, dataList2);
			gv2.setAdapter(adapter2);
			gv2.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList3)){
			ll_more_3.setVisibility(View.VISIBLE);
			gv3.setVisibility(View.VISIBLE);
			
			adapter3 = new MoreAdapter(this, dataList3);
			gv3.setAdapter(adapter3);
			gv3.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList4)){
			ll_more_4.setVisibility(View.VISIBLE);
			gv4.setVisibility(View.VISIBLE);
			
			adapter4 = new MoreAdapter(this, dataList4);
			gv4.setAdapter(adapter4);
			gv4.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList5)){
			ll_more_5.setVisibility(View.VISIBLE);
			gv5.setVisibility(View.VISIBLE);
			
			adapter5 = new MoreAdapter(this, dataList5);
			gv5.setAdapter(adapter5);
			gv5.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList6)){
			ll_more_6.setVisibility(View.VISIBLE);
			gv6.setVisibility(View.VISIBLE);
			
			adapter6 = new MoreAdapter(this, dataList6);
			gv6.setAdapter(adapter6);
			gv6.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList7)){
			ll_more_7.setVisibility(View.VISIBLE);
			gv7.setVisibility(View.VISIBLE);
			
			adapter7 = new MoreAdapter(this, dataList7);
			gv7.setAdapter(adapter7);
			gv7.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList8)){
			ll_more_8.setVisibility(View.VISIBLE);
			gv8.setVisibility(View.VISIBLE);
			
			adapter8 = new MoreAdapter(this, dataList8);
			gv8.setAdapter(adapter8);
			gv8.setOnItemClickListener(this);
		}
		if(!UniversalUtils.isStringEmpty(dataList9)){
			ll_more_9.setVisibility(View.VISIBLE);
			gv9.setVisibility(View.VISIBLE);
			
			adapter9 = new MoreAdapter(this, dataList9);
			gv9.setAdapter(adapter9);
			gv9.setOnItemClickListener(this);
		}
	}
	
	

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.bt_sure:
			intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

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
					MoreActivity.this.finish();
					break;
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Map<String, String> data = (Map<String, String>) parent.getAdapter()
				.getItem(position);
		intent = new Intent(this, HomebjActivity.class);
		intent.putExtra("home", (Serializable) data);
		startActivity(intent);

	}

}
