package com.lhdz.activity;

/**
 * 用户注册
 * @author 王哲
 * @date 2015-8-26
 */
import java.util.Arrays;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.EnumPro.eACCOUNTTYPE_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.NetConnectReq;
import com.lhdz.publicMsg.MsgInncDef.QuickRegisterReq;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MsgReceiveDef.QuickRegisterResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.RegistAccoutUtil;
import com.lhdz.util.StringUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.TimeButton;

public class RegisteredActivity extends BaseActivity implements OnClickListener {

	private TextView title;
	private EditText name, pwd, spwd, phone;
	private Button btnRegist;
	Intent intent;
	private TimeButton timeButton;
	private String mobile;
	private EditText vertifacation;

	private final static int MSG_REGIST_SUCCESS = 1;
	private final static int MSG_REGIST_ERROR = 2;
	private final static int BTN_TIMER_OVER = 3;
	
	private int seqConnAuth = -1;//连接登录认证服务器的sequence
	private int seqRegistNo = -1;//注册的sequence
	
	private MyApplication myApplication;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registered);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		
		initViews();
		timeButton.onCreate(savedInstanceState);
		backArrow();


	}
	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case MSG_REGIST_SUCCESS:
				
				RegisteredActivity.this.finish();
				
				break;
			case MSG_REGIST_ERROR:
				// 设置按钮为可点击
				btnRegist.setClickable(true);
				btnRegist
						.setBackgroundResource(R.drawable.selector_oppointment);
//				String errorMsg = (String) msg.obj;
//				Toast.makeText(RegisteredActivity.this, errorMsg,
//						Toast.LENGTH_SHORT).show();
				break;
			case BTN_TIMER_OVER:
				// 设置按钮为可点击
				btnRegist.setClickable(true);
				btnRegist
						.setBackgroundResource(R.drawable.selector_oppointment);
				handler.removeCallbacks(btnTimerRunnable);
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
		title.setText("注册");
		name = (EditText) findViewById(R.id.et_username);
		pwd = (EditText) findViewById(R.id.et_pwd);
		spwd = (EditText) findViewById(R.id.et_spwd);
		phone = (EditText) findViewById(R.id.et_tel);
		vertifacation = (EditText) findViewById(R.id.et_vertifacation);
		btnRegist = (Button) findViewById(R.id.bt_reg);
		btnRegist.setOnClickListener(this);
		timeButton = (TimeButton) findViewById(R.id.send_code);
		timeButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_reg:
			
			String strName = name.getText().toString().trim();
			String strPwd = pwd.getText().toString().trim();
			String strSpwd = spwd.getText().toString().trim();
			String strPhone = phone.getText().toString().trim();
			String strVertifacation = vertifacation.getText().toString().trim();
			
			
			if(UniversalUtils.isStringEmpty(strName)){
				ToastUtils.show(this, "用户昵称不能为空", 0);
				return;
			}
			if(UniversalUtils.isStringEmpty(strPwd)){
				ToastUtils.show(this, "密码不能为空", 0);
				return;
			}
			if(strPwd.length() < 6){
				ToastUtils.show(this, "密码长度不能小于6位", 0);
				return;
			}
			if(!strPwd.equals(strSpwd)){
				ToastUtils.show(this, "两次输入密码不一致", 0);
				return;
			}
			if(UniversalUtils.isStringEmpty(strVertifacation)){
				ToastUtils.show(this, "请输入验证码", 0);
				return;
			}
			if((MyApplication.vertifacationCode != Integer.parseInt(strVertifacation))){
				ToastUtils.show(this, "验证码不正确", 0);
				return;
			}
			if(!strPhone.equals(mobile)){
				ToastUtils.show(this, "您输入手机号码已改变，请重新输入", 0);
				return;
			}
		
			//若满足以上条件，则发送注册请求
			
			// 设置按钮为不可点击
			btnRegist.setClickable(false);
			btnRegist.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);

			// 判断登录认证服务器是否连接，若已连接则直接发送注册请求，若未连接则需要先发送连接请求，再发送注册请求
			if (myApplication.authSocketConn.isClose()) {
				myApplication.authSocketConn = new AuthSocketConn(
						PushData.getAuthIp(), PushData.getAuthPort());
				try {
					Thread.sleep(1000);
					loadConnectAuthData();// 连接登录认证服务器
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} else {
				loadRegistData();// 发送注册请求
			}
				
				
			break;
		case R.id.send_code:

			vertifacation.requestFocus();// 点击获取验证码之后让填充验证码框得到焦点

			// 点击按扭之后，判断手机号码格式是否正确
			mobile = phone.getText().toString().trim();
			if(UniversalUtils.isStringEmpty(mobile)){
				ToastUtils.show(this, "请输入正确的手机号", 0);
				return;
			}
			timeButton.setMobile(mobile);
			if (mobile.length() < 11) {
				ToastUtils.show(this, "请输入正确的手机号", 0);
			} else {
				// 获取验证码
				timeButton.settextAfter("秒后重新获取").setTextBefore("获取验证码")
						.setLenght(60 * 1000);
				new Thread(registThread).start();
			}
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

	/**
	 * 发送短信验证码
	 * 与网络有关的操作，必须另起一个线程
	 */
	Runnable registThread = new Runnable() {
		public void run() {
			RegistAccoutUtil.sendSMS(mobile);
		}
	};

	
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
					RegisteredActivity.this.finish();

					break;
				}
			}
		});
	}

