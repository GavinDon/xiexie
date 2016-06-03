package com.lhdz.activity;

/**
 * 订单支付
 * @author 王哲
 * @date 2015-8-26
 */
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.lhdz.alipay.CreatOrderInfo;
import com.lhdz.alipay.PayResult;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsUserSurePay_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wechat.MyWeChatPay;

public class OrderActivity extends BaseActivity implements OnClickListener,
		OnCheckedChangeListener {

	private TextView title;
	private LinearLayout zfb, wx, yhk;
	private ImageView iv1, iv2, iv3;
	private RadioGroup group;
	private Button btSure;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;

	private static final int BTN_TIMER_OVER = 5;
	private static final int MSG_LOAD_SUCCESS = 6;
	private static final int MSG_LOAD_ERROR = 7;

	private TextView order_value, order_state, order_price;

	private CreatOrderInfo creatOrderInfo = CreatOrderInfo.getInstance();
	private MyApplication myApplication = null;

	// intent传值
	private String goodsName = "";// 商品名称
	private String goodsDetail = "";// 商品描述
	private String price = "";// 商品价格

	private int uOrderID;// 订单orderId
	private String szOrderValue = "";// 订单编号
	private String szOrderStateName = "";// 订单状态

	// private HsUserOrderDailInfo_Pro orderDetailInfo = null;

	private int seqOrderPay = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order);
		myApplication = (MyApplication) getApplication();

		goodsName = getIntent().getStringExtra("goodsname");// 商品名称
		goodsDetail = getIntent().getStringExtra("goodsdetil");// 商品描述
		price = getIntent().getStringExtra("price");// 商品单价

		uOrderID = getIntent().getIntExtra("uOrderID", -1);// orderId
		szOrderValue = getIntent().getStringExtra("szOrderValue");// 订单编号
		szOrderStateName = getIntent().getStringExtra("szOrderStateName");// 订单状态

		// orderDetailInfo = (HsUserOrderDailInfo_Pro)
		// getIntent().getSerializableExtra("orderDetailInfo");//订单详情

		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		filter.addAction(Define.BROAD_CAST_RECV_WXPAY);
		registerReceiver(mReceiver, filter);

		initViews();
		listenCenter();
		backArrow();

	}

	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("订单支付");

		order_value = (TextView) findViewById(R.id.order_value);
		order_state = (TextView) findViewById(R.id.order_state);
		order_price = (TextView) findViewById(R.id.order_price);

		order_value.setText(szOrderValue);// 订单编号
		order_state.setText(szOrderStateName);// 订单状态
		order_price.setText(price);// 订单金额

		zfb = (LinearLayout) findViewById(R.id.lay_zhifubao);
		yhk = (LinearLayout) findViewById(R.id.lay_yinhang);
		wx = (LinearLayout) findViewById(R.id.lay_weixin);
		iv1 = (ImageView) findViewById(R.id.iv1);
		iv2 = (ImageView) findViewById(R.id.iv2);
		iv3 = (ImageView) findViewById(R.id.iv3);
		group = (RadioGroup) findViewById(R.id.ordergroup);
		btSure = (Button) findViewById(R.id.bt_sure);

	}

	/**
	 * 控件监听
	 */
	private void listenCenter() {
		zfb.setOnClickListener(this);
		yhk.setOnClickListener(this);
		wx.setOnClickListener(this);
		group.setOnCheckedChangeListener(this);
		group.check(R.id.lay_zhifubao);
		btSure.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.lay_zhifubao:
			group.check(R.id.lay_zhifubao);
			break;
		case R.id.lay_yinhang:
			group.check(R.id.lay_yinhang);
			break;
		case R.id.lay_weixin:
			group.check(R.id.lay_weixin);
			break;
		case R.id.bt_sure:
			// 付款流程：首先给支付宝端发送支付请求，当支付宝端支付成功之后，发送确认付款请求（用户同意付款请求），若
			// 该请求返回成功，则支付完成，关闭支付页面；若该请求返回失败，则支付失败，后续操作待定

			// 当前选中的支付方式id
			int checkId = group.getCheckedRadioButtonId();

			if (checkId == R.id.lay_zhifubao) {
				// 设置按钮为不可点击
				btSure.setClickable(false);
				btSure.setBackgroundResource(R.drawable.shape_btn_click_not);
				mHandler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);

				pay(btSure);// 调用支付
			} else if (checkId == R.id.lay_yinhang) {
				
				ToastUtils.show(this, "敬请期待", 0);
				
			} else if (checkId == R.id.lay_weixin) {
				// 设置按钮为不可点击
				btSure.setClickable(false);
				btSure.setBackgroundResource(R.drawable.shape_btn_click_not);
				mHandler.postDelayed(btnTimerRunnable, 1000*3);
				
				int wxPrice = (int) (Float.parseFloat(price) * 100);// 微信支付单位为分。所以在此处需要转换
				MyWeChatPay weChatPay = new MyWeChatPay(this, szOrderValue,goodsName, String.valueOf(wxPrice));
				weChatPay.registerWxAndPay();// 注册微信
				weChatPay.new GetPrepayIdTask().execute();// 生成预支付ID;
			}

			break;
		default:

			break;
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (zfb.getId() == checkedId) {
			iv1.setImageResource(R.drawable.select_green);
		} else {
			iv1.setImageResource(R.drawable.select_gray);
		}
		if (yhk.getId() == checkedId) {
			iv2.setImageResource(R.drawable.select_green);
		} else {
			iv2.setImageResource(R.drawable.select_gray);
		}
		if (wx.getId() == checkedId) {
			iv3.setImageResource(R.drawable.select_green);
		} else {
			iv3.setImageResource(R.drawable.select_gray);
		}

	}

	/**
	 *  用于对按钮不可点击进行计时
	 */
	Runnable btnTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(BTN_TIMER_OVER);
		}
	};

	
	/**
	 * 返回按钮的定义与监听
	 */
	public void backArrow() {
		TextView back;
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.public_back:// 后退键
					OrderActivity.this.finish();

					break;
				}
			}
		});
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					ToastUtils.show(OrderActivity.this, "支付成功",
							Toast.LENGTH_SHORT);

					loadData();

				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						ToastUtils.show(OrderActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT);

					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						ToastUtils.show(OrderActivity.this, "支付失败",
								Toast.LENGTH_SHORT);

						btSure.setClickable(true);
						btSure.setBackgroundResource(R.drawable.selector_oppointment);
						mHandler.removeCallbacks(btnTimerRunnable);
					}
				}
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(OrderActivity.this, "检查结果为：" + msg.obj,

				Toast.LENGTH_SHORT).show();
				break;
			}
			case BTN_TIMER_OVER:
				// 设置按钮为可点击
				btSure.setClickable(true);
				btSure.setBackgroundResource(R.drawable.selector_oppointment);
				mHandler.removeCallbacks(btnTimerRunnable);
				break;
			case MSG_LOAD_ERROR:
				// 设置按钮为可点击
				btSure.setClickable(true);
				btSure.setBackgroundResource(R.drawable.selector_oppointment);
				String errorResult = (String) msg.obj;
				ToastUtils.show(OrderActivity.this, errorResult,
						Toast.LENGTH_SHORT);
				break;
			case MSG_LOAD_SUCCESS:
				// 设置按钮为可点击
				btSure.setClickable(true);
				btSure.setBackgroundResource(R.drawable.selector_oppointment);
				finish();
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 调用支付宝支付接口
	 */
	public void pay(View v) {

		// 订单 需要传商品名，商品描述，商品价格
		String orderInfo = creatOrderInfo.getOrderInfo(goodsName, goodsDetail,
				price);

		// String orderInfo = creatOrderInfo.getOrderInfo("测试的商品", "该测试商品的详细描述",
		// "0.01");

		// 对订单做RSA 签名
		String sign = creatOrderInfo.sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ creatOrderInfo.getSignType();
		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(OrderActivity.this);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo);
				Log.i("result", result + "===");
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * 查询终端设备是否存在支付宝认证账户
	 * 
	 */
	public void check(View v) {
		Runnable checkRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask payTask = new PayTask(OrderActivity.this);
				// 调用查询接口，获取查询结果
				boolean isExist = payTask.checkAccountIfExist();
				Message msg = new Message();
				msg.what = SDK_CHECK_FLAG;
				msg.obj = isExist;
				mHandler.sendMessage(msg);
			}
		};

		Thread checkThread = new Thread(checkRunnable);
		checkThread.start();

	}

	/**
	 * 用户确认付款
	 */
	public void loadData() {
		seqOrderPay = MyApplication.SequenceNo++;
		HsUserSurePay_Req surePay_Req = new MsgInncDef().new HsUserSurePay_Req();
		surePay_Req.uUserID = myApplication.userId;// 用户id
		surePay_Req.iOrderID = uOrderID;// 订单id
		surePay_Req.szPrice = price;// 付款单价
		byte[] connData = HandleNetSendMsg.HandleHsUserSurePay_ReqToPro(
				surePay_Req, seqOrderPay);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("用户确认付款请求数据--sequence=" + seqOrderPay + "/"
				+ Arrays.toString(connData) + "=============");

	}

	
	/**
	 * 广播接收者
	 */
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME,
						-1);

				if (seqOrderPay == iSequence) {
					processOrderPayData(recvTime);
				}

			}
			/*
			 * 接收微信支付返回的结果0 成功，-1 失败，-2 用户取消
			 */
			if (Define.BROAD_CAST_RECV_WXPAY.equals(intent.getAction())) {
				int resultCold = intent.getExtras().getInt("result");
				if (resultCold == 0) {
					loadData();// 发送支付成功请求
				} else {
					btSure.setClickable(true);
					btSure.setBackgroundResource(R.drawable.selector_oppointment);
					mHandler.removeCallbacks(btnTimerRunnable);
				}
			}
		}
	};

	/**
	 *  处理支付响应数据
	 * @param recvTime
	 */
	private void processOrderPayData(long recvTime) {
		HsNetCommon_Resp common_Resp = (HsNetCommon_Resp) HandleMsgDistribute
				.getInstance().queryCompleteMsg(recvTime);
		if (common_Resp == null) {
			return;
		}
		LogUtils.i("确认付款返回数据成功");
		mHandler.removeCallbacks(btnTimerRunnable);
		if (common_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {

			int iUserId = common_Resp.iUserID;
			int iCompanyId = common_Resp.iSelectID;

			LogUtils.i("付款成功" + "iUserId = " + iUserId + ";iCompanyId = "
					+ iCompanyId);

			Message message = new Message();
			message.what = MSG_LOAD_SUCCESS;
			mHandler.sendMessage(message);

		} else {
			String result = UniversalUtils
					.judgeNetResult_Hs(common_Resp.eOperResult);
			LogUtils.i("失败" + result + "=============");

			Message message = new Message();
			message.what = MSG_LOAD_ERROR;
			message.obj = result;
			mHandler.sendMessage(message);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

}
