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
public class SAPReverseBomDTO {

	private String parentPartNumber; // old
	private String childPartNumber;
	private String newParentPartNumber; // new
	private String newChildPartNumber;
	private String newParentPartVersion;
	private String newChildPartVersion;
	private double qty = 0D;
	private String unit;

	public SAPReverseBomDTO(WTPart child, WTPart parent, WTPartUsageLink link, EChangeOrder eco) throws Exception {
//		WTPart parent = link.getUsedBy();
		// 이전 부모
		WTPart pre_parent = SAPHelper.manager.getPre(parent, eco);
//		WTPartMaster child = link.getUses();
		// 이전 자식
		WTPart pre_child = SAPHelper.manager.getPre(child, eco);

		setNewParentPartNumber(parent.getNumber());
		setNewChildPartNumber(child.getNumber());
		if (pre_parent != null) {
			setParentPartNumber(pre_parent.getNumber());
		}
		if (pre_child != null) {
			setChildPartNumber(pre_child.getNumber());
		}
		setQty(link.getQuantity().getAmount());
		setUnit(link.getQuantity().getUnit().toString().toUpperCase());
	}

}
