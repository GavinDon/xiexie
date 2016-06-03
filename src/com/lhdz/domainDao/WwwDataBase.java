package com.lhdz.domainDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WwwDataBase {
	private DbHelper mDbHelper;
	private SQLiteDatabase db;

	public WwwDataBase(Context context) {
		mDbHelper = new DbHelper(context);
	}

	/*
	 * 查询数据
	 */
	public List<Map<String, String>> query() {
		db = mDbHelper.getWritableDatabase();// 会判断数据库是否存在，不存在则执行oncreat;
		Cursor cursor = db.query(DbHelper.TABLE_NAME, null, null, null, null,
				null, null);
		List<Map<String, String>> dbList = new ArrayList<Map<String,String>>();
		String wwwName = null;
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			wwwName = cursor.getString(cursor.getColumnIndex(DbHelper.NAME));
			map.put("wwwName", wwwName);
			dbList.add(map);
		}
		cursor.close();
		closeDatabase(DbHelper.TABLE_NAME);
		return dbList;
	}

	/*
	 * 插入数据
	 */
	public void insertData(String value) {
		db = mDbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(DbHelper.NAME, value);
		db.insert(DbHelper.TABLE_NAME, null, values);
		closeDatabase(DbHelper.TABLE_NAME);
	}

	/*
	 * 关闭数据库
	 */
	public void closeDatabase(String DatabaseName) {
		mDbHelper.getWritableDatabase().close();

	}

}
