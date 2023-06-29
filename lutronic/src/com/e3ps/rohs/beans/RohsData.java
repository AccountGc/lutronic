package com.e3ps.rohs.beans;

import java.rmi.RemoteException;

import wt.doc.WTDocument;
import wt.util.WTException;

import com.e3ps.common.beans.VersionData;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.web.WebUtil;
import com.e3ps.rohs.ROHSMaterial;

public class RohsData extends VersionData{
	
	public ROHSMaterial rohs;
	public String number;
	public String icon;
	
	
	public RohsData(ROHSMaterial rohs) throws Exception {
		super(rohs);
		this.rohs = rohs;
		this.number = rohs.getNumber();
		
	}
	public String getDescription(boolean isView) {
		String description = StringUtil.checkNull(this.rohs.getDescription());
		if(isView) {
			description = WebUtil.getHtml(description);
		}
		return description;
	}
	
	public String getRohsType() {
		String type = this.rohs.getDocType().getDisplay(Message.getLocale());
		return type;
	}
	
	public String getManufactureDisplay(boolean isDisplay) throws Exception{
		String manufa =IBAUtil.getAttrValue(this.rohs, IBAKey.IBA_MANUFACTURE);
		if(isDisplay) {
			NumberCode code =NumberCodeHelper.service.getNumberCode("MANUFACTURE", manufa);
			String manufaName="";
			if(code !=null){
				manufaName =code.getName();
			}
			return manufaName;
		}else {
			return manufa;
		}
	}

	public ROHSMaterial getRohs() {
		return rohs;
	}

	public void setRohs(ROHSMaterial rohs) {
		this.rohs = rohs;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
}
