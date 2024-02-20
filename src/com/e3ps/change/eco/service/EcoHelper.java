package com.e3ps.change.eco.service;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.aspose.cells.BorderType;
import com.aspose.cells.Cell;
import com.aspose.cells.CellBorderType;
import com.aspose.cells.Color;
import com.aspose.cells.Font;
import com.aspose.cells.Picture;
import com.aspose.cells.PlacementType;
import com.aspose.cells.Row;
import com.aspose.cells.Style;
import com.aspose.cells.StyleFlag;
import com.aspose.cells.TextAlignmentType;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.e3ps.change.ECPRRequest;
import com.e3ps.change.ECRMRequest;
import com.e3ps.change.EChangeActivity;
import com.e3ps.change.EChangeNotice;
import com.e3ps.change.EChangeOrder;
import com.e3ps.change.EChangeRequest;
import com.e3ps.change.EOCompletePartLink;
import com.e3ps.change.EcnEco;
import com.e3ps.change.EcoPartLink;
import com.e3ps.change.EcoToEcprLink;
import com.e3ps.change.EcrmToEcoLink;
import com.e3ps.change.RequestOrderLink;
import com.e3ps.change.activity.dto.ActDTO;
import com.e3ps.change.activity.service.ActivityHelper;
import com.e3ps.change.cr.column.CrColumn;
import com.e3ps.change.ecn.column.EcnColumn;
import com.e3ps.change.eco.column.EcoColumn;
import com.e3ps.change.eco.dto.EcoDTO;
import com.e3ps.change.ecpr.column.EcprColumn;
import com.e3ps.change.ecrm.column.EcrmColumn;
import com.e3ps.change.util.EChangeUtils;
import com.e3ps.common.code.NumberCode;
import com.e3ps.common.code.dto.NumberCodeDTO;
import com.e3ps.common.code.service.NumberCodeHelper;
import com.e3ps.common.iba.IBAUtils;
import com.e3ps.common.util.AUIGridUtil;
import com.e3ps.common.util.CommonUtil;
import com.e3ps.common.util.PageQueryUtils;
import com.e3ps.common.util.QuerySpecUtils;
import com.e3ps.common.util.StringUtil;
import com.e3ps.doc.DocumentECOLink;
import com.e3ps.doc.column.DocumentColumn;
import com.e3ps.drawing.service.DrawingHelper;
import com.e3ps.org.dto.PeopleDTO;
import com.e3ps.org.service.OrgHelper;
import com.e3ps.part.service.PartHelper;
import com.e3ps.sap.conn.SAPConnection;
import com.e3ps.sap.dto.SAPSendBomDTO;
import com.e3ps.sap.service.SAPHelper;
import com.e3ps.workspace.ApprovalLine;
import com.e3ps.workspace.ApprovalMaster;
import com.e3ps.workspace.service.WorkspaceHelper;
import com.ibm.icu.text.DecimalFormat;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.State;
import wt.org.WTPrincipal;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.queue.ProcessingQueue;
import wt.queue.QueueHelper;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.WTProperties;
import wt.vc.views.View;
import wt.vc.views.ViewHelper;

public class EcoHelper {

	public static final EcoService service = ServiceFactory.getService(EcoService.class);
	public static final EcoHelper manager = new EcoHelper();

	/**
	 * 큐 관련 상수
	 */
	private static final String processQueueName = "SapProcessQueue";
	private static final String className = "com.e3ps.change.util.EChangeUtils";
	private static final String methodName = "afterEcoAction";

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		long start = System.currentTimeMillis() / 1000;
		System.out.println("ECO 쿼리 시작 = " + start);
		Map<String, Object> map = new HashMap<>();
		ArrayList<EcoColumn> list = new ArrayList<>();

		ArrayList<Map<String, String>> rows104 = (ArrayList<Map<String, String>>) params.get("rows104");

		String name = (String) params.get("name");
		String number = (String) params.get("number");
		String eoType = (String) params.get("eoType");
		String createdFrom = (String) params.get("createdFrom");
		String createdTo = (String) params.get("createdTo");
		String creatorOid = (String) params.get("creatorOid");
		String state = (String) params.get("state");
		String approveFrom = (String) params.get("approveFrom");
		String approveTo = (String) params.get("approveTo");
		String model = (String) params.get("modelcode");
		String licensing = (String) params.get("licensing");
		String riskType = (String) params.get("riskType");

