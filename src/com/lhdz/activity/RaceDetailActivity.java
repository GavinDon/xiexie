package com.lhdz.activity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsRaceCompanyInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderDailInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsCommon_Notify;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserOrderDetail_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 *  抢单详情--确认订单
 * @author wangf
 *
 */
public class RaceDetailActivity extends BaseActivity implements OnClickListener {
	
	private TextView title;//页面标题
	private TextView back;//返回按钮
	
	private int uOrderID = 0;//订单id
	private String szOrderValue = "";//订单号
	private HsRaceCompanyInfo_Pro raceCompanyInfo = null;//抢单公司数据
	private boolean isSelectCompany; 
	
	private HsUserOrderDailInfo_Pro orderDetailInfo = null;//订单详情
	
	private ImageView compaIcon;//公司图标
	private TextView tv_comname;//公司名称
	private RatingBar indent_company_rating;//公司星级
	private TextView tv_address;//公司地址
	
	private TextView indent_company_price;//公司报价
	private TextView indent_company_msg;//公司消息
	
	private TextView order_no;// 订单号
	private TextView order_state;// 订单状态
	private TextView order_servicetype;// 服务类型
	private TextView order_area;// 房屋面积
	private TextView order_herprice;// 心理价位
	private TextView order_servicetime;// 服务时间
	private TextView order_content;// 备注
	private TextView order_price;// 订单金额
	
	private RadioButton rb_AuthFlag;
	private RadioButton rb_Filing;
	private RadioButton rb_OffLine;
	private RadioButton rb_Nominate;
	
	private Button complainBtn;// 投诉按钮
	private Button sureBtn;// 确认按钮
	
	private MyApplication myApplication;
	private final static int MSG_LOAD_SUCCESS = 0;
	private final static int MSG_LOAD_ERROR = 1;
	private final static int MSG_CHOICE_COMPANY_SUCCESS = 2;
	private final static int MSG_CHOICE_COMPANY_ERROR = 3;
	private final static int LOAD_TIMER_OVER = 4;
//	List<Map<String, String>> serAddrInfo = null;//用户地址信息
	
	private CustomProgressDialog progressDialog;
	
	private int seqRaceDetail = -1;
	private int seqChoiceCompanyNo = -1;
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_LOAD_SUCCESS:
				
				//为界面设置数据
				setViewData();
				
				break;
			case MSG_LOAD_ERROR:
				Toast.makeText(RaceDetailActivity.this, msg.obj.toString(), 0).show();
				break;
			case MSG_CHOICE_COMPANY_SUCCESS:
				
				break;
			case MSG_CHOICE_COMPANY_ERROR:
				Toast.makeText(RaceDetailActivity.this, msg.obj.toString(), 0).show();
				break;
			case LOAD_TIMER_OVER:
				//服务端无返回，停止下拉刷新
				handler.removeCallbacks(loadTimerRunnable);
				progressDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indent_details_service);
		
		myApplication = (MyApplication) getApplication();
		
		uOrderID = getIntent().getIntExtra("uOrderID", -1);
		szOrderValue = getIntent().getStringExtra("szOrderValue");
		isSelectCompany = getIntent().getBooleanExtra("isSelectCompany", false);//用于标识是否已经选定服务公司
		raceCompanyInfo = (HsRaceCompanyInfo_Pro) getIntent().getSerializableExtra("raceCompanyInfo");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		loadOrderDetailData();//获取订单详情数据
		// 初始化控件
		initViews();
		
		
	}

	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("确认订单");
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(this);
		
		compaIcon = (ImageView) findViewById(R.id.compaIcon);
		tv_comname = (TextView) findViewById(R.id.tv_comname);
		indent_company_rating = (RatingBar) findViewById(R.id.indent_company_rating);
		tv_address = (TextView) findViewById(R.id.tv_address);
		
		rb_AuthFlag = (RadioButton) findViewById(R.id.rb_AuthFlag);//身份认证
		rb_Filing = (RadioButton) findViewById(R.id.rb_Filing);//证件备案
		rb_OffLine = (RadioButton) findViewById(R.id.rb_OffLine);//线下验证
		rb_Nominate = (RadioButton) findViewById(R.id.rb_Nominate);//官方推荐
		
		indent_company_price = (TextView) findViewById(R.id.indent_company_price);
		indent_company_msg = (TextView) findViewById(R.id.indent_company_msg);
		
		order_no = (TextView) findViewById(R.id.order_no);
		order_state = (TextView) findViewById(R.id.order_state);
		order_servicetype = (TextView) findViewById(R.id.order_servicetype);
		order_area = (TextView) findViewById(R.id.order_area);
		order_herprice = (TextView) findViewById(R.id.order_herprice);
		order_servicetime = (TextView) findViewById(R.id.order_servicetime);
		order_content = (TextView) findViewById(R.id.order_content);
		order_price = (TextView) findViewById(R.id.order_price);
		
		complainBtn = (Button) findViewById(R.id.indent_ser_complain);
		sureBtn = (Button) findViewById(R.id.indent_ser_sure);
		complainBtn.setOnClickListener(this);
		sureBtn.setOnClickListener(this);
		complainBtn.setVisibility(View.GONE);
		sureBtn.setVisibility(View.GONE);
		
