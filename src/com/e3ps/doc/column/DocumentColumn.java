package com.e3ps.doc.column;

import java.sql.Timestamp;

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
//	private boolean latest = false;
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

	public DocumentColumn(Object[] obj) throws Exception {
		this((WTDocument) obj[0]);
	}

	/**
	 * 문서검색에 사용될 클래스 - 리스트에 필요한 값만 세팅 속도 개선
	 */
	public DocumentColumn(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
//		setLatest(CommonUtil.isLatestVersion(doc));
		setNumber(doc.getNumber());
		setInteralnumber(IBAUtils.getStringValue(doc, "INTERALNUMBER"));
		setModel(keyToValue(IBAUtils.getStringValue(doc, "MODEL"), "MODEL"));
		setName(doc.getName());
		setLocation(doc.getLocation());
		setVersion(setVersionInfo(doc));
		setState(doc.getLifeCycleState().getDisplay());
		setWriter(IBAUtils.getStringValue(doc, "DSGN"));
		setCreator(doc.getCreatorName());
		setCreatedDate(doc.getCreateTimestamp());
		setCreatedDate_txt(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifier(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp());
		setModifiedDate_txt(doc.getModifyTimestamp().toString().substring(0, 10));
//		setPrimary(AUIGridUtil.primary(doc));
//		setSecondary(AUIGridUtil.secondary(doc));

		WTDocumentTypeInfo info = doc.getTypeInfoWTDocument();
		if (info != null) {
			if (StringUtil.checkString(info.getPtc_str_2())) {
				DocumentClassType classType1 = DocumentClassType.toDocumentClassType(info.getPtc_str_2());
				setClassType1_name(classType1.getDisplay());
			}

			if (info.getPtc_ref_2() != null) {
				DocumentClass classType2 = (DocumentClass) info.getPtc_ref_2().getObject();
				if (classType2 != null) {
					setClassType2_name(classType2.getName());
				}
			}

			if (info.getPtc_ref_3() != null) {
				DocumentClass classType3 = (DocumentClass) info.getPtc_ref_3().getObject();
				if (classType3 != null) {
					setClassType3_name(classType3.getName());
				}
			}
		}
	}

	/**
	 * 최신버건과 함께 표시
	 */
	private String setVersionInfo(WTDocument doc) throws Exception {
//		WTDocument latest = DocumentHelper.manager.latest(getOid());
		String version = VersionControlHelper.getVersionDisplayIdentifier(doc) + "."
				+ doc.getIterationIdentifier().getSeries().getValue();
//		String latest_version = latest.getVersionIdentifier().getSeries().getValue() + "."
//				+ latest.getIterationIdentifier().getSeries().getValue();
//		if (isLatest()) {
			return version;
//		} else {
//			return version + " <b><font color='red'>(" + latest_version + ")</font></b>";
//		}
	}

	/**
	 * IBA 코드값 디스플레이값으로 변경
	 */
	private String keyToValue(String code, String codeType) throws Exception {
		return NumberCodeHelper.manager.getNumberCodeName(code, codeType);
	}
}
