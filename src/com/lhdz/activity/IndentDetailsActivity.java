package com.lhdz.activity;

import java.io.Serializable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderDailInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.entity.ServiceListInfoEntity;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserOrderDetail_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

/**
 *  订单详情--抢单中,待支付,服务中,已完成
 * @author wangf
 *
 */
public class IndentDetailsActivity extends BaseActivity implements OnClickListener {
	
	private TextView title;//页面标题
	private TextView back;//返回按钮
	
	private int uOrderID = 0;//订单id
//	private String szOrderValue = "";//订单号
	private Map<String, String> orderListInfo;//订单列表中的信息
	private int iOrderState = 0;//订单状态
	private HsUserOrderDailInfo_Pro orderDetailInfo = null;
	
	private Button btnFirst;//
	private Button btnSecond;//
	private LinearLayout linBtnLayout = null;
	
	private TextView order_contact = null;
	private TextView order_phone = null;
	private TextView order_addr = null;
	
	private TextView order_no = null;
	private TextView order_state = null;
	private TextView order_servicetype = null;
	private TextView order_area = null;
	private TextView order_price = null;
	private TextView order_heart_price = null;
	private TextView order_servicelong = null;
	private TextView order_servicetime = null;
	private TextView order_content = null;
	
	private LinearLayout lay_state_service = null;
	private TextView order_company = null;
	private RatingBar order_rating = null;
	private TextView order_companyaddr = null;
	
	private LinearLayout lay_state_race = null;
	private TextView order_race_num = null;
	
	private LinearLayout indent_detail_servicelist = null;
	private LinearLayout ll_expand_service_list = null;
	private LinearLayout ll_add_service_list = null;
	private ImageView iv_expand_service_arrow = null;
	
	private LinearLayout order_complaint;//投诉的显示
	
	private final static int MSG_INDENT_DETAIL_SUCCESS = 0;
	private final static int MSG_INDENT_DETAIL_ERROR = 1;
	private final static int MSG_BACK_OUT_ORDER_ERROR = 2;
	private final static int LOAD_TIMER_OVER = 3;
	
	private final static int REQ_CODE_COMPLAINT = 200;//进入评价页面的RequestCode
	
	List<Map<String, String>> serAddrInfo = null;//用户地址信息
	
	private int seqIndentDetail = -1;//订单详情的sequence
	private int seqBackOutOrder = -1;//撤单的sequence
	private int seqAffirmFinish = -1;//确定完成的sequence
	
	private CustomProgressDialog mCustomProgressDialog;
	private MyApplication myApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_indent_details);
		
		myApplication = (MyApplication) getApplication();
		
		uOrderID = getIntent().getIntExtra("uOrderID", -1);//订单id
//		szOrderValue = getIntent().getStringExtra("szOrderValue");//订单号
		orderListInfo = (Map<String, String>) getIntent().getSerializableExtra("orderListInfo");
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		loadOrderDetailData();//获取订单详情数据
		// 初始化控件
		initViews();
		
		
		
		
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case MSG_INDENT_DETAIL_SUCCESS:
				//数据获取成功，为view显示数据
				querySerAddrData();
				setViewData();
				setButtonDataByState();
				break;
			case MSG_INDENT_DETAIL_ERROR:
