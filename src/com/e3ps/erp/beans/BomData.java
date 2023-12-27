package com.e3ps.erp.beans;

public class BomData {
	
	public String changeType; //신규 A,삭제 D,변경 C
	public String parent;
	public String child;
	public double amount;
	public String unit;
	public String ecoNumber;
	public String ver;
	
	public String pver;
	public String cver;
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public String getChild() {
		return child;
	}
	public void setChild(String child) {
		this.child = child;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getEcoNumber() {
		return ecoNumber;
	}
	public void setEcoNumber(String ecoNumber) {
		this.ecoNumber = ecoNumber;
	}
	public String getVer() {
		return ver;
	}
	public void setVer(String ver) {
		this.ver = ver;
	}
	public String getPver() {
		return pver;
	}
	public void setPver(String pver) {
		this.pver = pver;
	}
	public String getCver() {
		return cver;
	}
	public void setCver(String cver) {
		this.cver = cver;
	}
	
	
	
}
