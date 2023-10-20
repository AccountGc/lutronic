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
	
	// 외부 메일 변수
	private ArrayList<Map<String, String>> external = new ArrayList<Map<String, String>>();

	public EcnDTO() {

	}

	public EcnDTO(String oid) throws Exception {
		this((EChangeNotice) CommonUtil.getObject(oid));
	}

	public EcnDTO(EChangeNotice ecn) throws Exception {
		setOid(ecn.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(ecn.getEoName());
		setNumber(ecn.getEoNumber());
		setExternal(MailUserHelper.manager.getMailList(ecn.getPersistInfo().getObjectIdentifier().getStringValue()));
	}
}
