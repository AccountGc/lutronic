package com.e3ps.part.beans;

import java.util.HashMap;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;

@Getter
@Setter
public class PartViewData {

	private WTPart part;
	private String oid;
	private String name;
	private String number;
	private String location;
	private String version;
	private String state;
	private String creator;
	private String modifier;
	private String createdDate;
	private String modifiedDate;
	private HashMap<String, Object> attrs = new HashMap<>();

	public PartViewData(WTPart part) throws Exception {
		setPart(part);
		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(part.getName());
		setNumber(part.getNumber());
		setLocation(part.getLocation());
		setState(part.getLifeCycleState().getDisplay());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		setCreator(part.getCreatorFullName());
		setCreatedDate(part.getCreateTimestamp().toString().substring(0, 10));
		setModifier(part.getModifierFullName());
		setModifiedDate(part.getModifyTimestamp().toString().substring(0, 10));
	}
}
