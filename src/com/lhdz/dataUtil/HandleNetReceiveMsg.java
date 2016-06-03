package com.lhdz.dataUtil;

import com.google.protobuf.InvalidProtocolBufferException;
import com.lhdz.dataUtil.protobuf.AuthMsgPro;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ForgotPasswordResp_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_GetServerInfoResp_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_LoginResp_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_QueryBaseInfoResp_PRO_MSG;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_QuickRegisterResp_PRO;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsAppHomeInfo_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsBroadCastOrderSet_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCmpRaceOrder_Notify_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCmpRefuseAtticheResult_Notify_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCommon_Notify_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCommon_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCompanyComplain_Notify_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsGetDBVerInfo_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsGetUserJoinCompanyInfo_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsOrderRaceDetailGet_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsStarCompanyGet_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserGetServiceAddrs_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderDail_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderList_Resp_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSeeCmpServiceInfoGet_Resp_Pro;
import com.lhdz.dataUtil.protobuf.PublicmsgPro;
import com.lhdz.dataUtil.protobuf.PublicmsgPro.NetConnectResp_PRO_MSG;
import com.lhdz.dataUtil.protobuf.PublicmsgPro.Net_CommonResp_PRO_MSG;
import com.lhdz.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.lhdz.publicMsg.MsgReceiveDef.AuthNetCommonResp;
import com.lhdz.publicMsg.MsgReceiveDef.ForgotPasswordResp;
import com.lhdz.publicMsg.MsgReceiveDef.GetServerInfoResp;
import com.lhdz.publicMsg.MsgReceiveDef.HsAppHomeInfo_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsBroadCastOrderSet_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsCmpRaceOrder_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsCmpRefuseAtticheResult_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsCommon_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsCompanyComplain_Notify;
import com.lhdz.publicMsg.MsgReceiveDef.HsGetDBVerInfo_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsGetUserJoinCompanyList_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsOrderRaceDetailGet_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsStarCompanyGet_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserGetServiceAddrs_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserOrderDetail_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserOrderList_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserSeeCmpServiceInfoGet_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.NetConnectResp;
import com.lhdz.publicMsg.MsgReceiveDef.QueryBaseInfoResp;
import com.lhdz.publicMsg.MsgReceiveDef.QuickRegisterResp;
import com.lhdz.publicMsg.NetHouseMsgType;

/**
 * 数据处理
 * 将从网络获取的数据转为内部使用的数据
 */
