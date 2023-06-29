package com.e3ps.change.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.ManagedBaseline;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.beans.ECRData;
import com.e3ps.change.key.ChangeKey;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.beans.NumberCodeData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.service.PartHelper;
import com.e3ps.part.service.PartSearchHelper;
import com.e3ps.part.util.PartUtil;

public class StandardChangeWfService extends StandardManager implements ChangeWfService {
	
	public static StandardChangeWfService newStandardChangeWfService() throws Exception {
		final StandardChangeWfService instance = new StandardChangeWfService();
		instance.initialize();
		return instance;
	}
	
	/**
	 * ECA 활동 결재 유무 체크
	 * @param pbo
	 * @return
	 */
	@Override
	public boolean isApprovedTask(Object pbo) {
		//if (!(pbo instanceof EChangeActivity)) {
			return false;
		//}

		//EChangeActivity activity = (EChangeActivity) pbo;
		//return activity.getIsAprove();
	}
	
	@Override
	public Map<String,Object> wf_Document(String ecaOid) throws Exception {
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(ecaOid);

		String ecaCode = eca.getActiveType();//eca.get
		boolean isAttach = true;//eca.getDefinition().isIsAttach();
		WTUser user = (WTUser) SessionHelper.getPrincipal();
		String ecaCheck = "false";
		
		Map<String,Object> map = new HashMap<String,Object>();
		
		
		/*
		 * 
		 *    산출물
		 * 
		 */
		
		List<DocumentActivityLink>	docList	= ECAHelper.service.getECADocumentLink(eca);
		boolean isState = true;
		if(docList.size() ==0 ) isState = false;
		
		List<DocumentData> docLink = new ArrayList<DocumentData>(); 
		
		for(int i = 0 ; i <docList.size() ; i++){
			DocumentActivityLink link = docList.get(i);
			WTDocumentMaster master = link.getDoc(); 
			
			WTDocument doc =DocumentHelper.service.getLastDocument(master.getNumber());
			if(doc == null) {
				continue;
			}
			
			if(!doc.getLifeCycleState().toString().equals("APPROVED")) isState = false;
			
			DocumentData docData = new DocumentData(doc);
			//docData.setEtcOid(link.getPersistInfo().getObjectIdentifier().toString());
			
			docLink.add(docData);
			map.put("docLink", docLink);
		}
		
		map.put("isState",isState);
		map.put("isAttach",isAttach);
		map.put("ecaCode",ecaCode);
		map.put("ecaCheck", ecaCheck);
		//map.put("isDocument", eca.getDefinition().isIsDocument());
		//map.put("isDocState", eca.getDefinition().isIsDocState());
		
		return map;
	}
	
	@Override
	public List<Map> wf_CheckDrawing(String ecaOid) throws Exception {
		
		List<Map> list = new ArrayList<Map>();
		
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(ecaOid);
		ECOChange eo = eca.getEo();
		
		QueryResult qr = PersistenceHelper.manager.navigate(eo,"part",EcoPartLink.class,false);
		while(qr.hasMoreElements()){
			EcoPartLink link = (EcoPartLink)qr.nextElement();
			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster)link.getPart();
			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
			
			HashMap checkMap = new HashMap();//ECAHelper.service.checkIPOPart(part);
			String partType = (String)checkMap.get("PART_TYPE");
			boolean isControl = "ORCAD".equals(partType) ? true : false;
			boolean isDwg = (boolean)checkMap.get("DRAWING");
			boolean isPDF = (boolean)checkMap.get("isPDF");
			boolean isOrCad = (boolean)checkMap.get("isOrCad");
			boolean isOrCadDoc = (boolean)checkMap.get("isOrCadDoc");
			
			String partoid = part.getPersistInfo().getObjectIdentifier().toString();
			
			String imgUrl = DrawingHelper.service.getThumbnailSmallTag(DrawingHelper.service.getEPMDocument(part));
			String icon = CommonUtil.getObjectIconImageTag(part);
			
			Map map = new HashMap();
			map.put("imgUrl", imgUrl);
			map.put("icon", icon);
			map.put("partOid", partoid);
			map.put("partNumber", part.getNumber());
			map.put("partName", part.getName());
			map.put("partState", part.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("partVersion", part.getVersionIdentifier().getValue());
			map.put("partCreator", VersionControlHelper.getVersionCreator(part).getFullName());
			
			//Part Check
			map.put("isControl", isControl);
			map.put("isDwg", isDwg);
			map.put("isPDF", isPDF);
			map.put("isOrCad", isOrCad);
			map.put("isOrCadDoc", isOrCadDoc);
			
			list.add(map);
		}
		return list;
	}
	
