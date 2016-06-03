package com.lhdz.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoreDbHelper {
	private Context context;
	private CoreDbManager coreManager;
	private SQLiteDatabase db;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	public CoreDbHelper(Context context) {
		this.context = context;
		coreManager = new CoreDbManager(context);// 实例化coreManager对象主要传递一个context;
	}

	/*
	 * 得到对应表中的数据
	 */
	public List<Map<String, String>> queryCoredata(String sqlStatement) {
		db = coreManager.getDatabase();// 通过coreDbManager来创建并打开数据库
//		String sql = "select*from starcompanytable";// 查询表中所有数据测试用
		Cursor cursor = db.rawQuery(sqlStatement, null);
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				String columns_name = cursor.getColumnName(i);// 数据库中的每一列的名字;
				String columns_value = cursor.getString(cursor
						.getColumnIndex(columns_name));// 列对应的值;
				map.put(columns_name, columns_value);
			}
			list.add(map);
		}
		cursor.close();
		db.close();
		return list;
	}

	/*
	 * 插入数据
	 */
	public void insertCoreData(String sql) {
		db=coreManager.getDatabase();
		String test="insert into starcompanytable values(0,1,1,1,'xiaoming','01005')";
		db.execSQL(sql);
		db.close();

	}

	/*
	 * 更新数据
	 */
	public void updateData() {

	}

	/*
	 * 删除数据
	 */
	public void deleteData(String sql) {
		db=coreManager.getDatabase();
		db.execSQL(sql);
		db.close();

	}
}
