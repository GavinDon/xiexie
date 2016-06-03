package com.lhdz.fragment;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.activity.CityLocationActivity;
import com.lhdz.activity.CompanyDetailActivity;
import com.lhdz.activity.HelpWebActivity;
import com.lhdz.activity.IndentDetailsActivity;
import com.lhdz.activity.LoginActivity;
import com.lhdz.activity.MainFragment;
import com.lhdz.activity.R;
import com.lhdz.activity.StarCompanyActivity;
import com.lhdz.adapter.AdPlayerAdaper;
import com.lhdz.adapter.HotRecommendAdapter;
import com.lhdz.adapter.OutoftimeAdapter;
import com.lhdz.adapter.ShotcutAdapter;
import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsHotCompanyInfo_Req;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsStarCompanyGet_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.pulltorefresh.PullToRefreshBase;
import com.lhdz.pulltorefresh.PullToRefreshBase.Mode;
import com.lhdz.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.lhdz.pulltorefresh.PullToRefreshListView;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.FixedSpeedScroller;
import com.lhdz.util.GetScreenInchUtil;
import com.lhdz.util.GpsUtil;
import com.lhdz.util.GpsUtil.GetReceiveLandMark;
import com.lhdz.util.LogUtils;
import com.lhdz.util.NetWorkUtil;
import com.lhdz.util.SPUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.util.UpdateManager;
import com.lhdz.wediget.PopMenu;

