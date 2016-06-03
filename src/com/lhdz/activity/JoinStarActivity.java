package com.lhdz.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lhdz.adapter.JoinStarAdapter;
import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserAddCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsGetUserJoinCompanyList_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsStarCompanyGet_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.pulltorefresh.PullToRefreshBase;
import com.lhdz.pulltorefresh.PullToRefreshBase.Mode;
import com.lhdz.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.lhdz.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.lhdz.pulltorefresh.PullToRefreshListView;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.SearchUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.ClearEditText;

/**
 * 加入明星公司
 * @author wangf
 *
 */
public class JoinStarActivity extends BaseActivity implements OnClickListener,
		OnItemClickListener {
	private PullToRefreshListView listView;
	private TextView back;
	RadioGroup radioItem;
	private JoinStarAdapter adapter;
	MyApplication myApplication = null;
	private ClearEditText et_search;
	private SearchUtil mSearchutil;

	List<Map<String, String>> appHomeDataList = null;//首页快捷下单数据
	List<Map<String, String>> dbStarCompanyList = new ArrayList<Map<String, String>>();//数据库中 明星公司列表数据--用于加入明星公司
	List<Map<String, String>> joinStarCompanyList = new ArrayList<Map<String, String>>();//用于查询已加入或有加入状态的明星公司数据
	List<Map<String, String>> starCompanyDataList = new ArrayList<Map<String, String>>();//用于查询已加入或有加入状态的明星公司数据
	
//	List<Map<String, String>> netJoinStarCompanyList = new ArrayList<Map<String,String>>();//网络请求的  已加入明星公司数据
//	List<Map<String, String>> adapterStarCompanyList = new ArrayList<Map<String,String>>();//用于在adapter中显示的明星公司数据

//	List<Map<String, String>> starCompanyStorageList = new ArrayList<Map<String, String>>();//所有明星公司的存储数据--判断有无明星公司数据
	
	private CustomProgressDialog progressDialog;
	
	private final static int datalisterror = 0;
	private final static int datalistsuccess = 1;
	private final static int datalistnull = 2;
	private final static int MSG_LOAD_STORAGE_SUCCESS = 3;
	private final static int LOAD_TIMER_OVER = 4;
	private final static int MSG_LOAD_SUCCESS = 5;
	private final static int MSG_LOAD_ERROR = 6;
	
	private final static int requestCode = 101;//activity的请求码
	
	private int seqJoinStar = -1;
	private int seqStarCompany = -1;
	
	private int pageNum = 0;
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case MSG_LOAD_ERROR:
				if(MyApplication.loginState){
					queryAllStarCompanyData();
					adapter.setData(dbStarCompanyList);
				}else{
					adapter.setData(starCompanyDataList);
				}
				initSearch();
//				new GetDataTask().execute();
				break;
			case MSG_LOAD_SUCCESS:
				if(MyApplication.loginState){
					queryAllStarCompanyData();
					adapter.setData(dbStarCompanyList);
				}else{
					adapter.setData(starCompanyDataList);
				}
				initSearch();
//				new GetDataTask().execute();
				break;
			case datalistsuccess:
				if(MyApplication.loginState){
					queryAllStarCompanyData();
					adapter.setData(dbStarCompanyList);
				}else{
					adapter.setData(starCompanyDataList);
				}
				initSearch();
//				new GetDataTask().execute();
				break;
			case datalisterror:
				if(MyApplication.loginState){
					queryAllStarCompanyData();
					adapter.setData(dbStarCompanyList);
				}else{
					adapter.setData(starCompanyDataList);
				}
				initSearch();
//				new GetDataTask().execute();
				break;
			case datalistnull:
				if(MyApplication.loginState){
					queryAllStarCompanyData();
					adapter.setData(dbStarCompanyList);
				}else{
					adapter.setData(starCompanyDataList);
				}
				initSearch();
//				new GetDataTask().execute();
				break;
//			case MSG_LOAD_STORAGE_SUCCESS:
//				loadJoinData();
//				break;
			case LOAD_TIMER_OVER:
				handler.removeCallbacks(loadTimerRunnable);
				progressDialog.dismiss();
				progressDialog.setCanceledOnTouchOutside(false);
				initSearch();
				break;
			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_star);
		myApplication = (MyApplication) getApplication();
		
		queryAppHomeData();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initviews();
		
		progressDialog = new CustomProgressDialog(this);

		if (myApplication.loginState) {
			loadJoinData();
		}else{
//			listView.setMode(Mode.DISABLED);
//			ToastUtils.show(this, "您未登录，请登录后查看更多精彩内容", 1);
			loadStarCompanyData();
		}
		
		judgeStateAndLoadData();
		