//		//根据订单状态显示不同的按钮
//		sureBtn.setText("付款");
//		complainBtn.setVisibility(View.GONE);
	}
	
	
	/**
	 * 为view设置数据
	 */
	private void setViewData(){
		//公司信息
		ImageLoader.getInstance().displayImage(Define.URL_COMPANY_IMAGE + raceCompanyInfo.getSzCompanyUrl(), compaIcon);
		tv_comname.setText(raceCompanyInfo.getSzName());//公司名称
		indent_company_rating.setRating(UniversalUtils.processRatingLevel(raceCompanyInfo.getIStarLevel()));//公司星级
		tv_address.setText("地址："+raceCompanyInfo.getSzAddr());//公司地址
		
		setRbState();
		
		indent_company_price.setText("￥"+UniversalUtils.getString2Float(raceCompanyInfo.getSzPayMent())+" 元");//公司报价
		if(UniversalUtils.isStringEmpty(raceCompanyInfo.getSzServiceInfo())){
			indent_company_msg.setText("我公司将会为您提供高质量高效益的服务！");//公司消息
		}else{
			indent_company_msg.setText(raceCompanyInfo.getSzServiceInfo());//公司消息
		}
		
		//订单信息
		order_no.setText("订单号："+szOrderValue);
		order_state.setText(orderDetailInfo.getSzOrderStateName());//订单状态
		order_servicetype.setText("服务类型："+orderDetailInfo.getSzSerivceTypeName());
		
		
		// 根据服务类型判断是否需要显示房屋面积
		if (UniversalUtils.isInputArea(orderDetailInfo.getIServiceType())) {
			order_area.setText("房屋面积："+orderDetailInfo.getSzUintArea()+" 平米");
			order_area.setVisibility(View.VISIBLE);
		} else {
			order_area.setVisibility(View.GONE);
		}
		
		order_herprice.setText("心理价位："+UniversalUtils.getString2Float(orderDetailInfo.getSzHeartPrice())+" 元");
		order_servicetime.setText("服务时间："+orderDetailInfo.getSzSrvBeginTime());
		order_content.setText(orderDetailInfo.getSzRemark());//备注
		order_price.setText("订单金额："+UniversalUtils.getString2Float(raceCompanyInfo.getSzPayMent())+" 元");//订单金额
		
		if(isSelectCompany){
			sureBtn.setVisibility(View.VISIBLE);
			sureBtn.setText("立即支付");
		}else{
			sureBtn.setVisibility(View.VISIBLE);
			sureBtn.setText("选定并支付");
		}
		
	}
	

	
	/**
	 * 为公司标识设置数据
	 */
	private void setRbState(){
		rb_AuthFlag.getBackground().setLevel(raceCompanyInfo.getIAuthFlag());
		rb_Filing.getBackground().setLevel(raceCompanyInfo.getIFiling());
		rb_OffLine.getBackground().setLevel(raceCompanyInfo.getIOffLine());
		rb_Nominate.getBackground().setLevel(raceCompanyInfo.getINominate());
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.public_back:
			this.finish();
			break;
		case R.id.indent_ser_complain:
//			Toast.makeText(RaceDetailActivity.this, "敬请期待", 0).show();
//			Intent intent = new Intent(this, ChoiceServiceActivity.class);
//			intent.putExtra("orderInfo", (Serializable)orderInfo);
//			intent.putExtra("orderValue", orderValue);
//			startActivity(intent);
			break;
		case R.id.indent_ser_sure:
			
			//若已经选定服务公司，按钮显示为“立即支付”，此时点击按钮后直接进入支付页面
			//若未选定服务公司，按钮显示为“选定并支付”，此时点击按钮后需要发送选定该公司的请求。若请求返回成功，则进入支付页面，若返回失败，页面给出提示
			if(isSelectCompany){
				Intent intent = new Intent(RaceDetailActivity.this, OrderActivity.class);
				
//				intent.putExtra("orderDetailInfo", (Serializable)orderDetailInfo);//订单详情
				
				intent.putExtra("uOrderID", orderDetailInfo.getUOrderID());//orderId
				intent.putExtra("szOrderValue", szOrderValue);//订单号
				intent.putExtra("szOrderStateName", orderDetailInfo.getSzOrderStateName());//订单状态
				
				intent.putExtra("goodsname", orderDetailInfo.getSzSerivceTypeName());//商品名称
				intent.putExtra("goodsdetil", "歇歇服务");//商品描述
				intent.putExtra("price", UniversalUtils.getString2Float(raceCompanyInfo.getSzPayMent()));//商品单价
				
				if(ChoiceServiceActivity.instance != null){
					ChoiceServiceActivity.instance.finish();
				}
				
				startActivity(intent);
				finish();
			}else{
				//发送选定服务公司的请求
				loadChoiceCompanyData();
			}
			
			
			break;
		default:
			break;
		}

	}
	
	
	
	
