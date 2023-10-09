package com.e3ps.change.eco.dto;

import java.util.ArrayList;
import java.util.Map;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcoDTO {

	private String oid;
	private String name;
	private String eoCommentA;
	private String eoCommentB;
	private String eoCommentC;
	private String eoCommentD;
	private String licensing;
	private String riskType;
	private String eoType;
	// 변수용
	private String primary;
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> rows101 = new ArrayList<>(); // 관련 CR
	private ArrayList<Map<String, String>> rows200 = new ArrayList<>(); // 설변활동
	private ArrayList<Map<String, String>> rows500 = new ArrayList<>(); // 대상품목

	public EcoDTO() {

	}

	public EcoDTO(String oid) throws Exception {
		this((EChangeOrder) CommonUtil.getObject(oid));
	}

	public EcoDTO(EChangeOrder eco) throws Exception {
		setOid(eco.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(eco.getEoName());
		setEoCommentA(eco.getEoCommentA());
		setEoCommentB(eco.getEoCommentB());
		setEoCommentC(eco.getEoCommentC());
		setEoCommentD(eco.getEoCommentD());
		setLicensing(eco.getLicensingChange());
		setRiskType(eco.getRiskType());
		setEoType(eco.getEoType());
	}
}
