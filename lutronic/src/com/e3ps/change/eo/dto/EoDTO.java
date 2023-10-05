package com.e3ps.change.eo.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
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
	private String model_oid;
	private String model_name;
	private String model_code;
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
		setName(eo.getEoName());
		setEoType(eo.getEoType());
		// 모델 코드 처리??
		if (eo.getModel() != null) {
			NumberCode model = NumberCodeHelper.manager.getNumberCode(eo.getModel(), "MODEL");
			setModel_oid(model.getPersistInfo().getObjectIdentifier().getStringValue());
			setModel_name(model.getName());
			setModel_code(model.getCode());
		}
		setState(eo.getLifeCycleState().getDisplay());
		setCreator(eo.getCreatorFullName());
		setEoCommentA(eo.getEoCommentA());
		setEoCommentB(eo.getEoCommentB());
		setEoCommentC(eo.getEoCommentC());

	}
}
