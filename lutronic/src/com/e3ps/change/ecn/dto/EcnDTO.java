package com.e3ps.change.ecn.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.change.ecn.service.EcnHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.service.MailUserHelper;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;

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
	
	private JSONArray list = new JSONArray();

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
		setList(EcnHelper.manager.getEcnGroupPart(ecn));
	}
}
