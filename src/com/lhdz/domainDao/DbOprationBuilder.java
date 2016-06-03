package com.lhdz.domainDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCmpVerInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsHomenTypeInfo_Pro;
import com.lhdz.publicMsg.MsgReceiveDef.AuthLoginResp;
import com.lhdz.util.UniversalUtils;

public class DbOprationBuilder {

	public DbOprationBuilder() {

	}

	/**
	 * 查询表中某一个字段对应的值-n
	 * 
	 * @param item
	 *            查询的某一项
	 * @param table
	 *            表名
	 */
	public static String queryBuilder(String item, String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("select" + "\t").append(item + "\t").append("from" + "\t")
				.append(table);
		System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * 通过条件来筛选特定的值
	 * 
	 * @param item
	 *            需要查询的字段
	 * @param table
	 *            表名
	 * @param fieldName
	 *            条件字段名
	 * @param Value
	 *            条件值
	 * @return
	 */
	public static String queryBuilderby(String item, String table,
			String fieldName, String Value) {
		StringBuilder sb = new StringBuilder();
		sb.append("select" + "\t").append(item + "\t").append("from" + "\t")
				.append(table + "\t").append("where" + "\t")
				.append(fieldName + "\t").append("=" + "\t").append("'"+Value+"'");

		return sb.toString();

	}

	
	/**
	 * 查询表中所有字段对应的值
	 * 
	 * @param table
	 *            表名
	 * 
	 */
	public static String queryAllBuilder(String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("select"+"\t").append("*"+"\t").append("from"+"\t").append(table);
		return sb.toString();
	}
	
	

	/**
	 * 插入一个字段对应的值
	 * 
	 * @param table
	 *            表名
	 * @param filedName
	 *            字段名
	 * @param value
	 *            字段对应需要插入的值
	 * @return stringBuilder
	 */

	public static String insertBuider(String table, String filedName,
			String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("insert" + "\t").append("into" + "\t").append(table + "\t")
				.append("(" + filedName + ")" + "\t").append("values")
				.append("(" + value + ")");
		return sb.toString();
	}

	/**
	 * 更新表内容
	 * 
	 * @param table
	 * @param item
	 *            需要修改的字段
	 * @param value
	 *            修改的值
	 * @param filedName
	 *            条件字段
	 * @param filedValue
	 *            条件字段值
	 * @return
	 */
	public static String updateBuider(String table, String item,
			String value, String filedName, String filedValue) {
		StringBuilder sb = new StringBuilder();
		sb.append("update" + "\t").append(table + "\t").append("set" + "\t")
				.append(item + "\t").append("=" + "\t").append(value + "\t")
				.append("where" + "\t").append(filedName + "\t")
				.append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(filedValue)+"'");
		return sb.toString();

	}
	
	/**
	 * 更新表内容
	 * 
	 * @param table
	 * @param item
	 *            需要修改的字段
	 * @param value
	 *            修改的值
	 * @param filedName
	 *            条件字段
	 * @param filedValue
	 *            条件字段值
	 * @return
	 */
	public static String updateBuider(String table, String item,String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("update" + "\t").append(table + "\t").append("set" + "\t")
		.append(item + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(value) +"'"+ "\t");
		return sb.toString();
		
	}
	
	
	
	/**
	 * 删除表中所有数据
	 * 
	 * @param table
	 *            表名
	 */
	public static String deleteBuilder(String table) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete" + "\t").append("from" + "\t").append(table);
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	
	/**
	 * 通过条件来筛选特定的值
	 * 
	 * @param item
	 *            需要查询的字段
	 * @param table
	 *            表名
	 * @param fieldName
	 *            条件字段名
	 * @param Value
	 *            条件值
	 * @return
	 */
	public static String deleteBuilderby( String table,
			String fieldName, String Value) {
		StringBuilder sb = new StringBuilder();
		sb.append("delete" + "\t").append("from" + "\t")
				.append(table + "\t").append("where" + "\t")
				.append(fieldName + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(Value)+"'");

		return sb.toString();

	}
	
	
	
	
	
