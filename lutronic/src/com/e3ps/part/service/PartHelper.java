package com.e3ps.part.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestBody;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcrPartLink;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.VersionData;
import com.e3ps.common.code.service.CodeHelper;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.comments.CommentsData;
import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.org.People;
import com.e3ps.part.column.PartColumn;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.service.RohsHelper;
import com.ptc.wvs.server.util.PublishUtils;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.folder.Folder;
import wt.folder.FolderHelper;
import wt.folder.IteratedFolderMemberLink;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.org.WTUser;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.pds.DatabaseInfoUtilities;
import wt.query.ClassAttribute;
import wt.query.KeywordExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.services.ServiceFactory;
import wt.util.FileUtil;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import com.e3ps.common.util.StringUtil;

public class PartHelper {
	public static final PartService service = ServiceFactory.getService(PartService.class);
	public static final PartHelper manager = new PartHelper();

	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<PartColumn> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);
//
//		QuerySpecUtils.toCI(query, idx, WTPart.class);
//		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NUMBER, partNumber);
//		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NAME, partName);
//		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.CREATE_TIMESTAMP, createdFrom, createdTo);
//		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, modifiedFrom,
//				modifiedTo);
//		QuerySpecUtils.creatorQuery(query, idx, WTPart.class, creator);
//		QuerySpecUtils.toState(query, idx, WTPart.class, state);
//		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, WTPart.DEFAULT_UNIT, unit);
		// EcoDate
//		if (ecoPostdate.length() > 0 || ecoPredate.length() > 0) {
//			// AttributeDefDefaultView aview =
//			// IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_ECODATE);
//
//			// if (aview != null) {
//			if (query.getConditionCount() > 0) {
//				query.appendAnd();
//			}
//			int _idx = query.appendClassList(StringValue.class, false);
//			query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
//					"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
//			query.appendAnd();
//			// query.appendWhere(new SearchCondition(StringValue.class,
//			// "definitionReference.hierarchyID", SearchCondition.EQUAL,
//			// aview.getHierarchyID()), new int[] { _idx });
//			long did = getECODATESeqDefinitionId();
//			// System.out.println("Long Check !!!!!!!!!!!!!! did = "+ did );
//			SearchCondition sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", did);
//			query.appendWhere(sc, new int[] { _idx });
//			ClassInfo classinfo = WTIntrospector.getClassInfo(StringValue.class);
//			String task_seqColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, StringValue.VALUE);
//			RelationalExpression paramRelationalExpression = new KeywordExpression(
//					"SUBSTR(" + task_seqColumnName + ",INSTR(" + task_seqColumnName + ",',',-1)+1)");
//
//			if (ecoPredate.length() > 0) {
//				query.appendAnd();
//				RelationalExpression expression = new wt.query.ConstantExpression(ecoPredate);
//				SearchCondition searchCondition = new SearchCondition(paramRelationalExpression,
//						SearchCondition.GREATER_THAN_OR_EQUAL, expression);
//				query.appendWhere(searchCondition, new int[] { _idx });
//			}
//			RelationalExpression paramRelationalExpression2 = new KeywordExpression(
//					"SUBSTR(" + task_seqColumnName + ",INSTR(" + task_seqColumnName + ",',',-1)+1)");
//
//			if (ecoPostdate.length() > 0) {
//				query.appendAnd();
//				RelationalExpression expression = new wt.query.ConstantExpression(ecoPostdate);
//				SearchCondition searchCondition2 = new SearchCondition(paramRelationalExpression2,
//						SearchCondition.LESS_THAN_OR_EQUAL, expression);
//				query.appendWhere(searchCondition2, new int[] { _idx });
//			}
//		}
		// }

//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MODEL, model);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_DEPTCODE, deptcode);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_WEIGHT, weight);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MAT, mat);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_FINISH, finish);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_REMARKS, remarks);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_SPECIFICATION, specification);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_CHANGENO, ecoNo);
//		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_ECONO, eoNo);

		// folder search
