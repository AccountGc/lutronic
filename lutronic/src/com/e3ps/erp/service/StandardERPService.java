package com.e3ps.erp.service;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.util.WTException;
import wt.util.WTRuntimeException;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.service.ECOHelper;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.db.DBConnectionManager;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.iba.IBAAttributes;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.obj.ObjectUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.erp.BOMERP;
import com.e3ps.erp.ECOERP;
import com.e3ps.erp.PARTERP;
import com.e3ps.erp.beans.BomData;
import com.e3ps.erp.key.ERPKey;
import com.e3ps.erp.util.ERPUtil;
import com.e3ps.erp.util.SAPConnection;
import com.e3ps.part.beans.ObjectComarator;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.util.PartUtil;
import com.ptc.windchill.enterprise.requirement.integrity.IntegrityAttributesHelper.IBA_ATTRIBUTES;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import wt.services.StandardManager;

@SuppressWarnings("serial")
public class StandardERPService extends StandardManager implements ERPService{
	
	private  boolean isExcute = true;
	
	public static StandardERPService newStandardERPService() throws Exception {
		final StandardERPService instance = new StandardERPService();
		instance.initialize();
		return instance;
	}
	
	private View getView() throws WTException {
		return ViewHelper.service.getView("Design");
	}
	
	@Override
	public void sendERP(EChangeOrder eco) throws Exception{
		//System.out.println("====== START sendERP SAPConnection.isERPSend =" + SAPConnection.isERPSend);
		if(SAPConnection.isERPSend){
			//System.out.println("====== START sendERP =" + eco.getEoNumber());
			try{
				
				String eoType = eco.getEoType(); //설계변경, 양산 EO 인경우애만 ERP 전송
				
				if(eoType.equals(ECOKey.ECO_CHANGE)){
					sendECOERP(eco);
				}else if(eoType.equals(ECOKey.ECO_PRODUCT)){
					sendEOERP(eco);
				}else{
					return;
				}
				//Vector<WTPart> vecPart
				
			}catch(Exception e){
				
				e.printStackTrace();
				throw new Exception(e.getMessage());
			}
			//System.out.println("====== END sendERP =" + eco.getEoNumber());
		}
		
	}
	
	@Override
	public void sendEOERP(EChangeOrder eco) throws Exception{
		//완제품의 하위 품목 전송,EO 전송, BOM 전송
		List<WTPart> completePartLis = ECOSearchHelper.service.getCompletePartList(eco);
		List<WTPart> sendPartList = new ArrayList<WTPart>();
		Vector<BomData> vecBom = new Vector<BomData>();
		String ecoNumber = eco.getEoNumber(); 	
		
		//완제품 부품 리스트 
		for(WTPart part : completePartLis){
			if(!sendPartList.contains(part)){
				sendPartList.add(part);
			}
			sendPartList = getBOMToPart(part, sendPartList);
		}
		
		
		//BOM 리스트
		for(WTPart part : completePartLis){
			vecBom = getBom(part, null, getView(), vecBom,true);
		}
		
		//더미 부품 제거
		vecBom = ERPUtil.checkBOM(vecBom);
		
		//부품 정보 send
		sendERPToPart(sendPartList, ecoNumber);
		
		//EO 정보 send
		sendERPToECO(eco,vecBom);
		
		//BOM 정보 send
		sendERPToBOM(vecBom, ecoNumber);
		
	}
	
