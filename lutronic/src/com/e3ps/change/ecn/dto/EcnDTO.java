package com.e3ps.change.ecn.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.org.service.MailUserHelper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcnDTO {

	private String oid;
	private String name;
	private String number;
	private String eoCommentA;
	private String eoCommentB;
	
	// 결재 변수
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신
	private boolean self; // 자가 결재
	
	private boolean temprary;


	public EcnDTO() {

	}

	public EcnDTO(String oid) throws Exception {
		this((EChangeNotice) CommonUtil.getObject(oid));
	}

	public EcnDTO(EChangeNotice ecn) throws Exception {
		setOid(ecn.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(ecn.getEoName());
		setNumber(ecn.getEoNumber());
	}
}
