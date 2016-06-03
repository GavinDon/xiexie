package com.lhdz.dataUtil;

import com.lhdz.dataUtil.protobuf.AuthMsgPro;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_AccountActiveReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ForgotPasswordReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_GetServerInfoReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_LoginReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_QueryBaseInfoReq_PRO_MSG;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_QuickRegisterReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ResetPasswordReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_UpdatePasswordReq_PRO;
import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_UserSelfMoveInfoMod_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsAppHomenInfo_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsBroadCastOrderInfo_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCommon_Notify_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCommon_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsHotCompanyInfo_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsSubScribeOrderInfo_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserAddCompany_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserAskAppeal_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderValuate_Notify_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderValuate_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserQuitComany_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserServiceAddrInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserServiceAddrInfo_Req_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSurePay_Req_Pro;
import com.lhdz.dataUtil.protobuf.PublicmsgPro;
import com.lhdz.dataUtil.protobuf.PublicmsgPro.NetConnectReq_PRO_Msg;
import com.lhdz.dataUtil.protobuf.PublicmsgPro.Net_CommonReq_PRO_MSG;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.util.UniversalUtils;

/**
 * 数据处理类
 * 将需要发送的数据转为protobuf
 */
public class HandleNetSendMsg {

//	static int SequenceNo = 0;
	
	
	/**
	 * 建立连接请求 -- 连接 登录认证服务器  的连接请求
	 */
	public static byte[] HandleConnectToPro(MsgInncDef.NetConnectReq netConn,int sequence){
		
		//组织序列化消息
		PublicmsgPro.NetConnectReq_PRO_Msg.Builder builder = PublicmsgPro.NetConnectReq_PRO_Msg.newBuilder();
		
		builder.setIMsgFrom(netConn.iMsgFrom);//消息来源
		builder.setIUserid(netConn.iUserId);//客户端登录的userid
		builder.setSzAuthInfo(netConn.SzAuthInfo);//认证信息
		builder.setSzCrcInfo(netConn.szCrcInfo);	//加密循环校验
		
		//将消息内容建立
		NetConnectReq_PRO_Msg connectReq_PRO_Msg = builder.build();
		
		//将消息序列化
		byte[] msgProBody = connectReq_PRO_Msg.toByteArray();
		//消息的长度
		int msgLength = connectReq_PRO_Msg.getSerializedSize();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETCMD_CONNECT_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		//发送消息
		return msgByteArray;
		
	}
	
	
	
	/**
	 * 登录请求消息
	 */
	public static byte[] HandleLoginToPro(MsgInncDef.LoginReq loginMsg, int sequence){
		
		//组织序列化消息
		AuthMsgPro.AUTH_LoginReq_PRO.Builder builder = AuthMsgPro.AUTH_LoginReq_PRO.newBuilder();
		
		builder.setEAccountType(EnumPro.eACCOUNTTYPE_PRO.en_ACCOUNT_PHONE_PRO);//账号类型
		builder.setSzAccountID(loginMsg.strAccountID);//登录的账号，更新账号类型，这里的值也不一样
		builder.setSzPassword(loginMsg.strPasswd);//md5后的密码
		builder.setEStatus(EnumPro.eONLINESTATUS_PRO.en_STATUS_ONLINE_PRO);//在线状态
		
		//建立消息内容
		AUTH_LoginReq_PRO loginReq_PRO = builder.build();
		//消息长度
		int msgLength = loginReq_PRO.getSerializedSize();
		//将消息序列化
		byte[] msgProBody = loginReq_PRO.toByteArray();
		
		// 构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_USERLOGIN_REQ, msgLength,msgLength, sequence, 0);

		// 拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);

		return msgByteArray;
	}
	
	
	
