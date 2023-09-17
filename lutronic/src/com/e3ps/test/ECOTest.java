package com.e3ps.test;

import com.e3ps.common.iba.AttributeKey.IBAKey;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.iba.IBAUtil;

import wt.enterprise.RevisionControlled;

import com.e3ps.common.iba.AttributeKey;

import wt.iba.value.IBAHolder;

import com.e3ps.part.dto.PartData;
import com.e3ps.part.util.PartUtil;

import wt.folder.Folder;

import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.util.EpmPublishUtil;

import wt.epm.EPMDocument;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.epm.EPMDocumentMaster;

import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.erp.service.ERPHelper;

import wt.epm.structure.EPMReferenceLink;

import java.util.Map;
import java.util.Vector;

import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.change.service.ChangeUtil;
import com.e3ps.change.service.ECOHelper;
import com.e3ps.change.service.StandardECOService;

import wt.lifecycle.State;
import wt.fc.QueryResult;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.fc.PersistenceHelper;
import wt.util.WTException;
import wt.part.WTPartUsageLink;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

import com.e3ps.change.service.ECOSearchHelper;

import java.util.ArrayList;

import com.e3ps.common.util.CommonUtil;
import com.e3ps.change.EChangeOrder;

import wt.part.WTPart;

import java.util.List;

public class ECOTest {

