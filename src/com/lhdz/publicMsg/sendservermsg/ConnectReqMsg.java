//package com.lhdz.publicMsg.sendservermsg;
//
//import java.util.Arrays;
//
//import com.lhdz.dataUtil.HandleNetHeadMsg;
//import com.lhdz.dataUtil.HandleNetReceiveMsg;
//import com.lhdz.dataUtil.HandleNetSendMsg;
//import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
//import com.lhdz.publicMsg.MsgInncDef;
//import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
//import com.lhdz.publicMsg.NetHouseMsgType;
//import com.lhdz.socketutil.CallBackMsg;
//import com.lhdz.socketutil.SocketConn;
//import com.lhdz.util.LogUtils;
//import com.lhdz.util.UniversalUtils;
//
//
///**
// * 用于连接服务器的请求
// *
// */
//public class ConnectReqMsg {
//	
//	
//	
//	
//	/**
//	 * 加载 连接请求
//	 */
//	public static void loadConnectData() {
//		byte[] connData = HandleNetSendMsg.HandleConnectToPro(new MsgInncDef().new NetConnectReq(),1);
//		// 连接登录服务器
//		SocketConn.pushtoList(connData);
//		LogUtils.i("连接服务器请求数据"+ Arrays.toString(connData) + "----------");
//		SocketConn.callbackRecData(new CallBackMsg() {
//
//			@Override
//			public void RecvMsgSuccess(byte[] msg) {
//
//				byte[] proBufHead = UniversalUtils.getNetMsgHead(msg);
//				HandleNetHeadMsg headMsg = HandleNetHeadMsg
//						.parseHeadMag(proBufHead);
//				if (headMsg.uiMsgType != NetHouseMsgType.NETCMD_CONNECT_RESP)
//					return;
//
//				LogUtils.i("连接服务器成功" + Arrays.toString(msg)
//						+ "=============");
//				NetConnectResp netConn = (NetConnectResp) HandleNetReceiveMsg
//						.getParseMsgType(msg);
//				if (netConn.eResult == eOPERRESULT_PRO.E_OPER_SUCCESS_PRO) {
//					int time = netConn.iSrvTime;
//					int id = netConn.iUserid;
//
//					LogUtils.i("connData" + "time = " + time + "/ id = " + id);
//
//
//				} else {
//					String result = UniversalUtils
//							.judgeNetResult_Auth(netConn.eResult);
//					LogUtils.i("网络连接请求失败" + result + "=============");
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
//	
//	
//	
//	
//	
//	
//}
