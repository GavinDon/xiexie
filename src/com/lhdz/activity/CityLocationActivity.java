package com.lhdz.activity;

import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lhdz.adapter.SortCityAdapter;
import com.lhdz.cityDao.CityModel;
import com.lhdz.cityDao.DBhelper;
import com.lhdz.cityDao.ProvinceModel;
import com.lhdz.entity.SortModel;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.util.CharacterParser;
import com.lhdz.util.Define;
import com.lhdz.util.GetCityDataUtil;
import com.lhdz.util.GpsUtil;
import com.lhdz.util.GpsUtil.GetReceiveLandMark;
import com.lhdz.util.PinyinComparator;
import com.lhdz.util.SPUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.ClearEditText;
import com.lhdz.wediget.SideBar;
import com.lhdz.wediget.SideBar.OnTouchingLetterChangedListener;

public class CityLocationActivity extends BaseActivity implements
		OnClickListener, OnItemClickListener {
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortCityAdapter adapter;
	private DBhelper dbHelper;
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	private ClearEditText mClearEditText;
	// private List<Area> cityData = new ArrayList<Area>();
	private List<ProvinceModel> provinceList = null;// 所有省份数据
	private List<CityModel> cityList = new ArrayList<CityModel>();
	private TextView location_title;// 标题
	private TextView location_tip;// 定位的城市
	private LinearLayout locationBack;// 返回按扭;
	private View v; // List头部View

	private GetCityDataUtil getCityDataUtil = null;

	// 获取访问城市集合
	List<String> getRecentCity;
	String strRecent;
	/*
	 * 根据拼音来排列ListView里面的数据类
	 */

	private PinyinComparator pinyinComparator;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_location);
		// dbHelper = new DBhelper(this);
		// cityData = dbHelper.getCity();// 查询数据库获取城市名称;

		getCityDataUtil = new GetCityDataUtil(this);
		provinceList = getCityDataUtil.getProvinceList();

		for (int i = 0; i < provinceList.size(); i++) {
			cityList.addAll(provinceList.get(i).getCityList());
		}

		initViews();
		setCityLocation();// 给title设置定位结果
		initRecentCity();// 最近访问城市
		initHotCity();// 热门城市的初始化与监听
	}

	private void initViews() {
		v = LayoutInflater.from(this).inflate(R.layout.city_list_head, null);
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		sortListView.addHeaderView(v, null, false);

		locationBack = (LinearLayout) findViewById(R.id.location_delete_lin);
		location_title = (TextView) findViewById(R.id.location_title);
		location_tip = (TextView) v.findViewById(R.id.location_tip);
		location_tip.setOnClickListener(this);
		// location_tip.setText(MyApplication.stcliyLocation);
		// location_title.setText("当前位置—" + MyApplication.stcliyLocation);
		locationBack.setOnClickListener(this);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		// listview中Item点击事件
		sortListView.setOnItemClickListener(this);

		SourceDateList = filledData(cityList);// 数据源

		// 根据a-z进行排序源数据
		Collections.sort(SourceDateList, pinyinComparator);
		adapter = new SortCityAdapter(this, SourceDateList);
		sortListView.setAdapter(adapter);
		// ComputeListViewHeight.setListViewHeightBasedOnChildren(sortListView);//
		// 重新计算ListView的高度;

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);

		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				sortListView.removeHeaderView(v);
				// v.setVisibility(View.GONE);
				filterData(s.toString().toLowerCase());
				if (s.length() == 0) {
					sortListView.addHeaderView(v, null, false);
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

		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// int position=(int) adapter.getItemId(s.charAt(0));
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position + 1);
				}

			}
		});

	}

	/*
	 * 为ListView填充数据
	 */
	private List<SortModel> filledData(List<CityModel> date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date.get(i).getName());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(date.get(i).getName());
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString);
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/*
	 * 根据输入框中的值来过滤数据并更新ListView
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		SortModel sortModel = (SortModel) parent.getAdapter().getItem(position);

		String preCityName = MyApplication.stcliyLocation;

		// 获取选中城市名
		String name = sortModel.getName().toString().trim();
		// 标题栏设置选中城市
		location_title.setText(name);
		// 点击时处理最近访问城市数据的顺序以及共享参数中的存储个数
		processSpData(name);

		MyApplication.stcliyLocation = name;
		// 根据城市名转换城市ID
		MyApplication.iCityLocationId = getCityDataUtil.addrToAreaId(name);
		MyApplication.cityLocationFlag = true;

		// 把选中的城市名与ID放入sharedpreference里面;
		SPUtils.put(this, Define.SP_KEY_LOCATION_NAME, name);// 城市名
		SPUtils.put(this, Define.SP_KEY_LOCATION_ID,
				MyApplication.iCityLocationId);// 城市ID

		Intent data = new Intent();
		data.putExtra("isCityChange", preCityName.equals(name) ? false : true);
		setResult(Define.RESULTCODE_CITY_RESET, data);

		finish();

		// 这里要利用adapter.getItem(position)来获取当前position所对应的对象
		// 点击城市回到对应城市的首页
		// Toast.makeText(getApplication(), sortModel.getName(),
		// Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_delete_lin:

			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mClearEditText.getWindowToken(), 0);

			this.finish();
			break;
		case R.id.location_tip:

			if (!UniversalUtils.isStringEmpty(location_tip.getText().toString()
					.trim())) {

				String preCityName = MyApplication.stcliyLocation;

				String cityName = location_tip.getText().toString().trim();
				location_title.setText(cityName);

				MyApplication.stcliyLocation = cityName;
				// 根据城市名转换城市ID
				MyApplication.iCityLocationId = getCityDataUtil
						.addrToAreaId(cityName);
				MyApplication.cityLocationFlag = true;
				// 点击时处理最近访问城市数据的顺序以及共享参数中的存储个数
				processSpData(cityName);
				// 把选中的城市名与ID放入sharedpreference里面;
				SPUtils.put(this, Define.SP_KEY_LOCATION_NAME, cityName);// 城市名
				SPUtils.put(this, Define.SP_KEY_LOCATION_ID,
						MyApplication.iCityLocationId);// 城市ID

				Intent data = new Intent();
				data.putExtra("isCityChange",
						preCityName.equals(cityName) ? false : true);
				setResult(Define.RESULTCODE_CITY_RESET, data);
				finish();
			}
			break;

		}
	}

	/*
	 * 设置城市位置
	 */
	public void setCityLocation() {

		new GpsUtil(this).setLandMark(new GetReceiveLandMark() {

			@Override
			public void onGetLandMark(String location, int cityId) {

				location_tip.setText(location + "");

				if (UniversalUtils.isStringEmpty(MyApplication.stcliyLocation)) {
					MyApplication.stcliyLocation = location;
					MyApplication.iCityLocationId = cityId;
					// MyApplication.iCityLocationId =
					// getCityDataUtil.addrToAreaId(location);
					MyApplication.cityLocationFlag = true;

					// 把选中的城市名与ID放入sharedpreference里面;
					SPUtils.put(CityLocationActivity.this,
							Define.SP_KEY_LOCATION_NAME, location);// 城市名
					SPUtils.put(CityLocationActivity.this,
							Define.SP_KEY_LOCATION_ID,
							MyApplication.iCityLocationId);// 城市ID

					location_title.setText("" + location);
				} else {
					location_title.setText("" + MyApplication.stcliyLocation);
				}
			}

		});
	}

	/**
	 * 热门城市数据的初始化与监听
	 */
	private void initHotCity() {
		String arr_hotCity[] = { "西安市", "北京市", "上海市", "成都市", "杭州市", "郑州市",
				"武汉市", "天津市", "重庆市" };
		GridView gv_hotCity = (GridView) findViewById(R.id.gv_hotcity);
		gv_hotCity.setAdapter(new ArrayAdapter<String>(this,
				R.layout.city_recent_hot, R.id.tv_recent, arr_hotCity));
		gv_hotCity.setOnItemClickListener(new recentandHotCityListener());
		View v=LayoutInflater.from(this).inflate(R.layout.city_recent_hot, null);
		countTextViewWidth((TextView)v.findViewById(R.id.tv_recent));

	}

	/**
	 * 最近访问城市
	 */
	private void initRecentCity() {
		strRecent = (String) SPUtils.get(this, Define.SP_KEY_RECENT_CITY, "");
		try {
			if (UniversalUtils.isStringEmpty(strRecent)) {
				getRecentCity = new ArrayList<String>();
				getRecentCity.add(MyApplication.stcliyLocation);
				strRecent = SPUtils.List2String(getRecentCity);
				SPUtils.put(this, Define.SP_KEY_RECENT_CITY, strRecent);

			} else {
				// String[]arrayStr=new String[3];
				// arrayStr = strRecent.split(",");
				// getRecentCity = java.util.Arrays.asList(arrayStr);
			}

		} catch (Exception e) {

		}
		strRecent = (String) SPUtils.get(this, Define.SP_KEY_RECENT_CITY, "");
		try {
			getRecentCity = SPUtils.String2List(strRecent);
			GridView gv_recentCity = (GridView) findViewById(R.id.gv_recentcity);

			gv_recentCity.setAdapter(new ArrayAdapter<String>(this,
					R.layout.city_recent_hot, R.id.tv_recent, getRecentCity));
			gv_recentCity
					.setOnItemClickListener(new recentandHotCityListener());
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 最近访问城市gridview点击时跳转首页进行数据的请求
	 * 
	 * @author Administrator
	 * 
	 */
	class recentandHotCityListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String strClickCity = (String) parent.getAdapter()
					.getItem(position);
			processSpData(strClickCity);
			String preCityName = MyApplication.stcliyLocation;
			MyApplication.stcliyLocation = strClickCity;
			// 根据城市名转换城市ID
			MyApplication.iCityLocationId = getCityDataUtil
					.addrToAreaId(strClickCity);
			MyApplication.cityLocationFlag = true;
			// 把选中的城市名与ID放入sharedpreference里面;
			SPUtils.put(CityLocationActivity.this, Define.SP_KEY_LOCATION_NAME,
					strClickCity);// 城市名
			SPUtils.put(CityLocationActivity.this, Define.SP_KEY_LOCATION_ID,
					MyApplication.iCityLocationId);// 城市ID

			Intent data = new Intent();
			data.putExtra("isCityChange",
					preCityName.equals(strClickCity) ? false : true);
			setResult(Define.RESULTCODE_CITY_RESET, data);
			finish();

		}

	}

	/**
	 * 操作共享参数中的集合
	 * 
	 * @param name
	 */
	private void processSpData(String name) {
		strRecent = (String) SPUtils.get(this, Define.SP_KEY_RECENT_CITY, "");
		try {
			// 最近访问的显示在第一个
			getRecentCity.add(0, name);
			for (int i = 1; i < getRecentCity.size(); i++) {
				if (getRecentCity.get(i).contains(name)) {
					getRecentCity.remove(i);
				}
			}
			// 保证只显示4个最近访问的城市
			if (getRecentCity.size() > 4) {
				getRecentCity.remove(4);
			}
			strRecent = SPUtils.List2String(getRecentCity);

			SPUtils.put(this, Define.SP_KEY_RECENT_CITY, strRecent);

		} catch (Exception e) {

		}
	}

	@SuppressLint("NewApi") public void countTextViewWidth(TextView tv) {
		WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display d1 = window.getDefaultDisplay(); // 获取屏幕宽、高用
		Point size = new Point();
		d1.getSize(size);
		int width = size.x;
		tv.setWidth((int) (width*0.33));
		
	}

}
