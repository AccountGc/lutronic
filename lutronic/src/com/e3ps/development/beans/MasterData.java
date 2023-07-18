package com.e3ps.development.beans;

import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.development.devMaster;

import lombok.Getter;
import lombok.Setter;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Getter
@Setter
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

	public MasterData(devMaster master) throws Exception{
		
//		setMaster(master);
		
		setOid(master.getPersistInfo().getObjectIdentifier().toString());
		
		setName(StringUtil.checkNull(master.getName()));
		
		setModel(master.getModel());
		try {
			setModelName(NumberCodeHelper.service.getValue("MODEL", model));
		} catch (Exception e) {
			e.printStackTrace();
			setModelName("");
		}
		setFullName( "[" + getModel() + "] " + getName());
		
		setDevelopmentStart(master.getStartDay());
		setDevelopmentEnd(master.getEndDay());
		
//		setDm(master.getDm());
//		setDmOid(master.getDm().getPersistInfo().getObjectIdentifier().toString());
//		setDmName(master.getDm().getFullName());
		
		setState(master.getLifeCycleState().getDisplay(Message.getLocale()));
		
		setCreator(master.getCreatorFullName());
		setCreateDate(DateUtil.getDateString(master.getPersistInfo().getCreateStamp(), "a"));
		setModifyDate(DateUtil.getDateString(master.getPersistInfo().getModifyStamp(), "a"));
		
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
	
}
