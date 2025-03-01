package com.e3ps.doc.column;

import java.sql.Timestamp;

import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocumentClass;
import com.e3ps.doc.DocumentClassType;
import com.e3ps.doc.service.DocumentHelper;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;
import wt.doc.WTDocumentTypeInfo;
import wt.vc.VersionControlHelper;

@Getter
@Setter
public class DocumentColumn {

	private String oid;
	private int rowNum;
//	private boolean latest;
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
	private String createdDate_txt;
	private String modifier;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private String primary;
	private String secondary;

	private String classType1_name;
	private String classType2_name;
	private String classType3_name;

	public DocumentColumn() {

	}

	public DocumentColumn(String oid) throws Exception {
		this((WTDocument) CommonUtil.getObject(oid));
	}

	public DocumentColumn(Object[] obj) throws Exception {
		this((WTDocument) obj[0]);
	}

	/**
	 * 문서검색에 사용될 클래스 - 리스트에 필요한 값만 세팅 속도 개선
	 */
	public DocumentColumn(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(doc.getNumber());
		setModel(keyToValue(IBAUtils.getStringValue(doc, "MODEL"), "MODEL"));
		setName(doc.getName());
		setLocation(doc.getLocation());
		setVersion(doc.getVersionIdentifier().getSeries().getValue() + "." + doc.getIterationIdentifier().getSeries().getValue());
		setState(doc.getLifeCycleState().getDisplay());
//		setWriter(IBAUtils.getStringValue(doc, "DSGN"));
		setCreator(doc.getCreatorFullName());
		setCreatedDate(doc.getCreateTimestamp());
		setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifier(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp());
		setModifiedDate_txt(doc.getModifyTimestamp().toString().substring(0, 10));
	}

	/**
	 * 최신버건과 함께 표시
	 */
//	private String setVersionInfo(WTDocument doc) throws Exception {
//		WTDocument latest = DocumentHelper.manager.latest(getOid());
//		String version = VersionControlHelper.getVersionDisplayIdentifier(doc) + "."
//				+ doc.getIterationIdentifier().getSeries().getValue();
//		String latest_version = latest.getVersionIdentifier().getSeries().getValue() + "."
//				+ latest.getIterationIdentifier().getSeries().getValue();
//		if (isLatest()) {
//			return version;
//		} else {
//			return version + " <b><font color='red'>(" + latest_version + ")</font></b>";
//		}
//	}

	/**
	 * IBA 코드값 디스플레이값으로 변경
	 */
	private String keyToValue(String code, String codeType) throws Exception {
		NumberCode n = NumberCodeHelper.manager.getNumberCode(code, codeType);
		if (n != null) {
			return n.getCode() + " [<font color='red'><b>" + n.getName() + "</b></font>]";
		}
		return "";
	}
}