//	/**
//	 * 查询数据库 中 公司信息
//	 */
//	private void querySerAddrData(){
//		DataBaseService ds = new DataBaseService(this);
//		String sql1 = DbOprationBuilder.queryBuilderby("*", "userAddr","sqn", orderInfo.getIUserSrvedIndex()+"");
//		serAddrInfo = ds.query(sql1);
//		
//		if(serAddrInfo.size() == 0){
//			order_contact.setText("联系人："+"");//联系人
//			order_phone.setText("");//电话
//			order_addr.setText("联系地址 ："+"");//联系地址
//			return;
//		}
//		
//		order_contact.setText("联系人："+serAddrInfo.get(0).get("objName"));//联系人
//		order_phone.setText(serAddrInfo.get(0).get("objTel"));//电话
//		order_addr.setText("联系地址 ："+serAddrInfo.get(0).get("Addr"));//联系地址
//	}
	
	
	
	
	
	/**
	 * 获取订单 详情
	 */
	public void loadOrderDetailData(){
		seqRaceDetail = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = new MyApplication().userId;// 为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = uOrderID;// 选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandlHsUserOrderDetail_ReqToPro(hsNetCommonReq,seqRaceDetail);
		HouseSocketConn.pushtoList(connData);
		
		progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("connData订单  详情  的请求--sequence="+seqRaceDetail +"/"+connData+"=============");
	}
	
	
	
	/**
	 * 用户选择指定公司   数据请求
	 */
	private void loadChoiceCompanyData(){
		seqChoiceCompanyNo = MyApplication.SequenceNo++;
		HsCommon_Notify hsCommon_Notify  = new MsgInncDef().new HsCommon_Notify();
		hsCommon_Notify.uUserID = myApplication.userId;
		hsCommon_Notify.uCompanyID = raceCompanyInfo.getUCompanyID() ;
		hsCommon_Notify.iOrderID = uOrderID;
		byte[] connData = HandleNetSendMsg.HandleHsChoiceCompany_ReqToPro(hsCommon_Notify,seqChoiceCompanyNo);
		HouseSocketConn.pushtoList(connData);
		
		progressDialog = new CustomProgressDialog(this);
		progressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("用户选择指定公司的请求--sequence="+seqChoiceCompanyNo +"/"+Arrays.toString(connData)+"=============");
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

				if (seqRaceDetail == iSequence) {
					processRaceDetailData(recvTime);
				}
				else if(seqChoiceCompanyNo == iSequence){
					processChoiceCompanyData(recvTime);
				}
				
			}
		}
	};
	
	/**
	 * 处理抢单详情页面，订单详情响应的数据
	 * @param recvTime
	 */
	private void processRaceDetailData(long recvTime){
		HsUserOrderDetail_Resp hsUserOrderListResp = (HsUserOrderDetail_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsUserOrderListResp == null){
			return;
		}
		
		LogUtils.i("抢单详情页面--订单详情  返回数据成功");
		progressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsUserOrderListResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			orderDetailInfo = hsUserOrderListResp.orderDetail;
			
			Message message =new Message();
			message.what = MSG_LOAD_SUCCESS;
			handler.sendMessage(message);
			
			LogUtils.i("抢单详情页面--订单详情  获取成功"+"==========");		
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsUserOrderListResp.eOperResult);
			
			Message message =new Message();
			message.what = MSG_LOAD_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			
			LogUtils.i("抢单详情页面--订单详情获取失败"+result+"=============");					
		}
	}
	
	
	
	/**
	 * 处理用户选择指定公司响应的数据
	 * @param recvTime
	 */
	private void processChoiceCompanyData(long recvTime){
		HsNetCommon_Resp hsNetCommon_Resp  = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsNetCommon_Resp == null){
			return;
		}
		LogUtils.i("用户选择指定公司 返回数据成功");
		progressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsNetCommon_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			
			setDbStarCompanyData(raceCompanyInfo);
			
			//选定服务公司成功后，需要直接跳转到支付页面
			Intent intent = new Intent(RaceDetailActivity.this, OrderActivity.class);
			