public class HomePageFragment extends Fragment implements OnClickListener,
		OnPageChangeListener {

	// 明星宝贝
//	private ImageView starbaby;
	
	/**
	 * 标题定位相关
	 */
	private LinearLayout ll_cityLocation;
	private TextView tv_cityLocation;
	
	private String spCityLocationName;//sharePreference中的城市名称
	private int spCityLocationId;//sharepreference中共的城市id
	private boolean isShowLocationDialog = false;//用户位置改变对话框是否已经显示过
	
	private final static int REQ_ACTIVITY_CODE = 120;//用户进入定位Activity的RequestCode

	
	/**
	 * 整体页面的View
	 */
	private View view;
	
	
	/**
	 * listView相关
	 */
	private View headView;
	private View footView;
	private ListView listView;
	private PullToRefreshListView pullToRefreshListView;
	private LinearLayout ll_home_foot_login;
	private HotRecommendAdapter starCompanyAdapter = null;
	private int pageNum = 0;
	
	
	/**
	 * 快捷下单相关
	 */
	private ViewPager myViewPager;
//	private Shotcut1_Fragment shotcut1_Fragment;
	private Shotcut2_Fragment shotcut2_Fragment;
	private ArrayList<Fragment> myFragmnet = new ArrayList<Fragment>();// fragment集合
	private ImageView[] arrayCircle;//快捷下单小圆点
	private ImageView slideCircle1, slideCircle2;//快捷下单小圆点
	
	/**
	 * 广告GridView相关
	 */
	private GridView custGridView;
	private List<HashMap<String, Object>> listImageItem = new ArrayList<HashMap<String, Object>>();// gridview集合
	
	/**
	 * 广告轮播相关
	 */
	private int select = 0;
	private ViewPager adViewpager;
	private List<Integer> imgs = new ArrayList<Integer>();// 广告图片List;
	private int[] ints = new int[] { R.drawable.ad_home_newyear, R.drawable.ad_home_iphone,
									R.drawable.ad_home_xiexie,R.drawable.ad_home_good };
	
	/**
	 * 广告iPhone相关
	 */
	private ImageView iv_home_iphone;
	
	
	/**
	 * 热门推荐相关
	 */
	private RadioGroup rg_home_hotcompany;
	private RadioButton rb_home_clean;//保洁
	private RadioButton rb_home_move;//搬家
	private RadioButton rb_home_furniture;//家具维修
	private RadioButton rb_home_pet;//宠物服务
	private ImageView iv_home_arrow_left;
	private ImageView iv_home_arrow_right;
	private HorizontalScrollView hs_home_hotcompany;
	
	private int hotTypeID = 10; //热门推荐的类型id。默认为保洁（）
	

	/**
	 * 相关数据
	 */
	List<Map<String, String>> appHomeDataList = null;// 首页快捷下单数据
	List<Map<String, String>> starCompanyDataList = new ArrayList<Map<String,String>>();// 首页明星公司列表数据

	
	/**
	 * handle相关
	 */
	private final static int MSG_LOAD_SUCCESS = 10000;
	private final static int MSG_LOAD_ERROR = 10001;
	private final static int LOAD_TIMER_OVER = 10002;
	private final static int DELAY_COUNT_WIDTH = 10003;
	private final static int DELAY_AD_PAGER = 2;
	
	private MyApplication myApplication = null;

	/**
	 * 进度条相关
	 */
	private CustomProgressDialog progressDialog;
	
	/**
	 * 发送请求的sequenceNo
	 */
	private int seqStarCompany =-1;
	private int seqHotCompany =-1;
	private int seqAppHome =-1;
	

	private List<HashMap<String, Object>> listViewItem = new ArrayList<HashMap<String, Object>>();// listview集合
	private PopMenu pop;
	private CheckBox button;
	private ImageView ima;
	private View vs;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		new UpdateManager(getActivity()).checkUpdate();// 检测更新
		for (Integer integer : ints) {
			imgs.add(integer);
		}
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		filter.addAction(Define.BROAD_FRAGMENT_RECV_APPHOME);
		filter.addAction(Define.BROAD_FRAGMENT_RECV_STARCOMPANY);
		filter.addAction(Define.BROAD_FRAGMENT_RECV_LOGIN);
		getActivity().registerReceiver(mReceiver, filter);
		
		myApplication = (MyApplication) getActivity().getApplication();
		
		spCityLocationName = (String) SPUtils.get(MyApplication.context,Define.SP_KEY_LOCATION_NAME, "");
		spCityLocationId = (Integer) SPUtils.get(MyApplication.context, Define.SP_KEY_LOCATION_ID, 610100);
		
		MyApplication.stcliyLocation = spCityLocationName;
		MyApplication.iCityLocationId = spCityLocationId;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		loadgvImage();

		queryAppHomeData();
//		queryStarCompanyData();

		headView = inflater.inflate(R.layout.homepage_head, null);
		footView = inflater.inflate(R.layout.homepage_foot, null);
		view = inflater.inflate(R.layout.fragmnet_homepage3, null);
		initViews();
		clickListener();
		
		setCityLocation();//定位
		
		//根据登录状态
		if(MyApplication.loginState){
			pageNum = 0;
			hotTypeID = 10;
			starCompanyDataList.clear();
			loadHotCompanyData();
		}else{
			pageNum = 0;
		}
		
		return view;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	

	@Override
	public void onResume() {
		super.onResume();
		
		
//		// 根据登录状态
//		if (MyApplication.loginState) {
//			pageNum = 0;
//			loadStarCompanyData();
//			pullToRefreshListView.setMode(Mode.PULL_FROM_END);
//			listView.removeFooterView(footView);
//		} else {
//			pullToRefreshListView.setMode(Mode.DISABLED);
//			
//			starCompanyDataList.clear();
//			starCompanyAdapter.setData(starCompanyDataList);
//		}
		
		tv_cityLocation.setText(MyApplication.stcliyLocation);
		
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Define.RESULTCODE_CITY_RESET){
			boolean isCityChange = data.getBooleanExtra("isCityChange", false);
			if(isCityChange){
				pageNum = 0;
				starCompanyDataList.clear();
				loadHotCompanyData();
			}
		}
		
	}
	
	
	
	/*
	 * 设置城市位置
	 */
	public void setCityLocation() {
		if (NetWorkUtil.isNetworkConnected(getActivity()) != false) {

			new GpsUtil(getActivity()).setLandMark(new GetReceiveLandMark() {

				@Override
				public void onGetLandMark(String location, int cityId) {
					
					
					if (location == null) {
						tv_cityLocation.setText("正在加载中");
					} else {
						
						if(location.equals(SPUtils.get(MyApplication.context, Define.SP_KEY_LOCATION_NAME, ""))){
////							// 把选中的城市名与ID放入sharedpreference里面;
//							SPUtils.put(getActivity(), Define.SP_KEY_LOCATION_NAME, location);// 城市名
//							SPUtils.put(getActivity(), Define.SP_KEY_LOCATION_ID,MyApplication.iCityLocationId);// 城市ID
							tv_cityLocation.setText(location);
						}else{
							if(!MyApplication.cityLocationFlag){
								if(!isShowLocationDialog){
									showLocationDialog(location, cityId);
								}
							}
						}
						
					}
				}

			});
		} 
	}
	
	
	
	/**
	 * 定位改变对话框
	 */
	private void showLocationDialog(final String location, final int cityId) {
		SweetAlertDialog dialog = new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
		dialog.setContentText("系统定位您现在在"+location+",是否切换到"+location+"？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				
				SPUtils.put(MyApplication.context, Define.SP_KEY_LOCATION_NAME, location);// 城市名
				SPUtils.put(MyApplication.context, Define.SP_KEY_LOCATION_ID,cityId);// 城市ID
				spCityLocationName = location;
				spCityLocationId = cityId;
				tv_cityLocation.setText(location);
				
				MyApplication.stcliyLocation = location;
				MyApplication.iCityLocationId = cityId;
				MyApplication.cityLocationFlag = true;
				
				//当用户选择城市之后，重新加载数据
				starCompanyDataList.clear();
				pageNum = 0;
				loadHotCompanyData();
				
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		dialog.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				tv_cityLocation.setText(spCityLocationName);
				MyApplication.stcliyLocation = spCityLocationName;
				MyApplication.iCityLocationId = spCityLocationId;
				MyApplication.cityLocationFlag = true;
				
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		dialog.show();
		isShowLocationDialog = true;
	}
	


	@SuppressWarnings("unchecked")
	private void initViews() {

//		starbaby = (ImageView) headView.findViewById(R.id.tv_advert);
		slideCircle1 = (ImageView) headView.findViewById(R.id.slidepoint1);
		slideCircle2 = (ImageView) headView.findViewById(R.id.slidepoint2);// 快捷键滑动圆点;
		arrayCircle = new ImageView[] { slideCircle1, slideCircle2 };
		
		myViewPager = (ViewPager) headView.findViewById(R.id.viewpager_shotcut_key);// 快捷按钮Viewpager;
//		shotcut1_Fragment = new Shotcut1_Fragment();
		shotcut2_Fragment = new Shotcut2_Fragment();
//		shotcut1_Fragment.setShotcutFragmentData(appHomeDataList);
		shotcut2_Fragment.setShotcutFragmentData(appHomeDataList);

//		myFragmnet.add(shotcut1_Fragment);
		myFragmnet.add(shotcut2_Fragment);
		// myFragmnet.add(new Shotcut1_Fragment(appHomeDataList));
		// myFragmnet.add(new Shotcut2_Fragment(appHomeDataList));
		// Fragment嵌套Fragment时Fragmanager不能使用getsupportManager来得到。这样得到的是父Fragment的管理器;
		FragmentManager fm = getChildFragmentManager();
		// 快捷键的viewpager适配器和页面改变的监听：
		myViewPager.setAdapter(new ShotcutAdapter(fm, myFragmnet));
		myViewPager.setOnPageChangeListener(new PageChageListener());

		custGridView = (GridView) headView.findViewById(R.id.gridview_);
		custGridView.setAdapter(new OutoftimeAdapter(getActivity(),listImageItem));
		
		ll_home_foot_login = (LinearLayout) footView.findViewById(R.id.ll_home_foot_login);
		iv_home_iphone = (ImageView) headView.findViewById(R.id.iv_home_iphone);

		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.homepager3_listview);
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);

		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				
				String label = DateUtils.formatDateTime(getActivity()
						.getApplicationContext(), System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel("上次更新时间：" + label);
				
				
//				if(MyApplication.loginState){
					pullToRefreshListView.setMode(Mode.PULL_FROM_END);
					pageNum = 0;
					loadHotCompanyData();
//				}
				
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				
				if(starCompanyDataList.size() == 0){
					pageNum = 0;
				}else{
					pageNum = pageNum + Define.LOAD_DATA_NUM;
				}
				loadHotCompanyData();
				
			}
		});

		listView = pullToRefreshListView.getRefreshableView();
		listView.addHeaderView(headView, null, false);