	public static void main(String[] args) {
		String oid = "com.e3ps.change.EChangeOrder:174808110";
		ReferenceFactory rf = new ReferenceFactory();
		EChangeOrder eco;
		try {
			eco = (EChangeOrder) rf.getReference(oid).getObject();
			ERPHelper.service.sendECOERP(eco);
		} catch (WTRuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		sendECOERP(eco);
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void sendECOERP(EChangeOrder eco) throws Exception{
		//System.out.println("sendECOERP");
		//부품 리스트
		List<WTPart> changePartLis = ECOSearchHelper.service.ecoPartReviseList(eco);
		
		//eco에 ecopartlink로 연결된 부품 리스트 만들기.
		
		Vector<BomData> vecBom = new Vector<BomData>();
		String ecoNumber = eco.getEoNumber(); 	
		
		//BOM 리스트
		for(WTPart part : changePartLis){
			
			vecBom = compareBom(part,vecBom);
			//tempBom = getBom(part, null, getView(), tempBom,false);
		}
		//더미 부품 제거
		ERPUtil.checkBOM(vecBom);
		//부품 정보 send
		sendERPToPart(changePartLis, ecoNumber);
		//EO 정보 send
		sendERPToECO(eco,vecBom);
		//BOM 정보 send
		sendERPToBOM(vecBom, ecoNumber);
	}
	
	/**
	 * Bom에서 부품 전송 리스트 - EO 양산인 경우	
	 * @param part
	 * @param sendPartList
	 * @return
	 * @throws Exception
	 */
	public List<WTPart> getBOMToPart(WTPart part,List<WTPart> sendPartList) throws Exception {
		
		ArrayList list = this.descentLastPart(part, getView(),null);
		for (int i = 0; i < list.size(); i++) {
			
			Object[] o = (Object[]) list.get(i);
			
			WTPart cPart = (WTPart)o[1];
			
			if(!sendPartList.contains(cPart)){
				sendPartList.add(cPart);
				getBOMToPart(cPart, sendPartList);
			}
			
		}
		
		
		return sendPartList;
	}
	
	
	@Override
	public  Vector<BomData> getBom(WTPart part,WTPartUsageLink link,View view,Vector vecBom,boolean isALL) throws Exception{
		
		ArrayList list = this.descentLastPart(part, view,null);
	
		for (int i = 0; i < list.size(); i++) {
			ArrayList bomlist = new ArrayList();
			Object[] o = (Object[]) list.get(i);
			WTPartUsageLink linko =(WTPartUsageLink) o[0];
			WTPart pPart = (WTPart)linko.getRoleAObject();
			WTPart cPart = (WTPart)o[1];
			String state = cPart.getLifeCycleState().toString();
			
			if(!cPart.getLifeCycleState().toString().equals("APPROVED")){
				//System.out.println("1. cPart =" + cPart.getNumber() +","+cPart.getVersionIdentifier().toString());
            	WTPart tempPart = cPart;
            	cPart = (WTPart) ObjectUtil.getPreviousVersion(tempPart);
            	//System.out.println("2. cPart =" + cPart.getNumber() +","+cPart.getVersionIdentifier().toString());
            	if(cPart == null){
            		cPart = tempPart;
            	}
            	//System.out.println("3. cPart =" + cPart.getNumber() +","+cPart.getVersionIdentifier().toString());
            }
			
			
			if( (PartUtil.isChange(pPart.getNumber()) || PartUtil.isChange(cPart.getNumber()) )){
				
				continue;
			}
			
			BomData data = new BomData();
			/*
			bomlist.add(pPart);
			bomlist.add(cPart);
			bomlist.add(linko.getQuantity().getAmount());
			bomlist.add("ea");
			*/
			data.setParent(pPart.getNumber());
			data.setPver(pPart.getVersionIdentifier().getValue());
			data.setChild(cPart.getNumber());
			data.setCver(cPart.getVersionIdentifier().getValue());
			data.setAmount(linko.getQuantity().getAmount());
			data.setUnit(linko.getQuantity().getUnit().toString());
			data.setVer(cPart.getVersionIdentifier().getValue());
			data.setChangeType("A");
			vecBom.add(data);
			
			if(isALL){
				this.getBom(cPart, (WTPartUsageLink) o[0],view,vecBom,isALL);
			}
			
		}
		
		
		return vecBom;
	}
	
	@Override
	public  Vector<BomData> compareBom(WTPart part , Vector<BomData> bomList) throws Exception{
	
		String oid = CommonUtil.getOIDString(part);
		ArrayList<PartTreeData[]> result = ERPSearchHelper.service.getBaseLineCompare(oid);//getBaseLineCompare(oid);//ERPSearchHelper.service.getBaseLineCompare(oid);//getBaseLineCompare
		
		//
		
		for(int i=0; i< result.size(); i++){
		    PartTreeData[] o  = (PartTreeData[])result.get(i);
		   
		    PartTreeData data = o[0];
		    PartTreeData data2 = o[1];
		    BomData bomData = new BomData();
		    String partNumber = "";
		    String state = "E";	//기존 
		    bomData.setParent(part.getNumber());
		    bomData.setVer(part.getVersionIdentifier().getValue());
		    double amount = 0;
		    String unit = "";
		    String childPart = "";
		    int level = 0;
		    String ver = "";
		    if(data==null){
		    	state ="D";//"#D3D3D3";		//삭제
		    	amount = data2.quantity;
		    	unit = data2.unit;
		    	childPart = data2.number;
		    	level = data2.level;
		    	ver = data2.version;
		    }else {
		    	amount = data.quantity;
			    unit = data.unit;
			    childPart =  data.number;
			    level = data.level;
			    ver = data.version;
		    	if(data2==null){
		    		state ="A";//"#8FBC8F";		//추가
		    	}else{
		    		if(!data.compare(data2)){
		    			state = "C";		//변경
		    		}
		    	}
		    }
		    
		    if(childPart.equals(partNumber)){
		    	continue;
		    }
		    
		    if(state.equals("E")){
		    	continue;
		    }
		    
		    bomData.setChild(childPart);
		    bomData.setUnit(unit);
		    bomData.setAmount(amount);
		    bomData.setChangeType(state);
		    bomData.setVer(ver);
		    bomData.setCver(ver);;
		    bomList.add(bomData);
		    //System.out.println( bomData.parent+":"+bomData.child +":"+ bomData.changeType );
		}
			
		//Collections.sort(bomList, new ObjectComarator());
		//System.out.println("=========== compare END========== ");;
		
		
		return bomList;
		
	}
	
	@Override
	public ArrayList descentLastPart(WTPart part, wt.vc.views.View view, State state)
			throws WTException {
		ArrayList v = new ArrayList();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec
							.newWTPartStandardConfigSpec(view, state));
			QueryResult re = wt.part.WTPartHelper.service.getUsesWTParts(part,
					configSpec);
			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();

				if (!(oo[1] instanceof WTPart)) {
					continue;
				}
				v.add(oo);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new WTException();
		}
		return v;
	}
	
	
	@Override
	public void sendERPToPart(List<WTPart> sendPartList,String ecoNumber) throws Exception{
		
		// SAP Setting Start
		//System.out.println("yhkim7");
		JCoDestination destination =JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		//System.out.println("yhkim8");
		JCoFunction function = destination.getRepository().getFunction(SAPConnection.FUNTION_MATERIAL);
		if(function == null){
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}
		
		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		// SAP Setting END
		
		
		HashMap<String, String> mapWRKST = CodeHelper.service.getCodeMap(IBAKey.IBA_MAT);
		HashMap<String, String> mapZFINSH = CodeHelper.service.getCodeMap(IBAKey.IBA_FINISH);
		HashMap<String, String> mapZMODEL = CodeHelper.service.getCodeMap(IBAKey.IBA_MODEL);
		HashMap<String, String> mapZPRODM = CodeHelper.service.getCodeMap(IBAKey.IBA_PRODUCTMETHOD);
		HashMap<String, String> mapZDEPT = CodeHelper.service.getCodeMap(IBAKey.IBA_DEPTCODE);
		/*
		System.out.println("mapWRKST = " + mapWRKST.size());
		System.out.println("mapZFINSH = " + mapZFINSH.size());
		System.out.println("mapZMODEL = " + mapZMODEL.size());
		System.out.println("mapZPRODM = " + mapZPRODM.size());
		System.out.println("mapZDEPT = " + mapZDEPT.size());
		*/
		int i =1;
		for(WTPart part : sendPartList){
			
			if(PartUtil.isChange(part.getNumber())){
				continue;
			}
			
			HashMap attributeMap =IBAUtil.getAttributes(part);
			HashMap<String, String> numberMap = PartUtil.getPartNumberGoup(part.getNumber());
			String partType1 = StringUtil.checkNull(numberMap.get("PARTTYPE1"));
			String partType2 = StringUtil.checkNull(numberMap.get("PARTTYPE2"));
			String partType3 = StringUtil.checkNull(numberMap.get("PARTTYPE3"));
			String partGroup = partType1+partType2;
			
			String seqNumber = SequenceDao.manager.getERPSeq("PART");
			String ZIFNO = "PART-"+seqNumber;
			String weight = "1.5";
			try{
				weight = StringUtil.checkNull((String)attributeMap.get(AttributeKey.IBAKey.IBA_WEIGHT));
				if(weight.length()==0){
					weight = "0";
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			float floatWeight = Float.valueOf(weight);
			
			String WRKST = ERPUtil.valueErpType(IBAKey.IBA_MAT, mapWRKST, attributeMap);//StringUtil.checkNull((String)attributeMap.get(AttributeKey.IBAKey.IBA_MAT));
			String ZSPEC = StringUtil.checkNull((String)attributeMap.get(IBAKey.IBA_SPECIFICATION));
			String ZFINSH = ERPUtil.valueErpType(IBAKey.IBA_FINISH, mapZFINSH, attributeMap);//StringUtil.checkNull((String)attributeMap.get(AttributeKey.IBAKey.IBA_FINISH));
			String ZMODEL = ERPUtil.valueErpType(IBAKey.IBA_MODEL, mapZMODEL, attributeMap);//StringUtil.checkNull((String)attributeMap.get(AttributeKey.IBAKey.IBA_MODEL));
			String ZPRODM = ERPUtil.valueErpType(IBAKey.IBA_PRODUCTMETHOD, mapZPRODM, attributeMap);//StringUtil.checkNull((String)attributeMap.get(AttributeKey.IBAKey.IBA_PRODUCTMETHOD));
			String ZDEPT = ERPUtil.valueErpType(IBAKey.IBA_DEPTCODE, mapZDEPT, attributeMap);//StringUtil.checkNull((String)attributeMap.get(AttributeKey.IBAKey.IBA_DEPTCODE));
			String AENNR = ecoNumber;
			String ZEIVR = part.getVersionIdentifier().getValue();
			
			//Row Data Insert
			codes.insertRow(i);
			String partName = part.getName();
			codes.setValue("ZIFNO", ZIFNO);
			codes.setValue("MATNR", part.getNumber());
			codes.setValue("MAKTX", partName);
			codes.setValue("MEINS", part.getDefaultUnit().toString().toUpperCase());
			codes.setValue("MATKL", partGroup);
			codes.setValue("NTGEW", weight);
			codes.setValue("GEWEI", "g".toUpperCase());
			codes.setValue("WRKST",WRKST);
			codes.setValue("ZSPEC",ZSPEC);
			codes.setValue("ZFINSH",ZFINSH);
			codes.setValue("ZMODEL",ZMODEL);
			codes.setValue("ZPRODM",ZPRODM);
			codes.setValue("ZDEPT",ZDEPT);
			codes.setValue("ZMAT1",partType1);
			codes.setValue("ZMAT2",partType2);
			codes.setValue("ZMAT3",partType3);
			codes.setValue("AENNR",AENNR);
			codes.setValue("ZEIVR",ZEIVR);
			
			i++;
			
		}
		
		//System.out.println("============== START ERP PART SEND :"+ecoNumber +":"+codes.getNumRows()+"==============");
		
		codes.firstRow();
		for(int j = 1 ; j<=codes.getNumRows() ; j++,codes.nextRow()  ){
			String ZIFNO = (String)codes.getValue("ZIFNO");
			String MATNR = (String)codes.getValue("MATNR");
			String MAKTX = (String)codes.getValue("MAKTX");
			String MEINS = (String)codes.getValue("MEINS");
			String MATKL = (String)codes.getValue("MATKL");
			BigDecimal NTGEW = (BigDecimal)codes.getValue("NTGEW");
			String GEWEI = (String)codes.getValue("GEWEI");
			String WRKST = (String)codes.getValue("WRKST");
			String ZSPEC = (String)codes.getValue("ZSPEC");
			String ZFINSH = (String)codes.getValue("ZFINSH");
			String ZMODEL = (String)codes.getValue("ZMODEL");
			String ZPRODM = (String)codes.getValue("ZPRODM");
			String ZDEPT = (String)codes.getValue("ZDEPT");
			String ZMAT1 = (String)codes.getValue("ZMAT1");
			String ZMAT2 = (String)codes.getValue("ZMAT2");
			String ZMAT3 = (String)codes.getValue("ZMAT3");
			String AENNR = (String)codes.getValue("AENNR");
			String ZEIVR = (String)codes.getValue("ZEIVR");
			//System.out.println("========= START PART == " + MATNR +"=========");
			//System.out.println("ZIFNO == " + ZIFNO);
			//System.out.println("MATNR == " + MATNR);
			//System.out.println("MAKTX == " + MAKTX);
			//System.out.println("MEINS == " + MEINS);
			//System.out.println("MATKL == " + MATKL);
			//System.out.println("GEWEI == " + GEWEI);
			//System.out.println("WRKST==" + WRKST);
			//System.out.println("ZSPEC==" + ZSPEC);
			//System.out.println("ZFINSH==" + ZFINSH);
			//System.out.println("ZMODEL==" + ZMODEL);
			//System.out.println("ZPRODM==" + ZPRODM);
			//System.out.println("ZDEPT==" + ZDEPT);
			//System.out.println("ZMAT1==" + ZMAT1);
			//System.out.println("ZMAT2==" + ZMAT2);
			//System.out.println("ZMAT3==" + ZMAT3);
			//System.out.println("AENNR==" + AENNR);
			//System.out.println("ZEIVR==" + ZEIVR);
			
			
		}
		//System.out.println("============== END PART SEND :"+ecoNumber +"==============");
		
		//SAP Send
		//System.out.println("isExcute = " +isExcute);
		if(isExcute){
			function.execute(destination);
		}
		
		//SAP Return START
		//System.out.println("============== Start PART Return :"+ecoNumber +"==============");
		JCoTable returnCodes = function.getTableParameterList().getTable("ET_OUTPUT");
		returnCodes.firstRow();
		for(int j = 1 ; j<=returnCodes.getNumRows() ; j++,returnCodes.nextRow()  ){
			//System.out.println("ZIFNO = " +returnCodes.getValue("ZIFNO"));
			//System.out.println(".INCLUDE = " +codes.getValue(".INCLUDE"));
			//System.out.println("ZIFSTA = " +returnCodes.getValue("ZIFSTA"));
			//System.out.println("ZIFMSG = " +returnCodes.getValue("ZIFMSG"));
		}
		//System.out.println("============== End PART Return :"+ecoNumber +"==============");
		//SAP Return END
		
		//ERPHistory 
		ERPHistoryHelper.service.createHistoryPart(codes,returnCodes);
	}
	
	@Override
	public  void sendERPToECO(EChangeOrder eco,Vector<BomData> tempBOM) throws Exception{
		
		// SAP Setting Start
		JCoDestination destination =JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		
		JCoFunction function = destination.getRepository().getFunction(SAPConnection.FUNTION_ECO);
		if(function == null){
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}
		
		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		// SAP Setting END
		//Row Data Insert
		
		String ZIFNO = "";
		String AENNR = eco.getEoNumber();
		String AETXT = eco.getEoName();
		String DATUV ="";
		String AEGRU = eco.getEoCommentA();
		String ZECMID = eco.getEoType().equals(ECOKey.ECO_CHANGE)?"ECO":"EO";
		
		if(ZECMID.equals("EO")){
			DATUV = "20160101";
		}else{
			String toDay = DateUtil.getToDay();
			DATUV =DateUtil.addDate(toDay, 365);
			DATUV = DATUV.replace("-", "");
		}
		int h=1;
		for(int i = 0 ; i< tempBOM.size() ; i++){
			String seqNumber = SequenceDao.manager.getERPSeq("ECO");
			ZIFNO = "ECO-"+seqNumber;
			AENNR = eco.getEoNumber();
			AENNR = ERPUtil.getECOSEQ(AENNR, h);
			
			codes.insertRow(h);
			codes.setValue("ZIFNO", ZIFNO);
			codes.setValue("AENNR", AENNR);
			codes.setValue("AETXT", AETXT);
			codes.setValue("DATUV", DATUV);
			codes.setValue("AEGRU", AEGRU);
			codes.setValue("ZECMID", ZECMID);
			//System.out.println("============== ECO ="+h+" ="+AENNR);
			//System.out.println("ZIFNO == " + ZIFNO);
			//System.out.println("AENNR == " + AENNR);
			//System.out.println("AETXT == " + AETXT);
			//System.out.println("DATUV == " + DATUV);
			//System.out.println("AEGRU == " + AEGRU);
			//System.out.println("ZECMID == " + ZECMID);
			h++;
		}
		
		//System.out.println("============== START ERP ECO SEND :"+eco.getEoNumber() +":"+codes.getNumRows()+"==============");
		//System.out.println("codes="+codes.getNumRows());
		
		codes.firstRow();
		for(int j = 1 ; j<=codes.getNumRows() ; j++,codes.nextRow()  ){
			ZIFNO = (String)codes.getValue("ZIFNO");
			AENNR = (String)codes.getValue("AENNR");
			AETXT = (String)codes.getValue("AETXT");
			DATUV = (String)codes.getValue("DATUV");
			AEGRU = (String)codes.getValue("AEGRU");
			ZECMID = (String)codes.getValue("ZECMID");
			/*
			System.out.println("ZIFNO == " + ZIFNO);
			System.out.println("AENNR == " + AENNR);
			System.out.println("AETXT == " + AETXT);
			System.out.println("DATUV == " + DATUV);
			System.out.println("AEGRU == " + AEGRU);
			System.out.println("ZECMID == " + ZECMID);
			*/
			
		}
		
		//System.out.println("============== END ECO SEND :"+AENNR +"==============");
		
		//SAP Send
		//System.out.println("isExcute == " + isExcute);
		if(isExcute){
			function.execute(destination);
		}
		
		
		//SAP Return START
		//System.out.println("============== Start ECO Return :"+AENNR +"==============");
		JCoTable returnCodes = function.getTableParameterList().getTable("ET_OUTPUT");
		returnCodes.firstRow();
		for(int j = 1 ; j<=returnCodes.getNumRows() ; j++,returnCodes.nextRow()  ){
			//System.out.println("ZIFNO = " +returnCodes.getValue("ZIFNO"));
			//System.out.println(".INCLUDE = " +codes.getValue(".INCLUDE"));
			//System.out.println("ZIFSTA = " +returnCodes.getValue("ZIFSTA"));
			//System.out.println("ZIFMSG = " +returnCodes.getValue("ZIFMSG"));
		}
		//System.out.println("============== End ECO Return :"+AENNR +"==============");
		//SAP Return END
		
		//ERPHistory 
		ERPHistoryHelper.service.createHistoryECO(codes,returnCodes);
	}
		
	@Override
	public void sendERPToBOM(Vector<BomData> bomList,String ecoNumber) throws Exception{
		
		// SAP Setting Start
		JCoDestination destination =JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		
		JCoFunction function = destination.getRepository().getFunction(SAPConnection.FUNTION_BOM);
		if(function == null){
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}
		
		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		//SAP Setting END
		//Row Data Insert
		
		int h=1;
		HashMap<String, Object> map = new HashMap<String, Object>();
		for(int i = 0 ; i< bomList.size() ; i++){
			BomData data = bomList.get(i);
			/*
			WTPart parentPart = (WTPart)list.get(0);
			WTPart childPart = (WTPart)list.get(1);
			*/
			String MATNR = data.getParent(); //모부품
			String PVER = data.getPver();
			String IDNRK = data.getChild();//자부품
			String CVER = data.getCver();
			
			double temMENGE = data.getAmount();//수량
			int MENGE = Double.valueOf(temMENGE).intValue()  ;
			String MEINS= data.getUnit();//단위	
			MEINS = MEINS.toUpperCase();
			String seqNumber = SequenceDao.manager.getERPSeq("BOM");
			String ZIFNO = "BOM-"+seqNumber;
			String ZITMSTA = data.getChangeType(); 
			String AENNR = ERPUtil.getECOSEQ(ecoNumber, h);
			int POSNR = h;
			
			codes.insertRow(h);
			
			codes.setValue("ZIFNO", ZIFNO);
			codes.setValue("ZITMSTA", ZITMSTA);
			codes.setValue("MATNR", MATNR);
			codes.setValue("POSNR", POSNR);
			codes.setValue("IDNRK", IDNRK);
			codes.setValue("MENGE", MENGE);
			codes.setValue("MEINS", MEINS);
			codes.setValue("AENNR", AENNR);
			//System.out.println("============== BOM ="+h+" ="+AENNR);
			//System.out.println("ZIFNO == " + ZIFNO);
			//System.out.println("ZITMSTA == " + ZITMSTA);
			//System.out.println("MATNR == " + MATNR);
			//System.out.println("POSNR == " + POSNR);
			//System.out.println("IDNRK == " + IDNRK);
			//System.out.println("MENGE == " + MENGE);
			//System.out.println("MEINS == " + MEINS);
			//System.out.println("AENNR == " + AENNR);
			h++;
			HashMap<String, String> mapVer = new HashMap<String, String>();
			mapVer.put("PVER",PVER);
			mapVer.put("CVER",CVER);
			map.put(ZIFNO, mapVer);
			
		}
		
		//System.out.println("============== START ERP BOM SEND :"+ecoNumber +":"+codes.getNumRows()+"==============");
		//System.out.println("codes="+codes.getNumRows());
		
		codes.firstRow();
		for(int j = 1 ; j<=codes.getNumRows() ; j++,codes.nextRow()  ){
			
			String ZIFNO = (String)codes.getValue("ZIFNO");
			String ZITMSTA = (String)codes.getValue("ZITMSTA");
			String MATNR = (String)codes.getValue("MATNR");
			String POSNR = (String)codes.getValue("POSNR");
			String IDNRK = (String)codes.getValue("IDNRK");
			BigDecimal temMENGE = (BigDecimal)codes.getValue("MENGE");
			int MENGE = temMENGE.intValue();
			String MEINS = (String)codes.getValue("MEINS");
			String AENNR = (String)codes.getValue("AENNR");
			/*
			System.out.println("ZIFNO == " + ZIFNO);
			System.out.println("ZITMSTA == " + ZITMSTA);
			System.out.println("MATNR == " + MATNR);
			System.out.println("POSNR == " + POSNR);
			System.out.println("IDNRK == " + IDNRK);
			System.out.println("MENGE == " + MENGE);
			System.out.println("MEINS == " + MEINS);
			System.out.println("AENNR == " + AENNR);
			*/
			
		}
		
		//System.out.println("============== END BOM SEND :"+ecoNumber +"==============");
		
		//SAP Send
		//System.out.println("isExcute :"+isExcute +"==============");
		if(isExcute){
			function.execute(destination);
		}
		
		
		//SAP Return START
		//System.out.println("============== Start BOM Return :"+ecoNumber +"==============");
		JCoTable returnCodes = function.getTableParameterList().getTable("ET_OUTPUT");
		returnCodes.firstRow();
		for(int j = 1 ; j<=returnCodes.getNumRows() ; j++,returnCodes.nextRow()  ){
			//System.out.println("ZIFNO = " +returnCodes.getValue("ZIFNO"));
			//System.out.println(".INCLUDE = " +codes.getValue(".INCLUDE"));
			//System.out.println("ZIFSTA = " +returnCodes.getValue("ZIFSTA"));
			//System.out.println("ZIFMSG = " +returnCodes.getValue("ZIFMSG"));
		}
		//System.out.println("============== End BOM Return :"+ecoNumber +"==============");
		//SAP Return END
		//ERPHistory 
		ERPHistoryHelper.service.createHistoryBOM(codes,returnCodes,map);
	}
	
	@Override
	public ResultData erpCheckAction(HttpServletRequest request) throws Exception {
		//System.out.println("======== erpCheckAction 1============");
		ResultData data = new ResultData();
		
		String startZifno = StringUtil.checkNull(request.getParameter("startZifno"));
		String endZifno =  StringUtil.checkNull(request.getParameter("endZifno"));
		
		//System.out.println("======== erpCheckAction 1 = "+ startZifno+","+endZifno);
		if(startZifno.length()==0){
			data.setMessage("인터페이스 시작번호를 입력해 주세요");
			data.setResult(false);
			return data;
		}
		//System.out.println("======== erpCheckAction 2============");
		boolean isPART1 = startZifno.indexOf("PART")>=0;
		boolean isECO1 = startZifno.indexOf("ECO")>=0;
		boolean isBOM1 = startZifno.indexOf("BOM")>=0;
		
		if(endZifno.length()>0){
			boolean isPART2 = endZifno.indexOf("PART")>=0;
			boolean isECO2 = endZifno.indexOf("ECO")>=0;
			boolean isBOM2 = endZifno.indexOf("BOM")>=0;
			
			if( !( (isPART1 && isPART2) || (isECO1 && isECO2) || (isBOM1 ||isBOM2) )){
				data.setMessage("인터페이스 번호를 잘못 입력 하였습니다.");
				data.setResult(false);
				return data;
			}
		}
		
		Class cls = null;
		if(isPART1){
			cls = PARTERP.class;
		}else if(isECO1){
			cls = ECOERP.class;
		}else if(isBOM1) {
			cls = BOMERP.class;
		}
		
		data = returnERP(cls,startZifno, endZifno);
		return data;
		
	}
	
	
	
	@Override
	public ResultData returnERP(Class cls,String startZifno,String endZifno) throws Exception {
		ResultData data = new ResultData();
		
		// SAP Setting Start
		JCoDestination destination =JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		
		JCoFunction function = destination.getRepository().getFunction(SAPConnection.FUNTION_PDM_RETURN);
		if(function == null){
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}
		
		JCoTable codes = function.getTableParameterList().getTable("IT_INPUT");
		//SAP Setting START
		codes.insertRow(0);
		
		codes.setValue("LOW", startZifno);
		codes.setValue("HIGH", endZifno);
		//SAP Setting END
		
		//SAP CALL
		if(isExcute){
			function.execute(destination);
		}
		//SAP Return START
		JCoTable returnCodes = function.getTableParameterList().getTable("ET_OUTPUT");
		
		
		ERPHistoryHelper.service.updateHistory(cls,startZifno,endZifno, returnCodes);
		
		data.setMessage("ERP 현황 업데이트 완료 하였습니다.");
		data.setResult(true);
		//for(int j = 1 ; j<=returnCodes.getNumRows() ; j++,returnCodes.nextRow()  ){
			
		//}
		//SAP Return END
		return data;
	}
	
}
