package com.e3ps.sap.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import wt.part.WTPart;
import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;

@Getter
@Setter
@ToString
public class SAPBomDTO {

	private String newParentPartNumber;
	private String newChildPartNumber;
	private double qty = 0D;
	private String unit;

	public SAPBomDTO(WTPartUsageLink link) throws Exception {
		WTPart parent = link.getUsedBy();
		WTPartMaster child = link.getUses();
		setNewParentPartNumber(parent.getNumber());
		setNewChildPartNumber(child.getNumber());
		setQty(link.getQuantity().getAmount());
		setUnit(link.getQuantity().getUnit().toString().toUpperCase());
	}
}
