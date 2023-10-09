package com.e3ps.change.eo.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
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
	private String model_name;
	private String state;
	private String creator;
	private String createdDate;
	private String modifiedDate;
	private String eoCommentA = "";
	private String eoCommentB = "";
	private String eoCommentC = "";

	// 변수용
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows104 = new ArrayList<>(); // 완제품
	private ArrayList<Map<String, String>> rows90 = new ArrayList<>(); // 관련문서
	private ArrayList<Map<String, String>> rows200 = new ArrayList<>(); // ECA
	private ArrayList<Map<String, String>> rows300 = new ArrayList<>(); // 제품코드

	public EoDTO() {

	}

	public EoDTO(String oid) throws Exception {
		this((EChangeOrder) CommonUtil.getObject(oid));
	}

	public EoDTO(EChangeOrder eo) throws Exception {
		setOid(eo.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(eo.getEoNumber());
		setName(eo.getEoName());
		setEoType(eo.getEoType());
		// 모델 코드 처리??
		if (eo.getModel() != null) {
			setModel_name(EoHelper.manager.displayToModel(eo.getModel()));
		}
		setState(eo.getLifeCycleState().getDisplay());
		setCreator(eo.getCreatorFullName());
		setCreatedDate(eo.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(eo.getModifyTimestamp().toString().substring(0, 10));
		setEoCommentA(eo.getEoCommentA());
		setEoCommentB(eo.getEoCommentB());
		setEoCommentC(eo.getEoCommentC());

	}
}