//		listView.addFooterView(footView, null, false);

		starCompanyAdapter = new HotRecommendAdapter(getActivity(),appHomeDataList);
		pullToRefreshListView.setAdapter(starCompanyAdapter);
		starCompanyAdapter.setData(starCompanyDataList);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {
			// 热门推荐点击进入公司详情页面
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Map<String, String> starCompanyDetail = (Map<String, String>) parent.getAdapter().getItem(position);
				Intent intent = new Intent(getActivity(),CompanyDetailActivity.class);
				intent.putExtra("starCompanyDetail", (Serializable)starCompanyDetail);
				startActivity(intent);
				
			}
		});
		
		
		setRadioButtonWidth();
		initAd();
		GpsLocation();

	}
	
	
	/**
	 * 与定位相关的View
	 */
	private void GpsLocation() {
		tv_cityLocation = (TextView) view.findViewById(R.id.city_position);
		ll_cityLocation = (LinearLayout) view.findViewById(R.id.ll_gps);

	}
	
	
	/**
	 * 初始化热门推荐的选择控件
	 */
	private void setRadioButtonWidth(){
		hs_home_hotcompany = (HorizontalScrollView) headView.findViewById(R.id.hs_home_hotcompany);
		rg_home_hotcompany = (RadioGroup) headView.findViewById(R.id.rg_home_hotcompany);
		rg_home_hotcompany.check(R.id.rb_home_clean);
		
		rb_home_clean = (RadioButton) headView.findViewById(R.id.rb_home_clean);
		rb_home_move = (RadioButton) headView.findViewById(R.id.rb_home_move);
		rb_home_furniture = (RadioButton) headView.findViewById(R.id.rb_home_furniture);
		rb_home_pet = (RadioButton) headView.findViewById(R.id.rb_home_pet);
		rg_home_hotcompany.setOnCheckedChangeListener(new HotCompanyCheckChange());
		iv_home_arrow_left = (ImageView) headView.findViewById(R.id.iv_home_arrow_left);
		iv_home_arrow_right = (ImageView) headView.findViewById(R.id.iv_home_arrow_right);
		iv_home_arrow_left.setOnClickListener(this);
		iv_home_arrow_right.setOnClickListener(this);
		
		Message msg = new Message();
		msg.what = DELAY_COUNT_WIDTH;
		handler.sendMessageDelayed(msg, 10);
	}
	
	
	

	private void initAd() {
		adViewpager = (ViewPager) headView.findViewById(R.id.viewpager_ad);// 广告轮播viewpager;
		adViewpager.setAdapter(new AdPlayerAdaper(this.getActivity(), imgs));// 广告viewPager适配器
		adViewpager.setOffscreenPageLimit(1);
		startLoop();
		adViewpager.setOnPageChangeListener(this);
		adViewpager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					startLoop();
					break;
				case MotionEvent.ACTION_MOVE:
					stopLoop();
					break;
				case MotionEvent.ACTION_DOWN:
					stopLoop();
					break;

				}

				return false;
			}
		});
		try {
			// 利用Relect修改mScroller private Scroller mScroller = new
			// Scroller(context);
			Field field = ViewPager.class.getDeclaredField("mScroller");
			field.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					adViewpager.getContext(), new AccelerateInterpolator());
			field.set(adViewpager, scroller);
			scroller.setmDuration(200);// 设置图片平滑滚动持续的时间
		} catch (Exception e) {

		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (myFragmnet != null) {
			myFragmnet.clear();
		}
		if (listImageItem != null) {
			listImageItem.clear();
		}
		if (listViewItem != null) {
			listViewItem.clear();
		}
	}

	/*
	 * 添加监听事件
	 */
	private void clickListener() {
		// starbaby.setOnClickListener(this);
		adViewpager.setOnClickListener(this);
		ll_cityLocation.setOnClickListener(this);
		ll_home_foot_login.setOnClickListener(this);
		iv_home_iphone.setOnClickListener(this);
	}

	/*
	 * 为GridView添加图片资源;
	 */
	protected void loadgvImage() {
		int bg[] = new int[] { R.drawable.out_cake, R.drawable.out_flower,
				R.drawable.out_phone, R.drawable.out_gift, };
		String title[] = new String[] { "订蛋糕送鲜花", "9.9送花到家", "优惠预约", "预订送礼品" };
		String item[] = new String[] { "新用户专享", "新用户专享", "优惠后最高减免25元",
				"预订服务送百元大礼包" };
		for (int i = 0; i < bg.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("ItemImage", bg[i]);// 添加图像资源的ID
			map.put("title", title[i]);
			map.put("item", item[i]);
			listImageItem.add(map);
		}
	}

	/*
	 * 点击事件处理;
	 */
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.ll_gps:
			
			intent.setClass(getActivity(), CityLocationActivity.class);
			startActivityForResult(intent, REQ_ACTIVITY_CODE);
			
			break;
		case R.id.ll_home_foot_login:
			intent.setClass(getActivity(), LoginActivity.class);
			startActivity(intent);
			break;
		case R.id.iv_home_iphone:
			intent.setClass(getActivity(), HelpWebActivity.class);
			intent.putExtra("title", "活动详情");
			intent.putExtra("url", Define.URL_ACTIVITY);
			startActivity(intent);
			break;
		case R.id.iv_home_arrow_left:
