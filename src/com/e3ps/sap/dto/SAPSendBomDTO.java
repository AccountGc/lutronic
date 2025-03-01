package com.e3ps.sap.dto;

import com.e3ps.change.EChangeOrder;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.part.PartToPartLink;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.service.SAPHelper;

import lombok.Getter;
import lombok.Setter;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;

@Getter
@Setter
public class SAPSendBomDTO {

	private String parentPartNumber; // old
	private String childPartNumber;
	private String newParentPartNumber; // new
	private String newChildPartNumber;
	private String newParentPartVersion;
	private String newChildPartVersion;
	private String parentPartVersion;
	private String childPartVersion;
	private int qty = 0;
	private String unit;
	private String sendType;
	private String key;

	public SAPSendBomDTO() {

	}

	public SAPSendBomDTO(WTPart child, WTPart parent, WTPartUsageLink link, EChangeOrder eco) throws Exception {
		WTPart pre_parent = SAPHelper.manager.getPre(parent, eco);
		WTPart pre_child = SAPHelper.manager.getPre(child, eco);

		setNewParentPartNumber(parent.getNumber());
		setNewParentPartVersion(parent.getVersionIdentifier().getSeries().getValue());
		setNewChildPartNumber(child.getNumber());
		setNewChildPartVersion(child.getVersionIdentifier().getSeries().getValue());
		if (pre_parent != null) {
			setParentPartNumber(pre_parent.getNumber());
			setParentPartVersion(pre_parent.getVersionIdentifier().getSeries().getValue());
		} else {
			setParentPartNumber(parent.getNumber());
			setParentPartVersion(parent.getVersionIdentifier().getSeries().getValue());
		}
		if (pre_child != null) {
			setChildPartNumber(pre_child.getNumber());
			setChildPartVersion(pre_child.getVersionIdentifier().getSeries().getValue());
		} else {
			setChildPartNumber(child.getNumber());
			setChildPartVersion(child.getVersionIdentifier().getSeries().getValue());
		}
		setQty((int) link.getQuantity().getAmount());
		setUnit(link.getQuantity().getUnit().toString().toUpperCase());
	}

}
