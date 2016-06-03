package com.lhdz.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lhdz.cityDao.CityModel;
import com.lhdz.cityDao.DistrictModel;
import com.lhdz.cityDao.ProvinceModel;
import com.lhdz.util.Define;
import com.lhdz.util.GetCityDataUtil;
import com.lhdz.util.GetScreenInchUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.CityPop;
import com.lhdz.wediget.CityPop.CityCallBack;
import com.lhdz.wheel.OnWheelChangedListener;
import com.lhdz.wheel.WheelView;
import com.lhdz.wheel.adapters.ArrayWheelAdapter;


/**
 * 编辑用户信息界面
 * @author wangf
 *
 */
public class UserInfoEdit extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener, OnWheelChangedListener {
	// 编辑昵称
	private RelativeLayout rl_editnickname;
	private EditText et_edit_nickname;
	// 修改性别
	private RadioGroup rg_modify_sex;
	private RadioButton rb_man, rb_woman;
	// 修改显示地区
	private TextView tv_area;
	private String selectAreaId = "";//已选择的区域id

	// 编辑个性签名
	private EditText et_edit_graph;

	private TextView tv_edit_cancle;

	private TextView tv_edit_save;

	private TextView tv_edit_title;

	private static final int EDIT_NICKNAME = 1;
	private static final int MODIFY_SEX = 2;
	private static final int MODIFY_AREA = 3;
	private static final int EDIT_GRAPH = 4;
	private int pageFlag = 0;

	private ColorDrawable cd;

	private WheelView mViewProvince;
	private WheelView mViewCity;
	private WheelView mViewDistrict;

	protected String[] mProvinceDatas;// 所有省
	protected Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();

	protected Map<String, String[]> mDistrictDatasMap = new HashMap<String, String[]>();

	protected Map<String, String[]> mZipcodeDatasMap = new HashMap<String, String[]>();

