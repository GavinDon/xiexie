package com.lhdz.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lhdz.dataUtil.protobuf.EnumPro.eOPERRESULT_PRO;
import com.lhdz.dataUtil.protobuf.EnumPro.eUSERSEX_PRO;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.entity.ServiceListInfoEntity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;

/**
 * 通用工具方法类
 *
 */
public class UniversalUtils {

	
	/**
	 * 根据网络返回的状态判断操作结果信息--主要用于判断来自auth服务器的信息
	 * @param result_Pro
	 * @return
	 */
	public static String judgeNetResult_Auth(eOPERRESULT_PRO result_Pro){
		String strResult = "";
		
		if(result_Pro == null || "".equals(result_Pro)){
			strResult = "未知原因";
			return strResult;
		}
		
		switch (result_Pro) {
		case E_OPER_SUCCESS_PRO:
			strResult = "成功";
			break;
		case E_OPER_AUTHFAILER_PRO:
			strResult = "认证失败";
			break;
		case E_OPER_NOAUTHINFO_PRO:
			strResult = "认证信息不存在";
			break;
		case E_OPER_USERORPASSWD_PRO:
			strResult = "用户名或密码错误";
			break;
		case E_OPER_USERNOTEXIST_PRO:
			strResult = "用户不存在";
			break;
		case E_OPER_REJEST_ADDREQ_PRO:
			strResult = "拒绝加为好友";
			break;
		case E_OPER_ERRORPARAM_PRO:
			strResult = "参数错误";
			break;
		case E_OPER_LINKERROR_PRO:
			strResult = "链路异常";
			break;
		case E_OPER_CGINOMEMORY_PRO:
			strResult = "内存不足";
			break;
		case E_OPER_DATABASEERROR_PRO:
			strResult = "数据库操作失败";
			break;
		case E_OPER_PLOICIENOEXIST_PRO:
			strResult = "没有验证策略";
			break;
		case E_OPER_EMPTY_PLOICIEINFO_PRO:
			strResult = "策略内容为空";
			break;
		case E_OPER_PLOICIENOERROR_PRO:
			strResult = "策略验证不符";
			break;
		case E_OPER_NOOFFLINMSG_PRO:
			strResult = "没有离线消息";
			break;
		case E_OPER_SERVICEBUSY_PRO:
			strResult = "服务器忙";
			break;
		case E_OPER_ROOMNOTHERUSER_PRO:
			strResult = "群中没有该用户";
			break;
		case E_OPER_ROOMNOEXIST_PRO:
			strResult = "该群信息不存在";
			break;
		case E_OPER_MODROOMALIASFAIL_PRO:
			strResult = "修改群备注名失败";
			break;
		case E_OPER_ROOMNOTMANAGER_PRO:
			strResult = "非群管理员或群主;不能踢人";
			break;
		case E_OPER_SETMANAGERONLYHOST_PRO:
			strResult = "仅群主能设置管理员";
			break;
		case E_OPER_ROOMHAVEMAXMANAGER_PRO:
			strResult = "管理员人数已达上限";
			break;
		case E_OPER_ALREADYLOGIN_PRO:
			strResult = "该用户已登录";
			break;
		case E_OPER_SYS_CATCH_ERROR_PRO:
			strResult = "程序异常";
			break;
		case E_OPER_ERROR_DBADMIN_PRO:
			strResult = "提取数据库超级用户ID失败";
			break;
		case E_OPER_GETRESULT_ISNULL_PRO:
			strResult = "查询结果为空";
			break;
		case E_OPER_OVERROOMCOUNT_PRO:
			strResult = "超过群服务器建群数目";
			break;
		case E_OPER_NOTLOGIN_PRO:
			strResult = "用户未登录";
			break;
		case E_OPER_OPERTERMINATED_PRO:
			strResult = "操作被终止";
			break;
		case E_OPER_CLIENTVERERROR_PRO:
			strResult = "客户端版本不正确";
			break;
		case E_OPER_HOSTREJECTLEAVE_PRO:
			strResult = "不允许群主退群";
			break;
		case E_OPER_ONLYHOSTOPER_PRO:
			strResult = "只有群主可以执行此操作";
			break;
		case E_OPER_DB_ALLOC_FAILURE_PRO:
			strResult = "内存申请失败";
			break;
		case E_OPER_DB_DBCONN_ERROR_PRO:
			strResult = "连接数据库失败";
			break;
		case E_OPER_DB_SQLCONTENT_ERROR_PRO:
			strResult = "SQL语句有误";
			break;
		case E_OPER_DB_EXECSQL_ERROR_PRO:
			strResult = "执行SQL语句失败";
			break;
		case E_OPER_DB_SPROC_ERROR_PRO:
			strResult = "存储过程调用方式有误";
			break;
		case E_OPER_DB_EXECPORC_ERROR_PRO:
			strResult = "执行存储过程失败";
			break;
		case E_OPER_DB_INVALID_PARAM_PRO:
			strResult = "参数不正确";
			break;
		case E_OPER_DB_FETCHRESULT_ERROR_PRO:
			strResult = "执行结果错误";
			break;
		case E_OPER_DB_USERORPASSWD_PRO:
			strResult = "用户名或密码错误";
			break;
		case E_OPER_DB_USERNO_ERROR_PRO:
			strResult = "用户UserID号不正确";
			break;
		case E_OPER_DB_ADMIN_NOTEXIST_PRO:
			strResult = "管理员角色不存在";
			break;
		case E_OPER_DB_NOADMIN_PRO:
			strResult = "用户不是管理员";
			break;
		case E_OPER_DB_POLICIE_FAILURE_PRO:
			strResult = "安全策略验证失败";
			break;
		case E_OPER_DB_EMAIL_NOTEXIST_PRO:
			strResult = "用户邮箱不存在或禁用";
			break;
		case E_OPER_DB_KEYWORD_EXIST_PRO:
			strResult = "关键字已经存在";
			break;
		case E_OPER_DB_LOAD_MODETYPE_PRO:
			strResult = "加载模块类型不明确";
			break;
		case E_OPER_DB_NOT_FRIENDS_PRO:
			strResult = "用户不在好友列表中";
			break;
		case E_OPER_DB_NOT_USERGROUP_PRO:
			strResult = "用户分组不存在";
			break;
		case E_OPER_DB_MAX_ROOMMBMS_PRO:
			strResult = "群成员已达到最大数量";
			break;
		case E_OPER_DB_ACTIVE_TYPE_PRO:
			strResult = "用户激活类型不明确";
			break;
		case E_OPER_DB_ACCOUNT_ACTIVE_PRO:
			strResult = "用户激活账号失败";
			break;
		case E_OPER_DB_MOBILE_EXIST_PRO:
			strResult = "电话号码已经存在";
			break;
		case E_OPER_DB_EMAIL_EXIST_PRO:
			strResult = "邮箱地址已经存在";
			break;
		case E_OPER_DB_REGISTER_TYPE_PRO:
			strResult = "用户注册类型不明确";
			break;
		case E_OPER_DB_EXIST_FRIENDS_PRO:
			strResult = "用户已在好友列表中";
			break;
		case E_OPER_DB_USERID_ISUSED_PRO:
			strResult = "该UserID已经被使用";
			break;
		case E_OPER_DB_GETWAY_ISUSED_PRO:
			strResult = "网关ID已经被使用";
			break;
		case E_OPER_DB_USER_HADROOMS_PRO:
			strResult = "用户已经在房间列表中";
			break;
		case E_OPER_DB_USERINFO_REJECT_PRO:
			strResult = "用户已设置查看权限";
			break;
		
		default:
			strResult = "未知原因";
			break;
		}
		
		return strResult;
	}
	
	
	
