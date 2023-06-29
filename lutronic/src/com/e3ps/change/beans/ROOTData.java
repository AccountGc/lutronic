package com.e3ps.change.beans;

import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

public class ROOTData {
	
	
	public String oid;
	public String name;
	public String name_eng;
	public int sortNumber;
	public String description;
	public String viewDescription;
	
	public ROOTData(EChangeActivityDefinitionRoot def) {
				
		this.oid = CommonUtil.getOIDString(def);
		this.name = StringUtil.checkNull(def.getName());
		this.name_eng = StringUtil.checkNull(def.getName_eng());
		this.sortNumber = def.getSortNumber();
		this.description = StringUtil.checkNull(def.getDescription());
		this.viewDescription = WebUtil.getHtml(description);
		
	}
	
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_eng() {
		return name_eng;
	}

	public void setName_eng(String name_eng) {
		this.name_eng = name_eng;
	}

	public int getSortNumber() {
		return sortNumber;
	}

	public void setSortNumber(int sortNumber) {
		this.sortNumber = sortNumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getViewDescription() {
		return viewDescription;
	}

	public void setViewDescription(String viewDescription) {
		this.viewDescription = viewDescription;
	}
	
	

}
