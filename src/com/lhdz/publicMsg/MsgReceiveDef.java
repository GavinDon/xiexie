package com.lhdz.publicMsg;

import java.util.List;

import com.lhdz.dataUtil.protobuf.AuthMsgPro.AUTH_ServerInfo_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eACCOUNTTYPE_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eUSERSEX_PRO;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCmpVerInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsHomenTypeInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserAddCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderDailInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSeeCmpServiceInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserServiceAddrInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.dataUtil.protobuf.PublicmsgPro.Net_BaseInfo_PRO;

public class MsgReceiveDef {
	
	
	
	/**
	 * 登录认证--通用响应--只需要获得操作结果的，一律使用该消息
	 */
	public static class AuthNetCommonResp {
		public eOPERRESULT_PRO eResult;
		public int iUserid ;//用户id
	}
	
	
	/**
	 * 连接请求的服务器响应--登录认证服务器的连接响应
	 */
	public static class NetConnectResp {
		public eOPERRESULT_PRO eResult ;
		public int iUserid ; // 用户账号ID;
		public int iSrvTime ; // 服务器的时间
	}
	
	
	/**
	 * 登录响应
	 *
	 */
	public static class AuthLoginResp {
		public eOPERRESULT_PRO eResult;
		public int iuserid; // userid
		public String szEmail; // 邮箱
		public String szPhoneNum; // 手机
		public String szUserNick; // 昵称
		public String szSignaTure; // 个性签名
		public String szUserID; // 用户账号ID
		public int iHeadPic; // 头像ID
		public String szHeadPic; // 头像信息
		public int iGroupVer; // 分组版本
		public int iFriendVer; // 好友列表版本
		public int iVipLevel; // VIP等级
		public eUSERSEX_PRO eSex; // 性别
		public int iAreaID; // 地区ID
	}
	
	
	/**
	 * 快速注册响应
	 */
	public static class QuickRegisterResp {
		public eOPERRESULT_PRO eResult;
		public eACCOUNTTYPE_PRO eAccountType; // 账号类型
		public String szAccountID; // 账号
		public String szActivKey; // 账号激活码
		public int uUserID; // 账号唯一标示
	}
	
	
	/**
	 * 忘记密码消息响应
	 */
	public static class ForgotPasswordResp {
		public eOPERRESULT_PRO eResult;
		public int iuserid; // userid
		public String szUserID; // 账号ID;
		public String szMovbile; // 手机号
		public String szEmail; // 邮箱
		public String szUserNick; // 用户昵称
		public int iHeadPic; // 头像ID
		public String szHeadPic; // 头像MD5
	}
	
	
	/**
	 * 获取服务器列表响应/查询服务器信息响应
	 */
	public static class GetServerInfoResp {
		public eOPERRESULT_PRO eResult;
		public int iuserid; // userid
		public List<AUTH_ServerInfo_PRO> listInfo;// 服务器信息列表
//		public AUTH_ServerInfo_PRO info;// 服务器信息列表
	}
	
	
	
	/**
	 * 查找个人信息响应
	 */
	public static class QueryBaseInfoResp {
		public eOPERRESULT_PRO eResult;
		public int iuserid;// 源userid
		public Net_BaseInfo_PRO info;// 查找到的信息
	}
	
	
	// ============================以下为家政消息================================
	
	
	/**
	 * 家政服务--通用响应
	 */
	public static class HsNetCommon_Resp {
		public e_HsOperResult_Pro eOperResult;// 操作结果信息（必须）
		public int iUserID;// userId（必须）
		public int iSelectID;//公司id
	}
	
	
	