	/**
	 * 根据网络返回的状态判断操作结果信息--主要用户判断来自家政服务器的信息
	 * @param result_Pro
	 * @return
	 */
	public static String judgeNetResult_Hs(e_HsOperResult_Pro result_Pro){
		String strResult = "";
		
		if(result_Pro == null || "".equals(result_Pro)){
			strResult = "未知原因";
			return strResult;
		}
		
		switch (result_Pro) {
		case E_HSOPER_SUCCESS_PRO:
			strResult = "成功";
			break;
		case E_HSOPER_LINKERROR_PRO:
			strResult = "链路异常";
			break;
		case E_HSOPER_OPERERROR_PRO:
			strResult = "数据库操作失败";
			break;
		case E_HSOPER_ALLOC_FAILURE_PRO:
			strResult = "内存申请失败";
			break;
		case E_HSOPER_SERVICEBUSY_PRO:
			strResult = "服务器忙";
			break;
		case E_HSOPER_SYS_CATCH_ERROR_PRO:
			strResult = "程序异常";
			break;
		case E_HSOPER_GETRESULT_ISNULL_PRO:
			strResult = "查询结果为空";
			break;
		case E_HSOPER_NOTLOGIN_PRO:
			strResult = "用户未登录";
			break;
		case E_HSOPER_OPERTERMINATED_PRO:
			strResult = "操作被终止";
			break;
		case E_HSOPER_DB_ALLOCMEM_FAIL_PRO:
			strResult = "内存申请失败；";
			break;
		case E_HSOPER_DB_DBCONN_ERROR_PRO:
			strResult = "连接数据库失败";
			break;
		case E_HSOPER_DB_SQLCONTENT_ERROR_PRO:
			strResult = "SQL语句有误";
			break;
		case E_HSOPER_DB_EXECSQL_ERROR_PRO:
			strResult = "执行SQL语句失败";
			break;
		case E_HSOPER_DB_SPROC_ERROR_PRO:
			strResult = "存储过程调用方式有误";
			break;
		case E_HSOPER_DB_EXECPORC_ERROR_PRO:
			strResult = "执行存储过程失败";
			break;
		case E_HSOPER_DB_INVALID_PARAM_PRO:
			strResult = "参数不正确";
			break;
		case E_HSOPER_DB_FETCHRESULT_ERROR_PRO:
			strResult = "结果错误";
			break;
		case E_HSOPER_DB_USERORPASSWD_PRO:
			strResult = "用户名或密码错误";
			break;
		case E_HSOPER_DB_ADMIN_NOTEXIST_PRO:
			strResult = "管理员角色不存在";
			break;
		case E_HSOPER_DB_USERNOTADMIN_PRO:
			strResult = "用户不是管理员";
			break;
		case E_HSOPER_DB_POLICIE_FAILURE_PRO:
			strResult = "安全策略验证失败";
			break;
		case E_HSOPER_DB_USERNOTEXIST_PRO:
			strResult = "用户不存在或禁用";
			break;
		case E_HSOPER_DB_EMAIL_NOTEXIST_PRO:
			strResult = "用户邮箱不存在或禁用";
			break;
		case E_HSOPER_DB_MOBILE_NOTEXIST_PRO:
			strResult = "电话号码不存在或禁用";
			break;
		case E_HSOPER_DB_KEYWORD_EXIST_PRO:
			strResult = "关键字已经存在";
			break;
		case E_HSOPER_DB_REGISTER_TYPE_PRO:
			strResult = "用户注册类型不明确";
			break;
		case E_HSOPER_DB_ACTIVE_TYPE_PRO:
			strResult = "用户激活类型不明确";
			break;
		case E_HSOPER_DB_ACTIVEACCOUNTERR_PRO:
			strResult = "用户激活账号失败";
			break;
		case E_HSOPER_DB_MOBILE_EXIST_PRO:
			strResult = "电话号码已经存在";
			break;
		case E_HSOPER_DB_EMAIL_EXIST_PRO:
			strResult = "邮箱地址已经存在";
			break;
		case E_HSOPER_DB_ORDER_HASPAYID_PRO:
			strResult = "订单已支付";
			break;
		case E_HSOPER_DB_ORDER_NOHASEXIST_PRO:
			strResult = "序号已经被使用";
			break;
		case E_HSOPER_DB_ORDER_OVERONEEVAL_PRO:
			strResult = "订单只能被评论一次";
			break;
		case E_HSOPER_DB_ORDER_HASBACKOUT_PRO:
			strResult = "订单已被撤销";
			break;
		case E_HSOPER_DB_ORDER_NOTEXIST_PRO:
			strResult = "订单不存在";
			break;
		case E_HSCOMP_DB_INFO_HASEXIST_PRO:
			strResult = "公司信息已经存在";
			break;
		case E_HSCOMP_DB_ASSIGNORDER_FAIL_PRO:
			strResult = "公司派单行为失败";
			break;
		case E_HSCOMP_DB_COMPUSERONEASKONE_PRO:
			strResult = "一个公司只能有一个法人";
			break;
		case E_HSCOMP_DB_SERVICEEXIST_PRO:
			strResult = "公司服务信息已经存在";
			break;

		default:
			strResult = "未知原因";
			break;
		}
		return strResult;
	}
	
	
	

