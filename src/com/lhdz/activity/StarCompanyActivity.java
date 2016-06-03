package com.lhdz.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lhdz.adapter.StarCompanaydAdapter;
import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
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
import com.lhdz.util.LogUtils;
import com.lhdz.util.SearchUtil;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.ClearEditText;


/**
 * 明星公司页面
 * @author wangf
 *
 */
public class StarCompanyActivity extends BaseActivity implements
		OnItemClickListener, OnClickListener {

	private PullToRefreshListView listView;
	private TextView back;
	RadioGroup radioItem;
	private StarCompanaydAdapter adapter;
	private ClearEditText et_search;

	List<Map<String, String>> starCompanyDataList = new ArrayList<Map<String, String>>();// 明星公司列表数据
	List<Map<String, String>> appHomeDataList = null;// 首页快捷下单数据
	private MyApplication myApplication;
	private SearchUtil mSearchutil;// 搜索工具类;

	private final static int MSG_LOAD_SUCCESS = 1;
	private final static int MSG_LOAD_ERROR = 3;
	private final static int LOAD_TIMER_OVER = 2;

	private CustomProgressDialog progressDialog;

	private int seqStarCompany = -1;
	
	private int pageNum = 0;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				adapter.setData(starCompanyDataList);
				initSearch();
				break;
			case MSG_LOAD_ERROR:
				adapter.setData(starCompanyDataList);
				initSearch();
				break;
			case LOAD_TIMER_OVER:
				adapter.setData(starCompanyDataList);
				handler.removeCallbacks(loadTimerRunnable);
				progressDialog.dismiss();
				initSearch();
				break;
			}
			
		}
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_star_company);
		
		myApplication = (MyApplication) getApplication();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		//查询数据库中首页快捷下单信息
		queryAppHomeData();
//		//查询数据库中明星公司数据
//		queryStarCompanyData();
		
//		//若没有从数据库取到明星公司数据，需要从网络获取
//		if(starCompanyDataList.size() == 0){
//			loadStarCompanyData();
//		}
		
		initviews();
		adapter=new StarCompanaydAdapter(this,appHomeDataList);
		adapter.setData(starCompanyDataList);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		back.setOnClickListener(this);
		initSearch();

//		if(MyApplication.loginState){
			loadStarCompanyData();//请求明星公司数据