//			hs_home_hotcompany.arrowScroll(View.FOCUS_LEFT);
//			hs_home_hotcompany.scrollTo(20, 0);
//			LogUtils.i("left");
			
			break;
		case R.id.iv_home_arrow_right:
//			hs_home_hotcompany.arrowScroll(View.FOCUS_LEFT);
//			hs_home_hotcompany.scrollBy(20, 0);
//			LogUtils.i("right");
			
			break;
		default:
			break;
		}

	}

	/**
	 * 处理消息
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				starCompanyAdapter.setData(starCompanyDataList);
				break;
			case MSG_LOAD_ERROR:
				starCompanyAdapter.setData(starCompanyDataList);
				break;
			case LOAD_TIMER_OVER:
				starCompanyAdapter.setData(starCompanyDataList);
				handler.removeCallbacks(loadTimerRunnable);
				progressDialog.dismiss();
				break;
			case DELAY_COUNT_WIDTH:
				
				int screenWidth = GetScreenInchUtil.getScreenWH(getActivity())[0];
				int arrowWidth = iv_home_arrow_right.getWidth() + iv_home_arrow_left.getWidth();
				int remainWidth = screenWidth - arrowWidth;
				int i = remainWidth/3;
				rb_home_move.setWidth(remainWidth/3);
				rb_home_furniture.setWidth(remainWidth/3);
				rb_home_furniture.setWidth(remainWidth/3);
				rb_home_pet.setWidth(remainWidth/3);
				
				break;
			
			case DELAY_AD_PAGER:
				/*
				 * 用来更新广告页面的显示
				 */
				adViewpager.setCurrentItem(msg.arg1);
				break;

			default:
				break;
			}

		};
	};

	private Timer timer;

	private void startLoop() {
		if (timer == null)// 当用户快速滑动的时候，touchup有几率触发两次，导致开启两个定时器，加个判断防止
		{
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					Message message = handler.obtainMessage();
					message.what = DELAY_AD_PAGER;
					message.arg1 = select;
					handler.sendMessage(message);
					select++;
				}
			}, 10, 2000);
		}
	}

	private void stopLoop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	public class PageChageListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@SuppressWarnings("deprecation")
		@Override
		public void onPageSelected(int arg0) {
			if (arg0 == 0) {
				arrayCircle[0].setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.shotcut_greesilde));
				arrayCircle[1].setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.shotcut_slidepoint));

			} else if (arg0 == 1) {
				arrayCircle[1].setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.shotcut_greesilde));
				arrayCircle[0].setBackgroundDrawable(getResources()
						.getDrawable(R.drawable.shotcut_slidepoint));
			}

		}

	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		select = adViewpager.getCurrentItem();
	}
	
	
	public class HotCompanyCheckChange implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.rb_home_clean:
				LogUtils.i("保洁");
				pageNum = 0;
				hotTypeID = 10;
				starCompanyDataList.clear();
				loadHotCompanyData();
				break;
			case R.id.rb_home_move:
				LogUtils.i("搬家");
				pageNum = 0;
				hotTypeID = 30;
				starCompanyDataList.clear();
				loadHotCompanyData();
				break;
			case R.id.rb_home_furniture:
				LogUtils.i("家具维修");
				pageNum = 0;
				hotTypeID = 50;
				starCompanyDataList.clear();
				loadHotCompanyData();
				break;
			case R.id.rb_home_pet:
				LogUtils.i("宠物服务");
				pageNum = 0;
				hotTypeID = 90;
				starCompanyDataList.clear();
				loadHotCompanyData();
				break;

			default:
				break;
			}
			
		}
		
	}
	

	
	/**
	 * 查询数据库 中 首页快捷下单数据
	 */
	private void queryAppHomeData() {
//		String sql1 = DbOprationBuilder.queryBuilderby("*", "coreTable",
//				"ListSqn", "0");
		String sql1 = DbOprationBuilder.queryBuilderAppHomeOrderDesc();
		CoreDbHelper core1 = new CoreDbHelper(getActivity());
		appHomeDataList = core1.queryCoredata(sql1);
	}

	
	