	/**
	 * 根据网络返回的数据   判断用户的性别
	 * @param usersex
	 * @return
	 */
	public static String judgeUserSex(eUSERSEX_PRO usersex){
		String strResult = "";
		
		if(usersex == null || "".equals(usersex)){
			strResult = "未知性别";
			return strResult;
		}
		
		switch (usersex) {
		case EN_USER_MEN_PRO:
			strResult = "男";
			break;
		case EN_USER_WOMEN_PRO:
			strResult = "女";
			break;
		case EN_USER_MID_PRO:
			strResult = "中性人";
			break;
		}
		return strResult;
	}
	
	
	
	/**
	 * 根据服务类型的code获得服务类型
	 * @param appHomeDataList
	 * @param serviceCode
	 * @return
	 */
	public static List<Map<String, String>> getServiceType(List<Map<String, String>> appHomeDataList,String serviceCode){
		List<Map<String, String>> result = new ArrayList<Map<String,String>>();
		
		if(serviceCode == null || appHomeDataList == null){
			return result;
		}
		
		String[] codeArray = serviceCode.split(","); 
		
		for (int i = 0; i < codeArray.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			String code = codeArray[i];
			for (int j = 0; j < appHomeDataList.size(); j++) {
				String sonId = appHomeDataList.get(j).get("Sonid");
				if (code.equals(sonId)) {
					String sonName = appHomeDataList.get(j).get("SonName");
					map.put("code", sonName);
					map.put("Sonid", sonId);
					result.add(map);
					break;
				}
			}
		}
		
		return result;
	}
	
	
	
	
	
