package com.lhdz.activity;
/**
 * 设置密码
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
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.NetConnectReq;
import com.lhdz.publicMsg.MsgInncDef.ResetPasswordReq;
import com.lhdz.publicMsg.MsgReceiveDef.AuthNetCommonResp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.StringUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

public class SetPwdActivity extends BaseActivity implements OnClickListener{

	private TextView title;
	private EditText et_set_first;
	private EditText et_set_second;
	private Button btn_set_sure;
	
	private String firstPwd = "";//密码
	private String secondPwd = "";//确认密码
	
	private int iUserId = -1;
	private final static int MSG_LOAD_ERROR = 0;
	private final static int MSG_LOAD_SUCCESS = 1;
	private final static int BTN_TIMER_OVER = 3;
	
	private int seqSetPwd = -1;
	private int seqConnAuth = -1;//连接登录认证的sequence
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_pwd);
		iUserId = getIntent().getIntExtra("iuserid", -1);
		
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
		title.setText("设置密码");
		
		et_set_first = (EditText) findViewById(R.id.et_set_first);//密码
		et_set_second = (EditText) findViewById(R.id.et_set_second);//确认密码
		btn_set_sure = (Button) findViewById(R.id.btn_set_sure);
		btn_set_sure.setOnClickListener(this);
		
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				ToastUtils.show(SetPwdActivity.this, "重设密码成功", Toast.LENGTH_SHORT);
				finish();
				break;
			case MSG_LOAD_ERROR:
				//设置按钮为可点击
				btn_set_sure.setClickable(true);
				btn_set_sure.setBackgroundResource(R.drawable.selector_oppointment);
				ToastUtils.show(SetPwdActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT);
				break;
			case BTN_TIMER_OVER:
				//设置按钮为可点击
				btn_set_sure.setClickable(true);
				btn_set_sure.setBackgroundResource(R.drawable.selector_oppointment);
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
		case R.id.btn_set_sure:
			
			firstPwd = et_set_first.getText().toString().trim();
			secondPwd = et_set_second.getText().toString().trim();
			
			if(firstPwd.length() < 6){
				ToastUtils.show(this, "密码长度不能小于六位", Toast.LENGTH_SHORT);
				return;
			}
			if(secondPwd.length() < 6){
				ToastUtils.show(this, "确认密码长度不能小于六位", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(firstPwd)){
				ToastUtils.show(SetPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(secondPwd)){
				ToastUtils.show(SetPwdActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(!firstPwd.equals(secondPwd)){
				ToastUtils.show(SetPwdActivity.this, "密码和确认密码不一致", Toast.LENGTH_SHORT);
				return;
			}
			
			
			//设置按钮为不可点击
			btn_set_sure.setClickable(false);
			btn_set_sure.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
			
			
			//判断登录认证服务器是否连接，若已连接则直接发送忘记密码请求，若未连接则需要先发送连接请求，再发送忘记密码请求
			if(MyApplication.authSocketConn == null||MyApplication.authSocketConn.isClose()){
				MyApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(),PushData.getAuthPort());
				try {
					Thread.sleep(100);
					loadConnectAuthData();//连接登录认证服务器
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}else{
				loadSetPwdData();
			}
			
			
			break;

		default:
			break;
		}
		
	}
	
	
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
					SetPwdActivity.this.finish();

					break;
				}
			}
		});
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
	 * 重置密码的请求
	 */
	private void loadSetPwdData() {
		seqSetPwd = MyApplication.SequenceNo++ ;
		ResetPasswordReq resetPasswordReq = new MsgInncDef().new ResetPasswordReq();
		resetPasswordReq.iuserid = iUserId;
		resetPasswordReq.szNewPwd = StringUtil.MD5Encode(secondPwd);

		byte[] connData = HandleNetSendMsg.HandleResetPasswordReqToPro(resetPasswordReq,seqSetPwd);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("重设密码的请求--sequence="+seqSetPwd +"/"+Arrays.toString(connData) + "=============");
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

				if (seqSetPwd == iSequence) {
					processSetPwdData(recvTime);
				}
				else if(seqConnAuth == iSequence){
					processConnAuthData(recvTime);
				}
				
			}
		}
	};
	
	/**
	 * 处理设置密码的响应数据
	 * 
	 * @param recvTime
	 */
	private void processSetPwdData(long recvTime) {
		AuthNetCommonResp commonResp = (AuthNetCommonResp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (commonResp == null) {
			return;
		}

		handler.removeCallbacks(btnTimerRunnable);
		if (commonResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {

			LogUtils.i("重设密码成功" + "=============");

			Message message = new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);

		} else {
			String result = UniversalUtils
					.judgeNetResult_Auth(commonResp.eResult);
			LogUtils.i("重设密码失败==" + result + "=============");

			Message message = new Message();
			message.what = MSG_LOAD_ERROR;
			message.obj = result;
			handler.sendMessage(message);
		}
	}
	
	
	/**
	 * 处理连接登录认证服务器的数据
	 * 
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

			loadSetPwdData();
		}
	}
		
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		if(MyApplication.authSocketConn != null){
			MyApplication.authSocketConn.closeAuthSocket();
		}
		super.onDestroy();
	}
	
	
}
