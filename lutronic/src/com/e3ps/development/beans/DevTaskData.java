package com.e3ps.development.beans;

import wt.lifecycle.State;
import wt.org.WTUser;
import wt.session.SessionHelper;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.development.devMaster;
import com.e3ps.development.devTask;
import com.e3ps.development.service.DevelopmentQueryHelper;
import com.e3ps.development.service.DevelopmentQueryService;

public class DevTaskData {
	
	public devTask task;
	public String oid;
	
	public String name;
	public String description;
	public String state;
	
	public devMaster master;

	public DevTaskData(devTask task) {
		this.task = task;
		
		this.oid = task.getPersistInfo().getObjectIdentifier().toString();
		
		this.name = task.getName();
		this.description = task.getDescription();
		this.state = task.getLifeCycleState().getDisplay(Message.getLocale());
		
		this.master = task.getMaster();
	}

	public boolean isDm() {
		try {
			WTUser user = (WTUser)SessionHelper.manager.getPrincipal();
			return (user.getPersistInfo().getObjectIdentifier().toString()).equals(((this.master.getDm()).getPersistInfo().getObjectIdentifier().toString()));
		} catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isAdmin() {
		try {
			return CommonUtil.isAdmin();
		} catch(Exception e) {
			return false;
		}
	}
	
	public String getDescription(boolean isView) {
		String description = StringUtil.checkNull(this.task.getDescription());
		if(isView) {
			description = WebUtil.getHtml(description);
		}
		return description;
	}
	
	public boolean isState(String state) {
    	return (State.toState(state)).equals(task.getLifeCycleState());
    }
	
	public boolean isDelete() {
		return DevelopmentQueryHelper.service.isTaskDelete(this.oid);
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public devMaster getMaster() {
		return master;
	}

	public void setMaster(devMaster master) {
		this.master = master;
	}

}
