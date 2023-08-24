package com.e3ps.change.beans;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.DateUtil;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class ECNData extends EOData{
	
	private String oid;
	private String number;
	private String name;
	private String state;
	private String creator;
	private String createDateD;
	
	public ECNData(final EChangeNotice ecn) {
		super(ecn);
		
		setOid(ecn.getPersistInfo().getObjectIdentifier().toString());
		setNumber(ecn.getEoNumber());
		setName(ecn.getEoName());
		setState(ecn.getLifeCycleState().getDisplay());
		setCreator(ecn.getCreatorFullName());
		setCreateDateD(DateUtil.getDateString(ecn.getCreateTimestamp(), "d"));
	}
}	
