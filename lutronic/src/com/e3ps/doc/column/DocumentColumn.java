package com.e3ps.doc.column;

import java.sql.Timestamp;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.AUIGridUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.vc.VersionControlHelper;

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
	private Timestamp createdDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String primary;
	private String secondary;

	public DocumentColumn() {

	}

	public DocumentColumn(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(doc.getNumber());
		setInteralnumber(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_INTERALNUMBER));
		setModel(IBAUtil.getAttrValue(doc, AttributeKey.IBAKey.IBA_MODEL));
		setName(doc.getName());
		setLocation(doc.getLocation());
		setVersion(VersionControlHelper.getVersionDisplayIdentifier(doc) + "."
				+ doc.getIterationIdentifier().getSeries().getValue());
		setState(doc.getLifeCycleState().getDisplay());
		setWriter("");
		setCreator(doc.getCreatorName());
		setCreatedDate(doc.getCreateTimestamp());
		setModifiedDate(doc.getModifyTimestamp());
		setPrimary(AUIGridUtil.primary(doc));
		setSecondary(AUIGridUtil.secondary(doc));
	}
}