	@Override
	public List<Map<String,Object>> wf_OrderNumber(String ecaOid) throws Exception {
		
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		EChangeActivity eca = (EChangeActivity)CommonUtil.getObject(ecaOid);
		ECOChange eo = eca.getEo();
		
		QueryResult qr = PersistenceHelper.manager.navigate(eo,"part",EcoPartLink.class,false);
		while(qr.hasMoreElements()){
			EcoPartLink link = (EcoPartLink)qr.nextElement();
			String version = link.getVersion();
			WTPartMaster master = (WTPartMaster)link.getPart();
			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
			boolean seqCheck = IBAUtil.getBooleanValue(part, AttributeKey.EPMKey.IBA_SEQCHECK);
			String seqCheckStr = seqCheck?"채번됨":"채번안됨";
			String partoid = part.getPersistInfo().getObjectIdentifier().toString();
			
			String imgUrl = DrawingHelper.service.getThumbnailSmallTag(DrawingHelper.service.getEPMDocument(part));
			String icon = CommonUtil.getObjectIconImageTag(part);
			
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("imgUrl", imgUrl);
			map.put("icon", icon);
			map.put("partOid", partoid);
			map.put("partNumber", part.getNumber());
			map.put("partName", part.getName());
			map.put("partState", part.getLifeCycleState().getDisplay(Message.getLocale()));
			map.put("partVersion", part.getVersionIdentifier().getValue());
			map.put("partCreator", VersionControlHelper.getVersionCreator(part).getFullName());
			map.put("seqCheck", seqCheckStr);
			
			list.add(map);
		}
		Collections.sort(list, new Comparator<Map<String, Object >>() {

            @Override

            public int compare(Map<String, Object> first, Map<String, Object> second) {
                return ((String) first.get("partNumber")).compareTo((String) second.get("partNumber")); //descending order
            }

        });
		return list;
	}
	@Override
	public List<Map<String,Object>> wf_CheckPart(String oid) throws Exception {
		return wf_CheckPart(oid,false,"false");
	}
	
	@Override
	public List<Map<String,Object>> wf_CheckPart(String oid,boolean checkDummy,String distribute) throws Exception {
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
					map.put("nextOid", nextData.oid);
					map.put("nextVROid", nextData.vrOid);
					WTUser createUser = (WTUser) nextPart.getCreator().getPrincipal();
					String nextUserCreaterName = createUser.getFullName();
					map.put("nextUserCreaterName", nextUserCreaterName);
					map.put("nextName", nextData.name);
					map.put("nextNumber", nextData.number);
					map.put("nextVersion", nextData.version);
					map.put("nextIteration", nextData.iteration);
					map.put("nextState", nextData.getLifecycle());
					
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
			map.put("partOid", nowPartData.oid);
			map.put("linkOid", linkOid);
			map.put("rowSpan", rowSpan);
			map.put("revisable", revisableTemp);
			map.put("number", nowPartData.number);
			map.put("userCreatorName", userCreatorName);
			map.put("name", nowPartData.name);
			map.put("nowVROid", nowPartData.vrOid);
			map.put("productOid", nowPartData.getPDMLinkProductOid());
			//map.put("partOid", CommonUtil.getOIDString(part));
			map.put("version", nowPartData.version);
			map.put("iteration", nowPartData.iteration);
			map.put("state", nowPartData.getLifecycle());
			map.put("stateKey", nowPartData.stateKey);
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
		return list;
	}
	
	@Override
	public List<Map<String,String>> wf_Part_Include(String ecoOid) throws Exception {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		QueryResult rt = null;
		if(ecoOid.length()>0){
    		EChangeOrder eco = (EChangeOrder)CommonUtil.getObject(ecoOid);
    		rt = ECOSearchHelper.service.ecoPartLink(eco);
    		while( rt.hasMoreElements()){
				Object[] o = (Object[])rt.nextElement();
				
				EcoPartLink link = (EcoPartLink)o[0];
    			//String apply = StringUtil.checkNull(link.getStock()) ;
    			//String serial = StringUtil.checkNull(link.getSerialNumber()) ;
    			WTPartMaster master =  (WTPartMaster)link.getPart();

    			String version = link.getVersion();
    			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
    			
    			Map<String,String> map = new HashMap<String,String>();
    			map.put("linkOid", link.getPersistInfo().getObjectIdentifier().toString());
    			map.put("partNumber", part.getNumber());
    			map.put("partName", part.getName());
    			map.put("partVersion", part.getVersionIdentifier().getValue());
    			//map.put("apply", apply);
    			//map.put("serial", serial);
    			
    			list.add(map);
    		}
		}
		return list;
	}
	static class NumberAscCompare implements Comparator<Map<String,Object>> {
		 
		/**
		 * 오름차순(ASC)
		 */
			@Override
		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			//return o1.getName().compareTo(o2.getName());
				String o1N = String.valueOf(o1.get("Number"));
				String o2N = String.valueOf(o2.get("Number"));
			return o1N.compareTo(o2N);
		}
 
	}
}
