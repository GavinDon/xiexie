package com.lhdz.util;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import android.util.Log;

import com.lhdz.activity.RegisteredActivity;
import com.lhdz.publicMsg.MyApplication;

public class RegistAccoutUtil {

	private static String Url = "http://106.ihuyi.cn/webservice/sms.php?method=Submit";

	public static void sendSMS(String moblile) {
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(Url);

		client.getParams().setContentCharset("UTF-8");
		method.setRequestHeader("ContentType",
				"application/x-www-form-urlencoded;charset=UTF-8");

		int mobile_code = (int) ((Math.random() * 9 + 1) * 100000);
		MyApplication.vertifacationCode = mobile_code;

		String content = new String("您的验证码是：" + mobile_code + "。请不要把验证码泄露给其他人。如非本人操作，可不用理会！");
		LogUtils.i("手机验证码："+mobile_code);
		NameValuePair[] data = {// 提交短信
				new NameValuePair("account", "cf_lhdz"),
				// new NameValuePair("password", "8PenRA"), //
				// 密码可以使用明文密码或使用32位MD5加密
				new NameValuePair("password", StringUtil.MD5Encode("893850")),
				new NameValuePair("mobile", moblile),
				new NameValuePair("content", content), };

		method.setRequestBody(data);

		try {
			client.executeMethod(method);

			String SubmitResult = method.getResponseBodyAsString();// 返回xml结构体
			Document doc = DocumentHelper.parseText(SubmitResult);
			Element root = doc.getRootElement();

			String code = root.elementText("code");
			String msg = root.elementText("msg");
			String smsid = root.elementText("smsid");

			System.out.println(code);
			System.out.println(msg);
			System.out.println(smsid);

			if ("2".equals(code)) {
				Log.i("短信是否发送成功", "短信提交成功");
			}

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
}
