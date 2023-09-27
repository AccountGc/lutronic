package com.e3ps.doc.column;

import java.sql.Timestamp;

import com.e3ps.common.code.service.NumberCodeHelper;
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
	private String modifier;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String primary;
	private String secondary;

	public DocumentColumn() {

	}

	/**
	 * 문서검색에 사용될 클래스 - 리스트에 필요한 값만 세팅 속도 개선
	 */
	public DocumentColumn(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(doc.getNumber());
		setInteralnumber(IBAUtil.getStringValue(doc, "INTERALNUMBER"));
		setModel(keyToValue(IBAUtil.getStringValue(doc, "MODEL"), "MODEL"));
		setName(doc.getName());
		setLocation(doc.getLocation());
		setVersion(VersionControlHelper.getVersionDisplayIdentifier(doc) + "."
				+ doc.getIterationIdentifier().getSeries().getValue());
		setState(doc.getLifeCycleState().getDisplay());
		setWriter(IBAUtil.getStringValue(doc, "DSGN"));
		setCreator(doc.getCreatorName());
		setCreatedDate(doc.getCreateTimestamp());
		setModifier(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp());
		setPrimary(AUIGridUtil.primary(doc));
		setSecondary(AUIGridUtil.secondary(doc));
	}

	/**
	 * IBA 코드값 디스플레이값으로 변경
	 */
	private String keyToValue(String code, String codeType) throws Exception {
		return NumberCodeHelper.manager.getNumberCodeName(code, codeType);
	}
}
