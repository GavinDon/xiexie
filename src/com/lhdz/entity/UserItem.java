package com.lhdz.entity;

public class UserItem {

	public UserItem(boolean isShowTopDivider, int leftImg, String subhead, String SubItem) {
		this.isShowTopDivider = isShowTopDivider;
		this.leftImg = leftImg;
		this.subhead = subhead;
		this.SubItem = SubItem;
	}

	private boolean isShowTopDivider;
	private int leftImg;
	private String subhead;
	private String SubItem;

	public boolean isShowTopDivider() {
		return isShowTopDivider;
	}

	public void setShowTopDivider(boolean isShowTopDivider) {
		this.isShowTopDivider = isShowTopDivider;
	}

	public int getLeftImg() {
		return leftImg;
	}

	public void setLeftImg(int leftImg) {
		this.leftImg = leftImg;
	}

	public String getSubhead() {
		return subhead;
	}

	public void setSubhead(String subhead) {
		this.subhead = subhead;
	}

	public String getSubItem() {
		return SubItem;
	}

	public void setSubItem(String SubItem) {
		this.SubItem = SubItem;
	}

}
