package com.e3ps.common.code.beans;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;

public class NumberCodeData {
	
	public String code;
	public String name;
	public String checked;
	public String oid;
	public boolean isSeq;			//자동 (true),수동(false)
	public String seqNm;
	public boolean isTree;			//Tree(true),
	public String sort;
	public NumberCode Ncode;
	
	public NumberCodeData(NumberCode Ncode) {
		this.code = Ncode.getCode();
		this.name = Message.getNC(Ncode);
		this.oid = CommonUtil.getOIDString(Ncode);
		NumberCodeType codeType = Ncode.getCodeType();
		this.isSeq = codeType.getShortDescription().equals("true") ? true : false;
		this.seqNm = codeType.getLongDescription();
		this.isTree = codeType.getAbbreviatedDisplay().equals("true") ? true : false;
		this.sort = Ncode.getSort();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public boolean isSeq() {
		return isSeq;
	}

	public void setSeq(boolean isSeq) {
		this.isSeq = isSeq;
	}

	public String getSeqNm() {
		return seqNm;
	}

	public void setSeqNm(String seqNm) {
		this.seqNm = seqNm;
	}

	public boolean isTree() {
		return isTree;
	}

	public void setTree(boolean isTree) {
		this.isTree = isTree;
	}

	public NumberCode getNcode() {
		return Ncode;
	}

	public void setNcode(NumberCode ncode) {
		Ncode = ncode;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	
	
	
}
