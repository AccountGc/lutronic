package com.e3ps.common.beans;

import wt.content.ApplicationData;

public class BatchDownData {
	
	private String primaryOid;
	private Object primaryObject;
	private String objectType;
	private String oid;
	private String number;
	private String name;
	private String rev;
	private String creator;
	private String modifier;
	private ApplicationData appData; 
	private String attachType;
	private String fileName;
	private boolean isResult;
	private String msg;
	private String describe;
	
	public String getPrimaryOid() {
		return primaryOid;
	}
	public void setPrimaryOid(String primaryOid) {
		this.primaryOid = primaryOid;
	}
	
	
	public Object getPrimaryObject() {
		return primaryObject;
	}
	public void setPrimaryObject(Object primaryObject) {
		this.primaryObject = primaryObject;
	}
	public String getObjectType() {
		return objectType;
	}
	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRev() {
		return rev;
	}
	
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getModifier() {
		return modifier;
	}
	public void setModifier(String modifier) {
		this.modifier = modifier;
	}
	public ApplicationData getAppData() {
		return appData;
	}
	public void setAppData(ApplicationData appData) {
		this.appData = appData;
	}
	public void setRev(String rev) {
		this.rev = rev;
	}
	public String getAttachType() {
		return attachType;
	}
	public void setAttachType(String attachType) {
		this.attachType = attachType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public boolean isResult() {
		return isResult;
	}
	public void setResult(boolean isResult) {
		this.isResult = isResult;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getDescribe() {
		return describe;
	}
	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
	
	
	
}
