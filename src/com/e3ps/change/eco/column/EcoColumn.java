package com.e3ps.change.eco.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcoColumn {

	private String oid;
	private String number;
	private String name;
	private String sendType;
	private String licensing;
	private String riskType;
	private String licensing_name;
	private String riskType_name;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String approveDate;
	private String model;

	public EcoColumn() {

	}

	public EcoColumn(Object[] obj) throws Exception {
		this((EChangeOrder) obj[0]);
	}

	public EcoColumn(EChangeOrder eco) throws Exception {
		setOid(eco.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(eco.getEoNumber());
		setName(eco.getEoName());
		setLicensing(eco.getLicensingChange());
		setRiskType(eco.getRiskType());
		setLicensing_name(licensing(eco.getLicensingChange()));
		setRiskType_name(risk(eco.getRiskType()));
		setState(eco.getLifeCycleState().getDisplay());
		setCreator(eco.getCreatorFullName());
		setCreatedDate(eco.getCreateTimestamp());
		setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
		setApproveDate(eco.getEoApproveDate());
		setSendTypeInfo(eco);
		setModel(eco.getModel());
	}

	private void setSendTypeInfo(EChangeOrder eco) throws Exception {
		String sendType = eco.getSendType();
		if ("ECO".equals(sendType)) {
			setSendType("ECO");
		} else if ("SCO".equals(sendType)) {
			setSendType("SCO");
		} else if ("ORDER".equals(sendType)) {
			setSendType("선구매");
		}
	}

	/**
	 * 위험통제 디스플레이
	 */
	private String risk(String s) throws Exception {
		if ("NONE".equals(s)) {
			return "N/A";
		} else if ("0".equals(s)) {
			return "불필요";
		} else if ("1".equals(s)) {
			return "필요";
		}
		return "";
	}

	/**
	 * 인허가 디스플레이
	 */
	private String licensing(String s) throws Exception {
		if ("NONE".equals(s)) {
			return "N/A";
		} else if ("0".equals(s)) {
			return "불필요";
		} else if ("1".equals(s)) {
			return "필요";
		}
		return "";
	}
}