//		if (!"/Default/PART_Drawing".equals(location)) {
//			if (query.getConditionCount() > 0) {
//				query.appendAnd();
//			}
//
//			int folder_idx = query.addClassList(IteratedFolderMemberLink.class, false);
//			SearchCondition sc1 = new SearchCondition(
//					new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"),
//					SearchCondition.EQUAL, new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
//			sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
//			sc1.setOuterJoin(0);
//			query.appendWhere(sc1, new int[] { folder_idx, idx });
//
//			query.appendAnd();
//			ArrayList folders = CommonFolderHelper.service.getFolderTree(folder);
//			query.appendOpenParen();
//			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
//					SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()),
//					new int[] { folder_idx });
//
//			for (int fi = 0; fi < folders.size(); fi++) {
//				String[] s = (String[]) folders.get(fi);
//				Folder sf = (Folder) rf.getReference(s[2]).getObject();
//				query.appendOr();
//				query.appendWhere(
//						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
//								SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()),
//						new int[] { folder_idx });
//			}
//			query.appendCloseParen();
//		} else {
//			if (query.getConditionCount() > 0) {
//				query.appendAnd();
//			}
//			query.appendWhere(
//					new SearchCondition(WTPart.class, "master>number", SearchCondition.NOT_LIKE, "%DEL%", false),
//					new int[] { idx });
//
//		}
		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			PartColumn data = new PartColumn(part);
			list.add(data);
		}

		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public static long getECODATESeqDefinitionId() {
		StringDefinition itemSeqDefinition = null;
		try {
			QuerySpec select = new QuerySpec(StringDefinition.class);
			select.appendWhere(new SearchCondition(StringDefinition.class, "name", "=", "CHANGEDATE"), new int[] { 0 });
			QueryResult re = PersistenceHelper.manager.find(select);
			while (re.hasMoreElements()) {
				itemSeqDefinition = (StringDefinition) re.nextElement();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return itemSeqDefinition.getPersistInfo().getObjectIdentifier().getId();
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
					EChangeRequest ecr = (EChangeRequest) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(ecr, "part", EcrPartLink.class, false);
					while (qr.hasMoreElements()) {
						EcrPartLink link = (EcrPartLink) qr.nextElement();
						String version = link.getVersion();
						WTPartMaster master = (WTPartMaster) link.getPart();
						WTPart part = PartHelper.service.getPart(master.getNumber(), version);
						PartData data = new PartData(part);

						list.add(data);
					}
				} else if ("eco".equals(moduleType.toLowerCase())) {
					EChangeOrder eco = (EChangeOrder) obj;
					rt = ECOSearchHelper.service.ecoPartLink(eco);
					while (rt.hasMoreElements()) {
						Object[] o = (Object[]) rt.nextElement();

						EcoPartLink link = (EcoPartLink) o[0];

						WTPartMaster master = (WTPartMaster) link.getPart();
						String version = link.getVersion();

						WTPart part = PartHelper.service.getPart(master.getNumber(), version);
						PartData data = new PartData(part);
						// if(link.isBaseline()) data.setBaseline("checked");

						list.add(data);
					}
				} else if ("rohs".equals(moduleType)) {
					ROHSMaterial rohs = (ROHSMaterial) CommonUtil.getObject(oid);
					list = RohsHelper.manager.getROHSToPartList(rohs);
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
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(Comments.class, true);

		qs.appendWhere(new SearchCondition(Comments.class, "wtpartReference.key.id", "=",
				part.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "cNum"), false), new int[] { idx });
		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "thePersistInfo.createStamp"), false),
				new int[] { idx });

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
		QuerySpec qs = new QuerySpec();
		int idx = qs.appendClassList(Comments.class, true);
		qs.appendWhere(new SearchCondition(Comments.class, "wtpartReference.key.id", "=",
				part.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "oPerson", "=", com.getOwner().getFullName()),
				new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "cNum", "=", com.getCNum()), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "cStep", ">", com.getCStep()), new int[] { idx });
		qs.appendAnd();
		qs.appendWhere(new SearchCondition(Comments.class, "deleteYN", "=", "N"), new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(qs);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			count++;
		}
		return count;
	}

