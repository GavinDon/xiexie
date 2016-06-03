package com.lhdz.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetHeadMsg;
import com.lhdz.dataUtil.HandleNetReceiveMsg;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;

public class HandleNetDataService extends Service {

	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		String actionService = intent.getAction();
		long recvTime = intent.getLongExtra(Define.BROAD_RECVTIME, -1);
		
		//查询接收到的数据，当拿到数据之后会将该条数据从接收数据map中删除
		byte[] pendingDealData = HandleMsgDistribute.getInstance().queryRecvMsg(recvTime);
		if(pendingDealData == null || pendingDealData.length == 0){
			return START_REDELIVER_INTENT;
		}
		
		byte[] proBufHead = UniversalUtils.getNetMsgHead(pendingDealData);//消息头数据
		byte[] proBufBody = UniversalUtils.getNetMsgBody(pendingDealData);//消息体数据
		HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);//消息头解析
		
		int iSequenceNo = headMsg.uiSequenceNo;
		int iMsgType = headMsg.uiMsgType;
		
		LogUtils.i("android服务 收到响应--sequenceNo ="+iSequenceNo+" , iMsgType = "+iMsgType);
		
		//数据来源于登录认证服务器
		if(Define.BROAD_CAST_RECV_DATA_AUTH.equals(actionService)){
			Object objData = HandleNetReceiveMsg.getParseAuthMsgType(iMsgType, proBufBody);
			HandleMsgDistribute.getInstance().insertCompleteMsg(recvTime, objData);
			
			sendCompleteBroad(iSequenceNo, iMsgType, Define.BROAD_CAST_RECV_DATA_AUTH,recvTime);//发送广播给activity
		}
		
		//数据来源于家政服务器
		if(Define.BROAD_CAST_RECV_DATA_HOUSE.equals(actionService)){
			Object objData = HandleNetReceiveMsg.getParseHouseMsgType(iMsgType, proBufBody);
			HandleMsgDistribute.getInstance().insertCompleteMsg(recvTime, objData);
			
			sendCompleteBroad(iSequenceNo, iMsgType,Define.BROAD_CAST_RECV_DATA_HOUSE,recvTime);//发送广播给activity
		}
		
		
		return START_REDELIVER_INTENT;
	}

	
	
	
	/**
	 * 处理通知消息
	 */
	private void checkNotifyData(int iSequence, int iMsgType){
		//通知类消息的处理
		if(iMsgType == NetHouseMsgType.NETAPP_ORDERRACEHOMEN_NOTIFY){
			//家政公司抢单成功通知
//			processOrderRaceNotify(iSequence);
		}
		if(iMsgType == NetHouseMsgType.NETAPP_USER_ASKAPPEAL_NOTIFY){
			//家政公司提请申述通知
//			processCompanyComplainNotify(iSequence);
		}
		if(iMsgType == NetHouseMsgType.NETAPP_HOMENEXAMINE_NOTIFY){
			//家政公司审批结果通知
//			processCmpRefuseAtticheResultNotify(iSequence);
		}
		if(iMsgType == NetHouseMsgType.NETAPP_HAVELOGIN_NOTIFY){
			//账号在其他客户端登录通知
//			1、消息为通用通知。
//			2、用户端和商户端一样均需要处理此消息。
//			3、当接到此消息后，用户端或者商户端断开链路，提醒用户或者跳转登录界面。
//			4、有自动登录的，此时不允许自动登录。
			
		}
	}
	
	
	
	
	/**
	 * 发送广播，通知activity
	 */
	public void sendCompleteBroad(int iSequenceNo, int iMSgType, String dataFrom,long recvTime) {
		Intent intent = new Intent();
		intent.setAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		intent.putExtra(Define.BROAD_SEQUENCE, iSequenceNo);
		intent.putExtra(Define.BROAD_MSG_RECVTIME, recvTime);
		intent.putExtra(Define.BROAD_MSG_TYPE, iMSgType);
		intent.putExtra(Define.BROAD_DATA_FROM, dataFrom);
		MyApplication.context.sendBroadcast(intent);
	}
	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopSelf();
		super.onDestroy();
	}
	
}
