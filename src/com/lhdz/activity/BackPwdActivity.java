package com.lhdz.activity;


/**
 * 找回密码
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
import com.lhdz.publicMsg.MsgInncDef.ForgetPassWordReq;
import com.lhdz.publicMsg.MsgReceiveDef.ForgotPasswordResp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.RegistAccoutUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.TimeButton;

public class BackPwdActivity extends BaseActivity implements OnClickListener{

	private TextView title;
	
	private EditText et_back_phone;//手机号码
	private EditText et_back_code;//验证码
	private TimeButton btn_back_code;//发送验证码按钮
	private Button btn_back_sure;//确定按钮
	
	private String strMobile = "";//手机号码--必须有初始值
	
	private final static int MSG_LOAD_ERROR = 0;
	private final static int BTN_TIMER_OVER = 3;
	
	private MyApplication myApplication;
	
	private int seqConnAuth = -1;//连接登录认证的sequence
	private int seqBackPwdNo = -1;//找回密码的sequence
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_back_pwd);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initViews();
		backArrow();
		
	}

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("找回密码");
		
		et_back_phone = (EditText) findViewById(R.id.et_back_phone);//手机号码
		et_back_code = (EditText) findViewById(R.id.et_back_code);//验证码
		btn_back_code = (TimeButton) findViewById(R.id.btn_back_code);//发送验证码按钮
		btn_back_sure = (Button) findViewById(R.id.btn_back_sure);//确定按钮
		
		btn_back_code.setOnClickListener(this);
		btn_back_sure.setOnClickListener(this);
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
//			case 1:
////				Toast.makeText(BackPwdActivity.this, "登录失败，请重试", Toast.LENGTH_SHORT).show();
//				break;
			case MSG_LOAD_ERROR:
				//设置按钮为可点击
				btn_back_sure.setClickable(true);
				btn_back_sure.setBackgroundResource(R.drawable.selector_oppointment);
				ToastUtils.show(BackPwdActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT);
				break;
			case BTN_TIMER_OVER:
				//设置按钮为可点击
				btn_back_sure.setClickable(true);
				btn_back_sure.setBackgroundResource(R.drawable.selector_oppointment);
				handler.removeCallbacks(btnTimerRunnable);
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
	};
	

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btn_back_sure:
			// 确认按钮
			if(UniversalUtils.isStringEmpty(et_back_phone.getText().toString().trim())){
				ToastUtils.show(BackPwdActivity.this, "请输入手机号码", 0);
				return;
			}
			if (et_back_phone.getText().toString().trim().length() < 11) {
				ToastUtils.show(BackPwdActivity.this, "请输入正确的手机号",Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(et_back_code.getText().toString().trim())){
				ToastUtils.show(BackPwdActivity.this, "请输入验证码", 0);
				return;
			}
			if (MyApplication.vertifacationCode != Integer
					.parseInt(et_back_code.getText().toString().trim())) {
				ToastUtils.show(BackPwdActivity.this, "验证码不正确", 0);
				return;
			} 
			if (!strMobile.equals(et_back_phone.getText().toString().trim())) {
				ToastUtils.show(BackPwdActivity.this, "您输入手机号码已改变", 0);
				return;
			}

			// 设置按钮为不可点击
			btn_back_sure.setClickable(false);
			btn_back_sure.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);

			
			
			
			//判断登录认证服务器是否连接，若已连接则直接发送忘记密码请求，若未连接则需要先发送连接请求，再发送忘记密码请求
			if(myApplication.authSocketConn == null||myApplication.authSocketConn.isClose()){
				myApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(),PushData.getAuthPort());
				try {
					Thread.sleep(100);
					loadConnectAuthData();//连接登录认证服务器
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				loadBackPwdData();// 发送忘记密码请求
			}
			
			

			break;
		case R.id.btn_back_code:
			//发送验证码按钮
			strMobile = et_back_phone.getText().toString().trim();
			
			if (UniversalUtils.isStringEmpty(strMobile)) {
				ToastUtils.show(BackPwdActivity.this, "请输入正确的手机号码",Toast.LENGTH_SHORT);
				return;
			}
			if (strMobile.length() < 11) {
				ToastUtils.show(BackPwdActivity.this, "请输入正确的手机号",Toast.LENGTH_SHORT);
			} else {
				// 获取验证码
				btn_back_code.setMobile(strMobile);
				btn_back_code.settextAfter("秒后重新获取验证码").setTextBefore("获取验证码").setLenght(60 * 1000);
				new Thread(sendCodeThread).start();
			}
			
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
	Runnable sendCodeThread = new Runnable() {
		public void run() {
			RegistAccoutUtil.sendSMS(strMobile);
		}
	};
	
	
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
					BackPwdActivity.this.finish();
					break;
				}
			}
		});
	}
	
	
	
	

	
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
	 * 忘记密码的请求
	 */
	private void loadBackPwdData() {
		seqBackPwdNo = MyApplication.SequenceNo++;
		ForgetPassWordReq forgetPassWordReq = new MsgInncDef().new ForgetPassWordReq();
		forgetPassWordReq.szAccountType = eACCOUNTTYPE_PRO.en_ACCOUNT_PHONE_PRO; 
		forgetPassWordReq.szAccountID = strMobile;

		byte[] connData = HandleNetSendMsg
				.HandleForgetPassWordToPro(forgetPassWordReq,seqBackPwdNo);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("找回密码的请求--sequence="+seqBackPwdNo +"/"+Arrays.toString(connData) + "=============");

		
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

				if (seqBackPwdNo == iSequence) {
					processBackPwdData(recvTime);
				}
				else if(seqConnAuth == iSequence){
					processConnAuthData(recvTime);
				}
			}
		}
	};
	
	
	
	// 处理连接登录认证服务器的数据
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

			loadBackPwdData();// 当连接上登录认证服务器后需要发送找回密码请求
		}
	}
	
	
	
	/**
	 * 处理找回密码响应的数据
	 * @param iSequence
	 */
	private void processBackPwdData(long recvTime){
		ForgotPasswordResp forgotPasswordResp = (ForgotPasswordResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(forgotPasswordResp == null){
			return;
		}
		handler.removeCallbacks(btnTimerRunnable);
		if (forgotPasswordResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			int iuserid = forgotPasswordResp.iuserid;//userid
			String szUserID = forgotPasswordResp.szUserID;//账号id
			String szMobile = forgotPasswordResp.szMovbile;//手机号
			String szEmail = forgotPasswordResp.szEmail;//邮箱
			String szUserNick = forgotPasswordResp.szUserNick;//用户昵称
			
			
			LogUtils.i("找回密码成功"+"iuserid="+iuserid+" szUserID="+szUserID+" szMobile= "+szMobile +
					"szEmail="+szEmail+"szUserNick="+szUserNick+"=============");

			//跳转到重设密码界面
			Intent intent = new Intent(BackPwdActivity.this,SetPwdActivity.class);
			intent.putExtra("iuserid", iuserid);
			startActivity(intent);
			finish();

		} else {
			String result = UniversalUtils.judgeNetResult_Auth(forgotPasswordResp.eResult);
			LogUtils.i("找回密码失败=="+ result+"=============");
			
			Message message =new Message();
			message.what = MSG_LOAD_ERROR;
			message.obj = result;
			handler.sendMessage(message);
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(MyApplication.authSocketConn != null){
			MyApplication.authSocketConn.closeAuthSocket();
		}
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
	
}