//				Toast.makeText(IndentDetailsActivity.this, "获取订单详情失败"+msg.obj.toString(), 0).show();
				break;
			case LOAD_TIMER_OVER:
				//服务端无返回，停止下拉刷新
				handler.removeCallbacks(loadTimerRunnable);
				mCustomProgressDialog.dismiss();
				break;	
			default:
				break;
			}
		}
	};

	
	/**
	 * 初始化页面控件
	 */
	private void initViews() {
		title = (TextView) findViewById(R.id.tv_title);
		title.setText("订单详情");
		back = (TextView) findViewById(R.id.public_back);
		back.setOnClickListener(this);
		
		btnFirst = (Button) findViewById(R.id.btn_indent_details_first);//第一个按钮
		btnSecond = (Button) findViewById(R.id.btn_indent_details_second);//第二个按钮
		linBtnLayout = (LinearLayout) findViewById(R.id.indent_btn_lin);//按钮的layout
		btnFirst.setOnClickListener(this);
		btnSecond.setOnClickListener(this);
		
		
		order_contact = (TextView) findViewById(R.id.order_contact);//联系人
		order_phone = (TextView) findViewById(R.id.order_phone);//联系电话
		order_addr = (TextView) findViewById(R.id.order_addr);//联系地址
		
		order_no = (TextView) findViewById(R.id.order_no);//订单编号
		order_state = (TextView) findViewById(R.id.order_state);//订单状态
		order_servicetype = (TextView) findViewById(R.id.order_servicetype);//服务类型
		order_area = (TextView) findViewById(R.id.order_area);//房屋面积
		order_price = (TextView) findViewById(R.id.order_price);//订单金额
		order_heart_price = (TextView) findViewById(R.id.order_heart_price);//心理价位
//		order_servicelong = (TextView) findViewById(R.id.order_servicelong);
		order_servicetime = (TextView) findViewById(R.id.order_servicetime);//服务时间
		order_content = (TextView) findViewById(R.id.order_content);//服务备注
		
		order_complaint = (LinearLayout) findViewById(R.id.order_complaint);//投诉受理的显示
		
		indent_detail_servicelist = (LinearLayout) findViewById(R.id.indent_detail_servicelist);//服务清单整体
		ll_expand_service_list = (LinearLayout) findViewById(R.id.ll_expand_service_list);//服务伸缩
		ll_expand_service_list.setOnClickListener(this);
		iv_expand_service_arrow = (ImageView) findViewById(R.id.iv_expand_service_arrow);//服务伸缩的图标
		ll_add_service_list = (LinearLayout) findViewById(R.id.ll_add_service_list);//动态添加服务列表
				
		lay_state_service = (LinearLayout) findViewById(R.id.lay_state_service);//公司信息的layout
		order_company = (TextView) findViewById(R.id.order_company);//服务公司名称
		order_rating = (RatingBar) findViewById(R.id.order_rating);//公司星级
		order_companyaddr = (TextView) findViewById(R.id.order_companyaddr);//公司地址
		
		lay_state_race = (LinearLayout) findViewById(R.id.lay_state_race);//抢单数量的layout
		order_race_num = (TextView) findViewById(R.id.order_race_num);//抢单数量
		
		lay_state_race.setVisibility(View.GONE);//默认将此layout隐藏
		lay_state_service.setVisibility(View.GONE);//默认将此layout隐藏
	}
	
	
	/**
	 * 为 页面控件设置数据
	 */
	private void setViewData(){
		
		iOrderState = orderDetailInfo.getIOrderState();//订单状态
//		szOrderValue = orderDetailInfo.getSzOrderCode();//订单号
		
		order_no.setText("订单号："+orderDetailInfo.getSzOrderCode());
		order_state.setText(orderDetailInfo.getSzOrderStateName());//订单状态
		order_servicetype.setText("服务类型："+orderDetailInfo.getSzSerivceTypeName());
		
		//根据服务类型判断是否需要显示房屋面积
		if (UniversalUtils.isInputArea(orderDetailInfo.getIServiceType())) {
			order_area.setText("房屋面积："+orderDetailInfo.getSzUintArea()+" 平米");
			order_area.setVisibility(View.VISIBLE);
		}else{
			order_area.setVisibility(View.GONE);
		} 
		
		//若订单状态为  抢单中，则显示心理价位，不显示订单金额,
		//      若   不为抢单中，则显示订单金额，不显示心理价位
		if(NetHouseMsgType.ORDERSTATE_RACING == iOrderState){
			order_price.setVisibility(View.GONE);
			order_heart_price.setVisibility(View.VISIBLE);
			order_heart_price.setText("心理价位："+UniversalUtils.getString2Float(orderDetailInfo.getSzHeartPrice())+" 元");
		}else{
			order_heart_price.setVisibility(View.GONE);
			order_price.setVisibility(View.VISIBLE);
			order_price.setText("订单金额："+UniversalUtils.getString2Float(orderDetailInfo.getSzOrderPrice())+" 元");
		}
		
//		order_servicelong.setText("服务时长："+orderInfo.getIUsingTimes());
		order_servicetime.setText("服务时间："+ UniversalUtils.subTimeToMinute(orderDetailInfo.getSzSrvBeginTime()));
		order_content.setText(orderDetailInfo.getSzRemark());//备注
		
		//若订单状态为抢单中，则显示抢单数量，不显示公司信息；若不为抢单中，则显示公司信息
		if(NetHouseMsgType.ORDERSTATE_RACING == iOrderState){
			lay_state_race.setVisibility(View.VISIBLE);
			order_race_num.setText(orderDetailInfo.getIOrderRaceNum()+"");
		}else{
			lay_state_service.setVisibility(View.VISIBLE);
			order_company.setText("服务公司："+orderDetailInfo.getSzCompanyName());
			order_rating.setRating(UniversalUtils.processRatingLevel(orderDetailInfo.getIStarLevel()));//公司星级
			order_companyaddr.setText("地址："+orderDetailInfo.getSzCompanyAddr());
		}
		
		
		//投诉状态时显示投诉受理的layout
		if(NetHouseMsgType.ORDERSTATE_COMPLAINT == iOrderState){
			order_complaint.setVisibility(View.VISIBLE);
		}
		
		
		//预约下单时需要显示服务清单
		String szServiceItem = orderDetailInfo.getSzServItem();
		if(!UniversalUtils.isStringEmpty(szServiceItem)){
			List<ServiceListInfoEntity> listInfoEntities = UniversalUtils.parseServiceList(szServiceItem);
			setServiceData(listInfoEntities);
			
			order_area.setVisibility(View.GONE);//预约下单时不需要房屋面积
			order_heart_price.setVisibility(View.GONE);//预约下单时不需要心理价位
		}
		
	}

	
	/**
	 * 为服务清单控件设置数据
	 */
	private void setServiceData(List<ServiceListInfoEntity> listInfoEntities){
		int iChoiceNum = 0;//已选择服务类别的数量
		
		indent_detail_servicelist.setVisibility(View.GONE);
		ll_add_service_list.removeAllViews();
		
		for (int i = 0; i < listInfoEntities.size(); i++) {
			if (listInfoEntities.get(i).getServiceNum() != 0) {
				iChoiceNum++;
				ServiceListInfoEntity serviceInfo = listInfoEntities.get(i);
				
				View view = LayoutInflater.from(this).inflate(R.layout.item_appointment_service, null);
				TextView item_service_name = (TextView) view.findViewById(R.id.item_service_name);
				TextView item_service_num = (TextView) view.findViewById(R.id.item_service_num);
				TextView item_service_price = (TextView) view.findViewById(R.id.item_service_price);

				item_service_name.setText(serviceInfo.getServiceName());
				
				String serviceNum = UniversalUtils.getString2Float(serviceInfo.getServicePrice())+"/"+serviceInfo.getServiceUnit()+" x "+serviceInfo.getServiceNum();
				item_service_num.setText("¥"+serviceNum);
				
				double itemPrice = Double.parseDouble(serviceInfo.getServicePrice())*serviceInfo.getServiceNum();
				item_service_price.setText("¥"+UniversalUtils.getPoint2Float(itemPrice));

				ll_add_service_list.addView(view);
			}
		}
		//当用户选择的服务类别数量大于0时，需要显示服务列表和总价
		if(iChoiceNum > 0){
			indent_detail_servicelist.setVisibility(View.VISIBLE);
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
	


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.public_back:
			this.finish();
			break;
		case R.id.btn_indent_details_first:
			
			setFirstButtonClick();
			
			break;
		case R.id.btn_indent_details_second:
			
			setSecondButtonClick();
			
			break;
			
		case R.id.ll_expand_service_list:
			
			serviceListGoneOrVisibility();// 服务清单的显示与隐藏
			
			break;
		default:
			break;
		}

	}
	
	
	/**
	 * 根据不同状态为第一个按钮设置跳转
	 */
	private void setFirstButtonClick(){
		//抢单中--撤单
		if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
			showBackOutOrderDialog();//撤单对话框
		}
		//待支付--撤单
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_PAY){
			showBackOutOrderDialog();//撤单对话框
		}
		//服务中--投诉
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE){
//			Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
			Intent intent=new Intent(this,ComplaintsActivity.class);
			intent.putExtra("orderListInfo", (Serializable)orderListInfo);//订单列表中的信息
			startActivityForResult(intent, REQ_CODE_COMPLAINT);
//			startActivity(intent);
		}
		//已完成--此时无此按钮
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_FINISH){
//			Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	/**
	 * 根据不同的状态为第二个按钮设置跳转
	 */
	private void setSecondButtonClick(){
		//抢单中--选择服务
		if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
			Intent intent = new Intent(this, ChoiceServiceActivity.class);
//			intent.putExtra("orderInfo", (Serializable)orderDetailInfo);
			intent.putExtra("uOrderID", uOrderID);//订单id
			intent.putExtra("szOrderValue", orderDetailInfo.getSzOrderCode());//订单号
			intent.putExtra("szOrderStateName", orderDetailInfo.getSzOrderStateName());//订单状态
			startActivity(intent);
			finish();
		}
		//待支付--立即支付
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_PAY){
			Intent intent = new Intent(this, OrderActivity.class);
			
			intent.putExtra("uOrderID", orderDetailInfo.getUOrderID());//订单orderId
			intent.putExtra("szOrderValue", orderDetailInfo.getSzOrderCode());//订单编号
			intent.putExtra("szOrderStateName", orderDetailInfo.getSzOrderStateName());//订单状态
			
			intent.putExtra("goodsname", orderDetailInfo.getSzSerivceTypeName());//商品名称
			intent.putExtra("goodsdetil", "歇歇服务");//商品描述
			intent.putExtra("price", UniversalUtils.getString2Float(orderDetailInfo.getSzOrderPrice()));//商品单价
			
			startActivity(intent);
			finish();
		}
		//服务中--确定完成
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE){
			showAffirmFinishDialog();//确定完成  对话框
		}
		//已完成--评价
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_FINISH){
			Toast.makeText(this, "敬请期待", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	
	/**
	 * 根据订单状态为按钮设置数据
	 */
	private void setButtonDataByState(){
		linBtnLayout.setVisibility(View.VISIBLE);
		//抢单中--撤单+选择服务
		if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
			order_state.setTextColor(getResources().getColor(R.color.indent_orange));
			if(orderDetailInfo.getIOrderRaceNum() == 0){
				btnFirst.setVisibility(View.VISIBLE);
				btnSecond.setVisibility(View.VISIBLE);
				btnFirst.setText("撤单");
				btnSecond.setText("选择服务");
				
				btnSecond.setClickable(false);
				btnSecond.setBackgroundResource(R.drawable.shape_btn_click_not);
			}else{
				btnFirst.setVisibility(View.VISIBLE);
				btnSecond.setVisibility(View.VISIBLE);
				btnFirst.setText("撤单");
				btnSecond.setText("选择服务");
			}
		}
		//待支付--撤单+立即支付
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_PAY){
			order_state.setTextColor(getResources().getColor(R.color.indent_red));
			btnFirst.setVisibility(View.VISIBLE);
			btnSecond.setVisibility(View.VISIBLE);
			btnFirst.setText("撤单");
			btnSecond.setText("立即支付");
		}
		//服务中--投诉+确定完成
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE){
			order_state.setTextColor(getResources().getColor(R.color.indent_green));
			btnFirst.setVisibility(View.VISIBLE);
			btnSecond.setVisibility(View.VISIBLE);
			btnFirst.setText("投诉");
			btnSecond.setText("确定完成");
		}
		//已完成--评价
		else if(iOrderState == NetHouseMsgType.ORDERSTATE_FINISH){
			order_state.setTextColor(getResources().getColor(R.color.indent_gray));
			btnFirst.setVisibility(View.GONE);
			btnSecond.setVisibility(View.GONE);//一期无此功能，评价按钮隐藏
			btnSecond.setText("评价");
		}
		//其他状态
		else{
			order_state.setTextColor(getResources().getColor(R.color.indent_red));
			btnFirst.setVisibility(View.GONE);
			btnSecond.setVisibility(View.GONE);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == Define.RESULTCODE_COMPLAINT){
			boolean isComplaintSuccess = data.getBooleanExtra("isComplaintSuccess", false);
			if(isComplaintSuccess){
				//若投诉成功，则需要刷新页面，并且按钮不可点击
				linBtnLayout.setVisibility(View.GONE);
				order_state.setTextColor(getResources().getColor(R.color.indent_red));
				order_state.setText("投诉");//订单状态
			}else{
				//若未投诉成功，不做任何处理
			}
			
		}
		
	}
	
	
	
	
	/**
	 * 查询数据库 中 公司信息
	 */
	private void querySerAddrData(){
		DataBaseService ds = new DataBaseService(this);
		String sql1 = DbOprationBuilder.queryBuilderby("*", "userAddr","sqn", orderDetailInfo.getIUserSrvedIndex()+"");
		serAddrInfo = ds.query(sql1);
		
		if(serAddrInfo.size() == 0){
			order_contact.setText("联系人："+"");//联系人
			order_phone.setText("");//电话
			order_addr.setText("联系地址 ："+"");//联系地址
			return;
		}
		
		order_contact.setText("联系人："+serAddrInfo.get(0).get("objName"));//联系人
		order_phone.setText(serAddrInfo.get(0).get("objTel"));//电话
		order_addr.setText("联系地址 ："+serAddrInfo.get(0).get("longAddr"));//联系地址
	}
	
	
	
	/**
	 * 撤单 对话框
	 */
	private void showBackOutOrderDialog(){
		SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("确定撤销该订单吗？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				loadBackOutOrderData();//发送撤单请求
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		dialog.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		
		dialog.show();
	}
	
	
	/**
	 * 确定完成   对话框
	 */
	private void showAffirmFinishDialog(){
		SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("该订单是否完成？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				loadAffirmFinishData();//发送 确定完成 请求
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		dialog.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				sweetAlertDialog.dismissWithAnimation();
			}
		});
		
		dialog.show();
	}
	
	
	
	
	
	/**
	 * 获取订单 详情
	 */
	public void loadOrderDetailData(){
		seqIndentDetail = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = myApplication.userId;// 为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = uOrderID;// 选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandlHsUserOrderDetail_ReqToPro(hsNetCommonReq,seqIndentDetail);
		HouseSocketConn.pushtoList(connData);
		
		mCustomProgressDialog =new CustomProgressDialog(this);
		mCustomProgressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("connData订单  详情  的请求--sequence="+seqIndentDetail +"/"+connData+"=============");
		
	}
	
	
	/**
	 * 撤单 请求
	 */
	public void loadBackOutOrderData(){
		seqBackOutOrder = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = myApplication.userId;// 为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = uOrderID;// 选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandleHsBackOutOrder_ReqToPro(hsNetCommonReq,seqBackOutOrder);
		HouseSocketConn.pushtoList(connData);
		
		mCustomProgressDialog = new CustomProgressDialog(this);
		mCustomProgressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("用户撤单  的请求--sequence="+seqBackOutOrder +"/"+connData+"=============");
		
	}
	
	
	/**
	 * 确定完成  请求
	 */
	public void loadAffirmFinishData(){
		seqAffirmFinish = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = myApplication.userId;// 为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = uOrderID;// 选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandleHsAffirmFinishOrder_ReqToPro(hsNetCommonReq,seqAffirmFinish);
		HouseSocketConn.pushtoList(connData);
		
		mCustomProgressDialog = new CustomProgressDialog(this);
		mCustomProgressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("确定完成  的请求--sequence="+seqAffirmFinish +"/"+connData+"=============");
		
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

				if (seqIndentDetail == iSequence) {
					processIndentDetailData(recvTime);
				}
				else if(seqBackOutOrder == iSequence){
					processBackOutOrderData(recvTime);
				}
				else if(seqAffirmFinish == iSequence){
					processAffirmFinishData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理订单详情响应的数据
	 * @param iSequence
	 */
	private void processIndentDetailData(long recvTime){
		HsUserOrderDetail_Resp hsUserOrderListResp = (HsUserOrderDetail_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsUserOrderListResp == null){
			return;
		}
		
		mCustomProgressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsUserOrderListResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			orderDetailInfo = hsUserOrderListResp.orderDetail;
			
			Message message =new Message();
			message.what = MSG_INDENT_DETAIL_SUCCESS;
			handler.sendMessage(message);
			
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsUserOrderListResp.eOperResult);
			
			Message message =new Message();
			message.what = MSG_INDENT_DETAIL_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			
			LogUtils.i("订单详情获取失败"+result+"=============");					
		}
	}
	
	
	/**
	 * 处理 用户撤单 响应的数据
	 * @param iSequence
	 */
	private void processBackOutOrderData(long recvTime){
		HsNetCommon_Resp hsNetCommon_Resp = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsNetCommon_Resp == null){
			return;
		}
		
		mCustomProgressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsNetCommon_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			
			
			//撤单成功后需要删除数据库中该订单
			DataBaseService ds = new DataBaseService(this);
			String deleteSql = DbOprationBuilder.deleteBuilderby("orderList", "uOrderID", uOrderID+"");
			ds.delete(deleteSql);
			
			ToastUtils.show(this, "撤销订单成功", Toast.LENGTH_SHORT);
			LogUtils.i("撤单成功");
			finish();
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsNetCommon_Resp.eOperResult);
			
			ToastUtils.show(this, result, Toast.LENGTH_SHORT);
			
//			Message message = new Message();
//			message.what = MSG_BACK_OUT_ORDER_ERROR;
//			message.obj = result;
//			handler.sendMessage(message);
			
			LogUtils.i("撤单失败"+result);
		}
	}
	
	
	/**
	 * 处理   确定完成   响应的数据
	 * @param iSequence
	 */
	private void processAffirmFinishData(long recvTime){
		HsNetCommon_Resp hsNetCommon_Resp = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsNetCommon_Resp == null){
			return;
		}
		
		mCustomProgressDialog.dismiss();
		handler.removeCallbacks(loadTimerRunnable);
		if(hsNetCommon_Resp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			
			Toast.makeText(this, "确定完成成功", Toast.LENGTH_SHORT).show();
			LogUtils.i("确定完成成功");
			finish();
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsNetCommon_Resp.eOperResult);
			
			Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
			
//			Message message = new Message();
//			message.what = MSG_BACK_OUT_ORDER_ERROR;
//			message.obj = result;
//			handler.sendMessage(message);
			
			LogUtils.i("确定完成失败"+result);
		}
	}
	
	
	/**
	 *  若长时间服务端无返回，则停止停止进度条
	 */
	Runnable loadTimerRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(LOAD_TIMER_OVER);
		}
	};
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}
	
}
