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

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RohsData extends VersionData{
	
	public ROHSMaterial rohs;
	public String number;
	
	public RohsData(ROHSMaterial rohs) throws Exception {
		super(rohs);
		setRohs(rohs);
		setNumber(rohs.getNumber());
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
}
