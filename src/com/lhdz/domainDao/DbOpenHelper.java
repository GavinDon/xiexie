package com.lhdz.domainDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbOpenHelper extends SQLiteOpenHelper {

//	private static final int versionNum = 5;//2015-11-27修改明星公司表，添加字段szCompanyInstr 
//	private static final int versionNum = 6;//2015-11-28修改用户服务地址表，添加字段isDelete 
//	private static final int versionNum = 7;//2015-11-30添加服务器数据库数据版本表dbVerInfo
	private static final int versionNum = 8;//2015-12-01添加收藏明星公司表collectCompany
	
	public static final String DB_NAME = "database.db";
	// 登录表
	String strLinkInfo = "CREATE TABLE IF NOT EXISTS linkInfo (id integer primary key autoincrement,businessType varchar(32),businessId varchar(32),ki varchar(32),dns varchar(32),ip varchar(32), port varchar(32),time varchar(32))";
	// 用户服务地址
	String strUserAddr = "CREATE TABLE IF NOT EXISTS userAddr (id integer primary key autoincrement, userId varchar(32), areaId varchar(32),Addr varchar(32),longAddr varchar(32),selecState varchar(32),isDelete varchar(32),objTel varchar(32), objName varchar(32),sqn varchar(32),time varchar(32))";
	// 帐号信息表
	String strAuthInfo = "CREATE TABLE IF NOT EXISTS authInfo (id integer primary key autoincrement,userId varchar(32), accout varchar(32),passWord varchar(32),nickName varchar(32),autoGraph varchar(60),headIcon varchar(32),sex varchar(32),area varchar(32),loginState varchar(32),time varchar(32))";
	//订单列表
	String strOrderList = "CREATE TABLE IF NOT EXISTS orderList (id integer primary key autoincrement,userId varchar(32),uOrderID varchar(32),iRaceNum varchar(32),iOrderState varchar(32),iServiceType varchar(32),iUsingTimes varchar(32),szOrderValue varchar(60),szOrderStateName varchar(60),szServiceTypeName varchar(60),szHeartPrice varchar(60),szOrderPrice varchar(60),szCompanyName  varchar(60),time varchar(32))";
	//明星公司列表
	String strStarComapnyList = "CREATE TABLE IF NOT EXISTS starcompany (id integer primary key autoincrement,userId varchar(32),iCompanyID varchar(32), iOrderNum varchar(32),iValuNum varchar(32),iStarLevel varchar(32),iAuthFlag varchar(32),iFiling varchar(32),iOffLine varchar(32),iNominate varchar(32),szName varchar(32),szAddr varchar(60),szServiceInfo varchar(32),szCreateTime varchar(32),szCompanyUrl varchar(60),szCompanyInstr TEXT,iVerifyFlag varchar(32),szVerifyName varchar(32),time varchar(32))";
	//数据库数据版本
	String strDbVerInfo = "CREATE TABLE IF NOT EXISTS dbVerInfo (id integer primary key autoincrement,uVerType varchar(32),uVersion varchar(32),time varchar(32))";
	//收藏明星公司
	String strCollectCompany = "CREATE TABLE IF NOT EXISTS collectCompany (id integer primary key autoincrement,userId varchar(32),iCompanyID varchar(32),szName varchar(32),time varchar(32))";
	
	DbOpenHelper(Context context) {
		super(context, DB_NAME, null, versionNum);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(strLinkInfo);
		db.execSQL(strUserAddr);
		db.execSQL(strAuthInfo);
		db.execSQL(strOrderList);
		db.execSQL(strStarComapnyList);
		db.execSQL(strDbVerInfo);
		db.execSQL(strCollectCompany);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS linkInfo");
		db.execSQL("DROP TABLE IF EXISTS userAddr");
		db.execSQL("DROP TABLE IF EXISTS authInfo");
		db.execSQL("DROP TABLE IF EXISTS orderList");
		db.execSQL("DROP TABLE IF EXISTS starcompany");
		db.execSQL("DROP TABLE IF EXISTS dbVerInfo");
		db.execSQL("DROP TABLE IF EXISTS collectCompany");
		onCreate(db);
	}
}
