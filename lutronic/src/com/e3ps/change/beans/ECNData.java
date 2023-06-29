package com.e3ps.change.beans;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.DateUtil;



public class ECNData extends EOData{
	
	public String oid;
	public String number;
	public String name;
	public String state;
	public String creator;
	public String createDateD;
	
	public ECNData(final EChangeNotice ecn) {
		super(ecn);
		
		this.oid = ecn.getPersistInfo().getObjectIdentifier().toString();
		this.number = ecn.getEoNumber();
		this.name = ecn.getEoName();
		this.state = ecn.getLifeCycleState().getDisplay(Message.getLocale());
		this.creator = ecn.getCreatorFullName();
		this.createDateD = DateUtil.getDateString(ecn.getCreateTimestamp(), "d");
	}

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getCreateDateD() {
		return createDateD;
	}

	public void setCreateDateD(String createDateD) {
		this.createDateD = createDateD;
	}
	
}	
