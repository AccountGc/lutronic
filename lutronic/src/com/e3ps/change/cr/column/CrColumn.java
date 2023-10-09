package com.e3ps.change.cr.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeRequest;
import com.e3ps.change.cr.service.CrHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrColumn {

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

	public CrColumn() {

	}

	public CrColumn(Object[] obj) throws Exception {
		this((EChangeRequest) obj[0]);
	}

	public CrColumn(EChangeRequest cr) throws Exception {
		setOid(cr.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(cr.getEoName());
		setNumber(cr.getEoNumber());
		setChangeSection(CrHelper.manager.displayToSection(cr.getChangeSection()));
		setModel(CrHelper.manager.displayToModel(cr.getModel()));
		setCreateDepart(CrHelper.manager.displayToDept(cr.getCreateDepart()));
		setWriter(cr.getWriter());
		setApproveDate(cr.getApproveDate());
		setState(cr.getLifeCycleState().getDisplay());
		setCreator(cr.getCreatorFullName());
		setCreatedDate(cr.getCreateTimestamp());
		setCreatedDate_txt(cr.getCreateTimestamp().toString().substring(0, 10));
		setCreator(cr.getCreatorFullName());
	}
}
