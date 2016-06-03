package com.lhdz.publicMsg;

public class NetHouseMsgType {
	
	
	/**
	 * 订单状态
	 */
	public final static int ORDERSTATE_REQ = 100;//下单
	public final static int ORDERSTATE_RACING = 110;//抢单中
	public final static int ORDERSTATE_PAY = 120;//待支付
	public final static int ORDERSTATE_SERVICE = 130;//服务中
	public final static int ORDERSTATE_COMPLAINT = 140;//投诉
	public final static int ORDERSTATE_FINISH = 150;//已完成
	public final static int ORDERSTATE_ZHUANZHANG = 160;//转账
	public final static int ORDERSTATE_CHEDAN = 170;//已撤单
	
	/**
	 * 申请加入明星公司的状态
	 */
	public final static int JOINSTATE_APPLY = 200;//申请
	public final static int JOINSTATE_SHENHE = 210;//审核
	public final static int JOINSTATE_JOIN = 220;//加入
	public final static int JOINSTATE_JUJUE = 230;//拒绝
	public final static int JOINSTATE_JIEYUE = 240;//解约
	
	
	// 消息来源
	public final static int E_FROM_USER_PRO = 0; // 来自买家
	public final static int E_FROM_HOMEN_PRO = 1; // 来自商家
	public final static int E_HQ_SERVER_PRO = 2; // 来自平台

	public final static int NETAPP_HOMEDATA_GET_REQ = (0x00000020); // app首页数据提取请求
	public final static int NETAPP_HOMEDATA_GET_RESP = (0x00001020); // 响应
	public final static int NETAPP_HOMENTYPE_GET_REQ = (0x00000021); // app家政类型提取请求
	public final static int NETAPP_HOMENTYPE_GET_RESP = (0x00001021); // 响应
	public final static int NETAPP_STARCOMPANY_GET_REQ = (0x00000022); // 提取明星公司信息列表请求
	public final static int NETAPP_STARCOMPANY_GET_RESP = (0x00001022); // 响应
	public final static int NETAPP_COMPANYINFO_GET_REQ = (0x00000023);// 公司详情提取请求
	public final static int NETAPP_COMPANYINFO_GET_RESP = (0x00001023); // 响应
	public final static int NETAPP_ORDERLIST_GET_REQ = (0x00000024); // 用户订单列表提取请求
	public final static int NETAPP_ORDERLIST_GET_RESP = (0x00001024); // 响应
	public final static int NETAPP_ORDERINFO_GET_REQ = (0x00000025); // 订单详情查看请求
	public final static int NETAPP_ORDERINFO_GET_RESP = (0x00001025); // 响应
	public final static int NETAPP_ORDERRACEINFO_GET_REQ = (0x00000026); // 提取抢单公司信息请求
	public final static int NETAPP_ORDERRACEINFO_GET_RESP = (0x00001026); // 响应
	public final static int NETAPP_BROADCASTORDER_REQ = (0x00000027); // 用户发放订单请求
	public final static int NETAPP_BROADCASTORDER_RESP = (0x00001027); // 响应;
	public final static int NETAPP_USERADDCOMPANY_REQ = (0x00000028); // 申请加入指定公司请求
	public final static int NETAPP_USERADDCOMPANY_RESP = (0x00001028); // 响应家政公司请求
	public final static int NETAPP_SELECTORDERHOMEN_REQ = (0x00000029); // 选择指定的
	public final static int NETAPP_SELECTORDERHOMEN_RESP = (0x00001029); // 响应
	public final static int NETAPP_USER_BACKOUTORDER_REQ = (0x0000002a); // 用户撤单请求
	public final static int NETAPP_USER_BACKOUTORDER_RESP = (0x0000102a); // 响应
	public final static int NETAPP_USER_SUREPAY_REQ = (0x0000002b); // 用户同意付款请求；
	public final static int NETAPP_USER_SUREPAY_RESP = (0x0000102b); // 响应
	public final static int NETAPP_USER_ASKDAPPEAL_REQ = (0x0000002c); // 用户申请申诉请求
	public final static int NETAPP_USER_ASKDAPPEAL_RESP = (0x0000102c); // 用户响应
	public final static int NETAPP_USER_GETCHATMSG_REQ = (0x0000002d); // 用户提取聊天信息请求
	public final static int NETAPP_USER_GETCHATMSG_RESP = (0x0000102d); // 响应
	public final static int NETAPP_USER_SELFINFOGET_REQ = (0x0000002e); // 提取用户个人信息请求
	public final static int NETAPP_USER_SELFINFOGET_RESP = (0x0000102e); // 响应
	public final static int NETAPP_USER_GETADDCOMPANY_REQ = (0x0000002f); // 提取加入的家政公司请求
	public final static int NETAPP_USER_GETADDCOMPANY_RESP = (0x0000102f); // 响应
	public final static int NETAPP_USER_QUITCOMPANY_REQ = (0x00000030); // 退出家政公司请求
	public final static int NETAPP_USER_QUITCOMPANY_RESP = (0x00001030); // 响应
	public final static int NETAPP_USER_USEREVALUATE_SET_REQ = (0x00000031); // 用户对订单进行评价设置请求
	public final static int NETAPP_USER_USEREVALUATE_SET_RESP = (0x00001031); // 响应
	public final static int NETAPP_USER_USEREVALUATE_GET_REQ = (0x00000032); // 获取用户对订单的评价
	public final static int NETAPP_USER_USEREVALUATE_GET_RESP = (0x00001032); // 响应
	public final static int NETAPP_USER_ADDSERVICEADDR_REQ = (0x00000033); // 用户添加服务联系地址请求
	public final static int NETAPP_USER_ADDSERVICEADDR_RESP = (0x00001033); // 响应
	public final static int NETAPP_USER_SERVICEADDRS_GET_REQ = (0x00000034); // 用户提取服务联系地址请求
	public final static int NETAPP_USER_SERVICEADDRS_GET_RESP = (0x00001034); // 响应
	public final static int NETAPP_USER_SERVICEADDR_DEL_REQ = (0x00000035); // 删除服务联系地址请求消息
	public final static int NETAPP_USER_SERVICEADDR_DEL_RESP = (0x00001035); // 响应
	public final static int NETAPP_USER_AFFIRMFINISH_REQ = (0x00000036); // 用户对已完成的服务进行确认操作
	public final static int NETAPP_USER_AFFIRMFINISH_RESP = (0x00001036); // 响应
	public final static int NETAPP_USER_DBVERINFO_GET_REQ = (0x00000037); // 用户获取数据库最新数据版本信息请求
	public final static int NETAPP_USER_DBVERINFO_GET_RESP = (0x00001037); // 响应
	public final static int NETAPP_USER_CMPSRVINFOS_GET_REQ = (0x00000038); // 用户查看的公司服务信息请求消息
	public final static int NETAPP_USER_CMPSRVINFOS_GET_RESP = (0x00001038); // 响应
	public final static int NETAPP_USER_SUBSCRIBEORDER_REQ = (0x00000039);// 用户预约下单请求消息
	public final static int NETAPP_USER_SUBSCRIBEORDER_RESP = (0x00001039); // 响应
	public final static int NETAPP_USER_HOTCOMPANYGET_REQ = (0x0000003a); // 用户提取热门推荐请求消息
	public final static int NETAPP_USER_HOTCOMPANYGET_RESP = (0x0000103a); // 响应


