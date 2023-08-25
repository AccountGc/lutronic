package com.e3ps.doc.template;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentTemplateData {

	private String oid;
	private String docTemplateNumber;
	private String name;
	private NumberCode docTemplateType;
	private String description;
	
	public DocumentTemplateData() {
		
	}
	
	public DocumentTemplateData(DocumentTemplate docTemp) {
		setOid(CommonUtil.getOIDString(docTemp));
		setDocTemplateNumber(docTemp.getDocTemplateNumber());
		setName(docTemp.getName());
		setDocTemplateType(docTemp.getDocTemplateType());
		setDescription(docTemp.getDescription());
	}

}
