package com.lhdz.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserSeeCmpServiceInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.entity.ServiceListInfoEntity;
import com.lhdz.fragment.FragmentController;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsSubScribeOrderInfo_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsBroadCastOrderSet_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.DoubleTextWatcher;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;
import com.lhdz.wediget.AppointOrderPopmenu;
import com.lhdz.wediget.AppointOrderPopmenu.ServiceNumCallBack;
import com.lhdz.wediget.AppointPopmenu;

/**
 * 预约下单
 * @author wangf
 */
public class HomeAppointmentActivity extends BaseActivity implements OnClickListener {
	private TextView title;
	private ImageView logo;
	private TextView companyinclude;
	
	private TextView userName, userPhone, userAddress;
	private LinearLayout ll_include_address;
	
	private LinearLayout ll_choice_server;
	
	private LinearLayout ll_expand_service_list;//服务伸缩
	private ImageView iv_expand_service_arrow;//服务伸缩的图标
	private LinearLayout ll_add_service_list;//动态添加服务信息
	private TextView tv_appoint_price;
	private LinearLayout ll_service_list;//服务列表所有的控件
	
	private LinearLayout ll_appoint_choosedata;
	private TextView appoint_data;
	private LinearLayout ll_appoint_choosetime;
	private TextView appoint_time;
	
	private EditText beizhu; 
	
	private Button btnSure;
	
	private int flag;
	private Calendar cal;
	
	private CustomProgressDialog mCustomProgressDialog;

	private MyApplication myApplication = null;

	private final static int ordersuccess = 0;
	private final static int orderfails = 1;
	private final static int BTNTIMEROVER = 2;
	
	private List<Map<String, String>> serviceAddrList;// 用户服务地址列表
	
	private Map<String, String> serviceAddr = null;// 用户选中服务地址

	private final static int REQ_CODE = 100;
	private int seqOrderBroad = -1;
	
	private Map<String, String> starCompanyDetail;//明星公司的数据
	private HsUserSeeCmpServiceInfo_Pro cmpServiceInfo;//公司服务信息
	
	private List<ServiceListInfoEntity> listInfoEntities = new ArrayList<ServiceListInfoEntity>();//所有服务的数据
	
	private String strOrderService = null;//服务清单数据
	private double totlePrice = 0;//订单总价
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_appointment);
		
		myApplication = (MyApplication) getApplication();
		
		starCompanyDetail = (Map<String, String>) getIntent().getSerializableExtra("starCompanyDetail");
		cmpServiceInfo = (HsUserSeeCmpServiceInfo_Pro) getIntent().getSerializableExtra("cmpServiceInfo");
		
