package com.lhdz.util;

public class Define {
	
	
	/**
	 * 请求的URL
	 */
	public final static String URL_NEWS = "http://www.imxiexie.com/Information/index.html";//资讯的URL
	
	public final static String URL_UPDATE_VERSION = "http://www.imxiexie.com/download/android/aUpdate.txt";//版本更新请求的URL
	public final static String URL_TEST_UPDATE_VERSION = "http://www.imxiexie.com/test/android/aUpdate.txt";//测试版本更新请求的URL
	
	public final static String URL_SERVICE_COMPLAINT="http://www.imxiexie.com/Information/complaint.html";//服务投诉的URL
	public final static String URL_USER_AGREEMENT = "http://www.imxiexie.com/Information/useragreement.html";//用户协议的URL
	
	public final static String URL_QUESTION_WAY = "http://www.imxiexie.com/Information/Q_way.html";//问题反馈
	public final static String URL_QUESTION_ACCOUNT = "http://www.imxiexie.com/Information/Q_account.html";//账户问题
	public final static String URL_QUESTION_ORDER = "http://www.imxiexie.com/Information/Q_order.html";//订单问题
	public final static String URL_QUESTION_PAY = "http://www.imxiexie.com/Information/Q_pay.html";//支付问题
	public final static String URL_QUESTION_POINT = "http://www.imxiexie.com/Information/Q_point.html";//积分问题
	
	public final static String URL_ACTIVITY = "http://www.imxiexie.com/Information/activity.html";//积分问题
	public final static String URL_AD_FIRST = "http://www.imxiexie.com/Information/banner01.html";//过年的软文
	public final static String URL_AD_THREE = "http://www.imxiexie.com/Information/banner02.html";//平台的软文

	
	
	/*
	 * 图片路径
	 */
	public final static String URL_UPLOAD_USER_IMAGE = "http://www.imxiexie.com/upload_img.php?s=user"; // 上传服务图片路径;
	public final static String URL_DOWNLOAD_USER_IMAGE = "http://www.imxiexie.com/download/app_images/user/"; // 下载服务图片路径;
	
	public final static String URL_COMPANY_IMAGE = "http://www.imxiexie.com/download/app_images/company/"; // 公司图标路径;

	
	
	/**
	 * 文件存储路径
	 */
	public final static String PATH_APP_BASE = "/xiexie";
	public final static String PATH_COMPANY_IMAGE_CACHE = PATH_APP_BASE + "/imgs/company/";
	public final static String PATH_USER_IMAGE_CACHE = PATH_APP_BASE + "/imgs/user/";
	public final static String PATH_UPDATE_APK = PATH_APP_BASE + "/apk/";
	public final static String PATH_TEST_CRASH = PATH_APP_BASE + "/test/crash/";
	public final static String PATH_TEST_LOG = PATH_APP_BASE + "/test/log/";
	
	

	/**
	 * 用于设置按钮点击后，若网络未返回数据时，将按钮重置为原始状态的等待时间
	 */
	public final static int BTN_DELAY_TIME = 1000 * 20;
	
	
	
	/**
	 * 加载数据的数量
	 */
	public final static int LOAD_DATA_NUM = 15;

	
	
	/**
	 * activity返回的状态码
	 */
	public final static int RESULTCODE_SEX_MAN = 1000;
	public final static int RESULTCODE_SEX_WOMAN = 1001;
	public final static int RESULTCODE_NICK = 2000;
	public final static int RESULTCODE_AREA = 2001;
	public final static int RESULTCODE_GRAPH = 2002;
	public final static int RESULTCODE_SERVERADDR = 2003;
	public final static int RESULTCODE_COMPLAINT = 2004;//评价页面的返回
	public final static int RESULTCODE_TERMINATION = 2005;//退出明星公司页面的返回
	public final static int RESULTCODE_APPLYJOIN = 2006;//加入明星公司页面的返回
	public final static int RESULTCODE_CITY_RESET = 2007;//

	/**
	 * 更改个人头像
	 */
	public static final int PICK_FROM_CAMERA = 1;
	public static final int CROP_FROM_CAMERA = 2;
	public static final int PICK_FROM_ALBUM = 3;
	public static final String PHOTO_FILE_NAME = "temp_photo.jpg";

	/**
	 * 广播接收的注册
	 */
	public final static String BROAD_CAST_RECV_DATA_COMPLETE = "broad_cast_recv_data_complete";//
	public final static String BROAD_CAST_RECV_DATA_AUTH = "broad_cast_recv_data_auth";// 接收网络数据广播--来自登录认证服务器
	public final static String BROAD_CAST_RECV_DATA_HOUSE = "broad_cast_recv_data_house";// 接收网络数据广播--来自家政服务器
	
	public final static String BROAD_FRAGMENT_RECV_APPHOME = "broad_fragment_recv_apphome";//在fragment中接收到首页服务类型数据
	public final static String BROAD_FRAGMENT_RECV_STARCOMPANY = "broad_fragment_recv_starcompany";//在fragment中接收到明星公司数据
	public final static String BROAD_FRAGMENT_RECV_LOGIN = "broad_fragment_recv_login";//在fragment中接收到登录成功
	
	public final static String BROAD_BADGE_CLEAR = "broad_badge_clear";//在订单中发送清除badge的通知
	
	public final static String BROAD_SERVICE_UPDATE_VERSION = "broad_service_update_version";//客户端安装新版本的广播
	public final static String BROAD_CAST_RECV_WXPAY = "broad_cast_recv_wxpay_result";

	
	
	/**
	 * 广播的传值
	 */
	public final static String BROAD_DATA_FROM = "DATAFROM";// 广播通知activity的数据来源
	public final static String BROAD_RECVTIME = "RECVTIME";// 广播通知activity的接收时间
	public final static String BROAD_SEQUENCE = "SEQUENCENO";// 广播通知activity的sequence
	public final static String BROAD_MSG_RECVTIME = "MSG_RECVTIME";// 广播通知activity的recvtime
	public final static String BROAD_MSG_TYPE = "MSGTYPE";// 广播通知activity的msgtype
	
	
	
	/**
	 * 数据库数据版本的定义
	 */
	public final static int DBAPPHOME_VERTYPE = 100;//首页服务类型
	public final static int DBSTARCOMPANY_VERTYPE = 101;//明星公司
	
	
	/**
	 * SharedPreferences
	 */
	public final static String SP_KEY_ACCOUNT = "account";
	public final static String SP_KEY_LOCATION_NAME = "locationName";
	public final static String SP_KEY_LOCATION_ID = "locationId";
	public final static String SP_KEY_RECENT_CITY = "recentCity";


	
	

}
