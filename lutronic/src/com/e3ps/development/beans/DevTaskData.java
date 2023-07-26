package com.e3ps.development.beans;

import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.development.devMaster;
import com.e3ps.development.devTask;
import com.e3ps.development.service.DevelopmentQueryHelper;

import lombok.Getter;
import lombok.Setter;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Getter
@Setter
public class DevTaskData {
	
//	public devTask task;
	public String oid;
	
	public String name;
	public String description;
	public String state;
	
	public devMaster master;

	public DevTaskData(devTask task) {
//		this.task = task;
		
		setOid(task.getPersistInfo().getObjectIdentifier().toString());
		
		setName(task.getName());
		setDescription(task.getDescription());
		setState(task.getLifeCycleState().getDisplay(Message.getLocale()));
		
		setMaster(task.getMaster());
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
	
//	public String getDescription(boolean isView) {
//		String description = StringUtil.checkNull(this.task.getDescription());
//		if(isView) {
//			description = WebUtil.getHtml(description);
//		}
//		return description;
//	}
	
//	public boolean isState(String state) {
//    	return (State.toState(state)).equals(task.getLifeCycleState());
//    }
	
	public boolean isDelete() {
		return DevelopmentQueryHelper.service.isTaskDelete(this.oid);
	}
	
}