//			intent.putExtra("orderDetailInfo", (Serializable)orderDetailInfo);//订单详情
			
			intent.putExtra("uOrderID", orderDetailInfo.getUOrderID());
			intent.putExtra("szOrderValue", szOrderValue);//订单号
			intent.putExtra("szOrderStateName", orderDetailInfo.getSzOrderStateName());
			
			intent.putExtra("goodsname", orderDetailInfo.getSzSerivceTypeName());//商品名称
			intent.putExtra("goodsdetil", "歇歇服务");//商品描述
			intent.putExtra("price", UniversalUtils.getString2Float(raceCompanyInfo.getSzPayMent()));//商品单价
			
			if(ChoiceServiceActivity.instance != null){
				ChoiceServiceActivity.instance.finish();
			}
			
			startActivity(intent);
			finish();
			
			Message message =new Message();
			message.what = MSG_CHOICE_COMPANY_SUCCESS;
			handler.sendMessage(message);
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsNetCommon_Resp.eOperResult);
			
			Message message =new Message();
			message.what = MSG_CHOICE_COMPANY_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			
			LogUtils.i("用户选择指定公司  失败"+result+"=============");					
		}
	}
	
	
	/**
	 *  若长时间服务端无返回，则停止进度条
	 */
		Runnable loadTimerRunnable = new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(LOAD_TIMER_OVER);
			}
		};
		
		
		
	/**
	 * 将明星公司数据写入数据库中
	 * 	
	 * @param choiceCompanyInfo 用户选定的明星公司数据
	 */
	private void setDbStarCompanyData(HsRaceCompanyInfo_Pro choiceCompanyInfo) {
		DataBaseService ds = new DataBaseService(this);
		String sql = DbOprationBuilder.queryBuilderby("*", "starcompany",
				"iCompanyID", choiceCompanyInfo.getUCompanyID() + "");
		List<Map<String, String>> starCompanyDataList = ds.query(sql);
		if (starCompanyDataList.size() != 0) {
			return;
		}

		// 抢单公司信息中 无公司介绍，多了抢单公司要求的预付款金额
		Map<String, String> startCompanyMap = new HashMap<String, String>();
		startCompanyMap.put("iCompanyID", choiceCompanyInfo.getUCompanyID()+ "");
		startCompanyMap.put("iOrderNum", choiceCompanyInfo.getIOrderNum() + "");
		startCompanyMap.put("iValuNum", choiceCompanyInfo.getIValuNum() + "");
		startCompanyMap.put("iStarLevel", choiceCompanyInfo.getIStarLevel()+ "");
		startCompanyMap.put("iAuthFlag", choiceCompanyInfo.getIAuthFlag() + "");
		startCompanyMap.put("iFiling", choiceCompanyInfo.getIFiling() + "");
		startCompanyMap.put("iOffLine", choiceCompanyInfo.getIOffLine() + "");
		startCompanyMap.put("iNominate", choiceCompanyInfo.getINominate() + "");
		startCompanyMap.put("szName", choiceCompanyInfo.getSzName() + "");
		startCompanyMap.put("szAddr", choiceCompanyInfo.getSzAddr() + "");
		startCompanyMap.put("szServiceInfo",choiceCompanyInfo.getSzServiceInfo() + "");
		startCompanyMap.put("szCreateTime", choiceCompanyInfo.getSzCreateTime()+ "");
		startCompanyMap.put("szCompanyUrl", choiceCompanyInfo.getSzCompanyUrl()+ "");
		startCompanyMap.put("szCompanyInstr", "");

		String insertSql = DbOprationBuilder.insertStarCompanyAllBuilder(
				MyApplication.userId, startCompanyMap);
		ds.insert(insertSql);
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
