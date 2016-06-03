package com.lhdz.activity;

/**
 * 登录界面
 * @author 王哲
 * @date 2015-9-5
 */

import java.util.Arrays;
import java.util.List;

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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ServerInfo_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.AuthNetCommonReq;
import com.lhdz.publicMsg.MsgInncDef.LoginReq;
import com.lhdz.publicMsg.MsgInncDef.NetConnectReq;
import com.lhdz.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.lhdz.publicMsg.MsgReceiveDef.GetServerInfoResp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.DNSParsing;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.SPUtils;
import com.lhdz.util.StringUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.ClearEditText;

public class LoginActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {
	private TextView losepwd;
	private TextView toreg;
	private ClearEditText uname;
	private ClearEditText upwd;
	private Button btnLogin;
	Intent intent;
	private RadioGroup soLogin;

	private final static int MSG_LOGIN_SUCCESS = 2;
	private final static int MSG_LOGIN_ERROR = 0;
	private final static int MSG_CONN_ERROR = 1;
	private final static int BTN_TIMER_OVER = 3;
	
	/**
	 * 发送请求的sequenceNo
	 */
	private int seqLogin = -1;
	private int seqConnHouse = -1;
	private int seqConnAuth = -1;
	private int seqSerList = -1;
	
	private CustomProgressDialog progressDialog;

	MyApplication myApplication = null;
	private String pwdMd5 ;
	
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MSG_LOGIN_ERROR:
				// 设置按钮为可点击
				btnLogin.setClickable(true);
				btnLogin.setBackgroundResource(R.drawable.selector_login);
				progressDialog.dismiss();
				handler.removeCallbacks(btnTimerRunnable);
			
				String errorMsg = (String) msg.obj;
				ToastUtils.show(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT);
				break;
			case MSG_CONN_ERROR:
				// 设置按钮为可点击
				btnLogin.setClickable(true);
				btnLogin.setBackgroundResource(R.drawable.selector_login);
				progressDialog.dismiss();

				ToastUtils.show(LoginActivity.this, "连接失败", Toast.LENGTH_SHORT);
				break;
			case BTN_TIMER_OVER:
				// 设置按钮为可点击
				btnLogin.setClickable(true);
				btnLogin.setBackgroundResource(R.drawable.selector_login);
				handler.removeCallbacks(btnTimerRunnable);
				progressDialog.dismiss();
				break;
			case MSG_LOGIN_SUCCESS:
				try {
					//关闭登录认证服务器的socket
					MyApplication.authSocketConn.closeAuthSocket();
					Thread.sleep(5);
					//创建家政服务器的socket
					
					if(UniversalUtils.isStringEmpty(PushData.getHouseIp())){
						new Thread(hsRunnable).start();
					}else{
						if(MyApplication.houseSocketConn != null){
							MyApplication.houseSocketConn.closeHouseSocket();
							Thread.sleep(5);
						}
						MyApplication.houseSocketConn = new HouseSocketConn(PushData.getHouseIp(),PushData.getHousePort());
					}
					
					Thread.sleep(500);
					loadConnHouseBroad();
					Thread.sleep(500);
					btnLogin.setClickable(true);
					btnLogin.setBackgroundResource(R.drawable.selector_login);
					handler.removeCallbacks(btnTimerRunnable);
					progressDialog.dismiss();
					LoginActivity.this.finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
		setContentView(R.layout.activity_login);
		
		myApplication = (MyApplication)getApplication();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initViews();
		myApplication = (MyApplication) getApplication();

	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		String spAccount = (String) SPUtils.get(this, Define.SP_KEY_ACCOUNT, "");
		if(!UniversalUtils.isStringEmpty(spAccount)){
			uname.setText(spAccount);
			uname.setSelection(uname.getText().length());
		}
		
//		if(myApplication.authSocketConn.isClose()){
//			LogUtils.i("Auth socket已断开");
//		}else{
//			LogUtils.i("Auth socket处于连接状态");
//		}
		
		
	}
	
	
	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		losepwd = (TextView) findViewById(R.id.tv_losepwd);
		toreg = (TextView) findViewById(R.id.tv_toreg);
		uname = (ClearEditText) findViewById(R.id.et_username);
		upwd = (ClearEditText) findViewById(R.id.et_pwd);
		losepwd.setOnClickListener(this);
		toreg.setOnClickListener(this);
		btnLogin = (Button) findViewById(R.id.bt_login);
		btnLogin.setOnClickListener(this);
		soLogin = (RadioGroup) findViewById(R.id.rg_login);
		soLogin.setOnCheckedChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login:

