package com.e3ps.development.beans;

import wt.lifecycle.State;
import wt.org.WTUser;
import wt.session.SessionHelper;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.development.devActive;
import com.e3ps.development.devMaster;
import com.e3ps.development.devTask;
import com.e3ps.development.service.DevelopmentQueryHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevActiveData {

	private String oid;
	
	private devMaster master;
	private String masterName;
	
	private devTask task;
	private String taskName;
	
	private devActive active;
	
	private WTUser dm;
	private String dmName;
	
	private String name;
	private String activeDate;
	private String finishDate;
	
	private String state;
	
	private WTUser worker;
	private String workerOid;
	private String workerName;
	
	public DevActiveData(devActive active){
		setOid(active.getPersistInfo().getObjectIdentifier().toString());
		setMaster(active.getMaster());
		String masterNm = "[" + StringUtil.checkNull(this.master.getModel()) + "] " + StringUtil.checkNull(this.master.getName());
		setMasterName(masterNm);
		setTask(active.getTask());
		setTaskName(active.getTask().getName());
		setActive(active);
		setDm(active.getDm());
		setDmName(active.getDm().getFullName());
		setName(active.getName());
		setActiveDate(active.getActiveDate());
		setFinishDate(DateUtil.getDateString(active.getFinishDate(), "a"));
		setState(active.getLifeCycleState().getDisplay(Message.getLocale()));
		setWorker(active.getWorker());
		setWorkerOid(active.getWorker().getPersistInfo().getObjectIdentifier().toString());
		setWorkerName(active.getWorker().getFullName());
	}
	
	public boolean isAdmin() {
		try {
			return CommonUtil.isAdmin();
		} catch(Exception e) {
			return false;
		}
	}
	
	public boolean isDm() {
		try {
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			return (user.getPersistInfo().getObjectIdentifier().toString()).equals((this.dm.getPersistInfo().getObjectIdentifier().toString()));
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isWorker() {
		try {
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			return (user.getPersistInfo().getObjectIdentifier().toString()).equals(workerOid);
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String getDescription(boolean isView) {
		String description = StringUtil.checkNull(this.active.getDescription());
		if(isView) {
			description = WebUtil.getHtml(description);
		}
		return description;
	}
	
	public boolean isDelete() {
		return DevelopmentQueryHelper.service.isActiveDelete(this.oid);
	}
}