//		initSearch();//搜索

	}
	
	
	/**
	 * 初始化搜索控件及搜索所需的数据
	 */
	private void initSearch() {
		et_search = (ClearEditText) findViewById(R.id.et_search);
		if(MyApplication.loginState){
			mSearchutil = new SearchUtil(getApplicationContext(),dbStarCompanyList);
		}else{
			mSearchutil = new SearchUtil(getApplicationContext(),starCompanyDataList);
		}
		et_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String content = et_search.getText().toString();
				// 当输入的字符变化时过滤的值随之跟着变化
				List<Map<String, String>> filterStrData = mSearchutil
						.filterStrData(s.toString().toLowerCase(), "szName");
				adapter.setData(filterStrData);
				// 如果输入框内无字符的话设置回原来的数据;
				if (content == null || content.equals("")) {
					if(MyApplication.loginState){
						adapter.setData(dbStarCompanyList);
					}else{
						adapter.setData(starCompanyDataList);
					}
					
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
//		queryAllStarCompanyData();
//		adapter.setData(dbStarCompanyList);
//		initSearch();
		
//		queryAllStarCompanyData();
//		adapter.setData(dbStarCompanyList);
//		judgeStateAndLoadData();
		
	}
	
	
	/**
	 * 根据登录状态判断需要的数据
	 */
	private void judgeStateAndLoadData(){
		queryAllStarCompanyData();
		if (myApplication.loginState) {
			if (dbStarCompanyList.size() == 0) {
				return;
			}
			if(myApplication.userId != Integer.parseInt(dbStarCompanyList.get(0).get("userId"))){
				DataBaseService ds = new DataBaseService(JoinStarActivity.this);
				//若当前登录的用户与数据库中的明星公司表中的userID不一致，则需清除所有明星公司的加入状态
				String updateStateSql = DbOprationBuilder.updateBuider("starcompany","iVerifyFlag","0");
				//若当前登录的用户与数据库中的明星公司表中的userID不一致，同时需要更新明星公司表中的所有userID
 				String updateUserIDSql = DbOprationBuilder.updateBuider("starcompany","userId",myApplication.userId+"");
				ds.update(updateStateSql);
				ds.update(updateUserIDSql);
			}
			
		} 
	}
//	/**
//	 * 根据登录状态判断需要的数据
//	 */
//	private void judgeStateAndLoadData(){
//		queryAllStarCompanyData();
//		
//		if (myApplication.loginState) {
//			
//			//若当前数据库中明星公司数据为空，则请求网络
//			if(dbStarCompanyList.size() == 0){
////				progressDialog = new CustomProgressDialog(this);
////				progressDialog.show();
////				handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
//				loadJoinData();// 请求已加入的明星公司数据
//				return;
//			}
//			
//			if(myApplication.userId != Integer.parseInt(dbStarCompanyList.get(0).get("userId"))){
//				
//				DataBaseService ds = new DataBaseService(JoinStarActivity.this);
//				//若当前登录的用户与数据库中的明星公司表中的userID不一致，则需清除所有明星公司的加入状态
//				String updateStateSql = DbOprationBuilder.updateBuider("starcompany","iVerifyFlag","0");
//				//若当前登录的用户与数据库中的明星公司表中的userID不一致，同时需要更新明星公司表中的所有userID
//				String updateUserIDSql = DbOprationBuilder.updateBuider("starcompany","userId",myApplication.userId+"");
//				ds.update(updateStateSql);
//				ds.update(updateUserIDSql);
//				
//				//请求网络，重新获取已加入的数据
////				progressDialog = new CustomProgressDialog(this);
////				progressDialog.show();
////				handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
//				
////				loadJoinData();// 请求已加入的明星公司数据
//				return;
//			}
//			
//			//若数据库中有数据，并且当前登录用户与数据库中的明星公司表中的userID一致，则直接显示ListView
//			adapter.setData(dbStarCompanyList);
//			new GetDataTask().execute();
//		} else {
//			adapter.setData(dbStarCompanyList);
//			new GetDataTask().execute();
//		}
//	}
	
	
	

	/**
	 * 初始化页面控件
	 */
	private void initviews() {
		listView = (PullToRefreshListView) findViewById(R.id.lv_star);
		// 设置ListView为下拉刷新和上拉加载模式
		listView.setMode(Mode.PULL_FROM_END);
		// 下拉刷新
		listView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				String label = DateUtils.formatDateTime(JoinStarActivity.this, System
						.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME
								| DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel("上次更新时间：" + label);
				
//				if(MyApplication.loginState){
					pageNum = 0;
					listView.setMode(Mode.PULL_FROM_END);
					loadStarCompanyData();
//				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
//				if(MyApplication.loginState){
					progressDialog.show();
					handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
					pageNum = pageNum + Define.LOAD_DATA_NUM;
					loadStarCompanyData();
//				}
			}

			
		});
	
		
		
		
		adapter=new JoinStarAdapter(this,appHomeDataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		
		back = (TextView) findViewById(R.id.iv_retreat);
		back.setOnClickListener(this);
		radioItem = (RadioGroup) findViewById(R.id.rg_checktag);
		radioItem.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_companyname:

					break;

				}
			}
		});

	}



	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Map<String, String> starCompany = (Map<String, String>) parent.getAdapter().getItem(position);
		
		//根据状态跳转
		Intent intent = null;
		if(!myApplication.loginState){
			intent = new Intent(JoinStarActivity.this, LoginActivity.class);
			startActivity(intent);
			JoinStarActivity.this.finish();
			return;
		}
		//获取当前选中的item的状态标识
		int iVerifyFlag = 0;
		if(!UniversalUtils.isStringEmpty(starCompany.get("iVerifyFlag"))){
			iVerifyFlag = Integer.parseInt(starCompany.get("iVerifyFlag"));
		}
		
		//申请中
		if(NetHouseMsgType.JOINSTATE_APPLY == iVerifyFlag){
//			ToastUtils.show(JoinStarActivity.this, "申请中", 0);
			return;
		}
		//审核中
		if(NetHouseMsgType.JOINSTATE_SHENHE == iVerifyFlag){
//			ToastUtils.show(JoinStarActivity.this, "审核中", 0);
			return;
		}
		//已加入
		if(NetHouseMsgType.JOINSTATE_JOIN == iVerifyFlag){
			intent = new Intent(JoinStarActivity.this,TerminationActivity.class);
			intent.putExtra("starCompanyInfo", (Serializable)starCompany);
//			startActivity(intent);
			startActivityForResult(intent, requestCode);
			return;
		}
		
		//不是（ 申请中 审核中 已加入） 这三种状态的则需要进入   申请加入   页面
		intent = new Intent(JoinStarActivity.this, ApplyJoinActivity.class);
		intent.putExtra("starCompanyInfo", (Serializable)starCompany);
