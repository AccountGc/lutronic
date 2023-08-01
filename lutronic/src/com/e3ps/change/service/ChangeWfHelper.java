package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.service.StandardChangeWfService.NumberAscCompare;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.util.PartUtil;

import net.sf.json.JSONArray;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.services.ServiceFactory;
import wt.vc.baseline.ManagedBaseline;

public class ChangeWfHelper {
	public static final ChangeWfService service = ServiceFactory.getService(ChangeWfService.class);
	public static final ChangeWfHelper manager = new ChangeWfHelper();
	
	public JSONArray wf_CheckPart(String oid,boolean checkDummy,String distribute) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		Object obj = CommonUtil.getObject(oid);
		EChangeOrder eco = null;
		if(obj instanceof EChangeOrder)
		 eco = (EChangeOrder)obj;
		else if(obj instanceof EChangeActivity){
			eco = (EChangeOrder) ((EChangeActivity) obj).getEo();
		}
		boolean  isDistribute = StringUtil.checkNull(distribute).equals("true") ? true : false;
		//System.out.println("wf_CheckPart isDistribute =" + isDistribute);
		String type = eco.getEoType();
		int idx = 0;
		QueryResult qr = ECOSearchHelper.service.ecoPartLink(eco);
		while(qr.hasMoreElements()){
			
			idx++;
			String idxTemp = String.valueOf(idx);
			Map<String, Object> map = new HashMap<String,Object>();
			map.put("idxPart", idxTemp);
			
			Object[] o = (Object[])qr.nextElement();
			
			EcoPartLink link = (EcoPartLink)o[0];
			
			//EcoPartLink link = (EcoPartLink)qr.nextElement();
			String linkOid = CommonUtil.getOIDString(link);
			WTPartMaster master =link.getPart();
			boolean isDummy =PartUtil.isChange(master.getNumber());
			//더미 제외
			if(checkDummy){
				if(isDummy){
					continue;
				}
			}
			String version = link.getVersion();
			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
			String partOid = CommonUtil.getOIDString(part);
			String iteration = part.getIterationIdentifier().getSeries().getValue();
			String nextOid = null;
			boolean revisable = "APPROVED".equals(part.getLifeCycleState().toString()) 
					|| "DEV_APPROVED".equals(part.getLifeCycleState().toString());
			//String apply = StringUtil.checkNull(link.getStock()) ;
			//String serial =  StringUtil.checkNull(link.getSerialNumber()) ;
			WTPart nextPart = null;
			EPMDocument epm3d = null;
			Vector<EPMReferenceLink> vec2D = new Vector<EPMReferenceLink>();
			String isDisuse ="";
			//if(link.isIsDelete()) isDisuse ="<b><font color='red'>√</font></b>";
			boolean isBOM = link.isBaseline();
			String isBOMCheck ="";
			String BOMValueCheck = "";
			if(isBOM) {
				isBOMCheck ="<b><font color='red'>√</font></b>";
				BOMValueCheck = "checked";
			}
			if(link.isRevise()) {
				nextPart = (WTPart)com.e3ps.common.obj.ObjectUtil.getNextVersion(part);
				partOid = CommonUtil.getOIDString(nextPart);
				PartData nextData = new PartData(nextPart);
				nextOid = CommonUtil.getOIDString(nextPart);
				if(nextPart != null) {
					EPMBuildRule buildRule=PartSearchHelper.service.getBuildRule(nextPart);
					if(buildRule != null){
						epm3d =(EPMDocument)buildRule.getBuildSource();
						map.put("epm3dOid", CommonUtil.getOIDString(epm3d));
						map.put("epm3dNumber", epm3d.getNumber());
					}
					map.put("nextOid", nextData.getOid());
					map.put("nextVROid", nextData.getVrOid());
					WTUser createUser = (WTUser) nextPart.getCreator().getPrincipal();
					String nextUserCreaterName = createUser.getFullName();
					map.put("nextUserCreaterName", nextUserCreaterName);
					map.put("nextName", nextData.getName());
					map.put("nextNumber", nextData.getNumber());
					map.put("nextVersion", nextData.getVersion());
					map.put("nextIteration", nextData.getIteration());
					map.put("nextState", nextData.getState());
					
				}else{
					EPMBuildRule buildRule =PartSearchHelper.service.getBuildRule(part);
					if(buildRule != null){
						epm3d =(EPMDocument)buildRule.getBuildSource();
						map.put("epm3dOid", CommonUtil.getOIDString(epm3d));
						map.put("epm3dNumber", epm3d.getNumber());
					}
				}
				
				if(epm3d != null){
					vec2D =  EpmSearchHelper.service.getEPMReferenceList((EPMDocumentMaster)epm3d.getMaster());
				}
				revisable = false;
			}
			List<Map<String,Object>> list2d = new ArrayList<Map<String,Object>>();
			if(epm3d != null) { 
				for(int h= 0 ; h <vec2D.size() ; h++){ 
					EPMReferenceLink epmlink = vec2D.get(h);
					EPMDocument epm2d = epmlink.getReferencedBy();
					Map<String,Object> map2d = new HashMap<String,Object>();
					
					map2d.put("epm2dOid", CommonUtil.getOIDString(epm2d));
					map2d.put("epm2Number", epm2d.getNumber());
					list2d.add(map2d);
				}
				
			}
			
			String rowSpan = String.valueOf(vec2D.size());
			
			String revisableTemp = String.valueOf(revisable); 
			
		
			String bom = "<input type='button' onclick=javascript:viewBom('" + partOid+ "') value='BOM 전개'>";
			String baselineOid = "";
			if(isDistribute){
				ManagedBaseline baseline = ChangeHelper.service.getEOToPartBaseline(eco.getEoNumber(), part.getNumber());
				baselineOid = CommonUtil.getOIDString(baseline);
				bom = "<input type='button' onclick=javascript:distributeBOM('"+partOid+"','"+CommonUtil.getOIDString(baseline)+"') value='BOM 전개' >";	
			}
			
			/**
			 * 		관련 도면
			 * 
			 */
			/*
			List<String> descEpm = new ArrayList<String>();
			if(nextPart != null){
				Vector<EPMDescribeLink> vecDesc = EpmSearchHelper.service.getEPMDescribeLink(nextPart, true);
				for(EPMDescribeLink linkVec : vecDesc){
					EPMDocument epmDesc = linkVec.getDescribedBy();
					descEpm.add(epmDesc.getNumber());
				}
			}
			
			List<String> descEpm2 = new ArrayList<String>();
			if(nextPart != null){
				Vector<EPMDescribeLink> vecDesc = EpmSearchHelper.service.getEPMDescribeLink(part, true);
				for(EPMDescribeLink linkVec : vecDesc){
					EPMDocument epmDesc = linkVec.getDescribedBy();
					descEpm2.add(epmDesc.getNumber());
				}
			}
			*/
			PartData nowPartData = new PartData(part);
			WTUser createUser = (WTUser) part.getCreator().getPrincipal();
			String userCreatorName = createUser.getFullName();
			map.put("oid", oid);
			map.put("partOid", nowPartData.getOid());
			map.put("linkOid", linkOid);
			map.put("rowSpan", rowSpan);
			map.put("revisable", revisableTemp);
			map.put("number", nowPartData.getNumber());
			map.put("userCreatorName", userCreatorName);
			map.put("name", nowPartData.getName());
			map.put("nowVROid", nowPartData.getVrOid());
			map.put("productOid", nowPartData.getPDMLinkProductOid());
			//map.put("partOid", CommonUtil.getOIDString(part));
			map.put("version", nowPartData.getVersion());
			map.put("iteration", nowPartData.getIteration());
			map.put("state", nowPartData.getState());
			map.put("stateKey", nowPartData.getStateKey());
			map.put("isDisuse", isDisuse);
			map.put("nextPart", nextOid);
			map.put("bom", bom);
			map.put("list", list2d);
			//map.put("descEpm", descEpm);
			//map.put("descEpm2", descEpm2);
			//map.put("iteration", iteration);
			map.put("type", type);
			//map.put("apply", NumberCodeHelper.service.getValue("APPLY", apply));
			//map.put("serial", serial);
			map.put("BOMCheck",isBOMCheck);
			map.put("BOMValueCheck" , BOMValueCheck);
			map.put("isDummy", isDummy);
			map.put("baselineOid", baselineOid);
			
			list.add(map);
		}
		Collections.sort(list,new NumberAscCompare());
		return JSONArray.fromObject(list);
	}
}
