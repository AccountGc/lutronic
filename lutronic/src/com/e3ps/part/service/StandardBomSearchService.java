package com.e3ps.part.service;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.part.beans.ObjectComarator;
import com.e3ps.part.beans.PartData;
import com.e3ps.part.beans.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.ptc.cipjava.booleandict;

import wt.epm.EPMDocument;
import wt.facade.persistedcollection.PersistedCollectionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.collections.WTCollection;
import wt.fc.collections.WTSet;
import wt.iba.value.IBAHolder;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.State;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.QueueEntryInfo;
import wt.queue.QueueHelper;
import wt.queue.QueueManager;
import wt.services.StandardManager;
import wt.tools.support.qAnalyzer.QueueAnalyzer;
import wt.util.WTException;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.baseline.ManagedBaseline;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class StandardBomSearchService extends StandardManager implements BomSearchService {

	public static StandardBomSearchService newStandardBomSearchService() throws Exception {
		final StandardBomSearchService instance = new StandardBomSearchService();
		instance.initialize();
		return instance;
	}
	
	private View getView() throws WTException {
		return ViewHelper.service.getView("Design");
	}
	
	@Override
	public List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline) throws Exception{
	
		return getBOM(part, desc, baseline, true);
	}
	
	@Override
	public List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline,boolean isTop) throws Exception{
	
		return getBOM(part, desc, baseline,isTop,"0");
	}
	
	

	@Override
	public List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline,boolean isTop,String parentId) throws Exception{
		List<PartTreeData> dataList = new ArrayList<PartTreeData>();
		return getBOM(part, desc, baseline, false,dataList,isTop,parentId);
	}
	
	@Override
	public List<PartTreeData> getBOM(WTPart part, boolean desc, Baseline baseline,boolean isALL,List<PartTreeData> dataList,boolean isTop,String rowID) throws Exception{
		
		if(dataList == null){
			dataList = new ArrayList<PartTreeData>();
		}///
		String parentOid = CommonUtil.getOIDString(part);
		PartTreeData parentdata = null;
		if(isTop){
			parentdata = new PartTreeData(part, null, 0,"0");
			rowID = "0"+"#"+parentOid;
			//dataList.add(data);
		}
		
		
		View view = getView();
		List<Object[]> list= new ArrayList<Object[]>();
		
		if (desc) {
			if (baseline == null) {
				list = descentLastPart(part, view, null);
			} else {
				list = descentLastPart(part, baseline, null);
			}
		} else {
			if (baseline == null) {
				list = ancestorPart(part, view, null);
			} else {
				list = ancestorPart(part, baseline, null);
			}
		}
		
		//int idx = Integer.parseInt(parentId);
		
		
		for(Object[] ob : list){
			
			WTPartUsageLink link = (WTPartUsageLink) ob[0];
			WTPart childPart = (WTPart) ob[1];
			
			if(isALL){
				getBOM(childPart,desc, baseline,isALL,dataList,isTop,rowID);
						
			}else{
				PartTreeData childData = new PartTreeData(childPart,link,1,rowID);
				//boolean isChildren = isChildren(childPart, desc, baseline);
				//childData.setChildren(isChildren);
				dataList.add(childData);
			}
			
		}
		
		
		Collections.sort(dataList, new ObjectComarator());
		if(isTop){
			dataList.add(0, parentdata);
		}
		
		
		
		
		return dataList;
		
	}
	/*
	@Override
	public List<PartTreeData> getNextBOM(WTPart part, boolean desc, Baseline baseline,boolean isALL,List<PartTreeData> dataList) throws Exception{
		
		if(dataList == null){
			dataList = new ArrayList<PartTreeData>();
		}
		
		
		String parentOid = CommonUtil.getOIDString(part);
		
		View view = getView();
		List<Object[]> list= new ArrayList<Object[]>();
		
		if (desc) {
			if (baseline == null) {
				list = descentLastPart(part, view, null);
			} else {
				list = descentLastPart(part, baseline, null);
			}
		} else {
			if (baseline == null) {
				list = ancestorPart(part, view, null);
			} else {
				list = ancestorPart(part, baseline, null);
			}
		}
		
		for(Object[] ob : list){
			
			WTPartUsageLink link = (WTPartUsageLink) ob[0];
			WTPart childPart = (WTPart) ob[1];
			
			if(isALL){
				getBOM(childPart,desc, baseline,isALL,dataList,"");
						
			}else{
				PartTreeData childData = new PartTreeData(childPart,link,1,parentOid);
				
				dataList.add(childData);
			}
			
		}
		
		return dataList;
		
	}
	*/
	/**
	 * 정전개 1Level Design View BOM
	 * @param part
	 * @param view
	 * @param state
	 * @return
	 * @throws WTException
	 */
	@Override
	public  List<Object[]> descentLastPart(WTPart part, View view, State state)
			throws WTException {
		List<Object[]> v= new ArrayList<Object[]>();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec
							.newWTPartStandardConfigSpec(getView(), null));
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
	
	/**
	 * 정전객 1Level Baselin BOM
	 * @param part
	 * @param baseline
	 * @param state
	 * @return
	 * @throws WTException
	 */
	@Override
	public  List<Object[]>  descentLastPart(WTPart part, Baseline baseline, State state)
			throws WTException {
		List<Object[]> v= new ArrayList<Object[]>();
		if (!PersistenceHelper.isPersistent(part))
			return v;
		try {
			WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec
					.newWTPartBaselineConfigSpec(baseline);
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
	
	/**
	 * 역전개 1Level Design View BOM
	 * @param part
	 * @param view
	 * @param state
	 * @return
	 * @throws WTException
	 */
	@Override
	public List<Object[]> ancestorPart(WTPart part, View view, State state)
			throws WTException {
		List<Object[]> v= new ArrayList<Object[]>();
		try {
			WTPartMaster master = (WTPartMaster) part.getMaster();
			QuerySpec qs = new QuerySpec();
			int index1 = qs.addClassList(WTPartUsageLink.class, true);
			int index2 = qs.addClassList(WTPart.class, true);
			qs.appendWhere(new SearchCondition(WTPartUsageLink.class,
					"roleBObjectRef.key.id", "=", master.getPersistInfo()
							.getObjectIdentifier().getId()),
					new int[] { index1 });
			SearchCondition sc = new SearchCondition(new ClassAttribute(
					WTPartUsageLink.class, "roleAObjectRef.key.id"), "=",
					new ClassAttribute(WTPart.class,
							"thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { index1, index2 }, 0);
			sc.setOuterJoin(0);
			qs.appendAnd();
			qs.appendWhere(sc, new int[] { index1, index2 });
			qs.appendAnd();
			qs.appendWhere(new SearchCondition(WTPart.class,
					"iterationInfo.latest", SearchCondition.IS_TRUE, true),
					new int[] { index2 });
			if (view != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "view.key.id",
						"=", view.getPersistInfo().getObjectIdentifier()
								.getId()), new int[] { index2 });
			}
			if (state != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "state.state",
						"=", state.toString()), new int[] { index2 });
			}

			SearchUtil.addLastVersionCondition(qs, WTPart.class, index2);

			ClassInfo classinfo = WTIntrospector.getClassInfo(WTPart.class);
			PropertyDescriptor dd = classinfo.getPropertyDescriptor("number");
			
			
			/*
			ClassAttribute classattribute = new ClassAttribute(WTPart.class, (String) dd.getValue("QueryName"));
			classattribute.setColumnAlias("wtsort" + String.valueOf(0));
			
			qs.appendSelect(classattribute, index2, false);
			OrderBy orderby = new OrderBy(classattribute, false, null);
			qs.appendOrderBy(orderby, index2);
			*/
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,"master>number"), true), new int[] { index2 });
			
			//System.out.println("###	qs111111	==	"+qs);
			
			QueryResult re = PersistenceHelper.manager.find(qs);
			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();
				v.add(oo);
			}
		} catch (Exception ex) {
			throw new WTException();
		}
		return v;
	}
	
	/**
	 * 역전개 1Level Baselin BOM
	 * @param part
	 * @param baseline
	 * @param state
	 * @return
	 * @throws WTException
	 */
	@Override
	public List<Object[]> ancestorPart(WTPart part, Baseline baseline, State state)
			throws WTException {
		List<Object[]> v= new ArrayList<Object[]>();
		try {
			WTPartMaster master = (WTPartMaster) part.getMaster();
			QuerySpec qs = new QuerySpec();
			int index1 = qs.addClassList(WTPartUsageLink.class, true);
			int index2 = qs.addClassList(WTPart.class, true);
			qs.appendWhere(new SearchCondition(WTPartUsageLink.class,
					"roleBObjectRef.key.id", "=", master.getPersistInfo()
							.getObjectIdentifier().getId()),
					new int[] { index1 });
			SearchCondition sc = new SearchCondition(new ClassAttribute(
					WTPartUsageLink.class, "roleAObjectRef.key.id"), "=",
					new ClassAttribute(WTPart.class,
							"thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { index1, index2 }, 0);
			sc.setOuterJoin(0);
			qs.appendAnd();
			qs.appendWhere(sc, new int[] { index1, index2 });

			if (state != null) {
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class, "state.state",
						"=", state.toString()), new int[] { index2 });
			}

			if (baseline != null) {
				int index3 = qs.addClassList(BaselineMember.class, false);
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(WTPart.class,
						"thePersistInfo.theObjectIdentifier.id",
						BaselineMember.class, "roleBObjectRef.key.id"),
						new int[] { index2, index3 });
				qs.appendAnd();
				qs.appendWhere(new SearchCondition(BaselineMember.class,
						"roleAObjectRef.key.id", "=", baseline.getPersistInfo()
								.getObjectIdentifier().getId()),
						new int[] { index3 });
			}
			
			/*
			ClassInfo classinfo = WTIntrospector.getClassInfo(WTPart.class);
			PropertyDescriptor dd = classinfo.getPropertyDescriptor("number");
			ClassAttribute classattribute = new ClassAttribute(WTPart.class, (String) dd.getValue("QueryName"));
			classattribute.setColumnAlias("wtsort" + String.valueOf(0));
			qs.appendSelect(classattribute, index2, false);
			OrderBy orderby = new OrderBy(classattribute, false, null);
			qs.appendOrderBy(orderby, index2);
			*/
			
			qs.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class,"master>number"), true), new int[] { index2 });
			
			
			QueryResult re = PersistenceHelper.manager.find(qs);
			
			
			
			
			while (re.hasMoreElements()) {
				Object oo[] = (Object[]) re.nextElement();
				v.add(oo);
			}
		} catch (Exception ex) {
			throw new WTException();
		}
		return v;
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

	   	PartData data = new PartData(child, baseLine,isCheckDummy, desc);
	   	String number = data.getNumber();
		if(parent != null){
	   		 pPart = parent;
	   		parentOid = CommonUtil.getOIDString(pPart);
   			PartData parentData = new PartData(pPart, baseLine,isCheckDummy, desc);
   			
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
	   	String cOid = data.oid;
	   	int level = childLevel;
	   	String dwgNo = data.getDwgNo();
	   	String name  = data.getName();
	   	String[]  lineStack = new String[50];
	   	String rev =  data.version + "." + data.iteration;
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
	   	if(null==pID) id = ""+CommonUtil.getOIDLongValue(data.oid);
	   	else
	 	id =pID+"_"+CommonUtil.getOIDLongValue(data.oid);
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
		if(  count==0){
			map.put("children", new ArrayList<Map<String,Object>>());
		}
		//"children" : [] // children 에 빈배열을 삽입하면, Leaf 로 인식하여 lazyLoading 하지 않게 함.
		
	   	return map;
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
			line += "<img src='/Windchill/jsp/part/images/tree/" + empty + ".gif'></img>";
	    }
	    if(child.level>0){
		    if("join".equals(child.lineImg)){lineStack[child.level]="line";}
		    else lineStack[child.level] = "empty";
		    
		    lineImg += "<img src='/Windchill/jsp/part/images/tree/" + child.lineImg + ".gif' border=0></img>";
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
	/**
     * List<Map>을 jsonArray로 변환한다.
     *
     * @param list List<Map<String, Object>>.
     * @return JSONArray.
     */
    public  JSONArray getJsonArrayFromList( List<Map<String, Object>> list )
    {
        JSONArray jsonArray = new JSONArray();
        for( Map<String, Object> map : list ) {
            jsonArray.put(map);
        }
        
        return jsonArray;
    }
    
    /**
     * List<Map>을 jsonString으로 변환한다.
     *
     * @param list List<Map<String, Object>>.
     * @return String.
     */
    public  String getJsonStringFromList( List<Map<String, Object>> list )
    {
        JSONArray jsonArray = getJsonArrayFromList( list );
        return jsonArray.toJSONString();
    }
	/**
	 * 일괄 수정 BOM
	 */
	@Override
	public List<Map<String,Object>> updateAUIBomListGrid(String oid,boolean isCheckDummy) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart)rf.getReference(oid).getObject();
		
		View[] views = ViewHelper.service.getAllViews();

		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		Vector tempVec = new Vector();
		PartTreeData root = broker.getTree(part , !"false".equals(null),null , ViewHelper.service.getView(views[0].getName()));
		tempVec.add(part.getNumber());
		root.setLocationOid("T"+0);
		Map<String, Object> map1 = setUpdateBOMDate(null,root,0);
		list.add(map1);
		
		int idx =1;
		
		updateAUITreeSetting(root, list, idx,isCheckDummy,tempVec);
		int seq =1;
		for(Map<String, Object>  mapData : list ){
			mapData.put("seq", seq);
			seq++;
		}
		
		
		return list;
		
	}
	
	/**
	 * 일괄 수정 BOM
	 * @param parent
	 * @param list
	 * @param idx
	 * @param isCheckDummy
	 * @throws Exception
	 */
	private int updateAUITreeSetting(PartTreeData parent, List<Map<String, Object>> list , int idx,boolean isCheckDummy,Vector tempVec) throws Exception{
		   
		ArrayList<PartTreeData> childList = parent.children;
		int count = parent.children.size();
		
	    for(PartTreeData child : childList){
	    	child.setLocationOid("T"+idx);
	    	//System.out.println("part = " + child.number);
	    	//중복 제외
	    	if(tempVec.contains(child.number)){
	    		continue;
	    	}
	    	tempVec.add(child.number);
	    	if(isCheckDummy){
	    		boolean isChange = (PartUtil.isChange(parent.number) || PartUtil.isChange(child.number) );
		    	if( isChange  ){
					continue;
				}
	    	}
	    	
	    	Map<String, Object> map = setUpdateBOMDate(parent,child, idx);
			list.add(map);
			idx++;
			idx=updateAUITreeSetting(child, list, idx,isCheckDummy,tempVec);
	         //partDhtmlXTreeSetting(child, childMap, rowNum2);
	    }
	    return idx;
	}
	private int setExpandAUITreeSetting(WTPart rootPart,PartTreeData parent, List<Map<String, Object>> list , int idx,String moduleType,Vector tempVec) throws Exception{
		   
		ArrayList<PartTreeData> childList = parent.children;
		int count = parent.children.size();
		
	    for(PartTreeData child : childList){
	    	child.setLocationOid("T"+idx);
	    	//System.out.println("part = " + child.number);
	    	//중복 제외
	    	if(tempVec.contains(child.number)){
	    		continue;
	    	}
	    	tempVec.add(child.number);
	    	
	    	Map<String, Object> map = setExpandAUIBOMDate(rootPart,parent, moduleType, child, idx);
			list.add(map);
			idx++;
			idx=setExpandAUITreeSetting(rootPart,child, list, idx,moduleType,tempVec);
	         //partDhtmlXTreeSetting(child, childMap, rowNum2);
	    }
	    return idx;
	}
	/***
	 * BOM 일괄 수정용
	 * @param parent
	 * @param child
	 * @param idx
	 * @param manuFactureMap
	 * @param productMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> setUpdateBOMDate(PartTreeData parent,PartTreeData child,int idx) throws Exception{
		  Map<String, Object> map = new HashMap<String, Object>();
		
	   	  
	   	WTPart pPart = null;
	   	String pOid = "";
	   	String pNumber = "";
	   	WTPart cPart = child.part;
	   
	   	if(parent != null){
	   		
	   		 pPart = parent.part;
	   		 pOid  = parent.getLocationOid();//CommonUtil.getOIDString(pPart);
	   		 pNumber = pPart.getNumber();
	   	}
	   	String number = cPart.getNumber();
	   	String cOid = CommonUtil.getOIDString(cPart);
	   	int level = child.level;
	   	String dwgNo = child.getDwgNo();
	   	String name  = cPart.getName();
	   	String rev =  child.version + "." + child.iteration;
	   
	   	double quantity = child.quantity;
	   	
	   	
	   	String state = cPart.getLifeCycleState().getDisplay();
	   	
	   
	   	String dwgOid = child.dwgOid;
	   	String modifier = child.part.getModifierFullName();
	   
	   	
	   	String model = StringUtil.checkNull(child.model);
	   	String productmethod = StringUtil.checkNull(child.productmethod);
		String deptcode = StringUtil.checkNull(child.deptcode);
	    String unit = StringUtil.checkNull(child.unit);
		String manufacture = StringUtil.checkNull(child.manufacture);
		String mat = StringUtil.checkNull(child.mat);
		String finish = StringUtil.checkNull(child.finish);
		String remark = StringUtil.checkNull(child.remark);
		String weight = StringUtil.checkNull(child.weight);
		String specification = StringUtil.checkNull(child.specification);
		
	   	String id ="T"+idx;
	   	map.put("rowId", idx);
	   	map.put("id", id);
	   	map.put("oid", cOid);
	    map.put("dwgOid",	dwgOid);
	    map.put("parent", pOid);
	   	map.put("number", number);
	    map.put("level", level);
	   	map.put("dwgNo", dwgNo);
	   	map.put("name", name);
	   	map.put("rev", rev);
	   	map.put("modifier", modifier);
	   
	   	map.put("state", state);
	   	map.put("quantity", quantity);
		
	   	map.put("model", model);
	   	map.put("productmethod", productmethod);
	   	map.put("deptcode", deptcode);
		map.put("unit", unit);
		map.put("manufacture", manufacture);
		map.put("mat", mat);
		map.put("finish", finish);
		map.put("remark", remark);
		map.put("weight", weight);
		map.put("specification", specification); //
		
		
		
	   	return map;
	}
	/**
	 * 일괄 채번 수정 BOM
	 * @param parent
	 * @param list
	 * @param idx
	 * @param isCheckDummy
	 * @throws Exception
	 */
	private int updateAUIPartNumberTreeSetting(PartTreeData parent, List<Map<String, Object>> list , int idx,boolean isCheckDummy,Vector tempVec) throws Exception{
		   
		ArrayList<PartTreeData> childList = parent.children;
		int count = parent.children.size();
		
	    for(PartTreeData child : childList){
	    	child.setLocationOid("T"+idx);
	    	//System.out.println("part = " + child.number);
	    	//중복 제외
	    	if(tempVec.contains(child.number)){
	    		continue;
	    	}
	    	tempVec.add(child.number);
	    	if(isCheckDummy){
	    		boolean isChange = (PartUtil.isChange(parent.number) || PartUtil.isChange(child.number) );
		    	if( isChange  ){
					continue;
				}
	    	}
	    	
	    	Map<String, Object> map = setUpdatePattChangeDate(parent,child, idx);
			list.add(map);
			idx++;
			idx=updateAUIPartNumberTreeSetting(child, list, idx,isCheckDummy,tempVec);
	         //partDhtmlXTreeSetting(child, childMap, rowNum2);
	    }
	    return idx;
	}
	private Map<String, Object> setExpandAUIBOMDate(WTPart rootPart,PartTreeData parent, String moduleType,PartTreeData child,int idx) throws Exception{
		  Map<String, Object> map = new HashMap<String, Object>();
		
	   	  
	   	WTPart pPart = null;
	   	String pOid = "";
	   	String pNumber = "";
	   	WTPart cPart = child.part;
	   
	   	if(parent != null){
	   		
	   		 pPart = parent.part;
	   		 pOid  = parent.getLocationOid();//CommonUtil.getOIDString(pPart);
	   		 pNumber = pPart.getNumber();
	   	}
	   	String number = cPart.getNumber();
	   	String cOid = CommonUtil.getOIDString(cPart);
	   	int level = child.level;
	   	String dwgNo = child.getDwgNo();
	   	String name  = cPart.getName();
	   	String rev =  child.version + "." + child.iteration;
	   
	   	double quantity = child.quantity;
	   	
	   	
	   	String state = cPart.getLifeCycleState().getDisplay();
	   	
	   
	   	String dwgOid = child.dwgOid;
	   	String modifier = child.part.getModifierFullName();
	   
	   	
	   	String model = StringUtil.checkNull(child.model);
	   	String productmethod = StringUtil.checkNull(child.productmethod);
		String deptcode = StringUtil.checkNull(child.deptcode);
	    String unit = StringUtil.checkNull(child.unit);
		String manufacture = StringUtil.checkNull(child.manufacture);
		String mat = StringUtil.checkNull(child.mat);
		String finish = StringUtil.checkNull(child.finish);
		String remark = StringUtil.checkNull(child.remark);
		String weight = StringUtil.checkNull(child.weight);
		String specification = StringUtil.checkNull(child.specification);
		boolean isSelect = true;
		
		if("ECO".equals(moduleType) || "EO".equals(moduleType)){
			isSelect = PartSearchHelper.service.isSelectEO(cPart,moduleType);
		}
		if(rootPart.getNumber().equals(cPart.getNumber())){
			isSelect = false;
		}
	   	String id ="T"+idx;
	   	
		String lineImg = "";
	   	String line = "";
	   	String[]  lineStack = new String[50];
	   	for(int j=1; j< child.level; j++){
	    	
			String empty = lineStack[j];
			if(empty==null){empty="empty";}
			line += "<img src='/Windchill/jsp/part/images/tree/" + empty + ".gif'></img>";
	    }
	    if(child.level>0){
		    if("join".equals(child.lineImg)){lineStack[child.level]="line";}
		    else lineStack[child.level] = "empty";
		    
		    lineImg += "<img src='/Windchill/jsp/part/images/tree/" + child.lineImg + ".gif' border=0></img>";
	    }
	    //System.out.println(line +","+lineImg+","+pOid);
	    map.put("line", line);
	    map.put("lineImg", lineImg);
	    String icon = CommonUtil.getObjectIconImageTag(child.part);
	 	map.put("icon", icon);
	   	map.put("rowId", idx);
		map.put("isSelect", isSelect);
	   	map.put("id", id);
	   	map.put("oid", cOid);
	    map.put("dwgOid",	dwgOid);
	    map.put("parent", pOid);
	   	map.put("number", number);
	    map.put("level", level);
	   	map.put("dwgNo", dwgNo);
	   	map.put("name", name);
	   	map.put("rev", rev);
	   	map.put("modifier", modifier);
	   //System.out.println("cOid="+cOid+"\tnumber="+number+"\trev="+rev);
	   	map.put("state", state);
	   	map.put("quantity", quantity);
		
	   	map.put("model", model);
	   	map.put("productmethod", productmethod);
	   	map.put("deptcode", deptcode);
		map.put("unit", unit);
		map.put("manufacture", manufacture);
		map.put("mat", mat);
		map.put("finish", finish);
		map.put("remark", remark);
		map.put("weight", weight);
		map.put("specification", specification); //
		
		
		
	   	return map;
	}
	public List<Map<String,Object>> partExpandAUIBomListGrid(String oid,String moduleTypes, String desc) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart)rf.getReference(oid).getObject();
		
		View[] views = ViewHelper.service.getAllViews();

		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		Vector tempVec = new Vector();
		PartTreeData root = broker.getTree(part , !"false".equals(desc),null , ViewHelper.service.getView(views[0].getName()));
		tempVec.add(part.getNumber());
		root.setLocationOid("T"+0);
		Map<String, Object> map1 = setExpandAUIBOMDate(part,null, moduleTypes,root,0);
		list.add(map1);
		
		int idx =1;
		
		setExpandAUITreeSetting(part,root, list,idx,moduleTypes,tempVec);
		int seq =1;
		for(Map<String, Object>  mapData : list ){
			mapData.put("seq", seq);
			seq++;
		}
		
		
		return list;
		
	}
	
	/***
	 * BOM 일괄 채번 수정용
	 * @param parent
	 * @param child
	 * @param idx
	 * @param manuFactureMap
	 * @param productMap
	 * @return
	 * @throws Exception
	 */
	private Map<String, Object> setUpdatePattChangeDate(PartTreeData parent,PartTreeData child,int idx) throws Exception{
		  Map<String, Object> map = new HashMap<String, Object>();
		
	   	  
	   	WTPart pPart = null;
	   	String pOid = "";
	   	String pNumber = "";
	   	WTPart cPart = child.part;
	   
	   	if(parent != null){
	   		
	   		 pPart = parent.part;
	   		 pOid  = parent.getLocationOid();//CommonUtil.getOIDString(pPart);
	   		 pNumber = pPart.getNumber();
	   	}
	   	String number = cPart.getNumber();
	   	String cOid = CommonUtil.getOIDString(cPart);
	   	int level = child.level;
	   	String dwgNo = child.getDwgNo();
	   	String name  = cPart.getName();
	   	String rev =  child.version + "." + child.iteration;
	   	String dwgOid = child.dwgOid;
	  	String checked ="";
	 	String disabled ="";
	 	//boolean isCheckPartType = false;
	 	boolean isDisablePartType = false;
	 	String isCheckPartType = "불가능";
	 	String state = cPart.getLifeCycleState().getDisplay();
	 	PartData partData = new PartData(cPart);
	 	map.put("partName1", partData.getPartName(1));
	 	map.put("partName2", partData.getPartName(2));
	 	map.put("partName3", partData.getPartName(3));
	 	map.put("partName4", partData.getPartName(4));
	 	if(!child.isChange()){
	 		isDisablePartType = true;
	 		disabled ="disabled";
	 	}else{
	 		//isCheckPartType =true;
	 		isCheckPartType = "가능";
	 		checked = "checked";
	 	}
	 	map.put("isCheckPartType", isCheckPartType);
	 	//System.out.println("number="+number+"\tisCheckPartType="+isCheckPartType);
	 	map.put("isDisablePartType", isDisablePartType);
	 	map.put("disabled", disabled);
	 	map.put("checked", checked);
	   	String id ="T"+idx;
	   	map.put("rowId", idx);
	   	map.put("id", id);
	   	map.put("oid", cOid);
		map.put("oidL", CommonUtil.getOIDLongValue(cOid));
	    map.put("dwgOid",	dwgOid);
	    map.put("parent", pOid);
	   	map.put("number", number);
	    map.put("level", level);
	    map.put("partState", state);
	   	map.put("dwgNo", dwgNo);
	   	map.put("name", name);
	   	map.put("rev", rev);
	   	return map;
	}
	
	@Override
	public boolean isChildren(WTPart part,boolean desc,Baseline baseline) throws Exception{
		
		List<Object[]> list= new ArrayList<Object[]>();
		if (desc) {
			if (baseline == null) {
				list = descentLastPart(part, getView(), null);
			} else {
				list = descentLastPart(part, baseline, null);
			}
		} else {
			if (baseline == null) {
				list = ancestorPart(part, getView(), null);
			} else {
				list = ancestorPart(part, baseline, null);
			}
		}
		boolean isChilder = true;
		if(list.size()==0){
			isChilder = false;
		}
		
		return isChilder;
	}
	
	public List<PartTreeData> sort(List<PartTreeData> list) {

		List<PartTreeData> temp = new ArrayList<PartTreeData>();

		
		for(PartTreeData data : list){
			
			boolean flag2 = true;

			for (int l = 0; l < temp.size(); l++) {

				PartTreeData ptd = (PartTreeData) temp.get(l);

				if (data.itemSeq.compareTo(ptd.itemSeq) < 0) {
					temp.add(l, ptd);
					flag2 = false;
					break;
				}
			}
			
			if (flag2) {
				temp.add(data);
			}
		}

		return list;
	}

	@Override
	public List<Map<String, Object>> updateAUIPartChangeListGrid(String oid,
			boolean isCheckDummy) throws Exception {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		
		
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart)rf.getReference(oid).getObject();
		
		View[] views = ViewHelper.service.getAllViews();

		ArrayList result = new ArrayList();

		BomBroker broker = new BomBroker();
		Vector tempVec = new Vector();
		PartTreeData root = broker.getTree(part , !"false".equals(null),null , ViewHelper.service.getView(views[0].getName()));
		tempVec.add(part.getNumber());
		root.setLocationOid("T"+0);
		Map<String, Object> map1 = setUpdatePattChangeDate(null,root,0);
		list.add(map1);
		
		int idx =1;
		
		updateAUIPartNumberTreeSetting(root, list, idx,isCheckDummy,tempVec);
		int seq =1;
		for(Map<String, Object>  mapData : list ){
			mapData.put("seq", seq);
			seq++;
		}
		
		
		return list;	}

	@Override
	public List<Map<String, Object>> getAUIBOMRootChildAction(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		String oid = request.getParameter("oid");
		String view = request.getParameter("view");
		String desc = request.getParameter("desc");
		String baseline = request.getParameter("baseline");
		String checkDummy = request.getParameter("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		//System.out.println("getAUIPartTreeAction checkDummy = "+ checkDummy);
		//System.out.println("desc =" + desc);
		//System.out.println("baseline =" + baseline);
		//System.out.println("oid =" + oid);
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		Baseline bsobj = null;
		if (baseline != null && baseline.length() > 0) {
			bsobj = (Baseline) rf.getReference(baseline).getObject();
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
	
	@Override
	public List<Map<String, Object>> getAUIBOMPartChildAction(
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String oid = request.getParameter("callOid");
		String view = request.getParameter("view");
		String desc = request.getParameter("desc");
		String baseline = request.getParameter("baseline");
		String callLevel = request.getParameter("callLevel");
		String callPOid = request.getParameter("callPOid");
		String pID = request.getParameter("pID");
		
		WTPart parent = null;
		int lvl = 0;
		if(null!=callLevel && callLevel.length()>0)
			lvl = Integer.parseInt(callLevel);
		if(null!=callPOid &&  callPOid.length()>0){
			parent = (WTPart) CommonUtil.getObject(callPOid);
		}
			 
		String checkDummy = request.getParameter("checkDummy");
		boolean isCheckDummy = "true".equals(checkDummy) ? true : false;
		//System.out.println("getAUIPartTreeAction checkDummy = "+ checkDummy);
		//System.out.println("desc =" + desc);
		//System.out.println("baseline =" + baseline);
		//System.out.println("oid =" + oid);
		//System.out.println("callLevel =" + callLevel);
		//System.out.println("checkDummy =" + checkDummy);
		//System.out.println("lvl =" + lvl);
		//System.out.println("callPOid =" + callPOid);
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part = (WTPart) rf.getReference(oid).getObject();
		Baseline bsobj = null;
		if (baseline != null && baseline.length() > 0) {
			bsobj = (Baseline) rf.getReference(baseline).getObject();
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

		View[] views = ViewHelper.service.getAllViews();

		if(view == null){
			view = views[0].getName();
		}
		//MANUFACTURE
		HashMap<String, String> manuFactureMap = CodeHelper.service.getCodeMap("MANUFACTURE");
						
		//PRODUCTMETHOD
		HashMap<String, String> productMap= CodeHelper.service.getCodeMap("PRODUCTMETHOD");
		
		HashMap<String, String> departMap= CodeHelper.service.getCodeMap("DEPTCODE");
		HashMap<String, HashMap<String, String>> codeMap = new HashMap<String, HashMap<String,String>>();
		codeMap.put("manuFactureMap", manuFactureMap);
		codeMap.put("productMap", productMap);
		codeMap.put("departMap", departMap);
		boolean isDesc = !"false".equals(desc);
		/*Map<String, Object> map1 = setBoMDate2(parent, "T"+(lvl-1), part, bsobj, views[0], lvl, lvl, codeMap, isCheckDummy, isDesc);
				//Map<String, Object> map1 = setBoMDate2(null, "T0", part,bsobj,views[0], 0, 0, codeMap, isCheckDummy,isDesc); root
		list.add(map1);*/
		partAUITreeSetting2(list,parent, pID, part, bsobj, views[0], lvl, codeMap, isCheckDummy, isDesc);
		return list;
	}

	private void partAUITreeSetting2(List<Map<String, Object>> list,
			WTPart parent, String pID, WTPart child, Baseline bsobj,
			View view, int lvl, 
			HashMap<String, HashMap<String, String>> codeMap,
			boolean isCheckDummy, boolean isDesc) throws Exception {
		PartData data = new PartData(child,bsobj,isCheckDummy,isDesc);
		ArrayList<Object[]> slist = null;
		if(isDesc)
			slist = data.getDescPartlist();
		else
			slist = data.getAscPartlist();
		//System.out.println("number="+child.getNumber()+"\tsize="+slist.size());
		for(Object[] obj : slist){
	    	Map<String, Object> map = setBoMDate2(child, pID, (WTPart)obj[1], bsobj, view, lvl,  codeMap, isCheckDummy, isDesc);
			list.add(map);
	    }
	}

}
