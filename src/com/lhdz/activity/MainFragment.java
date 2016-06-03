package com.lhdz.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.activity.BaseActivity.ActivityStackControlUtil;
import com.lhdz.dao.CoreDbHelper;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ServerInfo_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCmpVerInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsHomenTypeInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserServiceAddrInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.fragment.FragmentController;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.AuthNetCommonReq;
import com.lhdz.publicMsg.MsgInncDef.HsAppHomenInfo_Req;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.GetServerInfoResp;
import com.lhdz.publicMsg.MsgReceiveDef.HsAppHomeInfo_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsCmpRaceOrder_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsCmpRefuseAtticheResult_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsCommon_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsCompanyComplain_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsGetDBVerInfo_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserGetServiceAddrs_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.DNSParsing;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.GetCityDataUtil;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.util.VibratorUtil;
import com.lhdz.wediget.BadgeView;
import com.lhdz.wediget.PopMenu;


/**
 * 承载 首页 、 订单 、 资讯 、 我  四个页面的Activity 
 * @author wangf
 *
 */
public class MainFragment extends FragmentActivity implements
		OnCheckedChangeListener, OnClickListener {

	private RadioGroup mRadioGroup;
	private Button btn_badge_view;//用于添加红色小圆点
	private long exitTime = 0;
	private PopMenu pop;
	private CheckBox cb_add;

	private FragmentController controller;
	
	private BadgeView mBadgeView;
	
	List<Map<String, String>> authInfoDataList = new ArrayList<Map<String,String>>();// 用户信息数据--从数据库来
//	private String pwdMd5;
	List<Map<String, String>> starCompanyDataList = null;// 首页明星公司列表数据
	private int iStarCompanyCount;
	
	private final static int MSG_SERVER_LIST_SUCCESS = 1;
	private final static int MSG_AUTO_LOGIN_SUCCESS = 2;
	private final static int MSG_CONN_HOUSE_SUCCESS = 3;
	private final static int LOAD_TIMER_OVER = 10002;
	
	private int iDbAppHomeDataVer = -1;//首页服务类型数据版本
	private int iDbStarCompanyVer = -1;//明星公司数据版本
	
	private int seqSerList = -1;//最优服务器列表的sequence
	private int seqAutoLogin = -1;//自动登录的sequence
	private int seqConnHouse = -1;//连接家政服务器的sequence
	private int seqDbVersionInfo = -1;//服务器数据版本的sequence
	private int seqGetServiceAddrNo = -1;//服务地址的sequence
	private int seqStarCompany = -1;//明星公司的sequence
	private int seqAppHome = -1;//首页服务类型的sequence
	
	private int dbStarCompanyVer = -1;//明星公司数据版本
	private int dbAppHomeDataVer = -1;//首页快捷下单数据版本
	private int webAppHomeDataVer = -1;
	private int webStarCompanyVer = -1;
	
	private boolean isFirstReceiveCompany = false;//判断是否是第一次接收明星公司数据
	
	private MyApplication mApplication;
//	private CustomProgressDialog progressDialog;
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SERVER_LIST_SUCCESS://获取最优服务器成功
				//解析域名，并判断是否需要自动登录
				new Thread(runnable).start();
				break;
			case MSG_CONN_HOUSE_SUCCESS://连接家政服务器成功
				
				try {
					if(MyApplication.loginState){//处于登录状态的才去查询用户的服务地址
						queryServiceAddrData();// 查询用户服务地址，并处理
					}
					Thread.sleep(10);
					loadDBVersionInfoData();//获取数据库版本请求
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			case LOAD_TIMER_OVER:
//				handler.removeCallbacks(loadTimerRunnable);
//				if(progressDialog != null){
//					progressDialog.dismiss();
//				}
				break;

			default:
				break;
			}
		}
	}; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除标题
		setContentView(R.layout.fragment_main);
		initViews();
		controller = FragmentController.getInstance(this, R.id.fl_content);
		controller.showFragment(0);
		mApplication = new MyApplication();// 妈蛋，用getApplication为什么不行？
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		filter.addAction(Define.BROAD_SERVICE_UPDATE_VERSION);
		filter.addAction(Define.BROAD_BADGE_CLEAR);
		registerReceiver(mReceiver, filter);
		
