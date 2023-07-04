package com.e3ps.change.beans;

import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class ROOTData {
	private String oid;
	private String name;
	private String name_eng;
	private int sortNumber;
	private String description;
	private String viewDescription;
	
	public ROOTData(EChangeActivityDefinitionRoot def) {
		setOid(CommonUtil.getOIDString(def));
		setName(StringUtil.checkNull(def.getName()));
		setName_eng(StringUtil.checkNull(def.getName_eng()));
		setSortNumber(def.getSortNumber());
		setDescription(StringUtil.checkNull(def.getDescription()));
		setViewDescription(WebUtil.getHtml(description));
	}
}
