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
	private String number;
	private String name;
	private String description;
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
	
	
	private HashMap<String, String> attr = new HashMap<String, String>();

	public DocumentDTO() {

	}

	public DocumentDTO(String oid) throws Exception {
		this((WTDocument) CommonUtil.getObject(oid));
	}

	public DocumentDTO(WTDocument doc) throws Exception {
		setOid(doc.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(doc.getName());
		setNumber(doc.getNumber());
<<<<<<< HEAD
		setDescription(
				doc.getDescription() == null ? doc.getTypeInfoWTDocument().getPtc_rht_1() : doc.getDescription());
=======
		setDescription(doc.getTypeInfoWTDocument().getPtc_str_1());
>>>>>>> b01afe442dc9530130ff043d4cf1f74c57f6e6d1
		setLocation(doc.getLocation());
		setDocumentType(doc.getDocType().getDisplay());
		setLatest(CommonUtil.isLatestVersion(doc));
		setState(doc.getLifeCycleState().getDisplay());
		setVersion(doc.getVersionIdentifier().getSeries().getValue());
		setIteration(doc.getIterationIdentifier().getSeries().getValue());
		setCreator(doc.getCreatorFullName());
		setCreatedDate(doc.getCreateTimestamp().toString().substring(0, 10));
		setModifiedDate(doc.getModifierFullName());
		setModifiedDate(doc.getModifyTimestamp().toString().substring(0, 10));
	}
	
	public void setIBAAttribute(WTDocument doc) throws Exception {
		
	}
}