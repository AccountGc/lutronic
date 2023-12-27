package com.e3ps.common.beans;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ResultData {
	
	public boolean result;
	public String message;
	public String oid;
	public File returnFile;
	public List<BatchDownData> batchData;
	public List<Map<String, String>> returnList;
	
	public boolean isResult() {
		return result;
	}
	public void setResult(boolean result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public File getReturnFile() {
		return returnFile;
	}
	public void setReturnFile(File returnFile) {
		this.returnFile = returnFile;
	}
	public List<BatchDownData> getBatchData() {
		return batchData;
	}
	public void setBatchData(List<BatchDownData> batchData) {
		this.batchData = batchData;
	}
	public List<Map<String, String>> getReturnList() {
		return returnList;
	}
	public void setReturnList(List<Map<String, String>> returnList) {
		this.returnList = returnList;
	}
	
	
	

}