//		startActivity(intent);
		startActivityForResult(intent, requestCode);
		
//		//标识不是已加入则需要加入
//		if(NetHouseMsgType.JOINSTATE_APPLY != iVerifyFlag){
//			intent = new Intent(JoinStarActivity.this, ApplyJoinActivity.class);
//			intent.putExtra("starCompanyInfo", (Serializable)starCompany);
//			startActivity(intent);
//			return;
//		}
//		//标识为已加入则需要解约
//		if(NetHouseMsgType.JOINSTATE_APPLY == iVerifyFlag){
//			intent = new Intent(JoinStarActivity.this,TerminationActivity.class);
//			intent.putExtra("starCompanyInfo", (Serializable)starCompany);
//			startActivity(intent);
//			return;
//		}
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_retreat:// 返回键
			this.finish();

			break;

		}
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		String strCompanyID = "";
		if(data != null){
			strCompanyID = data.getStringExtra("iCompanyID");
		}
		
		switch (resultCode) {
		case Define.RESULTCODE_APPLYJOIN://加入明星公司成功
			
			if (!UniversalUtils.isStringEmpty(strCompanyID)) {
				for (int i = 0; i < starCompanyDataList.size(); i++) {
					String starCompanyId = starCompanyDataList.get(i).get("iCompanyID");
					if (starCompanyId.equals(strCompanyID)) {
						starCompanyDataList.remove(i);
						break;
					}
				}
			}
			queryAllStarCompanyData();
			adapter.setData(dbStarCompanyList);
			initSearch();
			
			break;
			
		case Define.RESULTCODE_TERMINATION://退出明星公司成功
			
			if(!UniversalUtils.isStringEmpty(strCompanyID)){	
				for (int i = 0; i < starCompanyDataList.size(); i++) {
					String starCompanyId = starCompanyDataList.get(i).get("iCompanyID");
					if(starCompanyId.equals(strCompanyID)){
						starCompanyDataList.remove(i);
						break;
					}
				}
			}
			queryAllStarCompanyData();
			adapter.setData(dbStarCompanyList);
			initSearch();
			
			break;

		default:
			break;
		}
	}
	
	
	
	/**
	 * 查询数据库 中 首页快捷下单数据
	 */
	private void queryAppHomeData() {
		String sql1 = DbOprationBuilder.queryBuilderAppHomeOrderDesc();
		CoreDbHelper core1 = new CoreDbHelper(this);
		appHomeDataList = core1.queryCoredata(sql1);
	}