	protected String mCurrentProviceName;// 当前省的名称
	protected String mCurrentCityName;// 当前市的名称
	protected String mCurrentDistrictName = "";// 当前区的名称
	protected String mCurrentZipCode = "";// 当前区的邮政编码
	private List<ProvinceModel> provinceList = null;// 所有省份数据
	private View popView;
	private GetCityDataUtil cityDataUtil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_user_info_edit);

		initView();
		hideAllPage();
		judgeShowWitchPage();
		tv_edit_save.setOnClickListener(this);
		tv_edit_cancle.setOnClickListener(this);
	}

	/**
	 * 初始化页面控件
	 */
	private void initView() {
		
		rl_editnickname = (RelativeLayout) findViewById(R.id.rl_editnickname);
		et_edit_nickname = (EditText) findViewById(R.id.et_edit_nickname);
		rg_modify_sex = (RadioGroup) findViewById(R.id.rg_modify_sex);
		et_edit_graph = (EditText) findViewById(R.id.et_edit_graph);
		rg_modify_sex.setOnCheckedChangeListener(this);
		tv_edit_cancle = (TextView) findViewById(R.id.tv_back);
		tv_edit_save = (TextView) findViewById(R.id.tv_edit_save);
		tv_edit_title = (TextView) findViewById(R.id.tv_edit_title);
		rb_man = (RadioButton) findViewById(R.id.rb_man);
		rb_woman = (RadioButton) findViewById(R.id.rb_woman);
		tv_area = (TextView) findViewById(R.id.tv_area);
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			popWindowCity(tv_area);
		};
	};

	/**
	 * 判断显示哪一个页面
	 */
	public void judgeShowWitchPage() {
		Intent intent = getIntent();
		int page = intent.getExtras().getInt("edit");
		switch (page) {
		case 0:// 帐号修改
			break;
		case EDIT_NICKNAME:// 昵称修改
			rl_editnickname.setVisibility(View.VISIBLE);
			String nickName = intent.getExtras().getString("editnick");// 得到原来的值
			et_edit_nickname.setText(nickName);
			et_edit_nickname.requestFocus();
			et_edit_nickname.setSelection(nickName.length());
			tv_edit_title.setText("名字");
			pageFlag = 1;
			break;
		case MODIFY_SEX:// 性别修改
			rg_modify_sex.setVisibility(View.VISIBLE);
			tv_edit_save.setVisibility(View.GONE);
			tv_edit_title.setText("性别");
			String sex = intent.getExtras().getString("editsex");
			if (sex.equals("女")) {
				setSexImageShow(rb_woman);
			} else if (sex.equals("男")) {
				setSexImageShow(rb_man);
			}
			break;
		case MODIFY_AREA:
			tv_area.setVisibility(View.VISIBLE); // 显示出编辑地区控件
			tv_edit_title.setText("地区");
			getCityData(); // 获取城市数据
			
			int areaId = intent.getExtras().getInt("deitAreaId");
			selectAreaId = areaId + "";
			tv_area.setText(cityDataUtil.areaIdToAddr(areaId));//将areaid转换为具体地址
			
			// 此处弹出一个popupWindow不能在oncreat方法中弹出 因为有可能activity没有完全加载
			// (原因：popupWindow或者对话框之类的都是依赖于Activity)所以使用Handler来延时处理一下
			Message msg = Message.obtain(mHandler);
			mHandler.sendMessageDelayed(msg, 100);
			tv_area.setOnClickListener(this);
			pageFlag = 3;
			break;
		case EDIT_GRAPH: // 修改签名
			et_edit_graph.setVisibility(View.VISIBLE);
			tv_edit_title.setText("个性签名");
			String graph = intent.getExtras().getString("editgraph");
			et_edit_graph.setText(graph);
			et_edit_graph.requestFocus();
			et_edit_graph.setSelection(graph.length());
			pageFlag = 4;

			break;
		}

	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if (v.getId() == R.id.tv_edit_save) {
			switch (pageFlag) {
			case 1: // 保存昵称
				intent.putExtra("saveNick", et_edit_nickname.getText()
						.toString().trim());
				setResult(Define.RESULTCODE_NICK, intent);
				this.finish();
				break;
			case 3:// 保存地区
//				intent.putExtra("saveArea", tv_area.getText().toString().trim());
				if(UniversalUtils.isStringEmpty(selectAreaId)){
					ToastUtils.show(this, "请选择正确的地区", 1);
					return;
				}
				intent.putExtra("saveArea", selectAreaId);
				setResult(Define.RESULTCODE_AREA, intent);
				this.finish();

			case 4:// 保存签名
				intent.putExtra("saveGraph", et_edit_graph.getText().toString()
						.trim());
				setResult(Define.RESULTCODE_GRAPH, intent);
				this.finish();

			}
		}
		
		// 点击编辑地区控件时
		if (v.getId() == R.id.tv_area) {
//			popWindowCity(v);
		}
		if (v.getId() == R.id.tv_back) {
			this.finish();
		}

	}

	/**
	 *  隐藏所有的View
	 */
	public void hideAllPage() {
		rl_editnickname.setVisibility(View.GONE);
		rg_modify_sex.setVisibility(View.GONE);
		tv_area.setVisibility(View.GONE);
		et_edit_graph.setVisibility(View.GONE);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Intent intent = new Intent();
		switch (checkedId) {
		case R.id.rb_man:
			intent.putExtra("edit", "男");
			setSexImageShow(rb_man);
			setResult(Define.RESULTCODE_SEX_MAN, intent);
			this.finish();
			break;
		case R.id.rb_woman:
			intent.putExtra("edit", "女");
			setSexImageShow(rb_woman);
			setResult(Define.RESULTCODE_SEX_WOMAN, intent);
			this.finish();
			break;
		}
	}

	/**
	 * 显示选中性别时的的图片
	 */
	public void setSexImageShow(RadioButton radioButton) {
		rb_man.setCompoundDrawables(null, null, null, null);
		rb_woman.setCompoundDrawables(null, null, null, null);
		Drawable selcoterDrawable = getResources().getDrawable(
				R.drawable.image_sex);
		selcoterDrawable.setBounds(0, 0, selcoterDrawable.getMinimumWidth(),
				selcoterDrawable.getMinimumHeight());
		radioButton.setCompoundDrawables(null, null, selcoterDrawable, null);
	}

	/**
	 * 根据当前的市，更新区WheelView的信息
	 */
	private void updateAreas() {
		int pCurrent = mViewCity.getCurrentItem();
		mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
		String[] areas = mDistrictDatasMap.get(mCurrentCityName);

		if (areas == null) {
			areas = new String[] { "" };
		}
		mViewDistrict
				.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));
		mViewDistrict.setCurrentItem(0);

		mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[0];
		mCurrentZipCode = mZipcodeDatasMap.get(mCurrentCityName)[0];
	}

	/**
	 * 根据当前的省，更新市WheelView的信息
	 */
	private void updateCities() {
		int pCurrent = mViewProvince.getCurrentItem();
		mCurrentProviceName = mProvinceDatas[pCurrent];
		String[] cities = mCitisDatasMap.get(mCurrentProviceName);
		if (cities == null) {
			cities = new String[] { "" };
		}
		mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));
		mViewCity.setCurrentItem(0);
		updateAreas();
	}

	/**
	 * 弹出的选择城市控件
	 * 
	 * @param v
	 */
	private void popWindowCity(View v) {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		popView = LayoutInflater.from(this).inflate(R.layout.city_wheel, null);
		popView.findViewById(R.id.pop_city_cacel).setVisibility(View.GONE);
		lp.alpha = 0.7f;
		cd = new ColorDrawable(Color.WHITE);
		CityPop pop = new CityPop(this, popView, cd);
		pop.setOutsideTouchable(false);
		pop.showPopmenu(v, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, Gravity.BOTTOM, 0, GetScreenInchUtil.getVrtualBtnHeight(this));
//		getWindow().setAttributes(lp);

		pop.setCityOnClickListener(new CityCallBack() {

			@Override
			public void callBackConfirm(PopupWindow myPopmenu) {
				tv_area.setText(mCurrentProviceName + " " + mCurrentCityName
						+ " " + mCurrentDistrictName);
				selectAreaId = mCurrentZipCode;
//				myPopmenu.dismiss();
			}

			@Override
			public void callBackCacel(PopupWindow myPopmenu) {
//				myPopmenu.dismiss();
			}
		});

		initPopView(popView);
		setUpData();
	}

	/**
	 * 初始化选择城市控件
	 */
	private void initPopView(View popv) {
		mViewProvince = (WheelView) popv.findViewById(R.id.citypop_id_province);
		mViewCity = (WheelView) popv.findViewById(R.id.citypop_id_city);
		mViewDistrict = (WheelView) popv.findViewById(R.id.citypop_id_district);

		mViewProvince.addChangingListener(this);
		mViewCity.addChangingListener(this);
		mViewDistrict.addChangingListener(this);
	}

	private void setUpData() {
		// initProvinceDatasXml();
		mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(this,
				mProvinceDatas));
		// 设置可见条目数量
		mViewProvince.setVisibleItems(7);
		mViewCity.setVisibleItems(7);
		mViewDistrict.setVisibleItems(7);
		updateCities();
		updateAreas();
	}

	@Override
	public void onChanged(WheelView wheel, int oldValue, int newValue) {
		if (wheel == mViewProvince) {
			updateCities();
		} else if (wheel == mViewCity) {
			updateAreas();
		} else if (wheel == mViewDistrict) {
			mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)[newValue];
			mCurrentZipCode = mZipcodeDatasMap.get(mCurrentCityName)[newValue];
		}
	}

	/**
	 * 获取省市区的值
	 */
	public void getCityData() {
		cityDataUtil = new GetCityDataUtil(getApplicationContext());
		provinceList = cityDataUtil.getProvinceList();
		if (provinceList != null && !provinceList.isEmpty()) {
			mCurrentProviceName = provinceList.get(0).getName();
			List<CityModel> cityList = provinceList.get(0).getCityList();
			if (cityList != null && !cityList.isEmpty()) {
				mCurrentCityName = cityList.get(0).getName();
				List<DistrictModel> districtList = cityList.get(0)
						.getDistrictList();
				mCurrentDistrictName = districtList.get(0).getName();
				mCurrentZipCode = districtList.get(0).getZipcode();
			}
		}
		// */
		mProvinceDatas = new String[provinceList.size()];
		for (int i = 0; i < provinceList.size(); i++) {
			// 遍历所有省的数据
			mProvinceDatas[i] = provinceList.get(i).getName();
			List<CityModel> cityList = provinceList.get(i).getCityList();
			String[] cityNames = new String[cityList.size()];
			for (int j = 0; j < cityList.size(); j++) {
				// 遍历省下面的所有市的数据
				cityNames[j] = cityList.get(j).getName();
				List<DistrictModel> districtList = cityList.get(j)
						.getDistrictList();
				String[] distrinctNameArray = new String[districtList.size()];
				String[] distrinctCodeArray = new String[districtList.size()];
				DistrictModel[] distrinctArray = new DistrictModel[districtList
						.size()];
				for (int k = 0; k < districtList.size(); k++) {
					// 遍历市下面所有区/县的数据
					DistrictModel districtModel = new DistrictModel(districtList.get(k).getName(), districtList.get(k).getZipcode());
					// 区/县对于的邮编，保存到mZipcodeDatasMap
//					mZipcodeDatasMap.put(districtList.get(k).getName(),districtList.get(k).getZipcode());
					distrinctArray[k] = districtModel;
					distrinctNameArray[k] = districtModel.getName();
					distrinctCodeArray[k] = districtModel.getZipcode();
				}
				// 市-区/县的数据，保存到mDistrictDatasMap
				mDistrictDatasMap.put(cityNames[j], distrinctNameArray);
				mZipcodeDatasMap.put(cityNames[j],distrinctCodeArray);
			}
			// 省-市的数据，保存到mCitisDatasMap
			mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
		}

	}
	
	

}
