package com.e3ps.doc.dto;

import java.util.HashMap;

import com.e3ps.common.util.CommonUtil;

import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;

@Getter
@Setter
public class DocumentDTO {

	private String oid;
	private WTDocument doc;
	private String number;
	private String name;
	private String description;
	private String content;
	private String location;
	private String documentType;
	private boolean latest;
	private String state;
	private String version;
	private String iteration;
	private String creator;
	private String createdDate;
	private String modifier;
	private String modifiedDate;

	// IBA
	private String writer;
	private String model;
	private String preseration;
	private String interalnumber;
	private String dept;

	// auth
	private boolean _delete = false;
	private boolean _modify = false;
	private boolean _revise = false;

	private HashMap<String, String> attr = new HashMap<String, String>();

	public DocumentDTO() {

	}

	public DocumentDTO(String oid) throws Exception {
		this((WTDocument) CommonUtil.getObject(oid));
	}

	public DocumentDTO(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setDoc(doc);
		setName(doc.getName());
		setNumber(doc.getNumber());
		setDescription(doc.getDescription());
		setContent(doc.getTypeInfoWTDocument().getPtc_rht_1());
		setLocation(doc.getLocation());
		setDocumentType(doc.getDocType().getDisplay());
		setLatest(CommonUtil.isLatestVersion(doc));
		setState(doc.getLifeCycleState().getDisplay());
		setVersion(doc.getVersionIdentifier().getSeries().getValue());
		setIteration(doc.getIterationIdentifier().getSeries().getValue());
		setCreator(doc.getCreatorFullName());
		setCreatedDate(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifier(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp().toString().substring(0, 10));
	}

	private void setIBAAttribute(WTDocument doc) throws Exception {
		// IBAUtil .. 수정해야
	}

	private void setAuth(WTDocument doc) throws Exception {
		// 개정 권한 - (최신버전 && 승인됨)
		if (check("APPROVED") && isLatest()) {
			set_revise(true);
		}
		// 삭제, 수정 권한 - (최신버전 && (작업중 || 일괄결재중 || 재작업)) 
		if(isLatest() && (check("INWORK") || check("BATCHAPPROVAL") || check("REWORK")) {
			set_delete(true);
			set_modify(true);
		}
	}

	/**
	 * 상태값 여부 체크
	 */
	private boolean check(String state) throws Exception {
		boolean check = false;
		String compare = getDoc().getLifeCycleState().toString();
		if (compare.equals(state)) {
			check = true;
		}
		return check;
	}

}