package com.e3ps.change.eo.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeOrder;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EoColumn {

	private String oid;
	private String number;
	private String name;
	private String eoType;
	private String state;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String approveDate_txt;

	public EoColumn() {

	}

	public EoColumn(Object[] obj) throws Exception {
		this((EChangeOrder) obj[0]);
	}

	public EoColumn(EChangeOrder eo) throws Exception {
		setOid(eo.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(eo.getEoNumber());
		setName(eo.getName());
		setEoType(eo.getEoType());
		setState(eo.getLifeCycleState().getDisplay());
		setCreatedDate(eo.getCreateTimestamp());
		setCreatedDate_txt(eo.getCreateTimestamp().toString().substring(0, 10));
		setApproveDate_txt(eo.getEoApproveDate());
	}
}
