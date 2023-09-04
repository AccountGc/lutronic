package com.e3ps.common.code.beans;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.NumberCodeType;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberCodeData {
	
	public String code;
	public String name;
	public String engName;
	public String checked;
	public String oid;
	public boolean isSeq;			//자동 (true),수동(false)
	public String seqNm;
	public boolean isTree;			//Tree(true),
	public String sort;
	public boolean enabled;
	public String description;
	public String codeType;
	public String parentOid;
	
	public NumberCodeData() {
		
	}
	
	public NumberCodeData(NumberCode Ncode) {
		setCode(Ncode.getCode());
		setName(Ncode.getName());
		setEngName(Ncode.getEngName());
		setOid(CommonUtil.getOIDString(Ncode));
		NumberCodeType codeType = Ncode.getCodeType();
//		setSeq(codeType.getShortDescription().equals("true") ? true : false);
//		setSeqNm(codeType.getLongDescription());
		setTree(codeType.getAbbreviatedDisplay().equals("true") ? true : false);
		setSort(Ncode.getSort());
		setEnabled(Ncode.isDisabled());
		setDescription(Ncode.getDescription());
		setCodeType(codeType.toString());
		setParentOid(StringUtil.checkNull(CommonUtil.getOIDString(Ncode.getParent())));
	}
}
