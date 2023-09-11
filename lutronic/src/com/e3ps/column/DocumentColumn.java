package com.e3ps.column;

import java.sql.Timestamp;

import com.e3ps.common.util.AUIGridUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;

@Getter
@Setter
public class DocumentColumn {

	private String oid;
	private String number;
	private String interalnumber;
	private String model;
	private String name;
	private String location;
	private String version;
	private String state;
	private String writer;
	private String creator;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp createDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String primary;
	private String secondary;

	public DocumentColumn() {

	}

	public DocumentColumn(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(doc.getNumber());
		setName(doc.getName());
		setCreateDate(doc.getCreateTimestamp());
		setModifiedDate(doc.getModifyTimestamp());

		setPrimary(AUIGridUtil.primary(doc));
	}
}
