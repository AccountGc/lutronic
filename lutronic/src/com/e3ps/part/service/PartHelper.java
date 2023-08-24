package com.e3ps.part.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcrPartLink;
import com.e3ps.change.beans.ECOData;
import com.e3ps.change.beans.EoComparator;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.ResultData;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.CommentsData;
import com.e3ps.common.content.service.CommonContentHelper;
import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.iba.AttributeKey.ECOKey;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.service.CommonHelper;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.SequenceDao;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.development.beans.MasterData;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.org.People;
import com.e3ps.part.beans.ObjectComarator;
import com.e3ps.part.beans.PartData;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.service.RohsHelper;
import com.e3ps.rohs.service.RohsQueryHelper;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.StringValue;
import wt.inf.container.WTContainerRef;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleTemplate;
import wt.org.WTUser;
import wt.part.PartDocHelper;
import wt.part.PartType;
import wt.part.QuantityUnit;
import wt.part.Source;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartMaster;
import wt.pdmlink.PDMLinkProduct;
import wt.pds.DatabaseInfoUtilities;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.KeywordExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;
import wt.vc.views.ViewHelper;

public class PartHelper {
	public static final PartService service = ServiceFactory.getService(PartService.class);
	public static final PartHelper manager = new PartHelper();
	
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<PartData> list = new ArrayList<>();
		Map<String, Object> map = new HashMap<>();

