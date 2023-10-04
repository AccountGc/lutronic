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
	private String licensing;
	private String riskType;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String approveDate_txt;

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
		setState(eco.getLifeCycleState().getDisplay());
		setCreator(eco.getCreatorFullName());
		setCreatedDate(eco.getCreateTimestamp());
		setCreatedDate_txt(eco.getCreateTimestamp().toString().substring(0, 10));
		setApproveDate_txt(eco.getEoApproveDate());
	}
}
