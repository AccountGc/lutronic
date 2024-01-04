package com.e3ps.change.ecpr.column;

import java.sql.Timestamp;

import com.e3ps.change.ECPRRequest;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.change.ecpr.service.EcprHelper;
import com.e3ps.common.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcprColumn {
	private String oid;
	private String name;
	private String number;
	private String changeSection;
	private String model;
	private String createDepart;
	private String writer;
	private String approveDate;
	private String state;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String creator;
	private String writeDate;
	private String period;

	public EcprColumn() {

	}
	
	public EcprColumn(Object[] obj) throws Exception {
		this((ECPRRequest) obj[0]);
	}
	
	public EcprColumn(ECPRRequest ecpr) throws Exception {
		setOid(ecpr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(ecpr.getEoName());
		setNumber(ecpr.getEoNumber());
		setChangeSection(EcprHelper.manager.displayToSection(ecpr.getChangeSection()));
		setModel(EcprHelper.manager.displayToModel(ecpr.getModel()));
		setCreateDepart(ecpr.getCreateDepart());
		setWriter(ecpr.getWriter());
		setApproveDate(ecpr.getApproveDate());
		setState(ecpr.getLifeCycleState().getDisplay());
		setCreator(ecpr.getCreatorFullName());
		setCreatedDate(ecpr.getCreateTimestamp());
		setCreatedDate_txt(ecpr.getCreateTimestamp().toString().substring(0, 10));
		setCreator(ecpr.getCreatorFullName());
		setWriteDate(ecpr.getCreateDate());
		setPeriod(ecpr.getPeriod());
	}
}
