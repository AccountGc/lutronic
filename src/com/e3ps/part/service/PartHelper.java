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
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.service.ECOSearchHelper;
import com.e3ps.common.folder.beans.CommonFolderHelper;
import com.e3ps.common.iba.AttributeKey;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.message.Message;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.common.util.WCUtil;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.part.PartToPartLink;
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
import com.ptc.wpcfg.pdmabstr.PROEDependency;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMDescribeLink;
import wt.epm.structure.EPMReferenceLink;
import wt.epm.structure.EPMStructureHelper;
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
import wt.iba.value.BooleanValue;
import wt.lifecycle.State;
import wt.part.PartDocHelper;
import wt.part.QuantityUnit;
import wt.part.WTPart;
import wt.part.WTPartBaselineConfigSpec;
import wt.part.WTPartConfigSpec;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartStandardConfigSpec;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.vc.VersionControlHelper;
import wt.vc.baseline.Baseline;
import wt.vc.baseline.BaselineMember;
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
		String partNumber = StringUtil.checkNull((String) params.get("partNumber"));
		String partName = StringUtil.checkNull((String) params.get("partName"));
		String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
		String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
		String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
		String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
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
		String preOrder = (String) params.get("preOrder");

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(WTPart.class, true);

		// 상태 임시저장 제외
		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}
		query.appendWhere(
				new SearchCondition(WTPart.class, WTPart.LIFE_CYCLE_STATE, SearchCondition.NOT_EQUAL, "TEMPRARY"),
				new int[] { idx });

