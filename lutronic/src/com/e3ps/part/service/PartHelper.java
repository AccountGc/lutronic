package com.e3ps.part.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.web.bind.annotation.RequestBody;

import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcrPartLink;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.beans.VersionData;
import com.e3ps.common.comments.Comments;
import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtil;
import com.e3ps.common.message.Message;
import com.e3ps.common.query.SearchUtil;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.DateUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.org.People;
import com.e3ps.part.column.PartColumn;
import com.e3ps.part.dto.ObjectComarator;
import com.e3ps.part.dto.PartDTO;
import com.e3ps.part.dto.PartData;
import com.e3ps.part.dto.PartTreeData;
import com.e3ps.part.util.BomBroker;
import com.e3ps.part.util.PartUtil;
import com.e3ps.rohs.PartToRohsLink;
import com.e3ps.rohs.ROHSMaterial;
import com.e3ps.rohs.dto.RohsData;
import com.e3ps.rohs.service.RohsHelper;

import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
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
import wt.folder.SubFolder;
import wt.iba.definition.StringDefinition;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.IBAHolder;
import wt.iba.value.StringValue;
import wt.introspection.ClassInfo;
import wt.introspection.WTIntrospector;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.PartDocHelper;
import wt.part.WTPart;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.pds.DatabaseInfoUtilities;
import wt.query.ClassAttribute;
import wt.query.KeywordExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.RelationalExpression;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;
import wt.vc.wip.WorkInProgressHelper;

public class PartHelper {

	public static final String PART_ROOT = "/Default/PART_Drawing";

	public static final PartService service = ServiceFactory.getService(PartService.class);
	public static final PartHelper manager = new PartHelper();

	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<PartColumn> list = new ArrayList<>();
		ReferenceFactory rf = new ReferenceFactory();

