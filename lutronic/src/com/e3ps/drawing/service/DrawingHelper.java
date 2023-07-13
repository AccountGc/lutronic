package com.e3ps.drawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.drawing.beans.EpmData;
import com.e3ps.org.People;

import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.FloatValue;
import wt.iba.value.StringValue;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;

public class DrawingHelper {
	public static final DrawingService service = ServiceFactory.getService(DrawingService.class);
	public static final DrawingHelper manager = new DrawingHelper();
	public static final String ROOTLOCATION = "/Default/PART_Drawing";
	
	public Map<String, Object> listDrawingAction(HttpServletRequest request) throws Exception{
		
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EPMDocument.class, true);
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<EpmData> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();
		
		try {
			String foid 	        = StringUtil.checkNull(request.getParameter("foid"));	
			String location         = StringUtil.checkNull(request.getParameter("location"));		
			String cadDivision      = StringUtil.checkNull(request.getParameter("cadDivision")); 	
			String cadType          = StringUtil.checkNull(request.getParameter("cadType"));		
			String number           = StringUtil.checkNull(request.getParameter("number"));		
			String name             = StringUtil.checkNull(request.getParameter("name"));		
			String predate 	        = StringUtil.checkNull(request.getParameter("predate"));	
			String postdate         = StringUtil.checkNull(request.getParameter("postdate"));	
			String predate_modify 	= StringUtil.checkNull(request.getParameter("predate_modify"));
			String postdate_modify  = StringUtil.checkNull(request.getParameter("postdate_modify"));
			String creator 		    = StringUtil.checkNull(request.getParameter("creator"));
			String state 	        = StringUtil.checkNull(request.getParameter("state"));	
			String islastversion 	= StringUtil.checkNull(request.getParameter("islastversion"));
			String sortValue 	    = StringUtil.checkNull(request.getParameter("sortValue"));
			String sortCheck 	    = StringUtil.checkNull(request.getParameter("sortCheck"));
			String autoCadLink 	    = StringUtil.checkNull(request.getParameter("autoCadLink"));
			String unit             = StringUtil.checkNull(request.getParameter("unit"));     
			String model            = StringUtil.checkNull(request.getParameter("model"));   
			String productmethod    = StringUtil.checkNull(request.getParameter("productmethod"));
			String deptcode         = StringUtil.checkNull(request.getParameter("deptcode"));
			String weight1           = StringUtil.checkNull(request.getParameter("weight1"));
			String weight2           = StringUtil.checkNull(request.getParameter("weight2"));
			String manufacture      = StringUtil.checkNull(request.getParameter("manufacture"));
			String mat              = StringUtil.checkNull(request.getParameter("mat"));
			String finish           = StringUtil.checkNull(request.getParameter("finish"));
			String remarks          = StringUtil.checkNull(request.getParameter("remarks"));
			String specification    = StringUtil.checkNull(request.getParameter("specification"));

			
			String temp = "";
//			Folder folder = null;
//			if(foid!=null && foid.length() > 0){
//				folder = (Folder)rf.getReference(foid).getObject();
//				location = FolderHelper.getFolderPath( folder );
//				temp = folder.getName();
//			}else{
//				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
//				foid="";
//			}
			
			// 최신 이터레이션
			if(query.getConditionCount() > 0) { query.appendAnd(); }
			query.appendWhere(VersionControlHelper.getSearchCondition(EPMDocument.class, true), new int[]{idx});
			
			// 버전 검색
			if(!StringUtil.checkString(islastversion)) islastversion = "true";
			if("true".equals(islastversion)) {
				SearchUtil.addLastVersionCondition(query, EPMDocument.class, idx);;
			}
			
			//Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(EPMDocument.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx });

			
			//�����ȣ
			if(number.length() > 0){
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>number", SearchCondition.LIKE , "%"+number+"%", false), new int[]{idx});
			}else number = "";
			
			//�����
			if(name.length() > 0){
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>name", SearchCondition.LIKE , "%"+name+"%", false), new int[]{idx});
			}else name = "";	
			
			//�ۼ���
			if(creator.length()>0){
				People people = (People)rf.getReference(creator).getObject();
				WTUser user = people.getUser();
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"iterationInfo.creator.key","=", PersistenceHelper.getObjectIdentifier( user )), new int[]{idx});
			} else creator = "";
			
			//���°˻�
			if(StringUtil.checkString(state)) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class, "state.state" , SearchCondition.EQUAL, state), new int[]{idx});
			}
			
			
			
			//�ۼ����� (predate)
			if(StringUtil.checkString(predate)) {
			    if(query.getConditionCount()>0) { query.appendAnd(); }
			    query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.createStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[]{idx});
			}
			//�ۼ����� (postdate)
			if(StringUtil.checkString(postdate)) {
			    if(query.getConditionCount()>0) { query.appendAnd(); }
			    query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.createStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[]{idx});
			}
			
			//��������
			if(predate_modify.length() > 0) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[]{idx});
			}
			if(postdate_modify.length() > 0) {
				if(query.getConditionCount() > 0) { query.appendAnd(); }
				query.appendWhere(new SearchCondition(EPMDocument.class,"thePersistInfo.modifyStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[]{idx});
			}
			
			// 프로젝트 코드
			if (model.length() > 0) {
				
				
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + model + "%").toUpperCase()), new int[] { _idx });
				}
				
			} else {
				model = "";
			}

			// 제작방법
			if (productmethod.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_PRODUCTMETHOD);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + productmethod + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				productmethod = "";
			}

			// 부서
			if (deptcode.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DEPTCODE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				deptcode = "";
			}

			// 무게
			boolean searchWeight = weight1.length()> 0 || weight2.length() >0;
			//System.out.println("weight1 =" + weight1 +",weight2=" + weight2);
			if (searchWeight) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_WEIGHT);
				if (aview != null) {
					
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					query.appendOpenParen();
					
					//System.out.println("aview.getHierarchyID() =" + aview.getHierarchyID()); 
					int _idx = query.appendClassList(FloatValue.class, false);
					query.appendWhere(new SearchCondition(FloatValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(FloatValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					
					
					if(weight1.length()>0){
						query.appendAnd();
						query.appendWhere(new SearchCondition(FloatValue.class, "value", SearchCondition.GREATER_THAN_OR_EQUAL , Double.parseDouble(weight1)), new int[] { _idx });
					}
					
					if(weight2.length()>0){
						query.appendAnd();
						query.appendWhere(new SearchCondition(FloatValue.class, "value", SearchCondition.LESS_THAN_OR_EQUAL , Double.parseDouble(weight2)), new int[] { _idx });
					}
					query.appendCloseParen();
				}
			} 

			// MANUFACTURE
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + manufacture + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				manufacture = "";
			}

			// 재질
			if (mat.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MAT);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + mat + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				mat = "";
			}

			// 후처리
			if (finish.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_FINISH);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + finish + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				finish = "";
			}

			// 비고
			if (remarks.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_REMARKS);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + remarks + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				remarks = "";
			}

			// 사양
			if (specification.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_SPECIFICATION);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", EPMDocument.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + specification + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				specification = "";
			}	
			
			//Folder Search
