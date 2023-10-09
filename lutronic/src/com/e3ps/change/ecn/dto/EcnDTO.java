package com.e3ps.change.ecn.dto;

import com.e3ps.change.EChangeNotice;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcnDTO {

	private String oid;
	private String name;
	private String number;

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
