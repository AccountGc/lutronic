package com.e3ps.change.beans;

import wt.org.WTUser;

import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.org.People;
import com.e3ps.org.service.UserHelper;

public class EADData {
	
	
	public String oid;
	public String rootOid;
	public String name;
	public String name_eng;
	public String step;
	public String stepName;
	public String stepSort;
	public String activityType;
	public String activityName;
	//public WTUser activeUser;
	public String activeUserOid;
	public String activeUserName;
	public String departName;
	public int sortNumber;
	public String description;
	public String viewDescription;
	public String finishDate;
	public boolean isModify = true;
	public EADData(final EChangeActivityDefinition ead) {
		
		if(ead == null){
			return;
		}
		this.oid = CommonUtil.getOIDString(ead);
		this.rootOid = CommonUtil.getOIDString(ead.getRoot());
		this.name = StringUtil.checkNull(ead.getName());
		this.name_eng = StringUtil.checkNull(ead.getName_eng());
		this.step = StringUtil.checkNull(ead.getStep());
		this.activityType = StringUtil.checkNull(ead.getActiveType());
		this.activityName = ChangeUtil.getActivityName(ead.getActiveType());
		//this.activeUser = ead.getActiveUser();
		this.activeUserOid = "";
		this.activeUserName = "";
		if(ead.getActiveUser() != null){
			this.activeUserOid = CommonUtil.getOIDString(ead.getActiveUser());
			this.activeUserName = ead.getActiveUser().getFullName();
		}
		
		this.sortNumber = ead.getSortNumber();
		this.description = StringUtil.checkNull(ead.getDescription());
		this.viewDescription = WebUtil.getHtml(description);
		this.isModify = true;
		
		
	}
	
	
	/**
	 * eca 담당자 부서명
	 * @return
	 
	
	public String departName(){
		
		String departName = "";
		EChangeActivityDefinition ad = (EChangeActivityDefinition)CommonUtil.getObject(this.oid);
		if(ad.getActiveUser() !=null){
			
			People pp = UserHelper.service.getPeople(ad.getActiveUser());
			if(pp != null && pp.getDepartment() != null){
				departName = pp.getDepartment().getName();
			}
		}
		
		return departName;
	}
	*/
	/**
	 * Step의 Name
	 * @return
	 
	
	public String getStepName(){
		String stepName = "";
		NumberCode code =NumberCodeHelper.service.getNumberCode("EOSTEP", this.step);
		if(code != null){
			stepName = code.getName();
		}
		
		return stepName;
	}
*/
	/**일반 속성 **/
	
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

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}


	public String getStepName() {
		return stepName;
	}


	public String getFinishDate() {
		return finishDate;
	}


	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}


	public String getStepSort() {
		return stepSort;
	}


	public void setStepSort(String stepSort) {
		this.stepSort = stepSort;
	}


	public boolean isModify() {
		return isModify;
	}


	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	
	
}
