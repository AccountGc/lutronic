package com.e3ps.change.ecrm.column;

import java.sql.Timestamp;

import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecpr.service.EcprHelper;
import com.e3ps.change.ecrm.service.EcrmHelper;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcrmColumn {
	private int rowNum;
	private String oid;
	private String name;
	private String number;
	private String model;
	private String changeSection;
	private String createDepart;
	private String writer;
	private String writeDate;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String approveDate;
	private String period;

	public EcrmColumn() {

	}

	public EcrmColumn(Object[] obj) throws Exception {
		this((ECRMRequest) obj[0]);
	}

	public EcrmColumn(ECRMRequest ecrm) throws Exception {
		setOid(ecrm.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(ecrm.getEoName());
		setNumber(ecrm.getEoNumber());
		setModel(CrHelper.manager.displayToModel(ecrm.getModel()));
		setChangeSection(EcrmHelper.manager.displayToSection(ecrm.getChangeSection()));
		setCreateDepart(ecrm.getCreateDepart());
		setWriter(ecrm.getWriter());
		setWriteDate(ecrm.getCreateDate());
		setState(ecrm.getLifeCycleState().getDisplay());
		setCreator(ecrm.getCreatorFullName());
		setCreatedDate(ecrm.getCreateTimestamp());
		setCreatedDate_txt(ecrm.getCreateTimestamp().toString().substring(0, 10));
		setApproveDate(ecrm.getApproveDate());
		NumberCode period = NumberCodeHelper.manager.getNumberCode(ecrm.getPeriod(), "PRESERATION");
		if (period != null) {
			setPeriod(period.getName());
		}
	}
}
