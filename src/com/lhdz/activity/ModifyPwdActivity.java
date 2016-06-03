package com.lhdz.activity;


/**
 * 修改密码
 * 
 * @author wangf
 */

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
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
import com.lhdz.wediget.ClearEditText;

public class ModifyPwdActivity extends BaseActivity implements OnClickListener{

	private TextView title;
	
	private ClearEditText et_modify_old_pwd;
	private ClearEditText et_modify_first_pwd;
	private ClearEditText et_modify_second_pwd;
	private Button btn_modify_sure;
	
	private String dbOldMd5Pwd = "";//数据库保存的用户的旧密码
	private String oldMd5Pwd = "";//用户输入的旧密码的md5值
	
	private String oldPwd = "";//用户输入的旧密码
	private String firstPwd = "";//用户输入的新密码
	private String secondPwd = "";//用户输入的确认新密码
	
	private int iUserId = -1;
	
	private final static int MSG_LOAD_ERROR = 0;
	private final static int MSG_LOAD_SUCCESS = 1;
	private final static int BTN_TIMER_OVER = 3;
	
	private int seqModifyPwd = -1;
	private int seqConnAuth = -1;//连接登录认证的sequence
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pwd);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initViews();
		backArrow();
		queryAuthInfoData();
		
	}

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("修改密码");
		
		et_modify_old_pwd = (ClearEditText) findViewById(R.id.et_modify_old_pwd);//旧密码
		et_modify_first_pwd = (ClearEditText) findViewById(R.id.et_modify_first_pwd);//新密码
		et_modify_second_pwd = (ClearEditText) findViewById(R.id.et_modify_second_pwd);//确认新密码
		
		btn_modify_sure = (Button) findViewById(R.id.btn_modify_sure);
		btn_modify_sure.setOnClickListener(this);
		
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				ToastUtils.show(ModifyPwdActivity.this, "修改密码成功，请重新登录", Toast.LENGTH_SHORT);
				
				
				MyApplication.loginState = false;
				MyApplication.userId = 0;
				String sql = DbOprationBuilder.deleteBuilder("authInfo");
				DataBaseService ds = new DataBaseService(ModifyPwdActivity.this);
				ds.delete(sql);
				
				if(MyApplication.houseSocketConn != null){
					MyApplication.houseSocketConn.closeHouseSocket();
				}
				
				Intent intent = new Intent(ModifyPwdActivity.this, LoginActivity.class);
				startActivity(intent);
				
				if(AccoutSafeActivity.instanceAccoutSafeActivity != null){
					AccoutSafeActivity.instanceAccoutSafeActivity.finish();
				}
				if(SettingActivity.instanceSettingActivity != null){
					SettingActivity.instanceSettingActivity.finish();
				}
				
				finish();
				break;
			case MSG_LOAD_ERROR:
				//设置按钮为可点击
				btn_modify_sure.setClickable(true);
				btn_modify_sure.setBackgroundResource(R.drawable.selector_oppointment);
				ToastUtils.show(ModifyPwdActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT);
				break;
			case BTN_TIMER_OVER:
				//设置按钮为可点击
				btn_modify_sure.setClickable(true);
				btn_modify_sure.setBackgroundResource(R.drawable.selector_oppointment);
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
		switch (v.getId()) {
		case R.id.btn_modify_sure:
			
			oldPwd = et_modify_old_pwd.getText().toString().trim();
			firstPwd = et_modify_first_pwd.getText().toString().trim();
			secondPwd = et_modify_second_pwd.getText().toString().trim();
			
			oldMd5Pwd = StringUtil.MD5Encode(oldPwd);
			
			if(!oldMd5Pwd.equals(dbOldMd5Pwd)){
				ToastUtils.show(this, "您输入的旧密码错误", Toast.LENGTH_SHORT);
				return;
			}
			
			if(UniversalUtils.isStringEmpty(firstPwd)){
				ToastUtils.show(ModifyPwdActivity.this, "密码不能为空", Toast.LENGTH_SHORT);
				return;
			}
			if(UniversalUtils.isStringEmpty(secondPwd)){
				ToastUtils.show(ModifyPwdActivity.this, "确认密码不能为空", Toast.LENGTH_SHORT);
				return;
			}
			
			if(firstPwd.length() < 6){
				ToastUtils.show(this, "密码长度不能小于六位", Toast.LENGTH_SHORT);
				return;
			}
			if(secondPwd.length() < 6){
				ToastUtils.show(this, "确认密码长度不能小于六位", Toast.LENGTH_SHORT);
				return;
			}
			if(!firstPwd.equals(secondPwd)){
				ToastUtils.show(ModifyPwdActivity.this, "密码和确认密码不一致", Toast.LENGTH_SHORT);
				return;
			}
			
			
			//设置按钮为不可点击
			btn_modify_sure.setClickable(false);
			btn_modify_sure.setBackgroundResource(R.drawable.shape_btn_click_not);
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
				loadModifyPwdData();
			}
			
			
			break;

		default:
			break;
		}
		
	}
	
	
	
	/**
	 * 查询数据库中的用户信息
	 */
	public void queryAuthInfoData() {
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryAllBuilder("authInfo");
		List<Map<String, String>> authInfoDataList = ds.query(sql);
		
		if(authInfoDataList.size() != 0){
			dbOldMd5Pwd = authInfoDataList.get(0).get("passWord");
			iUserId = Integer.parseInt(authInfoDataList.get(0).get("userId"));
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
					ModifyPwdActivity.this.finish();

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
	 * 修改密码的请求
	 */
	private void loadModifyPwdData() {
		seqModifyPwd = MyApplication.SequenceNo++ ;
		ResetPasswordReq resetPasswordReq = new MsgInncDef().new ResetPasswordReq();
		resetPasswordReq.iuserid = iUserId;
		resetPasswordReq.szNewPwd = StringUtil.MD5Encode(secondPwd);

		byte[] connData = HandleNetSendMsg.HandleResetPasswordReqToPro(resetPasswordReq,seqModifyPwd);
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("修改密码的请求--sequence="+seqModifyPwd +"/"+Arrays.toString(connData) + "=============");
	}
	
	
	/**
	 *  广播接收者
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqModifyPwd == iSequence) {
					processModifyPwdData(recvTime);
				}
				else if(seqConnAuth == iSequence){
					processConnAuthData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理修改密码的响应数据
	 * @param recvTime
	 */
	private void processModifyPwdData(long recvTime){
		AuthNetCommonResp commonResp = (AuthNetCommonResp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(commonResp == null){
			return;
		}
		
		handler.removeCallbacks(btnTimerRunnable);
		if (commonResp.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
			
			LogUtils.i("修改密码成功"+"=============");

			Message message =new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);

		} else {
			String result = UniversalUtils.judgeNetResult_Auth(commonResp.eResult);
			LogUtils.i("修改密码失败=="+ result+"=============");
			
			Message message =new Message();
			message.what = MSG_LOAD_ERROR;
			message.obj = result;
			handler.sendMessage(message);
		}
	}
	
	
	/** 
	 * 处理连接登录认证服务器的数据
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

				loadModifyPwdData();
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