		String location = StringUtil.checkNull((String) params.get("location"));
		String islastversion = StringUtil.checkNull((String) params.get("islastversion"));
		String partNumber = StringUtil.checkNull((String) params.get("partNumber"));
		String partName = StringUtil.checkNull((String) params.get("partName"));
		String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
		String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
		String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
		String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
		String creator = StringUtil.checkNull((String) params.get("creator"));
		String creatorOid = StringUtil.checkNull((String) params.get("creatorOid"));
		String state = StringUtil.checkNull((String) params.get("state"));
		String model = StringUtil.checkNull((String) params.get("model"));
		String productmethod = StringUtil.checkNull((String) params.get("productmethod"));
		String deptcode = StringUtil.checkNull((String) params.get("deptcode"));
		String unit = StringUtil.checkNull((String) params.get("unit"));
		String weight = StringUtil.checkNull((String) params.get("weight"));
		String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
		String mat = StringUtil.checkNull((String) params.get("mat"));
		String finish = StringUtil.checkNull((String) params.get("finish"));
		String remarks = StringUtil.checkNull((String) params.get("remarks"));
		String specification = StringUtil.checkNull((String) params.get("specification"));
		String ecoNo = StringUtil.checkNull((String) params.get("ecoNo"));
		String eoNo = StringUtil.checkNull((String) params.get("eoNo"));
		boolean latest = (boolean) params.get("latest");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);

		// 상태 임시저장 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(
				new SearchCondition(WTPart.class, WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, "TEMPRARY"),
				new int[] { idx });

		QuerySpecUtils.toCI(query, idx, WTPart.class);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NUMBER, partNumber);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NAME, partName);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.CREATE_TIMESTAMP, createdFrom, createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);
		QuerySpecUtils.creatorQuery(query, idx, WTPart.class, creatorOid);
		QuerySpecUtils.toState(query, idx, WTPart.class, state);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(WTPart.class, "state.state", "<>", "TEMPRARY");
		query.appendWhere(sc, new int[] { idx });

		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, WTPart.DEFAULT_UNIT, unit);
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

		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MODEL, model);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_DEPTCODE, deptcode);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_WEIGHT, weight);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MAT, mat);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_FINISH, finish);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_REMARKS, remarks);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_SPECIFICATION, specification);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_CHANGENO, ecoNo);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_ECONO, eoNo);

		if (!StringUtil.checkString(location)) {
			location = "/Default/PART_Drawing";
		}

		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());

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
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
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

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTPart.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartColumn data = new PartColumn(obj);
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
		List<PartDTO> list = new ArrayList<PartDTO>();
		try {
			if (oid.length() > 0) {
				QueryResult rt = null;
				Object obj = (Object) CommonUtil.getObject(oid);
				if ("doc".equals(moduleType)) {
					WTDocument doc = (WTDocument) obj;
					rt = PartDocHelper.service.getAssociatedParts(doc);
					while (rt.hasMoreElements()) {
						WTPart part = (WTPart) rt.nextElement();
						PartDTO data = new PartDTO(part);
						list.add(data);
					}
				} else if ("drawing".equals(moduleType)) {
					EPMDocument epm = (EPMDocument) CommonUtil.getObject(oid);
					QueryResult qr = PersistenceHelper.manager.navigate(epm, "describes", EPMDescribeLink.class);
					while (qr.hasMoreElements()) {
						WTPart part = (WTPart) qr.nextElement();
						PartDTO data = new PartDTO(part);
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
						PartDTO data = new PartDTO(part);

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
						PartDTO data = new PartDTO(part);
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

	/**
	 * 부품 이력
	 */
	public JSONArray allIterationsOf(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		QueryResult result = VersionControlHelper.service.allIterationsOf(part.getMaster());
		while (result.hasMoreElements()) {
			WTPart p = (WTPart) result.nextElement();
			Map<String, String> map = new HashMap<>();
			PartData data = new PartData(p);
			map.put("oid", data.getOid());
			map.put("name", data.getName());
			map.put("number", data.getNumber());
			map.put("version", data.getVersion());
			map.put("creator", data.getCreator());
			map.put("createdDate", data.getCreateDate());
//			map.put("modifier", data.getModifier());
			map.put("modifiedDate", data.getModifyDate());
			map.put("note", p.getIterationNote());
//			map.put("primary", data.getPrimary());
//			map.put("secondary", data.getSecondary());
			list.add(map);
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

		Map<String, Object> map = new HashMap<>();
		ArrayList<PartColumn> list = new ArrayList<>();
		ReferenceFactory rf = new ReferenceFactory();

		String location = StringUtil.checkNull((String) params.get("location"));
		String islastversion = StringUtil.checkNull((String) params.get("islastversion"));
		String partNumber = StringUtil.checkNull((String) params.get("partNumber"));
		String partName = StringUtil.checkNull((String) params.get("partName"));
		String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
		String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
		String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
		String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
		String creator = StringUtil.checkNull((String) params.get("creator"));
		String creatorOid = StringUtil.checkNull((String) params.get("creatorOid"));
		String state = StringUtil.checkNull((String) params.get("state"));
		String model = StringUtil.checkNull((String) params.get("model"));
		String productmethod = StringUtil.checkNull((String) params.get("productmethod"));
		String deptcode = StringUtil.checkNull((String) params.get("deptcode"));
		String unit = StringUtil.checkNull((String) params.get("unit"));
		String weight = StringUtil.checkNull((String) params.get("weight"));
		String manufacture = StringUtil.checkNull((String) params.get("manufacture"));
		String mat = StringUtil.checkNull((String) params.get("mat"));
		String finish = StringUtil.checkNull((String) params.get("finish"));
		String remarks = StringUtil.checkNull((String) params.get("remarks"));
		String specification = StringUtil.checkNull((String) params.get("specification"));
		String ecoNo = StringUtil.checkNull((String) params.get("ecoNo"));
		String eoNo = StringUtil.checkNull((String) params.get("eoNo"));
		boolean latest = (boolean) params.get("latest");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);

		QuerySpecUtils.toCI(query, idx, WTPart.class);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NUMBER, partNumber);
		QuerySpecUtils.toLikeAnd(query, idx, WTPart.class, WTPart.NAME, partName);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.CREATE_TIMESTAMP, createdFrom, createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, modifiedFrom,
				modifiedTo);
		QuerySpecUtils.creatorQuery(query, idx, WTPart.class, creatorOid);
		QuerySpecUtils.toState(query, idx, WTPart.class, state);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		SearchCondition sc = new SearchCondition(WTPart.class, "state.state", "<>", "TEMPRARY");
		query.appendWhere(sc, new int[] { idx });

		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, WTPart.DEFAULT_UNIT, unit);

		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MODEL, model);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_PRODUCTMETHOD, productmethod);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_DEPTCODE, deptcode);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_WEIGHT, weight);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MANUFACTURE, manufacture);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_MAT, mat);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_FINISH, finish);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_REMARKS, remarks);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_SPECIFICATION, specification);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_CHANGENO, ecoNo);
		QuerySpecUtils.toIBALikeAnd(query, WTPart.class, idx, AttributeKey.IBAKey.IBA_ECONO, eoNo);
		Folder folder = FolderTaskLogic.getFolder(location, WCUtil.getWTContainerRef());

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
			query.appendWhere(new SearchCondition(IteratedFolderMemberLink.class, "roleAObjectRef.key.id",
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

		// Working Copy 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(
				new SearchCondition(WTPart.class, "checkoutInfo.state", SearchCondition.NOT_EQUAL, "wrk", false),
				new int[] { idx });

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.LIKE, "1_________", false),
				new int[] { idx });

		// 최신 이터레이션.
		if (latest) {
			QuerySpecUtils.toLatest(query, idx, WTPart.class);
		}

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			PartColumn data = new PartColumn(obj);
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

	public Map<String, Object> bomPartList(Map<String, Object> params) throws Exception {

		Map<String, Object> result = new HashMap<String, Object>();

		String oid = (String) params.get("oid");
		String bomType = (String) params.get("bomType");
		result.put("oid", oid);
		result.put("bomType", bomType);

		WTPart part = (WTPart) CommonUtil.getObject(oid);

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

	/**
	 * 품목 폴더 가져오기
	 */
	public JSONArray recurcive() throws Exception {
		ArrayList<String> list = new ArrayList<>();
		Folder root = FolderTaskLogic.getFolder(PART_ROOT, WCUtil.getWTContainerRef());
		Enumeration result = FolderTaskLogic.getSubFolders(root);
		while (result.hasMoreElements()) {
			Folder folder = (Folder) result.nextElement();
			list.add(folder.getFolderPath());
			recurcive(folder, list);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 품목 폴더 가져오기 재귀함수
	 */
	private void recurcive(Folder parent, ArrayList<String> list) throws Exception {
		QueryResult result = FolderHelper.service.findSubFolders(parent);
		while (result.hasMoreElements()) {
			SubFolder folder = (SubFolder) result.nextElement();
			list.add(folder.getFolderPath());
			recurcive(folder, list);
		}
	}

	/**
	 * 최신버전 부품
	 */
	public WTPart latest(String oid) throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(part.getMaster(), config);
		if (result.hasMoreElements()) {
			WTPart latest = (WTPart) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * 최신버전 부품
	 */
	public WTPart latest(WTPartMaster master) throws Exception {
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(master, config);
		if (result.hasMoreElements()) {
			WTPart latest = (WTPart) result.nextElement();
			return latest;
		}
		return null;
	}

	/**
	 * 부품 BOM 구조 호출 함수
	 */
	public ArrayList<WTPart> descendants(WTPart part) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		// root 추가
		list.add(part);
		View view = ViewHelper.service.getView(part.getViewName());
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			list.add(p);
			descendants(p, list);
		}
		return list;
	}

	/**
	 * 품목 BOM 구조 재귀 함수
	 */
	private void descendants(WTPart part, ArrayList<WTPart> list) throws Exception {
		View view = ViewHelper.service.getView(part.getViewName());
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			list.add(p);
			descendants(p, list);
		}
	}

	/**
	 * 완제품 가져오기
	 */
	public ArrayList<WTPart> collectEndItem(WTPart part, ArrayList<WTPart> data) throws Exception {
		ArrayList<WTPart> list = descendants(part);

		for (WTPart p : list) {
			String number = p.getNumber();
			if (number.equals(part.getNumber())) {
				continue;
			}

			if (isCollectNumber(number)) {
				continue;
			}

			if (isTopNumber(number)) {
				if (!data.contains(p)) {
					data.add(p);
				}
			}
		}
		return data;
	}

	/**
	 * PDM에서 채번 유무 확인
	 */
	public static boolean isCollectNumber(String partNumber) {
		boolean reValue = true;
		if (partNumber != null) {
			if (partNumber.length() == 10) {
				if (Pattern.matches("^[0-9]+$", partNumber)) {
					// 숫자임
					reValue = false;
				} else {
					// 숫자아님
					reValue = true;
				}
			} else {
				reValue = true;
			}
		} else {
			// 입력값 없음.
			reValue = true;
		}
		return reValue;
	}

	/**
	 * 최상위 품번인지 체크
	 */
	public static boolean isTopNumber(String number) {
		String firstNumber = number.substring(0, 1);
		String endNumber = number.substring(5, 8);// number.substring(5,number.length());
		if (firstNumber.equals("1") && !endNumber.endsWith("000")) { // 6,7,8이 000인경우
			return true;
		}
		return false;
	}

	public WTPart getLatest(WTPartMaster master) throws Exception {
		String number = master.getNumber();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		int idx_master = query.appendClassList(WTPartMaster.class, false);

		SearchCondition sc = null;

		sc = WorkInProgressHelper.getSearchCondition_CI(WTPart.class);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(WTPart.class, "masterReference.key.id", WTPartMaster.class,
				"thePersistInfo.theObjectIdentifier.id");
		query.appendWhere(sc, new int[] { idx, idx_master });
		query.appendAnd();

		sc = new SearchCondition(WTPartMaster.class, WTPartMaster.NUMBER, "=", number);
		query.appendWhere(sc, new int[] { idx_master });

		QueryResult result = PersistenceHelper.manager.find(query);
		WTPart part = null;
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			part = (WTPart) obj[0];
		}
		boolean isCheckout = WorkInProgressHelper.isCheckedOut(part);
		if (isCheckout) {
			throw new Exception("체크아웃된 부품 = " + part.getNumber());
		}

		boolean isWorkCopy = WorkInProgressHelper.isWorkingCopy(part);
		System.out.println("partHelper - isWorkCopy  : " + isWorkCopy);
		if (isWorkCopy) {
			throw new Exception("작업 복사본 부품 = " + part.getNumber());
		}

		return (WTPart) CommonUtil.getLatestVersion(part);
	}

	/**
	 * 부품 관련 객체 불러오기 메서드
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		if ("doc".equalsIgnoreCase(type)) {
			// 문서
			return JSONArray.fromObject(referenceDoc(part, list));
		} else if ("rohs".equalsIgnoreCase(type)) {
			// ROHS
			return JSONArray.fromObject(referenceRohs(part, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 문서
	 */
	private Object referenceDoc(WTPart part, ArrayList<Map<String, Object>> list) throws Exception {

		QueryResult result = PersistenceHelper.manager.navigate(part, "describedBy", WTPartDescribeLink.class);
		while (result.hasMoreElements()) {
			WTDocument ref = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(ref);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 ROHS
	 */
	private Object referenceRohs(WTPart part, ArrayList<Map<String, Object>> list) throws Exception {

		QueryResult result = PersistenceHelper.manager.navigate(part, "rohs", PartToRohsLink.class);
		while (result.hasMoreElements()) {
			ROHSMaterial ref = (ROHSMaterial) result.nextElement();
			RohsData data = new RohsData(ref);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return list;
	}

	/**
	 * 부품 번호와 버전에 맞는 부품 가져오는 함수
	 */
	public WTPart getPart(String number, String version) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPart.class, true);
		SearchCondition sc = VersionControlHelper.getSearchCondition(WTPart.class, true);
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, WTPart.NUMBER, number);
		QuerySpecUtils.toEqualsAnd(query, idx, WTPart.class, "versionInfo.identifier.versionId", version);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			return part;
		}
		return null;
	}

	/**
	 * 부품 BOM 리스트 (WTPart, View)
	 */
	public ArrayList<WTPart> descentsPart(WTPart part, View view) throws Exception {
		return descentsPart(part, view, null);
	}

	/**
	 * 부품 BOM 리스트 (WTPart)
	 */
	public ArrayList<WTPart> descentsPart(WTPart part) throws Exception {
		return descentsPart(part, (View) part.getView().getObject(), null);
	}

	public ArrayList<WTPart> descentsPart(WTPart part, View view, State state) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		if (!PersistenceHelper.isPersistent(part)) {
			return list;
		}

		WTPartStandardConfigSpec spec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, state);
		WTPartConfigSpec configSpec = WTPartConfigSpec.newWTPartConfigSpec(spec);
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}

			WTPart p = (WTPart) obj[1];
			if (p.getView() == null) {
				System.out.println("뷰가 널인 부품 = " + p.getNumber());
				continue;
			}

			if (view != null && view.getName().equals(((View) p.getView().getObject()).getName())) {
				list.add(p);
			}
		}
		return list;
	}

	/**
	 * 부품과 연결된 도면 찾아오기
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

}