			if(UniversalUtils.isStringEmpty(uname.getText().toString().trim())){
				ToastUtils.show(this, "账号不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(upwd.getText().toString().trim())){
				ToastUtils.show(this, "密码不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(upwd.getText().toString().trim().length() < 6){
				ToastUtils.show(this, "密码长度不能小于六位", Toast.LENGTH_SHORT);
				return;
			}
			
			//隐藏软键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			if (imm != null) {
				imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
			}  
			
			if(!MyApplication.connState){//判断网络连接状态
				ToastUtils.show(this, "网络连接错误，请检查网络连接", 0);
				return;
			}
			
			// 设置按钮为不可点击
			btnLogin.setClickable(false);
			btnLogin.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
			//设置加载进度条
			progressDialog = new CustomProgressDialog(this);
			progressDialog.show();

			
			//判断登录认证服务器是否连接，若已连接则直接发送登录请求，若未连接则需要先发送连接请求，在发送登录
			if(myApplication.authSocketConn == null || myApplication.authSocketConn.isClose()){
				
				if (UniversalUtils.isStringEmpty(PushData.getAuthIp())) {
					new Thread(anthRunnable).start();
				} else {
					try {
						
						if (myApplication.authSocketConn != null) {
							myApplication.authSocketConn.closeAuthSocket();
							Thread.sleep(5);
						}

						myApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());

						Thread.sleep(100);
						loadConnectAuthDataBroad();// 连接登录认证服务器
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				loadLoginData();// 登录请求
			}
			

			break;
		case R.id.tv_losepwd:
			intent = new Intent(this, BackPwdActivity.class);
			startActivity(intent);
			break;
		case R.id.tv_toreg:
			intent = new Intent(this, RegisteredActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}

	}

	/**
	 *  用于对按钮不可点击进行计时
	 */
	Runnable btnTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(BTN_TIMER_OVER);
		}
	};


	// 第三方登录
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.login_phone:
			ToastUtils.showToast(this, "敬请期待", 0);
			break;
		case R.id.login_wechat:
			ToastUtils.showToast(this, "敬请期待", 0);
			break;

		case R.id.login_qq:
			ToastUtils.showToast(this, "敬请期待", 0);
			break;

		case R.id.login_sina:
			ToastUtils.showToast(this, "敬请期待", 0);
			break;

		}
	}
	
	
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		
		if(MyApplication.authSocketConn != null){
			myApplication.authSocketConn.closeAuthSocket();
		}
		
		super.onDestroy();
	}
	
	
	
	/**
	 * 登录请求
	 */
	private void loadLoginData() {
		seqLogin = MyApplication.SequenceNo++;
		LoginReq loginReq = new MsgInncDef().new LoginReq();
		// loginReq.strAccountID = "15991712201";
		// loginReq.strPasswd = StringUtil.MD5Encode("111111");
		loginReq.strAccountID = uname.getText().toString().trim();
		loginReq.strPasswd = StringUtil.MD5Encode(upwd.getText().toString().trim());
		pwdMd5 = loginReq.strPasswd;
		byte[] connData = HandleNetSendMsg.HandleLoginToPro(loginReq,seqLogin);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("登录请求--sequence="+seqLogin +"/"+ Arrays.toString(connData) );
	}
	
	
	/**
	 * 连接家政服务器
	 */
	private void loadConnHouseBroad() {
		seqConnHouse = MyApplication.SequenceNo++;
		MyApplication.seqLoginConnHouse = seqConnHouse;
		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),seqConnHouse);
		// 连接家政服务器
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("连接家政服务器请求数据--sequence="+seqConnHouse +"/"+ Arrays.toString(connData) );
	}
	
	
	/**
	 * 加载 连接请求 -- 登录认证服务器
	 */
	public void loadConnectAuthDataBroad() {
		seqConnAuth = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),seqConnAuth);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("连接登录服务器请求数据--sequence="+seqConnAuth +"/"+ Arrays.toString(connData) + "----------");
	}
	
	
	/**
	 * 加载 最优服务器列表
	 * 
	 * @param userId
	 */
	public void loadDataServerListBroad() {
		seqSerList = MyApplication.SequenceNo++;
		// 获取服务器列表请求 使用通用请求的数据
		AuthNetCommonReq serverInfo = new MsgInncDef().new AuthNetCommonReq();
		serverInfo.iUserid = 0;
		// seqSer = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleServerListReqToPro(serverInfo,seqSerList);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("获取服务器列表请求数据--sequence="+seqSerList +"/"+ Arrays.toString(connData)
				+ "=============");
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
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqLogin == iSequence) {
					processLoginData(recvTime);
				}
				else if(seqConnAuth == iSequence){
					processConnAuthData(recvTime);
				}
				else if(seqSerList == iSequence){
					processServerListData(recvTime);
				}
			}
		}
	};
	
	
	/**
	 *  处理获取最优服务器的数据
	 * @param recvTime
	 */
	private void processServerListData(long recvTime) {
		GetServerInfoResp serverInfo = (GetServerInfoResp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (serverInfo == null) {
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

			LogUtils.i("最优服务器--" + "ip = " + ip + ", port = " + port
					+ ", strDns =" + strDns);

		}
	}
	
	/**
	 * 处理登录响应的数据
	 * @param recvTime
	 */
	private void processLoginData(long recvTime){
		AuthLoginResp authLoginResp = (AuthLoginResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		
		if(authLoginResp == null){
			return;
		}
		if (authLoginResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int userId = authLoginResp.iuserid;
			String phone = authLoginResp.szPhoneNum;
			String szUserID = authLoginResp.szUserID;
			String szUserNick = authLoginResp.szUserNick;
			String szSignaTure = authLoginResp.szSignaTure;

			myApplication.userId = userId;
			// 操作数据库，将部分表中内容清除
			DataBaseService ds = new DataBaseService(getApplicationContext());
			// 删除authInfo用户信息表中数据，需要重新将目前登录用户的用户信息加入表中
			String deleteUserSql = DbOprationBuilder.deleteBuilder("authInfo");
			// 删除orderList订单列表表中的数据，防止切换用户后还能查到之前的用户数据
			String deleteOrderSql = DbOprationBuilder.deleteBuilder("orderList");
			ds.delete(deleteUserSql);
			ds.delete(deleteOrderSql);
			// 将个人信息存入数据库
			String sql = DbOprationBuilder.insertUserInfoAllBuilder(authLoginResp, pwdMd5, 1).toString();
			ds.insert(sql);

			myApplication.loginState = true;
			
			SPUtils.put(this, Define.SP_KEY_ACCOUNT, uname.getText().toString().trim());
			
			LogUtils.i("userid = " + userId + "/ phone = " + phone + "/ szUserID= " + szUserID + "/ szUserNick =" +
					szUserNick + "/ szSignaTure = " + szSignaTure);
			
			handler.sendEmptyMessage(MSG_LOGIN_SUCCESS);

		} else {
			String result = UniversalUtils.judgeNetResult_Auth(authLoginResp.eResult);
			Message message = new Message();
			message.what = MSG_LOGIN_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			LogUtils.i("登录失败"+ result );
		}
	}
	
	
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

			LogUtils.i("连接登录认证服务器--" + "time = " + time + "/ userId = "+ userId);
			
			if(UniversalUtils.isStringEmpty(PushData.getHouseIp())){
				loadDataServerListBroad();
			}
			loadLoginData();// 当连接上登录认证服务器后需要发送登录请求
		}
	}
	
	
	
	
	/**
	 * 启动线程解析域名
	 * 用于解析登录认证服务器的域名
	 */
	Runnable anthRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (UniversalUtils.isStringEmpty(MyApplication.netParam
					.getAuthDns())) {
				PushData.setAuthIp(MyApplication.netParam.getAuthIp());
				PushData.setAuthPort(MyApplication.netParam.getAuthPort());
			} else {
				String strDns = MyApplication.netParam.getAuthDns();

				if (strDns.indexOf(":") > 0) {
					String ip = DNSParsing.getIP(strDns.substring(0,
							strDns.indexOf(":")));
					int port = Integer.parseInt(strDns.substring(
							strDns.indexOf(":") + 1, strDns.length()));
					MyApplication.netParam.setAuthDnsParsIp(ip);
					MyApplication.netParam.setAuthDnsParsPort(port);
					PushData.setAuthIp(ip);
					PushData.setAuthPort(port);
				} else {
					PushData.setAuthIp(MyApplication.netParam.getAuthIp());
					PushData.setAuthPort(MyApplication.netParam.getAuthPort());
				}

			}

			if (MyApplication.authSocketConn != null) {
				myApplication.authSocketConn.closeAuthSocket();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			LogUtils.i("创建 Auth socket");
			myApplication.authSocketConn = new AuthSocketConn(
					PushData.getAuthIp(), PushData.getAuthPort());
			loadConnectAuthDataBroad();
		}
	};
	
	
	/**
	 * 启动线程解析域名
	 * 用于解析家政服务器的域名
	 */
	Runnable hsRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (UniversalUtils.isStringEmpty(MyApplication.netParam.getHsDns())) {
				PushData.setHouseIp(MyApplication.netParam.getHsIp());// 把得到的最优服务器ip和port放入结构体中
				PushData.setHousePort(MyApplication.netParam.getHsPort());
			} else {
				String strDns = MyApplication.netParam.getHsDns();

				if (strDns.indexOf(":") > 0) {
					String strIp = DNSParsing.getIP(strDns.substring(0,
							strDns.indexOf(":")));
					int iPort = Integer.parseInt(strDns.substring(
							strDns.indexOf(":") + 1, strDns.length()));
					MyApplication.netParam.setHsDnsParsIp(strIp);
					MyApplication.netParam.setHsDnsParsPort(iPort);
					// 把得到的最优服务器ip和port放入结构体中
					PushData.setHouseIp(strIp);
					PushData.setHousePort(iPort);
				} else {
					PushData.setHouseIp(MyApplication.netParam.getHsIp());// 把得到的最优服务器ip和port放入结构体中
					PushData.setHousePort(MyApplication.netParam.getHsPort());
				}

			}

			if(MyApplication.houseSocketConn != null){
				MyApplication.houseSocketConn.closeHouseSocket();
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			myApplication.houseSocketConn = new HouseSocketConn(
					PushData.getHouseIp(), PushData.getHousePort());
		}
	};


}
