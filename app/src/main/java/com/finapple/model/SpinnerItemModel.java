package com.finapple.model;

import java.io.Serializable;

public class SpinnerItemModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String spinnerItemTypeId;
	private String spinnerItemType;
	private String spinnerItemName;
	private String spinnerCatType;

	public String getSpinnerItemType() {
		return spinnerItemType;
	}
	public void setSpinnerItemType(String spinnerItemType) {
		this.spinnerItemType = spinnerItemType;
	}
	public String getSpinnerItemName() {
		return spinnerItemName;
	}
	public void setSpinnerItemName(String spinnerItemName) {
		this.spinnerItemName = spinnerItemName;
	}
	public String getSpinnerCatType() {
		return spinnerCatType;
	}
	public void setSpinnerCatType(String spinnerCatType) {
		this.spinnerCatType = spinnerCatType;
	}
	public String getSpinnerItemTypeId() {
		return spinnerItemTypeId;
	}
	public void setSpinnerItemTypeId(String spinnerItemTypeId) {
		this.spinnerItemTypeId = spinnerItemTypeId;
	}
}