//	/**
//	 * 查询数据库 中 首页明星公司数据
//	 */
//	private void queryStarCompanyData() {
//		// 查询数据库
//		String sql = DbOprationBuilder.queryAllBuilder("starcompany");
////		CoreDbHelper core = new CoreDbHelper(getActivity());
//		DataBaseService ds = new DataBaseService(getActivity());
//		starCompanyDataList = ds.query(sql);
//	}

	
	
//	/**
//	 * 从网络请求首页明星公司数据
//	 */
//	public void loadStarCompanyData() {
//		seqStarCompany = MyApplication.SequenceNo++;
//		HsNetCommon_Req hsNetCommon_Req = new MsgInncDef().new HsNetCommon_Req();
//		hsNetCommon_Req.iSrcID = MyApplication.iCityLocationId;
//		hsNetCommon_Req.iSelectID = pageNum;
//		byte[] connData = HandleNetSendMsg.HandleHsStarCompanyReqToPro(hsNetCommon_Req, seqStarCompany);
//		
//		LogUtils.i("connData明星公司列表请求数据--sequence="+seqStarCompany +"/"+ Arrays.toString(connData) );
//
//		HouseSocketConn.pushtoList(connData);
//
////		progressDialog = new CustomProgressDialog(getActivity());
//		progressDialog = new CustomProgressDialog(getActivity());
//		progressDialog.show();
//		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
//	}
	
	
	
	/**
	 * 提取热门推荐信息请求消息
	 */
	public void loadHotCompanyData() {
		seqHotCompany = MyApplication.SequenceNo++;
		HsHotCompanyInfo_Req hotCompanyInfo_Req = new MsgInncDef().new HsHotCompanyInfo_Req();
		hotCompanyInfo_Req.uUserID = MyApplication.userId;
		hotCompanyInfo_Req.uAreaID = MyApplication.iCityLocationId;
		hotCompanyInfo_Req.uTypeID = hotTypeID;
		hotCompanyInfo_Req.iPosID = pageNum;
		byte[] connData = HandleNetSendMsg.HandleHsHotCompanyInfo_Req_ToPro(hotCompanyInfo_Req, seqHotCompany);
		
		LogUtils.i("connData热门公司列表请求数据--sequence="+seqHotCompany +"/"+ Arrays.toString(connData) );
		
		HouseSocketConn.pushtoList(connData);
		
//		progressDialog = new CustomProgressDialog(getActivity());
		progressDialog = new CustomProgressDialog(getActivity());
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
	}
	

