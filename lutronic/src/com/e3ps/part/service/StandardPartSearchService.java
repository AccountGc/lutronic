package com.e3ps.part.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.StandardManager;
import wt.util.WTException;
import wt.vc.VersionControlHelper;
import wt.vc.wip.WorkInProgressHelper;
import wt.vc.wip.Workable;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.doc.beans.DocumentData;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.drawing.service.EpmSearchHelper;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;

@SuppressWarnings("serial")
public class StandardPartSearchService extends StandardManager implements PartSearchService {

	public static StandardPartSearchService newStandardPartSearchService() throws Exception {
		final StandardPartSearchService instance = new StandardPartSearchService();
		instance.initialize();
		return instance;
	}
	
	@Override
	public  EPMBuildRule getBuildRule(Object obj) throws WTException {
		QueryResult qr = null;
		if(obj instanceof WTPart){
			WTPart part =(WTPart)obj;
			qr = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class, false);
		}else{
			EPMDocument epm =(EPMDocument)obj;
			qr = PersistenceHelper.manager.navigate(epm, "buildTarget", EPMBuildRule.class, false);
		}
      
        while (qr.hasMoreElements())
        {
            EPMBuildRule ebr = (EPMBuildRule) qr.nextElement();
            if (!WorkInProgressHelper.isWorkingCopy((Workable) ebr.getBuildSource()))
                return ebr;
        }

