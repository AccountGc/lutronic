package com.e3ps.change.activity.dto;

import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class DefDTO {

	private String oid;
	private String name;
	private int sort;
	private String description = "";

	public DefDTO() {

	}

	public DefDTO(String oid) throws Exception {
		this((EChangeActivityDefinitionRoot) CommonUtil.getObject(oid));
	}

	public DefDTO(EChangeActivityDefinitionRoot def) throws Exception {
		setOid(def.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(def.getName());
		setSort(def.getSortNumber());
		setDescription(def.getDescription()==null?"":def.getDescription());
	}
}