//	/**
//	 * 从网络请求首页快捷下单数据
//	 * 
//	 * @param getHome
//	 * @param userId
//	 */
//	public void loadAppHomenInfoData(int getHome, int userId) {
//		seqAppHome = MyApplication.SequenceNo++;
//		HsAppHomenInfo_Req homeInfo = new MsgInncDef().new HsAppHomenInfo_Req();
//		homeInfo.bGetHome = getHome;
//		homeInfo.iUserID = userId;
//
//		byte[] homeInfo_pro = HandleNetSendMsg
//				.HandleHsAppHomenInfoReqToPro(homeInfo, seqAppHome);
//		LogUtils.i("首页结构体的请求数据--sequence="+seqAppHome +"/"+ Arrays.toString(homeInfo_pro));
//		HouseSocketConn.pushtoList(homeInfo_pro);
//	}
	
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqStarCompany == iSequence) {
//					processStarCompanyListData(recvTime);
				}
				else if (seqHotCompany == iSequence) {
					processHotCompanyListData(recvTime);
				}
			}
			
			if(Define.BROAD_FRAGMENT_RECV_APPHOME.equals(intent.getAction())){
				queryAppHomeData();
//				if(shotcut1_Fragment != null){
//					shotcut1_Fragment.setShotcutFragmentData(appHomeDataList);
//				}
				if(shotcut2_Fragment != null){
					shotcut2_Fragment.setShotcutFragmentData(appHomeDataList);
				}
			}
			else if(Define.BROAD_FRAGMENT_RECV_LOGIN.equals(intent.getAction())){
				pageNum = 0;
				loadHotCompanyData();
				if(pullToRefreshListView != null){
					pullToRefreshListView.setMode(Mode.PULL_FROM_END);
				}
//				listView.removeFooterView(footView);
			}
			else if(Define.BROAD_FRAGMENT_RECV_STARCOMPANY.equals(intent.getAction())){
//				queryStarCompanyData();
//				// 明星公司列表获取成功，为adapter设置数据
//				starCompanyAdapter.setData(starCompanyDataList);
			}
			
			
			
			
//			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
//				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
//				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
//
//				if (seqStarCompany == iSequence) {
//					processStarCompanyListData(iSequence);
//				}
//				else if (seqAppHome == iSequence) {
//					processAppHomeData(iSequence);
//				}
//			}
		}
	};
	
	
