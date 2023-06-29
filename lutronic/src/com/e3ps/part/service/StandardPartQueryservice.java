package com.e3ps.part.service;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import wt.clients.folder.FolderTaskLogic;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pds.DatabaseInfoUtilities;
import wt.pds.WhereCondition;
import wt.query.ClassAttribute;
import wt.query.CompositeWhereExpression;
import wt.query.ConstantExpression;
import wt.query.KeywordExpression;
import wt.query.LogicalOperator;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.services.StandardManager;
import wt.vc.VersionControlHelper;

import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.groupware.workprocess.WFItemUserLink;
import com.e3ps.org.People;


@SuppressWarnings("serial")
public class StandardPartQueryservice extends StandardManager implements PartQueryservice {
	
	public static StandardPartQueryservice newStandardPartQueryservice() throws Exception {
		final StandardPartQueryservice instance = new StandardPartQueryservice();
		instance.initialize();
		return instance;
	}
	
	@Override
	public QuerySpec listPartSearchQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);
		ReferenceFactory rf = new ReferenceFactory();

		try {
			
			String foid = StringUtil.checkNull(request.getParameter("fid"));
			String islastversion = StringUtil.checkNull(request.getParameter("islastversion"));
			
			String partNumber = StringUtil.checkNull(request.getParameter("partNumber"));
			partNumber = partNumber.trim();
			String partName = StringUtil.checkNull(request.getParameter("partName"));
			String predate = StringUtil.checkNull(request.getParameter("predate"));
			String postdate = StringUtil.checkNull(request.getParameter("postdate"));
			String predate_modify = StringUtil.checkNull(request.getParameter("predate_modify"));
			String postdate_modify = StringUtil.checkNull(request.getParameter("postdate_modify"));
			String creator = StringUtil.checkNull(request.getParameter("creator"));
			String state = StringUtil.checkNull(request.getParameter("state"));

			String unit = StringUtil.checkNull(request.getParameter("unit"));

			String model = StringUtil.checkNull(request.getParameter("model"));									// 프로젝트 코드 (NumberCode, IBA)
			String productmethod = StringUtil.checkNull(request.getParameter("productmethod"));					// 제작방법 (NumberCode, IBA) 
			String deptcode = StringUtil.checkNull(request.getParameter("deptcode"));							// 부서 (NumberCode, IBA)
			
			String weight = StringUtil.checkNull(request.getParameter("weight"));								// 무게 (Key IN, IBA)
			String manufacture = StringUtil.checkNull(request.getParameter("manufacture"));						// MANUTACTURE (NumberCode, IBA)
			String mat = StringUtil.checkNull(request.getParameter("mat"));										// 재질 (NumberCode, IBA)
			String finish = StringUtil.checkNull(request.getParameter("finish"));								// 후처리 (NumberCode, IBA)
			String remarks = StringUtil.checkNull(request.getParameter("remarks"));								// 비고 (Key IN, IBA)
			String specification = StringUtil.checkNull(request.getParameter("specification"));					// 사양 (Key IN, iBA)
			String ecoNo = StringUtil.checkNull(request.getParameter("ecoNo"));									// ECO no (Key IN, iBA)
			String eoNo = StringUtil.checkNull(request.getParameter("eoNo"));	
			
			String ecoPostdate = StringUtil.checkNull(request.getParameter("ecoPostdate"));
			String ecoPredate = StringUtil.checkNull(request.getParameter("ecoPredate"));
			String checkDummy = StringUtil.checkNull(request.getParameter("checkDummy"));
			
			//System.out.println("checkDummy = " + checkDummy);
			//배포 관련 추가 
			boolean isProduction =  StringUtil.checkNull(request.getParameter("production")).equals("true") ? true : false;
			boolean ischeckDummy =  StringUtil.checkNull(request.getParameter("checkDummy")).equals("true") ? true : false;
			
			
			String sortValue = StringUtil.checkNull(request.getParameter("sortValue"));
			String sortCheck = StringUtil.checkNull(request.getParameter("sortCheck"));

			String location = StringUtil.checkNull(request.getParameter("location"));
			if (location == null || location.length() == 0) {
				location = "/Default/PART_Drawing";
			}
			if (sortCheck == null) {
				sortCheck = "true";
			}
			
			Folder folder = null;
			if (foid.length() > 0) {
				folder = (Folder) rf.getReference(foid).getObject();
				location = FolderHelper.getFolderPath(folder);
			} else {
				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
				foid = "";
			}
			
			// 최신 이터레이션
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { idx });
			
			// 버전 검색
			if (!StringUtil.checkString(islastversion))
				islastversion = "true";
			
			if ("true".equals(islastversion)) {
				SearchUtil.addLastVersionCondition(query, WTPart.class, idx);
			}
			
			//Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx });
			
			
			if(isProduction){
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "1_________", false), new int[] { idx });
			}
			
			
			
			// 품목코드
			if (partNumber.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "%" + partNumber + "%", false), new int[] { idx });
			}

			// 품명
			if (partName.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>name", SearchCondition.LIKE, "%" + partName + "%", false), new int[] { idx });
			}

			// 등록일
			if (predate.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
			}
			if (postdate.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
			}

			// 수정일
			if (predate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[] { idx });
			}
			if (postdate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[] { idx });
			}

			// 등록자
			if (creator.length() > 0) {
				People people = (People) rf.getReference(creator).getObject();
				WTUser user = people.getUser();
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[] { idx });
			}

			// 상태
			if (state.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "state.state", SearchCondition.EQUAL, state), new int[] { idx });
			}

			
			
			
			// 단위
			if(unit.length() > 0){
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, WTPart.DEFAULT_UNIT, SearchCondition.EQUAL, unit), new int[] {idx});
			}
			
			//EcoDate
			if (ecoPostdate.length() > 0 || ecoPredate.length()>0) {
				//AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_ECODATE);
				
				//if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					//query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				    long did = getECODATESeqDefinitionId();
				    //System.out.println("Long Check !!!!!!!!!!!!!! did = "+ did );
				    SearchCondition sc = new SearchCondition(StringValue.class, "definitionReference.key.id","=",did);
				    query.appendWhere(sc, new int[] { _idx });
					/*if(ecoPredate.length() > 0 ){
						query.appendAnd();
						query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.GREATER_THAN_OR_EQUAL, ecoPredate), new int[] { _idx });
					}
					
					if(ecoPostdate.length() > 0 ){
						query.appendAnd();
						query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LESS_THAN_OR_EQUAL, ecoPostdate), new int[] { _idx });
					}
					SELECT A0.ida2a2,A1.value , SUBSTR(A1.value,INSTR(A1.value,',',-1)+1), INSTR(A1.value,',',-1) FROM WTPart A0,StringValue A1,WTPARTMASTER A2
WHERE (A0.latestiterationInfo = 1) AND (UPPER(A0.statecheckoutInfo) <> 'WRK') AND (A0.statestate = 'APPROVED')  AND (A0.IDA3MASTERREFERENCE=A2.IDA2A2)
AND (A1.idA3A4 = A0.idA2A2) AND (A1.hierarchyIDA6 = '3899120390680943734') AND (SUBSTR(A1.value,INSTR(A1.value,',',-1)+1) >= '2017-07-26') AND (A2.WTPartNumber NOT LIKE '%DEL%')
ORDER BY A0.modifyStampA2 DESC;
					*
					*
					*
					*/
					ClassInfo classinfo = WTIntrospector.getClassInfo(StringValue.class);
					String task_seqColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, StringValue.VALUE);
					RelationalExpression paramRelationalExpression = new KeywordExpression("SUBSTR(" + task_seqColumnName +",INSTR("+task_seqColumnName+ ",',',-1)+1)" ); 
					
					if(ecoPredate.length() > 0 ){
						query.appendAnd();
						RelationalExpression expression = new wt.query.ConstantExpression(ecoPredate);
						SearchCondition searchCondition = new SearchCondition(paramRelationalExpression,SearchCondition.GREATER_THAN_OR_EQUAL,expression);
						query.appendWhere(searchCondition, new int[] { _idx });
					}
					RelationalExpression paramRelationalExpression2 = new KeywordExpression("SUBSTR(" + task_seqColumnName +",INSTR("+task_seqColumnName+ ",',',-1)+1)"); 
					
					if(ecoPostdate.length() > 0 ){
						query.appendAnd();
						RelationalExpression expression = new wt.query.ConstantExpression(ecoPostdate);
						SearchCondition searchCondition2 = new SearchCondition(paramRelationalExpression2,SearchCondition.LESS_THAN_OR_EQUAL,expression);
						query.appendWhere(searchCondition2, new int[] { _idx });
					}
				}
			//} 
			

			// 프로젝트 코드
			if (model.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				deptcode = "";
			}

			// 무게
			if (weight.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_WEIGHT);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + weight + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				weight = "";
			}

			// MANUFACTURE
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + specification + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				specification = "";
			}
			
			// ecoNo
			if (ecoNo.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_CHANGENO);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + ecoNo + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				ecoNo = "";
			}
			// 사양
			if (eoNo.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_ECONO);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + eoNo + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				eoNo = "";
			}
			
			// folder search
			if (!"/Default/PART_Drawing".equals(location)) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}

				int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
				SearchCondition sc1 = new SearchCondition(new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"), SearchCondition.EQUAL, new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
				sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
				sc1.setOuterJoin(0);
				query.appendWhere(sc1, new int[] { folder_idx, idx });

				query.appendAnd();
				ArrayList folders = CommonFolderHelper.service.getFolderTree(folder);
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()), new int[] { folder_idx });

				for (int fi = 0; fi < folders.size(); fi++) {
					String[] s = (String[]) folders.get(fi);
					Folder sf = (Folder) rf.getReference(s[2]).getObject();
					query.appendOr();
					query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()), new int[] { folder_idx });
				}
				query.appendCloseParen();
			} else {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.NOT_LIKE, "%DEL%", false), new int[] { idx });

			}

			// sorting
			if (sortValue.length() > 0) {
				boolean sort = "true".equals(sortCheck);
				if (!"creator".equals(sortValue)) {
					//query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), false), new int[] { idx });
					SearchUtil.setOrderBy(query, WTPart.class, idx, sortValue, "sort", sort);
				} else {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					int idx_user = query.appendClassList(WTUser.class, false);
					int idx_people = query.appendClassList(People.class, false);

					ClassAttribute ca = null;
					ClassAttribute ca2 = null;

					ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
					ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

					SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

					query.appendWhere(sc2, new int[] { idx, idx_user });

					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

					query.appendAnd();
					query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
					SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", sort);
				}
				/*
				if ("true".equals(sortCheck)) {
					if (!"creator".equals(sortValue)) {
						query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), true), new int[] { idx });
					} else {
						if (query.getConditionCount() > 0)
							query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);

						ClassAttribute ca = null;
						ClassAttribute ca2 = null;

						ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
						ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

						SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

						query.appendWhere(sc2, new int[] { idx, idx_user });

						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", true);
					}

				} else {
					if (!"creator".equals(sortValue)) {
						query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), false), new int[] { idx });
					} else {
						if (query.getConditionCount() > 0)
							query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);

						ClassAttribute ca = null;
						ClassAttribute ca2 = null;

						ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
						ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

						SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

						query.appendWhere(sc2, new int[] { idx, idx_user });

						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", false);
					}
				}
				*/
			} else {
				query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, WTPart.MODIFY_TIMESTAMP), true), new int[] { idx });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return query;
	}
	
	public QuerySpec listPartApprovedSearchQuery(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);
		ReferenceFactory rf = new ReferenceFactory();

		try {
			
			String foid = StringUtil.checkNull(request.getParameter("fid"));
			String islastversion = StringUtil.checkNull(request.getParameter("islastversion"));
			
			String partNumber = StringUtil.checkNull(request.getParameter("partNumber"));
			partNumber = partNumber.trim();
			String partName = StringUtil.checkNull(request.getParameter("partName"));
			String predate = StringUtil.checkNull(request.getParameter("predate"));
			String postdate = StringUtil.checkNull(request.getParameter("postdate"));
			String predate_modify = StringUtil.checkNull(request.getParameter("predate_modify"));
			String postdate_modify = StringUtil.checkNull(request.getParameter("postdate_modify"));
			String creator = StringUtil.checkNull(request.getParameter("creator"));
			String state = StringUtil.checkNull(request.getParameter("state"));

			String unit = StringUtil.checkNull(request.getParameter("unit"));

			String model = StringUtil.checkNull(request.getParameter("model"));									// 프로젝트 코드 (NumberCode, IBA)
			String productmethod = StringUtil.checkNull(request.getParameter("productmethod"));					// 제작방법 (NumberCode, IBA) 
			String deptcode = StringUtil.checkNull(request.getParameter("deptcode"));							// 부서 (NumberCode, IBA)
			
			String weight = StringUtil.checkNull(request.getParameter("weight"));								// 무게 (Key IN, IBA)
			String manufacture = StringUtil.checkNull(request.getParameter("manufacture"));						// MANUTACTURE (NumberCode, IBA)
			String mat = StringUtil.checkNull(request.getParameter("mat"));										// 재질 (NumberCode, IBA)
			String finish = StringUtil.checkNull(request.getParameter("finish"));								// 후처리 (NumberCode, IBA)
			String remarks = StringUtil.checkNull(request.getParameter("remarks"));								// 비고 (Key IN, IBA)
			String specification = StringUtil.checkNull(request.getParameter("specification"));					// 사양 (Key IN, iBA)
			String ecoNo = StringUtil.checkNull(request.getParameter("ecoNo"));									// ECO no (Key IN, iBA)
			String eoNo = StringUtil.checkNull(request.getParameter("eoNo"));	
			
			String ecoPostdate = StringUtil.checkNull(request.getParameter("ecoPostdate"));
			String ecoPredate = StringUtil.checkNull(request.getParameter("ecoPredate"));
			String checkDummy = StringUtil.checkNull(request.getParameter("checkDummy"));
			
			//System.out.println("checkDummy = " + checkDummy);
			//배포 관련 추가 
			boolean isProduction =  StringUtil.checkNull(request.getParameter("production")).equals("true") ? true : false;
			boolean ischeckDummy =  StringUtil.checkNull(request.getParameter("checkDummy")).equals("true") ? true : false;
			
			
			String sortValue = StringUtil.checkNull(request.getParameter("sortValue"));
			String sortCheck = StringUtil.checkNull(request.getParameter("sortCheck"));

			String location = StringUtil.checkNull(request.getParameter("location"));
			if (location == null || location.length() == 0) {
				location = "/Default/PART_Drawing";
			}
			if (sortCheck == null) {
				sortCheck = "true";
			}
			
			Folder folder = null;
			if (foid.length() > 0) {
				folder = (Folder) rf.getReference(foid).getObject();
				location = FolderHelper.getFolderPath(folder);
			} else {
				folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());
				foid = "";
			}
			
			// 최신 이터레이션
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { idx });
			
			// 버전 검색
			if (!StringUtil.checkString(islastversion))
				islastversion = "true";
			
			//if ("true".equals(islastversion)) {
				SearchUtil.addLastVersionCondition(query, WTPart.class, idx);
			//}
			
			//Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx });
			
			
			if(isProduction){
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "1_________", false), new int[] { idx });
			}
			
			
			
			// 품목코드
			if (partNumber.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "%" + partNumber + "%", false), new int[] { idx });
			}

			// 품명
			if (partName.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>name", SearchCondition.LIKE, "%" + partName + "%", false), new int[] { idx });
			}

			// 등록일
			if (predate.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate)), new int[] { idx });
			}
			if (postdate.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate)), new int[] { idx });
			}

			// 수정일
			if (predate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(predate_modify)), new int[] { idx });
			}
			if (postdate_modify.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(postdate_modify)), new int[] { idx });
			}

			// 등록자
			if (creator.length() > 0) {
				People people = (People) rf.getReference(creator).getObject();
				WTUser user = people.getUser();
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.creator.key.id", SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[] { idx });
			}

			// 상태
			if (state.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "state.state", SearchCondition.EQUAL, state), new int[] { idx });
			}

			
			
			
			// 단위
			if(unit.length() > 0){
				if(query.getConditionCount() > 0){
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, WTPart.DEFAULT_UNIT, SearchCondition.EQUAL, unit), new int[] {idx});
			}
			
			//EcoDate
			if (ecoPostdate.length() > 0 || ecoPredate.length()>0) {
				//AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_ECODATE);
				
				//if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					//query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
				    long did = getECODATESeqDefinitionId();
				    //System.out.println("Long Check !!!!!!!!!!!!!! did = "+ did );
				    SearchCondition sc = new SearchCondition(StringValue.class, "definitionReference.key.id","=",did);
				    query.appendWhere(sc, new int[] { _idx });
					/*if(ecoPredate.length() > 0 ){
						query.appendAnd();
						query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.GREATER_THAN_OR_EQUAL, ecoPredate), new int[] { _idx });
					}
					
					if(ecoPostdate.length() > 0 ){
						query.appendAnd();
						query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LESS_THAN_OR_EQUAL, ecoPostdate), new int[] { _idx });
					}
					SELECT A0.ida2a2,A1.value , SUBSTR(A1.value,INSTR(A1.value,',',-1)+1), INSTR(A1.value,',',-1) FROM WTPart A0,StringValue A1,WTPARTMASTER A2
WHERE (A0.latestiterationInfo = 1) AND (UPPER(A0.statecheckoutInfo) <> 'WRK') AND (A0.statestate = 'APPROVED')  AND (A0.IDA3MASTERREFERENCE=A2.IDA2A2)
AND (A1.idA3A4 = A0.idA2A2) AND (A1.hierarchyIDA6 = '3899120390680943734') AND (SUBSTR(A1.value,INSTR(A1.value,',',-1)+1) >= '2017-07-26') AND (A2.WTPartNumber NOT LIKE '%DEL%')
ORDER BY A0.modifyStampA2 DESC;
					*
					*
					*
					*/
					ClassInfo classinfo = WTIntrospector.getClassInfo(StringValue.class);
					String task_seqColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, StringValue.VALUE);
					RelationalExpression paramRelationalExpression = new KeywordExpression("SUBSTR(" + task_seqColumnName +",INSTR("+task_seqColumnName+ ",',',-1)+1)" ); 
					
					if(ecoPredate.length() > 0 ){
						query.appendAnd();
						RelationalExpression expression = new wt.query.ConstantExpression(ecoPredate);
						SearchCondition searchCondition = new SearchCondition(paramRelationalExpression,SearchCondition.GREATER_THAN_OR_EQUAL,expression);
						query.appendWhere(searchCondition, new int[] { _idx });
					}
					RelationalExpression paramRelationalExpression2 = new KeywordExpression("SUBSTR(" + task_seqColumnName +",INSTR("+task_seqColumnName+ ",',',-1)+1)"); 
					
					if(ecoPostdate.length() > 0 ){
						query.appendAnd();
						RelationalExpression expression = new wt.query.ConstantExpression(ecoPostdate);
						SearchCondition searchCondition2 = new SearchCondition(paramRelationalExpression2,SearchCondition.LESS_THAN_OR_EQUAL,expression);
						query.appendWhere(searchCondition2, new int[] { _idx });
					}
				}
			//} 
			

			// 프로젝트 코드
			if (model.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				deptcode = "";
			}

			// 무게
			if (weight.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_WEIGHT);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + weight + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				weight = "";
			}

			// MANUFACTURE
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + specification + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				specification = "";
			}
			
			// ecoNo
			if (ecoNo.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_CHANGENO);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + ecoNo + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				ecoNo = "";
			}
			// 사양
			if (eoNo.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_ECONO);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID", SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE, ("%" + eoNo + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				eoNo = "";
			}
			
			// folder search
			if (!"/Default/PART_Drawing".equals(location)) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}

				int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
				SearchCondition sc1 = new SearchCondition(new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"), SearchCondition.EQUAL, new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
				sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
				sc1.setOuterJoin(0);
				query.appendWhere(sc1, new int[] { folder_idx, idx });

				query.appendAnd();
				ArrayList folders = CommonFolderHelper.service.getFolderTree(folder);
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()), new int[] { folder_idx });

				for (int fi = 0; fi < folders.size(); fi++) {
					String[] s = (String[]) folders.get(fi);
					Folder sf = (Folder) rf.getReference(s[2]).getObject();
					query.appendOr();
					query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id", SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()), new int[] { folder_idx });
				}
				query.appendCloseParen();
			} else {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.NOT_LIKE, "%DEL%", false), new int[] { idx });

			}

			// sorting
			if (sortValue.length() > 0) {
				boolean sort = "true".equals(sortCheck);
				if (!"creator".equals(sortValue)) {
					//query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), false), new int[] { idx });
					SearchUtil.setOrderBy(query, WTPart.class, idx, sortValue, "sort", sort);
				} else {
					if (query.getConditionCount() > 0)
						query.appendAnd();
					int idx_user = query.appendClassList(WTUser.class, false);
					int idx_people = query.appendClassList(People.class, false);

					ClassAttribute ca = null;
					ClassAttribute ca2 = null;

					ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
					ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

					SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

					query.appendWhere(sc2, new int[] { idx, idx_user });

					ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

					query.appendAnd();
					query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
					SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", sort);
				}
				/*
				if ("true".equals(sortCheck)) {
					if (!"creator".equals(sortValue)) {
						query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), true), new int[] { idx });
					} else {
						if (query.getConditionCount() > 0)
							query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);

						ClassAttribute ca = null;
						ClassAttribute ca2 = null;

						ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
						ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

						SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

						query.appendWhere(sc2, new int[] { idx, idx_user });

						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", true);
					}

				} else {
					if (!"creator".equals(sortValue)) {
						query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), false), new int[] { idx });
					} else {
						if (query.getConditionCount() > 0)
							query.appendAnd();
						int idx_user = query.appendClassList(WTUser.class, false);
						int idx_people = query.appendClassList(People.class, false);

						ClassAttribute ca = null;
						ClassAttribute ca2 = null;

						ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
						ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

						SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

						query.appendWhere(sc2, new int[] { idx, idx_user });

						ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

						query.appendAnd();
						query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
						SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", false);
					}
				}
				*/
			} else {
				query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, WTPart.MODIFY_TIMESTAMP), true), new int[] { idx });
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(query.toString());
		return query;
	}
	
	 public static long getECODATESeqDefinitionId() {
		 StringDefinition itemSeqDefinition =null;
		    	try{
		    		QuerySpec select = new QuerySpec(StringDefinition.class);
		            select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", "CHANGEDATE"), new int[] { 0 });
		            QueryResult re = PersistenceHelper.manager.find(select);
		            while (re.hasMoreElements())
		            {
		            	itemSeqDefinition = (StringDefinition) re.nextElement();
		            }
		    	}catch(Exception ex){
		    		ex.printStackTrace();
		    	}
	    		return itemSeqDefinition.getPersistInfo().getObjectIdentifier().getId();
	    }
	@Override
	public QuerySpec searchSeqAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);
		ReferenceFactory rf = new ReferenceFactory();

		String partNumber = StringUtil.checkNull(request.getParameter("partNumber"));
		
		String sortValue = StringUtil.checkNull(request.getParameter("sortValue"));
		String sortCheck = StringUtil.checkNull(request.getParameter("sortCheck"));

		String location = StringUtil.checkNull(request.getParameter("location"));
		if (location == null || location.length() == 0) {
			location = "/Default/PART_Drawing";
		}
		if (sortCheck == null) {
			sortCheck = "true";
		}
		
		// 최신 이터레이션
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(VersionControlHelper.getSearchCondition(WTPart.class, true), new int[] { idx });
		
		SearchUtil.addLastVersionCondition(query, WTPart.class, idx);
		
		//Working Copy 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false), new int[] { idx });

		// 품목코드
		if (partNumber.length() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, partNumber + "%", false), new int[] { idx });
		}

		// sorting
		if (sortValue.length() > 0) {
			boolean sort = "true".equals(sortCheck);
			if (!"creator".equals(sortValue)) {
				//query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue), false), new int[] { idx });
				SearchUtil.setOrderBy(query, WTPart.class, idx, sortValue, "sort", sort);
			} else {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				int idx_user = query.appendClassList(WTUser.class, false);
				int idx_people = query.appendClassList(People.class, false);

				ClassAttribute ca = null;
				ClassAttribute ca2 = null;

				ca = new ClassAttribute(WTPart.class, "iterationInfo.creator.key.id");
				ca2 = new ClassAttribute(WTUser.class, "thePersistInfo.theObjectIdentifier.id");

				SearchCondition sc2 = new SearchCondition(ca, "=", ca2);

				query.appendWhere(sc2, new int[] { idx, idx_user });

				ClassAttribute ca3 = new ClassAttribute(People.class, "userReference.key.id");

				query.appendAnd();
				query.appendWhere(new SearchCondition(ca2, "=", ca3), new int[] { idx_user, idx_people });
				SearchUtil.setOrderBy(query, People.class, idx_people, People.NAME, "sort", sort);
			}
		} else {
			query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "thePersistInfo.createStamp"), true), new int[] { idx });
		}

		return query;
	}
	
}