	/**
	 * 核心--勿动
	 * 将char类型数据转为byte数组，只取数组的低8位，作为一个长度为1的数组返回
	 * 此方法用于组建连接网络的消息头
	 * @param c
	 * @return
	 */
	public static byte[] charToByte_Head(char c) {
		byte[] b = new byte[1];
		// b[0] = (byte) ((c & 0xFF00) >> 8);
		b[0] = (byte) (c & 0xFF);
		return b;
	}
	
	
	
	/**
	 * 核心--勿动
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。
	 *  此方法用于组建连接网络的消息头
	 *  
	 * @param value
	 * @return
	 */
	public static byte[] intToBytes_Head(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) (value & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[3] = (byte) ((value >> 24) & 0xFF);
		return src;
	}
	
	
	
	/**
	 * 将两个数组合并为一个数组，并返回
	 * 
	 * @param fist
	 * @param second
	 * @return
	 */
	public static byte[] copyByteArray(byte[] fist, byte[] second) {
		byte[] newByte = new byte[fist.length + second.length];
		System.arraycopy(fist, 0, newByte, 0, fist.length);
		System.arraycopy(second, 0, newByte, fist.length, second.length);
		return newByte;
	}
	
	
	
	/**
	 * 核心--勿动
	 * 将byte数组转为int型数据
	 * @param res
	 * @return
	 */
	public static int byteToInt(byte[] res) {
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}
	
	
	
	
	/**
	 * 核心--勿动
	 * 获取网络接收消息的消息头
	 * @param proBuf
	 * @return
	 */
	@SuppressLint("NewApi")
	public static byte[] getNetMsgHead(byte[] proBuf){
		byte[] headMsgArray = Arrays.copyOfRange(proBuf, 0, 23);
		return headMsgArray;
	}
	
	
	
	
	/**
	 * 核心--勿动
	 * 获取网络接收数据的消息体
	 * @param proBuf
	 * @return
	 */
	@SuppressLint("NewApi")
	public static byte[] getNetMsgBody(byte[] proBuf){
		byte[] msgBodyArray = Arrays.copyOfRange(proBuf, 23, proBuf.length);
		return msgBodyArray;
	}
	
	
	