	/**
	 * 提取首页家政结构体响应
	 */
	public static class HsAppHomeInfo_Resp {
		public e_HsOperResult_Pro eOperResult;// 操作结果信息（必须）
		public List<HsHomenTypeInfo_Pro> homenTypeList;// 家政类型列表
	}
	
	
	/**
	 * 广播下单 预约下单响应
	 */
	public static class HsBroadCastOrderSet_Resp {
		public e_HsOperResult_Pro eOperResult;//操作结果信息（必须）
		public int iOrderID; // 订单ID：（必须）
		public String szOrderCode; // 订单编号（必须）
		public String szCreateTime; // 订单创建时间
	}
	
	
	/**
	 * 提取明星公司列表响应
	 */
	public static class HsStarCompanyGet_Resp {
		public e_HsOperResult_Pro eOperResult;// 操作结果信息（必须）
		public int iTotalCount; // 公司总个数;
		public int iSendCount;//已经发送的公司个数;
		public List<HsCompanyInfo_Pro> companyList;// 明星公司列表
	}
	
	
	/**
	 * 用户订单 列表 提取响应
	 */
	public static class HsUserOrderList_Resp {
		public e_HsOperResult_Pro eOperResult;// 操作结果（必须）
		public int iTotalCount; // 总个数;
		public int iSendCount; // 已经发送的个数;
		public List<HsUserOrderInfo_Pro> orderList;// 用户订单列表
	}
	
	/**
	 * 用户订单  详情  提取响应
	 */
	public static class HsUserOrderDetail_Resp {
		public e_HsOperResult_Pro eOperResult;//操作结果（必须）
		public HsUserOrderDailInfo_Pro orderDetail;//用户订单  详情
	}
	
	/**
	 * 用户提取已加入的家政公司列表响应
	 */
	public static class HsGetUserJoinCompanyList_Resp {
		public e_HsOperResult_Pro eOperResult;
		public List<HsUserAddCompanyInfo_Pro> companyList;//加入的公司列表
	}
	
	
	/**
	 * 用户提取服务地址列表 响应
	 */

	public static class HsUserGetServiceAddrs_Resp {
		public e_HsOperResult_Pro eOperResult;
		public int uUserID; // 用户ID
		public List<HsUserServiceAddrInfo_Pro> addrList; // 联系地址信息
	}
	
	/**
	 * 提取抢单公司列表 响应
	 */
	
	public static class HsOrderRaceDetailGet_Resp {
		public e_HsOperResult_Pro eOperResult;
		public List<HsRaceCompanyInfo_Pro> raceCompanyList; // 抢单公司想信息
	}
	
	
	/**
	 * 获取数据库最新数据版本 响应
	 */
	public static class HsGetDBVerInfo_Resp {
		public e_HsOperResult_Pro eOperResult;
		public List<HsCmpVerInfo_Pro> versionList; // 版本信息 列表
	}
	
	
	
	/**
	 *  用户查看到的商户服务信息响应组织消息
	 *
	 */
	public static class HsUserSeeCmpServiceInfoGet_Resp {
		public e_HsOperResult_Pro eOperResult;
		public List<HsUserSeeCmpServiceInfo_Pro> cmpServiceList;
	}
	
	
	
	// ============================通知类消息================================
	
	
	
	/**
	 * 家政服务--通用通知消息
	 */
	public static class HsCommon_Notify {
		public int uUserID; // 为用户生成一个唯一的ID;
		public int uCompanyID; // 为公司成一个唯一的ID;
		public int iOrderID;
	}
	
	
	/**
	 * 家政公司抢单成功的通知
	 *
	 */
	public static class HsCmpRaceOrder_Notify {
		public int uCompanyID; // 公司ID；
		public int uOrderID; // 订单ID;
		public String szSrvPrice; // 服务价格
		public String szRemark; // 抢单说明信息;
	}
	

	/**
	 * 家政公司提请申诉通知
	 *
	 */
	public static class HsCompanyComplain_Notify {
		public int uOrderID; // 订单ID;
		public int uUserID; // 提交申诉的公司ID
		public String szContent; // 申诉内容
	}
	
	
	/**
	 *  家政公司审批结果通知
	 *
	 */
	public static class HsCmpRefuseAtticheResult_Notify {
		public int iCompanyID; // 公司唯一标示;
		public int uUserID; // 用户唯一标示
		public int uResult; // 审批结果， 0为同意，1为拒绝
		public String szRejReason; // 拒绝原因(拒绝时有效)
	}
	
	
}
