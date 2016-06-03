package com.lhdz.domainDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseService {
	private DbOpenHelper mDbHelper;
	private SQLiteDatabase db;

	public DataBaseService(Context context) {
		mDbHelper = new DbOpenHelper(context);
	}

	/*
	 * 用户登录需要的请求信息
	 */
	public void createTableLinkInfo() {
		String sql = "CREATE TABLE IF NOT EXISTS linkInfo (id integer primary key autoincrement,businessType varchar(32),businessId varchar(32),ki varchar(32),dns varchar(32),ip varchar(32), port varchar(32),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}

	//用户服务地址
	public void createTableAddr() {
		String sql = "CREATE TABLE IF NOT EXISTS userAddr (id integer primary key autoincrement, userId varchar(32),areaId varchar(32), Addr varchar(32),longAddr varchar(32),selecState varchar(32),isDelete varchar(32),objTel varchar(32), objName varchar(32),sqn varchar(32),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}

	// 个人信息（包括头像，昵称，签名，地址等）
	public void createtableAuthInfo() {
		String sql = "CREATE TABLE IF NOT EXISTS authInfo (id integer primary key autoincrement,userId varchar(32), accout varchar(32),passWord varchar(32),nickName varchar(32),autoGraph varchar(60),headIcon varchar(32),sex varchar(32),area varchar(32),loginState varchar(32),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}
	
	// 订单列表
	public void createtableOrderList() {
		String sql = "CREATE TABLE IF NOT EXISTS orderList (id integer primary key autoincrement,userId varchar(32),uOrderID varchar(32),iRaceNum varchar(32),iOrderState varchar(32),iServiceType varchar(32),iUsingTimes varchar(32),szOrderValue varchar(60),szOrderStateName varchar(60),szServiceTypeName varchar(60),szHeartPrice varchar(60),szOrderPrice varchar(60),szCompanyName  varchar(60),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}
	
	// 明星公司列表
	public void createtableStarCompanyList() {
		String sql = "CREATE TABLE IF NOT EXISTS starcompany (id integer primary key autoincrement,userId varchar(32),iCompanyID varchar(32), iOrderNum varchar(32),iValuNum varchar(32),iStarLevel varchar(32),iAuthFlag varchar(32),iFiling varchar(32),iOffLine varchar(32),iNominate varchar(32),szName varchar(32),szAddr varchar(60),szServiceInfo varchar(32),szCreateTime varchar(32),szCompanyUrl varchar(60),szCompanyInstr TEXT,iVerifyFlag varchar(32),szVerifyName varchar(32),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}
	
	// 数据库数据版本
	public void createtableDbVerInfo() {
		String sql = "CREATE TABLE IF NOT EXISTS dbVerInfo (id integer primary key autoincrement,uVerType varchar(32),uVersion varchar(32),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}
	
	// 收藏明星公司
	public void createtableCollectCompany() {
		String sql = "CREATE TABLE IF NOT EXISTS collectCompany (id integer primary key autoincrement,userId varchar(32),iCompanyID varchar(32),szName varchar(32),time varchar(32))";
		mDbHelper.getWritableDatabase().execSQL(sql);
		close();
	}
	
	

	/*
	 * 插入少量数据 
	 */

	public boolean insert(String table, ContentValues conValues) {
		boolean flag = false;
		long id = -1;
		db = mDbHelper.getWritableDatabase();
		id = db.insert(table, null, conValues);
		flag = (id != -1 ? true : false);
		close();
		return flag;
	}

	/*
	 * 使用sql语句来插入数据
	 */
	public void insert(String sql) {
		db = mDbHelper.getWritableDatabase();
		db.execSQL(sql);
		close();
	}

	/*
	 * 查询表中所有数据
	 */
	public List<Map<String, String>> query(String sqlState) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		if(mDbHelper == null){
			return list;
		}
		db = mDbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sqlState, null);
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				String colu_name = cursor.getColumnName(i);
				String colu_value = cursor.getString(cursor
						.getColumnIndex(colu_name));
				map.put(colu_name, colu_value);
			}
			list.add(map);
		}
		cursor.close();
		close();
		return list;
	}

	// 删除表中内容
	public void delete(String sql) {
		db = mDbHelper.getWritableDatabase();
		db.execSQL(sql);
		close();
	}

	// 更新内容
	public void update(String sql) {
		db = mDbHelper.getWritableDatabase();
		db.execSQL(sql);
		close();
	}

	public void close() {
		mDbHelper.getWritableDatabase().close();

	}

}
