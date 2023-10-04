package com.e3ps.change.eo.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EoDTO {

	private String oid;
	private String number;
	private String name;
	private String eoType;
	private String state;
	private String creator;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;

	// 변수용
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows104 = new ArrayList<>(); // 완제품
	private ArrayList<Map<String, String>> rows90 = new ArrayList<>(); // 관련문서

	public EoDTO() {

	}

	public EoDTO(String oid) throws Exception {
		this((EChangeOrder) CommonUtil.getObject(oid));
	}

	public EoDTO(EChangeOrder eo) throws Exception {
		setOid(eo.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(eo.getEoNumber());
		setEoType(eo.getEoType());
	}
}