	public static void main(String[] args) {
		try{
			String oid = "com.e3ps.change.EChangeOrder:8462588";
			Object obj = CommonUtil.getObject(oid);
			
			if(obj instanceof EChangeOrder){
				EChangeOrder eco = (EChangeOrder)obj;	
				List<WTPart> partList = ECOSearchHelper.service.ecoPartReviseList(eco);
				
				for(WTPart part : partList){
					//System.out.println(part.getNumber());
				}
				//List<WTPart> partList= ECOHelper.service.productPartList(eco);
				//ECOHelper.service.completeProduct(partList,eco);
				//System.out.println(eco.getLifeCycleState().toString().equals("APPROVED"));
				//ECOHelper.service.completeECO(eco);
				//ECOHelper.service.createECOBaseline(eco);
				//LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)eco, State.toState("APPROVED"));
			}
		}catch(Exception e){
			e.printStackTrace();
			//System.out.println(e.getMessage());
		}
	}
	public static void completeProduct(List<WTPart> list,EChangeOrder eco) throws Exception{	 
		
		/*최종 결재자,검토자*/
		Map<String, String> map = ChangeUtil.approvedSetData(eco);
		String eoType = eco.getEoType();
		String state ="APPROVED";
		if(eoType.equals(ECOKey.ECO_DEV)){
			state="DEV_APPROVED";
		}
		map.put("state", state);
		map.put("eoType", eoType);
		
		//중복 되는 부붐 제외
		Vector vecTemp = new Vector();
		for(WTPart part : list){
			
			if(vecTemp.contains(part)){
				continue;
			}else{
				vecTemp.add(part);
				completePartStateChange(part, map);
			}
			
		}
	}
	public static void completePartStateChange(WTPart part,Map<String, String> map) throws Exception {
		
		if(part.getNumber().equals("3010372200")){
			//System.out.println("part Number :: "+ part.getNumber()+"\t part Name :: "+ part.getName()+"    Strart");
		}
		Folder folder = null;
		String partState = part.getLifeCycleState().toString();
		if(partState.equals("APPROVED")){
			return;
		}
		PartData partData = new PartData(part);
		//Generic instance, 더미 파일은 승인 처리 하지 않음
		boolean isStateChange = !(partData.isGENERIC() || partData.isINSTANCE()) || !PartUtil.isChange(part.getNumber());
		/*Part State change*/
		
		String state = map.get("state");//"APPROVED";state
		
		if(isStateChange){
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)part, State.toState(state));
		}
		
		
		EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
		//속성 전파
		
		approvedIBAChange(part, map);
		if(epm != null){
			/*3D EPM State Change*/
			if(isStateChange){
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm, State.toState(state));
			}
			
			//속성 전파
			approvedIBAChange(epm, map);
			/*2D EPM State Change */
			Vector<EPMReferenceLink> vec =EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm.getMaster());
			for(int i = 0 ; i < vec.size() ; i++){
				EPMReferenceLink link = vec.get(i);
				EPMDocument epm2D = link.getReferencedBy();
				
				//if(epm2D.getAuthoringApplication().toString().equals("DRAWING")){
					EpmPublishUtil.publish(epm2D); 
				//}
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epm2D, State.toState(state));
			}
		}
		//관련 도면
		/*
		Vector<EPMDescribeLink> vecDesc =EpmSearchHelper.service.getEPMDescribeLink(part, true);
		for(EPMDescribeLink likDesc : vecDesc){
			EPMDocument epmDesc = likDesc.getDescribedBy();
			LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged)epmDesc, State.toState(state));
		}
		*/
		if(part.getNumber().equals("3010372200")){
			//System.out.println("Part Number"+part.getNumber()+"  emd ");
		}
	}
	public static List<WTPart> productPartList(EChangeOrder eco) throws Exception{	
		
		String view = "Design";
		List<WTPart> BomList = new ArrayList<WTPart>();
		List<WTPart> productList =ECOSearchHelper.service.getCompletePartList(eco);
		//System.out.println("productList start "+productList.size());
		for(WTPart productPart : productList){
			BomList.add(productPart);
			BomList = getBomList(productPart, ViewHelper.service.getView(view), BomList);
		}
		//System.out.println("productList emd");
		return BomList;
	}
	public static   List<WTPart>  getBomList(WTPart part,View view,List<WTPart> BomList) throws Exception{
		ArrayList list = descentLastPart(part, view,null);

		for (int i = 0; i < list.size(); i++) {
			
			Object[] o = (Object[]) list.get(i);
			WTPartUsageLink linko =(WTPartUsageLink) o[0];
			WTPart cPart = (WTPart)o[1];
			
			BomList.add(cPart);
			getBomList((WTPart) o[1],view,BomList);
		}
		
		
		return BomList;
	}
	private static ArrayList descentLastPart(WTPart part, wt.vc.views.View view, State state)
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
	public void approvedIBAChange(EChangeOrder eco) throws Exception{
		
		try{
			
			Map<String,String> map = ChangeUtil.approvedSetData(eco);
			
			
			List<WTPart>  partList= ECOSearchHelper.service.ecoPartReviseList(eco);
			for(WTPart part : partList){
				
				approvedIBAChange(part, map);
				
				EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
				
				if(epm != null){
					approvedIBAChange(epm, map);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void approvedIBAChange(RevisionControlled rc,Map<String,String> map) throws Exception{
		//RevisionControlled rc = null;
		
		map = ChangeUtil.changeApprove(rc, map);
		//IBAHolder
		//ECONO,ECODATE,CHK,APR
		String ecoNO = map.get("ECONO");
		String ecoDate = map.get("ECODATE");
		String approveName = map.get("APR");
		String checkerName = map.get("CHK");
		String eoType = map.get("eoType");
		
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, approveName , "string");
		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHK, checkerName , "string");
		String ecoNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_ECONO));
		//if(ecoNo.length()==0 || ecoNo.equals("-")){ //최초 한번만 등록
		boolean isECO = eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT) || eoType.equals(AttributeKey.ECOKey.ECO_PRODUCT); 
		boolean isFirst = ecoNo.length()== 0 || ecoNo.equals("-"); 
		if(isECO && isFirst){ //최초 한번만 등록 ,양산 이나 설계변경  , 개발 EO 제외
			IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_ECONO, ecoNO , "string");
			IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_ECODATE, ecoDate , "string");
		}
		
		//EO,ECO시 누적으로 등록
		String changeNo = IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_CHANGENO);
		String changeDate = IBAUtil.getAttrValue((IBAHolder)rc, IBAKey.IBA_CHANGEDATE);
		
		if(StringUtil.checkString(changeNo)){
			changeNo = changeNo+","+ecoNO;
		}else {
			changeNo = ecoNO;
		}
		
		if(StringUtil.checkString(changeDate)){
			changeDate = changeDate+","+ecoDate;
		}else {
			changeDate = ecoDate;
		}
		IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_CHANGENO, changeNo , "string");
		IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHANGEDATE, changeDate , "string");
		
		
	}
}
