package com.e3ps.change.eo.column;

import java.sql.Timestamp;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.eo.service.EoHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EoColumn {

	private int rowNum;
	private String oid;
	private String number;
	private String name;
	private String model;
	private String eoType;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;
	private String approveDate;

	public EoColumn() {

	}

	public EoColumn(Object[] obj) throws Exception {
		this((EChangeOrder) obj[0]);
	}

	public EoColumn(EChangeOrder eo) throws Exception {
		setOid(eo.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(eo.getEoNumber());
		setName(eo.getEoName());
		setModel(EoHelper.manager.displayToModel(eo.getModel()));
		setEoType(convert(eo.getEoType()));
		setState(eo.getLifeCycleState().getDisplay());
		setCreator(eo.getCreatorFullName());
		setCreatedDate(eo.getCreateTimestamp());
		setCreatedDate_txt(eo.getCreateTimestamp().toString().substring(0, 10));
		setApproveDate(eo.getEoApproveDate());
	}

	/*
	 * ( EO 구분 변경
	 */
	private String convert(String eoType) throws Exception {
		String rtn = "";
		if ("DEV".equals(eoType)) {
			rtn = "개발";
		} else if ("PRODUCT".equals(eoType)) {
			rtn = "양산";
		}
		return rtn;
	}
}