	// 来自家政公司的消息类型定义
	public final static int NETHOMEN_ORDERLIST_GET_REQ = (0x00000040); // 家政熱抢订单列表信息获取请求
	public final static int NETHOMEN_ORDERLIST_GET_RESP = (0x00001040); // 家政熱抢订单列表信息获取
	public final static int NETHOMEN_HANDLEAPPOINTORDER_REQ = (0x00000041); // 处理指派单请求
	public final static int NETHOMEN_HANDLEAPPOINTORDER_RESP = (0x00001041); // 处理指派单回应
	public final static int NETHOMEN_SENDREVIEW_REQ = (0x00000042); // 发送评价请求
	public final static int NETHOMEN_SENDREVIEW_RESP = (0x00001042); // 发送评价回应
	public final static int NETHOMEN_ORDERINFO_GET_REQ = (0x00000043); // 订单详情查看请求
	public final static int NETHOMEN_ORDERINFO_GET_RESP = (0x00001043); // 响应
	public final static int NETHOMEN_ORDERLIST_OTHER_GET_REQ = (0x00000044); // 获取抢单成功（已支付，进行中，请付款，已付款）或已完成的订单
	public final static int NETHOMEN_ORDERLIST_OTHER_GET_RESP = (0x00001044); // 响应
	public final static int NETHOMEN_JOINHOMENLIST_GET_REQ = (0x00000045); // 获取加入指定家政公司列表请求
	public final static int NETHOMEN_JOINHOMENLIST_GET_RESP = (0x00001045); // 获取加入指定家政公司列表请求响应
	public final static int NETHOMEN_RACEORDER_REQ = (0x00000046); // 抢单申请请求
	public final static int NETHOMEN_RACEORDER_RESP = (0x00001046); // 抢单申请回应
	public final static int NETHOMEN_VERITYSERVICEPEOPLE_REQ = (0x00000047); // 处理服务人员申请加入家政公司消息（审核服务人员）
	public final static int NETHOMEN_VERITYSERVICEPEOPLE_RESP = (0x00001047); // 处理服务人员响应
	public final static int NETHOMEN_RACEFAILEDLIST_GET_REQ = (0x00000048); // 获取抢单失败列表请求
	public final static int NETHOMEN_RACEFAILEDLIST_GET_RESP = (0x00001048); // 获取抢单失败列表请求响应
	public final static int NETHOMEN_CHATMSG_GET_REQ = (0x00000049); // 获取聊天消息
	public final static int NETHOMEN_CHATMSG_GET_RESP = (0x00001049); // 响应
	public final static int NETHOMEN_CHATMSG_SEND_REQ = (0x0000004a); // 发送聊天消息
	public final static int NETHOMEN_CHATMSG_SEND_RESP = (0x0000104a); // 响应
	public final static int NETHOMEN_GETUSERREVIEW_REQ = (0x0000004b); // 获取用户对家政公司的评价
	public final static int NETHOMEN_GETUSERREVIEW_RESP = (0x0000104b); // 响应

