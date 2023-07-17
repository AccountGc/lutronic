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
import com.e3ps.development.devMaster;

public class MasterData{
	
//	public devMaster master;
	
	public String oid;
	
	public String name;
	public String fullName;
	
	public String model;
	public String modelName;
	
	public String developmentStart;
	public String developmentEnd;
	
	public WTUser dm;
	public String dmOid;
	public String dmName;
	
	public String state;
	
	public String creator;
	public String createDate;
	public String modifyDate;

	public MasterData(devMaster master) {
		
//		this.master = master;
		
		this.oid = master.getPersistInfo().getObjectIdentifier().toString();
		
		this.name = StringUtil.checkNull(master.getName());
		
		this.model = master.getModel();
		try {
			this.modelName = NumberCodeHelper.service.getValue("MODEL", model);
		} catch (Exception e) {
			e.printStackTrace();
			this.modelName = "";
		}
		this.fullName = "[" + this.model + "] " + this.name;
		
		this.developmentStart = master.getStartDay();
		this.developmentEnd = master.getEndDay();
		
		this.dm = master.getDm();
		this.dmOid = master.getDm().getPersistInfo().getObjectIdentifier().toString();
		this.dmName = master.getDm().getFullName();
		
		this.state = master.getLifeCycleState().getDisplay(Message.getLocale());
		
		this.creator = master.getCreatorFullName();
		this.createDate = DateUtil.getDateString(master.getPersistInfo().getCreateStamp(), "a");
		this.modifyDate = DateUtil.getDateString(master.getPersistInfo().getModifyStamp(), "a");
		
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
	
	public String dateSubString(boolean isCreateDate) {
		if(isCreateDate) {
			return DateUtil.subString(this.createDate, 0, 10);
		}else {
			return DateUtil.subString(this.modifyDate, 0, 10);
		}
	}
	
//	public String getDescription(boolean isView) {
//		String description = StringUtil.checkNull(this.master.getDescription());
//		if(isView) {
//			description = WebUtil.getHtml(description);
//		}
//		return description;
//	}
//	
//	public boolean isState(String state) {
//    	return (State.toState(state)).equals(this.master.getLifeCycleState());
//    }
//	
//	/**
//	 * 폴더 위치
//	 * @return
//	 */
//	public String getLocation() {
//		return StringUtil.checkNull(this.master.getLocation()).replaceAll("/Default","");
//	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDmOid() {
		return dmOid;
	}

	public void setDmOid(String dmOid) {
		this.dmOid = dmOid;
	}

//	public devMaster getMaster() {
//		return master;
//	}
//
//	public void setMaster(devMaster master) {
//		this.master = master;
//	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getDevelopmentStart() {
		return developmentStart;
	}

	public void setDevelopmentStart(String developmentStart) {
		this.developmentStart = developmentStart;
	}

	public String getDevelopmentEnd() {
		return developmentEnd;
	}

	public void setDevelopmentEnd(String developmentEnd) {
		this.developmentEnd = developmentEnd;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public WTUser getDm() {
		return dm;
	}

	public void setDm(WTUser dm) {
		this.dm = dm;
	}

	public String getDmName() {
		return dmName;
	}

	public void setDmName(String dmName) {
		this.dmName = dmName;
	}
	
}