        return null;
    }
	
	@Override
	public Vector<EChangeOrder> getPartEoWorking(WTPart part){
		return getPartEoWorking(part, "ECO");
	}
	
	@Override
	public Vector<EChangeOrder> getPartEoWorking(WTPart part,String moduleType){
		Vector<EChangeOrder> vec = new Vector();
		try{
			EChangeOrder eco = null;
			//if(moduleType.equals("ECO")){
				//설계 변경 대상
				QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class);
		 	    //System.out.println("PartSearchHelper ::::::::::: getPartEo :" +eolinkQr.size());
		 	    if(eolinkQr != null && eolinkQr.size() > 0) {
		 	        while(eolinkQr.hasMoreElements()) {
		 	           eco = (EChangeOrder)eolinkQr.nextElement();
		 	           String state = eco.getState().toString();
		 	           //System.out.println("EcoPartLink eco =" + eco.getEoNumber() +","+ state);
		 	           if(state.equals("APPROVED") || state.equals("CANCELLED")  ){
		 	        	   continue;
		 	           }
		 	           
		 	           if(!vec.contains(eco)){
		 	        	   
		 	        	  vec.add(eco);
		 	           }
		 	          
		 	        }
		 	    }
			//}
			
	 	    
	 	   //완제품 대상
	 	   List<EOCompletePartLink> list = ECOSearchHelper.service.getCompletePartLink(part);
	 	   for(EOCompletePartLink link : list ){
	 		  eco = link.getEco();
	 		  String state = eco.getState().toString();
	 		  //System.out.println("EOCompletePartLink eco =" + eco.getEoNumber() +","+ state);
	 		
	 		  if( eco.getEoType().equals(ECOKey.ECO_CHANGE) || state.equals("APPROVED") || state.equals("CANCELLED") ){
	 			 continue;
	 		  }
	 		  
	 		  if(!vec.contains(eco)){
	        	  vec.add(eco);
	          }
	 	   
	 	   }
		}catch(Exception e){
			e.printStackTrace();
		}
		
 	    
 	    return vec;
	}
	
	/**
	 * WTPart 최신여부 체크
	 * @param part
	 * @return
	 */
	@Override
	public boolean isLastPart(WTPart part){
		
		try{
			
			if(part == null)return false;
			QuerySpec qs = new QuerySpec();
			Class cls = WTPart.class;
			int idx = qs.appendClassList(cls, true);
			
			//최신 이터레이션
			qs.appendWhere(VersionControlHelper.getSearchCondition(cls, true), new int[] { idx });
			 
			// 최신 버젼
			SearchUtil.addLastVersionCondition(qs, cls, idx);
			
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls,"thePersistInfo.theObjectIdentifier.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(part)),new int[] {idx});
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			if(rt.size() >0 )  return true;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * 설변 대상 품목 체크
	 * @param part
	 * @return true 이면 EO,ECO 없음, false 이면 EO,ECO 진행중
	 */
	@Override
	public boolean isSelectEO(WTPart part){
		
		return isSelectEO(part, "ECO");
    	
		 
    }
	
	/**
	 * 설변 대상 품목 체크
	 * @param part
	 * @return
	 */
	@Override
	public boolean isSelectEO(WTPart part,String moduleType){
		boolean isSelectEo = true;
		try{
			if(part == null) return false;
			Vector<EChangeOrder> vec = PartSearchHelper.service.getPartEoWorking(part,moduleType);
			//System.out.println(part.getNumber()+" isSelectEO vec.size() =" + vec.size());
			if(vec.size()>0) isSelectEo = false;
		}catch(Exception e){
			e.printStackTrace();
		}
    	
		return isSelectEo;
    }
	
	
	@Override
	public boolean isHasDocumentType(WTPart part, String type) throws Exception {

		QuerySpec spec = new QuerySpec();
		
		int idx_p = spec.addClassList(WTPart.class, false);
		int idx_d = spec.addClassList(WTDocument.class, false);
		int idx_l = spec.addClassList(WTPartDescribeLink.class, false);
		
		spec.setAdvancedQueryEnabled(true);
		ClassAttribute ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
		SQLFunction count = SQLFunction.newSQLFunction(SQLFunction.COUNT, ca);
		spec.appendSelect(count, new int[] {idx_d}, false);
		
		spec.appendJoin(idx_l, "roleA", idx_p);
		spec.appendJoin(idx_l, "roleB", idx_d);
		spec.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part)),new int[] {idx_p});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, type), new int[] {idx_d});
		
		QueryResult qr = PersistenceServerHelper.manager.query(spec);
		Object obj[] = (Object[]) qr.nextElement();
		int totalCount = ((BigDecimal) obj[0]).intValue();
    	
    	return (totalCount > 0);
	}
	
	@Override
	public List<WTPart> getPartEndItem(WTPart part,List<WTPart> partList) throws Exception{
		BomBroker broker = new BomBroker();
		ArrayList list = new ArrayList();
		
		PartTreeData root = broker.getTree(part, false, null);
		broker.setHtmlForm(root, list);
		
		//System.out.println(part.getNumber()+" : list.size() =" + list.size());
		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				PartTreeData data = (PartTreeData) list.get(i);
				/*
				if (data.children.size() > 0) {
					continue;
				}
				*/
				WTPart endPart = data.part;
				if(endPart.getNumber().equals(part.getNumber())){
					continue;
				}
				
				String partNumter = endPart.getNumber();
				
				if(PartUtil.isChange(partNumter)){
					continue;
				}
				//System.out.println("partNumter =" + partNumter);
				if(PartUtil.completeProductCheck(partNumter)){
					if(!partList.contains(endPart)){
						partList.add(endPart);
					}
				}
			}
		}
		
		return partList;
		
		
	}
	
	@Override
	public  List<WTPart> getPartInstance(WTPart part,List<WTPart> list) throws Exception{
		
		EPMDocument epm = DrawingHelper.service.getEPMDocument(part);
		List<EPMDocument> epmList = new ArrayList<EPMDocument>();
		if(epm != null){
			epmList = EpmSearchHelper.service.getInstance(epm, epmList);
		}
		List<WTPart> partList = new ArrayList<WTPart>();
		for(EPMDocument subEpm : epmList){
			WTPart subPart = DrawingHelper.service.getWTPart(subEpm);
			if(subPart != null){
				
				partList.add(subPart);
			}
			
		}
		
		return partList;
		
	}
	
	/**
	 * WTPart 최신여부 체크
	 * @param part
	 * @return
	 */
	@Override
	public String isLastPart(String oid){
		
		try{
			
			if(oid == null)return null;
			
			QuerySpec qs = new QuerySpec();
			Class cls = WTPart.class;
			int idx = qs.appendClassList(cls, true);
			
			//최신 이터레이션
			qs.appendWhere(VersionControlHelper.getSearchCondition(cls, true), new int[] { idx });
			 
			// 최신 버젼
			SearchUtil.addLastVersionCondition(qs, cls, idx);
			
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(cls,"thePersistInfo.theObjectIdentifier.id",SearchCondition.EQUAL,CommonUtil.getOIDLongValue(oid)),new int[] {idx});
			QueryResult rt = PersistenceHelper.manager.find(qs);
			
			if(rt.size() >0 )  {
				Object[] o = (Object[])rt.nextElement();
				WTPart part = (WTPart)o[0];
				return CommonUtil.getOIDString(part);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public String getHasDocumentOid(WTPart part, String docType)
			throws Exception {
		QuerySpec spec = new QuerySpec();
		
		int idx_p = spec.addClassList(WTPart.class, false);
		int idx_d = spec.addClassList(WTDocument.class, true);
		int idx_l = spec.addClassList(WTPartDescribeLink.class, false);
		
		spec.setAdvancedQueryEnabled(true);
		ClassAttribute ca = new ClassAttribute(WTDocument.class, WTDocument.NUMBER);
		/*SQLFunction count = SQLFunction.newSQLFunction(SQLFunction.COUNT, ca);
		spec.appendSelect(count, new int[] {idx_d}, false);*/
		
		spec.appendJoin(idx_l, "roleA", idx_p);
		spec.appendJoin(idx_l, "roleB", idx_d);
		spec.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(part)),new int[] {idx_p});
		spec.appendAnd();
		spec.appendWhere(new SearchCondition(WTDocument.class, WTDocument.DOC_TYPE, SearchCondition.EQUAL, docType), new int[] {idx_d});
		
		QueryResult qr = PersistenceServerHelper.manager.query(spec);
		//System.out.println("getHasDocumentOid="+qr.size());
		if(qr.size()==1){
			Object obj[] = (Object[]) qr.nextElement();
			String oid = CommonUtil.getOIDString((WTDocument) obj[0]);
			return oid;
		}
		/*int totalCount = ((BigDecimal) obj[0]).intValue();*/
		return null;
	}
	
}