//			if (location.length() > 0) {
//				int l = location.indexOf(ROOTLOCATION);
//
//				if (l >= 0) {
//					if (query.getConditionCount() > 0) {
//						query.appendAnd();
//					}
//					location = location
//							.substring((l + ROOTLOCATION.length()));
//					// Folder Search
//					int folder_idx = query.addClassList(EpmLocation.class,
//							false);
//					query.appendWhere(new SearchCondition(EpmLocation.class,
//							EpmLocation.EPM, EPMDocument.class,
//							"thePersistInfo.theObjectIdentifier.id"),
//							new int[] { folder_idx, idx });
//					query.appendAnd();
//
//					query.appendWhere(new SearchCondition(EpmLocation.class,
//							"loc", SearchCondition.LIKE, location + "%"),
//							new int[] { folder_idx });
//				}
//
//			}
			//CAD ����
			if(cadDivision.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , cadDivision, false), new int[]{idx});
			}
			
			//CAD Ÿ��
			if(cadType.length()>0){
				query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , cadType, false), new int[]{idx});
			}
			
			//autoCadLink
			if(autoCadLink.equals("true")){
				if(query.getConditionCount() > 0) query.appendAnd();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>authoringApplication", SearchCondition.EQUAL , "SOLIDWORKS", false), new int[]{idx});
				query.appendAnd();
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , "CADCOMPONENT", false), new int[]{idx});
				query.appendOr();
				query.appendWhere(new SearchCondition(EPMDocument.class,"master>docType", SearchCondition.EQUAL , "CADASSEMBLY", false), new int[]{idx});
				query.appendCloseParen();
			
			}
				
			//����
			if(sortCheck == null) sortCheck = "true";
			if(sortValue != null && sortValue.length() > 0) {
				if("true".equals(sortCheck)){
					if( !"creator".equals(sortValue)){
						query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class,sortValue), true), new int[] { idx });
					}else{
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EPMDocument.class, "iterationInfo.creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , true);
					}
					
				}else{
					if( !"creator".equals(sortValue)){
						query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class,sortValue), false), new int[] { idx });
					}else{
						if(query.getConditionCount() > 0) query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);
						
			            ClassAttribute ca = new ClassAttribute(EPMDocument.class, "iterationInfo.creator.key.id");
			            ClassAttribute ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");
			            query.appendWhere(new SearchCondition(ca, "=", ca2), new int[]{idx, idx_user});
						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");
						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[]{idx_user, idx_people});
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort" , false);
					}
				}
			}else{
				query.appendOrderBy(new OrderBy(new ClassAttribute(EPMDocument.class, EPMDocument.MODIFY_TIMESTAMP), true), new int[] { idx });    
			}
			
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EPMDocument epm = (EPMDocument) obj[0];
				EpmData data = new EpmData(epm);
				list.add(data);
			}

			map.put("list", list);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return map;
	}
}
