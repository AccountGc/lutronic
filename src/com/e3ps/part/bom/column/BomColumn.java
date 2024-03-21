package com.e3ps.part.bom.column;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.StringUtil;
import com.ptc.windchill.option.datautilities.OptionSetManageLocalRuleChangesDataUtility;

import lombok.Getter;
import lombok.Setter;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;

@Getter
@Setter
public class BomColumn {

	private int level;
	private WTPart part;
	private String name;
	private String number;
	private String dwgNo;
	private String rev;
	private String oemInfo;
	private String state;
	private String modifier;
	private String spec;
	private double qty = 1D;
	private String ecoNo;
	private String projectCode;
	private String dept;
	private String manufacture;
	private String method;

	public BomColumn() {

	}

	public BomColumn(WTPart part) throws Exception {
		this(part, null, 1);
	}

	public BomColumn(WTPart part, WTPartUsageLink link, int level) throws Exception {
		setLevel(level);
		setPart(part);
		setName(part.getName());
		setNumber(part.getNumber());
		setRev(part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue());
		setState(part.getLifeCycleState().getDisplay());
		setModifier(part.getModifierFullName());
		setSpec(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_SPECIFICATION));
		setManufacture(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_MANUFACTURE));
		setOemInfo(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_REMARKS));
		setDept(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_DEPTCODE));
		setMethod(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_PRODUCTMETHOD));
		setProjectCode(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_MODEL));
		setEcoNo(IBAUtil.getAttrValue(part, AttributeKey.IBAKey.IBA_CHANGENO));

		if (link != null) {
			setQty(link.getQuantity().getAmount());
		}
	}
}