//	/**
//	 * 处理明星公司列表数据
//	 * 
//	 * @param iSequence
//	 */
//	private void processStarCompanyListData(long recvTime) {
//		HsStarCompanyGet_Resp starCompany_Resp = (HsStarCompanyGet_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
//		progressDialog.dismiss();
//		pullToRefreshListView.onRefreshComplete();
//		handler.removeCallbacks(loadTimerRunnable);
//		if (starCompany_Resp == null) {
//			return;
//		}
//		if (starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
//			List<HsCompanyInfo_Pro> startCompanyList = starCompany_Resp.companyList;
//
//			DataBaseService ds = new DataBaseService(getActivity());
//
//			if (pageNum == 0) {
//				starCompanyDataList.clear();
//			}
//			
//			if(startCompanyList.size() < Define.LOAD_DATA_NUM){
//				pullToRefreshListView.setMode(Mode.DISABLED);
//			}
//
//			for (int i = 0; i < startCompanyList.size(); i++) {
//				Map<String, String> startCompanyMap = new HashMap<String, String>();
//				startCompanyMap.put("iCompanyID", startCompanyList.get(i)
//						.getICompanyID() + "");
//				startCompanyMap.put("iOrderNum", startCompanyList.get(i)
//						.getIOrderNum() + "");
//				startCompanyMap.put("iValuNum", startCompanyList.get(i)
//						.getIValuNum() + "");
//				startCompanyMap.put("iStarLevel", startCompanyList.get(i)
//						.getIStarLevel() + "");
//				startCompanyMap.put("iAuthFlag", startCompanyList.get(i)
//						.getIAuthFlag() + "");
//				startCompanyMap.put("iFiling", startCompanyList.get(i)
//						.getIFiling() + "");
//				startCompanyMap.put("iOffLine", startCompanyList.get(i)
//						.getIOffLine() + "");
//				startCompanyMap.put("iNominate", startCompanyList.get(i)
//						.getINominate() + "");
//				startCompanyMap.put("szName", startCompanyList.get(i)
//						.getSzName() + "");
//				startCompanyMap.put("szAddr", startCompanyList.get(i)
//						.getSzAddr() + "");
//				startCompanyMap.put("szServiceInfo", startCompanyList.get(i)
//						.getSzServiceInfo() + "");
//				startCompanyMap.put("szCreateTime", startCompanyList.get(i)
//						.getSzCreateTime() + "");
//				startCompanyMap.put("szCompanyUrl", startCompanyList.get(i)
//						.getSzCompanyUrl() + "");
//				startCompanyMap.put("szCompanyInstr", startCompanyList.get(i)
//						.getSzCompanyInstr() + "");
//
//				String updateSql = DbOprationBuilder.updateStarCompanyAllBuilder(startCompanyMap, startCompanyList.get(i).getICompanyID()+"");
//				ds.update(updateSql);
//				starCompanyDataList.add(startCompanyMap);
//			}
//
//			Message message = new Message();
//			message.what = MSG_LOAD_SUCCESS;
//			handler.sendMessage(message);
//
//		}
//		else if(starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_GETRESULT_ISNULL_PRO){
//			pullToRefreshListView.setMode(Mode.DISABLED);
//			Message message = new Message();
//			message.what = MSG_LOAD_SUCCESS;
//			handler.sendMessage(message);
//		}
//		else {
//			String result = UniversalUtils
//					.judgeNetResult_Hs(starCompany_Resp.eOperResult);
//			Log.i("明星公司列表获取失败", result + "===========");
//
//			Message message = new Message();
//			message.what = MSG_LOAD_ERROR;
//			handler.sendMessage(message);
//		}
//	}
	
	
	/**
	 * 处理热门公司列表数据
	 * 
	 * @param iSequence
	 */
	private void processHotCompanyListData(long recvTime) {
		HsStarCompanyGet_Resp starCompany_Resp = (HsStarCompanyGet_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		progressDialog.dismiss();
		pullToRefreshListView.onRefreshComplete();
		handler.removeCallbacks(loadTimerRunnable);
		if (starCompany_Resp == null) {
			return;
		}
		if (starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			List<HsCompanyInfo_Pro> startCompanyList = starCompany_Resp.companyList;
			
			DataBaseService ds = new DataBaseService(getActivity());
			
			if (pageNum == 0) {
				starCompanyDataList.clear();
			}
			
			if(startCompanyList.size() < Define.LOAD_DATA_NUM){
				pullToRefreshListView.setMode(Mode.DISABLED);
			}else{
				pullToRefreshListView.setMode(Mode.PULL_FROM_END);
			}
			
			for (int i = 0; i < startCompanyList.size(); i++) {
				Map<String, String> startCompanyMap = new HashMap<String, String>();
				startCompanyMap.put("iCompanyID", startCompanyList.get(i)
						.getICompanyID() + "");
				startCompanyMap.put("iOrderNum", startCompanyList.get(i)
						.getIOrderNum() + "");
				startCompanyMap.put("iValuNum", startCompanyList.get(i)
						.getIValuNum() + "");
				startCompanyMap.put("iStarLevel", startCompanyList.get(i)
						.getIStarLevel() + "");
				startCompanyMap.put("iAuthFlag", startCompanyList.get(i)
						.getIAuthFlag() + "");
				startCompanyMap.put("iFiling", startCompanyList.get(i)
						.getIFiling() + "");
				startCompanyMap.put("iOffLine", startCompanyList.get(i)
						.getIOffLine() + "");
				startCompanyMap.put("iNominate", startCompanyList.get(i)
						.getINominate() + "");
				startCompanyMap.put("szName", startCompanyList.get(i)
						.getSzName() + "");
				startCompanyMap.put("szAddr", startCompanyList.get(i)
						.getSzAddr() + "");
				startCompanyMap.put("szServiceInfo", startCompanyList.get(i)
						.getSzServiceInfo() + "");
				startCompanyMap.put("szCreateTime", startCompanyList.get(i)
						.getSzCreateTime() + "");
				startCompanyMap.put("szCompanyUrl", startCompanyList.get(i)
						.getSzCompanyUrl() + "");
				startCompanyMap.put("szCompanyInstr", startCompanyList.get(i)
						.getSzCompanyInstr() + "");
				
				String updateSql = DbOprationBuilder.updateStarCompanyAllBuilder(startCompanyMap, startCompanyList.get(i).getICompanyID()+"");
				ds.update(updateSql);
				starCompanyDataList.add(startCompanyMap);
			}
			
			Message message = new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);
			
		}
		else if(starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_GETRESULT_ISNULL_PRO){
			pullToRefreshListView.setMode(Mode.DISABLED);
			Message message = new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);
		}
		else {
			String result = UniversalUtils
					.judgeNetResult_Hs(starCompany_Resp.eOperResult);
			Log.i("热门公司列表获取失败", result + "===========");
			
			Message message = new Message();
			message.what = MSG_LOAD_ERROR;
			handler.sendMessage(message);
		}
	}

	
