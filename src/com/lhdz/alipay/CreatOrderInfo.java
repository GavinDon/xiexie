package com.lhdz.alipay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class CreatOrderInfo {
	// 商户PID
	public static final String PARTNER = "2088121001719572";
	// 商户收款账号
	public static final String SELLER = "zhifubao@shxlh.com.cn";
	// 商户私钥，pkcs8格式 --通过私钥生成公钥，上传到服务器。服务器与客户端公钥配对
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANopjoNwYnQkik8r0PFwhEofn/h5wqtybQ9FoIZqQpPQst/Jgcibj9+bOHouKpMu33oGYS7oPq0Bzxnb6R9tgjmIvqNX6G5yMvccgsT93mSjmpVGJxD7XHPPDbh/Uuzm9yaIJu8eJVgVa370gsy3FrXhOmybp46oVDUyrN/IhbWrAgMBAAECgYEA2E4JylKcm1E7fQIRmcGYuLGgd8mp9Tlv3ouHfJ5QwqRTJkVyE8gBn5AUMDgj6Gi6FQ/MPajxs7GueJItEwcN7E7WzgSBvbP7hf1frz6Bpixkx9lCDxkrike22KRvkvTRrhkXjSbLwf51SVvg9F/JQOQSgS9DVThLijw8Pjz3gzkCQQDyu3LXQoVOhco4dM5ybPCEdHbXSsiUgry7YL1Vu+zU/JDZCCbWzHPcrC7hJqjZ6R11waaQl+pjA4CsiFHcn0THAkEA5hZNOtdEUVZL45pOSCYklQuuq6akVaWlVdHE2SG5gxEAitk2qy80u7T4wjZENM6hHhH4GzhINmN30UiQzPRb/QJAZdlnf5FG9ELJLlU8xuCVL07bCs0zSrVs0cbuywmWf2wWrypveZPCTeblqM2a7gyVsvJ1nJhR3gf7NungR42AwwJAKKwukMU25W3s0loXlndvpg0/nkZB7IXwmSAQvRVSFtItl13YZSBeIQRXqPUwz2jrYEZq5lznnv2sU4mRVzmm/QJAGC2w4pjIbMuYvikva2e89rlPTYculv9dnOMCyILI24fKIr9Njz70LKG0uzpMwkAVrOqrhRPAUEv18nPhXe7qvQ==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";

	private CreatOrderInfo() {

	}

	private static CreatOrderInfo creatOrderInfo;

	public static CreatOrderInfo getInstance() {
		if (creatOrderInfo == null) {
			synchronized (CreatOrderInfo.class) {
				if (creatOrderInfo == null) {
					creatOrderInfo = new CreatOrderInfo();
				}

			}

		}
		return creatOrderInfo;

	}

	/**
	 * 组织订单信息
	 * 
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品祥情
	 * @param price
	 *            商品价格
	 * @return
	 */
	public String getOrderInfo(String subject, String body, String price) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
				+ "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	public String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