	/**
	 * 根据sonid判断是否需要输入房屋面积
	 * @param appHomeData
	 * @return
	 */
	public static boolean isInputArea(int sonId) {
		boolean bFlag = false;

		switch (sonId) {
		
		case 100:// 新居开荒
			bFlag = true;
			break;
//		case 101:// 外墙清洗
//			bFlag = true;
//			break;
		case 102:// 去除甲醛
			bFlag = true;
			break;
		case 104:// 地毯清洗
			bFlag = true;
			break;
		case 106:// 地板清理
			bFlag = true;
			break;
			
		case 200:// 客厅装修
			bFlag = true;
			break;
		case 201:// 餐厅装修
			bFlag = true;
			break;
		case 202:// 厨房装修
			bFlag = true;
			break;
		case 203:// 书房装修
			bFlag = true;
			break;
		case 204:// 卧室装修
			bFlag = true;
			break;
		case 205:// 阳台装修
			bFlag = true;
			break;
			
		case 500:// 地板维修
			bFlag = true;
			break;
			
		default:
			bFlag = false;
			break;
		}
		
		return bFlag;
	}
	
	
	
	/**
	 * 判断字符串是否为空
	 * @param data
	 * @return
	 */
	public static boolean isStringEmpty(String data) {
		if (data == null || "".equals(data))
			return true;
		return false;
	}

	
	/**
	 * 判断list集合是否为空
	 * @param list
	 * @return
	 */
	public static boolean isStringEmpty(List<?> list) {
		if (list == null || list.size() == 0)
			return true;
		return false;
	}
	
	
	
	/**
	 * 将字符串型数字转换为int型 -- 此处会捕获异常，不至于导致程序崩溃
	 * @param str
	 * @return
	 */
	public static int parseString2Int(String str) {
		if (isStringEmpty(str)) {
			return 0;
		}

		try {

			return Integer.parseInt(str);

		} catch (Exception e) {
			return 0;
		}
	}
	
	
	
	/**
	 * 保留两位小数
	 * @param d
	 * @return
	 */
	public static String getPoint2Float(double d)
	{
		DecimalFormat decimalFormat =new DecimalFormat("0.00");
		return decimalFormat.format(d);
	}
	
	
	
	/**
	 * 保留两位小数
	 * @param str
	 * @return
	 */
	public static String getString2Float(String str)
	{
		if(UniversalUtils.isStringEmpty(str)){
			return "0.00";
		}
		Double d = Double.valueOf(str);
		DecimalFormat decimalFormat =new DecimalFormat("0.00");
		return decimalFormat.format(d);
	}
	
	
	