//	/**
//	 * 加载明星公司数据
//	 */
//	private void queryAllStarCompanyData() {
//		dbStarCompanyList.clear();
//		joinStarCompanyList.clear();
//		// 查询数据库服务
//		DataBaseService ds = new DataBaseService(this);
//		//申请中的明星公司的数据
//		List<Map<String, String>> applyCompanyList = new ArrayList<Map<String,String>>();
//		//审核中的明星公司的数据
//		List<Map<String, String>> auditCompanyList = new ArrayList<Map<String,String>>();
//		//已加入的明星公司的数据
//		List<Map<String, String>> joinCompanyList = new ArrayList<Map<String,String>>();
//		//无加入状态的明星公司的数据
//		List<Map<String, String>> noStateCompanyList = new ArrayList<Map<String,String>>();
//		
//		String applySql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_APPLY+"");
//		String auditSql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_SHENHE+"");
//		String joinSql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_JOIN+"");
//		String noJoinSql = DbOprationBuilder.queryStarCompanyBuilderNot();
//		
//		//根据不同条件查询数据库
//		applyCompanyList.addAll(ds.query(applySql));
//		auditCompanyList.addAll(ds.query(auditSql));
//		joinCompanyList.addAll(ds.query(joinSql));
//		noStateCompanyList.addAll(ds.query(noJoinSql));
//		
//		//将所有数据按顺序显示--申请中 -> 审核中 -> 已加入 -> 无加入状态
//		dbStarCompanyList.addAll(applyCompanyList);
//		dbStarCompanyList.addAll(auditCompanyList);
//		dbStarCompanyList.addAll(joinCompanyList);
//		
//		joinStarCompanyList.addAll(dbStarCompanyList);//将有加入状态的数据放入joinStarCompanyList中
//		
//		dbStarCompanyList.addAll(noStateCompanyList);
//	}
	
	/**
	 * 加载明星公司数据
	 */
	private void queryAllStarCompanyData() {
		dbStarCompanyList.clear();
		// 查询数据库服务
		DataBaseService ds = new DataBaseService(this);
		//申请中的明星公司的数据
		List<Map<String, String>> applyCompanyList = new ArrayList<Map<String,String>>();
		//审核中的明星公司的数据
		List<Map<String, String>> auditCompanyList = new ArrayList<Map<String,String>>();
		//已加入的明星公司的数据
		List<Map<String, String>> joinCompanyList = new ArrayList<Map<String,String>>();
		
		String applySql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_APPLY+"");
		String auditSql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_SHENHE+"");
		String joinSql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_JOIN+"");
		
		//根据不同条件查询数据库
		applyCompanyList.addAll(ds.query(applySql));
		auditCompanyList.addAll(ds.query(auditSql));
		joinCompanyList.addAll(ds.query(joinSql));
		
		//将所有数据按顺序显示--申请中 -> 审核中 -> 已加入 -> 无加入状态
		dbStarCompanyList.addAll(applyCompanyList);
		dbStarCompanyList.addAll(auditCompanyList);
		dbStarCompanyList.addAll(joinCompanyList);
		
		dbStarCompanyList.addAll(starCompanyDataList);
	}
	
	
	
	/**
	 * 查询有加入状态的的公司信息
	 */
	private List<Map<String, String>> queryHasStateStarCompanyData(){
		List<Map<String, String>> hasStateCompanyList = new ArrayList<Map<String,String>>();
		
		DataBaseService ds = new DataBaseService(this);
		//申请中的明星公司的数据
		List<Map<String, String>> applyCompanyList = new ArrayList<Map<String,String>>();
		//审核中的明星公司的数据
		List<Map<String, String>> auditCompanyList = new ArrayList<Map<String,String>>();
		//已加入的明星公司的数据
		List<Map<String, String>> joinCompanyList = new ArrayList<Map<String,String>>();
		
		String applySql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_APPLY+"");
		String auditSql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_SHENHE+"");
		String joinSql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iVerifyFlag", NetHouseMsgType.JOINSTATE_JOIN+"");
		
		//根据不同条件查询数据库
		applyCompanyList.addAll(ds.query(applySql));
		auditCompanyList.addAll(ds.query(auditSql));
		joinCompanyList.addAll(ds.query(joinSql));
		
		hasStateCompanyList.addAll(applyCompanyList);
		hasStateCompanyList.addAll(auditCompanyList);
		hasStateCompanyList.addAll(joinCompanyList);
		
		return hasStateCompanyList;
	}
	
	
	
	/**
	 * 用于加载进行计时
	 */
	Runnable loadTimerRunnable = new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(LOAD_TIMER_OVER);
		}
	};
	
	
