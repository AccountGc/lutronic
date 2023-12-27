package com.e3ps.change.ecn.dto;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class EcnDTO {

	private String oid;
	private String name;
	private String number;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String eoCommentD;
	private String eoCommentE;
	private String workId;
	private boolean editable = false;

	public EcnDTO() {

	}

	public EcnDTO(String oid) throws Exception {
		this((EChangeNotice) CommonUtil.getObject(oid));
	}

	public EcnDTO(EChangeNotice ecn) throws Exception {
		setOid(ecn.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(ecn.getEoName());
		setNumber(ecn.getEoNumber());
		setEoCommentA(ecn.getEoCommentA());
		setEoCommentB(ecn.getEoCommentB());
		setEoCommentC(ecn.getEoCommentC());
		setEoCommentD(ecn.getEoCommentD());
		setEoCommentE(ecn.getEoCommentE());
		setWorkId(ecn.getWorker() != null ? ecn.getWorker().getName() : "");
		setAuth();
	}

	private void setAuth() throws Exception {
		WTUser user = CommonUtil.sessionUser();
		if (user.getName().equals(getWorkId()) || CommonUtil.isAdmin()) {
			setEditable(true);
		}
//		if (user.getName().equals(getWorkId()) || CommonUtil.isAdmin()) {
//			setEditable(true);
//		}
	}
}
