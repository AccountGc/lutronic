package com.e3ps.part.beans;

import com.e3ps.common.iba.AttributeKey;

import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;

@Getter
@Setter
public class PartDTO {

	private String oid;
	private String number;
	private String name;
	private String location;
	private String version;
	private String remarks;
	private String state;
	private String creator;
	private String createDate;
	private String modifyDate;
	private String ecoNo;

	public PartDTO(WTPart part) throws Exception {
		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(part.getNumber());
		setName(part.getName());
		setLocation(part.getLocation());
		setVersion(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
//		setRemark((String) map.get(AttributeKey.IBAKey.IBA_REMARKS));//
		setState(part.getLifeCycleState().getDisplay());
		setCreator(part.getCreatorFullName());
		setCreateDate(part.getCreateTimestamp().toString().substring(0, 10));
		setModifyDate(part.getModifyTimestamp().toString().substring(0, 10));
	}
}
