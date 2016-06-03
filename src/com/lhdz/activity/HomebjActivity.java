package com.lhdz.activity;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.fragment.FragmentController;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsBroadCastOrderInfo_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsBroadCastOrderSet_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.DoubleTextWatcher;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;


/**
 * 快捷下单
 * @author wangf
 */
public class HomebjActivity extends BaseActivity implements OnClickListener {
	private LinearLayout choosedata;
	private LinearLayout choosetime;
	private LinearLayout layaddress;
	private LinearLayout layhome, laytime;
	private ImageView logo;
	private TextView include, marketprice;
	private TextView data;
	private TextView time;
	private EditText home, price, beizhu, alltime;
	private TextView name, phone, address;
	private Button btnSure;
	private TextView title;
	private int flag;
	private Calendar cal;
	
	private CustomProgressDialog mCustomProgressDialog;
	private FragmentController controller;

	private MyApplication myApplication = null;

	private final static int ordersuccess = 0;
	private final static int orderfails = 1;
	private final static int BTNTIMEROVER = 2;
	private Map<String, String> appHomeData = null;// 首页数据
	private List<Map<String, String>> serviceAddrList;// 用户服务地址列表
	Map<String, String> serviceAdd = null;// 用户选中服务地址

	private final static int REQ_CODE = 100;
	private int seqOrderBroad = -1;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_bt1);
		
		myApplication = (MyApplication) getApplication();
		//来自于快捷下单
		appHomeData = (Map<String, String>) getIntent().getSerializableExtra("home");
		
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		initViews();
		backArrow();

	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub

			switch (msg.what) {
			case ordersuccess:
				ToastUtils.show(HomebjActivity.this, "下单成功", 1);
				// Intent intent = new Intent(HomebjActivity.this, 
				// OrderActivity.class);
				// startActivity(intent);
				
				finish();
				break;
			case orderfails:
				//设置按钮为可点击
				btnSure.setClickable(true);
				btnSure.setBackgroundResource(R.drawable.selector_oppointment);
				ToastUtils.show(HomebjActivity.this,"下单失败-" + msg.obj.toString(), 0);
				break;
			case BTNTIMEROVER:
				//设置按钮为可点击
				btnSure.setClickable(true);
				btnSure.setBackgroundResource(R.drawable.selector_oppointment);
				handler.removeCallbacks(btnTimerRunnable);
				mCustomProgressDialog.dismiss();
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}
	};

	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		// title.setText(getIntent().getBundleExtra("home").getString("1") );
		
		
		// 类型logo
		logo = (ImageView) findViewById(R.id.companyLogo);
		

		// 类型简介
		include = (TextView) findViewById(R.id.companyinclude);
		// include.setText(getIntent().getBundleExtra("home").getString("3") );
		
		// 市场参考价
		marketprice = (TextView) findViewById(R.id.marketprice);
		// marketprice.setText(getIntent().getBundleExtra("home").getString("4")
		// );
		
		// 时间
		data = (TextView) findViewById(R.id.data);
		time = (TextView) findViewById(R.id.time);
		// 姓名
		name = (TextView) findViewById(R.id.username);
		// 手机号
		phone = (TextView) findViewById(R.id.userphone);
		// 联系地址
		address = (TextView) findViewById(R.id.useraddress);
		// 房屋面积
		home = (EditText) findViewById(R.id.floorspace);
		// 心理价位
		price = (EditText) findViewById(R.id.userprice);
		new DoubleTextWatcher(price);//限制只能输入一个小数点并且小数点后只能输入一位
		// 备注
		beizhu = (EditText) findViewById(R.id.beizhu);
		// 服务总时长
		alltime = (EditText) findViewById(R.id.alltime);

		choosedata = (LinearLayout) findViewById(R.id.choosedata);
		choosetime = (LinearLayout) findViewById(R.id.choosetime);
		layaddress = (LinearLayout) findViewById(R.id.lay_address);
		layhome = (LinearLayout) findViewById(R.id.lay_home);
		laytime = (LinearLayout) findViewById(R.id.lay_alltime);
		
		choosedata.setOnClickListener(this);
		choosetime.setOnClickListener(this);
		layaddress.setOnClickListener(this);
		btnSure = (Button) findViewById(R.id.bt_sure);
		btnSure.setOnClickListener(this);

		setViewData();
	}
	
	
	/**
	 * 为页面控件设置数据
	 */
	private void setViewData(){
		int sonId = Integer.parseInt(appHomeData.get("Sonid"));
		int parentId = Integer.parseInt(appHomeData.get("Parentid"));
		
		title.setText(appHomeData.get("SonName"));
		logo.getDrawable().setLevel(sonId);
		include.setText(appHomeData.get("SubSketch"));
		marketprice.setText(appHomeData.get("SubRmark"));
	
		//根据服务类型判断是否需要输入房屋面积
		if(UniversalUtils.isInputArea(sonId)){
			layhome.setVisibility(View.VISIBLE);
		}
		
//		setNoteViewHint(parentId);
		
	}
	
	
	/**
	 * 添加备注View的提示信息
	 * @param parentId
	 */
	private void setNoteViewHint(int parentId) {
		if (parentId == 10) {//保洁
			beizhu.setHint("需提供住房面积等（100字以内）");
		}else if(parentId == 30){//搬家
			beizhu.setHint("需提供搬什么家具，哪些需要拆装等（100字以内）");
		}else if(parentId == 50){//家具维修
			beizhu.setHint("需提供门的品牌或材质等（100字以内）");
		}else if(parentId == 90){//宠物服务
			beizhu.setHint("需提供宠物身高，品种等（100字以内）");
		}
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		setAddrView();
		super.onResume();
	}
	
	

	/**
	 * 为地址控件设置数据
	 */
	private void setAddrView() {
		//若登录状态，则查询数据库，取出用户设置的默认地址
		if (myApplication.loginState) {
			queryServiceAdd();
			//若未查出数据，则直接返回
			if(serviceAddrList.size() <= 0){
				return;
			}
			//取出地址数据库中第一条联系地址的userID，若与当前登录的用户userID不一致，则返回
			int dbUserId = Integer.parseInt(serviceAddrList.get(0).get("userId"));
			if(dbUserId != myApplication.userId){
				return;
			}
			
			for (int i = 0; i < serviceAddrList.size(); i++) {
				if (serviceAddrList.get(i).get("selecState").equals("1")) {
					serviceAdd = serviceAddrList.get(i);
					name.setText(serviceAdd.get("objName"));
					phone.setText(serviceAdd.get("objTel"));
					address.setText(serviceAdd.get("longAddr"));
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Define.RESULTCODE_SERVERADDR) {
			serviceAdd = (Map<String, String>) data
					.getSerializableExtra("serviceAdd");
			if (serviceAdd == null) {
				name.setText("");
				phone.setText("");
				address.setText("");
			}else{
				name.setText(serviceAdd.get("objName"));
				phone.setText(serviceAdd.get("objTel"));
				address.setText(serviceAdd.get("longAddr"));
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.choosedata:
			onCreateDialog(R.id.choosedata).show();
			break;
		case R.id.choosetime:
			onCreateDialog(R.id.choosetime).show();
			break;
		case R.id.lay_address:
			// 若未登录，则登录，
			if (myApplication.loginState) {
				intent = new Intent(this, AddressActivity.class);
				startActivityForResult(intent, REQ_CODE);
			} else {
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}

			break;
		case R.id.bt_sure:
			
			//下单按钮按下时的数据准备
			judgeLoginAndLoad();

			break;

		default:
			break;
		}

	}
	
	
	/**
	 * 判断登录状态，并进行登录前的准备
	 */
	private void judgeLoginAndLoad(){
		/*
		 * 判断是否登录，若已经登录则允许下单，若没有登录则进入登录页面
		 */
		if (myApplication.loginState) {

			if (price.getText().toString().trim().equals("")
					|| data.getText().toString().trim().equals("")
					|| time.getText().toString().trim().equals("")) {
				ToastUtils.show(getApplicationContext(), "价格或时间不能为空，请重试！",1);
				return;
			}
			if (serviceAdd == null) {
				ToastUtils.show(getApplicationContext(), "请添加选择地址", 1);
				return;
			}
			if(Integer.parseInt(serviceAdd.get("userId")) != myApplication.userId){
				ToastUtils.show(getApplicationContext(), "请添加选择地址", 1);
				return;
			}
			
			
			String strDataTime = data.getText().toString().trim()+" "+time.getText().toString().trim();
			String beginMillis = UniversalUtils.datetimeToTimeMillis(strDataTime);
			long currrentMillis = System.currentTimeMillis();
			if(Long.parseLong(beginMillis) < currrentMillis){
				ToastUtils.show(this, "服务开始时间不能是当前时间", 0);
				return;
			}
			
			
			//根据服务类型判断是否需要输入房屋面积
			if (UniversalUtils.isInputArea(Integer.parseInt(appHomeData.get("Sonid")))) {
				
				if(UniversalUtils.isStringEmpty(home.getText().toString().trim())){
					ToastUtils.show(HomebjActivity.this, "请输入房屋面积", 1);
				}
				
			} else {
				home.setText("0");
			}
			

			//设置按钮为不可点击
			btnSure.setClickable(false);
			btnSure.setBackgroundResource(R.drawable.shape_btn_click_not);
			handler.postDelayed(btnTimerRunnable, Define.BTN_DELAY_TIME);
			//加载进度条
			mCustomProgressDialog = new CustomProgressDialog(this);
			mCustomProgressDialog.show();
			// 发送下单业务请求
			loadOrderData();
		} else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	
	/**
	 * 用于对按钮不可点击进行计时
	 */
	Runnable btnTimerRunnable = new Runnable(){
		@Override
		public void run() {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(BTNTIMEROVER);
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
					HomebjActivity.this.finish();

					break;
				}
			}
		});
	}

	/**
	 * 时间选择器
	 * 
	 * @param id
	 * @return
	 */
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		cal = Calendar.getInstance(Locale.CHINA);
		cal.setTimeInMillis(System.currentTimeMillis());
		
		switch (id) {
		case R.id.choosedata:
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year,
						int month, int dayOfMonth) {
					if (year > cal.get(Calendar.YEAR)) { // 设置年大于当前年，直接设置，不用判断下面的
						data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
								+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
						flag = 1;
					} else if (year == cal.get(Calendar.YEAR)) { // 设置年等于当前年，则向下开始判断月
						if (month > cal.get(Calendar.MONTH)) { // 设置月等于当前月，直接设置，不用判断下面的
							flag = 1;
							data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
									+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
						} else if (month == cal.get(Calendar.MONTH)) { // 设置月等于当前月，则向下开始判断日
							if (dayOfMonth > cal.get(Calendar.DAY_OF_MONTH)) { // 设置日大于当前月，直接设置，不用判断下面的
								flag = 1;
								data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
										+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
							} else if (dayOfMonth == cal
									.get(Calendar.DAY_OF_MONTH)) { // 设置日等于当前日，则向下开始判断时
								flag = 2;
								data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
										+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
							} else { // 设置日小于当前日，提示重新设置
								flag = 3;
								ToastUtils.show(HomebjActivity.this,
										"当前日不能小于今日,请重新设置", 1);
							}
						} else { // 设置月小于当前月，提示重新设置
							flag = 3;
							ToastUtils.show(HomebjActivity.this,
									"当前月不能小于今月，请重新设置", 1);
						}
					} else { // 设置年小于当前年，提示重新设置
						flag = 3;
						ToastUtils.show(HomebjActivity.this, "当前年不能小于今年，请重新设置",1);
					}

					
					//判断所选时间是否大于七天
					String beginMillis = UniversalUtils.dateToTimeMillis(year+"-"+(month+1)+"-"+dayOfMonth);
					long currrentMillis = System.currentTimeMillis();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String beginDate = sdf.format(new Date(Long.valueOf(beginMillis)));
					String currentDate = sdf.format(new Date(currrentMillis));
					if(Long.parseLong(beginMillis) - currrentMillis > 1000*60*60*24*7){
						flag = 3;
						ToastUtils.show(HomebjActivity.this, "服务开始时间不能大于七天", 1);
					}
					
					
					if (flag == 3) {
						datePicker.init(cal.get(Calendar.YEAR),
								cal.get(Calendar.MONTH),
								cal.get(Calendar.DAY_OF_MONTH), null);
						data.setText("");
					}
					
				}
			};
			dialog = new DatePickerDialog(HomebjActivity.this, dateListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			break;
		case R.id.choosetime:
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					switch (flag) {
					case 1: // 设置日期在当前日期之后，直接设置时间，不用判断
						time.setText(UniversalUtils.judgeHourOfDay(hourOfDay) + ":" + UniversalUtils.judgeMinute(minute));
						break;
					case 2: // 设置日期等于当前日期之后，判断时和分
						if (hourOfDay > cal.get(Calendar.HOUR_OF_DAY)) {
							time.setText(UniversalUtils.judgeHourOfDay(hourOfDay) + ":" + UniversalUtils.judgeMinute(minute));
						} else if (hourOfDay == cal.get(Calendar.HOUR_OF_DAY)) {
							if (minute > cal.get(Calendar.MINUTE)) {
								time.setText(UniversalUtils.judgeHourOfDay(hourOfDay) + ":" + UniversalUtils.judgeMinute(minute));
							} else {
								time.setText(UniversalUtils.judgeHourOfDay(cal.get(Calendar.HOUR_OF_DAY))
										+ ":" + UniversalUtils.judgeMinute(cal.get(Calendar.MINUTE)));
								ToastUtils.show(HomebjActivity.this,
										"请选择大于现在时刻的分钟", 1);
							}
						} else {
							time.setText(UniversalUtils.judgeHourOfDay(cal.get(Calendar.HOUR_OF_DAY)) + ":"
									+ UniversalUtils.judgeMinute(cal.get(Calendar.MINUTE)));
							ToastUtils.show(HomebjActivity.this, "请选择大于现在时刻的小时",1);
						}
						break;
					case 3: // 设置日期等于当前日期之前，提示日期还未设置正确，不能设置时间
						time.setText(UniversalUtils.judgeHourOfDay(cal.get(Calendar.HOUR_OF_DAY)) + ":"
								+ UniversalUtils.judgeMinute(cal.get(Calendar.MINUTE)));
						ToastUtils.show(HomebjActivity.this, "请先设置正确的日期", 2000);
						break;

					default:
						break;
					}
				}
			};
			dialog = new TimePickerDialog(HomebjActivity.this, timeListener,
					cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
			break;
		case 1:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("您要确定放弃编辑吗？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							arg0.cancel();
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

						}
					});
			builder.show();
			break;
		default:
			break;
		}
		return dialog;

	}
	
	

	/**
	 * 查询数据库中的服务地址
	 */
	private void queryServiceAdd() {
		DataBaseService ds = new DataBaseService(this);
//		String sql = DbOprationBuilder.queryAllBuilder("userAddr");
		//查询数据库中未删除的地址信息并显示--其中：isDelete状态标识为 0：未删除，1：已删除
		String sql = DbOprationBuilder.queryBuilderby("*", "userAddr", "isDelete", "0");
		serviceAddrList = ds.query(sql);
	}
	
	
	
	
	/**
	 * 下单请求
	 */
	public void loadOrderData() {
		seqOrderBroad = MyApplication.SequenceNo++;
		HsBroadCastOrderInfo_Req hsBroadCastOrderInfoReq = new MsgInncDef().new HsBroadCastOrderInfo_Req();
		hsBroadCastOrderInfoReq.uUserID = myApplication.userId;// 服务器分配给当前用户的唯一的ID号
		
		hsBroadCastOrderInfoReq.uCompanyID = 0;// 公司ID只有预约下单才不为0
		
		hsBroadCastOrderInfoReq.iOrderTypeID = Integer.parseInt(appHomeData
				.get("Sonid"));// 从数据库中获取的下单类型ID
		hsBroadCastOrderInfoReq.iUsrSrvedIndex = Integer.parseInt(serviceAdd
				.get("sqn"));// 服务地址索引
		hsBroadCastOrderInfoReq.szUnitArea = home.getText().toString().trim();// 单位面积
		hsBroadCastOrderInfoReq.szSrvBeginTime = data.getText().toString()
				.trim()
				+ " " + time.getText().toString().trim();// 开始时间
		hsBroadCastOrderInfoReq.szHeartPrice = price.getText().toString()
				.trim();// 心理价位
		hsBroadCastOrderInfoReq.szReMark = beizhu.getText().toString().trim();// 备注
		byte[] connData = HandleNetSendMsg
				.HandleHsBroadCastOrderReqToPro(hsBroadCastOrderInfoReq,seqOrderBroad);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("connData下单的请求--sequence="+seqOrderBroad +"/"+ Arrays.toString(connData) + "=============");
		
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
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqOrderBroad == iSequence) {
					processOrderBroadData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理下单响应的数据
	 * @param iSequence
	 */
	private void processOrderBroadData(long recvTime){
		HsBroadCastOrderSet_Resp hsBroadCastOrderSetResp = (HsBroadCastOrderSet_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsBroadCastOrderSetResp == null){
			return;
		}
		LogUtils.i("recv下单返回数据");
		//若服务端返回数据，将按钮不可点击的计时去掉
		handler.removeCallbacks(btnTimerRunnable);
		mCustomProgressDialog.dismiss();
		if (hsBroadCastOrderSetResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			int orderId = hsBroadCastOrderSetResp.iOrderID;
			String orderCode = hsBroadCastOrderSetResp.szOrderCode;
			String cerateTime = hsBroadCastOrderSetResp.szCreateTime;

			LogUtils.i("订单ID"+ orderId + "=============");
			LogUtils.i("订单编号"+orderCode + "=============");
			LogUtils.i("创建时间"+ cerateTime + "=============");

			Message message = new Message();
			message.what = ordersuccess;
			handler.sendMessage(message);
		} else {
			String result = UniversalUtils
					.judgeNetResult_Hs(hsBroadCastOrderSetResp.eOperResult);
			Log.i("下单失败", result + "===========");

			Message message = new Message();
			message.what = orderfails;
			message.obj = result;
			handler.sendMessage(message);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
