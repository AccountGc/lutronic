package com.e3ps.test;

import java.util.List;
import java.util.Vector;

import wt.fc.ReferenceFactory;
import wt.part.WTPart;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.views.ViewHelper;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.jdf.config.ConfigEx;
import com.e3ps.common.jdf.config.ConfigExImpl;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.erp.beans.BomData;
import com.e3ps.erp.service.ERPHelper;
import com.e3ps.erp.service.ERPSearchHelper;
import com.e3ps.erp.util.ERPUtil;

public class ERPBOMTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		ReferenceFactory rf = new ReferenceFactory();
//		String oid ="com.e3ps.change.EChangeOrder:174808110";
//		
//		EChangeOrder eco;
			
		ConfigExImpl conf = ConfigEx.getInstance("sap");
		String host = conf.getString("jco.client.ashost");
		
		//System.out.println(host);
		
		//eco에 ecopartlink로 연결된 부품 리스트 만들기.
		
		
		
		//BOM 리스트
			
//			try {
//				eco = (EChangeOrder) rf.getReference(oid).getObject();
//				List<WTPart> changePartLis = ECOSearchHelper.service.ecoPartReviseList(eco);
//				
//				System.out.println("yhkim1");
//				Vector<BomData> vecBom = new Vector<BomData>();
//				String ecoNumber = eco.getEoNumber(); 	
//				
//				for(WTPart part : changePartLis){
//					vecBom = ERPHelper.service.compareBom(part,vecBom);
//				}
//				
//				
//				
//				System.out.println("yhkim2");
//				//더미 부품 제거
//				ERPUtil.checkBOM(vecBom);
//				System.out.println("yhkim3");
//				//부품 정보 send
//				ERPHelper.service.sendERPToPart(changePartLis, ecoNumber);
//				System.out.println("yhkim4");
//				//EO 정보 send
//				ERPHelper.service.sendERPToECO(eco,vecBom);
//				System.out.println("yhkim5");
//				//BOM 정보 send
//				ERPHelper.service.sendERPToBOM(vecBom, ecoNumber);
//				System.out.println("yhkim");
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}	
			//tempBom = getBom(part, null, getView(), tempBom,false);
	
		
//		ERPHelper.service.sendERPToPart(sendPartList, ecoNumber);
//		try{
//			String oid ="wt.part.WTPart:8753049";
//			WTPart part = (WTPart)CommonUtil.getObject(oid);
//			Vector<BomData> vecBom = new Vector<BomData>();
//			ERPHelper.service.getBom(part, null, ViewHelper.service.getView("Design"), vecBom, true);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		
	}

}