	// 通知类消息
	public final static int NETAPP_USER_BACKOUT_NOTIFY = (0x00000062); // 用户撤单通知;
	public final static int NETAPP_USER_SUREPAY_NOTIFY = (0x00000063); // 用户确认付款通知
	public final static int NETAPP_HOMEN_CHAT_NOTIFY = (0x00000065); // 聊天信息通知;
	public final static int NETAPP_USER_QUITCOMPANY_NOTIFY = (0x00000066); // 用户退出家政公司通知
	public final static int NETAPP_USER_ORDER_EVALUATE_NOTIFY = (0x00000067); // 用户评价通知
	public final static int NETHOMEN_SENDREVIEW_NOTIFY = (0x00000068); // 家政公司的评价发送通知到app
	public final static int NETHOMEN_NEWORDER_NOTIFY = (0x00000069); // 新订单通知--App--Homen用
	public final static int NETHOMEN_SELECTHOMEN_NOTIFY = (0x0000006a); // 客户选定家政公司通知（即订单已付款），其他家政流单。--App--Homen
	public final static int NETHOMEN_CHATMSG_SEND_NOTIFY = (0x0000006b); // 发送聊天消息通知
	public final static int NETHOMEN_JOINHOMEN_REQ_NOTIFY = (0x0000006c); // 申请加入指定家政公司请求通知
	public final static int NETAPP_HAVELOGIN_NOTIFY = (0x00010003); // 账号在其他客户端登录通知
	public final static int NETAPP_ORDERRACEHOMEN_NOTIFY = (0x00010080); // 家政公司抢单通知;
	public final static int NETAPP_HOMENEXAMINE_NOTIFY = (0x00010081);// 家政公司审批结果通知;
	public final static int NETAPP_USER_ASKAPPEAL_NOTIFY = (0x00010082); // 家政公司提请申诉通知;
	public final static int NETAPP_USER_ORDERPAY_REQ_NOTIFY = (0x00010083); // 家政公司申请用户付款通知


