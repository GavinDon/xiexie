package com.lhdz.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.text.TextUtils;

import com.lhdz.entity.SortModel;

public class SearchUtil {
	private CharacterParser characterParser;
	private List<Map<String, Object>> data;
	private List<Map<String, String>> strData;

	/**
	 * 
	 * @param data
	 *            需要展示的源数据
	 */
	public SearchUtil(List<Map<String, Object>> data) {
		this.data = data;
		characterParser = CharacterParser.getInstance();

	}

	public SearchUtil(Context context, List<Map<String, String>> strData) {
		this.strData = strData;
		characterParser = CharacterParser.getInstance();
	}

	/**
	 * 
	 * @param key
	 *            就是搜索的字段值
	 * @return mSortList集合存入的是所有源数据中通过key拿的string值;
	 */
	public List<SortModel> filledData(String key) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < data.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(data.get(i).get(key).toString());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(data.get(i).get(key)
					.toString());
			String sortString = pinyin.substring(0, pinyin.length())
					.toLowerCase();

			sortModel.setSortLetters(sortString);

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 源数据为list<Map<String, Object>>
	 * 
	 * @param filterStr
	 *            在editText上输入的查询字符串
	 * @param key
	 *            就是搜索的字段值
	 * @return finalDatalist 最终需要展示的所有值
	 */

	public List<Map<String, Object>> filterData(String filterStr, String key) {
		List<SortModel> SourceDateList = filledData(key);
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		// 最终需要传入适配器的数据集合
		List<Map<String, Object>> finalDatalist = new ArrayList<Map<String, Object>>();
		// 创建两个存储过滤前后的字符串的集合
		List<String> searchDataList = new ArrayList<String>();// 查找出的所有字符串数据集合
		List<String> srcDataList = new ArrayList<String>(); // 从源数据中拿出需要搜索的字段的所有数据
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				String letterName = sortModel.getSortLetters();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())
						|| letterName.contains(filterStr)) {
					// 过滤后的值
					filterDateList.add(sortModel);
				}
			}
			// 遍历拿出过滤后的值并放入一个新的集合中
			for (int i = 0; i < filterDateList.size(); i++) {
				String fileterName = filterDateList.get(i).getName();
				searchDataList.add(fileterName);
			}
		}
		// 遍历拿出源数据中所有值并放入一个新的集合中;
		for (int j = 0; j < data.size(); j++) {
			String srcName = data.get(j).get(key).toString();
			srcDataList.add(srcName);
		}
		// 根据过滤后的值从源数据中拿出对应的值
		for (String str : searchDataList) {
			if (srcDataList.contains(str)) {
				for (int i = 0; i < data.size(); i++) {
					if (data.get(i).get(key).equals(str)) {
						// 拿出所有搜索出的值存入新集合;
						finalDatalist.add(data.get(i));
					}
				}
			}
		}
		return finalDatalist;
		// mAdapter.setData(finalDatalist);
	}

	/**
	 * 源数据为List<Map<String, String>>
	 * 
	 * @param filterStr
	 *            输入搜索的字符
	 * @param key
	 * @return
	 */
	public List<Map<String, String>> filterStrData(String filterStr, String key) {
		List<SortModel> SourceDateList = fillStrData(key);
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		// 最终需要传入适配器的数据集合
		List<Map<String, String>> finalDatalist = new ArrayList<Map<String, String>>();
		// 创建两个存储过滤前后的字符串的集合
		List<String> searchDataList = new ArrayList<String>();// 查找出的所有字符串数据集合
		List<String> srcDataList = new ArrayList<String>(); // 从源数据中拿出需要搜索的字段的所有数据
		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				String letterName = sortModel.getSortLetters();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())
						|| letterName.contains(filterStr)) {
					// 过滤后的值
					filterDateList.add(sortModel);
				}
			}
			// 遍历拿出过滤后的值并放入一个新的集合中
			for (int i = 0; i < filterDateList.size(); i++) {
				String fileterName = filterDateList.get(i).getName();
				searchDataList.add(fileterName);
			}
		}
		// 遍历拿出源数据中所有值并放入一个新的集合中;
		for (int j = 0; j < strData.size(); j++) {
			String srcName = strData.get(j).get(key).toString();
			srcDataList.add(srcName);
		}
		// 根据过滤后的值从源数据中拿出对应的值
		for (String str : searchDataList) {
			if (srcDataList.contains(str)) {
				for (int i = 0; i < strData.size(); i++) {
					if (strData.get(i).get(key).equals(str)) {
						// 拿出所有搜索出的值存入新集合;
						finalDatalist.add(strData.get(i));
					}
				}
			}
		}
		return finalDatalist;
		// mAdapter.setData(finalDatalist);
	}
	/**
	 * 
	 * @param key
	 *            就是搜索的字段值
	 * @return mSortList集合存入的是所有源数据中通过key拿的string值;
	 */
	public List<SortModel> fillStrData(String key) {
		List<SortModel> mSortList = new ArrayList<SortModel>();
		for (int i = 0; i < strData.size(); i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(strData.get(i).get(key).toString());
			// 汉字转换成拼音
			String pinyin = characterParser.getSelling(strData.get(i).get(key)
					.toString());
			String sortString = pinyin.substring(0, pinyin.length())
					.toLowerCase();

			sortModel.setSortLetters(sortString);

			mSortList.add(sortModel);
		}
		return mSortList;

	}

}
