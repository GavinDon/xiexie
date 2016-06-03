package com.lhdz.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lhdz.activity.HomebjActivity;
import com.lhdz.activity.MoreActivity;
import com.lhdz.activity.R;
import com.lhdz.adapter.AdapterShotcut1Button;
import com.lhdz.adapter.ShotCutButtonAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class Shotcut2_Fragment extends Fragment implements OnItemClickListener {

	private View view;
	ArrayList<String> mList = new ArrayList<String>();
	ArrayList<String> logoList = new ArrayList<String>();
	ArrayList<String> subList = new ArrayList<String>();
	ArrayList<String> priceList = new ArrayList<String>();

	private List<Map<String, String>> dataList = new ArrayList<Map<String,String>>();
	private GridView gv_shotcut2;
	private List<Map<String, String>> shotCut2Data = new ArrayList<Map<String, String>>();

	private ShotCutButtonAdapter shotCutButtonAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_shotcut2, null);
		setViewData();
		initViews();
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	
//	/**
//	 * 为GridView设置数据
//	 */
//	private void setViewData(){
//		if(dataList.size() < 15){
//			return;
//		}
//		Map<String, String> more = new HashMap<String, String>();
//		more.put("Sonid", "10000");
//		more.put("SonName", "更多分类");
//		for (int i = 8; i < 15; i++) {
//			shotCut2Data.add(dataList.get(i));
//		}
//		shotCut2Data.add(more);
//	}
	
	
	
	/**
	 * 为GridView设置数据
	 */
	private void setViewData(){
		if(dataList.size() < 8){
			return;
		}
		for (int i = 0; i < 7; i++) {
			shotCut2Data.add(dataList.get(i));
		}
		
		Map<String, String> more = new HashMap<String, String>();
		more.put("Sonid", "10000");
		more.put("SonName", "更多分类");
		shotCut2Data.add(more);
	}
	
	

	private void initViews() {
		
//		if(dataList.size() < 15){
//			return;
//		}
//		Map<String, String> more = new HashMap<String, String>();
//		more.put("Sonid", "10000");
//		more.put("SonName", "更多分类");
//		for (int i = 8; i < 15; i++) {
//			shotCut2Data.add(dataList.get(i));
//		}
//		shotCut2Data.add(more);
		
		gv_shotcut2 = (GridView) view.findViewById(R.id.gv_shotcut2);
		shotCutButtonAdapter = new ShotCutButtonAdapter(getActivity()
				.getApplicationContext(), shotCut2Data);
		gv_shotcut2.setAdapter(shotCutButtonAdapter);
		gv_shotcut2.setOnItemClickListener(this);

	}

	public void setShotcutFragmentData(List<Map<String, String>> dataList) {
		this.dataList = dataList;
		if(shotCutButtonAdapter != null){
			shotCut2Data.clear();
			setViewData();
			shotCutButtonAdapter.setData(shotCut2Data);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		if (position == 7) {
			intent.setClass(getActivity(), MoreActivity.class);
		} else {
			intent.setClass(getActivity(), HomebjActivity.class);
			intent.putExtra("home", (Serializable) shotCut2Data.get(position));
		}
		startActivity(intent);

	}

}
