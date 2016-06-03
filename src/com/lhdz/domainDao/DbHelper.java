package com.lhdz.domainDao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	// 数据库名
	static final String DB_NAME = "www.db";
	// 表名
	public static final String TABLE_NAME = "domain";
	public static final String LOGIN_TABLE_NAME = "loginTable";
	// 域名ID
	static final String ID = "_id";
	// 域名内容
	static final String NAME = "name";
	// 数据库版本号
	private final static int DATABASE_VERSION = 1;

	public DbHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	/*
	 * (non-Javadoc)只在数据库第一次创建的时候调用
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String domainSql = "create table IF NOT EXISTS " + TABLE_NAME + "("
				+ ID + " integer primary key autoincrement," + NAME
				+ " varchar(32))";
		db.execSQL(domainSql);
		Log.i("DbHelper", "excute---------");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
