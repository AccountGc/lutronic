package com.e3ps.doc.dto;

import java.util.Vector;

import com.e3ps.change.service.ChangeUtil;
import com.e3ps.common.beans.VersionData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;

import lombok.Getter;
import lombok.Setter;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.enterprise.BasicTemplateProcessor;
import wt.fc.QueryResult;
import wt.session.SessionHelper;

@Getter
@Setter
public class DocumentDTO {

	private String oid;
	private String number;
	private String name;
	private String description;
	private String location;
	private String documentType;
	private boolean latest;
	private String creator;
	private String createdDate;
	private String modifier;
	private String modifiedDate;

	public DocumentDTO() {

	}

	public DocumentDTO(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(doc.getName());
		setNumber(doc.getNumber());
		setDescription(doc.getTypeInfoWTDocument().getPtc_rht_1());
		setLocation(doc.getLocation());
		setDocumentType(doc.getDocType().getDisplay());
		setLatest(CommonUtil.isLatestVersion(doc));
		setCreator(doc.getCreatorFullName());
		setCreatedDate(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp().toString().substring(0, 10));
	}
}