//	/**
//	 * 处理首页appHome数据
//	 * @param iSequence
//	 */
//	private void processAppHomeData(int iSequence){
//		HsAppHomeInfo_Resp info_Resp = (HsAppHomeInfo_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(iSequence);
//		
//		if(info_Resp == null){
//			return;
//		}
//		if (info_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
//			Log.i("首页结构体数据已取得", "=============");
//			List<HsHomenTypeInfo_Pro> homeTypeList = info_Resp.homenTypeList;
//
//			CoreDbHelper dbHelper = new CoreDbHelper(getActivity());
//			String deleteSql = DbOprationBuilder
//					.deleteBuilder("coreTable");
//			dbHelper.deleteData(deleteSql);
//
//			for (int i = 0; i < homeTypeList.size(); i++) {
//				String sql = DbOprationBuilder
//						.insertAppHomeAllBuilder(homeTypeList.get(i));
//				dbHelper.insertCoreData(sql);
//			}
//
//			queryAppHomeData();
//
//			Message message = new Message();
//			message.what = messageAppHome;
//			handler.sendMessage(message);
//
//		} else {
//			String result = UniversalUtils
//					.judgeNetResult_Hs(info_Resp.eOperResult);
//			LogUtils.i("首页结构体数据获取失败"+ result + "===========");
//		}
//	}
	
	
	
	private class QueryDataTask extends
			AsyncTask<Void, Void, List<Map<String, String>>> {

		@Override
		protected List<Map<String, String>> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			queryAppHomeData();
//			queryStarCompanyData();
			return appHomeDataList;
		}

		@Override
		protected void onPostExecute(List<Map<String, String>> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			initViews();
			clickListener();

		}

	}

	/**
	 * 测试代码 模拟加载数据耗时操作
	 * 
	 * @author wangf
	 * 
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			pullToRefreshListView.onRefreshComplete();// 停止加载
		}
	}

	
	// 用于加载进行计时
	Runnable loadTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(LOAD_TIMER_OVER);
		}
	};

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub

		getActivity().unregisterReceiver(mReceiver);

		super.onDestroy();
	}
	

}
