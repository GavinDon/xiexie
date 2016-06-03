package com.lhdz.socketutil;

import java.net.UnknownHostException;

/**
 * 域名解析
 * @author 王哲
 *
 */
public class DNSParsing {
	
	private static String ip;
	public static String getIP(String host){
		try {
			java.net.InetAddress x= java.net.InetAddress.getByName(host);
			//得到字符串形式的ip地址
			ip=x.getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ip;
	}
}
