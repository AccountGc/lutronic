package com.e3ps.change.activity.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import com.e3ps.change.DocumentActivityLink;
import com.e3ps.change.ECOChange;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeActivityDefinition;
import com.e3ps.change.EChangeActivityDefinitionRoot;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.dto.DefDTO;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.service.DocumentHelper;
import com.e3ps.part.service.PartHelper;

import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.epm.build.EPMBuildHistory;
import wt.epm.build.EPMBuildRule;
import wt.epm.structure.EPMReferenceLink;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;
import wt.vc.VersionControlHelper;
import wt.vc.config.ConfigHelper;
import wt.vc.config.LatestConfigSpec;

public class ActivityHelper {

	public static final ActivityService service = ServiceFactory.getService(ActivityService.class);
	public static final ActivityHelper manager = new ActivityHelper();

	/**
	 * 루트 별 설계변경 활동 리스트
	 */
	public ArrayList<DefDTO> root() throws Exception {
		ArrayList<DefDTO> list = new ArrayList<DefDTO>();

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinitionRoot.class, true);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinitionRoot.class,
				EChangeActivityDefinitionRoot.SORT_NUMBER, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] o = (Object[]) result.nextElement();
			EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) o[0];
			DefDTO dto = new DefDTO(def);
			list.add(dto);
		}
		return list;
	}

	/**
	 * 설계변경 활동 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<ActDTO> list = new ArrayList<>();
		String root = (String) params.get("root");

		if (!StringUtil.checkString(root)) {
			map.put("list", list);
			map.put("topListCount", 1);
			map.put("pageSize", 30);
			map.put("total", 0);
			map.put("sessionid", 0L);
			map.put("curPage", 1);
			return map;
		}

		EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(root);

		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinition.class, true);

		QuerySpecUtils.toEquals(query, idx, EChangeActivityDefinition.class, "rootReference.key.id", def);
		QuerySpecUtils.toOrderBy(query, idx, EChangeActivityDefinition.class, EChangeActivityDefinition.SORT_NUMBER,
				false);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivityDefinition act = (EChangeActivityDefinition) obj[0];
			ActDTO dto = new ActDTO(act);
			list.add(dto);
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
	 * 설변 활동 담기
	 */
	public Map<String, String> getActMap() throws Exception {
		Map<String, String> map = new HashMap<String, String>();
//		map.put("ORDER_NUMBER", "진채번");
		map.put("REVISE_BOM", "개정/BOM 변경");
		map.put("DOCUMENT", "산출물 등록");
		return map;
	}

	/**
	 * 설변 활동 JSON
	 */
	public JSONArray toJsonActMap() throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> actMap = getActMap();

		Iterator it = actMap.keySet().iterator();
		while (it.hasNext()) {
			Map<String, String> map = new HashMap<>();
			String key = (String) it.next();
			String value = actMap.get(key);
			map.put("key", key);
			map.put("value", value);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 설변 활동 타입에 맞는 설변타입명 반환
	 */
	public String getActName(String act) throws Exception {
		Map<String, String> map = getActMap();
		return map.get(act);
	}

	/**
	 * 루트에 활동이 있는지 확인, true 있음 , false 없음
	 */
	public boolean dependency(String oid) throws Exception {
		EChangeActivityDefinitionRoot def = (EChangeActivityDefinitionRoot) CommonUtil.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.addClassList(EChangeActivityDefinition.class, true);
		QuerySpecUtils.toEquals(query, idx, EChangeActivityDefinition.class, "rootReference.key.id", def);
		QueryResult result = PersistenceHelper.manager.find(query);
		return result.size() > 0 ? true : false;
	}

	/**
	 * ECA 활동 목록 가져오기
	 */
	public ArrayList<ActDTO> activityList(String oid) throws Exception {
		ArrayList<ActDTO> list = new ArrayList<ActDTO>();
		ECOChange e = (ECOChange) CommonUtil.getObject(oid);
		ArrayList<EChangeActivity> result = colletActivity(e);

		for (EChangeActivity eca : result) {
//			ECAData data = new ECAData(eca);
//			ImageIcon icon = ChangeUtil.getECAStateImg(eca.getFinishDate(), data.state);
//			data.setIcon(icon);
//			list.add(data);
		}
		return list;
	}

	/**
	 * ECO 및 EO에 관련된 ECA 리스트
	 */
	public ArrayList<EChangeActivity> colletActivity(ECOChange eo) throws Exception {

		ArrayList<EChangeActivity> list = new ArrayList<EChangeActivity>();

		long id = eo.getPersistInfo().getObjectIdentifier().getId();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeActivity.class, true);
		int idx_n = query.appendClassList(NumberCode.class, false);

		SearchCondition sc = null;

		ClassAttribute ca = new ClassAttribute(EChangeActivity.class, EChangeActivity.STEP);
		ClassAttribute ca_n = new ClassAttribute(NumberCode.class, NumberCode.CODE);
		sc = new SearchCondition(ca, "=", ca_n);
		sc.setFromIndicies(new int[] { idx, idx_n }, 0);
		sc.setOuterJoin(0);
		query.appendWhere(sc, new int[] { idx, idx_n });

		query.appendAnd();
		query.appendWhere(new SearchCondition(EChangeActivity.class, "eoReference.key.id", SearchCondition.EQUAL, id),
				new int[] { idx });

		query.appendOrderBy(new OrderBy(new ClassAttribute(NumberCode.class, "sort"), false), new int[] { idx_n });
		query.appendOrderBy(new OrderBy(new ClassAttribute(EChangeActivity.class, "sortNumber"), false),
				new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivity eca = (EChangeActivity) obj[0];
			list.add(eca);
		}
		return list;
	}

	/**
	 * 접속한 사용자의 ECA 활동 리스트
	 */
	public Map<String, Object> eca(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		WTUser sessionUser = CommonUtil.sessionUser();
		String name = (String) params.get("name");
		String submiterOid = (String) params.get("submiterOid");
		String receiveFrom = (String) params.get("receiveFrom");
		String receiveTo = (String) params.get("receiveTo");

		QuerySpec query = new QuerySpec();
		int idx_eca = query.appendClassList(EChangeActivity.class, true);
		int idx_eco = query.appendClassList(EChangeOrder.class, false);

		QuerySpecUtils.toInnerJoin(query, EChangeActivity.class, EChangeOrder.class, "eoReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx_eca, idx_eco);

		// 관리자가 아닐경우
		if (!CommonUtil.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx_eca, EChangeActivity.class, "activeUserReference.key.id",
					sessionUser);
		}
		QuerySpecUtils.toEqualsAnd(query, idx_eca, EChangeActivity.class, "state.state", "INWORK");
		QuerySpecUtils.toLikeAnd(query, idx_eco, EChangeOrder.class, EChangeOrder.EO_NAME, name);
		QuerySpecUtils.toCreatorQuery(query, idx_eca, EChangeActivity.class, submiterOid);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx_eca, EChangeActivity.class, EChangeActivity.CREATE_TIMESTAMP,
				receiveFrom, receiveTo);
		QuerySpecUtils.toOrderBy(query, idx_eca, EChangeActivity.class, EChangeActivity.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EChangeActivity eca = (EChangeActivity) obj[0];
			EChangeOrder eo = (EChangeOrder) eca.getEo();
			Map<String, Object> data = new HashMap<>();
			data.put("oid", eca.getPersistInfo().getObjectIdentifier().getStringValue());
			data.put("number", eo.getEoNumber());
			data.put("name", eo.getEoName());
			data.put("step", NumberCodeHelper.manager.getNumberCodeName(eca.getStep(), "EOSTEP"));
			data.put("finishDate", eca.getFinishDate());
			data.put("state", eca.getLifeCycleState().getDisplay());
			data.put("activityName", getActName(eca.getActiveType()));
			data.put("activityType", eca.getActiveType());
			data.put("activityUser", eca.getActiveUser().getFullName());
			data.put("createdDate", eca.getCreateTimestamp());
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
	 * 설변활동 - 산출물 링크
	 */
	public JSONArray docList(String oid) throws Exception {
		return docList((EChangeActivity) CommonUtil.getObject(oid));
	}

	/**
	 * 설변활동 - 산출물 링크
	 */
	public JSONArray docList(EChangeActivity eca) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QueryResult qr = PersistenceHelper.manager.navigate(eca, "doc", DocumentActivityLink.class, false);
		while (qr.hasMoreElements()) {
			DocumentActivityLink link = (DocumentActivityLink) qr.nextElement();
			WTDocumentMaster m = link.getDoc();
			WTDocument doc = DocumentHelper.manager.latest(m);
			Map<String, String> map = new HashMap<>();
			map.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("oid", doc.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", doc.getName());
//			map.put("number", doc.getNumber());
			map.put("number", IBAUtils.getStringValue(doc, "INTERALNUMBER"));
			map.put("version", doc.getVersionIdentifier().getSeries().getValue() + "."
					+ doc.getIterationIdentifier().getSeries().getValue());
			map.put("creator", doc.getCreatorFullName());
			map.put("writer", IBAUtils.getStringValue(doc, "DSGN"));
			map.put("state", doc.getLifeCycleState().getDisplay());
			map.put("createdDate_txt", doc.getCreateTimestamp().toString().substring(0, 10));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * ECO 활동 대상 품목들
	 */
	public ArrayList<Map<String, Object>> getEcoRevisePart(String oid) throws Exception {
		Persistable per = CommonUtil.getObject(oid);
		EChangeOrder eco = null;
		if (per instanceof EChangeActivity) {
			EChangeActivity eca = (EChangeActivity) per;
			eco = (EChangeOrder) eca.getEo();
		} else if (per instanceof EChangeOrder) {
			eco = (EChangeOrder) per;
		}
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		QuerySpec query = new QuerySpec();
		int idx_link = query.appendClassList(EcoPartLink.class, true);
		int idx_m = query.appendClassList(WTPartMaster.class, false);

		ClassAttribute ca_m = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");
		ClassAttribute ca_link = new ClassAttribute(EcoPartLink.class, "roleAObjectRef.key.id");

		query.appendWhere(new SearchCondition(ca_m, "=", ca_link), new int[] { idx_m, idx_link });
		query.appendAnd();
		query.appendWhere(new SearchCondition(EcoPartLink.class, "roleBObjectRef.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_link });

		QuerySpecUtils.toOrderBy(query, idx_m, WTPartMaster.class, WTPartMaster.NUMBER, false);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EcoPartLink link = (EcoPartLink) obj[0];
			WTPartMaster master = link.getPart();

			Map<String, Object> map = new HashMap<String, Object>();

//			boolean isDummy = EChangeUtils.isDummy(master.getNumber());
//			// 더미 제외
//			if (dummyCheck && isDummy) {
//				if (isDummy) {
//					continue;
//				}
//			}

			String version = link.getVersion();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), version);
			boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
			boolean isFour = part.getNumber().startsWith("4"); // 4로 시작하는것은 무조건 모두 새품번
			// 승인됨의 경우 개정 프로세스
			// 나머지 작업중인건 개정된 부품쪽으로 이동f

			String link_oid = link.getPersistInfo().getObjectIdentifier().getStringValue();
			String part_oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
			boolean isRevise = link.isRevise(); // 개정여부?
			boolean preOrder = link.getPreOrder();
			map.put("oid", oid); // 활동 혹은 ECO OID
			map.put("link_oid", link_oid);
			map.put("delivery", link.getDelivery());
			map.put("complete", link.getComplete());
			map.put("inner", link.getInner());
			map.put("order", link.getOrders());
			map.put("part_state_code", link.getPartStateCode());
			map.put("preOrder", preOrder);
			if (isApproved) {
				// 개정 데이터
				map.put("part_oid", part_oid);
				map.put("part_number", part.getNumber());
				map.put("part_creator", part.getCreatorFullName());
				map.put("part_name", part.getName());
				map.put("part_version", part.getVersionIdentifier().getSeries().getValue() + "."
						+ part.getIterationIdentifier().getSeries().getValue());
				map.put("part_state", part.getLifeCycleState().getDisplay());

				// 개정된 데이터가 없을 경우
				if (!isRevise && !isFour) {
					map.put("next_oid", "");
					map.put("group", "");
					map.put("next_number", "개정된 데이터가 없습니다.");
					map.put("next_name", "개정된 데이터가 없습니다.");
					map.put("next_state", "개정된 데이터가 없습니다.");
					map.put("next_version", "개정된 데이터가 없습니다.");
					map.put("next_creator", "개정된 데이터가 없습니다.");
					map.put("epm_number", "개정된 데이터가 없습니다.");
					map.put("reference", "개정된 데이터가 없습니다.");
					map.put("after", true);
					map.put("afterMerge", true);
				} else {

					WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
					String group = EChangeUtils.manager.getPartGroup(next_part, eco);

					// 개정데이터가 있을경우
					map.put("group", group);
					map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("next_number", next_part.getNumber());
					map.put("next_name", next_part.getName());
					map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
							+ next_part.getIterationIdentifier().getSeries().getValue());
					map.put("next_creator", next_part.getCreatorFullName());
					map.put("next_state", next_part.getLifeCycleState().getDisplay());
					map.put("after", false);
					map.put("afterMerge", false);
				}
				map.put("prev", false);
			} else {

				// 이전 부품
				WTPart pre_part = EChangeUtils.manager.getEcoPrePart(eco, part);

				String group = EChangeUtils.manager.getPartGroup(part, eco);

				// 개정데이터가 있을경우
				map.put("group", group);
				map.put("next_oid", part_oid);
				map.put("next_number", part.getNumber());
				map.put("next_name", part.getName());
				map.put("next_version", part.getVersionIdentifier().getSeries().getValue() + "."
						+ part.getIterationIdentifier().getSeries().getValue());
				map.put("next_creator", part.getCreatorFullName());
				map.put("next_state", part.getLifeCycleState().getDisplay());

				if (pre_part == null) {
					map.put("part_oid", "");
					map.put("part_number", "개정전 데이터가 없습니다.");
					map.put("part_name", "개정전 데이터가 없습니다.");
					map.put("part_state", "개정전 데이터가 없습니다.");
					map.put("part_version", "개정전 데이터가 없습니다.");
					map.put("part_creator", "개정전 데이터가 없습니다.");

					map.put("preMerge", true);
					map.put("prev", true);
				} else {
					map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("part_number", pre_part.getNumber());
					map.put("part_name", pre_part.getName());
					map.put("part_state", pre_part.getLifeCycleState().getDisplay());
					map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
							+ pre_part.getIterationIdentifier().getSeries().getValue());
					map.put("part_creator", pre_part.getCreatorFullName());
					map.put("preMerge", false);
					map.put("prev", false);
				}
				map.put("after", false);
			}
			list.add(map);
		}

		return list;
	}

	/**
	 * ECO 개정 대상 품목 정보 가져오기
	 */
	public ArrayList<Map<String, Object>> load(Map<String, Object> params) throws Exception {
		ArrayList<String> list = (ArrayList<String>) params.get("list");
		ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		for (int i = 0; list != null && i < list.size(); i++) {
			String oid = (String) list.get(i);
			EcoPartLink link = (EcoPartLink) CommonUtil.getObject(oid);
			WTPart part = PartHelper.manager.getPart(link.getPart().getNumber(), link.getVersion());
			EPMDocument epm = PartHelper.manager.getEPMDocument(part);
			Map<String, Object> map = new HashMap<>();
			map.put("part_name", part.getName());
			map.put("part_number", part.getNumber());
			map.put("state", part.getLifeCycleState().getDisplay());
			map.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			map.put("latest", CommonUtil.isLatestVersion(part));
			if (epm != null) {
				map.put("epm_number", epm.getNumber());
				map.put("epm_oid", epm.getPersistInfo().getObjectIdentifier().getStringValue());
			}
			map.put("link_oid", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			data.add(map);
		}

		return data;
	}

	/**
	 * ECO 연관 CR 가져오기
	 */
	public ArrayList<Map<String, String>> getEcoRefCr(String oid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();

		Persistable per = CommonUtil.getObject(oid);
		EChangeOrder eco = null;
		if (per instanceof EChangeActivity) {
			EChangeActivity eca = (EChangeActivity) per;
			eco = (EChangeOrder) eca.getEo();
		} else if (per instanceof EChangeOrder) {
			eco = (EChangeOrder) per;
		}

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(RequestOrderLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, RequestOrderLink.class, RequestOrderLink.ECO_TYPE, eco.getEoType());
		QuerySpecUtils.toEqualsAnd(query, idx, RequestOrderLink.class, "roleAObjectRef.key.id", eco);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			RequestOrderLink link = (RequestOrderLink) obj[0];
			Map<String, String> map = new HashMap<>();
			EChangeRequest cr = link.getEcr();
			map.put("oid", cr.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", cr.getEoNumber());
			list.add(map);
		}

		return list;
	}

	/**
	 * 설변활동시 자동으로 이전 품목 가져와서 등록
	 */
	public WTPart prevPart(String number) throws Exception {
		WTPart prevPart = null;

		// 10자리가 아닐 경우 패스.
		if (number.length() != 10) {
			return null;
		}

		// 숫자아님
		if (!Pattern.matches("^[0-9]+$", number)) {
			return null;
		}

		String end = number.substring(8);
		// 최신품목임 00 리비전
		if ("00".equals(end)) {
			return null;
		}

		DecimalFormat df = new DecimalFormat("00");

		int prevSeq = Integer.parseInt(end) - 1;
		String front = number.substring(0, 8);
		String prevNumber = front + df.format(prevSeq);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(WTPartMaster.class, true);
		QuerySpecUtils.toEquals(query, idx, WTPartMaster.class, WTPartMaster.NUMBER, prevNumber);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			WTPartMaster m = (WTPartMaster) obj[0];
			LatestConfigSpec config = new LatestConfigSpec();
			QueryResult qr = ConfigHelper.service.filteredIterationsOf(m, config);
			if (qr.hasMoreElements()) {
				WTPart latest = (WTPart) qr.nextElement();
				return latest;
			}
		}

		return prevPart;
	}

	/**
	 * 해당품목과 관련된 3D, 2D 참조 도면
	 */
	public Map<String, ArrayList<Map<String, Object>>> reference(String oid) throws Exception {
		Map<String, ArrayList<Map<String, Object>>> result = new HashMap<>();

		if (!StringUtil.checkString(oid)) {
			return result;
		}
		WTPart part = (WTPart) CommonUtil.getObject(oid);

		ArrayList<Map<String, Object>> list3d = new ArrayList<>();
		ArrayList<Map<String, Object>> list2d = new ArrayList<>();

		QueryResult qr = null;
		if (VersionControlHelper.isLatestIteration(part)) {
			qr = PersistenceHelper.manager.navigate(part, "buildSource", EPMBuildRule.class);
		} else {
			qr = PersistenceHelper.manager.navigate(part, "builtBy", EPMBuildHistory.class);
		}
		while (qr.hasMoreElements()) {
			EPMDocument epm = (EPMDocument) qr.nextElement();
			Map<String, Object> map3d = new HashMap<>();
			map3d.put("name", epm.getName());
			map3d.put("number", epm.getNumber());
			map3d.put("version", epm.getVersionIdentifier().getSeries().getValue());
			map3d.put("state", epm.getLifeCycleState().getDisplay());
			list3d.add(map3d);

			EPMDocumentMaster m = (EPMDocumentMaster) epm.getMaster();
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EPMReferenceLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, EPMReferenceLink.class, "roleBObjectRef.key.id", m);
			QueryResult rs = PersistenceHelper.manager.find(query);
			while (rs.hasMoreElements()) {
				Object[] obj = (Object[]) rs.nextElement();
				EPMReferenceLink link = (EPMReferenceLink) obj[0];
				EPMDocument epm2D = link.getReferencedBy();
				Map<String, Object> map2d = new HashMap<>();
				map2d.put("name", epm2D.getName());
				map2d.put("number", epm2D.getNumber());
				map2d.put("version", epm2D.getVersionIdentifier().getSeries().getValue());
				map2d.put("state", epm2D.getLifeCycleState().getDisplay());
				map2d.put("refType", link.getReferenceType().getDisplay());
				list2d.add(map2d);
			}
		}
		result.put("3d", list3d);
		result.put("2d", list2d);

		return result;
	}

	/**
	 * 설변 대상 품목
	 */
	public JSONArray getEcoParts(String oid) throws Exception {
		Persistable per = CommonUtil.getObject(oid);
		EChangeOrder eco = null;
		if (per instanceof EChangeActivity) {
			EChangeActivity eca = (EChangeActivity) per;
			eco = (EChangeOrder) eca.getEo();
		} else if (per instanceof EChangeOrder) {
			eco = (EChangeOrder) per;
		}
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

		QuerySpec query = new QuerySpec();
		int idx_link = query.appendClassList(EcoPartLink.class, true);
		int idx_m = query.appendClassList(WTPartMaster.class, false);

		ClassAttribute ca_m = new ClassAttribute(WTPartMaster.class, "thePersistInfo.theObjectIdentifier.id");
		ClassAttribute ca_link = new ClassAttribute(EcoPartLink.class, "roleAObjectRef.key.id");

		query.appendWhere(new SearchCondition(ca_m, "=", ca_link), new int[] { idx_m, idx_link });
		query.appendAnd();
		query.appendWhere(new SearchCondition(EcoPartLink.class, "roleBObjectRef.key.id", "=",
				eco.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_link });

		QuerySpecUtils.toOrderBy(query, idx_m, WTPartMaster.class, WTPartMaster.NUMBER, false);
		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EcoPartLink link = (EcoPartLink) obj[0];
			WTPartMaster master = link.getPart();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), link.getVersion());
			Map<String, Object> map = new HashMap<>();

			map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("part_name", part.getName());
			map.put("part_number", part.getNumber());
			map.put("part_state", part.getLifeCycleState().getDisplay());
			map.put("part_version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			map.put("part_createdDate", part.getCreateTimestamp().toString().substring(0, 10));
			map.put("part_creator", part.getCreatorFullName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 설변활동 개수
	 */
	public int count() throws Exception {
		WTUser sessionUser = CommonUtil.sessionUser();
		QuerySpec query = new QuerySpec();
		int idx_eca = query.appendClassList(EChangeActivity.class, true);
		int idx_eco = query.appendClassList(EChangeOrder.class, false);

		QuerySpecUtils.toInnerJoin(query, EChangeActivity.class, EChangeOrder.class, "eoReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx_eca, idx_eco);

		// 관리자가 아닐경우
		if (!CommonUtil.isAdmin()) {
			QuerySpecUtils.toEqualsAnd(query, idx_eca, EChangeActivity.class, "activeUserReference.key.id",
					sessionUser);
		}
		QuerySpecUtils.toEqualsAnd(query, idx_eca, EChangeActivity.class, "state.state", "INWORK");
		QuerySpecUtils.toOrderBy(query, idx_eca, EChangeActivity.class, EChangeActivity.CREATE_TIMESTAMP, true);
		return PersistenceHelper.manager.find(query).size();
	}
}