//	public JSONArray include_ChangeECOView(String oid, String moduleType) throws Exception {
//		List<ECOData> list = new ArrayList<ECOData>();
//		if (StringUtil.checkString(oid)) {
//			if ("part".equals(moduleType)) {
//				WTPart part = (WTPart) CommonUtil.getObject(oid);
//				List<EChangeOrder> eolist = getPartTOECOList(part);
//				for (EChangeOrder eco : eolist) {
//					ECOData data = new ECOData(eco);
//					list.add(data);
//				}
//			}
//		}
//		return JSONArray.fromObject(list);
//	}

	public Map<String, Object> listProduction(@RequestBody Map<String, Object> params) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<VersionData> list = new ArrayList<VersionData>();
		Map<String, Object> map = new HashMap<>();

		try {

			String foid = StringUtil.checkNull((String) params.get("fid"));
			String islastversion = StringUtil.checkNull((String) params.get("islastversion"));

			String partNumber = StringUtil.checkNull((String) params.get("partNumber"));
			partNumber = partNumber.trim();
			String partName = StringUtil.checkNull((String) params.get("partName"));
			String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
			String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
			String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
			String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
			String creator = StringUtil.checkNull((String) params.get("creator"));
			String state = StringUtil.checkNull((String) params.get("state"));

			String unit = StringUtil.checkNull((String) params.get("unit"));

			String model = StringUtil.checkNull((String) params.get("model")); // 프로젝트 코드 (NumberCode, IBA)
			String productmethod = StringUtil.checkNull((String) params.get("productmethod")); // 제작방법 (NumberCode, IBA)
			String deptcode = StringUtil.checkNull((String) params.get("deptcode")); // 부서 (NumberCode, IBA)

			String weight = StringUtil.checkNull((String) params.get("weight")); // 무게 (Key IN, IBA)
			String manufacture = StringUtil.checkNull((String) params.get("manufacture")); // MANUTACTURE (NumberCode,
																							// IBA)
			String mat = StringUtil.checkNull((String) params.get("mat")); // 재질 (NumberCode, IBA)
			String finish = StringUtil.checkNull((String) params.get("finish")); // 후처리 (NumberCode, IBA)
			String remarks = StringUtil.checkNull((String) params.get("remarks")); // 비고 (Key IN, IBA)
			String specification = StringUtil.checkNull((String) params.get("specification")); // 사양 (Key IN, iBA)
			String ecoNo = StringUtil.checkNull((String) params.get("ecoNo")); // ECO no (Key IN, iBA)
			String eoNo = StringUtil.checkNull((String) params.get("eoNo"));

			String ecoPostdate = StringUtil.checkNull((String) params.get("ecoPostdate"));
			String ecoPredate = StringUtil.checkNull((String) params.get("ecoPredate"));
			String checkDummy = StringUtil.checkNull((String) params.get("checkDummy"));

			// System.out.println("checkDummy = " + checkDummy);
			// 배포 관련 추가
			boolean isProduction = StringUtil.checkNull((String) params.get("production")).equals("true") ? true
					: false;
			boolean ischeckDummy = StringUtil.checkNull((String) params.get("checkDummy")).equals("true") ? true
					: false;

			String sortValue = StringUtil.checkNull((String) params.get("sortValue"));
			String sortCheck = StringUtil.checkNull((String) params.get("sortCheck"));

			String location = StringUtil.checkNull((String) params.get("location"));
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

			// Working Copy 제외
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			query.appendWhere(
					new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false),
					new int[] { idx });

			if (isProduction) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(
						new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "1_________", false),
						new int[] { idx });
			}

			// 품목코드
			if (partNumber.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE,
						"%" + partNumber + "%", false), new int[] { idx });
			}

			// 품명
			if (partName.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "master>name", SearchCondition.LIKE,
						"%" + partName + "%", false), new int[] { idx });
			}

			// 등록일
			if (createdFrom.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(createdFrom)), new int[] { idx });
			}
			if (createdTo.trim().length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.createStamp",
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(createdTo)), new int[] { idx });
			}

			// 수정일
			if (modifiedFrom.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp",
						SearchCondition.GREATER_THAN, DateUtil.convertStartDate(modifiedFrom)), new int[] { idx });
			}
			if (modifiedTo.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.modifyStamp",
						SearchCondition.LESS_THAN, DateUtil.convertEndDate(modifiedTo)), new int[] { idx });
			}

			// 등록자
			if (creator.length() > 0) {
				People people = (People) rf.getReference(creator).getObject();
				WTUser user = people.getUser();
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.creator.key.id",
						SearchCondition.EQUAL, CommonUtil.getOIDLongValue(user)), new int[] { idx });
			}

			// 상태
			if (state.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, "state.state", SearchCondition.EQUAL, state),
						new int[] { idx });
			}

			// 단위
			if (unit.length() > 0) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(new SearchCondition(WTPart.class, WTPart.DEFAULT_UNIT, SearchCondition.EQUAL, unit),
						new int[] { idx });
			}

			// EcoDate
			if (ecoPostdate.length() > 0 || ecoPredate.length() > 0) {

				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				int _idx = query.appendClassList(StringValue.class, false);
				query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id", WTPart.class,
						"thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
				query.appendAnd();
				long did = getECODATESeqDefinitionId();
				SearchCondition sc = new SearchCondition(StringValue.class, "definitionReference.key.id", "=", did);
				query.appendWhere(sc, new int[] { _idx });
				ClassInfo classinfo = WTIntrospector.getClassInfo(StringValue.class);
				String task_seqColumnName = DatabaseInfoUtilities.getValidColumnName(classinfo, StringValue.VALUE);
				RelationalExpression paramRelationalExpression = new KeywordExpression(
						"SUBSTR(" + task_seqColumnName + ",INSTR(" + task_seqColumnName + ",',',-1)+1)");

				if (ecoPredate.length() > 0) {
					query.appendAnd();
					RelationalExpression expression = new wt.query.ConstantExpression(ecoPredate);
					SearchCondition searchCondition = new SearchCondition(paramRelationalExpression,
							SearchCondition.GREATER_THAN_OR_EQUAL, expression);
					query.appendWhere(searchCondition, new int[] { _idx });
				}
				RelationalExpression paramRelationalExpression2 = new KeywordExpression(
						"SUBSTR(" + task_seqColumnName + ",INSTR(" + task_seqColumnName + ",',',-1)+1)");

				if (ecoPostdate.length() > 0) {
					query.appendAnd();
					RelationalExpression expression = new wt.query.ConstantExpression(ecoPostdate);
					SearchCondition searchCondition2 = new SearchCondition(paramRelationalExpression2,
							SearchCondition.LESS_THAN_OR_EQUAL, expression);
					query.appendWhere(searchCondition2, new int[] { _idx });
				}
			}
			// }

			// 프로젝트 코드
			if (model.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MODEL);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + model + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				model = "";
			}

			// 제작방법
			if (productmethod.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_PRODUCTMETHOD);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + productmethod + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				productmethod = "";
			}

			// 부서
			if (deptcode.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_DEPTCODE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + deptcode + "%").toUpperCase()), new int[] { _idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + weight + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				weight = "";
			}

			// MANUFACTURE
			if (manufacture.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_MANUFACTURE);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + manufacture + "%").toUpperCase()), new int[] { _idx });
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
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + mat + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				mat = "";
			}

			// 후처리
			if (finish.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_FINISH);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + finish + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				finish = "";
			}

			// 비고
			if (remarks.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_REMARKS);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + remarks + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				remarks = "";
			}

			// 사양
			if (specification.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_SPECIFICATION);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + specification + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				specification = "";
			}

			// ecoNo
			if (ecoNo.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_CHANGENO);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + ecoNo + "%").toUpperCase()), new int[] { _idx });
				}
			} else {
				ecoNo = "";
			}
			// 사양
			if (eoNo.length() > 0) {
				AttributeDefDefaultView aview = IBADefinitionHelper.service
						.getAttributeDefDefaultViewByPath(AttributeKey.IBAKey.IBA_ECONO);
				if (aview != null) {
					if (query.getConditionCount() > 0) {
						query.appendAnd();
					}
					int _idx = query.appendClassList(StringValue.class, false);
					query.appendWhere(new SearchCondition(StringValue.class, "theIBAHolderReference.key.id",
							WTPart.class, "thePersistInfo.theObjectIdentifier.id"), new int[] { _idx, idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "definitionReference.hierarchyID",
							SearchCondition.EQUAL, aview.getHierarchyID()), new int[] { _idx });
					query.appendAnd();
					query.appendWhere(new SearchCondition(StringValue.class, "value", SearchCondition.LIKE,
							("%" + eoNo + "%").toUpperCase()), new int[] { _idx });
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
				SearchCondition sc1 = new SearchCondition(
						new ClassAttribute(IteratedFolderMemberLink.class, "roleBObjectRef.key.branchId"),
						SearchCondition.EQUAL, new ClassAttribute(WTPart.class, "iterationInfo.branchId"));
				sc1.setFromIndicies(new int[] { folder_idx, idx }, 0);
				sc1.setOuterJoin(0);
				query.appendWhere(sc1, new int[] { folder_idx, idx });

				query.appendAnd();
				ArrayList folders = CommonFolderHelper.service.getFolderTree(folder);
				query.appendOpenParen();
				query.appendWhere(
						new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
								SearchCondition.EQUAL, folder.getPersistInfo().getObjectIdentifier().getId()),
						new int[] { folder_idx });

				for (int fi = 0; fi < folders.size(); fi++) {
					String[] s = (String[]) folders.get(fi);
					Folder sf = (Folder) rf.getReference(s[2]).getObject();
					query.appendOr();
					query.appendWhere(
							new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
									SearchCondition.EQUAL, sf.getPersistInfo().getObjectIdentifier().getId()),
							new int[] { folder_idx });
				}
				query.appendCloseParen();
			} else {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}
				query.appendWhere(
						new SearchCondition(WTPart.class, "master>number", SearchCondition.NOT_LIKE, "%DEL%", false),
						new int[] { idx });

			}

			// sorting
			if (sortValue.length() > 0) {
				boolean sort = "true".equals(sortCheck);
				if (!"creator".equals(sortValue)) {
					// query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, sortValue),
					// false), new int[] { idx });
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
				query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, WTPart.MODIFY_TIMESTAMP), true),
						new int[] { idx });
			}

			PageQueryUtils pager = new PageQueryUtils(params, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTPart part = (WTPart) obj[0];
				VersionData data = new VersionData((RevisionControlled) part);
				data.setNumber(part.getNumber());
				String remarks2 = StringUtil
						.checkNull(IBAUtil.getAttrValue((IBAHolder) part, AttributeKey.IBAKey.IBA_REMARKS));
				data.setRemarks(remarks2);
				boolean isProduct = PartUtil.isProductCheck(part.getNumber());
				data.setProduct(isProduct);
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

	public Map<String, Object> bomPartList(Map<String, Object> params) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		String oid = (String) params.get("oid");
		String bomType = (String) params.get("bomType");
		result.put("oid", oid);
		result.put("bomType", bomType);

		WTPart part = (WTPart) CommonUtil.getObject(oid);
		// part = EulPartHelper.service.getPart(part.getNumber());
		PartData pd = new PartData(part);

		BomBroker broker = new BomBroker();
		ArrayList list = new ArrayList();
		result.put("partNumber", part.getNumber());
		String msg = null;
		String title = "";

		View[] views = ViewHelper.service.getAllViews();
		String view = views[0].getName();

		if ("up".equals(bomType)) {

			list = broker.ancestorPart(part, ViewHelper.service.getView(view), null);
			msg = Message.get("상위품목이 없습니다.");
			title = Message.get("상위품목");
		} else if ("down".equals(bomType)) {
			list = broker.descentLastPart(part, ViewHelper.service.getView(view), null);
			msg = Message.get("하위품목이 없습니다.");
			title = Message.get("하위품목");
		} else if ("end".equals(bomType)) {
			PartTreeData root = broker.getTree(part, false, null);
//			PartTreeData root = broker.getTree(part, false, null,ViewHelper.service.getView(view));
			broker.setHtmlForm(root, list);
			msg = Message.get("END ITEM이 없습니다.");

			title = Message.get("END ITEM");
		}
		result.put("msg", msg);
		result.put("title", title);
		List<Map<String, String>> item = new ArrayList<Map<String, String>>();
		if (list.size() > 0) {
			Collections.sort(list, new ObjectComarator());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < list.size(); i++) {
				if ("end".equals(bomType)) {
					PartTreeData data = (PartTreeData) list.get(i);

					//// System.out.println("END ITEM data.number = " + data.number);
					/*
					 * if (data.children.size() > 0) { continue; } //자기자신 제외
					 * if(part.getNumber().equals(data.number)){ continue; } //더미 제외
					 * if(PartUtil.isChange(data.number)){ continue; }
					 * 
					 * //마스터 완제품 제외 if(PartUtil.isProductCheck(data.number) &&
					 * !PartUtil.completeProductCheck(data.number)){ continue; }
					 */
					WTPart endPart = data.part;
					System.out.println("endPart                     : " + endPart);
					System.out.println("part                     : " + part);
					if (endPart.getNumber().equals(part.getNumber())) {
						continue;
					}

					String partNumter = endPart.getNumber();

					if (PartUtil.isChange(partNumter)) {
						continue;
					}
					//// System.out.println("partNumter =" + partNumter);
					if (!PartUtil.completeProductCheck(partNumter)) {
						continue;
					}
					String partendoid = CommonUtil.getOIDString(data.part);
					Map<String, String> map = new HashMap<String, String>();
					map.put("icon", CommonUtil.getObjectIconImageTag(data.part));
					map.put("oid", data.part.getPersistInfo().getObjectIdentifier().toString());
					map.put("number", data.number);
					map.put("name", data.name);
					map.put("state", data.part.getLifeCycleState().getDisplay(Message.getLocale()));
					map.put("version", data.version + "." + data.iteration);
					if (!sb.toString().contains(partendoid))
						item.add(map);
					sb.append(partendoid + ";");
				} else {
					Object[] o = (Object[]) list.get(i);
					WTPart partD = (WTPart) o[1];
					PartData data = new PartData(partD);

					Map<String, String> map = new HashMap<String, String>();
					map.put("icon", data.getIcon());
					map.put("oid", data.getOid());
					map.put("number", data.getNumber());
					map.put("name", data.getName());
//					map.put("state", data.getLifecycle());
					map.put("version", data.getVersion());
					String partendoid = CommonUtil.getOIDString(partD);
					if (!sb.toString().contains(partendoid))
						item.add(map);
					sb.append(partendoid + ";");
				}
			}
		}
		result.put("list", item);
		return result;
	}

	/**
	 * 품목과 연관된 3D 캐드 가져오기
	 */
	public EPMDocument getEPMDocument(WTPart part) throws Exception {
		EPMDocument epm = null;
		if (part == null) {
			return epm;
		}

		QueryResult result = null;
		if (VersionControlHelper.isLatestIteration(part)) {
			result = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class);
		} else {
			result = PersistenceHelper.manager.navigate(part, "builtBy", EPMBuildHistory.class);
		}
		if (result.hasMoreElements()) {
			epm = (EPMDocument) result.nextElement();
		}
		return epm;
	}

	/**
	 * 1품 1도인 업체에서만 사용가능
	 */
	public EPMDocument getEPMDocument2D(EPMDocument epm) throws Exception {
		EPMDocumentMaster m = (EPMDocumentMaster) epm.getMaster();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EPMReferenceLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, EPMReferenceLink.class, "roleBObjectRef.key.id", m);
		QuerySpecUtils.toEqualsAnd(query, idx, EPMReferenceLink.class, "referenceType", "DRAWING");
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EPMReferenceLink link = (EPMReferenceLink) obj[0];
			return link.getReferencedBy();
		}
		return null;
	}
	
	public Map<String, Object> partExcel(Map<String, Object> param,HttpServletResponse response) throws Exception {
		Map<String, Object> resultData = new HashMap<String, Object>();
		List<Map<String, Object>> bomList =BomSearchHelper.manager.getAllBomList(param);
		
		
		ReferenceFactory rf = new ReferenceFactory();
		WTPart part =  (WTPart) rf.getReference(param.get("oid").toString()).getObject();

		// Part Thumbnail
		String path = "C:\\ptc\\thumb";
//		String path = "/opt/ptc/partlistExcelImages";
		Representation representation = PublishUtils.getRepresentation(part);
		FileOutputStream fos = null;
		if (StringUtil.isNotNull(representation)) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.THUMBNAIL);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				String ext = FileUtil.getExtension(data.getFileName());
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);

				File file = new File(path + File.separator + part.getNumber().toUpperCase() + ".jsp");