		// 정렬
		String sortKey = (String) params.get("sortKey");
		String sortType = (String) params.get("sortType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeOrder.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_NUMBER, number);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.MODEL, model);
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "CHANGE");
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.LICENSING_CHANGE, licensing);
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.RISK_TYPE, riskType);
		QuerySpecUtils.toState(query, idx, EChangeOrder.class, state);
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeOrder.class, creatorOid);
		QuerySpecUtils.toLikeAnd(query, idx, EChangeOrder.class, EChangeOrder.MODEL, model);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, createdFrom,
				createdTo);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE, approveFrom,
				approveTo);

		if (rows104.size() > 0) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			int idx_p = query.appendClassList(EOCompletePartLink.class, false);
			query.appendWhere(
					new SearchCondition(EChangeOrder.class, "thePersistInfo.theObjectIdentifier.id",
							EOCompletePartLink.class, EOCompletePartLink.ROLE_BOBJECT_REF + ".key.id"),
					new int[] { idx, idx_p });
			query.appendAnd();
			query.appendOpenParen();
			for (int i = 0; i < rows104.size(); i++) {
				Map<String, String> row = (Map<String, String>) rows104.get(i);
				if (i != 0) {
					query.appendOr();
				}
				String oid = row.get("part_oid");
				WTPart part = (WTPart) CommonUtil.getObject(oid);
				long ids = part.getMaster().getPersistInfo().getObjectIdentifier().getId();
				query.appendWhere(new SearchCondition(EOCompletePartLink.class,
						EOCompletePartLink.ROLE_AOBJECT_REF + ".key.id", SearchCondition.EQUAL, ids),
						new int[] { idx_p });
			}
			query.appendCloseParen();
		}

		QuerySpecUtils.toOrderBy(query, idx, EChangeOrder.class, EChangeOrder.EO_APPROVE_DATE, true);
		boolean sort = QuerySpecUtils.toSort(sortType);
		QuerySpecUtils.toOrderBy(query, idx, EChangeOrder.class, toSortKey(sortKey), sort);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		int rowNum = (pager.getCpage() - 1) * pager.getPsize() + 1;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			EcoColumn data = new EcoColumn(obj);
			data.setRowNum(rowNum++);
			list.add(data);
		}
		map.put("list", list);
		map.put("topListCount", pager.getTotal());
		map.put("pageSize", pager.getPsize());
		map.put("total", pager.getTotalSize());
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		long end = System.currentTimeMillis() / 1000;
		System.out.println("ECO 쿼리 종료 = " + end + ", 걸린 시간 = " + (end - start));
		return map;
	}

	private String toSortKey(String sortKey) throws Exception {
		if ("number".equals(sortKey)) {
			return EChangeOrder.EO_NUMBER;
		} else if ("name".equals(sortKey)) {
			return EChangeOrder.EO_NAME;
		} else if ("model".equals(sortKey)) {
			return EChangeOrder.MODEL;
		} else if ("licensing_name".equals(sortKey)) {
			return EChangeOrder.LICENSING_CHANGE;
		} else if ("riskType_name".equals(sortKey)) {
			return EChangeOrder.RISK_TYPE;
		} else if ("state".equals(sortKey)) {
			return EChangeOrder.LIFE_CYCLE_STATE;
		} else if ("creator".equals(sortKey)) {
			return EChangeOrder.CREATOR_FULL_NAME;
		} else if ("createdDate".equals(sortKey)) {
			return EChangeOrder.CREATE_TIMESTAMP;
		} else if ("approveDate".equals(sortKey)) {
			return EChangeOrder.EO_APPROVE_DATE;
		}
		return EChangeOrder.CREATE_TIMESTAMP;
	}

	/**
	 * 설변관련 대상들 가져오기
	 */
	public Map<String, Object> dataMap(ArrayList<Map<String, String>> rows500) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		ArrayList<WTPart> clist = new ArrayList<WTPart>(); // 설변 대상 리스트
		ArrayList<WTPart> plist = new ArrayList<WTPart>(); // 완제품 리스트
		ArrayList<String> mlist = new ArrayList<String>(); // 제품 리스트
		String model = "";
		for (Map<String, String> row500 : rows500) {
			String part_oid = row500.get("part_oid");
			WTPart part = (WTPart) CommonUtil.getObject(part_oid);
			clist.add(part);
			plist = PartHelper.manager.collectEndItem(part, plist);
		}

		for (int i = 0; i < plist.size(); i++) {
			WTPart pp = (WTPart) plist.get(i);

			if (isSkip(pp)) {
				// 그냥 여기서 제외 하면 되는거 아닌가?
				plist.remove(i);
				continue;
			}
//			putModel(model, pp, mlist);
		}
		map.put("model", model);
		map.put("plist", plist);
		map.put("clist", clist);
		return map;
	}

	/**
	 * 설변 대상 품목 제외 ECO 일경우 최초버전이면서 작업중인 항목은 제외한다.
	 */
	public boolean isSkip(WTPart pp) throws Exception {
		String version = pp.getVersionIdentifier().getSeries().getValue();
		String state = pp.getLifeCycleState().toString();
		if (version.equals("A") && state.equals("INWORK")) {
			return true;
		}
		return false;
	}

	/**
	 * 모델명 복수개로 인해서 처리 하는 함수
	 */
	public String displayToModel(String model) throws Exception {
		String display = "";
		if (StringUtil.checkString(model)) {
			String[] ss = model.split(",");
			for (int i = 0; i < ss.length; i++) {
				String s = ss[i];
				if (s.length() > 0) {
					if (ss.length - 1 == i) {
						NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
						if (n != null) {
							display += s + " [<font color='red'><b>" + n.getName() + "</b></font>]";
						}
					} else {
						NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
						if (n != null) {
							display += s + " [<font color='red'><b>" + n.getName() + "</b></font>], ";
						}
					}
				}
			}
		}
		return display;
	}

	/**
	 * ECO 관련 객체들
	 */
	public JSONArray reference(String oid, String type) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		if ("part".equalsIgnoreCase(type)) {
			// 설계변경 품목
			return JSONArray.fromObject(referencePart(eco, list));
		} else if ("activity".equalsIgnoreCase(type)) {
			// 설계변경 활동
			return JSONArray.fromObject(referenceActivity(eco, list));
		} else if ("cr".equalsIgnoreCase(type)) {
			// 설계변경 활동
			return JSONArray.fromObject(referenceCr(eco, list));
		} else if ("complete".equals(type)) {
			return JSONArray.fromObject(referenceComplete(eco, list));
		} else if ("ecpr".equals(type)) {
			return JSONArray.fromObject(referenceEcpr(eco, list));
		} else if ("ecrm".equals(type)) {
			return JSONArray.fromObject(referenceEcrm(eco, list));
		} else if ("ecn".equals(type)) {
			return JSONArray.fromObject(referenceEcn(eco, list));
		} else if ("doc".equals(type)) {
			return JSONArray.fromObject(referenceDoc(eco, list));
		} else if ("MODEL".equalsIgnoreCase(type)) {
			// 제품
			return JSONArray.fromObject(referenceCode(eco, list));
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * MODEL
	 */
	private Object referenceCode(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		String[] codes = eco.getModel() != null ? eco.getModel().split(",") : null;

		if (codes != null && codes.length > 0) {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(NumberCode.class, true);
			for (int i = 0; i < codes.length; i++) {
				QuerySpecUtils.toEqualsOr(query, idx, NumberCode.class, NumberCode.CODE, codes[i]);
			}
			QuerySpecUtils.toEqualsAnd(query, idx, NumberCode.class, NumberCode.CODE_TYPE, "MODEL");
			QuerySpecUtils.toBooleanAnd(query, idx, NumberCode.class, NumberCode.DISABLED, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				NumberCode n = (NumberCode) obj[0];
				NumberCodeDTO dto = new NumberCodeDTO(n);
				Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * ECO 관련문서
	 */
	private Object referenceDoc(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "document", DocumentECOLink.class);
		while (result.hasMoreElements()) {
			WTDocument d = (WTDocument) result.nextElement();
			DocumentColumn dto = new DocumentColumn(d);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}
		return list;
	}

	/**
	 * 관련 ECPR
	 */
	private Object referenceEcpr(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecpr", EcoToEcprLink.class);
		while (qr.hasMoreElements()) {
			ECPRRequest ecpr = (ECPRRequest) qr.nextElement();
			EcprColumn data = new EcprColumn(ecpr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ECRM
	 */
	private Object referenceEcrm(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecrm", EcrmToEcoLink.class);
		while (qr.hasMoreElements()) {
			ECRMRequest ecrm = (ECRMRequest) qr.nextElement();
			EcrmColumn data = new EcrmColumn(ecrm);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 관련 ECN
	 */
	private Object referenceEcn(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecn", EcnEco.class);
		while (qr.hasMoreElements()) {
			EChangeNotice ecn = (EChangeNotice) qr.nextElement();
			EcnColumn data = new EcnColumn(ecn);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 완제품 목록
	 */
	private ArrayList<Map<String, Object>> referenceComplete(EChangeOrder eco, ArrayList<Map<String, Object>> list)
			throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class, false);
		while (result.hasMoreElements()) {
			EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
			WTPartMaster master = link.getCompletePart();
			WTPart part = PartHelper.manager.getLatest(master);
			Map<String, Object> map = new HashMap<>();
			map.put("link", link.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("number", part.getNumber());
			map.put("name", part.getName());
			map.put("state", part.getLifeCycleState().getDisplay());
			map.put("version", part.getVersionIdentifier().getSeries().getValue() + "."
					+ part.getIterationIdentifier().getSeries().getValue());
			map.put("creator", part.getCreatorFullName());
			map.put("createdDate_txt", part.getCreateTimestamp().toString().substring(0, 10));
			list.add(map);
		}
		return list;
	}

	/**
	 * ECO 설계변경 품목
	 */
	private ArrayList<Map<String, Object>> referencePart(EChangeOrder eco, ArrayList<Map<String, Object>> list)
			throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			WTPartMaster master = link.getPart();
//			WTPart part = PartHelper.manager.getLatest(master);
			WTPart part = PartHelper.manager.getPart(master.getNumber(), link.getVersion());
			Map<String, Object> map = new HashMap<>();

			boolean isPast = link.getPast();
			boolean preOrder = link.getPreOrder();
			map.put("delivery", link.getDelivery());
			map.put("complete", link.getComplete());
			map.put("inner", link.getInner());
			map.put("order", link.getOrders());
			map.put("part_state_code", link.getPartStateCode());
			map.put("preOrder", preOrder);
			map.put("weight", link.getWeight());
			map.put("link_oid", link.getPersistInfo().getObjectIdentifier().getStringValue());
			// 과거 데이터 처리
			if (!isPast) {

				boolean isRight = link.getRightPart();
				boolean isLeft = link.getLeftPart();
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				boolean isFour = part.getNumber().startsWith("4"); // 4로 시작하는것은 무조건 모두 새품번
				boolean isRevise = link.isRevise();

				if (isLeft) {
					map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("part_number", part.getNumber());
					map.put("part_name", part.getName());
					map.put("part_state", part.getLifeCycleState().getDisplay());
					map.put("part_version", part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue());
					map.put("part_creator", part.getCreatorFullName());

					// 개정된 데이터가 없을 경우
					if (!isRevise && !isFour) {
						map.put("next_oid", "");
						map.put("next_number", "개정 후 데이터가 없습니다.");
						map.put("next_name", "개정 후 데이터가 없습니다.");
						map.put("next_state", "개정 후 데이터가 없습니다.");
						map.put("next_version", "개정 후 데이터가 없습니다.");
						map.put("next_creator", "개정 후 데이터가 없습니다.");
						map.put("afterMerge", true);
					} else {
						WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
						if (next_part != null) {
							// 개정데이터가 있을경우
							map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
							map.put("next_number", next_part.getNumber());
							map.put("next_name", next_part.getName());
							map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
									+ next_part.getIterationIdentifier().getSeries().getValue());
							map.put("next_creator", next_part.getCreatorFullName());
							map.put("next_state", next_part.getLifeCycleState().getDisplay());
							map.put("afterMerge", false);
						} else {
							map.put("next_oid", "");
							map.put("next_number", "개정 후 데이터가 없습니다.");
							map.put("next_name", "개정 후 데이터가 없습니다.");
							map.put("next_state", "개정 후 데이터가 없습니다.");
							map.put("next_version", "개정 후 데이터가 없습니다.");
							map.put("next_creator", "개정 후 데이터가 없습니다.");
							map.put("afterMerge", true);
						}
					}
				} else if (isRight) {
					WTPart pre_part = EChangeUtils.manager.getEcoPrePart(eco, part);
					// 변경후
					map.put("next_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("next_number", part.getNumber());
					map.put("next_name", part.getName());
					map.put("next_state", part.getLifeCycleState().getDisplay());
					map.put("next_version", part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue());
					map.put("next_creator", part.getCreatorFullName());

					if (pre_part == null) {
						map.put("part_oid", "");
						map.put("part_number", "개정 전 데이터가 없습니다.");
						map.put("part_name", "개정 전 데이터가 없습니다.");
						map.put("part_state", "개정 전 데이터가 없습니다.");
						map.put("part_version", "개정 전 데이터가 없습니다.");
						map.put("part_creator", "개정 전 데이터가 없습니다.");
						map.put("preMerge", true);
					} else {
						map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("part_number", pre_part.getNumber());
						map.put("part_name", pre_part.getName());
						map.put("part_state", pre_part.getLifeCycleState().getDisplay());
						map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
								+ pre_part.getIterationIdentifier().getSeries().getValue());
						map.put("part_creator", pre_part.getCreatorFullName());
						map.put("preMerge", false);
					}
				}
				list.add(map);
			} else {
				WTPart pre_part = part;

				if (link.isRevise()) {
					WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
					if (next_part != null) {
						map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("part_number", pre_part.getNumber());
						map.put("part_name", pre_part.getName());
						map.put("part_state", pre_part.getLifeCycleState().getDisplay());
						map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
								+ pre_part.getIterationIdentifier().getSeries().getValue());
						map.put("part_creator", pre_part.getCreatorFullName());
						map.put("preMerge", false);

						map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("next_number", next_part.getNumber());
						map.put("next_name", next_part.getName());
						map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
								+ next_part.getIterationIdentifier().getSeries().getValue());
						map.put("next_creator", next_part.getCreatorFullName());
						map.put("next_state", next_part.getLifeCycleState().getDisplay());
						map.put("afterMerge", false);
//					} else {
//						map.put("next_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
//						map.put("next_number", pre_part.getNumber());
//						map.put("next_name", pre_part.getName());
//						map.put("next_state", pre_part.getLifeCycleState().getDisplay());
//						map.put("next_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
//								+ pre_part.getIterationIdentifier().getSeries().getValue());
//						map.put("next_creator", pre_part.getCreatorFullName());
//						map.put("preMerge", true);
					}
				} else {

					map.put("part_oid", "");
					map.put("part_number", "개정 전 데이터가 없습니다.");
					map.put("part_name", "개정 전 데이터가 없습니다.");
					map.put("part_state", "개정 전 데이터가 없습니다.");
					map.put("part_version", "개정 전 데이터가 없습니다.");
					map.put("part_creator", "개정 전 데이터가 없습니다.");
					map.put("preMerge", true);

					map.put("next_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("next_number", pre_part.getNumber());
					map.put("next_name", pre_part.getName());
					map.put("next_state", pre_part.getLifeCycleState().getDisplay());
					map.put("next_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
							+ pre_part.getIterationIdentifier().getSeries().getValue());
					map.put("next_creator", pre_part.getCreatorFullName());
					map.put("afterMerge", false);
				}
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * ECO 관련 설계변경 활동
	 */
	private Object referenceActivity(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		JSONArray j = new JSONArray();
		ArrayList<EChangeActivity> colletActivityList = ActivityHelper.manager.colletActivity(eco);
		for (EChangeActivity item : colletActivityList) {
			ActDTO dto = new ActDTO(item);
			Map<String, Object> map = AUIGridUtil.dtoToMap(dto);
			list.add(map);
		}

		return list;
	}

	/**
	 * ECO CR 목록
	 */
	private Object referenceCr(EChangeOrder eco, ArrayList<Map<String, Object>> list) throws Exception {
		QueryResult result = PersistenceHelper.manager.navigate(eco, "ecr", RequestOrderLink.class);
		while (result.hasMoreElements()) {
			EChangeRequest ecr = (EChangeRequest) result.nextElement();
			CrColumn data = new CrColumn(ecr);
			Map<String, Object> map = AUIGridUtil.dtoToMap(data);
			list.add(map);
		}
		return list;
	}

	/**
	 * ECO 결재 완료후 호출 되는 함수 큐 이용
	 */
	public void postAfterAction(EChangeOrder e) throws Exception {
		WTPrincipal principal = SessionHelper.manager.setAdministrator();
		ProcessingQueue queue = (ProcessingQueue) QueueHelper.manager.getQueue(processQueueName, ProcessingQueue.class);

		Hashtable<String, String> hash = new Hashtable<>();
		hash.put("oid", e.getPersistInfo().getObjectIdentifier().getStringValue());

		Class[] argClasses = { Hashtable.class };
		Object[] argObjects = { hash };

		queue.addEntry(principal, methodName, className, argClasses, argObjects);
	}

	/**
	 * ECO와 연결된 완제품 리스트
	 */
	public ArrayList<EOCompletePartLink> completeParts(Object obj) throws Exception {
		ArrayList<EOCompletePartLink> list = new ArrayList<EOCompletePartLink>();
		QueryResult result = null;
		if (obj instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) obj;
			result = PersistenceHelper.manager.navigate(eco, "completePart", EOCompletePartLink.class, false);
		} else if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EOCompletePartLink.class, false);
		}

		while (result.hasMoreElements()) {
			EOCompletePartLink link = (EOCompletePartLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	/**
	 * ECO 관련 품목 수집
	 */
	public ArrayList<EcoPartLink> ecoParts(Object obj) throws Exception {
		ArrayList<EcoPartLink> list = new ArrayList<EcoPartLink>();
		QueryResult result = null;
		if (obj instanceof EChangeOrder) {
			EChangeOrder eco = (EChangeOrder) obj;
			result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		} else if (obj instanceof WTPart) {
			WTPart part = (WTPart) obj;
			result = PersistenceHelper.manager.navigate(part.getMaster(), "eco", EcoPartLink.class, false);
		}

		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	/**
	 * IBA 값 세팅 및 상태 변경
	 */
	public void setIBAAndState(ArrayList<EcoPartLink> ecoParts, ArrayList<EOCompletePartLink> completeParts)
			throws Exception {
		// 설변대상 품목
		for (EcoPartLink link : ecoParts) {
			EChangeOrder eco = link.getEco();
			WTPartMaster m = link.getPart();
			String v = link.getVersion();
			WTPart part = PartHelper.manager.getPart(m.getNumber(), v);

			if (part != null) {
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				if (!isApproved) {
					LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
					part = (WTPart) PersistenceHelper.manager.refresh(part);
				}
				String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
				IBAUtils.appendIBA(part, "CHANGENO", eco.getEoNumber(), "s");
				IBAUtils.appendIBA(part, "CHANGEDATE", today, "s");

				EPMDocument epm = PartHelper.manager.getEPMDocument(part);
				if (epm != null) {
					epm = DrawingHelper.manager.latest((EPMDocumentMaster) epm.getMaster());
					isApproved = epm.getLifeCycleState().toString().equals("APPROVED");
					if (!isApproved) {
						LifeCycleHelper.service.setLifeCycleState(epm, State.toState("APPROVED"));
					}

					IBAUtils.appendIBA(epm, "CHANGENO", eco.getEoNumber(), "s");
					IBAUtils.appendIBA(epm, "CHANGEDATE", today, "s");

					EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);
					if (epm2d != null) {
						epm2d = DrawingHelper.manager.latest((EPMDocumentMaster) epm2d.getMaster());
						isApproved = epm2d.getLifeCycleState().toString().equals("APPROVED");
						if (!isApproved) {
							LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState("APPROVED"));
						}

						IBAUtils.appendIBA(epm2d, "CHANGENO", eco.getEoNumber(), "s");
						IBAUtils.appendIBA(epm2d, "CHANGEDATE", today, "s");
					}
				}
			}

			// 메카가 아닐경우에만 멀 하는데..???
//			if(!ChangeUtil.isMeca(location)){
//				IBAUtil.changeIBAValue((IBAHolder)rc, AttributeKey.IBAKey.IBA_APR, approveName , "string");
//				IBAUtil.changeIBAValue((IBAHolder)rc,  AttributeKey.IBAKey.IBA_CHK, checkerName , "string");
//			}

			// EO,ECO시 누적으로 등록
//			String changeNo = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGENO));
//			String changeDate = StringUtil.checkNull(IBAUtil.getAttrValue((IBAHolder) rc, IBAKey.IBA_CHANGEDATE));
		}

		// 완제품
		for (EOCompletePartLink link : completeParts) {
			EChangeOrder eco = link.getEco();
			WTPartMaster m = link.getCompletePart();
			String v = link.getVersion();
			WTPart part = PartHelper.manager.getPart(m.getNumber(), v);
			if (part != null) {
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				if (!isApproved) {
					LifeCycleHelper.service.setLifeCycleState(part, State.toState("APPROVED"));
					part = (WTPart) PersistenceHelper.manager.refresh(part);
				}

				// 최종승인일..
				String today = new Timestamp(new Date().getTime()).toString().substring(0, 10);
				IBAUtils.appendIBA(part, "CHANGENO", eco.getEoNumber(), "s");
				IBAUtils.appendIBA(part, "CHANGEDATE", today, "s");

				EPMDocument epm = PartHelper.manager.getEPMDocument(part);
				if (epm != null) {
					epm = DrawingHelper.manager.latest((EPMDocumentMaster) epm.getMaster());
					IBAUtils.appendIBA(epm, "CHANGENO", eco.getEoNumber(), "s");
					IBAUtils.appendIBA(epm, "CHANGEDATE", today, "s");

					EPMDocument epm2d = PartHelper.manager.getEPMDocument2D(epm);
					if (epm2d != null) {
						epm2d = DrawingHelper.manager.latest((EPMDocumentMaster) epm2d.getMaster());
						isApproved = epm2d.getLifeCycleState().toString().equals("APPROVED");
						if (!isApproved) {
							LifeCycleHelper.service.setLifeCycleState(epm2d, State.toState("APPROVED"));
						}

						IBAUtils.appendIBA(epm2d, "CHANGENO", eco.getEoNumber(), "s");
						IBAUtils.appendIBA(epm2d, "CHANGEDATE", today, "s");
					}

				}
			}
		}
	}

	/**
	 * ECO 다음번호
	 */
	public String getNextNumber(String number) throws Exception {
		DecimalFormat df = new DecimalFormat("00");
		String rtn = null;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeOrder.class, true);
		SearchCondition sc = new SearchCondition(EChangeOrder.class, EChangeOrder.EO_NUMBER, "LIKE", number + "%");
		query.appendWhere(sc, new int[] { idx });
		QuerySpecUtils.toOrderBy(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, true);
		QueryResult qr = PersistenceHelper.manager.find(query);
		// E2312N45
		if (qr.hasMoreElements()) {
			Object[] obj = (Object[]) qr.nextElement();
			EChangeOrder eco = (EChangeOrder) obj[0];
			String ecoNumber = eco.getEoNumber();
			String next = ecoNumber.substring(6); // 00
			int n = Integer.parseInt(next) + 1;
			rtn = number + df.format(n);
		} else {
			rtn = number + "01";
		}
		return rtn;
	}

	/**
	 * ECO 관련 도면 다운로드
	 */
	public void summaryData(Map<String, Object> params) throws Exception {
		String type = (String) params.get("type");

	}

	/*
	 * 설계변경 통보서
	 */
	public Map<String, Object> excel(String oid) throws Exception {
		Map<String, Object> result = new HashMap<>();
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		EcoDTO dto = new EcoDTO(eco);
//		Map<String, String> map = ChangeUtil.getApproveInfo(eco);
		try {
			Map<String, String> map = new HashMap<>();

			String wtHome = WTProperties.getServerProperties().getProperty("wt.home");
			String path = WTProperties.getServerProperties().getProperty("wt.temp");

			File orgFile = new File(wtHome + "/codebase/com/e3ps/change/eco/dto/eco.xlsx");

			File newFile = CommonUtil.copyFile(orgFile, new File(path + "/" + dto.getNumber() + ".xlsx"));

			Workbook workbook = new Workbook(newFile.getPath());
			Worksheet worksheet = workbook.getWorksheets().get(0);
			worksheet.setName(dto.getNumber()); // 시트 이름

			Cell numberCell = worksheet.getCells().get(8, 5);
			numberCell.putValue(dto.getNumber());

			// 작성 싸인 (16-I, 15-8)
			String signPath = OrgHelper.manager.getSignPath(eco.getCreatorName());
			if (signPath != null) {
				int picIndex = worksheet.getPictures().add(15, 8, signPath);
				Picture picture = worksheet.getPictures().get(picIndex);
				picture.setHeightCM(2.5);
				picture.setWidthCM(2.5);
				picture.setAutoSize(true);

				int cellRowIndex = 15; // Specify the row index of the cell
				int cellColumnIndex = 8; // Specify the column index of the cell
				int cellWidth = worksheet.getCells().getColumnWidthPixel(cellColumnIndex);
				int cellHeight = worksheet.getCells().getRowHeightPixel(cellRowIndex);
				int imageWidth = (int) picture.getWidthInch() * 96; // Convert width from cm to pixels
				int imageHeight = (int) picture.getHeightInch() * 96; // Convert height from cm to pixels

				int deltaX = (cellWidth - imageWidth) / 2;
//				int deltaY = (cellHeight - imageHeight) / 2;

				picture.setPlacement(PlacementType.FREE_FLOATING);
				picture.setUpperDeltaX(deltaX);
//				picture.setUpperDeltaY(deltaY);
			}

			// 작성자 (17-I, 16-8)
			Cell creatorCell = worksheet.getCells().get(16, 8);
			creatorCell.putValue(dto.getCreator());

			// 작성일 (18-I, 17-8)
			Cell createDateCell = worksheet.getCells().get(17, 8);
			createDateCell.putValue(dto.getCreatedDate_txt());

			ApprovalMaster master = WorkspaceHelper.manager.getMaster(eco);
			ApprovalLine preLine = WorkspaceHelper.manager.getLastPreApprovalLine(master);
			if (preLine != null) {
				// 승인 싸인 (16-I, 15-8)
				String agreeSignPath = OrgHelper.manager.getSignPath(preLine.getOwnership().getOwner().getName());
				if (agreeSignPath != null) {
					int picIndex = worksheet.getPictures().add(15, 10, agreeSignPath);
					Picture picture = worksheet.getPictures().get(picIndex);
					picture.setHeightCM(2.5);
					picture.setWidthCM(2.5);
					picture.setAutoSize(true);

					int cellRowIndex = 15; // Specify the row index of the cell
					int cellColumnIndex = 10; // Specify the column index of the cell
					int cellWidth = worksheet.getCells().getColumnWidthPixel(cellColumnIndex);
					int cellHeight = worksheet.getCells().getRowHeightPixel(cellRowIndex);
					int imageWidth = (int) picture.getWidthInch() * 96; // Convert width from cm to pixels
					int imageHeight = (int) picture.getHeightInch() * 96; // Convert height from cm to pixels

					int deltaX = (cellWidth - imageWidth) / 2;
//				int deltaY = (cellHeight - imageHeight) / 2;
					picture.setPlacement(PlacementType.FREE_FLOATING);
					picture.setUpperDeltaX(deltaX);
//				picture.setUpperDeltaY(deltaY);
				}

				// 검토자 (17-K, 16-10)
				Cell checkerCell = worksheet.getCells().get(16, 10);
				checkerCell.putValue(preLine.getOwnership().getOwner().getFullName());

				// 검토일 (18-K, 17-10)
				Cell checkerDateCell = worksheet.getCells().get(17, 10);
				checkerDateCell.putValue(
						preLine.getCompleteTime() != null ? preLine.getCompleteTime().toString().substring(0, 10) : "");
			}

			// 승인자 (17-M 16-12)
			ApprovalLine last = WorkspaceHelper.manager.getLastApprovalLine(master);

			// 승인 싸인 (16-I, 15-8)
			String approvalSignPath = OrgHelper.manager.getSignPath(last.getOwnership().getOwner().getName());
			if (approvalSignPath != null) {
				int picIndex = worksheet.getPictures().add(15, 12, approvalSignPath);
				Picture picture = worksheet.getPictures().get(picIndex);
				picture.setHeightCM(2.5);
				picture.setWidthCM(2.5);
				picture.setAutoSize(true);

				int cellRowIndex = 15; // Specify the row index of the cell
				int cellColumnIndex = 12; // Specify the column index of the cell
				int cellWidth = worksheet.getCells().getColumnWidthPixel(cellColumnIndex);
				int cellHeight = worksheet.getCells().getRowHeightPixel(cellRowIndex);
				int imageWidth = (int) picture.getWidthInch() * 96; // Convert width from cm to pixels
				int imageHeight = (int) picture.getHeightInch() * 96; // Convert height from cm to pixels

				int deltaX = (cellWidth - imageWidth) / 2;
//				int deltaY = (cellHeight - imageHeight) / 2;
				picture.setPlacement(PlacementType.FREE_FLOATING);
				picture.setUpperDeltaX(deltaX);
//				picture.setUpperDeltaY(deltaY);
			}

			// 승인자
			Cell approverCell = worksheet.getCells().get(16, 12);
			approverCell.putValue(last.getOwnership().getOwner().getFullName());

			// 승인일 (18-M, 17-12)
			Cell approverDateCell = worksheet.getCells().get(17, 12);
			approverDateCell
					.putValue(last.getCompleteTime() != null ? last.getCompleteTime().toString().substring(0, 10) : "");

			// 문서 번호 (29-D, 28-3)
			Cell _numberCell = worksheet.getCells().get(28, 3);
			_numberCell.putValue(dto.getNumber());

			// 제목 (30-D, 29-3)
			Cell nameCell = worksheet.getCells().get(29, 3);
			nameCell.putValue(dto.getName());

			// 작성일 (32-D, 31-3)
			Cell _createDateCell = worksheet.getCells().get(31, 3);
			_createDateCell.putValue(dto.getCreatedDate_txt());

			WTUser user = (WTUser) eco.getCreator().getPrincipal();

			PeopleDTO pdto = new PeopleDTO(user);

			// 작성부서 (32-I, 31-8)
			Cell deptCell = worksheet.getCells().get(31, 8);
			deptCell.putValue(pdto.getDepartment_name());

			// 승인일 (33-D, 32-3)
			Cell _approverDateCell = worksheet.getCells().get(32, 3);
			_approverDateCell.putValue(eco.getEoApproveDate());

			// 작성자 (33-I, 32-8)
			Cell _creatorCell = worksheet.getCells().get(32, 8);
			_creatorCell.putValue(dto.getCreator());

			// 제품명 (35-D, 34-3)

			String display = "";
			String model = eco.getModel();
			if (StringUtil.checkString(model)) {
				String[] ss = model.split(",");
				for (int i = 0; i < ss.length; i++) {
					String s = ss[i];
					if (s.length() > 0) {
						if (ss.length - 1 == i) {
							NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
							if (n != null) {
								display += s + " [" + n.getName() + "]";
							}
						} else {
							NumberCode n = NumberCodeHelper.manager.getNumberCode(s, "MODEL");
							if (n != null) {
								display += s + " [" + n.getName() + "], ";
							}
						}
					}
				}
			}

			Cell modelCell = worksheet.getCells().get(34, 3);
			modelCell.putValue(display);

			ArrayList<EOCompletePartLink> completeParts = completeParts(eco);

			// 완제품 품번 (36-D, 35-3)
			String completePart = "";
			for (int i = 0; i < completeParts.size(); i++) {
				EOCompletePartLink link = (EOCompletePartLink) completeParts.get(i);
				String n = link.getCompletePart().getNumber();
				completePart = completePart + n;
				if (i != (completeParts.size() - 1)) {
					completePart += ",";
				}
			}

			Cell completePartCell = worksheet.getCells().get(35, 3);
			completePartCell.putValue(completePart);

			// 변경사유(37-D, 36-3)
			Cell commentACell = worksheet.getCells().get(36, 3);

			Style style_UP = workbook.createStyle();
			style_UP.setHorizontalAlignment(TextAlignmentType.LEFT);
			style_UP.setShrinkToFit(true);

			Font font = style_UP.getFont();
			font.setSize(10);

			style_UP.setVerticalAlignment(TextAlignmentType.TOP);
			style_UP.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.THIN, Color.getBlack());
			style_UP.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());

			style_UP.setTextWrapped(true);
			commentACell.setStyle(style_UP);
			commentACell.putValue(dto.getEoCommentA());
			worksheet.getCells().setRowHeight(36, 350);
			Row comARow = worksheet.getCells().getRows().get(36);
			int height = (int) comARow.getHeight();
			String com = dto.getEoCommentA();
			if (StringUtil.checkString(com)) {
				for (int i = 0; i < com.length(); i++) {
					char ca = com.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						height += 150;
					}
				}
			}
			comARow.setHeight((short) height / 20);

			// 변경근거(38-D, 37-3)
			Row ecrNo = worksheet.getCells().getRows().get(37);
			Style cellStyleEcrNo_UP = workbook.createStyle();
			style_UP.setHorizontalAlignment(TextAlignmentType.LEFT);
			cellStyleEcrNo_UP.setShrinkToFit(true);

			Font sfont = cellStyleEcrNo_UP.getFont();
			sfont.setSize(10);

			cellStyleEcrNo_UP.setVerticalAlignment(TextAlignmentType.TOP);
			cellStyleEcrNo_UP.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			cellStyleEcrNo_UP.setBorder(BorderType.TOP_BORDER, CellBorderType.THIN, Color.getBlack());

			cellStyleEcrNo_UP.setTextWrapped(true);
			ecrNo.applyStyle(cellStyleEcrNo_UP, new StyleFlag());

			QueryResult qr = PersistenceHelper.manager.navigate(eco, "ecr", RequestOrderLink.class);
			String ecrNumbersNames = "";
			while (qr.hasMoreElements()) {
				EChangeRequest ecr = (EChangeRequest) qr.nextElement();
				String nn = ecr.getEoNumber() + " [" + ecr.getEoName() + "]";
				ecrNumbersNames += (nn + "\r\n");
			}

			// ecpr로 변경
			qr.reset();
			qr = PersistenceHelper.manager.navigate(eco, "ecpr", EcoToEcprLink.class);
			while (qr.hasMoreElements()) {
				ECPRRequest ecpr = (ECPRRequest) qr.nextElement();
				String nn = ecpr.getEoNumber() + " [" + ecpr.getEoName() + "]";
				ecrNumbersNames += (nn + "\r\n");
			}

			Row ecrNumbersNamesRow = worksheet.getCells().getRows().get(37);
			if (null != ecrNumbersNames) {
				for (int i = 0; i < ecrNumbersNames.length(); i++) {
					char ca = ecrNumbersNames.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						height += 150;
					}
				}
			}
			ecrNumbersNamesRow.setHeight((short) height / 20);
			Cell ecrNoCell = ecrNo.get(3); // 첫 번째 셀을 가리킵니다.
			ecrNoCell.putValue(ecrNumbersNames);

			/**
			 * 설계변경 부품 내역 46 Line(45 Index) 부터 ~
			 */

			Style textCenterStyle = workbook.createStyle();
			textCenterStyle.setHorizontalAlignment(TextAlignmentType.CENTER);
			textCenterStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textCenterStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textCenterStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textCenterStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textCenterStyle.getFont().setSize(10);

			Style textLeftStyle = workbook.createStyle();
			textLeftStyle.setHorizontalAlignment(TextAlignmentType.LEFT);
			textLeftStyle.setBorder(BorderType.BOTTOM_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textLeftStyle.setBorder(BorderType.TOP_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textLeftStyle.setBorder(BorderType.LEFT_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textLeftStyle.setBorder(BorderType.RIGHT_BORDER, CellBorderType.MEDIUM, Color.getBlack());
			textLeftStyle.getFont().setSize(10);

			int row = 45;
			int rowNum = 1;
			QueryResult rs = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
			while (rs.hasMoreElements()) {
				EcoPartLink eLink = (EcoPartLink) rs.nextElement();
				WTPartMaster mm = eLink.getPart();
				WTPart pp = PartHelper.manager.getPart(mm.getNumber(), eLink.getVersion());

				if (row > 45) {
//					POIUtil.copyRow(workbook, sheet, (row - 1), 1);
					worksheet.getCells().insertRows(row, 1, true);
					Row copyRow = worksheet.getCells().getRows().get(row);

					for (int i = 0; i < copyRow.getLastCell().getColumn() - 1; i++) {
						Cell copiedCell = copyRow.get(i);
						if (i == 0) {
							continue;
						}
						worksheet.getCells().merge((row), 2, 1, 3);
						worksheet.getCells().merge((row), 6, 1, 2);
						worksheet.getCells().merge((row), 13, 1, 2);

						copiedCell.setStyle(textCenterStyle);
					}
				}

				boolean isPast = eLink.getPast();

				String preNumber = "";
				String preName = "";
				String nextNumber = "";
				String nextName = "";

				if (!isPast) {
					boolean isLeft = eLink.getLeftPart();
					boolean isRight = eLink.getRightPart();
					// 신규 ECO
					// 신규 작성 데이터는 무조건 히스토리가 이제는 존재
					// 왼쪽으로 들어가는건 다음거를 구한다
					if (isLeft) {
						preNumber = pp.getNumber();
						preName = pp.getName();
						WTPart nextPart = EChangeUtils.manager.getEcoNextPart(eco, pp);
						if (nextPart != null) {
							nextNumber = nextPart.getNumber();
							nextName = nextPart.getName();
						}

						// 오른쪽으로 들어가는거는 이전을 구한다
					} else if (isRight) {
						WTPart prePart = EChangeUtils.manager.getEcoPrePart(eco, pp);
						if (prePart != null) {
							preNumber = prePart.getNumber();
							preName = prePart.getName();
						}
						nextNumber = pp.getNumber();
						nextName = pp.getName();
					}
				} else {
					// 과거 ECO
					preNumber = pp.getNumber();
					preName = pp.getName();
					if (eLink.isRevise()) {
						WTPart nextPart = (WTPart) EChangeUtils.manager.getNext(pp);
						if (nextPart != null) {
							nextNumber = nextPart.getNumber();
							nextName = nextPart.getName();
						}
					}
				}

				// 과거데이터

				// NO (B, 1)
				Cell excelNoCell = worksheet.getCells().getRows().get(row).get(1);
				excelNoCell.putValue(rowNum);

				// 변경 전 품번 (C, 2)
				Cell oldPartNumberCell = worksheet.getCells().getRows().get(row).get(2);
				oldPartNumberCell.putValue(preNumber);

				// 변경 후 품번 (F, 5)
				Cell newPartNumberCell = worksheet.getCells().getRows().get(row).get(5);
				newPartNumberCell.putValue(nextNumber);

				// 품명 (G, 6)
				Cell partNameCell = worksheet.getCells().getRows().get(row).get(6);
				partNameCell.putValue(!preName.equals("") ? preName : nextName); // 변경전 없나???)

				// 부품 상태 코드 (I, 8)
				Cell stateCodeCell = worksheet.getCells().getRows().get(row).get(8);
				stateCodeCell.putValue(eLink.getPartStateCode());

				Cell deliveryCell = worksheet.getCells().getRows().get(row).get(9);
				deliveryCell.putValue(eLink.getDelivery());

				// 완성 장비 (K, 10)
				Cell completeCell = worksheet.getCells().getRows().get(row).get(10);
				completeCell.putValue(eLink.getComplete());

				// 사내 재고 (L, 11)
				Cell stockCell = worksheet.getCells().getRows().get(row).get(11);
				stockCell.putValue(eLink.getInner());

				// 발주 부품 (M, 12)
				Cell orderCell = worksheet.getCells().getRows().get(row).get(12);
				orderCell.putValue(eLink.getOrders());

				// 중량 (N, 13)
				Cell weightCell = worksheet.getCells().getRows().get(row).get(13);
				weightCell.putValue(eLink.getWeight());

				rowNum++;
				row++;
			}

			/**
			 * 설계변경 부품 내역 끝
			 */

			if (row == 45)
				row++;

			row++;
			row++;
			row++;

			System.out.println("빈공간?=" + row);

			// 설계변경 세부내용 (blank)
			worksheet.getCells().setRowHeight(row, 90 / 20);
			row++;

			// 설계변경 세부내용 (B, 1)
//			worksheet.getCells().setRowHeight(row, 1050 / 20);
			worksheet.getCells().setRowHeight(row, 300);

			System.out.println("row1111=" + row);
			Cell commentBCell = worksheet.getCells().getRows().get(row).get(1);
			commentBCell.putValue(dto.getEoCommentB());

			Row comBRow = worksheet.getCells().getRows().get(row);
			int bheight = (int) comBRow.getHeight();
			String comB = dto.getEoCommentB();
			if (StringUtil.checkString(comB)) {
				for (int i = 0; i < comB.length(); i++) {
					char ca = comB.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						bheight += 300;
					}
				}
			}
			comBRow.setHeight((short) bheight / 20);
			row++;

			row++;
			row++;
			row++;

			/**
			 * 변경 문건 시작
			 */
			int startRow = row;
			int documentIndex = 1;

			ArrayList<Map<String, Object>> docList = ActivityHelper.manager.getDocFromActivity(eco);
			for (Map<String, Object> dmap : docList) {
				JSONArray arr = (JSONArray) dmap.get("data");
				for (int k = 0; k < arr.size(); k++) {
					JSONObject node = (JSONObject) arr.get(k);
					if (row > startRow) {

						worksheet.getCells().insertRows(row, 1, true);
						Row copyRow = worksheet.getCells().getRows().get(row);
						for (int i = 0; i < copyRow.getLastCell().getColumn() - 1; i++) {
							Cell copiedCell = copyRow.get(i);
							if (i == 0) {
								continue;
							}
							worksheet.getCells().merge((row), 1, 1, 3);
							worksheet.getCells().merge((row), 4, 1, 4);
							worksheet.getCells().merge((row), 8, 1, 4);
							worksheet.getCells().merge((row), 12, 1, 3);

							if (i == 4) {
								textLeftStyle.setIndentLevel(1);
								copiedCell.setStyle(textLeftStyle);
							} else {
								copiedCell.setStyle(textCenterStyle);
							}
						}
					}

					// NO (B, 1)
					Cell docNoCell = worksheet.getCells().getRows().get(row).get(1);
					docNoCell.putValue(documentIndex);

					// 문서명 (E, 4)
					Cell docNameCell = worksheet.getCells().getRows().get(row).get(4);
					docNameCell.putValue((String) node.get("name"));

					// 문서번호 (I, 8)
					Cell docNumberCell = worksheet.getCells().getRows().get(row).get(8);
					docNumberCell.putValue((String) node.get("number"));

					// 버전 (M, 12)
					Cell docVersionCell = worksheet.getCells().getRows().get(row).get(12);
					docVersionCell.putValue((String) node.get("version"));

					documentIndex++;

					row++;
				}
			}

			if (startRow == row)
				row++;
			row++;
			// 위험관리
			Cell licensingCell = worksheet.getCells().getRows().get(row).get(4);
			licensingCell.putValue(dto.getLicensing_name());
			// 위험 통제
			Cell riskTypeCell = worksheet.getCells().getRows().get(row).get(9);
			riskTypeCell.putValue(dto.getRiskType_name());
			row++;
			row++;

			// 특기사항 (blank)
			worksheet.getCells().setRowHeight(row, 90 / 20);
			row++;

			// 특기사항 (E, 4)
//			worksheet.getCells().setRowHeight(row, 1050 / 20);
			worksheet.getCells().setRowHeight(row, 350);

			Cell commentCCell = worksheet.getCells().getRows().get(row).get(4);
			commentCCell.putValue(dto.getEoCommentC());
			Row comCRow = worksheet.getCells().getRows().get(row);
			int cheight = (int) comCRow.getHeight();
			String comC = dto.getEoCommentC();
			if (null != comC) {
				for (int i = 0; i < comC.length(); i++) {
					char ca = comC.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						cheight += 350;
					}
				}
			}
			comCRow.setHeight((short) cheight / 20);
			row++;

			// 기타사항 Blank
			worksheet.getCells().setRowHeight(row, 90 / 20);
			row++;

			// 기타사항 (E, 4)
//			worksheet.getCells().setRowHeight(row, 1050 / 20);
			worksheet.getCells().setRowHeight(row, 350);

			Cell commentDCell = worksheet.getCells().getRows().get(row).get(4);
			commentDCell.putValue(dto.getEoCommentD());
			Row comDRow = worksheet.getCells().getRows().get(row);
			int dheight = (int) comDRow.getHeight();
			String comd = dto.getEoCommentD();
			if (null != comd) {
				for (int i = 0; i < comd.length(); i++) {
					char ca = comd.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						dheight += 350;
					}
				}
			}
			comDRow.setHeight((short) dheight / 20);
			row++;

			row++;
			row++;
			row++;

			/**
			 * 첨부파일 시작 (기타사항 +4)
			 */

			startRow = row;
			int attachCount = 1;

			QueryResult attachQr = ContentHelper.service.getContentsByRole(eco, ContentRoleType.SECONDARY);

			while (attachQr.hasMoreElements()) {

				ContentItem item = (ContentItem) attachQr.nextElement();

				if (item != null) {

					if (row > startRow) {

						worksheet.getCells().insertRows(row, 1, true);
						Row copyRow = worksheet.getCells().getRows().get(row);

						for (int i = 0; i < copyRow.getLastCell().getColumn() - 1; i++) {
							Cell copiedCell = copyRow.get(i);
							if (i == 0) {
								continue;
							}
							worksheet.getCells().merge((row), 2, 1, 13);

							if (i == 2) {
								textLeftStyle.setIndentLevel(1);
								copiedCell.setStyle(textLeftStyle);
							} else {
								copiedCell.setStyle(textCenterStyle);
							}
						}
					}

					ApplicationData appData = (ApplicationData) item;

					// NO (B, 1)
					Cell attchNoCell = worksheet.getCells().getRows().get(row).get(1);
					attchNoCell.putValue(attachCount);

					// 파일명 (C, 2)
					Cell attchNameCell = worksheet.getCells().getRows().get(row).get(2);
					attchNameCell.putValue(appData.getFileName());

					attachCount++;
					row++;
				}
			}

			/**
			 * 첨부파일 끝
			 */

			if (startRow == row)
				row++;
			row++;
			row++;
			row++;

			/**
			 * 결재자 의견 시작(첨부파일 +2)
			 */

			startRow = row;

			ApprovalLine submitLine = WorkspaceHelper.manager.getSubmitLine(master);
			if (row > startRow) {
				worksheet.getCells().insertRows(row, 1, true);
				Row copyRow = worksheet.getCells().getRows().get(row);
				worksheet.autoFitRow(row);
				for (int i = 0; i < copyRow.getLastCell().getColumn() - 1; i++) {
					Cell copiedCell = copyRow.get(i);
					if (i == 0) {
						continue;
					}

					worksheet.getCells().merge((row), 1, 1, 2);
					worksheet.getCells().merge((row), 3, 1, 2);
					worksheet.getCells().merge((row), 5, 1, 10);

					if (i == 5) {
						textLeftStyle.setIndentLevel(1);
						copiedCell.setStyle(textLeftStyle);
					} else {
						copiedCell.setStyle(textCenterStyle);
					}
				}
			}

			// 이름 (B, 1)
			Cell submitNameCell = worksheet.getCells().getRows().get(row).get(1);
			submitNameCell.putValue(submitLine.getOwnership().getOwner().getFullName());

			// 날짜 (D, 3)
			String sprocessDate = "";
			if (submitLine.getCompleteTime() != null) {
				sprocessDate = submitLine.getCompleteTime().toString().substring(0, 10);
			}

			Cell submitDateCell = worksheet.getCells().getRows().get(row).get(3);
			submitDateCell.putValue(sprocessDate);

			// 내용 (F, 5)
			Cell submitDescCell = worksheet.getCells().getRows().get(row).get(5);
			submitDescCell.putValue(submitLine.getDescription() != null ? submitLine.getDescription() : "");

			Row scomDescRow = worksheet.getCells().getRows().get(row);
			int sdescheight = (int) scomDescRow.getHeight();
			String scomdescd = submitLine.getDescription() != null ? submitLine.getDescription() : "";
			if (null != scomdescd) {
				for (int i = 0; i < scomdescd.length(); i++) {
					char ca = scomdescd.charAt(i);
					Character careCa = Character.valueOf('\n');
					if (ca == careCa) {
						sdescheight += 350;
					}
				}
			}
//			comDescRow.setHeight((short) descheight / 20);
			row++;

			// 합의 라인
			ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLine(master);
			for (ApprovalLine agreeLine : agreeLines) {
				if (row > startRow) {
					worksheet.getCells().insertRows(row, 1, true);
					Row copyRow = worksheet.getCells().getRows().get(row);
					worksheet.autoFitRow(row);
					for (int i = 0; i < copyRow.getLastCell().getColumn() - 1; i++) {
						Cell copiedCell = copyRow.get(i);
						if (i == 0) {
							continue;
						}

						worksheet.getCells().merge((row), 1, 1, 2);
						worksheet.getCells().merge((row), 3, 1, 2);
						worksheet.getCells().merge((row), 5, 1, 10);

						if (i == 5) {
							textLeftStyle.setIndentLevel(1);
							copiedCell.setStyle(textLeftStyle);
						} else {
							copiedCell.setStyle(textCenterStyle);
						}
					}
				}

				// 이름 (B, 1)
				Cell agreeNameCell = worksheet.getCells().getRows().get(row).get(1);
				agreeNameCell.putValue(agreeLine.getOwnership().getOwner().getFullName());

				// 날짜 (D, 3)
				String processDate = "";
				if (agreeLine.getCompleteTime() != null) {
					processDate = agreeLine.getCompleteTime().toString().substring(0, 10);
				}

				Cell agreeDateCell = worksheet.getCells().getRows().get(row).get(3);
				agreeDateCell.putValue(processDate);

				// 내용 (F, 5)
				Cell agreeDescCell = worksheet.getCells().getRows().get(row).get(5);
				agreeDescCell.putValue(agreeLine.getDescription() != null ? agreeLine.getDescription() : "");

				Row comDescRow = worksheet.getCells().getRows().get(row);
				int descheight = (int) comDescRow.getHeight();
				String comdescd = agreeLine.getDescription() != null ? agreeLine.getDescription() : "";
				if (null != comdescd) {
					for (int i = 0; i < comdescd.length(); i++) {
						char ca = comdescd.charAt(i);
						Character careCa = Character.valueOf('\n');
						if (ca == careCa) {
							descheight += 350;
						}
					}
				}
//				comDescRow.setHeight((short) descheight / 20);
				row++;
			}

			// 결재라인
			ArrayList<ApprovalLine> approvalLines = WorkspaceHelper.manager.getApprovalLines(master);
			for (ApprovalLine appovalLine : approvalLines) {

				if (row > startRow) {
					worksheet.getCells().insertRows(row, 1, true);
					Row copyRow = worksheet.getCells().getRows().get(row);
					worksheet.autoFitRow(row);
					for (int i = 0; i < copyRow.getLastCell().getColumn() - 1; i++) {
						Cell copiedCell = copyRow.get(i);
						if (i == 0) {
							continue;
						}

						worksheet.getCells().merge((row), 1, 1, 2);
						worksheet.getCells().merge((row), 3, 1, 2);
						worksheet.getCells().merge((row), 5, 1, 10);

						if (i == 5) {
							textLeftStyle.setIndentLevel(1);
							copiedCell.setStyle(textLeftStyle);
						} else {
							copiedCell.setStyle(textCenterStyle);
						}
					}
				}

				// 이름 (B, 1)
				Cell approvalNameCell = worksheet.getCells().getRows().get(row).get(1);
				approvalNameCell.putValue(appovalLine.getOwnership().getOwner().getFullName());

				// 날짜 (D, 3)
				String processDate = "";
				if (appovalLine.getCompleteTime() != null) {
					processDate = appovalLine.getCompleteTime().toString().substring(0, 10);
				}

				Cell approvalDateCell = worksheet.getCells().getRows().get(row).get(3);
				approvalDateCell.putValue(processDate);

				// 내용 (F, 5)
				Cell agreeDescCell = worksheet.getCells().getRows().get(row).get(5);
				agreeDescCell.putValue(appovalLine.getDescription() != null ? appovalLine.getDescription() : "");

				Row comDescRow = worksheet.getCells().getRows().get(row);
				int descheight = (int) comDescRow.getHeight();
				String comdescd = appovalLine.getDescription() != null ? appovalLine.getDescription() : "";
				if (null != comdescd) {
					for (int i = 0; i < comdescd.length(); i++) {
						char ca = comdescd.charAt(i);
						Character careCa = Character.valueOf('\n');
						if (ca == careCa) {
							descheight += 350;
						}
					}
				}
//				comDescRow.setHeight((short) descheight / 20);
				row++;
			}

			/**
			 * 결재자 의견 끝
			 */

//			System.out.println("쓰기 종료....." + POIUtil.getSheetRow(sheet));

			String fullPath = path + "/" + dto.getNumber() + ".xlsx";
			workbook.save(fullPath);
			result.put("name", newFile.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 년도별 ECO 현황
	 */
	public Map<String, ArrayList<Map<String, Integer>>> getChart(String start) throws Exception {
		if (!StringUtil.checkString(start)) {
			start = "2016";
		}
		Map<String, ArrayList<Map<String, Integer>>> map = new HashMap<>();
		// 5개..

		ArrayList<Map<String, Integer>> complete = new ArrayList<Map<String, Integer>>();
		ArrayList<Map<String, Integer>> progress = new ArrayList<Map<String, Integer>>();
		for (int i = 0; i < 10; i++) {
			Map<String, Integer> cMap = new HashMap<>();
			Map<String, Integer> pMap = new HashMap<>();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(EChangeOrder.class, true);
//			QuerySpecUtils.toState(query, idx, EChangeOrder.class, "APPROVED");

			String from = Integer.parseInt(start) + i + "-01-01";
			String to = Integer.parseInt(start) + i + "-12-31";

			QuerySpecUtils.toTimeGreaterAndLess(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, from,
					to);
			QueryResult result = PersistenceHelper.manager.find(query);

			int cValue = 0;
			int pValue = 0;

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				EChangeOrder eco = (EChangeOrder) obj[0];
				if (eco.getLifeCycleState().toString().equals("APPROVED")) {
					cValue++;
				} else {
					pValue++;
				}
			}
			cMap.put((start + i), cValue);
			pMap.put((start + i), pValue);

			complete.add(cMap);
			progress.add(pMap);
		}
		map.put("progress", progress);
		map.put("complete", complete);
		return map;
	}

	/**
	 * SAP 전송전 검증 해보기
	 */
	public Map<String, Object> validate(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();
		String oid = (String) params.get("oid");
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		System.out.println("SAP ECO BOM 전송 품목 검증 시작!");
		map = validateSendEcoBom(eco);
		System.out.println("SAP ECO BOM 전송 품목 검증 종료!");
		return map;
	}

	/**
	 * BOM 전송전 검증
	 */
	private Map<String, Object> validateSendEcoBom(EChangeOrder eco) throws Exception {
		Map<String, Object> rs = new HashMap<>();
		JCoDestination destination = JCoDestinationManager.getDestination(SAPConnection.DESTINATION_NAME);
		JCoFunction function = destination.getRepository().getFunction("ZPPIF_PDM_002");
		if (function == null) {
			throw new RuntimeException("STFC_CONNECTION not found in SAP.");
		}

		JCoParameterList importTable = function.getImportParameterList();
		importTable.setValue("IV_WERKS", "1000"); // 플랜트
		importTable.setValue("IV_TEST", "X"); // 플랜트

		int idx = 1;

		DecimalFormat df = new DecimalFormat("0000");
		Timestamp t = new Timestamp(new Date().getTime());
		String today = t.toString().substring(0, 10).replaceAll("-", "");

		JCoTable ecoTable = function.getTableParameterList().getTable("ET_ECM");
		ecoTable.appendRow();
		ecoTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 12자리??
		ecoTable.setValue("ZECMID", "ECO"); // EO/ECO 구분
		ecoTable.setValue("DATUV", today); // 보내는 날짜
		ecoTable.setValue("AEGRU", eco.getEoName()); // 변경사유 테스트 일단 한줄
		ecoTable.setValue("AETXT", eco.getEoName()); // 변경 내역 첫줄만 일단 테스트

		if (StringUtil.checkString(eco.getEoCommentA())) {
			String AETXT_L = eco.getEoCommentA() != null ? eco.getEoCommentA() : "";
			ecoTable.setValue("AETXT_L", AETXT_L.replaceAll("\n", "<br>"));
		} else {
			ecoTable.setValue("AETXT_L", ""); // 변경 내역 전체 내용
		}
		JCoTable bomTable = function.getTableParameterList().getTable("ET_BOM");
		QueryResult qr = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (qr.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) qr.nextElement();
			WTPartMaster master = link.getPart();
			String version = link.getVersion();
			WTPart target = PartHelper.manager.getPart(master.getNumber(), version);
			boolean isPast = link.getPast();

			WTPart next_part = null;
			WTPart pre_part = null;

			boolean isRight = link.getRightPart();
			boolean isLeft = link.getLeftPart();
			// 오른쪽이면 다음 버전 품목을 전송해야한다.. 이게 맞는듯
			if (isLeft) {
				// 왼쪽이면 승인됨 데이터..그니깐 개정후 데이터를 보낸다 근데 변경점이 없지만 PDM상에서 버전은 올라간 상태
				next_part = (WTPart) EChangeUtils.manager.getNext(target);
				pre_part = target;
			} else if (isRight) {
				// 오른쪽 데이터면 애시당초 바귄 대상 품번 그대로 넣어준다..
				next_part = target;
				pre_part = SAPHelper.manager.getPre(target, eco);
			}

			ArrayList<SAPSendBomDTO> sendList = new ArrayList<SAPSendBomDTO>();
			// 둘다 있을 경우
			if (pre_part != null && next_part != null) {
				ArrayList<Object[]> rights = SAPHelper.manager.sendList(next_part);
				ArrayList<Object[]> lefts = SAPHelper.manager.sendList(pre_part);

				ArrayList<Map<String, Object>> addList = SAPHelper.manager.addList(lefts, rights);
				ArrayList<Map<String, Object>> removeList = SAPHelper.manager.removeList(lefts, rights);

				ArrayList<String> addKey = new ArrayList<String>();
				// 추가 항목 넣음
				for (Map<String, Object> add : addList) {
					String addOid = (String) add.get("oid");
					WTPart addPart = (WTPart) CommonUtil.getObject(addOid);
					// 부모_자식
					String key = next_part.getNumber() + "_" + addPart.getNumber();

					SAPSendBomDTO addDto = new SAPSendBomDTO();
					addDto.setParentPartNumber(null);
					addDto.setChildPartNumber(null);
					addDto.setNewParentPartNumber(next_part.getNumber());
					addDto.setNewChildPartNumber(addPart.getNumber());
					addDto.setQty((int) add.get("qty"));
					addDto.setUnit((String) add.get("unit"));
					addDto.setSendType("추가품목");
					addDto.setKey(key);
					if (!addKey.contains(key)) {
						addKey.add(key);
						sendList.add(addDto);
					}
				}

				ArrayList<String> removeKey = new ArrayList<String>();
				// 삭제 항목 넣음
				for (Map<String, Object> remove : removeList) {
					String removeOid = (String) remove.get("oid");
					WTPart removePart = (WTPart) CommonUtil.getObject(removeOid);
					String key = pre_part.getNumber() + "_" + removePart.getNumber();
					SAPSendBomDTO removeDto = new SAPSendBomDTO();
					removeDto.setParentPartNumber(pre_part.getNumber());
					removeDto.setChildPartNumber(removePart.getNumber());
					removeDto.setNewParentPartNumber(null);
					removeDto.setNewChildPartNumber(null);
					removeDto.setQty((int) remove.get("qty"));
					removeDto.setUnit((String) remove.get("unit"));
					removeDto.setSendType("삭제품");
					removeDto.setKey(key);

					if (!removeKey.contains(key)) {
						removeKey.add(key);
						sendList.add(removeDto);
					}
				}

				// 변경 대상 리스트..
				ArrayList<SAPSendBomDTO> changeList = SAPHelper.manager.getOneLevel(next_part, eco);
				Iterator<SAPSendBomDTO> iterator = changeList.iterator();
				List<SAPSendBomDTO> itemsToRemove = new ArrayList<>();
//				ArrayList<String> changeKey = new ArrayList<String>();
				while (iterator.hasNext()) {
					SAPSendBomDTO dto = iterator.next();
					dto.setSendType("변경품");
					String compNum = dto.getNewChildPartNumber();

					// addList에서 같은 newChildPartNumber를 찾으면 itemsToRemove에 추가
					for (Map<String, Object> addMap : addList) {
						String addOid = (String) addMap.get("oid");
						WTPart addPart = (WTPart) CommonUtil.getObject(addOid);
						if (addPart.getNumber().equals(compNum)) {
							itemsToRemove.add(dto);
							break; // 이미 찾았으니 더 이상 검색할 필요가 없음
						}
					}
				}

				// itemsToRemove에 해당하는 모든 아이템을 changeList에서 제거
				changeList.removeAll(itemsToRemove);

				// 위의 반복문에서 변경된 changeList를 sendList에 추가
				sendList.addAll(changeList);
			}

			for (SAPSendBomDTO dto : sendList) {

				boolean isPass = isPass(dto);
				if (isPass) {
					continue;
				}

				System.out.println("전송타입 = " + dto.getSendType() + " || 이전부모품번 = " + dto.getParentPartNumber() + ", "
						+ " 이전자식품번 =  " + dto.getChildPartNumber() + ", 신규부모품번 = " + dto.getNewParentPartNumber()
						+ ", 신규자식품번 = " + dto.getNewChildPartNumber());

				bomTable.insertRow(idx);
				bomTable.setValue("AENNR8", eco.getEoNumber()); // 변경번호 12자리?
				bomTable.setValue("SEQNO", df.format(idx)); // 항목번호 ?? 고정인지.. 애매한데
				bomTable.setValue("MATNR_OLD", dto.getParentPartNumber()); // 이전 모품번
				bomTable.setValue("IDNRK_OLD", dto.getChildPartNumber()); // 이전 자품번
				bomTable.setValue("MATNR_NEW", dto.getNewParentPartNumber()); // 기존 모품번
				bomTable.setValue("IDNRK_NEW", dto.getNewChildPartNumber()); // 기존 자품번
				bomTable.setValue("MENGE", dto.getQty()); // 수량
				bomTable.setValue("MEINS", dto.getUnit()); // 단위
				bomTable.setValue("AENNR12", eco.getEoNumber() + df.format(idx)); // 변경번호 12자리
				idx++;
			}
		}
		function.execute(destination);

		JCoParameterList result = function.getExportParameterList();
		Object r_type = result.getValue("EV_STATUS");
		Object r_msg = result.getValue("EV_MESSAGE");

		JCoTable rtnTable = function.getTableParameterList().getTable("ET_BOM");
		rtnTable.firstRow();
		ArrayList<Map<String, Object>> rtnList = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < rtnTable.getNumRows(); i++, rtnTable.nextRow()) {
			Map<String, Object> rtnMap = new HashMap<>();
			Object ZIFSTA = rtnTable.getValue("ZIFSTA");
			Object ZIFMSG = rtnTable.getValue("ZIFMSG");
			Object MATNR_OLD = rtnTable.getValue("MATNR_OLD");
			Object IDNRK_OLD = rtnTable.getValue("IDNRK_OLD");
			Object MATNR_NEW = rtnTable.getValue("MATNR_NEW");
			Object IDNRK_NEW = rtnTable.getValue("IDNRK_NEW");
			System.out.println("이전부모 =  " + MATNR_OLD + ", 이전자식 = " + IDNRK_OLD + ", 신규부모 = " + MATNR_NEW + ", 신규자식 = "
					+ IDNRK_NEW + ", ZIFSTA=" + ZIFSTA + ", ZIFMSG=" + ZIFMSG);

			rtnMap.put("MATNR_OLD", MATNR_OLD);
			rtnMap.put("IDNRK_OLD", IDNRK_OLD);
			rtnMap.put("MATNR_NEW", MATNR_NEW);
			rtnMap.put("IDNRK_NEW", IDNRK_NEW);
			rtnMap.put("ZIFSTA", ZIFSTA);
			rtnMap.put("ZIFMSG", ZIFMSG);

			if (StringUtil.checkString((String) ZIFMSG)) {
				rtnMap.put("ERROR", "실패");
			} else {
				rtnMap.put("ERROR", "성공");
			}

			rtnList.add(rtnMap);

		}
		System.out.println("[ SAP JCO ] RETURN - TYPE:" + r_type);
		System.out.println("[ SAP JCO ] RETURN - MESSAGE:" + r_msg);

		if ("E".equals(r_type)) {
			rs.put("isValidate", false);
		} else {
			rs.put("isValidate", true);
		}
		rs.put("rtnList", rtnList);
		return rs;
	}

	/**
	 * 전송 제외 처리 체크
	 */
	private boolean isPass(SAPSendBomDTO dto) throws Exception {

		if (SAPHelper.manager.skipEight(dto.getChildPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipLength(dto.getChildPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipEight(dto.getParentPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipLength(dto.getParentPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipEight(dto.getChildPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipLength(dto.getChildPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipEight(dto.getParentPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipLength(dto.getParentPartNumber())) {
			return true;
		}

		// 신규
		if (SAPHelper.manager.skipEight(dto.getNewChildPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipLength(dto.getNewChildPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipEight(dto.getNewParentPartNumber())) {
			return true;
		}

		if (SAPHelper.manager.skipLength(dto.getNewParentPartNumber())) {
			return true;
		}
		return false;
	}

	/**
	 * 설변품목 더미 제외 여부
	 */
	public ArrayList<Map<String, Object>> reloadData(String oid, String skip) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		boolean isSkip = Boolean.parseBoolean(skip);
		EChangeOrder eco = (EChangeOrder) CommonUtil.getObject(oid);
		QueryResult result = PersistenceHelper.manager.navigate(eco, "part", EcoPartLink.class, false);
		while (result.hasMoreElements()) {
			EcoPartLink link = (EcoPartLink) result.nextElement();
			WTPartMaster master = link.getPart();
			WTPart part = PartHelper.manager.getPart(master.getNumber(), link.getVersion());

			if (isSkip) {
				if (EChangeUtils.isDummy(part.getNumber())) {
					continue;
				}
			}

			Map<String, Object> map = new HashMap<>();

			boolean isPast = link.getPast();
			boolean preOrder = link.getPreOrder();
			map.put("delivery", link.getDelivery());
			map.put("complete", link.getComplete());
			map.put("inner", link.getInner());
			map.put("order", link.getOrders());
			map.put("part_state_code", link.getPartStateCode());
			map.put("preOrder", preOrder);
			map.put("weight", link.getWeight());

			// 과거 데이터 처리
			if (!isPast) {

				boolean isRight = link.getRightPart();
				boolean isLeft = link.getLeftPart();
				boolean isApproved = part.getLifeCycleState().toString().equals("APPROVED");
				boolean isFour = part.getNumber().startsWith("4"); // 4로 시작하는것은 무조건 모두 새품번
				boolean isRevise = link.isRevise();

				if (isLeft) {
					map.put("part_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("part_number", part.getNumber());
					map.put("part_name", part.getName());
					map.put("part_state", part.getLifeCycleState().getDisplay());
					map.put("part_version", part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue());
					map.put("part_creator", part.getCreatorFullName());

					// 개정된 데이터가 없을 경우
					if (!isRevise && !isFour) {
						map.put("next_oid", "");
						map.put("next_number", "개정 후 데이터가 없습니다.");
						map.put("next_name", "개정 후 데이터가 없습니다.");
						map.put("next_state", "개정 후 데이터가 없습니다.");
						map.put("next_version", "개정 후 데이터가 없습니다.");
						map.put("next_creator", "개정 후 데이터가 없습니다.");
						map.put("afterMerge", true);
					} else {
						WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
						if (next_part != null) {
							// 개정데이터가 있을경우
							map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
							map.put("next_number", next_part.getNumber());
							map.put("next_name", next_part.getName());
							map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
									+ next_part.getIterationIdentifier().getSeries().getValue());
							map.put("next_creator", next_part.getCreatorFullName());
							map.put("next_state", next_part.getLifeCycleState().getDisplay());
							map.put("afterMerge", false);
						} else {
							map.put("next_oid", "");
							map.put("next_number", "개정 후 데이터가 없습니다.");
							map.put("next_name", "개정 후 데이터가 없습니다.");
							map.put("next_state", "개정 후 데이터가 없습니다.");
							map.put("next_version", "개정 후 데이터가 없습니다.");
							map.put("next_creator", "개정 후 데이터가 없습니다.");
							map.put("afterMerge", true);
						}
					}
				} else if (isRight) {
					WTPart pre_part = EChangeUtils.manager.getEcoPrePart(eco, part);
					// 변경후
					map.put("next_oid", part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("next_number", part.getNumber());
					map.put("next_name", part.getName());
					map.put("next_state", part.getLifeCycleState().getDisplay());
					map.put("next_version", part.getVersionIdentifier().getSeries().getValue() + "."
							+ part.getIterationIdentifier().getSeries().getValue());
					map.put("next_creator", part.getCreatorFullName());

					if (pre_part == null) {
						map.put("part_oid", "");
						map.put("part_number", "개정 전 데이터가 없습니다.");
						map.put("part_name", "개정 전 데이터가 없습니다.");
						map.put("part_state", "개정 전 데이터가 없습니다.");
						map.put("part_version", "개정 전 데이터가 없습니다.");
						map.put("part_creator", "개정 전 데이터가 없습니다.");
						map.put("preMerge", true);
					} else {
						map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("part_number", pre_part.getNumber());
						map.put("part_name", pre_part.getName());
						map.put("part_state", pre_part.getLifeCycleState().getDisplay());
						map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
								+ pre_part.getIterationIdentifier().getSeries().getValue());
						map.put("part_creator", pre_part.getCreatorFullName());
						map.put("preMerge", false);
					}
				}
				list.add(map);
			} else {
				WTPart pre_part = part;

				if (link.isRevise()) {
					WTPart next_part = (WTPart) EChangeUtils.manager.getNext(part);
					if (next_part != null) {
						map.put("part_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("part_number", pre_part.getNumber());
						map.put("part_name", pre_part.getName());
						map.put("part_state", pre_part.getLifeCycleState().getDisplay());
						map.put("part_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
								+ pre_part.getIterationIdentifier().getSeries().getValue());
						map.put("part_creator", pre_part.getCreatorFullName());
						map.put("preMerge", false);

						map.put("next_oid", next_part.getPersistInfo().getObjectIdentifier().getStringValue());
						map.put("next_number", next_part.getNumber());
						map.put("next_name", next_part.getName());
						map.put("next_version", next_part.getVersionIdentifier().getSeries().getValue() + "."
								+ next_part.getIterationIdentifier().getSeries().getValue());
						map.put("next_creator", next_part.getCreatorFullName());
						map.put("next_state", next_part.getLifeCycleState().getDisplay());
						map.put("afterMerge", false);
//					} else {
//						map.put("next_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
//						map.put("next_number", pre_part.getNumber());
//						map.put("next_name", pre_part.getName());
//						map.put("next_state", pre_part.getLifeCycleState().getDisplay());
//						map.put("next_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
//								+ pre_part.getIterationIdentifier().getSeries().getValue());
//						map.put("next_creator", pre_part.getCreatorFullName());
//						map.put("preMerge", true);
					}
				} else {

					map.put("part_oid", "");
					map.put("part_number", "개정 전 데이터가 없습니다.");
					map.put("part_name", "개정 전 데이터가 없습니다.");
					map.put("part_state", "개정 전 데이터가 없습니다.");
					map.put("part_version", "개정 전 데이터가 없습니다.");
					map.put("part_creator", "개정 전 데이터가 없습니다.");
					map.put("preMerge", true);

					map.put("next_oid", pre_part.getPersistInfo().getObjectIdentifier().getStringValue());
					map.put("next_number", pre_part.getNumber());
					map.put("next_name", pre_part.getName());
					map.put("next_state", pre_part.getLifeCycleState().getDisplay());
					map.put("next_version", pre_part.getVersionIdentifier().getSeries().getValue() + "."
							+ pre_part.getIterationIdentifier().getSeries().getValue());
					map.put("next_creator", pre_part.getCreatorFullName());
					map.put("afterMerge", false);
				}
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 내가 작성하고 승인되지 않은 ECO
	 */
	public QueryResult getMyEco(String oid) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(EChangeOrder.class, true);
		QuerySpecUtils.toCreatorQuery(query, idx, EChangeOrder.class, oid);
		QuerySpecUtils.toEqualsAnd(query, idx, EChangeOrder.class, EChangeOrder.EO_TYPE, "CHANGE");
		QuerySpecUtils.toNotEqualsAnd(query, idx, EChangeOrder.class, "state.state", "APPROVED");
		QuerySpecUtils.toOrderBy(query, idx, EChangeOrder.class, EChangeOrder.CREATE_TIMESTAMP, true);
		return PagingSessionHelper.openPagingSession(0, 5, query);
	}

	public void reverseStructure(WTPart end, ArrayList<WTPart> list) throws Exception {
		if (!list.contains(end)) {
			list.add(end);
		}
		WTPartMaster master = (WTPartMaster) end.getMaster();
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

		String viewName = end.getViewName();
		if (!StringUtil.checkString(viewName)) {
			viewName = "Design";
		}

		View view = ViewHelper.service.getView(viewName);
		if (view != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "view.key.id", "=",
					view.getPersistInfo().getObjectIdentifier().getId()), new int[] { idx_part });
		}

		String state = end.getLifeCycleState().toString();
		if (state != null) {
			query.appendAnd();
			query.appendWhere(new SearchCondition(WTPart.class, "state.state", "=", state), new int[] { idx_part });
		}

		QuerySpecUtils.toLatest(query, idx_part, WTPart.class);

		query.appendOrderBy(new OrderBy(new ClassAttribute(WTPart.class, "master>number"), true),
				new int[] { idx_part });

		QueryResult qr = PersistenceHelper.manager.find(query);
		while (qr.hasMoreElements()) {
			Object obj[] = (Object[]) qr.nextElement();
			WTPart p = (WTPart) obj[1];
			if (!list.contains(p)) {
				list.add(p);
			}
			reverseStructure(p, list);
		}
	}
}