//	/*
//	 * 请求注册帐号信息
//	 */
//	private void loadData() {
//		QuickRegisterReq registerReq = new MsgInncDef().new QuickRegisterReq();
//		registerReq.iAccountType = eACCOUNTTYPE_PRO.en_ACCOUNT_PHONE_PRO;
//		registerReq.iAgentID = 0;
//		registerReq.sAccountID = phone.getText().toString().trim();
//		registerReq.szPwd = StringUtil.MD5Encode(pwd.getText().toString()
//				.trim());
//		registerReq.szUserNick = name.getText().toString();
//		registerReq.szRegIP = "192.168.0.60";
//
//		byte[] connData = HandleNetSendMsg
//				.HandleQuickRegisterToPro(registerReq);
//		SocketConn.pushtoList(connData);
//		LogUtils.i("connData快速注册的请求" + connData + "=============");
//
//		SocketConn.callbackRecData(new CallBackMsg() {
//
//			@Override
//			public void RecvMsgSuccess(byte[] msg) {
//				byte[] proBufHead = UniversalUtils.getNetMsgHead(msg);
//				HandleNetHeadMsg headMsg = HandleNetHeadMsg
//						.parseHeadMag(proBufHead);
//				if (headMsg.uiMsgType != NetHouseMsgType.NETAUTH_QUICKREGISTE_RESP)
//					return;
//				LogUtils.i("recv快速注册返回数据成功" + msg + "=============");
//				QuickRegisterResp registerResp = (QuickRegisterResp) HandleNetReceiveMsg
//						.getParseMsgType(msg);
//				handler.removeCallbacks(btnTimerRunnable);
//				if (registerResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
//					Log.i("服务器端注册成功", "=============");
//					String accout = registerResp.szAccountID;
//					String szActivKey = registerResp.szActivKey;
//
//					LogUtils.i("帐号" + accout + "=============");
//					LogUtils.i("激活码" + szActivKey + "=============");
//					finish();
//
//				} else {
//					String result = UniversalUtils
//							.judgeNetResult_Auth(registerResp.eResult);
//
//					Message message = new Message();
//					message.what = msgRegistError;
//					message.obj = result;
//					handler.sendMessage(message);
//					LogUtils.i("注册失败==" + result + "=============");
//				}
//
//			}
//
//			@Override
//			public void RecvMsgLose() {
//
//			}
//		});
//	}
	
	
	/**
	 * 加载 连接请求 -- 登录认证服务器
	 */
	public void loadConnectAuthData() {
		seqConnAuth = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),seqConnAuth);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("连接登录服务器请求数据--sequence="+seqConnAuth +"/"+ Arrays.toString(connData) + "----------");
	}
	
	
	
	/**
	 * 注册帐号请求
	 */
	private void loadRegistData() {
		seqRegistNo = MyApplication.SequenceNo++;
		QuickRegisterReq registerReq = new MsgInncDef().new QuickRegisterReq();
		registerReq.iAccountType = eACCOUNTTYPE_PRO.en_ACCOUNT_PHONE_PRO;
		registerReq.iAgentID = 0;
		registerReq.sAccountID = phone.getText().toString().trim();
		registerReq.szPwd = StringUtil.MD5Encode(pwd.getText().toString().trim());
		registerReq.szUserNick = name.getText().toString();
		registerReq.szRegIP = "";

		byte[] connData = HandleNetSendMsg.HandleQuickRegisterToPro(registerReq,seqRegistNo);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("connData快速注册的请求--sequence="+seqRegistNo +"/"+ connData + "=============");
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

				if (seqRegistNo == iSequence) {
					processRegistData(recvTime);
				}
				else if(seqConnAuth == iSequence){
					processConnAuthData(recvTime);
				}
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

			loadRegistData();// 当连接上登录认证服务器后需要发送注册请求
		}
	}
	
	
	
	/**
	 * 处理注册的响应数据
	 * @param recvTime
	 */
	private void processRegistData(long recvTime){
		QuickRegisterResp registerResp = (QuickRegisterResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(registerResp == null){
			return ;
		}
		LogUtils.i("快速注册返回数据成功");
		handler.removeCallbacks(btnTimerRunnable);
		if (registerResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			Log.i("服务器端注册成功", "=============");
			String accout = registerResp.szAccountID;
			String szActivKey = registerResp.szActivKey;

			LogUtils.i("帐号" + accout + "=============");
			LogUtils.i("激活码" + szActivKey + "=============");
			
			ToastUtils.show(this, "注册成功", 1);
			
			Message message = new Message();
			message.what = MSG_REGIST_SUCCESS;
			handler.sendMessage(message);

		} else {
			String result = UniversalUtils.judgeNetResult_Auth(registerResp.eResult);
			
			ToastUtils.show(this, result, 1);

			Message message = new Message();
			message.what = MSG_REGIST_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			LogUtils.i("注册失败==" + result + "=============");
		}
	}
	
	

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mReceiver);
		timeButton.onDestroy();
	}
}