//	/*
//	 * 明星公司数据
//	 */
//	private void queryStarCompanyStorageData() {
//		starCompanyStorageList.clear();
//		// 查询数据库
//		String sql = DbOprationBuilder.queryAllBuilder("starcompany");
////		CoreDbHelper core = new CoreDbHelper(this);
//		DataBaseService ds = new DataBaseService(this);
//		starCompanyStorageList.addAll(ds.query(sql));
//		initSearch();
//	}
	
	
	
	/**
	 * 从网络请求已经加入的明星公司数据
	 */
	public void loadJoinData() {
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		seqJoinStar = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommon_Req = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommon_Req.iSrcID = myApplication.userId;
		hsNetCommon_Req.iSelectID = 0;
		byte[] connData = HandleNetSendMsg.HandleHsUserJoinCompanyList_ReqToPro(hsNetCommon_Req,seqJoinStar);

		HouseSocketConn.pushtoList(connData);
		LogUtils.i("已加入 明星公司列表请求数据--sequence="+seqJoinStar +"/"+connData + "=============");
	}
	
	
	/**
	 * 当本地数据库没有明星公司数据时， 从网络请求首页明星公司数据
	 */
	public void loadStarCompanyData() {
		seqStarCompany = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommon_Req = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommon_Req.iSrcID = MyApplication.iCityLocationId;
		hsNetCommon_Req.iSelectID = pageNum;
		byte[] connData = HandleNetSendMsg.HandleHsStarCompanyReqToPro(
				hsNetCommon_Req, seqStarCompany);

		HouseSocketConn.pushtoList(connData);
		LogUtils.i("connData明星公司列表请求数据--sequence=" + seqStarCompany + "/"
				+ Arrays.toString(connData) + "=============");

	}
	
	
	/**
	 * 广播接收者
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqJoinStar == iSequence) {
					processJoinStarData(recvTime);
				}
				else if(seqStarCompany == iSequence){
					processStarCompanyListData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理加入明星公司响应数据
	 * @param iSequence
	 */
	private void processJoinStarData(long recvTime){
		HsGetUserJoinCompanyList_Resp starCompany_Resp = (HsGetUserJoinCompanyList_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(starCompany_Resp == null){
			return;
		}
//		progressDialog.dismiss();
//		handler.removeCallbacks(loadTimerRunnable);
		if (starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			List<HsUserAddCompanyInfo_Pro> startCompanyList = starCompany_Resp.companyList;
			
			DataBaseService ds = new DataBaseService(JoinStarActivity.this);
//			String updateVerifyFlag = DbOprationBuilder.updateBuider("starcompany", "iVerifyFlag", "0");
//			String updateVerifyName = DbOprationBuilder.updateBuider("starcompany", "szVerifyName", "");
//			ds.update(updateVerifyFlag);
//			ds.update(updateVerifyName);
//			
//			for (int i = 0; i < startCompanyList.size(); i++) {
//				String updateSql = DbOprationBuilder.updateStarCompanyBuider(
//						startCompanyList.get(i).getIVerifyFlag()+"", 
//						startCompanyList.get(i).getSzVerifyName(), 
//						startCompanyList.get(i).getICompanyID()+"");
//				ds.update(updateSql);
//			}
			
			String updateVerifyFlag = DbOprationBuilder.updateBuider("starcompany", "iVerifyFlag", "0");
			String updateVerifyName = DbOprationBuilder.updateBuider("starcompany", "szVerifyName", "");
			ds.update(updateVerifyFlag);
			ds.update(updateVerifyName);
			
			for (int i = 0; i < startCompanyList.size(); i++) {
				setDbStarCompanyData(startCompanyList.get(i));
				String updateSql = DbOprationBuilder.updateStarCompanyBuider(
						startCompanyList.get(i).getIVerifyFlag()+"", 
						startCompanyList.get(i).getSzVerifyName(), 
						startCompanyList.get(i).getICompanyID()+"");
				ds.update(updateSql);
			}
			
			
//			Message message =new Message();
//			message.what = datalistsuccess;
//			handler.sendMessage(message);
			loadStarCompanyData();
			

		} else if(starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_GETRESULT_ISNULL_PRO){
			
			DataBaseService ds = new DataBaseService(JoinStarActivity.this);
			String updateVerifyFlag = DbOprationBuilder.updateBuider("starcompany", "iVerifyFlag", "0");
			String updateVerifyName = DbOprationBuilder.updateBuider("starcompany", "szVerifyName", "");
			ds.update(updateVerifyFlag);
			ds.update(updateVerifyName);
			
			Message message =new Message();
			message.what = datalistnull;
			handler.sendMessage(message);
			loadStarCompanyData();
		}else{
			Message message =new Message();
			message.what = datalisterror;
			handler.sendMessage(message);
			loadStarCompanyData();
			
			String result = UniversalUtils.judgeNetResult_Hs(starCompany_Resp.eOperResult);
			LogUtils.i("已加入 明星公司 列表数据失败"+ "=="+result +"==");
		}
	}
	
	
	
		/** 
		 * 处理明星公司列表数据
		 * @param recvTime 接收数据的时间
		 */
		private void processStarCompanyListData(long recvTime) {
			HsStarCompanyGet_Resp starCompany_Resp = (HsStarCompanyGet_Resp) HandleMsgDistribute
					.getInstance().queryCompleteMsg(recvTime);
			
			progressDialog.dismiss();
			handler.removeCallbacks(loadTimerRunnable);
			listView.onRefreshComplete();
			if (starCompany_Resp == null) {
				return;
			}
		if (starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			List<HsCompanyInfo_Pro> startCompanyList = starCompany_Resp.companyList;

			List<Map<String, String>> hasStateCompanyList = queryHasStateStarCompanyData();

			if (pageNum == 0) {
				starCompanyDataList.clear();
			}

			if (startCompanyList.size() < Define.LOAD_DATA_NUM) {
				listView.setMode(Mode.DISABLED);
			}

			for (int i = 0; i < startCompanyList.size(); i++) {
				Map<String, String> startCompanyMap = new HashMap<String, String>();
				startCompanyMap.put("iCompanyID", startCompanyList.get(i).getICompanyID() + "");
				startCompanyMap.put("iOrderNum", startCompanyList.get(i).getIOrderNum() + "");
				startCompanyMap.put("iValuNum", startCompanyList.get(i).getIValuNum() + "");
				startCompanyMap.put("iStarLevel", startCompanyList.get(i).getIStarLevel() + "");
				startCompanyMap.put("iAuthFlag", startCompanyList.get(i).getIAuthFlag() + "");
				startCompanyMap.put("iFiling", startCompanyList.get(i).getIFiling() + "");
				startCompanyMap.put("iOffLine", startCompanyList.get(i).getIOffLine() + "");
				startCompanyMap.put("iNominate", startCompanyList.get(i).getINominate() + "");
				startCompanyMap.put("szName", startCompanyList.get(i).getSzName() + "");
				startCompanyMap.put("szAddr", startCompanyList.get(i).getSzAddr() + "");
				startCompanyMap.put("szServiceInfo", startCompanyList.get(i).getSzServiceInfo() + "");
				startCompanyMap.put("szCreateTime", startCompanyList.get(i).getSzCreateTime() + "");
				startCompanyMap.put("szCompanyUrl", startCompanyList.get(i).getSzCompanyUrl() + "");
				startCompanyMap.put("szCompanyInstr", startCompanyList.get(i).getSzCompanyInstr() + "");

				if (MyApplication.loginState) {
					boolean isSelect = true;

					for (int j = 0; j < hasStateCompanyList.size(); j++) {
						if (hasStateCompanyList.get(j).get("iCompanyID").equals(startCompanyMap.get("iCompanyID"))) {
							isSelect = false;
							break;
						}
					}

					if (isSelect) {
						starCompanyDataList.add(startCompanyMap);
					}
				}else{
					starCompanyDataList.add(startCompanyMap);
				}
			}

			Message message = new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);

		}
			else if(starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_GETRESULT_ISNULL_PRO){
				listView.setMode(Mode.DISABLED);
				Message message = new Message();
				message.what = MSG_LOAD_SUCCESS;
				handler.sendMessage(message);
			}
			else {
				String result = UniversalUtils
						.judgeNetResult_Hs(starCompany_Resp.eOperResult);
				Log.i("明星公司列表获取失败", result + "===========");
				
				Message message = new Message();
				message.what = MSG_LOAD_ERROR;
				handler.sendMessage(message);
			}
		}

	
	
	/**
	 * 将已经加入的明星公司数据写入数据库中
	 * @param addCompanyInfo_Pro
	 */
	private void setDbStarCompanyData(HsUserAddCompanyInfo_Pro addCompanyInfo_Pro){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iCompanyID", addCompanyInfo_Pro.getICompanyID()+"");
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		if(starCompanyDataList.size() != 0){
			return;
		}
		
		
		//抢单公司信息中  无公司介绍，多了抢单公司要求的预付款金额
		Map<String, String> startCompanyMap = new HashMap<String, String>();
		startCompanyMap.put("iCompanyID", addCompanyInfo_Pro.getICompanyID()+"");
		startCompanyMap.put("iOrderNum", addCompanyInfo_Pro.getIOrderNum()+"");
		startCompanyMap.put("iValuNum", "");
		startCompanyMap.put("iStarLevel", addCompanyInfo_Pro.getIStarLevel()+"");
		startCompanyMap.put("iAuthFlag", addCompanyInfo_Pro.getIAuthFlag()+"");
		startCompanyMap.put("iFiling",addCompanyInfo_Pro.getIFiling()+"");
		startCompanyMap.put("iOffLine", addCompanyInfo_Pro.getIOffLine()+"");
		startCompanyMap.put("iNominate", addCompanyInfo_Pro.getINominate()+"");
		startCompanyMap.put("szName", addCompanyInfo_Pro.getSzName()+"");
		startCompanyMap.put("szAddr", addCompanyInfo_Pro.getSzAddr()+"");
		startCompanyMap.put("szServiceInfo", addCompanyInfo_Pro.getSzServiceInfo()+"");
		startCompanyMap.put("szCreateTime", addCompanyInfo_Pro.getSzCreateTime()+"");
		startCompanyMap.put("szCompanyUrl", addCompanyInfo_Pro.getSzCompanyUrl()+"");
		startCompanyMap.put("szCompanyInstr", "");
		
		String insertSql = DbOprationBuilder.insertStarCompanyAllBuilder(MyApplication.userId, startCompanyMap);
		ds.insert(insertSql);
	}
	

	/**
	 * 模拟加载数据耗时操作
	 * 
	 * @author wangf
	 * 
	 */
	private class GetDataTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return "";
		}
		@Override
		protected void onPostExecute(String result) {
			listView.onRefreshComplete();
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