//				FileOutputStream fos = new FileOutputStream(file);
				fos = new FileOutputStream(file);

				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
		}

		try {
			// 새로운 워크북(엑셀 파일) 생성
			XSSFWorkbook workbook = new XSSFWorkbook();

			// 워크북에 시트 생성
			Sheet sheet = workbook.createSheet("Sheet1");

			sheet.setColumnWidth(1, 30 * 256);
			// 헤더
			Row row = sheet.createRow(0);
			Cell cell = row.createCell(0);
			cell.setCellValue("Bom Editor");

			CellRangeAddress mergedRegion = new CellRangeAddress(0, 0, 0, 7);
			sheet.addMergedRegion(mergedRegion);
			style(workbook, cell);


			Row row1 = sheet.createRow(2);
			Cell cell01 = row1.createCell(0);
			cell01.setCellValue("Level");
			style2(workbook, cell01);

			Cell cell02 = row1.createCell(1);
			cell02.setCellValue("부품번호");
			style2(workbook, cell02);

			Cell cell03 = row1.createCell(2);
			cell03.setCellValue("도면번호");
			style2(workbook, cell03);

			Cell cell04 = row1.createCell(3);
			cell04.setCellValue("부품명");
			style2(workbook, cell04);

			Cell cell05 = row1.createCell(4);
			cell05.setCellValue("REV");
			style2(workbook, cell05);

			Cell cell06 = row1.createCell(5);
			cell06.setCellValue("OEM Info.");
			style2(workbook, cell06);

			Cell cell07 = row1.createCell(6);
			cell07.setCellValue("체크아웃 상태");
			style2(workbook, cell07);

			Cell cell08 = row1.createCell(7);
			cell08.setCellValue("상태");
			style2(workbook, cell08);
			
			Cell cell09 = row1.createCell(8);
			cell09.setCellValue("수정자");
			style2(workbook, cell09);
			
			Cell cell10 = row1.createCell(9);
			cell10.setCellValue("사양");
			style2(workbook, cell10);
			
			Cell cell11 = row1.createCell(10);
			cell11.setCellValue("수량");
			style2(workbook, cell11);
			
			Cell cell12 = row1.createCell(11);
			cell12.setCellValue("ECO NO.");
			style2(workbook, cell12);

			Cell cell13 = row1.createCell(12);
			cell13.setCellValue("프로젝트코드");
			style2(workbook, cell13);
			
			Cell cell14 = row1.createCell(13);
			cell14.setCellValue("부서");
			style2(workbook, cell14);

			Cell cell15 = row1.createCell(14);
			cell15.setCellValue("MANUFACTURER");
			style2(workbook, cell15);
			
			Cell cell16 = row1.createCell(15);
			cell16.setCellValue("제작방법");
			style2(workbook, cell16);
			
//			int rowCellCnt = 1;
//			int rowCnt = 5;
//			for (PARTLISTLink_HIS item : partlistLinkList) {
//				PARTLIST_HIS childPartlist = (PARTLIST_HIS) rf.getReference(item.getRoleBObjectId().toString())
//						.getObject();
//				WTPart childPart = childPartlist.getWtpart();
//
//				Row partlistRow = sheet.createRow(rowCnt);
//				// 로우 높이 조절
//				partlistRow.setHeightInPoints(100);
//				Cell partlistCell1 = partlistRow.createCell(0);
//				partlistCell1.setCellValue(rowCellCnt);
//				style2(workbook, partlistCell1);
//
//				// 이미지 파일 경로 설정 (예시: "image.jpg")
////		        String thumbnail = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(childPart),
////						ContentRoleType.THUMBNAIL);
////		        String imagePath = "";
////		        Cell partlistCell2 = partlistRow.createCell(1);
////
////			     // 이미지 파일 경로 설정 (예시: "image.jpg")
////			     String imagePath = ThumbnailUtils.thumbnails(childPart)[0];
////	
////			     // 이미지 파일 읽기
////			     FileInputStream imageStream = new FileInputStream(imagePath);
////			     byte[] imageData = IOUtils.toByteArray(imageStream);
////			     int pictureIdx = workbook.addPicture(imageData, Workbook.PICTURE_TYPE_JPEG);
////	
////			     // Drawing 객체 생성 및 워크북과 연결
////			     Drawing<?> drawing = sheet.createDrawingPatriarch();
////	
////			     // ClientAnchor 객체 생성하여 이미지가 들어갈 위치와 크기 지정
////			     ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
////			     anchor.setCol1(1); // 이미지가 들어갈 셀의 열 인덱스 (예시: 1)
////			     anchor.setRow1(rowCnt); // 이미지가 들어갈 셀의 행 인덱스
////	
////			     // 그림 추가
////			     Picture picture = drawing.createPicture(anchor, pictureIdx);
////	
////			     // 원본 크기로 유지하도록 설정 (선택적)
////			     picture.resize(1,1);
//
//				// add picture data to this workbook.
////				InputStream is = new FileInputStream(
////						"/opt/ptc/Windchill_12.0/Windchill/codebase/jsp/images/productview_openin_250.png");
////				InputStream is = new FileInputStream(paht"C:\\ptc\\Windchill_12.0\\Windchill\\codebase\\jsp\\images\\productview_openin_250.png");
////				InputStream is = new FileInputStream(path + "\\" + part.getNumber() + ".jpg");
//				InputStream is = null;
//				byte[] bytes = null;
//				int pictureIdx = 0;
//				if (StringUtils.isNotNull(fos)) {
//					is = new FileInputStream(path + "\\" + part.getNumber() + ".jpg");
//					bytes = IOUtils.toByteArray(is);
//					pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
//					is.close();
//				} else {
//					is = new FileInputStream("C:\\ptc\\Windchill_12.0\\Windchill\\codebase\\jsp\\images\\productview_openin_250.png");
//					bytes = IOUtils.toByteArray(is);
//					pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
//					is.close();
//				}
//				CreationHelper helper = workbook.getCreationHelper();
//				// Create the drawing patriarch. This is the top level container for all shapes.
//				Drawing drawing = sheet.createDrawingPatriarch();
//
//				// add a picture shape
//				ClientAnchor anchor = helper.createClientAnchor();
//				// set top-left corner of the picture,
//				// subsequent call of Picture#resize() will operate relative to it
//				anchor.setCol1(1);
//				anchor.setRow1(rowCnt);
//				anchor.setCol2(2);
//				anchor.setRow2(rowCnt + 1);
//				Picture pict = drawing.createPicture(anchor, pictureIdx);
//				// auto-size picture relative to its top-left corner
//				pict.resize(1, 1);
//
//				Cell partlistCell2 = partlistRow.createCell(1);
//				partlistCell2.setCellValue("");
//				style2(workbook, partlistCell2);
//
//				Cell partlistCell3 = partlistRow.createCell(2);
//				partlistCell3.setCellValue("자재".equals(PartHelper.manager.partTypeToDisplay(childPart)) == true
//						? IBAUtil.getStringValue(childPart, "PART_NO")
//						: IBAUtil.getStringValue(childPart, "ERP_CODE"));
//				style3(workbook, partlistCell3);
//
//				Cell partlistCell4 = partlistRow.createCell(3);
//				partlistCell4.setCellValue(IBAUtil.getStringValue(childPart, "DEV_CLASSIFICATION"));
//				style2(workbook, partlistCell4);
//
//				Cell partlistCell5 = partlistRow.createCell(4);
//				partlistCell5.setCellValue(childPart.getVersionIdentifier().getSeries().getValue());
//				style2(workbook, partlistCell5);
//
//				Cell partlistCell6 = partlistRow.createCell(5);
//				partlistCell6.setCellValue(childPartlist.getColor());
//				style2(workbook, partlistCell6);
//
//				Cell partlistCell7 = partlistRow.createCell(6);
//				partlistCell7.setCellValue(IBAUtil.getStringValue(childPart, "PART_NAME"));
//				style2(workbook, partlistCell7);
//
//				Cell partlistCell8 = partlistRow.createCell(7);
//				partlistCell8.setCellValue("");
//				style2(workbook, partlistCell8);
//				rowCnt++;
//				rowCellCnt++;
//				Map<String, Integer> cntMap = excelTree(childPartlist, masterHis, sheet, workbook, rowCnt, rowCellCnt);
//				if (cntMap.get("rowCnt") != rowCellCnt) {
//					rowCnt = cntMap.get("rowCnt");
//					rowCellCnt = cntMap.get("rowCellCnt");
//				}
//			}

			// 행 넓이 자동
//	        sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(6);
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

			LocalDate date = LocalDate.now();
			String now = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			now = now.replaceAll("-", "");
			now = now.substring(0, 8);

			response.setHeader("Content-Disposition",
					"attachment; filename=" + IBAUtil.getStringValue(part, "ERP_CODE") + "_" + now + ".xlsx");

			try {
				System.out.println("성공.");
				workbook.write(response.getOutputStream());
			} catch (Exception e) {
				System.out.println("살패.");
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return resultData;
	}
	
	
	private static void style(XSSFWorkbook workbook, Cell cell) {
		CellStyle style = workbook.createCellStyle();

		// 폰트 설정 (크기, 진하게)
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 15); // 폰트 크기
		font.setBold(true); // 진하게 설정

		style.setFont(font);

		// 가운데 정렬 설정
		style.setAlignment(HorizontalAlignment.CENTER);

		// 스타일 적용
		cell.setCellStyle(style);

	}

	private static void style2(XSSFWorkbook workbook, Cell cell) {
		CellStyle style = workbook.createCellStyle();

		// 테두리 설정
		style.setBorderTop(BorderStyle.THIN); // 상단 테두리
		style.setBorderBottom(BorderStyle.THIN); // 하단 테두리
		style.setBorderLeft(BorderStyle.THIN); // 왼쪽 테두리
		style.setBorderRight(BorderStyle.THIN); // 오른쪽 테두리

		// 폰트 설정 (크기, 진하게)
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 12); // 폰트 크기
		font.setBold(true); // 진하게 설정

		style.setFont(font);

		// 가운데 정렬 설정
		style.setAlignment(HorizontalAlignment.CENTER);

		// 높이 가운데 정렬 설정
		style.setVerticalAlignment(VerticalAlignment.CENTER);

		// 스타일 적용
		cell.setCellStyle(style);

	}

	private static void style3(XSSFWorkbook workbook, Cell cell) {
		CellStyle style = workbook.createCellStyle();

		// 테두리 설정
		style.setBorderTop(BorderStyle.THIN); // 상단 테두리
		style.setBorderBottom(BorderStyle.THIN); // 하단 테두리
		style.setBorderLeft(BorderStyle.THIN); // 왼쪽 테두리
		style.setBorderRight(BorderStyle.THIN); // 오른쪽 테두리

		// 폰트 설정 (크기, 진하게)
		Font font = workbook.createFont();
		font.setFontHeightInPoints((short) 12); // 폰트 크기
		font.setBold(true); // 진하게 설정

		style.setFont(font);

		// 스타일 적용
		cell.setCellStyle(style);

	}
}
