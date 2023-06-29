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

public class DevActiveData {

	public String oid;
	
	public devMaster master;
	public String masterName;
	
	public devTask task;
	public String taskName;
	
	public devActive active;
	
	public WTUser dm;
	public String dmName;
	
	public String name;
	public String activeDate;
	public String finishDate;
	
	public String state;
	
	public WTUser worker;
	public String workerOid;
	public String workerName;
	
	public DevActiveData(devActive active){
		
		this.oid = active.getPersistInfo().getObjectIdentifier().toString();

		this.master = active.getMaster();
		this.masterName = "[" + StringUtil.checkNull(this.master.getModel()) + "] " + StringUtil.checkNull(this.master.getName());
		
		this.task = active.getTask();
		this.taskName = active.getTask().getName();
		
		this.active = active;
		
		this.dm = active.getDm();
		this.dmName = active.getDm().getFullName();
		
		this.name = active.getName();
		this.activeDate = active.getActiveDate();
		this.finishDate = DateUtil.getDateString(active.getFinishDate(), "a");
		
		this.state = active.getLifeCycleState().getDisplay(Message.getLocale());
		
		this.worker = active.getWorker();
		this.workerOid = this.worker.getPersistInfo().getObjectIdentifier().toString();
		this.workerName = this.worker.getFullName();
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
	
    public devActive getActive() {
		return active;
	}

	public void setActive(devActive active) {
		this.active = active;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}

	public boolean isState(String state) {
    	return (State.toState(state)).equals(active.getLifeCycleState());
    }
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public devMaster getMaster() {
		return master;
	}

	public void setMaster(devMaster master) {
		this.master = master;
	}

	public devTask getTask() {
		return task;
	}

	public void setTask(devTask task) {
		this.task = task;
	}

	public WTUser getDm() {
		return dm;
	}

	public void setDm(WTUser dm) {
		this.dm = dm;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(String activeDate) {
		this.activeDate = activeDate;
	}

	public String getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(String finishDate) {
		this.finishDate = finishDate;
	}

	public WTUser getWorker() {
		return worker;
	}

	public void setWorker(WTUser worker) {
		this.worker = worker;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getWorkerOid() {
		return workerOid;
	}

	public void setWorkerOid(String workerOid) {
		this.workerOid = workerOid;
	}

	public String getWorkerName() {
		return workerName;
	}

	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	public String getMasterName() {
		return masterName;
	}

	public void setMasterName(String masterName) {
		this.masterName = masterName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
}
