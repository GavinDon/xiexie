package com.lhdz.adapter;
/**
 * 订单适配器
 * @author 王哲
 * @date 2015-8-26
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lhdz.activity.ChoiceServiceActivity;
import com.lhdz.activity.ComplaintsActivity;
import com.lhdz.activity.EvaluationActivity;
import com.lhdz.activity.IndentDetailsActivity;
import com.lhdz.activity.OrderActivity;
import com.lhdz.activity.RaceDetailActivity;
import com.lhdz.activity.R;
import com.lhdz.publicMsg.NetHouseMsgType;
import com.lhdz.util.LogUtils;
import com.lhdz.util.UniversalUtils;

public class IndentAdapter extends BaseAdapter{
	private Context mContext;
//	private List<HsUserOrderInfo_Pro> itemList;
	private List<Map<String, String>> itemList;
	
	private IndentCallBackListener callBackListener;
	

	public IndentAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		this.itemList = new ArrayList<Map<String,String>>();
	}

	@Override
	public int getCount() {
		if (itemList!=null) {
			return itemList.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if (itemList!=null) {
			return itemList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	
	public void setData(List<Map<String, String>> list) {
		this.itemList.clear();
		this.itemList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void addData(List<Map<String, String>> list) {
		this.itemList.addAll(list);
		notifyDataSetChanged();
	}
	
	
	public void clear() {
		this.itemList.clear();
		notifyDataSetChanged();
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Map<String, String> orderInfo = (Map<String, String>) itemList.get(position);
		if(orderInfo == null){
			return null;
		}
		
		final ViewHolder viewHolder;
		if (convertView==null) {
			convertView=LayoutInflater.from(mContext).inflate(R.layout.adapter_indent3_1, null);
			viewHolder = new ViewHolder();
			viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);//公司名称
			viewHolder.service_state = (TextView) convertView.findViewById(R.id.service_state);//订单状态
			viewHolder.service_num = (TextView) convertView.findViewById(R.id.service_num);//抢单数量
			viewHolder.order_id = (TextView) convertView.findViewById(R.id.order_id);//订单号
			viewHolder.service_type = (TextView) convertView.findViewById(R.id.service_type);//服务类型
//			viewHolder.service_time = (TextView) convertView.findViewById(R.id.service_time);//服务时长
			viewHolder.service_heart_price = (TextView) convertView.findViewById(R.id.service_price);//心理价位
			viewHolder.order_price = (TextView) convertView.findViewById(R.id.order_price);//订单金额
			
			viewHolder.bt_first = (Button) convertView.findViewById(R.id.bt_first);//投诉--第一个按钮
			viewHolder.bt_second = (Button) convertView.findViewById(R.id.bt_second);//评价--第二个按钮
			
			viewHolder.order_item = (LinearLayout) convertView.findViewById(R.id.indent_item_btn);//item的LinearLayout
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//订单状态
		final int iOrderState = Integer.parseInt(orderInfo.get("iOrderState"));
		
		//将控件设置为默认状态
		viewHolder.service_state.setTextColor(mContext.getResources().getColor(R.color.indent_gray));
		viewHolder.bt_first.setVisibility(View.GONE);
		viewHolder.bt_second.setVisibility(View.GONE);
		viewHolder.bt_first.setClickable(true);
		viewHolder.bt_second.setClickable(true);
		viewHolder.bt_first.setBackgroundResource(R.drawable.sel_indent_detail_w);
		viewHolder.bt_second.setBackgroundResource(R.drawable.sel_indent_detail_g);
		viewHolder.service_num.setVisibility(View.GONE);// 抢单数量隐藏
		viewHolder.service_heart_price.setVisibility(View.GONE);
		viewHolder.order_price.setVisibility(View.GONE);
		
		//第一个按钮
		viewHolder.bt_first.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//抢单中--撤单
				if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
					if(callBackListener != null){
						callBackListener.onClickBackOutOrder(orderInfo);
					}
				}
				//待支付--撤单
				else if(iOrderState == NetHouseMsgType.ORDERSTATE_PAY){
					if(callBackListener != null){
						callBackListener.onClickBackOutOrder(orderInfo);
					}
				}
				//服务中--投诉
				else if (iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE) {
//					Toast.makeText(mContext, "敬请期待", Toast.LENGTH_SHORT).show();
					Intent intent=new Intent(mContext,ComplaintsActivity.class);
					intent.putExtra("orderListInfo", (Serializable)orderInfo);//订单列表中的信息
					mContext.startActivity(intent);
				}
				//已完成--评价--该按钮无效
				else if (iOrderState == NetHouseMsgType.ORDERSTATE_FINISH) {
					Toast.makeText(mContext, "敬请期待", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
		
		//第二个按钮
		viewHolder.bt_second.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//抢单中--选择服务
				if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
					
					Intent intent = new Intent(mContext, ChoiceServiceActivity.class);
					intent.putExtra("uOrderID", Integer.parseInt(orderInfo.get("uOrderID")));//订单id
					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));//订单号
					intent.putExtra("szOrderStateName", orderInfo.get("szOrderStateName"));//订单状态
					mContext.startActivity(intent);
					
					
//					LogUtils.i(orderInfo.get("uOrderID"));
//					Toast.makeText(mContext, "敬请期待", Toast.LENGTH_SHORT).show();
//					Intent intent = new Intent(mContext, ChoiceServiceActivity.class);
//					intent.putExtra("orderInfo", (Serializable)orderInfo);//订单列表中的数据
//					intent.putExtra("orderValue", orderInfo.get("szOrderValue"));//订单号
//					mContext.startActivity(intent);
				}
				//待支付--立即支付
				else if(iOrderState == NetHouseMsgType.ORDERSTATE_PAY){
					Intent intent = new Intent(mContext, OrderActivity.class);
//					intent.putExtra("orderDetailInfo", (Serializable)orderDetailInfo);//订单详情
					
					intent.putExtra("uOrderID", Integer.parseInt(orderInfo.get("uOrderID")));//订单orderId
					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));//订单编号
					intent.putExtra("szOrderStateName", orderInfo.get("szOrderStateName"));//订单状态
					
					intent.putExtra("goodsname", orderInfo.get("szServiceTypeName"));//商品名称
					intent.putExtra("goodsdetil", "歇歇服务");//商品描述
					intent.putExtra("price", UniversalUtils.getString2Float(orderInfo.get("szOrderPrice")));//商品单价
					
					mContext.startActivity(intent);
				}
				//服务中--确定完成
				else if (iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE) {
					if(callBackListener != null){
						callBackListener.onClickAffirmFinish(orderInfo);
					}
				}
				//已完成--评价（每个用户对该订单只有一次评价机会）
				else if (iOrderState == NetHouseMsgType.ORDERSTATE_FINISH) {
//					Toast.makeText(mContext, "敬请期待", Toast.LENGTH_SHORT).show();
//					Intent intent=new Intent(mContext,EvaluationActivity.class);
//					intent.putExtra("orderId", Integer.parseInt(orderInfo.get("uOrderID")));//订单号
//					intent.putExtra("companyName", orderInfo.get("szCompanyName"));//公司名称
//					mContext.startActivity(intent);
				}
				
				
			}
		});
		
		
		viewHolder.order_item.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				//抢单中--撤单+选择服务
				if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
					Intent intent = new Intent(mContext, IndentDetailsActivity.class);
					intent.putExtra("uOrderID", Integer.parseInt(orderInfo.get("uOrderID")));//订单唯一标识--orderID
//					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));//订单号
					mContext.startActivity(intent);
				}
				//待支付--撤单+立即支付
				else if(iOrderState == NetHouseMsgType.ORDERSTATE_PAY){
					Intent intent = new Intent(mContext, IndentDetailsActivity.class);
					intent.putExtra("uOrderID", Integer.parseInt(orderInfo.get("uOrderID")));//订单唯一标识--orderID
//					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));//订单号
					mContext.startActivity(intent);
				}
				//服务中--投诉+确定完成
				else if (iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE) {
					Intent intent = new Intent(mContext,IndentDetailsActivity.class);
					intent.putExtra("uOrderID",Integer.parseInt(orderInfo.get("uOrderID")));// 订单唯一标识--orderID
//					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));// 订单号
					intent.putExtra("orderListInfo", (Serializable)orderInfo);//订单列表中的信息
					mContext.startActivity(intent);
				}
				//已完成--评价（每个用户对该订单只有一次评价机会）
				else if (iOrderState == NetHouseMsgType.ORDERSTATE_FINISH) {
					Intent intent = new Intent(mContext,IndentDetailsActivity.class);
					intent.putExtra("uOrderID",Integer.parseInt(orderInfo.get("uOrderID")));// 订单唯一标识--orderID
//					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));// 订单号
					mContext.startActivity(intent);
				}
				//其他状态--投诉等
				else{
					Intent intent = new Intent(mContext,IndentDetailsActivity.class);
					intent.putExtra("uOrderID",Integer.parseInt(orderInfo.get("uOrderID")));// 订单唯一标识--orderID
//					intent.putExtra("szOrderValue", orderInfo.get("szOrderValue"));// 订单号
					mContext.startActivity(intent);
				}
				
			}
		});
		
		
		
		
		// 抢单中--撤单+选择服务
		if (iOrderState == NetHouseMsgType.ORDERSTATE_RACING) {
			//设置订单状态颜色
			viewHolder.service_state.setTextColor(mContext.getResources().getColor(R.color.indent_orange));
			// 设置按钮显示
			viewHolder.bt_first.setText("撤单");
			viewHolder.bt_second.setText("选择服务");
			viewHolder.bt_first.setVisibility(View.VISIBLE);
			viewHolder.bt_second.setVisibility(View.VISIBLE);

			int iRaceNum = Integer.parseInt(orderInfo.get("iRaceNum"));// 抢单公司数量
			// 若抢单公司数量为0时，隐藏数量显示红点#ff6600，选择服务按钮置灰为不可点击
			if (iRaceNum == 0) {
				viewHolder.service_num.setVisibility(View.GONE);// 抢单数量隐藏
				viewHolder.bt_second.setClickable(false);
				viewHolder.bt_second.setBackgroundResource(R.drawable.shape_btn_click_not);
			} else if (iRaceNum > 0) {
				viewHolder.service_num.setText(orderInfo.get("iRaceNum"));// 抢单数量
				viewHolder.service_num.setVisibility(View.VISIBLE);// 抢单数量显示
				viewHolder.bt_second.setClickable(true);
				viewHolder.bt_second.setBackgroundResource(R.drawable.sel_indent_detail_g);
			}
		}
		// 待支付--撤单+立即支付
		if (iOrderState == NetHouseMsgType.ORDERSTATE_PAY) {
			//设置订单状态颜色
			viewHolder.service_state.setTextColor(mContext.getResources().getColor(R.color.indent_red));
			
			viewHolder.bt_first.setText("撤单");
			viewHolder.bt_second.setText("立即支付");
			viewHolder.bt_first.setVisibility(View.VISIBLE);
			viewHolder.bt_second.setVisibility(View.VISIBLE);
		}
		// 服务中--投诉+确定完成
		if (iOrderState == NetHouseMsgType.ORDERSTATE_SERVICE) {
			//设置订单状态颜色
			viewHolder.service_state.setTextColor(mContext.getResources().getColor(R.color.indent_green));
			
			viewHolder.bt_first.setText("投诉");
			viewHolder.bt_second.setText("确定完成");
			viewHolder.bt_first.setVisibility(View.VISIBLE);
			viewHolder.bt_second.setVisibility(View.VISIBLE);
		}
		// 已完成--评价（每个用户对该订单只有一次评价机会）
		if (iOrderState == NetHouseMsgType.ORDERSTATE_FINISH) {
			//设置订单状态颜色
			viewHolder.service_state.setTextColor(mContext.getResources().getColor(R.color.indent_gray));
			
//			viewHolder.bt_second.setText("评价");
//			viewHolder.bt_second.setVisibility(View.VISIBLE);
			//项目一期中，不做评价功能，并且对评价按钮进行隐藏
			viewHolder.bt_first.setVisibility(View.GONE);
			viewHolder.bt_second.setVisibility(View.GONE);
		}
		// 投诉
		if (iOrderState == NetHouseMsgType.ORDERSTATE_COMPLAINT) {
			//设置订单状态颜色
			viewHolder.service_state.setTextColor(mContext.getResources().getColor(R.color.indent_red));
		}
		// 下单
		if (iOrderState == NetHouseMsgType.ORDERSTATE_REQ) {

		}
		
		
		//抢单中状态，显示心理价位，隐藏订单金额
		//其他状态，显示订单金额，隐藏心理价位
		if(iOrderState == NetHouseMsgType.ORDERSTATE_RACING){
			viewHolder.service_heart_price.setVisibility(View.VISIBLE);
			viewHolder.service_heart_price.setText("心理价位："+UniversalUtils.getString2Float(orderInfo.get("szHeartPrice"))+" 元");//心理价位
		}else{
			viewHolder.order_price.setVisibility(View.VISIBLE);
			viewHolder.order_price.setText("订单金额："+UniversalUtils.getString2Float(orderInfo.get("szOrderPrice"))+" 元");//订单金额
		}
		
		viewHolder.service_state.setText(orderInfo.get("szOrderStateName"));//订单状态名称
		viewHolder.tv_name.setText(orderInfo.get("szCompanyName"));//公司名称
		viewHolder.order_id.setText("订单号："+orderInfo.get("szOrderValue"));//订单号
		viewHolder.service_type.setText("服务类型："+orderInfo.get("szServiceTypeName"));//服务类型
//		viewHolder.service_time.setText("服务时长："+orderInfo.getIUsingTimes());//服务时长
		
		return convertView;
	} 
	
	
	public interface IndentCallBackListener{
		public void onClickBackOutOrder(Map<String, String> orderInfo);
		public void onClickAffirmFinish(Map<String, String> orderInfo);
	}
	
	
	public void setIndentCallBack(IndentCallBackListener callBackListener){
		this.callBackListener = callBackListener;
	}
	
	
	private class ViewHolder{
		TextView tv_name = null;
		TextView service_state = null;
		TextView service_num = null;
		TextView order_id = null;
		TextView service_type = null;
//		TextView service_time = null;
		TextView service_heart_price = null;
		TextView order_price = null;
		
		Button bt_first = null;
		Button bt_second = null;
		
		LinearLayout order_item = null;
	}
	

}