public class HandleNetReceiveMsg {

//	/**
//	 * 根据消息头来分发处理消息
//	 * @param proBufArray
//	 */
//	public static Object getParseMsgType(byte[] proBufArray) {
//		
//		byte[] proBufHead = UniversalUtils.getNetMsgHead(proBufArray);
//		byte[] proBufBody = UniversalUtils.getNetMsgBody(proBufArray);
//		
//		HandleNetHeadMsg headMsg = HandleNetHeadMsg.parseHeadMag(proBufHead);
//		Object obj = null;
//		switch (headMsg.uiMsgType) {
//		case NetHouseMsgType.NETCMD_CONNECT_RESP:// 连接请求的响应
//			obj = HandleProToConnectResp(proBufBody);
//			break;
//		case NetHouseMsgType.NETAUTH_USERLOGIN_RESP:// 登录请求的响应
//			obj = HandleProToLoginResp(proBufBody);
//			break;
//		case NetHouseMsgType.NETAUTH_PASSWORDMOD_RESP://修改密码的响应（使用认证通用响应）
//			obj = HandleProToAuthCommonResp(proBufBody);
//			break;
//		case NetHouseMsgType.NETAUTH_QUICKREGISTE_RESP://快速注册的响应
//			obj = HandleProToQuickRegisterResp(proBufBody);
//			break;
//		case NetHouseMsgType.NETAUTH_ACCOUNT_ACTIVE_RESP:
//			obj = HandleProToAuthCommonResp(proBufBody);//激活账号的响应（使用认证通用响应）
//			break;
//		case NetHouseMsgType.NETAUTH_FORGET_PASSWORD_RESP:
//			obj = HandleProToForgotPasswordResp(proBufBody);//忘记密码的响应
//			break;
//		case NetHouseMsgType.NETAUTH_QUERYSERVERLIST_RESP:
//			obj = HandleProToGetServerInfoResp(proBufBody);//获取服务器列表响应/查询服务器信息响应
//			break;
//		case NetHouseMsgType.NETAUTH_RESET_PASSWD_RESP:
//			obj = HandleProToAuthCommonResp(proBufBody);//重设密码的响应（使用认证通用响应）
//			break;
//		case NetHouseMsgType.NETAUTH_QUERYBASEINFO_RESP:
//			obj = HandleProToQueryBaseInfoResp(proBufBody);//查找个人信息的响应
//			break;
//		case NetHouseMsgType.NETAUTH_SELFINFOMOD_RESP:
//			obj = HandleProToAuthCommonResp(proBufBody);//更新个人基本信息的响应(使用认证通用响应)
//			break;
//			//=============以下为家政消息=============
//		case NetHouseMsgType.NETAPP_HOMEDATA_GET_RESP:
//			obj = HandleProToHsAppHomeInfo_Resp(proBufBody);//提取首页家政结构体的响应
//			break;
//		case NetHouseMsgType.NETAPP_BROADCASTORDER_RESP:
//			obj = HandleProToHsBroadCastOrderSet_Resp(proBufBody);//用户发放订单请求的响应
//			break;
//		case NetHouseMsgType.NETAPP_STARCOMPANY_GET_RESP:
//			obj = HandleProToHsStarCompanyGet_Resp(proBufBody);//提取明星公司列表的响应
//			break;
//		case NetHouseMsgType.NETAPP_ORDERLIST_GET_RESP:
//			obj = HandleProToHsUserOrderList_Resp(proBufBody);//提取用户订单  列表  的响应
//			break;
//		case NetHouseMsgType.NETAPP_ORDERINFO_GET_RESP:
//			obj = HandleProToHsUserOrderDetail_Resp(proBufBody);//提取用户订单  详情  的响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_USEREVALUATE_SET_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//订单评价的响应（评价）--使用家政  通用响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_ASKDAPPEAL_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户提请申述的响应（投诉）--使用家政 通用响应
//			break;
//		case NetHouseMsgType.NETAPP_USERADDCOMPANY_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户加入指定公司的响应（加入明星公司）--使用家政 通用响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_QUITCOMPANY_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户退出指定的家政公司的响应（退出明星公司）--使用家政 通用响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_GETADDCOMPANY_RESP:
//			obj = HandleProToHsUserJoinCompanyList_Resp(proBufBody);//用户提取已加入的家政公司列表的响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_SUREPAY_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户确认付款的响应--使用 家政 通用响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_ADDSERVICEADDR_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户添加服务联系地址--使用 家政 通用响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_SERVICEADDRS_GET_RESP:
//			obj = HandleProToHsUserGetServiceAddrs_Resp(proBufBody);//用户提取服务地址列表 响应
//			break;
//		case NetHouseMsgType.NETAPP_USER_SERVICEADDR_DEL_RESP:
//			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户删除服务地址 响应
//			break;
//		case NetHouseMsgType.NETAPP_ORDERRACEINFO_GET_RESP:
//			obj = HandleProToHsOrderRaceDetailGet_Resp(proBufBody);//提取抢单公司列表 响应
//			break;
//			//==================通知类消息=====================
//		case NetHouseMsgType.NETHOMEN_SENDREVIEW_NOTIFY:
//			obj = HandleProToHsCommon_Notify(proBufBody);//家政公司的评价发送通知到app			
//			break;	
//		case NetHouseMsgType.NETAPP_USER_ASKAPPEAL_NOTIFY:
//			obj = HandleProToHsCommon_Notify(proBufBody);//用户提请申诉通知			
//			break;	
//			
//		}	
//
//		return obj;
//	}
	
	
	/**
	 * 
	 * 根据消息头来分发处理消息--登录认证服务器
	 * @param proBufArray
	 * @return
	 */
	public static Object getParseAuthMsgType(int iMsgType, byte[] proBufBody) {
		
		Object obj = null;

		switch (iMsgType) {

		case NetHouseMsgType.NETCMD_CONNECT_RESP:// 连接请求的响应
			obj = HandleProToConnectResp(proBufBody);
			break;
		case NetHouseMsgType.NETAUTH_USERLOGIN_RESP:// 登录请求的响应
			obj = HandleProToLoginResp(proBufBody);
			break;
		case NetHouseMsgType.NETAUTH_PASSWORDMOD_RESP:// 修改密码的响应（使用认证通用响应）
			obj = HandleProToAuthCommonResp(proBufBody);
			break;
		case NetHouseMsgType.NETAUTH_QUICKREGISTE_RESP:// 快速注册的响应
			obj = HandleProToQuickRegisterResp(proBufBody);
			break;
		case NetHouseMsgType.NETAUTH_ACCOUNT_ACTIVE_RESP:
			obj = HandleProToAuthCommonResp(proBufBody);// 激活账号的响应（使用认证通用响应）
			break;
		case NetHouseMsgType.NETAUTH_FORGET_PASSWORD_RESP:
			obj = HandleProToForgotPasswordResp(proBufBody);// 忘记密码的响应
			break;
		case NetHouseMsgType.NETAUTH_QUERYSERVERLIST_RESP:
			obj = HandleProToGetServerInfoResp(proBufBody);// 获取服务器列表响应/查询服务器信息响应
			break;
		case NetHouseMsgType.NETAUTH_RESET_PASSWD_RESP:
			obj = HandleProToAuthCommonResp(proBufBody);// 重设密码的响应（使用认证通用响应）
			break;
		case NetHouseMsgType.NETAUTH_QUERYBASEINFO_RESP:
			obj = HandleProToQueryBaseInfoResp(proBufBody);// 查找个人信息的响应
			break;
//		case NetHouseMsgType.NETAUTH_SELFINFOMOD_RESP:
//			obj = HandleProToAuthCommonResp(proBufBody);// 更新个人基本信息的响应(使用认证通用响应)
//			break;
		case NetHouseMsgType.NETAUTH_USERSELINFOMOD_RESP:
			obj = HandleProToAuthCommonResp(proBufBody);// 用户个人信息单独字段修改的响应(使用认证通用响应)
			break;

		default:
			break;
		}
		
		return obj;
	}
	
	
	/**
	 * 
	 * 根据消息头来分发处理消息--家政服务器
	 * @param proBufArray
	 * @return
	 */
	public static Object getParseHouseMsgType(int iMsgType, byte[] proBufBody) {
		
		Object obj = null;

		switch (iMsgType) {
		
		case NetHouseMsgType.NETCMD_CONNECT_RESP:// 连接请求的响应
			obj = HandleProToConnectResp(proBufBody);
			break;
		case NetHouseMsgType.NETAPP_HOMEDATA_GET_RESP:
			obj = HandleProToHsAppHomeInfo_Resp(proBufBody);//提取首页家政结构体的响应
			break;
		case NetHouseMsgType.NETAPP_BROADCASTORDER_RESP:
			obj = HandleProToHsBroadCastOrderSet_Resp(proBufBody);//用户发放订单请求的响应
			break;
		case NetHouseMsgType.NETAPP_USER_SUBSCRIBEORDER_RESP:
			obj = HandleProToHsBroadCastOrderSet_Resp(proBufBody);//用户预约下单请求的响应
			break;
		case NetHouseMsgType.NETAPP_STARCOMPANY_GET_RESP:
			obj = HandleProToHsStarCompanyGet_Resp(proBufBody);//提取明星公司列表的响应
			break;
		case NetHouseMsgType.NETAPP_ORDERLIST_GET_RESP:
			obj = HandleProToHsUserOrderList_Resp(proBufBody);//提取用户订单  列表  的响应
			break;
		case NetHouseMsgType.NETAPP_ORDERINFO_GET_RESP:
			obj = HandleProToHsUserOrderDetail_Resp(proBufBody);//提取用户订单  详情  的响应
			break;
		case NetHouseMsgType.NETAPP_USER_USEREVALUATE_SET_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//订单评价的响应（评价）--使用家政  通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_ASKDAPPEAL_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户提请申述的响应（投诉）--使用家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USERADDCOMPANY_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户加入指定公司的响应（加入明星公司）--使用家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_QUITCOMPANY_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户退出指定的家政公司的响应（退出明星公司）--使用家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_GETADDCOMPANY_RESP:
			obj = HandleProToHsUserJoinCompanyList_Resp(proBufBody);//用户提取已加入的家政公司列表的响应
			break;
		case NetHouseMsgType.NETAPP_USER_SUREPAY_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户确认付款的响应--使用 家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_ADDSERVICEADDR_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户添加服务联系地址--使用 家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_SERVICEADDRS_GET_RESP:
			obj = HandleProToHsUserGetServiceAddrs_Resp(proBufBody);//用户提取服务地址列表 响应
			break;
		case NetHouseMsgType.NETAPP_USER_SERVICEADDR_DEL_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户删除服务地址 响应 -- 使用 家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_ORDERRACEINFO_GET_RESP:
			obj = HandleProToHsOrderRaceDetailGet_Resp(proBufBody);//提取抢单公司列表 响应
			break;
		case NetHouseMsgType.NETAPP_USER_BACKOUTORDER_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户撤单 响应 -- 使用 家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_AFFIRMFINISH_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户对已完成的服务进行确认操作 响应(确定完成) -- 使用 家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_SELECTORDERHOMEN_RESP:
			obj = HandleProToHsNetCommon_Resp(proBufBody);//用户选择指定公司  响应 --使用家政 通用响应
			break;
		case NetHouseMsgType.NETAPP_USER_DBVERINFO_GET_RESP:
			obj = HandleProToHsGetDBVerInfo_Resp(proBufBody);//获取数据库最新数据版本 响应
			break;
		case NetHouseMsgType.NETAPP_USER_CMPSRVINFOS_GET_RESP:
			obj = HandleProToHsUserSeeCmpServiceInfoGet_Resp(proBufBody);//用户查看到的商户服务信息 响应
			break;
		case NetHouseMsgType.NETAPP_USER_HOTCOMPANYGET_RESP:
			obj = HandleProToHsStarCompanyGet_Resp(proBufBody);//提取热门推荐信息请求消息
			break;
			
			
		//------------------------以下为通知类消息-------------------------
			
		case NetHouseMsgType.NETAPP_ORDERRACEHOMEN_NOTIFY:
			obj = HandleProToHsCmpRaceOrder_Notify(proBufBody);//家政公司抢单成功的  通知
			break;
		case NetHouseMsgType.NETAPP_USER_ASKAPPEAL_NOTIFY:
			obj = HandleProToHsCompanyComplain_Notify(proBufBody);//家政公司提请申诉通知  
			break;
		case NetHouseMsgType.NETAPP_HOMENEXAMINE_NOTIFY:
			obj = HandleProToHsCmpRefuseAtticheResult_Notify(proBufBody);//家政公司审批结果通知
			break;
		case NetHouseMsgType.NETAPP_HAVELOGIN_NOTIFY:
			obj = HandleProToHsCommon_Notify(proBufBody);//账号在其他客户端登录通知--通用通知
			break;
		case NetHouseMsgType.NETAPP_USER_ORDERPAY_REQ_NOTIFY:
			obj = HandleProToHsCommon_Notify(proBufBody);//家政公司申请用户付款通知--通用通知
			break;

		default:
			break;
		}
		
		return obj;
	}
	
	
	
	
	/**
	 * 建立连接请求的响应 -- 连接登录认证服务器的响应
	 * @param proBuf
	 */
	private static NetConnectResp HandleProToConnectResp(byte[] proBufBody){
		
		//建立连接响应的数据体
		NetConnectResp connectReceive = new NetConnectResp();
		
			try {
				
				PublicmsgPro.NetConnectResp_PRO_MSG connectResp_PRO_MSG = NetConnectResp_PRO_MSG.parseFrom(proBufBody);
				
				connectReceive.eResult = connectResp_PRO_MSG.getEResult();//操作结果（成功/失败）
				connectReceive.iUserid = connectResp_PRO_MSG.getIUserid();//用户账号id
				connectReceive.iSrvTime = connectResp_PRO_MSG.getISrvTime();//服务器的时间

			} catch (InvalidProtocolBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		return connectReceive;
	}
	
	
	/**
	 * 登录请求的响应
	 * @param proBufBody
	 * @return
	 */
	private static AuthLoginResp HandleProToLoginResp(byte[] proBufBody){
		
		AuthLoginResp loginResp = new AuthLoginResp();
		
		try {
			AuthMsgPro.AUTH_LoginResp_PRO loginResp_PRO = AUTH_LoginResp_PRO.parseFrom(proBufBody);
			loginResp.eResult = loginResp_PRO.getEResult();
			loginResp.iuserid = loginResp_PRO.getIuserid();//userid
			loginResp.szEmail = loginResp_PRO.getSzEmail();//邮箱
			loginResp.szPhoneNum = loginResp_PRO.getSzPhoneNum();//手机
			loginResp.szUserNick = loginResp_PRO.getSzUserNick();//昵称
			loginResp.szSignaTure = loginResp_PRO.getSzSignaTure();//个性签名
			loginResp.szUserID = loginResp_PRO.getSzUserID();//用户账号id
			loginResp.iHeadPic = loginResp_PRO.getIHeadPic();//头像id
			loginResp.szHeadPic = loginResp_PRO.getSzHeadPic();//头像信息
			loginResp.iGroupVer = loginResp_PRO.getIGroupVer();//分组版本
			loginResp.iFriendVer = loginResp_PRO.getIFriendVer();//好友列表版本
			loginResp.iVipLevel = loginResp_PRO.getIVipLevel();//VIP等级
			loginResp.eSex = loginResp_PRO.getESex();//性别
			loginResp.iAreaID = loginResp_PRO.getIAreaID();//区域id

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return loginResp;
	}
	
	
	/**
	 * 登录认证--通用响应--只需要获得操作结果的，一律使用该消息
	 */
	private static AuthNetCommonResp HandleProToAuthCommonResp(byte[] proBufBody) {

		AuthNetCommonResp commonResp = new AuthNetCommonResp();

		try {
			PublicmsgPro.Net_CommonResp_PRO_MSG commonResp_PRO_MSG = Net_CommonResp_PRO_MSG
					.parseFrom(proBufBody);
			commonResp.eResult = commonResp_PRO_MSG.getEResult();
			commonResp.iUserid = commonResp_PRO_MSG.getIUserid();//用户id

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return commonResp;
	}
	
	
	/**
	 * 快速注册的响应
	 */
	private static QuickRegisterResp HandleProToQuickRegisterResp(byte[] proBufBody){
		
		QuickRegisterResp quickRegisterResp = new QuickRegisterResp();
		
		try {
			AuthMsgPro.AUTH_QuickRegisterResp_PRO quickRegisterResp_PRO = AUTH_QuickRegisterResp_PRO.parseFrom(proBufBody);
			quickRegisterResp.eResult = quickRegisterResp_PRO.getEResult();
			quickRegisterResp.eAccountType = quickRegisterResp_PRO.getEAccountType();//账号类型
			quickRegisterResp.szAccountID = quickRegisterResp_PRO.getSzAccountID();//账号
			quickRegisterResp.szActivKey = quickRegisterResp_PRO.getSzActivKey();//账号激活码
			quickRegisterResp.uUserID = quickRegisterResp_PRO.getUUserID();//账号唯一标识
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return quickRegisterResp;
	}
	
	
	/**
	 * 忘记密码的响应
	 */
	private static ForgotPasswordResp HandleProToForgotPasswordResp(byte[] proBufBody) {

		ForgotPasswordResp forgotPasswordResp = new ForgotPasswordResp();

		try {
			AuthMsgPro.AUTH_ForgotPasswordResp_PRO forgotPasswordResp_PRO = AUTH_ForgotPasswordResp_PRO.parseFrom(proBufBody);
			forgotPasswordResp.eResult = forgotPasswordResp_PRO.getEResult();
			forgotPasswordResp.iuserid = forgotPasswordResp_PRO.getIuserid();//userid
			forgotPasswordResp.szUserID = forgotPasswordResp_PRO.getSzUserID();//账号id
			forgotPasswordResp.szMovbile = forgotPasswordResp_PRO.getSzMovbile();//手机
			forgotPasswordResp.szEmail = forgotPasswordResp_PRO.getSzEmail();//邮箱
			forgotPasswordResp.szUserNick = forgotPasswordResp_PRO.getSzUserNick();//用户昵称
			forgotPasswordResp.iHeadPic = forgotPasswordResp_PRO.getIHeadPic();//头像id
			forgotPasswordResp.szHeadPic = forgotPasswordResp_PRO.getSzHeadPic();//头像md5

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return forgotPasswordResp;
	}
	
	
	/**
	 * 获取服务器列表响应/查询服务器信息响应
	 */
	private static GetServerInfoResp HandleProToGetServerInfoResp(byte[] proBufBody) {
		
		GetServerInfoResp getServerInfoResp = new GetServerInfoResp();
		
		try {
			AuthMsgPro.AUTH_GetServerInfoResp_PRO getServerInfoResp_PRO = AUTH_GetServerInfoResp_PRO.parseFrom(proBufBody);
			getServerInfoResp.eResult = getServerInfoResp_PRO.getEResult();
			getServerInfoResp.iuserid = getServerInfoResp_PRO.getIuserid();//userid
			getServerInfoResp.listInfo = getServerInfoResp_PRO.getInfoList();//服务器信息列表
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return getServerInfoResp;
	}
	
	
	
	
	/**
	 * 查找个人信息的响应
	 */
	private static QueryBaseInfoResp HandleProToQueryBaseInfoResp(byte[] proBufBody) {
		
		QueryBaseInfoResp queryBaseInfoResp = new QueryBaseInfoResp();
		
		try {
			AuthMsgPro.AUTH_QueryBaseInfoResp_PRO_MSG queryBaseInfoResp_PRO_MSG = AUTH_QueryBaseInfoResp_PRO_MSG.parseFrom(proBufBody);
			queryBaseInfoResp.eResult = queryBaseInfoResp_PRO_MSG.getEResult();
			queryBaseInfoResp.iuserid = queryBaseInfoResp_PRO_MSG.getIuserid();//源userid
			queryBaseInfoResp.info = queryBaseInfoResp_PRO_MSG.getInfo();//查找到的信息  
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return queryBaseInfoResp;
	}
	
	
	// ============================以下为家政消息================================
	
	
	/**
	 * 家政服务--通用响应--只需要获得操作结果的，一律使用该消息
	 */
	private static HsNetCommon_Resp HandleProToHsNetCommon_Resp(byte[] proBufBody) {

		HsNetCommon_Resp commonResp = new HsNetCommon_Resp();

		try {
			NetHouseMsgPro.HsCommon_Resp_Pro commonResp_PRO_MSG = HsCommon_Resp_Pro.parseFrom(proBufBody);
			commonResp.eOperResult = commonResp_PRO_MSG.getEOperResult();
			commonResp.iUserID = commonResp_PRO_MSG.getIUserID();//用户id
			commonResp.iSelectID = commonResp_PRO_MSG.getISelectID();

		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return commonResp;
	}
	
	
	
	/**
	 * 提取首页家政结构体的响应
	 */
	private static HsAppHomeInfo_Resp HandleProToHsAppHomeInfo_Resp(byte[] proBufBody) {
		
		HsAppHomeInfo_Resp hsAppHomeInfo_Resp = new HsAppHomeInfo_Resp();
		
		try {
			NetHouseMsgPro.HsAppHomeInfo_Resp_Pro homeInfo_Resp_Pro = HsAppHomeInfo_Resp_Pro.parseFrom(proBufBody);
			hsAppHomeInfo_Resp.eOperResult = homeInfo_Resp_Pro.getEOperResult();
			hsAppHomeInfo_Resp.homenTypeList = homeInfo_Resp_Pro.getValueList();//家政类型列表
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return hsAppHomeInfo_Resp;
	}
	
	
	
	/**
	 * 广播下单  预约下单  响应
	 */
	private static HsBroadCastOrderSet_Resp HandleProToHsBroadCastOrderSet_Resp(byte[] proBufBody) {
		
		HsBroadCastOrderSet_Resp broadCastOrderSet_Resp = new HsBroadCastOrderSet_Resp();
		
		try {
			NetHouseMsgPro.HsBroadCastOrderSet_Resp_Pro orderSet_Resp_Pro = HsBroadCastOrderSet_Resp_Pro.parseFrom(proBufBody);
			broadCastOrderSet_Resp.eOperResult = orderSet_Resp_Pro.getEOperResult();
			broadCastOrderSet_Resp.iOrderID = orderSet_Resp_Pro.getIOrderID();//订单id
			broadCastOrderSet_Resp.szOrderCode = orderSet_Resp_Pro.getSzOrderCode();//订单编号
			broadCastOrderSet_Resp.szCreateTime = orderSet_Resp_Pro.getSzCreateTime();//订单创建时间
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return broadCastOrderSet_Resp;
	}
	
	
	/**
	 * 提取明星公司列表响应
	 */
	private static HsStarCompanyGet_Resp HandleProToHsStarCompanyGet_Resp(byte[] proBufBody) {
		
		HsStarCompanyGet_Resp companyGet_Resp = new HsStarCompanyGet_Resp();
		
		try {
			NetHouseMsgPro.HsStarCompanyGet_Resp_Pro starCompanyGet_Resp_Pro = HsStarCompanyGet_Resp_Pro.parseFrom(proBufBody);
			companyGet_Resp.eOperResult = starCompanyGet_Resp_Pro.getEOperResult();
			companyGet_Resp.iTotalCount = starCompanyGet_Resp_Pro.getITotalCount();//公司总个数;
			companyGet_Resp.iSendCount = starCompanyGet_Resp_Pro.getISendCount();	//已经发送的公司个数;
			companyGet_Resp.companyList = starCompanyGet_Resp_Pro.getValueList();//明星公司列表
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return companyGet_Resp;
	}
	
	
	
	/**
	 * 提取  用户订单  列表  响应
	 */
	private static HsUserOrderList_Resp HandleProToHsUserOrderList_Resp(byte[] proBufBody) {
		
		HsUserOrderList_Resp orderList_Resp = new HsUserOrderList_Resp();
		
		try {
			NetHouseMsgPro.HsUserOrderList_Resp_Pro list_Resp_Pro = HsUserOrderList_Resp_Pro.parseFrom(proBufBody);
			orderList_Resp.eOperResult = list_Resp_Pro.getEOperResult();//操作结果信息
			orderList_Resp.iTotalCount = list_Resp_Pro.getITotalCount();//总个数
			orderList_Resp.iSendCount = list_Resp_Pro.getISendCount();//已经发送的个数
			orderList_Resp.orderList = list_Resp_Pro.getValueList();//用户订单列表
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return orderList_Resp;
	}
	
	
	/**
	 * 提取  用户订单  详情  响应
	 */
	private static HsUserOrderDetail_Resp HandleProToHsUserOrderDetail_Resp(byte[] proBufBody) {
		
		HsUserOrderDetail_Resp detail_Resp = new HsUserOrderDetail_Resp();
		
		try {
			NetHouseMsgPro.HsUserOrderDail_Resp_Pro dail_Resp_Pro = HsUserOrderDail_Resp_Pro.parseFrom(proBufBody);
			detail_Resp.eOperResult = dail_Resp_Pro.getEOperResult();//操作结果信息
			detail_Resp.orderDetail = dail_Resp_Pro.getValue();//用户订单  详情
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return detail_Resp;
	}
	
	
	/**
	 * 用户提取已加入的家政公司列表响应
	 */
	private static HsGetUserJoinCompanyList_Resp HandleProToHsUserJoinCompanyList_Resp(byte[] proBufBody) {
		
		HsGetUserJoinCompanyList_Resp companyList_Resp = new HsGetUserJoinCompanyList_Resp();
		
		try {
			NetHouseMsgPro.HsGetUserJoinCompanyInfo_Resp_Pro list_Resp_Pro = HsGetUserJoinCompanyInfo_Resp_Pro.parseFrom(proBufBody);
			companyList_Resp.eOperResult = list_Resp_Pro.getEOperResult();//操作结果信息
			companyList_Resp.companyList = list_Resp_Pro.getValueList();//用户加入的明星公司列表
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return companyList_Resp;
	}
	
	
	
	
	/**
	 * 用户提取服务地址列表 响应
	 */
	private static HsUserGetServiceAddrs_Resp HandleProToHsUserGetServiceAddrs_Resp(byte[] proBufBody) {
		
		HsUserGetServiceAddrs_Resp addrs_Resp = new HsUserGetServiceAddrs_Resp();
		
		try {
			NetHouseMsgPro.HsUserGetServiceAddrs_Resp_Pro addrs_Resp_Pro = HsUserGetServiceAddrs_Resp_Pro.parseFrom(proBufBody);
			addrs_Resp.eOperResult = addrs_Resp_Pro.getEOperResult();
			addrs_Resp.uUserID = addrs_Resp_Pro.getUUserID();//用户id
			addrs_Resp.addrList = addrs_Resp_Pro.getValueList();//联系地址信息
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return addrs_Resp;
	}
	
	
	/**
	 * 提取抢单公司列表 响应
	 */
	private static HsOrderRaceDetailGet_Resp HandleProToHsOrderRaceDetailGet_Resp(byte[] proBufBody) {
		
		HsOrderRaceDetailGet_Resp raceGet_Resp = new HsOrderRaceDetailGet_Resp();
		
		try {
			NetHouseMsgPro.HsOrderRaceDetailGet_Resp_Pro raceGet_Resp_Pro = HsOrderRaceDetailGet_Resp_Pro.parseFrom(proBufBody);
			raceGet_Resp.eOperResult = raceGet_Resp_Pro.getEOperResult();
			raceGet_Resp.raceCompanyList = raceGet_Resp_Pro.getValueList();//抢单公司列表
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return raceGet_Resp;
	}
	
	
	
	/**
	 * 获取数据库最新数据版本 响应
	 */
	private static HsGetDBVerInfo_Resp HandleProToHsGetDBVerInfo_Resp(byte[] proBufBody) {
		
		HsGetDBVerInfo_Resp dbVersionInfo_Resp = new HsGetDBVerInfo_Resp();
		
		try {
			NetHouseMsgPro.HsGetDBVerInfo_Resp_Pro dbVerInfo_Resp_Pro = HsGetDBVerInfo_Resp_Pro.parseFrom(proBufBody);
			dbVersionInfo_Resp.eOperResult = dbVerInfo_Resp_Pro.getEOperResult();
			dbVersionInfo_Resp.versionList = dbVerInfo_Resp_Pro.getVersionList();
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dbVersionInfo_Resp;
	}
	
	
	
	/**
	 * 用户查看到的商户服务信息   响应
	 */
	private static HsUserSeeCmpServiceInfoGet_Resp HandleProToHsUserSeeCmpServiceInfoGet_Resp(byte[] proBufBody) {
		
		HsUserSeeCmpServiceInfoGet_Resp serviceInfoGet_Resp = new HsUserSeeCmpServiceInfoGet_Resp();
		
		try {
			NetHouseMsgPro.HsUserSeeCmpServiceInfoGet_Resp_Pro infoGet_Resp_Pro = HsUserSeeCmpServiceInfoGet_Resp_Pro.parseFrom(proBufBody);
			serviceInfoGet_Resp.eOperResult = infoGet_Resp_Pro.getEOperResult();
			serviceInfoGet_Resp.cmpServiceList = infoGet_Resp_Pro.getValueList();
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return serviceInfoGet_Resp;
	}
	
	
	
	// ============================通知类消息================================
	
	
	
	
	/**
	 * 家政服务--通用通知
	 */
	private static HsCommon_Notify HandleProToHsCommon_Notify(byte[] proBufBody) {
		
		HsCommon_Notify common_Notify = new HsCommon_Notify();
		
		try {
			NetHouseMsgPro.HsCommon_Notify_Pro common_Notify_Pro = HsCommon_Notify_Pro.parseFrom(proBufBody);
			common_Notify.uUserID = common_Notify_Pro.getUUserID();
			common_Notify.uCompanyID = common_Notify_Pro.getUCompanyID();
			common_Notify.iOrderID = common_Notify_Pro.getIOrderID();
			
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return common_Notify;
	}
	
	
	
	
	
	
	/**
	 * 家政公司抢单成功的通知
	 */
	private static HsCmpRaceOrder_Notify HandleProToHsCmpRaceOrder_Notify(byte[] proBufBody) {
		
		HsCmpRaceOrder_Notify cmpRaceOrder_Notify = new HsCmpRaceOrder_Notify();
		
		try {
			NetHouseMsgPro.HsCmpRaceOrder_Notify_Pro cmpRaceOrder_Notify_Pro = HsCmpRaceOrder_Notify_Pro.parseFrom(proBufBody);
			cmpRaceOrder_Notify.uCompanyID = cmpRaceOrder_Notify_Pro.getUCompanyID();//公司id
			cmpRaceOrder_Notify.uOrderID = cmpRaceOrder_Notify_Pro.getUOrderID();//订单id
			cmpRaceOrder_Notify.szSrvPrice = cmpRaceOrder_Notify_Pro.getSzSrvPrice();//服务价格
			cmpRaceOrder_Notify.szRemark = cmpRaceOrder_Notify_Pro.getSzRemark();//抢单说明信息
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cmpRaceOrder_Notify;
	}
	
	
	
	/**
	 * 家政公司提请申诉通知
	 */
	private static HsCompanyComplain_Notify HandleProToHsCompanyComplain_Notify(byte[] proBufBody) {
		
		HsCompanyComplain_Notify companyComplain_Notify = new HsCompanyComplain_Notify();
		
		try {
			NetHouseMsgPro.HsCompanyComplain_Notify_Pro companyComplain_Notify_Pro = HsCompanyComplain_Notify_Pro.parseFrom(proBufBody);
			companyComplain_Notify.uOrderID = companyComplain_Notify_Pro.getUOrderID();//订单id
			companyComplain_Notify.uUserID = companyComplain_Notify_Pro.getUUserID();//提交申述的公司id
			companyComplain_Notify.szContent = companyComplain_Notify_Pro.getSzContent();//申述内容
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return companyComplain_Notify;
	}
	
	
	
	/**
	 * 家政公司审批结果通知
	 */
	private static HsCmpRefuseAtticheResult_Notify HandleProToHsCmpRefuseAtticheResult_Notify(byte[] proBufBody) {
		
		HsCmpRefuseAtticheResult_Notify cmpRefuseAtticheResult_Notify = new HsCmpRefuseAtticheResult_Notify();
		
		try {
			NetHouseMsgPro.HsCmpRefuseAtticheResult_Notify_Pro atticheResult_Notify_Pro = HsCmpRefuseAtticheResult_Notify_Pro.parseFrom(proBufBody);
			cmpRefuseAtticheResult_Notify.iCompanyID = atticheResult_Notify_Pro.getICompanyID();//公司唯一标识
			cmpRefuseAtticheResult_Notify.uUserID = atticheResult_Notify_Pro.getUUserID();//用户唯一标识
			cmpRefuseAtticheResult_Notify.uResult = atticheResult_Notify_Pro.getUResult();//审批结果，0为同意，1为拒绝
			cmpRefuseAtticheResult_Notify.szRejReason = atticheResult_Notify_Pro.getSzRejReason();//拒绝原因（拒绝时有效）
			
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cmpRefuseAtticheResult_Notify;
	}
	
	
	
	
	
	
}