//		}else{
//			listView.setMode(Mode.DISABLED);
//			ToastUtils.show(this, "您未登录，请登录后查看更多精彩内容", 1);
//		}
	}

	/**
	 * 初始化搜索控件和数据
	 */
	private void initSearch() {
		et_search = (ClearEditText) findViewById(R.id.et_search);
		mSearchutil = new SearchUtil(getApplicationContext(),
				starCompanyDataList);
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
					adapter.setData(starCompanyDataList);
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

	/**
	 * 初始化页面控件
	 */
	@SuppressWarnings("unchecked")
	private void initviews() {
		listView = (PullToRefreshListView) findViewById(R.id.lv_star);
		// 设置ListView为下拉刷新和上拉加载模式
		listView.setMode(Mode.PULL_FROM_END);
		listView.setOnRefreshListener(new OnRefreshListener2() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				//下拉刷新
				
				String label = DateUtils.formatDateTime(StarCompanyActivity.this, System
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
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				//上拉加载
				
//				if(MyApplication.loginState){
				if(starCompanyDataList.size() == 0){
					pageNum = 0;
				}else{
					pageNum = pageNum + Define.LOAD_DATA_NUM;
				}
					loadStarCompanyData();
//				}
			}
		});
		
		
		
		back = (TextView) findViewById(R.id.iv_retreat);
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

//	/**
//	 * 判断相关的登录状态 若未登录则无法加载数据
//	 */
//	private void judgeStateAndLoadData() {
//		queryStarCompanyData();
//		new GetDataTask().execute();
//	}

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

	/**
	 * 查询数据库 中 首页快捷下单数据
	 */
	private void queryAppHomeData() {
		String sql1 = DbOprationBuilder.queryBuilderAppHomeOrderDesc();
		CoreDbHelper core1 = new CoreDbHelper(this);
		appHomeDataList = core1.queryCoredata(sql1);
	}

//	/*
//	 * 加载明星公司数据
//	 */
//	private void queryStarCompanyData() {
//		starCompanyDataList.clear();
//		// 查询数据库
//		String sql = DbOprationBuilder.queryAllBuilder("starcompany");
////		CoreDbHelper core = new CoreDbHelper(this);
//		DataBaseService ds = new DataBaseService(this);
//		starCompanyDataList = ds.query(sql);
//	}

	
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Map<String, String> starCompanyDetail = (Map<String, String>) parent.getAdapter().getItem(position);
		Intent intent = new Intent(this,CompanyDetailActivity.class);
		intent.putExtra("starCompanyDetail", (Serializable)starCompanyDetail);
//		intent.putExtra("appHomeDataList", (Serializable)appHomeDataList);
		startActivity(intent);
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_retreat:// 返回键
			this.finish();

			break;

		}
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

		progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
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

				if (seqStarCompany == iSequence) {
					processStarCompanyListData(recvTime);
				}

			}
		}
	};

	/**
	 *  处理明星公司列表数据
	 * @param recvTime
	 */
	private void processStarCompanyListData(long recvTime) {
		HsStarCompanyGet_Resp starCompany_Resp = (HsStarCompanyGet_Resp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		progressDialog.dismiss();
		listView.onRefreshComplete();
		handler.removeCallbacks(loadTimerRunnable);
		if (starCompany_Resp == null) {
			return;
		}
		if (starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			List<HsCompanyInfo_Pro> startCompanyList = starCompany_Resp.companyList;

			DataBaseService ds = new DataBaseService(StarCompanyActivity.this);
//			String deleteSql = DbOprationBuilder.deleteBuilder("starcompany");
//			ds.delete(deleteSql);
//
//			for (int i = 0; i < startCompanyList.size(); i++) {
//				String sql = DbOprationBuilder.insertStarCompanyAllBuilder(
//						myApplication.userId, startCompanyList.get(i));
//				ds.insert(sql);
//			}
//
//			queryStarCompanyData();
			
			if(pageNum == 0){
				starCompanyDataList.clear();
			}
			
			if(startCompanyList.size() < Define.LOAD_DATA_NUM){
				listView.setMode(Mode.DISABLED);
			}
			
			
			for (int i = 0; i < startCompanyList.size(); i++) {
				Map<String, String> startCompanyMap = new HashMap<String, String>();
				startCompanyMap.put("iCompanyID", startCompanyList.get(i).getICompanyID()+"");
				startCompanyMap.put("iOrderNum", startCompanyList.get(i).getIOrderNum()+"");
				startCompanyMap.put("iValuNum", startCompanyList.get(i).getIValuNum()+"");
				startCompanyMap.put("iStarLevel", startCompanyList.get(i).getIStarLevel()+"");
				startCompanyMap.put("iAuthFlag", startCompanyList.get(i).getIAuthFlag()+"");
				startCompanyMap.put("iFiling", startCompanyList.get(i).getIFiling()+"");
				startCompanyMap.put("iOffLine", startCompanyList.get(i).getIOffLine()+"");
				startCompanyMap.put("iNominate", startCompanyList.get(i).getINominate()+"");
				startCompanyMap.put("szName", startCompanyList.get(i).getSzName()+"");
				startCompanyMap.put("szAddr", startCompanyList.get(i).getSzAddr()+"");
				startCompanyMap.put("szServiceInfo", startCompanyList.get(i).getSzServiceInfo()+"");
				startCompanyMap.put("szCreateTime", startCompanyList.get(i).getSzCreateTime()+"");
				startCompanyMap.put("szCompanyUrl", startCompanyList.get(i).getSzCompanyUrl()+"");
				startCompanyMap.put("szCompanyInstr", startCompanyList.get(i).getSzCompanyInstr()+"");
				
				String updateSql = DbOprationBuilder.updateStarCompanyAllBuilder(startCompanyMap, startCompanyList.get(i).getICompanyID()+"");
				ds.update(updateSql);
				starCompanyDataList.add(startCompanyMap);
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
	 * 用于加载进行计时
	 */
	Runnable loadTimerRunnable = new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(LOAD_TIMER_OVER);
		}
	};
	
	
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	};

}
