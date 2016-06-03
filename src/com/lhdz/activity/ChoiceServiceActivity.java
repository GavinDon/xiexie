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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.adapter.ChoiceServiceAdapter;
import com.lhdz.adapter.ChoiceServiceAdapter.ChoiceCompanyCallBack;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsCommon_Notify;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsOrderRaceDetailGet_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;

/**
 *  选择服务-抢单公司
 * @author wangf
 *
 */
public class ChoiceServiceActivity extends BaseActivity implements OnClickListener , OnCheckedChangeListener{
	
	public static ChoiceServiceActivity instance = null;
	
	private TextView title;//页面标题
	private TextView back;//返回按钮
	
	private ListView mLv;
	private ChoiceServiceAdapter mAdapter;
	private ArrayList<String> mList;
	
	private RadioGroup mGp;
	private RadioButton rb_company;
	private RadioButton rb_star;
	private RadioButton rb_offer;
	private RadioButton rb_more;
	
	private int uOrderID;//订单id
	private String szOrderValue = "";//订单号
	private String szOrderStateName = "";//订单状态
	
	private List<HsRaceCompanyInfo_Pro> raceCompanyList ;
	
	private HsRaceCompanyInfo_Pro choiceCompanyInfo;//用户选定的抢单公司详情
	
	private MyApplication myApplication;
	private final static int MSG_LOAD_SUCCESS = 0;
	private final static int MSG_LOAD_ERROR = 1;
	private final static int LOAD_TIMER_OVER = 2;
	private final static int MSG_CHOICE_COMPANY_SUCCESS = 3;
	private final static int MSG_CHOICE_COMPANY_ERROR = 4;
//	private final static int LOAD_TIMER_OVER = 3;
	
	private CustomProgressDialog progressDialog;
	
	private int seqChoiceServiceListNo = -1;
	private int seqChoiceCompanyNo = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choice_service);
		
		myApplication = (MyApplication) getApplication();
		instance = this;
		
		
//		orderDetailInfo = (HsUserOrderDailInfo_Pro) getIntent().getSerializableExtra("orderInfo");
		uOrderID = getIntent().getIntExtra("uOrderID", -1);//订单id
		szOrderValue = getIntent().getStringExtra("szOrderValue");//订单号
		szOrderStateName = getIntent().getStringExtra("szOrderStateName");//订单状态
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		raceCompanyList = new ArrayList<HsRaceCompanyInfo_Pro>();
		
		// 初始化控件
		initViews();
		
		//初始化数据
		loadRaceCompanyData();
//		initData();
		
		
		
	}
	
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				mAdapter.setData(raceCompanyList);
				//停止下拉刷新
//				mRefreshListView.onRefreshComplete();
				//查询数据库中订单数据--此处需要根据目前页面标签所在的位置来放置数据
//				getOrderListByRadioState();
//				mAdapter.setData(dbOrderList);
				break;
			case MSG_LOAD_ERROR:
				//停止下拉刷新
