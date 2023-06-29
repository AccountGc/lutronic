package com.e3ps.change.beans;

import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.editor.BEContext;
import com.e3ps.change.service.ChangeHelper;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.org.People;
import com.e3ps.org.beans.PeopleData;
import com.e3ps.org.service.UserHelper;

import wt.org.WTUser;
import wt.vc.wip.WorkInProgressHelper;



public class ECAData{
	
	
	public ImageIcon icon;
	
    public String oid;
    public String step;
    public String stepName;
    public String stepSort;
    public String name;
    public String activityType;
    public String activityName;
    //public String departmentName;
    public String activeUserOid;
    public String activeUserName;
    public String finishDate;
    public String state;
    public String stateName;
    public String completeDate;
    public String description;
    public String comments;
    public String departName;
    public boolean isAutoCreate=false;
    public boolean isModify = false;
    public List<DocumentData> docList;
    
    
	public ECAData(final EChangeActivity eca) {
		
		this.oid = CommonUtil.getOIDString(eca);
		this.step = eca.getStep();
		this.name = eca.getName();
		this.activityType = eca.getActiveType();
		this.activityName = ChangeUtil.getActivityName(eca.getActiveType());
		this.activeUserName ="";
		
		if(eca.getActiveUser() != null){
			activeUserName = eca.getActiveUser().getFullName();
			activeUserOid =CommonUtil.getOIDString(eca.getActiveUser());
		}
		
		this.finishDate =  DateUtil.getDateString(eca.getFinishDate(), "d");
		this.state = eca.getLifeCycleState().toString();
		this.stateName = eca.getLifeCycleState().getDisplay();
		
		if(state.equals("COMPLETED")){
    		this.completeDate = DateUtil.getDateString(eca.getModifyTimestamp(),"d");
    	}
		
		this.description =  WebUtil.getHtml(eca.getDescription());
		this.comments =  WebUtil.getHtml(eca.getComments());
		String eoNumber = eca.getEo().getEoNumber();
		this.isAutoCreate = this.name.startsWith(eoNumber);
		this.isModify = !state.equals("COMPLETED");
		
	}

	public String getStepSort() {
		return stepSort;
	}

	public void setStepSort(String stepSort) {
		this.stepSort = stepSort;
	}

	public String getDepartmentName(){
		
		String departmentName ="";
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(oid);
				
		if(eca.getActiveUser() != null){
			WTUser user = eca.getActiveUser();
			People pp = UserHelper.service.getPeople(user);
			if(pp != null && pp.getDepartment() != null){
				departmentName = pp.getDepartment().getName();
			}
			
		}
		
		return departmentName;
	}
	
	
	
	
	public ImageIcon getIcon() {
		
		return icon;
	}


	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}


	public String getOid() {
		return oid;
	}


	public void setOid(String oid) {
		this.oid = oid;
	}


	public String getStep() {
		return step;
	}


	public void setStep(String step) {
		this.step = step;
	}


	public String getStepName() {
		
		String stepName = "";
		NumberCode code =NumberCodeHelper.service.getNumberCode("EOSTEP", this.step);
		if(code != null){
			stepName = code.getName();
		}
		
		return stepName;
	}


	public void setStepName(String stepName) {
		this.stepName = stepName;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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

	public String getActiveUserName() {
		return activeUserName;
	}

	public void setActiveUserName(String activeUserName) {
		this.activeUserName = activeUserName;
	}

	public String getFinishDate() {
		return finishDate;
	}


	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getStateName() {
		return stateName;
	}


	public void setStateName(String stateName) {
		this.stateName = stateName;
	}


	public String getCompleteDate() {
		return completeDate;
	}


	public void setCompleteDate(String completeDate) {
		this.completeDate = completeDate;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<DocumentData> getDocList() {
		return docList;
	}

	public void setDocList(List<DocumentData> docList) {
		this.docList = docList;
	}

	public boolean isAutoCreate() {
		return isAutoCreate;
	}

	public void setAutoCreate(boolean isAutoCreate) {
		this.isAutoCreate = isAutoCreate;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public boolean isModify() {
		return isModify;
	}

	public void setModify(boolean isModify) {
		this.isModify = isModify;
	}

	public String getActiveUserOid() {
		return activeUserOid;
	}

	public void setActiveUserOid(String activeUserOid) {
		this.activeUserOid = activeUserOid;
	}
	
	
	
	
	
}	