//		queryStarCompanyData();
		loadDataServerListBroad(mApplication.userId);
	}


	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.radio_homepage:
			controller.showFragment(0);
			break;
		case R.id.radio_indent:
			controller.showFragment(1);
			break;
		case R.id.radio_news:
			controller.showFragment(2);
			break;
		case R.id.radio_mine:
			controller.showFragment(3);
			break;
		default:
			break;
		}
	}

	
	/**
	 *  初始化页面控件
	 */
	private void initViews() {
		mRadioGroup = (RadioGroup) findViewById(R.id.rb_main_page);
		cb_add = (CheckBox) findViewById(R.id.radio_add);
		btn_badge_view = (Button) findViewById(R.id.btn_badge_view);
		mRadioGroup.setOnCheckedChangeListener(this);
		cb_add.setOnClickListener(this);
		
		mBadgeView = new BadgeView(MainFragment.this, btn_badge_view);
	}
	
	
	/**
	 * 为订单按扭上添加角标显示数量
	 */
	public void setBadgeView() {

		MyApplication.badgeViewCount++;
		if (mBadgeView != null) {
			
			VibratorUtil.vibrate(this, new long[]{0,300,80,400}, false);   //延时0ms,震动300ms,停止80ms,震动400ms 
			VibratorUtil.ringTone(MyApplication.context);
			
			mBadgeView.setFocusable(false);
			mBadgeView.setTextSize(10);
			mBadgeView.setText(MyApplication.badgeViewCount + "");
			mBadgeView.show();
		}
	}
	
	
	
	
	
	/**
	 * 连接家政服务器
	 */
	private void loadConnectHsDataBroad() {
		seqConnHouse = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg
				.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),seqConnHouse);
		// 连接家政服务器
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("连接家政服务器请求数据--sequence="+seqConnHouse +"/"+ Arrays.toString(connData)+ "-------");
	}
	
	
	/**
	 * 加载 最优服务器列表
	 * 
	 * @param userId
	 */
	public void loadDataServerListBroad(int userId) {
		seqSerList = MyApplication.SequenceNo++;
		// 获取服务器列表请求 使用通用请求的数据
		AuthNetCommonReq serverInfo = new MsgInncDef().new AuthNetCommonReq();
		serverInfo.iUserid = userId;
		// seqSer = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleServerListReqToPro(serverInfo,seqSerList);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("获取服务器列表请求数据--sequence="+seqSerList +"/"+ Arrays.toString(connData)
				+ "=============");
			
//		if (MyApplication.connState) {
//			progressDialog = new CustomProgressDialog(this);
//			progressDialog.show();
//			handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
//		}
		
	}
	
	
	
	
	
	/**
	 * 获取数据库最新版本 请求
	 */
	private void loadDBVersionInfoData(){
		
//		DataBaseService ds = new DataBaseService(this);
//		String deleteSql = DbOprationBuilder.deleteBuilder("dbVerInfo");
//		ds.delete(deleteSql);

		queryDbDataVerInfo();//查询本地数据库数据版本
		
		seqDbVersionInfo = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommon_Req = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommon_Req.iSrcID = MyApplication.userId;
		hsNetCommon_Req.iSelectID = 0;
		byte[] connData = HandleNetSendMsg.HandleHsDBVersionInfo_ReqToPro(hsNetCommon_Req, seqDbVersionInfo);
		
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("获取数据库最新数据版本请求--sequence="+seqDbVersionInfo+"/"+Arrays.toString(connData)+"-----");
	}
	
	
	
	/**
	 * 用户提取服务地址 请求
	 */
	private void loadAddrListData() {
		seqGetServiceAddrNo = MyApplication.SequenceNo++;
		HsNetCommon_Req addrInfo_Req = new MsgInncDef().new HsNetCommon_Req();
		addrInfo_Req.iSrcID = MyApplication.userId;
		addrInfo_Req.iSelectID = 0;
		
		byte[] connData = HandleNetSendMsg.HandleHsUserGetServiceAddrs_ReqToPro(addrInfo_Req,seqGetServiceAddrNo);
		
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("提取服务地址 请求--sequence="+seqGetServiceAddrNo +"/"+ Arrays.toString(connData) + "----------");

	}
	
	
	
