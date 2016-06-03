package com.lhdz.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetWorkUtil {
	// private Context context;

	// /**
	// * �ж����繤����
	// *
	// * @param context
	// */
	// public NetWorkUtil(Context context) {
	// this.context = context;
	// }
	//
	// public NetWorkUtil(Context context, int type) {
	// this.context = context;
	// }
	//
	// public boolean isNetworkConnected() {
	// if (context != null) {
	// ConnectivityManager mConnectivityManager = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo mNetworkInfo = mConnectivityManager
	// .getActiveNetworkInfo();
	// if (mNetworkInfo != null) {
	// return mNetworkInfo.isAvailable();
	// } else {
	//
	// }
	//
	// }
	// return false;
	// }

	/*
	 * 判断是3G否有网络连接
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/*
	 * 判断是否有wifi连接
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null && mWiFiNetworkInfo.isConnected()) {
//				return mWiFiNetworkInfo.isAvailable();
				return true;
			}
		}
		return false;
	}

	
	/*
	 * 判断是否有mobile网络连接
	 */
	public static boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null && mMobileNetworkInfo.isConnected()) {
//				return mMobileNetworkInfo.isAvailable();
				return true;
			}
		}
		return false;
	}

	
	/*
	 * 获取网络类型
	 */
	public static int getConnectedType(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
				return mNetworkInfo.getType();
			}
		}
		return -1;
	}
	/**
	 * 获取IP地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getInetAddress(Context context) {
		// 获取wifi服务
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 判断wifi是否开启
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();
		String ip = intToIp(ipAddress);
		return ip;
	}

	private static String intToIp(int i) {

		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

}