	/**
	 * 更新密码请求
	 */
	public static byte[] HandleUpdatePasswordToPro(MsgInncDef.UpdatePasswordReq updatePasswordReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_UpdatePasswordReq_PRO.Builder builder = AUTH_UpdatePasswordReq_PRO.newBuilder();
		
		builder.setIuserid(updatePasswordReq.iuserid);//更新者
		builder.setSzOldPwd(updatePasswordReq.szOldPwd);//旧密码
		builder.setSzNewPwd(updatePasswordReq.szNewPwd);//新密码
		
		//建立消息
		AUTH_UpdatePasswordReq_PRO passwordReq_PRO = builder.build();
		//消息长度
		int msgLength = passwordReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = passwordReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_PASSWORDMOD_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 快速注册请求
 	 */
	public static byte[] HandleQuickRegisterToPro(MsgInncDef.QuickRegisterReq quickRegisterReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_QuickRegisterReq_PRO.Builder builder = AUTH_QuickRegisterReq_PRO.newBuilder();
		
		builder.setEAccountType(quickRegisterReq.iAccountType);//账号类型
		builder.setIAgentID(quickRegisterReq.iAgentID);//代理id号
		builder.setSzAccountID(quickRegisterReq.sAccountID);//账号
		builder.setSzPwd(quickRegisterReq.szPwd);//密码
		builder.setSzUserNick(quickRegisterReq.szUserNick);//昵称
		builder.setSzRegIP(quickRegisterReq.szRegIP);//注册的IP
		
		//建立消息
		AUTH_QuickRegisterReq_PRO quickRegisterReq_PRO = builder.build();
		//消息长度
		int msgLength = quickRegisterReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = quickRegisterReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_QUICKREGISTE_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 账号激活请求
	 */
	public static byte[] HandleAccountActiveToPro(MsgInncDef.AccountActiveReq accountActiveReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_AccountActiveReq_PRO.Builder builder = AUTH_AccountActiveReq_PRO.newBuilder();
		
		builder.setIUserID(accountActiveReq.iUserID);//账号id
		builder.setSzActivKey(accountActiveReq.szActivKey);//激活码
		
		//建立消息
		AUTH_AccountActiveReq_PRO accountActiveReq_PRO = builder.build();
		//消息长度
		int msgLength = accountActiveReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = accountActiveReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_ACCOUNT_ACTIVE_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 忘记密码请求
	 */
	public static byte[] HandleForgetPassWordToPro(MsgInncDef.ForgetPassWordReq forgetPassWordReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_ForgotPasswordReq_PRO.Builder builder = AUTH_ForgotPasswordReq_PRO.newBuilder();
		
		builder.setEAccountType(forgetPassWordReq.szAccountType);//账号类型
		builder.setSzAccountID(forgetPassWordReq.szAccountID);//忘记密码的账号
		
		//建立消息
		AUTH_ForgotPasswordReq_PRO forgotPasswordReq_PRO = builder.build();
		//消息长度
		int msgLength = forgotPasswordReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = forgotPasswordReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_FORGET_PASSWORD_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 查询服务器信息请求
	 */
	public static byte[] HandleGetServerInfoReqToPro(MsgInncDef.GetServerInfoReq getServerInfoReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_GetServerInfoReq_PRO.Builder builder = AUTH_GetServerInfoReq_PRO.newBuilder();
		
		builder.setIuserid(getServerInfoReq.iuserid);//userid
//		builder.setInfo(1, 11);//要查找的服务器类型列表
		
		//建立消息
		AUTH_GetServerInfoReq_PRO getServerInfoReq_PRO = builder.build();
		//消息长度
		int msgLength = getServerInfoReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = getServerInfoReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_QUERYSERVERLIST_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 获取服务器列表请求--使用通用请求的数据
	 */
	public static byte[] HandleServerListReqToPro(MsgInncDef.AuthNetCommonReq authNetCommonReq,int sequence){
		//组织序列化消息
		PublicmsgPro.Net_CommonReq_PRO_MSG.Builder builder = Net_CommonReq_PRO_MSG.newBuilder();
		
		builder.setIUserid(authNetCommonReq.iUserid);//userid
		
		//建立消息
		Net_CommonReq_PRO_MSG serverListReq_PRO = builder.build();
		//消息长度
		int msgLength = serverListReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = serverListReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_QUERYSERVERLIST_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 重设密码的请求
	 */
	public static byte[] HandleResetPasswordReqToPro(MsgInncDef.ResetPasswordReq resetPasswordReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_ResetPasswordReq_PRO.Builder builder = AUTH_ResetPasswordReq_PRO.newBuilder();
		
		builder.setIuserid(resetPasswordReq.iuserid);//账号id
		builder.setSzNewPwd(resetPasswordReq.szNewPwd);//新密码
		
		
		//建立消息
		AUTH_ResetPasswordReq_PRO resetPasswordReq_PRO = builder.build();
		//消息长度
		int msgLength = resetPasswordReq_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = resetPasswordReq_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_RESET_PASSWD_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	
	/**
	 * 查找个人信息的请求
	 */
	public static byte[] HandleQueryBaseInfoReqToPro(MsgInncDef.QueryBaseInfoReq queryBaseInfoReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_QueryBaseInfoReq_PRO_MSG.Builder builder = AUTH_QueryBaseInfoReq_PRO_MSG.newBuilder();
		
		builder.setIuserid(queryBaseInfoReq.iuserid);//源userid
		builder.setIDstuserid(queryBaseInfoReq.iDstuserid);//目的userid
		
		
		//建立消息
		AUTH_QueryBaseInfoReq_PRO_MSG queryBaseInfoReq_PRO_MSG = builder.build();
		//消息长度
		int msgLength = queryBaseInfoReq_PRO_MSG.getSerializedSize();
		//序列化消息
		byte[] msgProBody = queryBaseInfoReq_PRO_MSG.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_QUERYBASEINFO_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 个人资料修改的请求
	 */
	public static byte[] HandleUserInfoModReqToPro(MsgInncDef.AUTHUserSelfMoveInfoModReq infoModReq,int sequence){
		//组织序列化消息
		AuthMsgPro.AUTH_UserSelfMoveInfoMod_PRO.Builder builder = AUTH_UserSelfMoveInfoMod_PRO.newBuilder();
		
		builder.setIuserid(infoModReq.iuserid);//更新者
		builder.setIModType(infoModReq.iModType);//修改类别：:1：昵称, 2:个性签名, 3：手机号码,4：头像,5: 性别, 6:地区）
		builder.setSzModInfo(infoModReq.szModInfo);//数据修改内容
		
		
		//建立消息
		AUTH_UserSelfMoveInfoMod_PRO moveInfoMod_PRO = builder.build();
		//消息长度
		int msgLength = moveInfoMod_PRO.getSerializedSize();
		//序列化消息
		byte[] msgProBody = moveInfoMod_PRO.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAUTH_USERSELINFOMOD_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	// ============================以下为家政消息================================
	
	
	
	/**
	 * 提取首页家政结构体的请求
	 */
	public static byte[] HandleHsAppHomenInfoReqToPro(MsgInncDef.HsAppHomenInfo_Req appHomenInfo_Req, int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsAppHomenInfo_Req_Pro.Builder builder = HsAppHomenInfo_Req_Pro.newBuilder();
		
		builder.setBGetHome(appHomenInfo_Req.bGetHome);// true:提取【首页】订单类型数据， false:提取【更多】订单类型数据 (使用1表示true,0表示false)
		builder.setIUserID(appHomenInfo_Req.iUserID);//userId
		
		
		//建立消息
		HsAppHomenInfo_Req_Pro hsAppHomenInfo_Req_Pro = builder.build();
		//消息长度
		int msgLength = hsAppHomenInfo_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = hsAppHomenInfo_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_HOMEDATA_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 广播下单 发单  的请求
	 */
	public static byte[] HandleHsBroadCastOrderReqToPro(MsgInncDef.HsBroadCastOrderInfo_Req orderInfo_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsBroadCastOrderInfo_Req_Pro.Builder builder = HsBroadCastOrderInfo_Req_Pro.newBuilder();
		
		builder.setUUserID(orderInfo_Req.uUserID); // 用户id
		builder.setUCompanyID(orderInfo_Req.uCompanyID);// 公司id--预约下单时有效
		builder.setIOrderTypeID(orderInfo_Req.iOrderTypeID);// 下单类型
		builder.setIUsrSrvedIndex(orderInfo_Req.iUsrSrvedIndex);//服务地址索引
		builder.setSzSrvBeginTime(orderInfo_Req.szSrvBeginTime);//服务开始时间
		builder.setSzUnitArea(orderInfo_Req.szUnitArea);//单位面积
		builder.setSzHeartPrice(orderInfo_Req.szHeartPrice);//心理价位
		builder.setSzReMark(orderInfo_Req.szReMark);//备注信息
		
		//建立消息
		HsBroadCastOrderInfo_Req_Pro broadCastOrderInfo_Pro = builder.build();
		//消息长度
		int msgLength = broadCastOrderInfo_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = broadCastOrderInfo_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_BROADCASTORDER_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 预约下单  的请求
	 */
	public static byte[] HandleHsSubScribeOrderInfoReqToPro(MsgInncDef.HsSubScribeOrderInfo_Req orderInfo_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsSubScribeOrderInfo_Req_Pro.Builder builder = HsSubScribeOrderInfo_Req_Pro.newBuilder();
		
		builder.setUUserID(orderInfo_Req.uUserID); // 用户id
		builder.setUCompanyID(orderInfo_Req.uCompanyID);// 公司id--预约下单时有效
		builder.setIOrderTypeID(orderInfo_Req.iOrderTypeID);// 下单类型
		builder.setIUsrSrvedIndex(orderInfo_Req.iUsrSrvedIndex);//服务地址索引
		builder.setSzSrvBeginTime(orderInfo_Req.szSrvBeginTime);//服务开始时间
		builder.setSzServiceItem(orderInfo_Req.szServiceItem);//服务清单
		builder.setSzSrvPrice(orderInfo_Req.szSrvPrice);//服务价格
		builder.setSzReMark(orderInfo_Req.szReMark);//备注信息
		
		//建立消息
		HsSubScribeOrderInfo_Req_Pro subScribeOrderInfo_Req_Pro = builder.build();
		//消息长度
		int msgLength = subScribeOrderInfo_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = subScribeOrderInfo_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_SUBSCRIBEORDER_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	
	
	/**
	 * 明星公司列表提取请求--使用通用请求的数据
	 */
	public static byte[] HandleHsStarCompanyReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);//为用户生成一个唯一的ID
		builder.setISelectID(common_Req.iSelectID);//选定的乙方唯一ID
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_STARCOMPANY_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户订单  列表  提取请求--使用通用请求的数据
	 */
	public static byte[] HandleHsUserOrderList_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);//为用户生成一个唯一的ID
		builder.setISelectID(common_Req.iSelectID);//选定的乙方唯一ID
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_ORDERLIST_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 用户订单  详情  提取请求--使用通用请求的数据
	 */
	public static byte[] HandlHsUserOrderDetail_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);//为用户生成一个唯一的ID
		builder.setISelectID(common_Req.iSelectID);//选定的乙方唯一ID
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_ORDERINFO_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	
	/**
	 * 评价订单 请求（评价）
	 */
	public static byte[] HandleHsOrderValuate_ReqToPro(MsgInncDef.HsUserOrderValuate_Req orderValuate_Req,int sequence){
		
		//组织评价信息
		NetHouseMsgPro.HsUserOrderValuate_Pro.Builder infoBuilder = HsUserOrderValuate_Pro.newBuilder();
		infoBuilder.setUUserID(orderValuate_Req.uUserID);//用户id
		infoBuilder.setIOrderID(orderValuate_Req.iOrderID);//订单id
		infoBuilder.setIGiveStar(orderValuate_Req.iGiveStar);//给定星级
		infoBuilder.setSzRemark(orderValuate_Req.szRemark);//评价内容
		infoBuilder.setSzPicUrl(orderValuate_Req.szPicUrl);//评价内容
		HsUserOrderValuate_Pro valuate_Pro = infoBuilder.build();
		
		//组织序列化消息
		NetHouseMsgPro.HsUserOrderValuate_Notify_Pro.Builder builder = HsUserOrderValuate_Notify_Pro.newBuilder();
		
		builder.setUCompanyID(orderValuate_Req.uCompanyID);//公司id
		builder.setOrderValuatInfo(valuate_Pro);//评价信息
		
		//建立消息
		HsUserOrderValuate_Notify_Pro orderValuate_Notify_Pro = builder.build();
		//消息长度
		int msgLength = orderValuate_Notify_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = orderValuate_Notify_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_USEREVALUATE_SET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	
	/**
	 * 用户提请申述 请求（投诉）
	 */
	public static byte[] HandleHsUserAskAppeal_ReqToPro(MsgInncDef.HsUserAskAppeal_Req appeal_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsUserAskAppeal_Req_Pro.Builder builder = HsUserAskAppeal_Req_Pro.newBuilder();
		
		builder.setUUserID(appeal_Req.uUserID);//用户id
		builder.setUCompanyID(appeal_Req.uCompanyID);//公司id
		builder.setIOrderID(appeal_Req.iOrderID);//订单id
		builder.setSzApperlClass(appeal_Req.szApperlClass);//投诉类别
		builder.setSzReason(appeal_Req.szReason);//投诉原因
		
		
		//建立消息
		HsUserAskAppeal_Req_Pro appeal_Req_Pro = builder.build();
		//消息长度
		int msgLength = appeal_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = appeal_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_ASKDAPPEAL_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户加入指定公司 请求（加入明星公司）
	 */
	public static byte[] HandleHsUserAddCompany_ReqToPro(MsgInncDef.HsUserAddCompany_Req addCompany_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsUserAddCompany_Req_Pro.Builder builder = HsUserAddCompany_Req_Pro.newBuilder();
		
		builder.setUUserID(addCompany_Req.uUserID);//用户id
		builder.setUCompanyID(addCompany_Req.uCompanyID);//家政公司id
		builder.setISrvType(addCompany_Req.iSrvType);//服务类型
		builder.setSzName(addCompany_Req.szName);//用户名
		builder.setSzLinkTel(addCompany_Req.szLinkTel);//联系电话
		builder.setSzCurAddr(addCompany_Req.szCurAddr);//现居住地
		builder.setSzRemark(addCompany_Req.szRemark);//个人介绍
		
		
		//建立消息
		HsUserAddCompany_Req_Pro addCompany_Req_Pro = builder.build();
		//消息长度
		int msgLength = addCompany_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = addCompany_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USERADDCOMPANY_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户退出指定的家政公司请求（退出明星公司）
	 */
	public static byte[] HandleHsUserQuitCompany_ReqToPro(MsgInncDef.HsUserQuitComany_Req quitComany_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsUserQuitComany_Req_Pro.Builder builder = HsUserQuitComany_Req_Pro.newBuilder();
		
		builder.setUUserID(quitComany_Req.uUserID);//用户id
		builder.setUCompanyID(quitComany_Req.uCompanyID);//公司id
		builder.setSzRemark(quitComany_Req.szRemark);//退出原因
		
		
		//建立消息
		HsUserQuitComany_Req_Pro quitCompany_pro = builder.build();
		//消息长度
		int msgLength = quitCompany_pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = quitCompany_pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_QUITCOMPANY_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户提取已加入的家政公司列表请求--使用  通用请求  数据
	 */
	public static byte[] HandleHsUserJoinCompanyList_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro companyList_pro = builder.build();
		//消息长度
		int msgLength = companyList_pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = companyList_pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_GETADDCOMPANY_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户确认付款请求
	 */
	public static byte[] HandleHsUserSurePay_ReqToPro(MsgInncDef.HsUserSurePay_Req surePay_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsUserSurePay_Req_Pro.Builder builder = HsUserSurePay_Req_Pro.newBuilder();
		
		builder.setUUserID(surePay_Req.uUserID);//用户id
		builder.setIOrderID(surePay_Req.iOrderID);//订单id
		builder.setSzPrice(surePay_Req.szPrice);//付款单价
		
		
		//建立消息
		HsUserSurePay_Req_Pro pay_Req_Pro = builder.build();
		//消息长度
		int msgLength = pay_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = pay_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_SUREPAY_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户添加服务联系地址 请求
	 */
	public static byte[] HandleHsUserAddServiceAddr_ReqToPro(MsgInncDef.HsUserServiceAddrInfo_Req addr_Req,int sequence){
		
		//服务地址的信息
		NetHouseMsgPro.HsUserServiceAddrInfo_Pro.Builder infoBuilder = HsUserServiceAddrInfo_Pro.newBuilder();
		infoBuilder.setUAreaID(addr_Req.uAreaID);//地址区域id
		infoBuilder.setUAddrIndex(addr_Req.uAddrIndex);//地址序号
		infoBuilder.setSzUserName(addr_Req.szUserName);//联系人名
		infoBuilder.setSzUserTel(addr_Req.szUserTel);//联系人电话
		infoBuilder.setSzUserAddr(addr_Req.szUserAddr);//联系人地址
		HsUserServiceAddrInfo_Pro addrInfo_Pro = infoBuilder.build();
		
		//组织序列化消息
		NetHouseMsgPro.HsUserServiceAddrInfo_Req_Pro.Builder builder = HsUserServiceAddrInfo_Req_Pro.newBuilder();
		
		builder.setUUserID(addr_Req.uUserID);//用户id
		builder.setAddrInfo(addrInfo_Pro);//用户地址信息
		
		//建立消息
		HsUserServiceAddrInfo_Req_Pro addr_Req_Pro = builder.build();
		//消息长度
		int msgLength = addr_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = addr_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_ADDSERVICEADDR_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户提取服务地址列表 请求 -- 使用 通用请求 数据
	 */
	public static byte[] HandleHsUserGetServiceAddrs_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_SERVICEADDRS_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户删除服务地址 请求 -- 使用 通用请求 数据
	 */
	public static byte[] HandleHsUserDeleteServiceAddrs_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_SERVICEADDR_DEL_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 提取抢单公司列表 请求 -- 使用 通用请求 数据
	 */
	public static byte[] HandleHsOrderRaceDetailGet_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_ORDERRACEINFO_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 用户选择指定公司 请求 -- 使用   通用  通知  请求 数据
	 */
	public static byte[] HandleHsChoiceCompany_ReqToPro(MsgInncDef.HsCommon_Notify common_Notify,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Notify_Pro.Builder builder = HsCommon_Notify_Pro.newBuilder();
		
		builder.setUUserID(common_Notify.uUserID);
		builder.setUCompanyID(common_Notify.uCompanyID);
		builder.setIOrderID(common_Notify.iOrderID);
		
		
		//建立消息
		HsCommon_Notify_Pro common_Notify_Pro = builder.build();
		//消息长度
		int msgLength = common_Notify_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Notify_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_SELECTORDERHOMEN_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 用户撤单请求 -- 使用   通用请求 数据
	 */
	public static byte[] HandleHsBackOutOrder_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_BACKOUTORDER_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	/**
	 * 用户对已完成的服务进行确认操作 请求（确定完成）-- 使用   通用请求 数据 
	 */
	public static byte[] HandleHsAffirmFinishOrder_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_AFFIRMFINISH_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 获取数据库最新数据版本请求-- 使用   通用请求 数据 
	 */
	public static byte[] HandleHsDBVersionInfo_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_DBVERINFO_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 用户查看的公司服务信息请求消息-- 使用   通用请求 数据 
	 */
	public static byte[] HandleHsUserSeeCmpServiceInfo_ReqToPro(MsgInncDef.HsNetCommon_Req common_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsCommon_Req_Pro.Builder builder = HsCommon_Req_Pro.newBuilder();
		
		builder.setISrcID(common_Req.iSrcID);
		builder.setISelectID(common_Req.iSelectID);
		
		
		//建立消息
		HsCommon_Req_Pro common_Req_Pro = builder.build();
		//消息长度
		int msgLength = common_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = common_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_CMPSRVINFOS_GET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
	/**
	 * 提取热门推荐信息请求消息
	 */
	public static byte[] HandleHsHotCompanyInfo_Req_ToPro(MsgInncDef.HsHotCompanyInfo_Req hotCompanyInfo_Req,int sequence){
		//组织序列化消息
		NetHouseMsgPro.HsHotCompanyInfo_Req_Pro.Builder builder = HsHotCompanyInfo_Req_Pro.newBuilder();
		
		builder.setUUserID(hotCompanyInfo_Req.uUserID);
		builder.setUAreaID(hotCompanyInfo_Req.uAreaID);
		builder.setUTypeID(hotCompanyInfo_Req.uTypeID);
		builder.setIPosID(hotCompanyInfo_Req.iPosID);
		
		//建立消息
		HsHotCompanyInfo_Req_Pro hotCompanyInfo_Req_Pro = builder.build();
		//消息长度
		int msgLength = hotCompanyInfo_Req_Pro.getSerializedSize();
		//序列化消息
		byte[] msgProBody = hotCompanyInfo_Req_Pro.toByteArray();
		
		//构建消息头
		HandleNetHeadMsg headMsg = new HandleNetHeadMsg();
		byte[] msgByteHead = headMsg.buildHeadMsg(NetHouseMsgType.NETAPP_USER_HOTCOMPANYGET_REQ, msgLength, msgLength, sequence, 0);
		
		//拼接消息头和消息体
		final byte[] msgByteArray = UniversalUtils.copyByteArray(msgByteHead, msgProBody);
		
		return msgByteArray;
	}
	
	
	
}
