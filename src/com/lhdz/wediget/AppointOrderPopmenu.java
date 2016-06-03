package com.lhdz.wediget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.lhdz.activity.R;
import com.lhdz.adapter.PopAppointOrderAdapter;
import com.lhdz.adapter.PopAppointOrderAdapter.ServiceListCallBackLister;
import com.lhdz.entity.ServiceListInfoEntity;
import com.lhdz.util.GetScreenInchUtil;
import com.lhdz.util.LogUtils;
import com.lhdz.util.ToastUtils;
import com.lhdz.util.UniversalUtils;

public class AppointOrderPopmenu extends PopupWindow implements OnClickListener {
	private PopupWindow myPop;
	private View popView;
	private Activity context;
	private Button btn_pop_appoint_order;
	private ListView listView_pop_appoint;
	
	private PopAppointOrderAdapter appointOrderAdapter = null;
	
//	private List<String[]> serviceListData = null;//所有服务的数据
	private List<ServiceListInfoEntity> listInfoEntities = new ArrayList<ServiceListInfoEntity>();//所有服务的数据
	
	private ServiceNumCallBack serviceNumCallBack = null;
	

	public AppointOrderPopmenu(Activity context,List<ServiceListInfoEntity> listInfoEntities) {
		this.context = context;
		this.listInfoEntities = listInfoEntities;
		
		popView = LayoutInflater.from(context).inflate(R.layout.pop_appointment_order,null);
		myPop = new PopupWindow(popView, LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT, true);
		ColorDrawable cd = new ColorDrawable(R.color.white);
		myPop.setBackgroundDrawable(cd);
		myPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		initView();
	}

	public void showPopwindow(View parent) {
		myPop.setOutsideTouchable(true);
		myPop.setAnimationStyle(R.style.mypopwindow_anim_style);
		myPop.showAtLocation(parent, Gravity.BOTTOM, 0, GetScreenInchUtil.getVrtualBtnHeight(context));
		
		myPop.update();
		myPop.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				WindowManager.LayoutParams lp = context.getWindow().getAttributes();
				lp.alpha = 1.0f;
				context.getWindow().setAttributes(lp);
			}
		});
	}

	public void initView() {
		btn_pop_appoint_order = (Button) popView.findViewById(R.id.btn_pop_appoint_order);//提交按钮
		btn_pop_appoint_order.setOnClickListener(this);
		
		listView_pop_appoint = (ListView) popView.findViewById(R.id.lv_pop_appoint);
		
		//设置ListView的高度
		WindowManager windowManager = context.getWindowManager();
		Display display = windowManager.getDefaultDisplay(); 
		LayoutParams lp = listView_pop_appoint.getLayoutParams();
		lp.height = (int) (display.getHeight()*0.4);
		listView_pop_appoint.setLayoutParams(lp);
		
		appointOrderAdapter = new PopAppointOrderAdapter(context);
		appointOrderAdapter.setServiceListCallBackLister(new ServiceListCallBackLister() {
			
			@Override
			public void onClickMinus(int position, ServiceListInfoEntity entity) {
				
				int iOldNum = entity.getServiceNum();
				
				if(iOldNum <= 0){
					ToastUtils.show(context, "服务数量最小为0", 0);
					return;
				}
				
				int iNewNum = iOldNum - 1;
				entity.setServiceNum(iNewNum);
				listInfoEntities.set(position, entity);
				
				updataListViewItem(position, listView_pop_appoint);
			}
			
			@Override
			public void onClickAdd(int position, ServiceListInfoEntity entity) {
				
				int iOldNum = entity.getServiceNum();
				int iNewNum = iOldNum + 1;
				entity.setServiceNum(iNewNum);
				listInfoEntities.set(position, entity);
				
				updataListViewItem(position, listView_pop_appoint);
			}

			@Override
			public void afterTextChanged(CharSequence s, int position,ServiceListInfoEntity entity) {
				
				int iNewNum = UniversalUtils.parseString2Int(s.toString());
				entity.setServiceNum(iNewNum);
				listInfoEntities.set(position, entity);
				
			}

		});
		
		listView_pop_appoint.setAdapter(appointOrderAdapter);
//		initData();
		appointOrderAdapter.setData(listInfoEntities);
		
	}
	
	
	/**
	 * 更新ListView的一条item数据
	 * @param posi
	 * @param listView
	 */
	public void updataListViewItem(int posi, ListView listView) {
		int visibleFirstPosi = listView.getFirstVisiblePosition();//获取当前状态下list的第一个可见item的position
		int visibleLastPosi = listView.getLastVisiblePosition();//获取当前状态下list的最后一个可见item的position
		if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {
			View view = listView.getChildAt(posi - visibleFirstPosi);
			listView.getAdapter().getView(posi, view, listView);
		} else {
			View convertView = listView.getChildAt(posi);
			listView.getAdapter().getView(posi, convertView, listView);
		}
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_pop_appoint_order:
			if(serviceNumCallBack != null){
				serviceNumCallBack.serviceNumListener(listInfoEntities);
			}
			myPop.dismiss();
		}
	}
	
	
	
	
	
	
	public interface ServiceNumCallBack{
		public void serviceNumListener(List<ServiceListInfoEntity> serviceNumList);
	}
	
	
	public void setServiceNumCallBackLister(ServiceNumCallBack callBackLister){
		this.serviceNumCallBack = callBackLister;
	}

	
	
	
//	/**
//	 * 测试数据
//	 */
//	private List<String[]> initData(){
//		serviceListData = new ArrayList<String[]>();
//		
//		String[] test0 = {"0","保洁","45","小时"};
//		String[] test1 = {"1","保洁套餐","15","件"};
//		String[] test2 = {"2","保洁布","75","件"};
//		String[] test3 = {"3","保洁布","75","件"};
//		String[] test4 = {"4","保洁布","75","件"};
//		String[] test5 = {"5","保洁布","75","件"};
//		String[] test6 = {"6","保洁布","75","件"};
//		String[] test7 = {"7","保洁布","75","件"};
//		String[] test8 = {"8","保洁布","75","件"};
//		String[] test9 = {"9","保洁布","75","件"};
//		String[] test10 = {"10","保洁布","75","件"};
//		String[] test11 = {"11","保洁布","75","件"};
//		String[] test12 = {"12","保洁布","75","件"};
//		
//		serviceListData.add(test0);
//		serviceListData.add(test1);
//		serviceListData.add(test2);
//		serviceListData.add(test3);
//		serviceListData.add(test4);
//		serviceListData.add(test5);
//		serviceListData.add(test6);
//		serviceListData.add(test7);
//		serviceListData.add(test8);
//		serviceListData.add(test9);
//		serviceListData.add(test10);
//		serviceListData.add(test11);
//		serviceListData.add(test12);
//		return serviceListData;
//	}
//	
	/**
	 * 测试数据
	 */
	private List<ServiceListInfoEntity> initData(){
		
		for (int i = 0; i < 8; i++) {
			ServiceListInfoEntity entity = new ServiceListInfoEntity();
			entity.setServiceName("保洁");
			entity.setServicePrice("25");
			entity.setServiceUnit("小时");
			listInfoEntities.add(entity);
		}
		
		return listInfoEntities;
	}
	


}