		try {
			
			String foid = StringUtil.checkNull((String)params.get("fid"));
			String islastversion = StringUtil.checkNull((String)params.get("islastversion"));
			
			String partNumber = StringUtil.checkNull((String)params.get("partNumber"));
			partNumber = partNumber.trim();
			String partName = StringUtil.checkNull((String)params.get("partName"));
			String createdFrom = StringUtil.checkNull((String)params.get("createdFrom"));
			String createdTo = StringUtil.checkNull((String)params.get("createdTo"));
			String modifiedFrom = StringUtil.checkNull((String)params.get("modifiedFrom"));
			String modifiedTo = StringUtil.checkNull((String)params.get("modifiedTo"));
			String creator = StringUtil.checkNull((String)params.get("creator"));
			String state = StringUtil.checkNull((String)params.get("state"));

			String unit = StringUtil.checkNull((String)params.get("unit"));

			String model = StringUtil.checkNull((String)params.get("model"));									// 프로젝트 코드 (NumberCode, IBA)
			String productmethod = StringUtil.checkNull((String)params.get("productmethod"));					// 제작방법 (NumberCode, IBA) 
			String deptcode = StringUtil.checkNull((String)params.get("deptcode"));							// 부서 (NumberCode, IBA)
			
			String weight = StringUtil.checkNull((String)params.get("weight"));								// 무게 (Key IN, IBA)
			String manufacture = StringUtil.checkNull((String)params.get("manufacture"));						// MANUTACTURE (NumberCode, IBA)
			String mat = StringUtil.checkNull((String)params.get("mat"));										// 재질 (NumberCode, IBA)
			String finish = StringUtil.checkNull((String)params.get("finish"));								// 후처리 (NumberCode, IBA)
			String remarks = StringUtil.checkNull((String)params.get("remarks"));								// 비고 (Key IN, IBA)
			String specification = StringUtil.checkNull((String)params.get("specification"));					// 사양 (Key IN, iBA)
			String ecoNo = StringUtil.checkNull((String)params.get("ecoNo"));									// ECO no (Key IN, iBA)
			String eoNo = StringUtil.checkNull((String)params.get("eoNo"));	
			
			String ecoPostdate = StringUtil.checkNull((String)params.get("ecoPostdate"));
			String ecoPredate = StringUtil.checkNull((String)params.get("ecoPredate"));
			String checkDummy = StringUtil.checkNull((String)params.get("checkDummy"));
			
			//System.out.println("checkDummy = " + checkDummy);
			//배포 관련 추가 
			boolean isProduction =  StringUtil.checkNull((String)params.get("production")).equals("true") ? true : false;
			boolean ischeckDummy =  StringUtil.checkNull((String)params.get("checkDummy")).equals("true") ? true : false;
			
			
			String sortValue = StringUtil.checkNull((String)params.get("sortValue"));
			String sortCheck = StringUtil.checkNull((String)params.get("sortCheck"));

			String location = StringUtil.checkNull((String)params.get("location"));
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
			if (createdFrom.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(createdFrom)), new int[] { idx });
			}
			if (createdTo.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(createdTo)), new int[] { idx });
			}

			// 수정일
			if (modifiedFrom.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", SearchCondition.GREATER_THAN, DateUtil.convertStartDate(modifiedFrom)), new int[] { idx });
			}
			if (modifiedTo.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp", SearchCondition.LESS_THAN, DateUtil.convertEndDate(modifiedTo)), new int[] { idx });
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
			
			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				PartData data = new PartData((WTPart) obj[0]);
				list.add(data);
			}
			
			map.put("list", list);
			map.put("topListCount", pager.getTotal());
			map.put("pageSize", pager.getPsize());
			map.put("total", pager.getTotalSize());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
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
	
	public Map<String,Object> requestPartMapping(Map<String, Object> params) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		// 품목 기본 정보
		String oid = StringUtil.checkNull((String)params.get("oid"));
		if(oid.length() > 0) {
			map.put("oid", oid);
		}
		
		String lifecycle = StringUtil.checkNull((String)params.get("lifecycle"));							// LifeCycle
		String view = StringUtil.checkNull((String)params.get("view"));									// view
		String fid = StringUtil.checkNull((String)params.get("fid"));										// 분류체계
		String wtPartType = StringUtil.checkNull((String)params.get("wtPartType"));
		String source = StringUtil.checkNull((String)params.get("source"));
		
		//String[] partNames = request.getParameterValues("partName");										
		String partName1 = StringUtil.checkNull((String)params.get("partName1"));							// 품목명1 (NumberCode)
		String partName2 = StringUtil.checkNull((String)params.get("partName2"));							// 품목명2 (NumberCode)
		String partName3 = StringUtil.checkNull((String)params.get("partName3"));							// 품목명3 (NumberCode)
		String partName4 = StringUtil.checkNull((String)params.get("partName4"));							// 품목명4 (Key In)
		
		String partType1 = StringUtil.checkNull((String)params.get("partType1"));							// 품목구분 (NumberCode)
		String partType2 = StringUtil.checkNull((String)params.get("partType2"));							// 대 분류 (NumberCode)
		String partType3 = StringUtil.checkNull((String)params.get("partType3"));							// 중 분류 (NumberCode)
		String seq = StringUtil.checkNull((String)params.get("seq"));										// SEQ
		String etc = StringUtil.checkNull((String)params.get("etc"));										// 기타
		
		// 품목 속성
		String unit = StringUtil.checkNull((String)params.get("unit"));									// 단위 (NumberCode)
		String model = StringUtil.checkNull((String)params.get("model"));									// 프로젝트 코드 (NumberCode, IBA)
		String productmethod = StringUtil.checkNull((String)params.get("productmethod"));					// 제작방법 (NumberCode, IBA) 
		String deptcode = StringUtil.checkNull((String)params.get("deptcode"));							// 부서 (NumberCode, IBA)
		String weight = StringUtil.checkNull((String)params.get("weight"));								// 무게 (Key IN, IBA)
		String manufacture = StringUtil.checkNull((String)params.get("manufacture"));						// MANUTACTURE (NumberCode, IBA)
		String mat = StringUtil.checkNull((String)params.get("mat"));										// 재질 (NumberCode, IBA)
		String finish = StringUtil.checkNull((String)params.get("finish"));								// 후처리 (NumberCode, IBA)
		String remarks = StringUtil.checkNull((String)params.get("remarks"));								// 비고 (Key IN, IBA)
		String specification = StringUtil.checkNull((String)params.get("specification"));					// 사양 (Key IN, iBA)
		
		// 주 도면
		String primary = StringUtil.checkNull((String)params.get("PRIMARY"));
					
		// 관련 문서
		String[] docOids = (String[])params.get("docOid");
		
		// 관련 RoHs
		String[] rohsOid = (String[])params.get("rohsOid");
					
		// 첨부파일
		String[] secondary = (String[])params.get("SECONDARY");
		
		// 첨부 추가
		String[] delocIds = (String[])params.get("delocIds");

		String partName = "";
		String[] partNames = new String[]{partName1, partName2, partName3, partName4}; 
		for(int i=0; i < partNames.length; i++) {
			if(StringUtil.checkString(partNames[i])) {
				if(i != 0 && partName.length() != 0) {
					partName += "_";
				}
				partName += partNames[i];
			}
		}
		String partNumber = partType1 + partType2 + partType3;

		map.put("lifecycle", lifecycle);
		map.put("view", view);
		map.put("fid", fid);
		map.put("wtPartType", wtPartType);
		map.put("source", source);
		map.put("partName", partName);
		map.put("partName1", partName1);
		map.put("partName2", partName2);
		map.put("partName3", partName3);
		map.put("partName4", partName4);
		map.put("partNumber", partNumber);
		map.put("seq", seq);
		map.put("etc", etc);
		map.put("unit", unit);
		map.put("model", model);
		map.put("productmethod", productmethod);
		map.put("deptcode", deptcode);
		map.put("weight", weight);
		map.put("manufacture", manufacture);
		map.put("mat", mat);
		map.put("finish", finish);
		map.put("remarks", remarks);
		map.put("specification", specification);
		map.put("primary", primary);
		map.put("docOids", docOids);
		map.put("rohsOid", rohsOid);
		map.put("secondary", secondary);
		map.put("delocIds", delocIds);
			
		return map;
	}
	
	public JSONArray include_PartList(String oid, String moduleType) throws Exception {
		List<PartData> list = new ArrayList<PartData>();
		try {
			if (oid.length() > 0) {
				QueryResult rt = null;
				Object obj = (Object) CommonUtil.getObject(oid);
				if ("doc".equals(moduleType)) {
					WTDocument doc = (WTDocument) obj;
					rt = PartDocHelper.service.getAssociatedParts(doc);
					while (rt.hasMoreElements()) {
						WTPart part = (WTPart) rt.nextElement();
						PartData data = new PartData(part);
						list.add(data);
					}
				} else if ("drawing".equals(moduleType)) {
					EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(epm, "describes", EPMDescribeLink.class);
					while (qr.hasMoreElements()) {
						WTPart part = (WTPart) qr.nextElement();
						PartData data = new PartData(part);
						list.add(data);
					}
				} else if ("ecr".equals(moduleType)) {
					EChangeRequest ecr = (EChangeRequest)CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(ecr,"part",EcrPartLink.class,false);
					while(qr.hasMoreElements()){
						EcrPartLink link = (EcrPartLink)qr.nextElement();
						String version = link.getVersion();
						WTPartMaster master = (WTPartMaster)link.getPart();
						WTPart part = PartHelper.service.getPart(master.getNumber(),version);
		    			PartData data = new PartData(part);
		    			
		    			list.add(data);
		    		}
				} else if("eco".equals(moduleType.toLowerCase())){
					EChangeOrder eco = (EChangeOrder)obj;
		    		rt = ECOSearchHelper.service.ecoPartLink(eco);
		    		while( rt.hasMoreElements()){
		    			Object[] o = (Object[])rt.nextElement();
						
						EcoPartLink link = (EcoPartLink)o[0];
		    			
		    			WTPartMaster master =  (WTPartMaster)link.getPart();
		    			String version = link.getVersion();
		    			
		    			WTPart part = PartHelper.service.getPart(master.getNumber(),version);
		    			PartData data = new PartData(part);
		    			//if(link.isBaseline()) data.setBaseline("checked");
		    			
		    			list.add(data);
		    		}
				}else if("rohs".equals(moduleType)){
					ROHSMaterial rohs = (ROHSMaterial)CommonUtil.getObject(oid);
					list = RohsQueryHelper.service.getROHSToPartList(rohs);
					Collections.sort(list, new ObjectComarator());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return JSONArray.fromObject(list);
	}
	
	public List<CommentsData> commentsList(String oid) throws Exception {
		List<CommentsData> comList = new ArrayList<CommentsData>(); 
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		QuerySpec qs =  new QuerySpec();
		int idx = qs.appendClassList(Comments.class, true);
		
		qs.appendWhere(new SearchCondition(Comments.class, "wtpartReference.key.id", "=", part.getPersistInfo().getObjectIdentifier().getId()), new int[] {idx});
		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "cNum"), false), new int[] { idx });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "thePersistInfo.createStamp"), false), new int[] { idx });
		
		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommentsData data = new CommentsData((Comments) obj[0]);
			comList.add(data);
		}
		return comList;
	}
	
	public int getCommentsChild(Comments com) throws Exception {
		WTPart part = com.getWtpart();
		int count = 0;
		QuerySpec qs =  new QuerySpec();
		int idx = qs.appendClassList(Comments.class, true);
		qs.appendWhere(new SearchCondition(Comments.class, "wtpartReference.key.id", "=", part.getPersistInfo().getObjectIdentifier().getId()), new int[] {idx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "oPerson", "=", com.getOwner().getFullName()), new int[] {idx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "cNum", "=", com.getCNum()), new int[] {idx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "cStep", ">", com.getCStep()), new int[] {idx});
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "deleteYN", "=", "N"), new int[] {idx});
		
		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			count++;
		}
		return count;
	}
	
	public JSONArray include_ChangeECOView(String oid, String moduleType) throws Exception {
		List<ECOData> list = new ArrayList<ECOData>();
		if(StringUtil.checkString(oid)){
    		if("part".equals(moduleType)) {
    			WTPart part = (WTPart)CommonUtil.getObject(oid);
    			List<EChangeOrder> eolist = getPartTOECOList(part);
    			for(EChangeOrder eco : eolist){
    				ECOData data = new ECOData(eco);
    				list.add(data);
    			}
    		}
		}
		return JSONArray.fromObject(list);
	}
	
	public List<EChangeOrder> getPartTOECOList(WTPart part) throws Exception {
		
		List<EChangeOrder> list = new ArrayList<EChangeOrder>();
		QuerySpec qs = new QuerySpec();
		QueryResult eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class,true);
		
		while(eolinkQr.hasMoreElements()){
			EChangeOrder eo = (EChangeOrder)eolinkQr.nextElement();
			list.add(eo);
		}
		
		//ECO Type이 CHANGE 제외
		eolinkQr = PersistenceHelper.manager.navigate(part.getMaster(), "eco",EOCompletePartLink.class,true);
		while(eolinkQr.hasMoreElements()){
			EChangeOrder eo = (EChangeOrder)eolinkQr.nextElement();
			if(eo.getEoType().equals(ECOKey.ECO_CHANGE)) continue;
			list.add(eo);
		}
		Collections.sort(list, new EoComparator());
		return list;
	}
}
