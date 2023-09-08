package com.e3ps.part.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.value.IBAHolder;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class BomSearchHelper {
	public static final BomSearchService service = ServiceFactory.getService(BomSearchService.class);
	public static final BomSearchHelper manager = new BomSearchHelper();
	
	/**
	 * 일반 BOM 전체 조회 되는 로직
	 */
	public List<Map<String, Object>> getAUIPartTreeAction(	Map<String, Object> param) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String oid = (String) param.get("oid");
		String view = (String) param.get("view");
		String desc = (String) param.get("desc");
		String baseline2 = (String) param.get("baseline2");
		String checkDummy = (String) param.get("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();

		Baseline bsobj = null;
		if (!"null".equals(baseline2) && baseline2.length() > 0) {
			bsobj = (Baseline) rf.getReference(baseline2).getObject();
		}
		if (bsobj != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=", bsobj.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=", part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}

		BomBroker broker = new BomBroker();
		
		View[] views = ViewHelper.service.getAllViews();

		if(view == null){
			view = views[0].getName();
		}
		//MANUFACTURE
		HashMap<String, String> manuFactureMap = CodeHelper.service.getCodeMap("MANUFACTURE");
						
		//PRODUCTMETHOD
		HashMap<String, String> productMap= CodeHelper.service.getCodeMap("PRODUCTMETHOD");
		
		HashMap<String, String> departMap= CodeHelper.service.getCodeMap("DEPTCODE");
		//PartTreeData root = broker.getOneleveTree(part, bsobj);//
		PartTreeData root = broker.getTree(part, !"false".equals(desc), bsobj, ViewHelper.service.getView(view));
		
		HashMap<String, HashMap<String, String>> codeMap = new HashMap<String, HashMap<String,String>>();
		codeMap.put("manuFactureMap", manuFactureMap);
		codeMap.put("productMap", productMap);
		codeMap.put("departMap", departMap);
		root.setLocationOid("T"+0);
		
		
		Map<String, Object> map1 = setBoMDate(null,root,0,codeMap);
		list.add(map1);
		
		//getDhtmlXPartData(root,rowNum2);
		
		
		
		int idx =1;
		
//		partAUITreeSetting(root, list, idx,isCheckDummy);
//		int seq =1;
//		for(Map<String, Object>  mapData : list ){
//			mapData.put("seq", seq);
//			seq++;
//		}
		return list;
	}
	
	public List<Map<String, Object>> viewAUIPartBomChildAction(	Map<String, Object> param) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String oid = (String) param.get("oid");

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();


		BomBroker broker = new BomBroker();
		
		View[] views = ViewHelper.service.getAllViews();

		String view = views[0].getName();
		//MANUFACTURE
		HashMap<String, String> manuFactureMap = CodeHelper.service.getCodeMap("MANUFACTURE");
						
		//PRODUCTMETHOD
		HashMap<String, String> productMap= CodeHelper.service.getCodeMap("PRODUCTMETHOD");
		
		HashMap<String, String> departMap= CodeHelper.service.getCodeMap("DEPTCODE");
		//PartTreeData root = broker.getOneleveTree(part, bsobj);//
		PartTreeData root = broker.getTree(part, true, null, ViewHelper.service.getView(view));
		
		HashMap<String, HashMap<String, String>> codeMap = new HashMap<String, HashMap<String,String>>();
		codeMap.put("manuFactureMap", manuFactureMap);
		codeMap.put("productMap", productMap);
		codeMap.put("departMap", departMap);
		root.setLocationOid("T"+0);
		

		List<PartTreeData> childrenList =root.children;
		for(PartTreeData child : childrenList) {
			
			Map<String, Object> map1 = setBoMDate(root,child,0,codeMap);
			list.add(map1);
		}
		
		//getDhtmlXPartData(root,rowNum2);
		
		
		
		int idx =1;
		
//		partAUITreeSetting(root, list, idx,isCheckDummy);
//		int seq =1;
//		for(Map<String, Object>  mapData : list ){
//			mapData.put("seq", seq);
//			seq++;
//		}
		return list;
	}
	
	
	/***
	 * BOM View 용
	 * @param parent
	 * @param child
	 * @param idx
	 * @param manuFactureMap
	 * @param productMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> setBoMDate(PartTreeData parent,PartTreeData child,int idx,HashMap<String, HashMap<String, String>> codeMap) throws Exception{
		  Map<String, Object> map = new HashMap<String, Object>();
		
	   	  
	   	WTPart pPart = null;
	   	String pOid = "";
	   	String pNumber = "";
	 	String parentOid = "";
	   	WTPart cPart = child.part;
	    HashMap<String, String> manuFactureMap = codeMap.get("manuFactureMap");
	    HashMap<String, String> productMap = codeMap.get("productMap");
	    HashMap<String, String> departMap = codeMap.get("departMap");
	   	if(parent != null){
	   		 pPart = parent.part;
	   		 pOid  = parent.getLocationOid();//CommonUtil.getOIDString(pPart);
	   		parentOid = CommonUtil.getOIDString(pPart);
	   		 pNumber = pPart.getNumber();
	   	}
	   	String number = cPart.getNumber();
	   	String cOid = CommonUtil.getOIDString(cPart);
	   	int level = child.level;
	   	String dwgNo = child.getDwgNo();
	   	String name  = cPart.getName();
	   	String[]  lineStack = new String[50];
	   	String rev =  child.version + "." + child.iteration;
	   	String spec = child.getSpecification();
	   	double quantity = child.quantity;
	   	String ecoNo = child.ecoNo;
	   	String remarks =  StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) cPart, AttributeKey.IBAKey.IBA_REMARKS));
	   	String deptcode = StringUtil.checkNull(departMap.get(child.deptcode));
	   	String state = cPart.getLifeCycleState().getDisplay();
	   	String manufacture = StringUtil.checkNull(manuFactureMap.get(child.manufacture));
	   	String productmethod = StringUtil.checkNull(productMap.get(child.productmethod));
	   	boolean isChildren = child.isChildren;
	    ArrayList<PartTreeData> childList = child.children;
	   	int count = childList.size();
		System.out.println("number="+number+"\tcount = "+ count);
	   	String dwgOid = child.dwgOid;
	   	String modifier = child.part.getModifierFullName();
	   //	System.out.println(idx+" , "+pNumber+" , "+pOid+" , "+number + ","+cOid);
	   	String id = "T"+idx;
	   	//System.out.println(number +","+id+","+pOid);
	   	String lineImg = "";
	   	String line = "";
	   	for(int j=1; j< child.level; j++){
	    	
			String empty = lineStack[j];
			if(empty==null){empty="empty";}
			line += "<img src='/Windchill/extcore/jsp/part/images/tree/" + empty + ".gif'></img>";
	    }
	    if(child.level>0){
		    if("join".equals(child.lineImg)){lineStack[child.level]="line";}
		    else lineStack[child.level] = "empty";
		    
		    lineImg += "<img src='/Windchill/extcore/jsp/part/images/tree/" + child.lineImg + ".gif' border=0></img>";
	    }
	    
	    String treeId = "";
	    String model = StringUtil.checkNull(IBAUtil.getAttrValue(cPart, AttributeKey.IBAKey.IBA_MODEL));
		map.put("model", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MODEL, model));
	    //System.out.println(line +","+lineImg+","+pOid);
	    map.put("line", line);
	    map.put("lineImg", lineImg);
	   	map.put("rowId", idx);
	   	map.put("id", id);
	   	map.put("oid", cOid);
	    map.put("dwgOid",	dwgOid);
	    map.put("parent", pOid);
	    map.put("parentOid", parentOid);
	   	map.put("number", number);
	    map.put("level", level);
	   	map.put("dwgNo", dwgNo);
	   	map.put("name", name);
	   	map.put("rev", rev);
	   	map.put("remarks", remarks); //OEM Info.
	   	map.put("modifier", modifier);
	   	map.put("spec", spec); //
	   	map.put("state", state);
		//map.put("model", model);
	   	map.put("quantity", quantity);
		map.put("ecoNo", ecoNo);
		map.put("deptcode", deptcode);
		map.put("manufacture", manufacture);
		map.put("productmethod", productmethod);
		map.put("count", count);
		
		if(count==0)
			map.put("children", new ArrayList());
	   	return map;
	}
	
	private int partAUITreeSetting(PartTreeData parent, List<Map<String, Object>> list , int idx,boolean isCheckDummy) throws Exception{
		   
		ArrayList<PartTreeData> childList = parent.children;
		int count = parent.children.size();
		
		//MANUFACTURE
		HashMap<String, String> manuFactureMap = CodeHelper.service.getCodeMap("MANUFACTURE");
		//PRODUCTMETHOD
		HashMap<String, String> productMap= CodeHelper.service.getCodeMap("PRODUCTMETHOD");
		//DEPTCODE
		HashMap<String, String> departMap= CodeHelper.service.getCodeMap("DEPTCODE");
		//System.out.println("childList =" + childList.size());
		HashMap<String, HashMap<String, String>> codeMap = new HashMap<String, HashMap<String,String>>();
		codeMap.put("manuFactureMap", manuFactureMap);
		codeMap.put("productMap", productMap);
		codeMap.put("departMap", departMap);
	    for(PartTreeData child : childList){
	    	child.setLocationOid("T"+idx);
	    	//System.out.println("part = " + child.number);
	    	if(isCheckDummy){
	    		boolean isChange = (PartUtil.isChange(parent.number) || PartUtil.isChange(child.number) );
		    	if( isChange  ){
					continue;
				}
	    	}
	    	
	    	Map<String, Object> map = setBoMDate(parent,child, idx,codeMap);
			list.add(map);
			idx++;
			idx=partAUITreeSetting(child, list, idx,isCheckDummy);
			
	         //partDhtmlXTreeSetting(child, childMap, rowNum2);
	    }
	    
	    return idx;
	    
	}
	
	
	public List<Map<String, Object>> getAUIBOMRootChildAction(Map<String, Object> param) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String oid = (String) param.get("oid");
		String view = (String) param.get("view");
		String desc = (String) param.get("desc");
		String baseline = (String) param.get("baseline");
		String checkDummy = (String) param.get("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		Baseline bsobj = null;
		
//		if (!"null".equals(baseline) && baseline.length() > 0) {
//			bsobj = (Baseline) rf.getReference(baseline).getObject();
//		}
		
		if (bsobj != null) {
			QuerySpec qs = new QuerySpec();
			int ii = qs.addClassList(WTPart.class, true);
			int jj = qs.addClassList(BaselineMember.class, false);
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleBObjectRef.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { jj, ii });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=", bsobj.getPersistInfo().getObjectIdentifier().getId()), new int[] { jj });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class, "masterReference.key.id", "=", part.getMaster().getPersistInfo().getObjectIdentifier().getId()), new int[] { ii });
			QueryResult qr = PersistenceHelper.manager.find(qs);
			if (qr.hasMoreElements()) {
				Object[] o = (Object[]) qr.nextElement();
				part = (WTPart) o[0];
			}
		}

		View[] views = ViewHelper.service.getAllViews();

		if(view == null){
			view = views[0].getName();
		}
		//MANUFACTURE
		HashMap<String, String> manuFactureMap = CodeHelper.service.getCodeMap("MANUFACTURE");
						
		//PRODUCTMETHOD
		HashMap<String, String> productMap= CodeHelper.service.getCodeMap("PRODUCTMETHOD");
		
		HashMap<String, String> departMap= CodeHelper.service.getCodeMap("DEPTCODE");
		//PartTreeData root = broker.getOneleveTree(part, bsobj);//
		HashMap<String, HashMap<String, String>> codeMap = new HashMap<String, HashMap<String,String>>();
		codeMap.put("manuFactureMap", manuFactureMap);
		codeMap.put("productMap", productMap);
		codeMap.put("departMap", departMap);
		boolean isDesc = !"false".equals(desc);
		Map<String, Object> map1 = setBoMDate2(null,null, part,bsobj,views[0], 0,  codeMap, isCheckDummy,isDesc);
		list.add(map1);
		return list;
	}
	
	/***
	 * BOM View 용
	 * @param parent
	 * @param child
	 * @param idx
	 * @param manuFactureMap
	 * @param productMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> setBoMDate2(WTPart parent,String pID,WTPart child,Baseline baseLine,View view,int childLevel,HashMap<String, HashMap<String, String>> codeMap,boolean isCheckDummy,boolean desc) throws Exception{
		  Map<String, Object> map = new HashMap<String, Object>();
		
	   	  
	   	WTPart pPart = null;
	   	String parentOid = "";
	   	WTPart cPart = child;
	   	double quantity = 1;

//	   	PartData data = new PartData(child, baseLine,isCheckDummy, desc);
	   	PartData data = new PartData(child);
	   	String number = data.getNumber();
		if(parent != null){
	   		 pPart = parent;
	   		parentOid = CommonUtil.getOIDString(pPart);
   			PartData parentData = new PartData(pPart);
//   			PartData parentData = new PartData(pPart, baseLine,isCheckDummy, desc);
   			
   			ArrayList<Object[]> partObjList = null;
   			if(desc)
   				partObjList = parentData.getDescPartlist();
   			else
   				partObjList = parentData.getAscPartlist();
   			if(null!=partObjList && partObjList.size()>0){
   				for(Object[] obj : partObjList){
   					if(obj[1] instanceof WTPart){
   						WTPart c = (WTPart) obj[1];
   						if(isCheckDummy){
   							boolean isChange = (PartUtil.isChange(parent.getNumber()) || PartUtil.isChange(c.getNumber()) );
   					    	if( isChange  ){
   								continue;
   							}
   						}
   							
   						String cNumber = c.getNumber();
   						if(cNumber.equals(number)){
   							if( obj[0] instanceof WTPartUsageLink){
   								WTPartUsageLink link = (WTPartUsageLink) obj[0];
   								if(null!=link){
   									quantity = link.getQuantity().getAmount();
   									break;
   								}
   							}
   						}
   					}
   				}
   			}
	   	}
	   	String cOid = data.getOid();
	   	int level = childLevel;
//	   	String dwgNo = data.getDwgNo();
	   	String name  = data.getName();
	   	String[]  lineStack = new String[50];
	   	String rev =  data.getVersion()+ "." + data.getIteration();
	   	String spec = data.getSpecification();
	   	String ecoNo = data.getEcoNo();
	   	String remarks =  data.getRemark();
	   	String deptcode = data.getDeptcode();
	   	String state = cPart.getLifeCycleState().getDisplay();
	   	String manufacture = data.getManufacture();
	   	String productmethod = data.getProductmethod();
	   	ArrayList<Object[]> sonList = data.getDescPartlist();
		if(desc)
			sonList = data.getDescPartlist();
	   	else
	   		sonList = data.getAscPartlist();
	   	int count = sonList.size();
	   
		//System.out.println("number="+number+"\tcount = "+ count);
	   	String dwgOid = data.getDwgOid();
	   	String modifier = child.getModifierFullName();
	   //	System.out.println(idx+" , "+pNumber+" , "+pOid+" , "+number + ","+cOid);
	 	String id = "";
	   	if(null==pID) id = ""+CommonUtil.getOIDLongValue(cOid);
	   	else
	 	id =pID+"_"+CommonUtil.getOIDLongValue(cOid);
	   	//System.out.println(number +","+id+","+pOid);
	   	
	   	String lineImg = "";
	   	String line = "";
	   	for(int j=1; j< level; j++){
	    	
			String empty = lineStack[j];
			if(empty==null){empty="empty";}
			line += "<img src='/Windchill/jsp/part/images/tree/" + empty + ".gif'></img>";
	    }
	    if(level>0){
		    if("join".equals(data.getLineImg())){lineStack[level]="line";}
		    else lineStack[level] = "empty";
		    
		    lineImg += "<img src='/Windchill/jsp/part/images/tree/" + lineImg + ".gif' border=0></img>";
	    }
	    String treeId = "";
	    String model = StringUtil.checkNull(IBAUtil.getAttrValue(cPart, AttributeKey.IBAKey.IBA_MODEL));
		map.put("model", NumberCodeHelper.service.getValue(AttributeKey.IBAKey.IBA_MODEL, model));
	    //System.out.println(line +","+lineImg);
	    map.put("line", line);
	    map.put("lineImg", lineImg);
	   	map.put("rowId", id);
	   	map.put("id", id);
	   	map.put("oid", cOid);
	    map.put("dwgOid",	dwgOid);
	    map.put("parent", pID);
    	map.put("parentOid", parentOid);
	   	map.put("number", number);
	    map.put("level", level);
//	   	map.put("dwgNo", dwgNo);
	   	map.put("name", name);
	   	map.put("rev", rev);
	   	map.put("remarks", remarks); //OEM Info.
	   	map.put("modifier", modifier);
	   	map.put("spec", spec); //
	   	map.put("state", state);
		//map.put("model", model);
	   	map.put("quantity", quantity);
		map.put("ecoNo", ecoNo);
		map.put("deptcode", deptcode);
		map.put("manufacture", manufacture);
		map.put("productmethod", productmethod);
		map.put("count", count);
		if(  count==0){
			map.put("children", new ArrayList<Map<String,Object>>());
		}
		//"children" : [] // children 에 빈배열을 삽입하면, Leaf 로 인식하여 lazyLoading 하지 않게 함.
		
	   	return map;
	}
	
	/**
	 * bomEditor 조회
	 */
	public List<Map<String, Object>> bomEditorList(	Map<String, Object> param) throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String oid = (String) param.get("oid");

		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();

		Baseline bsobj = null;

		BomBroker broker = new BomBroker();
		
		View[] views = ViewHelper.service.getAllViews();

		String view = views[0].getName();
		//MANUFACTURE
		HashMap<String, String> manuFactureMap = CodeHelper.service.getCodeMap("MANUFACTURE");
						
		//PRODUCTMETHOD
		HashMap<String, String> productMap= CodeHelper.service.getCodeMap("PRODUCTMETHOD");
		
		HashMap<String, String> departMap= CodeHelper.service.getCodeMap("DEPTCODE");
		//PartTreeData root = broker.getOneleveTree(part, bsobj);//
		PartTreeData root = broker.getTree(part, true, bsobj, ViewHelper.service.getView(view));
		
		HashMap<String, HashMap<String, String>> codeMap = new HashMap<String, HashMap<String,String>>();
		codeMap.put("manuFactureMap", manuFactureMap);
		codeMap.put("productMap", productMap);
		codeMap.put("departMap", departMap);
		root.setLocationOid("T"+0);
		
		
		Map<String, Object> map1 = setBoMDate(null,root,0,codeMap);
		list.add(map1);
		
		//getDhtmlXPartData(root,rowNum2);
		
		
		
		int idx =1;
		
//		partAUITreeSetting(root, list, idx,isCheckDummy);
//		int seq =1;
//		for(Map<String, Object>  mapData : list ){
//			mapData.put("seq", seq);
//			seq++;
//		}
		return list;
	}
	
}
