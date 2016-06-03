package com.lhdz.service;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.telephony.NeighboringCellInfo;
import android.util.Log;
import android.view.WindowManager;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.alipay.a.a.i;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.AuthSocketConn;
import com.lhdz.socketutil.DNSParsing;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.socketutil.PushData;
import com.lhdz.util.LogUtils;
import com.lhdz.util.NetWorkUtil;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

public class NetWorkStateService extends Service {
	
	public MyBinder binder = new MyBinder();

	private boolean isConn = false;
	private boolean isNetWorkChange = false;
	
	private int checkLinkTimeOut = 0;
	private GetConnState onGetConnstate;
	
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
//				showNetWorkDialog();
			ToastUtils.show(getApplicationContext(), "网络连接错误", 0);
		}
	};
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);// 添加接收网络连接状态改变的action;
		registerReceiver(mReceiver, filter);
		
		
		Timer timer = new Timer();
		timer.schedule(new SocketMoniterTask(), 1000,2000);// 时时监测
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}


	public class MyBinder extends Binder {
		public NetWorkStateService getService() {
			return NetWorkStateService.this;
		}
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			
			if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)){
				boolean isNetWork = NetWorkUtil.isNetworkConnected(context);
				boolean isWifiConn = NetWorkUtil.isWifiConnected(context);
				boolean isMobileConn = NetWorkUtil.isMobileConnected(context);
				LogUtils.i("网络状态--isNetWork = " + isNetWork +"/ isWifiConn = " + isWifiConn + "/ isMobileConn = "
						+ isMobileConn);
				if (isNetWork) {
					
					isConn = true;//连接状态为true
					
					if(MyApplication.isNetConnOpen){
						//用于防止刚进入app时，会进行网络判断，然后会执行该处的连接方法
						MyApplication.isNetConnOpen = false;
						return;
					}
					if(isWifiConn){
						LogUtils.i("-----isWiFiConn");
						againConnectSocket();//重新连接socket
						isNetWorkChange = true;
					}
					if(isMobileConn){
						LogUtils.i("-----isMobileConn");
						againConnectSocket();//重新连接socket
						isNetWorkChange = true;
					}
					
					
					
				} else {
					
					if(MyApplication.houseSocketConn != null){
						MyApplication.houseSocketConn.closeHouseSocket();
					}
					if(MyApplication.authSocketConn != null){
						MyApplication.authSocketConn.closeAuthSocket();
					}
					
					Message message = Message.obtain(handler);
					handler.sendMessage(message);
					
					isConn = false;//连接状态为false
				}
				if (onGetConnstate != null) {
					onGetConnstate.getConnState(isConn);// 通知网络状态改变
					LogUtils.w("网络状态改变--isConn = "+ isConn);
				}
			}
			
		}

	};

	/*
	 * 定义回调接口
	 */

	public interface GetConnState {
		public void getConnState(boolean isConnected);
	}

	// 定义函数
	public void setConnState(GetConnState getConnectState) {
		this.onGetConnstate = getConnectState;
	}

	
	
	/**
	 * 处理socket的连接变化
	 * 
	 */
	class SocketMoniterTask extends TimerTask {

		@Override
		public void run() {
			
			if(!isConn){
				LogUtils.w("无网");
				return;
			}
			
//			if(!MyApplication.loginState){
//				LogUtils.w("未登录");
//				return;
//			}
			
//			if(UniversalUtils.isStringEmpty(PushData.getHouseIp())){
//				return;
//			}
			
			if(isNetWorkChange){
				if(MyApplication.houseSocketConn != null){
					MyApplication.houseSocketConn.closeHouseSocket();
					try {
						Thread.sleep(5);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				MyApplication.houseSocketConn = new HouseSocketConn(
						PushData.getHouseIp(), PushData.getHousePort());
				loadConnectHsDataBroad();
				isNetWorkChange = false;
				return;
			}
			
			if (MyApplication.houseSocketConn == null) {

				LogUtils.w("MyApplication.houseSocketConn == null ");
				
//				MyApplication.houseSocketConn = new HouseSocketConn(
//						PushData.getHouseIp(), PushData.getHousePort());
//				loadConnectHsDataBroad();

			} else {

				if (MyApplication.houseSocketConn.isClose()) {
					
					MyApplication.houseSocketConn.clearMsgList();
					LogUtils.w("MyApplication.houseSocketConn.isClose()");
					
					
					MyApplication.houseSocketConn.closeHouseSocket();
					try {
						Thread.sleep(5);
						MyApplication.houseSocketConn = new HouseSocketConn(
								PushData.getHouseIp(), PushData.getHousePort());
						loadConnectHsDataBroad();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					
					LogUtils.w("checkLinkTimeOut = " + checkLinkTimeOut);

					if (!MyApplication.houseSocketConn.isRecvDataFlag) {
						checkLinkTimeOut++;
						LogUtils.w("checkLinkTimeOut = " + checkLinkTimeOut);
					} else {
						checkLinkTimeOut = 0;
					}

					if (checkLinkTimeOut == 8) {
						checkLinkTimeOut = 0;
						LogUtils.w("checkLinkTimeOut = " + checkLinkTimeOut);
						LogUtils.w("网络进行重连");
						MyApplication.houseSocketConn.clearMsgList();
						MyApplication.houseSocketConn.closeHouseSocket();
						
						try {
							Thread.sleep(5);
							MyApplication.houseSocketConn = new HouseSocketConn(
									PushData.getHouseIp(), PushData.getHousePort());
							loadConnectHsDataBroad();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
		}
	}
	
	
	
	private void showNetWorkDialog(){
		SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.ERROR_TYPE);
		sweetAlertDialog.setTitleText("没网啦！");
		sweetAlertDialog.setCancelText("取消");
		sweetAlertDialog.setConfirmText("确定");
		sweetAlertDialog.setCanceledOnTouchOutside(true);
		sweetAlertDialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				
				
				if (android.os.Build.VERSION.SDK_INT > 13) {// 3.2以上打开设置界面，也可以直接用ACTION_WIRELESS_SETTINGS打开到wifi界面
					Intent intent = new Intent(Settings.ACTION_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} else {
					Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}
				
				sweetAlertDialog.dismissWithAnimation();
				
			}
		});
		sweetAlertDialog.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		sweetAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		sweetAlertDialog.show();
	}
	
	
	
	/**
	 * 根据目前所处的状态，重新连接socket
	 */
	private void againConnectSocket(){
		if(MyApplication.loginState){
//			//登陆后只与家政服务器有关,只需发送连接家政服务器的请求即可
//			if(MyApplication.houseSocketConn != null){
//				MyApplication.houseSocketConn.closeHouseSocket();
//			}
//			try {
//				Thread.sleep(5);
//				MyApplication.houseSocketConn = new HouseSocketConn(PushData.getHouseIp(), PushData.getHousePort());
//				loadConnectHsDataBroad();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}else{
//			//未登录时只与登录认证服务器有关
			if(UniversalUtils.isStringEmpty(PushData.getAuthIp())){
				//无网进入app，未登录，当有网时需要解析域名，并创建登录认证socket，并发送连接登录认证服务器请求
				new Thread(runnable).start();
			}
//			else{
//				//有网进入app，未登录，当网络断开后重新连接时，需要创建登录认证socket，并发送连接登录认证服务器请求
//				if(MyApplication.authSocketConn != null){
//					MyApplication.authSocketConn.closeAuthSocket();
//				}
//				MyApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());
//				loadConnectAuthDataBroad();
//			}
		}
	}
	
	
	
	/**
	 * 加载 连接请求 -- 登录认证服务器
	 */
	private void loadConnectAuthDataBroad() {
		MyApplication.seqServiceConnAuth = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),MyApplication.seqServiceConnAuth);
		// 连接登录服务器
		AuthSocketConn.pushtoList(connData);
		LogUtils.i("连接登录服务器请求数据--sequence="+MyApplication.seqServiceConnAuth +"/"+ Arrays.toString(connData) + "----------");
	}
	
	
	
	/**
	 * 连接家政服务器
	 */
	private void loadConnectHsDataBroad() {
		MyApplication.seqServiceConnHouse = MyApplication.SequenceNo++;
		byte[] connData = HandleNetSendMsg
				.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),MyApplication.seqServiceConnHouse);
		// 连接家政服务器
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("连接超时--重新连接家政服务器请求数据--sequence="+MyApplication.seqServiceConnHouse +"/"+ Arrays.toString(connData)+ "-------");
	}
	
	
	
	
	/**
	 * 启动线程解析域名
	 */
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (UniversalUtils.isStringEmpty(MyApplication.netParam.getAuthDns())) {
				PushData.setAuthIp(MyApplication.netParam.getAuthIp());
				PushData.setAuthPort(MyApplication.netParam.getAuthPort());
			} else {
				String strDns = MyApplication.netParam.getAuthDns();
				
				if(strDns.indexOf(":") > 0){
					String ip = DNSParsing.getIP(strDns.substring(0, strDns.indexOf(":")));
					int port = Integer.parseInt(strDns.substring(
							strDns.indexOf(":") + 1, strDns.length()));
					MyApplication.netParam.setAuthDnsParsIp(ip);
					MyApplication.netParam.setAuthDnsParsPort(port);
					PushData.setAuthIp(ip);
					PushData.setAuthPort(port);
				}else{
					PushData.setAuthIp(MyApplication.netParam.getAuthIp());
					PushData.setAuthPort(MyApplication.netParam.getAuthPort());
				}
				
			}
//			LogUtils.i("创建 Auth socket");
//			if(MyApplication.authSocketConn != null){
//				MyApplication.authSocketConn.closeAuthSocket();
//			}
//			MyApplication.authSocketConn = new AuthSocketConn(PushData.getAuthIp(), PushData.getAuthPort());
//			loadConnectAuthDataBroad();
		}
	};
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 在服务关闭的时候记得注销广播
		stopSelf();
		unregisterReceiver(mReceiver);

	}

}