	/**
	 * 排序查询数据--首页快捷下单的排序查询，按ListSqn升序，id升序查询
	 * @param item
	 * @param table
	 * @param fieldName
	 * @return
	 */
	public static String queryBuilderAppHomeOrderDesc() {
		StringBuilder sb = new StringBuilder();
//		sb.append("select" + "\t").append("*" + "\t").append("from" + "\t")
//		.append("coreTable" + "\t").append("ORDER" + "\t" + "BY" + "\t")
//		.append(fieldName + "\t").append("DESC" + "\t,").append("Id" + "\t").append("DESC");
		sb.append("select * from coreTable order by ListSqn asc , Id asc");
		return sb.toString();
		
	}
	
	
	
	/**
	 * 更新明星公司表--更新用户加入明星公司的状态
	 * @param iVerifyFlag	审核状态标识
	 * @param szVerifyName	审核状态名称
	 * @param iCompanyID	待更新的公司id
	 * @return
	 */
	 
	public static String updateStarCompanyBuider(String iVerifyFlag,
			String szVerifyName, String iCompanyID) {
		StringBuilder sb = new StringBuilder();
		sb.append("update" + "\t").append("starcompany" + "\t").append("set" + "\t")
				.append("iVerifyFlag" + "\t").append("=" + "\t").append("'"+iVerifyFlag+"'" + "\t")
				.append(", \t szVerifyName" + "\t").append("=" + "\t").append("'"+szVerifyName+"'"+ "\t")
				.append("where" + "\t").append("iCompanyID" + "\t").append("=" + "\t").append("'"+iCompanyID+"'");
		return sb.toString();

	}
	
	
	