//	/**
//	 * 从网络请求首页明星公司数据
//	 */
//	private void loadStarCompanyData() {
//		seqStarCompany = MyApplication.SequenceNo++;
//		HsNetCommon_Req hsNetCommon_Req = new MsgInncDef().new HsNetCommon_Req();
//		hsNetCommon_Req.iSrcID = 0;
//		hsNetCommon_Req.iSelectID = 0;
//		byte[] connData = HandleNetSendMsg.HandleHsStarCompanyReqToPro(hsNetCommon_Req, seqStarCompany);
//		HouseSocketConn.pushtoList(connData);
//		LogUtils.i("connData明星公司列表请求数据--sequence="+seqStarCompany +"/"+ Arrays.toString(connData) );
//	}
	
	
	/**
	 * 从网络请求首页快捷下单数据
	 * 
	 * @param getHome
	 * @param userId
	 */
	private void loadAppHomenInfoData(int getHome, int userId) {
		seqAppHome = MyApplication.SequenceNo++;
		HsAppHomenInfo_Req homeInfo = new MsgInncDef().new HsAppHomenInfo_Req();
		homeInfo.bGetHome = getHome;
		homeInfo.iUserID = userId;

		byte[] homeInfo_pro = HandleNetSendMsg
				.HandleHsAppHomenInfoReqToPro(homeInfo, seqAppHome);
		LogUtils.i("首页结构体的请求数据--sequence="+seqAppHome +"/"+ Arrays.toString(homeInfo_pro));
		HouseSocketConn.pushtoList(homeInfo_pro);
	}
	
	
	
	/**
	 * 广播接收数据
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				String dataFrom = intent.getStringExtra(Define.BROAD_DATA_FROM);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if(seqSerList == iSequence){
					seqSerList = -1;
					processServerListData(recvTime);
				}
//				else if(seqAutoLogin == iSequence){
//					processAutoLoginData(iSequence);
//				}
				else if(seqConnHouse == iSequence){
					seqConnHouse = -1;
					processConnHouseDataFromMain(recvTime);
				}
				else if(seqDbVersionInfo == iSequence){
					seqDbVersionInfo = -1;
					processDBVersionInfoData(recvTime);
				}
				else if(seqGetServiceAddrNo == iSequence){
					seqGetServiceAddrNo = -1;
					processGetServiceAddrData(recvTime);
				}
				else if(seqAppHome == iSequence){
					seqAppHome = -1;
					processAppHomeData(recvTime);
				}
//				else if(seqStarCompany == iSequence){
//					processStarCompanyListData(recvTime);
//				}
				
				
				//用于在别的页面发送请求数据，在此处接收响应
				if(MyApplication.seqLoginConnHouse == iSequence){
					LogUtils.i("=========连接家政服务器在mainfragment中接收到数据");
					MyApplication.seqLoginConnHouse = -1;
					processConnHouseDataFromLogin(recvTime);
					
				}
				if(MyApplication.seqServiceConnAuth == iSequence){
					MyApplication.seqServiceConnAuth = -1;
					processConnAuthData(recvTime);
				}
				if(MyApplication.seqServiceConnHouse == iSequence){
					MyApplication.seqServiceConnHouse = -1;
					processConnHouseDataFromService(recvTime);
				}
				
				
				//通知类消息的处理
				if(iMsgType == NetHouseMsgType.NETAPP_ORDERRACEHOMEN_NOTIFY){
					//家政公司抢单成功通知
					processOrderRaceNotify(recvTime);
				}
				if(iMsgType == NetHouseMsgType.NETAPP_USER_ASKAPPEAL_NOTIFY){
					//家政公司提请申述通知
					processCompanyComplainNotify(recvTime);
				}
				if(iMsgType == NetHouseMsgType.NETAPP_HOMENEXAMINE_NOTIFY){
					//家政公司审批结果通知
					processCmpRefuseAtticheResultNotify(recvTime);
				}
				if(iMsgType == NetHouseMsgType.NETAPP_HAVELOGIN_NOTIFY){
					//账号在其他客户端登录通知
//					1、消息为通用通知。
//					2、用户端和商户端一样均需要处理此消息。
//					3、当接到此消息后，用户端或者商户端断开链路，提醒用户或者跳转登录界面。
//					4、有自动登录的，此时不允许自动登录。
					processHaveLoginNotify(recvTime);
				}
				if(iMsgType == NetHouseMsgType.NETAPP_USER_ORDERPAY_REQ_NOTIFY){
					//家政公司申请用户付款通知
					processPayOrderNotify(recvTime);
				}
				

			}else if (Define.BROAD_SERVICE_UPDATE_VERSION.equals(intent.getAction())) {
				//apk版本升级，启动安装页面
				String strInstallApkPath = intent.getStringExtra("InstallApkPath");
				Uri url = Uri.parse(strInstallApkPath);
				Intent intentPath = new Intent(Intent.ACTION_VIEW);
				intentPath.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intentPath.setDataAndType(url,"application/vnd.android.package-archive");
				MainFragment.this.startActivity(intentPath);
				android.os.Process.killProcess(android.os.Process.myPid());// 如果不加上这句的话在apk安装完成之后点击单开会崩溃
			}else if(Define.BROAD_BADGE_CLEAR.equals(intent.getAction())){
				if(mBadgeView != null){
					mBadgeView.hide();
				}
				MyApplication.badgeViewCount = 0;
			}
		}
	};
	
	
	/**
	 *  处理连接登录认证服务器的数据
	 * @param recvTime
	 */
	private void processConnAuthData(long recvTime) {
		NetConnectResp connectResp = (NetConnectResp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (connectResp == null) {
			return;
		}
		if (connectResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int time = connectResp.iSrvTime;
			int userId = connectResp.iUserid;

			LogUtils.i("连接登录认证服务器--" + "time = " + time + "/ userId = "
					+ userId);
			 loadDataServerListBroad(userId);
		}
	}
	
	
	
	/**
	 * 处理获取最优服务器的数据
	 * @param recvTime
	 */
		private void processServerListData(long recvTime){
			GetServerInfoResp serverInfo = (GetServerInfoResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(serverInfo == null){
				return;
			}
			if (serverInfo.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
				String strDns = "";
				String ip = "";
				int port = 0;
				List<AUTH_ServerInfo_PRO> serverList = (List<AUTH_ServerInfo_PRO>) serverInfo.listInfo;
				for (int i = 0; i < serverList.size(); i++) {
					AUTH_ServerInfo_PRO serverItem = serverList.get(i);
					if (serverItem.getIServerType() == 11) {
						strDns = serverItem.getSzDNS();
						port = serverItem.getIPort();
						ip = serverItem.getSzIp();
					}

				}
				MyApplication.netParam.setHsIp(ip);
				MyApplication.netParam.setHsPort(port);
				MyApplication.netParam.setHsDns(strDns);

				LogUtils.i("最优服务器--" + "ip = " + ip + ", port = " + port + ", strDns =" +strDns);
				
				handler.sendEmptyMessage(MSG_SERVER_LIST_SUCCESS);
				
			}
		}
		
		
		/**
		 * 处理连接家政服务器的数据
		 * @param recvTime
		 */
		private void processConnHouseDataFromMain(long recvTime){
			NetConnectResp netConn = (NetConnectResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			
//			if(progressDialog != null){
//				progressDialog.dismiss();
//			}
			
			if(netConn == null){
				return;
			}
			if (netConn.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
				int time = netConn.iSrvTime;
				int userId = netConn.iUserid;
				LogUtils.i("fragment中已连接家政服务器" + "time = " + time+"/ userId" + userId);
				
				handler.sendEmptyMessage(MSG_CONN_HOUSE_SUCCESS);
				
				Intent intent = new Intent();
				intent.setAction(Define.BROAD_FRAGMENT_RECV_LOGIN);
				sendBroadcast(intent);
				
			} else {
				String result = UniversalUtils.judgeNetResult_Auth(netConn.eResult);
				LogUtils.i("fragment连接家政服务器失败" + result + "=======");
				LogUtils.i("重新连接家政服务器");
				loadConnectHsDataBroad();//连接家政失败，重试
				
			}
		}
		
		/**
		 *  处理连接家政服务器的数据--此处可处理来自登录页面的连接家政的响应
		 * @param recvTime
		 */
		private void processConnHouseDataFromLogin(long recvTime){
			NetConnectResp netConn = (NetConnectResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(netConn == null){
				return;
			}
			if (netConn.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
				int time = netConn.iSrvTime;
				int userId = netConn.iUserid;
				LogUtils.i("fragment中已连接家政服务器" + "time = " + time+"/ userId" + userId);
				
				handler.sendEmptyMessage(MSG_CONN_HOUSE_SUCCESS);
				
			} else {
				String result = UniversalUtils.judgeNetResult_Auth(netConn.eResult);
				LogUtils.i("fragment连接家政服务器失败" + result + "=======");
				LogUtils.i("重新连接家政服务器");
				loadConnectHsDataBroad();//连接家政失败，重试
				
			}
		}
		
		
		/**
		 *  处理连接家政服务器的数据--用于网络重连
		 * @param recvTime
		 */
		private void processConnHouseDataFromService(long recvTime){
			NetConnectResp netConn = (NetConnectResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(netConn == null){
				return;
			}
			if (netConn.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
				int time = netConn.iSrvTime;
				int userId = netConn.iUserid;
				LogUtils.i("fragment中已连接家政服务器" + "time = " + time+"/ userId" + userId);
				
				
			} else {
				String result = UniversalUtils.judgeNetResult_Auth(netConn.eResult);
				LogUtils.i("fragment连接家政服务器失败" + result + "=======");
				LogUtils.i("重新连接家政服务器");
				loadConnectHsDataBroad();//连接家政失败，重试
				
			}
		}
		
		
		/**
		 * 处理获取数据库数据版本响应数据
		 * @param iSequence
		 */
		private void processDBVersionInfoData(long recvTime){
			HsGetDBVerInfo_Resp dbVersionInfo_Resp = (HsGetDBVerInfo_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(dbVersionInfo_Resp == null){
				return;
			}
			if(dbVersionInfo_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
				List<HsCmpVerInfo_Pro> verInfoList = dbVersionInfo_Resp.versionList;
				
				LogUtils.i("获取数据版本成功");
				
				for (int i = 0; i < verInfoList.size(); i++) {
					int uVerType = verInfoList.get(i).getUVerType();
					int uVersion = verInfoList.get(i).getUVersion();
					
				if (uVerType == Define.DBAPPHOME_VERTYPE) {
					webAppHomeDataVer = uVersion;
				} else if (uVerType == Define.DBSTARCOMPANY_VERTYPE) {
					webStarCompanyVer = uVersion;
				}
//					String insertSql = DbOprationBuilder.insertDbVerInfoAllBuilder(verInfoList.get(i));
//					ds.insert(insertSql);
				}
				LogUtils.i("网络--数据版本信息--webAppHomeDataVer = " + webAppHomeDataVer + "/ webStarCompanyVer = " + webStarCompanyVer);
				
				judgeDbVersion();

			}else{
				String result = UniversalUtils.judgeNetResult_Hs(dbVersionInfo_Resp.eOperResult);
				LogUtils.i("获取数据版本失败--"+result);
			}
			
		}
		
		
		
//		/**
//		 * 处理明星公司列表数据
//		 * @param iSequence
//		 */
//		private void processStarCompanyListData(long recvTime){
//			HsStarCompanyGet_Resp starCompany_Resp = (HsStarCompanyGet_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
//
//			if(starCompany_Resp == null){
//				return;
//			}
//			if (starCompany_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
//				LogUtils.i("fragment中明星公司数据已取得");
//				
//				int iTotalCount = starCompany_Resp.iTotalCount;//公司总个数
//				int iSendCount = starCompany_Resp.iSendCount;//已发送数据的总个数
//				
//				List<HsCompanyInfo_Pro> startCompanyList = starCompany_Resp.companyList;
//
//				DataBaseService ds = new DataBaseService(this);
//				
//				
//			if (isFirstReceiveCompany) {
//				// 删除明星公司表中的数据
//				String deleteSql = DbOprationBuilder.deleteBuilder("starcompany");
//				ds.delete(deleteSql);
//				
//				isFirstReceiveCompany = false;
//			}
//				
//			
//			for (int i = 0; i < startCompanyList.size(); i++) {
//				String sql = DbOprationBuilder.insertStarCompanyAllBuilder(
//						MyApplication.userId, startCompanyList.get(i));
//				ds.insert(sql);
//			}
//			
//			
//			LogUtils.i("明星公司数据获取成功--iTotalCount="+iTotalCount+"/iSendCount="+iSendCount+"/startCompanyList.size()="+startCompanyList.size());	
//			if(iSendCount + startCompanyList.size() == iTotalCount && iTotalCount != 0){
//				// 删除数据库版本信息表中的明星公司版本信息
//				String deleteDbVerSql = DbOprationBuilder.deleteBuilderby("dbVerInfo", "uVerType", Define.DBSTARCOMPANY_VERTYPE+ "");
//				ds.delete(deleteDbVerSql);
//				// 插入数据库版本信息表中的明星公司数据
//				String insertStarDbVerSql = DbOprationBuilder.insertDbVerInfoAllBuilder(Define.DBSTARCOMPANY_VERTYPE, webStarCompanyVer);
//				ds.insert(insertStarDbVerSql);
//				
//				Intent intent = new Intent();
//				intent.setAction(Define.BROAD_FRAGMENT_RECV_STARCOMPANY);
//				sendBroadcast(intent);
//			}
//				
//				
//			
//				
//			} else {
//				String result = UniversalUtils
//						.judgeNetResult_Hs(starCompany_Resp.eOperResult);
//				LogUtils.i("明星公司列表获取失败"+ result + "===========");
//			}
//		}
		
		
		/**
		 * 处理首页appHome数据
		 * @param iSequence
		 */
		private void processAppHomeData(long recvTime){
			HsAppHomeInfo_Resp info_Resp = (HsAppHomeInfo_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			
			if(info_Resp == null){
				return;
			}
			if (info_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
				LogUtils.i("fragment中首页结构体数据已取得");
				List<HsHomenTypeInfo_Pro> homeTypeList = info_Resp.homenTypeList;

				CoreDbHelper dbHelper = new CoreDbHelper(this);
				String deleteSql = DbOprationBuilder.deleteBuilder("coreTable");
				dbHelper.deleteData(deleteSql);
				
				DataBaseService ds = new DataBaseService(this);
				String deleteDbVerSql = DbOprationBuilder.deleteBuilderby("dbVerInfo", "uVerType", Define.DBAPPHOME_VERTYPE+"");
				String insertAppDbVerSql = DbOprationBuilder.insertDbVerInfoAllBuilder(Define.DBAPPHOME_VERTYPE, webAppHomeDataVer);
				ds.delete(deleteDbVerSql);
				ds.insert(insertAppDbVerSql);

				for (int i = 0; i < homeTypeList.size(); i++) {
					String sql = DbOprationBuilder
							.insertAppHomeAllBuilder(homeTypeList.get(i));
					dbHelper.insertCoreData(sql);
				}

				Intent intent = new Intent();
				intent.setAction(Define.BROAD_FRAGMENT_RECV_APPHOME);
				sendBroadcast(intent);

			} else {
				String result = UniversalUtils
						.judgeNetResult_Hs(info_Resp.eOperResult);
				LogUtils.i("首页结构体数据获取失败"+ result + "===========");
			}
		}
		
		
		
		
		
		/**
		 * 处理获取服务地址响应的数据
		 * @param iSequence
		 */
		private void processGetServiceAddrData(long recvTime){
			HsUserGetServiceAddrs_Resp serviceAdd = (HsUserGetServiceAddrs_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(serviceAdd == null){
				return;
			}
			LogUtils.i("mainfragment提取服务地址 返回数据成功");
			if (serviceAdd.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
				
				List<HsUserServiceAddrInfo_Pro> serviceAddList = serviceAdd.addrList;
				LogUtils.i("mainfragment提取地址成功");
				
				DataBaseService ds = new DataBaseService(getApplicationContext());
				String deleteSql = DbOprationBuilder.deleteBuilder("userAddr");
				ds.delete(deleteSql);
				GetCityDataUtil cityDataUtil = new GetCityDataUtil(this);
				for (int i = 0; i < serviceAddList.size(); i++) {
					String strAddr = serviceAddList.get(i).getSzUserAddr();
					String strLongAddr = cityDataUtil.areaIdToAddr(serviceAddList.get(i).getUAreaID());
					
					String insertSql = DbOprationBuilder.insertUserServiceAddAllBuilder(
							MyApplication.userId,//userid
							serviceAddList.get(i).getUAreaID(),//地址区域序号
							strAddr,//联系人地址
							strLongAddr + strAddr,//详细地址
							0,//选中状态
							0,//是否删除
							serviceAddList.get(i).getSzUserTel(),//联系电话
							serviceAddList.get(i).getSzUserName(),//联系人名
							serviceAddList.get(i).getUAddrIndex()//地址序号
							);
					ds.insert(insertSql);
				}
				
			} else {
				String result = UniversalUtils.judgeNetResult_Hs(serviceAdd.eOperResult);
				LogUtils.i("mainfragment提取地址失败"+result+"=======");
				
			}
		}
		
		
		
		/**
		 * 处理 家政公司抢单成功的通知
		 * @param iSequence
		 */
		private void processOrderRaceNotify(long recvTime){
			HsCmpRaceOrder_Notify hsCmpRaceOrder_Notify = (HsCmpRaceOrder_Notify) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(hsCmpRaceOrder_Notify == null){
				return;
			}
			LogUtils.i("收到  家政公司抢单成功的通知");
			
			int uCompanyID = hsCmpRaceOrder_Notify.uCompanyID;
			int uOrderID = hsCmpRaceOrder_Notify.uOrderID;
			String szSrvPrice = hsCmpRaceOrder_Notify.szSrvPrice;
			String szRemark = hsCmpRaceOrder_Notify.szRemark;
			
			setBadgeView();//设置红色圆点
			
			LogUtils.i("家政公司抢单成功--uCompanyID="+uCompanyID+"/uOrderID="+uOrderID+
					"/szSrvPrice="+szSrvPrice+"/szRemark="+szRemark);
		}
		
		
		/**
		 * 处理 家政公司 提请申述的通知
		 * @param iSequence
		 */
		private void processCompanyComplainNotify(long recvTime){
			HsCompanyComplain_Notify companyComplain_Notify = (HsCompanyComplain_Notify) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(companyComplain_Notify == null){
				return;
			}
			LogUtils.i("收到 家政公司提请申述的通知");
			int uOrderID = companyComplain_Notify.uOrderID;//订单id
			int uUserID = companyComplain_Notify.uUserID;//提交申述的公司id
			String szContent = companyComplain_Notify.szContent;//申述内容
			
			LogUtils.i("家政公司提请申述--uOrderID="+uOrderID+"/uUserID="+uUserID+"/szContent="+szContent);
		}
		
		
		/**
		 * 家政公司审批结果通知
		 * @param iSequence
		 */
		private void processCmpRefuseAtticheResultNotify(long recvTime){
			HsCmpRefuseAtticheResult_Notify atticheResult_Notify = (HsCmpRefuseAtticheResult_Notify) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(atticheResult_Notify == null){
				return;
			}
			LogUtils.i("收到 家政公司提请审批结果通知");
			int iCompanyID = atticheResult_Notify.iCompanyID;
			int uUserID = atticheResult_Notify.uUserID;
			int uResult = atticheResult_Notify.uResult;
			String szRejReason = atticheResult_Notify.szRejReason;
			
		LogUtils.i("家政公司审批结果--iCompanyID=" + iCompanyID + "/uUserID=" + uUserID
				+ "/uResult=" + uResult + "/szRejReason=" + szRejReason);
		}
		
		
		/**
		 * 账号在其他客户端登录通知
		 * @param iSequence
		 */
		private void processHaveLoginNotify(long recvTime){
			HsCommon_Notify common_Notify = (HsCommon_Notify) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(common_Notify == null){
				return;
			}
			
			//清除用户数据
			MyApplication.loginState = false;
			MyApplication.userId = 0;
			String sql = DbOprationBuilder.deleteBuilder("authInfo");
			DataBaseService ds = new DataBaseService(MainFragment.this);
			ds.delete(sql);
			if(MyApplication.houseSocketConn != null){
				MyApplication.houseSocketConn.closeHouseSocket();
			}
			
			ToastUtils.show(this, "该账号已在异地登录，请重新登陆", 1);
			
			showExitDialog();
			LogUtils.i("收到 账号在其他客户端登录通知");
			
			
		}
		
		
		/**
		 * 家政公司申请用户付款通知
		 * @param iSequence
		 */
		private void processPayOrderNotify(long recvTime){
			HsCommon_Notify common_Notify = (HsCommon_Notify) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
			if(common_Notify == null){
				return;
			}
			int userId = common_Notify.uUserID;
			int companyId = common_Notify.uCompanyID;
			int orderId = common_Notify.iOrderID;
			
			List<Map<String, String>>  companyInfoList = queryStarCompanyById(companyId);
			if(!UniversalUtils.isStringEmpty(companyInfoList)){
//				ToastUtils.show(this, companyInfoList.get(0).get("szName")+" 通知支付订单", 1);
				showNotifyPayDialog(companyInfoList.get(0), orderId);
			}
//			setBadgeView();
			LogUtils.i("收到 家政公司申请用户付款通知--userId = " + userId + "/companyId="
				+ companyId + "/orderId=" + orderId);
		}
		
		
		/**
		 * 异地登录对话框
		 */
		private void showExitDialog() {
			
			SweetAlertDialog dialog = new SweetAlertDialog(MyApplication.context,SweetAlertDialog.WARNING_TYPE);
			dialog.setTitleText("该账号已在异地登录，请重新登陆");
			dialog.setCancelText("取消");
			dialog.setConfirmText("确定");
			dialog.setConfirmClickListener(new OnSweetClickListener() {

				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					// 点击退出登录按钮清除帐号信息
//					MyApplication.loginState = false;
//					MyApplication.userId = 0;
//					String sql = DbOprationBuilder.deleteBuilder("authInfo");
//					DataBaseService ds = new DataBaseService(MainFragment.this);
//					ds.delete(sql);
					
//					if(MyApplication.houseSocketConn != null){
//						MyApplication.houseSocketConn.closeHouseSocket();
//					}
					
					Intent intent = new Intent(MainFragment.this, LoginActivity.class);
					startActivity(intent);
					
					sweetAlertDialog.dismissWithAnimation();
				}
			});
			dialog.setCancelClickListener(new OnSweetClickListener() {

				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					// TODO Auto-generated method stub
					// 点击退出登录按钮清除帐号信息
//					MyApplication.loginState = false;
//					MyApplication.userId = 0;
//					String sql = DbOprationBuilder.deleteBuilder("authInfo");
//					DataBaseService ds = new DataBaseService(MainFragment.this);
//					ds.delete(sql);
					
//					if(MyApplication.houseSocketConn != null){
//						MyApplication.houseSocketConn.closeHouseSocket();
//					}
					
//					Intent intent = new Intent(MainFragment.this, LoginActivity.class);
//					startActivity(intent);
					
					sweetAlertDialog.dismissWithAnimation();
					
					if(MyApplication.authSocketConn != null){
						MyApplication.authSocketConn.closeAuthSocket();
					}
					if(MyApplication.houseSocketConn != null){
						MyApplication.houseSocketConn.closeHouseSocket();
					}
					
					ActivityStackControlUtil.finishProgram();
					finish();
//					android.os.Process.killProcess(android.os.Process.myPid());
					
					
				}
			});
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
		}
		
		
		/**
		 * 通知支付对话框
		 */
		private void showNotifyPayDialog(Map<String, String>  companyInfo,final int orderId) {
			SweetAlertDialog dialog = new SweetAlertDialog(MyApplication.context,SweetAlertDialog.WARNING_TYPE);
			dialog.setTitleText("通知支付");
			dialog.setContentText(companyInfo.get("szName")+"通知您支付订单");
			dialog.setCancelText("取消");
			dialog.setConfirmText("确定");
			dialog.setConfirmClickListener(new OnSweetClickListener() {
				
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					
					//跳到订单详情
					Intent intent = new Intent(MainFragment.this, IndentDetailsActivity.class);
					intent.putExtra("uOrderID", orderId);//订单唯一标识--orderID
					startActivity(intent);
					
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
			dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			dialog.show();
		}

		
	
	/**
	 * 启动线程解析域名
	 */
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (UniversalUtils.isStringEmpty(MyApplication.netParam.getHsDns()) ) {
				PushData.setHouseIp(MyApplication.netParam.getHsIp());// 把得到的最优服务器ip和port放入结构体中
				PushData.setHousePort(MyApplication.netParam.getHsPort());
			} else {
				String strDns = MyApplication.netParam.getHsDns();
				
				if(strDns.indexOf(":") > 0){
					String strIp = DNSParsing.getIP(strDns.substring(0,
							strDns.indexOf(":")));
					int iPort = Integer.parseInt(strDns.substring(
							strDns.indexOf(":") + 1, strDns.length()));
					MyApplication.netParam.setHsDnsParsIp(strIp);
					MyApplication.netParam.setHsDnsParsPort(iPort);
					// 把得到的最优服务器ip和port放入结构体中
					PushData.setHouseIp(strIp);
					PushData.setHousePort(iPort);
				}else{
					PushData.setHouseIp(MyApplication.netParam.getHsIp());// 把得到的最优服务器ip和port放入结构体中
					PushData.setHousePort(MyApplication.netParam.getHsPort());
				}
				
			}

			// 进行自动登录操作
			judgeAutoLogin();

		}
	};
		
		
		
	/**
	 * 自动登录--若本地有用户信息，则直接默认为登录状态，只需连接家政服务器
	 */
	private void judgeAutoLogin() {
		//拿到最优服务器后，关闭登录认证服务器的socket
		if(MyApplication.authSocketConn != null){
			MyApplication.authSocketConn.closeAuthSocket();
		}
		
		queryAuthInfoData();
		
		try {
			// 若不存在用户信息，则不需要自动登录，只需连接家政socket，此时使用的userID为 0
			if (authInfoDataList.size() == 0) {
				// 创建家政服务器的socket
				if (MyApplication.houseSocketConn != null) {
					MyApplication.houseSocketConn.closeHouseSocket();
					Thread.sleep(5);
				}
				MyApplication.houseSocketConn = new HouseSocketConn(
						PushData.getHouseIp(), PushData.getHousePort());
				loadConnectHsDataBroad();
				return;
			}

			// 若存在用户信息，则默认为自动登录，此时设置登录状态loginState = true ， userID = 保存的用户的id
			// 此时需要创建家政服务器的socket，连接使用的userid为用户的真是id
			if (!UniversalUtils.isStringEmpty(authInfoDataList.get(0).get("userId"))) {
				MyApplication.loginState = true;
				MyApplication.userId = Integer.parseInt(authInfoDataList.get(0).get("userId"));
			}
			// 创建家政服务器的socket
			if (MyApplication.houseSocketConn != null) {
				MyApplication.houseSocketConn.closeHouseSocket();
				Thread.sleep(5);
			}
			MyApplication.houseSocketConn = new HouseSocketConn(PushData.getHouseIp(), PushData.getHousePort());

			Thread.sleep(10);
			// 发送连接家政服务器请求
			loadConnectHsDataBroad();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	/**
	 * 判断本地数据版本与服务端数据版本是否一致，若不一致则需请求
	 */
	private void judgeDbVersion(){
		if(dbAppHomeDataVer != webAppHomeDataVer){
			loadAppHomenInfoData(1, MyApplication.userId);
		}
//		if(dbStarCompanyVer != webStarCompanyVer){
//			isFirstReceiveCompany = true;
//			loadStarCompanyData();
//		}
	}
	
	

	/**
	 * 查询数据库中的用户信息
	 */
	public void queryAuthInfoData() {
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryAllBuilder("authInfo");
		authInfoDataList = ds.query(sql);
	}

	/**
	 * 查询数据库中的服务地址
	 */
	private void queryServiceAddrData() {
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryAllBuilder("userAddr");
		List<Map<String, String>> addrListData = ds.query(sql);

		if (addrListData.size() <= 0) {
			// 若服务地址为空，需要查询
			loadAddrListData();
			return;
		}
		int dbUserId = Integer.parseInt(addrListData.get(0).get("userId"));
		if (dbUserId != MyApplication.userId) {
			// 若服务地址数据库的userID与当前登录用户的userID不一致，需要删除数据库的服务地址，并请求查询服务地址
			String deleteSql = DbOprationBuilder.deleteBuilder("userAddr");
			ds.delete(deleteSql);
			loadAddrListData();
		}
	}
	
	
	/**
	 * 查询本地数据库中数据版本信息
	 */
	private void queryDbDataVerInfo(){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryAllBuilder("dbVerInfo");
		List<Map<String, String>> dbVerInfoList = ds.query(sql);
		
		for (int i = 0; i < dbVerInfoList.size(); i++) {
			Map<String, String> dbVerInfo = dbVerInfoList.get(i);
			int uVerType = -1;
			int uVersion = -1;
			if(!UniversalUtils.isStringEmpty(dbVerInfo.get("uVerType"))){
				uVerType = Integer.parseInt(dbVerInfo.get("uVerType"));
			}
			if(!UniversalUtils.isStringEmpty(dbVerInfo.get("uVersion"))){
				uVersion = Integer.parseInt(dbVerInfo.get("uVersion"));
			}
			
			if(uVerType == Define.DBAPPHOME_VERTYPE){//首页服务类型
				dbAppHomeDataVer = uVersion;
			}else if(uVerType == Define.DBSTARCOMPANY_VERTYPE){//明星公司
				dbStarCompanyVer = uVersion;
			}
		}
		LogUtils.i("本地--首页服务类型数据版本 = "+dbAppHomeDataVer+" / 明星公司数据版本 = "+dbStarCompanyVer);
	}
	
	
	/**
	 * 根据明星公司id查询明星公司数据
	 * @param iCompanyId
	 * @return
	 */
	private List<Map<String, String>> queryStarCompanyById(int iCompanyId){
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany", "iCompanyID", iCompanyId+"");
		List<Map<String, String>> companyInfoList = ds.query(sql);
		return companyInfoList;
	}
	
	
	/**
	 * 查询数据库 中 首页明星公司数据
	 */
	private void queryStarCompanyData() {
		// 查询数据库
		String sql = DbOprationBuilder.queryAllBuilder("starcompany");
		DataBaseService ds = new DataBaseService(this);
		starCompanyDataList = ds.query(sql);
		iStarCompanyCount = starCompanyDataList.size();
	}
	

	@Override
	public void onBackPressed() {
		if (mRadioGroup.getCheckedRadioButtonId() == R.id.radio_homepage) {
			if (System.currentTimeMillis() - exitTime > 2000) {
				Toast.makeText(this, "再按一次退出歇歇", 0).show();
				exitTime = System.currentTimeMillis();
			} else {
				if(MyApplication.authSocketConn != null){
					MyApplication.authSocketConn.closeAuthSocket();
				}
				if(MyApplication.houseSocketConn != null){
					MyApplication.houseSocketConn.closeHouseSocket();
				}
				this.finish();
				System.exit(0);
			}
		} else {
			mRadioGroup.check(R.id.radio_homepage);
			controller.showFragment(0);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.radio_add:
			pop = new PopMenu(MainFragment.this);
			pop.showAsDropDown(v);
			break;

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
	
	

	@Override
	protected void onPause() {
		super.onPause();
	}

	
	@Override
	protected void onStop() {
		
		super.onStop();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		FragmentController.onDestroy();
		if (mApplication.netWorkState != null) {
			// 切记 一定要注销服务若不注销则在退出APP时会出现冒烟
			unbindService(mApplication.serviceConnection);
		}
	}
	

}
