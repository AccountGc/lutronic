package com.e3ps.change.beans;

import wt.org.WTUser;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;

public class DEFData {
	
	public String oid;
	public String rootOid;
	public String name;
	public String name_eng;
	public String step;
	public String activityType;
	public String activityName;
	public WTUser activeUser;
	public String activeUserOid;
	public String activeUserName;
	public int sortNumber;
	public String description;
	public String viewDescription;
	
	public DEFData(EChangeActivityDefinition def) {
		
		this.oid = CommonUtil.getOIDString(def);
		this.rootOid = CommonUtil.getOIDString(def.getRoot());
		this.name = StringUtil.checkNull(def.getName());
		this.name_eng = StringUtil.checkNull(def.getName_eng());
		this.step = StringUtil.checkNull(def.getStep());
		this.activityType = StringUtil.checkNull(def.getActiveType());
		this.activityName = ChangeUtil.getActivityName(this.activityType);
		this.activeUser = def.getActiveUser();
		if(activeUser != null){
			this.activeUserOid = CommonUtil.getOIDString(activeUser);
			this.activeUserName = activeUser.getFullName();
		}
		
		this.sortNumber = def.getSortNumber();
		this.description = StringUtil.checkNull(def.getDescription());
		this.viewDescription = WebUtil.getHtml(description);
		
	}
	
	public String getStepName(){
		
		String stepName ="";
		NumberCode code =NumberCodeHelper.service.getNumberCode("EOSTEP", this.step);
		
		if(code != null){
			stepName = code.getName();
		}
		
		return stepName;
	}
	
	/**
	 * 일반 속성
	 * @return
	 */

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
	
	public String getRootOid() {
		return rootOid;
	}

	public void setRootOid(String rootOid) {
		this.rootOid = rootOid;
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

	public String getStep() {
		return step;
	}

	public void setStep(String step) {
		this.step = step;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
	
	
	public WTUser getActiveUser() {
		return activeUser;
	}

	public void setActiveUser(WTUser activeUser) {
		this.activeUser = activeUser;
	}
	
	

	public String getActiveUserOid() {
		return activeUserOid;
	}

	public void setActiveUserOid(String activeUserOid) {
		this.activeUserOid = activeUserOid;
	}

	public String getActiveUserName() {
		return activeUserName;
	}

	public void setActiveUserName(String activeUserName) {
		this.activeUserName = activeUserName;
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