//		String s = "悲剧|12|小时;你猜|45|件;不错|12.45|平方米;不好|45.2|件;萝卜|2|小时;青菜|0.5|斤;";
//		listInfoEntities = UniversalUtils.parseServiceList(s);
		listInfoEntities = UniversalUtils.parseServiceList(cmpServiceInfo.getSzServiceItem());
		
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
				ToastUtils.show(HomeAppointmentActivity.this, "下单成功", 1);
				
				int orderId = msg.arg1;//订单id
				Intent intent = new Intent(HomeAppointmentActivity.this, IndentDetailsActivity.class);
				intent.putExtra("uOrderID", orderId);//订单唯一标识--orderID
				startActivity(intent);
				
				finish();
				break;
			case orderfails:
				//设置按钮为可点击
				btnSure.setClickable(true);
				btnSure.setBackgroundResource(R.drawable.selector_oppointment);
				ToastUtils.show(HomeAppointmentActivity.this,"下单失败=" + msg.obj.toString(), 0);
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
		title = (TextView) findViewById(R.id.tv_title);//标题
		logo = (ImageView) findViewById(R.id.appoint_companyLogo);//服务图标
		companyinclude = (TextView) findViewById(R.id.appoint_companyinclude);//服务介绍
		
		userName = (TextView) findViewById(R.id.username);// 姓名
		userPhone = (TextView) findViewById(R.id.userphone);// 手机号
		userAddress = (TextView) findViewById(R.id.useraddress);// 联系地址
		ll_include_address = (LinearLayout) findViewById(R.id.lay_address);//地址
		
		ll_choice_server = (LinearLayout) findViewById(R.id.ll_appoint_choice_server);//选择服务
		
		ll_expand_service_list = (LinearLayout) findViewById(R.id.ll_expand_service_list);//伸缩控件
		iv_expand_service_arrow = (ImageView) findViewById(R.id.iv_expand_service_arrow);//服务伸缩的图标
		ll_add_service_list = (LinearLayout) findViewById(R.id.ll_add_service_list);//动态的添加view
		tv_appoint_price = (TextView) findViewById(R.id.tv_appoint_price);//订单金额
		ll_service_list = (LinearLayout) findViewById(R.id.ll_appoint_service_list);//服务列表的所有控件
		
		ll_appoint_choosedata = (LinearLayout) findViewById(R.id.ll_appoint_choosedata);//请选择日期
		ll_appoint_choosetime = (LinearLayout) findViewById(R.id.ll_appoint_choosetime);//请选择时间
		appoint_data = (TextView) findViewById(R.id.appoint_data);//日期
		appoint_time = (TextView) findViewById(R.id.appoint_time);//时间
		
		beizhu = (EditText) findViewById(R.id.beizhu);//备注
		
		btnSure = (Button) findViewById(R.id.bt_appoint_sure);//确认按钮
		
		ll_expand_service_list.setOnClickListener(this);
		ll_include_address.setOnClickListener(this);
		ll_choice_server.setOnClickListener(this);
		ll_appoint_choosedata.setOnClickListener(this);
		ll_appoint_choosetime.setOnClickListener(this);
		btnSure.setOnClickListener(this);

		setViewData();
		setAddrView();
	}
	
	
	
	/**
	 * 为view设置数据
	 */
	private void setViewData(){
		ll_service_list.setVisibility(View.GONE);//将服务列表处的所有控件隐藏
		
		title.setText(cmpServiceInfo.getSzServiceName());//标题
		int sonId = cmpServiceInfo.getIServiceID();
		logo.getDrawable().setLevel(sonId);//图标
		companyinclude.setText(cmpServiceInfo.getSzRemark());//描述
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
					serviceAddr = serviceAddrList.get(i);
					userName.setText(serviceAddr.get("objName"));
					userPhone.setText(serviceAddr.get("objTel"));
					userAddress.setText(serviceAddr.get("longAddr"));
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == Define.RESULTCODE_SERVERADDR) {
			serviceAddr = (Map<String, String>) data.getSerializableExtra("serviceAdd");
			if (serviceAddr == null) {
				userName.setText("");
				userPhone.setText("");
				userAddress.setText("");
			}else{
				userName.setText(serviceAddr.get("objName"));
				userPhone.setText(serviceAddr.get("objTel"));
				userAddress.setText(serviceAddr.get("longAddr"));
			}
		}
	}

	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_appoint_choosedata:
			onCreateDialog(R.id.choosedata).show();
			break;
		case R.id.ll_appoint_choosetime:
			onCreateDialog(R.id.choosetime).show();
			break;
		case R.id.lay_address:
			// 若未登录，则登录，
			if (myApplication.loginState) {
				Intent intent = new Intent(this, AddressActivity.class);
				startActivityForResult(intent, REQ_CODE);
			} else {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.ll_expand_service_list:
			//伸缩控件
			serviceListGoneOrVisibility();
			break;
		case R.id.ll_appoint_choice_server:
			//选择服务
			choiceServicePopWindow(v);
			break;
		case R.id.bt_appoint_sure:
			
			judgeLoginAndLoad();//下单按钮按下时的数据准备

			break;

		default:
			break;
		}
	}
	
	
	
	/**
	 * 选择服务--弹出popupWindow
	 */
	private void choiceServicePopWindow(View view){
		AppointOrderPopmenu popmenu = new AppointOrderPopmenu(this,listInfoEntities);
		popmenu.setServiceNumCallBackLister(new ServiceNumCallBack() {
			
			@Override
			public void serviceNumListener(List<ServiceListInfoEntity> serviceNumList) {
				// TODO Auto-generated method stub
				listInfoEntities = serviceNumList;
				setServiceData(serviceNumList);
			}
		});
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.alpha = 0.7f;
		getWindow().setAttributes(lp);
		popmenu.showPopwindow(view);
	}
	
	
	/**
	 * 为服务列表控件设置数据
	 */
	private void setServiceData(List<ServiceListInfoEntity> listInfoEntities){
		int iChoiceNum = 0;//已选择服务类别的数量
		totlePrice = 0;//已选择的服务总价
		strOrderService = "";
		StringBuilder sbServiceItem = new StringBuilder();//服务清单
		
		ll_service_list.setVisibility(View.GONE);
		ll_add_service_list.removeAllViews();
		
		for (int i = 0; i < listInfoEntities.size(); i++) {
			if (listInfoEntities.get(i).getServiceNum() != 0) {
				iChoiceNum++;
				ServiceListInfoEntity serviceInfo = listInfoEntities.get(i);
				
				View view = LayoutInflater.from(this).inflate(R.layout.item_appointment_service, null);
				TextView item_service_name = (TextView) view.findViewById(R.id.item_service_name);
				TextView item_service_num = (TextView) view.findViewById(R.id.item_service_num);
				TextView item_service_price = (TextView) view.findViewById(R.id.item_service_price);

				String strServiceName = serviceInfo.getServiceName();
				item_service_name.setText(strServiceName);
				
				String serviceNum = UniversalUtils.getString2Float(serviceInfo.getServicePrice())+"/"+serviceInfo.getServiceUnit()+" x "+serviceInfo.getServiceNum();
				item_service_num.setText("¥"+serviceNum);
				
				double itemPrice = Double.parseDouble(serviceInfo.getServicePrice())*serviceInfo.getServiceNum();
				totlePrice = totlePrice + itemPrice;
				item_service_price.setText("¥"+UniversalUtils.getPoint2Float(itemPrice));

				ll_add_service_list.addView(view);
				
				String strServiceItem = UniversalUtils.appendServiceList(strServiceName, serviceInfo.getServicePrice(),
						serviceInfo.getServiceUnit(), serviceInfo.getServiceNum()) + ";";
				sbServiceItem.append(strServiceItem);
				
			}
		}
		//当用户选择的服务类别数量大于0时，需要显示服务列表和总价
		if(iChoiceNum > 0){
			ll_service_list.setVisibility(View.VISIBLE);
			tv_appoint_price.setText("¥"+UniversalUtils.getPoint2Float(totlePrice));
			strOrderService = sbServiceItem.substring(0, sbServiceItem.length()-1).toString();
		}
		
	}
	
	
	/**
	 * 服务清单的显示与隐藏
	 */
	private void serviceListGoneOrVisibility(){
		if(ll_add_service_list.getVisibility() == View.VISIBLE){
			ll_add_service_list.setVisibility(View.GONE);
			iv_expand_service_arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_gray_d));
		}
		else if(ll_add_service_list.getVisibility() == View.GONE){
			ll_add_service_list.setVisibility(View.VISIBLE);
			iv_expand_service_arrow.setBackgroundDrawable(getResources().getDrawable(R.drawable.arrow_gray_up));
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
			
			if(UniversalUtils.isStringEmpty(strOrderService)){
				ToastUtils.show(getApplicationContext(), "请选择服务",0);
				return;
			}

			if (appoint_data.getText().toString().trim().equals("")
					|| appoint_time.getText().toString().trim().equals("")) {
				ToastUtils.show(getApplicationContext(), "时间不能为空，请重试！",
						Toast.LENGTH_SHORT);
				return;
			}
			if (serviceAddr == null) {
				ToastUtils.show(getApplicationContext(), "请添加选择地址", Toast.LENGTH_SHORT);
				return;
			}
			if(Integer.parseInt(serviceAddr.get("userId")) != myApplication.userId){
				ToastUtils.show(getApplicationContext(), "请添加选择地址", Toast.LENGTH_SHORT);
				return;
			}
			
			
			String strDataTime = appoint_data.getText().toString().trim()+" "+appoint_time.getText().toString().trim();
			String beginMillis = UniversalUtils.datetimeToTimeMillis(strDataTime);
			long currrentMillis = System.currentTimeMillis();
			if(Long.parseLong(beginMillis) < currrentMillis){
				ToastUtils.show(this, "服务开始时间不能是当前时间", 0);
				return;
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
					HomeAppointmentActivity.this.finish();

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
		cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		switch (id) {
		case R.id.choosedata:
			DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker datePicker, int year,
						int month, int dayOfMonth) {
					if (year > cal.get(Calendar.YEAR)) { // 设置年大于当前年，直接设置，不用判断下面的
						appoint_data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
								+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
						flag = 1;
					} else if (year == cal.get(Calendar.YEAR)) { // 设置年等于当前年，则向下开始判断月
						if (month > cal.get(Calendar.MONTH)) { // 设置月等于当前月，直接设置，不用判断下面的
							flag = 1;
							appoint_data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
									+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
						} else if (month == cal.get(Calendar.MONTH)) { // 设置月等于当前月，则向下开始判断日
							if (dayOfMonth > cal.get(Calendar.DAY_OF_MONTH)) { // 设置日大于当前月，直接设置，不用判断下面的
								flag = 1;
								appoint_data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
										+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
							} else if (dayOfMonth == cal
									.get(Calendar.DAY_OF_MONTH)) { // 设置日等于当前日，则向下开始判断时
								flag = 2;
								appoint_data.setText(year + "-" + UniversalUtils.judgeMonth(month + 1) + "-"
										+ UniversalUtils.judgeDayOfMonth(dayOfMonth));
							} else { // 设置日小于当前日，提示重新设置
								flag = 3;
								ToastUtils.show(HomeAppointmentActivity.this,
										"当前日不能小于今日,请重新设置", 2000);
							}
						} else { // 设置月小于当前月，提示重新设置
							flag = 3;
							ToastUtils.show(HomeAppointmentActivity.this,
									"当前月不能小于今月，请重新设置", 2000);
						}
					} else { // 设置年小于当前年，提示重新设置
						flag = 3;
						ToastUtils.show(HomeAppointmentActivity.this, "当前年不能小于今年，请重新设置",
								2000);
					}

					
					//判断所选时间是否大于七天
					String beginMillis = UniversalUtils.dateToTimeMillis(year+"-"+(month+1)+"-"+dayOfMonth);
					long currrentMillis = System.currentTimeMillis();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					String beginDate = sdf.format(new Date(Long.valueOf(beginMillis)));
					String currentDate = sdf.format(new Date(currrentMillis));
					if(Long.parseLong(beginMillis) - currrentMillis > 1000*60*60*24*7){
						flag = 3;
						ToastUtils.show(HomeAppointmentActivity.this, "服务开始时间不能大于七天", Toast.LENGTH_SHORT);
					}
					
					
					if (flag == 3) {
						datePicker.init(cal.get(Calendar.YEAR),
								cal.get(Calendar.MONTH),
								cal.get(Calendar.DAY_OF_MONTH), null);
						appoint_data.setText("");
					}
					
				}
			};
			dialog = new DatePickerDialog(HomeAppointmentActivity.this, dateListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
			break;
		case R.id.choosetime:
			TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {

				@Override
				public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
					switch (flag) {
					case 1: // 设置日期在当前日期之后，直接设置时间，不用判断
						appoint_time.setText(UniversalUtils.judgeHourOfDay(hourOfDay) + ":" + UniversalUtils.judgeMinute(minute));
						break;
					case 2: // 设置日期等于当前日期之后，判断时和分
						if (hourOfDay > cal.get(Calendar.HOUR_OF_DAY)) {
							appoint_time.setText(UniversalUtils.judgeHourOfDay(hourOfDay) + ":" + UniversalUtils.judgeMinute(minute));
						} else if (hourOfDay == cal.get(Calendar.HOUR_OF_DAY)) {
							if (minute > cal.get(Calendar.MINUTE)) {
								appoint_time.setText(UniversalUtils.judgeHourOfDay(hourOfDay) + ":" + UniversalUtils.judgeMinute(minute));
							} else {
								appoint_time.setText(UniversalUtils.judgeHourOfDay(cal.get(Calendar.HOUR_OF_DAY))
										+ ":" + UniversalUtils.judgeMinute(cal.get(Calendar.MINUTE)));
								ToastUtils.show(HomeAppointmentActivity.this,
										"请选择大于现在时刻的分钟", 1);
							}
						} else {
							appoint_time.setText(UniversalUtils.judgeHourOfDay(cal.get(Calendar.HOUR_OF_DAY)) + ":"
									+ UniversalUtils.judgeMinute(cal.get(Calendar.MINUTE)));
							ToastUtils.show(HomeAppointmentActivity.this, "请选择大于现在时刻的小时",1);
						}
						break;
					case 3: // 设置日期等于当前日期之前，提示日期还未设置正确，不能设置时间
						appoint_time.setText(UniversalUtils.judgeHourOfDay(cal.get(Calendar.HOUR_OF_DAY)) + ":"
								+ UniversalUtils.judgeMinute(cal.get(Calendar.MINUTE)));
						ToastUtils.show(HomeAppointmentActivity.this, "请先设置正确的日期", 2000);
						break;

					default:
						break;
					}
				}
			};
			dialog = new TimePickerDialog(HomeAppointmentActivity.this, timeListener,
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
		//查询数据库中未删除的地址信息并显示--其中：isDelete状态标识为 0：未删除，1：已删除
		String sql = DbOprationBuilder.queryBuilderby("*", "userAddr", "isDelete", "0");
		serviceAddrList = ds.query(sql);
	}
	
	
	
	/**
	 * 下单请求
	 */
	public void loadOrderData() {
		seqOrderBroad = MyApplication.SequenceNo++;
		HsSubScribeOrderInfo_Req hsSubScribeOrderInfo_Req = new MsgInncDef().new HsSubScribeOrderInfo_Req();
		hsSubScribeOrderInfo_Req.uUserID = myApplication.userId;// 服务器分配给当前用户的唯一的ID号
		
		hsSubScribeOrderInfo_Req.uCompanyID = Integer.parseInt(starCompanyDetail.get("iCompanyID"));// 公司ID只有预约下单才不为0
		
		hsSubScribeOrderInfo_Req.iOrderTypeID = cmpServiceInfo.getIServiceID();// 下单类型ID
		hsSubScribeOrderInfo_Req.iUsrSrvedIndex = Integer.parseInt(serviceAddr.get("sqn"));// 服务地址索引
		hsSubScribeOrderInfo_Req.szSrvBeginTime = appoint_data.getText().toString().trim()
				+ " " + appoint_time.getText().toString().trim();// 开始时间
		hsSubScribeOrderInfo_Req.szServiceItem = strOrderService;//服务清单
		hsSubScribeOrderInfo_Req.szSrvPrice = UniversalUtils.getPoint2Float(totlePrice);// 服务价格
		hsSubScribeOrderInfo_Req.szReMark = beizhu.getText().toString().trim();// 备注
		byte[] connData = HandleNetSendMsg
				.HandleHsSubScribeOrderInfoReqToPro(hsSubScribeOrderInfo_Req,seqOrderBroad);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("connData预约下单的请求--sequence="+seqOrderBroad +"/"+ Arrays.toString(connData) + "=============");
		
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

			setDbStarCompanyData(starCompanyDetail);
			
			LogUtils.i("订单ID"+ orderId + "=============");
			LogUtils.i("订单编号"+orderCode + "=============");
			LogUtils.i("创建时间"+ cerateTime + "=============");

			Message message = new Message();
			message.arg1 = orderId;
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
	
	
	
	/**
	 * 将明星公司数据写入数据库中
	 * 当用户预约下单成功之后需要将该公司的信息写入数据库中
	 * 当数据库中有该公司的信息时不需要写入
	 * @param starCompanyDetail
	 */
	private void setDbStarCompanyData(Map<String, String> starCompanyDetail) {
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany","iCompanyID", starCompanyDetail.get("iCompanyID"));
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		if (starCompanyDataList.size() != 0) {
			return;
		}

		String insertSql = DbOprationBuilder.insertStarCompanyAllBuilder(
				MyApplication.userId, starCompanyDetail);
		ds.insert(insertSql);
	}
	
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
}
