package com.lhdz.wechat;

import com.umeng.socialize.media.UMVideo;

public class Constants {

	// 微信APP_ID
	public static final String WX_APP_ID = "wx7b43b4d5f3901b61";
	// 微信支付商户号
	public static final String MCH_ID = "1303240501";
	// 新浪app_key
	public static final String SINA_KEY = "4246797029";
	// 新浪app_secret
	public static final String SINA_SECRET = "275748d3248f3fa13e265e6f273fa559";

	// 微信API密钥，在商户平台设置，后续两次签名也用的是这个密钥
	public static final String WX_APP_SECRET = "8085a2071de4304d8967abac2761e1e9";
	
	public static final String WX_APP_SIGN="";

	/**
	 * 分享平台 中需要分享的参数
	 * 
	 */
	public static class shareContent {
		// 分享的视频
		public static final UMVideo video = new UMVideo(
				"http://video.sina.com.cn/p/sports/cba/v/2013-10-22/144463050817.html");
		// 分享的链接
		public static final String TargetUrl = "http://www.imxiexie.com";
		// 标题的图片
//		public static final String UMImage = "http://pic.pptbz.com/pptpic/201511/2015111950871897.jpg";
		public static final String UMImage = "http://www.imxiexie.com/download/wxfx.jpg";
	}
}
