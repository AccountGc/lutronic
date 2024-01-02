package com.e3ps.change.cr.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.service.CrHelper;
import com.e3ps.common.util.CommonUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrColumn {

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
	private String ecprStart;

	public CrColumn() {

	}

	public CrColumn(Object[] obj) throws Exception {
		this((EChangeRequest) obj[0]);
	}

	public CrColumn(EChangeRequest cr) throws Exception {
		setOid(cr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(cr.getEoName());
		setNumber(cr.getEoNumber());
		setModel(CrHelper.manager.displayToModel(cr.getModel()));
		setChangeSection(CrHelper.manager.displayToSection(cr.getChangeSection()));
//		setCreateDepart(CrHelper.manager.displayToDept(cr.getCreateDepart()));
		setCreateDepart(cr.getCreateDepart());
		setWriter(cr.getWriter());
		setWriteDate(cr.getCreateDate());
		setState(cr.getLifeCycleState().getDisplay());
		setCreator(cr.getCreatorFullName());
		setCreatedDate(cr.getCreateTimestamp());
		setCreatedDate_txt(cr.getCreateTimestamp().toString().substring(0, 10));
		setApproveDate(cr.getApproveDate());
		setEcprStart(cr.getEcprStart() ? "ECPR진행" : "ECPR미진행");
	}
}
