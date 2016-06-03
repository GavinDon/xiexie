package com.lhdz.wechat;

import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.lhdz.util.LogUtils;
import com.lhdz.util.NetWorkUtil;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class MyWeChatPay {
	private Context context;
	private IWXAPI api;

	private Map<String, String> resultunifiedorder; // 订单参数
	private PayReq req;
	private StringBuffer sb;
	private String orderValue, goodsName, price;// 商品订单号，描述，价格

	/**
	 * 
	 * @param context
	 * @param szOrderValue
	 *            订单号
	 * @param goodsDetail
	 *            商品描述
	 * @param price
	 *            商品价格
	 */
	public MyWeChatPay(Context context, String szOrderValue,
			String goodsName, String price) {
		sb = new StringBuffer();
		this.context = context;
		this.goodsName = goodsName;
		this.orderValue = szOrderValue;
		this.price = price;
		req = new PayReq();
	}

	/*
	 * 给APP注册微信
	 */
	public void registerWxAndPay() {
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(context, null);
	}

	/**
	 * 生成签名
	 */

	@SuppressLint("DefaultLocale")
	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.WX_APP_SECRET);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();// 签名后需要大写
		return packageSign;
	}

	/*
	 * 给订单做二次签名
	 */
	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.WX_APP_SECRET);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		return appSign;
	}

	/*
	 * 把集合中存储的所有订单信息转换成XML文件
	 */
	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		return sb.toString();
	}

	/**
	 * 通过统一下单返回预支付交易会话ID
	 * 
	 */
	public class GetPrepayIdTask extends
			AsyncTask<Void, Void, Map<String, String>> {

		@Override
		protected Map<String, String> doInBackground(Void... params) {
			// 统一下单的接口链接
			String url = String
					.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			// 获取统一下单详情
			String entity = genProductArgs();


			byte[] buf = Util.httpPost(url, entity);

			String content = new String(buf);
			Map<String, String> xml = decodeXml(content);
			return xml;

		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
			LogUtils.i(sb.toString());
			resultunifiedorder = result;
			genPayReq(); // 生成订单
			sendReq();// 发送订单请求

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	/*
	 * 解析 统一订单 返回的内容，主要拿到prepayId(支付交易会话ID)，在给订单签名时需要用到;
	 */
	public Map<String, String> decodeXml(String content) {

		Map<String, String> xml = new HashMap<String, String>();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
		}
		return xml;

	}

	/*
	 * 生成32位随机数
	 */
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	/*
	 * 生成时间戳不带毫秒
	 */
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000))
				.getBytes());
	}

	/*
	 * 生成统一下单xml 文件
	 */
	private String genProductArgs() {
		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid",
					Constants.WX_APP_ID));
			packageParams.add(new BasicNameValuePair("body", goodsName));// 商品描述
			packageParams.add(new BasicNameValuePair("is_subscribe", "Y"));
			packageParams
					.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",
					"http://www.imxiexie.com/download/app_images"));
			packageParams.add(new BasicNameValuePair("out_trade_no",
					genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",
					NetWorkUtil.getInetAddress(context)));// 终端IP
			packageParams.add(new BasicNameValuePair("total_fee", price));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);
			String toIso = new String(xmlstring.toString().getBytes(),
					"ISO8859-1");// 如果中文的话就会出错，转一下码
			return toIso;

		} catch (Exception e) {
			return null;
		}

	}

	/**
	 * 通过统一下单返回prepayId来重新组织订单详情;
	 */
	public void genPayReq() {

		req.appId = Constants.WX_APP_ID;
		req.partnerId = Constants.MCH_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "prepay_id=" + resultunifiedorder.get("prepay_id");
		req.nonceStr = genNonceStr(); // 32随机数
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));// 时间戳

		req.sign = genAppSign(signParams);// 签名

		sb.append("sign\n" + req.sign + "\n\n");


	}

	/*
	 * 注册并调用微信支付
	 */
	public void sendReq() {
		api.registerApp(Constants.WX_APP_ID);
		api.sendReq(req);
	}

}