	/**
	 * 调此方法输入所要转换的时间输入例如（"2014-06-14"）返回时间戳
	 * 
	 * @param time
	 * @return
	 */
	public static String dateToTimeMillis(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		Date date;
		String times = null;
		try {
			date = sdr.parse(time);
			long l = date.getTime();
			times = String.valueOf(l);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return times;
	}
	
	
	
	/**
	 * 调此方法输入所要转换的时间输入例如（"2014-06-14 12:12"）返回时间戳
	 * 
	 * @param time
	 * @return
	 */
	public static String datetimeToTimeMillis(String time) {
		SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.CHINA);
		Date date;
		String times = null;
		try {
			date = sdr.parse(time);
			long l = date.getTime();
			times = String.valueOf(l);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return times;
	}
	
	
	
	/**
	 * 判断分钟是否小于10
	 * @param minute
	 * @return
	 */
	public static String judgeMinute(int minute){
		return minute < 10 ? "0" + minute : "" + minute;
	}
	
	/**
	 * 判断小时是否小于10
	 * @param hourOfDay
	 * @return
	 */
	public static String judgeHourOfDay(int hourOfDay){
		return hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay; 
	}
	
	
	/**
	 * 判断月是否小于10
	 * @param month
	 * @return
	 */
	public static String judgeMonth(int month){
		return month < 10 ? "0" + month : "" + month;
	}
	
	
	/**
	 * 判断日是否小于10
	 * @param dayOfMonth
	 * @return
	 */
	public static String judgeDayOfMonth(int dayOfMonth){
		return dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
	}
	
	
	
	/**
	 * 格式化时间
	 * @param time  Long time
	 * @return  HH:mm
	 */
	public static String getTimeHHMM(Long time) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		Date data = new Date(time);
		return timeFormat.format(data);
	}

	
	/**
	 * 格式化时间
	 * @param time  Long time
	 * @return  MM-dd
	 */
	public static String getTimeMMDD(Long time) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("MM-dd");
		Date data = new Date(time);
		return timeFormat.format(data);
	}

	
	/**
	 * 格式化时间
	 * @param time  Long time
	 * @return  yyyy-MM-dd HH:mm
	 */
	public static String getTimeYMDHHMM(Long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return format.format(date);
	}

	
	/**
	 * 格式化时间
	 * @param time  Long time
	 * @return  yyyy-MM-dd
	 */
	public static String getTimeYMD(Long time) {
		Date date = new Date(time);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(date);
	}
	
	
	
	/**
	 * 将字符串中的"'"替换为"''"--一个单引号替换为两个单引号
	 * 用于数据库中的操作
	 * @param str
	 * @return
	 */
	public static String replaceQuotes(String str){
		if(isStringEmpty(str)){
			return "";
		}
		String strNew = str.replace("'", "''");
		return strNew;
	}
	
	
	
	/**
	 * 对手机号码进行隐藏处理
	 * 
	 * @param phone
	 *            18712345678
	 * @return 187****5678
	 */
	public static String getHiddenPhone(String phone) {
		if (isStringEmpty(phone)) {
			return "";
		}
		String str = phone.replace(" ", "");
		return str.substring(0, 3) + "****" + str.substring(7, str.length());
	}
	
	
	
	/**
	 * 将beforeTime中的秒去掉，只留下  年-月-日 时:分
	 * @param beforeTime  2015-12-05 15:20:00
	 * @return  2015-12-05 15:20
	 */
	public static String subTimeToMinute(String beforeTime){
		if(isStringEmpty(beforeTime)){
			return "";
		}
		int l = beforeTime.length();
		if(beforeTime.length() < 17){
			return beforeTime;
		}
		String strNewTime = beforeTime.substring(0, beforeTime.lastIndexOf(":"));
		return strNewTime;
	}
	
	
	
	/**
	 * 将String类型的星级数量转换为float类型
	 * 
	 * @param strStarLevel
	 * @return
	 */
	public static float processRatingLevel(String strStarLevel) {
		if (UniversalUtils.isStringEmpty(strStarLevel)) {
			return 0;
		}

		int fStarLevel = Integer.parseInt(strStarLevel);

		int iHigh = fStarLevel / 10;
		int iLow = fStarLevel % 10;

		String strNewStarLever = iHigh + "." + iLow;

		LogUtils.i(strNewStarLever);
		
		return Float.parseFloat(strNewStarLever);
	}
	
	
	/**
	 * 将int类型的星级数量转换为float类型
	 * 
	 * @param iStarLevel
	 * @return
	 */
	public static float processRatingLevel(int iStarLevel) {
		if (UniversalUtils.isStringEmpty(iStarLevel+"")) {
			return 0;
		}
		
		int iHigh = iStarLevel / 10;
		int iLow = iStarLevel % 10;
		
		String strNewStarLever = iHigh + "." + iLow;
		
		LogUtils.i(strNewStarLever);
		
		return Float.parseFloat(strNewStarLever);
	}
	
	
	
	
	/**
	 * 解析服务清单数据
	 * @param strServiceInfo
	 * @return
	 */
	public static List<ServiceListInfoEntity> parseServiceList(String strServiceInfo){
		List<ServiceListInfoEntity> infoEntitiesList = new ArrayList<ServiceListInfoEntity>();
		if(UniversalUtils.isStringEmpty(strServiceInfo)){
			return infoEntitiesList;
		}
		String[] serviceList = strServiceInfo.split(";");
		
		for (int i = 0; i < serviceList.length; i++) {
			String serviceItem = serviceList[i];
			String[] serviceContent = serviceItem.split("[|]");
			
			ServiceListInfoEntity infoEntity = new ServiceListInfoEntity();
			
			infoEntity.setServiceName(serviceContent[0]);//服务名称
			infoEntity.setServicePrice(serviceContent[1]);//服务单价
			infoEntity.setServiceUnit(serviceContent[2]);//服务单位
			
			if(serviceContent.length == 4){
				if(isStringEmpty(serviceContent[3])){
					infoEntity.setServiceNum(0);//服务数量
				}
				infoEntity.setServiceNum(Integer.parseInt(serviceContent[3]));//服务数量
			}
			infoEntitiesList.add(infoEntity);
		}
		
		return infoEntitiesList;
	}
	
	
	/**
	 * 组织服务清单数据
	 * @param strName
	 * @param strPrice
	 * @param strUnit
	 * @param iServiceNum
	 * @return
	 */
	public static String appendServiceList(String strName, String strPrice,String strUnit,int iServiceNum){
		String strService = strName+"|"+strPrice+"|"+strUnit+"|"+iServiceNum;
		return strService;
	}
	
	
	
	
	/**
     * 关键字高亮显示
     * 
     * @param target  需要高亮的关键字
     * @param text       需要显示的文字
     * @return spannable 处理完后的结果，记得不要toString()，否则没有效果
     */
    public static SpannableStringBuilder highlight(String text, String target) {
        SpannableStringBuilder spannable = new SpannableStringBuilder(text);
        CharacterStyle span = null;
 
        Pattern p = Pattern.compile(target);
        Matcher m = p.matcher(text);
        while (m.find()) {
            span = new ForegroundColorSpan(Color.RED);// 需要重复！
            spannable.setSpan(span, m.start(), m.end(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }
    
    
    /**
     * 是否存在SD卡
     * @return
     */
    public static boolean isSDCardExists() {
		return Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	
    
    /**
	 * SD卡根目录
	 * @return
	 */
	public static String getSDPath(){
		if(!isSDCardExists()){
			return null;
		}
		return  Environment.getExternalStorageDirectory().getAbsolutePath();
	}
    
	
}