	/**
	 * 查询表中所有字段对应的值--用于明星公司表中，所有字段不等于给定值的数据
	 * 
	 * @param table
	 *            表名
	 * 
	 */
	public static String queryStarCompanyBuilderNot() {
		StringBuilder sb = new StringBuilder();
		sb.append("select"+"\t").append("*"+"\t").append("from"+"\t").append("starcompany"+ "\t")
		.append("where"+"\t").append("iVerifyFlag"+"\t").append("is"+"\t"+"not"+"\t").append("200"+"\t")
		.append("and"+"\t").append("iVerifyFlag"+"\t").append("is"+"\t"+"not"+"\t").append("210"+"\t")
		.append("and"+"\t").append("iVerifyFlag"+"\t").append("is"+"\t"+"not"+"\t").append("220"+"\t");
		return sb.toString();
	}
	
	
	/**
	 * 明星公司列表 插入字段
	 * @param companyList
	 * @return
	 */
	public static String insertStarCompanyAllBuilder(int userid,HsCompanyInfo_Pro companyList){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into starcompany (userId,iCompanyID,iOrderNum,iValuNum,iStarLevel,iAuthFlag,iFiling,iOffLine,iNominate" +
				",szName,szAddr,szServiceInfo,szCreateTime,szCompanyUrl,szCompanyInstr,time) values (")
		
		.append(userid+",") //userid
		.append(companyList.getICompanyID()+",") //公司id
		.append(companyList.getIOrderNum()+",") //公司成功订单数
		.append(companyList.getIValuNum()+",") //雇主评价条数
		.append(companyList.getIStarLevel()+",") //公司星级
		.append(companyList.getIAuthFlag()+",") //身份认证标示
		.append(companyList.getIFiling()+",") 	//线下备案标示
		.append(companyList.getIOffLine()+",") //线下验证
		.append(companyList.getINominate()+",") //官方推荐
		.append("'"+UniversalUtils.replaceQuotes(companyList.getSzName())+"'"+",") //公司名称
		.append("'"+UniversalUtils.replaceQuotes(companyList.getSzAddr())+"'"+",") //公司地址
		.append("'"+UniversalUtils.replaceQuotes(companyList.getSzServiceInfo())+"'"+",") //服务信息（服务类型）
		.append("'"+UniversalUtils.replaceQuotes(companyList.getSzCreateTime())+"'"+",") //公司创建时间
		.append("'"+UniversalUtils.replaceQuotes(companyList.getSzCompanyUrl())+"'"+",") //公司URL
		.append("'"+UniversalUtils.replaceQuotes(companyList.getSzCompanyInstr())+"'"+",") //公司简介
		.append("'"+System.currentTimeMillis()+"'") //系统时间
		.append(")");
		
		return sb.toString();
	}
	
	
	/**
	 * 明星公司列表 插入字段
	 * @param startCompanyMap公司信息的map
	 * @return
	 */
	public static String insertStarCompanyAllBuilder(int userid,Map<String, String> startCompanyMap){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into starcompany (userId,iCompanyID,iOrderNum,iValuNum,iStarLevel,iAuthFlag,iFiling,iOffLine,iNominate" +
				",szName,szAddr,szServiceInfo,szCreateTime,szCompanyUrl,szCompanyInstr,time) values (")
				
				.append(userid+",") //userid
				.append("'"+startCompanyMap.get("iCompanyID")+"'"+",") //公司id
				.append("'"+startCompanyMap.get("iOrderNum")+"'"+",") //公司成功订单数
				.append("'"+startCompanyMap.get("iValuNum")+"'"+",") //雇主评价条数
				.append("'"+startCompanyMap.get("iStarLevel")+"'"+",") //公司星级
				.append("'"+startCompanyMap.get("iAuthFlag")+"'"+",") //身份认证标示
				.append("'"+startCompanyMap.get("iFiling")+"'"+",") 	//线下备案标示
				.append("'"+startCompanyMap.get("iOffLine")+"'"+",") //线下验证
				.append("'"+startCompanyMap.get("iNominate")+"'"+",") //官方推荐
				.append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szName"))+"'"+",") //公司名称
				.append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szAddr"))+"'"+",") //公司地址
				.append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szServiceInfo"))+"'"+",") //服务信息（服务类型）
				.append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szCreateTime"))+"'"+",") //公司创建时间
				.append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szCompanyUrl"))+"'"+",") //公司URL
				.append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szCompanyInstr"))+"'"+",") //公司简介
				.append("'"+System.currentTimeMillis()+"'") //系统时间
				.append(")");
		
		return sb.toString();
	}
	
	
	/**
	 * 明星公司列表 插入字段
	 * @param startCompanyMap公司信息的map
	 * @return
	 */
	public static String updateStarCompanyAllBuilder(Map<String, String> startCompanyMap, String iCompanyID){
		StringBuilder sb = new StringBuilder();
		sb.append("update starcompany set" + "\t")
				
				.append("iCompanyID" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iCompanyID")+"'" + "\t")
				.append(", \t iOrderNum" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iOrderNum")+"'"+ "\t")
				.append(", \t iValuNum" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iValuNum")+"'"+ "\t")
				.append(", \t iStarLevel" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iStarLevel")+"'"+ "\t")
				.append(", \t iAuthFlag" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iAuthFlag")+"'"+ "\t")
				.append(", \t iFiling" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iFiling")+"'"+ "\t")
				.append(", \t iOffLine" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iOffLine")+"'"+ "\t")
				.append(", \t iNominate" + "\t").append("=" + "\t").append("'"+startCompanyMap.get("iNominate")+"'"+ "\t")
				.append(", \t szName" + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szName"))+"'"+ "\t")
				.append(", \t szAddr" + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szAddr"))+"'"+ "\t")
				.append(", \t szServiceInfo" + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szServiceInfo"))+"'"+ "\t")
				.append(", \t szCreateTime" + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szCreateTime"))+"'"+ "\t")
				.append(", \t szCompanyUrl" + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szCompanyUrl"))+"'"+ "\t")
				.append(", \t szCompanyInstr" + "\t").append("=" + "\t").append("'"+UniversalUtils.replaceQuotes(startCompanyMap.get("szCompanyInstr"))+"'"+ "\t")
				.append(", \t time" + "\t").append("=" + "\t").append("'"+System.currentTimeMillis()+"'"+ "\t")
				
				.append("where" + "\t").append("iCompanyID" + "\t").append("=" + "\t").append("'"+iCompanyID+"'");
				
		
		return sb.toString();
	}
	
	
	
	/**
	 * 首页快捷下单数据  插入所有字段
	 * @param companyList
	 * @return
	 */
	public static String insertAppHomeAllBuilder(HsHomenTypeInfo_Pro appHomeList){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into coreTable (Parentid,Sonid,SonName,ParentName,ListSqn,SubSketch,SubRmark,CreateTime) values (")
		
		.append(appHomeList.getIParentID()+",") //大种类id
		.append(appHomeList.getISonID()+",") //子种类id
		.append("'"+UniversalUtils.replaceQuotes(appHomeList.getSzSonName())+"'"+",") //子种类名称
		.append("'"+UniversalUtils.replaceQuotes(appHomeList.getSzParentName())+"'"+",") //大种类名称
		.append(appHomeList.getIListSqn()+",") //显示位置
		.append("'"+UniversalUtils.replaceQuotes(appHomeList.getSzSubSketch())+"'"+",") 	//子种类简述
		.append("'"+UniversalUtils.replaceQuotes(appHomeList.getSzRmark())+"'"+",") //服务功能简介
		.append("'"+System.currentTimeMillis()+"'") //更新时间
		
		.append(")");
		
		return sb.toString();
	}
	
	
	/**
	 * 用户基本信息  插入所有字段
	 * @param companyList
	 * @return
	 */
	public static String insertUserInfoAllBuilder(AuthLoginResp loginResp,String pwd,int loginState){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into authInfo (userId,accout,passWord,nickName,autoGraph,headIcon,sex,area,loginState,time) values (")
		
		.append(loginResp.iuserid+",") //userID
		.append("'"+UniversalUtils.replaceQuotes(loginResp.szUserID)+"'"+",") //用户id
		.append("'"+pwd+"'"+",") //密码（经过md5后的密码）
		.append("'"+UniversalUtils.replaceQuotes(loginResp.szUserNick)+"'"+",") //昵称
		.append("'"+UniversalUtils.replaceQuotes(loginResp.szSignaTure)+"'"+",") //签名
		.append("'"+loginResp.iHeadPic+"'"+",") 	//头像
		.append("'"+UniversalUtils.judgeUserSex(loginResp.eSex)+"'"+",") //性别
		.append(loginResp.iAreaID+",") //地区
		.append(loginState+",") //登录状态
		.append(System.currentTimeMillis()) //登录状态
		
		.append(")");
		
		return sb.toString();
	}
	
	
	/**
	 * 用户服务地址信息  插入所有字段
	 * @param companyList
	 * @return
	 */
	public static String insertUserServiceAddAllBuilder(int userId,int areaId, String Addr,String longAddr,int selecState,int isDelete, String objTel,String objName,int sqn){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into userAddr (userId,areaId,Addr,longAddr,selecState,isDelete,objTel,objName,sqn,time) values (")
		
		.append(userId+",") //userID
		.append(areaId+",")//地址区域id
		.append("'"+UniversalUtils.replaceQuotes(Addr)+"'"+",") //地址
		.append("'"+UniversalUtils.replaceQuotes(longAddr)+"'"+",")//详细地址
		.append(selecState+",") //选中状态
		.append(isDelete+",") //是否删除
		.append("'"+UniversalUtils.replaceQuotes(objTel)+"'"+",") //电话
		.append("'"+UniversalUtils.replaceQuotes(objName)+"'"+",") //姓名
		.append(sqn+",") 	//位置
		.append(System.currentTimeMillis())//地址添加时间
		.append(")");
		
		return sb.toString();
	}
	
	
	
	/**
	 * 订单列表  插入所有字段
	 * @param orderList
	 * @return
	 */
	public static String insertOrderListAllBuilder(int userId ,HsUserOrderInfo_Pro orderInfo){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into orderList (userId,uOrderID,iRaceNum,iOrderState,iServiceType,iUsingTimes,szOrderValue,szOrderStateName," +
				"szServiceTypeName,szHeartPrice,szOrderPrice,szCompanyName,time) values (")
		
		.append(userId+",") //userID
		.append(orderInfo.getUOrderID()+",") //订单id（订单唯一标识）
		.append(orderInfo.getIRaceNum()+",") //竟单数量
		.append(orderInfo.getIOrderState()+",") //订单状态标识
		.append(orderInfo.getIServiceType()+",") //服务类型
		.append(orderInfo.getIUsingTimes()+",") //服务时长
		.append("'"+orderInfo.getSzOrderValue()+"'"+",") //订单号
		.append("'"+orderInfo.getSzOrderStateName()+"'"+",") //订单状态名称
		.append("'"+orderInfo.getSzServiceTypeName()+"'"+",") //服务类型名
		.append("'"+orderInfo.getSzHeartPrice()+"'"+",") //心理价位
		.append("'"+orderInfo.getSzOrderPrice()+"'"+",") //订单金额
		.append("'"+UniversalUtils.replaceQuotes(orderInfo.getSzCompanyName())+"'"+",") //服务公司名称
		.append(System.currentTimeMillis())//添加时间
		.append(")");
		
		return sb.toString();
	}
	
	
	
	/**
	 * 数据库数据版本  插入所有字段
	 * @param companyList
	 * @return
	 */
	public static String insertDbVerInfoAllBuilder(int uVerType, int uVersion){
		StringBuilder sb = new StringBuilder();
		sb.append("insert into dbVerInfo (uVerType,uVersion,time) values (")
		
		.append(uVerType+",") //版本类型
		.append(uVersion+",") //版本号
		.append(System.currentTimeMillis()) //时间
		
		.append(")");
		
		return sb.toString();
	}
	
	
	
	/**
	 * 收藏明星公司 插入字段
	 * @return
	 */
	public static String insertCollectCompanyAllBuilder(int userid,Map<String, String> starCompanyDetail){
		StringBuilder sb = new StringBuilder();
		String s = "userId varchar(32),iCompanyID varchar(32),szName varchar(32),time varchar(32))";
		sb.append("insert into collectCompany (userId,iCompanyID,szName,time) values (")
		
		.append(userid+",") //userid
		.append("'"+starCompanyDetail.get("iCompanyID")+"'"+",") //公司id
		.append("'"+starCompanyDetail.get("szName")+"'"+",") //公司名称
		.append("'"+System.currentTimeMillis()+"'") //系统时间
		.append(")");
		
		return sb.toString();
	}
	
	
	
	/**
	 * 查询收藏的明星公司数据
	 * 从明星公司starcompany表中查询iCompanyID在collectCompany表中存在的所有数据
	 * @return
	 */
	public static String queryCollectFromStarCompanyAllBuilder(int userID){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from starcompany where iCompanyID in (select iCompanyID from collectCompany where userId = ")
			.append(userID+")");
//		String sql = "select * from starcompany where iCompanyID in (select iCompanyID from collectCompany where userId = )";
		return sb.toString();
	}
	
	
	
	
	/**
	 * 查询收藏的明星公司数据--根据icompany和userID
	 * @return
	 */
	public static String queryCollectByCompanyIdAndUserId(int iCompanyId ,int userId){
		StringBuilder sb = new StringBuilder();
		sb.append("select"+"\t").append("*"+"\t").append("from"+"\t").append("collectCompany"+ "\t")
		.append("where"+"\t").append("iCompanyID"+"\t").append("="+"\t").append(iCompanyId+"\t")
		.append("and"+"\t").append("userId"+"\t").append("="+"\t").append(userId);
		return sb.toString();
	}
	
	
	
	
	


}
