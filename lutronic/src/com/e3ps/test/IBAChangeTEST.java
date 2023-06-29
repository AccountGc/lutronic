package com.e3ps.test;


import wt.epm.EPMDocument;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartService;

public class IBAChangeTEST {
	
	public static void main(String[] args) {
		
		String number ="3800608500";
		try{
			WTPart part=PartHelper.service.getPart(number);
			//String ver =""
			
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_REV, part.getVersionIdentifier().getValue() , "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_DES, part.getName() , "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_CHANGENO, "" , "string");
			IBAUtil.changeIBAValue(part,  AttributeKey.IBAKey.IBA_CHANGEDATE, "" , "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_ECONO, "" , "string");
			IBAUtil.changeIBAValue(part,  AttributeKey.IBAKey.IBA_ECODATE, "" , "string");
			IBAUtil.changeIBAValue(part, AttributeKey.IBAKey.IBA_APR, "" , "string");
			IBAUtil.changeIBAValue(part,  AttributeKey.IBAKey.IBA_CHK, "" , "string");
			
			EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
			if(epm != null){
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_REV, part.getVersionIdentifier().getValue() , "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_DES, part.getName() , "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_CHANGENO, "" , "string");
				IBAUtil.changeIBAValue(epm,  AttributeKey.IBAKey.IBA_CHANGEDATE, "" , "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_ECONO, "" , "string");
				IBAUtil.changeIBAValue(epm,  AttributeKey.IBAKey.IBA_ECODATE, "" , "string");
				IBAUtil.changeIBAValue(epm, AttributeKey.IBAKey.IBA_APR, "" , "string");
				IBAUtil.changeIBAValue(epm,  AttributeKey.IBAKey.IBA_CHK, "" , "string");
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