	// 登录类消息
	public final static int NETCMD_CONNECT_REQ = 0x00000001;//连接请求
	public final static int NETCMD_CONNECT_RESP = 0x00001001;	//响应
	public final static int NETCMD_ECHO	 = 0x00000002;	//心跳
	public final static int NETAUTH_USERLOGIN_REQ = 0x00000003; // 登录请求消息(帐号登录)
	public final static int NETAUTH_USERLOGIN_RESP = (0x00001003); // 响应
	public final static int NETAUTH_GETBESTSRV_REQ = (0x00000004); // 提取最优服务器请求
	public final static int NETAUTH_GETBESTSRV_RESP = (0x00001004); // 响应
	public final static int NETAUTH_QUERYSERVERLIST_REQ = (0x00000005); // 获取最优服务器列表请求
	public final static int NETAUTH_QUERYSERVERLIST_RESP = (0x00001005); // 获取最优服务器列表响应
	public final static int NETAUTH_SELFINFOGET_REQ = (0x00000006); // 个人信息提取请求
	public final static int NETAUTH_SELFINFOGET_RESP = (0x00001006); // 响应
	public final static int NETAUTH_QUERYBASEINFO_REQ = (0x00000007); // 个人基本信息查询请求
	public final static int NETAUTH_QUERYBASEINFO_RESP = (0x00001007); // 个人基本信息查询响应
	public final static int NETAUTH_SELFINFOMOD_REQ = (0x00000008); // 个人资料修改请求
	public final static int NETAUTH_SELFINFOMOD_RESP = (0x00001008); // 个人资料修改响应
	public final static int NETAUTH_PASSWORDMOD_REQ = (0x00000009); // 密码修改请求
	public final static int NETAUTH_PASSWORDMOD_RESP = (0x00001009); // 密码修改响应
	public final static int NETAUTH_ACCOUNT_ACTIVE_REQ = (0x0000000a); // 激活帐号请求
	public final static int NETAUTH_ACCOUNT_ACTIVE_RESP = (0x0000100a); // 激活帐号响应
	public final static int NETAUTH_QUERYFRIEDGROUP_REQ = (0x0000000b); // 查询好友分组请求
	public final static int NETAUTH_QUERYFRIEDGROUP_RESP = (0x0000100b); // 查询好友分组响应
	public final static int NETAUTH_QUERYFRIEDLIST_REQ = (0x0000000c); // 查询好友列表请求
	public final static int NETAUTH_QUERYFRIEDLIST_RESP = (0x0000100c); // 查询好友列表响应
	public final static int NETAUTH_QUERYROOMLIST_REQ = (0x0000000d); // 获取群列表请求
	public final static int NETAUTH_QUERYROOMLIST_RESP = (0x0000100d); // 获取群表响应
	public final static int NETAUTH_QUICKREGISTE_REQ = (0x0000000e); // 快速注册请求
	public final static int NETAUTH_QUICKREGISTE_RESP = (0x0000100e); // 快速注册响应
	public final static int NETAUTH_FORGET_PASSWORD_REQ = (0x00000010); // 忘记密码请求
	public final static int NETAUTH_FORGET_PASSWORD_RESP = (0x00001010); // 忘记密码响应
	public final static int NETAUTH_UNITE_LOGIN_REQ = (0x00000011); // 关联登陆请求
	public final static int NETAUTH_UNITE_LOGIN_RESP = (0x00001011); // 关联登陆响应
	public final static int NETAUTH_RESET_PASSWD_REQ = (0x00000012); // 重置密码请求
	public final static int NETAUTH_RESET_PASSWD_RESP = (0x00001012); // 重置密码响应
	public final static int NETAUTH_ACCOUNTAUTH_REQ = (0x00000013); // 注册账号验证请求
	public final static int NETAUTH_ACCOUNTAUTH_RESP = (0x00001013); // 注册账号验证响应
	public final static int NETAUTH_GETSCREENUSERS_REQ = (0x00000014); // 提取屏蔽人请求
	public final static int NETAUTH_GETSCREENUSERS_RESP = (0x00001014); // 提取屏蔽人响应
	public final static int NETAUTH_USERSELINFOMOD_REQ = (0x00000015); // 个人指定信息修改请求消息
	public final static int NETAUTH_USERSELINFOMOD_RESP = (0x00001015); // 个人指定信息修改响应消息;

	public final static int WEB_USERHEADPICMOD_REQ = (0x0000000a); // 用户个人头像修改请求
																	// 10
	public final static int WEB_USERHEADPICMOD_RESP = (0x0000100a); // 用户个人头像修改响应
																	// 4106
	public final static int WEB_QUERYBASEINFO_REQ = (0x0000000b); // 个人基本信息查询请求
																	// 11
	public final static int WEB_QUERYBASEINFO_RESP = (0x0000100b); // 个人基本信息查询响应
																	// 4107
	public final static int WEB_SELFINFOMOD_REQ = (0x00000012); // 个人资料修改请求 18
	public final static int WEB_SELFINFOMOD_RESP = (0x00001012); // 个人资料修改响应
																	// 4114
	public final static int WEB_FRIENDALIASMOD_REQ = (0x00000013); // 好友备注修改请求
																	// 19
	public final static int WEB_FRIENDALIASMOD_RESP = (0x00001013); // 好友备注修改响应
																	// 4115
	public final static int WEB_FRIENDGROUPMOD_REQ = (0x00000014); // 好友分组修改请求
																	// 20
	public final static int WEB_FRIENDGROUPMOD_RESP = (0x00001014); // 好友分组修改响应
																	// 4116
	public final static int WEB_CHATCOMMON_NOTIFY = (0x00000015); 	// 单聊通用通知 21
	public final static int WEB_CHATSEND_MSG = (0x00000016); 		// 聊天消息发送 22
	public final static int WEB_CHATRECV_MSG = (0x00001016); // 聊天消息接收 4118
	public final static int WEB_GETOFFLINE_REQ = (0x00000017); // 获取离线消息请求 23
	public final static int WEB_GETOFFLINE_RESP = (0x00001017); // 获取离线消息响应 4119
	public final static int WEB_ADDGROUP_REQ = (0x00000018); // 添加分组请求 24
	public final static int WEB_MODGROUP_REQ = (0x00000019); // 修改分组请求 25
	public final static int WEB_DELGROUP_REQ = (0x0000001a); // 删除分组请求 26
	public final static int WEB_GROUPOPER_RESP = (0x0000101a); // 分组操作响应消息 4122

}
