package com.lhdz.activity;


/**
 * 选择地址界面
 * @author 王哲
 * @date 2015-8-26
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.adapter.AddressAdapter;
import com.lhdz.adapter.AddressAdapter.AddrCallBackLister;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserServiceAddrInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserGetServiceAddrs_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.Define;
import com.lhdz.util.GetCityDataUtil;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

public class AddressActivity extends BaseActivity implements OnClickListener{
	
	
	private AddressAdapter mAdapter;
	private TextView title,add;
	private Button addrSureBtn;
	
	private ListView mListView;
	Map<String, String> serviceAddSel = null;
	
	MyApplication myApplication = null; 
	
	private final static int updateListView = 0;
	private final static int updateListViewFail = 1;
	private final static int deleteSuccess = 2;
	private final static int deleteFail = 3;
	
	private List<Map<String, String>> mListData = new ArrayList<Map<String,String>>();
	
	private int seqGetServiceAddrNo = -1;
	private int seqDeleteServiceAddrNo = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_service_address);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		registerReceiver(mReceiver, filter);
		
		
		myApplication = (MyApplication) getApplication();
		//获取province_data.xml中的城市数据
//		getProvinceData();
		//初始化控件
		initViews();
		backArrow();
	}
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		queryServiceAdd();
	
		int dbUserId = 0;
		if(mListData.size() != 0){
			dbUserId = Integer.parseInt(mListData.get(0).get("userId"));
		}
		if(mListData.size() == 0 || dbUserId != myApplication.userId){
			loadAddrListData();//请求地址列表
		}
		
	}
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			
			switch (msg.what) {
			case updateListView:
				//获取服务地址列表成功
				queryServiceAdd();
				break;
			case updateListViewFail:
				//获取服务地址列表失败
				ToastUtils.show(AddressActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT);
				break;
			case deleteSuccess:
				//删除地址成功
//				Toast.makeText(AddressActivity.this, "删除服务地址成功", Toast.LENGTH_SHORT).show();
				break;
			case deleteFail:
				//删除地址失败
//				Toast.makeText(AddressActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
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
		title.setText("服务地址");
		add = (TextView) findViewById(R.id.bt_del);
		add.setOnClickListener(this);
		add.setText("添加");
		addrSureBtn = (Button) findViewById(R.id.addr_sure_bt);
		addrSureBtn.setOnClickListener(this);
		mListView = (ListView) findViewById(R.id.addr_lv);
		mAdapter=new AddressAdapter(this);
		mAdapter.setAddrCallBackLister(new AddrCallBackLister() {
			
			@Override
			public void onClickEdit(Map<String, String> serviceAdd) {
				//编辑按钮的监听
//				if(isAddrUsed(serviceAdd.get(""))){
//					
//				}
				Intent intent = new Intent(AddressActivity.this, AddServiceAddrsActivity.class);//修改地址
				intent.putExtra("modifyAddr", (Serializable)serviceAdd);
				intent.putExtra("isModifyAddr", true);
				startActivity(intent);
			}
			
			@Override
			public void onClickDelete(Map<String, String> serviceAdd) {
				//删除按钮的监听,首先删除本地数据库的地址，再去请求删除服务器端的数据
				//现在需要逻辑删除，将该条地址的删除状态改变，但该地址仍然存在于数据库中
				showDeleteAddrDialog(serviceAdd);
				
//				DataBaseService ds = new DataBaseService(getApplicationContext());
//				String deleteSql = DbOprationBuilder.deleteBuilderby("userAddr", "id", serviceAdd.get("id"));
//				ds.delete(deleteSql);
//				//请求删除服务器的地址
//				loadAddrDeleteData(serviceAdd);
			}
			
			@Override
			public void onClickDefault(Map<String, String> serviceAdd) {
				//设置默认的监听
				DataBaseService ds = new DataBaseService(getApplicationContext());
				
				String insertSql = DbOprationBuilder.updateBuider("userAddr", "selecState", "0", "selecState", "1");
				ds.update(insertSql);
				String insertSql1= DbOprationBuilder.updateBuider("userAddr", "selecState", "1", "id", serviceAdd.get("id"));
				ds.update(insertSql1);
				
				queryServiceAdd();
			}
		});
		
		
		mListView.setAdapter(mAdapter);
		
	}
	
	
	
//	/**
//	 * 判断该地址是否在订单中使用
//	 * 若使用该地址的订单处于未完成状态，则不可编辑，不可删除
//	 * @param addrSqn
//	 */
//	private boolean isAddrUsed(String addrSqn) {
//		queryOrderListBy(addrSqn);
//		if (dbOrderList.size() == 0) {
//			return false;
//		}
//		for (int i = 0; i < dbOrderList.size(); i++) {
//			Map<String, String> orderMap = dbOrderList.get(i);
//			if (NetHouseMsgType.ORDERSTATE_FINISH == Integer.parseInt(orderMap.get("iOrderState"))) {
//				return true;
//			}
//		}
//
//		return false;
//	}
	
	
	
