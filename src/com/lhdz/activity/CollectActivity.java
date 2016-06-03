package com.lhdz.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.lhdz.adapter.CollectAdapter;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.util.CharacterParser;
import com.lhdz.util.SearchUtil;
import com.lhdz.wediget.ClearEditText;


/**
 * 收藏页面
 * @author wangf
 *
 */
public class CollectActivity extends BaseActivity implements OnClickListener ,OnItemClickListener{
	private ListView listView;
	private TextView title;
	List<Map<String, String>> collectCompanyDataList = new ArrayList<Map<String,String>>();
	private ClearEditText et_search;
	private CharacterParser characterParser;
	private CollectAdapter mAdapter;
	private SearchUtil mSearchutil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect);
		
		initviews();
		backArrow();// 返回
		
		mAdapter = new CollectAdapter(this);
		listView.setAdapter(mAdapter);
		listView.setOnItemClickListener(this);

	}

	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		queryStarCompanyData();
		initSearch();
		
	}
	
	
	/**
	 * 初始化页面控件
	 */
	private void initviews() {
		listView = (ListView) findViewById(R.id.lv_collect);

		title = (TextView) findViewById(R.id.tv_title);
		title.setText("收藏");
		characterParser = CharacterParser.getInstance();

	}
	
	
	/**
	 * 初始化搜索数据
	 */
	private void initSearch() {
		et_search = (ClearEditText) findViewById(R.id.et_search);
		mSearchutil = new SearchUtil(getApplicationContext(),
				collectCompanyDataList);
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String content = et_search.getText().toString();
				// 当输入的字符变化时过滤的值随之跟着变化
				List<Map<String, String>> filterStrData = mSearchutil
						.filterStrData(s.toString().toLowerCase(), "szName");
				mAdapter.setData(filterStrData);
				// 如果输入框内无字符的话设置回原来的数据;
				if (content == null || content.equals("")) {
					mAdapter.setData(collectCompanyDataList);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Map<String, String> starCompanyDetail = (Map<String, String>) parent.getAdapter().getItem(position);
		Intent intent = new Intent(CollectActivity.this,CompanyDetailActivity.class);
		intent.putExtra("starCompanyDetail", (Serializable)starCompanyDetail);
		startActivity(intent);
	}
	

//	@Override
//	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		
//		Map<String, String> starCompanyDetail = (Map<String, String>) parent.getAdapter().getItem(position);
//		Intent intent = new Intent(this,CompanyDetailActivity.class);
//		intent.putExtra("starCompanyDetail", (Serializable)starCompanyDetail);
////		intent.putExtra("appHomeDataList", (Serializable)appHomeDataList);
//		startActivity(intent);
//		
////		ViewHolder holder = (ViewHolder) view.getTag();// 得到listview中某一position的视图
////		 String title = holder.vhName.getText().toString();
////		 Intent intent = new Intent(StarCompanyActivity.this,
////		 CompanyDetailActivity.class);
////		
////		 startActivity(intent);
//
//	}

	@Override
	public void onClick(View v) {
 
	}
	
	
	/**
	 * 查询收藏的明星公司数据
	 */
	private void queryStarCompanyData() {
		collectCompanyDataList.clear();
		// 查询数据库
		String sql = DbOprationBuilder.queryCollectFromStarCompanyAllBuilder(MyApplication.userId);
		DataBaseService ds = new DataBaseService(this);
		collectCompanyDataList = ds.query(sql);
		
		mAdapter.setData(collectCompanyDataList);
	}
	
	
	/**
	 * 后退按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					CollectActivity.this.finish();
					break;
				}
			}
		});
	}


}
