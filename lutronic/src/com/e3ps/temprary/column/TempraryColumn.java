package com.e3ps.temprary.column;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.lifecycle.LifeCycleManaged;

@Getter
@Setter
public class TempraryColumn {

	private String oid;
	private String name;
	private String number;
	private String dataType;
	private String state;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createdDate;
	private String createdDate_txt;

	public TempraryColumn() {

	}

	public TempraryColumn(Object[] obj) throws Exception {
		this((LifeCycleManaged) obj[0]);
	}

	public TempraryColumn(LifeCycleManaged lcm) throws Exception {
		setOid(lcm.getPersistInfo().getObjectIdentifier().getStringValue());
		setInfo(lcm);
		setState(lcm.getLifeCycleState().getDisplay());
	}

	/**
	 * 기본객체 설정값
	 */
	private void setInfo(LifeCycleManaged lcm) throws Exception {
		if (lcm instanceof WTDocument) {
			WTDocument doc = (WTDocument) lcm;
			setName(doc.getName());
			setNumber(doc.getNumber());
			setDataType("문서");
			setCreator(doc.getCreatorFullName());
			setCreatedDate(doc.getCreateTimestamp());
			setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
		}
	}
}
