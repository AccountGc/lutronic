package com.e3ps.erp.util;

import java.util.HashMap;
import java.util.Vector;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.beans.BomData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.util.PartUtil;

import wt.enterprise.RevisionControlled;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.vc.VersionControlHelper;

public class ERPUtil {

	
	public static String erpSendType(WTPart part){
		String sendType ="C";
		String version = part.getVersionIdentifier().getValue();
		try{
			if("0".equals(version)){
				return checkPart(part);
			}else{
				return "R";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//windchill com.e3ps.erp.util.ERPUtil F00074 
		return sendType;
	}
	
	public static String checkPart(WTPart part){
		String sendType ="C";
		try{
			QueryResult qr = VersionControlHelper.service.allIterationsOf(part.getMaster());
			String state = "APPROVED";
			boolean isNew = true;
			long sOid =  CommonUtil.getOIDLongValue(part);
			while ( qr.hasMoreElements() ) {
				WTPart obj = (WTPart)qr.nextElement();
				long objOid = CommonUtil.getOIDLongValue(obj);
				if(sOid == objOid){
					continue;
				}
				if(state.equals(obj.getLifeCycleState().toString())){
					isNew = false;
					break;
				}
			}
			
			if(!isNew){
				sendType="R";
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sendType;
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			String number =args[0];
			WTPart part = PartHelper.service.getPart(number);
			String sendType = erpSendType(part);
			
			//System.out.println(part.getNumber() +","+part.getVersionIdentifier().getValue()+","+sendType);
		}catch(Exception e){
			e.printStackTrace();
			
			
		}
		
	}

	/**
	 * 루트노릭
	 */
	public static String valueErpType(String IBAKey,HashMap<String, String> mapCode,HashMap<String, Object> attributeMap){
		
		String value =  StringUtil.checkNull((String)attributeMap.get(IBAKey));
		if(value.length()>0){
			value = value + ":" + mapCode.get(value);
		}
		
		return value;
	}
	
	public static String getECOSEQ(String AENNR,int idx){
		
		String idxcount = String.valueOf(idx);
		if(idxcount.length()==1){
			AENNR = AENNR +"000"+idxcount;
		}else if(idxcount.length()==2){
			AENNR = AENNR +"00"+idxcount;
		}else if(idxcount.length()==3){
			AENNR = AENNR +"0"+idxcount;
		}else{
			AENNR = AENNR +idxcount;
		}
		
		return AENNR;
		
	}
	
	public static Vector<BomData> checkBOM(Vector<BomData> vecBOM){
		
		Vector<String> vecCheck = new Vector();
		//for(int i = 0 ; i <vecBOM.size() ;i++ ){
		for(int i = vecBOM.size() - 1; i >= 0; i--){
			
			BomData  data = vecBOM.get(i);
			String bomKey=data.parent+":"+data.child;
			
			if(PartUtil.isChange(data.parent) || PartUtil.isChange(data.child)){
				vecBOM.remove(i);
			}else{
				if(vecCheck.contains(bomKey)){
					vecBOM.remove(i);
				}else{
					vecCheck.add(bomKey);
				}
			}
			
			
		}
		
		return vecBOM;
	}
	
	
}