//	/**
//	 * 地址 不可删除 不可修改 对话框
//	 */
//	private void showAddrIsUsedDialog(){
//		SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
//		dialog.setConfirmClickListener(new OnSweetClickListener() {
//			
//			@Override
//			public void onClick(SweetAlertDialog sweetAlertDialog) {
//				// TODO Auto-generated method stub
//				sweetAlertDialog.dismissWithAnimation();
//			}
//		});
//		dialog.show();
//	}
	
	
	
	
	/**
	 * 确认删除地址对话框
	 */
	private void showDeleteAddrDialog(final Map<String, String> serviceAdd){
		SweetAlertDialog dialog = new SweetAlertDialog(AddressActivity.this,
				SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("确定删除该条地址吗？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				//首先删除本地数据库的地址，再去请求删除服务器端的数据
				DataBaseService ds = new DataBaseService(getApplicationContext());
//				String deleteSql = DbOprationBuilder.deleteBuilderby("userAddr", "id", serviceAdd.get("id"));
				String updateSql = DbOprationBuilder.updateBuider("userAddr", "isDelete", "1","id", serviceAdd.get("id"));
				ds.update(updateSql);
				queryServiceAdd();//本地删除服务地址之后，重新查询一次数据库，并更新显示
				//请求删除服务器的地址
				loadAddrDeleteData(serviceAdd);
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
	 * 查询数据库中的服务地址
	 */
	private void queryServiceAdd(){
		mListData.clear();
		DataBaseService ds = new DataBaseService(this);
//		String sql = DbOprationBuilder.queryAllBuilder("userAddr");
		//查询数据库中未删除的地址信息并显示--其中：isDelete状态标识为 0：未删除，1：已删除
		String sql = DbOprationBuilder.queryBuilderby("*", "userAddr", "isDelete", "0");
		mListData = ds.query(sql);
		
		if(mListData.size() <= 0){
			serviceAddSel = null;
			mAdapter.setData(mListData);
			return;
		}
		int dbUserId = Integer.parseInt(mListData.get(0).get("userId"));
		if(dbUserId != myApplication.userId){
			mListData.clear();
		}
		mAdapter.setData(mListData);
		getSelectAddr();
	}
	
	
	/**
	 * 获取当前选中的地址
	 */
	private void getSelectAddr(){
		for (int i = 0; i < mListData.size(); i++) {
			if(Integer.parseInt(mListData.get(i).get("selecState")) == 1 ){
				serviceAddSel = mListData.get(i);
			}
		}
	}
	

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.bt_del:
			intent.setClass(AddressActivity.this, AddServiceAddrsActivity.class);//添加新地址
			startActivity(intent);
			break;
		case R.id.addr_sure_bt:
			Intent data = new Intent();
			data.putExtra("serviceAdd", (Serializable)serviceAddSel);
			setResult(Define.RESULTCODE_SERVERADDR, data);
			finish();
			break;
		default:

			break;
		}
		
	}
	
	
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
					Intent data = new Intent();
					data.putExtra("serviceAdd", (Serializable)serviceAddSel);
					setResult(Define.RESULTCODE_SERVERADDR, data);
					AddressActivity.this.finish();

					break;
				}
			}
		});
	}
	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		super.onBackPressed();
		
		Intent data = new Intent();
		data.putExtra("serviceAdd", (Serializable)serviceAddSel);
		setResult(Define.RESULTCODE_SERVERADDR, data);
		finish();
	}
	
	
	
	
	/**
	 * 用户提取服务地址 请求
	 */
	public void loadAddrListData() {
		seqGetServiceAddrNo = MyApplication.SequenceNo++;
		HsNetCommon_Req addrInfo_Req = new MsgInncDef().new HsNetCommon_Req();
		addrInfo_Req.iSrcID = myApplication.userId;
		addrInfo_Req.iSelectID = 0;
		
		byte[] connData = HandleNetSendMsg.HandleHsUserGetServiceAddrs_ReqToPro(addrInfo_Req,seqGetServiceAddrNo);
		
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("提取服务地址 请求--sequence="+seqGetServiceAddrNo +"/"+ Arrays.toString(connData) + "----------");

	}
	
	
	/**
	 * 用户删除服务地址 请求
	 */
	public void loadAddrDeleteData(final Map<String, String> serviceAdd) {
		seqDeleteServiceAddrNo = MyApplication.SequenceNo++;
		HsNetCommon_Req addrInfo_Req = new MsgInncDef().new HsNetCommon_Req();
		addrInfo_Req.iSrcID = myApplication.userId;
		addrInfo_Req.iSelectID = Integer.parseInt(serviceAdd.get("sqn"));
		
		byte[] connData = HandleNetSendMsg
				.HandleHsUserDeleteServiceAddrs_ReqToPro(addrInfo_Req,seqDeleteServiceAddrNo);
		
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("删除服务地址 请求--sequence="+seqGetServiceAddrNo +"/"+ Arrays.toString(connData) + "----------");
		
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

				if (seqGetServiceAddrNo == iSequence) {
					processGetServiceAddrData(recvTime);
				}
				else if(seqDeleteServiceAddrNo == iSequence){
					processDeleteServiceAddrData(recvTime);
				}
				
			}
		}
	};
	
	
	/**
	 * 处理获取服务地址响应的数据
	 * @param iSequence
	 */
	private void processGetServiceAddrData(long recvTime){
		HsUserGetServiceAddrs_Resp serviceAdd = (HsUserGetServiceAddrs_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(serviceAdd == null){
			return;
		}
		LogUtils.i("提取服务地址 返回数据成功");
		if (serviceAdd.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			
			List<HsUserServiceAddrInfo_Pro> serviceAddList = serviceAdd.addrList;
			LogUtils.i("提取地址成功"+"提取成功=======");
			
			DataBaseService ds = new DataBaseService(getApplicationContext());
			String deleteSql = DbOprationBuilder.deleteBuilder("userAddr");
			ds.delete(deleteSql);
			
			GetCityDataUtil cityDataUtil = new GetCityDataUtil(this);
			for (int i = 0; i < serviceAddList.size(); i++) {
				String strAddr = serviceAddList.get(i).getSzUserAddr();
				String strLongAddr = cityDataUtil.areaIdToAddr(serviceAddList.get(i).getUAreaID());
				
				String insertSql = DbOprationBuilder.insertUserServiceAddAllBuilder(
						myApplication.userId,//userid
						serviceAddList.get(i).getUAreaID(),//地址区域序号
						strAddr,//联系人地址
						strLongAddr + strAddr,//详细地址
						0,//选中状态
						0,//是否删除--0：未删除，1：已删除
						serviceAddList.get(i).getSzUserTel(),//联系电话
						serviceAddList.get(i).getSzUserName(),//联系人名
						serviceAddList.get(i).getUAddrIndex()//地址序号
						);
				ds.insert(insertSql);
			}
			
			Message message =new Message();
			message.what = updateListView;
			handler.sendMessage(message);
			

		} else {
			String result = UniversalUtils.judgeNetResult_Hs(serviceAdd.eOperResult);
			LogUtils.i("提取地址失败"+ "/失败原因"+result+"=======");
			
			Message message =new Message();
			message.what = updateListViewFail;
			message.obj = result;
			handler.sendMessage(message);
		}
	}
	
	
	/**
	 * 处理删除地址的响应数据
	 * @param iSequence
	 */
	private void processDeleteServiceAddrData(long recvTime){
		HsNetCommon_Resp delAddResp = (HsNetCommon_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(delAddResp == null){
			return;
		}
		LogUtils.i("删除服务地址 返回数据成功");
		if (delAddResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO) {
			
//			DataBaseService ds = new DataBaseService(getApplicationContext());
//			String deleteSql = DbOprationBuilder.deleteBuilderby("userAddr", "id", serviceAdd.get("id"));
//			ds.delete(deleteSql);
			
			Message message =new Message();
			message.what = deleteSuccess;
			handler.sendMessage(message);
			
		} else {
			String result = UniversalUtils.judgeNetResult_Hs(delAddResp.eOperResult);
			LogUtils.i("删除地址失败"+ "失败原因"+result+"=======");
			
			Message message =new Message();
			message.what = deleteFail;
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
