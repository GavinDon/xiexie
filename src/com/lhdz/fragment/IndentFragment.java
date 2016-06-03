package com.lhdz.fragment;

/**
 * 订单界面
 * @author 王哲
 * @data 2015-8-26
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cn.pedant.SweetAlert.SweetAlertDialog.OnSweetClickListener;

import com.lhdz.activity.R;
import com.lhdz.adapter.IndentAdapter;
import com.lhdz.adapter.IndentAdapter.IndentCallBackListener;
import com.lhdz.dataUtil.HandleMsgDistribute;
import com.lhdz.dataUtil.HandleNetSendMsg;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.HsUserOrderInfo_Pro;
import com.lhdz.dataUtil.protobuf.NetHouseMsgPro.e_HsOperResult_Pro;
import com.lhdz.domainDao.DataBaseService;
import com.lhdz.domainDao.DbOprationBuilder;
import com.lhdz.publicMsg.MsgInncDef;
import com.lhdz.publicMsg.MsgInncDef.HsNetCommon_Req;
import com.lhdz.publicMsg.MsgReceiveDef.HsNetCommon_Resp;
import com.lhdz.publicMsg.MsgReceiveDef.HsUserOrderList_Resp;
import com.lhdz.publicMsg.MyApplication;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.pulltorefresh.PullToRefreshBase;
import com.lhdz.pulltorefresh.PullToRefreshBase.Mode;
import com.lhdz.pulltorefresh.PullToRefreshBase.OnLastItemVisibleListener;
import com.lhdz.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.lhdz.pulltorefresh.PullToRefreshListView;
import com.lhdz.socketutil.HouseSocketConn;
import com.lhdz.util.CustomProgressDialog;
import com.lhdz.util.Define;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

public class IndentFragment extends Fragment {
	private PullToRefreshListView mRefreshListView;
	// 订单列表适配器
	private IndentAdapter mAdapter;
	private List<Map<String, String>> dbOrderList;//数据库中的订单列表
	private int pageNum = 0;
	
	private RadioGroup mRadioGroup;
	private RadioButton rbIndentAll, rbIndentRacing, rbIndentPay, rbIndentService, rbIndentFinish;
	MyApplication mApp = null;
	private final static int UPDATE_LISTVIEW = 0;
	private final static int LOAD_DATA_ERROR = 1;
	private final static int LOAD_DATA_ISNULL = 2;
	private final static int LOAD_TIMER_OVER = 3;
	
	private int seqIndent =-1;//订单列表的sequence
	private int seqBackOutOrder =-1;//撤单的sequence
	private int seqAffirmFinish =-1;//确定完成的sequence
	
	private CustomProgressDialog mCustomProgressDialog;
	
	private int backOutOrderId = 0;//撤单的订单id
	
	
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			switch (msg.what) {
			case UPDATE_LISTVIEW:
				if(mCustomProgressDialog != null){
					mCustomProgressDialog.dismiss();
				}
				handler.removeCallbacks(loadTimerRunnable);
				//停止下拉刷新
				mRefreshListView.onRefreshComplete();
				//查询数据库中订单数据--此处需要根据目前页面标签所在的位置来放置数据
				getOrderListByRadioState();
				mAdapter.setData(dbOrderList);
				break;
			case LOAD_DATA_ISNULL:
				if(mCustomProgressDialog != null){
					mCustomProgressDialog.dismiss();
				}
				handler.removeCallbacks(loadTimerRunnable);
				//停止下拉刷新
				mRefreshListView.onRefreshComplete();
				//查询数据库中订单数据--此处需要根据目前页面标签所在的位置来放置数据
				getOrderListByRadioState();
				mAdapter.setData(dbOrderList);
				ToastUtils.show(getActivity(), "暂无订单信息", 0);
				break;
			case LOAD_DATA_ERROR:
				if(mCustomProgressDialog != null){
					mCustomProgressDialog.dismiss();
				}
				handler.removeCallbacks(loadTimerRunnable);
				//停止下拉刷新
				mRefreshListView.onRefreshComplete();
				//查询数据库中订单数据--此处需要根据目前页面标签所在的位置来放置数据
				getOrderListByRadioState();
				mAdapter.setData(dbOrderList);
				ToastUtils.show(getActivity(), msg.obj.toString(), 0);
				break;
			case LOAD_TIMER_OVER:
				if(mCustomProgressDialog != null){
					mCustomProgressDialog.dismiss();
				}
				//服务端无返回，停止下拉刷新
				handler.removeCallbacks(loadTimerRunnable);
				mRefreshListView.onRefreshComplete();
				//查询数据库中订单数据--此处需要根据目前页面标签所在的位置来放置数据
				getOrderListByRadioState();
				mAdapter.setData(dbOrderList);
				break;
			default:
				break;
			}
		}
	};
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Define.BROAD_CAST_RECV_DATA_COMPLETE);
		getActivity().registerReceiver(mReceiver, filter);
		
		mApp = (MyApplication) this.getActivity().getApplication();
		dbOrderList = new ArrayList<Map<String,String>>();//初始化数据
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_indent, null);
		
		initRadioGroupView(view);//初始化radiogroup控件
		
		//获取可以刷新的ListView
		mRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_indent);
		
		//设置ListView为下拉刷新和上拉加载模式
		mRefreshListView.setMode(Mode.BOTH);
		//下拉刷新
		mRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				String label = DateUtils.formatDateTime(getActivity()
						.getApplicationContext(), System.currentTimeMillis(),
						DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
								| DateUtils.FORMAT_ABBREV_ALL);

				refreshView.getLoadingLayoutProxy()
						.setLastUpdatedLabel("上次更新时间："+label);
				
				pageNum = 0;
				//加载数据
				judgeStateAndLoadData();
				
			}
		});
		//上拉加载
		mRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {

//				if(dbOrderList.size() > 0){
//					//取到目前订单列表中最后一条记录中的orderID，用此orderID去请求剩余的数据
//					pageNum = Integer.parseInt(dbOrderList.get(dbOrderList.size() - 1).get("uOrderID"));
//				}
				//加载数据
				judgeStateAndLoadData();
			}
		});
		
		
		mAdapter = new IndentAdapter(getActivity());
		mAdapter.setIndentCallBack(new IndentCallBackListener() {
			
			@Override
			public void onClickAffirmFinish(Map<String, String> orderInfo) {
				//确定完成按钮点击事件
				showAffirmFinishDialog(orderInfo);//确认完成对话框
			}
			
			@Override
			public void onClickBackOutOrder(Map<String, String> orderInfo) {
				//撤单按钮点击事件
				showBackOutOrderDialog(orderInfo);//撤单对话框
			}
		});
		
		
//		if (mApp.loginState) {
//			//加载进度条
//			mCustomProgressDialog = new CustomProgressDialog(getActivity());
//			mCustomProgressDialog.show();
//			handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
//			loadIndentDataBroad();
//		} else {
//			Toast.makeText(getActivity(), "您未登录，请登录后再试！", 0).show();
//			new GetDataTask().execute();
//		}
		
		
		mRefreshListView.setAdapter(mAdapter);
		return view;
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		
		if (mApp.loginState) {
			//加载进度条
			mCustomProgressDialog = new CustomProgressDialog(getActivity());
			mCustomProgressDialog.show();
			handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
			loadIndentDataBroad();
		} else {
			Toast.makeText(getActivity(), "您未登录，请登录后再试！", 0).show();
			new GetDataTask().execute();
		}
		
//		//判断登录状态并请求订单数据
//		judgeStateAndLoadData();
	}
	
	
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mRadioGroup.check(R.id.rb_all);
		super.onResume();
	}
	
	
	
	/**
	 * 初始化radiogroup控件
	 */
	private void initRadioGroupView(View view){
		mRadioGroup=(RadioGroup) view.findViewById(R.id.rg_indent);
		rbIndentAll = (RadioButton) view.findViewById(R.id.rb_all);
		rbIndentRacing = (RadioButton) view.findViewById(R.id.rb_racing);
		rbIndentPay = (RadioButton) view.findViewById(R.id.rb_pay);
		rbIndentService = (RadioButton) view.findViewById(R.id.rb_service);
		rbIndentFinish = (RadioButton) view.findViewById(R.id.rb_finish);
		
		mRadioGroup.check(R.id.rb_all);
		mRadioGroup.setOnCheckedChangeListener(new IndentRadioGroupListener());
	}
	
	
	
	/**
	 * 顶部radioGroup的监听事件
	 * @author wangf
	 *
	 */
	private class IndentRadioGroupListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			
			//若不是登录状态，则清空显示数据
			if(!mApp.loginState){
				dbOrderList.clear();
				return;
			}
			
			switch (checkedId) {
			case R.id.rb_all://全部
				queryOrderListAll();
				mAdapter.setData(dbOrderList);
				
				break;
			case R.id.rb_racing://抢单中
				queryOrderListBy(NetHouseMsgType.ORDERSTATE_RACING+"");
				mAdapter.setData(dbOrderList);
				
				break;
			case R.id.rb_pay://待支付
				queryOrderListBy(NetHouseMsgType.ORDERSTATE_PAY+"");
				mAdapter.setData(dbOrderList);
				
				break;
			case R.id.rb_service://服务中
				queryOrderListBy(NetHouseMsgType.ORDERSTATE_SERVICE+"");
				mAdapter.setData(dbOrderList);
				
				break;
			case R.id.rb_finish://已完成
				queryOrderListBy(NetHouseMsgType.ORDERSTATE_FINISH+"");
				mAdapter.setData(dbOrderList);
				
				break;

			default:
				break;
			}
			
		}
		
	}
	
	
	/**
	 * 根据目前RadioButton所在的状态信息查询数据库中订单数据
	 */
	private void getOrderListByRadioState(){
		int rbId = mRadioGroup.getCheckedRadioButtonId();
		switch (rbId) {
		case R.id.rb_all:
			LogUtils.i("全部");
			queryOrderListAll();
			break;
		case R.id.rb_racing:
			LogUtils.i("抢单中");
			queryOrderListBy(NetHouseMsgType.ORDERSTATE_RACING+"");
			break;
		case R.id.rb_pay:
			LogUtils.i("待支付");
			queryOrderListBy(NetHouseMsgType.ORDERSTATE_PAY+"");
			break;
		case R.id.rb_service:
			LogUtils.i("服务中");
			queryOrderListBy(NetHouseMsgType.ORDERSTATE_SERVICE+"");
			break;
		case R.id.rb_finish:
			LogUtils.i("完成");
			queryOrderListBy(NetHouseMsgType.ORDERSTATE_FINISH+"");
			break;
		default:
			break;
		}
	}
	
	
	
	/**
	 * 判断相关的登录状态 
	 * 若未登录则无法加载数据
	 */
	private void judgeStateAndLoadData() {
		if (mApp.loginState) {
			handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
			loadIndentDataBroad();
		} else {
			Toast.makeText(getActivity(), "您未登录，请登录后再试！", 0).show();
			new GetDataTask().execute();
//			mRefreshListView.onRefreshComplete();
		}
	}
	
	
	/**
	 * 确认 撤单 对话框
	 */
	private void showBackOutOrderDialog(final Map<String, String> orderInfo){
		SweetAlertDialog dialog = new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("确定撤销该订单吗？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				loadBackOutOrderData(orderInfo);//发送撤单请求
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
	 * 确定完成  对话框
	 */
	private void showAffirmFinishDialog(final Map<String, String> orderInfo){
		SweetAlertDialog dialog = new SweetAlertDialog(getActivity(),SweetAlertDialog.WARNING_TYPE);
		dialog.setTitleText("该订单是否完成？");
		dialog.setCancelText("取消");
		dialog.setConfirmText("确定");
		dialog.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				// TODO Auto-generated method stub
				
				loadAffirmFinishData(orderInfo);//发送撤单请求
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
	 * 订单列表的请求
	 */
	private void loadIndentDataBroad(){
		seqIndent = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = mApp.userId;//为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = pageNum ;//选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandleHsUserOrderList_ReqToPro(hsNetCommonReq,seqIndent);
		HouseSocketConn.pushtoList(connData);
		LogUtils.i("connData订单列表的请求--sequence="+seqIndent +"/"+Arrays.toString(connData)+"=============");
		
		judgeSendBadgeBroad();//发送清除badgeView的通知
	}
	
	
	
	/**
	 * 撤单 请求
	 */
	public void loadBackOutOrderData(Map<String, String> orderInfo){
		seqBackOutOrder = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = mApp.userId;// 为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = Integer.parseInt(orderInfo.get("uOrderID"));// 选定的乙方唯一ID
		backOutOrderId = Integer.parseInt(orderInfo.get("uOrderID"));
		byte[] connData = HandleNetSendMsg.HandleHsBackOutOrder_ReqToPro(hsNetCommonReq,seqBackOutOrder);
		HouseSocketConn.pushtoList(connData);
		
		mCustomProgressDialog = new CustomProgressDialog(getActivity());
		mCustomProgressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("用户撤单  的请求--sequence="+seqBackOutOrder +"/"+connData+"=============");
		
	}
	
	
	
	/**
	 * 确定完成  请求
	 */
	public void loadAffirmFinishData(Map<String, String> orderInfo){
		seqAffirmFinish = MyApplication.SequenceNo++;
		HsNetCommon_Req hsNetCommonReq = new MsgInncDef().new HsNetCommon_Req();
		hsNetCommonReq.iSrcID = mApp.userId;// 为用户生成一个唯一的ID;
		hsNetCommonReq.iSelectID = Integer.parseInt(orderInfo.get("uOrderID"));// 选定的乙方唯一ID
		byte[] connData = HandleNetSendMsg.HandleHsAffirmFinishOrder_ReqToPro(hsNetCommonReq,seqAffirmFinish);
		HouseSocketConn.pushtoList(connData);
		
		mCustomProgressDialog = new CustomProgressDialog(getActivity());
		mCustomProgressDialog.show();
		handler.postDelayed(loadTimerRunnable, Define.BTN_DELAY_TIME);
		
		LogUtils.i("确定完成  的请求--sequence="+seqAffirmFinish +"/"+connData+"=============");
		
	}
	
	
	
	
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (Define.BROAD_CAST_RECV_DATA_COMPLETE.equals(intent.getAction())) {
				int iSequence = intent.getIntExtra(Define.BROAD_SEQUENCE, -1);
				int iMsgType = intent.getIntExtra(Define.BROAD_MSG_TYPE, -1);
				long recvTime = intent.getLongExtra(Define.BROAD_MSG_RECVTIME, -1);

				if (seqIndent == iSequence) {
					processIndentListData(recvTime);
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
	 * 处理订单列表的响应数据
	 * @param iSequence
	 */
	private void processIndentListData(long recvTime){
		HsUserOrderList_Resp hsUserOrderListResp = (HsUserOrderList_Resp) HandleMsgDistribute.getInstance().queryCompleteMsg(recvTime);
		if(hsUserOrderListResp == null){
			return;
		}
		if(hsUserOrderListResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_SUCCESS_PRO){
			List<HsUserOrderInfo_Pro> orderList = hsUserOrderListResp.orderList;
			
			int iTotalCount = hsUserOrderListResp.iTotalCount;//总个数
			int iSendCount = hsUserOrderListResp.iSendCount;//已经发送的个数
			
			
			DataBaseService ds = new DataBaseService(getActivity());
			if (pageNum == 0) {
				if(iSendCount == 0){
					String deleteSql = DbOprationBuilder.deleteBuilder("orderList");
					ds.delete(deleteSql);
				}
			}

			for (int i = 0; i < orderList.size(); i++) {
				// 订单列表 数据库 插入数据
				String insertSql = DbOprationBuilder.insertOrderListAllBuilder(mApp.userId, orderList.get(i));
				ds.insert(insertSql);
			}
			
			
			LogUtils.i("订单数据获取成功--iTotalCount="+iTotalCount+"/iSendCount="+iSendCount+"/orderList.size()="+orderList.size());	
			if(iSendCount + orderList.size() == iTotalCount){
				Message message =new Message();
				message.what = UPDATE_LISTVIEW;
				handler.sendMessage(message);
			}
			
			
		}else if (hsUserOrderListResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_GETRESULT_ISNULL_PRO){
			LogUtils.i("订单数据获取成功--订单数据为空");	
			Message message =new Message();
			message.what = LOAD_DATA_ISNULL;
			handler.sendMessage(message);
		}
		else{
			String result = UniversalUtils.judgeNetResult_Hs(hsUserOrderListResp.eOperResult);
			
			Message message =new Message();
			message.what = LOAD_DATA_ERROR;
			message.obj = result;
			handler.sendMessage(message);
			
			
//			if(hsUserOrderListResp.eOperResult == e_HsOperResult_Pro.E_HSOPER_GETRESULT_ISNULL_PRO){
//				DataBaseService ds = new DataBaseService(getActivity());
//				String deleteSql = DbOprationBuilder.deleteBuilder("orderList");
//				ds.delete(deleteSql);
//			}
			
			LogUtils.i(result);					
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
			
			//撤单成功后需要删除数据库中该订单，并且更新页面显示
			DataBaseService ds = new DataBaseService(getActivity());
			String deleteSql = DbOprationBuilder.deleteBuilderby("orderList", "uOrderID", backOutOrderId+"");
			ds.delete(deleteSql);
			getOrderListByRadioState();
			mAdapter.setData(dbOrderList);
			backOutOrderId = 0;//将撤单的orderId恢复为默认的数据
			
//			loadIndentDataBroad();//撤单成功后重新获取订单数据
			Toast.makeText(getActivity(), "撤销订单成功", Toast.LENGTH_SHORT).show();
			LogUtils.i("撤单成功");
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsNetCommon_Resp.eOperResult);
			
			Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			
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
			
			loadIndentDataBroad();//确定完成后重新获取订单数据
			Toast.makeText(getActivity(), "确定完成成功", Toast.LENGTH_SHORT).show();
			LogUtils.i("确定完成成功");
			
		}else{
			String result = UniversalUtils.judgeNetResult_Hs(hsNetCommon_Resp.eOperResult);
			
			Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
			
//			Message message = new Message();
//			message.what = MSG_BACK_OUT_ORDER_ERROR;
//			message.obj = result;
//			handler.sendMessage(message);
			
			LogUtils.i("确定完成失败"+result);
		}
	}
	
	
	
	private class GetDataTask extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			mRefreshListView.onRefreshComplete();// 停止加载
		}
	}
	 

		/**
		 * 查询数据库中   所有   订单数据
		 */
		private void queryOrderListAll(){
			dbOrderList.clear();
			DataBaseService ds = new DataBaseService(MyApplication.context);
			String sql = DbOprationBuilder.queryAllBuilder("orderList");
			dbOrderList.addAll(ds.query(sql));
		}
		
		
		/**
		 * 查询数据库中   特定条件的订单数据
		 */
		private void queryOrderListBy(String state){
			dbOrderList.clear();
			DataBaseService ds = new DataBaseService(MyApplication.context);
			String sql = DbOprationBuilder.queryBuilderby("*", "orderList", "iOrderState", state);
			dbOrderList.addAll(ds.query(sql));
		}
		
		
		
		//若长时间服务端无返回，则停止停止进度条
		Runnable loadTimerRunnable = new Runnable(){
			@Override
			public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(LOAD_TIMER_OVER);
			}
		};
	
		
		
		private void judgeSendBadgeBroad(){
			if(MyApplication.badgeViewCount != 0){
				Intent intent = new Intent();
				intent.setAction(Define.BROAD_BADGE_CLEAR);
				getActivity().sendBroadcast(intent);
			}
		}
		
		
		public void onDestroy() {
			getActivity().unregisterReceiver(mReceiver);
			super.onDestroy();
		};
	
}
