package com.lhdz.activity.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.util.Log;
import android.widget.Toast;

import com.lhdz.activity.R;
import com.lhdz.util.Define;
import com.lhdz.util.ToastUtils;
import com.lhdz.wechat.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 支付结果回调界面，包名必须使用应用包名+wxapi
 * 
 * @author Administrator
 * 
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.pay_result); 
		
		api = WXAPIFactory.createWXAPI(this, Constants.WX_APP_ID);

		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			Intent intent = new Intent();
			switch (resp.errCode) {
			case 0:
				intent.setAction(Define.BROAD_CAST_RECV_WXPAY);
				intent.putExtra("result", 0);
				sendBroadcast(intent);
				this.finish();
				break;
			case -1:
				intent.setAction(Define.BROAD_CAST_RECV_WXPAY);
				intent.putExtra("result", -1);
				ToastUtils.show(this.getApplicationContext(), "支付失败",0);
				sendBroadcast(intent);
				this.finish();
				break;

			case -2:
				intent.setAction(Define.BROAD_CAST_RECV_WXPAY);
				intent.putExtra("result", -2);
				ToastUtils.show(this.getApplicationContext(), "您取消了支付", 0);
				sendBroadcast(intent);
				this.finish();
				break;
			}
		}

		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

		// if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
		// AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// builder.setTitle(R.string.app_tip);
		// builder.setMessage(getString(R.string.pay_result_callback_msg,
		// resp.errStr + ";code=" + String.valueOf(resp.errCode)));
		// builder.show();
		// }
	}
}