//		QuerySpecUtils.toCI(query, idx, WTPart.class);
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

		// 선구매 조건이 선택됫을 경우..
		if ("yes".equals(preOrder) || "no".equals(preOrder)) {

			AttributeDefDefaultView aview = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath("PREORDER");
			if (aview != null) {
				if (query.getConditionCount() > 0) {
					query.appendAnd();
				}

				int idx_ = query.appendClassList(BooleanValue.class, false);
				SearchCondition sc_ = new SearchCondition(
						new ClassAttribute(BooleanValue.class, "theIBAHolderReference.key.id"), "=",
						new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
				sc_.setFromIndicies(new int[] { idx_, idx }, 0);
				sc_.setOuterJoin(0);
				query.appendWhere(sc_, new int[] { idx_, idx });
				query.appendAnd();
				sc_ = new SearchCondition(BooleanValue.class, "definitionReference.key.id", "=",
						aview.getObjectID().getId());
				query.appendWhere(sc_, new int[] { idx_ });
				query.appendAnd();

				if (Boolean.parseBoolean(preOrder)) {
					sc_ = new SearchCondition(BooleanValue.class, BooleanValue.VALUE, SearchCondition.IS_TRUE);
				} else {
					sc_ = new SearchCondition(BooleanValue.class, BooleanValue.VALUE, SearchCondition.IS_FALSE);
				}
				query.appendWhere(sc_, new int[] { idx_ });
			}
		}

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
						WTPart part = PartHelper.service.getPart(master.getNumber());
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

						WTPart part = PartHelper.service.getPart(master.getNumber());
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
			map.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", p.getName());
			map.put("number", p.getNumber());
			map.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			map.put("creator", p.getCreatorFullName());
			map.put("createdDate", p.getCreateTimestamp().toString().substring(0, 10));
			map.put("modifiedDate", p.getModifyTimestamp().toString().substring(0, 10));
			map.put("note", p.getIterationNote());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

//	public List<CommentsData> commentsList(String oid) throws Exception {
//		List<CommentsData> comList = new ArrayList<CommentsData>();
//		WTPart part = (WTPart) CommonUtil.getObject(oid);
//		QuerySpec qs = new QuerySpec();
//		int idx = qs.appendClassList(Comments.class, true);
//
//		qs.appendWhere(new SearchCondition(Comments.class, "wtpartReference.key.id", "=",
//				part.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
//		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "cNum"), false), new int[] { idx });
//		qs.appendOrderBy(new OrderBy(new ClassAttribute(Comments.class, "thePersistInfo.createStamp"), false),
//				new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(qs);
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			CommentsData data = new CommentsData((Comments) obj[0]);
//			comList.add(data);
//		}
//		return comList;
//	}

//	public int getCommentsChild(Comments com) throws Exception {
//		WTPart part = com.getWtpart();
//		int count = 0;
//		QuerySpec qs = new QuerySpec();
//		int idx = qs.appendClassList(Comments.class, true);
//		qs.appendWhere(new SearchCondition(Comments.class, "wtpartReference.key.id", "=",
//				part.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx });
//		qs.appendAnd();
//		qs.appendWhere(new SearchCondition(Comments.class, "oPerson", "=", com.getOwner().getFullName()),
//				new int[] { idx });
//		qs.appendAnd();
//		qs.appendWhere(new SearchCondition(Comments.class, "cNum", "=", com.getCNum()), new int[] { idx });
//		qs.appendAnd();
//		qs.appendWhere(new SearchCondition(Comments.class, "cStep", ">", com.getCStep()), new int[] { idx });
//		qs.appendAnd();
//		qs.appendWhere(new SearchCondition(Comments.class, "deleteYN", "=", "N"), new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(qs);
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			count++;
//		}
//		return count;
//	}

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
		String partNumber = StringUtil.checkNull((String) params.get("partNumber"));
		String partName = StringUtil.checkNull((String) params.get("partName"));
		String createdFrom = StringUtil.checkNull((String) params.get("createdFrom"));
		String createdTo = StringUtil.checkNull((String) params.get("createdTo"));
		String modifiedFrom = StringUtil.checkNull((String) params.get("modifiedFrom"));
		String modifiedTo = StringUtil.checkNull((String) params.get("modifiedTo"));
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
	 * 품목 폴더 가져오기
	 */
	public JSONArray recurcive() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Folder root = FolderTaskLogic.getFolder(PART_ROOT, WCUtil.getWTContainerRef());
		Enumeration result = FolderTaskLogic.getSubFolders(root);
		while (result.hasMoreElements()) {
			Folder folder = (Folder) result.nextElement();
			Map<String, String> map = new HashMap<>();
			map.put("name", folder.getFolderPath());
			map.put("oid", folder.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
			recurcive(folder, list);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 품목 폴더 가져오기 재귀함수
	 */
	private void recurcive(Folder parent, ArrayList<Map<String, String>> list) throws Exception {
		QueryResult result = FolderHelper.service.findSubFolders(parent);
		while (result.hasMoreElements()) {
			SubFolder folder = (SubFolder) result.nextElement();
			Map<String, String> map = new HashMap<>();
			map.put("name", folder.getFolderPath());
			map.put("oid", folder.getPersistInfo().getObjectIdentifier().getStringValue());
			list.add(map);
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
	 * 최신버전 부품인지 확인
	 */
	public boolean isLatest(WTPart part) throws Exception {
		LatestConfigSpec config = new LatestConfigSpec();
		QueryResult result = ConfigHelper.service.filteredIterationsOf(part.getMaster(), config);
		if (result.hasMoreElements()) {
			WTPart latest = (WTPart) result.nextElement();
			if (latest == part) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 부품 BOM 구조 호출 함수
	 */
	public ArrayList<WTPart> descendants(WTPart part) throws Exception {
		ArrayList<WTPart> list = new ArrayList<WTPart>();
		// root 추가
		list.add(part);
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
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
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
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

		return latest(part.getPersistInfo().getObjectIdentifier().getStringValue());
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
		} else if ("eco".equalsIgnoreCase(type)) {
			return JSONArray.fromObject(referenceEco(part, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ECO
	 */
	private Object referenceEco(WTPart part, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate((WTPartMaster) part.getMaster(), "eco", EcoPartLink.class);
		while (qr.hasMoreElements()) {
			EChangeOrder eco = (EChangeOrder) qr.nextElement();
			EcoColumn data = new EcoColumn(eco);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
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

		WTPartStandardConfigSpec spec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
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
	 * 완제품
	 */
	public JSONArray end(String oid, String baseline) throws Exception {
		JSONArray end = new JSONArray();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		ArrayList<WTPart> list = new ArrayList<>();
		if (StringUtil.checkString(baseline)) {
			endRecursive(list, part, baseline);
		} else {
			endRecursive(list, part);
		}

		for (WTPart p : list) {
			Map<String, String> map = new HashMap<>();
			map.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", p.getNumber());
			map.put("name", p.getName());
			map.put("state", p.getLifeCycleState().getDisplay());
			map.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			end.add(map);
		}
		return end;
	}

	/**
	 * 완제품 재귀함수
	 */
	private void endRecursive(ArrayList<WTPart> list, WTPart part) throws Exception {
		WTPartMaster master = (WTPartMaster) part.getMaster();

		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });
		query.appendAnd();
		query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
				new int[] { idx_part });

		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);
		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.size() == 0) {
			if (!isCollectNumber(part.getNumber())) {
				if (isTopNumber(part.getNumber())) {
					if (!list.contains(part)) {
						list.add(part);
					}
				}
			}
		}
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			endRecursive(list, p);
		}
	}

	/**
	 * 완제품 베이스라인 재귀함수
	 */
	private void endRecursive(ArrayList<WTPart> list, WTPart part, String baseline) throws Exception {
		WTPartMaster master = (WTPartMaster) part.getMaster();
		State state = part.getLifeCycleState();
		QuerySpec query = new QuerySpec();
		int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
		int idx_part = query.appendClassList(WTPart.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
		SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
				"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
		sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
		sc.setOuterJoin(0);
		query.appendAnd();
		query.appendWhere(sc, new int[] { idx_usage, idx_part });

//		if (state != null) {
//			query.appendAnd();
//			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
//					new int[] { idx_part });
//		}

		if (baseline != null) {
			Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
			int idx_b = query.addClassList(BaselineMember.class, false);
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
					BaselineMember.class, "roleBObjectRef.key.id"), new int[] { idx_part, idx_b });
			query.appendAnd();
			query.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
					baseLine.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_b });
		}
		QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.size() == 0) {
			if (!isCollectNumber(part.getNumber())) {
				if (isTopNumber(part.getNumber())) {
					if (!list.contains(part)) {
						list.add(part);
					}
				}
			}
		}

		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			endRecursive(list, p, baseline);
		}
	}

	/**
	 * 하위품목
	 */
	public JSONArray lower(String oid, String baseline) throws Exception {
		JSONArray lower = new JSONArray();
		WTPart part = (WTPart) CommonUtil.getObject(oid);

		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		State state = part.getLifeCycleState();

		// 베이스라인
		QueryResult qr = null;
		if (StringUtil.checkString(baseline)) {
			Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
			WTPartBaselineConfigSpec configSpec = WTPartBaselineConfigSpec.newWTPartBaselineConfigSpec(baseLine);
			qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		} else {
//			WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
			WTPartConfigSpec configSpec = WTPartConfigSpec
					.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
			qr = WTPartHelper.service.getUsesWTParts(part, configSpec);
		}

		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			Map<String, String> map = new HashMap<>();
			map.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", p.getNumber());
			map.put("name", p.getName());
			map.put("state", p.getLifeCycleState().getDisplay());
			map.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			lower.add(map);
		}

		return lower;
	}

	/**
	 * 상위품목
	 */
	public JSONArray upper(String oid, String baseline) throws Exception {
		JSONArray upper = new JSONArray();
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		State state = part.getLifeCycleState();
		WTPartMaster master = (WTPartMaster) part.getMaster();
		QueryResult qr = null;
		if (StringUtil.checkString(baseline)) {
			QuerySpec query = new QuerySpec();
			int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
			int idx_part = query.appendClassList(WTPart.class, true);

			QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
			SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
					"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
			sc.setOuterJoin(0);
			query.appendAnd();
			query.appendWhere(sc, new int[] { idx_usage, idx_part });

//			if (state != null) {
//				query.appendAnd();
//				query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state.toString()),
//						new int[] { idx_part });
//			}

			if (baseline != null) {
				Baseline baseLine = (Baseline) CommonUtil.getObject(baseline);
				int idx_b = query.addClassList(BaselineMember.class, false);
				query.appendAnd();
				query.appendWhere(new SearchCondition(WTPart.class, "thePersistInfo.theObjectIdentifier.id",
						BaselineMember.class, "roleBObjectRef.key.id"), new int[] { idx_part, idx_b });
				query.appendAnd();
				query.appendWhere(new SearchCondition(BaselineMember.class, "roleAObjectRef.key.id", "=",
						baseLine.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_b });
			}
			QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
			qr = PersistenceHelper.manager.find(query);
		} else {
			QuerySpec query = new QuerySpec();
			int idx_usage = query.appendClassList(WTPartUsageLink.class, true);
			int idx_part = query.appendClassList(WTPart.class, true);

			QuerySpecUtils.toEqualsAnd(query, idx_usage, WTPartUsageLink.class, "roleBObjectRef.key.id", master);
			SearchCondition sc = new SearchCondition(new ClassAttribute(WTPartUsageLink.class, "roleAObjectRef.key.id"),
					"=", new ClassAttribute(WTPart.class, "thePersistInfo.theObjectIdentifier.id"));
			sc.setFromIndicies(new int[] { idx_usage, idx_part }, 0);
			sc.setOuterJoin(0);
			query.appendAnd();
			query.appendWhere(sc, new int[] { idx_usage, idx_part });
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest", SearchCondition.IS_TRUE, true),
					new int[] { idx_part });

			if (view != null) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
						view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
			}

			if (state != null) {
				query.appendAnd();
				query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
			}
			QuerySpecUtils.toLatest(query, idx_part, WTPart.class);
			QuerySpecUtils.toOrderBy(query, idx_part, WTPart.class, WTPart.NUMBER, true);
			qr = PersistenceHelper.manager.find(query);
		}

		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			Map<String, String> map = new HashMap<>();
			map.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", p.getNumber());
			map.put("name", p.getName());
			map.put("state", p.getLifeCycleState().getDisplay());
			map.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			upper.add(map);
		}

		return upper;
	}

	/**
	 * 품목 속성
	 */
	public Map<String, Object> attr(WTPart part) throws Exception {
		Map<String, Object> result = new HashMap<>();

		String model = IBAUtils.getStringValue(part, "MODEL");
		String productmethod = IBAUtils.getStringValue(part, "PRODUCTMETHOD");
		String deptcode = IBAUtils.getStringValue(part, "DEPTCODE");
		String unit = part.getDefaultUnit().toString().toUpperCase();
		float weight = IBAUtils.getFloatValue(part, "WEIGHT");
		String manufacture = IBAUtils.getStringValue(part, "MANUFACTURE");
		String mat = IBAUtils.getStringValue(part, "MAT");
		String finish = IBAUtils.getStringValue(part, "FINISH");
		String remarks = IBAUtils.getStringValue(part, "REMARKS");
		String specification = IBAUtils.getStringValue(part, "SPECIFICATION");
		String eoNo = IBAUtils.getStringValue(part, "ECONO");
		String eoDate = IBAUtils.getStringValue(part, "ECODATE");
		String chk = IBAUtils.getStringValue(part, "CHK");
		String apr = IBAUtils.getStringValue(part, "APR");
		String rev = IBAUtils.getStringValue(part, "REV");
		String des = IBAUtils.getStringValue(part, "DES");
		String ecoNo = IBAUtils.getStringValue(part, "CHANGENO");
		String ecoDate = IBAUtils.getStringValue(part, "CHANGEDATE");
		boolean preOrder = IBAUtils.getBooleanValue(part, "PREORDER");

		result.put("model", model != null ? model : "");
		result.put("productmethod", productmethod != null ? productmethod : "");
		result.put("deptcode", deptcode != null ? deptcode : "");
		result.put("unit", unit);
		result.put("weight", weight);
		result.put("manufacture", manufacture != null ? manufacture : "");
		result.put("mat", mat != null ? mat : "");
		result.put("finish", finish != null ? finish : "");
		result.put("remarks", remarks != null ? remarks : "");
		result.put("specification", specification != null ? specification : "");
		result.put("eoNo", eoNo != null ? eoNo : "");
		result.put("eoDate", eoDate != null ? eoDate : "");
		result.put("chk", chk != null ? chk : "");
		result.put("apr", apr != null ? apr : "");
		result.put("rev", rev != null ? rev : "");
		result.put("des", des != null ? des : "");
		result.put("ecoNo", ecoNo != null ? ecoNo : "");
		result.put("ecoDate", ecoDate != null ? ecoDate : "");
		result.put("preOrder", preOrder);
		return result;
	}

	/**
	 * 부품과 연결된 도면 가져오는 함수
	 */
	public EPMDocument getEPMDocument(WTPart part) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(part, EPMBuildRule.BUILD_SOURCE_ROLE, EPMBuildRule.class);
		if (qr.hasMoreElements()) {
			EPMDocument e = (EPMDocument) qr.nextElement();
			return e;
		}
		return null;
	}

	/**
	 * 3D모델과 연결된 2D가져오는 함수
	 */
	public EPMDocument getEPMDocument2D(EPMDocument epm) throws Exception {
		EPMDocumentMaster m = (EPMDocumentMaster) epm.getMaster();
		QueryResult qr = EPMStructureHelper.service.navigateReferencedBy(m, null, false);
		while (qr.hasMoreElements()) {
			EPMReferenceLink ref = (EPMReferenceLink) qr.nextElement();
			if (ref.getDepType() == PROEDependency.DEP_T_DRAW) {
				return ref.getReferencedBy();
			}
		}
		return null;
	}

	/**
	 * 3D모델과 연결된 2D가져오는 함수 부품으로 바로 찾기 원할 경우
	 */
	public EPMDocument getEPMDocument2D(WTPart part) throws Exception {
		EPMDocument epm = getEPMDocument(part);
		if (epm != null) {
			EPMDocumentMaster m = (EPMDocumentMaster) epm.getMaster();
			QueryResult qr = EPMStructureHelper.service.navigateReferencedBy(m, null, false);
			while (qr.hasMoreElements()) {
				EPMReferenceLink ref = (EPMReferenceLink) qr.nextElement();
				if (ref.getDepType() == PROEDependency.DEP_T_DRAW) {
					return ref.getReferencedBy();
				}
			}
		}
		return null;
	}

	/**
	 * 품목등록시 SEQ 리스트 보기
	 */
	public Map<String, Object> seq(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, String>> list = new ArrayList<>();
		String partNumber = (String) params.get("partNumber");
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(WTPart.class, true);
		QuerySpecUtils.toLikeRightAnd(query, idx, WTPart.class, WTPart.NUMBER, partNumber);
		QuerySpecUtils.toLatest(query, idx, WTPart.class);

		QuerySpecUtils.toOrderBy(query, idx, WTPart.class, WTPart.MODIFY_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPart part = (WTPart) obj[0];
			Map<String, String> data = new HashMap<>();
			data.put("number", part.getNumber());
			data.put("name", part.getName());
			data.put("location", part.getLocation());
			data.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			data.put("state", part.getLifeCycleState().getDisplay());
			data.put("creator", part.getCreatorFullName());
			data.put("createdDate", part.getCreateTimestamp().toString().substring(0, 10));
			data.put("modifitedDate", part.getModifyTimestamp().toString().substring(0, 10));
			data.put("remarks", IBAUtils.getStringValue(part, "REMARKS"));
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

	/**
	 * 채번용 페이지
	 */
	public JSONArray load(Map<String, Object> params) throws Exception {
		JSONArray list = new JSONArray();
		String oid = (String) params.get("oid");
		return descendants(list, oid);
	}

	/**
	 * 채번용 페이지
	 */
	private JSONArray descendants(JSONArray list, String oid) throws Exception {
		WTPart root = (WTPart) CommonUtil.getObject(oid);
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", root.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("level", 1);
		rootNode.put("number", root.getNumber());
		rootNode.put("name", root.getName());
		rootNode.put("version", root.getVersionIdentifier().getSeries().getValue() + "."
				+ root.getIterationIdentifier().getSeries().getValue());
		rootNode.put("partName4", root.getName());
		// 숫자일 경우 채번됨..
		if (!isUpdate(root.getNumber())) {
			rootNode.put("disabled", true);
			rootNode.put("checked", false);
		} else {
			rootNode.put("disabled", false);
			rootNode.put("checked", true);
		}
		String viewName = root.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		State state = root.getLifeCycleState();
		JSONArray children = new JSONArray();
//		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(root, configSpec);
		int level = 2;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];

			JSONObject node = new JSONObject();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("partName4", p.getName());
			if (!isUpdate(p.getNumber())) {
				node.put("disabled", true);
				node.put("checked", false);
			} else {
				node.put("disabled", false);
				node.put("checked", true);
			}
			boolean isLazy = isLazy(p, view, state);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}

			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}

	/**
	 * 채번 가능 여부
	 */
	private boolean isUpdate(String number) throws Exception {
		boolean isUpdate = true;
		if (number.length() == 10) {
			if (Pattern.matches("^[0-9]+$", number)) {
				isUpdate = false;
			} else {
				isUpdate = true;
			}
		} else {
			isUpdate = true;
		}
		return isUpdate;
	}

	/**
	 * 채번 뷰에서 레이지 로드
	 */
	public ArrayList<Map<String, Object>> lazyLoad(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		String oid = (String) params.get("oid");
		int level = (int) params.get("level");
		return descendants(list, oid, level);
	}

	/**
	 * 채번 뷰에서 레이지 로드
	 */
	private ArrayList<Map<String, Object>> descendants(ArrayList<Map<String, Object>> list, String oid, int level)
			throws Exception {
		WTPart part = (WTPart) CommonUtil.getObject(oid);
		String viewName = part.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		State state = part.getLifeCycleState();
//		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(part, configSpec);
		++level;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			WTPart p = (WTPart) obj[1];
			Map<String, Object> node = new HashMap<>();
			node.put("oid", p.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("level", level);
			node.put("number", p.getNumber());
			node.put("name", p.getName());
			node.put("version", p.getVersionIdentifier().getSeries().getValue() + "."
					+ p.getIterationIdentifier().getSeries().getValue());
			node.put("partName4", p.getName());
			if (!isUpdate(p.getNumber())) {
				node.put("disabled", true);
				node.put("checked", false);
			} else {
				node.put("disabled", false);
				node.put("checked", true);
			}
			boolean isLazy = isLazy(p, view, state);
			if (!isLazy) {
				node.put("isLazy", false);
				node.put("children", new JSONArray());
			} else {
				node.put("isLazy", true);
			}
			list.add(node);
		}
		return list;
	}

	/**
	 * 정전개 하위가 잇는지 없는지 판단
	 */
	private boolean isLazy(WTPart parent, View view, State state) throws Exception {
//		WTPartStandardConfigSpec configSpec = WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null);
		WTPartConfigSpec configSpec = WTPartConfigSpec
				.newWTPartConfigSpec(WTPartStandardConfigSpec.newWTPartStandardConfigSpec(view, null));
		QueryResult result = WTPartHelper.service.getUsesWTParts(parent, configSpec);
		boolean isLazy = false;
		while (result.hasMoreElements()) {
			Object obj[] = (Object[]) result.nextElement();
			if (!(obj[1] instanceof WTPart)) {
				continue;
			}
			isLazy = true;
			break;
		}
		return isLazy;
	}

	/**
	 * 품목 단위 JSON 형태로
	 */
	public JSONArray toUnitJson() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		QuantityUnit[] unitList = QuantityUnit.getQuantityUnitSet();
		for (QuantityUnit unit : unitList) {
			Map<String, String> map = new HashMap<>();
			map.put("key", unit.toString());
			map.put("value", unit.getDisplay() + " / " + unit.toString());
			list.add(map);
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * 품목 변경 이력 생성
	 */
	public JSONArray viewHistory(WTPart part) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		// 선택된게 이전으로 선택된거
		// 최신
		frontPart(part, list);

		// 대상의 중심은 ECO가 없다고 판단을한다??
		Map<String, String> map = new HashMap<>();
		map.put("selected", "YES");
		map.put("eco_oid", "");
		map.put("eoNumber", "");
		map.put("eo_createdDate_txt", "");
		map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
		map.put("number", part.getNumber());
		map.put("name", part.getName());
		map.put("state", part.getLifeCycleState().getDisplay());
		map.put("version", part.getVersionIdentifier().getSeries().getValue());
		map.put("creator", part.getCreatorFullName());
		map.put("createdDate_txt", part.getCreateTimestamp().toString().substring(0, 10));
		list.add(map);
		backPart(part, list);

		return JSONArray.fromObject(list);
	}

	/**
	 * 변경이력 가져오기 선택된 품목이 일단 이전 품번 일경우..
	 */
	private void frontPart(WTPart part, ArrayList<Map<String, String>> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartToPartLink.class, true);
		QuerySpecUtils.toEquals(query, idx, PartToPartLink.class, "roleAObjectRef.key.id", part.getMaster());
		QuerySpecUtils.toOrderBy(query, idx, PartToPartLink.class, PartToPartLink.CREATE_TIMESTAMP, false);
		// 실제로는 무조건 하나 인거 같은데..
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			PartToPartLink link = (PartToPartLink) obj[0];
			Map<String, String> map = new HashMap<>();
			WTPartMaster nextMaster = link.getAfter();
			String nextVersion = link.getAfterVersion();
			WTPart nextPart = getPart(nextMaster.getNumber(), nextVersion);
			EChangeOrder eco = link.getEco();
			if (nextPart != null) {
				map.put("selected", "NO");
				if (eco != null) {
					map.put("eco_oid", eco.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("eoNumber", eco.getEoNumber());
					map.put("eo_createdDate_txt", eco.getCreateTimestamp().toString().substring(0, 10));
				} else {
					map.put("eco_oid", "");
					map.put("eoNumber", "");
					map.put("eo_createdDate_txt", "");
				}
				map.put("part_oid", nextPart.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("number", nextPart.getNumber());
				map.put("name", nextPart.getName());
				map.put("state", nextPart.getLifeCycleState().getDisplay());
				map.put("version", nextPart.getVersionIdentifier().getSeries().getValue());
				map.put("creator", nextPart.getCreatorFullName());
				map.put("createdDate_txt", nextPart.getCreateTimestamp().toString().substring(0, 10));
				frontPart(nextPart, list);
				list.add(map);
			}
		}
	}

	/**
	 * 변경이력 가져오기 선택된 품목이 일단 다음 품번 일경우..
	 */
	private void backPart(WTPart part, ArrayList<Map<String, String>> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(PartToPartLink.class, true);
		QuerySpecUtils.toEquals(query, idx, PartToPartLink.class, "roleBObjectRef.key.id", part.getMaster());
		QuerySpecUtils.toOrderBy(query, idx, PartToPartLink.class, PartToPartLink.CREATE_TIMESTAMP, false);
		// 실제로는 무조건 하나 인거 같은데..
		QueryResult qr = PersistenceHelper.manager.find(query);
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			PartToPartLink link = (PartToPartLink) obj[0];
			Map<String, String> map = new HashMap<>();
			WTPartMaster preMaster = link.getPrev();
			String preVersion = link.getPreVersion();
			WTPart prePart = getPart(preMaster.getNumber(), preVersion);
			EChangeOrder eco = link.getEco();
			if (prePart != null) {
				if (eco != null) {
					map.put("selected", "NO");
					map.put("eco_oid", eco.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("eoNumber", eco.getEoNumber());
					map.put("eo_createdDate_txt", eco.getCreateTimestamp().toString().substring(0, 10));
				} else {
					map.put("eco_oid", "");
					map.put("eoNumber", "");
					map.put("eo_createdDate_txt", "");
				}
				map.put("part_oid", prePart.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("number", prePart.getNumber());
				map.put("name", prePart.getName());
				map.put("state", prePart.getLifeCycleState().getDisplay());
				map.put("version", prePart.getVersionIdentifier().getSeries().getValue());
				map.put("creator", prePart.getCreatorFullName());
				map.put("createdDate_txt", prePart.getCreateTimestamp().toString().substring(0, 10));
				backPart(prePart, list);
				list.add(map);
			}
		}
	}
}
