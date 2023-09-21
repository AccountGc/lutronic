package com.e3ps.change.beans;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.DateUtil;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class ECNData{
	private String oid;
	private String number;
	private String name;
	private String state;
	private String stateDisplay;
	private String creator;
	private String createDate;
	private String eoCommentA;
	private String eoCommentB;
	
	public ECNData() {
		
	}
	
	public ECNData(final EChangeNotice ecn) {
		setOid(ecn.getPersistInfo().getObjectIdentifier().toString());
		setNumber(ecn.getEoNumber());
		setName(ecn.getEoName());
		setState(ecn.getLifeCycleState().toString());
		setStateDisplay(ecn.getLifeCycleState().getDisplay());
		setCreator(ecn.getCreatorFullName());
		setCreateDate(DateUtil.getDateString(ecn.getCreateTimestamp(), "d"));
		setEoCommentA(ecn.getEoCommentA());
		setEoCommentB(ecn.getEoCommentB());
	}
}	