//				mRefreshListView.onRefreshComplete();
//				Toast.makeText(getActivity(), msg.obj.toString(), 0).show();
				break;
			case LOAD_TIMER_OVER:
				//服务端无返回，停止下拉刷新
				handler.removeCallbacks(loadTimerRunnable);
				progressDialog.dismiss();
				break;
			case MSG_CHOICE_COMPANY_SUCCESS:
				
				break;
			case MSG_CHOICE_COMPANY_ERROR:
				Toast.makeText(ChoiceServiceActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		}
	};
	
	
	

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("抢单公司");
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(this);
		
		mGp=(RadioGroup)findViewById(R.id.rg_choice);
		mGp.check(R.id.rb_choice_company);
		mGp.setOnCheckedChangeListener(this);
		
		
		rb_company = (RadioButton) findViewById(R.id.rb_choice_company);
		rb_star = (RadioButton) findViewById(R.id.rb_choice_star);
		rb_offer = (RadioButton) findViewById(R.id.rb_choice_offer);
		rb_more = (RadioButton) findViewById(R.id.rb_choice_more);
		
		mLv = (ListView)findViewById(R.id.lv_news);
		mAdapter = new ChoiceServiceAdapter(this, uOrderID, szOrderValue, szOrderStateName);
		mAdapter.setChoiceCompanyListener(new ChoiceCompanyCallBack() {
			
			@Override
			public void choiceCompany(HsRaceCompanyInfo_Pro raceCompanyInfo) {
				//用户在 抢单公司界面  直接点击预约下单时，调用该方法，表示用户选定该公司为服务公司，之后直接进入确认订单界面
				choiceCompanyInfo = raceCompanyInfo;
				showChoiceComapnyDialog();
			}
		});
		mLv.setAdapter(mAdapter);
	}
	
	
	/**
	 * 测试数据
	 */
	private void initData() {
		mList = new ArrayList<String>();
		mList.add("西安恒星家政服务有限公司1");
//		mList.add("西安恒星家政服务有限公司2");
//		mList.add("西安恒星家政服务有限公司3");
//		mList.add("西安恒星家政服务有限公司4");
//		mList.add("西安恒星家政服务有限公司5");
	}
	
	
	
	/**
	 * 确认选定公司对话框
	 */
	private void showChoiceComapnyDialog(){
		SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("确定选择该服务公司吗？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				loadChoiceCompanyData();//发送选定公司请求
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		dialog.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		
		dialog.show();
	}
	



	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.public_back:
			instance = null;
			this.finish();
			break;
		default:
			break;
		}

	}

	
	/**
	 * RadioGroup切换的监听
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
//		case R.id.rb_choice_company:
//			mAdapter = new ChoiceServiceAdapter(this, mList);
//			mAdapter.notifyDataSetChanged();
//			mLv.setAdapter(mAdapter);
//			break;
//		case R.id.rb_choice_star:
//			mAdapter = new ChoiceServiceAdapter(this, mList);
//			mAdapter.notifyDataSetChanged();
//			mLv.setAdapter(mAdapter);
//			break;
//		case R.id.rb_choice_offer:
//			mAdapter = new ChoiceServiceAdapter(this, mList);
//			mAdapter.notifyDataSetChanged();
//			mLv.setAdapter(mAdapter);
//			break;
//		case R.id.rb_choice_more:
//			mAdapter = new ChoiceServiceAdapter(this, mList);
//			mAdapter.notifyDataSetChanged();
//			mLv.setAdapter(mAdapter);
//			break;

		default:
			break;
		}

	}
	
	
	
	
	/**
	 * 提取抢单公司列表数据请求
	 */
	private void loadRaceCompanyData(){
		seqChoiceServiceListNo = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = myApplication.userId;//为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = uOrderID ;//选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandleHsOrderRaceDetailGet_ReqToPro(hsNetCommonReq,seqChoiceServiceListNo);
		HouseSocketConn.pushtoList(connData);
		
		progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("抢单公司列表的请求--sequence="+seqChoiceServiceListNo +"/"+Arrays.toString(connData)+"=============");
		
	}
	
	
	/**
	 * 用户选择指定公司   数据请求
	 */
	private void loadChoiceCompanyData(){
		seqChoiceCompanyNo = MyApplication.SequenceNo++;
		HsCommon_Notify hsCommon_Notify  = new MsgInncDef().new HsCommon_Notify();
		hsCommon_Notify.uUserID = myApplication.userId;
		hsCommon_Notify.uCompanyID = choiceCompanyInfo.getUCompanyID() ;
		hsCommon_Notify.iOrderID = uOrderID;
		byte[] connData = HandleNetSendMsg.HandleHsChoiceCompany_ReqToPro(hsCommon_Notify,seqChoiceCompanyNo);
		HouseSocketConn.pushtoList(connData);
		
		progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("用户选择指定公司的请求--sequence="+seqChoiceCompanyNo +"/"+Arrays.toString(connData)+"=============");
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

				if (seqChoiceServiceListNo == iSequence) {
					processChoiceServiceData(recvTime);
				}
				else if(seqChoiceCompanyNo == iSequence){
					processChoiceCompanyData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理选择服务列表响应的数据
	 * @param iSequence
	 */
	private void processChoiceServiceData(long recvTime){
		HsOrderRaceDetailGet_Resp hsOrderRaceDetailGet_Resp = (HsOrderRaceDetailGet_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsOrderRaceDetailGet_Resp == null){
			return;
		}
		LogUtils.i("抢单公司列表返回数据成功");
		progressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsOrderRaceDetailGet_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			raceCompanyList = hsOrderRaceDetailGet_Resp.raceCompanyList;
			
			Message message =new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsOrderRaceDetailGet_Resp.eOperResult);
			
			Message message =new Message();
			message.what = MSG_LOAD_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			
			LogUtils.i("抢单公司列表获取失败"+result+"=============");					
		}
	}
	
	
	/**
	 * 处理用户选择指定公司响应的数据
	 * @param iSequence
	 */
	private void processChoiceCompanyData(long recvTime){
		HsNetCommon_Resp hsNetCommon_Resp  = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsNetCommon_Resp == null){
			return;
		}
		LogUtils.i("用户选择指定公司 返回数据成功");
		progressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsNetCommon_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			
			setDbStarCompanyData(choiceCompanyInfo);
			
			Intent intent = new Intent(ChoiceServiceActivity.this, RaceDetailActivity.class);
			intent.putExtra("raceCompanyInfo", (Serializable)choiceCompanyInfo);//抢单公司数据
			intent.putExtra("uOrderID", uOrderID);//订单id
			intent.putExtra("szOrderValue", szOrderValue);//订单号
			intent.putExtra("isSelectCompany", true);//用于标识是否已经选定服务公司，从此按钮进入抢单详情时，已经选定公司
			startActivity(intent);
			instance = null;
			finish();
			
			Message message =new Message();
			message.what = MSG_CHOICE_COMPANY_SUCCESS;
			handler.sendMessage(message);
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsNetCommon_Resp.eOperResult);
			
			Message message =new Message();
			message.what = MSG_CHOICE_COMPANY_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			
			LogUtils.i("用户选择指定公司  失败"+result+"=============");					
		}
	}

	
	
	/**
	 * 将明星公司数据写入数据库中
	 * 首先判断数据库中是否存在该明星公司的数据，若存在在直接返回，若不存在则将该公司存入数据库中
	 * @param choiceCompanyInfo 用户选定的明星公司数据
	 */
	private void setDbStarCompanyData(HsRaceCompanyInfo_Pro choiceCompanyInfo){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iCompanyID", choiceCompanyInfo.getUCompanyID()+"");
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		if(starCompanyDataList.size() != 0){
			return;
		}
		
		
		//抢单公司信息中  无公司介绍，多了抢单公司要求的预付款金额
		Map<String, String> startCompanyMap = new HashMap<String, String>();
		startCompanyMap.put("iCompanyID", choiceCompanyInfo.getUCompanyID()+"");
		startCompanyMap.put("iOrderNum", choiceCompanyInfo.getIOrderNum()+"");
		startCompanyMap.put("iValuNum", choiceCompanyInfo.getIValuNum()+"");
		startCompanyMap.put("iStarLevel", choiceCompanyInfo.getIStarLevel()+"");
		startCompanyMap.put("iAuthFlag", choiceCompanyInfo.getIAuthFlag()+"");
		startCompanyMap.put("iFiling",choiceCompanyInfo.getIFiling()+"");
		startCompanyMap.put("iOffLine", choiceCompanyInfo.getIOffLine()+"");
		startCompanyMap.put("iNominate", choiceCompanyInfo.getINominate()+"");
		startCompanyMap.put("szName", choiceCompanyInfo.getSzName()+"");
		startCompanyMap.put("szAddr", choiceCompanyInfo.getSzAddr()+"");
		startCompanyMap.put("szServiceInfo", choiceCompanyInfo.getSzServiceInfo()+"");
		startCompanyMap.put("szCreateTime", choiceCompanyInfo.getSzCreateTime()+"");
		startCompanyMap.put("szCompanyUrl", choiceCompanyInfo.getSzCompanyUrl()+"");
		startCompanyMap.put("szCompanyInstr", "");
		
		String insertSql = DbOprationBuilder.insertStarCompanyAllBuilder(MyApplication.userId, startCompanyMap);
		ds.insert(insertSql);
	}
	
	
	
	
	// 若长时间服务端无返回，则停止进度条
	Runnable loadTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(LOAD_TIMER_OVER);
		}
	};
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
