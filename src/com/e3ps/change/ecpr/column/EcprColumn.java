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

	public EcprColumn() {

	}
	
	public EcprColumn(Object[] obj) throws Exception {
		this((ECPRRequest) obj[0]);
	}
	
	public EcprColumn(ECPRRequest cr) throws Exception {
		setOid(cr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(cr.getEoName());
		setNumber(cr.getEoNumber());
		setChangeSection(EcprHelper.manager.displayToSection(cr.getChangeSection()));
		setModel(EcprHelper.manager.displayToModel(cr.getModel()));
		setCreateDepart(cr.getCreateDepart());
		setWriter(cr.getWriter());
		setApproveDate(cr.getApproveDate());
		setState(cr.getLifeCycleState().getDisplay());
		setCreator(cr.getCreatorFullName());
		setCreatedDate(cr.getCreateTimestamp());
		setCreatedDate_txt(cr.getCreateTimestamp().toString().substring(0, 10));
		setCreator(cr.getCreatorFullName());
		setWriteDate(cr.getCreateDate());
	}
}
